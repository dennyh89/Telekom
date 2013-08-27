/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;

import android.util.Log;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import java.util.Set;


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
public class PDEEventSliderControllerState extends PDEEvent {


    // ----- properties -----

    /**
    * @brief   An id to inform content about what changes it has to made.
    */
    protected int mSliderControllerId;
    public int getSliderControllerId() {
        return mSliderControllerId;
    }
    public void setSliderControllerId(int sliderControllerId) {
        this.mSliderControllerId = sliderControllerId;
    }

    /**
     * @brief Inform about what changes where made on controller.
     */
    protected Set<PDESliderController.PDESliderControllerChanges> mSliderControllerChanges;
    public Set<PDESliderController.PDESliderControllerChanges> getSliderControllerChanges() {
        return mSliderControllerChanges;
    }
    public void setSliderControllerChanges(Set<PDESliderController.PDESliderControllerChanges> sliderControllerChanges) {
        this.mSliderControllerChanges = sliderControllerChanges;
    }

    /**
     * @brief   The position to set by the Slider. Range is 0..1.
     */
    protected float mSliderPosition;
    public float getSliderPosition() {
        return mSliderPosition;
    }
    public void setSliderPosition(float sliderPosition) {
        this.mSliderPosition = sliderPosition;
    }

    /**
     * @brief   The start position to set by the Slider. Range is 0..1.
     */
    protected float mSliderStartPosition;
    public float getSliderStartPosition() {
        return mSliderStartPosition;
    }
    public void setSliderStartPosition(float sliderStartPosition) {
        this.mSliderStartPosition = sliderStartPosition;
    }

    /**
     * @brief   The page size to set by the Slider. Range is 0..1.
     */
    protected float mSliderPageSize;
    public float getSliderPageSize() {
        return mSliderPageSize;
    }
    public void setSliderPageSize(float sliderPageSize) {
        this.mSliderPageSize = sliderPageSize;
    }

    /**
     * @brief   The slider position according to the user defined value range.
     */
    protected float mSliderPositionUserRange;
    public float getSliderPositionUserRange() {
        return mSliderPositionUserRange;
    }
    public void setSliderPositionUserRange(float sliderPositionUserRange) {
        this.mSliderPositionUserRange = sliderPositionUserRange;
    }

    /**
     * @brief   The slider start position according to the user defined value range.
     */
    protected float mSliderStartPositionUserRange;
    public float getSliderStartPositionUserRange() {
        return mSliderStartPositionUserRange;
    }
    public void setSliderStartPositionUserRange(float sliderStartPositionUserRange) {
        this.mSliderStartPositionUserRange = sliderStartPositionUserRange;
    }

    /**
     * @brief   The slider page size according to the user defined value range.
     */
    protected float mSliderPageSizeUserRange;
    public float getSliderPageSizeUserRange() {
        return mSliderPageSizeUserRange;
    }
    public void setSliderPageSizeUserRange(float sliderPageSizeUserRange) {
        this.mSliderPageSizeUserRange = sliderPageSizeUserRange;
    }

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEEventSliderControllerState.class.getName();

    public void printEvent() {
        Log.d(LOG_TAG,"Slider Event: \n"+getType()+"\nController: "+mSliderControllerId);
    }
}

/// @endcond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED