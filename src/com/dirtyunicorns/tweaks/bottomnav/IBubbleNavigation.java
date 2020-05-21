package com.dirtyunicorns.tweaks.bottomnav;

import android.graphics.Typeface;
import com.dirtyunicorns.tweaks.bottomnav.BubbleNavigationChangeListener;

@SuppressWarnings("unused")
public interface IBubbleNavigation {
    void setNavigationChangeListener(BubbleNavigationChangeListener navigationChangeListener);

    void setTypeface(Typeface typeface);

    int getCurrentActiveItemPosition();

    void setCurrentActiveItem(int position);
}
