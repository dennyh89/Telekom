/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.inputfields;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.R.color;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentControllerAdapterView;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.icon.PDEDrawableIcon;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;

import java.lang.reflect.Field;


/// @cond INTERNAL_CLASS

//----------------------------------------------------------------------------------------------------------------------
//  PDEEditText
//----------------------------------------------------------------------------------------------------------------------

// package protected no public/private/protected keyword
class PDEEditText extends EditText implements TextWatcher {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEEditText.class.getName();

    // Helper listener class to get informatio about size change
    public static interface OnFontSizeChangedListener {
        void onFontSizeChanged(float newSize);
    }

    // font modes
    enum PDEEditTextFontMode {
        Undefined,
        Automatic,
        Styleguide,
        Fixed
    }

    //----- properties -----

    // private variables

    // agent controller and helpers
    private PDEAgentController mAgentController = null;
    private PDEAgentControllerAdapterView mAgentControllerAdapter = null;

    private PDETypeface mFont;
    private PDEEditTextFontMode mFontMode;
    float mFontSize;
    float mDisplayWidth;
    float mDisplayHeight;

    PDEDrawableIcon mLeftIcon;
    OnFontSizeChangedListener mOnFontSizeChangedListener;

    // internal font and title configuration
    float mFontUsedSize;
    boolean mFontUsesCaps;
    float mIconToTextHeightRatio;

    // parameters needed
    private PDEParameterDictionary mParameters;


    /**
     * @brief Constructor.
     */
    public PDEEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    /**
     * @brief Constructor.
     */
    public PDEEditText(Context context) {
        super(context);
        init();
    }


    /**
     * @brief Constructor.
     */
    public PDEEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    /**
     * @brief Init function to initialise start properties of the edittext.
     */
    private void init() {
        mDisplayWidth = 0.0f;
        mDisplayHeight = 0.0f;

        mFont = null;
        mFontMode = PDEEditTextFontMode.Undefined;
        mFontSize = 0.0f;
        mFontUsedSize = 0.0f;
        mFontUsesCaps = false;
        mIconToTextHeightRatio = 2.0f;

        mOnFontSizeChangedListener = null;

        addTextChangedListener(this);
        // set default light blue colors for highlight
        setHighlightColor(getContext().getResources().getColor(color.DTLightUITextHighlight));
        setTextCursorDrawable(R.drawable.cursor_drawable);

        // set of parameter -> and start prepare font and colors -> this sets defaults (text color, hint color...)
        mParameters = new PDEParameterDictionary();
        prepareFont();
        updateTextColor();
        updateHintTextColor();
    }

   /* @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {

        Parcelable state = super.onSaveInstanceState();
        ViewParent p = getParent();
        View pp = (View)p.getParent();
            container.put(getId(), state);
    }*/

    /**
     * @brief Set a drawable to use for the cursor.
     *          This is no public function because for now this could only set on android in xml.
     *          So we dont know the exact behaviour of TextView if the value is changed during lifecycle, so we only allow/set
     *          this value in the init at first.
     *
     *  In API Level 12 Android introduce the "android:textCursorDrawable" attribute to allow a change of the cursor color
     *  Before this API Level the Cursor color should be always the text color.
     *  But when there is no textCursorDrawable set, API >= 12 have a white cursor
     *  !!!!!!!!!!!!!!!! WE CANT SEE THIS ON DEFAULT PDEINPUTFIELD WITH NEARLY WHITE BACKGROUND !!!!!!!!!!!!!!!!!!!!!!!!
     *
     * Because there is no function to set this value, we have to set the variable itself using reflection to set a correct
     * cursor color. We cant set this value via XML, because otherwise we cant compile with API Lever 11 or lower.
     * TODO: When a function is introduces -> try this function at first to have the best practise!!
     *
     * Behaviour seems to be like this:
     *    App Target sdk < 12 on device with android < 12 (there is no mCursorDrawableRes variable) -> cursor color default or text color
     *    App Target sdk < 12 on device with android >= 12 (without mCursorDrawableRes variable) -> cursor color default or text color
     *    App Target sdk < 12 on device with android >= 12 (with mCursorDrawableRes variable) -> cursor color is set (magenta default)
     *    App Target sdk >=12 -> use Android mCursorDrawableRes behaviour and set cursor to white by default
     *                        -> but our cursor color is set (magenta default) when mCursorDrawableRes variable exists
     */
    private void setTextCursorDrawable(int drawableId) {
        // valid?
        if(drawableId==-1)return;

        Field field = null;
        Class tmpClass = getClass();

        while(tmpClass!=null) {
            if(tmpClass==TextView.class){
                try {
                    field = tmpClass.getDeclaredField("mCursorDrawableRes");
                    if(field!=null){
                        field.setAccessible(true);
                        field.setInt(this, drawableId);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG,"Cant set cursor drawable for cursor color. Maybe the Android version is < 12 ??");
                    //e.printStackTrace();
                }
                // we finished trying to change cursor color
                return;
            }
            tmpClass=tmpClass.getSuperclass();
        }
    }


