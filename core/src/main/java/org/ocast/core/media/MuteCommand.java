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

package org.ocast.core.media;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describes a mute command
 */
class MuteCommand extends MediaCommand {
    private static final String KEY_MUTE = "mute";
    private static final String KEY_MUTE_ENABLED = "mute";

    private final boolean mute;

    MuteCommand(boolean mute) {
        super(KEY_MUTE);
        this.mute = mute;
    }

    @Override
    public JSONObject getParams() throws JSONException {
        JSONObject params = new JSONObject();
        params.put(KEY_MUTE_ENABLED, mute);
        return params;
    }
}
