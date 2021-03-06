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

import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A basic class to build/parse SSDP messages
 * Only M-SEARCH methods and location response are usefull for Dial
 */
public class SSDPMessage {

    private static final String SSDP_DISCOVER_EXTENSION = "\"ssdp:discover\"";
    private static final String SSDP_MAX_WAIT_TIME = "5";

    // Multicast channel and port reserved for SSDP by IANA
    static final String SSDP_MULTICAT_ADDRESS = "239.255.255.250";
    static final int SSDP_PORT = 1900;

    static final String LOCATION = "LOCATION";   //URL for device description
    static final String ST = "ST";               //Search Target

    private static final String HOST = "HOST";           //Should always be SSDP Multicast channel
    private static final String MAN = "MAN";             //Mandatory Extesion
    private static final String MX = "MX";               //Max Wait Time
    private static final String USN = "USN";             //Unique Service Name
    private static final String CRLF = "\r\n";

    /**
     * Type is inferred by the HTTP discover line / status line
     */
    public enum Type {
        /**
         * NOTIFY
         */
        NOTIFY("NOTIFY * HTTP/1.1"),
        /**
         * M-SEARCH request
         */
        M_SEARCH("M-SEARCH * HTTP/1.1"),
        /**
         * RESPONSE message
         */
        RESPONSE("HTTP/1.1 200 OK");

        private final String mValue;
        Type(String value) {
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }

        public static Type fromString(String text) {
            if (text != null) {
                for (Type t : Type.values()) {
                    if (text.equalsIgnoreCase(t.mValue)) {
                        return t;
                    }
                }
            }
            return null;
        }
    }

    private Type mType;
    private Map<String, String> mHeaders = new HashMap<>();

    public SSDPMessage(Type type) {
        this.mType = type;
    }

    /**
     * Get SSDP message type
     * @return the {@link org.ocast.discovery.SSDPMessage.Type Type} of the message
     */
    public Type getType() {
        return mType;
    }

    /**
     * Set the key header with value: value
     * @param key the header name
     * @param value the header value
     */
    private void addHeader(String key, String value){
        mHeaders.put(key, value);
    }

    /**
     * Get the corresponding header
     * @param name the header name
     * @return header value or null if header is not present
     */
    String getHeader(String name){
        return mHeaders.get(name);
    }

    /**
     * Retrieve and parse the urn provided in the USN header
     * eg: if header is "uuid:c4323fee-db4b-4227-9039-fa4b71589e26::"
     * the return value will be "c4323fee-db4b-4227-9039-fa4b71589e26"
     * @return a unique id
     */
    public String getUuid() {
        String usn = mHeaders.get(USN);
        return usn.split(":")[1];
    }

    /**
     * Returns an SSDPMessage to send a M-SEARCH message
     * @param searchTarget the urn identifying the devices targeted by the scanInternal discoverInternal
     *                     eg: urn:cast-ocast-org:service:cast:1
     * @return a M-SEARCH SSDP message
     */
    static SSDPMessage createMSearchMessage(String searchTarget) {
        SSDPMessage mSearchMessage = new SSDPMessage(Type.M_SEARCH);
        mSearchMessage.addHeader(HOST, String.format(Locale.US, "%s:%d", SSDPMessage.SSDP_MULTICAT_ADDRESS, SSDPMessage.SSDP_PORT));
        mSearchMessage.addHeader(MAN, SSDP_DISCOVER_EXTENSION);
        mSearchMessage.addHeader(MX, SSDP_MAX_WAIT_TIME);
        mSearchMessage.addHeader(ST, searchTarget);
        return mSearchMessage;
    }

    /**
     * Returns an SSDPMessage instance created according to the text parameter
     * @param txt a text SSDP payload, eg
     *            <pre>
     *            {@code
     *            HTTP/1.1 200 OK
     *            LOCATION: http://192.168.1.48/dd.xml
     *            CACHE-CONTROL: max-age=1800
     *            BOOTID.UPNP.ORG: 1
     *            }
     *            </pre>
     * @return an SSDPMessage
     */
    static SSDPMessage fromString(String txt) throws ParseException {
        String[] lines = txt.split(CRLF);
        Type type = Type.fromString(lines[0].trim());
        if(type == null){
            throw new ParseException("unexpected line: "+ lines[0].trim(),0);
        }
        SSDPMessage ssdpMessage = new SSDPMessage(type);
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            int index = line.indexOf(':');
            if (index > 0) {
                String header = line.substring(0, index).trim();
                String value = line.substring(index + 1).trim();
                ssdpMessage.addHeader(header, value);
            }
        }
        return ssdpMessage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mType).append(CRLF);
        for (Map.Entry<String,String> entry: mHeaders.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }
        builder.append(CRLF);
        return builder.toString();
    }
}
