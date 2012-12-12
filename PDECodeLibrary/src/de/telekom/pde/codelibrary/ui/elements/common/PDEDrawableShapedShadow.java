/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import de.telekom.pde.codelibrary.ui.color.PDEColor;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;


public class PDEDrawableShapedShadow extends Drawable {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEDrawableShapedShadow.class.getName();

    private PDEColor mShapeColor;
    private float mBlurRadius;
    private float mMaxBlurRadius;
    private float mLastBlurRadius;
    private Path mShapePath;
    private int mShapeType;
    private RectF mBoundingRect;
    private float mCornerRadius;
    private int mAlpha;
    private Paint mPaint = null;

//    protected static final int SHAPE_RECT = 0;
//    protected static final int SHAPE_ROUNDED_RECT = 1;
//    protected static final int SHAPE_OVAL = 2;
//    protected static final int SHAPE_CUSTOM_PATH = 3;



    public PDEDrawableShapedShadow() {
        // take over the default locally (these are the iOS default values for now)
        mShapeColor = PDEColor.valueOf(Color.BLACK);
        mBlurRadius = 3.0f;
        mMaxBlurRadius = mBlurRadius;
        mLastBlurRadius = mBlurRadius;
        mAlpha = 255;

        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        mBoundingRect = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        mCornerRadius = 0.0f;
        mPaint = new Paint();
        mShapePath = new Path();
    }

    /**
     * @brief Set shadow opacity.
     */
    public void setShapeOpacity(float opacity) {
        int alpha = Math.round(opacity * 255);
        setAlpha(alpha);
    }

    public float getShapeOpacity() {
        return mAlpha / 255;
    }

    /**
     * @brief Set shape color.
     */
    public void setShapeColor(PDEColor color) {
        // ToDo: override PDEColor:equals()  (optional)
        // any change?
        if (color.getIntegerColor() == mShapeColor.getIntegerColor()) {
            return;
        }

        // remember the color
        mShapeColor = color;

        invalidateSelf();
    }

    public PDEColor getShapeColor() {
        return mShapeColor;
    }


    /**
     * @brief Set shadow blur radius.
     */
    public void setBlurRadius(float radius) {
        // any change?
        if (mBlurRadius == radius) {
            return;
        }
        // remember old radius
        mLastBlurRadius = mBlurRadius;
        // remember new radius
        mBlurRadius = radius;

        // remember maximum blur radius for dirty rect
        if(mBlurRadius>mMaxBlurRadius){
            mMaxBlurRadius = mBlurRadius;
        }

        invalidateSelf();
    }

    /**
     * @brief Get current blur radius.
     */
    public float getBlurRadius() {
        return mBlurRadius;
    }

    /**
     * @brief Get maximum blur radius.
     *
     * Delivers the maximum blur radius that was set in the lifetime of this object.
     * We need the maximum blur radius for the calculation of a proper dirty rect.
     * It's not the most efficient solution, since the dirty rect will in many cases be
     * bigger than it would have to be, but it prevents drawing errors. Maybe we find a better solution
     * later on.
     */
    public float getMaxBlurRadius() {
        return mMaxBlurRadius;
    }

    /**
     * @brief Compares current and last blur radius and delivers bigger one.
     *
     * The blur radius can grow or shrink. For a quite optimal dirty rect calculation we need
     * to compare the current radius with the last one and use the bigger one of both.
     */
    public float getBiggerBlurRadius() {
        return (mBlurRadius>mLastBlurRadius)? mBlurRadius: mLastBlurRadius;
    }

    /**
     * @brief Set the path to use.
     *
     * Paths are always filled using the count rule (which is an iOS feature and cannot be changed at
     * the moment). So something like shapes with holes etc. cannot be done through this layer.
     */
    public void setShapePath(Path path) {
        // ToDo: Don't know if equals() is meaningful overriden here.
        // any change?
        if (mShapePath.equals(path)) {
            return;
        }

        // store the path
        mShapePath = path;
        mShapeType = PDEAvailableShapes.SHAPE_CUSTOM_PATH;
        invalidateSelf();
    }

    public Path getShapePath() {
        return mShapePath;
    }

    /**
     * @brief Set a rectangular path
     */
    public void setShapeRect(RectF rect) {
        mBoundingRect = rect;
        mShapeType = PDEAvailableShapes.SHAPE_RECT;
        invalidateSelf();
    }


