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

/**
 * Defines the logic of an object managing a discovery process
 */
public interface Discovery {
    /**
     * Defines an object listening to device add/removal
     */
    interface DiscoveryListener {

        /**
         * A new device has been found
         * @param dd Dial parsed from the Location URL
         */
        void onDeviceAdded(DiscoveredDevice dd);

        /**
         * A known device has been lost
         * @param dd DialDevice parsed from the Location URL
         */
        void onDeviceRemoved(DiscoveredDevice dd);
    }

    /**
     * starts the discovery process
     */
    void start();

    /**
     * stops the discovery process
     */
    void stop();
}
