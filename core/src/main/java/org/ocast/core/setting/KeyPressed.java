/*
 * Software Name : OCast SDK
 *
 *  Copyright (C) 2018 Orange
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

public class KeyPressed {

    private final String key;
    private final String code;
    private final boolean ctrl;
    private final boolean alt;
    private final boolean shift;
    private final boolean meta;
    private final int location;

    /**
     * Virtual keyValue pressed. The possible supported keys are defined by the w3c uievents-code following the KeyboardEvent Interface.
     *
     * @param key key value
     * @param code key code
     * @param ctrl crtl pressed
     * @param alt alt pressed
     * @param shift shift pressed
     * @param meta meta pressed
     * @param location key location on keyboard
     */
    public KeyPressed(String key, String code, boolean ctrl, boolean alt, boolean shift, boolean meta, int location) {
        this.key = key;
        this.code = code;
        this.ctrl = ctrl;
        this.alt = alt;
        this.shift = shift;
        this.meta = meta;
        this.location = location;
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    public boolean isCtrl() {
        return ctrl;
    }

    public boolean isAlt() {
        return alt;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isMeta() {
        return meta;
    }

    public int getLocation() {
        return location;
    }
}