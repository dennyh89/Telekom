package de.telekom.pde.codelibrary.ui.utils;


import android.graphics.Typeface;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;


//----------------------------------------------------------------------------------------------------------------------
//  PDETypefaceSpan
//----------------------------------------------------------------------------------------------------------------------


/**
 * Style a {@link Spannable} with a custom {@link Typeface}.
 * 
 * @author Tristan Waddington
 * @author Gabriel Weis
 */
public class PDETypefaceSpan extends MetricAffectingSpan
{

	private final Typeface	typeface;


	/**
	 * Load the {@link Typeface} and apply to a {@link Spannable}.
	 */
	public PDETypefaceSpan(final Typeface typeface)
	{
		this.typeface = typeface;
	}


	@Override
	public void updateDrawState(final TextPaint textPaint)
	{
		textPaint.setTypeface(typeface);
	}


	@Override
	public void updateMeasureState(final TextPaint textPaint)
	{
		textPaint.setTypeface(typeface);
	}
}