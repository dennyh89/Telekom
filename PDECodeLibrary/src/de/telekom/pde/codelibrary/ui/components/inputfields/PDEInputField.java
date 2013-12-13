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
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.R.color;
import de.telekom.pde.codelibrary.ui.R.styleable;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton.PDEButtonLayerType;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButtonLayerInterface;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEResourceAttributesHelper;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.layout.PDESquareRelativeLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDEInputField
//----------------------------------------------------------------------------------------------------------------------

@SuppressWarnings("unused")
public class PDEInputField extends RelativeLayout implements PDEIEventSource, TextWatcher, View.OnFocusChangeListener,
        TextView.OnEditorActionListener {

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
    //
    /**
     * @brief Event mask for all PDEAgentController events.
     */
    public static final String PDE_INPUTFIELD_EVENT_MASK_ACTION ="PDEInputField.action.*";
    public static final String PDE_INPUTFIELD_EVENT_ACTION_GOT_FOCUS = "PDEInputField.action.gotFocus";
    public static final String PDE_INPUTFIELD_EVENT_ACTION_LOST_FOCUS = "PDEInputField.action.lostFocus";
    public static final String PDE_INPUTFIELD_EVENT_ACTION_BEFORE_TEXT_CHANGED = "PDEInputField.action.beforeTextChanged";
    public static final String PDE_INPUTFIELD_EVENT_ACTION_ON_TEXT_CHANGED = "PDEInputField.action.onTextChanged";
    public static final String PDE_INPUTFIELD_EVENT_ACTION_AFTER_TEXT_CHANGED = "PDEInputField.action.afterTextChanged";
    public static final String PDE_INPUTFIELD_EVENT_ACTION_SHOULD_DO_EDITOR_ACTION = "PDEInputField.action.shouldDoEditorAction";
    public static final String PDE_INPUTFIELD_EVENT_ACTION_SHOULD_CLEAR_TEXT = "PDEInputField.action.shouldClearText";
    public static final String PDE_INPUTFIELD_EVENT_ACTION_DID_CLEAR_TEXT = "PDEInputField.action.didClearText";

    //----- properties -----

    // font modes
    enum PDEInputFieldFontMode {
        Undefined,
        Automatic,
        Styleguide,
        Fixed
    }

    // private variables
    float mWidth;
    float mHeight;
    private PDEButton mBackgroundButton;
    private PDEEditText mEditTextView;
    private PDESquareRelativeLayout mClearButtonContainer;
    private PDEButton mClearButton;
    private boolean mClearButtonEnabled;
    private float mClearButtonFontHeightToTextHeightRatio;
    private PDETypeface mFont;
    private PDEInputFieldFontMode mFontMode;
    private float mFontSize;
    float mFontUsedSize;
    boolean mFontUsesCaps;

    // protected variables
    protected PDEEventSource mEventSource;

    // parameters needed
    private PDEParameterDictionary mParameters;

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
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    public PDEInputField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    public PDEInputField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Init function to initialise start properties of the edittext.
     */
    private void init(Context context, android.util.AttributeSet attrs) {

        // inflate the input field with the correct layout
        RelativeLayout.inflate(getContext(), R.layout.pdeinputfieldlayout, this);

        //init
        mEventSource = new PDEEventSource();
        mEventSource.setEventDefaultSender(this, true);
        mClearButtonFontHeightToTextHeightRatio = 0.66f;

        mWidth = 0.0f;
        mHeight = 0.0f;
        mFont = null;
        mFontMode = PDEInputFieldFontMode.Undefined;
        mFontSize = 0.0f;
        mFontUsedSize = 0.0f;
        mFontUsesCaps = false;

        // remember some variables
        mEditTextView = (PDEEditText)findViewById(R.id.editTextView);
        //!!!!!!!!!!!IMPORTANT: disable saving of state !!!!!!!!!!!!!!!!! more infos at the bottom of this file at the
        // onSaveInstanceState and onRestoreInstanceState(Parcelable state) functions
        mEditTextView.setSaveEnabled(false);
        mEditTextView.addTextChangedListener(this);

        mBackgroundButton = (PDEButton)findViewById(R.id.pdeBackgroundButton);
        mClearButton = (PDEButton)findViewById(R.id.clearButtonView);
        mClearButtonContainer = (PDESquareRelativeLayout)findViewById(R.id.clearButtonContainer);


        // if this is called from developer tool (IDE) stop here
        if (isInEditMode()) return;

        //set button foreground layer to none and background to flat
        setInputFieldBackgroundLayerWithLayerType(PDEButton.PDEButtonLayerType.BackgroundTextFlat);
        mBackgroundButton.setButtonForegroundLayerWithLayerType(PDEButtonLayerType.ForegroundNone);

       // !!!!!!!!!!!!!!! TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // WE ONLY HAVE TO USE 1 AGENT-CONTROLLER, (AT THE MOMENT backgroundButton and TextField both have one used)
        // IN THE FUTURE REPLACE ONE BY THE OTHER, AT THE MOMENT USE TEXTFIELD CONTROLLER
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        mBackgroundButton.getAgentController().setInputEnabled(false);
        mBackgroundButton.addListener(this, "onBackgroundButtonClicked", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED);

        // set clear button and add listener to clear the text
        mClearButton.setButtonBackgroundLayer(new PDEInputFieldClearButtonBackground(context));
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

        //enable clear button by default(also sets current color to text color)
        setClearButtonEnabled(true);

        // set of parameter -> and start prepare font and colors -> this sets defaults (text color, hint color...)
        mParameters = new PDEParameterDictionary();
        prepareFont();
        updateTextColor();
        updateHintTextColor();

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

        if (sa.hasValue(R.styleable.PDEInputField_backgroundType)) {
            setInputFieldBackgroundLayerWithLayerType(sa.getInt(R.styleable.PDEInputField_backgroundType,0));
        }

        // set color of text
        if (sa.hasValue(R.styleable.PDEInputField_textColor)) {
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(styleable.PDEInputField_textColor,0);
            if (resourceID!=0) {
                setTextColor(PDEColor.valueOfColorID(resourceID));
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
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(styleable.PDEInputField_hintColor,0);
            if (resourceID!=0) {
                setHintTextColor(PDEColor.valueOfColorID(resourceID));
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
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(styleable.PDEInputField_textColorHighlight,0);
            if (resourceID!=0) {
                setHighlightColor(PDEColor.valueOfColorID(resourceID));
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
     * @brief Set the background layer.
    */
    public void setInputFieldBackgroundLayer(PDEButtonLayerInterface layer) {
        mBackgroundButton.setButtonBackgroundLayer(layer);
    }


    /**
     * @brief Set the background layer.
    */
    private void setInputFieldBackgroundLayerWithLayerType(int layerType) {
        mBackgroundButton.setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.values()[layerType]);
    }


    /**
     * @brief Select one of the default backgrounds
    */
    public void setInputFieldBackgroundLayerWithLayerType(PDEButtonLayerType layerType) {
        mBackgroundButton.setButtonBackgroundLayerWithLayerType(layerType);
    }


    /**
     * @brief Set one of default backgrounds by string
    */
    public void setInputFieldBackgroundLayerWithLayerTypeString(String layerType) {
        mBackgroundButton.setButtonBackgroundLayerWithLayerTypeString(layerType);
    }


    /**
     * @brief Called on PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED event changes from agentController of the clear button.
     */
    public void onClearButtonClicked(PDEEvent event) {
        PDEInputFieldEvent shouldClearTextEvent;
        PDEInputFieldEvent didClearTextEvent;

        // send event
        shouldClearTextEvent =new PDEInputFieldEvent();
        shouldClearTextEvent.setType(PDE_INPUTFIELD_EVENT_ACTION_SHOULD_CLEAR_TEXT);
        shouldClearTextEvent.setSender(this);
        getEventSource().sendEvent(shouldClearTextEvent);

        if (shouldClearTextEvent.getShouldDoAction()){
            clearEditTextField();
            didClearTextEvent =new PDEInputFieldEvent();
            didClearTextEvent.setType(PDE_INPUTFIELD_EVENT_ACTION_DID_CLEAR_TEXT);
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
        event.setType(PDE_INPUTFIELD_EVENT_ACTION_BEFORE_TEXT_CHANGED);
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
        event.setType(PDE_INPUTFIELD_EVENT_ACTION_ON_TEXT_CHANGED);
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

        //just check text and update colors if there is no more text, needed for hint color bug
        // !!!!! WORKAROUND to be able to set hint text color after setText and setHint !!!!!
        // we have to set the hint text color here after the text is changed manually else we got some android bugs with the hint/text color
        // when we to something like this
        // mInputField = (PDEInputField)findViewById(R.id.pdeInputField);
        // mInputField.addListener(this,"onInputFieldEventFromAgentController",PDEInputField.PDE_INPUTFIELD_EVENT_MASK_ACTION);
        // mInputField.setText("zuzu");
        // mInputField.setHint("käse");
        // mInputField.setHintTextColor(PDEColor.valueOf("#FF0000"));
        // mInputField.setTextColor(PDEColor.valueOf("#00FF00"));
        //
        // if we dont reset the hint color, android lost the hinttext color after we have a setText("") or else somewhere after the code above
        // it seems that the editText lost the hint when the color is set for the first time, but after an setText -> change the order -> correct hint and text colors
        if(TextUtils.isEmpty(editable)) {
            updateTextColor();
            updateHintTextColor();
        }

        // send event
        event =new PDEInputFieldEvent();
        event.setType(PDE_INPUTFIELD_EVENT_ACTION_AFTER_TEXT_CHANGED);
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
                event.setType(PDE_INPUTFIELD_EVENT_ACTION_GOT_FOCUS);
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
                event.setType(PDE_INPUTFIELD_EVENT_ACTION_LOST_FOCUS);
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
        event.setType(PDE_INPUTFIELD_EVENT_ACTION_SHOULD_DO_EDITOR_ACTION);
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
     * @return true if the clear button is enabled, false if not
     */
    public boolean isClearButtonEnabled() {
        return mClearButtonEnabled;
    }


    /**
     * @brief Show or hide the clear button on the right side of the inputfield.
     */
    private void updateClearButton() {
        // because the parent of the clear button is a square layout we have to set the clear button container visibility
        if (mEditTextView.hasFocus() && mClearButtonEnabled
                && !TextUtils.isEmpty(mEditTextView.getText())){
            mClearButtonContainer.setVisibility(VISIBLE);
        } else {
            mClearButtonContainer.setVisibility(GONE);
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
     * @brief Private function - set the text color.
     *
     * function for the text color which evaluates the parameters.
     */
    private void updateTextColor() {
        Object colorObject;
        PDEColor newTextColor;

        // get font parameters
        colorObject = mParameters.parameterObjectForName(PDEInputField.PDEInputFieldParameterTextColor);

        // determine font
        if (colorObject instanceof String) {
            newTextColor = PDEColor.valueOf((String)colorObject);
        } else if  (colorObject instanceof PDEColor) {
            newTextColor = (PDEColor)colorObject;
        } else {
            // use default
            newTextColor = PDEColor.valueOf(getContext().getResources().getColor(color.DTLightUIText));
        }

        // check color change
        if( newTextColor.equals(PDEColor.valueOf(mEditTextView.getCurrentTextColor())) ) return;

        // simply set the color
        mEditTextView.setTextColor(newTextColor.getIntegerColor());
        updateClearButtonTextColor();
    }


    /**
     * @brief Private function - set the hint text color.
     *
     * function for the hint text color which evaluates the parameters.
     */
    private void updateHintTextColor() {
        Object colorObject;
        PDEColor newHintTextColor;

        // get font parameters
        colorObject = mParameters.parameterObjectForName(PDEInputField.PDEInputFieldParameterHintTextColor);

        // determine font
        if (colorObject instanceof String) {
            newHintTextColor = PDEColor.valueOf((String)colorObject);
        } else if  (colorObject instanceof PDEColor) {
            newHintTextColor = (PDEColor)colorObject;
        } else {
            // use default
            newHintTextColor = PDEColor.valueOf(getContext().getResources().getColor(color.DTLightUIIndicativeText));
        }

        // check color change
        if( newHintTextColor.equals(PDEColor.valueOf(mEditTextView.getCurrentHintTextColor())) ) return;

        // simply set the color
        mEditTextView.setHintTextColor(newHintTextColor.getIntegerColor());
    }


    /**
     * @brief Private function - prepare the font.
     *
     * Preparation function for the font which evaluates the parameters. Note that we need to
     */
    private void prepareFont() {
        Object fontObject,fontSizeObject;
        PDEInputFieldFontMode fontMode;
        float fontSize;
        String str;
        PDETypeface lfont=null;

        // get font parameters
        fontObject = mParameters.parameterObjectForName(PDEInputField.PDEInputFieldParameterFont);
        fontSizeObject = mParameters.parameterObjectForName(PDEInputField.PDEInputFieldParameterFontSize);

        // determine font
        if (fontObject instanceof PDETypeface) {
            lfont = (PDETypeface)(fontObject);
        }

        // determine font size
        if (fontSizeObject instanceof Number) {
            fontSize = ((Number)fontSizeObject).floatValue();
            fontMode = PDEInputFieldFontMode.Fixed;
        } else if (fontSizeObject instanceof String) {
            // extract string
            str = (String) fontSizeObject;
            // button specific mode / fixed size?
            if (str.compareToIgnoreCase(PDEInputField.PDEInputFieldParameterValueSizeAuto)==0
                    || str.compareToIgnoreCase(PDEInputField.PDEInputFieldParameterValueSizeAutomatic) == 0) {
                // automatic sizing
                fontMode = PDEInputFieldFontMode.Automatic;
                fontSize = 0.0f;
            } else if (str.compareToIgnoreCase(PDEInputField.PDEInputFieldParameterValueSizeStyleguide) == 0) {
                // styleguide defined sizes sizing
                fontMode = PDEInputFieldFontMode.Styleguide;
                fontSize = 0.0f;
            } else {
                //not a button specific value -> parse string
                fontSize = PDEFontHelpers.parseFontSize(str, lfont, getContext().getResources().getDisplayMetrics());
                if (Float.isNaN(fontSize)) {
                    Log.e(LOG_TAG,"could not parse font string correctly: "+str);
                    fontSize = 24.0f;
                }
                fontMode = PDEInputFieldFontMode.Fixed;
            }
        } else {
            fontSize = 0.0f;
            fontMode = PDEInputFieldFontMode.Styleguide;
        }
        // changed?
        if (fontMode==mFontMode && fontSize==mFontSize && lfont==mFont) return;
        // remember
        mFontMode = fontMode;
        mFontSize = fontSize;
        //create font independent from size, so do it once here, or take parameter font
        if (lfont == null ) {
            //use default font if we dont have a correct one !!!!!!!!!!
            mFont = PDEFontHelpers.validFont(lfont);
        } else {
            // remember external font
            mFont = lfont;
        }

        // update font size, and relayout
        updateFontSize(false);
    }


    /**
     * @brief Calculate the new font size and apply it.
     *
     * Also calculate helper values necessary later in layouting. Important note: This function
     * does not issue a relayout on font changes - ensure a relayout is done properly outside of this function.
     * This avoids recursive calls of updateFontSize and updateLayout.
     */
    private void updateFontSize(boolean force) {
        float fontSize;
        boolean fontInCaps;

        // automatic modes calculate font size from height
        if (mFontMode == PDEInputFieldFontMode.Automatic) {
            // height in caps
            fontSize = mHeight / 3.0f;
            fontInCaps = true;
        } else if (mFontMode == PDEInputFieldFontMode.Styleguide) {
            // standardize the font to 4*BU, 3*BU, 2.5*BU button size height
            if (mHeight > PDEBuildingUnits.exactPixelFromBU(3.5f)) {
                // display height > 3,5*BU -> font for display height of 4*BU
                fontSize = PDEBuildingUnits.pixelFromBU(4.0f) / 3.0f;
            } else if (mHeight > PDEBuildingUnits.exactPixelFromBU(2.75f)) {
                // 2,75*BU < display height <= 3,5*BU -> font for display height of 3*BU
                fontSize = PDEBuildingUnits.exactBU();
            } else {
                // display height < 2,75*BU -> font for display height of 2,5*BU
                fontSize = PDEBuildingUnits.pixelFromBU(2.5f) / 3.0f;
            }
            fontInCaps = true;
        } else if (mFontMode == PDEInputFieldFontMode.Fixed) {
            // height in native font size
            fontSize = mFontSize;
            fontInCaps = false;
        } else {
            // unknown mode -> do nothing
            return;
        }

        // compare with last value, stop if not changed
        if (!force && fontSize==mFontUsedSize && fontInCaps==mFontUsesCaps) return;

        // remember
        mFontUsedSize = fontSize;
        mFontUsesCaps = fontInCaps;

        // eventually convert caps height to real height
        if (mFontUsesCaps) {
            fontSize = PDEFontHelpers.calculateFontSize(mFont, fontSize);
        }

        // assure readable font size
        fontSize = PDEFontHelpers.assureReadableFontSize(mFont, fontSize);

        // set the font and font size to the text layer
        mEditTextView.setTypeface(mFont.getTypeface());
        mEditTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);

        //the x on clear button should be 75% of the button text height so calculate this new size
       mClearButton.setFontSize(fontSize*mClearButtonFontHeightToTextHeightRatio);
    }


    /**
     * @brief Set title color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTextColor(PDEColor color) {
        // set the parameters
        mParameters.setParameter(PDEInputField.PDEInputFieldParameterTextColor, color);
        updateTextColor();
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
        return mParameters.parameterColorForName(PDEInputField.PDEInputFieldParameterTextColor);
    }


    /**
     * @brief Set hint text color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setHintTextColor(PDEColor color) {
        // set the parameters
        mParameters.setParameter(PDEInputField.PDEInputFieldParameterHintTextColor, color);
        updateHintTextColor();
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
        return mParameters.parameterColorForName(PDEInputField.PDEInputFieldParameterHintTextColor);
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
        mParameters.setParameter(PDEInputField.PDEInputFieldParameterFont, font);
        prepareFont();
    }


    /**
     * @brief Get the font.
     *
     * Only retrieves basic parameters, and only if a font was explicitly set before.
     */
    public PDETypeface getFont() {
        Object object;

        // get the object
        object = mParameters.parameterObjectForName(PDEInputField.PDEInputFieldParameterFont);

        // typecheck
        if (object == null || !(object instanceof PDETypeface)) return null;

        // done
        return (PDETypeface) object;
    }


    /**
     * @brief Set the font size directly.
     *
     * @param fontSize of the font (in point)
     */
    public void setFontSize(float fontSize) {
        // set directly as number
        mParameters.setParameter(PDEInputField.PDEInputFieldParameterFontSize, fontSize);
        prepareFont();
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
        mParameters.setParameter(PDEInputField.PDEInputFieldParameterFontSize, sizeString);
        prepareFont();
    }


    /**
     * @brief Get font size (float).
     *
     * Only retrieves basic parameters, and only if a float was directly set.
     * If you used setFontSizeWithString use fontSizeString to get the font size.
     * @return the set font size in float or 0.0f if the parameter was not set or was set as a string.
     */
    public float getFontSize() {
        if(mParameters.parameterObjectForName(PDEInputField.PDEInputFieldParameterFontSize) instanceof Float) {
            return mParameters.parameterFloatForName(PDEInputField.PDEInputFieldParameterFontSize);
        }

        return 0.0f;
    }


    /**
     * @brief Get font size string.
     *
     * Only retrieves basic parameters, and only if a string was previously set
     */
    public String getFontSizeString() {
        // retrieve parameter main value
        return mParameters.parameterValueForName(PDEInputField.PDEInputFieldParameterFontSize);
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
     * Interface only defined when SDK version >= 16 - before it will always return false;
     * Get the  value of the edittextview, input field doesn't holds value itself.
     */
    public boolean isCursorVisible() {
        if (Build.VERSION.SDK_INT >= 16) {
            return mEditTextView.isCursorVisible();
        } else {
            return false;
        }
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


//----- layout/sizing information --------------------------------------------------------------------------------------


    /**
     *  @brief Size changed.
     *
     * @param width New width.
     * @param height New height.
     * @param oldWidth Old width.
     * @param oldHeight Old height.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        doLayout(width,height);
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }


    /**
     * @brief Update all subviews of the TextField.
     *
     * Get the TextField bounds and update all subviews
     */
    private void doLayout(int width, int height) {
        mWidth = width;
        mHeight = height;
        updateFontSize(false);
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
     * @return Parcelable
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PDEInputField.class.getSimpleName(), super.onSaveInstanceState());
        bundle.putParcelable(mEditTextView.getClass().getSimpleName(), mEditTextView.onSaveInstanceState());
        return bundle;
    }


    /**
     * Restore the saved state.
     * @param state Parcelable state.
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(PDEInputField.class.getSimpleName()));
            mEditTextView.onRestoreInstanceState(bundle.getParcelable(mEditTextView.getClass().getSimpleName()));
            return;
        } else {
            super.onRestoreInstanceState(state);
        }
        super.onRestoreInstanceState(state);
    }
}
