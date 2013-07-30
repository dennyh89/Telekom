package de.telekom.pde.codelibrary.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;


/**
 * A custom {@link TextView} that will use the Telegrotesk Font.<br/>
 * You can specify in XML the {@link Typeface} to be used like this : <br/>
 * <code> pde:font="normal|fett"</code><br/>
 * Also define the custom attributes in your XML file : xmlns:pde="http://schemas.android.com/apk/res-auto".
 */
public class PDEFontTextView extends TextView
{

	public PDEFontTextView(final Context context)
	{
		super(context);
		setTypeface(PDEFontHelpers.getTeleGroteskNormal(context).getTypeface());
	}


	public PDEFontTextView(final Context context, final AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}


	public PDEFontTextView(final Context context, final AttributeSet attrs, final int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}


	private void init(final AttributeSet attrs, final int defStyle)
	{
		final TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PDEFontTextView, defStyle,
				0);
		final int fontType = a.getInteger(R.styleable.PDEFontTextView_font, 0);
		// As defined in pde_attrs.xml ->PDEFontTextView, 0 is normal font
		if (fontType == 0)
		{
			setTypeface(PDEFontHelpers.getTeleGroteskNormal(getContext()).getTypeface());
		}
		else
		{
			setTypeface(PDEFontHelpers.getTeleGroteskFett(getContext()).getTypeface());
		}
		
		a.recycle();
	}

}
