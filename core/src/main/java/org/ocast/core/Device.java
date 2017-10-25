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

package org.ocast.core;

import java.net.URL;

/**
 * Represents an OCast device
 */
public class Device {
    private final String friendlyName;
    private final String manufacturer;
    private final String modelName;
    private final String uuid;
    private final URL dialApplURL;

    public Device(String uuid, String friendlyName, String manufacturer, String modelName, URL urlBase) {
        this.uuid = uuid;
        this.friendlyName = friendlyName;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        dialApplURL = urlBase;
    }

    /**
     * Retrieve the device friendly name found in device tag
     *
     * @return friendly name
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Retrieve the manufacturer found in found in device tag
     *
     * @return
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Retrieve the modelName found in device tag
     *
     * @return
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Retrieve the UUID found in device tag
     *
     * @return the uuid value without uuid: prefix
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Retrieve the Dial application URL found in device tag URLBase or the one provided
     * to fromDeviceDescription if it comes from a header.
     *
     * @return the URL as a String
     */
    public String getDialApplURL() {
        return dialApplURL.toString();
    }

    /**
     * Retrieve the Dial application URL found in device tag URLBase or the one provided
     * to fromDeviceDescription if it comes from a header.
     *
     * @return an URL
     */
    public URL getDialURL() {
        return dialApplURL;
    }
}
