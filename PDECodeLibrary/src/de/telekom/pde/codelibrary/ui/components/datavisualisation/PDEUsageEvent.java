/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.datavisualisation;

import de.telekom.pde.codelibrary.ui.events.PDEEvent;

//----------------------------------------------------------------------------------------------------------------------
//  PDEUsageEvent
//----------------------------------------------------------------------------------------------------------------------

// special event for usage bar and usage circle
public class PDEUsageEvent extends PDEEvent {

    // events
    public static final String EVENT_MASK_ACTION = "PDEUsageEvent.action.*";
    public static final String EVENT_ACTION_NEW_FILL_VALUE = "PDEUsageEvent.action.NewValue.Fill";
    public static final String EVENT_ACTION_NEW_TOTAL_VALUE = "PDEUsageEvent.action.NewValue.Total";
    public static final String EVENT_ACTION_ANIMATION_FINISHED = "PDEUsageEvent.action.Animation.Finished";

    // interesting fill values
    protected double mCurrentFillValue;
    protected double mTotalValue;

    public PDEUsageEvent(){
        mTotalValue = 0.0f;
        mCurrentFillValue = 0.0f;
    }

    public void setCurrentFillValue(double value) {
        mCurrentFillValue = value;
    }

    public double getCurrentFillValue() {
        return mCurrentFillValue;
    }

    public void setTotalValue(double value) {
        mTotalValue = value;
    }

    public double getTotalValue() {
        return mTotalValue;
    }
}
