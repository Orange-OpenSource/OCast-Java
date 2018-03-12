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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.text.ParseException;

public class DialDeviceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void fromDeviceDescriptionWithoutURLBase() throws Exception {
        thrown.expect(ParseException.class);
        String ddXmlContent = new FileReader().readFile("dd_WithoutURLBase.xml");
        DialDevice.fromDeviceDescription(ddXmlContent, null, URI.create("http://127.0.0.1:56790/device-desc.xml"));
    }

    @Test
    public void fromDeviceDescriptionWithURLBase() throws Exception {
        String ddXmlContent = new FileReader().readFile("dd1_WithURLBase.xml");
        DialDevice dd = DialDevice.fromDeviceDescription(ddXmlContent, null, URI.create("http://127.0.0.1:56790/device-desc.xml"));
        assertThat(dd.getFriendlyName(), is(equalTo("device1")));
        assertThat(dd.getManufacturer(), is(equalTo("OCast")));
        assertThat(dd.getModelName(), is(equalTo("OCast")));
        assertThat(dd.getUuid(), is(equalTo("11111111-1111-1111-1111-111111111111")));
        assertThat(dd.getDialURI().toString(), is(equalTo("http://127.0.0.1:8008/apps")));
    }

    @Test
    public void fromDeviceDescriptionWithHeader() throws Exception {
        String ddXmlContent = new FileReader().readFile("dd_WithoutURLBase.xml");
        DialDevice dd = DialDevice.fromDeviceDescription(ddXmlContent, "http://127.0.0.1:8008/apps_in_header", URI.create("http://127.0.0.1:56790/device-desc.xml"));
        assertThat(dd.getFriendlyName(), is(equalTo("device1")));
        assertThat(dd.getManufacturer(), is(equalTo("OCast")));
        assertThat(dd.getModelName(), is(equalTo("OCast")));
        assertThat(dd.getUuid(), is(equalTo("11111111-1111-1111-1111-111111111111")));
        assertThat(dd.getDialURI().toString(), is(equalTo("http://127.0.0.1:8008/apps_in_header")));
    }

    @Test
    public void fromDeviceDescriptionMalformed() throws Exception {
        thrown.expect(ParseException.class);
        String ddXmlContent = new FileReader().readFile("dd_Invalid.xml");
        DialDevice.fromDeviceDescription(ddXmlContent, null, URI.create("http://127.0.0.1:56790/device-desc.xml"));
    }

    @Test
    public void equals() throws Exception {
        String ddXmlContent1 = new FileReader().readFile("dd_WithoutURLBase.xml");
        DialDevice dd1 = DialDevice.fromDeviceDescription(ddXmlContent1, "http://127.0.0.1:8008/apps", URI.create("http://127.0.0.1:56790/device-desc.xml"));
        String ddXmlContent2 = new FileReader().readFile("dd1_WithURLBase.xml");
        DialDevice dd2 = DialDevice.fromDeviceDescription(ddXmlContent2, "http://127.0.0.1:8008/apps_in_header", URI.create("http://127.0.0.1:56790/device-desc.xml"));
        assertThat(dd1, is(not(equalTo(dd2))));
        assertThat(dd1.hashCode(), is(not(equalTo(dd2.hashCode()))));
    }
}
