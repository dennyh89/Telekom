/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;


public class PDEDrawableShapedInnerShadow extends Drawable {
    PDEColor mShapeColor;
    float mBlurRadius;
    Path mShapePath;
    int mShapeType;
    RectF mBoundingRect;
    float mCornerRadius;
    int mAlpha;
    Paint mPaint = null;
    Paint mPaint2 = null;
    Paint mPaint3 = null;
    PointF mShadowOffset;


    public PDEDrawableShapedInnerShadow(){
        mShapeColor =  PDEColor.valueOf(Color.BLACK);
        mBlurRadius = (float) PDEBuildingUnits.oneFourthBU();
        mAlpha = 255;

        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        mBoundingRect = new RectF(0.0f,0.0f,0.0f,0.0f);
        mCornerRadius = 0.0f;
        mPaint = new Paint();
        mPaint2 = new Paint();
        mPaint3 = new Paint();
        mShapePath = new Path();
        mShadowOffset = new PointF(0,0);
        setShapeOpacity(0.28f);
        setShadowOffset(new PointF(0.0f, PDEBuildingUnits.oneTwelfthsBU()));
    }

    /**
     * @brief Set shadow opacity.
     */
    public void setShapeOpacity (float opacity){
        int alpha = Math.round(opacity*255);
        setAlpha(alpha);
    }

    public float getShapeOpacity(){
        return mAlpha/255;
    }


    /**
     * @brief Set shape color.
     */
    public void setShapeColor (PDEColor color){
        // ToDo: override PDEColor:equals()  (optional)
        // any change?
        if (color.getIntegerColor() == mShapeColor.getIntegerColor()) {
            return;
        }

        // remember the color
        mShapeColor = color;

        invalidateSelf();
    }

    public PDEColor getShapeColor(){
        return mShapeColor;
    }


    /**
     * @brief Set shadow blur radius.
     */
    public void setBlurRadius (float radius){
        // any change?
        if (mBlurRadius == radius) {
            return;
        }
        mBlurRadius = radius;
        invalidateSelf();
    }

    public float getBlurRadius(){
        return mBlurRadius;
    }


    /**
     * @brief Set the path to use.
     *
     * Paths are always filled using the count rule (which is an iOS feature and cannot be changed at
     * the moment). So something like shapes with holes etc. cannot be done through this layer.
     */
    public void setShapePath (Path path){
        // any change?
        if (mShapePath.equals(path)) {
            return;
        }

        // store the path
        mShapePath = path;
        mShapeType = PDEAvailableShapes.SHAPE_CUSTOM_PATH;
        invalidateSelf();
    }

    public Path getShapePath(){
        return mShapePath;
    }

    /**
     * @brief Set a rectangular path
     */
    public void setShapeRect(RectF rect){
        setBoundingRect(rect);
        mShapeType = PDEAvailableShapes.SHAPE_RECT;
        invalidateSelf();
    }

    /**
     * @brief Set a rectangular path with rounded corners.
     */
    public void setShapeRoundedRect(RectF rect, float cornerRadius){
        setBoundingRect(rect);
        mCornerRadius = cornerRadius;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        invalidateSelf();
    }

