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

package org.ocast.referencedriver;

import org.ocast.core.LinkProfile;
import org.ocast.core.SSLConfig;
import org.ocast.referencedriver.payload.EventPayload;
import org.ocast.referencedriver.payload.Payload;
import org.ocast.core.DriverEvent;
import org.ocast.core.DriverException;
import org.ocast.core.Link;
import org.ocast.core.LogTag;
import org.ocast.core.Reply;
import org.ocast.core.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * Defines a logical link between the driver and a remote device
 */
public class ReferenceLink implements Link {
    private static final String TAG = LogTag.LINK;
    public static final int PING_INTERVAL = 7;


    private final SSLConfig sslConfig;
    private final String websocketUrl;
    private State state;
    private WebSocket webSocket;
    private LinkListener linkListener;
    private int sequenceNumber;
    private final HashMap<Integer, CallbackRecord> callbacks = new HashMap<>();
    private String srcId =  UUID.randomUUID().toString();
    private Runnable onDisconnected;

    private enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING
    }

    public ReferenceLink(LinkProfile profile, LinkListener listener) {
        websocketUrl = profile.getApp2AppUrl();
        sslConfig = profile.getSSLConfig();
        linkListener = listener;
    }

    @Override
    public void connect(Runnable onSuccess, Consumer<Throwable> onFailure) {
        state = State.CONNECTING;

        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS);
        if(sslConfig != null) {
            builder.sslSocketFactory(sslConfig.getSocketFactory(), sslConfig.getTrustManager());
            builder.hostnameVerifier(sslConfig.getHostnameVerifier());
        }
        builder.pingInterval(PING_INTERVAL, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request;

        request = new Request.Builder().url(websocketUrl).build();
        InternalWebSocketListener webSocketListener = new InternalWebSocketListener(onSuccess, onFailure, t -> linkListener.onFailure(t));
        webSocket = client.newWebSocket(request, webSocketListener);
    }


    @Override
    public void disconnect(Runnable onSuccess) {
        onDisconnected = onSuccess;
        state = State.DISCONNECTING;
        webSocket.close(1000,"normal closure");
    }

    @Override
    public String getUrl() {
        return websocketUrl;
    }

    @Override
    public boolean sendPayload(String domain, JSONObject message, Consumer<Reply> onSuccess, Consumer<Throwable> onFailure) {
        sequenceNumber++;
        Payload.Builder builder = new Payload.Builder();
        builder.setDst(domain);
        builder.setSrc(srcId);
        builder.setId(sequenceNumber);
        builder.setType(Payload.Type.COMMAND);
        builder.setMessage(message);

        try {
            callbacks.put(sequenceNumber, new CallbackRecord(onSuccess, onFailure));
            String msg = builder.build().encode();
            boolean sent = webSocket.send(msg);
            if(!sent) {
                callbacks.remove(sequenceNumber);
            }
            return sent;
        } catch (JSONException e) {
            callbacks.get(sequenceNumber).failure.accept(e);
        }
        return false;
    }

    private final class InternalWebSocketListener extends okhttp3.WebSocketListener {
        Runnable onConnectSuccess;
        Consumer<Throwable> onConnectFailure;
        Consumer<Throwable> onFailure;

        public InternalWebSocketListener(Runnable onConnectSuccess, Consumer<Throwable> onConnectFailure, Consumer<Throwable> onFailure) {
            this.onConnectSuccess = onConnectSuccess;
            this.onConnectFailure = onConnectFailure;
            this.onFailure = onFailure;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Logger.getLogger(TAG).log(Level.FINE, "onOpen");
            state = State.CONNECTED;
            onConnectSuccess.run();
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Logger.getLogger(TAG).log(Level.FINE, "onClosed");
            if (state == State.DISCONNECTING) {
                state = State.DISCONNECTED;
                if (onDisconnected != null) {
                    onDisconnected.run();
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Logger.getLogger(TAG).log(Level.FINEST,"received command response: {0}", text);
            try {
                Payload payload = Payload.decode(text);
                if(payload.getType() == Payload.Type.EVENT) {
                   handleEvent(payload);
                } else if(payload.getType() == Payload.Type.REPLY) {
                    handleReply(payload);
                }
            } catch (JSONException e) {
               //add a log ?
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            if (state == State.CONNECTED || state == State.CONNECTING) {
                Logger.getLogger(TAG).log(Level.SEVERE, "failure: ", t);
                linkListener.onFailure(t);
            }
        }

        private void handleReply(Payload payload) {
            CallbackRecord record = callbacks.get(payload.getId());
            if(record == null) {
                return;
            }
            if(payload.getStatus() == Payload.Status.OK) {
                record.success.accept(new Reply() {
                    @Override
                    public String getStatus() {
                        return payload.getStatus().name();
                    }

                    @Override
                    public JSONObject getReply() {
                        return payload.getMessage();
                    }
                });
            } else {
                record.failure.accept(new DriverException("error "));
            }
        }

        private void handleEvent(Payload payload) {
            DriverEvent driverEvent = new EventPayload(payload.getSrc(), payload.getMessage());
            if (payload.getDst().equals(Payload.DST_BROADCAST) || payload.getSrc().equals("settings")) {
                linkListener.onEvent(driverEvent);
            }
        }
    }

    private class CallbackRecord {
        public final Consumer<Reply> success;
        public final Consumer<Throwable> failure;

        CallbackRecord(Consumer<Reply> success, Consumer<Throwable> failure) {
            this.success = success;
            this.failure = failure;
        }
    }
}
