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

import org.ocast.core.dial.DialServiceParser;
import org.ocast.core.dial.DialException;
import org.ocast.core.dial.DialService;
import org.ocast.core.function.Consumer;

import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.ocast.core.CallbackThreadHandler.callback;

/**
 * Device manager is used to get control over your device.
 */
public class DeviceManager implements Driver.DriverListener {

    private static final String TAG = LogTag.DEVICE;

    public enum Failure {
        DEVICE_LOST,
    }

    private static final Map<String, Driver.Factory> mRegisteredDrivers = new HashMap<>(2);
    private final Driver driver;

    private final Consumer<Failure> listener;
    private final URL baseDialURL;
    private final OkHttpClient httpClient;

    /**
     * Initializes a new DeviceManager
     * @param device the device to be managed
     * @param listener to be called to notify failures
     */
    public DeviceManager(Device device, Consumer<Failure> listener) {
        driver = createDriver(device);
        baseDialURL = device.getDialURL();
        this.listener = listener;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    /**
     * Initializes a new DeviceManager
     * @param device the device to be managed
     * @param sslConfig SSL configuration info
     * @param listener to be called to notify failures
     */
    public DeviceManager(Device device, SSLConfig sslConfig, Consumer<Failure> listener) {
        driver = createDriver(device, sslConfig);
        baseDialURL = device.getDialURL();
        this.listener = listener;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    /**
     * Registers a driver to connect to a device.
     * @param id Driver manufacturer's name (cases sensitive).
     *           This value must match the manufacturer name present in the response to a MSEARCH Target
     * @param factory A {@link org.ocast.core.Driver.Factory Driver.Factory}  that instantiate the relevant Driver.
     */
    public static void registerDriver(String id, Driver.Factory factory) {
        mRegisteredDrivers.put(id, factory);
    }


    /**
     *  Used to get a reference to the public Settings controller class
     * @param onSuccess to be called in case of success. Returns a reference to the publicSetting.
     * @param onFailure to be called in case of error
     */
    public void getPublicSettings(Consumer<PublicSettings> onSuccess, Consumer<Throwable> onFailure) {
        try {
            driver.connect(Driver.Module.PUBLIC_SETTINGS, () -> callback(onSuccess).accept(driver.getPublicSettings()), onFailure);
        } catch (DriverException e) {
            onFailure.accept(e);
        }
    }

    /**
     * Used to release the public settings controller resources
     * @param callback to be called once released
     */
    public void releasePublicSettings(Runnable callback) {
        driver.disconnect(Driver.Module.PUBLIC_SETTINGS,callback);
    }

    /**
     *  Used to get a reference to the private Setting controller class
     * @param onSuccess to be called in case of success. Returns a reference to the publicSetting.
     * @param onFailure to be called in case of error
     */
    public void getPrivateSettings(Consumer<PublicSettings> onSuccess, Consumer<Throwable> onFailure) {
        try {
            driver.connect(Driver.Module.PRIVATE_SETTINGS, () -> callback(onSuccess).accept(driver.getPublicSettings()), onFailure);
        } catch (DriverException e) {
            onFailure.accept(e);
        }
    }

    /**
     * Used to release the private settings controller resources
     * @param callback to be called once released
     */
    public void releasePrivateSettings(Runnable callback) {
        driver.disconnect(Driver.Module.PRIVATE_SETTINGS,callback);
    }

    /**
     * Used to retrieve a reference to the {@link org.ocast.core.ApplicationController ApplicationController}
     * @param appId the application id to be managed
     * @param onSuccess to be called in case of success. Returns a reference to the {@link org.ocast.core.ApplicationController ApplicationController}
     * @param onFailure to be called in case of error
     */
    public void getApplicationController(String appId, Consumer<ApplicationController> onSuccess, Consumer<Throwable> onFailure) {
        Request request = new Request.Builder().url(getApplicationURL(appId)).build();
        Call call = httpClient.newCall(request);
        call.enqueue(new DialCallbackConsumer<>(callback(onSuccess), callback(onFailure), this::isGetSuccess));
    }

    @Override
    public void onFailure(Driver.Failure failure) {
        Failure deviceFailure;
        if(failure == Driver.Failure.LOST) {
            deviceFailure = DeviceManager.Failure.DEVICE_LOST;
            listener.accept(deviceFailure);
        }
    }

    private ApplicationController isGetSuccess(Call call, Response response) throws ApplicationException {
        ResponseBody responseBody = response.body();
        Reader resultString = responseBody.charStream();
        DialService service;
        DialServiceParser parser;
        try {
            parser = new DialServiceParser(call.request().url().url());
            service = parser.parse(resultString);
            Logger.getLogger(TAG).log(Level.FINEST, "DialService {0}", service);
            return new ApplicationController(service, driver);
        } catch (DialException e) {
            throw new ApplicationException(DialError.INTERNAL_ERROR, e);
        }
    }

    private Driver createDriver(Device device) {
        try {
            return mRegisteredDrivers.get(device.getManufacturer()).createDriver(device, this);
        } catch(Exception e) {
            throw new IllegalArgumentException("could not create driver. Did you register it properly ?");

        }
    }

    private Driver createDriver(Device device, SSLConfig sslConfig) {
        try {
            return mRegisteredDrivers.get(device.getManufacturer()).createDriver(device, this, sslConfig);
        } catch(Exception e) {
            throw new IllegalArgumentException("could not create driver. Did you register it properly ?");

        }
    }

    private String getApplicationURL(String appId) {
        return String.format("%s/%s", baseDialURL, appId);
    }
}
