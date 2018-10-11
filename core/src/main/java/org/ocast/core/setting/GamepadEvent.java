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

import java.util.List;

public class GamepadEvent {

    private final List<AxeInfo> axes;
    private final int num;

    /**
     * Virtual gamepad event, matching the
     * Standard Gamepad with 4 axes and 17 buttons.
     * Several buttons can be clicked at the same time by providing the bitmask representation of each button (0 no button, 1, 2 and 4 at least).
     * Multiple clicks are detected by the server according to received requests frequency.
     * (Less than 100ms between 2 clicks on the same button number).
     *
     * @param axes AxeInfo
     * @param num gamepad number
     */
    public GamepadEvent(List<AxeInfo> axes, int num) {
        this.axes = axes;
        this.num = num;
    }

    public List<AxeInfo> getAxes() {
        return axes;
    }

    public int getNum() {
        return num;
    }
}