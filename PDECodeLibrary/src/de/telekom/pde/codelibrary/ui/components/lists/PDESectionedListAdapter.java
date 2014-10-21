/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.components.lists.adapters.PDEListAdapterInterface;
import de.telekom.pde.codelibrary.ui.components.lists.internal.SectionHeaderAndListAdapter;
import de.telekom.pde.codelibrary.ui.elements.complex.PDEListHeaderLayout;

//----------------------------------------------------------------------------------------------------------------------
// PDESectionedListAdapter
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Adapter for the management of sectioned lists.
 *
 * The main idea is that every section of a sectioned list has its own adapter that delivers the needed data.
 * This approach is very flexible, because you can use different adapters and layouts for each individual section
 * if this fits your needs.
 * The PDESectionedListAdapter itself is a management superstructure, because it manages all these sub-adapters of the
 * individual sections. It also implements the PDEListItem-wrapping which is needed for correct (styleguide-conform)
 * highlighting of the list elements. So adapters that are used within the PDESectionedListAdapter don't need to care
 * about PDEListItem-wrapping by themselves.
 */
public class PDESectionedListAdapter extends BaseAdapter {

    // tag for log outputs
    private final static String LOG_TAG = PDESectionedListAdapter.class.getSimpleName();
    // switch to turn debugging on/off
    private final static boolean DEBUG = false;
    // counter to manage the total number of view types used within this adapter (including sub-adapters)
    protected int viewTypeCounter = 1;
    // for default we assume, that every section can use a individual adapter and layout. So for correct recycling of
    // list items, the items of different adapters must have different view types. So this adapter takes care that each
    // sub-adapter gets automatically a unique type id assigned. The drawback of this approach is, that a list, that
    // shares an identical list item layout among all sections is less efficient than it could be, because it could
    // recycle list elements not only within the same section but also among section borders. So if you create such a
    // list you should turn this feature off. But know what you're doing. If the layouts differ, the whole thing might
    // crash.
    protected boolean uniqueTypeIDAutoAssignment = true;


    // data set observer that notifies the PDESectionedListAdapter if the contents of one of the sub-adapters changed.
    private final DataSetObserver mInternalDataSetObserver = new DataSetObserver() {
        // data set changed
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }


