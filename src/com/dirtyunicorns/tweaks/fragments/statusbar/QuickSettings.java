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

import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.ContentObserver;
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

import com.dirtyunicorns.support.preferences.CustomSeekBarPreference;

import java.util.ArrayList;
import java.util.List;

import com.dirtyunicorns.support.preferences.CustomSeekBarPreference;
import com.dirtyunicorns.support.preferences.SystemSettingMasterSwitchPreference;
import com.dirtyunicorns.support.preferences.SystemSettingEditTextPreference;
import com.dirtyunicorns.support.preferences.SystemSettingSwitchPreference;
import com.dirtyunicorns.support.preferences.SecureSettingListPreference;

@SearchIndexable
public class QuickSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {


    private static final String STATUS_BAR_CUSTOM_HEADER = "status_bar_custom_header";
    private static final String FOOTER_TEXT_STRING = "footer_text_string";
    private static final String QS_BLUR = "qs_blur";
    private static final String PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String PREF_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";
    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    private static final String NOTIFICATION_HEADER = "notification_headers";
    private static final String CENTER_NOTIFICATION_HEADERS = "center_notification_headers";
    private static final String HEADER_ICONS_STYLE = "headers_icons_style";

    private static final String BRIGHTNESS_SLIDER_MEMEUI = "brightness_slider_qs_unexpanded";

    private ListPreference mQuickPulldown;
    private ListPreference mTileAnimationStyle;
    private ListPreference mTileAnimationDuration;
    private ListPreference mTileAnimationInterpolator;
    private ListPreference mSmartPulldown;
    private CustomSeekBarPreference mQsRowsPort;
    private CustomSeekBarPreference mQsRowsLand;
    private CustomSeekBarPreference mQsColumnsPort;
    private CustomSeekBarPreference mQsColumnsLand;
    private SystemSettingMasterSwitchPreference mCustomHeader;
    private SystemSettingMasterSwitchPreference mQsBlur;
    private SystemSettingEditTextPreference mFooterString;
    private SystemSettingSwitchPreference mNotificationHeader;
    private SystemSettingSwitchPreference mCenterNotificationHeader;
    private SystemSettingSwitchPreference mHeaderIconsStyle;

