package de.telekom.pde.codelibrary.ui.inflater;


import android.view.LayoutInflater;


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
}