    /**
     * @brief Set the target listener, that listen to touches on this view.
     */
    public void setTargetListener(PDEIEventSource targetListener) {
        // valid?
        if(targetListener==null) return;

        // create agent controller
        mAgentController = new PDEAgentController();

        // link it via appropriate adapter
        mAgentControllerAdapter = new PDEAgentControllerAdapterView();
        mAgentControllerAdapter.linkAgent(mAgentController, this);

        // catch agent controller events for animation
        mAgentControllerAdapter.getEventSource().addListener(targetListener, "cbAgentController",
                PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION);
        mAgentControllerAdapter.getEventSource().requestOneTimeInitialization(targetListener, "cbAgentControllerSingle",
                PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION);

        // pass on agent adapter events to ourself, override the sender
        targetListener.getEventSource().forwardEvents(mAgentControllerAdapter,
                PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ACTION);
        targetListener.getEventSource().setEventDefaultSender(targetListener, true);
    }


    /**
     * @brief Get AgentController used by the button
     */
    public PDEAgentController getAgentController() {
        return mAgentController;
    }


    /**
     * @brief: Private function - set the text color.
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
        if( newTextColor.equals(PDEColor.valueOf(getCurrentTextColor())) ) return;

        // simply set the color
        setTextColor(newTextColor.getIntegerColor());

        //check to update also icon color
        updateLeftIconColor();
    }


    /**
     * @brief: Private function - set the icon color.
     *
     * Update the icon color if needed. At the moment only update iconFont
     */
    private void updateLeftIconColor() {
        //valid?
        if(mLeftIcon==null) return;
        if(mLeftIcon.isIconfont()) {
            //set the current text color, not the parameter color (parameters dont hold default values...)
            mLeftIcon.setElementIconColor(PDEColor.valueOf(getCurrentTextColor()));
        }
    }


