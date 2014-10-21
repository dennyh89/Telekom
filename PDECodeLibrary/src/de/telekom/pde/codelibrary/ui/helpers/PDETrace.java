package de.telekom.pde.codelibrary.ui.helpers;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Trace;

/// @cond INTERNAL_CLASS


/**
 * @brief Wrapper for Trace which takes care of api level.
 * So far covers only beginSection and endSection.
 */
public class PDETrace {

    private final static boolean DEBUG_TRACE = false;


    @SuppressLint("NewApi") // needed for Trace.beginSection which is api 11 (and ensured only be called then)
    public static void beginSection(String sectionName) {
        if (DEBUG_TRACE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Trace.beginSection(sectionName);
            }
        }
    }


    @SuppressLint("NewApi") // needed for Trace.endSection which is api 11 (and ensured only be called then)
    public static void endSection() {
        if (DEBUG_TRACE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Trace.endSection();
            }
        }
    }

}
/// @endcond INTERNAL_CLASS