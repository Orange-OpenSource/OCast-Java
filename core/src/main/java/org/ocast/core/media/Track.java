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

/**
 * Describes a media track
 */
public class Track {

    /**
     * Defines the track types
     */
    public enum Type {
        TEXT,
        AUDIO,
        VIDEO;
    }

    private final Type type;
    private final String language; // iso639-1/2
    private final String label;
    private final boolean enable;
    private final String trackId;

    static class Builder {
        private Type type;
        private String language;
        private String label;
        private boolean enable;
        private String trackId;

        public Builder setTrackType(Type trackType) {
            this.type = trackType;
            return this;
        }

        public Builder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public Builder setTrackId(String trackId) {
            this.trackId = trackId;
            return this;
        }

        public Track build() {
            return new Track(type, language, label, enable, trackId);
        }
    }

    public Track(Type type, String language, String label, boolean enable, String trackId) {
        this.type = type;
        this.language = language;
        this.label = label;
        this.enable = enable;
        this.trackId = trackId;
    }

    public Type getType() {
        return type;
    }
    public String getLanguage() {
        return language;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getTrackId() {
        return trackId;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", label,language);
    }
}
