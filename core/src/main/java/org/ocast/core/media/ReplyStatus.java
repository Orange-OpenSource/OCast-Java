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

package org.ocast.core.media;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Status provided in the reply of a media command
 */
public enum ReplyStatus {
    SUCCESS(0),
    CANNOT_PROCEED(2404),
    NOT_IMPLEMENTED(2400),
    MISSING_PARAMETER(2422),
    ILLEGAL_PLAYER_STATE(2414),
    NO_PLAYER_INITIALIZED(2413),
    INVALID_TRACK(2414),
    UNKNOWN_MEDIA_TYPE(2415),
    UNKNOWN_TRANSFERT_MODE(2006),
    INTERNAL_ERROR(2500),
    UNKNOWN_ERROR(5000);

    private final int code;
    private static final Map<Integer,ReplyStatus> lookup = new HashMap<>();

    static {
        for(ReplyStatus s : EnumSet.allOf(ReplyStatus.class))
            lookup.put(s.getCode(), s);
    }

    ReplyStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ReplyStatus get(int code) {
        ReplyStatus result = lookup.get(code);
        return result != null ? result : UNKNOWN_ERROR;
    }
}
