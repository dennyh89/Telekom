/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

//
//  Class for some Attributes helper functions (attrs.xml).
//

package de.telekom.pde.codelibrary.ui.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;


//----------------------------------------------------------------------------------------------------------------------
//  PDEResourceAttributesHelper
//----------------------------------------------------------------------------------------------------------------------



public class PDEResourceAttributesHelper {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEResourceAttributesHelper.class.getName();


    /**
     * @brief Check if an integer exists in an attribute integer array.
     */
    static public boolean isInEnum(Context context, int enumId, int toCheck ) {
        return isInIntArray(context, enumId, toCheck);
    }


    /**
     * @brief Check if an flag exists in an attribute flag array.
     */
    @SuppressWarnings("unused")
    static public boolean isInFlags(Context context, int flagId, int toCheck ) {
        return isInIntArray(context, flagId, toCheck);
    }


    /**
     * @brief Helper function to read out integer of array.
     */
    static private boolean isInIntArray(Context context, int arrayId, int toCheck) {
        int i;
        int[] attributeValues = getIntArray(context, arrayId);

        for (i = 0; i < attributeValues.length; i++) {
            if (attributeValues[i] == toCheck) return true;
        }

        // nothing found
        return false;
    }


    /**
     * @brief Get the array with the values declared in the enum/flag attribute in the attr.xml
     *         !!!!!!!!!!!!ONLY RETURNS ARRAY WITH VALUES, COULD BE EMPTY ARRAY !!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *         normally the array we get by "getIntArray" contains the attribute itself at the first position
     *
     * @param context current context
     * @param arrayId id of the array we looking for
     * @return array with entries, could be null
     */
    static public int[] getIntArray(Context context, int arrayId) {
        Resources res = context.getResources();
        int[] attributeValues = res.getIntArray(arrayId);

        // check if there are existing entries in the array
        // null -> no array with this id
        // length <=1 no array or no entries (If attribute has no enum/flag entries, size is always 1)
        // -> seems that the id of the proper attribute is always saved at the first position
        if (attributeValues == null || attributeValues.length <= 1){
            Log.e(LOG_TAG, "Empty or not existing integer resource array:" + res.getResourceName(arrayId));
            return new int[0];
        }
        return copyOfRange(attributeValues, 1, attributeValues.length);
    }


    //copied for API levels < 9
    private static int[] copyOfRange(int[] original, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        int originalLength = original.length;
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        int[] result = new int[resultLength];
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }
}
