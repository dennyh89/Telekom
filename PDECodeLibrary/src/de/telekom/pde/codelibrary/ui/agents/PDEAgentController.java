/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.agents;



//----------------------------------------------------------------------------------------------------------------------
//  PDEAgentController
//----------------------------------------------------------------------------------------------------------------------


import de.telekom.pde.codelibrary.ui.animation.PDEAnimationGroup;
import de.telekom.pde.codelibrary.ui.animation.PDEAnimationRoot;
import de.telekom.pde.codelibrary.ui.animation.PDELinearAnimation;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSourceDelegate;

import android.util.Log;
import de.telekom.pde.codelibrary.ui.timing.PDEFrameTiming;

import java.util.LinkedList;

/**
 * @brief The controlling logic for all agents
 *
 * All Agent state transitions are (for the moment) collected in this one class, which
 * handles standardized input and outputs state information and generates the necessary
 * events. Which behaviour an actual Agent then exposes is up to configuration. The default
 * Agent is a button.
 *
 * It's not certain that this will stay this way - eventually it will make more sense to
 * build specialized agent behaviours.
 */
public class PDEAgentController extends Object implements PDEIEventSource, PDEIEventSourceDelegate {

//----------------------------------------------------------------------------------------------------------------------
//  Configuration
//----------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEAgentController.class.getName();
    private final static boolean DEBUGPARAMS = false;

    // debug configurations
    //
    private final static boolean DEBUG_ACTIONS = false;
    private final static boolean DEBUG_COUNTS = false;
    private final static boolean DEBUG_STATE = false;
    private final static boolean DEBUG_VALUES = false;
    private final static boolean DEBUG_TIMING = false;

    // additional debugging (can also be achieved by manipulating global time factor); set to 0 to enforce immediate
    // actions
    //
    public static final double TIME_STRETCH = 1.0;

    // default values
    //
    public static final long INTERACTIVE_ATTACK_TIME = Math.round(50*TIME_STRETCH);
    public static final long INTERACTIVE_DECAY_TIME = Math.round(150*TIME_STRETCH);
    public static final long ACTION_SHOW_TIME = Math.round(50*TIME_STRETCH);
    public static final long STATECHANGETIME = Math.round(150*TIME_STRETCH);




    // Events

    /**
     * @brief Event mask for all PDEAgentController events.
     */
    public static final String PDE_AGENT_CONTROLLER_EVENT_MASK ="PDEAgentController.*";

    /**
     * @brief Event mask for all PDEAgentController animation events.
     */
    public static final String PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION ="PDEAgentController.animation.*";

    /**
     * @brief Event mask for all actions.
     */
    public static final String PDE_AGENT_CONTROLLER_EVENT_MASK_ACTION = "PDEAgentController.action.*";
    public static final String PDE_AGENT_CONTROLLER_EVENT_ANIMATION_INITIALIZE = "PDEAgentController.animation.initialize";
    public static final String PDE_AGENT_CONTROLLER_EVENT_ANIMATION_BEGIN_STATE_CHANGE = "PDEAgentController.animation.beginStateChange";
    public static final String PDE_AGENT_CONTROLLER_EVENT_ANIMATION_END_STATE_CHANGE = "PDEAgentController.animation.endStateChange";

    /**
     * @brief Event is sent after some animation property has changed. Event class is PDEEvent.
     *
     * Data sent is PDEAgentControllerEventState.
     */
    public static final String PDE_AGENT_CONTROLLER_EVENT_ANIMATION_DID_CHANGE ="PDEAgentController.animation.didChange";
    public static final String PDE_AGENT_CONTROLLER_EVENT_ACTION_BEGIN_INTERACTION = "PDEAgentController.action.beginInteraction";
    public static final String PDE_AGENT_CONTROLLER_EVENT_ACTION_END_INTERACTION = "PDEAgentController.action.endInteraction";

    /**
     * @brief Event is sent when user activates the agent.
     *
     * Activation usually happens when the user klicks on the agent. The agent stays active as long
     * as the click is active and the mouse is over the input area. The active status can be used
     * for behaviours like for e.g. forward buttons in music players. Don't use end of activation for
     * triggering events! Use DTAgentControllerActionSelected or DTAgentControllerActionWillBeSelected instead.
     *
     * No special data is sent along.
     */
    public static final String PDE_AGENT_CONTROLLER_EVENT_ACTION_ACTIVATED ="PDEAgentController.action.activated";
    /**
     * @brief Event is sent when user deactivates the agent.
     *
     * No special data is sent along.
     */
    public static final String PDE_AGENT_CONTROLLER_EVENT_ACTION_DEACTIVATED ="PDEAgentController.action.deactivated";
    /**
     * @brief Event is sent when the agent was successfully selected.
     *
     * The event is after the animation has successfully displayed the active state to the user. Normally,
     * you would use this event for all interaction, since it ensures that there's a proper visual
     * response to the user's action. Especially use this event when the action triggers a removal of the
     * agent (e.g. by switching to another screen).
     *
     * No special data is sent along.
     */
    public static final String PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED ="PDEAgentController.action.selected";
    /**
     * @brief Event is sent immediately when user successfully selects the agents.
     *
     * The agent system still needs some time to display the selection to the user. Use this event only if the
     * selection triggers another action inside the same screen, and the agent has the opportunity of finishing
     * it's animations afterwards. Examples would be a scrolldown button, where the scrolling can start while the
     * agent displays the selection.
     *
     * No special data is sent along.
     */
    public static final String PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED ="PDEAgentController.action.willBeSelected";



