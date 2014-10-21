/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.icon;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEIconView;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableIconImage
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Icon Class.
 *
 * Icon can be set by a drawable of by a string. The string can be the path to the resource, resource id or
 * a symbol from the IconFont, by using a string consisting of '#' and the corresponding letter.
 */
public class PDEDrawableIcon extends PDEDrawableMultilayer {
    private final static String LOG_TAG = PDEDrawableIcon.class.getName();

    //-----  properties ---------------------------------------------------------------------------------------------------
    private PDEColor mIconColor;
    private PDEColor mShadowColor;
    private boolean mShadowEnabled;
    private float mShadowXOffset;
    private float mShadowYOffset;
    private float mPadding;
    private PDEDrawableIconFont mIconFont;
    private PDEDrawableIconImage mIconImage;
    private boolean mDoBoundsChange;
    //private Drawable mIconDrawable;

    protected PDEIconView mWrapperView;


    /**
     * @brief Constructor taking image.
     */
    public PDEDrawableIcon() {
        super();
        init();
    }


    /**
     * @brief Constructor taking image.
     *
     * @param drawable Image to be shown
     */
    public PDEDrawableIcon(Drawable drawable) {
        super();
        init();
        setElementIconDrawable(drawable);

    }


    /**
     * @brief Constructor taking string.
     */
    public PDEDrawableIcon(String iconstring) {
        super();
        init();
        setElementIconString(iconstring);
    }


    /**
     * @brief Initializes start values.
     */
    private void init() {
        mIconFont = null;
        mIconImage = null;

        mIconColor = null;
        mShadowColor = new PDEColor();
        mShadowColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());
        mShadowEnabled = false;
        mShadowXOffset = 0.0f;
        mShadowYOffset = 1.0f;
        mPadding = 0.0f;

        mWrapperView = null;

