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
import org.ocast.core.setting.KeyPressed;
import org.ocast.referencedriver.payload.CommandPayload;

public class SendKeyPressed extends CommandPayload {

    private static final String KEY_KEY = "key";
    private static final String KEY_CODE = "code";
    private static final String KEY_CTRL = "ctrl";
    private static final String KEY_ALT = "alt";
    private static final String KEY_SHIFT = "shift";
    private static final String KEY_META = "meta";
    private static final String KEY_LOCATION = "location";

    public static JSONObject encode(KeyPressed keyPressed) {
        JSONObject json = new JSONObject();
        json.put(KEY_KEY, keyPressed.getKey());
        json.put(KEY_CODE, keyPressed.getCode());
        json.put(KEY_CTRL, keyPressed.isCtrl());
        json.put(KEY_ALT, keyPressed.isAlt());
        json.put(KEY_SHIFT, keyPressed.isShift());
        json.put(KEY_META, keyPressed.isMeta());
        json.put(KEY_LOCATION, keyPressed.getLocation());
        return encodeMessage(PublicSettingsImpl.SERVICE_SETTINGS_INPUT, "keyPressed", json);
    }

    public static Integer decode(Reply data) throws JSONException {
        JSONObject json = decodeMessage(data.getReply());
        return json.getInt(KEY_CODE);
    }
}
