/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.boxes;


import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBorderLine;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShape;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedInnerShadow;
import de.telekom.pde.codelibrary.ui.elements.wrapper.PDEViewWrapper;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableSunkenArea
//----------------------------------------------------------------------------------------------------------------------


public class PDEDrawableSunkenArea extends PDEDrawableMultilayer {

    //-----  properties ---------------------------------------------------------------------------------------------------
    protected PDEDrawableBorderLine mElementBorderLine;
    protected PDEDrawableShapedInnerShadow mElementInnerShadow;
    protected PDEDrawableShape mElementBackground;

    protected PDEColor mElementSunkenBackgroundColor;
    protected PDEColor mElementSunkenBorderColor;
    protected PDEColor mElementInnerShadowColor;

    protected float mElementInnerShadowOpacity;
    protected Point mElementInnerShadowOffset;
    protected float mElementInnerShadowBlurRadius;

    protected PDEViewWrapper mWrapperView;



//----- init -----------------------------------------------------------------------------------------------------------

    /**
     * @brief Constructor
     *
     *
     */
    public PDEDrawableSunkenArea() {
        mElementSunkenBackgroundColor = PDEColor.valueOf("DTWhite");
        mElementSunkenBorderColor = PDEColor.valueOf("DTUIBorder");
        mElementInnerShadowColor = PDEColor.valueOf("DTBlack");
        // Todo: Shape path

        // init sub layers
        initLayers();

        // init borderline
        mElementBorderLine.setElementBorderColor(mElementSunkenBorderColor.getIntegerColor());
        mElementBorderLine.setElementBorderWidth(1.0f);
        mElementBorderLine.setElementShapeRect();
        // init background
        mElementBackground.setElementBackgroundColor(mElementSunkenBackgroundColor.getIntegerColor());
        mElementBackground.setElementShapeRect();

        // init inner shadow
        mElementInnerShadowOffset = new Point(1,1);
        mElementInnerShadow.setElementShapeColor(mElementInnerShadowColor);
        // ToDo: check if this replacement is correct or should we use: setElementLightIncidenceOffset?
        //mElementInnerShadow.setShapeOffset(mElementInnerShadowOffset);
        mElementInnerShadow.setLayoutOffset(mElementInnerShadowOffset);
        mElementInnerShadow.setElementShapeRect();

        //mElementInnerShadowOpacity = 0.24f;
        //mElementInnerShadowBlurRadius = (float) PDEBuildingUnits.oneFourthBU();
        //mElementInnerShadow.setElementBlurRadius(mElementInnerShadowBlurRadius);
        //mElementInnerShadow.setElementShapeOpacity(mElementInnerShadowOpacity);

        mWrapperView = null;
    }



    // internal helpers
    private void initLayers() {
        // initialize sublayers
        mElementBorderLine = new PDEDrawableBorderLine();
        mElementInnerShadow = new PDEDrawableShapedInnerShadow();
        mElementBackground = new PDEDrawableShape();

        // hang in layers
        addLayer(mElementBackground);
        addLayer(mElementBorderLine);
        addLayer(mElementInnerShadow);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Get the background color.
     *
     * @return background color
     */
    public PDEColor getElementSunkenBackgroundColor(){
        return mElementSunkenBackgroundColor;
    }


    /**
     * @brief Set a new background color.
     *
     * @param color new background color
     */
    public void setElementSunkenBackgroundColor(PDEColor color){
        // anything to do?
        if (color.getIntegerColor() == mElementSunkenBackgroundColor.getIntegerColor()) return;
        // remember
        mElementSunkenBackgroundColor = color;
        // forward
        mElementBackground.setElementBackgroundColor(mElementSunkenBackgroundColor.getIntegerColor());
    }


    /**
     * @brief Get border color.
     *
     * @return border color
     */
    public PDEColor getElementSunkenBorderColor(){
        return mElementSunkenBorderColor;
    }


    /**
     * @brief Set new border color.
     *
     * @param color new border color
     */
    public void setElementSunkenBorderColor(PDEColor color){
        // anything to do?
        if (color.getIntegerColor() == mElementSunkenBorderColor.getIntegerColor()) return;
        // remember
        mElementSunkenBorderColor = color;
        // forward
        mElementBorderLine.setElementBorderColor(mElementSunkenBorderColor.getIntegerColor());
    }


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
        // todo: check correctness
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
        mElementInnerShadow.setElementShapeOpacity(mElementInnerShadowOpacity);
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
        updateInnerShadow(bounds);
    }


    /**
     * @brief update the backgroundLayer when the bounds change.
     *
     * @param bounds new bounds
     */
    private void updateBackgroundDrawable(Rect bounds) {
        mElementBackground.setBounds(bounds);
    }

    /**
     * @brief update the border line when the bounds change.
     *
     * @param bounds new bounds
     */
    private void updateBorderLine(Rect bounds) {
        mElementBorderLine.setBounds(bounds);
    }

    /**
     * @brief update the inner shadow when the bounds change.
     *
     * @param bounds new bounds
     */
    private void updateInnerShadow(Rect bounds) {
        //mElementInnerShadow.setBounds(bounds);
        // todo: check
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
    public void setElementShapeRect(){
        // config background
        mElementBackground.setElementShapeRect();
        // config inner shadow
        mElementInnerShadow.setElementShapeRect();
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
        // inner shadow
        mElementInnerShadow.setElementShapeRoundedRect(cornerRadius - 1.0f);
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
        // inner shadow
        mElementInnerShadow.setElementShapeOval();
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
