/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 *
 * kdanner - 20.03.14 : 10:28 
 */
package de.telekom.pde.codelibrary.ui.components.elementwrappers;


import android.content.Context;
import android.util.AttributeSet;

/**
 * @brief PDELayerTextView is Deprecated, since it has a inconsistent name -> use PDETextView instead.
 */
@Deprecated
public class PDELayerTextView extends PDETextView {
    /**
     * @brief Constructor.
     */
    public PDELayerTextView(Context context){
        super(context);

    }


    /**
     * @brief Constructor.
     */
    public PDELayerTextView(Context context, AttributeSet attrs){
        super(context, attrs);

    }


    /**
     * @brief Constructor.
     */
    public PDELayerTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

    }

}
