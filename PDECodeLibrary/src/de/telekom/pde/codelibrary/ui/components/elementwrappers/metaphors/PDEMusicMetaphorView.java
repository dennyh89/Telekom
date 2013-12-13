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
import de.telekom.pde.codelibrary.ui.elements.metaphor.PDEDrawableMusicMetaphor;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDEMusicMetaphorView
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Wrapper class hosting a PDEDrawableMusicMetaphorHaptic for usage in Layouts
 */
public class PDEMusicMetaphorView extends View {
    private PDEDrawableMusicMetaphor mMusic;


    /**
     * @brief Constructor.
     */
    public PDEMusicMetaphorView(Context context){
        super(context);
        init(null);
    }


    /**
     * @brief Constructor.
     */
    public PDEMusicMetaphorView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(attrs);
    }


    /**
     * @brief Constructor.
     */
    public PDEMusicMetaphorView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init(attrs);
    }


    /**
     * @brief Initialize.
     *
     *
     */
    protected void init(AttributeSet attrs){
        mMusic = new PDEDrawableMusicMetaphor(null);
        mMusic.setElementMiddleAligned(true);

        PDEUtils.setViewBackgroundDrawable(this, mMusic);

        setAttributes(attrs);
    }

    /**
     * @brief Load XML attributes.
     *
     *
     */
    private void setAttributes(AttributeSet attrs) {
        // valid?
        if(attrs == null) return;

        TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEMusicMetaphorView);

        //check icon source or string
        if (sa.hasValue(R.styleable.PDEMusicMetaphorView_src)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEMusicMetaphorView_src,0);
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
        if (sa.hasValue(R.styleable.PDEMusicMetaphorView_contentStyle)) {
            setContentStyle(sa.getInteger(R.styleable.PDEMusicMetaphorView_contentStyle, 0));
        }

        //set picture string
        if (sa.hasValue(R.styleable.PDEMusicMetaphorView_pictureString)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEMusicMetaphorView_pictureString,0);
            if(resourceID==0){
                setPictureString(sa.getString(R.styleable.PDEMusicMetaphorView_pictureString));
            } else {
                setPictureDrawable(getContext().getResources().getDrawable(resourceID));
            }
        }

        // set shadow enabled
        if (sa.hasValue(R.styleable.PDEMusicMetaphorView_shadowEnabled)) {
            setShadowEnabled(sa.getBoolean(R.styleable.PDEMusicMetaphorView_shadowEnabled, false));
        }
    }

    /**
     * @brief Set visual style.
     */
    public void setContentStyle(PDEConstants.PDEContentStyle style) {
        mMusic.setElementContentStyle(style);
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
        mMusic.setElementContentStyle(contentStyle);
    }

    /**
     * @brief Get visual style.
     */
    public PDEConstants.PDEContentStyle getContentStyle() {
        return mMusic.getElementContentStyle();
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
        mMusic.setElementPicture(drawable);
        requestLayout();
        invalidate();
    }

    /**
     * @brief Set picture drawable.
     */
    public void setPictureString(String path) {
        mMusic.setElementPicture(Drawable.createFromPath(path));
        requestLayout();
        invalidate();
    }


    /**
     * @brief Get icon drawable.
     */
    @SuppressWarnings("unused")
    public Drawable getPictureDrawable() {
        return mMusic.getElementPicture();
    }



    /**
     * @brief Returns the native size of the icon (e.g. from resource image)
     */
    public Point getNativeSize() {
        return mMusic.getNativeSize();
    }


    /**
     * @brief Returns if element has an icon drawable or icon string set.
     */
    public boolean hasPicture() {
        return mMusic.hasPicture();
    }


    /**
     * @brief Gets element width.
     */
    public int getElementWidth() {
        if (mMusic != null) return mMusic.getElementWidth();
        return 0;
    }


    /**
     * @brief Gets element height.
     */
    public int getElementHeight() {
        if (mMusic != null) return mMusic.getElementHeight();
        return 0;
    }

    /**
     * @brief Activate shadow.
     */
    public void setShadowEnabled(boolean enabled) {
        mMusic.setElementShadowEnabled(enabled);
    }

    /**
     * @brief Get if shadow is activated.
     */
    @SuppressWarnings("unused")
    public boolean getShadowEnabled() {
        return mMusic.getElementShadowEnabled();
    }


    /**
     * @brief Set View Size.
     */
    public void setViewSize(float width, float height){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.width = Math.round(width);
        layerParams.height = Math.round(height);
        setLayoutParams(layerParams);
    }


    /**
     * @brief Set View Offset.
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
     *
     */
    public void setViewLayoutRect(Rect rect) {
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.x = rect.left;
        layerParams.y = rect.top;
        layerParams.width = rect.width();
        layerParams.height = rect.height();

        setLayoutParams(layerParams);
    }

    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        // take height/width from the parameter ...
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        int newwidth = PDEBuildingUnits.roundUpToScreenCoordinates(getElementWidth());

        if (newwidth < width) {
            width = newwidth;
        }

        if (widthSpecMode == MeasureSpec.UNSPECIFIED && width == 0) {
            width = newwidth;
        }

        int newheight = PDEBuildingUnits.roundUpToScreenCoordinates(getElementHeight());

        if (newheight < height) {
            height = newheight;
        }

        if (heightSpecMode == MeasureSpec.UNSPECIFIED && height == 0) {
            height = newheight;
        }

        mMusic.setInternalBounds(width, height);

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height,heightMeasureSpec));
    }
}

