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

package org.ocast.referencedriver.setting;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocast.core.Reply;
import org.ocast.referencedriver.payload.CommandPayload;

public class GetDeviceID extends CommandPayload {

    private static final String KEY_ID = "id";

    public static JSONObject encode() {
        return encodeMessage(PublicSettingsImpl.SERVICE_SETTINGS_DEVICE, "getDeviceID", new JSONObject());
    }

    public static String decode(Reply data) throws JSONException {
        JSONObject json = decodeMessage(data.getReply());
        return json.getString(KEY_ID);
    }
}
