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

package org.ocast.core.dial;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class OcastServiceParserTest {

    ClassLoader classLoader;

    @Before
    public void setUp() throws IOException {
        classLoader = this.getClass().getClassLoader();
    }

    @Test
    public void parse() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource("ocastServiceRunning.xml").getFile());
        Reader in = new FileReader(file);
        DialServiceParser parser = new DialServiceParser(new URL("http://192.168.1.4:8008/apps/TestReceiver"));
        DialService<AdditionalData> service = parser.parse(in);
        assertThat(service, is(not(nullValue())));
        assertThat(service.getRunLink().toString(), is(equalTo("http://192.168.1.4:8008/apps/TestReceiver/run")));
        assertThat(service.getAdditionalData(), is(not(nullValue())));
        assertThat(service.getAdditionalData().getVersion(), is(equalTo("1.0")));
        assertThat(service.getAdditionalData().getApp2AppUrl(), is(equalTo("ws://127.0.0.1:1234/ocast")));
    }

    @Test(expected = DialException.class)
    public void parseMissingApp2App() throws Exception {
        File file = new File(classLoader.getResource("ocastServiceMissing.xml").getFile());
        Reader in = new FileReader(file);
        DialServiceParser parser = new DialServiceParser(new URL("http://192.168.1.4:8008/apps/TestReceiver"));
        DialService<AdditionalData> service = parser.parse(in);
        assertThat(service, is(not(nullValue())));
        assertThat(service.getAdditionalData(), is(not(nullValue())));
        assertThat(service.getAdditionalData().getVersion(), is(equalTo("1.0")));
        assertThat(service.getAdditionalData().getApp2AppUrl(), is(equalTo("ws://127.0.0.1:1234/ocast")));
    }

}