/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.agents;


//----------------------------------------------------------------------------------------------------------------------
//  AgentController adapters
//----------------------------------------------------------------------------------------------------------------------


import de.telekom.pde.codelibrary.ui.components.lists.PDEListItem;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;

import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.lang.ref.WeakReference;

/**
 * @brief Adapter for button style UI elements to AgentController
 *
 * This class links an AgentController to a UIControl. It consumes
 * the button's events and passes them on to the AgentController in a
 * suitable form.
 */
public class PDEAgentControllerAdapterView implements PDEIEventSource {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEAgentControllerAdapterView.class.getName();
    private final static boolean DEBUG = false;

    protected PDEAgentController mAgentController;
    protected WeakReference<View> mView;
    protected boolean mHighlight;
    protected boolean mDown;
    // Special Handling for List
    protected boolean mIsListItem;
    protected boolean mTouchDownMode;
    protected Runnable mPendingCheckForTap;
    protected Handler mHandler;
    protected View mTouchDownView;
    protected MotionEvent mDownEvent;

    /**
     * @brief Event source functionality
     */
    private PDEEventSource mEventSource;

    /**
     * @brief HitRect helper
     */
    //private Rect mHitRectView;

    /**
     * @brief Constructor.
     */
    public PDEAgentControllerAdapterView() {
        // init
        mAgentController = null;
        mView = null;
        mHighlight = false;
        mDown = false;
        //mHitRectView = new Rect();
        mIsListItem = false;
        mTouchDownMode = false;
        mPendingCheckForTap = null;
        mHandler = null;
        mTouchDownView = null;
        mDownEvent = null;

        mEventSource = new PDEEventSource();
    }

    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }

    @Override
    public Object addListener(Object target, String methodName) {
        return mEventSource.addListener(target, methodName);
    }

    @Override
    public Object addListener(Object target, String methodName, String eventMask) {
        return mEventSource.addListener(target, methodName, eventMask);
    }

    //----- properties -----

    /**
     * brief Link to the AgentController.
     *
     * The AgentController is retained by the link. The application does not need to
     * hold a reference to the AgentController.
     */
    //@property (readonly,nonatomic,retain) PDEAgentController *agentController;
    public PDEAgentController getAgentController() {
        return mAgentController;
    }

    public void setAgentController(PDEAgentController controller) {
        mAgentController = controller;
    }


    /**
     * brief Link to the UIControl.
     */
    //@property (readonly,nonatomic,weak) UIControl *control;
    public View getView() {
        if (mView == null) {
            return null;
        }
        return mView.get();
    }


    public void setView(View view) {
        mView = new WeakReference<View>(view);
    }

