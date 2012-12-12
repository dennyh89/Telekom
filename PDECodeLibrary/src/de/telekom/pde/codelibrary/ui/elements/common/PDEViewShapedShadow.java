/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class PDEViewShapedShadow extends View {
    protected PDEDrawableShapedShadow mDrawable;
    private final static String LOG_TAG = PDEViewShapedShadow.class.getName();

    public PDEViewShapedShadow(Context context){
        super(context);
        init();
    }

    public PDEViewShapedShadow(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    public PDEViewShapedShadow(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    protected void init(){
        setDrawable(new PDEDrawableShapedShadow());
    }

    public PDEDrawableShapedShadow getDrawable(){
        return mDrawable;
    }

    public void setDrawable(PDEDrawableShapedShadow drawable){
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
                Log.e(LOG_TAG, "could not draw shadow");
            }
        }
    }

    public void setShapeOpacity (float opacity){
        mDrawable.setShapeOpacity(opacity);
    }

    public float getShapeOpacity(){
        return mDrawable.getShapeOpacity();
    }


    public void setShapeColor (PDEColor color){
        mDrawable.setShapeColor(color);
    }

    public PDEColor getShapeColor(){
        return mDrawable.getShapeColor();
    }

    public void setShapeOffset (PointF offset){
        PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        if(lp!= null){
            lp.x = (int)offset.x;
            lp.y = (int)offset.y;
            setLayoutParams(lp);
        }
        // ToDo: invalidate???
    }

    public PointF getShapeOffset(){
        PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        return new PointF(lp.x,lp.y);
    }


    public void setBlurRadius (float radius){
        mDrawable.setBlurRadius(radius);
    }

    public float getBlurRadius(){
        return mDrawable.getBlurRadius();
    }

    public void setShapePath (Path path){
        mDrawable.setShapePath(path);
    }

    public Path getShapePath(){
        return mDrawable.getShapePath();
    }

    public void setShapeRect(RectF rect){
        mDrawable.setShapeRect(rect);
    }

    public void setShapeRoundedRect(RectF rect, float cornerRadius){
        mDrawable.setShapeRoundedRect(rect,cornerRadius);
    }

    public void setShapeOval(RectF rect){
        setShapeOval(rect);
    }

    public void setAlpha(int alpha) {
        mDrawable.setAlpha(alpha);
    }
}
