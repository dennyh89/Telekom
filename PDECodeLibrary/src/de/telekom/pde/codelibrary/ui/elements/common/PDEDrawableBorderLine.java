/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.*;
import de.telekom.pde.codelibrary.ui.color.PDEColor;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableBorderLine
//----------------------------------------------------------------------------------------------------------------------


// todo: 1) Check if path drawing and resizing is working properly
// todo: 2) Set initial border color


/**
 * @brief With this Drawable the outline of the button is drawn.
 *
 * Some Buttons have a thin outline around them. This outline is drawn by this drawable.
 */


public class PDEDrawableBorderLine extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    // parameters
    protected float mElementCornerRadius;
    protected float mElementBorderWidth;
    protected int mElementBorderColor;

    // shape stuff
    protected Path mElementShapePath;
    private int mShapeType;

    // other
    private Paint mBorderPaint = null;


//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    /**
     * @brief Class initialization.
     */
    public PDEDrawableBorderLine() {
        // init drawable basics
        super();
        // init
        mElementCornerRadius = 10.0f;
        mElementBorderWidth = 2.0f;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        mElementShapePath = new Path();

        update(true);
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set the corner radius (rounded rect)
     */
    public void setElementCornerRadius(float radius) {
        // anything to do?
        if (radius == mElementCornerRadius) return;
        // remember
        mElementCornerRadius = radius;
        // update
        update();
    }


    /**
     * @brief Get the corner radius (rounded rect)
     */
    public float getElementCornerRadius() {
        return mElementCornerRadius;
    }

    /**
     * @brief Set the width of the outline
     */
    public void setElementBorderWidth(float width) {
        // anything to do?
        if (width == mElementBorderWidth) return;
        // remember
        mElementBorderWidth = width;
        mBorderPaint.setStrokeWidth(mElementBorderWidth);
        // update
        update();
    }

    /**
     * @brief Get the width of the outline
     *
     * @return Width of the outline
     */
    public float getElementBorderWidth() {
        return mElementBorderWidth;
    }


    /**
     * @brief Set the color of the outline
     *
     * @param color of the outline
     */
    public void setElementBorderColor(int color) {
        // anything to do?
        if (color == mElementBorderColor) return;
        // remember
        mElementBorderColor = color;
        mBorderPaint.setColor(PDEColor.getIntegerColorCombinedWithAlpha(mElementBorderColor, mAlpha));
        // update
        update();
    }


    /**
     * @brief Get the color of the outline
     *
     * @return color of the outline
     */
    public int getElementBorderColor() {
        return mElementBorderColor;
    }


    /**
     * @brief Set custom path of shape
     *
     * @param path A path that defines a custom shape
     */
    // ToDo: test this
    public void setElementShapePath(Path path) {
        // ToDo: Don't know if equals() is meaningful overriden here.
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
     * @brief Get custom path of shape
     *
     * @return path of our custom shape
     */
    public Path getElementShapePath() {
        return mElementShapePath;
    }


    /**
     * @brief Set a rectangular shape
     */
    public void setElementShapeRect() {
        mShapeType = PDEAvailableShapes.SHAPE_RECT;
        // update
        update();
    }


    /**
     * @brief Set a rectangular shape with rounded corners.
     *
     * @param cornerRadius the radius of the rounded corners of our rounded rectangle
     */
    public void setElementShapeRoundedRect(float cornerRadius) {
        mElementCornerRadius = cornerRadius;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        // update
        update();
    }

    /**
     * @brief Set oval shape of outline.
     */
    public void setElementShapeOval() {
        mShapeType = PDEAvailableShapes.SHAPE_OVAL;
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
        createBorderPaint();
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
        mBorderPaint.setColor(PDEColor.getIntegerColorCombinedWithAlpha(mElementBorderColor, mAlpha));
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
        RectF frame;
        Rect normalizedBoundsRect;

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;

        // normalized version of the bounding rect
        normalizedBoundsRect = new Rect(0, 0, bounds.width(), bounds.height());

        // we seem not to be pixel aligned, so if we try to draw a line with one pixel width and we draw it between
        // two pixels, this results in line which is two pixel width and doesn't have the correct color.
        // So we have to shift the coordinate by a half pixel to be pixel aligned again.
        frame = new RectF(normalizedBoundsRect.left + mPixelShift, normalizedBoundsRect.top + mPixelShift,
                          normalizedBoundsRect.right - mPixelShift,
                          normalizedBoundsRect.bottom - mPixelShift);


        // ToDo: Custom path needs testing
        // draw the desired shape of the outline
        switch (mShapeType) {
            case PDEAvailableShapes.SHAPE_RECT:
                c.drawRect(frame, mBorderPaint);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                c.drawRoundRect(frame, mElementCornerRadius, mElementCornerRadius, mBorderPaint);
                break;
            case PDEAvailableShapes.SHAPE_OVAL:
                c.drawOval(frame, mBorderPaint);
                break;
            case PDEAvailableShapes.SHAPE_CUSTOM_PATH:
                c.drawPath(mElementShapePath, mBorderPaint);
                break;
        }
    }
}

