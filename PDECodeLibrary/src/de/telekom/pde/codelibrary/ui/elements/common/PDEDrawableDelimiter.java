/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.color.PDEColor;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableDelimiter
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Graphics primitive - a simple line for e.g. separating list entries.
 */
@SuppressWarnings("unused")
public class PDEDrawableDelimiter extends PDEDrawableBase {

    public enum PDEDrawableDelimiterType {
        PDEDrawableDelimiterTypeHorizontal,
        PDEDrawableDelimiterTypeVertical
    }

    // paint
    private Paint mBackgroundPaint;
    // other
    private PDEDrawableDelimiterType mElementType;
    private PDEColor mElementBackgroundColor;


    // initialization

    public PDEDrawableDelimiter() {
        super();
        mElementType = PDEDrawableDelimiterType.PDEDrawableDelimiterTypeHorizontal;
        mElementBackgroundColor = PDEColor.valueOf("DTGrey220");

        update(true);
    }


    /**
     * @brief Set fill (background) color.
     *
     * @param color New background color of the delimiter.
     */
    public void setElementBackgroundColor(PDEColor color)
    {
        // any change?
        if (color == mElementBackgroundColor) return;

        // remember the color
        mElementBackgroundColor = color;
        mBackgroundPaint.setColor(mElementBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));

        //update
        update(true);
    }


    /**
     * @brief Get fill (background) color.
     *
     */
    public PDEColor getElementBackgroundColor(){
        return mElementBackgroundColor;
    }


    /**
     * @brief Set horizontal or vertical orientation of delimiter.
     *
     * @param elementType Constant that defines the delimiter either as horizontal or vertical.
     */
    public void setElementType(PDEDrawableDelimiterType elementType)
    {
        // any change?
        if (elementType == mElementType) return;

        // remember the Type
        mElementType = elementType;

        // switch orientation
        switch (elementType) {
            case PDEDrawableDelimiterTypeHorizontal:
                // setWidth
                setLayoutWidth(getBounds().height());
                break;
            case PDEDrawableDelimiterTypeVertical:
                // setHeight
                setLayoutHeight(getBounds().width());
                break;
        }
    }

    /**
     * @brief Get horizontal or vertical orientation of delimiter.
     *
     */
    public PDEDrawableDelimiterType getElementType(){
        return mElementType;
    }


    /**
     * @brief Set Element Height
     *
     * @param height The new height of the delimiter.
     */
    @Override
    public void setLayoutHeight(int height)
    {
        // ignore if type is horizontal
        if (mElementType == PDEDrawableDelimiterType.PDEDrawableDelimiterTypeVertical){
            // width is 1 pixel
            setLayoutSize(new Point(1,height));
        }
    }


    /**
     * @brief Set Element Width
     *
     * @param width The new width of the delimiter.
     */
    @Override
    public void setLayoutWidth (int width)
    {
        // ignore if type is vertical
        if (mElementType == PDEDrawableDelimiterType.PDEDrawableDelimiterTypeHorizontal){
            // height is 1 pixel
            setLayoutSize(new Point(width,1));
        }
    }


    /**
     * @brief Called when bounds set via rect.
     */
    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(limitBounds(bounds));
    }


    /**
     * @brief Called when bounds set via left/top/right/bottom values.
     */
    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        Rect newBounds = limitBounds(new Rect(left,top, right, bottom));
        super.setBounds(newBounds.left, newBounds.top, newBounds.right, newBounds.bottom);
    }


    /**
    * @brief Set new Bounds the drawable.
    *
    * Depending on the type/orientation of the delimiter only the meaningful value (width or height) is remembered,
    * the other one is automatically set to 1 pixel.
    *
    * @param bounds New bounds of the delimiter.
    */
    private Rect limitBounds(Rect bounds) {
        Rect newBounds = new Rect(bounds.left,bounds.top,bounds.right,bounds.bottom);

        // security
        if (mElementType == PDEDrawableDelimiterType.PDEDrawableDelimiterTypeHorizontal && bounds.height() > 1){
            newBounds.bottom = newBounds.top + 1;
        } else if (mElementType == PDEDrawableDelimiterType.PDEDrawableDelimiterTypeVertical && bounds.width() > 1){
            newBounds.right = newBounds.left + 1;
        }

         return newBounds;
    }

// todo Alex: please check this function:
    /**
     * @brief In order to be usable als delimiter in android native list, there has to be an intrinsic height.
     * @return 1 if it is an element of type horizontal
     */
    @Override
    public int getIntrinsicHeight() {
        if (mElementType == PDEDrawableDelimiterType.PDEDrawableDelimiterTypeHorizontal) {
            return 1;
        }
        return super.getIntrinsicHeight();
    }


// todo Alex: please check this function:
    /**
     * @brief In order to be usable als delimiter in android native list, there has to be an intrinsic width.
     * @return 1 if it is an element of type vertical
     */
    @Override
    public int getIntrinsicWidth() {
        if (mElementType == PDEDrawableDelimiterType.PDEDrawableDelimiterTypeVertical) {
            return 1;
        }
        return super.getIntrinsicWidth();
    }



    //---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Update all used Paint-Instances here.
     *
     * If global paint properties like e.g. alpha, dither, colorFilter, etc. change all important Paint-Instances that
     * are used within this drawable have to be updated. Place the code to recreate the Paints (with the new values)
     * within this method. You can also use it for updates of a distinct paint (e.g. when a distinct color has
     * changed), although this means some overhead.
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
        mBackgroundPaint.setColor(mElementBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));
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
        // draw normalized rect
        c.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
    }
}
