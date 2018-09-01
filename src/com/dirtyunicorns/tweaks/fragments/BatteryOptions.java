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
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import androidx.preference.*;

import android.util.Log;
import android.view.View;
import android.view.WindowManagerGlobal;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.ArrayList;
import java.util.List;

import com.dirtyunicorns.support.preferences.SystemSettingMasterSwitchPreference;
import com.dirtyunicorns.support.colorpicker.ColorPickerPreference;

public class BatteryOptions extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String BATTERY_LIGHT_ENABLED = "battery_light_enabled";
    private static final String PREF_BATT_BAR = "battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";
    private static final String PREF_BATT_BAR_CHARGING_COLOR = "battery_bar_charging_color";
    private static final String PREF_BATT_BAR_LOW_COLOR_WARNING = "battery_bar_battery_low_color_warning";
    private static final String PREF_BATT_BAR_USE_GRADIENT_COLOR = "battery_bar_use_gradient_color";
    private static final String PREF_BATT_BAR_LOW_COLOR = "battery_bar_low_color";
    private static final String PREF_BATT_BAR_HIGH_COLOR = "battery_bar_high_color";

    private ListPreference mBatteryBar;
    private ListPreference mBatteryBarStyle;
    private ListPreference mBatteryBarThickness;
    private SwitchPreference mBatteryBarChargingAnimation;
    private SwitchPreference mBatteryBarUseGradient;
    private ColorPickerPreference mBatteryBarColor;
    private ColorPickerPreference mBatteryBarChargingColor;
    private ColorPickerPreference mBatteryBarBatteryLowColor;
    private ColorPickerPreference mBatteryBarBatteryLowColorWarn;
    private ColorPickerPreference mBatteryBarBatteryHighColor;
    private SystemSettingMasterSwitchPreference mBatteryLightEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_options);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mBatteryLightEnabled = (SystemSettingMasterSwitchPreference) findPreference(BATTERY_LIGHT_ENABLED);
        mBatteryLightEnabled.setOnPreferenceChangeListener(this);
        int batteryLightEnabled = Settings.System.getInt(getContentResolver(),
                BATTERY_LIGHT_ENABLED, 1);
        mBatteryLightEnabled.setChecked(batteryLightEnabled != 0);

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        int batteryBarLocation = Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_LOCATION, 0);
        mBatteryBar.setValue(String.valueOf(batteryBarLocation));
        mBatteryBar.setSummary(mBatteryBar.getEntry());
        mBatteryBar.setOnPreferenceChangeListener(this);

        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        int batteryBarStyle = Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_STYLE, 0);
        mBatteryBarStyle.setValue(String.valueOf(batteryBarStyle));
        mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntry());
        mBatteryBarStyle.setOnPreferenceChangeListener(this);

        mBatteryBarThickness = (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
        int batteryBarThickness = Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_THICKNESS, 1);
        mBatteryBarThickness.setValue(String.valueOf(batteryBarThickness));
        mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntry());
        mBatteryBarThickness.setOnPreferenceChangeListener(this);

        mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
        int barColor = Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_COLOR, 0xff00a3ff);
        String barColorHex = String.format("#%08x", (0xff00a3ff & barColor));
        mBatteryBarColor.setSummary(barColorHex);
        mBatteryBarColor.setNewPreviewColor(barColor);
        mBatteryBarColor.setAlphaSliderEnabled(true);
        mBatteryBarColor.setOnPreferenceChangeListener(this);

        mBatteryBarChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_CHARGING_COLOR);
        int chargeColor = Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_CHARGING_COLOR, 0xff00f00);
        String chargeColorHex = String.format("#%08x", (0xff00ff00 & chargeColor));
        mBatteryBarChargingColor.setSummary(chargeColorHex);
        mBatteryBarChargingColor.setNewPreviewColor(chargeColor);
        mBatteryBarChargingColor.setAlphaSliderEnabled(true);
        mBatteryBarChargingColor.setOnPreferenceChangeListener(this);

        mBatteryBarBatteryLowColorWarn = (ColorPickerPreference) findPreference(PREF_BATT_BAR_LOW_COLOR_WARNING);
        int warnColor = Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_BATTERY_LOW_COLOR_WARNING, 0xffff6600);
        String warnColorHex = String.format("#%08x", (0xffff6600 & warnColor));
        mBatteryBarBatteryLowColorWarn.setSummary(warnColorHex);
        mBatteryBarBatteryLowColorWarn.setNewPreviewColor(warnColor);
        mBatteryBarBatteryLowColorWarn.setAlphaSliderEnabled(true);
        mBatteryBarBatteryLowColorWarn.setOnPreferenceChangeListener(this);

        mBatteryBarBatteryLowColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_LOW_COLOR);
        int lowColor = Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_LOW_COLOR, 0xffff0040);
        String lowColorHex = String.format("#%08x", (0xffff0040 & lowColor));
        mBatteryBarBatteryLowColor.setSummary(lowColorHex);
        mBatteryBarBatteryLowColor.setNewPreviewColor(lowColor);
        mBatteryBarBatteryLowColor.setAlphaSliderEnabled(true);
        mBatteryBarBatteryLowColor.setOnPreferenceChangeListener(this);

        mBatteryBarBatteryHighColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_HIGH_COLOR);
        int highColor = Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_HIGH_COLOR, 0xff99CC00);
        String highColorHex = String.format("#%08x", (0xff99CC00 & highColor));
        mBatteryBarBatteryHighColor.setSummary(highColorHex);
        mBatteryBarBatteryHighColor.setNewPreviewColor(highColor);
        mBatteryBarBatteryHighColor.setAlphaSliderEnabled(true);
        mBatteryBarBatteryHighColor.setOnPreferenceChangeListener(this);

        mBatteryBarUseGradient = (SwitchPreference) findPreference(PREF_BATT_BAR_USE_GRADIENT_COLOR);
        mBatteryBarUseGradient.setChecked(Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_USE_GRADIENT_COLOR, 0) == 1);
        mBatteryBarUseGradient.setOnPreferenceChangeListener(this);

        mBatteryBarChargingAnimation = (SwitchPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(resolver,
                Settings.System.BATTERY_BAR_ANIMATE, 0) == 1);
        mBatteryBarChargingAnimation.setOnPreferenceChangeListener(this);

        updateBatteryBarOptions();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryLightEnabled) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
		BATTERY_LIGHT_ENABLED, value ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.BATTERY_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBarChargingColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.BATTERY_BAR_CHARGING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBarBatteryLowColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.BATTERY_BAR_LOW_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBarBatteryLowColorWarn) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    "battery_bar_battery_low_color_warning", intHex);
            return true;
        } else if (preference == mBatteryBarBatteryHighColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.BATTERY_BAR_BATTERY_LOW_COLOR_WARNING, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryBar) {
            int val = Integer.parseInt((String) newValue);
            int index = mBatteryBar.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.BATTERY_BAR_LOCATION, val);
            mBatteryBar.setSummary(mBatteryBar.getEntries()[index]);
            updateBatteryBarOptions();
            return true;
        } else if (preference == mBatteryBarStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mBatteryBarStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                   Settings.System.BATTERY_BAR_STYLE, val);
            mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarThickness) {
            int val = Integer.parseInt((String) newValue);
            int index = mBatteryBarThickness.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                   Settings.System.BATTERY_BAR_THICKNESS, val);
            mBatteryBarStyle.setSummary(mBatteryBarThickness.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarChargingAnimation) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.BATTERY_BAR_ANIMATE, value ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarUseGradient) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.BATTERY_BAR_USE_GRADIENT_COLOR, value ? 1 : 0);
            return true;
       }
        return false;
    }

    private void updateBatteryBarOptions() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.BATTERY_BAR_LOCATION, 0) == 0) {
            mBatteryBarStyle.setEnabled(false);
            mBatteryBarThickness.setEnabled(false);
            mBatteryBarChargingAnimation.setEnabled(false);
            mBatteryBarColor.setEnabled(false);
            mBatteryBarUseGradient.setEnabled(false);
            mBatteryBarBatteryLowColor.setEnabled(false);
            mBatteryBarBatteryHighColor.setEnabled(false);
            mBatteryBarBatteryLowColorWarn.setEnabled(false);
        } else {
            mBatteryBarStyle.setEnabled(true);
            mBatteryBarThickness.setEnabled(true);
            mBatteryBarChargingAnimation.setEnabled(true);
            mBatteryBarColor.setEnabled(true);
            mBatteryBarUseGradient.setEnabled(true);
            mBatteryBarBatteryLowColor.setEnabled(true);
            mBatteryBarBatteryHighColor.setEnabled(true);
            mBatteryBarBatteryLowColorWarn.setEnabled(true);
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
                    final ArrayList<SearchIndexableResource> result = new ArrayList<>();
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.battery_options;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
        }
    };
}
