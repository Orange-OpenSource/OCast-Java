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

import org.ocast.core.function.Consumer;

/**
 * Enumerates the different errors related to Dial
 */
public enum DialError {
        /**
         * The application does not exists
         */
        APPLICATION_NOT_FOUND,
        /**
         * An error occured on the server side
         */
        INTERNAL_ERROR,
        /**
         * An error occured on the network
         */
        NETWORK_ERROR,
        /**
         * Joining (see {@link org.ocast.core.ApplicationController#join join})  the application could not succeed as the application is not running
         */
        APPLICATION_NOT_RUNNING,
        /**
         * The application could not start fast enough
         */
        TIMEOUT
}
