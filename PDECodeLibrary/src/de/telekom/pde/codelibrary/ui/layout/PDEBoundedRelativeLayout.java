/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 *
 * kdanner - 20.06.13 : 16:45
 */

package de.telekom.pde.codelibrary.ui.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import de.telekom.pde.codelibrary.ui.R;

//----------------------------------------------------------------------------------------------------------------------
//  PDEBoundedRelativeLayout
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief RelativeLayout with a maximum height and (or) maximum width functionality.
 *
 * The android relative layout doesn't support a maximum dimension, this one extends this functionality. The dimensions
 * can be set via xml or programmatically.
 * The two custom xml attributes are max_width and max_height. You need a project specific xml name space.
 */
@SuppressWarnings("unused")
public class PDEBoundedRelativeLayout extends RelativeLayout {

    private int mMaxWidth;
    private int mMaxHeight;

    /**
     * @brief Constructor.
     *
     * @param context
     */
    public PDEBoundedRelativeLayout(Context context) {
        super(context);
        init (null);
    }

    /**
     * @brief Constructor.
     *
     * @param context
     * @param attrs
     */
    public PDEBoundedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    /**
     * @brief Constructor.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PDEBoundedRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * @brief Common init function for all constructors.
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        // set default values
        mMaxWidth = -1;
        mMaxHeight = -1;

        // read xml attributes
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PDEBoundedRelativeLayout);
            mMaxWidth = a.getDimensionPixelSize(R.styleable.PDEBoundedRelativeLayout_max_width, -1);
            mMaxHeight = a.getDimensionPixelSize(R.styleable.PDEBoundedRelativeLayout_max_height, -1);
            a.recycle();
        }
    }


    /**
     * @brief Set the maximum width in pixel for this layout.
     *
     * -1 disables the maximum width again
     *
     * @param maxWidth width in pixel
     */
    public void setMaxWidth(int maxWidth) {
        // changes?
        if (maxWidth == mMaxWidth) return;

        // remember
        mMaxWidth = maxWidth;

        // schedule pass of layout tree
        requestLayout();
    }


    /**
     * @brief Query current maximum width setting.
     *
     * A value of zero or less means that there is no maximum width set.
     *
     * @return
     */
    public int getMaxWidth() {
        return mMaxWidth;
    }


    /**
     * @brief Set the maximum height in pixel for this layout.
     *
     * -1 disables the maximum height again
     *
     * @param maxHeight width in pixel
     */
    public void setMaxHeight(int maxHeight) {
        // changes?
        if (maxHeight == mMaxHeight) return;

        // remember
        mMaxHeight = maxHeight;

        // schedule pass of layout tree
        requestLayout();
    }


    /**
     * @brief Query current maximum height setting.
     *
     * A value of zero or less means that there is no maximum height set.
     *
     * @return
     */
    public int getMaxHeight() {
        return mMaxHeight;
    }


    /**
     * @brief Overwritten layouting function - ensures that there are maximum dimensions.
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mMaxWidth > 0 && mMaxWidth < measuredWidth) {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, measureMode);
        }

        if (mMaxHeight > 0 && mMaxHeight < measuredHeight) {
            int measureMode = MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, measureMode);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
