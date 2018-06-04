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

package org.ocast.referencedriver.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocast.core.setting.DeviceSettingController;
import org.ocast.core.setting.SettingReplyCode;
import org.ocast.core.setting.UpdateStatus;
import org.ocast.referencedriver.setting.SettingEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A dedicated {@link org.ocast.core.DataStream DataStream} that manages public setting
 */
public class DeviceSettingControllerImpl extends DeviceSettingController {

    private static final String SERVICE_NAME = "org.ocast.settings.device";

    private static final String KEY_STATE = "state";
    private static final String KEY_VERSION = "version";
    private static final String KEY_PROGRESS = "progress";

    /**
     * Instanciate a DeviceSettingControllerImpl
     * @param listener the listener to be notified of update status updates
     */
    public DeviceSettingControllerImpl(DeviceSettingControllerListener listener) {
        super(SERVICE_NAME, listener);
    }

    @Override
    public void onMessage(JSONObject message) {
        Logger.getLogger(SERVICE_NAME).log(Level.FINEST, "onMessage: {0}", message);
        try {
            SettingEvent settingEvent = SettingEvent.decode(message);
            switch(settingEvent.getName()) {
                case "updateStatus":
                    JSONObject json = settingEvent.getParams();
                    String state = json.getString(KEY_STATE);
                    String version = json.getString(KEY_VERSION);
                    int progress = json.getInt(KEY_PROGRESS);
                    UpdateStatus updateStatus = new UpdateStatus(SettingReplyCode.SUCCESS.getCode(), state, version, progress);
                    listener.onUpdateStatus(updateStatus);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            Logger.getLogger(SERVICE_NAME).log(Level.WARNING, "could not parse message", e);
        }
    }
}
