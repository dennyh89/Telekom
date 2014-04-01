/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */
package de.telekom.pde.codelibrary.ui.elements.metaphor;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;


//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawablePhotoFrame
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows picture With Frame.
 */
public class PDEDrawablePhotoFrame extends PDEDrawableMultilayer {

//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEDrawablePhotoFrameImage mPhotoFrameImage;
    private PDEConstants.PDEContentStyle mStyle;

    private Drawable mPicture;
    private final static float CONST_ASPECT_RATIO = 1.5f;
    private int mOriginalWidth;
    private int mOriginalHeight;
    private Rect mOriginalBounds;

    private int mPaddingTop;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingBottom;

    private boolean mShadowEnabled;

    // drawing helpers
    private boolean mMiddleAligned;
    private PDEDrawableShapedShadow mElementShadowDrawable;



//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     *
     * @param drawable Image to be shown
     */
    public PDEDrawablePhotoFrame(Drawable drawable) {
        // init drawable basics
        super();
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        mMiddleAligned = false;
        // init PDE defaults
        mPicture = drawable;

        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mPicture != null) {
            mOriginalHeight = mPicture.getIntrinsicHeight();
            mOriginalWidth = mPicture.getIntrinsicWidth();
        }

        mPaddingLeft = 0;
        mPaddingTop = 0;
        mPaddingRight = 0;
        mPaddingBottom = 0;

        // shadow is created on demand
        mElementShadowDrawable = null;
        mShadowEnabled = false;

        mPhotoFrameImage = new PDEDrawablePhotoFrameImage(drawable);
        addLayer(mPhotoFrameImage);
    }


    //---------------------------------------------------------------------------------------------------------------------
