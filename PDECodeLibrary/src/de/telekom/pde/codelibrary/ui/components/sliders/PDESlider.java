
/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants.PDEContentStyle;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.components.sliders.PDESliderContentInterface.PDESliderContentOrientation;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

//----------------------------------------------------------------------------------------------------------------------
//  PDESlider
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief This is a Slider, controlled by slider events.
 *
 * The slider does nothing itself, it just manages contents which do the actual animation.
 * PDESlider offers a few predefined content variants as preset, but does not prevent you using your own graphics.
 *
 * The structure builds up own following different constructs:
 *
 * PDESliderContent:
 * Only one sliderContent type can be assigned to a PDESlider.
 * The Slider Content is responsible for the visible component of the PDESlider.
 * It holds the drawables and will set, via a interface received, values to it.
 *
 * PDESliderController:
 * A Slider is able to store different controllers associated to specific ids.
 * Through Controllers it is possible to change the contents property assigned to their assigned id.
 * Each Controller will send out events to inform about changes being made on him which will be passed on by the
 * Slider.
 *
 * PDEEventSliderControllerState
 * Are send by the controller and will be passed on by the PDESlider to registered listeners and to the content.
 *
 * PDESliderScroller:
 * To make the PDESlider touch interactive customized scrollers classes can be derived from a scroller base class.
 */
public class PDESlider extends PDEAbsoluteLayout implements PDEIEventSource {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDESlider.class.getName();
    // debug messages switch
    private final static boolean DEBUG_PARAMS = false;

    // boolean flag for disable the parent touch interception
    private boolean mParentTouchInterceptionDisabled;

    protected ArrayList<Object> mStrongPDEEventListenerHolder;

//----------------------------------------------------------------------------------------------------------------------
//  PDESlider helper class
//----------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Parcelable class for storing slider values
     */
    static class SavedState extends BaseSavedState {

        ArrayList<PDESliderControllerAssociator> savedSliderControllerBag;


        /**
         * Constructor called from {@link PDESlider#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }


        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            savedSliderControllerBag = new ArrayList<PDESliderControllerAssociator>();
            in.readTypedList(savedSliderControllerBag, PDESliderControllerAssociator.CREATOR);
        }


        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeTypedList(savedSliderControllerBag);
        }


        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }


            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    /**
     * @brief Inner class for receiving callbacks via reflection.
     *
     * The reflection mechanism of android (or java) is screwed up if a target class contains a method with at run time
     * unknown class parameters. This happens on pre API level 14 devices for AccessibilityNodeInfo which is used in the
     * onInitializeAccessibilityNodeInfo function.
     * By putting the call back functions into a separate class - this problem doesn't occur.
     */
    private class EventReceiver {

        /**
         * @brief This is called for Agent Events send from the ScrollHandlerBase.
         */
        @SuppressWarnings("unused")
        public void cbScrollHandlerBase(PDEEvent event) {

            // right type?
            if (event.isType(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK)) {

                // send event to content
                sendSliderEvent(event);
            }
        }


        /**
         * @param event PDEEvent
         * @brief Called on changes from the slider controllers.
         */
        @SuppressWarnings("unused")
        public void cbSliderController(PDEEvent event) {

            if (event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK_DATA)) {
                // inform listeners tha a change will be made

                // set id of sending controller
                replaceControllerIdForEvent(event);

                // send event to listeners
                mEventSource.sendEvent(event);
            } else if (event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_ACTION_DID_CHANGE)) {
                // send event to change the content

                // set id of sending controller
                replaceControllerIdForEvent(event);

                if (((PDEEventSliderControllerState) event).getSliderControllerId() == 0) {
                    //sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);

                    AccessibilityManager am = (AccessibilityManager) PDECodeLibrary.getInstance()
                                                                                   .getApplicationContext()
                                                                                   .getSystemService(Context.ACCESSIBILITY_SERVICE);
                    if (am.isEnabled()) {
                        scheduleAccessibilityEventSender();
                    }
                }

                // send event to content
                sendSliderEvent(event);
            }
        }


