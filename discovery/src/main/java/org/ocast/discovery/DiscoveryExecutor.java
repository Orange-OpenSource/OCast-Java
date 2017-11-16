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
 * Defines the logic of an object managing a discovery process
 * @param <T>
 */
public interface DiscoveryExecutor<T> {
    /**
     * Listener interface invoked during the discovery process
     * @param <T>
     */
    interface ExecutorListener<T> {
        /**
         * Called when a discovery request has been sent
         */
        void onLocationSent();

        /**
         * Called when a location has been detected
         * @param message the corresponding response
         */
        void onLocationReceived(T message);

        /**
         * Called when an error occured
         */
        void onError();
    }

    /**
     * adds a listener to this executor
     * @param listener the listener to be notified
     */
    void addListener(ExecutorListener<T> listener);

    /**
     * removes this listener from the list of litener this executor notifies
     * @param listener
     */
    void removeListener(ExecutorListener<T> listener);

    /**
     * starts the discovery process
     */
    void start();

    /**
     * stops the discovery process
     */
    void stop();
}
