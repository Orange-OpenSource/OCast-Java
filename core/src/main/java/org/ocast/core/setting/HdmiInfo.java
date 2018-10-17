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

package org.ocast.core.setting;

public class HdmiInfo {
    private final int code;
    private final String edid;
    private final String eEdid;
    private final String currentResolution;
    private final boolean currentHdcpStatus;
    private final String currentHdcpVersion;
    private final boolean currentHPDStatus;

    public HdmiInfo(int code, String edid, String eEdid, String currentResolution, boolean currentHdcpStatus, String currentHdcpVersion,boolean currentHPDStatus) {
        this.code = code;
        this.edid = edid;
        this.eEdid = eEdid;
        this.currentResolution = currentResolution;
        this.currentHdcpStatus = currentHdcpStatus;
        this.currentHdcpVersion = currentHdcpVersion;
        this.currentHPDStatus = currentHPDStatus;
    }

    public int getCode() {
        return code;
    }

    public String getEdid() {
        return edid;
    }

    public String geteEdid() {
        return eEdid;
    }

    public String getCurrentResolution() {
        return currentResolution;
    }

    public boolean isCurrentHdcpStatus() {
        return currentHdcpStatus;
    }

    public String getCurrentHdcpVersion() {
        return currentHdcpVersion;
    }

    public boolean isCurrentHPDStatus() {
        return currentHPDStatus;
    }
}
