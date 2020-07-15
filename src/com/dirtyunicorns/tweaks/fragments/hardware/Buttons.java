/*
 * Copyright (C) 2017-2019 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dirtyunicorns.tweaks.fragments.hardware;

import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.*;

import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.util.du.Utils;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import com.dirtyunicorns.support.preferences.CustomSeekBarPreference;

@SearchIndexable
public class Buttons extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String VOLUME_KEY_CURSOR_CONTROL = "volume_key_cursor_control";
    private static final String DOUBLE_TAP_POWER_FLASHLIGHT = "double_tap_power_flashlight";
    private static final String KILL_APP_LONGPRESS_BACK = "kill_app_longpress_back";
    private static final String LONG_PRESS_KILL_DELAY = "long_press_kill_delay";

    private SwitchPreference mKillAppLongPressBack;
    private ListPreference mVolumeKeyCursorControl;
    private ListPreference mDoubleTapPowerFlashlight;
    private CustomSeekBarPreference mLongpressKillDelay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.buttons);
        final PreferenceScreen prefScreen = getPreferenceScreen();

        // volume key cursor control
        mVolumeKeyCursorControl = (ListPreference) findPreference(VOLUME_KEY_CURSOR_CONTROL);
        if (mVolumeKeyCursorControl != null) {
            mVolumeKeyCursorControl.setOnPreferenceChangeListener(this);
            int volumeRockerCursorControl = Settings.System.getInt(getContentResolver(),
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL, 0);
           mVolumeKeyCursorControl.setValue(Integer.toString(volumeRockerCursorControl));
           mVolumeKeyCursorControl.setSummary(mVolumeKeyCursorControl.getEntry());
	}

        mDoubleTapPowerFlashlight =
                (ListPreference) prefScreen.findPreference(DOUBLE_TAP_POWER_FLASHLIGHT);
        if (deviceHasFlashlight()) {
            mDoubleTapPowerFlashlight.setOnPreferenceChangeListener(this);
            mDoubleTapPowerFlashlight.setValue(Integer.toString(Settings.Secure.getInt(getContext()
                    .getContentResolver(), Settings.Secure.TORCH_POWER_BUTTON_GESTURE, 0)));
            mDoubleTapPowerFlashlight.setSummary(mDoubleTapPowerFlashlight.getEntry());
        } else {
            prefScreen.removePreference(mDoubleTapPowerFlashlight);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mVolumeKeyCursorControl) {
            String volumeKeyCursorControl = (String) newValue;
            int volumeKeyCursorControlValue = Integer.parseInt(volumeKeyCursorControl);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL, volumeKeyCursorControlValue);
            int volumeKeyCursorControlIndex = mVolumeKeyCursorControl
                    .findIndexOfValue(volumeKeyCursorControl);
            mVolumeKeyCursorControl
                    .setSummary(mVolumeKeyCursorControl.getEntries()[volumeKeyCursorControlIndex]);
            return true;
        } else if (preference == mDoubleTapPowerFlashlight) {
            int torchPowerButtonValue = Integer.parseInt((String) newValue);
            Settings.Secure.putInt(getContext().getContentResolver(),
                    Settings.Secure.TORCH_POWER_BUTTON_GESTURE, torchPowerButtonValue);
            int index = mDoubleTapPowerFlashlight.findIndexOfValue((String) newValue);
            mDoubleTapPowerFlashlight.setSummary(
                    mDoubleTapPowerFlashlight.getEntries()[index]);
            if (torchPowerButtonValue == 1) {
                // if doubletap for torch is enabled, switch off double tap for camera
                Settings.Secure.putInt(getContext().getContentResolver(),
                        Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
                        1/*camera gesture is disabled when 1*/);
            }
            return true;
        }
        return false;
    }

    private boolean deviceHasFlashlight() {
        return Utils.deviceHasFlashlight(getContext());
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DIRTYTWEAKS;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.buttons;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
        }
    };
}
