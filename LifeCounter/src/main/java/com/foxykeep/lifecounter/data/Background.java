package com.foxykeep.lifecounter.data;

import com.foxykeep.lifecounter.R;
import com.foxykeep.lifecounter.sharedprefs.SharedPrefsConfig;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.widget.ImageView;

import java.util.ArrayList;

public final class Background {

    private static final int TYPE_COLOR = 0;
    private static final int TYPE_GRADIENT = 1;
    private static final int TYPE_DRAWABLE = 2;

    private static final ArrayList<Background> BACKGROUND_LIST = new ArrayList<Background>();

    public final int id;
    public final int type;
    public final int drawableResId;
    public final int thumbnailDrawableResId;
    public final int colorResId;
    public final int color2ResId;

    private Background(int id, int type, int drawableResId, int thumbnailDrawableResId,
            int colorResId, int color2ResId) {
        this.id = id;
        this.type = type;
        this.drawableResId = drawableResId;
        this.thumbnailDrawableResId = thumbnailDrawableResId;
        this.colorResId = colorResId;
        this.color2ResId = color2ResId;
    }

    private static Background newColor(int id, int colorResId) {
        return new Background(id, TYPE_COLOR, 0 /* drawableResId */,
                0 /* thumbnailDrawableResId */, colorResId, 0 /* color2ResId */);
    }

    private static Background newGradient(int id, int colorResId, int color2ResId) {
        return new Background(id, TYPE_GRADIENT, 0 /* drawableResId */,
                0 /* thumbnailDrawableResId */, colorResId, color2ResId);
    }

    private static Background newDrawable(int id, int drawableResId, int thumbnailDrawableResId) {
        return new Background(id, TYPE_DRAWABLE, drawableResId, thumbnailDrawableResId,
                0 /* colorResId */, 0 /* color2ResId */);
    }

    public static ArrayList<Background> getBackgrounds() {
        if (BACKGROUND_LIST.isEmpty()) {
            BACKGROUND_LIST.add(Background.newColor(1 /* id */, R.color.background_1_color));
            BACKGROUND_LIST.add(Background.newColor(2 /* id */, R.color.background_2_color));
            BACKGROUND_LIST.add(Background.newColor(3 /* id */, R.color.background_3_color));
            BACKGROUND_LIST.add(Background.newColor(4 /* id */, R.color.background_4_color));
            BACKGROUND_LIST.add(Background.newGradient(5 /* id */, R.color.background_5_color,
                    R.color.background_5_color2));
            BACKGROUND_LIST.add(Background.newGradient(6 /* id */, R.color.background_6_color,
                    R.color.background_6_color2));
            BACKGROUND_LIST.add(Background.newGradient(7 /* id */, R.color.background_7_color,
                    R.color.background_7_color2));
            BACKGROUND_LIST.add(Background.newDrawable(8 /* id */, R.drawable.bg_fox,
                    R.drawable.thumb_fox));
            BACKGROUND_LIST.add(Background.newDrawable(9 /* id */, R.drawable.bg_red_hell,
                    R.drawable.thumb_red_hell));
            BACKGROUND_LIST.add(Background.newDrawable(10 /* id */, R.drawable.bg_wood,
                    R.drawable.thumb_wood));
            BACKGROUND_LIST.add(Background.newDrawable(11 /* id */, R.drawable.bg_black_head,
                    R.drawable.thumb_black_head));
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

    public Drawable getThumbnail(Context context) {
        if (type == TYPE_DRAWABLE) {
            Resources res = context.getResources();
            return res.getDrawable(thumbnailDrawableResId);
        } else {
            return getDrawable(context);
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void loadSavedBackgroundOnView(ImageView imageView) {
        Context context = imageView.getContext();
        Background currentBackground = null;

        int backgroundId = SharedPrefsConfig.getInt(context, SharedPrefsConfig.BACKGROUND_ID,
                getDefaultBackgroundId());
        ArrayList<Background> backgroundList = Background.getBackgrounds();
        for (int i = 0, size = backgroundList.size(); i < size; i++) {
            Background background = backgroundList.get(i);
            if (background.id == backgroundId) {
                currentBackground = background;
                break;
            }
        }

        if (currentBackground != null) {
            imageView.setImageDrawable(currentBackground.getDrawable(context));
        }
    }

    private static int getDefaultBackgroundId() {
        return 7; // The blue/black gradient
    }
}
