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

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.media.MediaRouteDescriptor;
import android.support.v7.media.MediaRouteDiscoveryRequest;
import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouteProviderDescriptor;
import android.util.Log;

import org.ocast.discovery.DialDevice;
import org.ocast.discovery.DiscoveryListener;
import org.ocast.discovery.Discovery;
import org.ocast.discovery.DiscoveryReliability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OCastMediaRouteProvider extends MediaRouteProvider implements WifiMonitor.Listener {

    public static final String CATEGORY_OCAST = "org.ocast.CATEGORY_OCAST";
    private static final String TAG = OCastMediaRouteProvider.class.getSimpleName();
    private final Context mContext;
    private Handler mHandler;
    private List<IntentFilter> mCategoryIntentFilterList;
    private IntentFilter mWifiMonitorIntentFilter = new IntentFilter();
    private MediaRouteDiscoveryRequest mCurrentRequest;
    Discovery mActiveDiscovery, mProcessDiscovery;
    private final ConnectivityManager mConnectivityManager;
    private WifiMonitor mWifiMonitorReceiver = new WifiMonitor(this);
    private Map<String, MediaRouteDescriptor> mRoutes = new HashMap<>();

    private DiscoveryListener callback = new DiscoveryListener() {
        @Override
        public void onDeviceAdded(DialDevice dd) {
            Bundle bundledDevice = new Bundle();
            MediaRouteDevice device = new MediaRouteDevice(dd);
            bundledDevice.putParcelable(MediaRouteDevice.EXTRA_DEVICE,device);
            Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(getContext().getPackageName())
                    .build();
            MediaRouteDescriptor routeDescriptor = new MediaRouteDescriptor.Builder(
                    dd.getUuid(),
                    dd.getFriendlyName())
                    .setDescription(dd.getModelName())
                    .setIconUri(uri)
                    .addControlFilters(mCategoryIntentFilterList)
                    .setExtras(bundledDevice)
                    .build();
            mRoutes.put(dd.getUuid(),routeDescriptor);
            publishRoutes();
        }

        @Override
        public void onDeviceRemoved(DialDevice dd) {
            mRoutes.remove(dd.getUuid());
            publishRoutes();
        }
    };

    private void publishRoutes() {
        final MediaRouteProviderDescriptor.Builder providerDescriptorBuilder = new MediaRouteProviderDescriptor.Builder();
        for(MediaRouteDescriptor d: mRoutes.values()) {
            providerDescriptorBuilder.addRoute(d);
        }
        final MediaRouteProviderDescriptor providerDescriptor = providerDescriptorBuilder.build();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setDescriptor(providerDescriptor);
            }
        });
    }

    public OCastMediaRouteProvider(@NonNull Context context, List<String> searchTargets) {
        super(context);
        mContext = context.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
        mActiveDiscovery = new Discovery(searchTargets, callback, DiscoveryReliability.HIGH);
        mProcessDiscovery = new Discovery(searchTargets, callback, DiscoveryReliability.LOW);
        mCategoryIntentFilterList = new ArrayList<>();
        IntentFilter f = new IntentFilter();
        f.addCategory(CATEGORY_OCAST);
        mCategoryIntentFilterList.add(f);
        mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiMonitorIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    @Nullable
    @Override
    public RouteController onCreateRouteController(String routeId) {
        return super.onCreateRouteController(routeId);
    }

    @Override
    public void onDiscoveryRequestChanged(@Nullable MediaRouteDiscoveryRequest request) {
        if(request != null) {
            if(mCurrentRequest == null) {
                mContext.registerReceiver(mWifiMonitorReceiver, mWifiMonitorIntentFilter);
            }
            Log.d(TAG, "onDiscoveryRequest "+request.toString());
            NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            boolean isWiFi = isConnected && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            if(isWiFi || isEmulator()) {
                startDiscovery(request);
            }
        } else {
            mContext.unregisterReceiver(mWifiMonitorReceiver);
            stopDiscovery();
        }
        mCurrentRequest = request;
    }

    private void startDiscovery(MediaRouteDiscoveryRequest request) {
        if (request.isActiveScan()) {
            mProcessDiscovery.stop();
            mActiveDiscovery.start();
        } else {
            mActiveDiscovery.stop();
            mProcessDiscovery.start();

        }
    }

    private void stopDiscovery() {
        Log.d(TAG, "onDiscoveryRequest no discovery required");
        mProcessDiscovery.stop();
        mActiveDiscovery.stop();
    }

    @Override
    public void onConnectionStateChanged(boolean isConnected) {
        if(isConnected) {
            startDiscovery(mCurrentRequest);
            publishRoutes();
        } else {
            setDescriptor(null);
            stopDiscovery();
        }
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
