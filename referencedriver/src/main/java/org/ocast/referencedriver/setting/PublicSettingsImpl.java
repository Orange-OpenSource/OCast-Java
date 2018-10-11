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

import org.ocast.core.Link;
import org.ocast.core.PublicSettings;
import org.ocast.core.function.Consumer;
import org.ocast.core.setting.GamepadEvent;
import org.ocast.core.setting.KeyPressed;
import org.ocast.core.setting.MouseEvent;
import org.ocast.core.setting.UpdateStatus;

import static org.ocast.core.CallbackThreadHandler.callback;

public class PublicSettingsImpl implements PublicSettings {

    private static final String DOMAIN_SETTINGS = "settings";
    static final String SERVICE_SETTINGS_DEVICE = "org.ocast.settings.device";

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
    public void sendKeyPressed(KeyPressed keyPressed, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure) {
        link.sendPayload(DOMAIN_SETTINGS, SendKeyPressed.encode(keyPressed),
                callback(r -> onSuccess.accept(SendKeyPressed.decode(r))),
                callback(onFailure));
    }

    @Override
    public void sendMouseEvent(MouseEvent mouseEvent, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure) {
        link.sendPayload(DOMAIN_SETTINGS, SendMouseEvents.encode(mouseEvent),
                callback(r -> onSuccess.accept(SendMouseEvents.decode(r))),
                callback(onFailure));
    }

    @Override
    public void sendGamepadEvent(GamepadEvent gamepadEvent, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure) {
        link.sendPayload(DOMAIN_SETTINGS, SendGamepadEvent.encode(gamepadEvent),
                callback(r -> onSuccess.accept(SendGamepadEvent.decode(r))),
                callback(onFailure));
    }
}
