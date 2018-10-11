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

import org.json.JSONException;
import org.json.JSONObject;
import org.ocast.core.Reply;
import org.ocast.core.setting.MouseEvent;
import org.ocast.referencedriver.payload.CommandPayload;

public class SendMouseEvents extends CommandPayload {

    private static final String KEY_BUTTONS = "buttons";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_CODE = "code";

    public static JSONObject encode(MouseEvent mouseEvent) {
        JSONObject json = new JSONObject();
        json.put(KEY_X, mouseEvent.getX());
        json.put(KEY_Y, mouseEvent.getY());
        json.put(KEY_BUTTONS, mouseEvent.getButtons());
        return encodeMessage(PublicSettingsImpl.SERVICE_SETTINGS_DEVICE, "mouseEvent", json);
    }

    public static Integer decode(Reply data) throws JSONException {
        JSONObject json = decodeMessage(data.getReply());
        return json.getInt(KEY_CODE);
    }
}
