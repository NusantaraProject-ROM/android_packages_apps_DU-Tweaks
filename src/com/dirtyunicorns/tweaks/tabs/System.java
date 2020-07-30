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

package com.dirtyunicorns.tweaks.tabs;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class System extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {


    private static final String NUSANTARA_PARTS_CATEGORY = "nusantara_parts_category";
    private static final String NOTIFICATIONS_CATEGORY = "notifications_category";
    private static final String MISC_CATEGORY = "miscellaneous_category";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.system);

        Preference NusantaraParts = findPreference(NUSANTARA_PARTS_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_nusantara_parts_available)) {
            getPreferenceScreen().removePreference(NusantaraParts);
        }

        Preference Notifications = findPreference(NOTIFICATIONS_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_notifications)) {
            getPreferenceScreen().removePreference(Notifications);
        }

        Preference MiscOptions = findPreference(MISC_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_misc_options)) {
            getPreferenceScreen().removePreference(MiscOptions);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DIRTYTWEAKS;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return false;
    }
}
