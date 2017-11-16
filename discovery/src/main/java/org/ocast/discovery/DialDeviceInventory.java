/*
 * Software Name : OCast SDK
 *
 *  Copyright (C) 2017 Orange
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.ocast.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines a {@link org.ocast.discovery.DeviceInventory DeviceInventory} for DIAL devices
 */
public class DialDeviceInventory  implements DeviceInventory<SSDPMessage>, DeviceDescriptionRequest.Callbacks {

    private static final String TAG = LogTag.DISCOVERY;

    private static final int DEFAULT_RETRY = 2;
    private static final int IMMEDIATE = 0;
    private final DeviceDescriptionRequest mDeviceDescriptionRequest;
    private final Map<String, DialDevice> mDevices = new ConcurrentHashMap<>();
    private final Map<String, Long> mLastResponse = new ConcurrentHashMap<>();
    private final DiscoveryListener mDiscoveryListener;
    private final int mRetry;
    private long mCurrentScan;

    public DialDeviceInventory(DiscoveryListener listener) {
        this(listener, DEFAULT_RETRY);
    }

    public DialDeviceInventory(DiscoveryListener listener, int retry) {
        mDeviceDescriptionRequest = new DeviceDescriptionRequest();
        mRetry = retry;
        mDiscoveryListener = listener;
    }

    private DialDevice getDeviceDescriptionByUudi(String uuid) {
        for (DialDevice dd : mDevices.values()) {
            if (uuid.equals(dd.getUuid())) {
                return dd;
            }
        }
        return null;
    }

    @Override
    public List<DialDevice> getDeviceDescriptions() {
        return new ArrayList<>(mDevices.values());
    }

    @Override
    public void refresh() {
        mCurrentScan = 0;
        for(Map.Entry<String, Long> entry: mLastResponse.entrySet()) {
            mLastResponse.put(entry.getKey(), 0L);
        }
    }

    @Override
    public void onLocationSent() {
        pruneDevices(mRetry);
        mCurrentScan++;
    }

    @Override
    public void onLocationReceived(SSDPMessage ssdpMessage) {
        long now = System.currentTimeMillis();
        String deviceUuid = ssdpMessage.getUuid();
        Logger.getLogger(TAG).log(Level.FINE, "onLocationReceived UUID {0}  / time {1}", new Object[]{deviceUuid, now});
        requestDeviceDescription(ssdpMessage.getHeader(SSDPMessage.LOCATION));
    }

    @Override
    public void onError() {
        pruneDevices(IMMEDIATE);
    }

    @Override
    public void onDeviceDescription(String location, DialDevice dd) {
        DialDevice knownDevice = getDeviceDescriptionByUudi(dd.getUuid());
        if (knownDevice == null) {
            Logger.getLogger(TAG).log(Level.FINE,  "Looks like a brand new device at {0}", location);
            mDevices.put(location, dd);
            mDiscoveryListener.onDeviceAdded(dd);
        } else {
            if (!knownDevice.equals(dd)) {
                Logger.getLogger(TAG).log(Level.FINE,  "Looks like a device that changed");
                mDevices.put(location, dd);
                mDiscoveryListener.onDeviceAdded(dd);
            }
        }
        mLastResponse.put(dd.getUuid(), mCurrentScan);
    }

    @Override
    public void onError(String location) {
        //This means we didn't get the dd.xml right for example due to SocketTimeout or parsing error
        //The sockettimeout happen pretty often with some hardware. That's we decide not to remove
        //a device to early and prefer to wait for X retry scan to consider something went wrong
        Logger.getLogger(TAG).log(Level.SEVERE, "could not fetch {0}", location);
    }

    /**
     * Execute a Http Request on the specified address in order to retrieve a cast device's dd.xml (Device Description)
     *
     * @param location
     */
    private void requestDeviceDescription(final String location) {
        Logger.getLogger(TAG).log(Level.FINE,  "Retrieving device description through {0}", location);
        mDeviceDescriptionRequest.getDeviceDescription(location, this);
    }

    private void pruneDevices(int threshold) {
        for (Map.Entry<String, DialDevice> entry : mDevices.entrySet()) {
            DialDevice dd = entry.getValue();
            String uuid = entry.getKey();
            if (mLastResponse.containsKey(dd.getUuid())) {
                long lastScan = mLastResponse.get(dd.getUuid());
                long delta = mCurrentScan - lastScan;
                Logger.getLogger(TAG).log(Level.FINE,  " {0} ({1})  / delta {2} ({3})", new Object[]{dd.getUuid(), dd.getFriendlyName(), delta, mCurrentScan});
                if (delta >= threshold) {
                    mLastResponse.remove(dd.getUuid());
                    mDevices.remove(uuid);
                    mDiscoveryListener.onDeviceRemoved(dd);
                }
            }
        }
    }
}
