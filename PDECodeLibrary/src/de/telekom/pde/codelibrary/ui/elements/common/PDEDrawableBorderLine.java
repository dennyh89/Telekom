/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;


public class PDEDrawableBorderLine extends Drawable{
    protected float mCornerRadius;
    protected float mBorderWidth;
    protected int mBorderColor;
    private int mAlpha = 0xFF;
    private Paint mPaint = null;
    private ColorFilter mColorFilter;
    private Path mShapePath;
    Rect mBoundingRect;
    private int mShapeType;



    public PDEDrawableBorderLine(){
        mCornerRadius = 10.0f;
        mBorderWidth = 2.0f;
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
        RectF frame;
        Float pixelShift;


        Rect normalizedBoundsRect;


        if(mBoundingRect.width()<=0 || mBoundingRect.height()<=0){
            return;
        }

        normalizedBoundsRect = new Rect(0,0,mBoundingRect.right-mBoundingRect.left,
                                        mBoundingRect.bottom-mBoundingRect.top);


        Bitmap b = Bitmap.createBitmap((int)mBoundingRect.width(),(int)mBoundingRect.height(),
                                       Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        mPaint.setAlpha(mAlpha);
        mPaint.setAntiAlias(true);

        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(mBorderColor);
//        mPaint.setColor(Color.BLACK);

        pixelShift = 0.5f;


        frame = new RectF(normalizedBoundsRect.left+pixelShift,normalizedBoundsRect.top+pixelShift,normalizedBoundsRect.right-pixelShift,
                          normalizedBoundsRect.bottom-pixelShift);
//        c.drawRoundRect(frame, mCornerRadius, mCornerRadius, mPaint);

        // ToDo: other shapes than roundedRect need testing
        switch (mShapeType) {
            case PDEAvailableShapes.SHAPE_RECT:
                c.drawRect(frame, mPaint);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                c.drawRoundRect(frame, mCornerRadius, mCornerRadius, mPaint);
                break;
            case PDEAvailableShapes.SHAPE_OVAL:
                c.drawOval(frame, mPaint);
                break;
            case PDEAvailableShapes.SHAPE_CUSTOM_PATH:
                c.drawPath(mShapePath, mPaint);
                break;
        }

//
//        // x-treme silly hack for testing
//        if(mPaint.getColor()>=-3092272){
//            Log.d("BLA","ARC-Time")   ;
//            c.drawArc(new RectF(0.0f,0.0f,2*mCornerRadius,2*mCornerRadius),180.0f,90.0f,false,mPaint);
//            c.drawArc(new RectF(frame.right-2*mCornerRadius,0.0f,frame.right,2*mCornerRadius),270.0f,90.0f,false,
//                      mPaint);
//            c.drawArc(new RectF(frame.right-2*mCornerRadius,frame.bottom-2*mCornerRadius,frame.right,frame.bottom),0.0f,
//                      90.0f,false,mPaint);
//            c.drawArc(new RectF(0.0f,frame.bottom-2*mCornerRadius,2*mCornerRadius,frame.bottom),90.0f,
//                      90.0f,false,mPaint);
//        }




          canvas.drawBitmap(b,mBoundingRect.left,mBoundingRect.top,mPaint);

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

    public void setBorderWidth(float width){
        mBorderWidth = width;
        invalidateSelf();
    }

    public float getBorderWidth(){
        return mBorderWidth;
    }



    public void setBorderColor(int color){
        mBorderColor = color;
        invalidateSelf();
    }

    public int getBorderColor() {
        return mBorderColor;
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



}
