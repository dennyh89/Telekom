/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 *
 * kdanner - 21.06.13 : 16:59
 */

package de.telekom.pde.codelibrary.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.inflater.PDEInflaterUtils;

@SuppressWarnings("unused")
public class PDEFragmentActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PDECodeLibrary.getInstance().isAssignmentOfDefaultFontToTextViewsEnabled()) {
            // this won't work since the inflater factory is already set. See implemented in onCreateView instead.
            PDEInflaterUtils.setFontFactory(getLayoutInflater());
        }
    }


    /**
     * @brief Create TextViews already here, to be able to set telegrotesk font.
     */
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);

        if (view == null) {
            view = PDEInflaterUtils.onCreateTextViewAndSetFont(name,context,attrs);
        }


        return view;
    }

}