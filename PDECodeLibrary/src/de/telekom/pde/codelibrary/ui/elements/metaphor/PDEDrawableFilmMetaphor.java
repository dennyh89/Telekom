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
//  PDEDrawableFilmMetaphor
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Film Metaphor
 */
public class PDEDrawableFilmMetaphor extends PDEDrawableMultilayer {

//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEDrawableFilmMetaphorImage mFilmMetaphorImage;

    private PDEConstants.PDEContentStyle mStyle;
    private Drawable mPicture;
    private boolean mMiddleAligned;
    private int mOriginalHeight;
    private int mOriginalWidth;

    private boolean mShadowEnabled;
    private final static float CONST_ASPECT_RATIO = 20.0f / 29.0f;

    // outer shadow
    private PDEDrawableShapedShadow mElementShadowDrawable;

//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     */
    public PDEDrawableFilmMetaphor(Drawable drawable)
    {
        // init drawable basics
        super();
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        mMiddleAligned = false;
        mPicture = drawable;

        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mPicture != null) {
            mOriginalHeight = mPicture.getIntrinsicHeight();
            mOriginalWidth = mPicture.getIntrinsicWidth();
        }

        // shadow is created on demand
        mElementShadowDrawable = null;
        mShadowEnabled = false;

        mFilmMetaphorImage = new PDEDrawableFilmMetaphorImage(drawable);
        addLayer(mFilmMetaphorImage);
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
            mElementShadowDrawable = mFilmMetaphorImage.createElementShadow();
            insertLayerAtIndex(mElementShadowDrawable,0);
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
        mFilmMetaphorImage.setElementContentStyle(style);

        if (mShadowEnabled && mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat && mElementShadowDrawable != null) {
            removeLayer(mElementShadowDrawable);
            mElementShadowDrawable = null;
        }

        if (mShadowEnabled && mStyle == PDEConstants.PDEContentStyle.PDEContentStyleHaptic && mElementShadowDrawable == null) {
            mElementShadowDrawable = mFilmMetaphorImage.createElementShadow();
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

        mFilmMetaphorImage.setElementPicture(mPicture);
    }

    /**
     * @brief Get Picture
     */
    @SuppressWarnings("unused")
    public Drawable getElementPicture() {

        return mPicture;
    }


    /**
     * @brief Set darkstyle color
     */
    public void setElementDarkStyle(boolean isDarkStyle) {
        mFilmMetaphorImage.setElementDarkStyle(isDarkStyle);
    }


    /**
     * @brief Get if element is colored in dark style
     */
    @SuppressWarnings("unused")
    public boolean getElementDarkStyle() {
        return mFilmMetaphorImage.getElementDarkStyle();
    }


    /**
     * @brief Set middle aligned
     *
     * Middle aligned is for use in View, when true the element is aligned in the middle of the border,
     * when false in the upper left
     */
    public void setElementMiddleAligned(boolean aligned) {
        if (mMiddleAligned == aligned) return;
        mMiddleAligned = aligned;

        doLayout();
    }


    /**
     * @brief Helper function to get intrinsic size of the picture
     */
    public Point getNativeSize() {
        if (mPicture != null) {
            return new Point(mPicture.getIntrinsicWidth(), mPicture.getIntrinsicHeight());
        }

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
     * @brief Calculate element height haptic
     */
    private int calculateElementHeightFlat() {
        if ((float)mOriginalWidth / (float)mOriginalHeight > CONST_ASPECT_RATIO) {
            return Math.round(((float)mOriginalWidth / CONST_ASPECT_RATIO) + 4);
        } else {
            return mOriginalHeight + 4;
        }
    }


    /**
     * @brief Calculate element width flat
     */
    private int calculateElementWidthFlat() {
        if ((float)mOriginalWidth/(float)mOriginalHeight > CONST_ASPECT_RATIO) {
            return mOriginalWidth + 4;
        } else {
            return Math.round(((float)mOriginalHeight * CONST_ASPECT_RATIO) + 4);
        }
    }


    /**
     * @brief Calculate element height haptic
     */
    protected int calculateElementHeightHaptic() {
        float height;
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        if ((float)mOriginalWidth / (float)mOriginalHeight > CONST_ASPECT_RATIO) {
            height =  (float)mOriginalWidth / CONST_ASPECT_RATIO;
        } else {
            height =  mOriginalHeight;
        }

        return Math.round(height / (28.0f / 29.0f)) + 2 * shadowWidth;
    }


    /**
     * @brief Calculate element width haptic
     */
    protected int calculateElementWidthHaptic() {
        float width;
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        if ((float)mOriginalWidth / (float)mOriginalHeight > CONST_ASPECT_RATIO) {
            width = mOriginalWidth;
        } else {
            width = (float)mOriginalHeight * CONST_ASPECT_RATIO;
        }

        return Math.round(width / (19.5f / 20.0f)) + 2 * shadowWidth;
    }




//----------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set width of the element.
     *
     * Convenience function.
     *
     * @param width The new width of the element.
     */
    @Override
    public void setLayoutWidth(int width) {
        setLayoutSize(new Point(width, Math.round((float) width / CONST_ASPECT_RATIO)));
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
        setLayoutSize(new Point(Math.round(height * CONST_ASPECT_RATIO), height));
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
     * @param bounds Available space
     * @return  Rect with correct aspect ratio, fitting in available space
     */
    public Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds;

        //calculate size, based on aspect ratio
        if ((float)bounds.width() / (float)bounds.height() > CONST_ASPECT_RATIO) {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(((float)newBounds.height() * CONST_ASPECT_RATIO));

            if (mMiddleAligned) {
                int horizontalShift = (bounds.width()-newBounds.width())/2;
                newBounds.left += horizontalShift;
                newBounds.right += horizontalShift;
            }

        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.bottom = newBounds.top + Math.round((float)newBounds.width() / CONST_ASPECT_RATIO);

            if (mMiddleAligned) {
                int verticalShift = (bounds.height()-newBounds.height())/2;
                newBounds.top += verticalShift;
                newBounds.bottom += verticalShift;
            }
        }

        return newBounds;
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
        mFilmMetaphorImage.setLayoutSize(bounds.width(), bounds.height());
        mFilmMetaphorImage.setLayoutOffset(bounds.left, bounds.top);
    }

}
