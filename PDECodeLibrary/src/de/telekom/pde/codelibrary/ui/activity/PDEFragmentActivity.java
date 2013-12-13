/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 *
 * kdanner - 21.06.13 : 16:59
 */

package de.telekom.pde.codelibrary.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.inflater.PDEInflaterUtils;

@SuppressWarnings("unused")
public class PDEFragmentActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PDECodeLibrary.getInstance().isAssignmentOfDefaultFontToTextViewsEnabled()) {
            PDEInflaterUtils.setFontFactory(getLayoutInflater());
        }
    }
}