        mDoBoundsChange = true;
    }


    /**
     * @brief Returns whether this icon has a native size (e.g. from a resource image).
     */
    public boolean hasNativeSize() {
        if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            return (((PDEDrawableIconImage)getLayerAtIndex(0)).getElementImage() != null);
        }
        return false;
    }


    /**
     * @brief Returns the native size of the icon (e.g. from a resource image).
     */
    public Point getNativeSize() {

        if (getNumberOfLayers() == 0 || !hasNativeSize()) return new Point(0, 0);

        if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            return new Point(((PDEDrawableIconImage) getLayerAtIndex(0)).getElementImage().getIntrinsicWidth(),
                             ((PDEDrawableIconImage) getLayerAtIndex(0)).getElementImage().getIntrinsicHeight());
        }
        return new Point(0, 0);
    }


    /**
     * @brief Get height of the Element.
     */
    public int getElementHeight() {
        if (isIconfont()) {
            return mIconFont.getElementHeight();
        } else {
            return getNativeSize().y + 1;
        }
    }


    /**
     * @brief Get Width of the Element.
     */
    public int getElementWidth() {
        if (isIconfont()) {
            return mIconFont.getElementWidth();
        } else {
            return getNativeSize().x + 1;
        }
    }


    /**
     * @brief Returns whether element has an icon drawable or icon string set.
     */
    public boolean hasElementIcon() {
        if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
             return !(((PDEDrawableIconImage) getLayerAtIndex(0)).getElementImage() == null);
        }

        if (getLayerAtIndex(0) instanceof  PDEDrawableIconFont) {
            return !(TextUtils.isEmpty(((PDEDrawableIconFont) getLayerAtIndex(0)).getElementIconText()));
        }

        return false;
    }


    /**
     * @brief Checks if icon drawable is PDEIconFont.
     */
    public boolean isIconfont() {
        return (getLayerAtIndex(0) instanceof PDEDrawableIconFont);
    }


    /**
     * @brief Set icon image.
     */
    public void setElementIconDrawable(Drawable image) {
        if (image == null)
        {
            removeLayerAtIndex(0);
            return;
        }

        mIconImage = new PDEDrawableIconImage(image);
        if (mIconColor != null) mIconImage.setElementIconColor(mIconColor);
        mIconImage.setElementShadowEnabled(mShadowEnabled);
        mIconImage.setElementShadowColor(mShadowColor);
        mIconImage.setElementShadowXOffset(mShadowXOffset);
        mIconImage.setElementShadowYOffset(mShadowYOffset);
        mIconImage.setElementPadding(mPadding);
        addLayer(mIconImage);

        //update(true);
    }


    /**
     * @brief Get icon by drawable.
     */
    public Drawable getElementIconDrawable() {
        if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            return ((PDEDrawableIconImage) getLayerAtIndex(0)).getElementImage();
        }

        return null;
    }


    /**
     * @brief Set icon by string.
     */
    public void setElementIconString(String iconString) {
        Drawable imageFromString;

        if (TextUtils.isEmpty(iconString)) {
            removeLayerAtIndex(0);
            return;
        }

        if (iconString.charAt(0) == '#') {
            if (mIconFont == null) {
                mIconFont = new PDEDrawableIconFont(iconString.substring(1));
            } else {
                mIconFont.setElementIconText(iconString.substring(1));
            }

            if (mIconColor != null) mIconFont.setElementIconColor(mIconColor);
            mIconFont.setElementShadowEnabled(mShadowEnabled);
            mIconFont.setElementShadowColor(mShadowColor);
            mIconFont.setElementShadowXOffset(mShadowXOffset);
            mIconFont.setElementShadowYOffset(mShadowYOffset);
            mIconFont.setElementPadding(mPadding);
            addLayer(mIconFont);
            //update(true);
        } else {
            imageFromString = Drawable.createFromPath(iconString);

            if (imageFromString == null) {
                Log.e(LOG_TAG, "Image could not be loaded");
                removeLayerAtIndex(0);
            } else {
                setElementIconDrawable(imageFromString);
            }
        }
    }


    /**
     * @brief Get icon string.
     */
    public String getElementIconString() {
        if (getLayerAtIndex(0) instanceof PDEDrawableIconFont) {
            return ((PDEDrawableIconFont) getLayerAtIndex(0)).getElementIconText();
        }

        return null;
    }


    /**
     * @brief Set Icon by drawable or string.
     *
     * Based on type of object either setElementIconDrawable of set ElementIconString is called.
     * If icon is not a String or Drawable, the parameter is set to null.
     */
    public void setElementIcon(Object icon) {
        if (icon instanceof String) {
            setElementIconString((String) icon);
        } else if (icon instanceof Drawable) {
            setElementIconDrawable((Drawable) icon);
        } else {
            //unknown type of no icon
            setElementIconDrawable(null);
        }
    }


    /**
     * @brief Returns icon image if set, else icon string, else null.
     */
    public Object getElementIcon() {
        if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            return ((PDEDrawableIconImage) getLayerAtIndex(0)).getElementImage();
        } else if (getLayerAtIndex(0) instanceof PDEDrawableIconFont) {
            return ((PDEDrawableIconFont) getLayerAtIndex(0)).getElementIconText();
        } else {
            return null;
        }
    }


    /**
     * @brief Set Icon Color.
     */
    public void setElementIconColor(PDEColor color) {
        //any change?
        if (color.equals(mIconColor)) return;

        //remember
        mIconColor = color;


        if (getLayerAtIndex(0) instanceof PDEDrawableIconFont) {
            ((PDEDrawableIconFont)getLayerAtIndex(0)).setElementIconColor(color);
        } else if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            ((PDEDrawableIconImage)getLayerAtIndex(0)).setElementIconColor(color);
        }

        //redraw
        //update();
    }


    /**
     * @brief Get Icon Color.
     */
    public PDEColor getElementIconColor() {
        return mIconColor;
    }


    /**
     * @brief Set shadow enabled.
     */
    public void setElementShadowEnabled(boolean enabled) {
        //any change?
        if (enabled == mShadowEnabled) return;

        //remember
        mShadowEnabled = enabled;

        if (getLayerAtIndex(0) instanceof PDEDrawableIconFont) {
            ((PDEDrawableIconFont)getLayerAtIndex(0)).setElementShadowEnabled(enabled);
        } else if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            ((PDEDrawableIconImage)getLayerAtIndex(0)).setElementShadowEnabled(enabled);
        }

        //update(true);
    }


    /**
     * @brief Get whether shadow is enabled.
     */
    public boolean getElementShadowEnabled() {
        return mShadowEnabled;
    }


    /**
     * @brief Set shadow color.
     */
    public void setElementShadowColor(PDEColor color) {
        //any change?
        if (color.equals(mShadowColor)) return;

        //remember
        mShadowColor = color;

        if (getLayerAtIndex(0) instanceof PDEDrawableIconFont) {
            ((PDEDrawableIconFont)getLayerAtIndex(0)).setElementShadowColor(color);
        } else if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            ((PDEDrawableIconImage)getLayerAtIndex(0)).setElementShadowColor(color);
        }

        //update();
    }


    /**
     * @brief Get shadow color.
     */
    public PDEColor getElementShadowColor() {
        return mShadowColor;
    }


    /**
     * @brief Set shadow x offset.
     */
    public void setElementShadowXOffset(float offset) {
        //any change?
        if (offset == mShadowXOffset) return;

        //remember
        mShadowXOffset = offset;

        if (getLayerAtIndex(0) instanceof PDEDrawableIconFont) {
            ((PDEDrawableIconFont)getLayerAtIndex(0)).setElementShadowXOffset(offset);
        } else if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            ((PDEDrawableIconImage)getLayerAtIndex(0)).setElementShadowXOffset(offset);
        }

        //update(true);
    }


    /**
     * @brief Get shadow x offset.
     */
    public float getElementShadowXOffset() {
        return mShadowXOffset;
    }


    /**
     * @brief Set shadow y offset.
     */
    public void setElementShadowYOffset(float offset) {
        //any change?
        if (offset == mShadowYOffset) return;

        //remember
        mShadowYOffset = offset;

        if (getLayerAtIndex(0) instanceof PDEDrawableIconFont) {
            ((PDEDrawableIconFont)getLayerAtIndex(0)).setElementShadowYOffset(offset);
        } else if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            ((PDEDrawableIconImage)getLayerAtIndex(0)).setElementShadowYOffset(offset);
        }

        //update(true);
    }


    /**
     * @brief Get shadow y offset.
     */
    public float getElementShadowYOffset() {
        return mShadowYOffset;
    }


    /**
     * @brief Set Padding.
     */
    public void setElementPadding(float padding) {
        //any change?
        if (padding == mPadding) return;

        //remember
        mPadding = padding;

        if (getLayerAtIndex(0) instanceof PDEDrawableIconFont) {
            ((PDEDrawableIconFont)getLayerAtIndex(0)).setElementPadding(padding);
        } else if (getLayerAtIndex(0) instanceof PDEDrawableIconImage) {
            ((PDEDrawableIconImage)getLayerAtIndex(0)).setElementPadding(padding);
        }

        //update(true);
    }


    /**
     * @brief Get Padding.
     */
    public float getElementPadding() {
        return mPadding;
    }


    /**
     * @brief Called when the bounds changed.
     *
     * Gets bounds of the child drawable after bounds have changed and sets these bounds. While doing this
     * onBoundsChange is deactivated to prevent recursion.
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        if (mDoBoundsChange) {
            mDoBoundsChange = false;
            super.onBoundsChange(bounds);
            if (getLayerAtIndex(0) != null) {
                setBounds(getLayerAtIndex(0).getBounds());
            }
            mDoBoundsChange = true;
        }
    }


    /**
     * @brief Update all of my sublayers.
     */
    @Override
    protected void doLayout() {
        Rect bounds = getBounds();
        if (getLayerAtIndex(0) != null) {
            getLayerAtIndex(0).setBounds(0, 0, bounds.width(), bounds.height());
        }


    }


