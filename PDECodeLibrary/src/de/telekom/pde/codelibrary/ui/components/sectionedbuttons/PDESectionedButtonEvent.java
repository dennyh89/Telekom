/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014 Neuland Multimedia GmbH.
 */
package de.telekom.pde.codelibrary.ui.components.sectionedbuttons;


import de.telekom.pde.codelibrary.ui.events.PDEEvent;


// ---------------------------------------------------------------------------------------------------------------------
//  PDESectionedButtonEvent
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Special Event for PDESectionedButton.
 */
public class PDESectionedButtonEvent extends PDEEvent {

    /**
     * @brief Global tag for log outputs.
     */
    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDESectionedButtonEvent.class.getName();

    // variable to remember the selected section index
    private int mSelectedSectionIndex;


    /**
     * @brief Constructor.
     */
    public PDESectionedButtonEvent() {
        super();
        setSelectedSectionIndex(PDESectionedButton.PDESectionedButtonNoSectionSelected);
    }


    /**
     * @brief Set the current selected index.
     */
    public void setSelectedSectionIndex(int index) {
        mSelectedSectionIndex = index;
    }


    /**
     * @brief Get the current selected index.
     */
    public int getSelectedSectionIndex() {
        return mSelectedSectionIndex;
    }
}
