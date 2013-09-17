package com.foxykeep.lifecounter.data;

import com.foxykeep.lifecounter.R;
import com.foxykeep.lifecounter.sharedprefs.SharedPrefsConfig;
import com.foxykeep.lifecounter.util.PlatformVersion;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;

import java.util.ArrayList;

public final class Background {

    private static final int TYPE_COLOR = 0;
    private static final int TYPE_GRADIENT = 1;
    private static final int TYPE_DRAWABLE = 2;

    private static final ArrayList<Background> BACKGROUND_LIST = new ArrayList<Background>();

    public final int id;
    public final int type;
    public final int drawableResId;
    public final int colorResId;
    public final int color2ResId;

    private Background(int id, int type, int drawableResId, int colorResId, int color2ResId) {
        this.id = id;
        this.type = type;
        this.drawableResId = drawableResId;
        this.colorResId = colorResId;
        this.color2ResId = color2ResId;
    }

    private static Background newColor(int id, int colorResId) {
        return new Background(id, TYPE_COLOR, 0 /* drawableResId */, colorResId,
                0 /* color2ResId */);
    }

    private static Background newGradient(int id, int colorResId, int color2ResId) {
        return new Background(id, TYPE_GRADIENT, 0 /* drawableResId */, colorResId, color2ResId);
    }

    private static Background newDrawable(int id, int drawableResId) {
        return new Background(id, TYPE_DRAWABLE, drawableResId, 0 /* colorResId */,
                0 /* color2ResId */);
    }

    public static ArrayList<Background> getBackgrounds() {
        if (BACKGROUND_LIST.isEmpty()) {
            BACKGROUND_LIST.add(Background.newGradient(1 /* id */, R.color.background_1_color,
                    R.color.background_1_color2));
            BACKGROUND_LIST.add(Background.newColor(2 /* id */, R.color.background_2_color));
            BACKGROUND_LIST.add(Background.newGradient(3 /* id */, R.color.background_3_color,
                    R.color.background_3_color2));
            BACKGROUND_LIST.add(Background.newGradient(4 /* id */, R.color.background_4_color,
                    R.color.background_4_color2));
            BACKGROUND_LIST.add(Background.newColor(5 /* id */, R.color.background_5_color));
            BACKGROUND_LIST.add(Background.newColor(6 /* id */, R.color.background_6_color));
            BACKGROUND_LIST.add(Background.newColor(7 /* id */, R.color.background_7_color));
            BACKGROUND_LIST.add(Background.newDrawable(8 /* id */, R.drawable.bg_fox));
        }

        return BACKGROUND_LIST;
    }

    public Drawable getDrawable(Context context) {
        Resources res = context.getResources();

        switch (type) {
            case TYPE_COLOR:
                return new ColorDrawable(res.getColor(colorResId));

            case TYPE_GRADIENT:
                int[] colors = { res.getColor(colorResId), res.getColor(color2ResId) };
                return new GradientDrawable(GradientDrawable.Orientation.TR_BL, colors);

            case TYPE_DRAWABLE:
                return res.getDrawable(drawableResId);

            default:
                throw new IllegalArgumentException("Illegal type: " + type);
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void loadSavedBackgroundOnView(View view) {
        Context context = view.getContext();
        Background currentBackground = null;

        int backgroundId = SharedPrefsConfig.getInt(context, SharedPrefsConfig.BACKGROUND_ID, 1);
        ArrayList<Background> backgroundList = Background.getBackgrounds();
        for (int i = 0, size = backgroundList.size(); i < size; i++) {
            Background background = backgroundList.get(i);
            if (background.id == backgroundId) {
                currentBackground = background;
                break;
            }
        }

        if (currentBackground != null) {
            if (PlatformVersion.isAtLeastJellyBean()) {
                view.setBackground(currentBackground.getDrawable(context));
            } else {
                view.setBackgroundDrawable(currentBackground.getDrawable(context));
            }
        }

    }
}
