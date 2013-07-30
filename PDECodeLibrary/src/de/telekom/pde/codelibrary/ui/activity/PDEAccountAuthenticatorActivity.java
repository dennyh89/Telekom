/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 *
 * kdanner - 21.06.13 : 17:08 
 */
package de.telekom.pde.codelibrary.ui.activity;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.inflater.PDEInflaterUtils;

public class PDEAccountAuthenticatorActivity extends AccountAuthenticatorActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PDECodeLibrary.getInstance().isAssignmentOfDefaultFontToTextViewsEnabled()) {
            PDEInflaterUtils.setFontFactory(getLayoutInflater());
        }
    }
}