    /**
     * @brief Internal agent controller states.
     */
    protected static final int PDE_AGENT_CONTROLLER_STATE_IDLE = 0;
    protected static final int PDE_AGENT_CONTROLLER_STATE_INTERACTIVE = 1;
    protected static final int PDE_AGENT_CONTROLLER_STATE_SHOWING = 2;
    protected static final int PDE_AGENT_CONTROLLER_STATE_UNDERSTANDING = 3;
    protected static final int PDE_AGENT_CONTROLLER_STATE_DOING = 4;
    protected static final int PDE_AGENT_CONTROLLER_STATE_DONE = 5;



    protected static final int PDE_AGENT_CONTROLLER_ACTION_GENERIC = 0;
    protected static final int PDE_AGENT_CONTROLLER_ACTION_FOCUS = 1;
    protected static final int PDE_AGENT_CONTROLLER_ACTION_UNFOCUS = 2;
    protected static final int PDE_AGENT_CONTROLLER_ACTION_HIGHLIGHT = 3;
    protected static final int PDE_AGENT_CONTROLLER_ACTION_UNHIGHLIGHT = 4;
    protected static final int PDE_AGENT_CONTROLLER_ACTION_PRESS = 5;
    protected static final int PDE_AGENT_CONTROLLER_ACTION_DO_PRESS = 6;
    protected static final int PDE_AGENT_CONTROLLER_ACTION_CANCEL_PRESS = 7;


    // pending actions
    LinkedList<Object> mPendingActions;

    // state helpers
    boolean mStateAnimationReversed;

    // UI counting
    int mNumFocus;
    int mNumHighlights;
    int mNumPresses;

    // internal states
    boolean mInternalFocus;
    boolean mInternalHighlight;
    boolean mInternalPress;
    int mInternalSelections;

    // outside state management
    boolean mOutsideActive;
    boolean mOutsideActiveBracket;
    int mPendingSelections1;
    int mPendingSelections2;

    // main state machine
    int mAgentState;
    boolean mInputCheckScheduled;

    // UI animations
    PDEAnimationGroup mAnimations;
    PDELinearAnimation mFocusAnimation;
    PDELinearAnimation mHighlightAnimation;
    PDELinearAnimation mFocusAndHighlightAnimation;
    PDELinearAnimation mPressAnimation;
    PDELinearAnimation mDownAnimation;
    PDELinearAnimation mInteractionAnimation;
    PDELinearAnimation mStateAnimation;

    /**
     * @brief The agent's state.
     *
     * States don't interfere with the agent's main agentstate logic (taking input, doing, etc.). They are
     * a higher level of animation and performed by the agent regardless of other states. The main reason for them
     * being in the agent is that it's a convenient place to manage them, and that sometimes state changes must be
     * delayed until the agent is in a state where it can actually react to them.
     */
    private String mState;

    /**
     * @brief The agent's current state.
     *
     * May be behind the state stored in the main state. Used for state animations.
     */
    private String mCurrentState;

    /**
     * @brief The agent's next state.
     *
     * Only valid when a state animation is in progress.
     */
    private String mNextState;

    // our own timing
    boolean mTiming;

    /**
     * @brief Time it takes the agent to change main states.
     */
    long mStateChangeTime;

    /**
     * @brief PDEEventSource instance that provides the event sending behaviour
     */
    private PDEEventSource mEventSource;



    //----- properties -----

    // configuration

    /**
     * @brief Time it takes all interactive elements to reach full active state.
     *
     * Interactive elements are focus, highlight, etc. and all state transitions between idle, focus,
     * taking input etc. in forward direction
     */
    protected long mInteractiveAttackTime;

    public void setInteractiveAttackTime(long time){
        mInteractiveAttackTime = time;
    }

    public long getInteractiveAttackTime(){
        return mInteractiveAttackTime;
    }

    /**
     * @brief Time it takes to all interactive elements to go back to base state.
     *
     * For state transitions, the time to backward-transition in the agent state model.
     */
    protected long mInteractiveDecayTime;

    public void setInteractiveDecayTime(long time){
        mInteractiveDecayTime = time;
    }

    public long getInteractiveDecayTime(){
        return mInteractiveDecayTime;
    }

    /**
     * @brief Time the agent must stay in taking input/pressed state as a minimum.
     *
     * To show the users that something was activated. Otherwise a short press would just start animating, and then
     * go back to idle, without much visual change.
     */
    protected long mActionShowTime;

    public void setActionShowTime(long time){
        mActionShowTime = time;
    }

    public long getActionShowTime(){
        return mActionShowTime;
    }


    // animation properties

    /**
     * @brief Animation between state and nextState. Range is 0..1
     */
    protected double mStateAnimationProgress;
    public double getStateAnimationProgress(){
        return mStateAnimationProgress;
    }
    public void setStateAnimationProgress(double stateAnimationProgress){
        mStateAnimationProgress = stateAnimationProgress;
    }


    /**
     * @brief Focus animation. Range is 0..1
     */
    protected double mAgentAnimationFocus;
    public double getAgentAnimationFocus(){
        return mAgentAnimationFocus;
    }
    public void setAgentAnimationFocus(double focus){
        mAgentAnimationFocus = focus;
    }

    /**
     * @brief Highlight animation. Range is 0..1
     */
    protected double mAgentAnimationHighlight;
    public double getAgentAnimationHighlight(){
        return mAgentAnimationHighlight;
    }
    public void setAgentAnimationHighlight(double highlight){
        mAgentAnimationHighlight = highlight;
    }


    /**
     * @brief Pressed animation. Range is 0..1
     */
    protected double mAgentAnimationPress;
    public double getAgentAnimationPress(){
        return mAgentAnimationPress;
    }
    public void setAgentAnimationPress(double press){
        mAgentAnimationPress = press;
    }


    /**
     * @brief Pressed animation. Range is 0..1
     */
    protected double mAgentAnimationDown;
    public double getAgentAnimationDown(){
        return mAgentAnimationDown;
    }
    public void setAgentAnimationDown(double down){
        mAgentAnimationDown = down;
    }

