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

import java.net.URL;

public class DialService<T> {

    private final URL baseURL;
    private final String name;
    private final State state;
    private final URL runLink;
    private final T additionalData;

    public enum State {
        UNKNOWN,
        RUNNING,
        STOPPED,
    }

    public DialService(URL baseURL, String name, State state, URL link, T additionanData) {
        this.baseURL = baseURL;
        this.name = name;
        this.state = state;
        runLink = link;
        additionalData = additionanData;
    }

    public URL getBaseURL() {return baseURL;}

    public String getName() {
        return name;
    }

    public State getState() {
        return state;
    }

    public URL getRunLink() {
        return runLink;
    }

    public T getAdditionalData() {
        return additionalData;
    }
}


