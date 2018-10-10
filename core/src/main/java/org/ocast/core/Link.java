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
 * Interface Link is implemented by driver libraries to provide a network link with the device
 */
public interface Link {
    /**
     * Establish a communication link with the device
     * @param onSuccess called when the link has been properly connected
     * @param onFailure called when an error
     */
    void connect(Runnable onSuccess, Consumer<Throwable> onFailure);

    /**
     * Release the communication link
     * @param onSuccess
     */
    void disconnect(Runnable onSuccess);

    /**
     * Returns the URL used to establish the link
     * @return
     */
    String getUrl();

    /**
     * Send a payload on the link for the given {@link org.ocast.core.Driver.Module Module}
     * @param domain the targeted Module
     * @param payload the payload to be sent
     * @param onSuccess called on success, providing the Reply
     * @param onFailure called if an error occured
     * @return
     */
    boolean sendPayload(String domain, JSONObject payload, Consumer<Reply> onSuccess, Consumer<Throwable> onFailure);

    /**
     * Listener interface to be notified of event happening on the Link
     */
    interface LinkListener {
        /**
         * Invoked when an error occured
         * @param t the cause of the error
         */
        void onFailure(Throwable t);

        /**
         * an unsollicited event has been reported on the Link (could be a playback status update)
         * @param link
         * @param driverEvent
         */
        void onEvent(Link link, DriverEvent driverEvent);
    }
}
