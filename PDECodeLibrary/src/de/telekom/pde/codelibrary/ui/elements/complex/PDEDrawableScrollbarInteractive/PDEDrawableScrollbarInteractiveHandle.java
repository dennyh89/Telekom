/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableScrollbarInteractive;


//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableScrollbarInteractiveHandle
//----------------------------------------------------------------------------------------------------------------------

import android.graphics.Point;
import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableDelimiter;


/**
 * @brief Graphics primitive - the handle for the interactive scrollbar.
 */
public class PDEDrawableScrollbarInteractiveHandle extends PDEDrawableMultilayer {

//-----  properties ---------------------------------------------------------------------------------------------------
    // type
    protected PDEDrawableScrollbarInteractive.PDEDrawableScrollbarInteractiveType mElementScrollbarType;
    // colors
    protected PDEColor mElementBackgroundColor;
    protected PDEColor mElementBorderColor;
    protected PDEColor mElementRiffleColor1;
    protected PDEColor mElementRiffleColor2;
    // corner radius
    protected float mElementCornerRadius;
    // sublayers
    private PDEDrawableRoundedBox mElementBackgroundDrawable;
    private PDEDrawableMultilayer mElementRiffleDrawable;


//----- init -----------------------------------------------------------------------------------------------------------

    /**
     * @brief Initialization.
     */
    public PDEDrawableScrollbarInteractiveHandle(){
        // init properties defaults
        mElementScrollbarType = PDEDrawableScrollbarInteractive.PDEDrawableScrollbarInteractiveType.Vertical;
        // init sublayers
        initLayers();
        // init layers to PDE defaults
        mElementBackgroundDrawable.setElementCornerRadius(PDEBuildingUnits.pixelFromBU(0.25f));
        mElementBackgroundDrawable.setElementBorderWidth(1.0f);
        setLightStyle();
    }


