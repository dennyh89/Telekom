/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
        * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
        * https://www.design.telekom.com/myaccount/terms-of-use/
        *
        * Copyright (c) 2012. Neuland Multimedia GmbH.
        */
package de.telekom.pde.codelibrary.ui.elements.metaphor;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableMusicMetaphor
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Music Metaphor
 */
public class PDEDrawableMusicMetaphor extends PDEDrawableMultilayer {

    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawableMusicMetaphor.class.getName();

    private PDEDrawableMusicMetaphorImage mMusicMetaphorImage;
    
//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEConstants.PDEContentStyle mStyle;

    private final static float CONST_ASPECT_RATIO = 20.0f / 18.0f;
    private Drawable mPicture;
    private int mOriginalHeight;
    private int mOriginalWidth;

    private boolean mShadowEnabled;
    private PDEDrawableShapedShadow mElementShadowDrawable;


//----- init -----------------------------------------------------------------------------------------------------------

    /**
     * @brief Constructor
     */
    public PDEDrawableMusicMetaphor(Drawable drawable) {
       // init drawable basics
        super();
        // init PDE defaults
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        mPicture = drawable;

        mOriginalWidth = 0;
        mOriginalHeight = 0;
        if (mPicture != null) {
            mOriginalHeight = mPicture.getIntrinsicHeight();
            mOriginalWidth = mPicture.getIntrinsicWidth();
        }

        mShadowEnabled = false;
        mElementShadowDrawable = null;

        mMusicMetaphorImage = new PDEDrawableMusicMetaphorImage(drawable);
        addLayer(mMusicMetaphorImage);
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
            mElementShadowDrawable = mMusicMetaphorImage.createElementShadow();
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
        mMusicMetaphorImage.setElementContentStyle(style);

        if (mShadowEnabled
                && mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat
                && mElementShadowDrawable != null) {
            removeLayer(mElementShadowDrawable);
            mElementShadowDrawable = null;
        }

        if (mShadowEnabled
                && mStyle == PDEConstants.PDEContentStyle.PDEContentStyleHaptic
                && mElementShadowDrawable == null) {
            mElementShadowDrawable = mMusicMetaphorImage.createElementShadow();
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
    public void setElementPicture(Drawable picture) {
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

        mMusicMetaphorImage.setElementPicture(picture);
    }


    /**
     * @brief Get Picture
     */
    public Drawable getElementPicture() {
        return mPicture;
    }


    /**
     * @brief Set darkstyle color
     */
    public void setElementDarkStyle(boolean isDarkStyle) {
        mMusicMetaphorImage.setElementDarkStyle(isDarkStyle);
    }


    /**
     * @brief Get if element is colored in dark style
     */
    @SuppressWarnings("unused")
    public boolean getElementDarkStyle() {
        return mMusicMetaphorImage.getElementDarkStyle();
    }


    /**
     * @brief Set middle aligned
     *
     * Middle aligned is for use in View, when true the element is aligned in the middle of the border,
     * when false in the upper left
     */
    public void setElementMiddleAligned(boolean aligned) {
        if (mMusicMetaphorImage.mMiddleAligned == aligned) return;
        mMusicMetaphorImage.mMiddleAligned = aligned;

        doLayout();
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
        int returnValue = 0;

        if (mOriginalHeight > 0) {
            if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
                returnValue = calculateElementHeightFlat();
            } else {
                returnValue = calculateElementHeightHaptic();
            }
        }

        return returnValue;
    }


    /**
     * @brief Get element width.
     */
    public int getElementWidth() {
        int returnValue = 0;

        if (mOriginalWidth > 0) {
            if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
                returnValue = calculateElementWidthFlat();
            } else {
                returnValue = calculateElementWidthHaptic();
            }
        }

        return returnValue;
    }


    /**
     * @brief Calculates element height
     */
    protected int calculateElementWidthFlat() {
        if (mOriginalHeight > mOriginalWidth) {
            return mOriginalWidth + 4;
        } else {
            return mOriginalHeight + 4;
        }
    }


    /**
     * @brief Calculates element width
     */
    protected int calculateElementHeightFlat() {
        if (mOriginalHeight > mOriginalWidth) {
            return mOriginalWidth + 4;
        } else {
            return mOriginalHeight + 4;
        }
    }


    /**
     * @brief Calculates element height
     */
    private int calculateElementWidthHaptic() {
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        if (mOriginalHeight > mOriginalWidth) {
            return Math.round(((float)mOriginalWidth / 34.5f) * 40.0f) + 2 * shadowWidth;
        } else {
            return Math.round(((float)mOriginalHeight / 34.5f) * 40.0f) + 2 * shadowWidth;
        }
    }


    /**
     * @brief Calculates element width
     */
    private int calculateElementHeightHaptic() {
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        if (mOriginalHeight > mOriginalWidth) {
            return Math.round(((float)mOriginalWidth / 34.5f) * 36.0f) + 2 * shadowWidth;
        } else {
            return Math.round(((float)mOriginalHeight / 34.5f) * 36.0f) + 2 * shadowWidth;
        }
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------





    /**
     * @brief Set width of the element.
     *
     * Convenience function.
     */
    @Override
    public void setLayoutWidth(int width) {
        if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
            // the metaphor has the same width and height, so this assignment is correct
            // noinspection SuspiciousNameCombination
            setLayoutSize(new Point(width, width ));
        } else {
            setLayoutSize(new Point(width, Math.round((float) width / CONST_ASPECT_RATIO)));
        }

    }


    /**
     * @brief Set height of the element.
     *
     * Convenience function.
     */
    @Override
    public void setLayoutHeight(int height) {
        if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
            // the metaphor has the same height and width, so this assignment is correct
            // noinspection SuspiciousNameCombination
            setLayoutSize(new Point(height, height));
        } else {
            setLayoutSize(new Point(Math.round(height * CONST_ASPECT_RATIO), height));
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
        mMusicMetaphorImage.setLayoutSize(bounds.width(), bounds.height());
        mMusicMetaphorImage.setLayoutOffset(bounds.left, bounds.top);
    }

}
