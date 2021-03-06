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

public class VersionInfo {
    private final int code;
    private final String name;
    private final String sWVersion;
    private final String hWVersion;
    private final boolean powerAlert;

    /**
     * Initializes a new VersionInfo object using the provided values
     * @param code an int as return code
     * @param name a String identifying thedongle name
     * @param sWVersion a String identifying the software version
     * @param hWVersion a String identifying the hardware version
     * @param powerAlert a boolean identifying the power alert flag
     */
    public VersionInfo(int code, String name, String sWVersion, String hWVersion, boolean powerAlert) {
        this.code = code;
        this.name = name;
        this.sWVersion = sWVersion;
        this.hWVersion = hWVersion;
        this.powerAlert = powerAlert;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getsWVersion() {
        return sWVersion;
    }

    public String gethWVersion() {
        return hWVersion;
    }

    public boolean havePowerAlert() {
        return powerAlert;
    }
}
