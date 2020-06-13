package com.dirtyunicorns.tweaks.bottomnav;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import com.dirtyunicorns.tweaks.R;

public class ViewUtils {

    public static int getThemeAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    public static void updateDrawableColor(Drawable drawable, int color) {
        if (drawable == null)
            return;
            drawable.setTint(color);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}
