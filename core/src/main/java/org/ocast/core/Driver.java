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

package org.ocast.core;

import org.ocast.core.dial.AdditionalData;
import org.ocast.core.function.Consumer;

import org.json.JSONObject;

/**
 * Manage the connection to a device module and the communication with its browser
 */
public interface Driver {

    /**
     * Internal module accessible within the device
     */
    enum Module {
        /**
         * Module to setup the remote application network channel
         */
        APPLICATION,
        /**
         * Module to setup the public settings network channel
         */
        PUBLIC_SETTINGS,
        /**
         * Module to setup the private settings network channel
         */
        PRIVATE_SETTINGS
    }

    enum Failure {
        LOST,
    }

    /**
     * Establish a network communication channel with the given {@link Module Module}
     * @param module the targeted Module
     * @param onSuccess to be called in case of success.
     * @param onFailure to be called in case of error.
     * @throws DriverException
     */
    void connect(Module module, Runnable onSuccess, Consumer<Throwable> onFailure) throws DriverException;

    /**
     * Establish a network communication channel with the given {@link Module Module}
     * @param module the targeted Module
     * @param onSuccess to be called in case of success.
     * @param onFailure to be called in case of error.
     * @throws DriverException
     */
    void connect(Module module, AdditionalData additionalData, Runnable onSuccess, Consumer<Throwable> onFailure) throws DriverException;

    /**
     * Disconnects the given {@link Module Module}
     * @param module the module to be disconnected
     * @param onSuccess to be called in case of success.
     */
    void disconnect(Module module, Runnable onSuccess);

    /**
     * Sends data the the remote application browser
     * @param data
     * @param onSuccess
     * @param onFailure
     */
    void sendBrowserData(JSONObject data, Consumer<JSONObject> onSuccess, Consumer<Throwable> onFailure);

    /**
     *
     * @param browser
     */
    void registerBrowser(Driver.BrowserListener browser);

    PublicSettings getPublicSettings();

    PrivateSettings getPrivateSettings();

    /**
     * Callback invoked when a failure occured in the Driver
     */
    interface DriverListener {
        void onFailure(Failure failure);
    }

    /**
     * Callback to notify that the remote browser sent data
     */
    interface BrowserListener {
        void onData(JSONObject data);
    }

    /**
     * Factory to create a Driver
     */
    interface Factory {
        /**
         * Create a driver for the given {@link Device} device
         * @param device the device to be controlled by the driver
         * @param listener a listener that will be notified of driver failures
         * @return
         */
        Driver createDriver(Device device, DriverListener listener);
        /**
         * Create a driver for the given {@link Device} device
         * @param device the device to be controlled by the driver
         * @param listener  a listener that will be notified of driver failures
         * @param sslConfig additionnal SSL related configuration if needed
         * @return
         */
        Driver createDriver(Device device, DriverListener listener, SSLConfig sslConfig);
    }
}
