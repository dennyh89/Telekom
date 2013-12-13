/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.boxes;


import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedInnerShadow;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableSunkenArea
//----------------------------------------------------------------------------------------------------------------------

@SuppressWarnings("unused")
public class PDEDrawableSunkenArea extends PDEDrawableArea {

    //-----  properties ---------------------------------------------------------------------------------------------------

    protected PDEDrawableShapedInnerShadow mElementInnerShadow;
    protected PDEColor mElementInnerShadowColor;

    protected float mElementInnerShadowOpacity;
    protected Point mElementInnerShadowOffset;
    protected float mElementInnerShadowBlurRadius;


//----- init -----------------------------------------------------------------------------------------------------------

    /**
     * @brief Constructor
     *
     *
     */
    public PDEDrawableSunkenArea() {
        mElementBackgroundColor = PDEColor.valueOf("DTWhite");
        mElementBorderColor = PDEColor.valueOf("DTUIBorder");
//        mElementInnerShadowColor = PDEColor.valueOf("DTBlack");
        mElementInnerShadowColor = PDEColor.valueOf("Black34Alpha");
        mElementInnerShadowOpacity = 1.0f;
        mElementInnerShadowBlurRadius = (float) PDEBuildingUnits.oneSixthBU();

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

        // init inner shadow
        mElementInnerShadowOffset = new Point(1,1);
        mElementInnerShadow.setElementShapeColor(mElementInnerShadowColor);
        // ToDo: check if this replacement is correct or should we use: setElementLightIncidenceOffset?
        //mElementInnerShadow.setShapeOffset(mElementInnerShadowOffset);
        mElementInnerShadow.setLayoutOffset(mElementInnerShadowOffset);
        mElementInnerShadow.setElementShapeRect();
        mElementInnerShadow.setElementShapeOpacity(mElementInnerShadowOpacity);
        mElementInnerShadow.setElementBlurRadius(mElementInnerShadowBlurRadius);


        //mElementInnerShadowOpacity = 0.24f;
        //mElementInnerShadowBlurRadius = (float) PDEBuildingUnits.oneFourthBU();
        //mElementInnerShadow.setElementBlurRadius(mElementInnerShadowBlurRadius);
        //mElementInnerShadow.setElementShapeOpacity(mElementInnerShadowOpacity);

        mWrapperView = null;
    }



    // internal helpers
    protected void initLayers() {
        // base class init
        super.initLayers();
        // initialize sublayers
        mElementInnerShadow = new PDEDrawableShapedInnerShadow();

        // hang in layers
        addLayer(mElementInnerShadow);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------



    /**
     * @brief Get inner shadow color.
     *
     * @return color of inner shadow.
     */
    public PDEColor getElementInnerShadowColor(){
        return mElementInnerShadowColor;
    }


    /**
     * @brief Set inner shadow color.
     *
     * @param color new color of inner shadow.
     */
    public void setElementInnerShadowColor(PDEColor color){
        // anything to do?
        if (color.getIntegerColor() == mElementInnerShadowColor.getIntegerColor()) return;
        // remember
        mElementInnerShadowColor = color;
        // forward
        mElementInnerShadow.setElementShapeColor(mElementInnerShadowColor);
    }


    /**
     * @brief Get opacity of inner shadow.
     *
     * @return opacity of inner shadow.
     */
    public float getElementInnerShadowOpacity(){
        return mElementInnerShadowOpacity;
    }


    /**
     * @brief Set opacity of inner shadow.
     *
     * @param opacity new opacity of inner shadow.
     */
    public void setElementInnerShadowOpacity(float opacity){
        // anything to do?
        if (mElementInnerShadowOpacity == opacity) return;
        // remember
        mElementInnerShadowOpacity = opacity;
        // forward
        mElementInnerShadow.setElementShapeOpacity(mElementInnerShadowOpacity);
    }


    /**
     * @brief Get offset of inner shadow.
     *
     * @return Offset of inner shadow.
     */
    public Point getElementInnerShadowOffset(){
        return mElementInnerShadowOffset;
    }


    /**
     * @brief Set offset of inner shadow.
     *
     * @param offset new offset of inner shadow.
     */
    public void setElementInnerShadowOffset(Point offset){
        // anything to do?
        if (mElementInnerShadowOffset.equals(offset.x,offset.y)) return;
        // remember
        mElementInnerShadowOffset = offset;
        // forward
        mElementInnerShadow.setLayoutOffset(mElementInnerShadowOffset);
    }


    public void setElementInnerShadowLightIncidenceOffset(PointF offset){
        mElementInnerShadow.setElementLightIncidenceOffset(offset);
    }


    /**
     * @brief Get blur radius of inner shadow.
     *
     * @return blur radius of inner shadow.
     */
    public float getElementInnerShadowBlurRadius(){
        return mElementInnerShadowBlurRadius;
    }


    /**
     * @brief Set blur radius of inner shadow.
     *
     * @param radius new blur radius of inner shadow.
     */
    public void setElementInnerShadowBlurRadius(float radius){
        if (mElementInnerShadowBlurRadius == radius) return;
        mElementInnerShadowBlurRadius = radius;
        mElementInnerShadow.setElementBlurRadius(mElementInnerShadowBlurRadius);
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

        // base class
        super.doLayout();

        bounds = getBounds();
        updateInnerShadow(bounds);
    }



    /**
     * @brief update the inner shadow when the bounds change.
     *
     * @param bounds new bounds
     */
    protected void updateInnerShadow(Rect bounds) {
        mElementInnerShadow.setLayoutRect(new Rect(mElementInnerShadowOffset.x + bounds.left,
                                                   mElementInnerShadowOffset.y + bounds.top,
                                                   bounds.right -  mElementInnerShadowOffset.x,
                                                   bounds.bottom - mElementInnerShadowOffset.y));
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- Layout ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    // ToDo: setElementShapePath

    /**
     * @brief Set the shape of the layer to a rectangle with the given size.
     *
     */
    @Override
    public void setElementShapeRect(){
        // base class
        super.setElementShapeRect();
        // config inner shadow
        mElementInnerShadow.setElementShapeRect();
    }


    /**
     * @brief Set the shape of the sunken layer to a rounded rectangle.
     *
     * @param cornerRadius radius of the rounded corners
     */
    @Override
    public void setElementShapeRoundedRect(float cornerRadius) {
        // base class
        super.setElementShapeRoundedRect(cornerRadius);
        // inner shadow
        mElementInnerShadow.setElementShapeRoundedRect(cornerRadius - 1.0f);
    }


    /**
     * @brief Set the shape of the sunken layer to an oval.
     *
     */
    @Override
    public void setElementShapeOval() {
        // base class
        super.setElementShapeOval();
        // inner shadow
        mElementInnerShadow.setElementShapeOval();
    }

}
