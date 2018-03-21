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

package org.ocast.referencedriver.settings;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocast.core.Reply;
import org.ocast.core.setting.UpdateStatus;
import org.ocast.referencedriver.payload.CommandPayload;

import static org.ocast.referencedriver.settings.PublicSettingsImpl.SERVICE_SETTINGS_DEVICE;

public class GetUpdateStatus extends CommandPayload {

    public static final String KEY_CODE = "code";
    public static final String KEY_STATE = "state";
    public static final String KEY_VERSION = "version";
    public static final String KEY_PROGRESS = "progress";

    public static JSONObject encode() {
        return encodeMessage(SERVICE_SETTINGS_DEVICE, "getUpdateStatus", new JSONObject());
    }

    public static UpdateStatus decode(Reply data) throws JSONException {
        JSONObject json = decodeMessage(data.getReply());
        final int code = json.getInt(KEY_CODE);
        final String state = json.getString(KEY_STATE);
        final String version = json.getString(KEY_VERSION);
        final int progress = json.getInt(KEY_PROGRESS);
        return new UpdateStatus(code, state, version, progress);
    }
}
