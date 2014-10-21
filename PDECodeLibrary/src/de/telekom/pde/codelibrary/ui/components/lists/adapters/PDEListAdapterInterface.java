/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists.adapters;

//----------------------------------------------------------------------------------------------------------------------
// PDEListAdapterInterface
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Common interface of all PDE list adapters.
 */
public interface PDEListAdapterInterface {

    /**
     * @brief Enable/Disable automatic wrapping of the item views into PDEListItems
     *
     * PDEListItems handle the styleguide conform highlighting of list elements. So all list item views have to get
     * wrapped into such a PDEListItem. Sometimes a parent adapter already does the wrapping (e.g. when this adapter
     * is used within the PDESectionedListAdapter) so this adapter shouldn't do the wrapping (a second time). With this
     * method it's possible to turn the auto wrapping on or off. As default it is turned on.
     *
     * @param autoWrapping false for turning auto wrapping off.
     */
    public void setAutoPDEListItemWrapping(boolean autoWrapping);

    /**
     * @brief Tells if automatic PDEListItem wrapping is currently turned on or off.
     */
    @SuppressWarnings("unused")
    public boolean isAutoPDEListItemWrapping();
}
