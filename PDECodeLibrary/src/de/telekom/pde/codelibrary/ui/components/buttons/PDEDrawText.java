/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;

/// @cond INTERNAL_CLASS
@SuppressWarnings("unused")
public class PDEDrawText extends View {

    private final static String LOG_TAG = "PDEDrawText";
    private final static boolean DEBUG_ELLIPSIZE = false;
    private final static boolean DEBUG_OUTPUT = false;
    private final static boolean SHOW_DEBUG_MEASURE_LOGS = false;

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

    private Rect mMeasureTextBounds = new Rect();


    private int mWidth;
    private int mHeight;

    public PDEDrawText(Context context) {
        super(context);
        init(context);

    }


    public PDEDrawText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public PDEDrawText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        if (DEBUG_OUTPUT) Log.d(LOG_TAG, "draw " + mWidth + ", " + mHeight);

        if (mBackgroundPaint != null) {
            canvas.drawRect(new Rect(0, 0, mWidth, mHeight), mBackgroundPaint);
        }

        // draw the shadow if enabled
        if (mShadowAlpha > 0.0f) {
            internalDraw(canvas, mShadowOffsetX, mShadowOffsetY, mShadowPaint);
        }

        // draw the text
        internalDraw(canvas, 0.0f, 0.0f, mPaint);
    }


    private void internalDraw(Canvas canvas, float offsetX, float offsetY, Paint paint) {
        if (mShownText != null) {
            canvas.drawText(mShownText, -mBoundsLeft + offsetX, -mMetricsTops + offsetY, paint);
        }
    }


    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }


    /**
     * @param color new text color
     * @brief Set the text color.
     */
    public void setTextColor(int color) {
        mTextColor = color;
        mPaint.setColor(mTextColor);
    }


    /**
     * @param typeface new typeface
     * @brief Set the typeface.
     */
    public void setTypeface(Typeface typeface) {
        // security
        if (typeface == null) return;

        // check for same value
        if (mTypeface == typeface) return;

        if (DEBUG_OUTPUT) Log.d(LOG_TAG, "setTypeface");

        mTypeface = typeface;
        recalculate();
    }


    /**
     * @param size size of the text in pixel units
     * @brief Set text size in pixel.
     */
    public void setTextSize(float size) {
        if (mTextSize == size) return;

        if (DEBUG_OUTPUT) Log.d(LOG_TAG, "setTextSize(" + size + ")");

        mTextSize = size;
        recalculate();
    }


    /**
     * @brief Get text size in pixel.
     */
    public float getTextSize() {
        return mTextSize;
    }


    /**
     * @param text new text
     * @brief Set text content
     */
    public void setText(String text) {
        if (TextUtils.isEmpty(text)) text = "";
        if (text.equals(mText)) return;

        mText = text;
        recalculate();
    }


    public void setSize(int w, int h) {
        onSizeChanged(w, h, mWidth, mHeight);
    }


    /**
     * @param width     New width.
     * @param height    New height.
     * @param oldWidth  Old width.
     * @param oldHeight Old height.
     * @brief Size changed.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        if (DEBUG_OUTPUT) Log.d(LOG_TAG, "onSizeChanged " + width + "," + height);

        // check for changes
        if (width == mWidth && height == mHeight) return;

        // remember
        mWidth = width;
        mHeight = height;

        if (mEllipsize) {
            recalculate();
        }
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//
//        if (DEBUG_OUTPUT) Log.d(LOG_TAG, "onLayout "+left+", "+ top+", "+ right+", "+ bottom+")");
//
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        float textWidth = 0.0f;
        float textHeight = 0.0f;

        if (SHOW_DEBUG_MEASURE_LOGS) {
            Log.d(LOG_TAG, "onMeasure " + MeasureSpec.toString(widthMeasureSpec) + " x "
                    + MeasureSpec.toString(heightMeasureSpec));
        }

        if (mPaint != null && !TextUtils.isEmpty(mShownText)) {
            mPaint.getTextBounds(mShownText, 0, mShownText.length(), mMeasureTextBounds);
            textHeight = mMeasureTextBounds.height();
            // the width is not always correct in textBounds... so use measureText
            textWidth = mPaint.measureText(mShownText);
        }

        setMeasuredDimension(resolveSize((int) Math.ceil(textWidth), widthMeasureSpec),
                resolveSize((int) Math.ceil(textHeight), heightMeasureSpec));

        if (SHOW_DEBUG_MEASURE_LOGS) {
            Log.d(LOG_TAG, "onMeasure result: " + getMeasuredWidth() + " x " + getMeasuredHeight());
        }
    }

    /**
     * @param ellipsize on or off
     * @brief turn ellipsize on / off
     */
    public void setEllipsize(boolean ellipsize) {
        mEllipsize = ellipsize;
    }


    /**
     * @brief Internal initializer.
     */
    private void init(Context context) {
        setBackgroundResource(0);

        mTypeface = PDETypeface.sDefaultFont.getTypeface();
        mTextSize = 20;
        mTextColor = context.getResources().getColor(R.color.DTBlack);
        mBackgroundColor = context.getResources().getColor(R.color.DTTransparentWhite);

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
            mShadowPaint.setAlpha((int) mShadowAlpha * 255);
        } else {
            mShadowPaint = null;
        }

        if (Color.alpha(mBackgroundColor) > 0) {
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
            mPaint.getTextBounds(mText, 0, mText.length(), ellipsizeBounds);

            if (ellipsizeBounds.width() > mWidth) {
                if (DEBUG_ELLIPSIZE) {
                    Log.d(LOG_TAG, "Ellipsize needed! width: " + mWidth + " textWidth:" + mPaint.measureText(mText));
                }
                mShownText = TextUtils.substring(mText, 0, mText.length() - 1);

                while (ellipsizeBounds.width() > mWidth
                        && mShownText.length() - 1 > 0) {
                    mShownText = TextUtils.substring(mShownText, 0, mShownText.length() - 1);
                    mPaint.getTextBounds((mShownText + EllipsizeString), 0,
                            (mShownText + EllipsizeString).length(), ellipsizeBounds);
                }
                mShownText = mShownText + EllipsizeString;
                if (DEBUG_ELLIPSIZE) {
                    Log.d(LOG_TAG, "Ellipsize orig. text: '" + mText + "' shown text: '" + mShownText + "' " + mPaint.measureText(mShownText));
                }
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
     * @param offsetX x-offset of the shadow text
     * @param offsetY y-offset of the shadow text
     * @param color   color of the shadow text
     * @param alpha   alpha value, which will be set for the color. Ranges from 0.0f to 1.0f.
     * @brief Configure the text shadow layer.
     * If the alpha value is zero than no shadow layer will be drawn.
     */
    public void setShadowLayer(float offsetX, float offsetY, int color, float alpha) {
        if (DEBUG_OUTPUT)
            Log.d(LOG_TAG, "setShadowLayer x:" + offsetX + " y:" + offsetY + " color:" + color + " alpha:" + alpha);
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

        recalculate();
    }


    /**
     * @param offsetX x-offset of the shadow text
     * @param offsetY y-offset of the shadow text
     * @param color   color of the shadow text (including alpha)
     * @brief Configure the text shadow layer.
     * If the alpha value of the color is zero than no shadow layer will be drawn.
     */
    public void setShadowLayer(float offsetX, float offsetY, int color) {
        setShadowLayer(offsetX, offsetY, color, Color.alpha(color) / 255.0f);
    }
}


/// @endcond INTERNAL_CLASS
