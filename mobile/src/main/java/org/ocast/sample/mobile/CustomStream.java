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

package org.ocast.sample.mobile;

import org.ocast.core.DataStream;
import org.ocast.core.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomStream extends DataStream {
    public static final String SERVICE_NAME = "org.ocast.custom";

    public CustomStream() {
        super(SERVICE_NAME);
    }

    @Override
    public void onMessage(JSONObject message) {

    }

    public void sendCustomData(Consumer<JSONObject> onSuccess, Consumer<Throwable> onFailure) {
        JSONObject customData = new JSONObject();
        try {
            customData.put("custom", "this is some custom data");
            sendMessage(customData, onSuccess, onFailure);
        } catch (JSONException e) {
            //should not happen
        }
    }
}
