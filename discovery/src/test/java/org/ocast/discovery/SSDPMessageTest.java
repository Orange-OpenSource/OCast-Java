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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SSDPMessageTest {

    public static final String PAYLOAD_RESPONSE_OK = "HTTP/1.1 200 OK\r\n" +
            "LOCATION: http://127.0.0.1:8089/dd.xml\r\n" +
            "CACHE-CONTROL: max-age=1800\r\n" +
            "EXT:\r\n" +
            "BOOTID.UPNP.ORG: 1\r\n" +
            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
            "USN: uuid:c4323fee-db4b-4227-9039-fa4b71589e26::\r\n" +
            "\r\n";

    public static final String PAYLOAD_RESPONSE_OK2 = "HTTP/1.1 200 OK\r\n" +
            "LOCATION: http://127.0.0.1:8089/dd2.xml\r\n" +
            "CACHE-CONTROL: max-age=1800\r\n" +
            "EXT:\r\n" +
            "BOOTID.UPNP.ORG: 1\r\n" +
            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
            "USN: uuid:c4323fee-db4b-4227-9039-fa4b71589e27::\r\n"+
            "\r\n";

    public static final String PAYLOAD_RESPONSE_WITHOUT_LOCATION = "HTTP/1.1 200 OK\r\n" +
            "CACHE-CONTROL: max-age=1800\r\n" +
            "EXT:\r\n" +
            "BOOTID.UPNP.ORG: 1\r\n" +
            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
            "USN: uuid:c4323fee-db4b-4227-9039-fa4b71589e26::\r\n" +
            "\r\n";

    public static final String BAD_SSDP_PAYLOAD = "HTTP/1.1 200 OK\r\n" +
            "CACHE-CONTROL: max-age=1800\r\n" +
            "EXT:\r\n" +
            "BOOTID.UPNP.ORG: 1\r\n" +
            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
            "USN: uuid:c4323fee-db4b-4227-9039-fa4b71589e27::";

    public static final String PAYLOAD_UNKNOWN_TYPE = "Dummy response line\r\n" +
            "LOCATION: http://192.168.1.48/dd.xml\r\n" +
            "CACHE-CONTROL: max-age=1800\r\n" +
            "EXT:\r\n" +
            "BOOTID.UPNP.ORG: 1\r\n" +
            "SERVER: Linux/2.6 UPnP/1.0 quick_ssdp/1.0\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n" +
            "USN: uuid:c4323fee-db4b-4227-9039-fa4b71589e26::\r\n" +
            "\r\n";

    public static final String PAYLOAD_MSEARCH = "M-SEARCH * HTTP/1.1\r\n" +
            "ST: urn:cast-ocast-org:service:cast:1\r\n"+
            "HOST: 239.255.255.250:1900\r\n" +
            "MAN: \"ssdp:discover\"\r\n" +
            "MX: 10\r\n"+
            "\r\n";

    public static final String PAYLOAD_NOTIFY = "NOTIFY * HTTP/1.1\r\n" +
            "Host: 239.255.255.250:1900\r\n" +
            "Location: http://192.168.1.33:2222/\r\n" +
            "Cache-Control: max-age=1800\r\n" +
            "Server: UPnP/1.0 DLNADOC/1.50 Platinum/1.0.5.13\r\n" +
            "NTS: ssdp:alive\r\n" +
            "USN: uuid:506f0cff-1568-4e20-9d6e-f34fb7::upnp:rootdevice\r\n" +
            "NT: upnp:rootdevice\r\n" +
            "\r\n";
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseResponse() throws Exception {
        SSDPMessage ssdp = SSDPMessage.fromString(PAYLOAD_RESPONSE_OK);
        assertThat(ssdp, is(not(nullValue())));
        assertThat(ssdp.getType(), is(equalTo(SSDPMessage.Type.RESPONSE)));
        assertThat(ssdp.getUuid(), is(equalTo("c4323fee-db4b-4227-9039-fa4b71589e26")));
        assertThat(ssdp.getHeader("LOCATION"), is(equalTo("http://127.0.0.1:8089/dd.xml")));
    }

    @Test
    public void parseResponseWithoutLocation() throws Exception {
        SSDPMessage ssdp = SSDPMessage.fromString(PAYLOAD_RESPONSE_WITHOUT_LOCATION);
        assertThat(ssdp, is(not(nullValue())));
        assertThat(ssdp.getType(), is(equalTo(SSDPMessage.Type.RESPONSE)));
        assertThat(ssdp.getUuid(), is(equalTo("c4323fee-db4b-4227-9039-fa4b71589e26")));
        assertThat(ssdp.getHeader("LOCATION"), is(nullValue()));
    }

    @Test
    public void parseNotify() throws Exception {
        SSDPMessage ssdp = SSDPMessage.fromString(PAYLOAD_NOTIFY);
        assertThat(ssdp, is(not(nullValue())));
        assertThat(ssdp.getType(), is(equalTo(SSDPMessage.Type.NOTIFY)));
    }

    @Test
    public void parseResponseWithWrongStatusLine() throws Exception {
        thrown.expect(ParseException.class);
        SSDPMessage.fromString(PAYLOAD_UNKNOWN_TYPE);
    }

    @Test
    public void createMSearchMessage() throws Exception {
        SSDPMessage msg = SSDPMessage.createMSearchMessage("urn:cast-ocast-org:service:cast:1",10);
        assertThat(msg.getType(), is(equalTo(SSDPMessage.Type.M_SEARCH)));
        assertThat(msg.toString(), is(equalTo(PAYLOAD_MSEARCH)));
    }
}
