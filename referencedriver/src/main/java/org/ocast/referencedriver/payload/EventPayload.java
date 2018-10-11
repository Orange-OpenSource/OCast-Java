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

package org.ocast.referencedriver.payload;

import org.ocast.core.DriverEvent;

import org.json.JSONObject;

/**
 * Defines a payload representing an event
 */
public class EventPayload implements DriverEvent {
    private String domain;
    private static final String KEY_SERVICE = "service";

    public EventPayload(String domain, JSONObject message) {
        this.domain = domain;
        this.message = message;
    }

    private JSONObject message;

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public JSONObject getData() {
        return message;
    }

    public String getService() {
        return message.getString(KEY_SERVICE);
    }
}
