package com.dirtyunicorns.tweaks.bottomnav;

import android.graphics.Typeface;
import com.dirtyunicorns.tweaks.bottomnav.BubbleNavigationChangeListener;

public interface IBubbleNavigation {
    void setNavigationChangeListener(BubbleNavigationChangeListener navigationChangeListener);

    int getCurrentActiveItemPosition();

    void setCurrentActiveItem(int position);
}