//----- global functions -----

    /**
     * @brief Link an AgentController to a UIControl element.
     */
    public void linkAgent(PDEAgentController agentController, View view) {
        // remember linked objects (this uses the setters, which automatically unlink the old ones)
        setAgentController(agentController);
        setView(view);
        // security
        if (getView() == null) {
            return;
        }
        // check if we're clickable
        if (!getView().isClickable()) {
            Log.w(LOG_TAG, "linkAgent: The given View is not clickable! This adapter only works correctly with " +
                           "clickable views");
        }
        //getView().getHitRect(mHitRectView);

        // check if the delivered View is a item of our PDEListView
        if (view instanceof PDEListItem){
            mIsListItem = true;
        } else {
            mIsListItem = false;
        }
        // set touch listener
        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                int currentXPosition = Math.round(motionEvent.getX());
                int currentYPosition = Math.round(motionEvent.getY());
                Rect hitRect = new Rect();

                // get current hit rect!
                getView().getHitRect(hitRect);
                // motion event gives coordinates relative to current view -> update it
                currentXPosition += hitRect.left;
                currentYPosition += hitRect.top;

//                Log.d(LOG_TAG, "position: "+currentXPosition+", "+currentYPosition);

                // react on interesting motion events
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (DEBUG) Log.d(LOG_TAG,"Agent: ActionDown");
                        // if the view is a List-Item it should have a tap-timeout before it really reacts (like it is
                        // also handled in the native android lists). So we remember the touchdown by setting the
                        // touchdown-mode and check after a timeout if we're still in touchdown-mode. If we're still
                        // in touchdown-mode the tap is accepted and we start the wisual reaction. Cancel- and
                        // Up-Events during the timeout reset the touchdown-mode and therefore cancel the tap.
                        // If the view is not an element of a list it can react immediately.
                        if (mIsListItem){
                            // set touch mode to true
                            mTouchDownMode = true;
                            // remember the view that was touched down and the motion event
                            mTouchDownView = view;
                            mDownEvent = motionEvent;
                            // create the check for a valid tap
                            mPendingCheckForTap = new CheckForTap();
                            // post the tap check in order to be processed later on
                            postDelayed(mPendingCheckForTap,ViewConfiguration.getTapTimeout());
                        } else {
                            // if we're not part of a list, we can react immediately
                            actionTouchDown(view, motionEvent);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // Todo: find test case to proof cancel-message
                        doCancel(view,motionEvent);
//                        if (DEBUG) Log.d(LOG_TAG,"Agent: ActionCancel");
//                        // if we're part of a list and the touch down already occured we have to cancel the pending
//                        // check for a valid tap and reset the touch mode
//                        if (mIsListItem && mTouchDownMode){
//                            // reset touch mode to false
//                            mTouchDownMode = false;
//                            // remove pending tap checks
//                            mHandler.removeCallbacks(mPendingCheckForTap);
//                        }
//                        actionTouchCancel(view, motionEvent);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (DEBUG) Log.d(LOG_TAG,"Agent: ActionUp");
                        // if we're part of a list and the touch down already occured we have to cancel the pending
                        // check for a valid tap and reset the touch mode
                        if (mIsListItem && mTouchDownMode){
                            // reset touch mode to false
                            mTouchDownMode = false;
                            // remove pending tap checks
                            mHandler.removeCallbacks(mPendingCheckForTap);
                            // So, the touch down already occurred and we waited on the pending tap check. When the
                            // touch up event occurred during this time of waiting and occurred on the same view as the
                            // touch down event, we have a valid "quick tap". So we'll have to start the touch down
                            // action immediately before the touch up action gets started.
                            if (view == mTouchDownView){
                                // start touch down action with the data we remembered before.
                                actionTouchDown(mTouchDownView, mDownEvent);
                            }

                        }
                        // check if we released inside or outside of the view
                        if (hitRect.contains(currentXPosition, currentYPosition)) {
                            actionTouchUpInside(view, motionEvent);
                        } else {
                            actionTouchUpOutside(view, motionEvent);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (DEBUG) Log.d(LOG_TAG,"Agent: ActionMove");
                        // check if we enter or leave the view with this move (--> change of highlight)
                        if (hitRect.contains(currentXPosition, currentYPosition)) {
                            if (!mHighlight) {
                                actionTouchDragEnter(view, motionEvent);
                            }
                        } else {
                            if (mHighlight) {
                                actionTouchDragExit(view, motionEvent);
                            }
                        }
                        break;
                }

                return false;
            }
        });

        // and register our own event source as event forwarder (all events)
        getEventSource().forwardEvents(getAgentController().getEventSource());
    }


    /**
     * @brief Handle UIControl actions.
     *
     * A touch implies that the finger is inside the object, so we're highlighted first and then
     * pressed
     */
    private void actionTouchDown(Object sender, InputEvent event)  // ToDo: MotionEvent???
    {
        // we‘re highlighted when we're first touched
        if (!mHighlight) {
            // remember
            mHighlight = true;
            // pass on to agent
            if (getAgentController() != null) {
                getAgentController().addHighlight();
            }
        }

        // and we're pressed
        if (!mDown) {
            // remember
            mDown = true;
            // pass on to agent
            if (getAgentController() != null) {
                getAgentController().addPress();
            }
        }
    }


    /**
     * @brief Handle UIControl actions.
     *
     * We enter the active area from outside while still pressed.
     */
    private void actionTouchDragEnter(Object sender, InputEvent event) {
        // are we really down? Otherwise we don't react
        if (!mDown) {
            return;
        }

        // take the highlight again
        if (!mHighlight) {
            // remember
            mHighlight = true;
            // pass on to agent
            if (getAgentController() != null) {
                getAgentController().addHighlight();
            }
        }
    }


    /**
     * @brief Handle UIControl actions.
     *
     * We leave the active area while still pressed.
     */
    private void actionTouchDragExit(Object sender, InputEvent event) {
        // are we really down? Otherwise we don't react
        if (!mDown) {
            return;
        }

        // loose the highlight
        if (mHighlight) {
            // remember
            mHighlight = false;
            // pass on to agent
            if (getAgentController() != null) {
                getAgentController().removeHighlight();
            }
        }
    }


    /**
     * @brief Handle UIControl actions.
     *
     * We are released while the finger is still inside. This results
     * in a successful tap.
     */
    private void actionTouchUpInside(Object sender, InputEvent event) {
        // are we really down? Otherwise we don't react
        if (!mDown) {
            return;
        }

        // we're not down any more
        mDown = false;

        // tell the agent to perform the press
        if (getAgentController() != null) {
            getAgentController().doPress();
        }

        // loose the highlight
        if (mHighlight) {
            // remember
            mHighlight = false;
            // pass on to agent
            if (getAgentController() != null) {
                getAgentController().removeHighlight();
            }
        }
    }


    /**
     * @brief Handle UIControl actions.
     *
     * We are released while the finger is outside. The press is cancelled.
     */
    private void actionTouchUpOutside(Object sender, InputEvent event) {
        // are we really down? Otherwise we don't react
        if (!mDown) {
            return;
        }

        // loose the highlight first if necessary
        if (mHighlight) {
            // remember
            mHighlight = false;
            // pass on to agent
            if (getAgentController() != null) {
                getAgentController().removeHighlight();
            }
        }

        // we're not down any more
        mDown = false;

        // tell the agent to cancel the press
        if (getAgentController() != null) {
            getAgentController().cancelPress();
        }
    }


    /**
     * @brief Handle UIControl actions.
     *
     * The touch is cancelled (e.g. by an overlying gesture). Cancel the press.
     */
    private void actionTouchCancel(Object sender, InputEvent event) {
        // are we really down? Otherwise we don't react
        if (!mDown) {
            return;
        }

        // loose the highlight first if necessary
        if (mHighlight) {
            // remember
            mHighlight = false;
            // pass on to agent
            if (getAgentController() != null) {
                getAgentController().removeHighlight();
            }
        }

        // we're not down any more
        mDown = false;

        // tell the agent to cancel the press
        if (getAgentController() != null) {
            getAgentController().cancelPress();
        }
    }




