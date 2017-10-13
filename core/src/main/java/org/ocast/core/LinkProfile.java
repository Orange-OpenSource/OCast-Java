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


import java.net.URI;

/**
 * Describes properties of a {@link org.ocast.core.Link Link}
 * These properties are :
 * - an app to app URL that will be used to setup the underlying channel
 * - a hostname, for cases where the URL is not known by the upper layers
 * - an {@link org.ocast.core.SSLConfig SSLConfig} to setup SSL channel
 */
public class LinkProfile {
    private final SSLConfig sslConfig;
    private final String app2AppUrl;
    private final String hostname;

    /**
     * Builder class to construct new instances
     */
    public static class Builder {
        private String app2AppUrl;
        private String hostname;
        private SSLConfig sslConfig;

        /**
         * Sets the app to app URL for this profile
         * @param app2AppUrl the URL to be used to communicate with the remote browser
         * @return the builder
         */
        public Builder setApp2AppUrl(String app2AppUrl) {
            this.app2AppUrl = app2AppUrl;
            return this;
        }

        /**
         * Sets the hostname for this profile
         * @param hostname the hostname of the remote device
         * @return the builder
         */
        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        /**
         * Sets the {@link org.ocast.core.SSLConfig SSLConfig} for this profile
         * @param sslConfig the SSL configuration details
         * @return the builder
         */
        public Builder setSslConfig(SSLConfig sslConfig) {
            this.sslConfig = sslConfig;
            return this;
        }

        /**
         * Constructs the new object instance
         * @return a LinkProfile instance
         */
        public LinkProfile build() {
            return new LinkProfile(app2AppUrl, hostname, sslConfig);
        }
    }

    private LinkProfile(String app2AppUrl, String hostname,SSLConfig sslConfig) {
        this.app2AppUrl = app2AppUrl;
        this.sslConfig = sslConfig;
        this.hostname = hostname;
    }

    /**
     * Returns the {@link org.ocast.core.SSLConfig SSLConfig}
     * @return
     */
    public SSLConfig getSSLConfig() {
        return sslConfig;
    }

    /**
     * Returns the app to app URL
     * @return
     */
    public String getApp2AppUrl() {
        return app2AppUrl;
    }

    /**
     * Returns the hostname
     * @return
     */
    public String getHostname() {
        if(hostname == null && app2AppUrl != null) {
            return URI.create(app2AppUrl).getHost();
        } else {
            return hostname;
        }
    }
}