    /**
     * @brief: Private function - set the hint text color.
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
        if( newHintTextColor.equals(PDEColor.valueOf(getCurrentHintTextColor())) ) return;

        // simply set the color
        setHintTextColor(newHintTextColor.getIntegerColor());
    }


    /**
     * @brief Private function - prepare the font.
     *
     * Preparation function for the font which evaluates the parameters. Note that we need to
     */
    private void prepareFont() {
        Object fontObject,fontSizeObject;
        PDEEditTextFontMode fontMode;
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
            fontMode = PDEEditTextFontMode.Fixed;
        } else if (fontSizeObject instanceof String) {
            // extract string
            str = (String) fontSizeObject;
            // button specific mode / fixed size?
            if (str.compareToIgnoreCase(PDEInputField.PDEInputFieldParameterValueSizeAuto)==0
                    || str.compareToIgnoreCase(PDEInputField.PDEInputFieldParameterValueSizeAutomatic) == 0) {
                // automatic sizing
                fontMode = PDEEditTextFontMode.Automatic;
                fontSize = 0.0f;
            } else if (str.compareToIgnoreCase(PDEInputField.PDEInputFieldParameterValueSizeStyleguide) == 0) {
                // styleguide defined sizes sizing
                fontMode = PDEEditTextFontMode.Styleguide;
                fontSize = 0.0f;
            } else {
                //not a button specific value -> parse string
                fontSize = PDEFontHelpers.parseFontSize(str, lfont, getContext().getResources().getDisplayMetrics());
                if (Float.isNaN(fontSize)) {
                    Log.e(LOG_TAG,"could not parse font string correctly: "+str);
                    fontSize = 24.0f;
                }
                fontMode = PDEEditTextFontMode.Fixed;
            }
        } else {
            fontSize = 0.0f;
            fontMode = PDEEditTextFontMode.Styleguide;
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

        //only update when there is text or icon
        if((mLeftIcon!=null && !mLeftIcon.hasElementIcon()) && TextUtils.isEmpty(getText()) ){
            return;
        }

        // automatic modes calculate font size from height
        if (mFontMode == PDEEditTextFontMode.Automatic) {
            // height in caps
            fontSize = mDisplayHeight / 3.0f;
            fontInCaps = true;
        } else if (mFontMode == PDEEditTextFontMode.Styleguide) {
            // standardize the font to 4*BU, 3*BU, 2.5*BU button size height
            if (mDisplayHeight > PDEBuildingUnits.exactPixelFromBU(3.5f)) {
                // display height > 3,5*BU -> font for display height of 4*BU
                fontSize = PDEBuildingUnits.pixelFromBU(4.0f) / 3.0f;
            } else if (mDisplayHeight > PDEBuildingUnits.exactPixelFromBU(2.75f)) {
                // 2,75*BU < display height <= 3,5*BU -> font for display height of 3*BU
                fontSize = PDEBuildingUnits.exactBU();
            } else {
                // display height < 2,75*BU -> font for display height of 2,5*BU
                fontSize = PDEBuildingUnits.pixelFromBU(2.5f) / 3.0f;
            }
            fontInCaps = true;
        } else if (mFontMode == PDEEditTextFontMode.Fixed) {
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
        setTypeface(mFont.getTypeface());
        setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);

        //inform listener about size change
        if(mOnFontSizeChangedListener!=null) {
            mOnFontSizeChangedListener.onFontSizeChanged(fontSize);
        }

        //size changed so update left icon if exists
        updateLeftIcon();
    }


