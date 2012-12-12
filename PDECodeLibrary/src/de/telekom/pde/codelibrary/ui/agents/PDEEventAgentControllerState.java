/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.agents;

import de.telekom.pde.codelibrary.ui.events.PDEEvent;

//----------------------------------------------------------------------------------------------------------------------
//  PDEAgentController events
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Event state sent along with all events.
 *
 * Contains the complete state of the animation (along with all additional transition informations)
 * Use this to pass on to all objects, they should extract the necessary state for themselves.
 */
public class PDEEventAgentControllerState extends PDEEvent {
    /**
     * @brief Current highlevel state, or original state if doing state animations.
     */
    protected String mCurrentState;
    /**
     * @brief Next highlevel state if a state animation is happening.
     *
     * Be careful - state animations may turn around, so this state may never be fully reached. The agent may
     * turn the animation around at any time without further notice.
     */
    protected String mNextState;
    /**
     * @brief State animation. Range is 0..1. If there is no animation, value is 0.
     */
    protected double mStateAnimationProgress;
    /**
     * @brief Focus animation. Range is 0..1
     */
    protected double mAgentAnimationFocus;
    /**
     * @brief Highlight animation. Range is 0..1
     */
    protected double mAgentAnimationHighlight;
    /**
     * @brief Pressed animation. Range is 0..1
     */
    protected double mAgentAnimationPress;
    /**
     * @brief Down animation. Range is 0..1
     */
    protected double mAgentAnimationDown;
    /**
     * @brief Combined focus and highlight animation. Range is 0..1
     */
    protected double mAgentAnimationCombinedFocusAndHighlight;
    /**
     * @brief Combination of focus/highlight and pressed animations. Range is 0..2
     *
     * This animation corresponds to the agentstate model where one value (usually color) is animated
     * through the states. 0 is "available", 1 is "focus", 2 is "taking input".
     */
    protected double mAgentAnimationCombinedInteraction;


    public void setCurrentState(String currentState){
        mCurrentState = currentState;
    }
    public void setNextState(String nextState) {
        mNextState = nextState;
    }
    public void setStateAnimationProgress(double stateAnimationProgress){
        mStateAnimationProgress = stateAnimationProgress;
    }

    public void setAgentAnimationFocus(double focus) {
        mAgentAnimationFocus = focus;
    }

    public void setAgentAnimationHighlight(double highlight) {
        mAgentAnimationHighlight = highlight;
    }

    public void setAgentAnimationPress(double press) {
        mAgentAnimationPress = press;
    }

    public void setAgentAnimationDown(double down) {
        mAgentAnimationDown = down;
    }

    public void setAgentAnimationCombinedFocusAndHighlight (double combinedFocusAndHighlight) {
        mAgentAnimationCombinedFocusAndHighlight = combinedFocusAndHighlight;
    }

    public void setAgentAnimationCombinedInteraction(double combinedInteraction) {
        mAgentAnimationCombinedInteraction = combinedInteraction;
    }


    public String getCurrentState(){
        return mCurrentState;
    }

    public String getNextState() {
        return mNextState;
    }

    public double getStateAnimationProgress(){
        return mStateAnimationProgress;
    }

    public double getAgentAnimationFocus(){
        return mAgentAnimationFocus;
    }

    public double getAgentAnimationHighlight(){
        return mAgentAnimationHighlight;
    }

    public double getAgentAnimationPress(){
        return mAgentAnimationPress;
    }

    public double getAgentAnimationDown(){
        return mAgentAnimationDown;
    }

    public double getAgentAnimationCombinedFocusAndHighlight(){
        return mAgentAnimationCombinedFocusAndHighlight;
    }

    public double getAgentAnimationCombinedInteraction(){
        return mAgentAnimationCombinedInteraction;
    }
}
