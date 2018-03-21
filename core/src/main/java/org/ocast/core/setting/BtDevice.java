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

import java.util.List;

public class BtDevice {

    private final String name;
    private final String mac;
    private final String code;
    private final List<String> profile;
    private final String state;
    private final int battery;

    public BtDevice(String name, String mac, String code, List<String> profile, String state, int battery) {
        this.name = name;
        this.mac = mac;
        this.code = code;
        this.profile = profile;
        this.state = state;
        this.battery = battery;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public String getCode() {
        return code;
    }

    public List<String> getProfile() {
        return profile;
    }

    public String getState() {
        return state;
    }

    public int getBattery() {
        return battery;
    }
}
