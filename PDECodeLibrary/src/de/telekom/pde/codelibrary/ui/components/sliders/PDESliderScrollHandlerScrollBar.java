/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;

import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.components.sliders.PDESliderContentInterface.PDESliderContentOrientation;



//----------------------------------------------------------------------------------------------------------------------
//  PDESliderScrollHandlerScrollBar Vertical/Horizontal
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief A simple scrollHandler that allows dragging the handle of a vertical/horizontal scrollBar.
 */
public class PDESliderScrollHandlerScrollBar extends PDESliderScrollHandlerBase {

    /**
     * @brief Global tag for log outputs.
     */
    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDESliderScrollHandlerScrollBar.class.getName();
    
    // debug messages switch
    @SuppressWarnings("unused")
    private final static boolean DEBUG_PARAMS = false;


    // ----- properties -----

    // drag handling
    private boolean mHandleIsDragged;


    /**
     * @brief Initialization with vertical orientation by default
     */
    public PDESliderScrollHandlerScrollBar() {
        // base implementation with vertical orientation by default
        super();
    }


    /**
     * @brief Initialization
     */
    @SuppressWarnings("unused")
    public PDESliderScrollHandlerScrollBar(PDESliderContentOrientation orientation) {
        // base implementation
        super(orientation);
    }


    /**
     * @brief Private initialization
     */
    @Override
    protected void init(PDESliderContentOrientation orientation) {
        super.init(orientation);

        mHandleIsDragged = false;
    }


//----- over-writable actions ------------------------------------------------------------------------------------------


    /**
     * @brief This is called when a User Touch hits the Slider's handle.
     *
     * Also a position of our range 0...1 is passed.
     */
    @Override
    protected void actionTouchDownInsideHandle(float position) {

        // security check
        if (mOwningSlider == null) return;

        // get drag access
        mHandleIsDragged = mOwningSlider.getDragAccessForController(0);

        // start dragging
        if (mHandleIsDragged) startDragging();
    }


    /**
     * @brief This is called when a User Touch hits the Slider's handle and drags it.
     *
     * A position of our range 0...1 is passed to which the handle can be set.
     */
    @Override
    protected void actionTouchDragHandle(float position) {

        // security check
        if (mOwningSlider == null) return;

        // set position if in drag
        if (mHandleIsDragged)
            mOwningSlider.getSliderControllerForId(0).setSliderPosition(position, true);
    }


    /**
     * @brief This is called when a User ended with dragging the handle.
     *
     * Also a position of our range 0...1 is passed.
     */
    @Override
    protected void actionTouchDragHandleEnded(float position) {

        // security check
        if (mOwningSlider == null) return;

        // end drag
        mHandleIsDragged = false;

        // release drag access
        mOwningSlider.releaseDragAccessForController(0);
    }


//----- Content Access -------------------------------------------------------------------------------------------------


    /**
     * @brief Get the frame of the handle relative to the slider view.
     *  Normally returns the real size of the handle frame.
     *  If a bigger click frame is needed, just change the frame here.
     */
    @Override
    protected Rect getHandleClickFrame() {
        // security check
        if (mOwningSlider == null) return new Rect();

        return mOwningSlider.getHandleFrame();
    }


    /**
     * @brief Get the frame of the slider content relative to the slider view.
     *  Normally returns the real size of the content frame.
     *  If a bigger click frame is needed, just change the frame here.
     */
    @Override
    protected Rect getContentClickFrame() {
        // security check
        if (mOwningSlider == null) return new Rect();

        return mOwningSlider.getContentFrame();
    }
}
