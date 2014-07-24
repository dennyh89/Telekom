/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;




//----------------------------------------------------------------------------------------------------------------------
//  PDESliderScrollHandlerBase
//----------------------------------------------------------------------------------------------------------------------


import android.graphics.Rect;
import android.view.MotionEvent;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.components.sliders.PDESliderContentInterface.PDESliderContentOrientation;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;


/**
 *  @brief  This is a base class that offers support for handling touch events on PDESliders.
 *          You can extend this class to easily add touch functionality to a Slider.
 *          But you will have to provide information about the slider content in getHandleClickFrame and getContentClickFrame.
 *          See existing Scroll subclasses for examples.
 */
public abstract class PDESliderScrollHandlerBase implements PDEIEventSource {


    /**
     * @brief Global tag for log outputs.
     */
    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDESliderScrollHandlerBase.class.getName();


//----------------------------------------------------------------------------------------------------------------------
//  PDESliderScrollHandlerBase constants
//----------------------------------------------------------------------------------------------------------------------

    // ----- properties -----

    // reference slider
    protected PDESlider mOwningSlider;

    // store Orientation
    protected PDESliderContentOrientation mContentOrientation;

    // dragging
    private boolean mDragActive;
    private Rect mDragStartHandleFrame, mDragStartTouchLocation;

    // agent controller
    private PDEAgentController mAgentController;
    private PDEEventSource mEventSource;


    /**
     * @brief Initialization with orientation vertical by default.
     */
    public PDESliderScrollHandlerBase() {
        init(PDESliderContentOrientation.PDESliderContentOrientationVertical);
    }


    /**
     * @brief Initialization with orientation
     */
    @SuppressWarnings("unused")
    public PDESliderScrollHandlerBase(PDESliderContentOrientation orientation) {
        init(orientation);
    }


    /**
     * @brief Private initialization
     */
    protected void init(PDESliderContentOrientation orientation) {
        // default settings
        mOwningSlider = null;
        mContentOrientation = orientation;
        mDragActive = false;

        // setup event source
        mEventSource = new PDEEventSource();

        // setup agent controller
        mAgentController = new PDEAgentController();

        // catch agent controller events for animation
        mAgentController.addListener(this, "cbAgentController", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK);
    }


    /**
     * @brief Sets reference to owning PDESlider.
     */
    public void setOwningSlider(PDESlider owningSlider) {

        // store
        mOwningSlider = owningSlider;
    }


//----- Get Content Access ------------------------------------------------------------------------------------


    /**
     * @brief Get the click frame of the handle relative to the slider view.
     *  Normally returns the real size of the handle frame.
     *  If a bigger click frame is needed, just change the frame here.
     *
     */
    protected abstract Rect getHandleClickFrame();


    /**
     * @brief Get the click frame of the slider content relative to the slider view.
     *  Normally returns the real size of the content frame.
     *  If a bigger click frame is needed, just change the frame here.
     *
     */
    protected abstract Rect getContentClickFrame();


// ----- Over-writable Actions ------------------------------------------------------------------------------------------


    /**
     * @brief This is called when a User Touch hits the Slider's handle.
     *
     * Also a position of our range 0...1 is passed.
     */
    protected void actionTouchDownInsideHandle(float position) {

        // do nothing in base class
    }


    /**
     * @brief This is called when a User Touch hits the Slider's content but not it's handle.
     *
     * Also a position of our range 0...1 is passed.
     */
    protected void actionTouchDownOutsideHandle(float position) {

        // do nothing in base class
    }


    /**
     * @brief This is called when a User Touch hits the Slider's handle and drags it.
     *
     * A position of our range 0...1 is passed to which the handle can be set.
     */
    protected void actionTouchDragHandle(float position) {

        // do nothing in base class
    }


    /**
     * @brief This is called when a User ended with dragging the handle.
     *
     * Also a position of our range 0...1 is passed.
     */
    protected void actionTouchDragHandleEnded(float position) {
        // do nothing in base class
    }


    /**
     * @brief This is called when the user's touch ends.
     *
     * Also a position of our range 0...1 is passed.
     */
    @SuppressWarnings({"unused", "EmptyMethod"})
    protected void actionTouchUp(float position) {
        // do nothing in base class
    }