// -------- List Specials ----------------------------------------------------------------------------------------------

    /**
     * @brief Causes the Runnable r to be added to the message queue, to be run after the specified amount of time elapses.
     *
     * Convenience function.
     * We use this mechanism in order to start delayed the runnable which checks if we're still in touchdown-mode.
     *
     * @param r The Runnable that will be executed.
     * @param delayMillis The delay (in milliseconds) until the Runnable will be executed.
     *
     * @return Returns true if the Runnable was successfully placed in to the message queue.
     */
    protected boolean postDelayed(Runnable r, long delayMillis) {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler.postDelayed(r,delayMillis);
    }


    /**
     * @brief Helper class for a delayed check for a Tap.
     *
     * We set the touchdown-mode to true when the down-event occurs. Then we post this Runnable into the message
     * queue in order to be started after a given delay. When the timeout has passed the Runnable gets started,
     * checks if touchdownd-mode is still true and in this case starts the normal reaction to an accepted tap.
     * If events occurred that resetted the touchdown-mode in the meantime, there will be no reaction at all.
     */
    final class CheckForTap implements Runnable {
        /**
         * @brief constructor.
         */
        public CheckForTap(){
        }

        /**
         * @brief the actual tap check.
         */
        public void run() {
            if (mTouchDownMode) {
                // reset mode
                mTouchDownMode = false;
                // start action
                actionTouchDown(mTouchDownView, mDownEvent);
            }
        }
    }


    /**
     * @brief Public callable handling of a cancel event (hack).
     *
     * On Devices below Android 4.0 the event handling doesn't work as intended, so for proper handling of Cancel we
     * have to be able to call the cancel-eventhandling manually (not by eventsystem). I don't like this workaround,
     * but as long as we have to deal with lower devices it seems to be the only way.
     *
     * @param view the view that received the touch event.
     * @param motionEvent the received touch event.
     */
    public void doCancel(View view, MotionEvent motionEvent) {
        if (DEBUG) Log.d(LOG_TAG,"Agent: ActionCancel");
        // if we're part of a list and the touch down already occured we have to cancel the pending
        // check for a valid tap and reset the touch mode
        if (mIsListItem && mTouchDownMode){
            // reset touch mode to false
            mTouchDownMode = false;
            // remove pending tap checks
            mHandler.removeCallbacks(mPendingCheckForTap);
        }
        actionTouchCancel(view, motionEvent);
    }
}
