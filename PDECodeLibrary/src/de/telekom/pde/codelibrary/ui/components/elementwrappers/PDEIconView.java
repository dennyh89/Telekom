/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.elementwrappers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.icon.PDEDrawableIcon;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;

//----------------------------------------------------------------------------------------------------------------------
//  PDEIconView
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Wrapper class hosting a PDEDrawableIcon for usage in Layouts
 */
@SuppressWarnings("unused")
public class PDEIconView extends View {

    private PDEDrawableIcon mIcon;


    /**
     * @brief Constructor.
     */
    public PDEIconView(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    public PDEIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    public PDEIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Initialize.
     */
    protected void init(Context context, AttributeSet attrs) {
        mIcon = new PDEDrawableIcon("");

        PDEUtils.setViewBackgroundDrawable(this, mIcon);

        setAttributes(context, attrs);
    }


    /**
     * @brief Load XML attributes
     */
    private void setAttributes(Context context, AttributeSet attrs) {
        // valid?
        if (attrs == null) return;

        TypedArray sa = context.obtainStyledAttributes(attrs, R.styleable.PDEIconView);

        //check icon source or string
        if (sa != null && sa.hasValue(R.styleable.PDEIconView_src)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEIconView_src, 0);
            if (resourceID == 0) {
                setIconString(sa.getString(R.styleable.PDEIconView_src));
            } else {
                if (getResources() != null) {
                    setIconDrawable(getResources().getDrawable(resourceID));
                }
            }
        } else {
            int res = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", -1);
            if (res != -1 && getResources() != null) {
                setIconDrawable(getResources().getDrawable(res));
            }
        }

        if (sa != null) {

            //set icon string
            if (sa.hasValue(R.styleable.PDEIconView_iconString)) {
                //check if this is a resource value
                int resourceID = sa.getResourceId(R.styleable.PDEIconView_iconString, 0);
                if (resourceID == 0) {
                    setIconString(sa.getString(R.styleable.PDEIconView_iconString));
                } else {
                    setIconDrawable(getContext().getResources().getDrawable(resourceID));
                }
            }

            // set icon color
            if (sa.hasValue(R.styleable.PDEIconView_iconColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEIconView_iconColor, 0);
                if (resourceID != 0) {
                    setIconColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setIconColor(sa.getColor(R.styleable.PDEIconView_iconColor, R.color.DTBlack));
                }
            }

            // set shadow color
            if (sa.hasValue(R.styleable.PDEIconView_shadowColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEIconView_shadowColor, 0);
                if (resourceID != 0) {
                    setShadowColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setShadowColor(sa.getColor(R.styleable.PDEIconView_shadowColor, R.color.DTWhite));
                }
            }

            // set shadow enabled
            if (sa.hasValue(R.styleable.PDEIconView_shadowEnabled)) {
                setShadowEnabled(sa.getBoolean(R.styleable.PDEIconView_shadowEnabled, false));
            }

            // set shadow offset x
            if (sa.hasValue(R.styleable.PDEIconView_shadowOffsetX)) {
                setShadowOffsetX(sa.getFloat(R.styleable.PDEIconView_shadowOffsetX, 0.0f));
            }

            // set shadow offset y
            if (sa.hasValue(R.styleable.PDEIconView_shadowOffsetY)) {
                setShadowOffsetY(sa.getFloat(R.styleable.PDEIconView_shadowOffsetY, 1.0f));
            }

            // set padding
            if (sa.hasValue(R.styleable.PDEIconView_padding)) {
                setPaddingAll(sa.getString(R.styleable.PDEIconView_padding));
            } else {
                setPaddingAll(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "paddingLeft"));
            }

            sa.recycle();
        }
    }


    /**
     * @brief Set icon String.
     */
    public void setIconString(String iconString) {
        mIcon.setElementIconString(iconString);
        requestLayout();
    }


    /**
     * @brief Get icon String.
     */
    public String getIconString() {
        return mIcon.getElementIconString();
    }


    /**
     * @brief Set icon from int id.
     */
    public void setIconFromID(int id) {
        setIconDrawable(getContext().getResources().getDrawable(id));
        requestLayout();
    }


    /**
     * @brief Set icon drawable.
     */
    public void setIconDrawable(Drawable drawable) {
        mIcon.setElementIconDrawable(drawable);
        requestLayout();
    }


    /**
     * @brief Get icon drawable.
     */
    public Drawable getIconDrawable() {
        return mIcon.getElementIconDrawable();
    }


    /**
     * @brief Set Icon.
     */
    public void setIcon(Object icon) {
        mIcon.setElementIcon(icon);
        requestLayout();
    }


    /**
     * @brief Returns icon image if set, else icon string, else null.
     */
    public Object getElementIcon() {
        return mIcon.getElementIcon();
    }


    /**
     * @brief Set icon color.
     */
    public void setIconColor(int color) {
        mIcon.setElementIconColor(PDEColor.valueOf(color));
        requestLayout();
    }


    /**
     * @brief Set icon color.
     */
    public void setIconColor(PDEColor color) {
        mIcon.setElementIconColor(color);
        requestLayout();
    }


    /**
     * @brief Get icon color.
     */
    public PDEColor getIconColor() {
        return mIcon.getElementIconColor();
    }


    /**
     * @brief Private helper to set same padding on all sides with dimension string (could be 0.7BU etc...)
     */
    private void setPaddingAll(String paddingAll) {
        if (!TextUtils.isEmpty(paddingAll)) {
            setPaddingAll((int) PDEBuildingUnits.parseSize(paddingAll));
        }
    }


    /**
     * @brief Set padding.
     */
    public void setPaddingAll(int padding) {
        mIcon.setElementPadding(padding);
        requestLayout();
    }


    /**
     * @brief Get padding.
     */
    public int getPadding() {
        return Math.round(mIcon.getElementPadding());
    }


    /**
     * @brief Set shadow color.
     */
    public void setShadowColor(int color) {
        mIcon.setElementShadowColor(PDEColor.valueOf(color));
        requestLayout();
    }


    /**
     * @brief Set shadow color.
     */
    public void setShadowColor(PDEColor color) {
        mIcon.setElementShadowColor(color);
        requestLayout();
    }


    /**
     * @brief Get shadow color.
     */
    public PDEColor getShadowColor() {
        return mIcon.getElementShadowColor();
    }


    /**
     * @brief Set shadow x offset.
     */
    public void setShadowOffsetX(float offset) {
        mIcon.setElementShadowXOffset(offset);
        requestLayout();
    }


    /**
     * @brief Get shadow x offset.
     */
    public float getShadowOffsetX() {
        return mIcon.getElementShadowXOffset();
    }


    /**
     * @brief Set shadow y offset.
     */
    public void setShadowOffsetY(float offset) {
        mIcon.setElementShadowYOffset(offset);
        requestLayout();
    }


    /**
     * @brief Get shadow y offset.
     */
    public float getShadowOffsetY() {
        return mIcon.getElementShadowYOffset();
    }


    /**
     * @brief Enable shadow.
     */
    public void setShadowEnabled(boolean enabled) {
        mIcon.setElementShadowEnabled(enabled);
    }


    /**
     * @brief Get if shadow is enabled.
     */
    public boolean getShadowEnabled() {
        return mIcon.getElementShadowEnabled();
    }


    /**
     * @brief Returns if the icon has a native size (e.g. from resource image).
     */
    public boolean hasNativeSize() {
        return mIcon.hasNativeSize();
    }


    /**
     * @brief Returns the native size of the icon (e.g. from resource image).
     */
    public Point getNativeSize() {
        return mIcon.getNativeSize();
    }


    /**
     * @brief Returns if element has an icon drawable or icon string set.
     */
    public boolean hasElementIcon() {
        return mIcon.hasElementIcon();
    }


    /**
     * @brief Gets element width.
     */
    public int getElementWidth() {
        if (mIcon != null) {
            return mIcon.getElementWidth();
        }
        return 0;
    }


    /**
     * @brief Gets element height.
     */
    public int getElementHeight() {
        if (mIcon != null) {
            return mIcon.getElementHeight();
        }
        return 0;
    }


    /**
     * @brief Checks if icon drawable is PDEIconFont.
     */
    public boolean isIconfont() {
        return mIcon.isIconfont();
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

        int newWidth = PDEBuildingUnits.roundUpToScreenCoordinates(getElementWidth());

        if (newWidth < width) {
            width = newWidth;
        }

        if (widthSpecMode == MeasureSpec.UNSPECIFIED && width == 0) {
            width = newWidth;
        }

        int newHeight = PDEBuildingUnits.roundUpToScreenCoordinates(getElementHeight());

        if (newHeight < height) {
            height = newHeight;
        }

        if (heightSpecMode == MeasureSpec.UNSPECIFIED && height == 0) {
            height = newHeight;
        }

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                             resolveSize(height, heightMeasureSpec));
    }
}