        /**
         * @param event PDEEvent
         * @brief Called on specially requested initializations from our slider controllers.
         */
        @SuppressWarnings("unused")
        public void cbSliderControllerSingle(PDEEvent event) {

            // do nothing if no initialization content is defined
            if (mSliderContentToInitialize == null) return;

            // send to the content if it's an event we want to listen
            if (event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_ACTION_INITIALIZE)) {

                // set id of sending controller
                replaceControllerIdForEvent(event);

                // send to content
                sendSliderEvent(event);
            }
        }


    }

//----------------------------------------------------------------------------------------------------------------------
//  PDESlider constants
//----------------------------------------------------------------------------------------------------------------------

    // ----- constants -----


    /**
     * @brief Telekom well known slider contents.
     */
    public enum PDESliderContentType {
        /* ATTENTION: IF YOU ADD A NEW TYPE HERE, DON'T FORGET TO ADD IT IN ATTRS.XML AS WELL!!!!!
            AND IF YOU ADD IT IN BETWEEN, YOU HAVE TO ADJUST THE INDEXES OF ALL FOLLOWING TYPES IN ATTRS.XML AS WELL!!!
         */
        ProgressBarFlat,
        ProgressBarHaptic,
        SliderBarFlat,
        SliderBarHaptic,
        ScrollBarHorizontal,
        ScrollBarVertical,
        ScrollBarHandleOnlyHorizontal,
        ScrollBarHandleOnlyVertical
    }


    private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;

    // ----- properties -----

    // content
    private PDESliderContentInterface mSliderContent;

    // controller
    private ArrayList<PDESliderControllerAssociator> mSliderControllerBag;

    // EventSource
    protected PDEEventSource mEventSource;

    // scroll handling
    private PDESliderScrollHandlerBase mScrollHandler;

    // helper
    private PDESliderContentInterface mSliderContentToInitialize;

    private final EventReceiver mEventReceiver = new EventReceiver();

    private float mKeyProgressIncrement;

    private AccessibilityEventSender mAccessibilityEventSender;


    /**
     * @brief Constructor for PDESlider
     */
    public PDESlider(android.content.Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor for PDESlider
     */
    public PDESlider(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        if (DEBUG_PARAMS) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                Log.d(LOG_TAG, "PDEButton-Attr(" + i + "): " + attrs.getAttributeName(i) + " => "
                               + attrs.getAttributeValue(i));
            }
        }
        init(context, attrs);
    }


    /**
     * @brief Constructor for PDESlider
     */
    public PDESlider(android.content.Context context, android.util.AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context, attributeSet);
    }


    /**
     * @brief Internal initialisation.
     *
     * Create necessary graphics, start with a default configuration.
     */
    protected void init(Context context, AttributeSet attrs) {

        if (isInEditMode()) return;

        PDESliderController defaultController;

        // init
        mEventSource = new PDEEventSource();
        mStrongPDEEventListenerHolder = new ArrayList<Object>();

        mSliderContent = null;
        mSliderControllerBag = new ArrayList<PDESliderControllerAssociator>();

        mKeyProgressIncrement = 0.01f;

        // create and set default slider controller
        defaultController = new PDESliderController();
        setSliderControllerForId(defaultController, 0);

        setClipChildren(false);

        // disable the interception of the touch event in the parent view
        setParentTouchInterceptionDisabled(true);

        LayoutInflater.from(context).inflate(R.layout.pdeslider, this, true);

        // set Slider Content
        if (attrs != null) {
            TypedArray sa = context.obtainStyledAttributes(attrs, R.styleable.PDESlider);

            if (sa != null) {
                // create content
                if (sa.hasValue(R.styleable.PDESlider_pde_contentType)) {
                    setSliderContentType(sa.getInt(R.styleable.PDESlider_pde_contentType, 0));
                }

                sa.recycle();
            }
        }
    }

