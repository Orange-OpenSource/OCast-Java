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

package org.ocast.core.media;

import org.ocast.core.DataStream;
import org.ocast.core.DriverException;
import org.ocast.core.function.Consumer;

import org.ocast.core.function.ThrowingConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A dedicated {@link org.ocast.core.DataStream DataStream} that manages media control like playing a
 * video, audio or loading picture
 */
public class MediaController extends DataStream {

    private static final String SERVICE_NAME = "org.ocast.media";
    private final MediaControllerListener listener;

    /**
     * Provides informations on playback status (position, playing,...) and metadata (title, tracks,...)
     */
    public interface MediaControllerListener {
        /**
         * called when a media status has been received from the web application
         * @param status the updated status
         */
        void onPlaybackStatus(PlaybackStatus status);

        /**
         * called when the media metadata changed.
         * @param metadata the updated metadata
         */
        void onMetadataChanged(Metadata metadata);
    }

    /**
     * Instanciate a MediaController
     * @param listener the listener to be notified of metadata and playback status updates
     */
    public MediaController(MediaControllerListener listener) {
        super(SERVICE_NAME);
        this.listener = listener;
    }

    /**
     * Prepare a media playback
     * @param command the data representing the media to be casted.
     * @param onSuccess to be called in case of success.
     * @param onFailure to be called in case of error
     */
    public void prepare(PrepareCommand command, Runnable onSuccess, Consumer<Throwable> onFailure) {
        sendSimpleCommand(onSuccess, onFailure, command);
    }

    /**
     * Stops the current media playback
     * @param onSuccess
     * @param onFailure
     */
    public void stop(Runnable onSuccess, Consumer<Throwable> onFailure) {
        MediaCommand command = new MediaCommand("stop");
        sendSimpleCommand(onSuccess, onFailure, command);
    }

    /**
     * Pauses the current media playback
     * @param onSuccess
     * @param onFailure
     */
    public void pause(Runnable onSuccess, Consumer<Throwable> onFailure) {
        MediaCommand command = new MediaCommand("pause");
        sendSimpleCommand(onSuccess, onFailure, command);
    }

    /**
     * Resumes the current media playback
     * @param onSuccess
     * @param onFailure
     */
    public void resume(Runnable onSuccess, Consumer<Throwable> onFailure) {
        MediaCommand command = new MediaCommand("resume");
        sendSimpleCommand(onSuccess, onFailure, command);
    }

    /**
     * Plays the current media from a given position
     * @param position the position expressed as seconds since start
     * @param onSuccess
     * @param onFailure
     */
    public void play(double position, Runnable onSuccess, Consumer<Throwable> onFailure) {
        //TODO
    }

    /**
     * Sets the volume level
     * @param level a value in a [0..1] interval
     * @param onSuccess
     * @param onFailure
     */
    public void volume(float level, Runnable onSuccess, Consumer<Throwable> onFailure) {
        //TODO
    }

    /**
     * Mute the current playback
     * @param enable true to mute, false to unmute
     * @param onSuccess
     * @param onFailure
     */
    public void mute(boolean enable, Runnable onSuccess, Consumer<Throwable> onFailure) {
        //TODO
    }

    /**
     * Seeks the current media to a given position
     * @param position the position expressed as seconds since start
     * @param onSuccess
     * @param onFailure
     */
    public void seek(double position, Runnable onSuccess, Consumer<Throwable> onFailure) {
        MediaCommand command = new SeekCommand((long)position);
        sendSimpleCommand(onSuccess, onFailure, command);
    }

    /**
     * enable or disable media {@link org.ocast.core.media.Track} track
     * @param track the track information
     * @param onSuccess
     * @param onFailure
     */
    public void track(Track track, Runnable onSuccess, Consumer<Throwable> onFailure) {
        MediaCommand command = new TrackCommand(track.getType(), track.getTrackId(), track.isEnable());
        sendSimpleCommand(onSuccess, onFailure, command);

    }

    /**
     * Gets current media status.
     * @param onSuccess to be called on success with the PlaybackStatus
     * @param onFailure
     */
    public void getPlaybackStatus(Consumer<PlaybackStatus> onSuccess, Consumer<Throwable> onFailure) {
        MediaCommand command = new MediaCommand("getPlaybackStatus");
        ThrowingConsumer<JSONObject,Exception> jsonProcessing = json -> {
            PlaybackStatus s = PlaybackStatus.decode(MediaCommand.getReplyParams(json));
            onSuccess.accept(s);
        };
        sendCommand(command, jsonProcessing, onFailure);
    }

    /**
     * Gets current media metadata.
     * @param onSuccess to be called on success with the Metadata
     * @param onFailure to be called on error
     */
    public void getMetadata(Consumer<Metadata> onSuccess, Consumer<Throwable> onFailure) {
        MediaCommand command = new MediaCommand("getMetadata");
        ThrowingConsumer<JSONObject,Exception> jsonProcessing = json -> {
            Metadata m = Metadata.decode(MediaCommand.getReplyParams(json));
            onSuccess.accept(m);
        };
        sendCommand(command, jsonProcessing, onFailure);
    }

    @Override
    public void onMessage(JSONObject message) {
        Logger.getLogger(SERVICE_NAME).log(Level.FINEST, "onMessage: {0}", message);
        try {
            MediaEvent mediaEvent = MediaEvent.decode(message);
            switch(mediaEvent.getName()) {
                case "playbackStatus":
                    PlaybackStatus playbackStatus = PlaybackStatus.decode(mediaEvent.getParams());
                    listener.onPlaybackStatus(playbackStatus);
                    break;
                case "metadataChanged":
                    Metadata metadata = Metadata.decode(mediaEvent.getParams());
                    listener.onMetadataChanged(metadata);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            Logger.getLogger(SERVICE_NAME).log(Level.WARNING, "could not parse message", e);
        }
    }

    private void sendSimpleCommand(Runnable onSuccess, Consumer<Throwable> onFailure, MediaCommand command) {
        ThrowingConsumer<JSONObject,Exception> replyHandler = json -> onSuccess.run();
        sendCommand(command, replyHandler, onFailure);
    }

    private void sendCommand(MediaCommand command, ThrowingConsumer<JSONObject, Exception> jsonProcessing, Consumer<Throwable> onFailure) {
        try {
            ThrowingConsumer<JSONObject, Exception> replyHandler = j -> {
                ReplyStatus status = MediaCommand.decode(j);
                if (status == ReplyStatus.SUCCESS) {
                    jsonProcessing.accept(j);
                } else {
                    onFailure.accept(new DriverException(String.valueOf(status.getCode())));
                }
            };
            Consumer<JSONObject> success = ThrowingConsumer.checked(replyHandler
                    , onFailure);
            sendMessage(command.encode(), success, onFailure);
        } catch (JSONException e) {
            onFailure.accept(e);
        }
    }
}
