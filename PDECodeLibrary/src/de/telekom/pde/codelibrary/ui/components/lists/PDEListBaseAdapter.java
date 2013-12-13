/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists;

//----------------------------------------------------------------------------------------------------------------------
//  PDEListBaseAdapter
//----------------------------------------------------------------------------------------------------------------------

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;


/**
 * @brief Base adapter for all PDELists.
 *
 * When dealing with PDELists we recommend to use this class as a base class for your derived list adapter.
 * For styleguide conform behaviour of the list elements we have to wrap the xml-layouted list items into PDEListItems.
 * This base class handles the correct wrapping process. It also creates PDEHolder objects to increase the
 * performance of the item recycling. It creates these holder objects automatically by default to increase your
 * comfort.
 *
 */
public abstract class PDEListBaseAdapter extends BaseAdapter {

//-----  properties ---------------------------------------------------------------------------------------------------
    // resource ID of the item layout
    protected int mItemTemplateResourceID;
    // number of items in the list
    protected int mItemCount;
    // For some of the subviews of the list item view we want to be able to set the content. This array holds the IDs
    // of the subviews we want to access later on in order to change their content.
    protected int[] mTargetViewIDs;
    // This base adapter can handle the automatic creation of holder objects. These holder objects have to implement
    // PDEHolderInterface then. The user can overload createHolder() if he wants to use his own version of a
    // Holder that implements PDEHolderInterface. But the user can also use his completely own custom holder (or he can
    // even relinquish to use a holder at all). Then he has to do all the holder handling on its own and he should
    // turn of the automatic holder generation and the handling of these holders.
    protected boolean mCreateHolderAutomatically;


    /**
     * @brief constructor
     *
     * @param context activity context
     * @param itemTemplateResourceID Resource ID for the layout of the list item.
     * @param targetViewIDs Resource IDs of the subviews of the item view. Deliver all IDs of the subviews of which
     *                      you want to change the content later on.
     */
    public PDEListBaseAdapter(Context context, int itemTemplateResourceID, int[] targetViewIDs){
        mItemTemplateResourceID = itemTemplateResourceID;
        mItemCount = 0;
        mTargetViewIDs = targetViewIDs;
        // for default the automatic holder generation is turned on.
        mCreateHolderAutomatically = true;
    }


    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount(){
        return mItemCount;
    }


    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     * data set.
     * @return The data at the specified position.
     */
    @Override
    public abstract Object getItem(int position);


    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public abstract long getItemId(int position);



    /**
     * @brief Get a View that displays the data at the specified position in the data set.
     *
     * In order to give our list items the styleguide agentstate highlight behaviour we have to wrap the xml-layouted
     * list items into PDEListItems. This function does the needed work for you. It creates the PDEListItem object
     * and delivers this object the resource ID of the list item layout. The PDEListItem object inflates it
     * internally. This method also does the automatic creation of the holder elements which increase the list
     * performance. It calls the functions initListItem and fillListItem which should be overriden by the user in the
     * derived adapter for initialization / content update purposes. Normally you have to override at least
     * fillListItem because we use list item recycling and so you always have to set the actual contents of the list
     * item. At the end the method delivers the PDEListItem View.
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        PDEListItem listItem;
        PDEHolderInterface holder;

        // Is there a convertedView we can reuse to save performance?
        if (convertView == null){
            // create list item
            listItem = new PDEListItem(PDECodeLibrary.getInstance().getApplicationContext());
            // remember list position
            listItem.setListPosition(position);
            // set layout template
            listItem.setTemplate(mItemTemplateResourceID);
            if (mCreateHolderAutomatically){
                // create holder (factory)
                holder = createHolder(mItemTemplateResourceID);
                // init current holder
                holder.initHolder(listItem.getLayoutedView(), mTargetViewIDs);
                // set current holder
                listItem.setHolder(holder);
            }
            // add event listener
            listItem.addListener(parent, "onPDEListItemClicked");
            // init the layout elements of the list item
            initListItem(listItem);
        } else {
            // we can recycle a former listitem
            listItem = (PDEListItem)convertView;
            // update list position
            listItem.setListPosition(position);
        }



       // fill list item with actual data
       fillListItem(listItem);

        // return the list item
        return listItem;
    }

    /**
     * @brief Initialize layout elements of the list item
     *
     * Override in derived class if needed.
     *
     * @param listItem the listitem that should be initialized
     */
    protected void initListItem(PDEListItem listItem){}

    /**
     * @brief Fill list item with actual data
     *
     *  Override in derived class if needed.
     *
     * @param listItem the list item whose data should be updated.
     */
    protected void fillListItem(PDEListItem listItem){}


    /**
     * @brief Factory function for creation of holder object.
     *
     * If you wan to use an other holder class than the default one, overload this method and deliver your own holder.
     *
     * @param type if you want to have other holders than the default one you can deliver information about which one
     *             you want to have by using this type parameter.
     *
     * @return deliver a holder that was derived from PDEHolderInterface
     */
    protected PDEHolderInterface createHolder(int type){
        // default holder
        return new PDEHolder();
    }


    /**
     * @brief Set number of list items.
     *
     * This base adapter holds no data source for the list items. Normally the number of entries in the data source
     * define the size of the list. In this base adapter the items and their contents are created
     * dynamically. So we have to deliver how many of them we want to have. If you want to use a data source,
     * add it in a derived class (and keep mItemCount up to date if you don't also overload getCount()).
     *
     * @param num set the number of list items you want to have.
     */
    public void setItemCount(int num){
        mItemCount = num;
    }

    /**
     * @brief Turn on / off automatic creation of holder objects.
     *
     * If you want to use custom holder classes which do not implement PDEHolderInterface or if you don't want to use
     * holders at all, then turn the automatic creation off. If you only want to use a custom holder that implements
     * PDEHolderInterface and not the default one (PDEHolder) then simply override createHolder and leave automatic
     * creation turned on.
     *
     * @param autoCreation true -> automatic creation on / false -> automatic creation off
     */
    public void setAutomaticHolderCreation (boolean autoCreation) {
        mCreateHolderAutomatically = autoCreation;
    }


    /**
     * @brief Find out if automatic creation is turned on.
     *
     * @return true -> automatic holder generation is on, false -> automatic holder generation is off
     */
    @SuppressWarnings("unused")
    public boolean isHolderCreatedAutomatically () {
        return  mCreateHolderAutomatically;
    }
}
