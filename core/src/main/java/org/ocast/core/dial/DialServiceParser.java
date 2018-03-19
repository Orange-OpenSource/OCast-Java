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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @hide
 */
public class DialServiceParser {

    private static final String OCAST_NAMESPACE = "urn:cast-ocast-org:service:cast:1";
    private static final String TAG_ADDITIONAL_DATA = "additionalData";
    private static final String TAG_LINK = "link";
    private static final String TAG_NAME = "name";
    private static final String TAG_STATE = "state";
    private static final String TAG_SERVICE = "service";

    private final URL baseUrl;
    private static final Map<String, AbsAdditionalDataParser> additionnalDataParser = new HashMap<>();

    static {
        additionnalDataParser.put(OCAST_NAMESPACE, new OcastAdditionalDataParser());
    }

    public DialServiceParser(URL baseServiceUrl) {
        baseUrl = baseServiceUrl;
    }

    public DialService parse(Reader xml) throws DialException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(xml);
            parser.nextTag();

            return readService(parser);
        } catch(XmlPullParserException | IOException e) {
            throw new DialException(e);
        }
    }

    private DialService readService(XmlPullParser parser) throws DialException, IOException, XmlPullParserException {
        DialService service;
        String name = null;
        DialService.State state = null;
        URL absoluteLink = null;
        AdditionalData additionalData = null;

        parser.require(XmlPullParser.START_TAG, "urn:dial-multiscreen-org:schemas:dial", TAG_SERVICE);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals(TAG_ADDITIONAL_DATA)) {
                additionalData = readAdditionalData(parser);
            } else if (tag.equals(TAG_NAME)) {
                name = DialParserUtils.readTextValue(parser, TAG_NAME, null);
            } else if (tag.equals(TAG_STATE)) {
                state = readState(parser);
            } else if (tag.equals(TAG_LINK)) {
                URI link = readLink(parser);
                if(link != null && link.isAbsolute()) {
                    absoluteLink = link.toURL();
                } else {
                    try {
                        // not confident about this being the right way to do it
                        absoluteLink = new URI(baseUrl.toString() + "/").resolve(link).toURL();
                    } catch (URISyntaxException e) {
                        throw new DialException(e);
                    }
                }
            } else {
                DialParserUtils.skip(parser);
            }
        }

        service = new DialService(baseUrl, name, state, absoluteLink, additionalData);
        checkService(service);
        return service;
    }

    // Processes link tags in the feed.
    private URI readLink(XmlPullParser parser) throws IOException, XmlPullParserException, DialException {
        URI link = null;
        parser.require(XmlPullParser.START_TAG, null, TAG_LINK);
        String relType = parser.getAttributeValue(null, "rel");
        String href = parser.getAttributeValue(null, "href");
        if ("run".equals(relType) && href != null){
            try {
                link = new URI(href);
            } catch (URISyntaxException e) {
                throw new DialException(e);
            }
            parser.nextTag();
        }
    parser.require(XmlPullParser.END_TAG, null, TAG_LINK);
        return link;
    }

    private AdditionalData readAdditionalData(XmlPullParser parser) throws IOException, XmlPullParserException, DialException {
        AdditionalData data = null;
        parser.require(XmlPullParser.START_TAG, null, TAG_ADDITIONAL_DATA);
        AbsAdditionalDataParser addparser = additionnalDataParser.get(parser.getNamespace("ocast"));
        if(addparser != null) {
            data = new OcastAdditionalDataParser().parseAdditionalData(parser);
        } else {
            DialParserUtils.skip(parser);
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_ADDITIONAL_DATA);
        return data;
    }

    private DialService.State readState(XmlPullParser parser) throws IOException, XmlPullParserException {
        try {
            String stateValue = DialParserUtils.readTextValue(parser, TAG_STATE, null);
            return DialService.State.valueOf(stateValue.toUpperCase());
        } catch(IllegalArgumentException e) {
            return DialService.State.UNKNOWN;
        }
    }

    private void checkService(DialService service) throws DialException {
        if(service.getName() == null || service.getName().length() == 0) {
            throw new DialException("missing <name>");
        }
    }
}