// ----- optional shadow ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------



    /**
     * @brief Activate shadow.
     */
    public void setElementShadowEnabled(boolean enabled) {
        //any change?
        if (enabled == mShadowEnabled) return;
        //remember
        mShadowEnabled = enabled;

        if (mShadowEnabled) {
            mElementShadowDrawable = mPhotoFrameImage.createElementShadow();
            insertLayerAtIndex(mElementShadowDrawable, 0);
        } else {
            removeLayer(mElementShadowDrawable);
            mElementShadowDrawable = null;
        }

        doLayout();
    }

    /**
     * @brief Get if shadow is activated.
     */
    public boolean getElementShadowEnabled() {
        return mShadowEnabled;
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set visual style
     */
    public void setElementContentStyle(PDEConstants.PDEContentStyle style) {
        //any change?
        if (style == mStyle) return;
        //remember
        mStyle = style;
        mPhotoFrameImage.setElementContentStyle(style);

        if (mShadowEnabled
                && mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat
                && mElementShadowDrawable != null) {
            removeLayer(mElementShadowDrawable);
            mElementShadowDrawable = null;
        }

        if (mShadowEnabled
                && mStyle == PDEConstants.PDEContentStyle.PDEContentStyleHaptic
                && mElementShadowDrawable == null) {
            mElementShadowDrawable = mPhotoFrameImage.createElementShadow();
            insertLayerAtIndex(mElementShadowDrawable,0);
        }

        doLayout();
    }

    /**
     * @brief Get visual style
     */
    @SuppressWarnings("unused")
    public PDEConstants.PDEContentStyle getElementContentStyle() {
        return mStyle;
    }


    /**
     * @brief Set Picture
     */
    public void setElementPicture(Drawable picture)
    {
        //any change?
        if (picture == mPicture) return;
        //remember
        mPicture = picture;

        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mPicture != null) {
            mOriginalHeight = mPicture.getIntrinsicHeight();
            mOriginalWidth = mPicture.getIntrinsicWidth();
        }

        correctBounds();

        //redraw
        mPhotoFrameImage.setElementPicture(picture);
    }


    /**
     * @brief Get Picture
     */
    public Drawable getElementPicture()
    {
        return mPicture;
    }


    /**
     * @brief Set Border Color
     */
    public void setElementBorderColor(PDEColor color) {
        mPhotoFrameImage.setElementBorderColor(color);
    }


    /**
     * @brief Get Border Color
     */
    public PDEColor getElementBorderColor() {
        return mPhotoFrameImage.getElementBorderColor();
    }


    /**
     * @brief Set darkstyle color
     */
    public void setElementDarkStyle(boolean isDarkStyle) {
        mPhotoFrameImage.setElementDarkStyle(isDarkStyle);
    }


    /**
     * @brief Get if element is colored in dark style
     */
    @SuppressWarnings("unused")
    public boolean getElementDarkStyle() {
        return mPhotoFrameImage.getElementDarkStyle();
    }


    /**
     * @brief Set middle aligned
     *
     * Middle aligned is for use in PDEPhotoFrameView, when true the picture is aligned in the middle of the border,
     * when false in the upper left
     */
    public void setElementMiddleAligned(boolean aligned) {
        if (mMiddleAligned == aligned) return;
        mMiddleAligned = aligned;

        doLayout();
    }


    /**
     * @brief Set all padding
     */
    @SuppressWarnings("unused")
    public void setElementPaddingAll(int padding) {
        //any change?
        if (padding == mPaddingLeft
                && padding == mPaddingTop
                && padding == mPaddingRight
                && padding == mPaddingBottom) {
            return;
        }

        //remember
        mPaddingLeft = padding;
        mPaddingTop = padding;
        mPaddingRight = padding;
        mPaddingBottom = padding;
        //redraw
        doLayout();
    }


    /**
     * @brief Set all paddings in one function
     */
    @SuppressWarnings("unused")
    public void setElementPaddingAll(int left, int top, int right, int bottom) {
        setElementPaddingLeft(left);
        setElementPaddingTop(top);
        setElementPaddingRight(right);
        setElementPaddingBottom(bottom);

        //redraw
        doLayout();
    }


    /**
     * @brief Set left padding
     */
    public void setElementPaddingLeft(int padding) {
        //any change?
        if (padding == mPaddingLeft) return;

        //remember
        mPaddingLeft = padding;

        //redraw
        doLayout();
    }


    /**
     * @brief Set top padding
     */
    public void setElementPaddingTop(int padding) {
        //any change?
        if (padding == mPaddingTop) return;

        //remember
        mPaddingTop = padding;

        //redraw
        doLayout();
    }


    /**
     * @brief Set right padding
     */
    public void setElementPaddingRight(int padding) {
        //any change?
        if (padding == mPaddingRight) return;

        //remember
        mPaddingRight = padding;

        //redraw
        doLayout();
    }


    /**
     * @brief Set bottom padding
     */
    public void setElementPaddingBottom(int padding) {
        //any change?
        if (padding == mPaddingBottom) return;
        //remember
        mPaddingBottom = padding;
        //redraw
        doLayout();
    }


    /**
     * @brief Get left padding
     */
    @SuppressWarnings("unused")
    public int getElementPaddingLeft() {
        return mPaddingLeft;
    }


    /**
     * @brief Get top padding
     */
    @SuppressWarnings("unused")
    public int getElementPaddingTop() {
        return mPaddingTop;
    }


    /**
     * @brief Get right padding
     */
    @SuppressWarnings("unused")
    public int getElementPaddingRight() {
        return mPaddingRight;
    }


    /**
     * @brief Get left padding
     */
    @SuppressWarnings("unused")
    public int getElementPaddingBottom() {
        return mPaddingBottom;
    }


    /**
     * @brief Get padding rect
     */
    @SuppressWarnings("unused")
    public Rect getElementPaddingRect() {
        return new Rect(mPaddingLeft,mPaddingTop,mPaddingRight,mPaddingBottom);
    }


    /**
     * @brief Helper function to get aspect ratio
     *
     * @return Current valid aspect ratio
     */
    private float getElementAspectRatio() {
        if (mPicture == null) return CONST_ASPECT_RATIO;

        if (mPicture.getIntrinsicWidth() >= mPicture.getIntrinsicHeight()){
            return CONST_ASPECT_RATIO;
        } else {
            return 1 / CONST_ASPECT_RATIO;
        }
    }


    /**
     * @brief Helper function to get intrinsic size of the picture
     */
    public Point getNativeSize() {
        if (mPicture != null) return new Point(mPicture.getIntrinsicWidth(), mPicture.getIntrinsicHeight());
        return new Point(0, 0);
    }


    /**
     * @brief Helper function to test if photo frame has a picture in it
     */
    public boolean hasPicture() {
        return mPicture != null;
    }


    /**
     * @brief Get element height.
     */
    public int getElementHeight() {
        if (mOriginalHeight >= 0) {
            if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
                return calculateElementHeightFlat();
            } else {
                return calculateElementHeightHaptic();
            }

        }
        return 0;
    }


    /**
     * @brief Get element width.
     */
    public int getElementWidth() {
        if (mOriginalWidth >= 0) {
            if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
                return calculateElementWidthFlat();
            } else {
                return calculateElementWidthHaptic();
            }

        }
        return 0;
    }


    /**
     * @brief Calculate element height.
     */
    private int calculateElementHeightFlat() {
        return mOriginalHeight + 4;
    }


    /**
     * @brief Calculate element width.
     */
    private int calculateElementWidthFlat() {
        return mOriginalWidth + 4;
    }


    /**
     * @brief Calculate element height.
     */
    private int calculateElementHeightHaptic() {
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        int y = Math.max(mOriginalWidth, mOriginalHeight);
        int res = Math.round((5.0f / 21.0f + (float) y / 105.0f));
        //minimum border width 0.2 BU
        if (res < 0.2* PDEBuildingUnits.BU()) res = (int)Math.round(0.2 * PDEBuildingUnits.BU());

        return mOriginalHeight + 2 * res + 2 * shadowWidth;
    }


    /**
     * @brief Calculate element width.
     */
    private int calculateElementWidthHaptic() {
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        int y = Math.max(mOriginalWidth, mOriginalHeight);
        int res = Math.round((5.0f / 21.0f + (float) y / 105.0f));
        //minimum border width 0.2 BU
        if (res < 0.2* PDEBuildingUnits.BU()) res = (int)Math.round(0.2 * PDEBuildingUnits.BU());

        return mOriginalWidth + 2*res + 2*shadowWidth;
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set width of the element.
     *
     * Convenience function.
     *
     * @param width The new width of the element.
     */
    @Override
    public void setLayoutWidth(int width) {
        setLayoutSize(new Point(width, Math.round((float) width / getElementAspectRatio())));
    }


    /**
     * @brief Set height of the element.
     *
     * Convenience function.
     *
     * @param height The new height of the element.
     */
    @Override
    public void setLayoutHeight(int height) {
        setLayoutSize(new Point(Math.round(height * getElementAspectRatio()), height));
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
     * @param  bounds Available Space for the element
     * @return Rect with the correct aspect ratio, fitting in available space
     *
     * When mMiddleAligned is set to true, resulting bounds are shifted to the middle of the available space
     */
    public Rect elementCalculateAspectRatioBounds(Rect bounds) {
        mOriginalBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);

        //subtract padding
        Rect boundsWithPadding = new Rect(bounds.left + mPaddingLeft, bounds.top + mPaddingTop,
                bounds.right - mPaddingRight, bounds.bottom - mPaddingBottom);
        Rect newBounds  = new Rect(0,0,0,0);

        //safety
        if (boundsWithPadding.height() <= 0 || boundsWithPadding.width() <= 0) return newBounds;

        if ((float)boundsWithPadding.width() / (float)boundsWithPadding.height() > getElementAspectRatio() ) {
            newBounds = new Rect(boundsWithPadding.left, boundsWithPadding.top, 0, boundsWithPadding.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getElementAspectRatio());
            //shifts bounds to center

            if (mMiddleAligned) {
                int horizontalShift = (boundsWithPadding.width() - newBounds.width()) / 2;
                newBounds.left += horizontalShift;
                newBounds.right += horizontalShift;
            }
        } else {
            newBounds = new Rect(boundsWithPadding.left, boundsWithPadding.top, boundsWithPadding.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getElementAspectRatio());
            //shifts bounds to center
            if (mMiddleAligned) {
                int verticalShift = (boundsWithPadding.height()-newBounds.height())/2;
                newBounds.top += verticalShift;
                newBounds.bottom += verticalShift;
            }
        }

        return newBounds;
    }


    /**
     * @brief Corrects bounds when these are not properly updated, for example when view is used in lists
     */
    public void correctBounds() {
        if ((mPicture.getIntrinsicWidth() > mPicture.getIntrinsicHeight()
                    && getBounds().height() > getBounds().width())
                ||  (mPicture.getIntrinsicHeight() > mPicture.getIntrinsicWidth()
                    && getBounds().width() > getBounds().height())) {
            setBounds(mOriginalBounds.left, mOriginalBounds.top, Math.max(mOriginalBounds.bottom,
                            mOriginalBounds.right), Math.max(mOriginalBounds.bottom, mOriginalBounds.right));
        }
    }


    /**
     * @brief Function where the multilayer reacts on bound changes.
     */
    @Override
    protected void doLayout() {
        Rect bounds = getBounds();

        bounds = updateShadowDrawable(bounds);
        updateFilmImageDrawable(bounds);

        //inform this layer about changes
        invalidateSelf();
    }


    /**
     * @brief update function for the image
     */
    private Rect updateShadowDrawable(Rect bounds) {
        Rect frameRect = new Rect(0, 0, bounds.width(), bounds.height());
        if (mElementShadowDrawable != null) {
            mElementShadowDrawable.setBounds(frameRect);
            int shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
            frameRect = new Rect(frameRect.left + shadowWidth,
                    frameRect.top + shadowWidth - PDEBuildingUnits.oneTwelfthsBU(),
                    frameRect.right - shadowWidth,
                    frameRect.bottom - shadowWidth - PDEBuildingUnits.oneTwelfthsBU());
        }

        return frameRect;
    }


    /**
     * @brief update function for the image
     */
    private void updateFilmImageDrawable(Rect bounds) {
        mPhotoFrameImage.setLayoutSize(bounds.width(), bounds.height());
        mPhotoFrameImage.setLayoutOffset(bounds.left, bounds.top);

        //correctBounds();
    }

}

