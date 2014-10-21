/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.components.lists.PDEListItem;
import de.telekom.pde.codelibrary.ui.components.lists.viewbinders.PDESimpleCursorAdapterViewBinder;


//----------------------------------------------------------------------------------------------------------------------
// PDESimpleCursorAdapter
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief SimpleCursorAdapter which can handle various PDE layout components (like e.g. PDETextView).
 *
 * The default Android SimpleCursorAdapter can only handle a bunch of default Android views like e.g. TextView or
 * ImageView.
 * Additionally to these Android default views, this Adapter can also handle PDE views like PDETextView, PDEButton,
 * PDEIconView and PDEPhotoFrameView.
 * This adapter is also available of PDEListItem wrapping in order to ensure the styleguide conform highlighting of
 * the list items.
 */
@SuppressWarnings("unused")
public class PDESimpleCursorAdapter extends SimpleCursorAdapter implements PDEListAdapterInterface {
    // flag for automatic PDEListItem wrapping
    protected boolean mAutoPDEListItemWrapping = true;


    /**
     * @brief Standard constructor.
     *
     * @param context The context where the ListView associated with this
     *            SimpleListItemFactory is running
     * @param layout resource identifier of a layout file that defines the views
     *            for this list item. The layout file should include at least
     *            those named views defined in "to"
     * @param c The database cursor.  Can be null if the cursor is not available yet.
     * @param from A list of column names representing the data to bind to the UI.  Can be null
     *            if the cursor is not available yet.
     * @param to The views that should display column in the "from" parameter.
     *            These should all be TextViews. The first N views in this list
     *            are given the values of the first N columns in the from
     *            parameter.  Can be null if the cursor is not available yet.
     * @param flags Flags used to determine the behavior of the adapter,
     * as per {@link android.widget.CursorAdapter#CursorAdapter(Context, Cursor, int)}.
     */
    public PDESimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        // custom view binder that enables the handling of the PDE views
        setViewBinder(new PDESimpleCursorAdapterViewBinder());
    }


// ---------------- Auto PDEListItem Wrapping -------------------------------------------------------------


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
    @Override
    public void setAutoPDEListItemWrapping(boolean autoWrapping) {
        mAutoPDEListItemWrapping = autoWrapping;
    }


    /**
     * @brief Tells if automatic PDEListItem wrapping is currently turned on or off.
     */
    @Override
    public boolean isAutoPDEListItemWrapping() {
        return mAutoPDEListItemWrapping;
    }


    /**
     * @brief Apart from the standard getView() functionality this method handles the automatic PDEListItemWrapping.
     *
     * This method works like the standard getView() method of the adapter class (@link Adapter#getView)
     * Additionally it cares about the automatic PDEListItem wrapping if it is activated.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if automatic PDE List Item wrapping is turned on, we have to wrap the content views into PDEListItems
        // on our own, in order to ensure that the item highlighting is done the styleguide way
        if (isAutoPDEListItemWrapping()) {
            PDEListItem convertWrapperView;
            View oldContentView, newContentView;
            // check if we have a convertView which we can recycle
            if (convertView != null && convertView instanceof PDEListItem) {
                convertWrapperView = (PDEListItem) convertView;
                // get old ContentView
                oldContentView = convertWrapperView.getContentView();
                // get new contentView
                newContentView = super.getView(position, oldContentView, parent);
                // if new contentView differs from old contentView remember it
                if (newContentView != oldContentView) {
                    // remember new content view
                    convertWrapperView.setContentView(newContentView);
                }
                // remember list position
                convertWrapperView.setListPosition(position);
                // deliver wrapped View
                return convertWrapperView;
            } else {
                // create list item
                PDEListItem listItem = new PDEListItem(PDECodeLibrary.getInstance().getApplicationContext());
                // remember list position
                listItem.setListPosition(position);
                // get new content view
                newContentView = super.getView(position, null, parent);
                // wrap new content view into PDE list item
                listItem.setContentView(newContentView);
                // add event listener
                listItem.addListener(parent, "onPDEListItemClicked");
                return listItem;
            }
        } else {
            // if auto PDE List Item wrapping is turned off, some parent adapter cares about the correct highlighting
            // and we don't have to do it.
            return super.getView(position, convertView, parent);
        }
    }
}