/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;


import android.graphics.Point;
import android.graphics.Rect;


//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableInterface
//----------------------------------------------------------------------------------------------------------------------

@SuppressWarnings("unused")
public interface PDEDrawableInterface {

    /**
     * @brief Set Drawable Height
     */
    public void setLayoutHeight(int height);

    /**
     * @brief Set Drawable Width
     */
    public void setLayoutWidth (int width);


    /**
     * @brief Set Drawable Size
     *
     * Convenience function
     *
     * @param size new size of the multilayer. size.x == width, size.y == height
     */
    public void setLayoutSize (Point size);


    /**
     * @brief Set Drawable Size
     *
     * @param width new width of the multilayer
     * @param height new height of the multilayer
     */
    public void setLayoutSize (int width, int height);



    /**
     * @brief Set Multilayer Offset
     *
     * @param offset new offset of the multilayer
     */
    public void setLayoutOffset(Point offset);

    /**
     * @brief Set Multilayer Offset
     *
     * @param x new x-position of the multilayer
     * @param y new y-position of the multilayer
     */
    public void setLayoutOffset(int x, int y);


    /**
     * @brief Set the layout rectangle of the Multilayer.
     *
     * Sets the offset and size by the rect values
     *
     * @param rect The new layout rect of the element.
     */
    public void setLayoutRect(Rect rect);


    /**
     * @brief Set additional padding that is needed to display things outside of the element like e.g. outer shadow.
     *
     * @param padding additional padding
     */
    public void setNeededPadding(int padding);

    /**
     * @brief Returns the padding the element needs to be displayed correctly.
     *
     * Some things like an outer shadow have to be drawn outside of the element bounds.
     * So the View that holds the element has to be sized bigger than the element bounds.
     * For proper layouting the view must be extended to each direction by the value delivered by
     * this function.
     *
     * @return the needed padding
     */
    public int getNeededPadding();


    /**
     * @brief Get the layout rectangle.
     */
    public Rect getLayoutRect();


    /**
     * @brief Get the layout size.
     */
    public Point getLayoutSize();


    /**
     * @brief Get the layout offset.
     */
    public Point getLayoutOffset();


    /**
     * @brief Get the layout width.
     */
    public int getLayoutWidth();


    /**
     * @brief Get the layout height.
     */
    public int getLayoutHeight();

}