//----- scroll handling ------------------------------------------------------------------------------------------------


    /**
     * @param scrollHandler A Scroller customized for the actual content.
     * @brief Set a scroller to add touch functionality.
     */
    public void setScrollHandler(PDESliderScrollHandlerBase scrollHandler) {

        // make clean up
        if (mScrollHandler != null) {
            // remove listeners
            mScrollHandler.getEventSource().removeListenersForTarget(this);
        }

        // store scroller
        mScrollHandler = scrollHandler;

        // check if scroller is set
        if (mScrollHandler == null) {
            // no scroll handler no focus
            setFocusable(false);
            return;
        }

        // add reference
        mScrollHandler.setOwningSlider(this);

        // add listener
        mScrollHandler.addListener(this.mEventReceiver, "cbScrollHandlerBase",
                                   PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK);

        // we also should be focusable, since there is a scrollHandler
        setFocusable(true);
    }


    /**
     * @brief Get the scroll handler.
     */
    public PDESliderScrollHandlerBase getScrollHandler() {
        return mScrollHandler;
    }


    public void setParentTouchInterceptionDisabled(boolean disabled) {
        mParentTouchInterceptionDisabled = disabled;
    }


    /**
     * @brief Send touch events to our Scroller.
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        int action = event.getAction();

        // check if scrollHandler exists
        if (mScrollHandler == null) return false;

        // touches began
        if (action == MotionEvent.ACTION_DOWN) {
            // check parent and disable intercept of touches
            if (getParent() != null && mParentTouchInterceptionDisabled) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            mScrollHandler.actionTouchesBegan(event);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            // touch moved
            mScrollHandler.actionTouchesMoved(event);
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            // touch ended
            // check parent and enable intercept of touches
            if (getParent() != null && mParentTouchInterceptionDisabled) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            mScrollHandler.actionTouchesEnded(event);
            return true;
        } else if (action == MotionEvent.ACTION_CANCEL) {
            // touch canceled

            // check parent and enable intercept of touches
            if (getParent() != null && mParentTouchInterceptionDisabled) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            mScrollHandler.actionTouchesCancelled(event);
            return true;
        } else {
            // something else

            // check parent and enable intercept of touches
            if (getParent() != null && mParentTouchInterceptionDisabled) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }

        // base implementation
        return super.onTouchEvent(event);
    }

//----- content handling -----------------------------------------------------------------------------------------------


    /**
     * @brief Set Slider Content with Integer Content type
     */
    private void setSliderContentType(int contentType) {
        setSliderContentType(PDESliderContentType.values()[contentType]);
    }


    /**
     * @param contentType A slider content type constant
     * @brief Set the new slider content. Using the content Type
     * Removes the old content.
     */
    public void setSliderContentType(PDESliderContentType contentType) {

        PDESliderContentInterface newContent;

        // create and set the new content, remember type
        switch (contentType) {
            case ProgressBarFlat:
                newContent = new PDESliderContentProgressBar(getContext(),
                                                             PDEContentStyle.PDEContentStyleFlat);
                break;
            case ProgressBarHaptic:
                newContent = new PDESliderContentProgressBar(getContext(),
                                                             PDEContentStyle.PDEContentStyleHaptic);
                break;
            case ScrollBarHorizontal:
                newContent = new PDESliderContentScrollBar(getContext(),
                                                           PDESliderContentOrientation.PDESliderContentOrientationHorizontal);
                break;
            case SliderBarFlat:
                newContent = new PDESliderContentSliderBar(getContext(),
                                                           PDEContentStyle.PDEContentStyleFlat);
                break;
            case SliderBarHaptic:
                newContent = new PDESliderContentSliderBar(getContext(),
                                                           PDEContentStyle.PDEContentStyleHaptic);
                break;
            case ScrollBarVertical:
                newContent = new PDESliderContentScrollBar(getContext());
                break;
            case ScrollBarHandleOnlyHorizontal:
                newContent = new PDESliderContentScrollBar(getContext(),
                                                           PDESliderContentOrientation.PDESliderContentOrientationHorizontal);
                ((PDESliderContentScrollBar) newContent).setHandleOnly(true);
                break;
            case ScrollBarHandleOnlyVertical:
                newContent = new PDESliderContentScrollBar(getContext());
                ((PDESliderContentScrollBar) newContent).setHandleOnly(true);
                break;
            default:
                //error
                newContent = null;
                break;
        }

        setSliderContent(newContent);
    }


    /**
     * @param content A slider content object
     * @brief Set the new slider content.
     * Removes the old content.
     */
    public void setSliderContent(PDESliderContentInterface content) {
        ViewGroup.LayoutParams lp;
        // remove old content from the layer if it exists
        if (mSliderContent != null
            && mSliderContent.getLayer() != null
            && mSliderContent.getLayer().getParent() != null) {
            ((ViewGroup) mSliderContent.getLayer().getParent()).removeView(mSliderContent.getLayer());
        }

        // release old drag access
        releaseAllDragAccesses();
        // reset scroll handler
        setScrollHandler(null);

        // remember
        mSliderContent = content;

        if (mSliderContent != null) {
            lp = mSliderContent.getLayer().getLayoutParams();
            if (lp == null) {
                lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            }
            mSliderContent.getLayer().setLayoutParams(lp);

            // add the content to the layer
            ((ViewGroup) findViewById(R.id.pdeslider_content_slot)).addView(mSliderContent.getLayer());
        }

        if (content instanceof PDESliderContentSliderBar) {
            setScrollHandler(new PDESliderScrollHandlerSliderBar());
        } else if (content instanceof PDESliderContentScrollBar) {
            setScrollHandler(
                    new PDESliderScrollHandlerScrollBar(((PDESliderContentScrollBar) content).getContentOrientation()));
        } else if (content instanceof PDESliderContentProgressBar) {
            // in this case no handler is needed
            setScrollHandler(null);
        } else {
            // custom class -> handler needs to be set manually
            Log.d(LOG_TAG, "setSliderContent - custom class. There was no handler set, if needed do it manually!");
        }

        // new content needs to be initialized with parameters
        requestSliderContentInit();
    }


    /**
     * @brief Get the currently used slider content.
     */
    public PDESliderContentInterface getSliderContent() {
        return mSliderContent;
    }


    /**
     * @brief Requests content initialization on next Draw.
     */
    private void requestSliderContentInit() {

        // get the view tree observer
        ViewTreeObserver vto = this.getViewTreeObserver();

        if (vto != null) {
            // add listener
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver vto = PDESlider.this.getViewTreeObserver();
                    if (vto != null) {
                        vto.removeOnPreDrawListener(this);
                    }
                    PDESlider.this.initializeSliderContent();
                    return true;
                }
            });
        }
    }


    /**
     * @brief Requests initialization Events for a slider content from slider controllers.
     */
    private void initializeSliderContent() {

        // security check
        if (mSliderContent == null) return;

        // send complete slider initialization to set the contents values
        mSliderContentToInitialize = mSliderContent;

        // request initialization for all controller
        for (PDESliderControllerAssociator associator : mSliderControllerBag) {
            associator.sliderController.getEventSource().requestOneTimeInitialization(this.mEventReceiver,
                                                                                      "cbSliderControllerSingle",
                                                                                      PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK);
        }

        // done
        mSliderContentToInitialize = null;
    }


    public void setKeyProgressIncrement(float increment) {

        mKeyProgressIncrement = increment;
    }


    public float getKeyProgressIncrement() {
        return mKeyProgressIncrement;
    }

