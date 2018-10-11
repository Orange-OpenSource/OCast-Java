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

package org.ocast.referencedriver.setting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocast.core.Reply;
import org.ocast.core.setting.AxeInfo;
import org.ocast.core.setting.GamepadEvent;
import org.ocast.referencedriver.payload.CommandPayload;

public class SendGamepadEvent extends CommandPayload {

    private static final String KEY_BUTTONS = "buttons";
    private static final String KEY_NUM = "num";
    private static final String KEY_AXES = "axes";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_CODE = "code";

    public static JSONObject encode(GamepadEvent gamepadEvent) {
        JSONObject json = new JSONObject();

        JSONArray axes = new JSONArray();
        for (AxeInfo axe : gamepadEvent.getAxes()) {
            JSONObject jAxe = new JSONObject();
            jAxe.put(KEY_X, axe.getX());
            jAxe.put(KEY_Y, axe.getY());
            jAxe.put(KEY_BUTTONS, axe.getButtons());
            axes.put(jAxe);
        }
        json.put(KEY_AXES, axes);
        json.put(KEY_NUM, gamepadEvent.getNum());

        return encodeMessage(PublicSettingsImpl.SERVICE_SETTINGS_DEVICE, "gamepadEvent", json);
    }

    public static Integer decode(Reply data) throws JSONException {
        JSONObject json = decodeMessage(data.getReply());
        return json.getInt(KEY_CODE);
    }
}
