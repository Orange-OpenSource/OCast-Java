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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describes the status of the current media
 */
public class PlaybackStatus {
    private static final String KEY_POSITION = "position";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_STATUS = "state";

    private final float volume;
    private final boolean mute;
    private final PlaybackState state;
    private final double position;
    private final double duration;

    static class Builder {
        private float volume;
        private boolean mute;
        private PlaybackState state;
        private double position;
        private double duration;

        public Builder setVolume(float volume) {
            this.volume = volume;
            return this;
        }

        public Builder setMute(boolean mute) {
            this.mute = mute;
            return this;
        }

        public Builder setState(PlaybackState state) {
            this.state = state;
            return this;
        }

        public Builder setPosition(double position) {
            this.position = position;
            return this;
        }

        public Builder setDuration(double duration) {
            this.duration = duration;
            return this;
        }

        public PlaybackStatus build() {
            return new PlaybackStatus(volume, mute, state, position, duration);
        }
    }

    private PlaybackStatus(float volume, boolean mute, PlaybackState state, double position, double duration) {
        this.volume = volume;
        this.mute = mute;
        this.state = state;
        this.position = position;
        this.duration = duration;
    }

    public float getVolume() {
        return volume;
    }

    public boolean isMute() {
        return mute;
    }

    public PlaybackState getState() {
        return state;
    }

    public double getPosition() {
        return position;
    }

    public double getDuration() {
        return duration;
    }

    static PlaybackStatus decode(JSONObject json) throws JSONException {
        PlaybackStatus.Builder builder = new PlaybackStatus.Builder();
        double position = json.getDouble(KEY_POSITION);
        builder.setPosition(position);
        double duration = json.getDouble(KEY_DURATION);
        builder.setDuration(duration);
        String state = json.getString(KEY_STATUS);
        try {
            builder.setState(PlaybackState.valueOf(state.toUpperCase()));
        } catch(IllegalArgumentException e) {
            throw new JSONException("invalid status:" + state);
        }
        return builder.build();
    }
}
