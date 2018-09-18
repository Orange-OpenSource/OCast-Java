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

package org.ocast.discovery;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * A request to retrieve a {@link org.ocast.discovery.DialDevice DialDevice} based on a location URL
 */
public class DeviceDescriptionRequest {
    private static final String TAG = LogTag.DISCOVERY;

    private static final String APP_DIAL_URL_HEADER = "Application-DIAL-URL";
    private static final String APP_URL_HEADER = "Application-URL";
    private final OkHttpClient mClient;

    /**
     * Defines an object that will notify a DeviceDescriptipnRequest result
     */
    public interface Callbacks {
        /**
         * called when a device description has been fetched
         * @param location the location URL that has been fetched
         * @param dd the corresponding {@link org.ocast.discovery.DialDevice DialDevice}
         */
        void onDeviceDescription(URI location, DialDevice dd);

        /**
         * called when a location could not be fetched
         * @param location the location URL that could not be fetched
         */
        void onError(URI location);
    }

    /**
     * Initializes a newly created DeviceDescriptionRequest object using default timeout
     */
    public DeviceDescriptionRequest() {
        this(5,5);
    }

    /**
     * Constructs a new DeviceDescriptionRequest using provided timeout
     * @param connectTimeout connection timeout (seconds)
     * @param readTimeout read timeout (seconds)
     */
    public DeviceDescriptionRequest(int connectTimeout, int readTimeout) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Attempts to retrieve a {@link org.ocast.discovery.DialDevice DialDevice} at a specific
     * location
     * @param location the location URL of a device
     * @param cb the callback notifying the request result
     */
    public void getDeviceDescription(final URI location, final Callbacks cb) {
        Logger.getLogger(TAG).log(Level.FINE,"Retrieving device description through {0}", location);

        DateFormat formatter = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(System.currentTimeMillis());
        formatter.setCalendar(calendar);

        Request request = new Request.Builder()
                .url(location.toString())
                .header("Date", String.format("%s GMT",formatter.format(calendar.getTime())))
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cb.onError(location);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    cb.onError(location);
                }
                Headers responseHeaders = response.headers();
                ResponseBody responseBody = response.body();
                String resultString = responseBody.string();
                String headerApplicationURL = responseHeaders.get(APP_DIAL_URL_HEADER);
                if(headerApplicationURL == null) {
                    headerApplicationURL = responseHeaders.get(APP_URL_HEADER);
                }
                try {
                    DialDevice dd = DialDevice.fromDeviceDescription(resultString, headerApplicationURL, location);
                    cb.onDeviceDescription(location, dd);
                } catch(ParseException e) {
                    Logger.getLogger(TAG).log(Level.SEVERE, "could not parse :" + resultString);
                    cb.onError(location);
                }
            }
        });
    }
}
