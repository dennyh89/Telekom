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


import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;

import android.graphics.Rect;
import android.util.Log;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * @brief Adapter for button style UI elements to AgentController
 *
 * This class links an AgentController to a UIControl. It consumes
 * the button's events and passes them on to the AgentController in a
 * suitable form.
 */
public class PDEAgentControllerAdapterView extends Object implements PDEIEventSource {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEAgentControllerAdapterView.class.getName();

    protected PDEAgentController mAgentController;
    protected WeakReference<View> mView;
    protected boolean mHighlight;
    protected boolean mDown;

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
                        actionTouchDown(view, motionEvent);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // Todo: find test case to proof cancel-message
                        actionTouchCancel(view, motionEvent);
                        break;
                    case MotionEvent.ACTION_UP:
                        // check if we released inside or outside of the view
                        if (hitRect.contains(currentXPosition, currentYPosition)) {
                            actionTouchUpInside(view, motionEvent);
                        } else {
                            actionTouchUpOutside(view, motionEvent);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
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
}
