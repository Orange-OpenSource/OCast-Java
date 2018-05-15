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


import org.ocast.core.function.Consumer;

import org.json.JSONObject;

/**
 * Used for custom messaging. It allows an application to send and receive customized messages to/from
 * the web application.
 */
public abstract class DataStream {
    private final String serviceName;
    private Browser browser;

    /**
     * Create a Datastream for the given service name
     * @param serviceName the service name
     */
    public DataStream(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Attach the stream to the provided {@link org.ocast.core.Browser Browser}
     * @param browser the
     */
    public void setBrowser(Browser browser) {
        this.browser = browser;
        this.browser.registerStream(this);
    }

    /**
     * Remove the stream to the provided {@link org.ocast.core.Browser Browser}
     * @param browser the
     */
    public void unsetBrowser(Browser browser) {
        this.browser.unregisterStream(this);
        this.browser = null;
    }

    /**
     * Send a message on the DataStream
     * @param message the message to be sent
     * @param onSuccess to be called on success
     * @param onFailure to be called if an error occured
     */
    public void sendMessage(JSONObject message, Consumer<JSONObject> onSuccess, Consumer<Throwable> onFailure) {
        if (browser != null) {
            browser.sendData(serviceName, message, onSuccess, onFailure);
        }
    }

    /**
     * Subclass must implement this method to manage incoming messages
     * @param message the incoming message
     */
    public abstract void onMessage(JSONObject message);

    /**
     * Get the service name this DataStream is used for.
     * @return the service name if this DataStream
     */
    public String getServiceName() {
        return serviceName;
    }
}
