/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PDEViewBackground extends View {
    protected Drawable mDrawable;
    private final static String LOG_TAG = PDEViewBackground.class.getName();

    public PDEViewBackground(Context context, Drawable drawable){
        super(context);
        init(drawable);
    }

    public PDEViewBackground(Context context, AttributeSet attrs, Drawable drawable){
        super(context,attrs);
        init(drawable);
    }

    public PDEViewBackground(Context context, AttributeSet attrs, int defStyle, Drawable drawable){
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

            if (clippingDrawableSet == false) {
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

            if (clippingDrawableSet == false) {
                Log.e(LOG_TAG, "could not draw background");
            }
        }
    }

   /*
    public void setCornerRadius(float radius){
        mDrawable.setCornerRadius(radius);
    }

    public float getCornerRadius(){
        return mDrawable.getCornerRadius();
    }

    public void setBorderWidth(float width){
        mDrawable.setBorderWidth(width);
    }

    public float getBorderWidth(){
        return mDrawable.getBorderWidth();
    }

    public void setColors(int topColor,int middleColor,int bottomColor){
        mDrawable.setColors(topColor,middleColor,bottomColor);
    }

    public void setTopColor(int color){
        mDrawable.setTopColor(color);
    }

    public void setMiddleColor(int color){
        mDrawable.setMiddleColor(color);
    }

    public void setBottomColor(int color){
        mDrawable.setBottomColor(color);
    }

    public int[] getColors(){
        return mDrawable.getColors();
    }

    public int getTopColor(){
        return mDrawable.getTopColor();
    }

    public int getMiddleColor(){
        return mDrawable.getMiddleColor();
    }

    public int getBottomColor(){
        return mDrawable.getBottomColor();
    }

    public void setBorderColor(int color){
        mDrawable.setBorderColor(color);
    }

    public int getBorderColor() {
        return mDrawable.getBorderColor();
    }

    public void setBounds(Rect bounds){
        mDrawable.setBounds(bounds);
    }

    public void setBounds(int left, int top, int right, int bottom){
        mDrawable.setBounds(left,top,right,bottom);
    }

    public  Rect getBounds(){
        return mDrawable.getBounds();
    }
    */
}
