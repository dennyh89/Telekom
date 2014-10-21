/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.components.lists.PDEListItem;
import de.telekom.pde.codelibrary.ui.components.lists.internal.ClonedArrayAdapter;

//----------------------------------------------------------------------------------------------------------------------
// PDEArrayAdapter
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Array Adapter which can handle TextViews AND PDETextViews.
 *
 * The default Android ArrayAdapter can only handle a default Android TextView. This extension of ArrayAdapter can also
 * handle a PDETextView, so it can be used with styleguide conform layouts. It's also available of PDEListItem wrapping
 * in order to ensure the styleguide conform highlighting of the list items.
 */
public class PDEArrayAdapter<T> extends ClonedArrayAdapter<T> implements PDEListAdapterInterface {
    protected boolean mAutoPDEListItemWrapping = true;


    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a TextView / PDETextView to use when
     *                 instantiating views.
     */
    @SuppressWarnings("unused")
    public PDEArrayAdapter(Context context, int resource) {
        super(context, resource);
    }


    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param textViewResourceId The id of the TextView / PDETextView within the layout resource to be populated
     */
    @SuppressWarnings("unused")
    public PDEArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }


    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a TextView / PDETextView to use when
     *                 instantiating views.
     * @param objects The objects to represent in the ListView.
     */
    @SuppressWarnings("unused")
    public PDEArrayAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
    }


    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param textViewResourceId The id of the TextView / PDETextView within the layout resource to be populated
     * @param objects The objects to represent in the ListView.
     */
    public PDEArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }


    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a TextView / PDETextView to use when
     *                 instantiating views.
     * @param objects The objects to represent in the ListView.
     */
    @SuppressWarnings("unused")
    public PDEArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }


    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param textViewResourceId The id of the TextView / PDETextView within the layout resource to be populated
     * @param objects The objects to represent in the ListView.
     */
    public PDEArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }


    /**
     * @brief Creates / recycles view and fills it with the current data.
     *
     * This function either tries to recycle a given convertView or inflates a new view from the given resource.
     * Either way it fills the view with the current data of the underlying data set.
     *
     * @param position within the dataset / list.
     * @param convertView view which can possibly be recycled.
     * @param parent parent view which holds the view we handle here.
     * @param resource resource to inflate the view.
     *
     * @return created and filled view
     */
    protected View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View view;
        PDETextView pdeText;
        TextView txtView;

        // init
        pdeText = null;
        txtView = null;

        // recycle convertView if we have one, otherwise inflate new view from given resource
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        // check if we should target distinct (sub)view addressed by a given resource ID
        if (mFieldId != 0) {
            View foundView = null;
            // try to find the view that fits the given resource ID
            if (view != null) foundView = view.findViewById(mFieldId);
            // if we recycled the convertView and we can't find the addressed subview within it, we switch to the fallback
            // solution. We inflate the given resource to a new view and search for the addressed subview within the new
            // view.
            if (foundView == null && view == convertView) {
                Log.d("PDEArrayAdapter",
                      "The delivered field id couldn't be found in the convert view, so we inflate the resource and try again");
                view = mInflater.inflate(resource, parent, false);
                if (view != null) foundView = view.findViewById(mFieldId);
            }
            // if we weren't successful at all in finding the addressed subview we quit the processing here.
            if (foundView == null) {
                Log.e("PDEArrayAdapter", "Couldn't find the view.You must supply a valid resource ID.");
                throw new IllegalStateException(
                        "PDEArrayAdapter requires a valid the resource ID", null);
            } else {
                // if we found the addressed subview, we check if it is a TextView or a PDETextView. Otherwise we quit
                // the processing.
                if (foundView instanceof TextView) {
                    txtView = (TextView) foundView;
                } else if (foundView instanceof PDETextView) {
                    pdeText = (PDETextView) foundView;
                } else {
                    Log.e("PDEArrayAdapter", "You must supply a resource ID for a TextView or a PDETextView");
                    throw new IllegalStateException(
                            "PDEArrayAdapter requires the resource ID to be a TextView or a PDETextView", null);
                }
            }
        }
        // if there was no ID given for a subview, we check if the view itself is a TextView or a PDETextView. Otherwise
        // we quit the processing.
        else if (view instanceof TextView) {
            txtView = (TextView) view;
        } else if (view instanceof PDETextView) {
            pdeText = (PDETextView) view;
        } else {
            Log.e("PDEArrayAdapter", "You must supply a resource ID for a TextView or a PDETextView");
            throw new IllegalStateException(
                    "PDEArrayAdapter requires the resource ID to be a TextView or a PDETextView", null);
        }

        // get item from data set for delivered position
        T item = getItem(position);

        if (item == null) {
            Log.e("PDEArrayAdapter", "Couldn't find an item in the data set for the delivered position");
            throw new IllegalStateException(
                    "PDEArrayAdapter couldn't find an item in the data set for the delivered position", null);
        }

        // fill current data into TextView / PDETextView
        if (txtView != null) {
            if (item instanceof CharSequence) {
                txtView.setText((CharSequence) item);
            } else {
                txtView.setText(item.toString());
            }
        } else {
            if (item instanceof String) {
                pdeText.setText((String) item);
            } else {
                pdeText.setText(item.toString());
            }
        }

        return view;
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
