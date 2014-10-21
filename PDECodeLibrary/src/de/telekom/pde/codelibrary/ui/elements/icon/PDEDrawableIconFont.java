/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */
package de.telekom.pde.codelibrary.ui.elements.icon;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableIconFont
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows a character of the IconFont.
 */
public class PDEDrawableIconFont extends PDEDrawableBase {

    //-----  properties ---------------------------------------------------------------------------------------------------
    private String mIconText;
    private float mIconAspectRatio;
    private boolean mElementStretchToSize;

    private Paint mIconPaint;
    private PDEColor mIconColor;
    private PDETypeface mTextStyle;

    private Paint mShadowPaint;
    private PDEColor mShadowColor;
    private boolean mShadowEnabled;
    private int mBoundsHeight;
    private int mBoundsWidth;

    private float mShadowXOffset;
    private float mShadowYOffset;
    private float mPadding;

//----- init -----------------------------------------------------------------------------------------------------------


    /**
     * @brief Constructor
     *
     */
    public PDEDrawableIconFont(String icon) {
        // init drawable basics
        super();
        // init PDE defaults
        mElementStretchToSize = false;
        mIconText = icon;

        mIconPaint = new Paint();
        mIconColor = new PDEColor();
        mIconColor.setColor(PDEColor.valueOf("DTBlack").getIntegerColor());

        mShadowPaint = new Paint();
        mShadowColor = new PDEColor();
        mShadowColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());
        mShadowEnabled = false;
        mShadowXOffset = 0.0f;
        mShadowYOffset = 1.0f;

        mBoundsHeight = 0;
        mBoundsWidth = 0;
        mPadding = 1.0f;

        //set icon color and textstyle
        mTextStyle = PDETypeface.createByName("Tele_Iconfont.ttf");
        mIconPaint.setTypeface(mTextStyle.getTypeface());
        mShadowPaint.setTypeface(mTextStyle.getTypeface());

        calculateIconConstants();
        update(true);
    }

//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set Icon
     */
    public void setElementIconText(String icon) {
        //any change?
        if (icon.equals(mIconText)) return;
        //remember
        mIconText = icon;
        calculateIconConstants();
        // update
        update();
    }


    /*
     * @brief Get Icon
     */
    @SuppressWarnings("unused")
    public String getElementIconText() {
        return mIconText;
    }


    /**
     * @brief Set Icon Color
     */
    public void setElementIconColor(PDEColor color) {
        //any change?
        if (color.equals(mIconColor)) return;
        //remember
        mIconColor = color;
        createIconPaint();
        //redraw
        update();
    }


    /*
     * @brief Get Icon Color
     */
    @SuppressWarnings("unused")
    public PDEColor getElementIconColor() {
        return mIconColor;
    }


    /**
     * @brief Set Shadow Color
     */
    public void setElementShadowColor(PDEColor color) {
        //any change?
        if (color.equals(mShadowColor)) return;
        //remember
        mShadowColor = color;
        createShadowPaint();
        //redraw
        update();
    }


    /*
     * @brief Get shadow Color
     */
    @SuppressWarnings("unused")
    public PDEColor getElementShadowColor() {
        return mShadowColor;
    }


    /**
     * @brief Set if Icon stretches to available space
     */
    @SuppressWarnings("unused")
    public void setElementStretchToSize(boolean stretch) {
        //any change?
        if (stretch == mElementStretchToSize) return;
        //remember
        mElementStretchToSize = stretch;
        //redraw
        update();
    }


    /**
     * @brief Get if Icon stretches to available space
     */
    @SuppressWarnings("unused")
    public boolean getElementStretchToSize() {
        return mElementStretchToSize;
    }


    /**
     * @brief Set if shadow is enabled
     */
    public void setElementShadowEnabled(boolean enabled) {
        //any change?
        if (enabled == mShadowEnabled) return;
        //remember
        mShadowEnabled = enabled;
        //redraw
        update();
    }


    /**
     * @brief Get if shadow is enabled
     */
    @SuppressWarnings("unused")
    public boolean getElementShadowEnabled() {
        return mShadowEnabled;
    }


    /**
     * @brief Set shadow x offset
     */
    public void setElementShadowXOffset(float offset) {
        //any change?
        if (offset == mShadowXOffset) return;
        //remember
        mShadowXOffset = offset;
        //redraw
        update();
    }


    /**
     * @brief Get shadow x offset
     */
    @SuppressWarnings("unused")
    public float getElementShadowXOffset() {
        return mShadowXOffset;
    }


    /**
     * @brief Set shadow y offset
     */
    public void setElementShadowYOffset(float offset) {
        //any change?
        if (offset == mShadowYOffset) return;
        //remember
        mShadowYOffset = offset;
        //redraw
        update();
    }


    /**
     * @brief Get shadow y offset
     */
    @SuppressWarnings("unused")
    public float getElementShadowYOffset() {
        return mShadowYOffset;
    }


    /**
     * @brief Set padding
     */
    public void setElementPadding(float padding) {
        //any change?
        if (padding == mPadding) return;
        //remember
        mPadding = padding;
        //redraw
        update();
    }


    /**
     * @brief Get padding
     */
    @SuppressWarnings("unused")
    public float getElementPadding() {
        return mPadding;
    }


    /**
     * @brief Called when bounds set via rect.
     */
    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(elementCalculateAspectRatioBounds(bounds));
    }


    /**
     * @brief Called when bounds set via left/top/right/bottom values.
     *
     */
    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        Rect aspectRatioBounds = elementCalculateAspectRatioBounds(new Rect(left, top, right, bottom));
        super.setBounds(aspectRatioBounds.left, aspectRatioBounds.top,
                        aspectRatioBounds.right, aspectRatioBounds.bottom);
    }

