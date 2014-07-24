/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.boxes;


import android.graphics.Rect;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBorderLine;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShape;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEViewWrapper;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableArea
//----------------------------------------------------------------------------------------------------------------------


public class PDEDrawableArea extends PDEDrawableMultilayer {

    //-----  properties ---------------------------------------------------------------------------------------------------
    protected PDEDrawableBorderLine mElementBorderLine;
    protected PDEDrawableShape mElementBackground;

    protected PDEColor mElementBackgroundColor;
    protected PDEColor mElementBorderColor;

    protected PDEViewWrapper mWrapperView;


//----- init -----------------------------------------------------------------------------------------------------------


    /**
     * @brief Constructor
     */
    public PDEDrawableArea() {
        mElementBackgroundColor = PDEColor.valueOf("DTWhite");
        mElementBorderColor = PDEColor.valueOf("DTUIBorder");

        // Todo: Shape path

        // init sub layers
        initLayers();

        // init borderline
        mElementBorderLine.setElementBorderColor(mElementBorderColor.getIntegerColor());
        mElementBorderLine.setElementBorderWidth(1.0f);
        mElementBorderLine.setElementShapeRect();
        // init background
        mElementBackground.setElementBackgroundColor(mElementBackgroundColor.getIntegerColor());
        mElementBackground.setElementShapeRect();
        mWrapperView = null;
    }


    // internal helpers
    protected void initLayers() {
        // initialize sublayers
        mElementBorderLine = new PDEDrawableBorderLine();
        mElementBackground = new PDEDrawableShape();

        // hang in layers
        addLayer(mElementBackground);
        addLayer(mElementBorderLine);
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Get the background color.
     *
     * @return background color
     */
    public PDEColor getElementBackgroundColor() {
        return mElementBackgroundColor;
    }


    /**
     * @brief Set a new background color.
     *
     * @param color new background color
     */
    public void setElementBackgroundColor(PDEColor color) {
        // anything to do?
        if (color.getIntegerColor() == mElementBackgroundColor.getIntegerColor()) return;

        // remember
        mElementBackgroundColor = color;

        // forward
        mElementBackground.setElementBackgroundColor(mElementBackgroundColor.getIntegerColor());
    }


    /**
     * @brief Get border color.
     *
     * @return border color
     */
    public PDEColor getElementBorderColor() {
        return mElementBorderColor;
    }


    /**
     * @brief Set new border color.
     *
     * @param color new border color
     */
    public void setElementBorderColor(PDEColor color) {
        // anything to do?
        if (color.getIntegerColor() == mElementBorderColor.getIntegerColor()) return;

        // remember
        mElementBorderColor = color;

        // forward
        mElementBorderLine.setElementBorderColor(mElementBorderColor.getIntegerColor());
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief calculates and applies new layout values.
     */
    @Override
    protected void doLayout() {
        Rect bounds;

        bounds = getBounds();
        updateBackgroundDrawable(bounds);
        updateBorderLine(bounds);
    }


    /**
     * @brief update the backgroundLayer when the bounds change.
     *
     * @param bounds new bounds
     */
    protected void updateBackgroundDrawable(Rect bounds) {
        mElementBackground.setBounds(bounds);
    }


    /**
     * @brief update the border line when the bounds change.
     *
     * @param bounds new bounds
     */
    protected void updateBorderLine(Rect bounds) {
        mElementBorderLine.setBounds(bounds);
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Layout ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    // ToDo: setElementShapePath


    /**
     * @brief Set the shape of the layer to a rectangle with the given size.
     *
     */
    public void setElementShapeRect() {
        // config background
        mElementBackground.setElementShapeRect();
        // config borderline
        mElementBorderLine.setElementShapeRect();
    }


    /**
     * @brief Set the shape of the sunken layer to a rounded rectangle.
     *
     * @param cornerRadius radius of the rounded corners
     */
    public void setElementShapeRoundedRect(float cornerRadius) {
        // background
        mElementBackground.setElementShapeRoundedRect(cornerRadius);
        // border line
        mElementBorderLine.setElementShapeRoundedRect(cornerRadius);
    }


    /**
     * @brief Set the shape of the sunken layer to an oval.
     *
     */
    public void setElementShapeOval() {
        // background
        mElementBackground.setElementShapeOval();
        // border line
        mElementBorderLine.setElementShapeOval();
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Wrapper View  ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    public PDEViewWrapper getWrapperView() {
        if (mWrapperView == null) {
            mWrapperView = new PDEViewWrapper(PDECodeLibrary.getInstance().getApplicationContext(), this);
        }
        return mWrapperView;
    }

}
