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

import org.ocast.core.dial.DialException;
import org.ocast.core.dial.DialService;
import org.ocast.core.dial.AdditionalData;
import org.ocast.core.dial.DialServiceParser;
import org.ocast.core.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.ocast.core.CallbackThreadHandler.callback;

/**
 * Provides means to control the web application.
 * The ApplicationController also gives you access to the {@link org.ocast.core.media.MediaController}
 * which provides your application with basic cast control functions.
 * A reference to the ApplicationController can be obtained using the {@link org.ocast.core.DeviceManager}
 * class via the {@link org.ocast.core.DeviceManager#getApplicationController getApplicationController} method.
 */
public class ApplicationController extends DataStream {
    private static final String TAG = LogTag.APPLICATION;

    private static final String SERVICE_WEBAPP = "org.ocast.webapp";
    private static final int CONNECTED_TIMEOUT = 10;
    private static final String KEY_NAME = "name";
    private static final String KEY_CONNECTION_STATUS = "connectionStatus";
    private static final String KEY_PARAMS = "params";
    private static final String KEY_STATUS = "status";
    private static final String STATE_CONNECTED = "connected";

    private final OkHttpClient httpClient;
    private DialService<AdditionalData> dialService;
    private final Driver driver;
    private CountDownLatch connectedLatch;
    private final Browser browser;

    public ApplicationController(DialService dialService, Driver driver) {
        super(SERVICE_WEBAPP);
        this.dialService = dialService;
        this.driver = driver;
        browser = new Browser(driver);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS);
        builder.addInterceptor(loggingInterceptor);
        httpClient = builder.build();
    }

    /**
     *Starts the web application on the device. Will restart the web application if it is already running
     * on the device.
     * @param onSuccess to be called in case of success.
     * @param onFailure to be called in case of error
     */
    public void start(Runnable onSuccess, Consumer<Throwable> onFailure) {
        AdditionalData additionalData = dialService.getAdditionalData();
        manageStream(this);
        try {
            Runnable onConnectSuccess = () -> {
                connectedLatch = new CountDownLatch(1);
                Request request = new Request.Builder().url(dialService.getBaseURL()).post(RequestBody.create(null, "")).build();
                StartInterceptor startInterceptor = new StartInterceptor();
                OkHttpClient client = httpClient.newBuilder().addInterceptor(startInterceptor).build();
                Call call = client.newCall(request);
                call.enqueue(new DialCallbackRunnable(callback(onSuccess), callback(onFailure), this::isStartSuccess));
            };

            if (additionalData != null) {
                driver.connect(Driver.Module.APPLICATION, additionalData,
                        onConnectSuccess,
                        onFailure);
            } else {
                driver.connect(Driver.Module.APPLICATION,
                        onConnectSuccess,
                        onFailure);
            }
        } catch (DriverException e) {
            onFailure.accept(e);
        }
    }


    /**
     * Joins the web application on the device. Fails if another web application is already running on the device.
     * @param onSuccess to be called in case of success.
     * @param onFailure to be called in case of error
     */
    public void join(Runnable onSuccess, Consumer<Throwable> onFailure) {
        AdditionalData additionalData = dialService.getAdditionalData();
        manageStream(this);
        try {
            Runnable onConnectSuccess = () -> {
                Request request = new Request.Builder().url(dialService.getBaseURL()).build();
                Call call = httpClient.newCall(request);
                call.enqueue(new DialCallbackRunnable(
                        callback(onSuccess),
                        callback(onFailure),
                        this::isJoinSuccess));
            };

            if (additionalData != null) {
                driver.connect(Driver.Module.APPLICATION, additionalData,
                        onConnectSuccess,
                        onFailure);
            } else {
                driver.connect(Driver.Module.APPLICATION,
                        onConnectSuccess,
                        onFailure);
            }
        } catch (DriverException e) {
            onFailure.accept(e);
        }
    }

    /**
     *  Stops the web application on the device
     * @param onSuccess to be called in case of success.
     * @param onFailure to be called in case of error
     */
    public void stop(Runnable onSuccess, Consumer<Throwable> onFailure) {
        try {
            URL runLink = dialService.getRunLink();
            if (runLink == null) {
                throw new ApplicationException(DialError.APPLICATION_NOT_RUNNING);
            }
            Request request = new Request.Builder().url(runLink).delete().build();
            Call call = httpClient.newCall(request);
            call.enqueue(new DialCallbackRunnable(callback(onSuccess), callback(onFailure), (c, r) -> driver.disconnect(Driver.Module.APPLICATION, onSuccess)));
        } catch (ApplicationException e) {
            onFailure.accept(e);
        }
    }

    /**
     * Used to get control over a user's specific stream
     * @param stream custom stream to be managed
     */
    public void manageStream(DataStream stream) {
        stream.setBrowser(browser);
    }

    @Override
    public void onMessage(JSONObject message) {
        try {
            if(isConnectedEvent(message) && connectedLatch != null) {
                connectedLatch.countDown();
            }
        } catch (JSONException e) {
            Logger.getLogger(TAG).log(Level.SEVERE, "onMessage failure", e);
        }
    }

    private void isStartSuccess(Call call, Response response) throws ApplicationException {
        try {
            ResponseBody responseBody = response.body();
            Reader resultString = responseBody.charStream();
            if (connectedLatch.await(CONNECTED_TIMEOUT, TimeUnit.SECONDS)) {
                DialServiceParser parser = new DialServiceParser(call.request().url().url());
                dialService = parser.parse(resultString);
            } else {
                throw new ApplicationException(DialError.TIMEOUT);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApplicationException(DialError.INTERNAL_ERROR);
        } catch (DialException e) {
            throw new ApplicationException(DialError.INTERNAL_ERROR, e);
        }
    }

    private void isJoinSuccess(Call call, Response response) throws ApplicationException {
        ResponseBody responseBody = response.body();
        Reader resultString = responseBody.charStream();
        try {
            DialServiceParser parser = new DialServiceParser(call.request().url().url());
            dialService = parser.parse(resultString);
            if (!DialService.State.RUNNING.equals(dialService.getState())) {
                throw new ApplicationException(DialError.APPLICATION_NOT_RUNNING);
            }
        } catch (DialException e) {
            throw new ApplicationException(DialError.INTERNAL_ERROR, e);
        }
    }

    private boolean isConnectedEvent(JSONObject message) throws JSONException {
        String name = message.getString(KEY_NAME);
        if(name.equals(KEY_CONNECTION_STATUS)) {
            String status = message.getJSONObject(KEY_PARAMS).getString(KEY_STATUS);
            return STATE_CONNECTED.equals(status);
        }
        return false;
    }

    private class StartInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            if(response.code() == 201) {
                Request newRequest;
                newRequest = new Request.Builder().url(dialService.getBaseURL()).build();
                return chain.proceed(newRequest);
            }
            return response;
        }
    }
}
