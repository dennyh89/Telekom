/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists;

//----------------------------------------------------------------------------------------------------------------------
// PDEHolder
//----------------------------------------------------------------------------------------------------------------------

import android.view.View;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEIconView;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.metaphors.PDEPhotoFrameView;

import java.util.HashMap;

/**
 * @brief Holder class for List Item Elements.
 *
 * With this holder class it's possible to recycle list items more performance efficient.
 * For now it only supports PDEIconViews, PDEPhotoFrameViews and PDELayerTextViews as target views,
 * but it can be easily extended.
 */
public class PDEHolder implements PDEHolderInterface {

//-----  properties ---------------------------------------------------------------------------------------------------
    // hashMap that maps given resource IDs to the stored target views.
    HashMap<Integer,TargetViewHelper> mTargetViewMap;

    // initialization
    public PDEHolder() {
        // create hash map
        mTargetViewMap = new HashMap<Integer, TargetViewHelper>();
    }

    /**
     * @brief Init the desired subViews of the row item layout.
     *
     * This function gets the complete view of the current list item and a array with
     * the IDs of some of the subViews of this view. We probably want to edit the content of these subviews later on.
     * So we extract these subviews from the main view and store them in this holder for faster access.
     *
     * @param layoutView the (main) view of the current list item.
     * @param targetViewIDs the IDs of the subviews (of layoutView) which we want to easily access later on.
     */
    public void initHolder(View layoutView, int[] targetViewIDs){
        View subView;
        TargetViewHelper target;
        int i;

        // quit if there are no subview IDs
        if (targetViewIDs == null) return;

        // run through all given subview IDs
        for (i=0;i<targetViewIDs.length;i++) {
            // extract the subview with the given ID from the main view.
            subView = layoutView.findViewById(targetViewIDs[i]);
            // does the view exist?
            if (subView != null){
                // create a store helper
                target = new TargetViewHelper();
                // store extracted subview as targetView
                target.targetView = subView;
                // find out the type  of the targetView and remember it
                if (target.targetView instanceof PDETextView){
                    // text type
                    target.type = TargetViewHelper.TYPE_PDE_TEXT;
                } else if (target.targetView instanceof PDEIconView) {
                    // icon type
                    target.type = TargetViewHelper.TYPE_PDE_ICON;
                } else if (target.targetView instanceof PDEPhotoFrameView) {
                    // icon type
                    target.type = TargetViewHelper.TYPE_PDE_PHOTO_FRAME;
                }

                // if we found a known type add the store helper object to our map
                if (target.type != 0) {
                    mTargetViewMap.put(targetViewIDs[i], target);
                }
            }
        }
    }


    /**
     * @brief Set s content for our target view.
     *
     * @param targetViewID the ID of the view that should receive the content.
     * @param value the string content for our target view.
     */
    public void setTargetViewContent(int targetViewID, String value){
        TargetViewHelper target;

        // get the object that holds the view addressed by the ID
        target = mTargetViewMap.get(targetViewID);
        // found a object for the given ID?
        if (target != null) {
            // fill in the content by using the setters of the respective type
            if (target.type == TargetViewHelper.TYPE_PDE_TEXT && target.targetView != null){
                ((PDETextView) target.targetView).setText(value);
            } else if (target.type == TargetViewHelper.TYPE_PDE_ICON && target.targetView != null){
                ((PDEIconView) target.targetView).setIconString(value);
            }  else if (target.type == TargetViewHelper.TYPE_PDE_PHOTO_FRAME && target.targetView != null){
                ((PDEPhotoFrameView) target.targetView).setPictureString(value);
            }
        }
    }

    /**
     * @brief Set s content for our target view.
     *
     * @param targetViewID the ID of the view that should receive the content.
     * @param value the integer content for our target view. Most useful for resource IDs.
     */
    public void setTargetViewContent(int targetViewID, int value){
        TargetViewHelper target;

        // get the object that holds the view addressed by the ID
        target = mTargetViewMap.get(targetViewID);
        // found a object for the given ID?
        if (target != null) {
            if (target.type == TargetViewHelper.TYPE_PDE_TEXT && target.targetView != null){
                // fill in the string addressed by a resource ID
                ((PDETextView) target.targetView).setTextFromID(value);
            } else if (target.type == TargetViewHelper.TYPE_PDE_ICON && target.targetView != null){
                // fill in the drawable addressed by a resource ID
                ((PDEIconView) target.targetView).setIconFromID(value);
            } else if (target.type == TargetViewHelper.TYPE_PDE_PHOTO_FRAME && target.targetView != null){
                // fill in the drawable addressed by a resource ID
                ((PDEPhotoFrameView) target.targetView).setPhotoFromID(value);
            }
        }
    }

    /**
     * @brief Helper for storing the target views and their types.
     */
    class TargetViewHelper {
        // available types
        public final static int TYPE_PDE_TEXT = 1;
        public final static int TYPE_PDE_ICON = 2;
        public final static int TYPE_PDE_PHOTO_FRAME = 3;

        // view & its type
        public View targetView;
        public int type = 0;
    }
}
