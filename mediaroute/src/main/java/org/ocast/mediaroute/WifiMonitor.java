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

package org.ocast.mediaroute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiMonitor extends BroadcastReceiver {

    private static final String TAG = WifiMonitor.class.getSimpleName();
    private Listener mListener;
    private boolean mIsConnected;

    public interface Listener {
        void onConnectionStateChanged(boolean onOff);
    }

    public WifiMonitor(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo =
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()) {
                if(!mIsConnected) {
                    Log.d(TAG, "Wifi is connected: " + String.valueOf(networkInfo));
                    mListener.onConnectionStateChanged(true);
                    mIsConnected = true;
                }
            } else {
                if(mIsConnected) {
                    Log.d(TAG, "Wifi is disconnected: " + String.valueOf(networkInfo));
                    mIsConnected = false;
                    mListener.onConnectionStateChanged(false);
                }
            }
        }
    }

    public boolean isConnected() {
        return mIsConnected;
    }
}