    /**
     * @brief Combined focus and highlight animation. Range is 0..1
     */
    protected double mAgentAnimationCombinedFocusAndHighlight;
    public double getAgentAnimationCombinedFocusAndHighlight(){
        return mAgentAnimationCombinedFocusAndHighlight;
    }
    public void setAgentAnimationCombinedFocusAndHighlight(double value){
        mAgentAnimationCombinedFocusAndHighlight = value;
    }

    /**
     * @brief Combination of focus/highlight and pressed animations. Range is 0..2
     *
     * This animation corresponds to the agentstate model where one value (usually color) is animated
     * through the states. 0 is "available", 1 is "focus", 2 is "taking input".
     */
    protected double mAgentAnimationCombinedInteraction;
    public double getAgentAnimationCombinedInteraction(){
        return mAgentAnimationCombinedInteraction;
    }
    public void setmAgentAnimationCombinedInteraction(double value){
        mAgentAnimationCombinedInteraction = value;
    }


    //----- functions -----


    public PDEAgentController(){
        // init
        mState = "default";
        mCurrentState = "default";
        mNextState = null;
        mStateAnimationReversed = false;
        mNumFocus = 0;
        mNumHighlights = 0;
        mNumPresses = 0;
        mInternalFocus = false;
        mInternalHighlight = false;
        mInternalPress = false;
        mInternalSelections = 0;
        mOutsideActive = false;
        mOutsideActiveBracket = false;
        mPendingSelections1 = 0;
        mPendingSelections2 = 0;
        mAgentState = PDE_AGENT_CONTROLLER_STATE_IDLE;
        mInputCheckScheduled = false;
        mStateAnimationProgress = 0.0;
        mAgentAnimationFocus = 0.0;
        mAgentAnimationHighlight = 0.0;
        mAgentAnimationPress = 0.0;
        mAgentAnimationDown = 0.0;
        mAgentAnimationCombinedFocusAndHighlight = 0.0;
        mAgentAnimationCombinedInteraction = 0.0;
        mTiming = false;

        // set default values
        mInteractiveAttackTime = INTERACTIVE_ATTACK_TIME;
        mInteractiveDecayTime = INTERACTIVE_DECAY_TIME;
        mActionShowTime = ACTION_SHOW_TIME;
        mStateChangeTime = STATECHANGETIME;

        // init substructures
        mPendingActions = new LinkedList<Object>();
        mAnimations = new PDEAnimationGroup();
        mAnimations.setDidChangeTarget(this,"changed");
        PDEAnimationRoot.addSubAnimationStatic(mAnimations);
        mFocusAnimation = new PDELinearAnimation();
        mAnimations.addSubAnimation(mFocusAnimation);
        mHighlightAnimation = new PDELinearAnimation();
        mAnimations.addSubAnimation(mHighlightAnimation);
        mFocusAndHighlightAnimation = new PDELinearAnimation();
        mAnimations.addSubAnimation(mFocusAndHighlightAnimation);
        mPressAnimation = new PDELinearAnimation();
        mAnimations.addSubAnimation(mPressAnimation);
        mDownAnimation = new PDELinearAnimation();
        mAnimations.addSubAnimation(mDownAnimation);
        mInteractionAnimation = new PDELinearAnimation();
        mAnimations.addSubAnimation(mInteractionAnimation);
        mStateAnimation = new PDELinearAnimation();
        mAnimations.addSubAnimation(mStateAnimation);

        // ToDo PDEEventSource mixin

        // create DTEventSender instance
        mEventSource = new PDEEventSource();
        // set ourselves as the default sender (optional)
        mEventSource.setEventDefaultSender(this, true); //ToDo:check if true is correct here
        // set ourselves as delegate (optional)   // ToDo: check if this is the intended usage
        mEventSource.setEventSourceDelegate(this);
    }


    /**
     * @brief Change the main state.
     */
    public void setState(String state)
    {
        // any change?
        if (mState == state) return;

        // remember
        mState = state;

        // schedule an update
        addAction(PDE_AGENT_CONTROLLER_ACTION_GENERIC);
    }

    /**
     * @brief Get the main state.
     */
    public String getState()
    {
        return mState;
    }

        // ToDo check code in iOS dealloc() (self.timing = NO)


    // interaction functions
    /**
     * @brief Add focus.
     */
    public void addFocus(){
        // count up focus
        mNumFocus++;

        // debugging
        if(DEBUG_COUNTS){
            Log.d(LOG_TAG,"Adding focus; focuscount: "+mNumFocus);
        }

        // any relevant change? -> add to pending actions
        if (mNumFocus == 1){
            addAction(PDE_AGENT_CONTROLLER_ACTION_FOCUS);
        }
    }

    /**
     * @brief Remove focus.
     */
    public void removeFocus(){
       // count down focus
        mNumFocus--;

        if(DEBUG_COUNTS){
            Log.d(LOG_TAG,"Removing focus; focuscount: "+mNumFocus);
        }
        // any relevant change? -> add to pending actions
        if(mNumFocus==0){
            addAction(PDE_AGENT_CONTROLLER_ACTION_UNFOCUS);
        }
    }

    /**
     * @brief Add highlight.
     */
    public void addHighlight(){
        // count uo highlights
        mNumHighlights++;

        // debugging
        if(DEBUG_COUNTS){
            Log.d(LOG_TAG,"Adding highlight; highlightcount: "+mNumHighlights);
        }

        // any relevant change? ->  add to pending actions
        if(mNumHighlights==1){
            addAction(PDE_AGENT_CONTROLLER_ACTION_HIGHLIGHT);
        }
    }


