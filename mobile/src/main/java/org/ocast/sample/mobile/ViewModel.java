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

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.databinding.ObservableField;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;

import org.ocast.core.media.Track;

import java.util.List;

@InverseBindingMethods({
        @InverseBindingMethod(type = AppCompatSpinner.class,
                attribute = "selectedItemPosition")
})
public class ViewModel extends BaseObservable {
    private final ViewModelListener listener;
    boolean mIsConnected = false;
    boolean mPaused = false;
    public ObservableField<String> webAppStatus = new ObservableField<>();
    public ObservableField<String> webAppReason = new ObservableField<>();
    public ObservableField<String> playerState = new ObservableField<>();
    public ObservableField<String> mediaTitle = new ObservableField<>();
    public ObservableField<String> mediaType = new ObservableField<>();
    public ObservableField<String> webappname = new ObservableField<>();
    public ObservableField<Double> duration = new ObservableField<>();
    public ObservableField<Double> position = new ObservableField<>();
    private List<Track> audioTracks;
    private List<Track> subtitleTracks;
    private Integer selectedAudioTrackPosition = 0;
    private Integer selectedSubtitleTrackPosition = 0;

    public interface ViewModelListener {
        void onAudioTrackChanged(Track track);
        void onSeek(long position);
        void onCustomDataClicked();
        void onSubtitleTrackChanged(Track track);
    }

    public ViewModel(String webAppName, ViewModelListener listener) {
        this.webappname.set(webAppName);
        this.listener = listener;
    }

    @Bindable
    public boolean isConnected() {
        return mIsConnected;
    }

    public void setConnected(boolean connected) {
        mIsConnected = connected;
        notifyPropertyChanged(BR.connected);
    }

    @Bindable
    public boolean isPaused() {
        return mPaused;
    }

    public void setPaused(boolean paused) {
        mPaused = paused;
        notifyPropertyChanged(BR.paused);
    }

    public void setAudioTracks(List<Track> audioTracks) {
        this.audioTracks = audioTracks;
        for(int i=0 ; i<audioTracks.size(); i++) {
            if(audioTracks.get(i).isEnable()) {
                setSelectedAudioTrackPosition(i);
            }
        }
        notifyPropertyChanged(BR.audioTracks);
    }

    public void setSubtitleTracks(List<Track> subtitleTracks) {
        this.subtitleTracks = subtitleTracks;
        boolean subtitleOn = false;
        Track noneTrack = new Track(Track.Type.TEXT,"none", "none", true, "none");
        this.subtitleTracks.add(0, noneTrack);
        for(int i=1 ; i<subtitleTracks.size(); i++) {
            if(subtitleTracks.get(i).isEnable()) {
                setSelectedSubtitleTrackPosition(i);
                subtitleOn = true;
            }
        }
        if(!subtitleOn) {
            setSelectedSubtitleTrackPosition(0);
        }
        notifyPropertyChanged(BR.subtitleTracks);
    }

    @BindingAdapter("selectedItemPositionAttrChanged")
    public void setSelectedItemPositionListener(AppCompatSpinner view, final InverseBindingListener selectedItemPositionChange) {
        if (selectedItemPositionChange == null) {
            view.setOnItemSelectedListener(null);
        } else {
            view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedItemPositionChange.onChange();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @InverseBindingAdapter(attribute = "selectedItemPosition")
    public Integer getSelectedItemPosition(AppCompatSpinner spinner)
    {
        return spinner.getSelectedItemPosition();
    }

    @BindingAdapter("selectedItemPosition")
    public void setSelectedItemPosition(AppCompatSpinner spinner,int position)
    {
        if(spinner.getSelectedItemPosition()!=position)
            spinner.setSelection(position);
    }

    @Bindable
    public Integer getSelectedAudioTrackPosition()
    {
        return selectedAudioTrackPosition;
    }

    public void setSelectedAudioTrackPosition(Integer selectedTrackPosition)
    {
        this.selectedAudioTrackPosition = selectedTrackPosition;
        if(selectedAudioTrackPosition > 0 && selectedAudioTrackPosition < audioTracks.size()) {
             listener.onAudioTrackChanged(audioTracks.get(selectedAudioTrackPosition));
            notifyPropertyChanged(BR.selectedAudioTrackPosition);
        }
    }

    @Bindable
    public Integer getSelectedSubtitleTrackPosition()
    {
        return selectedSubtitleTrackPosition;
    }

    public void setSelectedSubtitleTrackPosition(Integer selectedTrackPosition)
    {
        Track track = null;
        if(selectedTrackPosition == 0) {
            if(this.selectedSubtitleTrackPosition > 0) {
                track = subtitleTracks.get(this.selectedSubtitleTrackPosition);
                track = new Track(track.getType(), track.getLanguage(), track.getLabel(), false, track.getTrackId());
            }
        } else {
            track = subtitleTracks.get(selectedTrackPosition);
            track = new Track(track.getType(), track.getLanguage(), track.getLabel(), true, track.getTrackId());
        }
        if(track != null) {
            listener.onSubtitleTrackChanged(track);
            notifyPropertyChanged(BR.selectedSubtitleTrackPosition);
        }
        this.selectedSubtitleTrackPosition = selectedTrackPosition;
    }

    @Bindable
    public List<Track> getAudioTracks() {
        return audioTracks;
    }

    @Bindable
    public List<Track> getSubtitleTracks() {
        return subtitleTracks;
    }

    public void onValueChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        if(fromUser) {
            listener.onSeek(progresValue);
        }
    }

    public void onCustomDataClicked(View view) {
        listener.onCustomDataClicked();
    }

    public void setWebAppStatus(String status) {
        webAppStatus.set(status);
        webAppReason.set("");
    }

    public void setWebAppStatus(String status, Throwable t) {
        webAppStatus.set(status);
        webAppReason.set(String.format("%s", t != null ? t.toString() : ""));
    }

    public String getWebAppName() {
        return webappname.get();
    }

    public TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!webappname.get().equals(s.toString())) {
                webappname.set(s.toString());
            }
        }
    };
}
