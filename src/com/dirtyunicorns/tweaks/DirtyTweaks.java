/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
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

package com.dirtyunicorns.tweaks;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import com.dirtyunicorns.tweaks.fragments.team.TeamActivity;
import com.dirtyunicorns.tweaks.tabs.Lockscreen;
import com.dirtyunicorns.tweaks.tabs.Hardware;
import com.dirtyunicorns.tweaks.tabs.Statusbar;
import com.dirtyunicorns.tweaks.tabs.System;

public class DirtyTweaks extends SettingsPreferenceFragment implements   
       Preference.OnPreferenceChangeListener {

    private MenuItem mMenuItem;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        View view = inflater.inflate(R.layout.dirtytweaks, container, false);

        getActivity().setTitle(R.string.dirtytweaks_title);

        final BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.navigation);
        final ViewPager viewPager = view.findViewById(R.id.viewpager);
        PagerAdapter mPagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
              if (item.getItemId() == navigation.getSelectedItemId()) {
              return false;
              } else {
                switch(item.getItemId()){
                    case R.id.system:
                        viewPager.setCurrentItem(0);
                        break;
                     case R.id.lockscreen:
                        viewPager.setCurrentItem(1);
                        break;
                     case R.id.statusbar:
                        viewPager.setCurrentItem(2);
                        break;
                     case R.id.hardware:
                        viewPager.setCurrentItem(3);
                        break;
                   }
                return true;
               }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(mMenuItem != null) {
                    mMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                mMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setHasOptionsMenu(true);
        navigation.setSelectedItemId(R.id.system);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        return view;
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        PagerAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new System();
            frags[1] = new Lockscreen();
            frags[2] = new Statusbar();
            frags[3] = new Hardware();
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private String[] getTitles() {
        String titleString[];
        titleString = new String[]{
                getString(R.string.bottom_nav_system_title),
                getString(R.string.bottom_nav_lockscreen_title),
                getString(R.string.bottom_nav_statusbar_title),
                getString(R.string.bottom_nav_hardware_title)};

        return titleString;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DIRTYTWEAKS;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.dialog_team_title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(mContext, TeamActivity.class);
                mContext.startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}
