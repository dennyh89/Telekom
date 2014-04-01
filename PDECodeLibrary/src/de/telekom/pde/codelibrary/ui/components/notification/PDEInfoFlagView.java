/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.notification;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;


/// @cond INTERNAL_CLASS

//----------------------------------------------------------------------------------------------------------------------
// PDEInfoFlagView
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Info Flag Notification under the terms of the styleguide.
 *
 */
public class PDEInfoFlagView extends PDENotificationBaseView {

    protected boolean mTitleEnabled;

    /**
     * @brief Constructor.
     */
    public PDEInfoFlagView(Context context){
        super(context);
    }


    /**
     * @brief Constructor.
     */
    public PDEInfoFlagView(Context context, AttributeSet attrs){
        super(context, attrs);
    }


    /**
     * @brief Special initialisation of Info Flag View
     *
     * @param attrs xml attributes
     */
    protected void init(AttributeSet attrs){
        // if in developer tool (IDE) stop here
        if (isInEditMode()) return;

        mTitleEnabled = true;

        // base class init
        super.init(attrs);

        // InfoFlag View has different corner rounding than the base class
        mSpeechBubble.setElementCornerRadius(PDEBuildingUnits.twoThirdsBU());
        // adapt distance between title and message
        mTitle.setPaddingBottom(PDEBuildingUnits.oneHalfBU());
        updateMessagePadding();
    }


    /**
     * @brief Load XML attributes.
     *
     *
     */
    protected void setAttributes(AttributeSet attrs){
        // security
        if (attrs == null) return;

        // base class call
        super.setAttributes(attrs);

        TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEInfoFlagView);

        // enable / disable title
        if (sa.hasValue(R.styleable.PDEInfoFlagView_titleEnabled)) {
            setTitleEnabled(sa.getBoolean(R.styleable.PDEInfoFlagView_titleEnabled, true));
        }
    }


    /**
     * @brief Enable / Disable title.
     *
     * If the title is disabled we have a plain multiline notification left. This is NOT defined in styleguide visuals!!!
     * However it was used this way in the login sample.
     *
     * @param enable enable/disable title
     */
    public void setTitleEnabled(boolean enable){
        // anything to do?
        if (mTitleEnabled == enable) return;
        // remember
        mTitleEnabled = enable;

        // update
        if (enable) {
            mTitle.setVisibility(VISIBLE);
        } else {
            mTitle.setVisibility(GONE);
        }
        updateMessagePadding();
    }


    /**
     * @brief Check if title is enabled / disabled.
     */
    @SuppressWarnings("unused")
    public boolean isTitleEnabled(){
        return mTitleEnabled;
    }


    /**
     * @brief Update helper.
     */
    protected void updateMessagePadding(){
        if (mTitleEnabled) {
            mMessage.setPaddingTop(PDEBuildingUnits.oneHalfBU());
        } else {
            mMessage.setPaddingTop(PDEBuildingUnits.BU());
        }
    }
}


/// @endcond INTERNAL_CLASS