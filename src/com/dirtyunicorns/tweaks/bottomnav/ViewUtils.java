package com.dirtyunicorns.tweaks.bottomnav;

import static android.os.UserHandle.USER_SYSTEM;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.TypedValue;
import android.os.RemoteException;

import com.android.internal.util.du.ThemesUtils;
import com.android.internal.util.du.Utils;

import java.util.Objects;

import com.android.settings.R;

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

    public static void handleOverlays(String packagename, Boolean state, IOverlayManager mOverlayManager) {
        try {
            mOverlayManager.setEnabled(packagename, state, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void handleBackgrounds(Boolean state, Context context, int mode, String[] overlays, IOverlayManager mOverlayManager) {
        if (context != null) {
            Objects.requireNonNull(context.getSystemService(UiModeManager.class))
                    .setNightMode(mode);
        }
        for (int i = 0; i < overlays.length; i++) {
            String background = overlays[i];
            try {
                mOverlayManager.setEnabled(background, state, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean threeButtonNavbarEnabled(Context context) {
        boolean defaultToNavigationBar = Utils.deviceSupportNavigationBar(context);
        boolean navigationBar = Settings.System.getInt(context.getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR, defaultToNavigationBar ? 1 : 0) == 1;
        boolean hasNavbar = (context.getResources().getInteger(
                com.android.internal.R.integer.config_navBarInteractionMode) == 0)
                && navigationBar;
        return hasNavbar;
    }
}