    /**
     * @brief This is called when the user's touch is cancelled.
     *
     * Also a position of our range 0...1 is passed.
     */
    @SuppressWarnings({"unused", "EmptyMethod"})
    protected void actionTouchCancel(float position) {
        // do nothing in base class
    }


// ----- touch events from slider --------------------------------------------------------------------------------------


    /**
     * @brief This is called from PDE slider on touches began.
     *
     * Calculates Positions in 0..1 range and passes them to overwritable functions
     * if touch hits the main rect.
     */
    public void actionTouchesBegan(MotionEvent event) {

        Rect location;
        float position;
        boolean hitsContent, hitsHandle;

        // get location
        location = getTouchLocation(event);

        // check for hits
        hitsContent = Rect.intersects(location, getContentClickFrame());
        hitsHandle = Rect.intersects(location, getHandleClickFrame());
        //Point p = new Point((int) event.getX(),(int) event.getY());
        //boolean test = getHandleClickFrame().contains(p.x,p.y);

        // check if location hits touch elements
        if (!hitsContent && !hitsHandle) return;

        // convert into slider range
        position = turnLocationFrameIntoSliderPosition(location);

        // touch down inside handle
        if (hitsHandle) {

            // send action
            actionTouchDownInsideHandle(position);
        }

        // touch down outside handle
        else {

            // send action
            actionTouchDownOutsideHandle(position);
        }

        // prepare for dragging
        mDragStartTouchLocation = location;
        mDragStartHandleFrame = getHandleClickFrame();
    }


    /**
     * @brief This is called from PDE slider on touches moved.
     *
     * Calculates Positions in 0..1 range and passes them to overwritable functions.
     */
    public void actionTouchesMoved(MotionEvent event) {

        Rect location, handleFrame;
        float position;
        int handleWidth,handleHeight;

        // get location
        location = getTouchLocation(event);

        // has handle been hit?
        if (mDragActive) {
            // get Start handle
            handleFrame  = new Rect(mDragStartHandleFrame);
            handleWidth  = handleFrame.width();
            handleHeight = handleFrame.height();

            // determine position relative to movement and content orientation
            if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationHorizontal) {
                // horizontal
                handleFrame.left  = location.left - mDragStartTouchLocation.left +mDragStartHandleFrame.left;
                handleFrame.right = handleFrame.left + handleWidth;

            } else if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationVertical) {
                // vertical
                handleFrame.top = location.top - mDragStartTouchLocation.top + mDragStartHandleFrame.top;
                handleFrame.bottom = handleFrame.top + handleHeight;
            }

            // get Handle position
            position = turnLocationFrameIntoSliderPosition(handleFrame);

            // touch drag
            actionTouchDragHandle(position);
        }
    }


    /**
     * @brief This is called from PDE slider on touches ended.
     *
     * Pass on to overwritable functions.
     */
    public void actionTouchesEnded(MotionEvent event) {

        Rect location;
        float position;

        // get location
        location = getTouchLocation(event);

        // convert into slider position
        position = turnLocationFrameIntoSliderPosition(location);

        // handle touch
        actionTouchUp(position);

        // stop dragging
        if (mDragActive) {
            mDragActive = false;
            actionTouchDragHandleEnded(position);

            // inform agent Controller
            mAgentController.cancelPress();
            mAgentController.removeFocus();
        }
    }


    /**
     * @brief This is called from PDE slider on touches ended.
     *
     * Pass on to overwritable functions.
     */
    public void actionTouchesCancelled(MotionEvent event) {

        Rect location;
        float position;

        // get location
        location = getTouchLocation(event);

        // convert into slider position
        position = turnLocationFrameIntoSliderPosition(location);

        // handle touch
        actionTouchCancel(position);

        // stop dragging
        if (mDragActive) {
            mDragActive = false;
            actionTouchDragHandleEnded(position);

            // inform agent Controller
            mAgentController.cancelPress();
            mAgentController.removeFocus();
        }
    }

    public void addHighlight(){
        mAgentController.addHighlight();
    }

    public void removeHighlight(){
        mAgentController.removeHighlight();
    }

