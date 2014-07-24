/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.icon;

import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableIconImage
//----------------------------------------------------------------------------------------------------------------------


public class PDEDrawableIconImage extends PDEDrawableBase {

    //-----  properties ---------------------------------------------------------------------------------------------------
    private Drawable mElementImage;
    private Drawable mShadowImage;
    private float mImageAspectRatio;
    private boolean mAspectRatioEnabled;

    private PDEColor mShadowColor;
    private boolean mShadowEnabled;

    private float mShadowXOffset;
    private float mShadowYOffset;
    private float mPadding;

    private PDEColor mIconColor;
    private LightingColorFilter mShadowColorFilter;
    private LightingColorFilter mIconColorFilter;


    /**
     * @brief Constructor
     *
     * @param drawable Image to be shown
     */
    public PDEDrawableIconImage(Drawable drawable) {
        super();
        mElementImage = drawable;
        mAspectRatioEnabled = true;

        float intrinsicWidth = (float) mElementImage.getIntrinsicWidth();
        float intrinsicHeight = (float) mElementImage.getIntrinsicHeight();
        if (intrinsicWidth != 0 && intrinsicHeight != 0) {
            mImageAspectRatio = intrinsicWidth / intrinsicHeight;
        } else {
            mImageAspectRatio = 1;
        }

        mIconColor = null;
        mIconColorFilter = null;

        mShadowColor = new PDEColor();
        mShadowColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());
        mShadowColorFilter = new LightingColorFilter
                (mShadowColor.getIntegerColor(), mShadowColor.getIntegerColor());
        mShadowEnabled = false;
        mShadowXOffset = 0.0f;
        mShadowYOffset = 1.0f;

        mPadding = 1.0f;

        updateImage();
        update(true);
    }


    @SuppressWarnings("unused")
    public void setElementAspectRatio(float aspectRatio) {
        // any change?
        if (aspectRatio == mImageAspectRatio) return;

        // remember
        mImageAspectRatio = aspectRatio;

        // update
        update();
    }


    @SuppressWarnings("unused")
    public void enableAspectRatio(boolean enable) {
        // any change?
        if (mAspectRatioEnabled == enable) return;

        // remember
        mAspectRatioEnabled = enable;

        // update
        update();
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
        super.setBounds(aspectRatioBounds.left,
                        aspectRatioBounds.top,
                        aspectRatioBounds.right,
                        aspectRatioBounds.bottom);
    }


    /**
     * @brief Calculate the correct aspect ratio bounds.
     *
     * @return Rect with correct aspect ratio, fitting in available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds;

        if (!mAspectRatioEnabled) return bounds;
        if ((float) bounds.width() / (float) bounds.height() > mImageAspectRatio) {
            newBounds = new Rect(bounds.left, bounds.top, 0, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * mImageAspectRatio);
        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / mImageAspectRatio);
        }

        return newBounds;
    }


    /**
     * @brief Update all of my sub-layers.
     */
    @Override
    protected void doLayout() {
        Rect bounds = getBounds();
        mElementImage.setBounds(0, 0, bounds.width(), bounds.height());
    }


    protected void updateAllPaints() {
        // no further paints needed so apply paint changes directly on the image
        mElementImage.setAlpha(mAlpha);
        mElementImage.setDither(mDither);
        if (mIconColorFilter == null) {
            mElementImage.setColorFilter(mColorFilter);
        }
    }


    /**
     * @brief Set Element Height
     */
    public void setLayoutHeight(int height) {
        if (mAspectRatioEnabled) {
            setLayoutSize(Math.round(height * mImageAspectRatio), height);
        } else {
            setLayoutSize(getBounds().width(), height);
        }
    }


    /**
     * @brief Set Element Width
     */
    public void setLayoutWidth(int width) {
        if (mAspectRatioEnabled) {
            setLayoutSize(width, Math.round(width / mImageAspectRatio));
        } else {
            setLayoutSize(width, getBounds().height());
        }
    }


    /**
     * @brief Set Image
     */
    public void setElementImage(Drawable image) {
        //any change?
        if (image == mElementImage) return;

        //remember
        mElementImage = image;
        //aspect ratio
        float intrinsicWidth = (float) mElementImage.getIntrinsicWidth();
        float intrinsicHeight = (float) mElementImage.getIntrinsicHeight();
        if (intrinsicWidth != 0 && intrinsicHeight != 0) {
            mImageAspectRatio = intrinsicWidth / intrinsicHeight;
        } else {
            mImageAspectRatio = 1;
        }

        updateImage();
        update(true);
    }


    /**
     * @brief Get Image
     */
    public Drawable getElementImage() {
        return mElementImage;
    }


    /**
     * @brief Set Image Color
     */
    public void setElementIconColor(PDEColor color) {
        if (color == mIconColor) return;
        mIconColor = color;

        if (mIconColor != null) {
            mIconColorFilter = new LightingColorFilter
                    (mIconColor.getIntegerColor(), mIconColor.getIntegerColor());
        } else {
            mIconColorFilter = null;
        }

        updateImage();
        update(true);
    }


    /**
     * @brief Get Image Color
     */
    @SuppressWarnings("unused")
    public PDEColor getElementIconColor() {
        return mIconColor;
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
     * @brief Set Shadow Color
     */
    public void setElementShadowColor(PDEColor color) {
        //any change?
        if (color == mShadowColor) return;
        //remember
        mShadowColor = color;

        if (mShadowColor != null) {
            mShadowColorFilter = new LightingColorFilter
                    (mShadowColor.getIntegerColor(), mShadowColor.getIntegerColor());
        } else {
            mShadowColorFilter = null;
        }
        //redraw
        updateImage();
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
     * @brief Updates image colors
     */
    private void updateImage() {
        mShadowImage = mElementImage.getConstantState().newDrawable();
        mShadowImage.mutate().setColorFilter(mShadowColorFilter);

        if (mIconColorFilter == null) {
            mElementImage.mutate().setColorFilter(null);
        } else {
            mElementImage.setColorFilter(mIconColorFilter);
        }

    }


    /**
     * @brief Draw Icon and shadow if enabled
     *
     * @param c the Canvas of the DrawingBitmap we want to draw into.
     * @param bounds the current bounding rect of our Drawable.
     */
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        //padding
        bounds = new Rect(Math.round(mPixelShift + mPadding),
                          Math.round(mPixelShift + mPadding),
                          Math.round(bounds.width() - mPixelShift - mPadding),
                          Math.round(bounds.height() - mPixelShift - mPadding));

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;

        //set shadow
        if (mShadowEnabled) {
            Rect shadowBounds = new Rect(Math.round(bounds.left + mShadowXOffset),
                                         Math.round(bounds.top + mShadowYOffset),
                                         Math.round(bounds.right + mShadowXOffset),
                                         Math.round(bounds.bottom + mShadowYOffset));

            mShadowImage.setBounds(shadowBounds);
            mShadowImage.draw(c);
        }

        //draw image
        mElementImage.setBounds(bounds);
        mElementImage.draw(c);
    }

}
