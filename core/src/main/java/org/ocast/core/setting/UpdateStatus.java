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

/**
 * Provides information related to the firmware update status
 */
public class UpdateStatus {

    private final int code;
    private final String state;
    private final String version;
    private final int progress;

    /**
     * Initializes a new UpdateStatus object using the provided values
     * @param code a Integer identifying the result code
     * @param state a String identifying the firmware state
     * @param version a String identifying the firmware version
     * @param progress a Integer identifying the progress of firmware update
     */
    public UpdateStatus(int code, String state, String version, int progress) {
        this.code = code;
        this.state = state;
        this.version = version;
        this.progress = progress;
    }

    /**
     * Returns the result code
     * 0 for success, 11xx for other errors where xx refers to error codes that the component must describe
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the firmware state
     * "notChecked", "upToDate", "newVersionFound" ,"newVersionReady", "downloading", "error", "success"
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     * Returns the version of the image to update
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the percentage downloaded firmware (only for "downloading")
     * @return
     */
    public int getProgress() {
        return progress;
    }
}
