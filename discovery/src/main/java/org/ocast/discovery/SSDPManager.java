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
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SSDPManager provides a high level API to discover services with Simple Service Discovery Protocol
 */
public class SSDPManager {
    private static final String TAG = LogTag.SSDP;

    private static final int SECOND_TO_MILLI = 1000;
    private static final int IMMEDIATE = 0;

    private final DeviceDescriptionRequest deviceDescriptionRequest;
    private final Map<URI, DialDevice> knownDevices = Collections.synchronizedMap(new HashMap<>());
    private final Map<URI, Long> latestResponseForLocation = Collections.synchronizedMap(new HashMap<>());
    private Set<String> searchTargets = Collections.synchronizedSet(new HashSet<>());

    private final DiscoveryListener discoveryListener;
    private SSDPSocket socket;
    private long currentScan;

    /**
     * Interface for listening SSDP service discovery
     */
    public interface DiscoveryListener {
        /**
         * A new service has been found
         * @param location SSDP location URL
         */
        void onServiceFound(URI location);

        /**
         * A known service has been lost
         * @param location DialDevice location
         */
        void onServiceLost(URI location);

        /**
         * A service description has been retrieved
         * @param device DialDevice parsed from the Location URL
         */
        void onServiceResolved(DialDevice device);

        /**
         * A service description could not be retrieved
         * @param location SSDP Location URL
         */
        void onServiceResolveFailed(URI location);
    }

    /**
     * Callback to a device description request
     */
    private final DeviceDescriptionRequest.Callbacks deviceDescriptionCallback = new DeviceDescriptionRequest.Callbacks() {
        @Override
        public void onDeviceDescription(URI location, DialDevice dd) {
            DialDevice knownDevice = getDeviceDescriptionByUudi(dd.getUuid());
            if (knownDevice == null) {
                Logger.getLogger(TAG).log(Level.FINE,  "Looks like a brand new device at {0}", location);
                knownDevices.put(location, dd);
                discoveryListener.onServiceResolved(dd);
            } else {
                if (!knownDevice.equals(dd)) {
                    Logger.getLogger(TAG).log(Level.FINE,  "Looks like a device that changed");
                    knownDevices.put(location, dd);
                    discoveryListener.onServiceResolved(dd);
                }
            }
        }

        @Override
        public void onError(URI location) {
            //This means we didn't get the dd.xml right, for example due to SocketTimeout or
            // parsing error.
            Logger.getLogger(TAG).log(Level.SEVERE, "could not fetch {0}", location);
            discoveryListener.onServiceResolveFailed(location);
        }

        private DialDevice getDeviceDescriptionByUudi(String uuid) {
            synchronized (knownDevices) {
                for (Map.Entry<URI, DialDevice> entry : knownDevices.entrySet()) {
                    DialDevice dd = entry.getValue();
                    if (uuid.equals(dd.getUuid())) {
                        return dd;
                    }
                }
            }
            return null;
        }
    };
    private final SSDPSocket.Listener socketListener = response -> {
        if (response.getType() == SSDPMessage.Type.RESPONSE) {
            if (validateResponse(response)) {
                onLocationReceived(response);
            } else {
                Logger.getLogger(TAG).log(Level.WARNING, "Skipping response from:" + response.getHeader(SSDPMessage.ST));
            }
        } else if (response.getType() == SSDPMessage.Type.NOTIFY) {
            Logger.getLogger(TAG).log(Level.FINEST, "got a NOTIFY");
        }
    };

    /**
     * Execute a Http Request on the given address in order to retrieve a cast device's description
     *
     * @param location of the device description xml file
     */
    public void resolve(URI location) {
        Logger.getLogger(TAG).log(Level.FINE,  "Retrieving device description through {0}", location);
        deviceDescriptionRequest.getDeviceDescription(location, deviceDescriptionCallback);
    }

    /**
     * Allow subclass to customize the socket creation
     * @return a MulticastSocket
     * @throws IOException
     */
    protected SSDPSocket createSocket() throws IOException {
        SSDPSocket s = new SSDPSocket();
        s.init(false);
        return s;
    }

    /**
     * Instanciate a SSDPManager to discover a given search target
     * @param searchTarget   the search target corresponding to devices of interest
     * @param listener a listener interested in discovery events
     */
    public SSDPManager(final String searchTarget, DiscoveryListener listener) {
        this(new HashSet<>(Arrays.asList(searchTarget)),listener);
    }

    /**
     * Instanciate a SSDPManager to discover a given set of search target
     * @param searchTargetList
     * @param listener
     */
    public SSDPManager(Set<String> searchTargetList, DiscoveryListener listener) {
        discoveryListener = listener;
        deviceDescriptionRequest = new DeviceDescriptionRequest();
        searchTargets = searchTargetList;
    }

    /**
     * Starts actively polling the network to scan devices by sending a "M-SEARCH discover"
     * @param reliability timeout/retries after we consider no device responded
     */
    public void discoverServices(DiscoveryReliability reliability) {
        Logger.getLogger(TAG).log(Level.INFO, "Starting discovery...");
        currentScan = 0;
        knownDevices.clear();
        latestResponseForLocation.clear();
        Thread discoveryThread = new Thread(getPeriodicDiscoveryTask(reliability));
        discoveryThread.start();
    }