//----- Controller Linkage ---------------------------------------------------------------------------------------------


    /**
     * @param controller   A Slider controller to set
     * @param controllerId Id to associate the slider controller
     * @brief Add a given Slider Controller for the given Id to the controller bag Array.
     * If the given id is already used this will remove
     * and clean up the old controller for the given id.
     * Handing over nil for controller will only remove the controller on given id.
     */
    public void setSliderControllerForId(PDESliderController controller, int controllerId) {

        PDESliderControllerAssociator oldAssociator;
        PDESliderControllerAssociator newAssociator;
        int count;

        // init
        oldAssociator = null;
        count = 0;

        // check if a old controller exists for the given id
        for (PDESliderControllerAssociator associator : mSliderControllerBag) {

            // remember old associator
            if (associator.sliderControllerId == controllerId) oldAssociator = associator;
        }

        // if old Associator exists make a clean up
        if (oldAssociator != null) {

            // check if there is only one listener for this controller
            for (PDESliderControllerAssociator associator : mSliderControllerBag) {

                // count same controller
                if (associator.sliderController == oldAssociator.sliderController) count++;
            }

            // remove Listeners to the old controller only, when our bag holds this controller one single time
            if (count == 1) oldAssociator.sliderController.getEventSource().removeListenersForTarget(this);

            // remove references to old controller
            mSliderControllerBag.remove(oldAssociator);
        }

        // set back
        count = 0;

        // check if new controller exists
        if (controller != null) {

            // initialize ne Controller Associator
            newAssociator = new PDESliderControllerAssociator();
            newAssociator.sliderControllerId = controllerId;
            newAssociator.sliderController = controller;

            // add Associator to the Controller Bag
            mSliderControllerBag.add(newAssociator);

            // check if there is already a listener for this controller
            for (PDESliderControllerAssociator associator : mSliderControllerBag) {

                // count same controller
                if (associator.sliderController == controller) count++;
            }

            // add Listener for new Controller if there is not already a listener
            if (count == 1) {
                controller.addListener(this.mEventReceiver, "cbSliderController");
            }
        }
    }


    /**
     * @param controllerId Id to get associated Controller.
     * @return A Slider controller associated to given Id.
     * @brief Get the Slider Controller for the given ID.
     * Creates and hands out a new Controller if there is no Controller on give id.
     */
    public PDESliderController getSliderControllerForId(int controllerId) {

        PDESliderController controller;

        // init
        controller = null;

        // check if a controller exists for the given id
        for (PDESliderControllerAssociator associator : mSliderControllerBag) {

            // get Slider controller
            if (associator.sliderControllerId == controllerId) controller = associator.sliderController;
        }

        // return controller if it exists
        if (controller != null) return controller;

        // init new Controller
        controller = new PDESliderController();

        // set for given id
        setSliderControllerForId(controller, controllerId);

        // done
        return controller;
    }

    // needs to be public otherwise it cannot be called from event source (11.10.2012)

    // needs to be public otherwise it cannot be called from event source (11.10.2012)


    /**
     * @param event PDEEvent
     * @brief Send a slider Event to the content.
     * Checks the sender of the event and replaces it's id into the event.
     */
    private void sendSliderEvent(PDEEvent event) {

        // does the content exist
        if (mSliderContent == null) return;

        // send event to content
        mSliderContent.sliderEvent(event);
    }


    /**
     * @param event PDEEvent
     * @brief Checks the sender of the event and replaces it's id into the given event.
     */
    private void replaceControllerIdForEvent(PDEEvent event) {

        PDEEventSliderControllerState slideEvent;

        // cast event to set new Variables
        slideEvent = (PDEEventSliderControllerState) event;

        slideEvent.setSlider(this);

        // get the sender of the event
        for (PDESliderControllerAssociator associator : mSliderControllerBag) {

            // get sending Slider Controller
            if (associator.sliderController == event.getSender()) {
                // set id
                slideEvent.setSliderControllerId(associator.sliderControllerId);
            }
        }
    }

