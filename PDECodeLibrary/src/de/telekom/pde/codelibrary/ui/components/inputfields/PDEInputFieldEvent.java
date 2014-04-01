/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.inputfields;


import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;


//----------------------------------------------------------------------------------------------------------------------
//  PDEInputFieldEvent
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Special Event for PDEInputField.
 */
public class PDEInputFieldEvent extends PDEEvent{

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEInputFieldEvent.class.getName();

    //----- properties -----

    // protected variables
    protected boolean mShouldDoAction;
    protected int mStartPos;
    protected int mLengthBefore;
    protected int mLengthAfter;
    protected int mCount;
    protected CharSequence mCurrentText;

    //protected variables for onEditorAction events
    protected KeyEvent mKeyEvent;
    protected int mActionId;


    /**
     * @brief Constructor.
     */
    public PDEInputFieldEvent() {
        super();
        setShouldDoAction(true);
        setStartPos(0);
        setLengthBefore(0);
        setLengthAfter(0);
        setCount(0);
        setCurrentText(null);
        setKeyEvent(null);
        setActionId(EditorInfo.IME_ACTION_UNSPECIFIED);
    }


    /**
     * @brief Set the should do action flag.
     */
    public void setShouldDoAction(boolean doAction) {
        mShouldDoAction = doAction;
    }


    /**
     * @brief Get the should do action flag.
     */
    public boolean getShouldDoAction() {
        return mShouldDoAction;
    }


    /**
     * @brief Set the start position value.
     */
    public void setStartPos(int startPos) {
        mStartPos = startPos;
    }


    /**
     * @brief Get the start position value.
     */
    public int getStartPos() {
        return mStartPos;
    }


    /**
     * @brief Set the lengthBefore value.
     */
    public void setLengthBefore(int lengthBefore) {
        mLengthBefore = lengthBefore;
    }


    /**
     * @brief Get the lengthBefore value.
     */
    public int getLengthBefore() {
        return mLengthBefore;
    }


    /**
     * @brief Set the lengthAfter value.
     */
    public void setLengthAfter(int lengthAfter) {
        mLengthAfter = lengthAfter;
    }


    /**
     * @brief Get the lengthAfter value.
     */
    public int getLengthAfter() {
        return mLengthAfter;
    }


    /**
     * @brief Set the count value.
     */
    public void setCount(int count) {
        mCount = count;
    }


    /**
     * @brief Get the count value.
     */
    public int getCount() {
        return mCount;
    }


    /**
     * @brief Set the current text.
     */
    public void setCurrentText(CharSequence newText) {
        mCurrentText = newText;
    }


    /**
     * @brief Get the current text.
     */
    public CharSequence getCurrentText() {
        return mCurrentText;
    }


    /**
     * @brief Set the current Key event, used for onEditorAction events.
     */
    public void setKeyEvent(KeyEvent keyEvent) {
        mKeyEvent = keyEvent;
    }


    /**
     * @brief Get the current Key event, used for onEditorAction events.
     */
    public KeyEvent getKeyEvent() {
        return mKeyEvent;
    }


    /**
     * @brief Set the current action id, used for onEditorAction events.
     */
    public void setActionId(int actionId) {
        mActionId = actionId;
    }


    /**
     * @brief Get the current action id, used for onEditorAction events.
     */
    public int getActionId() {
        return mActionId;
    }
}