    /**
     * @brief internal initial helper to inti needed layers. shadow is not created here, it must
     *        inited by createShadow seperatly
     */
    private void initLayers() {
        // initialize sublayers
        mElementBackgroundDrawable = new PDEDrawableRoundedBox();
        mElementRiffleDrawable = new PDEDrawableMultilayer();

        for (int i=0; i<8; i++) {
            mElementRiffleDrawable.addLayer(new PDEDrawableDelimiter());
        }

        // hang in layers
        addLayer(mElementBackgroundDrawable);
        addLayer(mElementRiffleDrawable);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set fill (background) color.
     *
     * @param color The new backgroundcolor of the handle.
     */
    public void setElementBackgroundColor(PDEColor color) {
        // security
        if (mElementBackgroundDrawable == null ) {
            return;
        }
        // any change?
        if (color.getIntegerColor() == mElementBackgroundDrawable.getElementBackgroundColor().getIntegerColor()) return;

        // remember
        mElementBackgroundDrawable.setElementBackgroundColor(color);

        // update
        invalidateSelf();
    }


    /**
     * @brief Get background color.
     *
     * @return The color of the background.
     */
    public PDEColor getElementBackgroundColor() {
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
        if (mElementBackgroundDrawable == null ) {
            return;
        }
        // any change?
        if (color.getIntegerColor() == mElementBackgroundDrawable.getElementBorderColor().getIntegerColor()) return;

        // remember
        mElementBackgroundDrawable.setElementBorderColor(color);

        // update
        invalidateSelf();
    }


    /**
     * @brief Get outline color.
     *
     * @return The color of the outline.
     */
    public PDEColor getElementBorderColor() {
        if (mElementBackgroundDrawable == null) {
            return null;
        }
        return mElementBackgroundDrawable.getElementBorderColor();
    }


    /**
     * @brief Set corner radius.
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
    }


    /**
     * @brief Get corner radius
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
     * @brief Set riffle colors.
     */
    public void setElementRiffleColors(PDEColor riffleCol1, PDEColor riffleCol2) {
        // any change?
        if (riffleCol1 == mElementRiffleColor1 && riffleCol2 == mElementRiffleColor2) return;

        // remember
        mElementRiffleColor1 = riffleCol1;
        mElementRiffleColor2 = riffleCol2;

        // update
        doLayout();
    }


    /**
     * @brief Set light style.
     */
    public void setLightStyle() {
        PDEColor color1, color2;

        // lightstyle properties
        setElementBackgroundColor(PDEColor.valueOf("DTGrey4"));
        setElementBorderColor(PDEColor.valueOf("DTGrey6"));

        // riffle color 1 = black 10%
        color1 = PDEColor.valueOf("DTBlack");
        color1.setAlpha(0.1f);

        // riffle color 2 = white 20%
        color2 = PDEColor.valueOf("DTWhite");
        color2.setAlpha(0.2f);

        setElementRiffleColors(color1,color2);
    }


    /**
     * @brief Set dark style.
     */
    public void setDarkStyle() {
        PDEColor color1, color2;

        // lightstyle properties
        setElementBackgroundColor(PDEColor.valueOf("DTGrey100"));
        setElementBorderColor(PDEColor.valueOf("DTGrey50"));

        // riffle color 1 = black 20%
        color1 = PDEColor.valueOf("DTBlack");
        color1.setAlpha(0.2f);

        // riffle color 2 = white 20%
        color2 = PDEColor.valueOf("DTWhite");
        color2.setAlpha(0.2f);

        setElementRiffleColors(color1,color2);
    }


    /**
     * @brief Set type / orientation of the scrollbar.
     *
     * @param type new type of the scrollbar.
     */
    public void setElementScrollbarType(PDEDrawableScrollbarInteractive.PDEDrawableScrollbarInteractiveType type) {
        // only allow valid types
        if (mElementScrollbarType != PDEDrawableScrollbarInteractive.PDEDrawableScrollbarInteractiveType
                .Horizontal &&
                mElementScrollbarType != PDEDrawableScrollbarInteractive.PDEDrawableScrollbarInteractiveType
                        .Vertical) return;
        // any change?
        if (type == mElementScrollbarType) {
            return;
        }

        // remember
        mElementScrollbarType = type;

        doLayout();
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------



    /**
     * @brief update function.
     */
    protected void doLayout(){
        // get current bounds
        Rect bounds = getBounds();

        // update sublayers
        updateBackgroundDrawable(bounds);
        updateRiffleDrawable(bounds);
    }


    /**
     * @brief Updates the background drawable.
     *
     * @param bounds The new bounds of the background.
     */
    private void updateBackgroundDrawable(Rect bounds) {
        mElementBackgroundDrawable.setBounds(new Rect(0,0,bounds.width(),bounds.height()));
    }


    /**
     * @brief Updates the riffle drawable.
     *
     * @param bounds the new bounds.
     */
    private void updateRiffleDrawable(Rect bounds) {
        Rect nr;
        int ypos = 0;
        int index = 0;
        float width,height;
        Point position;

        // init
        width = 0;
        height = 0;

        // set layout properties
        if (mElementScrollbarType == PDEDrawableScrollbarInteractive.PDEDrawableScrollbarInteractiveType
                .Vertical) {
            width = bounds.width()-PDEBuildingUnits.twoThirdsBU();
            height = 11.0f;
            nr = new Rect(0,0,Math.round(width),1);
        } else {
            width = 11.0f;
            height = bounds.height() - PDEBuildingUnits.twoThirdsBU();
            nr = new Rect(0,0,1,Math.round(height));
        }

        // center riffle in me
        position = new Point(Math.round(bounds.width() / 2.0f - width / 2.0f),
                             Math.round(bounds.height() / 2.0f - height / 2.0f));
        mElementRiffleDrawable.setBounds(position.x, position.y, position.x + Math.round(width),
                                         position.y + Math.round(height));

        // draw lines
        for (int u = 0; u < mElementRiffleDrawable.getNumberOfLayers(); u++) {
            PDEDrawableDelimiter l = (PDEDrawableDelimiter) mElementRiffleDrawable.getLayerAtIndex(u);

            if (mElementScrollbarType == PDEDrawableScrollbarInteractive.PDEDrawableScrollbarInteractiveType.Vertical){
                l.setElementType(PDEDrawableDelimiter.PDEDrawableDelimiterType.PDEDrawableDelimiterTypeHorizontal);
                l.setLayoutSize(new Point(nr.width(), nr.height()));
                l.setLayoutOffset(new Point(0, ypos));
            } else {
                l.setElementType(PDEDrawableDelimiter.PDEDrawableDelimiterType.PDEDrawableDelimiterTypeVertical);
                l.setLayoutSize(new Point(nr.width(), nr.height()));
                l.setLayoutOffset(new Point(ypos, 0));
            }

            // set color
            if (index == 0 || index == 3 || index == 6 || index == 9) {
                l.setElementBackgroundColor(mElementRiffleColor1);
            }
            if (index == 1 || index == 4 || index == 7 || index == 10) {
                l.setElementBackgroundColor(mElementRiffleColor2);
                ypos++;
                index++;
            }
            ypos++;
            index++;
        }

    }
}
