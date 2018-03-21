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

package org.ocast.referencedriver.settings;

import org.ocast.core.Link;
import org.ocast.core.PublicSettings;
import org.ocast.core.VersionInfo;
import org.ocast.core.function.Consumer;
import org.ocast.core.setting.UpdateStatus;

import static org.ocast.core.CallbackThreadHandler.callback;

public class PublicSettingsImpl implements PublicSettings {

    private final String DOMAIN_SETTINGS = "settings";
    public static final String SERVICE_SETTINGS_DEVICE = "org.ocast.settings.device";

    private final Link link;

    public PublicSettingsImpl(Link link) {
        this.link = link;
    }

    @Override
    public void getUpdateStatus(Consumer<UpdateStatus> onSuccess, Consumer<Throwable> onFailure) {
        link.sendPayload(DOMAIN_SETTINGS, GetUpdateStatus.encode(),
                callback(r -> onSuccess.accept(GetUpdateStatus.decode(r))),
                callback(onFailure));
    }

    @Override
    public void getDeviceID(Consumer<String> onSuccess, Consumer<Throwable> onFailure) {
        link.sendPayload(DOMAIN_SETTINGS, GetDeviceID.encode(),
                callback(r -> onSuccess.accept(GetDeviceID.decode(r))),
                callback(onFailure));
    }

    @Override
    public void getVersion(Consumer<VersionInfo> onSuccess, Consumer<Throwable> onFailure) {
    }
}
