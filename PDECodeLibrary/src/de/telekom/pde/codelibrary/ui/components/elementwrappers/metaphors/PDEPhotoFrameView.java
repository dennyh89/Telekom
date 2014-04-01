/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.elementwrappers.metaphors;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.elements.metaphor.PDEDrawablePhotoFrame;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;


//----------------------------------------------------------------------------------------------------------------------
//  PDEPhotoFrameView
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Wrapper class hosting a PDEDrawablePhotoFrame for usage in Layouts
 */
public class PDEPhotoFrameView extends View {

    // metaphor drawable
    private PDEDrawablePhotoFrame mPhoto;

    // rect helper variable to avoid allocation during layout/measure
    private Rect mInternalCalculateAspectRatioBounds;


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
     */
    protected void init(AttributeSet attrs){
        mPhoto = new PDEDrawablePhotoFrame(null);
        mPhoto.setElementMiddleAligned(true);

        mInternalCalculateAspectRatioBounds = new Rect(0,0,0,0);

        PDEUtils.setViewBackgroundDrawable(this, mPhoto);

        setAttributes(attrs);
    }

    /**
     * @brief Load XML attributes.
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

        // set content style
        if (sa.hasValue(R.styleable.PDEPhotoFrameView_contentStyle)) {
            setContentStyle(sa.getInteger(R.styleable.PDEPhotoFrameView_contentStyle, 0));
        }

        //set picture string
        if (sa.hasValue(R.styleable.PDEPhotoFrameView_pictureString)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEPhotoFrameView_pictureString,0);
            if(resourceID==0){
                setPictureString(sa.getString(R.styleable.PDEPhotoFrameView_pictureString));
            } else {
                setPictureDrawable(getContext().getResources().getDrawable(resourceID));
            }
        }

        // set shadow enabled
        if (sa.hasValue(R.styleable.PDEPhotoFrameView_shadowEnabled)) {
            setShadowEnabled(sa.getBoolean(R.styleable.PDEPhotoFrameView_shadowEnabled, false));
        }
    }

    /**
     * @brief Set visual style.
     */
    public void setContentStyle(PDEConstants.PDEContentStyle style) {
        mPhoto.setElementContentStyle(style);
    }

    /**
     * @brief Set visual style.
     */
    public void setContentStyle(int style) {
        PDEConstants.PDEContentStyle contentStyle;
        try {
            contentStyle = PDEConstants.PDEContentStyle.values()[style];
        } catch (Exception e) {
            contentStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        }
        mPhoto.setElementContentStyle(contentStyle);
    }

    /**
     * @brief Get visual style.
     */
    public PDEConstants.PDEContentStyle getContentStyle() {
        return mPhoto.getElementContentStyle();
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
    @SuppressWarnings("unused")
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
        if (mPhoto != null) return mPhoto.getElementWidth();
        return 0;
    }


    /**
     * @brief Gets element height.
     */
    public int getElementHeight() {
        if (mPhoto != null) return mPhoto.getElementHeight();
        return 0;
    }


    /**
     * @brief Activate shadow.
     */
    public void setShadowEnabled(boolean enabled) {
        mPhoto.setElementShadowEnabled(enabled);
    }


    /**
     * @brief Get if shadow is activated.
     */
    @SuppressWarnings("unused")
    public boolean getShadowEnabled() {
        return mPhoto.getElementShadowEnabled();
    }


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height, newHeight;
        int width, newWidth;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        // take height/width from the parameter ...
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        newWidth = PDEBuildingUnits.roundUpToScreenCoordinates(getElementWidth());

        if (newWidth < width) {
            width = newWidth;
        }

        if (widthSpecMode == MeasureSpec.UNSPECIFIED && width == 0) {
            width = newWidth;
        }

        newHeight = PDEBuildingUnits.roundUpToScreenCoordinates(getElementHeight());

        if (newHeight < height) {
            height = newHeight;
        }

        if (heightSpecMode == MeasureSpec.UNSPECIFIED && height == 0) {
            height = newHeight;
        }

        if (mPhoto != null) {
            mInternalCalculateAspectRatioBounds.set(0,0,width,height);
            mInternalCalculateAspectRatioBounds = mPhoto.elementCalculateAspectRatioBounds(mInternalCalculateAspectRatioBounds);
            width = mInternalCalculateAspectRatioBounds.width();
            height = mInternalCalculateAspectRatioBounds.height();
        }

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height,heightMeasureSpec));
    }
}

