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

public class SettingEvent {
    private static final String KEY_NAME = "name";
    private static final String KEY_PARAMS = "params";
    private final String name;
    private final JSONObject params;

    public SettingEvent(String name, JSONObject params) {
        this.name = name;
        this.params = params;
    }

    /**
     * Gets the event name
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the data associated with the event
     * @return a JSONObject holding the event specific data
     */
    public JSONObject getParams() {
        return params;
    }

    /**
     * Decodes the input json as a SettingEvent
     * @param input input JSON
     * @return a SettingEvent
     * @throws JSONException if the json is not correctly formatted
     */
    public static SettingEvent decode(JSONObject input) throws JSONException {
        String name = input.getString(KEY_NAME);
        JSONObject params = input.getJSONObject(KEY_PARAMS);
        return new SettingEvent(name, params);
    }
}