    private SystemSettingSwitchPreference mBrightnessMemeUI;
    private SecureSettingListPreference mBrightnessSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.quick_settings);

        final ContentResolver resolver = getActivity().getContentResolver();

        int value = Settings.System.getIntForUser(resolver,
                Settings.System.QS_ROWS_PORTRAIT, 2, UserHandle.USER_CURRENT);
        mQsRowsPort = (CustomSeekBarPreference) findPreference("qs_rows_portrait");
        mQsRowsPort.setValue(value);
        mQsRowsPort.setOnPreferenceChangeListener(this);

        value = Settings.System.getIntForUser(resolver,
                Settings.System.QS_ROWS_LANDSCAPE, 2, UserHandle.USER_CURRENT);
        mQsRowsLand = (CustomSeekBarPreference) findPreference("qs_rows_landscape");
        mQsRowsLand.setValue(value);
        mQsRowsLand.setOnPreferenceChangeListener(this);

        value = Settings.System.getIntForUser(resolver,
                Settings.System.QS_COLUMNS_PORTRAIT, 6, UserHandle.USER_CURRENT);
        mQsColumnsPort = (CustomSeekBarPreference) findPreference("qs_columns_portrait");
        mQsColumnsPort.setValue(value);
        mQsColumnsPort.setOnPreferenceChangeListener(this);

        value = Settings.System.getIntForUser(resolver,
                Settings.System.QS_COLUMNS_LANDSCAPE, 6, UserHandle.USER_CURRENT);
        mQsColumnsLand = (CustomSeekBarPreference) findPreference("qs_columns_landscape");
        mQsColumnsLand.setValue(value);
        mQsColumnsLand.setOnPreferenceChangeListener(this);

        int qpmode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0, UserHandle.USER_CURRENT);
        mQuickPulldown = (ListPreference) findPreference("status_bar_quick_qs_pulldown");
        mQuickPulldown.setValue(String.valueOf(qpmode));
        mQuickPulldown.setSummary(mQuickPulldown.getEntry());
        mQuickPulldown.setOnPreferenceChangeListener(this);

        mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
        mSmartPulldown.setOnPreferenceChangeListener(this);
        int smartPulldown = Settings.System.getInt(resolver,
                Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);

        mCustomHeader = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CUSTOM_HEADER);
        int qsHeader = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER, 0);
        mCustomHeader.setChecked(qsHeader != 0);
        mCustomHeader.setOnPreferenceChangeListener(this);

        mQsBlur = (SystemSettingMasterSwitchPreference) findPreference(QS_BLUR);
        mQsBlur.setChecked((Settings.System.getInt(resolver,
                Settings.System.QS_BLUR, 0) == 1));
        mQsBlur.setOnPreferenceChangeListener(this);

        mTileAnimationStyle = (ListPreference) findPreference(PREF_TILE_ANIM_STYLE);
        int tileAnimationStyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.ANIM_TILE_STYLE, 0, UserHandle.USER_CURRENT);
        mTileAnimationStyle.setValue(String.valueOf(tileAnimationStyle));
        updateTileAnimationStyleSummary(tileAnimationStyle);
        updateAnimTileStyle(tileAnimationStyle);
        mTileAnimationStyle.setOnPreferenceChangeListener(this);

        mTileAnimationDuration = (ListPreference) findPreference(PREF_TILE_ANIM_DURATION);
        int tileAnimationDuration = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.ANIM_TILE_DURATION, 2000, UserHandle.USER_CURRENT);
        mTileAnimationDuration.setValue(String.valueOf(tileAnimationDuration));
        updateTileAnimationDurationSummary(tileAnimationDuration);
        mTileAnimationDuration.setOnPreferenceChangeListener(this);

        mTileAnimationInterpolator = (ListPreference) findPreference(PREF_TILE_ANIM_INTERPOLATOR);
        int tileAnimationInterpolator = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.ANIM_TILE_INTERPOLATOR, 0, UserHandle.USER_CURRENT);
        mTileAnimationInterpolator.setValue(String.valueOf(tileAnimationInterpolator));
        updateTileAnimationInterpolatorSummary(tileAnimationInterpolator);
        mTileAnimationInterpolator.setOnPreferenceChangeListener(this);

        mNotificationHeader = (SystemSettingSwitchPreference) findPreference(NOTIFICATION_HEADER);
        mNotificationHeader.setChecked((Settings.System.getInt(resolver,
                Settings.System.NOTIFICATION_HEADERS, 1) == 1));
        mNotificationHeader.setOnPreferenceChangeListener(this);

        mCenterNotificationHeader = findPreference(CENTER_NOTIFICATION_HEADERS);
        mCenterNotificationHeader.setChecked((Settings.System.getInt(resolver,
                Settings.System.CENTER_NOTIFICATION_HEADERS, 0) == 1));
        mCenterNotificationHeader.setOnPreferenceChangeListener(this);

        mHeaderIconsStyle = findPreference(HEADER_ICONS_STYLE);
        mHeaderIconsStyle.setChecked((Settings.System.getInt(resolver,
                Settings.System.HEADER_ICONS_STYLE, 0) == 1));
        mHeaderIconsStyle.setOnPreferenceChangeListener(this);

        mFooterString = (SystemSettingEditTextPreference) findPreference(FOOTER_TEXT_STRING);
        mFooterString.setOnPreferenceChangeListener(this);
        String footerString = Settings.System.getString(getContentResolver(),
                FOOTER_TEXT_STRING);
        if (footerString != null && footerString != "")
            mFooterString.setText(footerString);
        else {
            mFooterString.setText("#NusantaraProject");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.FOOTER_TEXT_STRING, "#NusantaraProject");
        }

        mBrightnessMemeUI = (SystemSettingSwitchPreference) findPreference(BRIGHTNESS_SLIDER_MEMEUI);
        mBrightnessMemeUI.setChecked((Settings.System.getIntForUser(resolver,
                Settings.System.BRIGHTNESS_SLIDER_QS_UNEXPANDED, 0, UserHandle.USER_CURRENT) != 0));
        mBrightnessMemeUI.setOnPreferenceChangeListener(this);

        mBrightnessSlider = (SecureSettingListPreference) findPreference("qs_show_brightness_slider");
        mBrightnessSlider.setOnPreferenceChangeListener(this);
        updateBrightness();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQsRowsPort) {
            int val = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_ROWS_PORTRAIT, val, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQsRowsLand) {
            int val = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_ROWS_LANDSCAPE, val, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQsColumnsPort) {
            int val = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_COLUMNS_PORTRAIT, val, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQsColumnsLand) {
            int val = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_COLUMNS_LANDSCAPE, val, UserHandle.USER_CURRENT);
            return true;
       } else if (preference == mQuickPulldown) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, value,
                    UserHandle.USER_CURRENT);
            int index = mQuickPulldown.findIndexOfValue((String) newValue);
            mQuickPulldown.setSummary(
                    mQuickPulldown.getEntries()[index]);
            return true;
        } else if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_SMART_PULLDOWN, smartPulldown);
            updateSmartPulldownSummary(smartPulldown);
            return true;
        } else if (preference == mCustomHeader) {
            boolean header = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CUSTOM_HEADER, header ? 1 : 0);
            return true;
        } else if (preference == mFooterString) {
            String value = (String) newValue;
            if (value != "" && value != null)
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.FOOTER_TEXT_STRING, value);
            else {
                mFooterString.setText("#Durex");
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.FOOTER_TEXT_STRING, "#Durex");
            }
            return true;
        } else if (preference == mTileAnimationStyle) {
            int tileAnimationStyle = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.ANIM_TILE_STYLE,
                    tileAnimationStyle, UserHandle.USER_CURRENT);
            updateTileAnimationStyleSummary(tileAnimationStyle);
            updateAnimTileStyle(tileAnimationStyle);
            return true;
        } else if (preference == mTileAnimationDuration) {
            int tileAnimationDuration = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.ANIM_TILE_DURATION,
                    tileAnimationDuration, UserHandle.USER_CURRENT);
            updateTileAnimationDurationSummary(tileAnimationDuration);
            return true;
        } else if (preference == mTileAnimationInterpolator) {
            int tileAnimationInterpolator = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.ANIM_TILE_INTERPOLATOR,
                    tileAnimationInterpolator, UserHandle.USER_CURRENT);
            updateTileAnimationInterpolatorSummary(tileAnimationInterpolator);
            return true;
        } else if (preference == mQsBlur) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.QS_BLUR, value ? 1 : 0);
            return true;
        } else if (preference == mNotificationHeader) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.NOTIFICATION_HEADERS, value ? 1 : 0);
            ActionUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mCenterNotificationHeader) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.CENTER_NOTIFICATION_HEADERS, value ? 1 : 0);
            ActionUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mHeaderIconsStyle) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.HEADER_ICONS_STYLE, value ? 1 : 0);
            ActionUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mBrightnessMemeUI) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.BRIGHTNESS_SLIDER_QS_UNEXPANDED,
            value ? 1 : 0, UserHandle.USER_CURRENT);
            updateBrightness();
            return true;
        } else if (preference == mBrightnessSlider) {
            int brightness = Integer.valueOf((String) newValue);
            return true;
        }
        return false;
    }

    private void updateTileAnimationStyleSummary(int tileAnimationStyle) {
        String prefix = (String) mTileAnimationStyle.getEntries()[mTileAnimationStyle.findIndexOfValue(String
                .valueOf(tileAnimationStyle))];
        mTileAnimationStyle.setSummary(getResources().getString(R.string.qs_set_animation_style, prefix));
    }

    private void updateTileAnimationDurationSummary(int tileAnimationDuration) {
        String prefix = (String) mTileAnimationDuration.getEntries()[mTileAnimationDuration.findIndexOfValue(String
                .valueOf(tileAnimationDuration))];
        mTileAnimationDuration.setSummary(getResources().getString(R.string.qs_set_animation_duration, prefix));
    }

    private void updateTileAnimationInterpolatorSummary(int tileAnimationInterpolator) {
        String prefix = (String) mTileAnimationInterpolator.getEntries()[mTileAnimationInterpolator.findIndexOfValue(String
                .valueOf(tileAnimationInterpolator))];
        mTileAnimationInterpolator.setSummary(getResources().getString(R.string.qs_set_animation_interpolator, prefix));
    }

    private void updateAnimTileStyle(int tileAnimationStyle) {
        if (mTileAnimationDuration != null) {
            if (tileAnimationStyle == 0) {
                mTileAnimationDuration.setEnabled(false);
                mTileAnimationInterpolator.setEnabled(false);
            } else {
                mTileAnimationDuration.setEnabled(true);
                mTileAnimationInterpolator.setEnabled(true);
            }
        }
    }

    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else if (value == 3) {
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_none_summary));
        } else {
            String type = res.getString(value == 1
                    ? R.string.smart_pulldown_dismissable
                    : R.string.smart_pulldown_ongoing);
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
    }


    private void updateBrightness() {
        boolean brightnessUnexpaned = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.BRIGHTNESS_SLIDER_QS_UNEXPANDED, 1, UserHandle.USER_CURRENT) != 1;

        if (!brightnessUnexpaned) {
            mBrightnessSlider.setValue(String.valueOf(0));
            mBrightnessSlider.setEnabled(false);
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
                    sir.xmlResId = R.xml.quick_settings;
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
