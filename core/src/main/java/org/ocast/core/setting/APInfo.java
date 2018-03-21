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

public class APInfo {
    private final String ssid;
    private final String bssid;
    private final float frequency;
    private final int rssi;
    private final int security;

    public APInfo(String ssid, String bssid, float frequency, int rssi, int security) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.frequency = frequency;
        this.rssi = rssi;
        this.security = security;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public float getFrequency() {
        return frequency;
    }

    public int getRssi() {
        return rssi;
    }

    public int getSecurity() {
        return security;
    }
}
