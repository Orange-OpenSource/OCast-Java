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

public class DiscoveredDevice {
    private final String mFriendlyName;
    private final String mManufacturer;
    private final String mModelName;
    private final String mUuid;
    private final URI mDialApplURI;

    public DiscoveredDevice(String uuid, String friendlyName, String manufacturer, String modelName, URI urlBase) {
        mUuid = uuid;
        mFriendlyName = friendlyName;
        mManufacturer = manufacturer;
        mModelName = modelName;
        mDialApplURI = urlBase;
    }

    /**
     * Retrieve the device friendly name found in device tag
     *
     * @return friendly name
     */
    public String getFriendlyName() {
        return mFriendlyName;
    }

    /**
     * Retrieve the manufacturer found in found in device tag
     *
     * @return
     */
    public String getManufacturer() {
        return mManufacturer;
    }

    /**
     * Retrieve the modelName found in device tag
     *
     * @return
     */
    public String getModelName() {
        return mModelName;
    }

    /**
     * Retrieve the UUID found in device tag
     *
     * @return the uuid value without uuid: prefix
     */
    public String getUuid() {
        return mUuid;
    }

    /**
     * Retrieve the Dial application URL found in device tag URLBase or the one provided
     * to fromDeviceDescription if it comes from a header.
     *
     * @return a URI object representing the Dial application URL
     */
    public URI getDialURI() {
        return mDialApplURI;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscoveredDevice that = (DialDevice) o;

        if (mFriendlyName != null ? !mFriendlyName.equals(that.mFriendlyName) : that.mFriendlyName != null)
            return false;
        if (mManufacturer != null ? !mManufacturer.equals(that.mManufacturer) : that.mManufacturer != null)
            return false;
        if (mModelName != null ? !mModelName.equals(that.mModelName) : that.mModelName != null)
            return false;
        if (mUuid != null ? !mUuid.equals(that.mUuid) : that.mUuid != null) return false;
        return mDialApplURI != null ? mDialApplURI.equals(that.mDialApplURI) : that.mDialApplURI == null;

    }

    @Override
    public int hashCode() {
        int result = mFriendlyName != null ? mFriendlyName.hashCode() : 0;
        result = 31 * result + (mManufacturer != null ? mManufacturer.hashCode() : 0);
        result = 31 * result + (mModelName != null ? mModelName.hashCode() : 0);
        result = 31 * result + (mUuid != null ? mUuid.hashCode() : 0);
        result = 31 * result + (mDialApplURI != null ? mDialApplURI.hashCode() : 0);
        return result;
    }
}