//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Calculate the correct aspect ratio bounds.
     *
     * @return Rect with correct aspect ratio, fitting in available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds;

        if ((float) bounds.width() / (float) bounds.height() > getAspectRatio()) {
            newBounds = new Rect(bounds.left, bounds.top, 0, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getAspectRatio());
        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getAspectRatio());
        }

        return newBounds;
    }


    /**
     * @brief Set width of the element.
     *
     * Convenience function.
     *
     * @param width The new width of the element.
     */
    public void setLayoutWidth(int width) {
        setLayoutSize(new Point(width, Math.round((float) width / getAspectRatio())));
    }


    /**
     * @brief Set height of the element.
     *
     * Convenience function.
     *
     * @param height The new height of the element.
     */
    public void setLayoutHeight(int height) {
        setLayoutSize(new Point(Math.round(height * getAspectRatio()), height));
    }

//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update background and border paint values
     */
    protected void updateAllPaints() {
        createIconPaint();
        createShadowPaint();
    }


    /**
     * @brief create icon paint
     */
    private void createIconPaint() {
        mIconPaint = new Paint();
        mIconPaint.setAntiAlias(true);
        mIconPaint.setColorFilter(mColorFilter);
        mIconPaint.setDither(mDither);
        mIconPaint.setColor(mIconColor.newIntegerColorWithCombinedAlpha(mAlpha));
        mIconPaint.setTypeface(mTextStyle.getTypeface());
    }


    /**
     * @brief create shadow paint
     */
    private void createShadowPaint() {
        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColorFilter(mColorFilter);
        mShadowPaint.setDither(mDither);
        mShadowPaint.setColor(mShadowColor.newIntegerColorWithCombinedAlpha(mAlpha));
        mShadowPaint.setTypeface(mTextStyle.getTypeface());
    }


    /**
     * @brief gets aspect ratio
     */
    private float getAspectRatio() {
        if (mElementStretchToSize) {
            return mIconAspectRatio;
        } else {
            return 1.0f;
        }
    }


    /**
     * @brief calculates icon aspect ratio, used to make sure icon height takes all the available height
     */
    private void calculateIconConstants() {
        Rect textbounds = new Rect();

        if (mIconText != null) {
            mIconPaint.setTextSize(500);
            mIconPaint.getTextBounds(mIconText, 0, mIconText.length(), textbounds);
            mIconAspectRatio = (float) textbounds.width() / (float) textbounds.height();
        }
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
    @Override
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        Rect textbounds = new Rect();
        float leftposition, topposition, yrelation, xrelation;

        //substract padding
        mBoundsHeight = bounds.height();
        mBoundsWidth = bounds.width();

        bounds = new Rect(Math.round(mPixelShift) + Math.round(mPadding),
                          Math.round(mPixelShift) + Math.round(mPadding),
                          bounds.width() - Math.round(mPixelShift) - Math.round(mPadding),
                          bounds.height() - Math.round(mPixelShift) - Math.round(mPadding));

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null || mIconText == null) return;
        // set text size
        mIconPaint.setTextSize(bounds.height());
        mShadowPaint.setTextSize(bounds.height());
        //get text size
        mIconPaint.getTextBounds(mIconText, 0, mIconText.length(), textbounds);

        // if icon is stretched to size, text size relations to the bounds are calculated, to get text size from these
        if (mElementStretchToSize) {
            yrelation = (float) bounds.height() / (float) textbounds.height();
            xrelation = (float) bounds.width() / (float) textbounds.width();

            if (xrelation < yrelation) {
                mIconPaint.setTextSize(((float) bounds.height() * xrelation));
                mShadowPaint.setTextSize(((float) bounds.height() * xrelation));
            } else {
                mIconPaint.setTextSize(((float) bounds.height() * yrelation));
                mShadowPaint.setTextSize(((float) bounds.height() * yrelation));
            }

            mIconPaint.getTextBounds(mIconText, 0, mIconText.length(), textbounds);
        } else {
            //if icon is wider than high, textsize is reduced to prevent clipping on the right
            if (mIconAspectRatio > 1 && textbounds.width() > bounds.width()) {
                mIconPaint.setTextSize((bounds.height() / mIconAspectRatio) - 1.0f);
                mShadowPaint.setTextSize((bounds.height() / mIconAspectRatio) - 1.0f);
                mIconPaint.getTextBounds(mIconText, 0, mIconText.length(), textbounds);
            }
        }

        //set icon position, upper left when icon is stretched to size, or in the middle else
        //the icon "ö" is not intended to be shown in the vertical center
        if (mElementStretchToSize) {
            leftposition = bounds.left - textbounds.left;
            topposition = bounds.top - textbounds.top;
        } else if (mIconText.equals("ö")) {
            leftposition = bounds.left - textbounds.left + 0.5f * (bounds.width() - textbounds.width());
            topposition = bounds.top - textbounds.top;
        } else {
            leftposition = bounds.left - textbounds.left + 0.5f * (bounds.width() - textbounds.width());
            topposition = bounds.top - textbounds.top + 0.5f * (bounds.height() - textbounds.height());
        }

        // get canvas of the drawing-bitmap
        //draw shadow
        if (mShadowEnabled) c.drawText(mIconText, leftposition + mShadowXOffset,
                                       topposition + mShadowYOffset, mShadowPaint);
        //draw icon
        c.drawText(mIconText, leftposition, topposition, mIconPaint);
    }


    /**
     * @brief get height of the element
     */
    public int getElementHeight() {
        return mBoundsHeight;
    }


    /**
     * @brief get width of the element
     */
    public int getElementWidth() {
        return mBoundsWidth;
    }

}



