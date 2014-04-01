/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.elementwrappers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;

//----------------------------------------------------------------------------------------------------------------------
//  PDEViewWrapper
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Wrapper class hosting Drawable for usage in Layouts
 */
public class PDEViewWrapper extends View {

    protected Drawable mDrawable;
    private final static String LOG_TAG = PDEViewWrapper.class.getName();


    public PDEViewWrapper(Context context, Drawable drawable){
        super(context);
        init(drawable);
    }


    @SuppressWarnings("unused")
    public PDEViewWrapper(Context context, AttributeSet attrs, Drawable drawable){
        super(context,attrs);
        init(drawable);
    }


    @SuppressWarnings("unused")
    public PDEViewWrapper(Context context, AttributeSet attrs, int defStyle, Drawable drawable){
        super(context,attrs,defStyle);
        init(drawable);
    }


    protected void init(Drawable drawable){
        setDrawable(drawable);
    }


    public Drawable getDrawable(){
        return mDrawable;
    }


    public void setDrawable(Drawable drawable){
        if (drawable != null){
            mDrawable = drawable;
            PDEUtils.setViewBackgroundDrawable(this, mDrawable);
        }
    }
}
