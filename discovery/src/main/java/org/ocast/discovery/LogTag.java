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

/**
 * Helper to define log tags
 */
public class LogTag {
    private static final String PREFIX = "ocast";

    private static final String DISCOVERY_M = "discovery";
    private static final String SSDP_M = "ssdp";

    /**
     * tag identifying discovery related log entries
     */
    public static final String DISCOVERY = tag(PREFIX,DISCOVERY_M);

    /**
     * tag identifying SSDP related log entries
     */
    public static final String SSDP = tag(PREFIX,DISCOVERY_M, SSDP_M);

    private LogTag() {
    }

    private static String tag(String... path) {
        StringBuilder result = new StringBuilder(path[0]);
        for(int i=1; i<path.length; i++) {
            result.append("."+path[i]);
        }
        return result.toString();
    }
}
