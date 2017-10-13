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

package org.ocast.sample.mobile;


import android.os.Handler;
import android.os.Looper;

import org.ocast.core.function.CallbackWrapper;
import org.ocast.core.function.Consumer;

public class MainThreadWrapper implements CallbackWrapper {
    public MainThreadWrapper() {
    }

    @Override
    public  <T> Consumer<T> wrap(Consumer<T> consumer) {
        return i -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> consumer.accept(i));
        };
    }

    @Override
    public Runnable wrap(Runnable runnable) {
        return () -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(runnable::run);
        };
    }
}