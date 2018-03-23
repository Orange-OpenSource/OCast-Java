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

package org.ocast.core.setting;

public class DeviceInfo {
    private final int code;
    private final String vendor;
    private final String model;
    private final String serialNumber;
    private final String MACAddress;
    private final String countryCode;

    public DeviceInfo(int code, String vendor, String model, String serialNumber, String MACAddress, String countryCode) {
        this.code = code;
        this.vendor = vendor;
        this.model = model;
        this.serialNumber = serialNumber;
        this.MACAddress = MACAddress;
        this.countryCode = countryCode;
    }

    public int getCode() {
        return code;
    }

    public String getVendor() {
        return vendor;
    }

    public String getModel() {
        return model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getMACAddress() {
        return MACAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
