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
// PDEDrawableShapedOuterShadow
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Graphics primitive - an outer shadow with several possible shapes.
 *
 * This class is used for outer shadows of elements. The possible shapes are:
 * rectangle, rounded rectangle, oval or a custom shape by path.
 */
public class PDEDrawableShapedOuterShadow extends PDEDrawableBase {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawableShapedOuterShadow.class.getName();

//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEColor mElementShapeColor;
    private Path mElementShapePath;
    private float mElementCornerRadius;
    private Paint mBackgroundPaint = null;



//----- init -----------------------------------------------------------------------------------------------------------
    public PDEDrawableShapedOuterShadow() {
        // init drawable basics
        super();
        // take over the default locally (these are the iOS default values for now)
        mElementShapeColor = PDEColor.valueOf(Color.BLACK);
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
    @SuppressWarnings("unused")
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



//---------------------------------------------------------------------------------------------------------------------
// ----- Drawable overrides ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief draws the shadow
     */
    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        // security
        if (bounds.width() <=0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        canvas.drawBitmap(mDrawingBitmap, bounds.left, bounds.top, new Paint());
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
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(3);
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
        mDrawingBitmap = Bitmap.createBitmap(bounds.width(),
                                             bounds.height(),
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
        RectF drawRect;
        RectF normalizedBoundsRect;

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;

        // normalized and pixelshifted
        normalizedBoundsRect = new RectF(mPixelShift,
                mPixelShift,
                bounds.right - bounds.left - mPixelShift,
                bounds.bottom - bounds.top - mPixelShift);


        // blur mask with 0.0 radius blur crashes, so don't set blur filter and make the rest invisible
        mBackgroundPaint.setColor(mElementShapeColor.newIntegerColorWithCombinedAlpha(mAlpha));

        drawRect = new RectF(normalizedBoundsRect.left,
                             normalizedBoundsRect.top,
                             normalizedBoundsRect.right,
                             normalizedBoundsRect.bottom);

        mElementShapePath = new Path();
        mElementShapePath.addArc(new RectF(drawRect.left,
                    drawRect.bottom - 2 * mElementCornerRadius,
                    drawRect.left + 2 * mElementCornerRadius,
                    drawRect.bottom),
                180, -90);
        mElementShapePath.lineTo(drawRect.right - mElementCornerRadius, drawRect.bottom);
        mElementShapePath.addArc(new RectF(drawRect.right - 2 * mElementCornerRadius,
                    drawRect.bottom - 2 * mElementCornerRadius,
                    drawRect.right,
                    drawRect.bottom),
                90, -90);



        c.drawPath(mElementShapePath, mBackgroundPaint);

    }
}