    /**
     * @brief Remove highlight.
     */
    public void removeHighlight(){
        // count down highlights
        mNumHighlights--;

        // debugging
        if(DEBUG_COUNTS){
            Log.d (LOG_TAG,"Removing highlight; highlightcount: "+mNumHighlights);
        }

        // any relevant change? -> add to pending actions
        if (mNumHighlights == 0) {
            addAction(PDE_AGENT_CONTROLLER_ACTION_UNHIGHLIGHT);
        }
    }

    /**
     * @brief Add a press.
     */
    public void addPress(){
        // count up presses
        mNumPresses++;

        // debugging
        if(DEBUG_COUNTS){
            Log.d (LOG_TAG,"Adding press; presscount: "+mNumPresses);
        }

        // any relevant change? -> add to pending actions
        if (mNumPresses == 1) {
            addAction(PDE_AGENT_CONTROLLER_ACTION_PRESS);
        }
    }

    /**
     * @brief Perform a press (active).
     *
     * Removes a press. The press is executed and the Agent continues eventually
     * with UNDERSTANDING. This function is called when the user untaps the
     * agent while the finger is in the active area. DoPress/CancelPress must
     * match the number of addPresses.
     *
     * UNDERSTANDING requires program interaction for non-automatic buttons.
     */
    public void doPress(){
        // count down presses
        mNumPresses--;

        // debugging
        if(DEBUG_COUNTS){
            Log.d (LOG_TAG,"Doing press; presscount: "+mNumPresses);
        }

        // any relevant change? -> add to pending actions
        if (mNumPresses==0) {
            addAction(PDE_AGENT_CONTROLLER_ACTION_DO_PRESS);
        }
    }

    /**
     * @brief Cancel a press.
     *
     * Removes a press without any actions. This function is called when the finger leaves
     * outside the active area, or some other component overrides the tap. DoPress/CancelPress must
     * match the number of addPresses.
     */
    public void cancelPress(){
        // count down presses
        mNumPresses--;

        // debugging
        if(DEBUG_COUNTS){
            Log.d (LOG_TAG,"Cancelling press; presscount: "+mNumPresses);
        }

        // any relevant change? -> add to pending actions
        if (mNumPresses==0) {
            addAction(PDE_AGENT_CONTROLLER_ACTION_CANCEL_PRESS);
        }
    }

    /**
     * @brief EventSource implementation. Get Event Sender instance.
     */
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

