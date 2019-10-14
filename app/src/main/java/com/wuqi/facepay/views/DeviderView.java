package com.wuqi.facepay.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class DeviderView extends View {

    private Paint mPaint = new Paint();

    public DeviderView(Context context) {
        super(context);
    }

    public DeviderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(Color.parseColor("#ffdcdcdc"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0,0,getWidth(),1,mPaint);
    }
}
