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

public class AxeInfo {
    private final double x;
    private final double y;
    private final int buttons;

    public AxeInfo(double x, double y, int buttons) {
        this.x = x;
        this.y = y;
        this.buttons = buttons;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getButtons() {
        return buttons;
    }
}
