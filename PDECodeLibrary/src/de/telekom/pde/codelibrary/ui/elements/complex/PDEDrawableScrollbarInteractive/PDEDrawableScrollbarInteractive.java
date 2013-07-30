/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableScrollbarInteractive;

import android.graphics.Point;
import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedInnerShadow;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableScrollbarInteractive
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Graphics primitive - an interactive scrollbar.
 */
public class PDEDrawableScrollbarInteractive extends PDEDrawableMultilayer {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawableScrollbarInteractive.class.getName();

//----- constants ----------------------------------------------------------------------------------------------------
    public enum PDEDrawableScrollbarInteractiveType {
        Horizontal,
        Vertical
    }


//-----  properties ---------------------------------------------------------------------------------------------------

    // scrolling sizes / positions
    protected float mElementScrollPos;
    protected float mElementScrollContentSize;
    protected float mElementScrollPageSize;
    // type
    protected PDEDrawableScrollbarInteractiveType mElementScrollbarType;
    // sub layers
    private PDEDrawableRoundedBox mElementBackgroundDrawable;
    private PDEDrawableScrollbarInteractiveHandle mElementHandleDrawable;
    private PDEDrawableShapedShadow mElementShadowDrawable;
    private PDEDrawableShapedInnerShadow mElementInnerShadowDrawable;


//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    public PDEDrawableScrollbarInteractive() {
        // default type
        mElementScrollbarType = PDEDrawableScrollbarInteractiveType.Vertical;
        // init
        mElementBackgroundDrawable = null;
        mElementScrollContentSize = 200;
        mElementScrollPageSize = 100;
        mElementScrollPos = 50;

        // init sub layers
        initLayers();

