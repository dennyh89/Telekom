/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.complex;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableScrollBarIndicative
//----------------------------------------------------------------------------------------------------------------------


import android.graphics.Point;
import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEViewWrapper;


/**
 * @brief Graphics primitive - a noninteractive scrollbar.
 *
 * A scrollbar that indicates the current position within a scrollable content.
 * The user can't interact with the scrollbar, it only shows the position.
 * Technically this is a multilayer that arranges two rounded boxes skillfully.
 */
public class PDEDrawableScrollBarIndicative extends PDEDrawableMultilayer {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawableScrollBarIndicative.class.getName();

//----- constants ----------------------------------------------------------------------------------------------------
    public enum PDEDrawableScrollbarIndicativeType {
        PDEDrawableScrollbarIndicativeTypeHorizontal,
        PDEDrawableScrollbarIndicativeTypeVertical
    }


//-----  properties ---------------------------------------------------------------------------------------------------

    // scrolling sizes / positions
    protected float mElementScrollPos;
    protected float mElementScrollContentSize;
    protected float mElementScrollPageSize;

    // type
    protected PDEDrawableScrollbarIndicativeType mElementScrollbarType;

    // sublayers
    private PDEDrawableRoundedBox mElementBackgroundDrawable;
    private PDEDrawableRoundedBox mElementIndicatorDrawable;

    // wrapper view
    protected PDEViewWrapper mWrapperView;



//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    public PDEDrawableScrollBarIndicative() {
        // default type
        mElementScrollbarType = PDEDrawableScrollbarIndicativeType.PDEDrawableScrollbarIndicativeTypeVertical;
        // init
        mElementBackgroundDrawable = null;
        mElementIndicatorDrawable = null;
        mElementScrollContentSize = 200;
        mElementScrollPageSize = 100;
        mElementScrollPos = 50;

        // init sublayers
        initLayers();

        // init PDE defaults
        if (mElementIndicatorDrawable != null && mElementBackgroundDrawable != null) {
            // background
            mElementBackgroundDrawable.setElementBackgroundColor(PDEColor.valueOf("DTWhite"));
//            mElementBackgroundDrawable.setElementBorderColor(PDEColor.valueOf("DTGrey6"));
            mElementBackgroundDrawable.setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
            mElementBackgroundDrawable.setElementCornerRadius(PDEBuildingUnits.pixelFromBU(0.25f));
            mElementBackgroundDrawable.setElementBorderWidth(1.0f);
            // indicator
            mElementIndicatorDrawable.setElementBackgroundColor(PDEColor.valueOf("DTGrey1"));
            PDEColor color = new PDEColor(mElementIndicatorDrawable.getElementBackgroundColor());
            color.setAlpha(0.9f);
            mElementIndicatorDrawable.setElementBorderColor(color);
            mElementIndicatorDrawable.setElementCornerRadius(mElementBackgroundDrawable.getElementCornerRadius());
            mElementIndicatorDrawable.setElementBorderWidth(1.0f);
        }


