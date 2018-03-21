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

package org.ocast.core;

/**
 * Provides information related to the device version
 */
public class VersionInfo {
    private final String softwareVersion;
    private final String hardwareVersion;

    /**
     * Initializes a new VersionInfo object using the provided values
     * @param softwareVersion a String identifying the software version
     * @param hardwareVersion a String identifying the hardware version
     */
    public VersionInfo(String softwareVersion, String hardwareVersion)
    {
        this.softwareVersion = softwareVersion;
        this.hardwareVersion = hardwareVersion;
    }

    /**
     * Returns the software version
     * @return
     */
    public String getSoftwareVersion()
    {
        return softwareVersion;
    }

    /**
     * Returns the hardware version
     * @return
     */
    public String getHardwareVersion()
    {
        return hardwareVersion;
    }

}
