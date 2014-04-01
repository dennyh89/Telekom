package de.telekom.pde.codelibrary.ui.inflater;


import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;


public class PDEInflaterUtils {

	/**
	 * Sets up the {@link PDEFontLayoutFactory} using the TeleGrotesk font, to be used for this inflater.</b>
	 * If this inflater already has a factory set up, nothing will happen.
	 * @param inflater - the inflater on which to apply the {@link PDEFontLayoutFactory}.
	 */
	public static void setFontFactory(final LayoutInflater inflater) {
		if (inflater.getFactory() == null) {
			inflater.setFactory(new PDEFontLayoutFactory());
		}
	}


    /**
     * @brief Helper function for LayoutInflater.Factory.onCreateView, which creates TextViews and sets the
     * telegrotesk font.
     */
    public static View onCreateTextViewAndSetFont(String name, Context context, AttributeSet attrs) {
        View view = null;

        if (PDECodeLibrary.getInstance().isAssignmentOfDefaultFontToTextViewsEnabled()
                && name.equalsIgnoreCase("TextView")) {
            final LayoutInflater li = LayoutInflater.from(context);
            try {
                view = li.createView(TextView.class.getName(), null, attrs);

                if (view != null && view instanceof TextView) {
                    ((TextView) view).setTypeface(PDETypeface.sDefaultFont.getTypeface());
                }
            } catch (final InflateException e) {
                e.printStackTrace();
                // Handle any inflation exception here
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
                // Handle any ClassNotFoundException here
            }

        }

        return view;
    }
}
