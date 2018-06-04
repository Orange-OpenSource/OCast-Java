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

package org.ocast.core;

import org.ocast.core.function.Consumer;
import org.ocast.core.setting.APListReply;
import org.ocast.core.setting.BtDeviceList;
import org.ocast.core.setting.DeviceInfo;
import org.ocast.core.setting.PinCode;
import org.ocast.core.setting.VersionInfo;
import org.ocast.core.setting.NetworkInfo;
import org.ocast.core.setting.SendCommandReply;
import org.ocast.core.setting.WifiInfo;
import java.util.List;

/**
 * Interface PrivateSettings is implemented by driver libraries to provide access to private settings configuration
 */
public interface PrivateSettings {
    void setDeviceName(String name, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void scanAPs(int pinCode, Consumer<APListReply> onSuccess, Consumer<Throwable> onFailure);

    void getAPList(Consumer<APListReply> onSuccess, Consumer<Throwable> onFailure);

    void remAP(String ssid, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void setAP(String ssid, String password, String bssid, int security, int pinCode, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void pbWPS(int pinCode, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void getWifiInfo(Consumer<WifiInfo> onSuccess, Consumer<Throwable> onFailure);

    void getNetworkInfo(Consumer<NetworkInfo> onSuccess, Consumer<Throwable> onFailure);

    void getAPPinCode(Consumer<PinCode> onSuccess, Consumer<Throwable> onFailure);

    void getInfo(Consumer<VersionInfo> onSuccess, Consumer<Throwable> onFailure);

    void reboot(Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void getDeviceInfo(Consumer<DeviceInfo> onSuccess, Consumer<Throwable> onFailure);

    void reset(Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void checkFlash(Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void startDiscovery(List<String> profile, int timeout, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void stopDiscovery(Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);

    void getDevices(List<String> profile, Consumer<BtDeviceList> onSuccess, Consumer<Throwable> onFailure);

    void sendCommand(String cmd, String mac, Consumer<SendCommandReply> onSuccess, Consumer<Throwable> onFailure);

    void sendPinCode(String code, Consumer<Integer> onSuccess, Consumer<Throwable> onFailure);
}
