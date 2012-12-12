/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;


public class PDEDrawableShape extends Drawable {
    protected float mCornerRadius;
    protected int mColor;
    private Paint mPaint = null;
    private ColorFilter mColorFilter;
    private Rect mBoundingRect;
    private int mShapeType;
    private Path mShapePath;


    public PDEDrawableShape(){
        mCornerRadius = 0.0f;
        mColor = 0xFF000000;
        mPaint = new Paint();
        mBoundingRect = new Rect(0,0,0,0);
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        mShapePath = new Path();
    }

    @Override
    public void setAlpha(int alpha) {
        mColor = Color.argb(alpha,Color.red(mColor),Color.green(mColor),Color.blue(mColor));
        invalidateSelf();
    }

    @Override
    public void draw(android.graphics.Canvas canvas){
        if(mBoundingRect.width()>0 && mBoundingRect.height()>0){
            RectF boundsRect = new RectF(mBoundingRect);

            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mColor);
            // ToDo: other shapes than roundedRect need testing
            switch (mShapeType) {
                case PDEAvailableShapes.SHAPE_RECT:
                    canvas.drawRect(boundsRect, mPaint);
                    break;
                case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                    canvas.drawRoundRect(boundsRect, mCornerRadius, mCornerRadius, mPaint);
                    break;
                case PDEAvailableShapes.SHAPE_OVAL:
                    canvas.drawOval(boundsRect, mPaint);
                    break;
                case PDEAvailableShapes.SHAPE_CUSTOM_PATH:
                    canvas.drawPath(mShapePath, mPaint);
                    break;
            }
        }

    }

    /**
     * @brief abstract in Drawable, need to override
     *
     */
    @Override
    public int getOpacity(){
        return PixelFormat.OPAQUE;
    }

    /**
     * @brief abstract in Drawable, need to override
     *
     */
    @Override
    public void setColorFilter(android.graphics.ColorFilter cf){
        mColorFilter = cf;
    }

    public void setCornerRadius(float radius){
        mCornerRadius = radius;
        invalidateSelf();
    }

    public float getCornerRadius(){
        return mCornerRadius;
    }

    public void setBackgroundColor(int color){
        mColor = color;
        invalidateSelf();
    }

    public int getBackgroundColor(){
        return mColor;
    }

    public void setBoundingRect(Rect bounds){
        if(mBoundingRect.width() != bounds.width() || mBoundingRect.height() != bounds.height()){
            mBoundingRect = bounds;
            invalidateSelf();
        }
    }

    public Rect getBoundingRect(){
        return mBoundingRect;
    }


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
        mBoundingRect = new Rect((int)rect.left,(int)rect.top,(int)rect.right,(int)rect.bottom);
        mShapeType = PDEAvailableShapes.SHAPE_RECT;
        invalidateSelf();
    }


    /**
     * @brief Set a rectangular path with rounded corners.
     */
    public void setShapeRoundedRect(RectF rect, float cornerRadius) {
        mBoundingRect = new Rect((int)rect.left,(int)rect.top,(int)rect.right,(int)rect.bottom);
        mCornerRadius = cornerRadius;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        invalidateSelf();
    }

    /**
     * @brief Set a oval inscribing the rect. Use a square to get a circle
     */
    public void setShapeOval(RectF rect) {
        mBoundingRect = new Rect((int)rect.left,(int)rect.top,(int)rect.right,(int)rect.bottom);
        mShapeType = PDEAvailableShapes.SHAPE_OVAL;
        invalidateSelf();
    }

    public void setShapeOpacity (float opacity){
        int alpha = Math.round(opacity*255);
        setAlpha(alpha);
    }
}
