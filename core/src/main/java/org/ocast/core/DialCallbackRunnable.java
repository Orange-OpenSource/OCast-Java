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
import org.ocast.core.function.ThrowingBiConsumer;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

class DialCallbackRunnable implements okhttp3.Callback {
    private final Runnable success;
    private final Consumer<Throwable> failure;
    private final ThrowingBiConsumer<Call, Response, ApplicationException> handler;

    public DialCallbackRunnable(Runnable success, Consumer<Throwable> failure, ThrowingBiConsumer<Call, Response, ApplicationException> handler) {
        this.success = success;
        this.failure = failure;
        this.handler = handler;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        failure.accept(new ApplicationException(DialError.NETWORK_ERROR, e));

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            if (response.isSuccessful()) {
                handler.accept(call, response);
                success.run();
            } else if (response.code() == 404) {
                throw new ApplicationException(DialError.APPLICATION_NOT_FOUND);
            } else {
                throw new ApplicationException(DialError.INTERNAL_ERROR);
            }
        } catch (ApplicationException e) {
            failure.accept(e);
        }
    }
}
