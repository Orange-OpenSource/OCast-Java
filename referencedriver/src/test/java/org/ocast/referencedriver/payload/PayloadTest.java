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

package org.ocast.referencedriver.payload;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PayloadTest {

    private ClassLoader classLoader;

    @Before
    public void setUp() throws IOException {
        classLoader = this.getClass().getClassLoader();
    }

    @Test
    public void decode() throws Exception {
        File inputFile = new File(classLoader.getResource("event.json").getFile());
        String input = FileUtils.readFileToString(inputFile, Charset.defaultCharset());
        Payload parsed = Payload.decode(input);
        assertThat(parsed.getDst(), is(Payload.DST_BROADCAST));
        assertThat(parsed.getSrc(), is("browser"));
        assertThat(parsed.getType(), is(Payload.Type.EVENT));
        assertThat(parsed.getId(), is(1));
        assertThat(parsed.getMessage(), instanceOf(JSONObject.class));
    }

    @Test(expected = JSONException.class)
    public void decodeWithInvalidType() throws Exception {
        File inputFile = new File(classLoader.getResource("eventInvalidType.json").getFile());
        String input = FileUtils.readFileToString(inputFile, Charset.defaultCharset());
        Payload.decode(input);
    }

    @Test(expected = JSONException.class)
    public void decodeJSONWithInvalidId() throws Exception {
        File inputFile = new File(classLoader.getResource("eventInvalidId.json").getFile());
        String input = FileUtils.readFileToString(inputFile, Charset.defaultCharset());
        Payload.decode(input);
    }
}