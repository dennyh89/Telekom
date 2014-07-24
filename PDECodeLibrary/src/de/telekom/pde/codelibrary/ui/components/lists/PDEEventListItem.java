/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists;

import de.telekom.pde.codelibrary.ui.events.PDEEvent;

//----------------------------------------------------------------------------------------------------------------------
//  PDEEventListItem, but should be PDEListItemEvent
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Information about clicked list item is sent along with all events.
 *
 */
public class PDEEventListItem extends PDEEvent {
    // the position of the item within the list
    protected int mPosition;

    /**
     * @brief Set position of item within the list.
     *
     * @param pos position of item within list.
     */
    public void setListPosition(int pos){
        mPosition = pos;
    }

    /**
     * @brief Get position of item within the list.
     *
     * @return list position
     */
    public int getListPosition(){
        return mPosition;
    }
}
