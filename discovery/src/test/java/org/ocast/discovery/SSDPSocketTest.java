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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SSDPSocketTest {

    private class TestListener implements SSDPSocket.Listener {
        @Override
        public void onResponse(SSDPMessage response) {
        }
    }

    private class ReceiveAnswer implements Answer<DatagramSocket> {
        private int mCount = 0;
        private final String[] mAnswers;

        ReceiveAnswer(String[] answers) {
            mAnswers = answers;
        }

        @Override
        public DatagramSocket answer(InvocationOnMock invocation) throws Throwable {
            //if needed, the Socket can be retrieved with:
            //DatagramSocket socket = (DatagramSocket) invocation.getMock();
            int timeout = 5000;
            Object[] args = invocation.getArguments();
            if (mCount < mAnswers.length) {
                Thread.sleep(timeout/2);//not advised but not completely useless to have this test
                ((DatagramPacket) args[0]).setData(mAnswers[mCount].getBytes());
                mCount++;
                return null;
            } else {
                Thread.sleep(timeout);//not advised but not completely useless to have this test
                throw new InterruptedIOException();
            }
        }
    }

    @Test
    public void readSingleResponse() throws Exception {
        final MulticastSocket socket = mock(MulticastSocket.class);
        doAnswer(new SSDPSocketTest.ReceiveAnswer(new String[]{SSDPMessageTest.PAYLOAD_RESPONSE_OK}))
                .when(socket)
                .receive(any(DatagramPacket.class));

        SSDPSocket ssdp = new SSDPSocket() {
            @Override
            protected MulticastSocket createSocket() {
                return socket;
            }
        };
        SSDPSocket.Listener callback = Mockito.spy(new TestListener());

        ssdp.init(false);
        List<SSDPMessage> results = ssdp.read(5000, callback);
        verify(callback, times(1)).onResponse(any(SSDPMessage.class));
        Assert.assertThat(results.size(),is(equalTo(1)));
        SSDPMessage result = results.get(0);
        assertThat(result.getType(), is(equalTo(SSDPMessage.Type.RESPONSE)));
    }

    @Test
    public void readTwoResponse() throws Exception {
        final MulticastSocket socket = mock(MulticastSocket.class);
        doAnswer(new SSDPSocketTest.ReceiveAnswer(new String[]{SSDPMessageTest.PAYLOAD_RESPONSE_OK,SSDPMessageTest.PAYLOAD_RESPONSE_OK2}))
                .when(socket)
                .receive(any(DatagramPacket.class));

        SSDPSocket ssdp = new SSDPSocket() {
            @Override
            protected MulticastSocket createSocket() {
                return socket;
            }
        };
        SSDPSocket.Listener callback = Mockito.spy(new TestListener());

        ssdp.init(false);
        List<SSDPMessage> results = ssdp.read(5000, callback);
        verify(callback, times(2)).onResponse(any(SSDPMessage.class));
        Assert.assertThat(results.size(),is(equalTo(2)));
        for(SSDPMessage result: results) {
            assertThat(result.getType(), is(equalTo(SSDPMessage.Type.RESPONSE)));
        }
    }
}
