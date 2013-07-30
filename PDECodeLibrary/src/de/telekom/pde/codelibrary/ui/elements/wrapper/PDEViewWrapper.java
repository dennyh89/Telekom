/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.wrapper;

import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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

    public PDEViewWrapper(Context context, AttributeSet attrs, Drawable drawable){
        super(context,attrs);
        init(drawable);
    }

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
            // setBackgroundDrawable is marked as deprecated in api level 16, in order to avoid the warning use reflection.
            try {
                //try to use the setBackground function which was introduced in android 4.1 (api level 16)
                method = getClass().getMethod("setBackground", new Class[] {Drawable.class});
                method.invoke(this,mDrawable);
                clippingDrawableSet = true;
            } catch (NoSuchMethodException e) {
                // function not available
            } catch (IllegalAccessException e) {
                // function not available
            } catch (InvocationTargetException e) {
                // function not available
            }

            if (!clippingDrawableSet) {
                try {
                    //try to use the setBackgroundDrawable which is deprecated in android 4.1
                    method = getClass().getMethod("setBackgroundDrawable", new Class[] {Drawable.class});
                    method.invoke(this,mDrawable);
                    clippingDrawableSet = true;
                } catch (NoSuchMethodException e) {
                    // function not available
                } catch (IllegalAccessException e) {
                    // function not available
                } catch (InvocationTargetException e) {
                    // function not available
                }
            }

            if (!clippingDrawableSet) {
                Log.e(LOG_TAG, "could not draw background");
            }
        }
    }

    public void setViewSize(float width, float height){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.width = Math.round(width);
        layerParams.height = Math.round(height);
        setLayoutParams(layerParams);
    }

    public void setViewOffset(float x, float y){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.x = Math.round(x);
        layerParams.y = Math.round(y);
        setLayoutParams(layerParams);
    }

    public void setViewLayoutRect(Rect rect) {
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.x = rect.left;
        layerParams.y = rect.top;
        layerParams.width = rect.width();
        layerParams.height = rect.height();

        setLayoutParams(layerParams);
    }

}
