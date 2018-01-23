package com.tencent.wstt.gt.proInfo.floatView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tencent.wstt.gt.R;


public class SMLightView extends View {
    private boolean redraw = false;
    private Paint paint = new Paint();

    private float centerX = 0;
    private float centerY = 0;
    private float mRadius = 0;

    public SMLightView(Context context) {
        super(context);
    }

    public SMLightView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SMLightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SMLightView, defStyleAttr, 0);

        centerX = a.getDimensionPixelSize(R.styleable.SMLightView_centerX, 26);
        centerY = a.getDimensionPixelSize(R.styleable.SMLightView_centerY, 17);
        mRadius = a.getDimensionPixelSize(R.styleable.SMLightView_radius, 10);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!redraw)
            paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(centerX, centerY, mRadius, paint);
    }

    public void drawRed() {
        redraw = true;
        paint.setColor(Color.RED);
        postInvalidate();
    }

    public void drawYellow() {
        redraw = true;
        paint.setColor(Color.YELLOW);
        postInvalidate();
    }

    public void drawGreen() {
        redraw = true;
        paint.setColor(Color.GREEN);
        postInvalidate();
    }
}
