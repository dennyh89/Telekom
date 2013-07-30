/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableBase;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableShapedShadow
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Graphics primitive - an outer shadow with several possible shapes.
 *
 * This class is used for outer shadows of elements. The possible shapes are:
 * rectangle, rounded rectangle, oval or a custom shape by path.
 */
public class PDEDrawableShapedShadow extends PDEDrawableBase {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawableShapedShadow.class.getName();

//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEColor mElementShapeColor;
    private float mElementBlurRadius;
    private Path mElementShapePath;
    private int mElementShapeType;
    private float mElementCornerRadius;
    private Paint mBackgroundPaint = null;



//----- init -----------------------------------------------------------------------------------------------------------
    public PDEDrawableShapedShadow() {
        // init drawable basics
        super();
        // take over the default locally (these are the iOS default values for now)
        mElementShapeColor = PDEColor.valueOf(Color.BLACK);
        mElementBlurRadius = 3.0f;
        mElementShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        mElementCornerRadius = PDEBuildingUnits.oneThirdBU();//0.0f;
        mBackgroundPaint = new Paint();
        mElementShapePath = new Path();
        update(true);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set shadow opacity.
     */
    public void setElementShapeOpacity(float opacity) {
        int alpha = Math.round(opacity * 255);
        setAlpha(alpha);
    }


    /**
     * @brief Get the shadow opacity of the shape.
     *
     * @return opacity of the shape
     */
    public float getElementShapeOpacity() {
        return mAlpha / 255;
    }


    /**
     * @brief Set shape color.
     */
    public void setElementShapeColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementShapeColor.getIntegerColor()) return;
        // remember the color
        mElementShapeColor = color;
        // update
        update(true);
    }


    /**
     * @brief Get shape color.
     *
     * @return shape color
     */
    public PDEColor getElementShapeColor() {
        return mElementShapeColor;
    }


    /**
     * @brief Set shadow blur radius.
     */
    public void setElementBlurRadius(float radius) {
        // any change?
        if (mElementBlurRadius == radius) return;
        // remember
        mElementBlurRadius = radius;
        // update
        createDrawingBitmap();
        update(true);
    }


    /**
     * @brief Get current blur radius.
     */
    public float getElementBlurRadius() {
        return mElementBlurRadius;
    }


    /**
     * @brief Set the path to use.
     *
     * Paths are always filled using the count rule (which is an iOS feature and cannot be changed at
     * the moment). So something like shapes with holes etc. cannot be done through this layer.
     */
    public void setElementShapePath(Path path) {
        // ToDo: Don't know if equals() is meaningful overriden here.
        // any change?
   //     if (mElementShapePath.equals(path)) return;
        // store the path
        mElementShapePath = path;
        mElementShapeType = PDEAvailableShapes.SHAPE_CUSTOM_PATH;
        update();
    }


    /**
     * @brief Get the used custom path.
     *
     * @return custom path
     */
    public Path getElementShapePath() {
        return mElementShapePath;
    }


    /**
     * @brief Set a rectangular path
     */
    public void setElementShapeRect() {
        mElementShapeType = PDEAvailableShapes.SHAPE_RECT;
        update();
    }


    /**
     * @brief Set a rectangular path with rounded corners.
     */
    public void setElementShapeRoundedRect(float cornerRadius) {
        mElementCornerRadius = cornerRadius;
        mElementShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        update();
    }


    /**
     * @brief Set a oval inscribing the rect. Use a square to get a circle
     */
    public void setElementShapeOval() {
        mElementShapeType = PDEAvailableShapes.SHAPE_OVAL;
        update();
    }





//---------------------------------------------------------------------------------------------------------------------
// ----- Drawable overrides ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief draws the shadow
     */
    @Override
    public void draw(android.graphics.Canvas canvas) {
        Rect bounds = getBounds();

        // security
        if (bounds.width() <=0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        canvas.drawBitmap(mDrawingBitmap, bounds.left - mElementBlurRadius, bounds.top - mElementBlurRadius, new Paint());
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
        if (bounds.width() <= 0 || bounds.height() <= 0) return;
        // use bitmap to avoid gfx-acceleration bug
        if (mDrawingBitmap != null) mDrawingBitmap.recycle();
        // create bitmap
        mDrawingBitmap = Bitmap.createBitmap(bounds.width() + 2 * (int) mElementBlurRadius,
                                             bounds.height() + 2 * (int) mElementBlurRadius,
                                             Bitmap.Config.ARGB_8888);
    }


    /**
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    @Override
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        BlurMaskFilter blur;
        RectF drawRect;
        RectF normalizedBoundsRect;

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;

        // normalized and pixelshifted
        normalizedBoundsRect = new RectF(mPixelShift, mPixelShift, bounds.right - bounds.left - mPixelShift,
                                         bounds.bottom - bounds.top - mPixelShift);

        if (mElementBlurRadius <= 0.0) {
            // blur mask with 0.0 radius blur crashes, so don't set blur filter and make the rest invisible
            mBackgroundPaint.setColor(mElementShapeColor.newIntegerColorWithCombinedAlpha(0));
        } else {
            blur = new BlurMaskFilter(mElementBlurRadius, BlurMaskFilter.Blur.NORMAL);
            mBackgroundPaint.setMaskFilter(blur);
            mBackgroundPaint.setColor(mElementShapeColor.newIntegerColorWithCombinedAlpha(mAlpha));
        }

        drawRect = new RectF(normalizedBoundsRect.left + mElementBlurRadius,
                             normalizedBoundsRect.top + mElementBlurRadius,
                             normalizedBoundsRect.right + mElementBlurRadius,
                             normalizedBoundsRect.bottom + mElementBlurRadius);
        switch (mElementShapeType) {
            case PDEAvailableShapes.SHAPE_RECT:
                c.drawRect(drawRect, mBackgroundPaint);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                c.drawRoundRect(drawRect, mElementCornerRadius, mElementCornerRadius, mBackgroundPaint);
                break;
            case PDEAvailableShapes.SHAPE_OVAL:
                c.drawOval(drawRect, mBackgroundPaint);
                break;
            case PDEAvailableShapes.SHAPE_CUSTOM_PATH:
                c.drawPath(mElementShapePath, mBackgroundPaint);
                break;
        }
    }
}
