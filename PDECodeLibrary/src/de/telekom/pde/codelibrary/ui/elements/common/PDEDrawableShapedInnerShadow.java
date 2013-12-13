/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.*;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableShapedInnerShadow
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Graphics primitive - an inner shadow of an element.
 *
 * This class is used for inner shadows of elements. The possible shapes are:
 * rectangle, rounded rectangle, oval or a custom shape by path.
 */
@SuppressWarnings("unused")
public class PDEDrawableShapedInnerShadow extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    protected PDEColor mElementShapeColor;
    protected float mElementBlurRadius;
    protected Path mElementShapePath;
    private int mShapeType;
    protected float mElementCornerRadius;
    protected PointF mElementLightIncidenceOffset;
    // drawing helpers
    private Paint mBackgroundPaint = null;
    private Paint mPaint2 = null;
    private Paint mPaint3 = null;
    // private helpers
    RectF mDrawRect;
    float mOffsetFactor;



//----- init -----------------------------------------------------------------------------------------------------------

    public PDEDrawableShapedInnerShadow(){
        // init drawable basics
        super();
        // init PDE defaults
//        mElementShapeColor =  PDEColor.valueOf(Color.BLACK);
        mElementShapeColor =  PDEColor.valueOf("Black34Alpha");
//        mElementBlurRadius = (float) PDEBuildingUnits.oneFourthBU();
        mElementBlurRadius = (float) PDEBuildingUnits.oneSixthBU();
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        //mElementCornerRadius = 0.0f;
        mElementCornerRadius = PDEBuildingUnits.exactOneThirdBU();
        mBackgroundPaint = new Paint();
        mPaint2 = new Paint();
        mPaint3 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint3.setAntiAlias(true);
        mElementShapePath = new Path();
        mElementLightIncidenceOffset = new PointF(0,0);
        mDrawRect = null;
        mOffsetFactor = 0;

        //init paints for drawing
        update(true);
//        setElementShapeOpacity(0.28f);
        setElementShapeOpacity(1.0f);
        setElementLightIncidenceOffset(new PointF(0.0f, PDEBuildingUnits.oneTwelfthsBU()));
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set shadow opacity.
     *
     * @param opacity new opacity of the element.
     */
    public void setElementShapeOpacity(float opacity){
        int alpha = Math.round(opacity*255);
        setAlpha(alpha);
    }


    /**
     * @brief Get shadow opacity.
     *
     * @return element opacity
     */
    public float getElementShapeOpacity(){
        return mAlpha/255;
    }


    /**
     * @brief Set shape color.
     *
     * @param color new shape color
     */
    public void setElementShapeColor(PDEColor color){
        // any change?
        if (color.getIntegerColor() == mElementShapeColor.getIntegerColor()) {
            return;
        }
        // remember the color
        mElementShapeColor = color;
        // update
        update(true);
    }


    /**
     * @brief Get shape color.
     *
     * @return color of element
     */
    public PDEColor getElementShapeColor(){
        return mElementShapeColor;
    }


    /**
     * @brief Set shadow blur radius.
     *
     * @param radius new blur radius of shadow
     */
    public void setElementBlurRadius(float radius){
        // any change?
        if (mElementBlurRadius == radius) {
            return;
        }
        // remember
        mElementBlurRadius = radius;
        // update
        update();
    }

    /**
     * @brief Get shadow blur radius.
     */
    public float getBlurRadius(){
        return mElementBlurRadius;
    }


    /**
     * @brief Set the path to use.
     *
     * Paths are always filled using the count rule (which is an iOS feature and cannot be changed at
     * the moment). So something like shapes with holes etc. cannot be done through this layer.
     */
    public void setElementShapePath(Path path){
        // any change?
        if (mElementShapePath.equals(path)) {
            return;
        }

        // store the path
        mElementShapePath = path;
        mShapeType = PDEAvailableShapes.SHAPE_CUSTOM_PATH;
        // update
        update();
    }


    /**
     * @brief Get custom path.
     */
    public Path getElementShapePath(){
        return mElementShapePath;
    }


    /**
     * @brief Set a rectangular path
     */
    public void setElementShapeRect(){
        mShapeType = PDEAvailableShapes.SHAPE_RECT;
        // update
        update();
    }


    /**
     * @brief Set a rectangular path with rounded corners.
     */
    public void setElementShapeRoundedRect(float cornerRadius){
        mElementCornerRadius = cornerRadius;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        // update
        update();
    }


    /**
     * @brief Set a oval inscribing the rect. Use a square to get a circle
     */
    public void setElementShapeOval(){
        mShapeType = PDEAvailableShapes.SHAPE_OVAL;
        // update
        update();
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- Drawable overrides ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief draws the inner shadow
     */
    @Override
    public void draw(android.graphics.Canvas canvas){
        Rect bounds = getBounds();

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawRect == null || mDrawingBitmap == null) {
            return;
        }

        // draw the resulting bitmap on our canvas
        canvas.drawBitmap(mDrawingBitmap, mDrawRect.left+ mElementLightIncidenceOffset.x,
                          mDrawRect.top+ mElementLightIncidenceOffset.y, new Paint());
    }


    /**
     * @brief Helper that draws the configured shape into the given canvas.
     *
     * @param canvas canvas to draw shape into
     * @param rect layout rect
     * @param radius radius for rounded corners (for rounded rect only)
     * @param paint paint settings
     */
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
                canvas.drawPath(mElementShapePath,paint);
                break;
        }
    }


    /**
     * @brief Sets offset of the inner rectangle that determines the thickness of shadow borders.
     *
     * To indicate the direction of the light we must be able to tweak the thickness of the shadow for the
     * different element sides. If we think of a light that comes from top-left, the top and the left side cast a
     * larger shadow than the bottom and the right side. So we have to shift this offset in the bottom-right direction.
     *
     * @param offset offset of the inner rectangle
     */
    public void setElementLightIncidenceOffset(PointF offset){
        // anything to do?
        if(offset.x == mElementLightIncidenceOffset.x && offset.y == mElementLightIncidenceOffset.y){
            return;
        }
        // remember
        mElementLightIncidenceOffset = offset;
        // update
        createDrawingBitmap();
        update();
    }


    /**
     * @brief Get light incidence offset
     * @return light incidence offset
     */
    public PointF getElementLightIncidenceOffset(){
        return mElementLightIncidenceOffset;
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update background and border paint values
     */
    @Override
    protected void updateAllPaints() {
        createBackgroundPaint();
    }


    /**
     * @brief create background paint for drawing
     */
    private void createBackgroundPaint() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColorFilter(mColorFilter);
        mBackgroundPaint.setDither(mDither);
        mBackgroundPaint.setColor(mElementShapeColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- Drawing Bitmap ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Creates the bitmap in which we draw our element.
     *
     * We draw our element in a bitmap first before we draw this bitmap on the canvas.
     * The reason for this detour is that we can avoid annoying graphic acceleration bugs in this way.
     * If the size of the element changes, we have to recreate the bitmap by calling this function.
     */
    @Override
    protected void createDrawingBitmap(){
        Rect bounds = getBounds();

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0) {
            return;
        }
        // use bitmap to avoid gfx-acceleration bug
        if (mDrawingBitmap != null) {
            mDrawingBitmap.recycle();
        }

        // determine offset factor
        if (Math.abs(mElementLightIncidenceOffset.x)>Math.abs(mElementLightIncidenceOffset.y)){
            mOffsetFactor = Math.abs(mElementLightIncidenceOffset.x);
        } else {
            mOffsetFactor = Math.abs(mElementLightIncidenceOffset.y);
        }
        // drawing rect
        mDrawRect = new RectF(bounds.left- mOffsetFactor,bounds.top- mOffsetFactor,
                              bounds.right+ mOffsetFactor,bounds.bottom+ mOffsetFactor);

        mDrawingBitmap = Bitmap.createBitmap((int)mDrawRect.width(), (int)mDrawRect.height(), Bitmap.Config.ARGB_8888);
    }


    /**
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    @Override
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        RectF normalizedBoundingRect;
        RectF outlineRect;
        RectF shadowClipRect;
        Path clipPath = new Path();

        //mBackgroundPaint.reset();
        mPaint2.reset();
        mPaint3.reset();
        //mBackgroundPaint.setAntiAlias(true);
        mPaint2.setAntiAlias(true);
        mPaint3.setAntiAlias(true);

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mElementBlurRadius + mOffsetFactor <=0 ||
            mDrawingBitmap == null) {
            return;
        }

        // normalized version of drawing rect
        normalizedBoundingRect = new RectF(0,0, mDrawRect.right- mDrawRect.left,
                                           mDrawRect.bottom- mDrawRect.top);
        // rect of the outline
        outlineRect = new RectF(normalizedBoundingRect.left-1.0f,
                                normalizedBoundingRect.top-1.0f,
                                normalizedBoundingRect.right+1.0f,
                                normalizedBoundingRect.bottom+1.0f);
        // outline
        //mBackgroundPaint.setStyle(Paint.Style.FILL);
        //if (mColorFilter != null) mBackgroundPaint.setColorFilter(mColorFilter);
        //mBackgroundPaint.setDither(mDither);
        //mBackgroundPaint.setColor(mElementShapeColor.getIntegerColor());
        drawCurrentShape(c, outlineRect, mElementCornerRadius, mBackgroundPaint);

        // inner rect
        mPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        mPaint2.setColor(Color.TRANSPARENT);
        mPaint2.setMaskFilter(new BlurMaskFilter(mElementBlurRadius + mOffsetFactor, BlurMaskFilter.Blur.INNER));
        drawCurrentShape(c, normalizedBoundingRect, mElementCornerRadius, mPaint2);

        // Mask to cut overlapping stuff
        mPaint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        mPaint3.setColor(Color.TRANSPARENT);
        mPaint3.setStyle(Paint.Style.FILL);
        clipPath.reset();
        clipPath.addRect(outlineRect, Path.Direction.CW);
        shadowClipRect = new RectF(outlineRect.left+ mOffsetFactor - mElementLightIncidenceOffset.x+1.0f,
                                   outlineRect.top+ mOffsetFactor - mElementLightIncidenceOffset.y+1.0f,
                                   outlineRect.right- mOffsetFactor - mElementLightIncidenceOffset.x-1.0f,
                                   outlineRect.bottom- mOffsetFactor - mElementLightIncidenceOffset.y-1.0f);


        switch(mShapeType){
            case PDEAvailableShapes.SHAPE_RECT:
                clipPath.addRect(shadowClipRect,Path.Direction.CW);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                clipPath.addRoundRect(shadowClipRect, mElementCornerRadius +1.0f, mElementCornerRadius +1.0f,Path.Direction.CW);
                break;
            case PDEAvailableShapes.SHAPE_OVAL:
                clipPath.addOval(shadowClipRect,Path.Direction.CW);
                break;
            case PDEAvailableShapes.SHAPE_CUSTOM_PATH:         // ToDo: This option needs further testing
                clipPath.addPath(mElementShapePath);
                break;
        }

        clipPath.setFillType(Path.FillType.EVEN_ODD);
        clipPath.close();
        c.drawPath(clipPath, mPaint3);
    }
}





