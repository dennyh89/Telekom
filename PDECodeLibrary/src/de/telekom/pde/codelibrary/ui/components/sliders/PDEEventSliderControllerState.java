/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;

import de.telekom.pde.codelibrary.ui.events.PDEEvent;

import java.util.Set;


//----------------------------------------------------------------------------------------------------------------------
//  PDESliderController State
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief   Event state sent along with all slide events.
 *
 *          Contains the complete state of the slider changes.
 *          Each value is given in the 0..1 range that the slider content uses for
 *          setting up the slider and it's equivalent out of the user defined value range.
 *          The slider contents should extract the necessary values for themselves.
 *
 *
 *          Sliders can contain different controllers, for assigning them, a event comes with an id.
 */
public class PDEEventSliderControllerState extends PDEEvent {

    protected int mSliderControllerId;
    protected Set<PDESliderController.PDESliderControllerChanges> mSliderControllerChanges;
    protected boolean mSliderChangeFromUser;
    protected float mSliderPosition;
    protected float mSliderStartPosition;
    protected float mSliderPageSize;
    protected float mSliderPositionUserRange;
    protected float mSliderStartPositionUserRange;
    protected float mSliderPageSizeUserRange;
    protected PDESlider mSlider;

    // ----- properties -----

    /**
    * @brief   An id to inform content about what changes it has to made.
    */
    public int getSliderControllerId() {
        return mSliderControllerId;
    }


    /**
     * @brief   Set an id to inform content about what changes it has to made.
     */
    public void setSliderControllerId(int sliderControllerId) {
        mSliderControllerId = sliderControllerId;
    }


    /**
     * @brief Inform about what changes where made on controller.
     */
    @SuppressWarnings("unused")
    public Set<PDESliderController.PDESliderControllerChanges> getSliderControllerChanges() {
        return mSliderControllerChanges;
    }


    /**
     * @brief Set slider controller changes.
     */
    public void setSliderControllerChanges(Set<PDESliderController.PDESliderControllerChanges> sliderControllerChanges) {
        mSliderControllerChanges = sliderControllerChanges;
    }


    /**
     * @brief Inform whether the changes where made by the end user or programmatically.
     */
    @SuppressWarnings("unused")
    public boolean getSliderChangeFromUser() {
        return mSliderChangeFromUser;
    }


    /**
     * @brief Set information whether the changes where made by the end user or programmatically.
     */
    public void setSliderChangeFromUser(boolean fromUser) {
        mSliderChangeFromUser = fromUser;
    }


    /**
     * @brief   The position to set by the Slider. Range is 0..1.
     */
    public float getSliderPosition() {
        return mSliderPosition;
    }


    /**
     * @brief   Set the position of the Slider. Range is 0..1.
     */
    public void setSliderPosition(float sliderPosition) {
        mSliderPosition = sliderPosition;
    }


    /**
     * @brief   The start position to set by the Slider. Range is 0..1.
     */
    public float getSliderStartPosition() {
        return mSliderStartPosition;
    }


    /**
     * @brief   Set the start position of the Slider. Range is 0..1.
     */
    public void setSliderStartPosition(float sliderStartPosition) {
        this.mSliderStartPosition = sliderStartPosition;
    }


    /**
     * @brief   The page size to set by the Slider. Range is 0..1.
     */
    public float getSliderPageSize() {
        return mSliderPageSize;
    }


    /**
     * @brief   Set the page size. Range is 0..1.
     */
    public void setSliderPageSize(float sliderPageSize) {
        this.mSliderPageSize = sliderPageSize;
    }


    /**
     * @brief   The slider position according to the user defined value range.
     */
    public float getSliderPositionUserRange() {
        return mSliderPositionUserRange;
    }


    /**
     * @brief   Set the slider position according to the user defined value range.
     */
    public void setSliderPositionUserRange(float sliderPositionUserRange) {
        mSliderPositionUserRange = sliderPositionUserRange;
    }


    /**
     * @brief   The slider start position according to the user defined value range.
     */
    public float getSliderStartPositionUserRange() {
        return mSliderStartPositionUserRange;
    }


    /**
     * @brief   Set the slider start position according to the user defined value range.
     */
    public void setSliderStartPositionUserRange(float sliderStartPositionUserRange) {
        mSliderStartPositionUserRange = sliderStartPositionUserRange;
    }


    /**
     * @brief   The slider page size according to the user defined value range.
     */
    public float getSliderPageSizeUserRange() {
        return mSliderPageSizeUserRange;
    }


    /**
     * @brief   Set the slider page size according to the user defined value range.
     */
    public void setSliderPageSizeUserRange(float sliderPageSizeUserRange) {
        mSliderPageSizeUserRange = sliderPageSizeUserRange;
    }


    /**
     * @brief   Get the used slider.
     */
    public PDESlider getSlider() {
        return mSlider;
    }


    /**
     * @brief   Set the slider.
     */
    public void setSlider(PDESlider slider) {
        mSlider = slider;
    }
}

