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

import java.net.URL;

/**
 * Describes the media to be casted.
 */
public class PrepareCommand extends MediaCommand {
    private static final String KEY_URL = "url";
    private static final String KEY_FREQUENCY = "frequency";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_LOGO = "logo";
    private static final String KEY_MEDIA_TYPE = "mediaType";
    private static final String KEY_TRANSFER_MODE = "transferMode";
    private static final String KEY_AUTOPLAY = "autoplay";
    private final URL url;
    private final int updateFreq;
    private final String title;
    private final String subtitle;
    private final URL logo;
    private final MediaType mediaType;
    private final TransferMode transferMode;
    private final boolean autoplay;

    /**
     * Builder to initialize and instanciate a PrepareCommand
     */
    public static class Builder {
        private URL url;
        private int updateFreq;
        private String title;
        private String subtitle;
        private URL logo;
        private MediaType mediaType;
        private TransferMode transferMode;
        private boolean autoplay;
        private JSONObject options;

        public Builder setUrl(URL url) {
            this.url = url;
            return this;
        }

        public Builder setUpdateFreq(int updateFreq) {
            this.updateFreq = updateFreq;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder setLogo(URL logo) {
            this.logo = logo;
            return this;
        }

        public Builder setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setTransferMode(TransferMode transferMode) {
            this.transferMode = transferMode;
            return this;
        }

        public Builder setAutoplay(boolean autoplay) {
            this.autoplay = autoplay;
            return this;
        }

        public Builder setOptions(JSONObject options) {
            this.options = options;
            return this;
        }

        public PrepareCommand build() {
            return new PrepareCommand(url, updateFreq, title, subtitle, logo, mediaType, transferMode, autoplay, options);
        }
    }

    private PrepareCommand(URL url, int updateFreq, String title, String subtitle, URL logo, MediaType mediaType, TransferMode transferMode, boolean autoplay, JSONObject options) {
        super("prepare", options);
        this.url = url;
        this.updateFreq = updateFreq;
        this.title = title;
        this.subtitle = subtitle;
        this.logo = logo;
        this.mediaType = mediaType;
        this.transferMode = transferMode;
        this.autoplay = autoplay;
    }

    @Override
    public JSONObject getParams() throws JSONException {
        JSONObject params = new JSONObject();
        params.put(KEY_URL, url.toString());
        params.put(KEY_FREQUENCY, updateFreq);
        params.put(KEY_TITLE, title);
        params.put(KEY_SUBTITLE, subtitle);
        if (logo != null) {
            params.put(KEY_LOGO, logo.toString());
        }
        params.put(KEY_MEDIA_TYPE, mediaType.name().toLowerCase());
        params.put(KEY_TRANSFER_MODE, transferMode.name().toLowerCase());
        params.put(KEY_AUTOPLAY, autoplay);
        return params;
    }
}
