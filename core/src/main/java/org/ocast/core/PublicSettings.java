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

package org.ocast.core;

import org.ocast.core.function.Consumer;
import org.ocast.core.setting.DeviceId;
import org.ocast.core.setting.GamepadEvent;
import org.ocast.core.setting.KeyPressed;
import org.ocast.core.setting.MouseEvent;
import org.ocast.core.setting.UpdateStatus;

/**
 * Interface PublicSettings is implemented by driver libraries to provide access to public settings configuration
 */
public interface PublicSettings {

    /**
     * Retrieves the {@link org.ocast.core.setting.UpdateStatus UpdateStatus} of the device
     * @param onSuccess to be called on success
     * @param onFailure to be called when an error occured
     */
    void getUpdateStatus(Consumer<UpdateStatus> onSuccess, Consumer<Throwable> onFailure);

    /**
     * Retrieve the unique ID of the device (serial number)
     * @param onSuccess to be called on success
     * @param onFailure to be called when an error occured
     */
    void getDeviceID(Consumer<DeviceId> onSuccess, Consumer<Throwable> onFailure);

    /**
     * Send Virtual key event
     * @param keyPressed the key pressed
     * @param onSuccess to be called on success
     * @param onFailure to be called when an error occured
     */
    void sendKeyPressed(KeyPressed keyPressed, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    /**
     * Send Virtual mouse click & movement event
     * @param mouseEvent the mouse event
     * @param onSuccess to be called on success
     * @param onFailure to be called when an error occured
     */
    void sendMouseEvent(MouseEvent mouseEvent, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    /**
     * Send Virtual gamepad event
     * @param gamepadEvent the gamepad event
     * @param onSuccess to be called on success
     * @param onFailure to be called when an error occured
     */
    void sendGamepadEvent(GamepadEvent gamepadEvent, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);
}
