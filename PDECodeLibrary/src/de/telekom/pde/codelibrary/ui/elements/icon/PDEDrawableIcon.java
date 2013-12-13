/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.icon;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableIconImage
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Icon Class.
 *
 * Icon can be set by a drawable of by a string. The string can be the path to the resource, resource id or
 * a symbol from the Iconfont, by using a string consisting of '#' and the corresponding letter.
 */
public class PDEDrawableIcon extends PDEDrawableBase {
    private final static String LOG_TAG = PDEDrawableIcon.class.getName();

//-----  properties ---------------------------------------------------------------------------------------------------
    private Drawable mImage;
    private String mIconString;
    private PDEColor mIconColor;
    private PDEColor mShadowColor;
    private boolean mShadowEnabled;
    private float mShadowXOffset;
    private float mShadowYOffset;
    private float mPadding;
    private PDEDrawableIconFont mIconfont;
    private PDEDrawableIconImage mIconImage;
    private boolean mDoBoundsChange;
    private Drawable mIconDrawable;


    /**
     * @brief Constructor taking image.
     */
    public PDEDrawableIcon() {
        super();
        initialize();
    }


    /**
     * @brief Constructor taking image.
     *
     * @param drawable Image to be shown
     */
    public PDEDrawableIcon(Drawable drawable) {
        super();
        initialize();
        setElementIconDrawable(drawable);

    }


    /**
     * @brief Constructor taking string.
     */
    public PDEDrawableIcon(String iconstring) {
        super();
        initialize();
        setElementIconString(iconstring);
    }


    /**
     * @brief Initializes start values.
     */
    private void initialize() {
        mIconString = null;
        mImage = null;
        mIconfont = null;
        mIconImage = null;

        mIconColor = null;
        mShadowColor = new PDEColor();
        mShadowColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());
        mShadowEnabled = false;
        mShadowXOffset = 0.0f;
        mShadowYOffset = 1.0f;
        mPadding = 0.0f;

