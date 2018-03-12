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

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SSDPManagerTest {

    private static SSDPMessage SSDP_MESSAGE_RESPONSE_OK;
    private static SSDPMessage SSDP_MESSAGE_RESPONSE_OK2;
    private static SSDPMessage SSDP_MESSAGE_WITHOUT_LOCATION;

    static {
        try {
            SSDP_MESSAGE_RESPONSE_OK = SSDPMessage.fromString(SSDPMessageTest.PAYLOAD_RESPONSE_OK);
            SSDP_MESSAGE_RESPONSE_OK2 = SSDPMessage.fromString(SSDPMessageTest.PAYLOAD_RESPONSE_OK2);
            SSDP_MESSAGE_WITHOUT_LOCATION = SSDPMessage.fromString(SSDPMessageTest.PAYLOAD_RESPONSE_WITHOUT_LOCATION);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class FakeListener extends TestableCallback<URI> implements SSDPManager.DiscoveryListener {

        FakeListener() {
            super();
        }

        FakeListener(int count) {
            super(count);
        }

        @Override
        public void onServiceFound(URI location) {
            setResult(location);
            countDown();
        }

        @Override
        public void onServiceLost(URI location) {

        }

        @Override
        public void onServiceResolved(DialDevice dd) {

        }

        @Override
        public void onServiceResolveFailed(URI location) {

        }
    }

    private class ReadAnswer implements Answer<List<SSDPMessage>> {
        private int mCount = 0;
        private SSDPMessage[][] mAnswers;

        ReadAnswer(SSDPMessage[] ssdpMessage) {
            mAnswers = new SSDPMessage[][]{ssdpMessage};
        }

        ReadAnswer(SSDPMessage[][] answers) {
            mAnswers = answers;
        }

        @Override
        public List<SSDPMessage> answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            List<SSDPMessage> result = new ArrayList<>();
            if(mCount<mAnswers.length) {
                for (SSDPMessage m : mAnswers[mCount]) {
                    Thread.sleep(500);
                    ((SSDPSocket.Listener) args[1]).onResponse(m);
                    result.add(m);
                }
                mCount++;
            } else {
                Thread.sleep(10000);
            }
            return result;
        }
    }

    @Test
    public void scanSingleResult() throws Exception {
        final SSDPSocket socket = mock(SSDPSocket.class);
        doAnswer(new SSDPManagerTest.ReadAnswer(new SSDPMessage[]{SSDP_MESSAGE_RESPONSE_OK})).when(socket).read(any(Integer.class), any(SSDPSocket.Listener.class));

        FakeListener callback = Mockito.spy(new FakeListener());
        SSDPManager ssdp = new SSDPManager("urn:cast-ocast-org:service:cast:1", callback) {
            @Override
            protected SSDPSocket createSocket() {
                return socket;
            }
        };

        ssdp.discoverServices(DiscoveryReliability.HIGH);
        callback.await();
        ssdp.stopDiscovery();
        verify(callback, times(1)).onServiceFound(any(URI.class));
        URI result = callback.getResult();
        assertThat(result.toString(), is(equalTo("http://127.0.0.1:8089/dd.xml")));
    }

    @Test
    public void scanTwoResult() throws Exception {
        final SSDPSocket socket = mock(SSDPSocket.class);
        doAnswer(new SSDPManagerTest.ReadAnswer(new SSDPMessage[]{SSDP_MESSAGE_RESPONSE_OK, SSDP_MESSAGE_RESPONSE_OK2})).when(socket).read(any(Integer.class), any(SSDPSocket.Listener.class));

        FakeListener callback = Mockito.spy(new FakeListener(2));
        SSDPManager ssdp = new SSDPManager("urn:cast-ocast-org:service:cast:1", callback) {
            @Override
            protected SSDPSocket createSocket() {
                return socket;
            }
        };
        ssdp.discoverServices(DiscoveryReliability.HIGH);
        callback.await();
        ssdp.stopDiscovery();
        verify(callback, times(2)).onServiceFound(any(URI.class));
    }

    @Test
    public void scanWithTimeout() throws Exception {
        final SSDPSocket socketMessage = mock(SSDPSocket.class);

        FakeListener callback = Mockito.spy(new FakeListener());
        SSDPManager ssdp = new SSDPManager("urn:cast-ocast-org:service:cast:1", callback) {
            @Override
            protected SSDPSocket createSocket() {
                return socketMessage;
            }
        };
        ssdp.discoverServices(DiscoveryReliability.HIGH);
        callback.await(DiscoveryReliability.HIGH.getTimeout(), TimeUnit.SECONDS);
        ssdp.stopDiscovery();
        verify(callback, times(0)).onServiceFound(any(URI.class));
    }

    @Test
    public void testScanWithWrongST() throws Exception {
        final SSDPSocket socket = mock(SSDPSocket.class);
        doAnswer(new SSDPManagerTest.ReadAnswer(new SSDPMessage[]{SSDP_MESSAGE_RESPONSE_OK})).when(socket).read(any(Integer.class), any(SSDPSocket.Listener.class));


        FakeListener callback = Mockito.spy(new FakeListener());
        SSDPManager ssdp = new SSDPManager("urn:dummy:service:vucast:1", callback) {
            @Override
            protected SSDPSocket createSocket() {
                return socket;
            }
        };
        ssdp.discoverServices(DiscoveryReliability.HIGH);
        //socketMessage.
        callback.await(DiscoveryReliability.HIGH.getTimeout(), TimeUnit.SECONDS);
        ssdp.stopDiscovery();
        verify(callback, times(0)).onServiceFound(any(URI.class));
    }

    @Test
    public void testScanWithoutLocation() throws Exception {
        final SSDPSocket socket = mock(SSDPSocket.class);
        doAnswer(new SSDPManagerTest.ReadAnswer(new SSDPMessage[]{SSDP_MESSAGE_WITHOUT_LOCATION})).when(socket).read(any(Integer.class), any(SSDPSocket.Listener.class));

        FakeListener callback = Mockito.spy(new FakeListener());
        SSDPManager ssdp = new SSDPManager("urn:cast-ocast-org:service:cast:1", callback) {
            @Override
            protected SSDPSocket createSocket() {
                return socket;
            }
        };
        ssdp.discoverServices(DiscoveryReliability.HIGH);
        callback.await(DiscoveryReliability.HIGH.getTimeout(), TimeUnit.SECONDS);
        ssdp.stopDiscovery();
        verify(callback, times(0)).onServiceFound(any(URI.class));
    }

    @Test
    public void startActiveScan() throws Exception {
        final SSDPSocket socket = mock(SSDPSocket.class);
        doAnswer(new SSDPManagerTest.ReadAnswer(new SSDPMessage[]{SSDP_MESSAGE_RESPONSE_OK, SSDP_MESSAGE_RESPONSE_OK2})).when(socket).read(any(Integer.class), any(SSDPSocket.Listener.class));

        FakeListener callback = Mockito.spy(new FakeListener(4));
        SSDPManager ssdp = new SSDPManager("urn:cast-ocast-org:service:cast:1", callback) {
            @Override
            protected SSDPSocket createSocket() {
                return socket;
            }
        };
        ssdp.discoverServices(DiscoveryReliability.HIGH);
        callback.await(DiscoveryReliability.HIGH.getTimeout(), TimeUnit.SECONDS);
        verify(callback, times(2)).onServiceFound(any(URI.class));
        ssdp.stopDiscovery();
    }

    @Test
    public void changeActiveScan() throws Exception {
        SSDPSocket socket = mock(SSDPSocket.class);
        FakeListener callback = Mockito.spy(new FakeListener(2));
        SSDPManager ssdp = new SSDPManager("urn:cast-ocast-org:service:cast:1", callback) {
            @Override
            protected SSDPSocket createSocket() {
                return socket;
            }
        };
        doAnswer(new SSDPManagerTest.ReadAnswer(new SSDPMessage[]{SSDP_MESSAGE_RESPONSE_OK, SSDP_MESSAGE_RESPONSE_OK2})).when(socket).read(any(Integer.class), any(SSDPSocket.Listener.class));
        ssdp.discoverServices(DiscoveryReliability.MEDIUM);
        callback.await();
        verify(callback, times(2)).onServiceFound(any(URI.class));
        ssdp.changeReliability(new DiscoveryReliability(5,1));
        doAnswer(new SSDPManagerTest.ReadAnswer(new SSDPMessage[]{SSDP_MESSAGE_RESPONSE_OK})).when(socket).read(any(Integer.class), any(SSDPSocket.Listener.class));
        Thread.sleep(1000);
        ssdp.stopDiscovery();
        verify(callback, times(1)).onServiceLost(any(URI.class));
    }
}
