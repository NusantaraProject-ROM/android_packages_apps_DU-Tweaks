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

package com.dirtyunicorns.tweaks.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.search.SearchIndexable;

import com.dirtyunicorns.support.colorpicker.ColorPickerPreference;
import com.dirtyunicorns.support.preferences.CustomSeekBarPreference;
import com.dirtyunicorns.support.preferences.SystemSettingSeekBarPreference;
import com.dirtyunicorns.tweaks.preferences.AmbientLightSettingsPreview;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class PulseNotificationLights extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String AMBIENT_LIGHT_COLOR = "ambient_light_color";
    private static final String AMBIENT_LIGHT_CUSTOM_COLOR = "ambient_light_custom_color";
    private static final String AMBIENT_LIGHT_DURATION = "ambient_light_duration";
    private static final String AMBIENT_LIGHT_REPEAT_COUNT = "ambient_light_repeat_count";

    private ListPreference mEdgeLightColorMode;
    private CustomSeekBarPreference mEdgeLightDurationPreference;
    private SystemSettingSeekBarPreference mEdgeLightRepeatCountPreference;
    private ColorPickerPreference mEdgeLightColorPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pulse_notification_lights);

        mEdgeLightColorMode = (ListPreference) findPreference(AMBIENT_LIGHT_COLOR);
        int edgeLightColorMode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.AMBIENT_LIGHT_COLOR, 0, UserHandle.USER_CURRENT);
        mEdgeLightColorMode.setValue(String.valueOf(edgeLightColorMode));
        mEdgeLightColorMode.setSummary(mEdgeLightColorMode.getEntry());
        mEdgeLightColorMode.setOnPreferenceChangeListener(this);

        mEdgeLightColorPreference = (ColorPickerPreference) findPreference(AMBIENT_LIGHT_CUSTOM_COLOR);
        int edgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.AMBIENT_LIGHT_CUSTOM_COLOR, 0xFF3980FF);
        mEdgeLightColorPreference.setNewPreviewColor(edgeLightColor);

        AmbientLightSettingsPreview.setAmbientLightPreviewColor(edgeLightColor);
        String edgeLightColorHex = ColorPickerPreference.convertToARGB(edgeLightColor);
        if (edgeLightColorHex.equals("#3980ff")) {
            mEdgeLightColorPreference.setSummary(R.string.default_string);
        } else {
            mEdgeLightColorPreference.setSummary(edgeLightColorHex);
        }
        mEdgeLightColorPreference.setOnPreferenceChangeListener(this);

        mEdgeLightDurationPreference = (CustomSeekBarPreference) findPreference(AMBIENT_LIGHT_DURATION);
        int lightDuration = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.AMBIENT_LIGHT_DURATION, 2, UserHandle.USER_CURRENT);
        mEdgeLightDurationPreference.setValue(lightDuration);
        mEdgeLightDurationPreference.setOnPreferenceChangeListener(this);

        mEdgeLightRepeatCountPreference = (SystemSettingSeekBarPreference) findPreference(AMBIENT_LIGHT_REPEAT_COUNT);
        int rCount = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.AMBIENT_LIGHT_REPEAT_COUNT, 0, UserHandle.USER_CURRENT);
        mEdgeLightRepeatCountPreference.setValue(rCount);
        mEdgeLightRepeatCountPreference.setOnPreferenceChangeListener(this);

        updateColorPrefs(edgeLightColorMode);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#3980ff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            AmbientLightSettingsPreview.setAmbientLightPreviewColor(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AMBIENT_LIGHT_CUSTOM_COLOR, intHex);
            return true;
        } else if (preference == mEdgeLightDurationPreference) {
            int value = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.AMBIENT_LIGHT_DURATION, value, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mEdgeLightRepeatCountPreference) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AMBIENT_LIGHT_REPEAT_COUNT, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mEdgeLightColorMode) {
            int edgeLightColorMode = Integer.valueOf((String) newValue);
            int index = mEdgeLightColorMode.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.AMBIENT_LIGHT_COLOR, edgeLightColorMode, UserHandle.USER_CURRENT);
            mEdgeLightColorMode.setSummary(mEdgeLightColorMode.getEntries()[index]);
            updateColorPrefs(edgeLightColorMode);
            return true;
        }
        return false;
    }

    private void updateColorPrefs(int edgeLightColorMode) {
        if (mEdgeLightColorPreference != null) {
            if (edgeLightColorMode == 3) {
                getPreferenceScreen().addPreference(mEdgeLightColorPreference);
            } else {
                getPreferenceScreen().removePreference(mEdgeLightColorPreference);
            }
        }
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
                    sir.xmlResId = R.xml.pulse_notification_lights;
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
