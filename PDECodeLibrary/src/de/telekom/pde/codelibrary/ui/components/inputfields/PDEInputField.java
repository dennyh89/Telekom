/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.inputfields;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.SavedState;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.R.styleable;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton.PDEButtonLayerType;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButtonLayerInterface;
import de.telekom.pde.codelibrary.ui.components.inputfields.PDEEditText.OnFontSizeChangedListener;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDEResourceAttributesHelper;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.layout.PDESquareRelativeLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDEInputField
//----------------------------------------------------------------------------------------------------------------------

@SuppressWarnings("unused")
public class PDEInputField extends RelativeLayout implements PDEIEventSource, TextWatcher, View.OnFocusChangeListener,
        TextView.OnEditorActionListener, OnFontSizeChangedListener{

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEInputField.class.getName();

    //----- local constants -----


    // parameter strings
    //
    public static final String PDEInputFieldParameterFont = "font";
    public static final String PDEInputFieldParameterFontSize = "fontSize";
    public static final String PDEInputFieldParameterTextColor = "textColor";
    public static final String PDEInputFieldParameterHintTextColor = "hintTextColor";

    // well known parameter data strings
    //
    public static final String PDEInputFieldParameterValueSizeAuto = "auto";
    public static final String PDEInputFieldParameterValueSizeAutomatic = "automatic";
    public static final String PDEInputFieldParameterValueSizeStyleguide = "styleguide";



    // Events


    /**
     * @brief Event mask for all PDEAgentController events.
     */
    public static final String PDEInputFieldEventMask ="PDEInputField.*";
    public static final String PDEInputFieldEventActionGotFocus = "PDEInputField.gotFocus";
    public static final String PDEInputFieldEventActionLostFocus = "PDEInputField.lostFocus";
    public static final String PDEInputFieldEventActionBeforeTextChanged = "PDEInputField.beforeTextChanged";
    public static final String PDEInputFieldEventActionOnTextChanged = "PDEInputField.onTextChanged";
    public static final String PDEInputFieldEventActionAfterTextChanged = "PDEInputField.afterTextChanged";
    public static final String PDEInputFieldEventActionShouldDoEditorAction = "PDEInputField.shouldDoEditorAction";
    public static final String PDEInputFieldEventActionShouldClearText = "PDEInputField.shouldClearText";
    public static final String PDEInputFieldEventActionDidClearText = "PDEInputField.didClearText";

    //----- properties -----

    // private variables
    private PDEButton mBackgroundButton;
    private PDEEditText mEditTextView;
    private PDEButton mClearButton;
    private boolean mClearButtonEnabled;
    private float mClearButtonFontHeightToTextHeightRatio;

    // protected variables
    protected PDEEventSource mEventSource;


    //static variables

    // supported input type mask
    private static int mSupportedInputTypesMask;

    // static initialize
    static {
        Context context = PDECodeLibrary.getInstance().getApplicationContext();

        //import supported input types
        importSupportedInputTypes(context);
    }


    /**
     * @brief Constructor.
     */
    public PDEInputField(Context context) {
        super(context);
        init(null);
    }


    /**
     * @brief Constructor.
     */
    public PDEInputField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    /**
     * @brief Constructor.
     */
    public PDEInputField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }


    /**
     * @brief Init function to initialise start properties of the edittext.
     */
    private void init(android.util.AttributeSet attrs) {

        // inflate the input field with the correct layout
        PDESquareRelativeLayout.inflate(getContext(), R.layout.pdeinputfieldlayout, this);

        //init
        mEventSource = new PDEEventSource();
        mEventSource.setEventDefaultSender(this, true);
        mClearButtonFontHeightToTextHeightRatio = 0.75f;

        // remember some variables
        mEditTextView = (PDEEditText)findViewById(R.id.editTextView);
        //!!!!!!!!!!!IMPORTANT: disable saving of state !!!!!!!!!!!!!!!!! more infos at the bottom of this file at the
        // onSaveInstanceState and onRestoreInstanceState(Parcelable state) functions
        mEditTextView.setSaveEnabled(false);
        mBackgroundButton = (PDEButton)findViewById(R.id.pdeBackgroundButton);
        mClearButton = (PDEButton)findViewById(R.id.clearButtonView);

        //set button layer type
        mBackgroundButton.setButtonBackgroundLayerWithLayerType(PDEButton.PDEButtonLayerType.BackgroundText);
        mBackgroundButton.setButtonForegroundLayerWithLayerType(PDEButtonLayerType.ForegroundNone);

       // !!!!!!!!!!!!!!! TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // WE ONLY HAVE TO USE 1 AGENTCONTROLLER, (AT THE MOMENT backgroundbutton and textfield both have one used)
        // IN THE FUTURE REPLACE ONE BY THE OTHER, AT THE MOMENT USE TEXFIELD CONTROLLER
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        mBackgroundButton.getAgentController().setInputEnabled(false);
        mBackgroundButton.addListener(this, "onBackgroundButtonClicked", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED);

        // set clear button and add listener to clear the text
        mClearButton.setButtonBackgroundLayerWithLayerType(PDEButton.PDEButtonLayerType.BackgroundPlate);
        mClearButton.setIconColored(true);
        mClearButton.addListener(this, "onClearButtonClicked", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED);

        // forward edittext touch events to background button
        mEditTextView.setTargetListener(mBackgroundButton);
        // listen to text changes of edittext
        mEditTextView.addTextChangedListener(this);

        // listen to focus changes of edittext
        mEditTextView.setOnFocusChangeListener(this);

        //disable focus for some elements
        mBackgroundButton.setFocusable(false);
        mClearButton.setFocusable(false);
        mBackgroundButton.setFocusableInTouchMode(false);
        mClearButton.setFocusableInTouchMode(false);

        // listen to on editor actions of edittext
        mEditTextView.setOnEditorActionListener(this);
        //because font size handling is done in edit text, we want to get size changes to inform clear button
        mEditTextView.setOnFontSizeChangedListener(this);

        //enable clear button by default(also sets current color to text color)
        setClearButtonEnabled(true);

        //set the xml attributes
        setAttributes(attrs);
    }


    private void setAttributes(AttributeSet attrs) {
        // valid?
        if(attrs==null) return;

        String text;
        TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEInputField);

        // set text
        text = sa.getString(R.styleable.PDEInputField_text);
        if (TextUtils.isEmpty(text)) {
            // try to get "android:text" attribute instead
            text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
        }
        setText(text);

        // set typeface (font)
        if( sa.hasValue(R.styleable.PDEInputField_typeface)) {
            setFont(PDETypeface.createByName(sa.getString(R.styleable.PDEInputField_typeface)));
        }

        // set the text size
        if( sa.hasValue(R.styleable.PDEInputField_textSize)) {
            String text_size = sa.getString(R.styleable.PDEInputField_textSize);
            setFontSize(text_size);
        }

        // set color of text
        if (sa.hasValue(R.styleable.PDEInputField_textColor)) {
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String txt = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","textColor");
            if (txt != null && txt.startsWith("@")) {
                symbolicColor = Integer.valueOf(txt.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setTextColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setTextColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setTextColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setTextColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                } else {
                    setTextColor(sa.getColor(R.styleable.PDEInputField_textColor, R.color.DTBlack));
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setTextColor(sa.getColor(R.styleable.PDEInputField_textColor, R.color.DTBlack));
            }
        }

        // set cursor visibility
        if(sa.hasValue(R.styleable.PDEInputField_cursorVisible)) {
            setCursurVisible(sa.getBoolean(R.styleable.PDEInputField_cursorVisible,true));
        }

        //check icon source or string
        if (sa.hasValue(R.styleable.PDEInputField_leftIcon)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEInputField_leftIcon,0);
            if(resourceID==0){
                setLeftIcon(sa.getString(R.styleable.PDEInputField_leftIcon));
            } else {
                setLeftIcon(getContext().getResources().getDrawable(resourceID));
            }
        }

        // set hint text
        if(sa.hasValue(R.styleable.PDEInputField_hint)) {
            setHint(sa.getText(R.styleable.PDEInputField_hint));
        }

        // set hint color
        if(sa.hasValue(R.styleable.PDEInputField_hintColor)) {
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String txt = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","hintColor");
            if (txt != null && txt.startsWith("@")) {
                symbolicColor = Integer.valueOf(txt.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setHintTextColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setHintTextColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setHintTextColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setHintTextColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                } else {
                    setHintTextColor(sa.getColor(R.styleable.PDEInputField_hintColor, R.color.DTBlack));
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setHintTextColor(sa.getColor(R.styleable.PDEInputField_hintColor, R.color.DTBlack));
            }
        }

        // set on focus selection
        if(sa.hasValue(R.styleable.PDEInputField_selectAllOnFocus)) {
            setSelectAllOnFocus(sa.getBoolean(R.styleable.PDEInputField_selectAllOnFocus,false));
        }

        // set highlight text color
        if(sa.hasValue(R.styleable.PDEInputField_textColorHighlight)) {
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String txt = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","textColorHighLight");
            if (txt != null && txt.startsWith("@")) {
                symbolicColor = Integer.valueOf(txt.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setHighlightColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setHighlightColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setHighlightColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setHighlightColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                } else {
                    setHighlightColor(sa.getColor(R.styleable.PDEInputField_textColorHighlight, R.color.DTMagenta));
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setHighlightColor(sa.getColor(R.styleable.PDEInputField_textColorHighlight, R.color.DTMagenta));
            }
        }

        // set password mode
        if(sa.hasValue(R.styleable.PDEInputField_password)) {
            if(sa.getBoolean(R.styleable.PDEInputField_password,false)) {
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }

        // set ellipsize mode
        if(sa.hasValue(R.styleable.PDEInputField_ellipsize)) {
            setEllipsize(sa.getInt(R.styleable.PDEInputField_ellipsize, 0));
        }

        // set input type mode
        if(sa.hasValue(R.styleable.PDEInputField_inputType)) {
            setInputType(sa.getInt(R.styleable.PDEInputField_inputType,InputType.TYPE_NULL));
        }

        // set ime options
        if(sa.hasValue(R.styleable.PDEInputField_imeOptions)) {
            setImeOptions(sa.getInt(R.styleable.PDEInputField_imeOptions, EditorInfo.IME_ACTION_DONE));
        }

        //check if clear button is disabled or enabled
        if(sa.hasValue(styleable.PDEInputField_clearButtonEnabled)) {
            setClearButtonEnabled(sa.getBoolean(styleable.PDEInputField_clearButtonEnabled,true));
        }

        // icon to text height
        if(sa.hasValue(styleable.PDEInputField_iconToTextHeightRatio)) {
            setIconToTextHeightRatio(sa.getFloat(styleable.PDEInputField_iconToTextHeightRatio, 2.0f));
        }
        
        sa.recycle();
    }


    /**
     * @brief Called on PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED event changes from agentController of the clear button.
     */
    public void onClearButtonClicked(PDEEvent event) {
        PDEInputFieldEvent shouldClearTextEvent;
        PDEInputFieldEvent didClearTextEvent;

        // send event
        shouldClearTextEvent =new PDEInputFieldEvent();
        shouldClearTextEvent.setType(PDEInputFieldEventActionShouldClearText);
        shouldClearTextEvent.setSender(this);
        getEventSource().sendEvent(shouldClearTextEvent);

        if (shouldClearTextEvent.getShouldDoAction()){
            clearEditTextField();
            didClearTextEvent =new PDEInputFieldEvent();
            didClearTextEvent.setType(PDEInputFieldEventActionDidClearText);
            didClearTextEvent.setSender(this);
            getEventSource().sendEvent(didClearTextEvent);
        }
    }


    /**
     * @brief Called on PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED event changes from agentController of the background button.
     */
    public void onBackgroundButtonClicked(PDEEvent event) {
        updateClearButton();
    }


    /**
     * @brief Clears the inputfield text.
     */
    private void clearEditTextField() {
        setText("");
    }


    /**
     * @brief Listener called before the text changed.
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int startPos, int count, int lengthAfter) {
        PDEInputFieldEvent event;

        // send event
        event =new PDEInputFieldEvent();
        event.setType(PDEInputFieldEventActionBeforeTextChanged);
        event.setSender(this);
        event.setCurrentText(charSequence);
        event.setStartPos(startPos);
        event.setCount(count);
        event.setLengthAfter(lengthAfter);
        getEventSource().sendEvent(event);
    }


    /**
     * @brief Listener called when the text changed.
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int startPos, int lengthBefore, int count) {
        PDEInputFieldEvent event;

        updateClearButton();

        // send event
        event =new PDEInputFieldEvent();
        event.setType(PDEInputFieldEventActionOnTextChanged);
        event.setSender(this);
        event.setCurrentText(charSequence);
        event.setStartPos(startPos);
        event.setCount(count);
        event.setLengthBefore(lengthBefore);
        getEventSource().sendEvent(event);
    }


    /**
     * @brief Listener called after the text changed.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        PDEInputFieldEvent event;

        // send event
        event =new PDEInputFieldEvent();
        event.setType(PDEInputFieldEventActionAfterTextChanged);
        event.setSender(this);
        event.setCurrentText(editable);
        getEventSource().sendEvent(event);
    }


    /**
     * @brief Listener called when focus changed.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mEditTextView) {
            if (hasFocus) {
                PDEInputFieldEvent event;

                // !!!!!!!!!!!!!!! TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // WE ONLY HAVE TO USE 1 AGENTCONTROLLER, (AT THE MOMENT backgroundbutton and textfield both have one used)
                // IN THE FUTURE REPLACE ONE BY THE OTHER, AT THE MOMENT USE TEXFIELD CONTROLLER
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // disable input in agent controller before we send our events
                mEditTextView.getAgentController().setInputEnabled(false);
                updateClearButton();

                // send event
                event = new PDEInputFieldEvent();
                event.setType(PDEInputFieldEventActionGotFocus);
                event.setSender(this);
                getEventSource().sendEvent(event);
            } else {
                PDEInputFieldEvent event;

                // !!!!!!!!!!!!!!! TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // WE ONLY HAVE TO USE 1 AGENTCONTROLLER, (AT THE MOMENT backgroundbutton and textfield both have one used)
                // IN THE FUTURE REPLACE ONE BY THE OTHER, AT THE MOMENT USE TEXFIELD CONTROLLER
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // enable input in agent controller again after we send our events
                mEditTextView.getAgentController().setInputEnabled(true);
                updateClearButton();

                // send event
                event = new PDEInputFieldEvent();
                event.setType(PDEInputFieldEventActionLostFocus);
                event.setSender(this);
                getEventSource().sendEvent(event);
            }
        }
    }


    /**
     * @brief Listener called on some editor action of the keyboard.
     */
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        //action id z.B EditorInfo.IME_ACTION_DONE
        PDEInputFieldEvent event;
        // send event
        event = new PDEInputFieldEvent();
        event.setType(PDEInputFieldEventActionShouldDoEditorAction);
        event.setSender(this);
        event.setActionId(actionId);
        event.setKeyEvent(keyEvent);
        event.setShouldDoAction(false);
        getEventSource().sendEvent(event);

        return event.getShouldDoAction();
    }


    /**
     * @brief Set the left drawable by id for the inputfield (e.g magnifier for searchfield)
     */
    public void setLeftIcon(int drawableID) {
        mEditTextView.setLeftIcon(drawableID);
    }

    /**
     * @brief Set the left drawable for the inputfield (e.g magnifier for searchfield)
     */
    public void setLeftIcon(Drawable drawable) {
        mEditTextView.setLeftIcon(drawable);
    }


    /**
     * @brief Set the left icon string.
     *  Icon string can either be a # plus char, signalising to take the iconfont, or a resource string
     */
    public void setLeftIcon(String icon) {
        mEditTextView.setLeftIcon(icon);
    }


    /**
     * @brief Get the left icon of this inputfield, drawable if set or string if set, else null
     */
    public Object getLeftIcon() {
        return mEditTextView.getLeftIcon();
    }


    /**
     * @brief Get the left icon drawable of this inputfield. Null if drawable not set.
     */
    public Drawable getLeftIconDrawable() {
        return mEditTextView.getLeftIconDrawable();
    }


    /**
     * @brief Get the left icon sting of this inputfield. Null if string not set.
     */
    public String getLeftIconString() {
        return mEditTextView.getLeftIconString();
    }


    /**
     * @brief Returns true if icon image or icon string was set
     */
    public boolean hasLeftIcon() {
        return mEditTextView.hasLeftIcon();
    }


    /**
     * @brief Disable or enable the right clear button.
     */
    public void setClearButtonEnabled(boolean enabled) {
        mClearButtonEnabled = enabled;
        updateClearButtonTextColor();
        updateClearButton();
    }


    /**
     * Get information about clear button status.
     * @return
     */
    public boolean isClearButtonEnabled() {
        return mClearButtonEnabled;
    }


    /**
     * @brief Show or hide the clear button on the right side of the inputfield.
     */
    private void updateClearButton() {
        if (mEditTextView.hasFocus() && mClearButtonEnabled
                && mEditTextView.getText() != null &&  mEditTextView.getText().length() > 0){
            mClearButton.setVisibility(RelativeLayout.VISIBLE);
        } else {
            mClearButton.setVisibility(RelativeLayout.GONE);
        }
    }


    /**
     * @brief Set the ime options.
     *
     * FOR SUPPORTED TYPES -> LOOK IN ATTRIBUTES for inputType attribute (attr.xml) and use this (same as in EditorInfo) ->all supported at the moment)
     *
     * Flag is not checked against supported type mask, to guarantee that only supported types are valid,
     * because the values are not really bit flags.
     *
     * Set the  value of the edittextview, input field doesn't holds value itself.
     */
    public void setImeOptions(int imeOptions) {
        mEditTextView.setImeOptions(imeOptions);
    }


    /**
     * @brief Get the ime options
     */
    public int getImeOptions() {
        return mEditTextView.getImeOptions();
    }


//----- EditTextView functions -----------------------------------------------------------------------------------------


    /**
     * @brief Set title color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTextColor(PDEColor color) {
        // set the parameters
        mEditTextView.setTextColor(color);
        updateClearButtonTextColor();
    }


    /**
     * @brief Set title color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTextColor(String color) {
        setTextColor(PDEColor.valueOf(color));
    }


    /**
     * @brief Set text color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTextColor(int color) {
        setTextColor(PDEColor.valueOf(color));
    }


    /**
     * @brief Get text color.
     * Only returns values the user sets before. Default values are not received.
     *
     */
    public PDEColor getTextColor() {
        return mEditTextView.getTextColor();
    }


    /**
     * @brief Set hint text color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setHintTextColor(PDEColor color) {
        // set the parameters
        mEditTextView.setHintTextColor(color);
    }


    /**
     * @brief Set hint text color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setHintTextColor(String color) {
        setHintTextColor(PDEColor.valueOf(color));
    }


    /**
     * @brief Set hint text color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setHintTextColor(int color) {
        setHintTextColor(PDEColor.valueOf(color));
    }


    /**
     * @brief Get the hint color value of the edittextview.
     *
     * Only returns values the user sets before. Default values are not received.
     * Get the hint color value of the edittextview, input field doesn't holds value itself.
     */
    public PDEColor getHintTextColor() {
        return mEditTextView.getHintTextColor();
    }


    /**
     * @brief Set ratio of icon heiht to text height.
     *
     * @param ratio ratio of icon heiht to text height.
     */
    public void setIconToTextHeightRatio(float ratio){
        mEditTextView.setIconToTextHeightRatio(ratio);
    }


    /**
     * @brief Get ratio of icon heiht to text height.
     * @return ratio of icon heiht to text height.
     */
    public float getIconToTextHeightRatio() {
        return mEditTextView.getIconToTextHeightRatio();
    }



//----- EditTextView functions without parameters ----------------------------------------------------------------------


    /**
     * @brief Set the text value of the edittextview.
     *
     * Pass the text value through the edittextview, input field doesn't holds value itself.
     */
    public void setText(CharSequence text) {
        //be sure we call PDEEditText setText function with update the colors to avoid android bugs
        mEditTextView.setText(text);
    }


    /**
     * @brief Set the text value of the edittextview.
     *
     * Pass the text value through the edittextview, input field doesn't holds value itself.
     */
    public void setText(int resid) {
        setText(getContext().getResources().getText(resid));
    }


    /**
     * @brief Get the text value of the edittextview.
     *
     * Get the text value of the edittextview, input field doesn't holds value itself.
     */
    public Editable getText() {
        //TODO: Check if there is the same problem like on ios -> text below
        //we dont use parameters here to avoid bugs when textcolor is changed while editing
        //the text variable of textfield is changed while editing, but we dont notice this so we dont have information in the parameter
        //we can always update parameter when textchange is finished, but this maybe dont have good performance
        return mEditTextView.getText();
    }


    /**
     * @brief Set font.
     *
     * If nothing else is set the size of the font will be calculated fitting to the size of the button.
     * For fixed size (from the font you set with this function) call setFontSizeFromFont
     *
     * @param font for the inputfield
     */
    public void setFont(PDETypeface font) {
        // set the parameters
        mEditTextView.setFont(font);
    }


    /**
     * @brief Get the font.
     *
     * Only retrieves basic parameters, and only if a font was explicitly set before.
     */
    public PDETypeface getFont() {
        return mEditTextView.getFont();
    }


    /**
     * @brief Set the font size directly.
     *
     * @param fontSize of the font (in point)
     */
    public void setFontSize(float fontSize) {
        // set directly as number
        mEditTextView.setFontSize(fontSize);
    }


    /**
     * @brief Set the font size bei size String.
     *
     * The string must follow the format float[unit]. Unit is optional but if present valid values are "BU", "%" and "Caps".
     * It is also possible to set the strings "auto" or "automatic", and to "styleguide"
     *
     * @param sizeString
     */
    public void  setFontSize(String sizeString) {
        // set the string
        mEditTextView.setFontSize(sizeString);
    }


    /**
     * @brief Get font size (float).
     *
     * Only retrieves basic parameters, and only if a float was directly set.
     * If you used setFontSizeWithString use fontSizeString to get the font size.
     * @return the set font size in float or 0.0f if the parameter was not set or was set as a string.
     */
    public float getFontSize() {
        return mEditTextView.getFontSize();
    }


    /**
     * @brief Get font size string.
     *
     * Only retrieves basic parameters, and only if a string was previously set
     */
    public String getFontSizeString() {
        // retrieve parameter main value
        return mEditTextView.getFontSizeString();
    }


    /**
     * React on Font size changes.
     * @param newSize
     */
    public void onFontSizeChanged(float newSize) {
        //because the edittext handles font size changes, we have to get the current value of it so set same size to clear button
        //the x on clear button should be 75% of the button text height so calculate new size
        mClearButton.setFontSize(newSize*mClearButtonFontHeightToTextHeightRatio);
    }


    /**
     * @brief Set the hint value of the edittextview via charsequence.
     *
     * Pass the hint value through the edittextview, input field doesn't holds value itself.
     */
    public void setHint(CharSequence hintText) {
        mEditTextView.setHint(hintText);
    }


    /**
     * @brief Set the hint value of the edittextview via resource id.
     *
     * Pass the hint value through the edittextview, input field doesn't holds value itself.
     */
    public void setHint(int resid) {
        mEditTextView.setHint(resid);
    }


    /**
     * @brief Get the hint value of the edittextview.
     *
     * Get the hint value of the edittextview, input field doesn't holds value itself.
     */
    public CharSequence getHint() {
        return mEditTextView.getHint();
    }


    /**
     * @brief Set the selected text of the edittextview, the whole text is selected.
     *
     * Pass the  value through the edittextview, input field doesn't holds value itself.
     */
    public void selectAll() {
        mEditTextView.selectAll();
    }


    /**
     * @brief Set the selected text of the edittextview from start to stop.
     *
     * Pass the  value through the edittextview, input field doesn't holds value itself.
     */
    public void setSelection(int start, int stop) {
        mEditTextView.setSelection(start, stop);
    }


    /**
     * @brief Move the cursor to offset index.
     *
     * Pass the  value through the edittextview, input field doesn't holds value itself.
     */
    public void setSelection(int index) {
        mEditTextView.setSelection(index);
    }


    /**
     * @brief Get the start of the selection.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public int getSelectionStart() {
        return mEditTextView.getSelectionStart();
    }


    /**
     * @brief Get the end of the selection.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public int getSelectionEnd() {
        return mEditTextView.getSelectionEnd();
    }


    /**
     * @brief Return true iff there is a selection inside this text view.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public boolean hasSelection() {
        return mEditTextView.hasSelection();
    }


    /**
     * @brief Causes words in the text that are longer than the view is wide to be ellipsized instead of broken in the middle.
     *
     * FOR SUPPORTED TYPES -> LOOK IN ATTRIBUTES for ellipsize attribute (attr.xml)
     *
     * We check against xml attributes in code to avoid 2 different types (our and original),
     * and to avoid double handling with an extra enumeration for our supported types.
     * So we don't have to check if xml is consistent to our enum/values and check these against the original values.
     * -> !!!! Be sure that the xml values are consistent to the original values !!!!
     *
     * Pass the  value through the edittextview, input field doesn't holds value itself.
     */
    public void setEllipsize(TruncateAt ellipsis) {
        if( PDEResourceAttributesHelper.isInEnum(getContext(),R.attr.ellipsize, ellipsis.ordinal()) ) {
            mEditTextView.setEllipsize(ellipsis);
        } else {
            Log.e(LOG_TAG,"NOT SUPPORTED TRUNCATE TYPE:"+ellipsis.name());
        }
    }


    /**
     * @brief This is just a internal helper function for xml attribute setting of the truncateAt attribute
     */
    private void setEllipsize(int ellipsis) {
        try {
            TruncateAt truncateAtType = TruncateAt.values()[ellipsis];
            setEllipsize(truncateAtType);
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG,"NOT SUPPORTED ENUM VALUE OF TRUNCATE TYPE:"+ellipsis +" --> check attr.xml!!");
        }
    }


    /**
     * @brief Returns where, if anywhere, words that are longer than the view is wide should be ellipsized.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public TruncateAt getEllipsize() {
        return mEditTextView.getEllipsize();
    }


    /**
     * @brief Set whether the cursor is visible. The default is true.
     *
     * Pass the  value through the edittextview, input field doesn't holds value itself.
     */
    public void setCursurVisible(boolean visible) {
        mEditTextView.setCursorVisible(visible);
    }


    /**
     * @brief Whether or not the cursor is visible.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public boolean isCursorVisible() {
        return mEditTextView.isCursorVisible();
    }


    /**
     * @brief Returns the length, in characters, of the text.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public int length() {
        return mEditTextView.length();
    }


    /**
     * @brief Get the selection state of the edit text view.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public boolean isSelected() {
        return mEditTextView.isSelected();
    }


    /**
     * @brief Changes the selection state of the edit text view.
     *
     * Set the  value of the edittextview, input field doesn't holds value itself.
     */
    public void setSelected(boolean selected) {
        mEditTextView.setSelected(selected);

    }


    /**
     * @brief Set the edittextview so that when it takes focus, all the text is selected.
     *
     * Set the  value of the edittextview, input field doesn't holds value itself.
     */
    public void setSelectAllOnFocus(boolean selected) {
        mEditTextView.setSelectAllOnFocus(selected);
    }


    /**
     * @brief Sets the color used to display the selection highlight.
     *
     * Set the  value of the edittextview, input field doesn't holds value itself.
     */
    public void setHighlightColor(PDEColor color) {
        // valid parameter?
        // here is a null pointer check, because text color and hint text color use the parameters, where a null value is valid
        if(color==null) {
            Log.e(LOG_TAG, "Try to set highlight color with null pointer --> we do nothing here");
            return;
        }

        setHighlightColor(color.getIntegerColor());
    }


    /**
     * @brief Sets the color used to display the selection highlight.
     *
     * Set the  value of the edittextview, input field doesn't holds value itself.
     */
    public void setHighlightColor(int color) {
        mEditTextView.setHighlightColor(color);
    }


    /**
     * @brief Gets the color used to display the selection highlight.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public PDEColor getHighlightColor() {
        return PDEColor.valueOf(mEditTextView.getHighlightColor());
    }


    /**
     * @brief Set the type of the content with a constant as defined for inputType.
     * This will take care of changing the key listener, by calling setKeyListener(KeyListener), to match the given content type.
     *
     * FOR SUPPORTED TYPES -> LOOK IN ATTRIBUTES for inputType attribute (attr.xml) and use this (same as in InputType, but no all supported)
     *
     * Flag is checked against supported type mask, to guarantee that only supported types are valid.
     * This mask is set in the init function, by import values from inputType attribute in attr.xml
     *
     * Set the  value of the edittextview, input field doesn't holds value itself.
     */
    public void setInputType(int type) {
        int validType = type&mSupportedInputTypesMask;
        if(validType!=type) {
            Log.e(LOG_TAG,"Try to set one or more inpuType flags, not supported -> unsupported types are ignored!!!!!");
        }
        //set correct flags (remove not supported flags from type)
        mEditTextView.setInputType(validType);
    }


    /**
     * @brief Get the type of the content with a constant as defined for inputType.
     *
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public int getInputType() {
        return mEditTextView.getInputType();
    }


    /**
     * @brief Update the clear button text color
     *
     * The text color has to be updated, because we use a iconFont for the X
     */
    private void updateClearButtonTextColor() {
        //get current text color (not  getTextColor because this only returns the parameter color)
        mClearButton.setTextColor(PDEColor.valueOf(mEditTextView.getCurrentTextColor()));
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


    //----- free parameter setting -----------------------------------------------------------------------------------------


    /**
     * @brief Set parameter, distribute changes.
     *      This is only used for PDEButton.PDEButtonParameterColor at the moment
     */
    public void mergeParameter(String name, String value, String subKey) {
        if( PDEButton.PDEButtonParameterColor.equalsIgnoreCase(name)) {
            mBackgroundButton.mergeParameter(name, value, subKey);
        }
    }


//----- button state handling ------------------------------------------------------------------------------------------


    /**
     * @brief Get the button's current state.
     *
     * The current state is always the last set state. If the agent is in an animation between states, this
     * is not reflected here.
     */
    public String getMainState() {
        // !!!!!!!!!!!!!!! TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // WE ONLY HAVE TO USE 1 AGENTCONTROLLER, (AT THE MOMENT backgroundbutton and textfield both have one used)
        // IN THE FUTURE REPLACE ONE BY THE OTHER, AT THE MOMENT USE TEXFIELD CONTROLLER
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // map to agent controller
        return mEditTextView.getMainState();
    }


    /**
     * @brief Get the button's current state.
     *
     * The current state is always the last set state. If the agent is in an animation between states, this
     * is not reflected here.
     */
    public void setMainState(String state) {
        // !!!!!!!!!!!!!!! TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // WE ONLY HAVE TO USE 1 AGENTCONTROLLER, (AT THE MOMENT backgroundbutton and textfield both have one used)
        // IN THE FUTURE REPLACE ONE BY THE OTHER, AT THE MOMENT USE TEXFIELD CONTROLLER
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // map to agent controller
        mEditTextView.setMainState(state);
    }


//----- helper functions -----------------------------------------------------------------------------------------------

    /**
     * @brief Import all supported input types from the inputType attribute of attr.xml to create a bitmask.
     *
     * FOR SUPPORTED TYPES -> LOOK IN ATTRIBUTES for inputType attribute (attr.xml) and use this (same as in InputType, but no all supported)
     *
     * We check against xml attributes in code to avoid 2 different types (our and original),
     * and to avoid double handling with an extra enumeration for our supported types.
     * So we don't have to check if xml is consistent to our values and check these against the original values.
     * -> Only to be sure that the xml values are consistent to the original truncate values
     */
    private static void importSupportedInputTypes(Context context) {
        int i = 0;

        mSupportedInputTypesMask = 0;

        int[] attributeValues = PDEResourceAttributesHelper.getIntArray(context,R.attr.inputType);

        for(i=0;i<attributeValues.length;i++){
            mSupportedInputTypesMask |= attributeValues[i];
        }

    }



//----- remember/restore state functions ---------------------------------------------------------------------------------------
//
// Because every edittext in our inputfield has the same id, the  default state restore fails
// so we disable the save of the edittext and do it by yourself here
// The failure is in the dispatchSaveInstanceState, because the state is saved here with the view id in an array
// For every InputField we have the same id for the edittext, so the saved state is allways replaced by the next
// EditText saveInstance call..!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //
//----- remember state functions ---------------------------------------------------------------------------------------


    /**
     * Save the current state.
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(this.getClass().getSimpleName(), super.onSaveInstanceState());
        bundle.putParcelable(mEditTextView.getClass().getSimpleName(), mEditTextView.onSaveInstanceState());
        return bundle;
    }


    /**
     * Restore the saved state.
     * @param state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(this.getClass().getSimpleName()));
            mEditTextView.onRestoreInstanceState(bundle.getParcelable(mEditTextView.getClass().getSimpleName()));
            return;
        } else {
            super.onRestoreInstanceState(state);
        }
        super.onRestoreInstanceState(state);
    }
}
