/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists;

//----------------------------------------------------------------------------------------------------------------------
// PDEHolder
//----------------------------------------------------------------------------------------------------------------------

import android.view.View;

/**
 * @brief Interface for PDE (List) Holder classes.
 *
 * If you want to use a holder class that is able to use the maximum comfort given by the PDEListBaseAdapter class,
 * implement this Interface when creating a holder class. You can also use a completly custom holder class,
 * but then you have to do the complete handling (creation, initialization and value updating) on your own.
 *
 */
public interface PDEHolderInterface {

    /**
     * @brief Init the desired subViews of the row item layout.
     *
     * This function gets the complete view of the current list item and a array with
     * the IDs of some of the subViews of this view. We probably want to edit the content of these subviews later on.
     * So we extract these subviews from the main view and store them in this holder for faster access.
     *
     * @param layoutView the (main) view of the current list item.
     * @param targetViewIDs the IDs of the subviews (of layoutView) which we want to easily access later on.
     */
    public void initHolder(View layoutView, int[] targetViewIDs);

    /**
     * @brief Set s content for our target view.
     *
     * @param targetViewID the ID of the view that should receive the content.
     * @param value the string content for our target view.
     */
    public void setTargetViewContent(int targetViewID, String value);

    /**
     * @brief Set s content for our target view.
     *
     * @param targetViewID the ID of the view that should receive the content.
     * @param value the integer content for our target view. Most useful for resource IDs.
     */
    public void setTargetViewContent(int targetViewID, int value);
}