    /**
     * @brief Set a rectangular path with rounded corners.
     */
    public void setShapeRoundedRect(RectF rect, float cornerRadius) {
        mBoundingRect = rect;
        mCornerRadius = cornerRadius;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        invalidateSelf();
    }

    /**
     * @brief Set a oval inscribing the rect. Use a square to get a circle
     */
    public void setShapeOval(RectF rect) {
        mBoundingRect = rect;
        mShapeType = PDEAvailableShapes.SHAPE_OVAL;
        invalidateSelf();
    }


    @Override
    public void setAlpha(int alpha) {
        if (mAlpha == alpha) {
            return;
        }
        mAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public void draw(android.graphics.Canvas canvas) {
        BlurMaskFilter blur;
        RectF drawRect;
        RectF normalizedBoundsRect;

        if(mBoundingRect.width()<=0 || mBoundingRect.height()<=0){
            return;
        }

        Bitmap b = Bitmap.createBitmap((int)mBoundingRect.width()+2*(int)mBlurRadius,(int)mBoundingRect.height()+2*(int)mBlurRadius,
                                       Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        normalizedBoundsRect=new RectF(0.0f,0.0f,mBoundingRect.right-mBoundingRect.left,
                                      mBoundingRect.bottom-mBoundingRect.top);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mShapeColor.getIntegerColor());
//        Log.d(LOG_TAG,"MyShadow Radius: "+mBlurRadius);
        if (mBlurRadius <= 0.0) {
            // blur mask with 0.0 radius blur crashes, so don't set blur filter and make the rest invisible
            mPaint.setAlpha(0);
        } else {
            blur = new BlurMaskFilter(mBlurRadius, BlurMaskFilter.Blur.NORMAL);
            mPaint.setMaskFilter(blur);
            mPaint.setAlpha(mAlpha);
        }

        // ToDo: Newer changes are tested with rounded rect only; test with others
        // ToDo: custom path will most likely not work anymore
        drawRect = new RectF(normalizedBoundsRect.left+mBlurRadius,normalizedBoundsRect.top+mBlurRadius,
                             normalizedBoundsRect.right+mBlurRadius,normalizedBoundsRect.bottom+mBlurRadius);
        switch (mShapeType) {
            case PDEAvailableShapes.SHAPE_RECT:
                c.drawRect(drawRect, mPaint);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                c.drawRoundRect(drawRect, mCornerRadius, mCornerRadius, mPaint);
                break;
            case PDEAvailableShapes.SHAPE_OVAL:
                c.drawOval(drawRect, mPaint);
                break;
            case PDEAvailableShapes.SHAPE_CUSTOM_PATH:
                c.drawPath(mShapePath, mPaint);
                break;
        }
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(b,mBoundingRect.left-mBlurRadius,mBoundingRect.top-mBlurRadius,p);

        // Alternative
//
//            mPaint.reset();
//            mPaint.setAntiAlias(true);
//            mPaint.setStyle(Paint.Style.FILL);
//            mPaint.setColor(mShapeColor.getIntegerColor());
//            if (mBlurRadius <= 0.0) {
//                // blur mask with 0.0 radius blur crashes, so don't set blur filter and make the rest invisible
//                mPaint.setAlpha(0);
//            } else {
//                blur = new BlurMaskFilter(mBlurRadius, BlurMaskFilter.Blur.NORMAL);
//                mPaint.setMaskFilter(blur);
//                mPaint.setAlpha(mAlpha);
//            }
//
//
//            switch (mShapeType) {
//                case SHAPE_RECT:
//                    canvas.drawRect(mBoundingRect, mPaint);
//                    break;
//                case SHAPE_ROUNDED_RECT:
//                    canvas.drawRoundRect(mBoundingRect, mCornerRadius, mCornerRadius, mPaint);
//                    break;
//                case SHAPE_OVAL:
//                    canvas.drawOval(mBoundingRect, mPaint);
//                    break;
//                case SHAPE_CUSTOM_PATH:
//                    canvas.drawPath(mShapePath, mPaint);
//                    break;
//            }
//
    }

    /**
     * @brief abstract in Drawable, need to override
     *
     */
    @Override
    public int getOpacity() {
        // ToDo: does int realy make sense for opacity? Otherwise we could simply forward getShapeOpacity here?!?
        return PixelFormat.OPAQUE;
    }

    /**
     * @brief abstract in Drawable, need to override
     *
     */
    @Override
    public void setColorFilter(android.graphics.ColorFilter cf) {
        // ToDo: Don't know if we really need this
    }
}
