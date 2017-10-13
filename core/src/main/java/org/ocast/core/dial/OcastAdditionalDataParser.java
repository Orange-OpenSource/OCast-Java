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

import java.io.IOException;

public class OcastAdditionalDataParser extends AbsAdditionalDataParser {

    public static final String OCAST_NAMESPACE = "urn:cast-ocast-org:service:cast:1";
    private static final String X_OCAST_APP_2_APP_URL = "X_OCAST_App2AppURL";
    private static final String X_OCAST_VERSION = "X_OCAST_Version";

    @Override
    protected AdditionalData parseAdditionalData(XmlPullParser parser) throws DialException, IOException, XmlPullParserException {
        String app2appUrl = null;
        String version = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(X_OCAST_APP_2_APP_URL)) {
                app2appUrl = DialParserUtils.readTextValue(parser, X_OCAST_APP_2_APP_URL, OCAST_NAMESPACE);
            } else if (name.equals(X_OCAST_VERSION)) {
                version = DialParserUtils.readTextValue(parser, X_OCAST_VERSION, OCAST_NAMESPACE);
            } else {
                DialParserUtils.skip(parser);
            }
        }

        if(app2appUrl == null) {
            throw new DialException(String.format("missing <%s>", X_OCAST_APP_2_APP_URL));
        }
        return new AdditionalData(app2appUrl, version);
    }
}
