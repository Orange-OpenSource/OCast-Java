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

    public InputSettingController(String serviceName, InputSettingControllerListener listener) {
        super(serviceName);
        this.listener = listener;
    }

    /**
     * Provides informations on input (keyboard, mouse...)
     */
    public interface InputSettingControllerListener {

        void onKeyPressed(String key, String code, boolean ctrl, boolean alt, boolean shift, boolean meta, int location);

        void onMouseEvent(int x, int y, int buttons);

        void onGamepadEvent(List<AxeInfo> axes, int buttons);
    }

    @Override
    public void onMessage(JSONObject message) {
    }
}