        mWrapperView = null;
    }


    // internal helpers
    private void initLayers() {
        // initialize sublayers
        mElementBackgroundDrawable = new PDEDrawableRoundedBox();
        mElementIndicatorDrawable = new PDEDrawableRoundedBox();

        // TODO offset???

        // hang in layers
        addLayer(mElementBackgroundDrawable);
        addLayer(mElementIndicatorDrawable);
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set fill (background) color.
     *
     * It's the background color for the bar, not for the indicator.
     *
     * @param color The new background color.
     */
    public void setElementBackgroundColor(PDEColor color) {
        // security
        if (mElementBackgroundDrawable == null) {
            return;
        }
        // forward
        mElementBackgroundDrawable.setElementBackgroundColor(color);
    }


    /**
     * @brief Get background color of the scrollbar.
     *
     * @return The color of the background.
     */
    public PDEColor getElementBackgroundColor() {
        // security
        if (mElementBackgroundDrawable == null) {
            return null;
        }
        return mElementBackgroundDrawable.getElementBackgroundColor();
    }


    /**
     * @brief Set border color.
     *
     * @param color The new color of the outline.
     */
    public void setElementBorderColor(PDEColor color) {
        // security
        if (mElementBackgroundDrawable == null) {
            return;
        }
        // forward
        mElementBackgroundDrawable.setElementBorderColor(color);
    }


    /**
     * @brief Get outline color.
     *
     * @return The color of the outline.
     */
    public PDEColor getElementBorderColor() {
        // security
        if (mElementBackgroundDrawable == null) {
            return null;
        }
        return mElementBackgroundDrawable.getElementBorderColor();
    }


    /**
     * @brief Set corner radius.
     *
     * With this function the corner radius for the bar and the indicator is set at once.
     *
     * @param radius The new radius of the rounded corners.
     */
    public void setElementCornerRadius(float radius) {
        // security
        if (mElementBackgroundDrawable == null || mElementIndicatorDrawable == null) {
            return;
        }
        // forward
        mElementBackgroundDrawable.setElementCornerRadius(radius);
        // indicator has the same radius
        mElementIndicatorDrawable.setElementCornerRadius(radius);
    }


    /**
     * @brief Get corner radius of bar & indicator.
     *
     * @return The radius of the rounded corners.
     */
    public float getElementCornerRadius() {
        // security
        if (mElementBackgroundDrawable == null) {
            return 0;
        }
        return mElementBackgroundDrawable.getElementCornerRadius();
    }

    /**
     * @brief Set color of the indicator.
     *
     * This also determines the color of the indicator outline.
     *
     * @param color The new indicator color.
     */
    public void setElementScrollValueIndicatorColor(PDEColor color) {
        PDEColor color2 = new PDEColor(color);
        // security
        if (mElementIndicatorDrawable == null) {
            return;
        }
        // forward
        mElementIndicatorDrawable.setElementBackgroundColor(color);
        // set border same color as background, but with 0.9 alpha
        color2.setAlpha(0.9f);
        mElementIndicatorDrawable.setElementBorderColor(color2);
    }


    /**
     * @brief Get color of the indicator.
     *
     * @return The color of the indicator.
     */
    @SuppressWarnings("unused")
    public PDEColor getElementScrollValueIndicatorColor() {
        // security
        if (mElementIndicatorDrawable == null) {
            return null;
        }
        return mElementIndicatorDrawable.getElementBackgroundColor();
    }


    /**
     * @brief Set type / orientation of the scrollbar.
     *
     * @param type new type of the scrollbar.
     */
    public void setElementScrollbarType(PDEDrawableScrollbarIndicativeType type) {
        // any change?
        if (type == mElementScrollbarType) {
            return;
        }

        // remember
        mElementScrollbarType = type;

        doLayout();
    }


    /**
     * @brief Get the rect of the indicator.
     */
    public Rect getIndicatorFrame() {
        return mElementIndicatorDrawable.getLayoutRect();
    }

//---------------------------------------------------------------------------------------------------------------------
// ----- scroll sizes / positions ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief setter function to set the content scroll range value
     */
    public void setElementScrollContentSize(float val) {
        // changed?
        if (mElementScrollContentSize == val) {
            return;
        }
        // remember
        mElementScrollContentSize = val;
        // perform the layout
        doLayout();
    }


    /**
     * @brief setter function to set the page size.
     *
     * @param size The new page size.
     */
    public void setElementScrollPageSize(float size) {
        // any change?
        if (size == mElementScrollPageSize) {
            return;
        }
        // remember
        mElementScrollPageSize = size;
        // perform the layout
        doLayout();
    }

    /**
     * @brief function to set the current progress value
     *
     * @param pos the new position of the indicator.
     */
    public void setElementScrollPos(float pos) {
        // any change?
        if (pos == mElementScrollPos) {
            return;
        }
        // remember
        mElementScrollPos = pos;
        // perform the layout
        doLayout();
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
        // ignore if our type is vertical
        if (mElementScrollbarType == PDEDrawableScrollbarIndicativeType.PDEDrawableScrollbarIndicativeTypeHorizontal) {
            // anything to do?
            if (width == getBounds().width()) {
                return;
            }
            setLayoutSize(new Point(width, getBounds().height()));
        }
    }


    /**
     * @brief Set height of the element.
     *
     * Convenience function.
     *
     * @param height The new height of the element.
     */
    public void setLayoutHeight(int height) {
        // ignore if our type is horizontal
        if (mElementScrollbarType == PDEDrawableScrollbarIndicativeType.PDEDrawableScrollbarIndicativeTypeVertical) {
            // anything to do?
            if (height == getBounds().height()) {
                return;
            }
            setLayoutSize(new Point(getBounds().width(), height));
        }
    }


    /**
     * @brief calculates and applies new layout values.
     */
    protected void doLayout() {
        Rect bounds;

        // get current bounds
        bounds = getBounds();
        // update sublayers
        updateBackgroundDrawable(bounds);
        updateIndicatorDrawable(bounds);
    }


    /**
     * @brief update the backgroundLayer when the bounds change.
     *
     * @param bounds new bounds
     */
    private void updateBackgroundDrawable(Rect bounds) {
        mElementBackgroundDrawable.setBounds(bounds);
    }


    /**
     * @brief update the indicatorLayer
     * @param bounds new bounds
     */
    @SuppressWarnings("unused")
    private void updateIndicatorDrawable(Rect bounds) {
        Point pos;
        float fullSize, newSize, newPos, width, height;
        Rect frame;

        // init
        width = 0;
        height = 0;

        // division by zero check
        if (mElementScrollContentSize == 0) {
            return;
        }

        // calc new indicator size
        if (mElementScrollbarType == PDEDrawableScrollbarIndicativeType.PDEDrawableScrollbarIndicativeTypeVertical) {
            fullSize = mElementBackgroundDrawable.getBounds().height();
        } else if (mElementScrollbarType ==
                   PDEDrawableScrollbarIndicativeType.PDEDrawableScrollbarIndicativeTypeHorizontal) {
            fullSize = mElementBackgroundDrawable.getBounds().width();
        } else {
            return;
        }

        newSize = mElementScrollPageSize * fullSize / mElementScrollContentSize;

        // set size
        if (mElementScrollbarType == PDEDrawableScrollbarIndicativeType.PDEDrawableScrollbarIndicativeTypeVertical) {
            width = mElementBackgroundDrawable.getBounds().width();
            height = newSize;
        } else {
            width = newSize;
            height = mElementBackgroundDrawable.getBounds().height();
        }

        // calc offset
        newPos = mElementScrollPos * fullSize / mElementScrollContentSize;

        if (mElementScrollbarType == PDEDrawableScrollbarIndicativeType.PDEDrawableScrollbarIndicativeTypeVertical) {
            pos = new Point(0, Math.round(newPos));
        } else {
            pos = new Point(Math.round(newPos), 0);
        }

        // build new frame rect
        frame = new Rect(pos.x, pos.y, pos.x + Math.round(width), pos.y + Math.round(height));
        // set as new bounds
        mElementIndicatorDrawable.setBounds(frame);
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Wrapper View  ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    public PDEViewWrapper getWrapperView() {
        if (mWrapperView == null) {
           mWrapperView = new PDEViewWrapper(PDECodeLibrary.getInstance().getApplicationContext(),this);
        }
       return mWrapperView;
    }
}