        // data set invalid
        @Override
        public void onInvalidated() {
            super.onInvalidated();
            notifyDataSetInvalidated();
        }
    };

    // helper adapter that holds the sections
    protected final SectionHeaderAndListAdapter mMainAdapter;
    // type id constant of the section headers
    public final static int TYPE_SECTION_HEADER = 0;


    /**
     * @brief Constructor
     */
    public PDESectionedListAdapter(Context context) {
        mMainAdapter = new SectionHeaderAndListAdapter(context, R.layout.pde_list_header_clustered);
    }


    /**
     * @brief Constructor
     */
    public PDESectionedListAdapter(Context context, int resource) {
        mMainAdapter = new SectionHeaderAndListAdapter(context, resource);
    }


    /**
     * @brief Constructor
     *
     * @param type the type / layout of the section headers
     */
    public PDESectionedListAdapter(Context context, PDEListHeaderLayout.PDEListHeaderType type) {
        if (type == PDEListHeaderLayout.PDEListHeaderType.PDEListHeaderTypeHeadline) {
            mMainAdapter = new SectionHeaderAndListAdapter(context, R.layout.pde_list_header);
        } else {
            mMainAdapter = new SectionHeaderAndListAdapter(context, R.layout.pde_list_header_clustered);
        }
    }


    /**
     * @brief Add section without an header.
     *
     * @param adapter Adapter for this section.
     *
     * @return index of the section
     */
    public int addSection(final Adapter adapter) {
        // check if the delivered adapter is capable of PDEListItem-wrapping. If this is the case, turn it off, since
        // the PDESectionedListAdapter does its own wrapping.
        if (adapter instanceof PDEListAdapterInterface) {
            ((PDEListAdapterInterface) adapter).setAutoPDEListItemWrapping(false);
        }
        // add the adapter of the section
        mMainAdapter.add(adapter);
        // assign unique view type id for this section
        resolveViewTypeID(mMainAdapter.getItem(mMainAdapter.getCount() - 1));
        // register the data observer in order to stay in touch with data changes
        adapter.registerDataSetObserver(mInternalDataSetObserver);

        return mMainAdapter.getCount();
    }


    /**
     * @brief Add section with header.
     *
     * @param section   Section title.
     * @param adapter   Adapter for this section.
     * @param showCount Set if count is shown.
     *
     * @return index of the section
     */
    public int addSection(String section, final Adapter adapter, final boolean showCount) {
        // check if the delivered adapter is capable of PDEListItem-wrapping. If this is the case, turn it off, since
        // the PDESectionedListAdapter does its own wrapping.
        if (adapter instanceof PDEListAdapterInterface) {
            ((PDEListAdapterInterface) adapter).setAutoPDEListItemWrapping(false);
        }
        // add the adapter of the section
        mMainAdapter.add(section, adapter, new SectionHeaderAndListAdapter.SectionHeaderCountSupplier() {
            // override count supplier methods
            @Override
            public int getCountShownInHeader() {
                return adapter.getCount();
            }


            // "store" showCount parameter
            @Override
            public boolean showCount() {
                return showCount;
            }
        });
        // assign unique view type id for this section
        resolveViewTypeID(mMainAdapter.getItem(mMainAdapter.getCount() - 1));
        // register the data observer in order to stay in touch with data changes
        adapter.registerDataSetObserver(mInternalDataSetObserver);

        return mMainAdapter.getCount();
    }


    /**
     * @brief Insert Section without header.
     *
     * @param adapter Adapter for this section.
     * @param index   Index to insert to.
     *
     * @return total section count
     */
    @SuppressWarnings("unused")
    public int insertSection(final Adapter adapter, int index) {
        // check if the delivered adapter is capable of PDEListItem-wrapping. If this is the case, turn it off, since
        // the PDESectionedListAdapter does its own wrapping.
        if (adapter instanceof PDEListAdapterInterface) {
            ((PDEListAdapterInterface) adapter).setAutoPDEListItemWrapping(false);
        }
        // insert the adapter of the section at distinct index
        mMainAdapter.insert(adapter, index);
        // assign unique view type id for this section
        resolveViewTypeID(mMainAdapter.getItem(index));
        // register the data observer in order to stay in touch with data changes
        adapter.registerDataSetObserver(mInternalDataSetObserver);

        return mMainAdapter.getCount();
    }


    /**
     * @brief Insert section with header.
     *
     * @param section   Section title.
     * @param adapter   Adapter for this section.
     * @param showCount Set if count is shown.
     * @param index     Index to insert to.
     *
     * @return total section count
     */
    public int insertSection(String section, final Adapter adapter, final boolean showCount, int index) {
        // check if the delivered adapter is capable of PDEListItem-wrapping. If this is the case, turn it off, since
        // the PDESectionedListAdapter does its own wrapping.
        if (adapter instanceof PDEListAdapterInterface) {
            ((PDEListAdapterInterface) adapter).setAutoPDEListItemWrapping(false);
        }
        // insert the adapter of the section at distinct index
        mMainAdapter.insert(section, adapter, new SectionHeaderAndListAdapter.SectionHeaderCountSupplier() {
            // override count supplier methods
            @Override
            public int getCountShownInHeader() {
                return adapter.getCount();
            }


            // "store" showCount parameter
            @Override
            public boolean showCount() {
                return showCount;
            }
        }, index);

        // assign unique view type id for this section
        resolveViewTypeID(mMainAdapter.getItem(index));
        // register the data observer in order to stay in touch with data changes
        adapter.registerDataSetObserver(mInternalDataSetObserver);

        return mMainAdapter.getCount();
    }


    /**
     * @brief Get Section adapter.
     */
    public Adapter getSectionAdapter(int sectionIndex) {
        return mMainAdapter.getItem(sectionIndex).getAdapter();
    }


    /**
     * @brief Remove section at index.
     */
    public boolean removeSection(int sectionIndex) {
        if (sectionIndex >= mMainAdapter.getCount()) {
            return false;
        }
        mMainAdapter.remove(sectionIndex);

        notifyDataSetChanged();

        return true;
    }


    /**
     * @brief Get Section index for item (all items in all sections are counted).
     */
    public int getSectionForItem(int position) {
        int currentPosition = 0;
        int currentSection;
        int itemsInSection;

        for (currentSection = 0; currentSection < mMainAdapter.getCount(); currentSection++) {
            itemsInSection = mMainAdapter.getSectionHeaderAndItemCount(currentSection);
            if (position <= currentPosition + itemsInSection) {
                return currentSection;
            }

            // no hit - update position
            currentPosition += itemsInSection;
        }

        return -1;
    }


    /**
     * @brief Get Section count.
     */
    public int getSectionCount() {
        return mMainAdapter.getCount();
    }


    /**
     * @brief Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        // debug
        if (DEBUG) Log.d(LOG_TAG, "getItem(" + position + ")");

        int currentPosition = 0;
        int currentSection;
        int itemsInSection;

        for (currentSection = 0; currentSection < mMainAdapter.getCount(); currentSection++) {
            // debug
            if (DEBUG) Log.d(LOG_TAG, "getItem(" + position + ") currentPosition " + currentPosition + " for loop");
            // get SectionInfo of the section
            SectionHeaderAndListAdapter.SectionInfo info = mMainAdapter.getItem(currentSection);
            // does the section show a header?
            if (info.isShowHeader()) {
                // debug
                if (DEBUG) Log.d(LOG_TAG, "getItem(" + position + ") section " + currentSection + " has header");
                // found?
                if (position == currentPosition) {
                    // the header is the item we were looking for
                    return info;
                }
                currentPosition++;
            } else {
                // debug
                if (DEBUG) Log.d(LOG_TAG, "getItem(" + position + ") section " + currentSection + " has NO header");
            }

            // get number of items in the current section
            itemsInSection = info.getAdapter().getCount();
            // debug
            if (DEBUG)
                Log.d(LOG_TAG, "getItem(" + position + ") section " + currentSection + " has items: " + itemsInSection);
            // is item within current section?
            if (position - currentPosition <= itemsInSection) {
                // debug
                if (DEBUG) Log.d(LOG_TAG,
                                 "getItem(" + position + ") section " + currentSection + " getItem " + (position
                                                                                                        - currentPosition));
                // return the item
                return info.getAdapter().getItem(position - currentPosition);
            }

            // no hit - update position (jump to next section)
            currentPosition += itemsInSection;
        }

        // nothing found
        return null;
    }


    /**
     * @brief How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        // total together all sections, plus one for each section header
        int total = 0;
        int currentSection;

        for (currentSection = 0; currentSection < mMainAdapter.getCount(); currentSection++) {
            total += mMainAdapter.getSectionHeaderAndItemCount(currentSection);
        }

        // debug
        if (DEBUG) Log.d(LOG_TAG, "getCount -> " + total);

        return total;
    }


    /**
     * <p>
     * Returns the number of types of Views that will be created by
     * {@link #getView}. Each type represents a set of views that can be
     * converted in {@link #getView}. If the adapter always returns the same
     * type of View for all items, this method should return 1.
     * </p>
     * <p>
     * This method will only be called when when the adapter is set on the
     * the {@link android.widget.AdapterView}.
     * </p>
     *
     * @return The number of types of Views that will be created by this adapter
     */
    @Override
    public int getViewTypeCount() {
        // A former approach provided code here, which determined dynamically the total number of view types, but there
        // is one problem. According to the Android documentation,
        // getViewTypeCount() is only called once by the system. This is when the adapter is set. Since it will be
        // possible to add or remove sections dynamically during runtime, this function will deliver different values
        // whenever it is called from somewhere. But the RecycleBin of the list only knows the value it got at the
        // beginning when the adapter was set into the list. Sadly the value doesn't update when notifyDataSetChanged()
        // is called. So it's a bad idea to deliver possibly changing values here, since it can lead to index out of
        // bounds exceptions. For now we simply deliver a high number. It's a poor kludge, but if we want to stay
        // flexible there seems no better way right now.
        return 10000;
    }


    /**
     * Get the type of View that will be created by {@link #getView} for the specified item.
     *
     * @param position The position of the item within the adapter's data set whose view type we
     *                 want.
     * @return An integer representing the type of View. Two views should share the same type if one
     * can be converted to the other in {@link #getView}. Note: Integers must be in the
     * range 0 to {@link #getViewTypeCount} - 1. {@link #IGNORE_ITEM_VIEW_TYPE} can
     * also be returned.
     * @see #IGNORE_ITEM_VIEW_TYPE
     */
    @Override
    public int getItemViewType(int position) {
        int currentPosition = -1;
        int sectionSize, currentSection;

        for (currentSection = 0; currentSection < mMainAdapter.getCount(); currentSection++) {
            // get SectionInfo of the section
            SectionHeaderAndListAdapter.SectionInfo info = mMainAdapter.getItem(currentSection);
            // does the section show a header?
            if (info.isShowHeader()) {
                currentPosition++;
                if (position == currentPosition) {
                    //debug
                    if (DEBUG) {
                        Log.d(LOG_TAG,
                              "title " + info.getTitle() + " getItemViewType(" + position + ") -> TYPE_SECTION_HEADER");
                    }
                    // item is a header
                    return TYPE_SECTION_HEADER;
                }
            }

            // get number of section elements
            sectionSize = info.getAdapter().getCount();
            // is the item within the current section?
            if (position <= currentPosition + sectionSize) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "title " + info.getTitle() +
                                   " getItemViewType(" + position + ") -> " +
                                   (((uniqueTypeIDAutoAssignment) ? info.getTypeID() : 0)
                                    + info.getAdapter().getItemViewType(position - currentPosition)));
                }

                // if auto assignment is on, deliver unique type id
                return ((uniqueTypeIDAutoAssignment) ? info.getTypeID() : 0)
                       + info.getAdapter().getItemViewType(position - currentPosition);

            }
            // jump to next section
            currentPosition += sectionSize;
        }

        // debug
        if (DEBUG) {
            Log.d(LOG_TAG, "getItemViewType(" + position + ") -> -1");
        }

        // not found
        return -1;
    }


    /**
     * Indicates whether all the items in this adapter are enabled. If the
     * value returned by this method changes over time, there is no guarantee
     * it will take effect.  If true, it means all items are selectable and
     * clickable (there is no separator.)
     *
     * @return True if all items are enabled, false otherwise.
     * @see #isEnabled(int)
     */
    @Override
    public boolean areAllItemsEnabled() {
        // this way I get back my divider lines and as long as I return false in the isEnabled() function for the headers
        // they're still not clickable.
        return true;
    }


    /**
     * Returns true if the item at the specified position is not a separator.
     * (A separator is a non-selectable, non-clickable item).
     *
     * The result is unspecified if position is invalid. An {@link ArrayIndexOutOfBoundsException}
     * should be thrown in that case for fast failure.
     *
     * @param position Index of the item
     * @return True if the item is not a separator
     * @see #areAllItemsEnabled()
     */
    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }


    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PDEListItem convertWrapperView;
        View contentView;
        int size;
        int currentPosition = 0;
        int currentSection;

        if (DEBUG) Log.d(LOG_TAG, "getView(" + position + ")");

        // go through all sections
        for (currentSection = 0; currentSection < mMainAdapter.getCount(); currentSection++) {
            // get Section Info of current section
            SectionHeaderAndListAdapter.SectionInfo info = mMainAdapter.getItem(currentSection);

            // debug
            if (DEBUG) {
                Log.d(LOG_TAG, "getView(" + position + ") currentPosition: " + currentPosition + " currentSection: "
                               + currentSection);
            }

            // check if the section should show a section header
            if (info.isShowHeader()) {
                if (currentPosition == position) {
                    if (DEBUG) Log.d(LOG_TAG, "getView(" + position + ") mMainAdapter.getView(" + currentSection + ")");
                    return mMainAdapter.getView(currentSection, convertView, parent);
                }

                // it is not the header
                currentPosition++;
            }

            // get number of items in the current section
            size = info.getAdapter().getCount();

            // check if the item we're looking for is within the current section
            if (position < currentPosition + size) {
                // debug
                if (DEBUG) {
                    Log.d(LOG_TAG,
                          "getView(" + position + ") position is in this section " + currentPosition + " size: "
                          + size);
                }

                // check if we have a convertView which we can recycle
                if (convertView != null && convertView instanceof PDEListItem) {
                    convertWrapperView = (PDEListItem) convertView;
                    // debug
                    if (DEBUG) {
                        Log.d(LOG_TAG, "getView(" + position + ") section.getView(" + (position - currentPosition));
                    }

                    // get the content view (without PDEListItem wrapping)
                    contentView = info.getAdapter().getView(position - currentPosition,
                                                            convertWrapperView.getContentView(),
                                                            parent);
                    // if the content of the convertView and the fetched content are not the same, set the new content
                    if (contentView != convertWrapperView.getContentView()) {
                        convertWrapperView.setContentView(contentView);
                    }
                    // remember list position
                    convertWrapperView.setListPosition(position);
                    // debug
                    if (DEBUG) Log.d(LOG_TAG, "recycled");
                    // deliver wrapped View
                    return convertWrapperView;
                } else {
                    contentView = info.getAdapter().getView(position - currentPosition, convertView, parent);
                    // create list item
                    PDEListItem listItem = new PDEListItem(PDECodeLibrary.getInstance().getApplicationContext());
                    // remember list position
                    listItem.setListPosition(position);
                    // set content view
                    listItem.setContentView(contentView);
                    // add event listener
                    listItem.addListener(parent, "onPDEListItemClicked"); // ToDo: check if already set
                    // debug
                    if (DEBUG) Log.d(LOG_TAG, "new view");
                    // return list item
                    return listItem;
                }


            }
            // jump to next section
            currentPosition += size;
        }

        // nothing found
        return null;
    }


    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * @brief Assigns unique view id for section
     *
     * @param info section info
     */
    protected void resolveViewTypeID(SectionHeaderAndListAdapter.SectionInfo info) {
        // security
        if (info == null) {
            return;
        }
        // set value of current view id counter for the given section
        info.setTypeID(viewTypeCounter);
        // raise counter by amount of view types into the given section adapter
        viewTypeCounter += info.getAdapter().getViewTypeCount();
    }


    /**
     * @brief Activate / deactivate auto-assignment of unique type ids
     *
     * @param autoAssignment True == activate; False == deactivate
     */
    public void setUniqueTypeIDAutoAssignment(boolean autoAssignment) {
        // anything to do?
        if (autoAssignment == uniqueTypeIDAutoAssignment) return;
        // remember
        uniqueTypeIDAutoAssignment = autoAssignment;
    }


    /**
     * @brief Check if auto-assignment of unique type ids is turned on or off
     */
    @SuppressWarnings("unused")
    public boolean isUniqueTypeIDAutoAssigned() {
        return uniqueTypeIDAutoAssignment;
    }
}