// ----- Helper --------------------------------------------------------------------------------------------------------


    /**
     * @brief Helper to turn a event into a touch location
     *
     * Because of a touch point's impreciseness we prefer to work with a rect. The center of the rect is the exact touch point.
     *
     * @param touch A touch event
     * @return A rect representing a touch point, in relation to the slider's view.
     */
    protected Rect getTouchLocation(MotionEvent touch) {

        Rect touchLocation;
        int impreciseness;

        // set impreciseness
        impreciseness = PDEBuildingUnits.pixelFromBU(1.5f);

        // setup Rect
        touchLocation = new Rect();
        touchLocation.left   = (int) touch.getX() - impreciseness;
        touchLocation.top    = (int) touch.getY() - impreciseness;
        touchLocation.right  = (int) touch.getX() + impreciseness;
        touchLocation.bottom = (int) touch.getY() + impreciseness;

        // done
        return touchLocation;
    }


    /**
     * @brief Helper to turn a touch location Frame into internal position values.
     *
     * @param locationFrame A touch location frame inside the view
     * @return A position in 0..1 in range which can be set.
     */
    protected float turnLocationFrameIntoSliderPosition(Rect locationFrame) {

        float position;
        Rect handleFrame, contentFrame;

        // get values
        position = -1;
        handleFrame = getHandleClickFrame();
        contentFrame = getContentClickFrame();

        // handle horizontal content
        if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationHorizontal) {

            // get center of the location frame
            position = locationFrame.left - contentFrame.left + locationFrame.width()/2;

            // get center relative to handle start position
            position = position - handleFrame.width() /2;

            // turn into 0...1 range
            position = position / (contentFrame.width() - handleFrame.width());
        }

        // handle vertical content
        else if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationVertical) {

            // get center of the location frame
            position = locationFrame.top - contentFrame.top + locationFrame.height()/2;

            // get center relative to handle start position
            position = position - handleFrame.height() / 2;

            // turn into 0...1 range
            position = position / (contentFrame.height()-handleFrame.height());
        }

        // turn  into 0...1 range
        if (position < 0) position = 0;
        if (position > 1) position = 1;

        // done
        return position;
    }


// ----- Dragging ------------------------------------------------------------------------------------------------------


    /**
     * @brief Manually start dragging. Used for customizing the slider's drag behaviour.
     *
     */
    public void startDragging() {

        // call internal
        startDragging_priv();
    }


    /**
     * @brief Start dragging and add focus.
     *
     */
    private void startDragging_priv() {

        if (mDragActive) return;

        // start dragging
        mDragActive = true;

        // inform agent controller
        mAgentController.addFocus();
        mAgentController.addPress();
    }


// ----- Agent Controller Handling -------------------------------------------------------------------------------------


    // needs to be public otherwise it cannot be called from PDEEventSource

    /**
     * @brief Called on changes from agent controller
     */
    @SuppressWarnings("unused")
    public void cbAgentController(PDEEvent event) {

        // pass on event
        if (event.isType(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK))
            sendAgentEvent(event);
    }


    /**
     * @brief Send Event to owning Slider.
     *
     */
    private void sendAgentEvent(PDEEvent event) {

        // send event
        mEventSource.sendEvent(event);
    }


//----- Event Handling -------------------------------------------------------------------------------------------------


    /**
     * @brief Get the eventSource which is responsible for sending PDEEvents events.
     * Most of the events are coming form the PDEAgentController.
     * @return PDEEventSource
     */
    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }


    /**
     * @brief Add event Listener.
     *
     * PDEIEventSource Interface implementation.
     *
     * @param target    Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @return Object which can be used to remove this listener
     *
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName) {
        return mEventSource.addListener(target, methodName);
    }


    /**
     * @brief Add event Listener.
     *
     * PDEIEventSource Interface implementation.
     *
     * @param target    Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @param eventMask PDEAgentController event mask.
     *                  Will be most of the time PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED or
     *                  PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED
     * @return Object which can be used to remove this listener
     *
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName, String eventMask) {
        return mEventSource.addListener(target, methodName, eventMask);
    }


    /**
     * @brief Remove the specified listener .
     *
     * @param listener The listener reference returned by addListener:
     * @return boolean Returns whether we have found & removed the listener or not
     */
    @SuppressWarnings("unused")
    public boolean removeListener(Object listener) {
        return mEventSource.removeListener(listener);
    }
}
