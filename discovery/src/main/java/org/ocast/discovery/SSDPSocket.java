/*
 * Software Name : OCast SDK
 *
 *  Copyright (C) 2018 Orange
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
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SSDPSocket {
    private static final String TAG = SSDPSocket.class.getSimpleName();
    private static final int READ_BUFFER_SIZE = 4096;

    private MulticastSocket socket;
    private InetAddress multicastAddress;

    public interface Listener {
        void onResponse(SSDPMessage response);
    }

    /**
     * Allow subclass to customize the socket creation
     * @return a MulticastSocket
     * @throws IOException
     */
    protected MulticastSocket createSocket() throws IOException {
        return new MulticastSocket(SSDPMessage.SSDP_PORT);
    }

    /**
     * Initialize the socket
     * @param joinGroup flag to tell whether we should join the multicast group to manage NOTIFY
     * @throws IOException
     */
    public void init(boolean joinGroup) throws IOException {
        try {
            multicastAddress = InetAddress.getByName(SSDPMessage.SSDP_MULTICAT_ADDRESS);
            socket = createSocket();
            if(joinGroup) {
                socket.joinGroup(multicastAddress);
            }
        } catch (UnknownHostException e) {
            //Should not happen as we provide a dotted address representation
            Logger.getLogger(TAG).log(Level.WARNING, "Exception at init ", e);
        }
    }

    /**
     * Close the SSDPSocket
     */
    public void close() {
        socket.close();
    }


    /**
     * Send a SSDPMessage, typically M-SEARCH
     * @param message to be sent
     * @throws IOException
     */
    public void send(SSDPMessage message) throws IOException {
        byte[] payload = message.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(payload, payload.length, multicastAddress, SSDPMessage.SSDP_PORT);
        socket.send(packet);
    }

    /**
     * Read data on the multicast channel
     * @param timeout amount of time the method should wait for data
     * @param responseListener the object to be notified just in time
     * @return a List of SSDPMessage received until timeout
     * @throws IOException
     */
    public List<SSDPMessage> read(int timeout, Listener responseListener) throws IOException {
        List<SSDPMessage> result = new ArrayList<>();
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            Logger.getLogger(TAG).log(Level.WARNING,"could not set socket timeout");
        }
        long endTime = System.currentTimeMillis() + timeout;
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        do {
            try {
                socket.receive(receivedPacket);
                String data = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                Logger.getLogger(TAG).log(Level.FINEST, "Received UDP packet : {0}", data.replace("\r", ""));
                SSDPMessage ssdpResponse = SSDPMessage.fromString(data);
                result.add(ssdpResponse);
                responseListener.onResponse(ssdpResponse);
            } catch (ParseException e) {
                Logger.getLogger(TAG).log(Level.WARNING, "ignoring malformed payload", e);
            } catch (InterruptedIOException e) {
                //Catching this exception to stay in the loop after a receive timeout
                Logger.getLogger(TAG).log(Level.FINEST, "socket receive timeout");
            }
        } while (endTime - System.currentTimeMillis() > 0);
        return result;
    }
}