    private void updateLeftIcon() {
        //valid?
        if(mLeftIcon==null) return;
        Point iconSize = new Point(0,0);

        // Is there a icon we need to adjust, normally there must be a icon if mLeftIcon is not null
        if (mLeftIcon.hasElementIcon()) {
            //has iconlayer a native size
            if(mLeftIcon.hasNativeSize()) {
                iconSize = mLeftIcon.getNativeSize();
            } else {
                //calculate icon font height
                iconSize.y = PDEBuildingUnits.roundToScreenCoordinates(PDEFontHelpers.getCapHeight(mFont,getTextSize())*mIconToTextHeightRatio);
                iconSize.x = iconSize.y;
            }
        }
        mLeftIcon.setLayoutSize(iconSize);
        //setCompoundDrawables must called when icon size changed because internal they work with the size the icon
        // had at the call of the function. Size change afterwards dont have any effect!!!
        setCompoundDrawables(mLeftIcon,null,null,null);
        updateLeftIconColor();
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
     * @brief Get text color.
     * Only returns values the user sets before. Default values are not returned.
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
     * @brief Get the hint color value of the edittextview.
     *
     * Get the hint color value of the edittextview, input field doesn't holds value itself.
     */
    public PDEColor getHintTextColor() {
        return mParameters.parameterColorForName(PDEInputField.PDEInputFieldParameterHintTextColor);
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
     * @brief Listener called before the text changed.
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int startPos, int count, int lengthAfter) {
        //nothing to do here;
    }


    /**
     * @brief Listener called when the text changed.
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int startPos, int lengthBefore, int count) {
        //nothing to do here
    }


    /**
     * @brief Listener called after the text changed.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        //just check text and update colors if there is no more text, needed for hint color bug
        // !!!!! WORKAROUND to be able to set hint text color after setText and setHint !!!!!
        // we have to set the hint text color here after the text is changed manually else we got some android bugs with the hint/text color
        // when we to something like this
        // mInputField = (PDEInputField)findViewById(R.id.pdeInputField);
        // mInputField.addListener(this,"onInputFieldEventFromAgentController",PDEInputField.PDEInputFieldEventMask);
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
    }


    private void createLeftIcon(Object object) {
        if(object!=null){
            mLeftIcon = new PDEDrawableIcon();
            mLeftIcon.setElementIcon(object);
            //setCompoundDrawables must called when icon size changed because internal they work with the size the icon
            // had at the call of the function. Size change afterwards dont have any effect!!!
        } else {
            mLeftIcon = null;
            setCompoundDrawables(null,null,null,null);
        }
        updateLeftIcon();
    }


    /**
     * @brief Set the left drawable by id for the inputfield (e.g magnifier for searchfield)
     */
    public void setLeftIcon(int drawableID) {
        try {
            createLeftIcon(getContext().getResources().getDrawable(drawableID));
        } catch (Exception exception) {
            createLeftIcon(null);
        }
    }


    /**
     * @brief Set the left drawable for the inputfield (e.g magnifier for searchfield)
     */
    public void setLeftIcon(Drawable drawable) {
        createLeftIcon(drawable);
    }


    /**
     * @brief Set the left icon string.
     *  Icon string can either be a # plus char, signalising to take the iconfont, or a resource string
     */
    public void setLeftIcon(String icon) {
        createLeftIcon(icon);
    }

    /**
     * @brief Set new ratio of Icon height to text height.
     *
     * @param ratio ratio of icon height to text height.
     */
    public void setIconToTextHeightRatio(float ratio) {
        // anything to do?
        if (ratio == mIconToTextHeightRatio) return;

        // remember
        mIconToTextHeightRatio = ratio;

        // update
        updateLeftIcon(); // enough?
    }


    /**
     * @brief Get the left icon of this inputfield
     */
    public Object getLeftIcon() {
        if(mLeftIcon!=null){
            return mLeftIcon.getElementIcon();
        }
        return null;
    }


    /**
     * @brief Get the left icon drawable of this inputfield
     */
    public Drawable getLeftIconDrawable() {
        if(mLeftIcon!=null){
            return mLeftIcon.getElementIconDrawable();
        }
        return null;
    }


    /**
     * @brief Get the left icon sting of this inputfield
     */
    public String getLeftIconString() {
        if(mLeftIcon!=null){
            return mLeftIcon.getElementIconString();
        }
        return null;
    }

    /**
     * @brief Returns true if icon image or icon string was set
     */
    public boolean hasLeftIcon() {
        if(mLeftIcon!=null){
            return true;
        }
        return false;
    }

    /**
     * @brief Returns ratio of icon height to text height
     */
    public float getIconToTextHeightRatio() {
        return mIconToTextHeightRatio;
    }


    /**
     * Set listener to inform about font size changes
     * @param listener
     */
    public void setOnFontSizeChangedListener(OnFontSizeChangedListener listener) {
        mOnFontSizeChangedListener = listener;
    }

    //----- layout/sizing information --------------------------------------------------------------------------------------


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        doLayout(w,h);
        super.onSizeChanged(w, h, oldw, oldh);
    }


    /**
     * @brief Update all subviews of the TextField.
     *
     * Get the TextField bounds and update all subviews
     */
    private void doLayout(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;

        // update the font size
       updateFontSize(false);
    }

//----- button state handling ------------------------------------------------------------------------------------------
    // !!!!!!!!!!!!!!! TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // WE ONLY HAVE TO USE 1 AGENTCONTROLLER, (AT THE MOMENT backgroundbutton and textfield both have one used)
    // IN THE FUTURE REPLACE ONE BY THE OTHER, AT THE MOMENT USE TEXFIELD CONTROLLER
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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
        return mAgentController.getState();
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
        // IN THE FUTURE REPLACE ONE BY THE OTHER, AT THE MOMENT USE TEXTFIELD CONTROLLER
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // map to agent controller
        mAgentController.setState(state);
    }


}


/// @endcond INTERNAL_CLASS