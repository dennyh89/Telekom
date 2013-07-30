/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;

import de.telekom.pde.codelibrary.ui.events.PDEEvent;


/// @cond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED

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
public class PDESliderControllerState extends PDEEvent {



    /**
    * @brief   An id to inform content about what changes it has to made.
    */
    protected int mSliderControllerId;

    /**
     * @brief   The position to set by the Slider. Range is 0..1.
     */
    protected float mSliderPosition;

    /**
     * @brief   The start position to set by the Slider. Range is 0..1.
     */
    protected float mSliderStartPosition;

    /**
     * @brief   The page size to set by the Slider. Range is 0..1.
     */
    protected float mSliderPageSize;


    public int getSliderControllerId() {
        return mSliderControllerId;
    }


    public void setSliderControllerId(int mSliderControllerId) {
        this.mSliderControllerId = mSliderControllerId;
    }


    public float getSliderPosition() {
        return mSliderPosition;
    }


    public void setSliderPosition(float mSliderPosition) {
        this.mSliderPosition = mSliderPosition;
    }


    public float getSliderStartPosition() {
        return mSliderStartPosition;
    }


    public void setSliderStartPosition(float mSliderStartPosition) {
        this.mSliderStartPosition = mSliderStartPosition;
    }


    public float getSliderPageSize() {
        return mSliderPageSize;
    }


    public void setSliderPageSize(float mSliderPageSize) {
        this.mSliderPageSize = mSliderPageSize;
    }
}


/// @endcond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED