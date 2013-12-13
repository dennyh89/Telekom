/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.helpers;



import android.graphics.Rect;


//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayoutHelper
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief PDEButton layout structure helper.
 *
 * Contains some useful layouting rects. Layouts are sent to the button layers in order and may be modified
 * by them to constrain other layers.
 */
public class PDEButtonLayoutHelper
{
    // layouting rect
    public Rect mButtonRect; //screenrect
    public Rect mLayoutRect; //outlineRect
    public Rect mClipRect;

    @SuppressWarnings("unused")
    public PDEButtonLayoutHelper() {
        mButtonRect = new Rect(0, 0, 0, 0);
        mLayoutRect = new Rect(0, 0, 0, 0);
        mClipRect = new Rect(0, 0, 0, 0);
    }

    @SuppressWarnings("unused")
    public PDEButtonLayoutHelper(PDEButtonLayoutHelper helper) {
        mButtonRect = new Rect(helper.mButtonRect);
        mLayoutRect = new Rect(helper.mLayoutRect);
        mClipRect = new Rect(helper.mClipRect);
    }

    public boolean equals(PDEButtonLayoutHelper helper){
        return (mButtonRect.left == helper.mButtonRect.left &&
           mButtonRect.top == helper.mButtonRect.top &&
           mButtonRect.right == helper.mButtonRect.right &&
           mButtonRect.bottom == helper.mButtonRect.bottom &&
           mLayoutRect.left == helper.mLayoutRect.left &&
           mLayoutRect.top == helper.mLayoutRect.top &&
           mLayoutRect.right == helper.mLayoutRect.right &&
           mLayoutRect.bottom == helper.mLayoutRect.bottom &&
           mClipRect.left == helper.mClipRect.left &&
           mClipRect.top == helper.mClipRect.top &&
           mClipRect.right == helper.mClipRect.right &&
           mClipRect.bottom == helper.mClipRect.bottom);
    }
}
