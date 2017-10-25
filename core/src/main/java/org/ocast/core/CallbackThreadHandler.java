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

import org.ocast.core.function.CallbackWrapper;
import org.ocast.core.function.Consumer;

/**
 * A class handling callback invocations according to a CallbackWrapper that will dispatch the task
 * on a specific thread.
 */
public class CallbackThreadHandler {
    private CallbackWrapper wrapper;
    static CallbackThreadHandler sHandler;

    private CallbackThreadHandler(CallbackWrapper callbackWrapper) {
        wrapper = callbackWrapper;
    }

    public static void init(CallbackWrapper callbackWrapper) {
        sHandler = new CallbackThreadHandler(callbackWrapper);
    }

    private <T> Consumer<T> wrap(Consumer<T> consumer) {
        return wrapper.wrap(consumer);
    }

    private Runnable wrap(Runnable runnable) {
        return wrapper.wrap(runnable);
    }

    public static <T> Consumer<T> callback(Consumer<T> consumer) {
        return sHandler.wrap(consumer);
    }

    public static Runnable callback(Runnable runnable) {
        return sHandler.wrap(runnable);
    }
}
