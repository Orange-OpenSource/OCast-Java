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
 * Defines the different Playback states
 */
public enum PlaybackState {
    UNKNOWN(0),
    IDLE(1),
    PLAYING(2),
    PAUSED(3),
    BUFFERING(4);

    private final int code;
    private static final Map<Integer,PlaybackState> lookup = new HashMap<>();

    static {
        for(PlaybackState s : EnumSet.allOf(PlaybackState.class))
            lookup.put(s.getCode(), s);
    }

    PlaybackState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PlaybackState get(int code) {
        PlaybackState result = lookup.get(code);
        return result != null ? result : UNKNOWN;
    }
}
