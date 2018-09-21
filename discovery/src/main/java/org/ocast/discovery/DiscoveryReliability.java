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

package org.ocast.discovery;

/**
 * Defines a level a reliability for the discovery process
 *
 * It is based on two factors: the timeout of a discovery request and the number of attempts before
 * considering a device has been lost.
 */
public class DiscoveryReliability {
    private final int mTimeout;
    private final int mRetry;

    /**
     * defines a discovery request to be sent every 3 seconds, consider a device lost if it didn't
     * respond 2 consecutive times.
     */
    public static final DiscoveryReliability HIGH = new DiscoveryReliability(20, 2);
    /**
     * defines a discovery request to be sent every 6 seconds, consider a device lost if it didn't
     * respond 3 consecutive times.
     */
    public static final DiscoveryReliability MEDIUM = new DiscoveryReliability(30, 3);
    /**
     * defines a discovery request to be sent every 10 seconds, consider a device lost if it didn't
     * respond 5 consecutive times.
     */
    public static final DiscoveryReliability LOW = new DiscoveryReliability(60, 5);

    /**
     * Constructs an object and initializes it with the provided values
     * @param timeout timeout for a discovery request (seconds)
     * @param retry number of attempts before considering a device has been lost
     */
    public DiscoveryReliability(int timeout, int retry) {
        this.mTimeout = timeout;
        this.mRetry = retry;
    }

    /**
     * get the retry number before considering a device got lost
     * @return the number of retry
     */
    public int getRetry() {
        return mRetry;
    }

    /**
     * get the discovery timeout to consider no device responded
     * @return a duration in seconds
     */
    public int getTimeout() {
        return mTimeout;
    }

    @Override
    public String toString() {
        return "reliability [retry:"+mRetry+" timeout:"+mTimeout+"]";
    }
}