    /**
     * @brief EventSourceDelegate implementation. Send data update for initialization and
     *        maintain a consistent activation status.
     */
    @Override
    public void eventSourceDidAddListener(Object listener){
        PDEEventAgentControllerState event;

        // send initial animation change event
        event = createStateEvent();
        event.setType(PDE_AGENT_CONTROLLER_EVENT_ANIMATION_INITIALIZE);
        getEventSource().sendEvent(event,listener);

        if (mOutsideActiveBracket) {
            // send
            if( DEBUG_ACTIONS ){
                Log.d(LOG_TAG,"AgentController: BeginInteraction (on listener add)");
            }
            getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_BEGIN_INTERACTION,listener);
        }
        // if we're outside active, we must maintain a consistent state with the listener
        if (mOutsideActive) {
            // send
            if(DEBUG_ACTIONS){
                Log.d(LOG_TAG,"Activated (on listener add)");
            }
            getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_ACTIVATED,listener);
        }
    }


    /**
     * @brief EventSourceDelegate implementation. Maintain a consistent state when a
     *        listener is removed
     */
      public void eventSourceWillRemoveListener(Object listener){
          // if we're outside active, we must remove the state from the listener
          if (mOutsideActive) {
              //send
              if(DEBUG_ACTIONS){
                  Log.d(LOG_TAG,"Deactivated (on listener remove)");
              }
              getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_DEACTIVATED,listener);
          }
          if (mOutsideActiveBracket) {
              // send
              if(DEBUG_ACTIONS){
                  Log.d(LOG_TAG,"AgentController: EndInteraction (on listener remove)");
              }
              getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_END_INTERACTION,listener);
          }
      }


    /**
     * @brief Add an action to the list. Trigger a delayed check for inputs.
     *
     */
    public void addAction(int action)
    {
        // add to list
        mPendingActions.add(action);

        // if there's already a check scheduled, stop here
        if (mInputCheckScheduled) return;

        // remember we are scheduled
        mInputCheckScheduled = true;

        // schedule it
        PDEFrameTiming.getInstance().postExecuteFunction(this,"checkInput");
    }


    /**
     * @brief State check when input was changed
     *
     * Do input checks asynchronously. Perform all pending actions.
     */
    public void checkInput(){
        // go through all actions and act accordingly
        performActions();

        // check animations after all state changes have been processed (states stay if they need any
        // important animation to happen and don't go on)
        checkAnimation();

        // some animation change might have happened immediately -> update the state (the next state update might
        // rechange the state again). For single-state timings, we need to catch the visual update
        updateAnimationData();

        // we performed, so we're no longer scheduled
        mInputCheckScheduled = false;
    }


    /**
    * @brief Go through the action queue and perform all pending actions.
    */
    public void performActions()
    {
        int i;
        Number action;

        // perform them all (note that actions could be added while processing (e.g. state changes), so do a
        // oldstyle enumeration
        for (i=0; i<mPendingActions.size(); i++) {
            action = (Number)mPendingActions.get(i);
            performAction(action.intValue());
        }

        // cleanup actions
        mPendingActions.clear();
    }


    /**
     * @brief Perform a single action.
     *
     * Actions are performed and states are changed accordingly. Animations are started/stopped as necessary;
     * we rely on the animation system to work correctly if several changes happen at once.
     */
    public void performAction(int action)
    {
        // change internal state now for the action
        switch (action) {
            case PDE_AGENT_CONTROLLER_ACTION_FOCUS:
                // remember we have internal focus
                mInternalFocus = true;
                break;
            case PDE_AGENT_CONTROLLER_ACTION_UNFOCUS:
                // remember we have no more focus
                mInternalFocus = false;
                break;
            case PDE_AGENT_CONTROLLER_ACTION_HIGHLIGHT:
                // remember we have highlight
                mInternalHighlight = true;
                break;
            case PDE_AGENT_CONTROLLER_ACTION_UNHIGHLIGHT:
                // remember we have no more highlight
                mInternalHighlight = false;
                break;
            case PDE_AGENT_CONTROLLER_ACTION_PRESS:
                // remember we have a press
                mInternalPress = true;
                break;
            case PDE_AGENT_CONTROLLER_ACTION_DO_PRESS:
                // remember the press is done
                mInternalPress = false;
                // if we're in a state where will be able to process it,
                if ( stateCanHandleAction(action) ) {
                // remember to process it
                mInternalSelections++;
                // also remember to send it out immediately
                mPendingSelections1++;
            }
            break;
            case PDE_AGENT_CONTROLLER_ACTION_CANCEL_PRESS:
                // remember the press is done
                mInternalPress = false;
                break;
        }

        // send out action events based on current (unmodified) state
        // (reason: the actions trigger state changes; it's quite hard to track them, so it's easier to
        // send them before where the state is clear. It's a little bit more checking logic in here, but makes the
        // rest a lot easier. Note that state changes themselves must never send out any actions)
        checkActions();

        // adjust the state
        checkState(false);

        // check actions again (state changes sometimes trigger action changes).
        // This is not perfect; eventually think of a better way.
        checkActions();
    }


    /**
     * @brief Information if the current state accepts user interaction.
     */
    public boolean stateCanHandleAction(int action)
    {
        // basic states do
        if (mAgentState == PDE_AGENT_CONTROLLER_STATE_IDLE
                || mAgentState == PDE_AGENT_CONTROLLER_STATE_INTERACTIVE
                || mAgentState == PDE_AGENT_CONTROLLER_STATE_SHOWING) {
            // these are interactive states or will be interactive states on the action
            return true;
        } else {
            // this state must not handle user interaction (and also not cache it)
            return false;
        }
    }


    /**
     * @brief Check for state changes.
     *
     * Based on current state and input variables, state changes are triggered.
     * Several state changes might be triggered in sequence, depending on how the
     * agent is configured (states might be instantaneously skipped, however the
     * logic always walks through them). We do the check until the state is stable.
     * Timed states may only be checked if allowTiming is set; this is to stay
     * in timed states at least one frame.
     *
     * checkState() is used internally on input changes, and when timed.
     */
    protected void checkState(boolean allowTiming){
        int oldState, count;

        // check main state until it's stable
        count = 0;
        do {
            // remember old state
            oldState=mAgentState;
            // check a single state change
            checkSingleState(allowTiming);
            // safety
            count++;
            if (count > 100) {
                Log.e(LOG_TAG,"***********BUG: state not stable");
                break;
            }
        } while (oldState!=mAgentState);
    }

    /**
     * @brief Check for state changes, do one step.
     *
     * Only do one step based on current state, current input, and configuration.
     * Send out appropriate change notifications and trigger animations where necessary.
     */
    public void checkSingleState(boolean allowTiming){
        switch (mAgentState) {
            case PDE_AGENT_CONTROLLER_STATE_IDLE:
                // if we have any input state requiring interaction, change the state
                if (mInternalFocus || mInternalHighlight || mInternalPress) {
                    setAgentState(PDE_AGENT_CONTROLLER_STATE_INTERACTIVE);
                }
                break;
            case PDE_AGENT_CONTROLLER_STATE_INTERACTIVE:
                // action taken depends on pending selections
                if (mInternalSelections>0) {
                    // continue with showing (the selections are sent out at the end of
                    // showing state and resetted there)
                    setAgentState(PDE_AGENT_CONTROLLER_STATE_SHOWING);
                } else if (!mInternalFocus && !mInternalHighlight && !mInternalPress) {
                    // back to idle
                    setAgentState(PDE_AGENT_CONTROLLER_STATE_IDLE);
                }
                break;
            case PDE_AGENT_CONTROLLER_STATE_SHOWING:
                // check if we have shown the press for long enough. Note that all required animations (Press, down and
                // combined interaction) must satisfy the condition. We don't know what's used outside, so we better
                // act safely. Only allow this change to happen if we're actually timed (we want to stay in this
                // state for at least one frame).
                if (allowTiming) {
                    if ((!mPressAnimation.isRunning() && mPressAnimation.getTimeSinceDone()>=getActionShowTime())
                            && (!mDownAnimation.isRunning() && mDownAnimation.getTimeSinceDone()>=mActionShowTime)
                            && (!mInteractionAnimation.isRunning() && mInteractionAnimation.getTimeSinceDone()>=getActionShowTime())) {
                        // remember any pending selections for mass sending later (we want to preserve the order)
                        mPendingSelections2 = mInternalSelections;
                        mInternalSelections = 0;
                        // now we can continue to doing.
                        setAgentState(PDE_AGENT_CONTROLLER_STATE_DOING);
                    }
                } else {
                    Log.e(LOG_TAG,"Failed state change: Timing not allowed");
                }
                break;
            case PDE_AGENT_CONTROLLER_STATE_DOING:
                // nothing to do here any more. For now, back to IDLE
                setAgentState(PDE_AGENT_CONTROLLER_STATE_IDLE);
                break;
            default:
                // all other states do not react
                break;
        }
    }

    /**
     * @brief Set the new state
     *
     * Send out state change messages to delegate
     */
    public void setAgentState (int agentState){
        int oldState;

        // any change?
        if (mAgentState==agentState) return;

        // remember old state, set new one
        oldState=mAgentState;
        mAgentState=agentState;

        // some states are timed, set accordingly
        if (mAgentState == PDE_AGENT_CONTROLLER_STATE_SHOWING) {
            setTiming(true);//mTiming = true;
        } else {
            setTiming(false);//mTiming = false;
        }

        // debug
        if(DEBUG_STATE){
            Log.d (LOG_TAG,"State change "+oldState+"->"+mAgentState);
        }
    }

    /**
     * @brief Check for pending actions
     *
     * This sends out all actions that are sent immediately and are associated with
     * input state.
     */
    public void checkActions() {
        // depending on state, we might have to send out active/deactive and interactionstart/end events
        if ( stateCanHandleAction(PDE_AGENT_CONTROLLER_ACTION_GENERIC) ) {
            // do we need to start with interaction? (we need to do so if we're pressed)
            if (mInternalPress && !mOutsideActiveBracket) {
                // now active
                mOutsideActiveBracket = true;
                // send the action
                if(DEBUG_ACTIONS){
                    Log.d (LOG_TAG,"AgentController: BeginInteraction");
                }
                getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_BEGIN_INTERACTION);
            }
            // are we active?
            if (mInternalPress && (mInternalHighlight||mInternalFocus) && !mOutsideActive) {
                // now active
                mOutsideActive = true;
                // send the action
                if(DEBUG_ACTIONS){
                    Log.d (LOG_TAG,"AgentController: Activated");
                }
                getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_ACTIVATED);
            }
            // send out pending presses inbetween here keeps good ordering
            while (mPendingSelections1>0) {
                // count down
                mPendingSelections1--;
                // send the action
                if(DEBUG_ACTIONS){
                    Log.d (LOG_TAG,"AgentController: WillBeSelected");
                }
                getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED);
            }
            // are we inactive?
            if ((!mInternalPress || !(mInternalHighlight||mInternalFocus)) && mOutsideActive) {
                // no longer active
                mOutsideActive = false;
                // send the action
                if(DEBUG_ACTIONS){
                    Log.d (LOG_TAG,"AgentController: Deactivated");
                }
                getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_DEACTIVATED);
            }
            // did we stop interaction?
            if (!mInternalPress && mOutsideActiveBracket) {
                // no longer active
                mOutsideActiveBracket = false;
                // send the action
                if(DEBUG_ACTIONS){
                    Log.d (LOG_TAG,"AgentController: EndInteraction");
                }
                getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_END_INTERACTION);
            }
        } else {
            // we cannot handle any more actions, take away activation and interaction states
            if (mOutsideActive) {
                // no longer active
                mOutsideActive = false;
                // send the action
                if(DEBUG_ACTIONS){
                    Log.d (LOG_TAG,"AgentController: Deactivated");
                }
                getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_DEACTIVATED);
            }
            if (mOutsideActiveBracket) {
                // no longer active
                mOutsideActiveBracket = false;
                // send the action
                if(DEBUG_ACTIONS){
                    Log.d (LOG_TAG,"AgentController: EndInteraction");
                }
                getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_END_INTERACTION);
            }
        }

        // send out any still pending "will be selected"
        while (mPendingSelections1>0) {
            // count down
            mPendingSelections1--;
            // send the action
            if(DEBUG_ACTIONS){
                Log.d (LOG_TAG,"AgentController: WillBeSelected");
            }
            getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED);
        }

        // send out any pending "selected"
        while (mPendingSelections2>0) {
            // count down
            mPendingSelections2--;
            // send the action
            if(DEBUG_ACTIONS){
                Log.d (LOG_TAG,"AgentController: Selected");
            }
            getEventSource().sendEvent(PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED);
        }
    }

    /**
     * @brief Update animation states.
     *
     * Startup animations if necessary. They are started only when the output value
     * changes, otherwise they are kept running in their current behaviour. Change management stays
     * inside the animation - additional care is taken in there to not use calculated values, so rounding
     * errors should not occur.
     *
     * Note: should we allow non-linear movements of states, we probably have to do it here, since the appropriate
     *       decisions can no longer be made by the animation then.
     */
    public void checkAnimation(){
        PDEEventAgentControllerState event;
        boolean show;
        double value;

        // determine value for focus
        if (mAgentState== PDE_AGENT_CONTROLLER_STATE_INTERACTIVE) {
            // in interactive state, we show the focus if necessary
            if (mInternalFocus) show=true; else show=false;
        } else {
            // in all other states we don't show the focus
            show = false;
        }

        // and apply it (speed is dependent on the direction)
        if (show) {
            // on: fast
            mFocusAnimation.goToValueWithDurationForDistance(1.0,getInteractiveAttackTime(),1.0);
        } else {
            // off: slow
            mFocusAnimation.goToValueWithDurationForDistance(0.0,getInteractiveDecayTime(),1.0);
        }

        // determine value for highlight
        if (mAgentState== PDE_AGENT_CONTROLLER_STATE_INTERACTIVE) {
            // in interactive state, we show the highlight if necessary
            if (mInternalHighlight) show=true; else show=false;
        } else {
            // in all other states we don't show the highlight
            show = false;
        }

        // and apply it (speed is dependent on the direction)
        if (show) {
            // on: fast
            mHighlightAnimation.goToValueWithDurationForDistance(1.0,getInteractiveAttackTime(),1.0);
        } else {
            // off: slow
            mHighlightAnimation.goToValueWithDurationForDistance(0.0,getInteractiveDecayTime(),1.0);
        }

        // combined animation for focus and highlight
        if (mAgentState== PDE_AGENT_CONTROLLER_STATE_INTERACTIVE) {
            // in interactive state, we show the highlight if necessary
            if (mInternalFocus || mInternalHighlight) show=true; else show=false;
        } else {
            show = false;
        }

        // and apply it (speed is dependent on the direction)
        if (show) {
            // on: fast
            mFocusAndHighlightAnimation.goToValueWithDurationForDistance(1.0,getInteractiveAttackTime(),1.0);
        } else {
            // off: slow
            mFocusAndHighlightAnimation.goToValueWithDurationForDistance(0.0,getInteractiveDecayTime(),1.0);
        }

        // determine value for pressed
        if (mAgentState== PDE_AGENT_CONTROLLER_STATE_SHOWING) {
            // when showing, we are always visible
            show=true;
        } else if (mAgentState== PDE_AGENT_CONTROLLER_STATE_INTERACTIVE) {
            // in interactive state, we show pressed if necessary
            if (mInternalPress) show=true; else show=false;
        } else {
            // in all other states we don't show pressed (for now. pressed might be shown longer
            // depending on configuration of gfx, this needs to be implemented)
            show = false;
        }

        // and apply it (speed is dependent on the direction)
        if (show) {
            // on: fast
            mPressAnimation.goToValueWithDurationForDistance(1.0,getInteractiveAttackTime(),1.0);
        } else {
            // off: slow
            mPressAnimation.goToValueWithDurationForDistance(0.0,getInteractiveDecayTime(),1.0);
        }

        // down animation - the button is only down when it's pressed and highlighted. Roughly the same animation as
        // the interactive state animation, without the need to go through the intermediate state -> this smoothes
        // out some animations
        if (mAgentState== PDE_AGENT_CONTROLLER_STATE_SHOWING) {
            // when showing, we are always visible
            show = true;
        } else if (mAgentState== PDE_AGENT_CONTROLLER_STATE_INTERACTIVE) {
            // in interactive state, we show the highlight if necessary
            if (mInternalPress && (mInternalFocus || mInternalHighlight)) show=true; else show=false;
        } else {
            show = false;
        }

        // and apply it (speed is dependent on the direction)
        if (show) {
            // on: fast
            mDownAnimation.goToValueWithDurationForDistance(1.0,mInteractiveAttackTime,1.0);
        } else {
            // off: slow
            mDownAnimation.goToValueWithDurationForDistance(0.0,mInteractiveDecayTime,1.0);
        }

        // determine value for combined interaction
        if (mAgentState== PDE_AGENT_CONTROLLER_STATE_SHOWING) {
            // when showing, we are always visible
            value = 2.0;
        } else if (mAgentState== PDE_AGENT_CONTROLLER_STATE_INTERACTIVE) {
            // in interactive state, we show pressed if necessary. Note complex if - I want to avoid possible rounding
            // issues. Better: don't compare to target, store the actual int value somewhere here internally and
            // calculate/use this as base.
            if (mInternalPress) {
                if (mInternalFocus || mInternalHighlight) {
                    value = 2.0;
                } else {
                    value = 1.0;
                }
            } else {
                if (mInternalFocus || mInternalHighlight) {
                    value = 1.0;
                } else {
                    value = 0.0;
                }
            }
        } else {
            // in all other states we don't show pressed (for now. pressed might be shown longer
            // depending on configuration of gfx, this needs to be implemented)
            value = 0.0;
        }

        // apply (speed is also dependent on the direction - but skipping one or more states has no effect.
        if (value != mInteractionAnimation.getTarget()) {
            // only do something on change, but now use the actual value where we are as indicator for the timing
            if (value > mInteractionAnimation.getValue()) {
                // on: fast
                mInteractionAnimation.goToValueWithDurationLimitedAtDistance(value,getInteractiveAttackTime(),1.0);
            } else if (value < mInteractionAnimation.getValue()) {
                // off: slow
                mInteractionAnimation.goToValueWithDurationLimitedAtDistance(value,getInteractiveDecayTime(),1.0);
            }
        }

        // are we inside a state animation and should change direction?
        if (mNextState != null) {
            if (!mStateAnimationReversed && mState.equals(mCurrentState)) {
                // we're going back to the current state -> reverse now
                mStateAnimationReversed = true;
                // and adjust the animation
                mStateAnimation.goToValueWithDurationForDistance(0.0,mStateChangeTime,1.0);
            }
            if (mStateAnimationReversed && mState.equals(mNextState)) {
                // we want to go to the original state again
                mStateAnimationReversed = false;
                // and adjust the animation
                mStateAnimation.goToValueWithDurationForDistance(1.0,mStateChangeTime,1.0);
            }
        }

        // should we start a new state animation?
        if (mNextState == null && !mState.equals(mCurrentState)) {
            // set the next state
            mNextState = mState;
            mStateAnimation.setValueImmediate(0.0);
            mStateAnimationProgress = 0.0;
            // send out a "state change starting" message now
            event = createStateEvent();
            event.setType(PDE_AGENT_CONTROLLER_EVENT_ANIMATION_BEGIN_STATE_CHANGE);
            getEventSource().sendEvent(event);
            // start the animation
            mStateAnimation.goToValueWithDurationForDistance(1.0,mStateChangeTime,1.0);
        }
    }

    /**
     * @brief Take over current animation data
     *
     * If something changed, send out a change notification to the delegate
     */
    public void updateAnimationData(){
        PDEEventAgentControllerState event;
        double value;
        boolean changed;

        // init
        changed = false;

        // check focus
        value = mFocusAnimation.getValue();
        if (value != getAgentAnimationFocus()) {
            // take over
            setAgentAnimationFocus(value);
            // remember as changed
            changed = true;
        }

        // check highlight
        value = mHighlightAnimation.getValue();
        if (value != getAgentAnimationHighlight()) {
            // take over
            setAgentAnimationHighlight(value);
            // remember as changed
            changed = true;
        }

        // check combined focus and highlight
        value = mFocusAndHighlightAnimation.getValue();
        if (value != getAgentAnimationCombinedFocusAndHighlight()) {
            // take over
            setAgentAnimationCombinedFocusAndHighlight(value);
            // remember as changed
            changed = true;
        }

        // check pressed
        value = mPressAnimation.getValue();
        if (value != getAgentAnimationPress()) {
            // take over
            setAgentAnimationPress(value);
            // remember as changed
            changed = true;
        }

        // check down
        value = mDownAnimation.getValue();
        if (value != mAgentAnimationDown) {
            // take over
            mAgentAnimationDown = value;
            // remember as changed
            changed = true;
        }

        // check combined focus and highlight
        value = mFocusAndHighlightAnimation.getValue();
        if (value != getAgentAnimationCombinedFocusAndHighlight()) {
            // take over
            setAgentAnimationCombinedFocusAndHighlight(value);
            // remember as changed
            changed = true;
        }

        // check combined interaction
        value = mInteractionAnimation.getValue();
        if (value != getAgentAnimationCombinedInteraction()) {
            // take over
            setmAgentAnimationCombinedInteraction(value);
            // remember as changed
            changed = true;
        }

        // check state
        if (mNextState != null) {
            value = mStateAnimation.getValue();
            if (value != mStateAnimationProgress) {
                // take over
                mStateAnimationProgress = value;
                // remember as changed
                changed = true;
            }
        }

        // send out change notification if necessary
        if (changed) {
            // debug
            if(DEBUG_VALUES){
                Log.d(LOG_TAG," Data changed "+mCurrentState+";"+mNextState+","+mStateAnimationProgress+","
                              +mAgentAnimationFocus+", "+mAgentAnimationHighlight+", "
                              +mAgentAnimationPress+", "+mAgentAnimationDown+", "
                              +mAgentAnimationCombinedFocusAndHighlight+","
                              +mAgentAnimationCombinedInteraction);
            }
            // send animation change event
            event = createStateEvent();
            event.setType(PDE_AGENT_CONTROLLER_EVENT_ANIMATION_DID_CHANGE);
            getEventSource().sendEvent(event);
        }

        // is the state animation done?
        if (mNextState!=null && !mStateAnimation.isRunning()) {
            // stop the animation by setting the current state
            if (!mStateAnimationReversed) {
                mCurrentState = mNextState;
            }
            mNextState = null;
            mStateAnimationReversed = false;
            mStateAnimation.setValueImmediate(0.0);
            mStateAnimationProgress = 0.0;
            // send out "state change done" event
            event = createStateEvent();
            event.setType(PDE_AGENT_CONTROLLER_EVENT_ANIMATION_END_STATE_CHANGE);
            getEventSource().sendEvent(event);
            // check if we need to start the next animation immediately
            if (!mState.equals(mCurrentState)) {
                // set the next state (no need to reset the animation, it's been already cleared before)
                mNextState = mState;
                // send out a "state change starting" message now
                event = createStateEvent();
                event.setType(PDE_AGENT_CONTROLLER_EVENT_ANIMATION_BEGIN_STATE_CHANGE);
                getEventSource().sendEvent(event);
                // start the animation
                mStateAnimation.goToValueWithDurationForDistance(1.0,mStateChangeTime,1.0);
            }
        }
    }


    // frame timing
    /**
     * @brief Turn autotiming on or off
     *
     * Autotiming is done by linking into the global display timing
     */
    public void setTiming (boolean timing){
        // any change?
        if (mTiming == timing) return;

        // remember new state
        mTiming = timing;

        // and perform action
        if (mTiming) {
            // debug
            if(DEBUG_TIMING){
                Log.d(LOG_TAG,"Starting secondary timing");
            }
            // link to global timing
            PDEFrameTiming.getInstance().addListener(this,"time");
        } else {
            // unlink from global timing
            PDEFrameTiming.getInstance().removeListener(this);
            // debug
            if(DEBUG_TIMING){
                Log.d(LOG_TAG,"Stopped secondary timing");
            }
        }
    }

    /**
     * @brief Timing function called when manually timed, or when something in the animations changed.
     *
     * It's necessary to run the full checks if something in the animations changed; they might have changed their state,
     * which would then retrigger the next phase.
     *
     * Manual timing is only invoked for special cases where an animation is not timed, but needs to be checked if it's
     * done for a given time.
     */
    public void changed(){
        // if we have any pending actions, perform them first
        // (note: this should not happen if the frame timing works as it should. All end-of-runloop stuff
        // should have been already processed)
        performActions();

        // check animations (this starts them if necessary -> timed states may need animations to be running correctly)
        checkAnimation();

        // check state with timing
        checkState(true);

        // the state change might trigger some actions to be sent
        checkActions();

        // check animations again after processing timed state (this is not perfect; eventually think of splitting
        // the state machine into a fixed and a timed part).
        checkAnimation();

        // update animation values
        updateAnimationData();
    }

    /**
     * @brief timing function
     *
     *  Originally we wanted to have a time() method and a time(Long) method. But this seems to make problems
     *  with java reflection. So now the originally time() method is called changed(),
     *  the other one stays as time(Long). For now it simply calls the other one ( changed() ).
     */
    public void time(Long time){
        changed();
    }



    /**
     * @brief Event data helper.
     */
    PDEEventAgentControllerState createStateEvent(){
        PDEEventAgentControllerState event;

        // create event
        event=new PDEEventAgentControllerState();

        // fill in data
        event.setCurrentState(mCurrentState);
        event.setNextState(mNextState);
        event.setStateAnimationProgress(mStateAnimationProgress);
        event.setAgentAnimationFocus(getAgentAnimationFocus());
        event.setAgentAnimationHighlight(getAgentAnimationHighlight());
        event.setAgentAnimationPress(getAgentAnimationPress());
        event.setAgentAnimationDown(getAgentAnimationDown());
        event.setAgentAnimationCombinedFocusAndHighlight(getAgentAnimationCombinedFocusAndHighlight());
        event.setAgentAnimationCombinedInteraction(getAgentAnimationCombinedInteraction());

        // done
        return event;
    }
}
