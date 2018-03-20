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

package org.ocast.referencedriver.payload;


import org.json.JSONException;
import org.json.JSONObject;

public class PayloadCommand {

    private static final String KEY_SERVICE = "service";
    private static final String KEY_DATA = "data";

    public static JSONObject getPayload(String service, JSONObject data) {
        JSONObject messageContent = new JSONObject();
        try {
            messageContent.put(KEY_SERVICE, service);
            messageContent.put(KEY_DATA, data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return messageContent;
    }
}
