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
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@PowerMockIgnore("javax.net.ssl.*")
public class DialDialDeviceInventoryTest {

    private MockWebServer server;
    private static SSDPMessage SSDP_MESSAGE1, SSDP_MESSAGE2;

    static {
        try {
            SSDP_MESSAGE1 = SSDPMessage.fromString(
                    "HTTP/1.1 200 OK\r\n" +
                            "LOCATION: http://127.0.0.1:30000/dd1.xml\r\n" +
                            "CACHE-CONTROL: max-age=1800\r\n" +
                            "EXT:\r\n" +
                            "BOOTID.UPNP.ORG: 1\r\n" +
                            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
                            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
                            "USN: uuid:11111111-1111-1111-1111-111111111111::"
            );

            SSDP_MESSAGE2 = SSDPMessage.fromString(
                    "HTTP/1.1 200 OK\r\n" +
                            "LOCATION: http://127.0.0.1:30000/dd2.xml\r\n" +
                            "CACHE-CONTROL: max-age=1800\r\n" +
                            "EXT:\r\n" +
                            "BOOTID.UPNP.ORG: 1\r\n" +
                            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
                            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
                            "USN: uuid:22222222-2222-2222-2222-222222222222::"
            );
        } catch (ParseException e) {
            //Something appears to be wrong in the above message
        }
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

    public class TestListener extends TestableCallback<DialDevice> implements DiscoveryListener {

        TestListener() {
            super();
        }

        TestListener(int count) {
            super(count);
        }

        @Override
        public void onDeviceAdded(DialDevice dd) {
            countDown();
        }

        @Override
        public void onDeviceRemoved(DialDevice dd) {
            countDown();
        }
    }

    @Test
    public void testSingleDevice() throws Exception {
        String ddXmlContent = new FileReader().readFile("dd1_WithURLBase.xml");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent));
        TestListener callback = Mockito.spy(new TestListener());
        DialDeviceInventory mgr = new DialDeviceInventory(callback);
        mgr.onLocationSent();
        mgr.onLocationReceived(SSDP_MESSAGE1);
        callback.await(2, TimeUnit.SECONDS);
        verify(callback, times(1)).onDeviceAdded(any(DialDevice.class));
        assertThat(mgr.getDeviceDescriptions(), hasSize(1));
        assertThat(mgr.getDeviceDescriptions(), hasItem(new DialDevice("11111111-1111-1111-1111-111111111111", "device1", "OCast", "OCast", new URL("http://127.0.0.1:8008/apps"))));
    }

    @Test
    public void testTwoDevice() throws Exception {
        String ddXmlContent1 = new FileReader().readFile("dd1_WithURLBase.xml");
        String ddXmlContent2 = new FileReader().readFile("dd2_WithURLBase.xml");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent1));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent2));
        TestListener callback = Mockito.spy(new TestListener(2));
        DialDeviceInventory mgr = new DialDeviceInventory(callback);
        mgr.onLocationSent();
        mgr.onLocationReceived(SSDP_MESSAGE1);
        mgr.onLocationReceived(SSDP_MESSAGE2);
        callback.await(5, TimeUnit.SECONDS);
        verify(callback, times(2)).onDeviceAdded(any(DialDevice.class));
        assertThat(mgr.getDeviceDescriptions(), hasSize(2));
        assertThat(mgr.getDeviceDescriptions(), containsInAnyOrder(new DialDevice("11111111-1111-1111-1111-111111111111",
                        "device1", "OCast", "OCast", new URL("http://127.0.0.1:8008/apps")),
                new DialDevice("22222222-2222-2222-2222-222222222222",
                        "device2", "OCast", "OCast", new URL("http://127.0.0.1:8008/apps"))));
    }

    @Test
    public void testTwoDeviceThenZero() throws Exception {
        String ddXmlContent1 = new FileReader().readFile("dd1_WithURLBase.xml");
        String ddXmlContent2 = new FileReader().readFile("dd2_WithURLBase.xml");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent1));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent2));
        TestListener callback = Mockito.spy(new TestListener(2));
        DialDeviceInventory mgr = new DialDeviceInventory(callback);
        mgr.onLocationSent();
        mgr.onLocationReceived(SSDP_MESSAGE1);
        mgr.onLocationReceived(SSDP_MESSAGE2);
        callback.await(5, TimeUnit.SECONDS);
        mgr.onLocationSent();//no response
        mgr.onLocationSent();//no response
        mgr.onLocationSent();//there has been two location request without any response -> onDeviceRemoved
        verify(callback, times(2)).onDeviceAdded(any(DialDevice.class));
        verify(callback, times(2)).onDeviceRemoved(any(DialDevice.class));
        assertThat(mgr.getDeviceDescriptions(), hasSize(0));
    }

    @Test
    public void testDeviceChanged() throws Exception {
        String ddXmlContent1 = new FileReader().readFile("dd1_WithURLBase.xml");
        String ddXmlContent2 = new FileReader().readFile("dd1_Changed.xml");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent1));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent2));
        TestListener callback = Mockito.spy(new TestListener(2));
        DialDeviceInventory mgr = new DialDeviceInventory(callback);
        mgr.onLocationSent();
        mgr.onLocationReceived(SSDP_MESSAGE1);
        Thread.sleep(1000);//not advised but not completely useless to have this test
        mgr.onLocationSent();
        mgr.onLocationReceived(SSDP_MESSAGE1);
        callback.await(5, TimeUnit.SECONDS);
        verify(callback, times(2)).onDeviceAdded(any(DialDevice.class));
        assertThat(mgr.getDeviceDescriptions(), hasSize(1));
        assertThat(mgr.getDeviceDescriptions(), hasItem(new DialDevice("11111111-1111-1111-1111-111111111111",
                "nouveau nom", "OCast", "OCast", new URL("http://127.0.0.1:8008/apps"))));
    }

    @Test
    public void test2ndDDNotFound() throws Exception {
        String ddXmlContent1 = new FileReader().readFile("dd1_WithURLBase.xml");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ddXmlContent1));
        TestListener callback = Mockito.spy(new TestListener(2));
        DialDeviceInventory mgr = new DialDeviceInventory(callback);
        mgr.onLocationSent();
        mgr.onLocationReceived(SSDP_MESSAGE1);
        Thread.sleep(1000);//not advised but not completely useless to have this test
        mgr.onLocationSent();
        mgr.onLocationReceived(SSDP_MESSAGE1);
        callback.await(5, TimeUnit.SECONDS);
        verify(callback, times(1)).onDeviceAdded(any(DialDevice.class));
        verify(callback, times(0)).onDeviceRemoved(any(DialDevice.class));
        assertThat(mgr.getDeviceDescriptions(), hasSize(1));
    }
}
