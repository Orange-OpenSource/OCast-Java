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

import android.databinding.DataBindingUtil;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;
import org.ocast.core.CallbackThreadHandler;
import org.ocast.core.Device;
import org.ocast.referencedriver.ReferenceDriver;
import org.ocast.core.ApplicationController;
import org.ocast.core.DeviceManager;
import org.ocast.core.media.MediaController;
import org.ocast.core.media.Metadata;
import org.ocast.core.media.PrepareCommand;
import org.ocast.core.media.MediaType;
import org.ocast.core.media.PlaybackState;
import org.ocast.core.media.PlaybackStatus;
import org.ocast.core.media.Track;
import org.ocast.core.media.TransferMode;
import org.ocast.mediaroute.MediaRouteDevice;
import org.ocast.mediaroute.OCastMediaRouteProvider;
import org.ocast.mediaroute.OCastRouteHelper;
import org.ocast.sample.mobile.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements MediaController.MediaControllerListener, ViewModel.ViewModelListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MenuItem mediaRouteMenuItem;
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private android.support.v7.media.MediaRouter.Callback mMediaRouterCallBack = new MediaRouterCallBackImpl();

    private DeviceManager mDeviceManager;
    private ViewModel viewmodel;
    private MediaController mediaController;
    private ApplicationController mApplicationController;
    private CustomStream customController;
    private boolean webAppRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CallbackThreadHandler.init(new MainThreadWrapper());
        if (BuildConfig.DEBUG) {
            //adb shell setprop log.tag.ocast.driver.link VERBOSE
            //adb shell setprop log.tag.ocast.driver.link OFF
            try {
                InputStream ins = getResources().openRawResource(R.raw.logging);
                DebugHelper.initLogging(ins);
            } catch (IOException e) {
                Log.e(TAG, "could not initialize logger");
            }
        }

        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main );
        viewmodel = new ViewModel(getString(R.string.webapp_id), this);
        binding.setViewmodel(viewmodel);
        setSupportActionBar(binding.myToolbar);
        binding.buttonStart.setOnClickListener(v->startWebApp());
        binding.buttonStop.setOnClickListener(v-> stopWebApp());
        binding.buttonPlayMedia.setOnClickListener(v -> playMedia());
        binding.buttonPauseMedia.setOnClickListener(v-> pauseMedia());
        binding.buttonStopMedia.setOnClickListener(v -> stopMedia());
        OCastRouteHelper.addMediaRouteProvider(getApplicationContext(), new HashSet<>(Arrays.asList(ReferenceDriver.SEARCH_TARGET)));
        DeviceManager.registerDriver("Orange SA", new ReferenceDriver.ReferenceFactory());
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(OCastMediaRouteProvider.CATEGORY_OCAST)
                .build();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallBack, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    private void pauseMedia() {
        if (mediaController != null) {
            if (viewmodel.isPaused()) {
                mediaController.resume(() -> Log.d("TAG", "stop"), t -> Log.d(TAG, "failure:", t));
            } else {
                mediaController.pause(() -> Log.d("TAG", "stop"), t -> Log.d(TAG, "failure:", t));
            }
        }
    }

    private void onMetadata(Metadata m) {
        Log.d(TAG,"getMetadata success " + m.getTitle());
        viewmodel.setAudioTracks(m.getAudioTracks());
        viewmodel.setSubtitleTracks(m.getSubtitleTracks());
        viewmodel.mediaTitle.set(m.getTitle());
        viewmodel.mediaType.set(m.getMediaType().name());
    }

    private void stopMedia() {
        if (mediaController != null) {
            mediaController.stop(() -> Log.d("TAG", "stop"), t -> Log.d(TAG, "failure:", t));
        }
    }

    private void playMedia() {
        PrepareCommand.Builder builder = new PrepareCommand.Builder();
        try {
            builder.setUrl(new URL("http://sample.vodobox.com/planete_interdite/planete_interdite_alternate.m3u8"));
            builder.setMediaType(MediaType.VIDEO);
            builder.setAutoplay(true);
            builder.setTitle("Planète interdite");
            builder.setSubtitle("");
            builder.setTransferMode(TransferMode.STREAMED);
            builder.setLogo(new URL("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Orange_logo.svg/240px-Orange_logo.svg.png"));
            builder.setUpdateFreq(1);
            /*
            Sample code to add custom options
            JSONObject options = new JSONObject();
            options.put("optional_key", "optional_value");
            builder.setOptions(options);
            */
            PrepareCommand prepareParams = builder.build();
            mediaController.prepare(prepareParams, () -> mediaController.getMetadata(m -> onMetadata(m), t -> Log.d(TAG, "failure",t)), t -> Log.d(TAG, "failure:",t));
        } catch (Exception e) {
            Log.e(TAG, "could not play media", e);
        }
    }

    private void startWebApp() {
        viewmodel.setWebAppStatus("start-pending");
        mApplicationController.start(
                () -> webAppRunning("start-OK" ),
                t -> viewmodel.setWebAppStatus("start-NOK", t));
    }

    private void stopWebApp() {
        if (mApplicationController != null && webAppRunning) {
            webAppRunning = false;
            mApplicationController.stop(
                    () -> viewmodel.setWebAppStatus("stop-OK"),
                    t -> viewmodel.setWebAppStatus("stop-NOK", t));
        }
    }

    private void webAppRunning(String status) {
        viewmodel.setWebAppStatus(status);
        mediaController =  new MediaController(this);
        mApplicationController.manageStream(mediaController);
        customController = new CustomStream();
        mApplicationController.manageStream(customController);
        mediaController.getMetadata(this::onMetadata, t->Log.d(TAG,"could not get metadata"));
        webAppRunning = true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider actionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);

        MediaRouteSelector.Builder builder = new MediaRouteSelector.Builder().addControlCategory(OCastMediaRouteProvider.CATEGORY_OCAST);
        MediaRouteSelector customSelector = builder.build();
        actionProvider.setRouteSelector(customSelector);

        return true;
    }

    @Override
    public void onPlaybackStatus(PlaybackStatus status) {
        Log.d(TAG, "playbackStatus:" + status.getPosition());
        if (status.getState() == PlaybackState.PAUSED) {
            viewmodel.setPaused(true);
        } else if(status.getState() == PlaybackState.PLAYING){
            viewmodel.setPaused(false);
        }
        viewmodel.duration.set(status.getDuration());
        viewmodel.position.set(status.getPosition());
        viewmodel.playerState.set(status.getState().toString());
        viewmodel.volumeLevel.set(status.getVolume()*1000);
        viewmodel.mute.set(status.isMute());
    }

    @Override
    public void onMetadataChanged(Metadata metadata) {
        onMetadata(metadata);
    }

    @Override
    public void onAudioTrackChanged(Track track) {
        if (mediaController != null) {
            mediaController.track(track, () -> Log.d(TAG, "success"), t -> Log.d(TAG, "error: ", t));
        }
    }

    @Override
    public void onSubtitleTrackChanged(Track track) {
        if (mediaController != null) {
            mediaController.track(track, () -> Log.d(TAG, "success"), t -> Log.d(TAG, "error: ", t));
        }
    }

    @Override
    public void onVolumeChanged(double volumeLevel) {
        if (mediaController != null) {
            mediaController.volume(volumeLevel, () -> Log.d(TAG, "success"), t -> Log.d(TAG, "error: ", t));
        }
    }

    @Override
    public void onCheckedChanged(boolean isMuted) {
        if (mediaController != null) {
            mediaController.mute(isMuted, () -> Log.d(TAG, "success"), t -> Log.d(TAG, "error: ", t));
        }
    }

    @Override
    public void onSeek(long position) {
        if (mediaController != null) {
            mediaController.seek(position, () -> Log.d(TAG, "success"), t -> Log.d(TAG, "error: ", t));
        }
    }

    @Override
    public void onCustomDataClicked() {
        if (customController != null) {
            customController.sendCustomData(j -> Log.d(TAG, "custom:" + j), t -> Log.d(TAG, "failure:", t));
        }
    }

    public class MediaRouterCallBackImpl extends MediaRouter.Callback {
        MediaRouteSelector orangeSelector = new MediaRouteSelector.Builder().addControlCategory(OCastMediaRouteProvider.CATEGORY_OCAST).build();

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
            if (route.matchesSelector(orangeSelector)) {
                Log.d(TAG, "selected a OCast device");
                Bundle extras = route.getExtras();
                if (extras.containsKey(MediaRouteDevice.EXTRA_DEVICE)) {
                    MediaRouteDevice d = extras.getParcelable(MediaRouteDevice.EXTRA_DEVICE);
                    Device device = new Device(d.getUuid(),d.getFriendlyName(), d.getManufacturer(), d.getModelName(), d.getDialURI());
                    mDeviceManager = new DeviceManager(device, MainActivity.this::onDeviceStateChanged);
                    mDeviceManager.getApplicationController(viewmodel.getWebAppName(),
                            app -> {
                                mApplicationController = app;
                                viewmodel.setConnected(true);
                    }, t -> Log.d(TAG, "failure: " + t));
                }
            }
        }

        @Override
        public void onRouteUnselected(MediaRouter mediaRouter, MediaRouter.RouteInfo route) {
            if (route.matchesSelector(orangeSelector)) {
                Log.d(TAG, "unselected an OCast device");
            }
        }

        @Override
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
            if (route.matchesSelector(orangeSelector)) {
                Log.d(TAG, "removed an OCast device");
            }
        }
    }

    private void onDeviceStateChanged(DeviceManager.Failure s) {
        Log.d(TAG, "device state changed:" + s);
        if (s.equals(DeviceManager.Failure.DEVICE_LOST)) {
            viewmodel.setConnected(false);
        }
    }
}