        // init PDE defaults
        if (mElementBackgroundDrawable != null) {
            // background
            mElementBackgroundDrawable.setElementBackgroundColor(PDEColor.valueOf("DTWhite"));
            mElementBackgroundDrawable.setElementBorderColor(PDEColor.valueOf("DTGrey6"));
            mElementBackgroundDrawable.setElementCornerRadius(PDEBuildingUnits.pixelFromBU(0.25f));
            mElementBackgroundDrawable.setElementBorderWidth(1.0f);
        }
    }


    /**
     * @brief Init sub layers.
     */
    private void initLayers() {
        // initialize sublayers
        // background
        mElementBackgroundDrawable = new PDEDrawableRoundedBox();
        // handle
        mElementHandleDrawable = new PDEDrawableScrollbarInteractiveHandle();
        mElementHandleDrawable.setElementScrollbarType(mElementScrollbarType);
        // outer shadow
        mElementShadowDrawable = new PDEDrawableShapedShadow();
        mElementShadowDrawable.setElementShapeRoundedRect(mElementBackgroundDrawable.getElementCornerRadius());
        mElementShadowDrawable.setElementShapeOpacity(0.2f);
        // toDo mElementShadowDrawable.setElementOffset(new Point(0,PDEBuildingUnits.oneTwelthsBU));

        // inner shadow
        mElementInnerShadowDrawable = new PDEDrawableShapedInnerShadow();
        mElementInnerShadowDrawable.setLayoutRect(new Rect(0,0,0,0));
        mElementInnerShadowDrawable.setElementShapeRoundedRect(mElementBackgroundDrawable.getElementCornerRadius());

        // hang in sublayers
        addLayer(mElementBackgroundDrawable);
        addLayer(mElementInnerShadowDrawable);
        addLayer(mElementShadowDrawable);
        addLayer(mElementHandleDrawable);
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set fill (background) color.
     *
     * It's the background color for the bar, not for the handle.
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
     * @brief Set all colors needed for the handle.
     *
     * With this function all the colors of the handle can be set.
     * The riffle on the handle is constructed by using two different colors.
     * Normally one of them is dark and one is light to yield a kind of spatial effect.
     *
     * @param bg The background color of the handle.
     * @param border The border color of the handle.
     * @param riffleCol1 The first color of the riffle.
     * @param riffleCol2 The second color of the riffle.
     */
     public void setElementHandleColors(PDEColor bg, PDEColor border, PDEColor riffleCol1, PDEColor riffleCol2) {
         // set handle colors
         mElementHandleDrawable.setElementBackgroundColor(bg);
         mElementHandleDrawable.setElementBorderColor(border);
         mElementHandleDrawable.setElementRiffleColors(riffleCol1,riffleCol2);
     }


    /**
     * @brief Set corner radius.
     *
     * With this function the corner radius for the bar and the handle is set at once.
     *
     * @param radius The new radius of the rounded corners.
     */
    public void setElementCornerRadius(float radius) {
        // security
        if (mElementBackgroundDrawable == null ) {
            return;
        }
        // forward
        mElementBackgroundDrawable.setElementCornerRadius(radius);
        mElementHandleDrawable.setElementCornerRadius(radius);
    }


    /**
     * @brief Get corner radius of bar & handle.
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
     * @brief Set type / orientation of the scrollbar.
     *
     * @param type new type of the scrollbar.
     */
    public void setElementScrollbarType(PDEDrawableScrollbarInteractiveType type) {
        // valid value?
        if (type != PDEDrawableScrollbarInteractiveType.Horizontal &&
                type != PDEDrawableScrollbarInteractiveType.Vertical) return;

        // any change?
        if (type == mElementScrollbarType) {
            return;
        }

        // remember
        mElementScrollbarType = type;
        mElementHandleDrawable.setElementScrollbarType(type);

        // update
        doLayout();
    }


    /**
     * @brief Set all colors of the scrollbar to the light style.
     */
    public void setLightStyle() {
        setElementBackgroundColor(PDEColor.valueOf("DTWhite"));
        setElementBackgroundColor(PDEColor.valueOf("DTGrey6"));

        // set handle light style
        mElementHandleDrawable.setLightStyle();
    }

    /**
     * @brief Set all colors of the scrollbar to the dark style.
     */
    public void setDarkStyle() {
        PDEColor color;

        // background black with alpha 10%
        color = PDEColor.valueOf("DTBlack");
        color.setAlpha(0.1f);
        setElementBackgroundColor(color);

        setElementBorderColor(PDEColor.valueOf("DTGrey50"));

        // set handle dark style
        mElementHandleDrawable.setDarkStyle();
    }





//---------------------------------------------------------------------------------------------------------------------
// ----- scroll sizes / positions ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief setter function to set the content scroll range value.
     *
     * @param val new content size.
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

        invalidateSelf();
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
        if (mElementScrollbarType == PDEDrawableScrollbarInteractiveType.Horizontal) {
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
        if (mElementScrollbarType == PDEDrawableScrollbarInteractiveType.Vertical) {
            // anything to do?
            if (height == getBounds().height()) {
                return;
            }
            setLayoutSize(new Point(getBounds().width(), height));
        }
    }




    /**
     * @brief Calculates and applies new layout values.
     */
    protected void doLayout() {
        Rect bounds;
        // get current bounds
        bounds = getBounds();
        // update background
        updateBackgroundDrawable(bounds);
        // update handle
        updateHandleDrawable(bounds);
    }


    /**
     * @brief update the backgroundLayer when the bounds change.
     *
     * @param bounds new bounds
     */
    private void updateBackgroundDrawable(Rect bounds) {
        Rect shadowRect = new Rect(Math.round(bounds.left + 1), Math.round(bounds.top + 1),
                                   Math.round(bounds.right - 1), Math.round(bounds.bottom - 1));
        mElementBackgroundDrawable.setBounds(bounds);
        mElementInnerShadowDrawable.setLayoutRect(shadowRect);
        mElementInnerShadowDrawable.setElementShapeRoundedRect(mElementBackgroundDrawable.getElementCornerRadius() -
                                                               1.0f);
    }


    /**
     * @brief update the indicatorLayer
     * @param bounds new bounds
     */
    private void updateHandleDrawable(Rect bounds) {
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
        if (mElementScrollbarType == PDEDrawableScrollbarInteractiveType.Vertical) {
            fullSize = mElementBackgroundDrawable.getBounds().height();
        } else if (mElementScrollbarType ==
                   PDEDrawableScrollbarInteractiveType.Horizontal) {
            fullSize = mElementBackgroundDrawable.getBounds().width();
        } else {
            return;
        }

        newSize = mElementScrollPageSize * fullSize / mElementScrollContentSize;

        // set size
        if (mElementScrollbarType == PDEDrawableScrollbarInteractiveType.Vertical) {
            // vertical scrollbar
            width = mElementBackgroundDrawable.getBounds().width();
            height = newSize;
        } else {
            // horizontal scrollbar
            width = newSize;
            height = mElementBackgroundDrawable.getBounds().height();
        }

        // calc offset
        newPos = mElementScrollPos * fullSize / mElementScrollContentSize;

        if (mElementScrollbarType == PDEDrawableScrollbarInteractiveType.Vertical) {
            pos = new Point(0, Math.round(newPos));
        } else {
            pos = new Point(Math.round(newPos), 0);
        }

        // build new frame rect
        frame = new Rect(pos.x, pos.y, pos.x + Math.round(width), pos.y + Math.round(height));
        // set as new bounds
        mElementHandleDrawable.setBounds(frame);

        // todo shadow with setBounds
//        mElementShadowDrawable.setBounds(new Rect(frame.left,frame.top+PDEBuildingUnits.oneTwelfthsBU(),
//                                                  frame.right,frame.bottom+PDEBuildingUnits.oneTwelfthsBU()));
        mElementShadowDrawable.setLayoutRect(new Rect(frame.left,frame.top+PDEBuildingUnits.oneTwelfthsBU(),
                                                      frame.width(),frame.height()));
        mElementShadowDrawable.setElementShapeRoundedRect(mElementBackgroundDrawable.getElementCornerRadius());
    }
}
