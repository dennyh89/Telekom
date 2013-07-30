/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableBase;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawablePath
//----------------------------------------------------------------------------------------------------------------------

// todo: check if we shouldn't extend PDEDrawableShape by the painting of a border, because with a border PDEDrawableShape could also do the job of this class I think.

/**
 * @brief With this Drawable you can draw elements with custom shapes.
 *
 * The custom shape is defined by a given path.
 */
public class PDEDrawablePath extends PDEDrawableBase {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawablePath.class.getName();


//-----  properties ---------------------------------------------------------------------------------------------------
    // colors
    protected PDEColor mElementBackgroundColor;
    protected PDEColor mElementBorderColor;
    // measurements
    protected float mElementBorderWidth;

    // path that should be drawn
    private Path mElementPath;

    // drawable helpers
    private Paint mBorderPaint;
    private Paint mBackgroundPaint;


//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    public PDEDrawablePath() {
        // init drawable basics
        super();
        // init PDE defaults
        mElementPath = new Path();
        mElementBorderWidth = 1.0f;
        mElementBackgroundColor = PDEColor.valueOf("DTBlack");
        mElementBorderColor = PDEColor.valueOf("DTBlack");

        //init paints for drawing
        update(true);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set fill (background) color.
     *
     * @param color The new backgroundcolor of the element.
     */
    public void setElementBackgroundColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBackgroundColor.getIntegerColor()) {
            return;
        }

        // remember
        mElementBackgroundColor = color;
        mBackgroundPaint.setColor(mElementBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));

        // update
        update();
    }


    /**
     * @brief Get background color.
     *
     * @return The color of the background.
     */
    public PDEColor getElementBackgroundColor() {
        return mElementBackgroundColor;
    }


    /**
     * @brief Set border color.
     *
     * @param color The new color of the outline.
     */
    public void setElementBorderColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBorderColor.getIntegerColor()) {
            return;
        }

        // remember
        mElementBorderColor = color;
        mBorderPaint.setColor(mElementBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));

        // update
        update();
    }


    /**
     * @brief Get outline color.
     *
     * @return The color of the outline.
     */
    public PDEColor getElementBorderColor() {
        return mElementBorderColor;
    }


    /**
     * @brief Set border width.
     *
     * @param width The new width of the outline.
     */
    public void setElementBorderWidth(@SuppressWarnings("SameParameterValue") float width) {
        // any change?
        if (width == mElementBorderWidth) {
            return;
        }

        // remember
        mElementBorderWidth = width;
        mBorderPaint.setStrokeWidth(mElementBorderWidth);

        // update
        update();
    }


    /**
     * @brief Get border width.
     *
     * @return The width of the outline.
     */
    public float getElementBorderWidth() {
        return mElementBorderWidth;
    }


    /**
     * @brief Set path that should be drawn.
     *
     * @param path path that should be drawn.
     */
    public void setElementPath(Path path) {
        mElementPath = path;
        // update
        update();
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
        createBorderPaint();
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
        mBackgroundPaint.setColor(mElementBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create border paint for drawing
     */
    private void createBorderPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mElementBorderWidth);
        mBorderPaint.setColorFilter(mColorFilter);
        mBorderPaint.setDither(mDither);
        mBorderPaint.setColor(mElementBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- Drawing Bitmap ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;

        c.drawPath(mElementPath, mBackgroundPaint);
        c.drawPath(mElementPath, mBorderPaint);
    }
}
