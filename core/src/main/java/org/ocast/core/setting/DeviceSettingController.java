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

package org.ocast.core.setting;

import org.json.JSONObject;
import org.ocast.core.DataStream;

public class DeviceSettingController extends DataStream {

    protected final DeviceSettingControllerListener listener;

    public DeviceSettingController(String serviceName, DeviceSettingControllerListener listener) {
        super(serviceName);
        this.listener = listener;
    }

    /**
     * Provides informations on update status (progress, status,...)
     */
    public interface DeviceSettingControllerListener {
        /**
         * called each seconds for downloading, otherwise when state changes.
         * @param updateStatus the updated update status
         */
        void onUpdateStatus(UpdateStatus updateStatus);
    }

    @Override
    public void onMessage(JSONObject message) {
    }
}