//----- Event Handling -------------------------------------------------------------------------------------------------


    /**
     * @return PDEEventSource
     * @brief Get the eventSource which is responsible for sending PDEEvents events.
     * Most of the events are coming form the PDEAgentController.
     */
    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }


    /**
     * @param target     Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @return Object which can be used to remove this listener
     * @brief Add event Listener - hold strong pointer to it.
     *
     * PDEIEventSource Interface implementation, with additional local storage of (strong) pointer to it.
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName) {
        mStrongPDEEventListenerHolder.add(target);
        return mEventSource.addListener(target, methodName);
    }


    /**
     * @param target     Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @param eventMask  PDEAgentController event mask.
     *                   Will be most of the time PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED or
     *                   PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED
     * @return Object which can be used to remove this listener
     * @brief Add event Listener - hold strong pointer to it.
     *
     * PDEIEventSource Interface implementation, with additional local storage of (strong) pointer to it.
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName, String eventMask) {
        mStrongPDEEventListenerHolder.add(target);
        return mEventSource.addListener(target, methodName, eventMask);
    }


    /**
     * @param listener The listener reference returned by addListener.
     * @return Returns whether we have found & removed the listener or not.
     * @brief Remove the specified listener.
     *
     * Also deletes local strong pointer.
     */
    @SuppressWarnings("unused")
    public boolean removeListener(Object listener) {
        mStrongPDEEventListenerHolder.remove(listener);
        return mEventSource.removeListener(listener);
    }


    /**
     * @brief Get the click frame of the content handle relative to the slider view.
     */
    public Rect getHandleFrame() {
        Rect handleRect = new Rect(mSliderContent.getHandleFrame());
        handleRect.offset(getPaddingLeft(), getPaddingBottom());
        return handleRect;
    }


    /**
     * @brief Get the click frame of the content relative to the slider view.
     */
    public Rect getContentFrame() {
        Rect contentRect = new Rect(mSliderContent.getContentFrame());
        contentRect.offset(getPaddingLeft(), getPaddingBottom());
        return contentRect;
    }

