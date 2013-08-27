
/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;


/// @cond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED

//----------------------------------------------------------------------------------------------------------------------
//  PDESlider
//----------------------------------------------------------------------------------------------------------------------



import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;
import java.util.ArrayList;


/**
 * @brief   This is a Slider, controlled by slider events.
 *
 * The slider does nothing itself, it just manages contents which do the actual animation.
 * PDESlider offers a few predefined content variants as preset, but does not prevent you using your own graphics.
 */
public class PDESlider extends PDEAbsoluteLayout implements PDEIEventSource {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDESlider.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;



//----------------------------------------------------------------------------------------------------------------------
//  PDESlider helper classes
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
        public void writeToParcel(Parcel out, int flags) {
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



//----------------------------------------------------------------------------------------------------------------------
//  PDESlider constants
//----------------------------------------------------------------------------------------------------------------------



    //----- constants -----


    /**
     * @brief Telekom well known slider contents.
     */
    public enum PDESliderContentType {
        /* ATTENTION: IF YOU ADD A NEW TYPE HERE, DON'T FORGET TO ADD IT IN ATTRS.XML AS WELL!!!!!
            AND IF YOU ADD IT IN BETWEEN, YOU HAVE TO ADJUST THE INDEXES OF ALL FOLLOWING TYPES IN ATTRS.XML AS WELL!!!
         */
        ProgressBar,
        ScrollbarHorizontal
    }


    //----- properties -----

    // content
    private PDESliderContentInterface mSliderContent;
    private PDESliderContentType mSliderContentType;

    // controller
    private ArrayList<PDESliderControllerAssociator> mSliderControllerBag;

    // Eventsource
    protected PDEEventSource mEventSource;

    // helper
    private PDESliderContentInterface mSliderContentToInitialize;
    private boolean mContentNeedsInitialization;


    /**
     * @brief Constructor for PDESlider
     *
     * @param context
     */
    public PDESlider(android.content.Context context) {
        super(context);
        init(null);
    }


    /**
     * @brief Constructor for PDESlider
     *
     * @param context
     * @param attrs
     */
    public PDESlider(android.content.Context context, android.util.AttributeSet attrs) {
        super(context,attrs);
        if(DEBUGPARAMS){
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                Log.d(LOG_TAG, "PDEButton-Attr(" + i + "): " + attrs.getAttributeName(i) + " => " + attrs.getAttributeValue(i));
            }
        }
        init(attrs);
    }


    /**
     * @brief Constructor for PDESlider
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PDESlider(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }


    /**
     * @brief Internal initialisation.
     *
     * Create necessary graphics, start with a default configuration.
     */
    protected void init(AttributeSet attrs) {

        PDESliderController defaultController;

        // init
        mEventSource = new PDEEventSource();
        mSliderContent = null;
        mSliderControllerBag = new ArrayList<PDESliderControllerAssociator>();
        mContentNeedsInitialization = false;

        // create and set default slider controller
        defaultController = new PDESliderController();
        setSliderControllerForId(defaultController, 0);

        setClipChildren(true);
        setBackgroundColor(Color.BLUE);

        LayoutInflater.from(getContext()).inflate(R.layout.pdeslider, this, true);

        // set Slider Content
        if (attrs != null) {
            TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDESlider);

            // create content
            if (sa.hasValue(R.styleable.PDESlider_contentType)) {

                setSliderWithContentType(sa.getInt(R.styleable.PDESlider_contentType, 0));
            }

            sa.recycle();
        }
    }


