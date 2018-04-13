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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum SettingReplyCode {
    SUCCESS(0),
    NOT_IMPLEMENTED(1200),
    WIFI_PINCODE_NOT_CORRECT(1201),
    WIFI_FAILED_TO_RETRIEVE_PINCODE(1202),
    BT_PINCODE_IS_REQUIRED(1401),
    BT_DISCONNECT_OR_PAIR_ON_UNAVAILABLE_DEVICE(1402),
    BT_START_DISCOVERY_IN_PROGRESS(1403),
    BT_STOP_DISCOVERY_FAILED(1404),
    BT_DEVICE_AUTHENTICATION_FAILURE(1405),
    BT_UNKNOWN_ERROR(1407),
    UNKNOWN_ERROR(5000);

    private final int code;
    private static final Map<Integer,SettingReplyCode> lookup = new HashMap<>();

    static {
        for(SettingReplyCode s : EnumSet.allOf(SettingReplyCode.class))
            lookup.put(s.getCode(), s);
    }

    SettingReplyCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SettingReplyCode get(int code) {
        SettingReplyCode result = lookup.get(code);
        return result != null ? result : UNKNOWN_ERROR;
    }
}
