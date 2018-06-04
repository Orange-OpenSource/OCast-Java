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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocast.core.setting.AxeInfo;
import org.ocast.core.setting.InputSettingController;
import org.ocast.referencedriver.setting.SettingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputSettingControllerImpl extends InputSettingController {

    private static final String SERVICE_NAME = "org.ocast.settings.input";

    private static final String KEY_KEY = "key";
    private static final String KEY_CODE = "code";
    private static final String KEY_CTRL = "ctrl";
    private static final String KEY_ALT = "alt";
    private static final String KEY_SHIFT = "shift";
    private static final String KEY_META = "meta";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_BUTTONS = "buttons";
    private static final String KEY_AXES = "axes";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";

    /**
     * Instanciate a DeviceSettingControllerImpl
     * @param listener the listener to be notified of input events
     */
    public InputSettingControllerImpl(InputSettingControllerListener listener) {
        super(SERVICE_NAME, listener);
    }

    @Override
    public void onMessage(JSONObject message) {
        Logger.getLogger(SERVICE_NAME).log(Level.FINEST, "onMessage: {0}", message);
        try {
            SettingEvent settingEvent = SettingEvent.decode(message);
            switch(settingEvent.getName()) {
                case "keyPressed":
                    JSONObject keyPressed = settingEvent.getParams();
                    String key = keyPressed.getString(KEY_KEY);
                    String code = keyPressed.getString(KEY_CODE);
                    boolean ctrl = keyPressed.getBoolean(KEY_CTRL);
                    boolean alt = keyPressed.getBoolean(KEY_ALT);
                    boolean shift = keyPressed.getBoolean(KEY_SHIFT);
                    boolean meta = keyPressed.getBoolean(KEY_META);
                    int location = keyPressed.getInt(KEY_LOCATION);
                    listener.onKeyPressed(key, code, ctrl, alt, shift, meta, location);
                    break;
                case "mouseEvent":
                    JSONObject mouseEvent = settingEvent.getParams();
                    int x = mouseEvent.getInt(KEY_X);
                    int y = mouseEvent.getInt(KEY_Y);
                    int mouseButtons = mouseEvent.getInt(KEY_BUTTONS);
                    listener.onMouseEvent(x, y , mouseButtons);
                    break;
                case "gamepadEvent":
                    JSONObject gamepadEvent = settingEvent.getParams();
                    List<AxeInfo> axes = new ArrayList<>();
                    JSONArray jAxes = gamepadEvent.optJSONArray(KEY_AXES);
                    if (jAxes != null) {
                        for (int p = 0; p < jAxes.length(); p++) {
                            JSONObject jAxe = jAxes.getJSONObject(p);
                            int axeX = jAxe.getInt(KEY_X);
                            int axeY = jAxe.getInt(KEY_Y);
                            int axeButtons = jAxe.getInt(KEY_BUTTONS);
                            AxeInfo axe = new AxeInfo(axeX, axeY, axeButtons);
                            axes.add(axe);
                        }
                    }
                    int gamepadButtons = gamepadEvent.getInt(KEY_BUTTONS);
                    listener.onGamepadEvent(axes, gamepadButtons);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            Logger.getLogger(SERVICE_NAME).log(Level.WARNING, "could not parse message", e);
        }
    }
}
