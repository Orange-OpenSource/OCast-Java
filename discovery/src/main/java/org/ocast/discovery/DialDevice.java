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
import java.net.MalformedURLException;
import java.net.URL;
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
public class DialDevice {
    private final String mFriendlyName;
	private final String mManufacturer;
	private final String mModelName;
	private final String mUuid;
	private final URL mDialApplURL;

	public DialDevice(String uuid, String friendlyName, String manufacturer, String modelName, URL urlBase) {
		mUuid = uuid;
		mFriendlyName = friendlyName;
		mManufacturer = manufacturer;
		mModelName = modelName;
		mDialApplURL = urlBase;
	}

    /**
	 * Retrieve the device friendly name found in device tag
	 *
	 * @return friendly name
	 */
	public String getFriendlyName() {
		return mFriendlyName;
	}

	/**
	 * Retrieve the manufacturer found in found in device tag
	 *
	 * @return
	 */
	public String getManufacturer() {
		return mManufacturer;
	}

	/**
	 * Retrieve the modelName found in device tag
	 *
	 * @return
	 */
	public String getModelName() {
		return mModelName;
	}

	/**
	 * Retrieve the UUID found in device tag
	 *
	 * @return the uuid value without uuid: prefix
	 */
	public String getUuid() {
		return mUuid;
	}

	/**
	 * Retrieve the Dial application URL found in device tag URLBase or the one provided
	 * to fromDeviceDescription if it comes from a header.
	 *
	 * @return
	 */
	public String getDialApplURL() {
		return mDialApplURL.toString();
	}

	public URL getDialURL() {
		return mDialApplURL;
	}

	/**
	 * Build a DeviceDescription from an XML representation
	 *
	 * @param xml XML representation string
	 * @param dialUrlHeader     Dial application URL if provided in a header
	 * @return the resulting DeviceDescription
     * @throws ParseException if the XML content is invalid
	 */
	public static DialDevice fromDeviceDescription(String xml, String dialUrlHeader) throws ParseException {
		String friendlyName = null;
		String manufacturer = null;
		String modelName = null;
		String uuid = null;
		String urlBase = dialUrlHeader;
		URL url;
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
			url = new URL(urlBase);

		} catch (XmlPullParserException e) {
			throw new ParseException("Could not parse device description ", e.getLineNumber());
		} catch (MalformedURLException e) {
			throw new ParseException("Could find Dial URL", -1);
		} catch (IOException e) {
			throw new ParseException("Could not parse device description", -1);
		}
		return new DialDevice(uuid, friendlyName, manufacturer, modelName, url);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DialDevice that = (DialDevice) o;

        if (mFriendlyName != null ? !mFriendlyName.equals(that.mFriendlyName) : that.mFriendlyName != null)
            return false;
        if (mManufacturer != null ? !mManufacturer.equals(that.mManufacturer) : that.mManufacturer != null)
            return false;
        if (mModelName != null ? !mModelName.equals(that.mModelName) : that.mModelName != null)
            return false;
        if (mUuid != null ? !mUuid.equals(that.mUuid) : that.mUuid != null) return false;
        return mDialApplURL != null ? mDialApplURL.equals(that.mDialApplURL) : that.mDialApplURL == null;

    }

    @Override
    public int hashCode() {
        int result = mFriendlyName != null ? mFriendlyName.hashCode() : 0;
        result = 31 * result + (mManufacturer != null ? mManufacturer.hashCode() : 0);
        result = 31 * result + (mModelName != null ? mModelName.hashCode() : 0);
        result = 31 * result + (mUuid != null ? mUuid.hashCode() : 0);
        result = 31 * result + (mDialApplURL != null ? mDialApplURL.hashCode() : 0);
        return result;
    }
}
