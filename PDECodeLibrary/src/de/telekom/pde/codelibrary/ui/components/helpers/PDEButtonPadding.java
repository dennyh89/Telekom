/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.helpers;


import android.graphics.Rect;

//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonPadding
//----------------------------------------------------------------------------------------------------------------------


public class PDEButtonPadding {
    protected Rect mPaddingRect;

    public PDEButtonPadding(){
        mPaddingRect = new Rect(0,0,0,0);
    }

    @SuppressWarnings("unused")
    public void putPaddingRequest (int left, int top, int right, int bottom){
        // only remember request values if they're larger than the ones we already collected
        if(left>mPaddingRect.left){
            mPaddingRect.left = left;
        }
        if(top>mPaddingRect.top){
            mPaddingRect.top = top;
        }
        if(right>mPaddingRect.right){
            mPaddingRect.right = right;
        }
        if(bottom>mPaddingRect.bottom){
            mPaddingRect.bottom = bottom;
        }
    }
    public void putPaddingRequest (Rect rect){
        // only remember request values if they're larger than the ones we already collected
        if(rect.left>mPaddingRect.left){
            mPaddingRect.left = rect.left;
        }
        if(rect.top>mPaddingRect.top){
            mPaddingRect.top = rect.top;
        }
        if(rect.right>mPaddingRect.right){
            mPaddingRect.right = rect.right;
        }
        if(rect.bottom>mPaddingRect.bottom){
            mPaddingRect.bottom = rect.bottom;
        }
    }

    public int getLeft(){
        return mPaddingRect.left;
    }

    public int getTop(){
        return mPaddingRect.top;
    }
    public int getRight(){
        return mPaddingRect.right;
    }
    public int getBottom(){
        return mPaddingRect.bottom;
    }

    public Rect getPaddingRect(){
        return mPaddingRect;
    }
}
