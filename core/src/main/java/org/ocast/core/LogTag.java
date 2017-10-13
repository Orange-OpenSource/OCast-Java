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
 * Helper to initialize log tags according to the SDK structure
 */
public class LogTag {
    private static final String PREFIX = "ocast";

    private static final String CORE_M = "core";
    private static final String DRIVER_M = "driver";

    /**
     * tags device management related logs
     */
    public static final String DEVICE = tag(PREFIX, CORE_M, "device");
    /**
     * tags browser communication related los
     */
    public static final String BROWSER = tag(PREFIX,CORE_M,"browser");
    /**
     * tags application management logs
     */
    public static final String APPLICATION = tag(PREFIX,CORE_M,"application");
    /**
     * tags media management logs
     */
    public static final String MEDIA = tag(PREFIX,CORE_M,"media");


    /**
     * tags link related logs
     */
    public static final String LINK = tag(PREFIX,DRIVER_M,"link");

    private LogTag() {
    }

    private static String tag(String... path) {
        StringBuilder result = new StringBuilder(path[0]);
        for(int i=1; i<path.length; i++) {
            result.append(".").append(path[i]);
        }
        return result.toString();
    }
}