    /**
     * Change the DiscoveryReliability of the discovery
     * @param reliability
     */
    public void changeReliability(DiscoveryReliability reliability) {
        Logger.getLogger(TAG).log(Level.INFO, "Changing discovery...");
        if(socket != null) {
            socket.close();
            currentScan--;//don't account ongoing scan as it has been cancelled
            Thread discoveryThread = new Thread(getPeriodicDiscoveryTask(reliability));
            discoveryThread.start();
        }
    }

    /**
     * Stops scanning to discover devices
     */
    public void stopDiscovery() {
        Logger.getLogger(TAG).log(Level.INFO, "Stopping discovery...");
        if(socket != null) {
            socket.close();
        }
    }

    /**
     * Get a list of devices discovered during latest network scan
     * @return
     */
    public List<DialDevice> getDeviceDescriptions() {
        return new ArrayList<>(knownDevices.values());
    }

    private void onLocationReceived(SSDPMessage ssdpMessage) {
        String location = ssdpMessage.getHeader(SSDPMessage.LOCATION);
        latestResponseForLocation.put(URI.create(location), currentScan);
        discoveryListener.onServiceFound(URI.create(location));
    }

    private void onError() {
        pruneDevices(IMMEDIATE);
    }

    /**
     * Get a Runnable that will periodically send "discover" request
     */
    private Runnable getPeriodicDiscoveryTask(DiscoveryReliability reliability) {
        int timeout =reliability.getTimeout() * SECOND_TO_MILLI;
        return () -> {
            socket = null;
            try {
                socket = createSocket();
            } catch (IOException e) {
                Logger.getLogger(TAG).log(Level.WARNING, "could not create Socket", e);
            }
            try {
                while (true) { //Note: loop exit with SocketException
                    Logger.getLogger(TAG).log(Level.FINE, "timeout {0}", timeout);
                    discoverInternal(socket, timeout, reliability.getRetry());
                }
            } catch (SocketException e) {
                Logger.getLogger(TAG).log(Level.WARNING, "Socket closed", e);
            } catch (IOException e) {
                Logger.getLogger(TAG).log(Level.WARNING, "could not execute request (Network might be unreachable)", e);
                onError();
            } catch (Exception e) {
                Logger.getLogger(TAG).log(Level.WARNING, "Exception ", e);
                onError();
            }
        };
    }

    private void pruneDevices(int threshold) {
        synchronized (latestResponseForLocation) {
            for (Iterator<Map.Entry<URI, Long>> iterator = latestResponseForLocation.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<URI, Long> entry = iterator.next();
                URI location = entry.getKey();
                long lastScan = entry.getValue();
                long delta = currentScan - lastScan;
                Logger.getLogger(TAG).log(Level.FINE, " {0} / delta {1} ({2})", new Object[]{location, delta, currentScan});
                if (delta >= threshold) {
                    iterator.remove();
                    discoveryListener.onServiceLost(location);
                    removeKnownDevice(location);
                }
            }
        }
    }

    private void removeKnownDevice(URI location) {
        synchronized (knownDevices) {
            for (Iterator<Map.Entry<URI, DialDevice>> iterator = knownDevices.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<URI, DialDevice> entry = iterator.next();
                URI uri = entry.getKey();
                if (location.equals(uri)) {
                    iterator.remove();
                }
            }
        }
    }

    private List<SSDPMessage> discoverInternal(SSDPSocket socket, int timeout, int retry) throws IOException {
        pruneDevices(retry);
        for (SSDPMessage mSearch : buildMSearchPacket(searchTargets, timeout)) {
            socket.send(mSearch);
        }
        currentScan++;
        List<SSDPMessage> result = socket.read(timeout, socketListener);
        if (result.isEmpty()) {
            Logger.getLogger(TAG).log(Level.FINE, "no device found");
        }
        return result;
    }

    private boolean validateResponse(SSDPMessage ssdpResponse) {
        String location = ssdpResponse.getHeader(SSDPMessage.LOCATION);
        String searchTarget = ssdpResponse.getHeader(SSDPMessage.ST);
        return (location != null && location.length() > 0 &&
                searchTargets.contains(searchTarget));
    }

    /**
     * Build a list of M-SEARCH packet to be sent on the multicast address
     * @param mx Max wait time indicated in the packet
     * @return a List of SSDPMessage
     */
    private List<SSDPMessage> buildMSearchPacket(Set<String> searchTargets, int mx) {
        List<SSDPMessage> mSearchPacketList = new ArrayList<>();
        synchronized (searchTargets) {
            for (String searchTarget : searchTargets) {
                SSDPMessage mSearch = SSDPMessage.createMSearchMessage(searchTarget, mx);
                mSearchPacketList.add(mSearch);
            }
        }
        return mSearchPacketList;
    }
}
