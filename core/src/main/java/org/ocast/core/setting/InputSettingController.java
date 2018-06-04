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

import org.json.JSONObject;
import org.ocast.core.DataStream;

import java.util.List;

public class InputSettingController extends DataStream {

    protected final InputSettingControllerListener listener;

    /**
     * Provides informations on input (keyboard, mouse...)
     */
    public interface InputSettingControllerListener {

        /**
         * Virtual keyValue pressed. The possible supported keys are defined by the w3c uievents-code following the KeyboardEvent Interface.
         * @param key key value
         * @param code key code
         * @param ctrl crtl pressed
         * @param alt alt pressed
         * @param shift shift pressed
         * @param meta meta pressed
         * @param location key location on keyboard
         */
        void onKeyPressed(String key, String code, boolean ctrl, boolean alt, boolean shift, boolean meta, int location);

        /**
         * Virtual mouse click & movement
         * Calculated according to the sensitivity (x2- x1 * sensitivity , y2-y1 * sensitivity)
         * Several buttons can be clicked at the same time by providing the bitmask representation of each button (0 no button, 1, 2 and 4 at least).
         * Double clicks are detected by the server according to received requests frequency.
         * (Less than 700ms between 2 clicks on the same button number).
         * @param x x movement
         * @param y y movement
         * @param buttons bitmask representation of each button
         */
        void onMouseEvent(int x, int y, int buttons);

        /**
         * Virtual gamepad event, matching the
         * Standard Gamepad with 4 axes and 17 buttons.
         * Several buttons can be clicked at the same time by providing the bitmask representation of each button (0 no button, 1, 2 and 4 at least).
         * Multiple clicks are detected by the server according to received requests frequency.
         * (Less than 100ms between 2 clicks on the same button number).
         * @param axes AxeInfo
         * @param buttons bitmask representation of each button
         */
        void onGamepadEvent(List<AxeInfo> axes, int buttons);
    }

    /**
     * Instanciate a InputSettingController
     * @param listener the listener to be notified of input events
     */
    public InputSettingController(String serviceName, InputSettingControllerListener listener) {
        super(serviceName);
        this.listener = listener;
    }

    @Override
    public void onMessage(JSONObject message) {
    }
}
