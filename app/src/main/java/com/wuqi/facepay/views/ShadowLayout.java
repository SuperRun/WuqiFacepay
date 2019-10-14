package com.wuqi.facepay.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.wuqi.facepay.R;

public class ShadowLayout extends FrameLayout {
    private int mShadowColor;
    private float mShadowRadius;
    private float mCornerRadius;
    private float mDx;
    private float mDy;

    private boolean mInvalidateShadowOnSizeChanged = true;
    private boolean mForceInvalidateShadow = false;

    public ShadowLayout(Context context) {

        super(context);
        initView(context, null);
        Log.d("ShadowLayout", "context");
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        Log.d("ShadowLayout", "context attrs");
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        Log.d("ShadowLayout", "context attrs defStyleAttr");
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("onSizeChanged", "onSizeChanged");
        if(w > 0 && h > 0 && (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("changed", "changed");
//        if (mForceInvalidateShadow) {
//            Log.d("onLayout", "onLayout");
//            mForceInvalidateShadow = false;
//            setBackgroundCompat(right - left, bottom - top);
//        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
        Log.d("ShadowOnSizeChanged", "ShadowOnSizeChanged");
    }

    public void invalidateShadow() {
        mForceInvalidateShadow = true;
        requestLayout();
        invalidate();
        Log.d("invalidateShadow", "invalidateShadow");
    }

    // 初始化
    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);

        int xPadding = (int) (mShadowRadius + Math.abs(mDx));
        int yPadding = (int) (mShadowRadius + Math.abs(mDy));
        setPadding(xPadding, yPadding, xPadding, yPadding);
        Log.d("initView", "initView");
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
        Bitmap bitmap = createShadowBitmap(w, h, mCornerRadius, mShadowRadius, mDx, mDy, mShadowColor, Color.TRANSPARENT);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
        Log.d("setBackgroundCompat", "setBackgroundCompat");
    }

    // 获取属性值
    private void initAttributes(Context context, AttributeSet attrs) {

        TypedArray attr = getTypedArray(context, attrs, R.styleable.ShadowLayout);

        if (attr == null) {
            return;
        }

        try {
            mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_sl_cornerRadius, getResources().getDimension(R.dimen.default_corner_radius));
            mShadowRadius = attr.getDimension(R.styleable.ShadowLayout_sl_shadowRadius, getResources().getDimension(R.dimen.default_shadow_radius));
            mDx = attr.getDimension(R.styleable.ShadowLayout_sl_dx, 0);
            mDy = attr.getDimension(R.styleable.ShadowLayout_sl_dy, 0);
            mShadowColor = attr.getColor(R.styleable.ShadowLayout_sl_shadowColor, getResources().getColor(R.color.default_shadow_color));
            Log.d("initAttributes", "initAttributes");
            Log.d("Shadow", String.valueOf(mShadowColor));
            Log.d("mDx", String.valueOf((mDx)));
            Log.d("mDy", String.valueOf((mDy)));
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    @SuppressLint("ResourceAsColor")
    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
                                      float dx, float dy, int shadowColor, int fillColor) {
        Log.d("createShadowBitmap", "createShadowBitmap");
        Log.d("BitmapWidth", String.valueOf(shadowWidth));
        Log.d("BitmapHeight", String.valueOf(shadowHeight));
        // 创建位图
        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);

        if (dy > 0) {
            shadowRect.top += dy;
            shadowRect.bottom -= dy;
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }

        if (dx > 0) {
            shadowRect.left += dx;
            shadowRect.right -= dx;
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }


        Paint shadowPaint = new Paint();

        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.WHITE);
        shadowPaint.setStrokeWidth(1);
//        shadowPaint.setAlpha(120);
        if (!isInEditMode()) {
            Log.d("createShadowBitmap", "setShadowLayer");
            Log.d("shadowRadius", String.valueOf(shadowRadius));
            Log.d("shadowColor", String.valueOf(shadowColor));
//            shadowPaint.setShader(new SweepGradient(200, 200, Color.RED, Color.BLUE));
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);

        return output;
    }

}
