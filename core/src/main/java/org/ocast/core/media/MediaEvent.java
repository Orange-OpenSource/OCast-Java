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

public class MediaEvent {

    private static final String KEY_NAME = "name";
    private static final String KEY_PARAMS = "params";
    private final String name;
    private final JSONObject params;

    public MediaEvent(String name, JSONObject params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public JSONObject getParams() {
        return params;
    }

    public static MediaEvent decode(JSONObject input) throws JSONException {
        String name = input.getString(KEY_NAME);
        JSONObject params = input.getJSONObject(KEY_PARAMS);

        return new MediaEvent(name, params);


    }
}
