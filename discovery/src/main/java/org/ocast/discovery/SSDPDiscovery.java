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
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * defines a SSDP {@link org.ocast.discovery.DiscoveryExecutor DiscoveryExecutor}
 */
public class SSDPDiscovery implements DiscoveryExecutor<SSDPMessage> {
    private static final String TAG = LogTag.DISCOVERY;

    private static final int SECOND_TO_MILLI = 1000;
    private static final int DEFAULT_MSEARCH_TIMEOUT = 3;
    private static final int READ_BUFFER_SIZE = 4096;

    private InetAddress mBroadcastAddress;
    private DatagramSocket mSocket;
    private List<String> mSearchTargetList = new ArrayList<>();
    private List<DatagramPacket> mSearchPacketList = new ArrayList<>();
    private int mMSearchTimeout;

    private ExecutorService mDiscoveryThread = Executors.newSingleThreadExecutor();
    private boolean mRunning;
    private Set<ExecutorListener<SSDPMessage>> mExecutorListenerListener = new CopyOnWriteArraySet<>();


    /**
     * @param searchTarget the scanInternal target corresponding to devices of interest
     */
    public SSDPDiscovery(final String searchTarget) {
        this(new ArrayList<>(Arrays.asList(searchTarget)), DEFAULT_MSEARCH_TIMEOUT);
    }

    /**
     * @param searchTarget   the scanInternal target corresponding to devices of interest
     * @param msearchtimeout
     */
    public SSDPDiscovery(final String searchTarget, final int msearchtimeout) {
        this(new ArrayList<>(Arrays.asList(searchTarget)), msearchtimeout);
    }

    public SSDPDiscovery(List<String> searchTargetList) {
        this(searchTargetList, DEFAULT_MSEARCH_TIMEOUT);
    }

    public SSDPDiscovery(List<String> searchTargetList, int msearchtimeout) {
        mMSearchTimeout = msearchtimeout * SECOND_TO_MILLI;
        mSearchTargetList = searchTargetList;
        try {
            mBroadcastAddress = InetAddress.getByName(SSDPMessage.SSDP_MULTICAT_CHANNEL_ADDRESS);
        } catch (UnknownHostException e) {
            //Should not happen as we provide a dotted address representation
            Logger.getLogger(TAG).log(Level.WARNING, "Exception at init ", e);
        }
        for (String searchTarget : mSearchTargetList) {
            String message = SSDPMessage.createMSearchMessage(searchTarget, msearchtimeout).toString();
            byte[] payload = message.getBytes();
            DatagramPacket packet = new DatagramPacket(payload, payload.length, mBroadcastAddress, SSDPMessage.SSDP_PORT);
            mSearchPacketList.add(packet);
        }
    }

    protected DatagramSocket createSocket() throws SocketException {
        DatagramSocket socket = new DatagramSocket();
        socket.setReuseAddress(true);
        return socket;
    }

    @Override
    public void addListener(ExecutorListener<SSDPMessage> listener) {
        mExecutorListenerListener.add(listener);
    }

    @Override
    public void removeListener(ExecutorListener<SSDPMessage> listener) {
        mExecutorListenerListener.remove(listener);
    }

    /**
     * Starts actively polling the network to scanInternal devices by sending a M-SEARCH discoverInternal
     */
    @Override
    public void start() {
        Logger.getLogger(TAG).log(Level.INFO, "Starting active scan...");
        if (!mRunning) {
            mRunning = true;
            mDiscoveryThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        mSocket = createSocket();
                        while (mRunning) {
                            discoverInternal(mMSearchTimeout);
                        }
                        mSocket.close();
                    } catch (SocketException e) {
                        Logger.getLogger(TAG).log(Level.WARNING, "could not create Socket", e);
                        mRunning = false;
                        notifyOnError();
                    } catch (IOException e) {
                        Logger.getLogger(TAG).log(Level.WARNING, "could not execute request", e);
                        mRunning = false;
                        notifyOnError();
                    }
                }
            });
        }
    }

    /**
     * Stops active scan
     */
    @Override
    public void stop() {
        Logger.getLogger(TAG).log(Level.INFO, "Stopping discovery...");
        mRunning = false;
    }

    public List<SSDPMessage> discover(long timeout) {
        try {
            mSocket = createSocket();
            return discoverInternal(timeout);
        } catch (SocketException e) {
            Logger.getLogger(TAG).log(Level.WARNING,"could not create Socket", e);
        } catch (IOException e) {
            Logger.getLogger(TAG).log(Level.WARNING, "Exception sending discoverInternal packet", e);
        } finally {
            mSocket.close();
        }
        return new ArrayList<>();
    }

    /**
     * Create an M-SEARCH SSDPMessage targeting searchTarget and sends it on the network.
     * This method then reads data on the socket until discovery timeout occurs.
     *
     * @param timeout
     */
    public List<SSDPMessage> discoverInternal(long timeout) throws IOException {
        for (DatagramPacket packet : mSearchPacketList) {
            mSocket.send(packet);
        }
        notifyOnLocationSent();
        List<SSDPMessage> result = readResponses(timeout);
        if (result.isEmpty()) {
            Logger.getLogger(TAG).log(Level.FINE, "no device found");
        }
        return result;
    }

    private List<SSDPMessage> readResponses(long timeout) throws IOException {
        List<SSDPMessage> result = new ArrayList<>();
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        mSocket.setSoTimeout((int) timeout);
        long endTime = System.currentTimeMillis() + timeout;
        do {
            try {
                mSocket.receive(receivedPacket);
                String data = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                Logger.getLogger(TAG).log(Level.FINEST, "Received UDP packet : " + data.replace("\r", ""));
                SSDPMessage ssdpResponse = SSDPMessage.parseResponse(data);
                if (mSearchTargetList.contains(ssdpResponse.getHeader(SSDPMessage.ST))) {
                    notifyOnLocationReceived(ssdpResponse);
                    result.add(ssdpResponse);
                } else {
                    Logger.getLogger(TAG).log(Level.WARNING, "Skipping response from:" + ssdpResponse.getHeader(SSDPMessage.ST));
                }
            } catch (ParseException e) {
                Logger.getLogger(TAG).log(Level.SEVERE, "Could not parse response", e);
            } catch (InterruptedIOException e) {
                //Catching this exception to stay in the loop after a receive timeout
                Logger.getLogger(TAG).log(Level.FINEST, "socket receive timeout");
            }
        } while (endTime - System.currentTimeMillis() > 0);
        return result;
    }

    private void notifyOnLocationSent() {
        for (ExecutorListener<SSDPMessage> listener : mExecutorListenerListener) {
            listener.onLocationSent();
        }
    }

    private void notifyOnLocationReceived(SSDPMessage ssdpMessage) {
        for (ExecutorListener<SSDPMessage> listener : mExecutorListenerListener) {
            listener.onLocationReceived(ssdpMessage);
        }
    }

    private void notifyOnError() {
        for (ExecutorListener<SSDPMessage> listener : mExecutorListenerListener) {
            listener.onError();
        }
    }
}
