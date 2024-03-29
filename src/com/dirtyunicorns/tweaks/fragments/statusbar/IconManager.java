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

package com.dirtyunicorns.tweaks.fragments.statusbar;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.du.ActionUtils;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

import com.dirtyunicorns.support.preferences.SystemSettingMasterSwitchPreference;
import com.dirtyunicorns.support.preferences.SystemSettingSwitchPreference;

@SearchIndexable
public class IconManager extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String STATUS_BAR_LOGO = "status_bar_logo";
    private static final String STATUSBAR_ICONS_STYLE = "statusbar_icons_style";
    private static final String STATUSBAR_DUAL_ROW = "statusbar_dual_row";

    private SystemSettingMasterSwitchPreference mStatusBarLogo;
    private SystemSettingSwitchPreference mStatusbarIconsStyle;
    private SystemSettingSwitchPreference mStatusbarDualRow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.icon_manager);

        final PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();
        Context mContext = getContext();

        mStatusBarLogo = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_LOGO);
        mStatusBarLogo.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_LOGO, 0) == 1));
        mStatusBarLogo.setOnPreferenceChangeListener(this);

        mStatusbarIconsStyle = (SystemSettingSwitchPreference) findPreference(STATUSBAR_ICONS_STYLE);
        mStatusbarIconsStyle.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_ICONS_STYLE, 0) == 1));
        mStatusbarIconsStyle.setOnPreferenceChangeListener(this);

        mStatusbarDualRow = (SystemSettingSwitchPreference) findPreference(STATUSBAR_DUAL_ROW);
        mStatusbarDualRow.setChecked((Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_DUAL_ROW, 0) == 1));
        mStatusbarDualRow.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO, value ? 1 : 0);
            return true;
       } else if (preference == mStatusbarIconsStyle) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_ICONS_STYLE, value ? 1 : 0);
            ActionUtils.showSystemUiRestartDialog(getContext());
            return true;
       } else if (preference == mStatusbarDualRow) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_DUAL_ROW, value ? 1 : 0);
            ActionUtils.showSystemUiRestartDialog(getContext());
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
                    sir.xmlResId = R.xml.icon_manager;
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
