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

public class MouseEvent {

    private final int x;
    private final int y;
    private final int buttons;

    /**
     * Virtual mouse click & movement
     * Calculated according to the sensitivity (x2- x1 * sensitivity , y2-y1 * sensitivity)
     * Several buttons can be clicked at the same time by providing the bitmask representation of each button (0 no button, 1, 2 and 4 at least).
     * Double clicks are detected by the server according to received requests frequency.
     * (Less than 700ms between 2 clicks on the same button number).
     *
     * @param x x movement
     * @param y y movement
     * @param buttons bitmask representation of each button
     */
    public MouseEvent(int x, int y, int buttons) {
        this.x = x;
        this.y = y;
        this.buttons = buttons;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getButtons() {
        return buttons;
    }
}