//----- content handling --------------------------------------------------------------------------------------------------


    /**
     * @brief Set Slider Content with Integer Content type
     *
     * @param contentType
     */
    private void setSliderWithContentType(int contentType) {
        setSliderWithContentType(PDESliderContentType.values()[contentType]);
    }


    /**
     * @brief   Set the new slider content. Using the content Type
     *          Removes the old content.
     *
     * @param   contentType A slider content type constant
     */
    public void setSliderWithContentType(PDESliderContentType contentType) {
        PDESliderContentInterface content = null;

        // any change?
        if (contentType == mSliderContentType) {
            return;
        }

        // remove old content from the layer if it exists
        if (mSliderContent != null) {
            ((ViewGroup)mSliderContent.getLayer().getParent()).removeView(mSliderContent.getLayer());
        }

        // create and set the new content, remember type
        switch (contentType) {
            case ProgressBar:
                content = new PDESliderContentProgressBar(getContext());
                break;
            case ScrollbarHorizontal:
                content = new PDESliderContentScrollbarHorizontal(getContext());
                break;
            default:
                //error
                content = null;
                break;
        }

        // success ?
        if (content == null) {
            return;
        }

        // remember
        mSliderContent = content;
        mSliderContentType = contentType;

        // add the content to the layer
        ((ViewGroup)findViewById(R.id.pdeslider_content_slot)).addView(mSliderContent.getLayer(),new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // new content needs to be initialized with parameters
        mContentNeedsInitialization = true;
    }


    /**
     * @brief request initialization Events for a new slider content.
     */
    private void initializeSliderContent() {

        // security check
        if (mSliderContent == null) return;

        // send complete slider initialization to set the contents values
        mSliderContentToInitialize = mSliderContent;

        // request initialization for all controller
        for (PDESliderControllerAssociator associator: mSliderControllerBag) {
            associator.sliderController.getEventSource().requestOneTimeInitialization(this,"cbSliderControllerSingle",
                    PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK);
        }

        // done
        mSliderContentToInitialize = null;
    }


//----- Controller Linkage ---------------------------------------------------------------------------------------------


    /**
     * @brief   Add a given Slider Controller for the given Id to the controller bag Array.
     *          If the given id is already used this will remove
     *          and clean up the old controller for the given id.
     *          Handing over nil for controller will only remove the controller on given id.
     *
     * @param   controller    A Slider controller to set
     * @param   controllerId  Id to associate the slider controller
     */
    public void setSliderControllerForId(PDESliderController controller, int controllerId) {

        PDESliderControllerAssociator oldAssociator;
        PDESliderControllerAssociator newAssociator;
        int count;

        // init
        oldAssociator = null;
        count = 0;

        // check if a old controller exists for the given id
        for (PDESliderControllerAssociator associator: mSliderControllerBag) {

            // remember old associator
            if (associator.sliderControllerId == controllerId) oldAssociator = associator;
        }

        // if old Associator exists make a clean up
        if (oldAssociator != null) {

            // check if there is only one listener for this controller
            for (PDESliderControllerAssociator associator: mSliderControllerBag) {

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
            for (PDESliderControllerAssociator associator: mSliderControllerBag) {

                // count same controller
                if (associator.sliderController == controller) count++;
            }

            // add Listener for new Controller if there is not already a listener
            if (count == 1) controller.addListener(this, "cbSliderController");
        }
    }


    /**
     * @brief   Get the Slider Controller for the given ID.
     *          Creates and hands out a new Controller if there is no Controller on give id.
     *
     * @return                  A Slider controller associated to given Id.
     * @param   controllerId    Id to get assoicated Controller.
     */
    public PDESliderController getSliderControllerForId(int controllerId) {

        PDESliderController controller;

        // init
        controller = null;

        // check if a controller exists for the given id
        for (PDESliderControllerAssociator associator: mSliderControllerBag) {

            // get Slider controller
            if (associator.sliderControllerId == controllerId) controller = associator.sliderController;
        }

        // return controller if it exists
        if (controller != null) return controller;

        // init new Controller
        controller = new PDESliderController();

        // set for given id
        setSliderControllerForId(controller,controllerId);

        // done
        return controller;
    }


    // needs to be public otherwise it cannot be called from eventsource (11.10.2012)

    /**
     * @brief   Called on changes from the slider controllers.
     *
     * @param   event PDEEvent
     */
    public void cbSliderController(PDEEvent event) {

        // inform listeners tha a change will be made
        if (event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK_DATA)) {

            // set id of sending controller
            replaceControllerIdForEvent(event);

            ((PDEEventSliderControllerState) event).printEvent();

            // send event to listeners
            mEventSource.sendEvent(event);
        }

        // send event to change the content
        else if (event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK_ACTION_DID_CHANGE)) {

            // set id of sending controller
            replaceControllerIdForEvent(event);

            ((PDEEventSliderControllerState) event).printEvent();

            // send event to content
            sendSliderEvent(event);
        }
    }


    // needs to be public otherwise it cannot be called from eventsource (11.10.2012)

    /**
     * @brief   Called on specially requested initializations from our slider controllers.
     *
     * @param   event PDEEvent
     */
    public void cbSliderControllerSingle(PDEEvent event) {

        // do nothing if no initialization layer is defined
        if (mSliderContentToInitialize == null) return;

        // send to the content if it's an event we want to listen
        if (event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK_ACTION_INITIALIZE)) {

            // set id of sending controller
            replaceControllerIdForEvent(event);
            ((PDEEventSliderControllerState) event).printEvent();

            // send to content
            sendSliderEvent(event);
        }
    }


    /**
     * @brief   Send a slider Event to the content
     *          Checks the sender of the event and replaces it's id into the event
     *
     * @param   event PDEEvent
     */
    private void sendSliderEvent(PDEEvent event) {

        // does the content exist
        if (mSliderContent == null) return;

        // send event to content
        mSliderContent.sliderEvent(event);
    }


    /**
     * @brief   Checks the sender of the event and replaces it's id into the given event.
     *
     * @param   event PDEEvent
     */
    private void replaceControllerIdForEvent(PDEEvent event) {

         PDEEventSliderControllerState slideEvent;

        // get the sender of the event
        for (PDESliderControllerAssociator associator: mSliderControllerBag) {

            // get sending Slider Controller
            if (associator.sliderController == event.getSender()) {

                // cast event to set new Variables
                slideEvent = (PDEEventSliderControllerState) event;

                // set id
                slideEvent.setSliderControllerId(associator.sliderControllerId);
            }
        }
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
     * @brief: Add event Listener.
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
     * @brief: Add event Listener.
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


    public boolean removeListener(Object listener) {
        return mEventSource.removeListener(listener);
    }


//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief   Layouting will cause a new initialization of the slider only, if it's size or content has changed.
     *
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed,left,top,right,bottom);

        // initialize after size change
        if (changed) {
            mContentNeedsInitialization = false;
            initializeSliderContent();
        }

        // initialize after content change
        else if (mContentNeedsInitialization) {
            mContentNeedsInitialization = false;
            initializeSliderContent();
        }
    }


//----- Android Persistence --------------------------------------------------------------------------------------------


    /**
     * @brief Overwritten system function to restore slider specific values
     * @param state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        ArrayList<PDESliderControllerAssociator> sliderContollerBag;

        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        // get saved controller bag
        sliderContollerBag = ss.savedSliderControllerBag;

        //set sliders
        for (PDESliderControllerAssociator associator: sliderContollerBag) {
            setSliderControllerForId(associator.sliderController, associator.sliderControllerId);
        }
    }


    /**
     * @brief Overwritten system function to store slider specific values.
     * E.g. for Activity restoring.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState =  super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        // save slider controller bag which needs to be restored
        ss.savedSliderControllerBag = mSliderControllerBag;

        return ss;
    }

}

/// @endcond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED