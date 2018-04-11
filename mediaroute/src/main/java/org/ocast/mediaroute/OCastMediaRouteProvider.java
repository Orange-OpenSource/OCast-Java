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

import org.ocast.discovery.DiscoveredDevice;
import org.ocast.discovery.Discovery;
import org.ocast.discovery.SSDPDiscovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A media route provider publishing OCast routes
 */
public class OCastMediaRouteProvider extends MediaRouteProvider implements WifiMonitor.Listener {

    public static final String CATEGORY_OCAST = "org.ocast.CATEGORY_OCAST";
    private static final String TAG = OCastMediaRouteProvider.class.getSimpleName();
    private final Context mContext;
    private Handler mHandler;
    private List<IntentFilter> mCategoryIntentFilterList;
    private IntentFilter mWifiMonitorIntentFilter = new IntentFilter();
    private MediaRouteDiscoveryRequest mCurrentRequest;
    private final SSDPDiscovery mSSDPDiscovery;
    private final ConnectivityManager mConnectivityManager;
    private WifiMonitor mWifiMonitorReceiver = new WifiMonitor(this);
    private Map<String, MediaRouteDescriptor> mRoutes = Collections.synchronizedMap(new HashMap<>());

    private Discovery.DiscoveryListener listener = new Discovery.DiscoveryListener() {
        @Override
        public void onDeviceAdded(DiscoveredDevice dd) {
            MediaRouteDescriptor routeDescriptor = createMediaRouteDescriptor(dd);
            mRoutes.put(dd.getFriendlyName(),routeDescriptor);
            publishRoutes();
        }

        @Override
        public void onDeviceRemoved(DiscoveredDevice dd) {
            for (Iterator<Map.Entry<String, MediaRouteDescriptor>> iterator = mRoutes.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, MediaRouteDescriptor> entry = iterator.next();
                String friendlyName = entry.getKey();
                if (friendlyName.equals(dd.getFriendlyName())) {
                    iterator.remove();
                }
            }
            publishRoutes();
        }

        @NonNull
        private MediaRouteDescriptor createMediaRouteDescriptor(DiscoveredDevice device) {
            Bundle bundledDevice = new Bundle();
            MediaRouteDevice mediaRouteDevice = new MediaRouteDevice(device);
            bundledDevice.putParcelable(MediaRouteDevice.EXTRA_DEVICE,mediaRouteDevice);
            Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(getContext().getPackageName())
                    .build();
            return new MediaRouteDescriptor.Builder(
                    device.getUuid(),
                    device.getFriendlyName())
                    .setDescription(device.getModelName())
                    .setIconUri(uri)
                    .addControlFilters(mCategoryIntentFilterList)
                    .setExtras(bundledDevice)
                    .build();
        }
    };

    private void publishRoutes() {
        final MediaRouteProviderDescriptor.Builder providerDescriptorBuilder = new MediaRouteProviderDescriptor.Builder();
        for (Iterator<Map.Entry<String, MediaRouteDescriptor>> iterator = mRoutes.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, MediaRouteDescriptor> entry = iterator.next();
            providerDescriptorBuilder.addRoute(entry.getValue());
        }
        final MediaRouteProviderDescriptor providerDescriptor = providerDescriptorBuilder.build();
        mHandler.post(() -> setDescriptor(providerDescriptor));
    }

    public OCastMediaRouteProvider(@NonNull Context context, Set<String> searchTargets) {
        super(context);
        mContext = context.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
        mSSDPDiscovery = new SSDPDiscovery(searchTargets, listener);
        mCategoryIntentFilterList = new ArrayList<>();
        IntentFilter f = new IntentFilter();
        f.addCategory(CATEGORY_OCAST);
        mCategoryIntentFilterList.add(f);
        mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiMonitorIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    @Override
    public void onDiscoveryRequestChanged(@Nullable MediaRouteDiscoveryRequest request) {
        if(request != null) {
            if(mCurrentRequest == null) {
                mContext.registerReceiver(mWifiMonitorReceiver, mWifiMonitorIntentFilter);
                mRoutes.clear();
                publishRoutes();
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
        mSSDPDiscovery.start(request.isActiveScan());
    }

    private void stopDiscovery() {
        Log.d(TAG, "onDiscoveryRequest no discovery required");
        mSSDPDiscovery.stop();
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
