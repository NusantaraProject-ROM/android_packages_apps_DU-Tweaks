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

import static com.dirtyunicorns.tweaks.bottomnav.ViewUtils.handleBackgrounds;
import static com.dirtyunicorns.tweaks.bottomnav.ViewUtils.handleOverlays;
import static com.dirtyunicorns.tweaks.bottomnav.ViewUtils.threeButtonNavbarEnabled;

import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.R;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.SettingsPreferenceFragment;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.util.du.ThemesUtils;
import com.android.internal.util.du.Utils;

import java.util.ArrayList;
import java.util.List;

import com.dirtyunicorns.support.colorpicker.ColorPickerPreference;

@SearchIndexable
public class Themes extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String PREF_THEME_NAVBAR_PICKER = "theme_navbar_picker";
    private static final String PREF_QS_HEADER_STYLE = "qs_header_style";
    private static final String PREF_SWITCH_STYLE = "switch_style";
    private static final String PREF_TILE_STYLE = "qs_tile_style";
    public static final String PREF_THEME_NAVBAR_STYLE = "theme_navbar_style";
    public static final String PREF_ADAPTIVE_ICON_SHAPE = "adapative_icon_shape";
    public static final String PREF_STATUSBAR_ICONS = "statusbar_icons";
    public static final String PREF_THEME_SWITCH = "theme_switch";
    public static final String PREF_FONT_PICKER = "font_picker";

    // Gvisual Mod
    private static final String PREF_SB_HEIGHT = "sb_height_style";
    private static final String PREF_UI_RADIUS = "ui_radius_style";

    private static final String ACCENT_COLOR = "accent_color";
    static final int DEFAULT_ACCENT_COLOR = 0xff1a73e8;

    private Context mContext;
    private IOverlayManager mOverlayManager;
    private UiModeManager mUiModeManager;

    private ListPreference mAdaptiveIconShape;
    private ListPreference mStatusbarIcons;
    private ListPreference mThemeSwitch;
    private ListPreference mQsHeaderStyle;
    private ListPreference mSwitchStyle;
    private ListPreference mQsTileStyle;
    private ListPreference mNavbarPicker;
    private ListPreference mFontPicker;
    private ColorPickerPreference mAccentColor;

    //Gvisual
    private ListPreference mSBHeight;
    private ListPreference mUiRadius;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.themes);

        mContext = getActivity();
        PreferenceScreen prefSet = getPreferenceScreen();

        // Theme services
        mUiModeManager = getContext().getSystemService(UiModeManager.class);
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));

        // Themes
        mThemeSwitch = (ListPreference) findPreference(PREF_THEME_SWITCH);
        if (Utils.isThemeEnabled("com.android.theme.materialocean.system")) {
            mThemeSwitch.setValue("8");
        } else if (Utils.isThemeEnabled("com.android.theme.darkgrey.system")) {
            mThemeSwitch.setValue("7");
        } else if (Utils.isThemeEnabled("com.android.theme.bakedgreen.system")) {
            mThemeSwitch.setValue("6");
        } else if (Utils.isThemeEnabled("com.android.theme.chocox.system")) {
            mThemeSwitch.setValue("5");
        } else if (Utils.isThemeEnabled("com.android.theme.solarizeddark.system")) {
            mThemeSwitch.setValue("4");
        } else if (Utils.isThemeEnabled("com.android.theme.pitchblack.system")) {
            mThemeSwitch.setValue("3");
        } else if (mUiModeManager != null) {
            if (mUiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                mThemeSwitch.setValue("2");
            } else {
                mThemeSwitch.setValue("1");
            }
        }
        mThemeSwitch.setSummary(mThemeSwitch.getEntry());
        mThemeSwitch.setOnPreferenceChangeListener(this);	

        // Statusbar icons
        mStatusbarIcons = (ListPreference) findPreference(PREF_STATUSBAR_ICONS);
        int sbIconsValue = getOverlayPosition(ThemesUtils.STATUSBAR_ICONS);

        if (sbIconsValue != -1) {
            mStatusbarIcons.setValue(String.valueOf(sbIconsValue + 2));
        } else {
            mStatusbarIcons.setValue("1");
        }
        mStatusbarIcons.setSummary(mStatusbarIcons.getEntry());
        mStatusbarIcons.setOnPreferenceChangeListener(this);

        mAdaptiveIconShape = (ListPreference) findPreference(PREF_ADAPTIVE_ICON_SHAPE);
        int iconShapeValue = getOverlayPosition(ThemesUtils.ADAPTIVE_ICON_SHAPE);
        if (iconShapeValue != -1) {
            mAdaptiveIconShape.setValue(String.valueOf(iconShapeValue + 2));
        } else {
            mAdaptiveIconShape.setValue("1");
        }
        mAdaptiveIconShape.setSummary(mAdaptiveIconShape.getEntry());
        mAdaptiveIconShape.setOnPreferenceChangeListener(this);

        mNavbarPicker = (ListPreference) findPreference(PREF_THEME_NAVBAR_PICKER);
        if (threeButtonNavbarEnabled(mContext)) {
            int navbarStyleValues = getOverlayPosition(ThemesUtils.NAVBAR_STYLES);
            if (navbarStyleValues != -1) {
                mNavbarPicker.setValue(String.valueOf(navbarStyleValues + 2));
            } else {
                mNavbarPicker.setValue("1");
            }
            mNavbarPicker.setSummary(mNavbarPicker.getEntry());
            mNavbarPicker.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mNavbarPicker);
        }

        mQsHeaderStyle = (ListPreference) findPreference(PREF_QS_HEADER_STYLE);
        int qsStyleValue = getOverlayPosition(ThemesUtils.QS_HEADER_THEMES);
        if (qsStyleValue != -1) {
            mQsHeaderStyle.setValue(String.valueOf(qsStyleValue + 2));
        } else {
            mQsHeaderStyle.setValue("1");
        }
        mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
        mQsHeaderStyle.setOnPreferenceChangeListener(this);     

        // Statusbar icons
        mSwitchStyle = (ListPreference) findPreference(PREF_SWITCH_STYLE);
        int switchStyleValue = getOverlayPosition(ThemesUtils.SWITCH_STYLE);
        if (switchStyleValue != -1) {
            mSwitchStyle.setValue(String.valueOf(switchStyleValue + 2));
        } else {
            mSwitchStyle.setValue("1");
        }
        mSwitchStyle.setSummary(mSwitchStyle.getEntry());
        mSwitchStyle.setOnPreferenceChangeListener(this);

        mQsTileStyle = (ListPreference) findPreference(PREF_TILE_STYLE);
        int qsTileStyle = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QS_TILE_STYLE, 0);
        int qsTileStyleValue = getOverlayPosition(ThemesUtils.QS_TILE_THEMES);
        if (qsTileStyleValue != 0) {
            mQsTileStyle.setValue(String.valueOf(qsTileStyle));
        }
        mQsTileStyle.setSummary(mQsTileStyle.getEntry());
        mQsTileStyle.setOnPreferenceChangeListener(this);

        mAccentColor = (ColorPickerPreference) findPreference(ACCENT_COLOR);
        int intColor = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.ACCENT_COLOR, DEFAULT_ACCENT_COLOR, UserHandle.USER_CURRENT);
        String hexColor = String.format("#%08x", (0xff1a73e8 & intColor));
        mAccentColor.setNewPreviewColor(intColor);
        mAccentColor.setOnPreferenceChangeListener(this);

        // Font picker
        mFontPicker = (ListPreference) findPreference(PREF_FONT_PICKER);
        int fontPickerValue = getOverlayPosition(ThemesUtils.FONTS);
        if (fontPickerValue != -1) {
            mFontPicker.setValue(String.valueOf(fontPickerValue + 2));
        } else {
            mFontPicker.setValue("1");
        }
        mFontPicker.setSummary(mFontPicker.getEntry());
        mFontPicker.setOnPreferenceChangeListener(this);

        setGvisualMod();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mAccentColor) {
            String hex = ColorPickerPreference.convertToARGB(
                  Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(resolver,
                    Settings.System.ACCENT_COLOR, intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQsTileStyle) {
            String value = (String) newValue;
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.QS_TILE_STYLE, Integer.valueOf(value));
            int valueIndex = mQsTileStyle.findIndexOfValue(value);
            mQsTileStyle.setSummary(mQsTileStyle.getEntries()[valueIndex]);
            String overlayName = getOverlayName(ThemesUtils.QS_TILE_THEMES);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (valueIndex > 0) {
                    handleOverlays(ThemesUtils.QS_TILE_THEMES[valueIndex],
                            true, mOverlayManager);
                }
            return true;
        } else if (preference == mThemeSwitch) {
            String themeSwitch = (String) newValue;
                switch (themeSwitch) {
                    case "1":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.CHOCO_X, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.BAKED_GREEN, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.DARK_GREY, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "2":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.CHOCO_X, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.BAKED_GREEN, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.DARK_GREY, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "3":
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.CHOCO_X, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.BAKED_GREEN, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.DARK_GREY, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "4":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.CHOCO_X, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.BAKED_GREEN, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.DARK_GREY, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "5":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.CHOCO_X, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.BAKED_GREEN, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.DARK_GREY, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "6":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.CHOCO_X, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.BAKED_GREEN, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.DARK_GREY, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "7":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.CHOCO_X, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.BAKED_GREEN, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.DARK_GREY, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "8":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.CHOCO_X, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.BAKED_GREEN, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.DARK_GREY, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
            }
            mThemeSwitch.setSummary(mThemeSwitch.getEntry());
          return true;
        } else if (preference == mStatusbarIcons) {
            String statusbarIcons = (String) newValue;
            String overlayName = getOverlayName(ThemesUtils.STATUSBAR_ICONS);
            int statusbarIconsValue = Integer.parseInt(statusbarIcons);
            mStatusbarIcons.setValue(String.valueOf(statusbarIconsValue));
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (statusbarIconsValue > 1) {
                    handleOverlays(ThemesUtils.STATUSBAR_ICONS[statusbarIconsValue - 2],
                            true, mOverlayManager);
            }
            mStatusbarIcons.setSummary(mStatusbarIcons.getEntry());
            return true;
        } else if (preference == mAdaptiveIconShape) {
            String adapativeIconShape = (String) newValue;
            String overlayName = getOverlayName(ThemesUtils.ADAPTIVE_ICON_SHAPE);
            int adapativeIconShapeValue = Integer.parseInt(adapativeIconShape);
            mAdaptiveIconShape.setValue(String.valueOf(adapativeIconShapeValue));
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (adapativeIconShapeValue > 1) {
                    handleOverlays(ThemesUtils.ADAPTIVE_ICON_SHAPE[adapativeIconShapeValue - 2],
                            true, mOverlayManager);
            }
            mAdaptiveIconShape.setSummary(mAdaptiveIconShape.getEntry());
            return true;
        } else if (preference == mQsHeaderStyle) {
            String qsHeaderStyle = (String) newValue;
            int qsHeaderStyleValue = Integer.parseInt(qsHeaderStyle);
            mQsHeaderStyle.setValue(String.valueOf(qsHeaderStyleValue));
            String overlayName = getOverlayName(ThemesUtils.QS_HEADER_THEMES);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (qsHeaderStyleValue > 1) {
                    handleOverlays(ThemesUtils.QS_HEADER_THEMES[qsHeaderStyleValue -2],
                            true, mOverlayManager);
            }
            mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
            return true;
        } else if (preference == mSwitchStyle) {
            String switchStyle = (String) newValue;
            int switchStyleValue = Integer.parseInt(switchStyle);
            mSwitchStyle.setValue(String.valueOf(switchStyleValue));
            String overlayName = getOverlayName(ThemesUtils.SWITCH_STYLE);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (switchStyleValue > 1) {
                    handleOverlays(ThemesUtils.SWITCH_STYLE[switchStyleValue - 2],
                            true, mOverlayManager);
            }
            mSwitchStyle.setSummary(mSwitchStyle.getEntry());
            return true;
        } else if (preference == mNavbarPicker) {
            String navbarStyle = (String) newValue;
            int navbarStyleValue = Integer.parseInt(navbarStyle);
            mNavbarPicker.setValue(String.valueOf(navbarStyleValue));
            String overlayName = getOverlayName(ThemesUtils.NAVBAR_STYLES);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (navbarStyleValue > 1) {
                    handleOverlays(ThemesUtils.NAVBAR_STYLES[navbarStyleValue - 2],
                            true, mOverlayManager);
            }
            mNavbarPicker.setSummary(mNavbarPicker.getEntry());
            return true;
        } else if (preference == mFontPicker) {
            String font = (String) newValue;
            int fontTypeValue = Integer.parseInt(font);
            mFontPicker.setValue(String.valueOf(fontTypeValue));
            String overlayName = getOverlayName(ThemesUtils.FONTS);
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (fontTypeValue > 1) {
                handleOverlays(ThemesUtils.FONTS[fontTypeValue - 2],
                        true, mOverlayManager);
            }
            mFontPicker.setSummary(mFontPicker.getEntry());
            return true;
        } else if (preference == mSBHeight) {
            String sbHStyle = (String) newValue;
            int sbHStyleValue = Integer.parseInt(sbHStyle);
            mSBHeight.setValue(String.valueOf(sbHStyleValue));
            String overlayName = getOverlayName(ThemesUtils.STATUSBAR_HEIGHT);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (sbHStyleValue > 1) {
                    handleOverlays(ThemesUtils.STATUSBAR_HEIGHT[sbHStyleValue - 2],
                            true, mOverlayManager);
            }
            mSBHeight.setSummary(mSBHeight.getEntry());
            return true;
        } else if (preference == mUiRadius) {
            String uiRStyle = (String) newValue;
            int uiRStyleValue = Integer.parseInt(uiRStyle);
            mUiRadius.setValue(String.valueOf(uiRStyleValue));
            String overlayName = getOverlayName(ThemesUtils.UI_RADIUS);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (uiRStyleValue > 1) {
                    handleOverlays(ThemesUtils.UI_RADIUS[uiRStyleValue - 2],
                            true, mOverlayManager);
            }
            mUiRadius.setSummary(mUiRadius.getEntry());
            return true;
        }
        return false;
    }

    private void setGvisualMod() {

        // Statusbar height
        mSBHeight = (ListPreference) findPreference(PREF_SB_HEIGHT);
        int sbHeightStyleValue = getOverlayPosition(ThemesUtils.STATUSBAR_HEIGHT);
        if (sbHeightStyleValue != -1) {
            mSBHeight.setValue(String.valueOf(sbHeightStyleValue + 2));
        } else {
            mSBHeight.setValue("1");
        }
        mSBHeight.setSummary(mSBHeight.getEntry());
        mSBHeight.setOnPreferenceChangeListener(this);

        // UI Radius
        mUiRadius = (ListPreference) findPreference(PREF_UI_RADIUS);
        int uiRadiusStyleValue = getOverlayPosition(ThemesUtils.UI_RADIUS);
        if (uiRadiusStyleValue != -1) {
            mUiRadius.setValue(String.valueOf(uiRadiusStyleValue + 2));
        } else {
            mUiRadius.setValue("1");
        }
        mUiRadius.setSummary(mUiRadius.getEntry());
        mUiRadius.setOnPreferenceChangeListener(this);
    }

    private int getOverlayPosition(String[] overlays) {
        int position = -1;
        for (int i = 0; i < overlays.length; i++) {
            String overlay = overlays[i];
            if (Utils.isThemeEnabled(overlay)) {
                position = i;
            }
        }
        return position;
    }

    private String getOverlayName(String[] overlays) {
        String overlayName = null;
        for (int i = 0; i < overlays.length; i++) {
            String overlay = overlays[i];
            if (Utils.isThemeEnabled(overlay)) {
                overlayName = overlay;
            }
        }
        return overlayName;
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
                    sir.xmlResId = R.xml.themes;
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