    /**
     * @brief Set a oval inscribing the rect. Use a square to get a circle
     */
    public void setShapeOval(RectF rect){
        setBoundingRect(rect);
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
    public void draw(android.graphics.Canvas canvas){
//        Bitmap colorShape, alphaMask, shadowImage;
        Bitmap bitmap;
        Canvas c;
        mPaint.reset();
        mPaint2.reset();
        mPaint3.reset();
        mPaint.setAntiAlias(true);
        mPaint2.setAntiAlias(true);
        mPaint3.setAntiAlias(true);
        RectF drawRect;
        RectF normalizedBoundingRect;
        RectF outlineRect;
        RectF shadowClipRect;
        Path clipPath = new Path();

        float offsetFactor;

        // security
        if (mBoundingRect.width() <= 0 || mBoundingRect.height() <= 0 ){
//            || mInnerRect.width() <= 0 || mInnerRect.height() <= 0) {
            return;
        }

        if (Math.abs(mShadowOffset.x)>Math.abs(mShadowOffset.y)){
            offsetFactor = Math.abs(mShadowOffset.x);
        } else {
            offsetFactor = Math.abs(mShadowOffset.y);
        }
        drawRect = new RectF(mBoundingRect.left-offsetFactor,mBoundingRect.top-offsetFactor,
                        mBoundingRect.right+offsetFactor,mBoundingRect.bottom+offsetFactor);


//
//        bitmap = Bitmap.createBitmap((int) mBoundingRect.width(), (int) mBoundingRect.height(),
//                                     Bitmap.Config.ARGB_8888);
        bitmap = Bitmap.createBitmap((int) drawRect.width(), (int) drawRect.height(),
                                     Bitmap.Config.ARGB_8888);

        c = new Canvas(bitmap);

//        normalizedBoundingRect = new RectF(0,0,mBoundingRect.right-mBoundingRect.left,
//                                           mBoundingRect.bottom-mBoundingRect.top);
        normalizedBoundingRect = new RectF(0,0,drawRect.right-drawRect.left,
                                           drawRect.bottom-drawRect.top);

        outlineRect = new RectF(normalizedBoundingRect.left-1.0f,
                               normalizedBoundingRect.top-1.0f,
                               normalizedBoundingRect.right+1.0f,
                               normalizedBoundingRect.bottom+1.0f);
        // outline
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mShapeColor.getIntegerColor());
        drawCurrentShape(c,outlineRect,mCornerRadius,mPaint);

        // inner rect
        mPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        mPaint2.setColor(Color.TRANSPARENT);
        mPaint2.setMaskFilter(new BlurMaskFilter(mBlurRadius+offsetFactor,BlurMaskFilter.Blur.INNER));
        drawCurrentShape(c,normalizedBoundingRect,mCornerRadius,mPaint2);

        // Mask to cut overlapping stuff
        mPaint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        mPaint3.setColor(Color.TRANSPARENT);
        mPaint3.setStyle(Paint.Style.FILL);
        //mPaint3.setStrokeWidth(5.0f+2.0f);
        clipPath.reset();
        clipPath.addRect(outlineRect, Path.Direction.CW);
        shadowClipRect = new RectF(outlineRect.left+offsetFactor-mShadowOffset.x+1.0f,
                             outlineRect.top+offsetFactor-mShadowOffset.y+1.0f,
                             outlineRect.right-offsetFactor-mShadowOffset.x-1.0f,
                             outlineRect.bottom-offsetFactor-mShadowOffset.y-1.0f);


        switch(mShapeType){
            case PDEAvailableShapes.SHAPE_RECT:
                clipPath.addRect(shadowClipRect,Path.Direction.CW);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                clipPath.addRoundRect(shadowClipRect,mCornerRadius+1.0f,mCornerRadius+1.0f,Path.Direction.CW);
                break;
            case PDEAvailableShapes.SHAPE_OVAL:
                clipPath.addOval(shadowClipRect,Path.Direction.CW);
                break;
            case PDEAvailableShapes.SHAPE_CUSTOM_PATH:         // ToDo: This option needs further testing
                clipPath.addPath(mShapePath);
                break;
        }




        clipPath.setFillType(Path.FillType.EVEN_ODD);
        clipPath.close();
        //drawCurrentShape(c,helperRect,mCornerRadius+1.0f,mPaint3);
        c.drawPath(clipPath,mPaint3);



        mPaint.setAlpha(mAlpha);
        // draw the resulting bitmap on our canvas
//        canvas.drawBitmap(bitmap,mBoundingRect.left,mBoundingRect.top,mPaint);
        canvas.drawBitmap(bitmap,drawRect.left+mShadowOffset.x,drawRect.top+mShadowOffset.y,mPaint);

        /*
        // first create a bitmap with the size, shape and the color of the shadow
        colorShape = Bitmap.createBitmap((int)mBoundingRect.width(),(int)mBoundingRect.height(),
                                         Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(colorShape);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mShapeColor.getIntegerColor());
        drawCurrentShape(c);

        // Next create a bitmap that works as an alpha mask. It's the negative black form of the shadow we need.
        // All black pixels of this mask will be transparent later on, so if we combine it with a bitmap in the color
        // of the shadow we'll receive the positive shadow image
        alphaMask = Bitmap.createBitmap((int)mBoundingRect.width(),(int)mBoundingRect.height(),
                                         Bitmap.Config.ARGB_8888);
        c = new Canvas(alphaMask);
        BlurMaskFilter blur = new BlurMaskFilter(mBlurRadius,BlurMaskFilter.Blur.INNER);
        mPaint.setMaskFilter(blur);
        drawCurrentShape(c);
        // reset
        mPaint.setMaskFilter(null);

        // now combine the color-shape bitmap and the alpha-mask bitmap to a new bitmap that carries the positive
        // shadow image
        shadowImage = Bitmap.createBitmap((int)mBoundingRect.width(),(int)mBoundingRect.height(),
                                         Bitmap.Config.ARGB_8888);
        c = new Canvas(shadowImage);

        mPaint.setFilterBitmap(false);
        c.drawBitmap(colorShape,0,0,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        c.drawBitmap(alphaMask,0,0,mPaint);
        mPaint.setXfermode(null);

        mPaint.setAlpha(mAlpha);

        // draw the resulting bitmap on our canvas
        canvas.drawBitmap(shadowImage,0,0,mPaint);*/
    }

    protected void drawCurrentShape(Canvas canvas, RectF rect, float radius, Paint paint){
        switch(mShapeType){
            case PDEAvailableShapes.SHAPE_RECT:
                canvas.drawRect(rect,paint);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                canvas.drawRoundRect(rect,radius,radius,paint);
                break;
            case PDEAvailableShapes.SHAPE_OVAL:
                canvas.drawOval(rect,paint);
                break;
            case PDEAvailableShapes.SHAPE_CUSTOM_PATH:         // ToDo: This option needs further testing
                canvas.drawPath(mShapePath,paint);
                break;
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
        // ToDo: Don't know if we really need this
    }

    private void setBoundingRect(RectF rect){
        if(rect.left == mBoundingRect.left
           && rect.top == mBoundingRect.top
           && rect.right == mBoundingRect.right
           && rect.bottom == mBoundingRect.bottom){
            return;
        }
        mBoundingRect = rect;
        invalidateSelf();
    }

    public void setShadowOffset(PointF offset){
        if(offset.x == mShadowOffset.x && offset.y == mShadowOffset.y){
            return;
        }
        mShadowOffset = offset;
        invalidateSelf();
    }

    public PointF getShadowOffset(){
        return mShadowOffset;
    }
}





