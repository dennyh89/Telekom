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
import de.telekom.pde.codelibrary.ui.elements.metaphor.PDEDrawableVideoMetaphor;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;


//----------------------------------------------------------------------------------------------------------------------
//  PDEVideoMetaphorView
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Wrapper class hosting a PDEDrawableVideoMetaphorHaptic for usage in Layouts
 */
public class PDEVideoMetaphorView extends View {

    // metaphor drawable
    private PDEDrawableVideoMetaphor mVideo;

    // rect helper variable to avoid allocation during layout/measure
    private Rect mInternalCalculateAspectRatioBounds;

    /**
     * @brief Constructor.
     */
    public PDEVideoMetaphorView(Context context){
        super(context);
        init(null);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEVideoMetaphorView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEVideoMetaphorView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init(attrs);
    }


    /**
     * @brief Initialize.
     */
    protected void init(AttributeSet attrs){
        mVideo = new PDEDrawableVideoMetaphor(null, "");
        mVideo.setElementMiddleAligned(true);

        mInternalCalculateAspectRatioBounds = new Rect(0,0,0,0);

        PDEUtils.setViewBackgroundDrawable(this, mVideo);

        setAttributes(attrs);
    }


    /**
     * @brief Load XML attributes.
     */
    private void setAttributes(AttributeSet attrs) {
        // valid?
        if (attrs == null) return;

        TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEVideoMetaphorView);

        //check icon source or string
        if (sa.hasValue(R.styleable.PDEVideoMetaphorView_src)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEVideoMetaphorView_src, 0);
            if (resourceID != 0){
                setPictureDrawable(getContext().getResources().getDrawable(resourceID));
            }
        } else {
            int res = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android",
                    "src",
                    -1);
            if (res != -1) {
                setPictureDrawable(getContext().getResources().getDrawable(res));
            }
        }

        //set picture string
        if (sa.hasValue(R.styleable.PDEVideoMetaphorView_pictureString)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEVideoMetaphorView_pictureString, 0);
            if (resourceID == 0){
                setPictureString(sa.getString(R.styleable.PDEVideoMetaphorView_pictureString));
            } else {
                setPictureDrawable(getContext().getResources().getDrawable(resourceID));
            }
        }

        // set content style
        if (sa.hasValue(R.styleable.PDEVideoMetaphorView_contentStyle)) {
            setContentStyle(sa.getInteger(R.styleable.PDEVideoMetaphorView_contentStyle, 0));
        }

        //set time string
        // set text
        if (sa.hasValue(R.styleable.PDEVideoMetaphorView_timeString)) {
            setTimeString(sa.getString(R.styleable.PDEVideoMetaphorView_timeString));
        }

        //set if 16/9 format
        // set shadow enabled
        if (sa.hasValue(R.styleable.PDEVideoMetaphorView_format169)) {
            set169Format(sa.getBoolean(R.styleable.PDEVideoMetaphorView_format169, true));
        }

        // set shadow enabled
        if (sa.hasValue(R.styleable.PDEVideoMetaphorView_shadowEnabled)) {
            setShadowEnabled(sa.getBoolean(R.styleable.PDEVideoMetaphorView_shadowEnabled, false));
        }
    }


    /**
     * @brief Set visual style.
     */
    public void setContentStyle(PDEConstants.PDEContentStyle style) {
        mVideo.setElementContentStyle(style);
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
        mVideo.setElementContentStyle(contentStyle);
    }


    /**
     * @brief Get visual style.
     */
    public PDEConstants.PDEContentStyle getStyle() {
        return mVideo.getElementContentStyle();
    }


    /**
     * @brief Set picture from int id.
     */
    @SuppressWarnings("unused")
    public void setPhotoFromID(int id) {
        setPictureDrawable(getContext().getResources().getDrawable(id));
    }


    /**
     * @brief Set picture drawable.
     */
    public void setPictureDrawable(Drawable drawable) {
        mVideo.setElementScene(drawable);
        requestLayout();
        invalidate();
    }


    /**
     * @brief Set picture drawable.
     */
    public void setPictureString(String path) {
        mVideo.setElementScene(Drawable.createFromPath(path));
        requestLayout();
        invalidate();
    }


    /**
     * @brief Set time string.
     */
    public void setTimeString(String timeString) {
        mVideo.setElementTimeString(timeString);
        requestLayout();
        invalidate();
    }


    /**
     * @brief Set if 16/9 format.
     */
    public void set169Format(boolean f169) {
        mVideo.setElementFormat169(f169);
        requestLayout();
        invalidate();
    }


    /**
     * @brief Get icon drawable.
     */
    @SuppressWarnings("unused")
    public Drawable getPictureDrawable() {
        return mVideo.getElementScene();
    }


    /**
     * @brief Activate shadow.
     */
    public void setShadowEnabled(boolean enabled) {
        mVideo.setElementShadowEnabled(enabled);
    }


    /**
     * @brief Get if shadow is activated.
     */
    @SuppressWarnings("unused")
    public boolean getShadowEnabled() {
        return mVideo.getElementShadowEnabled();
    }


    /**
     * @brief Returns the native size of the icon (e.g. from resource image)
     */
    public Point getNativeSize() {
        return mVideo.getNativeSize();
    }


    /**
     * @brief Returns if element has an icon drawable or icon string set.
     */
    public boolean hasPicture() {
        return mVideo.hasPicture();
    }


    /**
     * @brief Gets element width.
     */
    public int getElementWidth() {
        if (mVideo != null) return mVideo.getElementWidth();
        return 0;
    }


    /**
     * @brief Gets element height.
     */
    public int getElementHeight() {
        if (mVideo != null) return mVideo.getElementHeight();
        return 0;
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

        if (mVideo != null) {
            mInternalCalculateAspectRatioBounds.set(0,0,width,height);
            mInternalCalculateAspectRatioBounds = mVideo.elementCalculateAspectRatioBounds(mInternalCalculateAspectRatioBounds);
            width = mInternalCalculateAspectRatioBounds.width();
            height = mInternalCalculateAspectRatioBounds.height();
        }

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height,heightMeasureSpec));
    }
}

