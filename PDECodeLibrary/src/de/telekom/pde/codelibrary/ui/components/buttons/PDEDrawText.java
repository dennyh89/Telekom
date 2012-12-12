/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.content.Context;
import android.graphics.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

public class PDEDrawText extends View {

    private final static String LOG_TAG = PDEDrawText.class.getName();
    private final static boolean DEBUG_ELLIPSIZE = false;
    private final static boolean DEBUG_OUTPUT = false;

    private final static String EllipsizeString = "...";

    private int mBackgroundColor;
    private int mTextColor;
    private String mText;
    private String mShownText;
    private float mTextSize;
    private Typeface mTypeface;
    private boolean mEllipsize = false;
    private float mMetricsTops;
    private float mBoundsLeft;

    private float mShadowOffsetX;
    private float mShadowOffsetY;
    private float mShadowAlpha;
    private int mShadowColor;

    private Paint mPaint = new Paint();
    private Paint mShadowPaint = null;
    private Paint mBackgroundPaint = null;


    private int mWidth;
    private int mHeight;

    public PDEDrawText(Context context) {
        super(context);
        init();

    }

    public PDEDrawText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PDEDrawText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);    //To change body of overridden methods use File | Settings | File Templates.

        if (DEBUG_OUTPUT) Log.d(LOG_TAG,"draw");

        if (mBackgroundPaint != null) {
            canvas.drawRect(new Rect(0,0,mWidth,mHeight),mBackgroundPaint);
        }

        // draw the shadow if enabled
        if (mShadowAlpha > 0.0f) {
            internalDraw(canvas, mShadowOffsetX, mShadowOffsetY, mShadowPaint);
        }

        // draw the text
        internalDraw(canvas, 0.0f, 0.0f, mPaint);
    }

    private void internalDraw(Canvas canvas, float offsetX, float offsetY, Paint paint) {

        canvas.drawText(mShownText, -mBoundsLeft + offsetX, -mMetricsTops + offsetY, paint);

    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        // todo
    }

    /**
     * @brief Set the text color.
     * @param color
     */
    public void setTextColor(int color) {
        mTextColor = color;
        mPaint.setColor(mTextColor);
    }

    /**
     * @brief Set the typeface.
     * @param typeface
     */
    public void setTypeface(Typeface typeface){
        // security
        if (typeface == null) return;

        // check for same value
        if (mTypeface == typeface) return;

        if (DEBUG_OUTPUT) Log.d(LOG_TAG,"setTypeface");

        mTypeface = typeface;
        recalculate();
    }

    /**
     *  @brief Set text size in pixel.
     * @param size size of the text in pixel units
     */
    public void setTextSize(float size) {
        if (mTextSize == size) return;

        if (DEBUG_OUTPUT) Log.d(LOG_TAG,"setTextSize("+size+")");

        mTextSize = size;
        recalculate();
    }

    /**
     *
     * @param text
     */
    public void setText(String text){
        if (TextUtils.isEmpty(text)) text = "";
        if (text.equals(mText)) return;

        mText = text;
        recalculate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);    //To change body of overridden methods use File | Settings | File Templates.

        if (DEBUG_OUTPUT) Log.d(LOG_TAG,"onSizeChanged w, h"+w+","+h);

        if (w == mWidth && h == mHeight) return;

        mWidth = w;
        mHeight = h;

        if (mEllipsize) {
            recalculate();
        }
    }

    /**
     * @brief
     * @param ellipsize
     */
    public void setEllipsize (boolean ellipsize) {
        mEllipsize = ellipsize;
    }

    /**
     * @brief Internal initializer.
     */
    private void init() {
        setBackgroundResource(0);

        mTypeface = PDETypeface.sDefaultFont.getTypeface();
        mTextSize = 20;
        mTextColor = getContext().getResources().getColor(R.color.DTBlack);
        mBackgroundColor = getContext().getResources().getColor(R.color.DTTransparentWhite);

        mShadowOffsetX = 0.0f;
        mShadowOffsetY = 0.0f;
        mShadowAlpha = 0.0f;
        mShadowColor = 0;

        recalculate();
    }

    /**
     * @brief Prepare all values and the object for drawing.
     */
    private void recalculate() {
        //mPaint = new Paint();
        Paint.FontMetrics metrics;

        mPaint.setTypeface(mTypeface);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);

        if (mShadowAlpha > 0.0f) {
            if (mShadowPaint == null) {
                mShadowPaint = new Paint();
            }
            mShadowPaint.setTypeface(mTypeface);
            mShadowPaint.setAntiAlias(true);
            mShadowPaint.setColor(mShadowColor);
            mShadowPaint.setTextSize(mTextSize);
            mShadowPaint.setAlpha((int)mShadowAlpha*255);
        } else {
            mShadowPaint = null;
        }

        if (Color.alpha(mBackgroundColor) > 0 ) {
            if (mBackgroundPaint == null) {
                mBackgroundPaint = new Paint();
            }
            mBackgroundPaint.setColor(mBackgroundColor);

        } else {
            mBackgroundPaint = null;
        }

        metrics = mPaint.getFontMetrics();
        mMetricsTops = metrics.top;

        Rect ellipsizeBounds = new Rect();
        if (mEllipsize) {
            if (DEBUG_ELLIPSIZE) Log.d(LOG_TAG, "Ellipsize on");
            mPaint.getTextBounds(mText,0,mText.length(),ellipsizeBounds);

            if (ellipsizeBounds.width() > mWidth) {
                if (DEBUG_ELLIPSIZE) Log.d(LOG_TAG, "Ellipsize needed! width: "+mWidth+" textWidth:"+mPaint.measureText(mText));
                mShownText = TextUtils.substring(mText, 0, mText.length()-1);

                while (ellipsizeBounds.width() > mWidth && mShownText.length()-1 > 0) {
                    mShownText = TextUtils.substring(mShownText, 0, mShownText.length()-1);
                    mPaint.getTextBounds((mShownText+EllipsizeString),0,(mShownText+EllipsizeString).length(),ellipsizeBounds);
                }
                mShownText = mShownText+EllipsizeString;
                if (DEBUG_ELLIPSIZE) Log.d(LOG_TAG, "Ellipsize orig. text: '"+mText+"' shown text: '"+mShownText +"' "+ mPaint.measureText(mShownText) );
            } else {
                //fits inside
                if (DEBUG_ELLIPSIZE) Log.d(LOG_TAG, "Ellipsize not needed");
                mShownText = mText;
            }
        } else {
            if (DEBUG_ELLIPSIZE) Log.d(LOG_TAG, "Ellipsize off");
            mShownText = mText;
        }

        mBoundsLeft = 0;
        if (!TextUtils.isEmpty(mShownText)) {
            Rect bounds = new Rect();
            mPaint.getTextBounds(mShownText, 0, mShownText.length(), bounds);

            mBoundsLeft = bounds.left;
        }

        // mark as dirty
        invalidate();

    }

    /**
     * @brief Configure the text shadow layer.
     * If the alpha value is zero than no shadow layer will be drawn.
     *
     * @param offsetX x-offset of the shadow text
     * @param offsetY y-offset of the shadow text
     * @param color color of the shadow text
     * @param alpha alpha value, which will be set for the color. Ranges from 0.0f to 1.0f.
     */
    public void setShadowLayer(float offsetX, float offsetY, int color, float alpha) {
        if (alpha <= 0.0f) {
            mShadowOffsetX = 0.0f;
            mShadowOffsetY = 0.0f;
            mShadowAlpha = 0.0f;
            mShadowColor = 0;
        } else {
            mShadowOffsetX = offsetX;
            mShadowOffsetY = offsetY;
            mShadowAlpha = alpha;
            mShadowColor = color;
        }
    }

    /**
     * @brief Configure the text shadow layer.
     * If the alpha value of the color is zero than no shadow layer will be drawn.
     *
     * @param offsetX x-offset of the shadow text
     * @param offsetY y-offset of the shadow text
     * @param color color of the shadow text (including alpha)
     */
    public void setShadowLayer(float offsetX, float offsetY, int color) {
        setShadowLayer(offsetX, offsetY, color, Color.alpha(color)/255.0f);
    }


}
