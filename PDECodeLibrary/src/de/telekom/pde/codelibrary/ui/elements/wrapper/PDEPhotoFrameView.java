/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.wrapper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.elements.metapher.PDEDrawablePhotoFrame;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//----------------------------------------------------------------------------------------------------------------------
//  PDEPhotoFrameView
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Wrapper class hosting a PDEDrawablePhotoFrame for usage in Layouts
 */
public class PDEPhotoFrameView extends View {
    private PDEDrawablePhotoFrame mPhoto;


    /**
     * @brief Constructor.
     */
    public PDEPhotoFrameView(Context context){
        super(context);
        init(null);
    }


    /**
     * @brief Constructor.
     */
    public PDEPhotoFrameView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(attrs);
    }


    /**
     * @brief Constructor.
     */
    public PDEPhotoFrameView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init(attrs);
    }


    /**
     * @brief Initialize.
     *
     * @param attrs
     */
    protected void init(AttributeSet attrs){
        mPhoto = new PDEDrawablePhotoFrame(null);
        mPhoto.setElementMiddleAligned(true);

        boolean clippingDrawableSet = false;
        Method method;

        // setBackgroundDrawable is marked as deprecated in api level 16, in order to avoid the warning use reflection.
        try {
            //try to use the setBackground function which was introduced in android 4.1 (api level 16)
            method = getClass().getMethod("setBackground", new Class[] {Drawable.class});
            method.invoke(this,mPhoto);
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
                method.invoke(this,mPhoto);
                clippingDrawableSet = true;
            } catch (NoSuchMethodException e) {
                // function not available
            } catch (IllegalAccessException e) {
                // function not available
            } catch (InvocationTargetException e) {
                // function not available
            }
        }

        setAttributes(attrs);
    }

    /**
     * @brief Load XML attributes.
     *
     * @param attrs
     */
    private void setAttributes(AttributeSet attrs) {
        // valid?
        if(attrs == null) return;

        TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEPhotoFrameView);

        //check icon source or string
        if (sa.hasValue(R.styleable.PDEPhotoFrameView_src)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEPhotoFrameView_src,0);
            if(resourceID != 0){
                setPictureDrawable(getContext().getResources().getDrawable(resourceID));
            }
        }
        else {
            int res = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android","src",-1);
            if (res != -1) {
                setPictureDrawable(getContext().getResources().getDrawable(res));
            }
        }

        //set picture string
        if (sa.hasValue(R.styleable.PDEPhotoFrameView_pictureString)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEPhotoFrameView_pictureString,0);
            if(resourceID==0){
                setPictureString(sa.getString(R.styleable.PDEIconView_iconString));
            } else {
                setPictureDrawable(getContext().getResources().getDrawable(resourceID));
            }
        }
    }



    /**
     * @brief Set photo from int id.
     */
    public void setPhotoFromID(int id) {
        setPictureDrawable(getContext().getResources().getDrawable(id));
    }


    /**
     * @brief Set photo drawable.
     */
    public void setPictureDrawable(Drawable drawable) {
        mPhoto.setElementPicture(drawable);
        requestLayout();
        invalidate();
    }

    /**
     * @brief Set photo drawable.
     */
    public void setPictureString(String path) {
        mPhoto.setElementPicture(Drawable.createFromPath(path));
        requestLayout();
        invalidate();
    }


    /**
     * @brief Get icon drawable.
     */
    public Drawable getPictureDrawable() {
        return mPhoto.getElementPicture();
    }



    /**
     * @brief Returns the native size of the icon (e.g. from resource image)
     */
    public Point getNativeSize() {
        return mPhoto.getNativeSize();
    }


    /**
     * @brief Returns if element has an icon drawable or icon string set.
     */
    public boolean hasPicture() {
        return mPhoto.hasPicture();
    }


    /**
     * @brief Gets element width.
     */
    public int getElementWidth() {
        if (mPhoto != null) mPhoto.getElementWidth();
        return 0;
    }


    /**
     * @brief Gets element height.
     */
    public int getElementHeight() {
        if (mPhoto != null) mPhoto.getElementHeight();
        return 0;
    }


    /**
     * @brief Set View Size.
     *
     * @param width
     * @param height
     */
    public void setViewSize(float width, float height){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.width = Math.round(width);
        layerParams.height = Math.round(height);
        setLayoutParams(layerParams);
    }


    /**
     * @brief Set View Offset.
     *
     * @param x
     * @param y
     */
    public void setViewOffset(float x, float y){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.x = Math.round(x);
        layerParams.y = Math.round(y);
        setLayoutParams(layerParams);
    }


    /**
     * @brief Set View Rect.
     *
     * @param rect
     */
    public void setViewLayoutRect(Rect rect) {
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.x = rect.left;
        layerParams.y = rect.top;
        layerParams.width = rect.width();
        layerParams.height = rect.height();

        setLayoutParams(layerParams);
    }
}

