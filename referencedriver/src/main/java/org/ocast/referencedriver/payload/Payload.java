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

package org.ocast.referencedriver.payload;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines a payload that is used by the driver protocol
 */
public class Payload {

    public static final String DST_BROADCAST = "*";
    private static final String KEY_DST = "dst";
    private static final String KEY_SRC = "src";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATUS = "status";

    private final String dst;
    private final String src;
    private final Type type;
    private final int id;
    private final Status status;
    private final JSONObject message;

    /**
     * Defines the payload type
     */
    public enum Type {
        /**
         * indicates an unsolicited event
         */
        EVENT,
        /**
         * indicates a reply to a command
         */
        REPLY,
        /**
         * indicates a command
         */
        COMMAND
    }

    /**
     * Defines the different reply status
     */
    public enum Status {
        OK,
        JSON_FORMAT_ERROR,
        VALUE_FORMAT_ERROR,
        MISSING_MANDATORY_FIELD,
        UNKNOWN, INTERNAL_ERROR
    }

    public static class Builder {
        private String dst;
        private String src;
        private Type type;
        private int id;
        private Status status;
        private JSONObject message;

        public Builder setDst(String dst) {
            this.dst = dst;
            return this;
        }

        public Builder setSrc(String src) {
            this.src = src;
            return this;
        }

        public Builder setType(Type type) {
           this.type = type;
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder setMessage(JSONObject message) {
            this.message = message;
            return this;
        }

        public Payload build() {
            return new Payload(dst, src, type, id, status, message);
        }
    }

    public Payload(String dst, String src, Type type, int id, Status status, JSONObject message) {
        this.dst = dst;
        this.src = src;
        this.type = type;
        this.id = id;
        this.status = status;
        this.message = message;
    }

    public String getDst() {
        return dst;
    }

    public String getSrc() {
        return src;
    }

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public JSONObject getMessage() {
        return message;
    }

    public static Payload decode(String payload) throws JSONException {
        Builder builder = new Builder();
        JSONObject json = new JSONObject(payload);
        builder.setDst(json.getString(KEY_DST));
        builder.setSrc(json.getString(KEY_SRC));
        Type type;
        String s = json.getString(KEY_TYPE);
        if(Type.EVENT.name().equalsIgnoreCase(s)) {
            type = Type.EVENT;
        } else if(Type.REPLY.name().equalsIgnoreCase(s)) {
            type = Type.REPLY;
        } else {
            throw new JSONException("invalid type: " + s);
        }
        builder.setType(type);
        builder.setId(json.getInt(KEY_ID));
        try {
            builder.setStatus(Status.valueOf(json.optString(KEY_STATUS, "").toUpperCase()));
        } catch(IllegalArgumentException e) {
            builder.setStatus(Status.UNKNOWN);
        }
        builder.setMessage(json.getJSONObject(KEY_MESSAGE));
        return builder.build();
    }

    public String encode() throws JSONException {
        JSONObject output = new JSONObject();
        output.put(KEY_DST, dst);
        output.put(KEY_SRC, src);
        output.put(KEY_TYPE, type.name().toLowerCase());
        output.put(KEY_ID, id);
        output.put(KEY_MESSAGE, message);
        return output.toString();
    }
}
