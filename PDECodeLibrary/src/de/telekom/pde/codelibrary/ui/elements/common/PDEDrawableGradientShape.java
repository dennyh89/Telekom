/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;


public class PDEDrawableGradientShape extends Drawable {
    protected float mCornerRadius;
    protected int mColors[];
    private int mAlpha = 0xFF;
    private Paint mPaint = null;
    private ColorFilter mColorFilter;
    private Rect mBoundingRect;
    private int mShapeType;
    private Path mShapePath;

    public PDEDrawableGradientShape(){
        mCornerRadius = 10.0f;
        mColors = new int[]{0,0,0};
        mPaint = new Paint();
        mBoundingRect = new Rect(0,0,0,0);
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        mShapePath = new Path();
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public void draw(android.graphics.Canvas canvas){
       if(mBoundingRect.width()>0 && mBoundingRect.height()>0){
           // RectF boundsRect = new RectF(getBounds());
            RectF boundsRect = new RectF(mBoundingRect);
            // ToDo: check if mAlpha == 0 would work correctly here or if we have to change the alpha channels of the
            // colors
            mPaint.setAlpha(mAlpha);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShader(new LinearGradient( (boundsRect.right-boundsRect.left)/2,boundsRect.top,
                                                 (boundsRect.right-boundsRect.left)/2,boundsRect.bottom,mColors,null,
                                                 Shader.TileMode.MIRROR));
//            canvas.drawRoundRect(boundsRect, mCornerRadius, mCornerRadius, mPaint);
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


    public void setColors(int topColor,int middleColor,int bottomColor){
        mColors = new int[]{topColor,middleColor,bottomColor};
        invalidateSelf();
    }

    public void setTopColor(int color){
        mColors[0]=color;
        invalidateSelf();
    }

    public void setMiddleColor(int color){
        mColors[1]=color;
        invalidateSelf();
    }

    public void setBottomColor(int color){
        mColors[2]=color;
        invalidateSelf();
    }

    public int[] getColors(){
        return mColors;
    }

    public int getTopColor(){
        return mColors[0];
    }

    public int getMiddleColor(){
        return mColors[1];
    }

    public int getBottomColor(){
        return mColors[2];
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
    public void setShapeRect(Rect rect) {
        mBoundingRect = rect;
        mShapeType = PDEAvailableShapes.SHAPE_RECT;
        invalidateSelf();
    }


    /**
     * @brief Set a rectangular path with rounded corners.
     */
    public void setShapeRoundedRect(Rect rect, float cornerRadius) {
        mBoundingRect = rect;
        mCornerRadius = cornerRadius;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        invalidateSelf();
    }

    /**
     * @brief Set a oval inscribing the rect. Use a square to get a circle
     */
    public void setShapeOval(Rect rect) {
        mBoundingRect = rect;
        mShapeType = PDEAvailableShapes.SHAPE_OVAL;
        invalidateSelf();
    }
}



