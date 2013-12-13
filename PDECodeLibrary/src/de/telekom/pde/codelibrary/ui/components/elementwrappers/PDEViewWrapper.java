/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.elementwrappers;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        boolean clippingDrawableSet = false;
        Method method;
        if (drawable != null){
            mDrawable = drawable;

            PDEUtils.setViewBackgroundDrawable(this,mDrawable);

            if (!clippingDrawableSet) {
                Log.e(LOG_TAG, "could not draw background");
            }
        }
    }

    // todo can we really work with PDEAbsoluteLayout LayoutParams only here?


    /**
     * @brief Set size of view in PDEAbsoluteLayout.
     * The width and height values are rounded mathematically to integer values.
     */
    public void setViewSize(float width, float height){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        if (layerParams == null) {
            layerParams = new PDEAbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0,
                    0);
        }
        layerParams.width = Math.round(width);
        layerParams.height = Math.round(height);
        setLayoutParams(layerParams);
    }


    /**
     * @brief Set offset of view in PDEAbsoluteLayout.
     * The x and y values are rounded mathematically to integer values.
     */
    public void setViewOffset(float x, float y){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        if (layerParams == null) {
            layerParams = new PDEAbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0,
                    0);
        }
        layerParams.x = Math.round(x);
        layerParams.y = Math.round(y);
        setLayoutParams(layerParams);
    }


    /**
     * @brief Set layout rect of view in PDEAbsoluteLayout.
     */
    public void setViewLayoutRect(Rect rect) {
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        if (layerParams == null) {
            layerParams = new PDEAbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0,
                    0);
        }
        layerParams.x = rect.left;
        layerParams.y = rect.top;
        layerParams.width = rect.width();
        layerParams.height = rect.height();

        setLayoutParams(layerParams);
    }

}