//    /**
//     * @brief Updates image color.
//     */
//    protected void updateAllPaints() {
//        // no further paints needed so apply paint changes directly on the image
//        if (mIconDrawable != null) {
//            mIconDrawable.setAlpha(mAlpha);
//            mIconDrawable.setDither(mDither);
//            mIconDrawable.setColorFilter(mColorFilter);
//        }
//    }


    /**
     * @brief Set element Height.
     */
    public void setLayoutHeight(int height) {
        setLayoutSize(getBounds().width(), height);
    }


    /**
     * @brief Set element Width.
     */
    public void setLayoutWidth(int width) {
        setLayoutSize(width, getBounds().height());
    }


//    /**
//     * @brief Draws image.
//     *
//     * @param c the Canvas of the DrawingBitmap we want to draw into.
//     * @param bounds the current bounding rect of our Drawable.
//     */
//    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
//        // security
//        if (getNumberOfLayers() == 0 || bounds.width() <= 0 || bounds.height() <= 0) return;
//
//        getLayerAtIndex(0).draw(c);
//    }

    //---------------------------------------------------------------------------------------------------------------------
// ----- Wrapper View  ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Delivers its own Wrapper-View.
     *
     * If we want to use a Drawable within a ViewGroup/Layout we have to put it into some view first. We could write
     * a lot of specialised Views that simply forward all functions we need to configure the underlying drawable or
     * we can use one standardized View with only some basic functions to wrap all our custom Drawables within.
     * Configuration is then done directly on the Drawable. In order to keep a better overview and spare a lot of
     * maintenance-intensive View-Code we decided for the latter option. Every custom drawable carries its own
     * Wrapper-View which is created on demand and places the drawable within.
     *
     * @return a simple view that wraps this Drawable.
     */
    public PDEIconView getWrapperView() {
        if (mWrapperView == null) {
            mWrapperView = new PDEIconView(PDECodeLibrary.getInstance().getApplicationContext(), this);
        }
        return mWrapperView;
    }


}