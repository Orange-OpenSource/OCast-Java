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

public class ApplicationException extends Exception {


    private DialError error;

    public ApplicationException(DialError errorCode) {
        super();
        this.error = errorCode;
    }

    public ApplicationException(String msg) {
        super(msg);
    }

    public ApplicationException(String msg, DialError errorCode) {
        super(msg);
        this.error = errorCode;
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(DialError errorCode, Throwable cause) {
        this(cause);
        this.error = errorCode;
    }

    public ApplicationException(String msg, Throwable throwable) {
        super(msg, throwable);

    }

    public ApplicationException(String msg, Throwable throwable, DialError errorCode) {
        this(msg, throwable);
        this.error = errorCode;

    }

    @Override
    public String toString() {
        return error.name();
    }
}
