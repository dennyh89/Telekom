/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */


/// @cond INTERNAL_CLASS
package de.telekom.pde.codelibrary.ui.components.lists.internal;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.elements.complex.PDEListHeaderLayout;

//----------------------------------------------------------------------------------------------------------------------
// SectionHeaderAndListAdapter
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Adapter which manages the section headers.
 *
 * The PDE section headers can show a title and the count of the list items of the respective section.
 * This adapter helps to handle the information that is important to manage the sections and their headers.
 */
public class SectionHeaderAndListAdapter extends ClonedArrayAdapter<SectionHeaderAndListAdapter.SectionInfo> {
    // tag for log outputs
    private final static String LOG_TAG = SectionHeaderAndListAdapter.class.getSimpleName();


    /**
     * Interface to supply values for the item count in section.
     */
    public interface SectionHeaderCountSupplier {
        // get the current count
        int getCountShownInHeader();

        // should the header show the count or not
        boolean showCount();
    }


    /**
     * @brief Data helper class that stores information about a section.
     */
    public class SectionInfo {
        // count helper
        protected SectionHeaderCountSupplier mCountSupplier;
        // section title
        protected String mTitle;
        // adapter that manages the items of a section
        protected Adapter mAdapter;
        // flag that decides if the section shows a header within the list
        protected boolean mShowHeader;
        // helper for the type counting
        protected int mTypeID;


        /**
         * @brief Constructor
         *
         * @param title section title
         * @param adapter adapter that manages the items of the section
         * @param countSupplier count helper
         */
        public SectionInfo(String title, Adapter adapter, SectionHeaderCountSupplier countSupplier) {
            mCountSupplier = countSupplier;
            mTitle = title;
            mAdapter = adapter;
            mShowHeader = true;
            mTypeID = 0;
        }


        /**
         * @brief Constructor
         *
         * @param title section title
         * @param adapter adapter that manages the items of the section
         */
        @SuppressWarnings("unused")
        public SectionInfo(String title, Adapter adapter) {
            mTitle = title;
            mCountSupplier = null;
            mAdapter = adapter;
            mShowHeader = true;
            mTypeID = 0;
        }


        /**
         * @brief Constructor
         *
         * @param adapter adapter that manages the items of the section
         */
        public SectionInfo(Adapter adapter) {
            mTitle = null;
            mCountSupplier = null;
            mAdapter = adapter;
            mShowHeader = false;
            mTypeID = 0;
        }


        /**
         * @brief Constructor
         *
         * @param title section title
         * @param adapter adapter that manages the items of the section
         * @param count item count which should be shown in the header (can differ from the real number of the items)
         */
        @SuppressWarnings("unused")
        public SectionInfo(String title, Adapter adapter, final int count) {
            mTitle = title;
            mAdapter = adapter;
            mShowHeader = true;
            mTypeID = 0;
            // override count supplier methods and place the delivered count
            mCountSupplier = new SectionHeaderCountSupplier() {
                @Override
                public int getCountShownInHeader() {
                    return count;
                }


                @Override
                public boolean showCount() {
                    return true;
                }
            };
        }


        /**
         * @brief Getter for the count supplier.
         */
        public SectionHeaderCountSupplier getCountSupplier() {
            return mCountSupplier;
        }


        /**
         * @brief Sets a count supplier.
         */
        @SuppressWarnings("unused")
        public void setCountSupplier(SectionHeaderCountSupplier supplier) {
            mCountSupplier = supplier;
        }


        /**
         * @brief Get section title.
         */
        public String getTitle() {
            return mTitle;
        }


        /**
         * @brief Set section title.
         */
        public void setTitle(String title) {
            mTitle = title;
        }


        /**
         * @brief Get section adapter.
         */
        public Adapter getAdapter() {
            return mAdapter;
        }


        /**
         * @brief Set section adapter.
         */
        public void setAdapter(Adapter adapter) {
            mAdapter = adapter;
        }


        /**
         * @brief Does the section show a header within the list?
         */
        public boolean isShowHeader() {
            return mShowHeader;
        }


        /**
         * @brief Activate/Deactivate header for the section.
         */
        @SuppressWarnings("unused")
        public void setShowHeader(boolean showHeader) {
            mShowHeader = showHeader;
        }


