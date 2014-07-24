/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.notification;

import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;

import android.content.Context;
import android.util.AttributeSet;


//----------------------------------------------------------------------------------------------------------------------
// PDEToolTip
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Tool Tip Notification under the terms of the styleguide.
 */
public class PDEToolTip extends PDENotificationBase {


    /**
     * @brief Constructor.
     */
    public PDEToolTip(Context context) {
        super(context);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEToolTip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * @param attrs xml attributes
     * @brief Special initialisation of Tool Tip View
     */
    protected void init(Context context, AttributeSet attrs) {
        // if in developer tool (IDE) stop here
        if (isInEditMode()) return;

        // base class init
        super.init(context, attrs);

        // Tool Tip has different corner rounding than the base class
        mSpeechBubble.setElementCornerRadius(PDEBuildingUnits.oneThirdBU());

        // Tool Tip has no title
        mTitle.setVisibility(GONE);

        // Message is single lined and ellipsized
        mMessage.setMaxLines(1);
        mMessage.setEllipsize(true);
    }
}

