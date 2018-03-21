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

import org.ocast.core.Driver;
import org.ocast.core.DriverException;
import org.ocast.core.DriverEvent;
import org.ocast.core.Link;
import org.ocast.core.LinkProfile;
import org.ocast.core.PrivateSettings;
import org.ocast.core.PublicSettings;
import org.ocast.core.SSLConfig;
import org.ocast.core.VersionInfo;
import org.ocast.core.dial.AdditionalData;
import org.ocast.core.function.Consumer;
import org.ocast.core.function.ThrowingConsumer;
import org.ocast.core.Device;

import org.json.JSONObject;
import org.ocast.core.setting.DeviceSettingController;
import org.ocast.referencedriver.settings.PublicSettingsImpl;

import java.util.EnumMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Defines a driver implementing communications layers with a remote device
 */
public class ReferenceDriver implements Driver, Link.LinkListener {

    public static final String SEARCH_TARGET = "urn:cast-ocast-org:service:cast:1";
    private final Device device;
    protected Map<Module, Link> links = new EnumMap<>(Module.class);
    private final DriverListener listener;
    private BrowserListener browserListener;

    private final SSLConfig sslConfig;

    @Override
    public void onFailure(Throwable t) {
        listener.onFailure(Failure.LOST);
    }

    @Override
    public void onEvent(DriverEvent driverEvent) {
        if("browser".equals(driverEvent.getDomain())) {
            browserListener.onData(driverEvent.getData());
        }
    }

    /**
     * A {@link org.ocast.core.Driver.Factory Driver.Factory} for driver creation
     */
    public static class ReferenceFactory implements Driver.Factory {
        public ReferenceDriver createDriver(Device device, DriverListener listener) {
            return new ReferenceDriver(device, listener);
        }
        @Override
        public Driver createDriver(Device device, DriverListener listener, SSLConfig sslConfig) {
            return new ReferenceDriver(device, listener, sslConfig);
        }
    }

    public ReferenceDriver(Device device, DriverListener listener) {
        this.device = device;
        this.listener = listener;
        this.sslConfig = null;
    }

    public ReferenceDriver(Device device, DriverListener listener, SSLConfig sslConfig) {
        this.device = device;
        this.listener = listener;
        this.sslConfig = sslConfig;
    }

    @Override
    public void connect(Module module, Runnable onSuccess, Consumer<Throwable> onFailure) throws DriverException {
        LinkProfile profile;
        switch(module) {
            case PUBLIC_SETTINGS:
            case PRIVATE_SETTINGS:
                LinkProfile.Builder builder = new LinkProfile.Builder().setApp2AppUrl(
                        String.format("wss://%s:4433/%s",
                                device.getDialURI().getHost(),
                                "/ocast")
                );
                if(sslConfig != null) {
                    builder.setSslConfig(sslConfig);
                }
                profile = builder.build();
                break;
            default:
                throw new DriverException("unsupported module");
        }
        connect(module, profile, onSuccess, onFailure);
    }

    @Override
    public void connect(Module module, AdditionalData additionalData, Runnable onSuccess, Consumer<Throwable> onFailure) throws DriverException {
        LinkProfile profile;
        switch(module) {
            case APPLICATION:
                LinkProfile.Builder builder = new LinkProfile.Builder().setApp2AppUrl(
                        additionalData.getApp2AppUrl()
                );
                if (sslConfig != null) {
                    builder.setSslConfig(sslConfig);
                }
                profile = builder.build();
                break;
            default:
                throw new DriverException(("unsupported module"));
        }
        connect(module, profile, onSuccess, onFailure);
    }

    private void connect(Module module, LinkProfile profile, Runnable onSuccess, Consumer<Throwable> onFailure) {
        final Link refLink = getLink(profile);
        refLink.connect(() -> {
            links.put(module, refLink);
            onSuccess.run();
        }, onFailure);
    }

    @Override
    public void disconnect(Module module, Runnable onSuccess) {
        final Link link = links.get(module);
        if(isLinkRemovable(module)) {
            link.disconnect( () -> {
                links.remove(module);
                onSuccess.run();
            });
        }
    }

    @Override
    public void sendBrowserData(JSONObject data, Consumer<JSONObject> onSuccess, Consumer<Throwable> onFailure) {
        links.get(Module.APPLICATION).sendPayload("browser", data,
                ThrowingConsumer.checked(j -> onSuccess.accept(j.getReply()), onFailure),
                onFailure);
    }

    @Override
    public void registerBrowser(BrowserListener browser) {
        browserListener = browser;
    }

    @Override
    public PublicSettings getPublicSettings() {
        return new PublicSettingsImpl(links.get(Module.PUBLIC_SETTINGS));
    }

    @Override
    public PrivateSettings getPrivateSettings() {
        throw new NotImplementedException();
    }

    private boolean isLinkRemovable(Module module) {
        Map<Module, Link> map = new EnumMap<>(links);
        Link link = map.remove(module);
        return !map.containsValue(link);
    }

    @Override
    public DeviceSettingController getDeviceSettingController(DeviceSettingController.DeviceSettingControllerListener listenner) {
        return new DeviceSettingControllerImpl(listenner);
    }

    private Link getLink(LinkProfile profile) {
        for(Link l: links.values()) {
            if(l.getUrl().equals(profile.getApp2AppUrl())) {
                return l;
            }
        }
        return new ReferenceLink(profile, this);
    }
}
