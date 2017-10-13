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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class SSLConfig {

    private final X509TrustManager trustManager;
    private final SSLSocketFactory socketFactory;
    private final HostnameVerifier hostnameVerifier;

    /**
     * Configuration that will be used by the underlying {@link org.ocast.core.Link Link} to setup SSL
     * @param trustManager a TrustManager instance that suits your needs
     * @param socketFactory a SocketFactory instance that suits your needs
     * @param hostnameVerifier a custom hostname verifier
     */
    public SSLConfig(X509TrustManager trustManager,
                     SSLSocketFactory socketFactory,
                     HostnameVerifier hostnameVerifier) {
        this.trustManager = trustManager;
        this.socketFactory = socketFactory;
        this.hostnameVerifier = hostnameVerifier;
    }

    /**
     * Return the configured TrustManager
     * @return
     */
    public X509TrustManager getTrustManager() {
        return trustManager;
    }

    /**
     * Return the configured SocketFactory
     * @return
     */
    public SSLSocketFactory getSocketFactory() {
        return socketFactory;
    }

    /**
     * Return the custom HostnameVerifier
     * @return
     */
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }
}
