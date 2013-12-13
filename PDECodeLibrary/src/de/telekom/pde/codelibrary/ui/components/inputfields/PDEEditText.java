/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.inputfields;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.R.color;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentControllerAdapterView;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.icon.PDEDrawableIcon;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;

import java.lang.reflect.Field;


/// @cond INTERNAL_CLASS

//----------------------------------------------------------------------------------------------------------------------
//  PDEEditText
//----------------------------------------------------------------------------------------------------------------------

// package protected no public/private/protected keyword
class PDEEditText extends EditText {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEEditText.class.getName();

    //----- properties -----

    // private variables

    // agent controller and helpers
    private PDEAgentController mAgentController = null;
    private PDEAgentControllerAdapterView mAgentControllerAdapter = null;

    PDEDrawableIcon mLeftIcon;

    // internal icon-font ratio
    float mIconToTextHeightRatio;


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEEditText(Context context) {
        super(context);
        init();
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    /**
     * @brief Init function to initialise start properties of the edittext.
     */
    private void init() {
        mIconToTextHeightRatio = 1.7f;

        // set default light blue colors for highlight
        setHighlightColor(getContext().getResources().getColor(color.DTLightUITextHighlight));
        setTextCursorDrawable(R.drawable.cursor_drawable);
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
        Class<?> tmpClass = getClass();

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
     * @brief Private function - set the icon color.
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
                iconSize.y = PDEBuildingUnits.roundToScreenCoordinates(PDEFontHelpers.getCapHeight(getTypeface(),getTextSize())*mIconToTextHeightRatio);
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
     * @brief Update the Text color, and also the left icon.
     */
    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        //check to update also icon color
        updateLeftIconColor();
    }


    /**
     * @brief Set typeface.
     */
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
        //size maybe changed so update left icon if exists
        updateLeftIcon();
    }


    /**
     * @brief Set text size.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit,size);
        //size maybe changed so update left icon if exists
        updateLeftIcon();
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
        return (mLeftIcon!=null);
    }

    /**
     * @brief Returns ratio of icon height to text height
     */
    public float getIconToTextHeightRatio() {
        return mIconToTextHeightRatio;
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