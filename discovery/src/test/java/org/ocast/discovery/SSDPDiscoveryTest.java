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

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SSDPDiscoveryTest {

    public static final String GOOD_SSDP_PAYLOAD1 = "HTTP/1.1 200 OK\r\n" +
            "LOCATION: http://127.0.0.1:8089/dd.xml\r\n" +
            "CACHE-CONTROL: max-age=1800\r\n" +
            "EXT:\r\n" +
            "BOOTID.UPNP.ORG: 1\r\n" +
            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
            "USN: uuid:c4323fee-db4b-4227-9039-fa4b71589e26::";

    public static final String GOOD_SSDP_PAYLOAD2 = "HTTP/1.1 200 OK\r\n" +
            "LOCATION: http://127.0.0.1:8089/dd2.xml\r\n" +
            "CACHE-CONTROL: max-age=1800\r\n" +
            "EXT:\r\n" +
            "BOOTID.UPNP.ORG: 1\r\n" +
            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
            "USN: uuid:c4323fee-db4b-4227-9039-fa4b71589e27::";

    public static final String BAD_SSDP_PAYLOAD = "HTTP/1.1 200 OK\r\n" +
            "CACHE-CONTROL: max-age=1800\r\n" +
            "EXT:\r\n" +
            "BOOTID.UPNP.ORG: 1\r\n" +
            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
            "USN: uuid:c4323fee-db4b-4227-9039-fa4b71589e27::";


    private class ReceiveAnswer implements Answer<DatagramSocket> {
        private final int mResponseToSend;
        private int mCount = 0;
        private String[] mAnswers = {GOOD_SSDP_PAYLOAD1, GOOD_SSDP_PAYLOAD2};

        public ReceiveAnswer(String[] answers) {
            mResponseToSend = answers.length;
            mAnswers = answers;
        }

        @Override
        public DatagramSocket answer(InvocationOnMock invocation) throws Throwable {
            //if needed, the Socket can be retrieved with:
            //DatagramSocket socket = (DatagramSocket) invocation.getMock();
            int timeout = 5000;
            Object[] args = invocation.getArguments();
            if (mCount < mResponseToSend) {
                Thread.sleep(timeout/2);
                ((DatagramPacket) args[0]).setData(mAnswers[mCount].getBytes());
                mCount++;
                return null;
            } else {
                Thread.sleep(timeout);
                throw new InterruptedIOException();
            }
        }
    }

    private class FakeExecutorListener extends TestableCallback<SSDPMessage> implements DiscoveryExecutor.ExecutorListener<SSDPMessage> {

        public FakeExecutorListener() {
            super();
        }

        public FakeExecutorListener(int count) {
            super(count);
        }

        @Override
        public void onLocationSent() {
        }

        @Override
        public void onLocationReceived(SSDPMessage message) {
            setResult(message);
            countDown();
        }

        @Override
        public void onError() {
            countDown();
        }
    }

    @Test
    public void discover() throws Exception {
        final DatagramSocket socketMessage = mock(DatagramSocket.class);
        doAnswer(new ReceiveAnswer(new String[]{GOOD_SSDP_PAYLOAD1, GOOD_SSDP_PAYLOAD2})).when(socketMessage).receive(any(DatagramPacket.class));

        SSDPDiscovery ssdp = new SSDPDiscovery("urn:cast-ocast-org:service:cast:1", 5000) {
            @Override
            protected DatagramSocket createSocket() {
                return socketMessage;
            }
        };
        List<SSDPMessage> result = ssdp.discover(5000);
        assertThat(result, hasSize(2));

    }

    @Test
    public void scanSingleResult() throws Exception {
        final DatagramSocket socketMessage = mock(DatagramSocket.class);
        doAnswer(new ReceiveAnswer(new String[]{GOOD_SSDP_PAYLOAD1})).when(socketMessage).receive(any(DatagramPacket.class));

        FakeExecutorListener callback = Mockito.spy(new FakeExecutorListener());
        SSDPDiscovery ssdp = new SSDPDiscovery("urn:cast-ocast-org:service:cast:1", 3000) {
            @Override
            protected DatagramSocket createSocket() {
                return socketMessage;
            }
        };
        ssdp.addListener(callback);
        ssdp.start();
        callback.await(3, TimeUnit.SECONDS);
        verify(callback, times(1)).onLocationSent();
        verify(callback, times(1)).onLocationReceived(any(SSDPMessage.class));
        SSDPMessage result = callback.getResult();
        assertThat(result.getUuid(), is(equalTo("c4323fee-db4b-4227-9039-fa4b71589e26")));
    }

    @Test
    public void scanTwoResult() throws Exception {
        final DatagramSocket socketMessage = mock(DatagramSocket.class);
        doAnswer(new ReceiveAnswer(new String[]{GOOD_SSDP_PAYLOAD1, GOOD_SSDP_PAYLOAD2})).when(socketMessage).receive(Mockito.any(DatagramPacket.class));

        FakeExecutorListener callback = Mockito.spy(new FakeExecutorListener(2));
        SSDPDiscovery ssdp = new SSDPDiscovery("urn:cast-ocast-org:service:cast:1", 5000) {
            @Override
            protected DatagramSocket createSocket() {
                return socketMessage;
            }
        };
        ssdp.addListener(callback);
        ssdp.start();
        callback.await(6, TimeUnit.SECONDS);
        verify(callback, times(2)).onLocationReceived(any(SSDPMessage.class));
    }

    @Test
    public void scanWithTimeout() throws Exception {
        final DatagramSocket socketMessage = mock(DatagramSocket.class);
        doAnswer(new ReceiveAnswer(new String[]{})).when(socketMessage).receive(Mockito.any(DatagramPacket.class));

        FakeExecutorListener callback = Mockito.spy(new FakeExecutorListener());
        SSDPDiscovery ssdp = new SSDPDiscovery("urn:cast-ocast-org:service:cast:1", 2000) {
            @Override
            protected DatagramSocket createSocket() {
                return socketMessage;
            }
        };
        ssdp.addListener(callback);
        ssdp.start();
        callback.await(3, TimeUnit.SECONDS);
        verify(callback, times(1)).onLocationSent();
        verify(callback, times(0)).onLocationReceived(any(SSDPMessage.class));
    }

    @Test
    public void testScanWithWrongST() throws Exception {
        final DatagramSocket socketMessage = mock(DatagramSocket.class);
        doAnswer(new ReceiveAnswer(new String[]{GOOD_SSDP_PAYLOAD1})).when(socketMessage).receive(Mockito.any(DatagramPacket.class));

        FakeExecutorListener callback = Mockito.spy(new FakeExecutorListener());
        SSDPDiscovery ssdp = new SSDPDiscovery("urn:dummy:service:vucast:1", 3000) {
            @Override
            protected DatagramSocket createSocket() {
                return socketMessage;
            }
        };
        ssdp.addListener(callback);
        ssdp.start();
        callback.await(3, TimeUnit.SECONDS);
        verify(callback, times(1)).onLocationSent();
        verify(callback, times(0)).onLocationReceived(any(SSDPMessage.class));
    }

    @Test
    public void testScanWithoutLocation() throws Exception {
        final DatagramSocket socketMessage = mock(DatagramSocket.class);
        doAnswer(new ReceiveAnswer(new String[]{BAD_SSDP_PAYLOAD})).when(socketMessage).receive(Mockito.any(DatagramPacket.class));

        FakeExecutorListener callback = Mockito.spy(new FakeExecutorListener());
        SSDPDiscovery ssdp = new SSDPDiscovery("urn:cast-ocast-org:service:cast:1", 3000) {
            @Override
            protected DatagramSocket createSocket() {
                return socketMessage;
            }
        };
        ssdp.addListener(callback);
        ssdp.start();
        callback.await(3, TimeUnit.SECONDS);
        verify(callback, times(1)).onLocationSent();
        verify(callback, times(0)).onLocationReceived(any(SSDPMessage.class));
    }

    @Test
    public void startActiveScan() throws Exception {
        final DatagramSocket socketMessage = mock(DatagramSocket.class);
        doAnswer(new ReceiveAnswer(new String[]{GOOD_SSDP_PAYLOAD1, GOOD_SSDP_PAYLOAD2,GOOD_SSDP_PAYLOAD1, GOOD_SSDP_PAYLOAD2})).when(socketMessage).receive(any(DatagramPacket.class));

        FakeExecutorListener callback = Mockito.spy(new FakeExecutorListener(4));
        SSDPDiscovery ssdp = new SSDPDiscovery("urn:cast-ocast-org:service:cast:1", 5000) {
            @Override
            protected DatagramSocket createSocket() {
                return socketMessage;
            }
        };
        ssdp.addListener(callback);
        ssdp.start();
        callback.await(11, TimeUnit.SECONDS);
        verify(callback, times(4)).onLocationReceived(any(SSDPMessage.class));
        ssdp.stop();
    }
}
