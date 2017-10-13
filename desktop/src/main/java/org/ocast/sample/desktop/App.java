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

package org.ocast.sample.desktop;

import org.ocast.core.ApplicationController;
import org.ocast.core.CallbackThreadHandler;
import org.ocast.core.Device;
import org.ocast.core.DeviceManager;
import org.ocast.core.SimpleWrapper;
import org.ocast.core.function.Consumer;
import org.ocast.core.media.MediaController;
import org.ocast.core.media.MediaType;
import org.ocast.core.media.Metadata;
import org.ocast.core.media.PlaybackStatus;
import org.ocast.core.media.PrepareCommand;
import org.ocast.core.media.TransferMode;
import org.ocast.discovery.DialDevice;
import org.ocast.discovery.Discovery;
import org.ocast.discovery.DiscoveryListener;
import org.ocast.discovery.DiscoveryReliability;
import org.ocast.referencedriver.ReferenceDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;


public class App implements MediaController.MediaControllerListener {

    private static final int PLAY_DURATION = 10 * 1000;
    private final Logger logger = Logger.getLogger("sample");
    private final Discovery activeDiscovery;
    private final CountDownLatch stop = new CountDownLatch(1);
    private final Consumer<Throwable> errorLog = t -> logger.log(Level.WARNING, "error:",t);

    private final DiscoveryListener callback = new DiscoveryListener() {
        @Override
        public void onDeviceAdded(DialDevice dd) {
            activeDiscovery.stop();
            logger.log(Level.INFO, String.format("found %s", dd.getFriendlyName()));
            selectDevice(dd);
            logger.log(Level.INFO,"stopping discovery");
        }

        @Override
        public void onDeviceRemoved(DialDevice dd) {
            logger.log(Level.INFO,"device removed");
        }
    };

    public static void main(String[] args) {
        DeviceManager.registerDriver("Orange SA", new ReferenceDriver.ReferenceFactory());
        App main = new App();
        main.run();
    }

    private App() {
        CallbackThreadHandler.init(new SimpleWrapper());
        activeDiscovery = new Discovery(ReferenceDriver.SEARCH_TARGET, callback, DiscoveryReliability.HIGH);
    }

    private void run() {
        activeDiscovery.start();
        try {
            stop.await();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "error", e);
        }
        System.exit(0);
    }

    private void selectDevice(DialDevice dd) {
        Device selectedDevice = new Device(dd.getUuid(),dd.getFriendlyName(),dd.getManufacturer(),
                dd.getModelName(),dd.getDialURL());
        DeviceManager manager = new DeviceManager(selectedDevice, t -> logger.log(Level.WARNING, "Device error:", t));

        manager.getApplicationController("Orange-DefaultReceiver-DEV",
                this::launchApp,
                errorLog);
    }

    private void launchApp(ApplicationController app) {
        app.start( () -> {
            logger.log(Level.INFO,"Application launched");
            playMovie(app);
            }, errorLog);
    }

    private void playMovie(ApplicationController app) {
        MediaController mediaController = new MediaController(this);
        app.manageStream(mediaController);
        PrepareCommand.Builder builder = new PrepareCommand.Builder();
        try {
            builder.setUrl(new URL("https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/mp4/BigBuckBunny.mp4"));
            builder.setMediaType(MediaType.VIDEO);
            builder.setAutoplay(true);
            builder.setTitle("Big Buck Bunny");
            builder.setSubtitle("");
            builder.setTransferMode(TransferMode.STREAMED);
            builder.setLogo(new URL("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"));
            builder.setUpdateFreq(1);
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "could not play media", e);
        }
        PrepareCommand prepareParams = builder.build();
        mediaController.prepare(prepareParams, () -> {
            logger.log(Level.INFO,"Media launched");
            moviePlaying(app, mediaController);
            }, errorLog);

    }

    private void moviePlaying(ApplicationController app, MediaController media) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                media.stop( () -> stopApplication(app),errorLog);
            }
        }, PLAY_DURATION);
    }

    private void stopApplication(ApplicationController app) {
        app.stop(this::exit, errorLog);
    }

    private void exit() {
        stop.countDown();
    }

    @Override
    public void onPlaybackStatus(PlaybackStatus status) {
        //Do something with the status like updating the UI to show progress
    }

    @Override
    public void onMetadataChanged(Metadata metadata) {
        //Do something with the metadata like updating the UI to allow changing audio track
    }
}
