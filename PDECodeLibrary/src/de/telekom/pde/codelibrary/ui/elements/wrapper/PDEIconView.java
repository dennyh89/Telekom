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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.icon.PDEDrawableIcon;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//----------------------------------------------------------------------------------------------------------------------
//  PDEIconView
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Wrapper class hosting a PDEDrawableIcon for usage in Layouts
 */
public class PDEIconView extends View {

   private PDEDrawableIcon mIcon;

    /**
     * @brief Constructor.
     */
    public PDEIconView(Context context){
        super(context);
        init(null);
    }


    /**
     * @brief Constructor.
     */
    public PDEIconView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(attrs);
    }


    /**
     * @brief Constructor.
     */
    public PDEIconView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init(attrs);
    }


    /**
     * @brief Initialize.
     *
     * @param attrs
     */
    protected void init(AttributeSet attrs){
        mIcon = new PDEDrawableIcon("");

        boolean clippingDrawableSet = false;
        Method method;

        // setBackgroundDrawable is marked as deprecated in api level 16, in order to avoid the warning use reflection.
        try {
            //try to use the setBackground function which was introduced in android 4.1 (api level 16)
            method = getClass().getMethod("setBackground", new Class[] {Drawable.class});
            method.invoke(this,mIcon);
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
                method.invoke(this,mIcon);
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
     * @brief Load XML attributs
     *
     * @param attrs
     */
    private void setAttributes(AttributeSet attrs) {
        // valid?
        if(attrs==null) return;

        TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEIconView);

        //check icon source or string
        if (sa.hasValue(R.styleable.PDEIconView_src)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEIconView_src,0);
            if(resourceID==0){
                setIconString(sa.getString(R.styleable.PDEIconView_src));
            } else {
                setIconDrawable(getContext().getResources().getDrawable(resourceID));
            }
        }
        else {
            int res = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android","src",-1);
            if (res != -1) {
                setIconDrawable(getContext().getResources().getDrawable(res));
            }
        }

        //set icon string
        if (sa.hasValue(R.styleable.PDEIconView_iconString)) {
            //check if this is a resource value
            int resourceID = sa.getResourceId(R.styleable.PDEIconView_iconString,0);
            if(resourceID==0){
                setIconString(sa.getString(R.styleable.PDEIconView_iconString));
            } else {
                setIconDrawable(getContext().getResources().getDrawable(resourceID));
            }
        }

        // set icon color
        if (sa.hasValue(R.styleable.PDEIconView_iconColor)) {
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","iconColor");
            if (text != null && text.startsWith("@")) {
                symbolicColor = Integer.valueOf(text.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setIconColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setIconColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setIconColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setIconColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                } else {
                    setIconColor(sa.getColor(R.styleable.PDEIconView_iconColor, R.color.DTBlack));
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setIconColor(sa.getColor(R.styleable.PDEIconView_iconColor, R.color.DTBlack));
            }
        }

        // set shadow color
        if (sa.hasValue(R.styleable.PDEIconView_shadowColor)) {
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","shadowColor");
            if (text != null && text.startsWith("@")) {
                symbolicColor = Integer.valueOf(text.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setShadowColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setShadowColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setShadowColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setShadowColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                } else {
                    setShadowColor(sa.getColor(R.styleable.PDEIconView_shadowColor, R.color.DTWhite));
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setShadowColor(sa.getColor(R.styleable.PDEIconView_shadowColor, R.color.DTWhite));
            }
        }

        // set shadow enabled
        if (sa.hasValue(R.styleable.PDEIconView_shadowEnabled)) {
            setShadowEnabled(sa.getBoolean(R.styleable.PDEIconView_shadowEnabled,false));
        }

        // set shadow offset x
        if (sa.hasValue(R.styleable.PDEIconView_shadowOffsetX)) {
            setShadowOffsetX(sa.getFloat(R.styleable.PDEIconView_shadowOffsetX,0.0f));
        }

        // set shadow offset y
        if (sa.hasValue(R.styleable.PDEIconView_shadowOffsetY)) {
            setShadowOffsetY(sa.getFloat(R.styleable.PDEIconView_shadowOffsetY,1.0f));
        }

        // set padding
        if (sa.hasValue(R.styleable.PDEIconView_padding)) {
            setPaddingAll(sa.getDimensionPixelSize(R.styleable.PDEIconView_padding,0));
        }  else {
            String dim = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","padding");
            if (!TextUtils.isEmpty(dim)) {
                setPaddingAll(parseDimension(dim));
            }
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
    public void setIcon(Object icon)
    {
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
        if (mIcon != null) mIcon.getElementWidth();
        return 0;
    }


    /**
     * @brief Gets element height.
     */
    public int getElementHeight() {
        if (mIcon != null) mIcon.getElementHeight();
        return 0;
    }


    /**
     * @brief Checks if icon drawable is PDEIconfont.
     */
    public boolean isIconfont() {
        return mIcon.isIconfont();
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

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height,heightMeasureSpec));
    }



    /**
     * Takes dimension string from resources and transforms it in pixel value.
     */
    public int parseDimension(String dimensionString  )
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float size = Float.NaN;
        int endOfFloatIndex = -1;

        Pattern p = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(dimensionString);

        if (m.find()) {
            if (m.start() == 0) {
                // float only at the beginning
                size = Float.valueOf(dimensionString.substring(m.start(),
                        m.end()));
                endOfFloatIndex = m.end();
            }
        }

        if (!Float.isNaN(size) && endOfFloatIndex > -1 &&
                endOfFloatIndex < dimensionString.length()) {
            String unitPart = dimensionString.substring(endOfFloatIndex);
            if (unitPart.compareToIgnoreCase("BU") == 0) {
                size = PDEBuildingUnits.exactPixelFromBU(size);
            } else if (unitPart.compareToIgnoreCase("px") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, metrics);
            } else if (unitPart.compareToIgnoreCase("dp") == 0 ||
                    unitPart.compareToIgnoreCase("dip") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, metrics);
            } else if (unitPart.compareToIgnoreCase("sp") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics);
            } else if (unitPart.compareToIgnoreCase("dt") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, size, metrics);
            } else if (unitPart.compareToIgnoreCase("in") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, size, metrics);
            } else if (unitPart.compareToIgnoreCase("mm") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, size, metrics);
            }
        }
        return Math.round(size);
    }
}
