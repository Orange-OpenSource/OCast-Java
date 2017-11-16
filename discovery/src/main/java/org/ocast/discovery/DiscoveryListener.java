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
 * Defines an object listening to device add/removal
 */
public interface DiscoveryListener {
        /**
         * A new device has been found
         * @param dd Dial parsed from the Location URL
         */
        void onDeviceAdded(DialDevice dd);

        /**
         * A known device has been lost
         * @param dd DialDevice parsed from the Location URL
         */
        void onDeviceRemoved(DialDevice dd);
}
