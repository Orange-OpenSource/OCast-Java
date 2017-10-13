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

import org.ocast.core.function.Consumer;

/**
 * Interface PublicSettings is implemented by driver libraries to provide access to public
 * settings configuration
 */
public interface PublicSettings {
    /**
     * Retrieves the {@link org.ocast.core.VersionInfo VersionInfo} of the device
     * @param onSuccess to be called on success
     * @param onFailure to be called when an error occured
     */
    void getVersion(Consumer<VersionInfo> onSuccess, Consumer<Throwable> onFailure);
}