        mDoBoundsChange = true;
    }


    /**
     * @brief Updates icon with drawable or string.
     */
    private void updateIconDrawable() {
        if (mImage == null) {
            if (TextUtils.isEmpty(mIconString)) {
                mIconDrawable = null;
                return;
            }

            if (mIconString.charAt(0) == '#') {
                if (mIconfont == null) {
                    mIconfont = new PDEDrawableIconFont(mIconString.substring(1));
                } else {
                    mIconfont.setElementIconText(mIconString.substring(1));
                }
                if (mIconColor != null) mIconfont.setElementIconColor(mIconColor);
                mIconfont.setElementShadowEnabled(mShadowEnabled);
                mIconfont.setElementShadowColor(mShadowColor);
                mIconfont.setElementShadowXOffset(mShadowXOffset);
                mIconfont.setElementShadowYOffset(mShadowYOffset);
                mIconfont.setElementPadding(mPadding);
                mIconDrawable = mIconfont;
                return;
            } else {
                mImage = Drawable.createFromPath(mIconString);

                if (mImage == null) {
                    Log.e(LOG_TAG, "Image could not be loaded");
                    mIconDrawable = null;
                    return;
                }
            }
        }


        if (mIconImage == null) {
            mIconImage = new PDEDrawableIconImage(mImage);
        } else {
            mIconImage.setElementImage(mImage);
        }
        if (mIconColor != null) mIconImage.setElementIconColor(mIconColor);
        mIconImage.setElementShadowEnabled(mShadowEnabled);
        mIconImage.setElementShadowColor(mShadowColor);
        mIconImage.setElementShadowXOffset(mShadowXOffset);
        mIconImage.setElementShadowYOffset(mShadowYOffset);
        mIconImage.setElementPadding(mPadding);
        mIconDrawable = mIconImage;
    }


    /**
     * @brief Returns whether this icon has a native size (e.g. from a resource image).
     */
    public boolean hasNativeSize() {
        return (mImage != null);
    }


    /**
     * @brief Returns the native size of the icon (e.g. from a resource image).
     */
    public Point getNativeSize() {
        if (mImage != null) return new Point(mImage.getIntrinsicWidth(),mImage.getIntrinsicHeight());
        return new Point(0,0);
    }


    /**
     * @brief Get height of the Element.
     */
    public int getElementHeight() {
        if (isIconfont()) {
            return mIconfont.getElementHeight();
        } else {
            return getNativeSize().y + 1;
        }
    }


    /**
     * @brief Get Width of the Element.
     */
    public int getElementWidth() {
        if (isIconfont()) {
            return mIconfont.getElementWidth();
        } else {
            return getNativeSize().x + 1;
        }
    }


    /**
     * @brief Returns whether element has an icon drawable or icon string set.
     */
    public boolean hasElementIcon() {
        return !(mIconImage == null && TextUtils.isEmpty(mIconString));
    }


    /**
     * @brief Checks if icon drawable is PDEIconFont.
     */
    public boolean isIconfont() {
        return (mIconDrawable instanceof PDEDrawableIconFont);
    }


    /**
     * @brief Set icon image.
     */
    public void setElementIconDrawable(Drawable image)
    {
        //any change?
        if (image == mImage) return;

        //remember
        mImage = image;
        mIconString = null;
        updateIconDrawable();
        update(true);
    }


    /**
     * @brief Get icon by drawable.
     */
    public Drawable getElementIconDrawable()
    {
        if (mImage != null) return mImage;
        return null;
    }


    /**
     * @brief Set icon by string.
     */
    public void setElementIconString(String iconstring)
    {
        //any change?
        if (TextUtils.equals(iconstring,mIconString)) return;

        //remember
        mIconString = iconstring;
        mImage = null;
        updateIconDrawable();
        update(true);
    }


    /**
     * @brief Get icon string.
     */
    public String getElementIconString()
    {
        if (mIconString != null) return mIconString;
        return null;
    }


    /**
     * @brief Set Icon by drawable or string.
     *
     * Based on type of object either setElementIconDrawable of set ElementIconString is called.
     */
    public void setElementIcon(Object icon)
    {
        if (icon instanceof String) {
            setElementIconString((String)icon);
        } else if (icon instanceof Drawable) {
            setElementIconDrawable((Drawable) icon);
        }
    }


    /**
     * @brief Returns icon image if set, else icon string, else null.
     */
    public Object getElementIcon()
    {
        if (mImage != null) {
            return mImage;
        } else if (!TextUtils.isEmpty(mIconString)) {
            return mIconString;
        } else {
            return null;
        }
    }


    /**
     * @brief Set Icon Color.
     */
    public void setElementIconColor(PDEColor color) {
        //any change?
        if (color == mIconColor) return;
        //remember
        mIconColor = color;

        updateIconDrawable();
        //redraw
        update();
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
        updateIconDrawable();
        update(true);
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
        if (color == mShadowColor) return;
        //remember
        mShadowColor = color;
        updateIconDrawable();
        update();
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
    public  void setElementShadowXOffset(float offset) {
        //any change?
        if (offset == mShadowXOffset) return;

        //remember
        mShadowXOffset = offset;
        updateIconDrawable();
        update(true);
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
        updateIconDrawable();
        update(true);
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
        updateIconDrawable();
        update(true);
    }


    /**
     * @brief Get Padding.
     */
    public float getElementPadding() {
        return mPadding;
    }


    /**
     * @brief Called when bounds set via rect.
     */
    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
    }


    /**
     * @brief Called when bounds set via left/top/right/bottom values.
     */
    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
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
            if (mIconDrawable != null) setBounds(mIconDrawable.getBounds());
            mDoBoundsChange = true;
        }
    }


    /**
     * @brief Update all of my sublayers.
     */
    @Override
    protected void doLayout() {
        Rect bounds = getBounds();
        if (mIconDrawable != null) mIconDrawable.setBounds(0,0,bounds.width(),bounds.height());
    }


    /**
     * @brief Updates image color.
     */
    protected void updateAllPaints() {
        // no further paints needed so apply paint changes directly on the image
        if (mIconDrawable != null) {
            mIconDrawable.setAlpha(mAlpha);
            mIconDrawable.setDither(mDither);
            mIconDrawable.setColorFilter(mColorFilter);
        }
    }


    /**
     * @brief Set element Height.
     */
    public void setLayoutHeight(int height)
    {
        setLayoutSize(getBounds().width(), height);
    }


    /**
     * @brief Set element Width.
     */
    public void setLayoutWidth (int width)
    {
        setLayoutSize(width,getBounds().height());
    }


    /**
     * @brief Draws image.
     *
     * @param c the Canvas of the DrawingBitmap we want to draw into.
     * @param bounds the current bounding rect of our Drawable.
     */
    protected void updateDrawingBitmap (Canvas c, Rect bounds) {
        // security
        if (mIconDrawable==null || bounds.width() <= 0 || bounds.height() <= 0) return;

        mIconDrawable.draw(c);
    }

}