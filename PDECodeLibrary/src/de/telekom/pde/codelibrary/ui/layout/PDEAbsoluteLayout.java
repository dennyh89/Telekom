
package de.telekom.pde.codelibrary.ui.layout;

/*
 *
 * This code has been modified by Neuland Multimedia GmbH and is based on 
 * AbsoluteLayout from the Android Open Source Project
 *  - This code is not a contribution -
 * 
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews.RemoteView;

import de.telekom.pde.codelibrary.ui.R;


/**
 * A layout that lets you specify exact locations (x/y coordinates) of its children. Absolute
 * layouts are less flexible and harder to maintain than other types of layouts without absolute
 * positioning.
 * <p>
 * <strong>XML attributes</strong>
 * </p>
 */
@RemoteView
public class PDEAbsoluteLayout extends ViewGroup {

    public static class PDEAbsoluteLayoutHelper {


        /**
         * @brief Set View Size.
         */
        public static void setViewSize(View view, int width, int height) {
            PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) view.getLayoutParams();
            if (layerParams != null) {
                layerParams.width = width;
                layerParams.height = height;
                view.setLayoutParams(layerParams);
            }
        }


        /**
         * @brief Set View Size.
         */
        public static void setViewSize(View view, Point size) {
            setViewSize(view, size.x, size.y);
        }


        /**
         * @brief Set View width.
         */
        public static void setViewWidth(View view, int width) {
            PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) view.getLayoutParams();
            if (layerParams != null) {
                setViewSize(view, width, layerParams.height);
            }
        }


        /**
         * @brief Set View height.
         */
        public static void setViewHeight(View view, int height) {
            PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) view.getLayoutParams();
            if (layerParams != null) {
                setViewSize(view, layerParams.width, height);
            }
        }


        /**
         * @brief Set View Offset.
         */
        public static void setViewOffset(View view, int x, int y) {
            PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) view.getLayoutParams();
            if (layerParams != null) {
                layerParams.x = x;
                layerParams.y = y;
                view.setLayoutParams(layerParams);
            }
        }


        /**
         * @brief Set View Offset.
         */
        public static void setViewOffset(View view, Point offset) {
            setViewOffset(view, offset.x, offset.y);
        }


        /**
         * @brief Set View Rect.
         */
        public static void setViewRect(View view, Rect rect) {
            setViewOffset(view, rect.left, rect.top);
            setViewSize(view, rect.width(), rect.height());
        }
    }


    /**
     * Constructor
     *
     * @param context {@link Context}
     */
    public PDEAbsoluteLayout(Context context) {
        super(context);
    }


    /**
     * Constructor
     *
     * @param context {@link Context}
     * @param attrs   {@link AttributeSet}
     */
    public PDEAbsoluteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * Constructor
     *
     * @param context  {@link Context}
     * @param attrs    {@link AttributeSet}
     * @param defStyle int
     */
    public PDEAbsoluteLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * onMeasure
     *
     * @param widthMeasureSpec  int
     * @param heightMeasureSpec int
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxHeight = 0;
        int maxWidth = 0;

        // Find out how big everyone wants to be
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // Find rightmost and bottom-most child
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                int childRight = child.getMeasuredWidth();
                int childBottom = child.getMeasuredHeight();

                PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) child
                        .getLayoutParams();

                if (lp != null) {
                    childRight += lp.x;
                    childBottom += lp.y;
                }

                maxWidth = Math.max(maxWidth, childRight);
                maxHeight = Math.max(maxHeight, childBottom);
            }
        }

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
                             resolveSize(maxHeight, heightMeasureSpec));
    }


    /**
     * @return a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}, a height of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and with the coordinates (0,
     * 0).
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop();
                PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) child
                        .getLayoutParams();
                if (lp != null) {
                    childLeft += lp.x;
                    childTop += lp.y;
                }
                child.layout(childLeft, childTop,
                             childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            }
        }
    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PDEAbsoluteLayout.LayoutParams(getContext(), attrs);
    }


    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof PDEAbsoluteLayout.LayoutParams;
    }


    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }


    /**
     * Per-child layout information associated with AbsoluteLayout.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        /**
         * The horizontal, or X, location of the child within the view group.
         */
        public int x;

        /**
         * The vertical, or Y, location of the child within the view group.
         */
        public int y;


        /**
         * Creates a new set of layout parameters with the specified width, height and location.
         *
         * @param width  the width, either {@link #MATCH_PARENT}, {@link #WRAP_CONTENT} or a fixed
         *               size in pixels
         * @param height the height, either {@link #MATCH_PARENT}, {@link #WRAP_CONTENT} or a fixed
         *               size in pixels
         * @param x      the X location of the child
         * @param y      the Y location of the child
         */
        public LayoutParams(int width, int height, int x, int y) {
            super(width, height);
            this.x = x;
            this.y = y;
        }


        /**
         * Creates a new set of layout parameters. The values are extracted from the supplied
         * attributes set and context. The XML attributes mapped to this set of layout parameters
         * are:
         * <ul>
         * <li><code>layout_x</code>: the X location of the child</li>
         * <li><code>layout_y</code>: the Y location of the child</li>
         * <li>All the XML attributes from {@link android.view.ViewGroup.LayoutParams}</li>
         * </ul>
         *
         * @param c     the application environment
         * @param attrs the set of attributes from which to extract the layout parameters values
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PDEAbsoluteLayout_Layout);
            if (a != null) {
                x = a.getDimensionPixelOffset(R.styleable.PDEAbsoluteLayout_Layout_pde_layoutX, 0);
                y = a.getDimensionPixelOffset(R.styleable.PDEAbsoluteLayout_Layout_pde_layoutY, 0);
                a.recycle();
            }
        }


        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


    /**
     * Provides manual access to onLayout method
     */
    @SuppressLint("WrongCall")
    @SuppressWarnings("unused")
    public void updateLayout(boolean changed, int l, int t, int r, int b) {
        onLayout(changed, l, t, r, b);
    }
}
