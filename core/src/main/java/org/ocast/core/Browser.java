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
import org.ocast.core.function.ThrowingConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ocast.core.CallbackThreadHandler.callback;

/**
 * Class managing communication with the remote browser
 */
public class Browser implements Driver.BrowserListener {
    private static final String TAG = LogTag.BROWSER;

    private static final String KEY_SERVICE = "service";
    private static final String KEY_DATA = "data";

    private final Driver driver;
    private final HashMap<String, DataStream> registeredStreams = new HashMap<>(2);


    /**
     * Initializes a new Browser that will use the given {@link org.ocast.core.Driver Driver} to communicate
     * with the device.
     * @param driver a {@link org.ocast.core.Driver Driver} object that will be used to send/receive data
     */
    public Browser (Driver driver){
        this.driver = driver;
        this.driver.registerBrowser(this);
    }

    /**
     * Sends data to the browser specifying a specific service to be addressed. It basically wraps
     * data adding the service key
     * @param service the targeted service
     * @param data data to be sent
     * @param onSuccess to be called on success
     * @param onFailure to be called on failure
     */
    public void sendData(String service, JSONObject data, Consumer<JSONObject> onSuccess, Consumer<Throwable> onFailure) {
        JSONObject messageContent = new JSONObject();
        try {
            messageContent.put(KEY_SERVICE, service);
            messageContent.put(KEY_DATA, data);
        } catch (JSONException e) {
            //That definitely should not happen.
            throw new RuntimeException(e);
        }
        driver.sendBrowserData(messageContent, ThrowingConsumer.checked(
                json -> callback(onSuccess).accept(json.getJSONObject(KEY_DATA)),
                    callback(onFailure)),
                    callback(onFailure));
    }

    /**
     * Registers a {@link org.ocast.core.DataStream DataStream} that will be able to communicate with
     * the remote browser
     * @param dataStream a {@link org.ocast.core.DataStream DataStream} object that will be managed by this Browser
     */
    public void registerStream(DataStream dataStream) {
        registeredStreams.put(dataStream.getServiceName(), dataStream);
    }

    /**
     * Unregisters a {@link org.ocast.core.DataStream DataStream}
     * @param dataStream a {@link org.ocast.core.DataStream DataStream} object that will be unmanaged by this Browser
     */
    public void unregisterStream(DataStream dataStream) {
        registeredStreams.remove(dataStream.getServiceName());
    }

    @Override
    public void onData(JSONObject browserData) {
        try {
            Logger.getLogger(TAG).log(Level.FINEST,"received: {0} ", browserData.toString());
            String serviceName = browserData.getString(KEY_SERVICE);
            JSONObject data = browserData.getJSONObject(KEY_DATA);
            DataStream serviceStream = registeredStreams.get(serviceName);
            if(serviceStream != null) {
                callback( () -> serviceStream.onMessage(data)).run();
            }
        } catch (JSONException e) {
            Logger.getLogger(TAG).log(Level.SEVERE,"onData failed", e);
        }

    }
}
