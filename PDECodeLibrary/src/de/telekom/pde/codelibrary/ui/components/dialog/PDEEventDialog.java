/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.dialog;

import de.telekom.pde.codelibrary.ui.events.PDEEvent;


//----------------------------------------------------------------------------------------------------------------------
//  PDEEventDialog
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Special event vor constructDialog.
 */
public class PDEEventDialog extends PDEEvent {
    // integer result value that shows the decision of the user (or how he closed the dialog)
    protected int mButtonResult;
    // contains the label ID of the pressed button if the the app programmer used a predefined dialog and not a customized one
    protected String mButtonResultLabelID;


    /**
     * @brief Set the button result.
     *
     * There are two ways for the user to close a dialog:
     * - He takes a decision and presses one of possible answer buttons
     * - He cancels the dialog by pressing the back button
     *
     * This integer result value represents the picked result (button 1, button 2, button 3, back button).
     *
     * The predefined integer constants for this can be found in PDEDialogActivity (e.g. PDEDialogActivity:PDE_DIALOG_RESULT_BUTTON1)
     *
     * @param btn result integer value that shows the pressed button.
     */
    public void setButtonResult(int btn){
        mButtonResult = btn;
    }


    /**
     * @brief Set the ID of the button label of the pressed button.
     *
     * If one of the predefined dialogs was used, then the buttons carry an ID for their label which can be resolved
     * from the resources. The label ID of the pressed button will be stored here. If there was a custom label assigned
     * to the button from the app programmer or the back button was pressed, then you can't resolve this ID.
     *
     * @param id of the label of the pressed button.
     */
    public void setButtonResultLabelID(String id){
        mButtonResultLabelID = id;
    }

    /**
     * @brief Get the button result.
     *
     * @return the button which was pressed to end the dialog.
     */
    public int getButtonResult(){
        return mButtonResult;
    }


    /**
     * @brief Get the label ID of the pressed button.
     *
     * @return the label ID of the pressed button.
     */
    public String getButtonResultLabelID(){
        return mButtonResultLabelID;
    }
}
