/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

//
// PDE commonly used helper functions for parameters. Really specific, but often used, so located here.
//

package de.telekom.pde.codelibrary.ui.components.helpers;


import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.agents.PDEEventAgentControllerState;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;

public class PDEAgentHelper {

    private String mCurrentState;
    private String mNextState;
    private double mStateProgress;
    private double mInteractiveState;
    private double mDownState;


    public static int PDEAgentHelperAnimationStateOnly = 0;
    public static int PDEAgentHelperAnimationInteractive = 1;
    public static int PDEAgentHelperAnimationDown = 2;


    //
    // Simple Helper class for getInterpolationInformationState1()
    public class InterpolationStateHelper {
        public String mState1;
        public String mState2;
        public float mStateBlend;
        public String mSubState1;
        public String mSubState2;
        public float mSubStateBlend;

        public InterpolationStateHelper() {
            mState1 = null;
            mState2 = null;
            mStateBlend = 0.0f;
            mSubState1 = null;
            mSubState2 = null;
            mSubStateBlend = 0.0f;
        }
    }


    /**
     * @brief Constructor.
     */
    public PDEAgentHelper() {
        mCurrentState = PDEButton.PDEButtonStateDefault;
        mNextState= null;
        mStateProgress=0.0f;
        mInteractiveState = 0.0f;
        mDownState = 0.0f;
    }


    //----- event processing -----------------------------------------------------------------------------------------------


    /**
     * @brief Process an agent event.
     *
     * Store all necessary information; check for changes.
     *
     * @return YES if something changed and an update is required.
     */
    public boolean processAgentEvent(PDEEvent event)
    {
        boolean changed;

        // init
        changed = false;

        // action depending on event
        if (event.isType(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION)) {
            PDEEventAgentControllerState e = (PDEEventAgentControllerState)event;
            // animation phase did change -> extract from data packet
            changed |= updateCurrentState_priv(e.getCurrentState());
            changed |= updateNextState_priv(e.getNextState());
            changed |= updateStateProgress_priv(e.getStateAnimationProgress());
            changed |= updateInteractiveState_priv(e.getAgentAnimationCombinedInteraction());
            changed |= updateDownState_priv(e.getAgentAnimationDown());
        }

        // done
        return changed;
    }

    /**
     * @brief Current state changed
     */
    public boolean updateCurrentState_priv(String currentState)
    {
        // any change?
        if (mCurrentState == currentState) return false;

        // remember
        mCurrentState = currentState;

        // there was some change
        return true;
    }


    /**
     * @brief Current state changed
     */
    public boolean updateNextState_priv(String nextState)
    {
        // any change?
        if (mNextState == nextState) return false;

        // remember
        mNextState = nextState;

        // there was some change
        return true;
    }


    /**
     * @brief State animation phase changed.
     */
    public boolean updateStateProgress_priv(double stateProgress)
    {
        // any change?
        if (mStateProgress == stateProgress) return false;

        // remember
        mStateProgress = stateProgress;

        // there was some change
        return true;
    }

    /**
     * @brief Animation phase changed.
     */
    boolean updateInteractiveState_priv(double state)
    {
        // any change?
        if (mInteractiveState == state) return false;

        // remember
        mInteractiveState = state;

        // there was some change
        return true;
    }


    /**
     * @brief Animation phase changed.
     */
    public boolean updateDownState_priv(double state)
    {
        // any change?
        if (mDownState == state) return false;

        // remember
        mDownState = state;

        // there was some change
        return true;
    }

    //----- information retrieval ------------------------------------------------------------------------------------------


    /**
     * @brief Determine parameters for interpolation from current stored agent state.
     */
    public InterpolationStateHelper getInterpolationInformationForAnimation(int animation)
    {
        InterpolationStateHelper iStateHelper = new InterpolationStateHelper();

        // state interpolation can be retrieved directly from values
        iStateHelper.mState1 = mCurrentState;
        iStateHelper.mState2 = mNextState;
        iStateHelper.mStateBlend = (float)mStateProgress;


        // state interpolation depends on what animation we're listening to
        if (animation == PDEAgentHelperAnimationInteractive) {
            // interpolation between idle,focus and takinginput
            if (mInteractiveState <= 0.0) {
                // base (idle) state
                iStateHelper.mSubState1 = PDEButton.PDEButtonAgentStateIdle;
                iStateHelper.mSubState2 = null;
                iStateHelper.mSubStateBlend = 0.0f;
            } else if (mInteractiveState < 1.0) {
                // animate between idle and focus
                iStateHelper.mSubState1 = PDEButton.PDEButtonAgentStateIdle;
                iStateHelper.mSubState2 = PDEButton.PDEButtonAgentStateFocus;
                iStateHelper.mSubStateBlend = (float) mInteractiveState;
            } else if (mInteractiveState == 1.0) {
                // focus state
                iStateHelper.mSubState1 = PDEButton.PDEButtonAgentStateFocus;
                iStateHelper.mSubState2 = null;
                iStateHelper.mSubStateBlend = 0.0f;
            } else if (mInteractiveState < 2.0) {
                // animation between focus and taking input
                iStateHelper.mSubState1 = PDEButton.PDEButtonAgentStateFocus;
                iStateHelper.mSubState2 = PDEButton.PDEButtonAgentStateTakingInput;
                iStateHelper.mSubStateBlend = (float) (mInteractiveState - 1.0);
            } else {
                // taking input state
                iStateHelper.mSubState1 = PDEButton.PDEButtonAgentStateTakingInput;
                iStateHelper.mSubState2 = null;
                iStateHelper.mSubStateBlend = 0.0f;
            }
        } else if (animation == PDEAgentHelperAnimationDown) {
            // interpolation between idle and down
            if (mDownState <= 0.0) {
                // base (idle) state
                iStateHelper.mSubState1 = PDEButton.PDEButtonAgentStateIdle;
                iStateHelper.mSubState2 = null;
                iStateHelper.mSubStateBlend = 0.0f;
            } else if (mDownState < 1.0) {
                // animate between idle and down
                iStateHelper.mSubState1 = PDEButton.PDEButtonAgentStateIdle;
                iStateHelper.mSubState2 = PDEButton.PDEButtonAgentStateDown;
                iStateHelper.mSubStateBlend = (float) mDownState;
            } else {
                // focus state
                iStateHelper.mSubState1 = PDEButton.PDEButtonAgentStateDown;
                iStateHelper.mSubState2 = null;
                iStateHelper.mSubStateBlend = 0.0f;
            }
        } else {
            // either stateonly or unknown mode, no substates
            iStateHelper.mSubState1 = null;
            iStateHelper.mSubState2 = null;
            iStateHelper.mSubStateBlend = 0.0f;
        }

        return iStateHelper;
    }

}