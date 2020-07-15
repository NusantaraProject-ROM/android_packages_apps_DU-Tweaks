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

package com.dirtyunicorns.tweaks.fragments.system;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Intent;
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
import com.android.settingslib.search.SearchIndexable;

import com.android.internal.util.du.Utils;

import java.util.ArrayList;
import java.util.List;

import com.dirtyunicorns.support.colorpicker.ColorPickerPreference;
import com.dirtyunicorns.support.preferences.GlobalSettingMasterSwitchPreference;
import com.dirtyunicorns.support.preferences.SystemSettingMasterSwitchPreference;

@SearchIndexable
public class Notifications extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String LIGHTS_CATEGORY = "notification_lights";
    private static final String HEADS_UP_NOTIFICATIONS_ENABLED = "heads_up_notifications_enabled";
    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";
    private static final String AMBIENT_NOTIFICATION_LIGHT = "ambient_notification_light";

    private GlobalSettingMasterSwitchPreference mHeadsUpEnabled;
    private SystemSettingMasterSwitchPreference mPulseAmbientEnabled;
    private PreferenceCategory mLightsCategory;
    private ListPreference mFlashlightOnCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notifications);
        PreferenceScreen prefScreen = getPreferenceScreen();

        mHeadsUpEnabled = (GlobalSettingMasterSwitchPreference) findPreference(HEADS_UP_NOTIFICATIONS_ENABLED);
        mHeadsUpEnabled.setOnPreferenceChangeListener(this);
        int headsUpEnabled = Settings.Global.getInt(getContentResolver(),
                HEADS_UP_NOTIFICATIONS_ENABLED, 1);
        mHeadsUpEnabled.setChecked(headsUpEnabled != 0);

        mPulseAmbientEnabled = (SystemSettingMasterSwitchPreference) findPreference(AMBIENT_NOTIFICATION_LIGHT);
        mPulseAmbientEnabled.setOnPreferenceChangeListener(this);
        int edgeLightEnabled = Settings.System.getInt(getContentResolver(),
                AMBIENT_NOTIFICATION_LIGHT, 0);
        mPulseAmbientEnabled.setChecked(edgeLightEnabled != 0);

        mLightsCategory = (PreferenceCategory) findPreference(LIGHTS_CATEGORY);
        if (!getResources().getBoolean(com.android.internal.R.bool.config_hasNotificationLed)) {
            prefScreen.removePreference(mLightsCategory);
        }

        mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
        Preference FlashOnCall = findPreference("flashlight_on_call");
        int flashlightValue = Settings.System.getInt(getContentResolver(),
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
        mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
        mFlashlightOnCall.setOnPreferenceChangeListener(this);
        if (!Utils.deviceHasFlashlight(getActivity())) {
            prefScreen.removePreference(FlashOnCall);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mHeadsUpEnabled) {
            boolean value = (Boolean) newValue;
            Settings.Global.putInt(getContentResolver(),
                  HEADS_UP_NOTIFICATIONS_ENABLED, value ? 1 : 0);
            return true;
        } else if (preference == mFlashlightOnCall) {
            int flashlightValue = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(resolver,
                    Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
            mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
            mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
            return true;
        } else if (preference == mPulseAmbientEnabled) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                  AMBIENT_NOTIFICATION_LIGHT, value ? 1 : 0);
            return true;
        }
        return false;
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
                    sir.xmlResId = R.xml.notifications;
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
