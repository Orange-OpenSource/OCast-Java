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

package org.ocast.referencedriver;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocast.core.setting.DeviceSettingController;
import org.ocast.core.setting.UpdateStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceSettingControllerImpl extends DeviceSettingController {

    private static final String SERVICE_NAME = "org.ocast.settings.device";

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
            DeviceSettingEvent deviceSettingEvent = DeviceSettingEvent.decode(message);
            switch(deviceSettingEvent.getName()) {
                case "updateStatus":
                    JSONObject json = deviceSettingEvent.getParams();
                    String state = json.getString("state");
                    String version = json.getString("version");
                    int progress = json.getInt("progress");
                    UpdateStatus updateStatus = new UpdateStatus(0, state, version, progress);
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
