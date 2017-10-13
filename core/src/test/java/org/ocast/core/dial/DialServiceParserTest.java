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

public class DialServiceParserTest {

    ClassLoader classLoader;

    @Before
    public void setUp() throws IOException {
        classLoader = this.getClass().getClassLoader();
    }

    @Test
    public void parse() throws Exception {
        File file = new File(classLoader.getResource("dialServiceRunning.xml").getFile());
        Reader in = new FileReader(file);
        DialServiceParser parser = new DialServiceParser(new URL("http://192.168.1.4:8008/apps/TestReceiver/"));
        DialService serviceRunning = parser.parse(in);
        assertThat(serviceRunning, is(not(nullValue())));
        assertThat(serviceRunning.getName(), is(equalTo("TestReceiver")));
        assertThat(serviceRunning.getState(), is(equalTo(DialService.State.RUNNING)));
        assertThat(serviceRunning.getRunLink().toString(), is(equalTo("http://192.168.1.4:8008/apps/TestReceiver/run")));

        file = new File(classLoader.getResource("dialServiceStopped.xml").getFile());
        in = new FileReader(file);

        DialService serviceStopped = parser.parse(in);
        assertThat(serviceStopped, is(not(nullValue())));
        assertThat(serviceStopped.getName(), is(equalTo("TestReceiver")));
        assertThat(serviceStopped.getState(), is(equalTo(DialService.State.STOPPED)));

        file = new File(classLoader.getResource("dialServiceUnknown.xml").getFile());
        in = new FileReader(file);
        DialService serviceUnknown = parser.parse(in);
        assertThat(serviceUnknown, is(not(nullValue())));
        assertThat(serviceUnknown.getName(), is(equalTo("TestReceiver")));
        assertThat(serviceUnknown.getState(), is(equalTo(DialService.State.UNKNOWN)));

        file = new File(classLoader.getResource("dialServiceRelativeLink.xml").getFile());
        in = new FileReader(file);
        DialService serviceRelativeLink = parser.parse(in);
        assertThat(serviceRelativeLink, is(not(nullValue())));
        assertThat(serviceRelativeLink.getRunLink().toString(), is(equalTo("http://192.168.1.4:8008/apps/TestReceiver/run")));
    }

    @Test(expected = DialException.class)
    public void parseMalformedUrlExceptions() throws Exception {
        File file = new File(classLoader.getResource("dialServiceWrongLink.xml").getFile());
        Reader in = new FileReader(file);
        DialServiceParser parser = new DialServiceParser(new URL("http://192.168.1.4:8008/apps/TestReceiver"));
        DialService service = parser.parse(in);
        assertThat(service, is(not(nullValue())));
        assertThat(service.getRunLink().toString(), is(equalTo("http://192.168.1.4:8008/apps/TestReceiver/run")));
    }

}