        /**
         * @brief Get type counter ID.
         */
        public int getTypeID() {
            return mTypeID;
        }


        /**
         * @brief Set type counter ID.
         */
        public void setTypeID(int id) {
            mTypeID = id;
        }
    }


    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     */
    public SectionHeaderAndListAdapter(Context context, int resource) {
        super(context, resource, 0, new ArrayList<SectionInfo>());
    }


    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     */
    @SuppressWarnings("unused")
    public SectionHeaderAndListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId, new ArrayList<SectionInfo>());
    }


    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    @SuppressWarnings("unused")
    public SectionHeaderAndListAdapter(Context context, int resource, SectionInfo[] objects) {
        super(context, resource, 0, Arrays.asList(objects));
    }


    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects            The objects to represent in the ListView.
     */
    @SuppressWarnings("unused")
    public SectionHeaderAndListAdapter(Context context, int resource, int textViewResourceId, SectionInfo[] objects) {
        super(context, resource, textViewResourceId, Arrays.asList(objects));
    }


    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    @SuppressWarnings("unused")
    public SectionHeaderAndListAdapter(Context context, int resource, ArrayList<SectionInfo> objects) {
        super(context, resource, 0, objects);
    }


    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects            The objects to represent in the ListView.
     */
    @SuppressWarnings("unused")
    public SectionHeaderAndListAdapter(Context context,
                                       int resource,
                                       int textViewResourceId,
                                       ArrayList<SectionInfo> objects) {
        super(context, resource, textViewResourceId, objects);
    }


    /**
     * @brief Remove a section from list / adapter.
     *
     * @param index index of the section that should be removed
     *
     * @return the removed section
     */
    public SectionInfo remove(int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                return mOriginalValues.remove(index);
            } else {
                return mObjects.remove(index);
            }
        }
    }


    /**
     * @brief Creates the view of the section header and fills it with data.
     *
     * @param position list position
     * @param convertView probably recycleable header view
     * @param parent parent adapter/list of the view that should be created
     * @param resource resource id for the view that should be created
     *
     * @return the created and filled header view
     */
    @Override
    protected View createViewFromResource(int position, View convertView, ViewGroup parent,
                                          int resource) {
        View view, foundView;
        PDEListHeaderLayout pdeHeader;
        PDETextView pdeTextViewHeader;
        TextView textViewHeader;

        // init
        pdeHeader = null;
        pdeTextViewHeader = null;
        textViewHeader = null;

        // no convert view? -> inflate the resource id
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            // check if the convert view has a usable subview which is addressed by the fieldId
            if (mFieldId > 0
                && convertView.findViewById(mFieldId) != null
                && (convertView.findViewById(mFieldId) instanceof PDEListHeaderLayout ||
                    convertView.findViewById(mFieldId) instanceof PDETextView ||
                    convertView.findViewById(mFieldId) instanceof TextView)) {
                view = convertView;
            }
            // if there's no fieldId, check if the convert view itself is of a usable type
            else if (convertView instanceof PDEListHeaderLayout ||
                     convertView instanceof PDETextView ||
                     convertView instanceof TextView) {
                view = convertView;
            }
            // if delivered convert view isn't usable at all, inflate the resource id
            else {
                view = mInflater.inflate(resource, parent, false);
            }
        }

        // no view available -> quit
        if (view == null) {
            Log.e(LOG_TAG, "Could neither reuse convert view, nor inflate a valid view from resource.");
            throw new IllegalStateException(
                    LOG_TAG + " Could neither reuse convert view, nor inflate a valid view from resource.");
        }

        // no fieldId available, so cast view directly
        if (mFieldId == 0) {
            if (view instanceof PDEListHeaderLayout) {
                pdeHeader = (PDEListHeaderLayout) view;
            } else if (view instanceof PDETextView) {
                pdeTextViewHeader = (PDETextView) view;
            } else if (view instanceof TextView) {
                textViewHeader = (TextView) view;
            }
        }
        // if fieldId available, get subView from view and cast the subView
        else {
            foundView = view.findViewById(mFieldId);
            if (foundView instanceof PDEListHeaderLayout) {
                pdeHeader = (PDEListHeaderLayout) foundView;
            } else if (foundView instanceof PDETextView) {
                pdeTextViewHeader = (PDETextView) foundView;
            } else if (foundView instanceof TextView) {
                textViewHeader = (TextView) foundView;
            }
        }

        // no usable view available -> quit
        if (pdeHeader == null && pdeTextViewHeader == null && textViewHeader == null) {
            Log.e(LOG_TAG, "You must supply a resource ID for a PDEListHeaderLayout or a PDETextView or a TextView.");
            throw new IllegalStateException(
                    LOG_TAG + " requires the resource ID to be a PDEListHeaderLayout or a PDETextView or a TextView.");
        }

        // get data from data set for this position
        SectionInfo item = getItem(position);

        // security
        if (item == null) {
            Log.e(LOG_TAG, "No valid data available for this position in the data set.");
            throw new IllegalStateException(
                    LOG_TAG + " No valid data available for this position in the data set.");
        }

        // fill data in the header object that is currently valid
        if (pdeHeader != null) {
            pdeHeader.fillItem(item);
        } else if (pdeTextViewHeader != null) {
            pdeTextViewHeader.setText(item.getTitle());
        } else {
            textViewHeader.setText(item.getTitle());
        }

        // return view
        return view;
    }


    /**
     * @brief Add section to adapter.
     *
     * @param title section header title
     * @param adapter adapter that holds the items of the section
     * @param countSupplier helper for the counting of the items
     */
    public void add(String title, Adapter adapter, SectionHeaderCountSupplier countSupplier) {
        add(new SectionInfo(title, adapter, countSupplier));
    }


    /**
     * @brief Insert section into adapter at distinct index position.
     *
     * @param title section header title
     * @param adapter adapter that holds the items of the section
     * @param countSupplier helper for the counting of the items
     * @param index the index position where the section should be inserted into
     */
    public void insert(String title, Adapter adapter, SectionHeaderCountSupplier countSupplier, int index) {
        insert(new SectionInfo(title, adapter, countSupplier), index);
    }


    /**
     * @brief Add section to adapter.
     *
     * @param adapter adapter that holds the items of the section
     */
    public void add(Adapter adapter) {
        add(new SectionInfo(adapter));
    }


    /**
     * @brief Insert section into adapter at distinct index position.
     *
     * @param adapter adapter that holds the items of the section
     * @param index the index position where the section should be inserted into
     */
    public void insert(Adapter adapter, int index) {
        super.insert(new SectionInfo(adapter), index);
    }


    /**
     * @brief Add section to adapter.
     *
     * @param sectionInfo the object that holds all important information about the section.
     */
    @Override
    public void add(SectionInfo sectionInfo) {
        super.add(sectionInfo);
    }


    /**
     * @brief Insert section into adapter at distinct index position.
     *
     * @param sectionInfo the object that holds all important information about the section.
     * @param index The index at which the object must be inserted.
     */
    @Override
    public void insert(SectionInfo sectionInfo, int index) {
        super.insert(sectionInfo, index);
    }


    /**
     * @brief Get section by position.
     *
     * @param position the position of the section.
     *
     * @return the object that holds all important information about the section.
     */
    @Override
    public SectionInfo getItem(int position) {
        return super.getItem(position);
    }


    /**
     * @brief Total number of section elements (header + items).
     *
     * Header is only counted when visible.
     *
     * @param position the position of the section.
     *
     * @return sum of section header and section items.
     */
    public int getSectionHeaderAndItemCount(int position) {
        SectionInfo section = getItem(position);

        if (section.isShowHeader()) {
            return section.getAdapter().getCount() + 1;
        } else {
            return section.getAdapter().getCount();
        }
    }


    /**
     * @brief Get number of the items of the section (without header)
     *
     * @param position the position of the section.
     * @return Number of the items of the section (without header)
     */
    @SuppressWarnings("unused")
    public int getSectionItemCount(int position) {
        return getItem(position).getAdapter().getCount();
    }


    /**
     * @brief Checks if the header of the referenced section is shown.
     *
     * @param position index of section
     *
     * @return true == header is shown; false == header is not shown
     */
    @SuppressWarnings("unused")
    public boolean getSectionShowsHeader(int position) {
        return getItem(position).isShowHeader();
    }
}
/// @endcond INTERNAL_CLASS


















