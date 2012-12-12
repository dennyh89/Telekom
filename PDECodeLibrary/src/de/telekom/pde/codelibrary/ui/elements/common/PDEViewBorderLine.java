/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */


package de.telekom.pde.codelibrary.ui.elements.common;

import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class PDEViewBorderLine extends View {
    public PDEDrawableBorderLine mDrawable;
    private final static String LOG_TAG = PDEViewBorderLine.class.getName();

    public PDEViewBorderLine(Context context){
        super(context);
        init();
    }

    public PDEViewBorderLine(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    public PDEViewBorderLine(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    protected void init(){
        setDrawable(new PDEDrawableBorderLine());
    }

    public PDEDrawableBorderLine getDrawable(){
        return mDrawable;
    }

    public void setDrawable(PDEDrawableBorderLine drawable){
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
                Log.e(LOG_TAG, "could not draw border line");
            }
        }
    }



    public void setLineOffset (PointF offset){
        PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        if(lp!= null){
            lp.x = (int)offset.x;
            lp.y = (int)offset.y;
            setLayoutParams(lp);
        }
    }

    public PointF getLineOffset(){
        PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        return new PointF(lp.x,lp.y);
    }


    public void setBorderColor (int color){
        mDrawable.setBorderColor(color);
    }

    public int getBorderColor(){
        return mDrawable.getBorderColor();
    }

    public void setBorderWidth (float width){
        mDrawable.setBorderWidth(width);
    }

    public float getBorderWidth(){
        return mDrawable.getBorderWidth();
    }

    public void setCornerRadius(float radius){
        mDrawable.setCornerRadius(radius);
    }

    public float getCornerRadius(){
        return mDrawable.getCornerRadius();
    }

    public void setBoundingRect(Rect bounds){
        mDrawable.setBoundingRect(bounds);
    }

    public Rect getBoundingRect(){
        return mDrawable.getBoundingRect();
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
        mDrawable.setShapeOval(rect);
    }

    public void setAlpha(int alpha) {
        mDrawable.setAlpha(alpha);
    }
}

