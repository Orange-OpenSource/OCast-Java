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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@PowerMockIgnore("javax.net.ssl.*")
public class DialDeviceDescriptionRequestTest {

    MockWebServer server;

    public class TestCallback extends TestableCallback<DialDevice> implements DeviceDescriptionRequest.Callbacks {
        @Override
        public void onDeviceDescription(URI location, DialDevice dd) {
            setResult(dd);
            countDown();
        }

        @Override
        public void onError(URI location) {
            countDown();
        }
    }

    @BeforeClass
    public static void initLogger() {
        System.setProperty("java.util.logging.config.file", ClassLoader.getSystemResource("logging.properties").getPath());
    }

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start(30000);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void getDeviceDescription200() throws Exception {
        String ddXmlContent = new FileReader().readFile("dd1_WithURLBase.xml");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent));

        DeviceDescriptionRequest request = new DeviceDescriptionRequest();

        HttpUrl url = server.url("/dd.xml");
        System.out.println(url);
        TestCallback callback = Mockito.spy(new TestCallback());
        request.getDeviceDescription(URI.create(url.toString()), callback);
        callback.await(5000, TimeUnit.MILLISECONDS);
        verify(callback, times(1)).onDeviceDescription(eq(url.uri()), any(DialDevice.class));
        assertEquals("11111111-1111-1111-1111-111111111111", callback.getResult().getUuid());
    }

    @Test
    public void getDeviceDescriptionURLInHeader() throws Exception {
        String ddXmlContent = new FileReader().readFile("dd_WithoutURLBase.xml");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Application-DIAL-URL", "http://127.0.0.1:8008/apps")
                .setBody(ddXmlContent));

        DeviceDescriptionRequest request = new DeviceDescriptionRequest();

        HttpUrl url = server.url("/dd.xml");
        TestCallback callback = Mockito.spy(new TestCallback());
        request.getDeviceDescription(URI.create(url.toString()), callback);
        callback.await(5000, TimeUnit.MILLISECONDS);
        verify(callback, times(1)).onDeviceDescription(eq(url.uri()), any(DialDevice.class));
        assertThat(callback.getResult().getDialURI().toString(), is(equalTo("http://127.0.0.1:8008/apps")));
    }

    @Test
    public void getDeviceDescription404() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("not found"));

        DeviceDescriptionRequest request = new DeviceDescriptionRequest();

        HttpUrl url = server.url("/dd.xml");
        TestCallback callback = Mockito.spy(new TestCallback());
        request.getDeviceDescription(URI.create(url.toString()), callback);
        callback.await(5000, TimeUnit.MILLISECONDS);
        verify(callback, times(1)).onError(eq(url.uri()));
    }

    @Test
    public void getDeviceDescriptionNoServer() throws Exception {
        DeviceDescriptionRequest request = new DeviceDescriptionRequest();

        HttpUrl url = server.url("/dd.xml");
        TestCallback callback = Mockito.spy(new TestCallback());
        request.getDeviceDescription(URI.create(url.toString()), callback);
        callback.await(20000, TimeUnit.MILLISECONDS);
        verify(callback, times(1)).onError(eq(url.uri()));
    }

    @Test
    public void getDeviceDescriptionInvalidXml() throws Exception {
        String ddXmlContent = new FileReader().readFile("dd_Invalid.xml");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent));

        DeviceDescriptionRequest request = new DeviceDescriptionRequest();

        HttpUrl url = server.url("/dd.xml");
        TestCallback callback = Mockito.spy(new TestCallback());
        request.getDeviceDescription(URI.create(url.toString()), callback);
        callback.await(5000, TimeUnit.MILLISECONDS);
        verify(callback, times(1)).onError(eq(url.uri()));
    }

}
