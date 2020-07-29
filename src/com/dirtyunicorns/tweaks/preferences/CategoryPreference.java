/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.dirtyunicorns.tweaks.preferences;

import android.annotation.ColorInt;
import android.content.res.ColorStateList;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;
import java.util.Random;
import android.widget.RelativeLayout;

public class CategoryPreference extends Preference {

    private final View.OnClickListener mClickListener = this::performClick;

    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;
    private int mColorRandom;
    private int mColorRandomAlpha;
    RelativeLayout cardBg;

    public CategoryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Preference);

        mAllowDividerAbove = TypedArrayUtils.getBoolean(a, R.styleable.Preference_allowDividerAbove,
                R.styleable.Preference_allowDividerAbove, false);
        mAllowDividerBelow = TypedArrayUtils.getBoolean(a, R.styleable.Preference_allowDividerBelow,
                R.styleable.Preference_allowDividerBelow, false);
        a.recycle();

        setLayoutResource(R.layout.category_preference);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setOnClickListener(mClickListener);

        final boolean selectable = isSelectable();
        holder.itemView.setFocusable(selectable);
        holder.itemView.setClickable(selectable);
        holder.setDividerAllowedAbove(mAllowDividerAbove);
        holder.setDividerAllowedBelow(mAllowDividerBelow);
        mColorRandom = getRandomColor();
        mColorRandomAlpha = adjustAlpha(mColorRandom, 0.3f);
        cardBg = (RelativeLayout) holder.findViewById(R.id.card_bg);
        cardBg.setBackgroundColor(mColorRandomAlpha);

    }


    public int getRandomColor(){
       Random rnd = new Random();
       return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @ColorInt
    private static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