// ------ drag access --------------------------------------------------------------------------------------------------


    /**
     * @param controllerId controller id to get drag access
     * @return Boolean if access is given
     * @brief Get Drag Access to given controller.
     */
    public boolean getDragAccessForController(int controllerId) {

        PDESliderController controller;

        // get wanted controller
        controller = getSliderControllerForId(controllerId);

        // get access
        return controller.getDragAccessForSlider(this);
    }


    /**
     * @param controllerId controller id to release drag access
     * @brief Releases drag access for given Controller.
     * If Controller doesn't have access nothing will happen.
     */
    public void releaseDragAccessForController(int controllerId) {

        PDESliderController controller;

        // get wanted controller
        controller = getSliderControllerForId(controllerId);

        // release access
        controller.releaseDragAccessForSlider(this);
    }


    /**
     * @brief Release all drag accesses to stored Controllers.
     */
    public void releaseAllDragAccesses() {

        // release drag access for all controllers
        for (PDESliderControllerAssociator associator : mSliderControllerBag) {

            // release access
            associator.sliderController.releaseDragAccessForSlider(this);
        }
    }


    /**
     * @brief Release Drag Access if this View is detached from it's window
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // release drag access
        releaseAllDragAccesses();
    }

//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @param width     New width.
     * @param height    New height.
     * @param oldWidth  Old width.
     * @param oldHeight Old height.
     * @brief Size changes will cause a new initialization of the slider content
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {

        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // any change
        if (oldWidth == width && oldHeight == height) return;

        // initialize content
        requestSliderContentInit();
    }


    /**
     * @return the needed padding
     * @brief Returns the padding the component needs to be displayed correctly.
     *
     * Some things like an outer shadow have to be drawn outside of the layer bounds.
     * So the View that holds the element has to be sized bigger than the element bounds.
     * For a proper layout the view must be extended to each direction by the value delivered by
     * this function.
     */
    public Rect getNeededPadding() {
        // check if content exists
        if (mSliderContent == null) return new Rect(0, 0, 0, 0);

        // get needed padding of the content
        return mSliderContent.getSliderContentPadding();
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

        if (isEnabled()) {
            float progress = getSliderControllerForId(0).getSliderPosition();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (progress <= 0) break;
                    getSliderControllerForId(0).setSliderPosition(progress - mKeyProgressIncrement, true);
                    //onKeyChange();
                    return true;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (progress >= 1.0f) break;
                    getSliderControllerForId(0).setSliderPosition(progress + mKeyProgressIncrement, true);
                    //onKeyChange();
                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    //----- Focus handling -----------------------------------------------------------------------------------------------


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        // focus handling for agent controller handling
        if (gainFocus) {
            // set focus indication
            getScrollHandler().addHighlight();
        } else {
            // remove focus indication
            getScrollHandler().removeHighlight();
        }
    }


    /**
     * @brief Accessibility function which is called by the system API level >= 14.
     */
    @SuppressLint("NewApi")
    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);

        event.setClassName(SeekBar.class.getName());

        event.setItemCount(100);
        event.setCurrentItemIndex((int) (getSliderControllerForId(0).getSliderPosition() * 100.0f));
    }


    /**
     * @brief Accessibility function which is called by the system API level >= 14.
     */
    @SuppressLint("NewApi")
    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);

        // className makes the textToSpeech system say the right type (in the right language)
        info.setClassName(SeekBar.class.getName());

        if (isEnabled()) {
            final float progress = getSliderControllerForId(0).getSliderPosition();
            if (progress > 0) {
                info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
            }
            if (progress < 1.0f) {
                info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
        }
    }


    @SuppressLint("NewApi")
    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        if (!isEnabled()) {
            return false;
        }

        final float progress = getSliderControllerForId(0).getSliderPosition();
        final float increment = 0.2f;
        switch (action) {
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD: {
                if (progress <= 0) {
                    return false;
                }
                getSliderControllerForId(0).setSliderPosition(progress - increment, true);
                //onKeyChange();
                return true;
            }
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD: {
                if (progress >= 1.0f) {
                    return false;
                }
                getSliderControllerForId(0).setSliderPosition(progress + increment, true);
                //onKeyChange();
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean result = super.dispatchPopulateAccessibilityEvent(event);

        List<CharSequence> text = event.getText();

        // looks like we are below API 14
        if (text.isEmpty()) {
            event.setClassName(SeekBar.class.getName());
            event.setItemCount(100);
            event.setCurrentItemIndex((int) (getSliderControllerForId(0).getSliderPosition() * 100.0f));
        }

        return result;
    }


    /**
     * @brief Schedule a command for sending an accessibility event.
     * </br>
     * Note: A command is used to ensure that accessibility events
     * are sent at most one in a given time frame to save
     * system resources while the progress changes quickly.
     */
    private void scheduleAccessibilityEventSender() {
        if (mAccessibilityEventSender == null) {
            mAccessibilityEventSender = new AccessibilityEventSender();
        } else {
            removeCallbacks(mAccessibilityEventSender);
        }
        postDelayed(mAccessibilityEventSender, TIMEOUT_SEND_ACCESSIBILITY_EVENT);
    }


    /**
     * @brief Command for sending an accessibility event.
     */
    private class AccessibilityEventSender implements Runnable {
        public void run() {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        }
    }

//----- Android Persistence --------------------------------------------------------------------------------------------


    /**
     * @brief Overwritten system function to restore slider specific values
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        ArrayList<PDESliderControllerAssociator> sliderControllerBag;
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        // get saved controller bag
        sliderControllerBag = ss.savedSliderControllerBag;

        //set sliders
        for (PDESliderControllerAssociator associator : sliderControllerBag) {
            setSliderControllerForId(associator.sliderController, associator.sliderControllerId);
        }
    }


    /**
     * @brief Overwritten system function to store slider specific values.
     * E.g. for Activity restoring.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        // save slider controller bag which needs to be restored
        ss.savedSliderControllerBag = mSliderControllerBag;

        return ss;
    }
}
