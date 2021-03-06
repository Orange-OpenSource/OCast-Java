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
 * Represents a media related event notified by the remote application. Can be an update of the
 * playback status or a change of the metadata.
 */
public class MediaEvent {

    private static final String KEY_NAME = "name";
    private static final String KEY_PARAMS = "params";
    private static final String KEY_OPTIONS = "options";
    private final String name;
    private final JSONObject params;
    private final JSONObject options;

    public MediaEvent(String name, JSONObject params, JSONObject options) {
        this.name = name;
        this.params = params;
        this.options = options;
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
     * Retrieves the options associated with the event
     * @return a JSONObject holding the event specific options
     */
    public JSONObject getOptions() {
        return options;
    }

    /**
     * Decodes the input json as a MediaEvent
     * @param input
     * @return a MediaEvent
     * @throws JSONException if the json is not correctly formatted
     */
    public static MediaEvent decode(JSONObject input) throws JSONException {
        String name = input.getString(KEY_NAME);
        JSONObject params = input.getJSONObject(KEY_PARAMS);
        JSONObject options = input.optJSONObject(KEY_OPTIONS);
        return new MediaEvent(name, params, options);
    }
}
