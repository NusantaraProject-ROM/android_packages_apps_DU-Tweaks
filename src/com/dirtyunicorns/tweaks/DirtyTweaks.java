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

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.dirtyunicorns.tweaks.tabs.Lockscreen;
import com.dirtyunicorns.tweaks.tabs.Hardware;
import com.dirtyunicorns.tweaks.tabs.Statusbar;
import com.dirtyunicorns.tweaks.tabs.System;
import com.dirtyunicorns.tweaks.bottomnav.BubbleNavigationConstraintView;
import com.dirtyunicorns.tweaks.bottomnav.BubbleNavigationChangeListener;

public class DirtyTweaks extends SettingsPreferenceFragment {

    Context mContext;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        View view = inflater.inflate(R.layout.dirtytweaks, container, false);

        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.dirtytweaks_title);
        }

        BubbleNavigationConstraintView bubbleNavigationConstraintView =  (BubbleNavigationConstraintView) view.findViewById(R.id.bottom_navigation_view_constraint);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        PagerAdapter mPagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        bubbleNavigationConstraintView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch(view.getId()) {
                    case R.id.system:
                    case R.id.lockscreen:
                    case R.id.statusbar:
                    case R.id.hardware:
                         viewPager.setCurrentItem(position, true);
                    break;
                   }
               }
           });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bubbleNavigationConstraintView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        setHasOptionsMenu(true);
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

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DIRTYTWEAKS;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            return true;
        }
        return false;
    }
}
