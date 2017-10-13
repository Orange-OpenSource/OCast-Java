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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Metadata {
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_LOGO = "logo";
    private static final String KEY_MEDIA_TYPE = "mediaType";
    private static final String KEY_SUBTITLE_TRACKS = "textTracks";
    private static final String KEY_AUDIO_TRACKS = "audioTracks";
    private static final String KEY_VIDEO_TRACKS = "videoTracks";

    private static final String KEY_TRACK_TYPE = "type";
    private static final String KEY_TRACK_LANGUAGE = "language";
    private static final String KEY_TRACK_LABEL = "label";
    private static final String KEY_TRACK_ENABLED = "enabled";
    private static final String KEY_TRACK_ID = "trackId";

    private final String title;
    private final String subtitle;
    private final URI logo;
    private final MediaType mediaType;
    private final List<Track> subtitleTracks;
    private final List<Track> audioTracks;
    private final List<Track> videoTracks;

    public static class Builder {
        private String title;
        private String subtitle;
        private URI logo;
        private MediaType mediaType;
        private List<Track> subtitleTracks;
        private List<Track> audioTracks;
        private List<Track> videoTracks;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder setLogo(URI logo) {
            this.logo = logo;
            return this;
        }

        public Builder setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setSubtitleTracks(List<Track> subtitleTracks) {
            this.subtitleTracks = subtitleTracks;
            return this;
        }

        public Builder setAudioTracks(List<Track> audioTracks) {
            this.audioTracks = audioTracks;
            return this;
        }

        public Builder setVideoTracks(List<Track> videoTracks) {
            this.videoTracks = videoTracks;
            return this;
        }

        public Metadata build() {
            return new Metadata(title, subtitle, logo, mediaType, subtitleTracks, audioTracks, videoTracks);
        }
    }

    public Metadata(String title, String subtitle, URI logo, MediaType mediaType, List<Track> subtitleTracks, List<Track> audioTracks, List<Track> videoTracks) {
        this.title = title;
        this.subtitle = subtitle;
        this.logo = logo;
        this.mediaType = mediaType;
        this.subtitleTracks = subtitleTracks;
        this.audioTracks = audioTracks;
        this.videoTracks = videoTracks;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public URI getLogo() {
        return logo;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public List<Track> getSubtitleTracks() {
        return subtitleTracks;
    }

    public List<Track> getAudioTracks() {
        return audioTracks;
    }

    public List<Track> getVideoTracks() {
        return videoTracks;
    }

    public static Metadata decode(JSONObject json) throws JSONException {
        Metadata.Builder builder = new Metadata.Builder();
        builder.setTitle(json.getString(KEY_TITLE));
        builder.setSubtitle(json.getString(KEY_SUBTITLE));
        try {
            builder.setLogo(new URI(json.getString(KEY_LOGO)));
        } catch (URISyntaxException e) {
            throw new JSONException(("invalid logo url"));
        }
        String mediaType = json.getString(KEY_MEDIA_TYPE);
        try {
            builder.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));
        } catch(IllegalArgumentException e) {
            throw new JSONException("invalid mediaType:" + mediaType);
        }
        JSONArray opt = json.optJSONArray(KEY_SUBTITLE_TRACKS);
        if(opt != null) {
            builder.setSubtitleTracks(parseTrackList(opt));
        }
        opt = json.optJSONArray(KEY_AUDIO_TRACKS);
        if(opt != null) {
            builder.setAudioTracks(parseTrackList(opt));
        }
        opt = json.optJSONArray(KEY_VIDEO_TRACKS);
        if(opt != null) {
            builder.setVideoTracks(parseTrackList(opt));
        }
        return builder.build();
    }

    private static List<Track> parseTrackList(JSONArray array) throws JSONException {
        List<Track> result = new ArrayList<>(array.length());
        for(int i=0; i< array.length(); i++) {
            Track t = parseTrack(array.getJSONObject(i));
            result.add(t);
        }
        return result;
    }

    private static Track parseTrack(JSONObject json) throws JSONException{
        Track.Builder builder = new Track.Builder();
        String mediaType = json.getString(KEY_TRACK_TYPE);
        try {
            builder.setTrackType(Track.Type.valueOf(mediaType.toUpperCase()));
        } catch(IllegalArgumentException e) {
            throw new JSONException("invalid mediaType:" + mediaType);
        }
        builder.setLanguage(json.getString(KEY_TRACK_LANGUAGE));
        builder.setLabel(json.getString(KEY_TRACK_LABEL));
        builder.setEnable(json.getBoolean(KEY_TRACK_ENABLED));
        builder.setTrackId(json.getString(KEY_TRACK_ID));
        return builder.build();
    }
}
