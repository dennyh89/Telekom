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
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableVideoMetaphor
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Video Scene with black Bars and Time
 */
public class PDEDrawableVideoMetaphor extends PDEDrawableMultilayer {

//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEDrawableVideoMetaphorImage mVideoMetaphorImage;
    private PDEConstants.PDEContentStyle mStyle;

    private Drawable mScene;
    private boolean  m169Format;
    private boolean mMiddleAligned;
    private int mOriginalHeight;
    private int mOriginalWidth;
    private boolean mShadowEnabled;
    private PDEDrawableShapedShadow mElementShadowDrawable;


//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     */
    public PDEDrawableVideoMetaphor(Drawable drawable, String timeString) {
        // init drawable basics
        super();

        // init
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        mScene = drawable;
        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mScene != null) {
            mOriginalHeight = mScene.getIntrinsicHeight();
            mOriginalWidth = mScene.getIntrinsicWidth();
        }

        m169Format = true;
        mMiddleAligned = false;

        // shadow is created on demand
        mElementShadowDrawable = null;
        mShadowEnabled = false;

        mVideoMetaphorImage = new PDEDrawableVideoMetaphorImage(drawable, timeString);
        addLayer(mVideoMetaphorImage);
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
            mElementShadowDrawable = mVideoMetaphorImage.createElementShadow();
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
        mVideoMetaphorImage.setElementContentStyle(style);

        if (mShadowEnabled
                && mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat
                && mElementShadowDrawable != null) {
            removeLayer(mElementShadowDrawable);
            mElementShadowDrawable = null;
        }

        if (mShadowEnabled
                && mStyle == PDEConstants.PDEContentStyle.PDEContentStyleHaptic
                && mElementShadowDrawable == null) {
            mElementShadowDrawable = mVideoMetaphorImage.createElementShadow();
            insertLayerAtIndex(mElementShadowDrawable, 0);
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
     * @brief Set Scene
     */
    public void setElementScene(Drawable scene) {
        //any change?
        if (scene == mScene) return;

        //remember
        mScene = scene;

        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mScene != null) {
            mOriginalHeight = mScene.getIntrinsicHeight();
            mOriginalWidth = mScene.getIntrinsicWidth();
        }

        //redraw
        mVideoMetaphorImage.setElementScene(scene);
    }


    /**
     * @brief Get Scene
     */
    public Drawable getElementScene() {
        return mScene;
    }


    /**
     * @brief Set Time String
     *
     * @param ts The time string in the lower right corner
     */
    public void setElementTimeString(String ts) {
        mVideoMetaphorImage.setElementTimeString(ts);
    }


    /**
     * @brief Get Time String
     *
     * @return the time string of the lower right corner
     */
    @SuppressWarnings("unused")
    public String getElementTimeString() {
        return mVideoMetaphorImage.getElementTimeString();
    }


    /**
     * @brief Set if Format is 16:9, is false it is 1:1
     */
    public void setElementFormat169(boolean f169) {
        //any change?
        if (m169Format == f169) return;
        //remember
        m169Format = f169;
        // set current bounds again to update
        setBounds(getBounds());
        mVideoMetaphorImage.setElementFormat169(f169);

        //redraw
        doLayout();
    }


    /**
     * @brief Get if Format is 16:9
     */
    @SuppressWarnings("unused")
    public boolean getElementFormat169() {
        return m169Format;
    }


    /**
     * @brief Set darkstyle color
     */
    public void setElementDarkStyle(boolean isDarkStyle) {
        mVideoMetaphorImage.setElementDarkStyle(isDarkStyle);
    }


    /**
     * @brief Get if element is colored in dark style
     */
    @SuppressWarnings("unused")
    public boolean getElementDarkStyle() {
        return mVideoMetaphorImage.getElementDarkStyle();
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
    }


    /**
     * @brief helper function to get aspect ratio
     */
    private float getElementAspectRatio() {
        if (m169Format) {
            return 16.0f / 9.0f;
        } else {
            return 1.0f;
        }
    }


    /**
     * @brief Helper function to get intrinsic size of the picture
     */
    public Point getNativeSize() {
        if (mScene != null) return new Point(mScene.getIntrinsicWidth(), mScene.getIntrinsicHeight());
        return new Point(0, 0);
    }


    /**
     * @brief Helper function to test if photo frame has a picture in it
     */
    public boolean hasPicture() {
        return mScene != null;
    }

    /**
     * @brief Get element height.
     */
    public int getElementHeight() {
        if (mOriginalHeight >= 0) {
            int shadowWidth = 0;
            if (mShadowEnabled) {
                shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
            }

            if ((float)mOriginalWidth/(float)mOriginalHeight > getElementAspectRatio()) {
                return Math.round(((float)mOriginalWidth / getElementAspectRatio()) + 4 + 2 * shadowWidth);
            } else {
                return mOriginalHeight + 4 + 2 * shadowWidth;
            }
        }

        return 0;
    }


    /**
     * @brief Get element width.
     */
    public int getElementWidth() {

        if (mOriginalWidth >= 0) {
            int shadowWidth = 0;
            if (mShadowEnabled) {
                shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
            }

            if ((float)mOriginalWidth/(float)mOriginalHeight > getElementAspectRatio()) {
                return mOriginalWidth + 4 + 2*shadowWidth;
            } else {
                return Math.round((float)mOriginalHeight * getElementAspectRatio()) + 4 + 2*shadowWidth;
            }
        }

        return 0;
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
     * @param bounds Available space
     * @return Rect with correct aspect ratio, fitting in available space
     */
    public Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds;

        if ((float)bounds.width() / (float)bounds.height() > getElementAspectRatio() ) {
            newBounds = new Rect(bounds.left, bounds.top, 0, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getElementAspectRatio());

            if (mMiddleAligned) {
                int horizontalShift = (bounds.width() - newBounds.width()) / 2;
                newBounds.left += horizontalShift;
                newBounds.right += horizontalShift;
            }
        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getElementAspectRatio());

            if (mMiddleAligned) {
                int verticalShift = (bounds.height() - newBounds.height()) / 2;
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
        mVideoMetaphorImage.setLayoutSize(bounds.width(), bounds.height());
        mVideoMetaphorImage.setLayoutOffset(bounds.left, bounds.top);
    }


}
