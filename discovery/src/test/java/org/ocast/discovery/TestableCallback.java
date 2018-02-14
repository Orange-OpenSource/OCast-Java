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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestableCallback<T> {
    private final CountDownLatch mLatch;
    private T mResult;

    public TestableCallback() {
        mLatch = new CountDownLatch(1);
    }

    public TestableCallback(int count) {
        mLatch  = new CountDownLatch(count);
    }

    public T getResult() {
        return mResult;
    }

    public void setResult(T result) {
        mResult = result;
    }

    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        mLatch.await(timeout, unit);
    }

    public void await() throws InterruptedException {
        mLatch.await();
    }

    public void countDown() {
        mLatch.countDown();
    }
}
