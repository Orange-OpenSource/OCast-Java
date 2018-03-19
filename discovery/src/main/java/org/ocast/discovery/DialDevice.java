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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Defines a DialDevice as per its DIAL device description.
 * This class provides a helper method to get an object from the parsing of the dd.xml file
 * eg:
 * <pre>
 * {@code
 * <root xmlns="urn:schemas-upnp-org:device-1-0" xmlns:r="urn:restful-tv-org:schemas:upnp-dd">
 *     <specVersion>
 *         <major>1</major>
 *         <minor>0</minor>
 *     </specVersion>
 *     <device>
 *         <deviceType>urn:schemas-upnp-org:device:tvdevice:1</deviceType>
 *         <friendlyName>device_1</friendlyName>
 *         <manufacturer>OCast</manufacturer>
 *         <modelName>OCast</modelName>
 *         <UDN>uuid:c4323fee-db4b-4aa7-9039-fa4b71589e26</UDN>
 *     </device>
 * </root>
 * }
 * </pre>
 */
public class DialDevice extends DiscoveredDevice {

	private URI location;

	public DialDevice(String uuid, String friendlyName, String manufacturer, String modelName, URI url, URI location) {
		super(uuid, friendlyName, manufacturer, modelName, url);
		this.location = location;
	}

	public URI getLocation() {
		return location;
	}

	/**
	 * Build a DeviceDescription from an XML representation
	 *
	 * @param xml XML representation string
	 * @param dialUrlHeader     Dial application URL if provided in a header
	 * @return the resulting DeviceDescription
     * @throws ParseException if the XML content is invalid
	 */
	public static DialDevice fromDeviceDescription(String xml, String dialUrlHeader, URI location) throws ParseException {
		String friendlyName = null;
		String manufacturer = null;
		String modelName = null;
		String uuid = null;
		String urlBase = dialUrlHeader;
		URI url;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			StringReader sr = new StringReader(xml);
			parser.setInput(sr);
			int eventType = parser.getEventType();
			String currentTagName = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_TAG:
						currentTagName = parser.getName();
						// ensure we have the correct root tag
						if (parser.getDepth() == 1 && !"root".equals(currentTagName)) {
							throw new ParseException("Malformed XML. Expected root, got: " + currentTagName, parser.getLineNumber());
						}
						break;
					case XmlPullParser.TEXT:
						if (currentTagName != null) {
							if ("friendlyName".equals(currentTagName)) {
								friendlyName = parser.getText();
							} else if ("UDN".equals(currentTagName)) {
								uuid = parser.getText();
								if (uuid.startsWith("uuid")) {
									uuid = uuid.split(":")[1];
								}
							} else if ("manufacturer".equals(currentTagName)) {
								manufacturer = parser.getText();
							} else if ("modelName".equals(currentTagName)) {
								modelName = parser.getText();
							} else if ("URLBase".equals(currentTagName) && dialUrlHeader == null) {
								urlBase = parser.getText();
							}
						}
						break;
					case XmlPullParser.END_TAG:
						currentTagName = null;
						break;
					default:
						break;
				}
				eventType = parser.next();
			}
			sr.close();
			if(urlBase != null) {
				url = new URI(urlBase);
				return new DialDevice(uuid, friendlyName, manufacturer, modelName, url, location);
			} else {
				throw new ParseException("Could find Dial URL", -1);
			}
		} catch (XmlPullParserException e) {
			throw new ParseException("Could not parse device description ", e.getLineNumber());
		} catch (IOException e) {
			throw new ParseException("Could not parse device description", -1);
		} catch (URISyntaxException e) {
			throw new ParseException("Could find Dial URL", -1);
		}
	}
}
