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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Discovery {

    private static final String TAG = LogTag.DISCOVERY;
    private final DiscoveryExecutor mDiscovery;
    private final DeviceInventory mDeviceInventory;
    private boolean mRunning;


    public Discovery(List<String> searchTargetList, DiscoveryListener listener, DiscoveryReliability reliabilty) {
        mDeviceInventory = new DialDeviceInventory(listener, reliabilty.getRetry());
        mDiscovery = new SSDPDiscovery(searchTargetList, reliabilty.getTimeout());
    }

    public Discovery(List<String> searchTargetList, DiscoveryListener callback) {
        this(searchTargetList, callback, DiscoveryReliability.MEDIUM);
    }

    public Discovery(String searchTarget, DiscoveryListener callback) {
        this(new ArrayList<>(Arrays.asList(searchTarget)), callback);
    }

    public Discovery(String searchTarget, DiscoveryListener callback, DiscoveryReliability reliabilty) {
        this(new ArrayList<>(Arrays.asList(searchTarget)), callback, reliabilty);
    }

    /**
     * Starts SSDP Discovery
     */
    public void start() {
        Logger.getLogger(TAG).log(Level.INFO, "Starting discovery...");
        if(!mRunning) {
            mDiscovery.addListener(mDeviceInventory);
            mDeviceInventory.refresh();
            mDiscovery.start();
            mRunning = true;
        } else {
            Logger.getLogger(TAG).log(Level.WARNING,"already running");
        }
    }

    /**
     * Stops SSDP Discovery
     */
    public void stop() {
        Logger.getLogger(TAG).log(Level.INFO,"Stopping discovery...");
        if(mRunning) {
            mDiscovery.removeListener(mDeviceInventory);
            mDiscovery.stop();
            mRunning = false;
        } else {
            Logger.getLogger(TAG).log(Level.WARNING,"discovery not running");
        }
    }

    public List<DialDevice> getDeviceDescriptions() {
        return mDeviceInventory.getDeviceDescriptions();
    }
}
