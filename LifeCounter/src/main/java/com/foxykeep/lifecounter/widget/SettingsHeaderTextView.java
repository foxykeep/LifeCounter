package com.foxykeep.lifecounter.widget;

import com.foxykeep.lifecounter.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public final class SettingsHeaderTextView extends TextView {

    private Paint mUnderlinePaint;
    private int mUnderlineThickness;

    public SettingsHeaderTextView(Context context) {
        super(context);
        initialize();
    }

    public SettingsHeaderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SettingsHeaderTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        Resources res = getResources();

        mUnderlinePaint = new Paint();
        mUnderlinePaint.setColor(res.getColor(R.color.settings_header_text_color));

        mUnderlineThickness = res.getDimensionPixelSize(
                R.dimen.settings_header_underline_thickness);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();

        canvas.drawRect(0, height - mUnderlineThickness, getWidth(), height, mUnderlinePaint);
    }
}
