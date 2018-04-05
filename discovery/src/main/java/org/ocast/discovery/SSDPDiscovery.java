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

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * defines a SSDP {@link Discovery Discovery}
 */
public class SSDPDiscovery implements Discovery {
    private static final String TAG = LogTag.SSDP;

    SSDPManager manager;
    DiscoveryListener listener;
    private boolean mRunning = false;
    private Set<DialDevice> devices = Collections.synchronizedSet(new HashSet<>());
    private final SSDPManager.DiscoveryListener ssdpListener = new SSDPManager.DiscoveryListener() {

        @Override
        public void onServiceFound(URI location) {
            manager.resolve(location);
        }

        @Override
        public void onServiceLost(URI location) {
            for (Iterator<DialDevice> iterator = devices.iterator(); iterator.hasNext(); ) {
                DialDevice d = iterator.next();
                if (d.getLocation().equals(location)) {
                    iterator.remove();
                    listener.onDeviceRemoved(d);
                }
            }
        }

        @Override
        public void onServiceResolved(DialDevice dd) {
            devices.add(dd);
            listener.onDeviceAdded(dd);
        }

        @Override
        public void onServiceResolveFailed(URI location) {
            //ignored as we are interested in resolved services
        }
    };


    /**
     * @param searchTarget the scanInternal target corresponding to devices of interest
     */
    public SSDPDiscovery(final String searchTarget, DiscoveryListener listener) {
        this(new HashSet<>(Arrays.asList(searchTarget)), listener);
    }


    public SSDPDiscovery(Set<String> searchTargetList, DiscoveryListener listener) {
        this.listener = listener;
        manager = new SSDPManager(searchTargetList, ssdpListener);
    }

    /**
     * Starts polling the network to scan devices by sending a M-SEARCH discover
     */
    @Override
    public void start() {
        start(false);
    }

    /**
     * Starts actively polling the network to scan devices by sending a M-SEARCH discover
     */
    public void start(boolean active) {
        Logger.getLogger(TAG).log(Level.INFO, active ? "start active discovery":"start discovery");
        DiscoveryReliability reliability = active ? DiscoveryReliability.HIGH:DiscoveryReliability.MEDIUM;
        if(mRunning) {
            manager.changeReliability(reliability);
        } else {
            manager.discoverServices(reliability);
            mRunning = true;
        }
    }

    /**
     * Stops active scan
     */
    @Override
    public void stop() {
        Logger.getLogger(TAG).log(Level.INFO, "Stopping discovery...");
        manager.stopDiscovery();
        mRunning = false;
    }
}
