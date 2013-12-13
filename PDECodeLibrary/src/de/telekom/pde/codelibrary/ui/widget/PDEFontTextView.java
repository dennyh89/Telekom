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

    @SuppressWarnings("unused")
	public PDEFontTextView(final Context context)	{
		super(context);

        // don't do the init when shown in developer tool (IDE)
        if (isInEditMode()) return;

        setTypeface(PDEFontHelpers.getNormal().getTypeface());
	}


    @SuppressWarnings("unused")
	public PDEFontTextView(final Context context, final AttributeSet attrs)
	{
		super(context, attrs);

        // don't do the init when shown in developer tool (IDE)
        if (isInEditMode()) return;


        init(attrs, 0);
	}


    @SuppressWarnings("unused")
	public PDEFontTextView(final Context context, final AttributeSet attrs, final int defStyle)
	{
		super(context, attrs, defStyle);

        // don't do the init when shown in developer tool (IDE)
        if (isInEditMode()) return;


        init(attrs, defStyle);
	}


	private void init(final AttributeSet attrs, final int defStyle)
	{

        final TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.PDEFontTextView, defStyle,	0);
		final int fontType = a.getInteger(R.styleable.PDEFontTextView_font, 0);
		// As defined in pde_attrs.xml ->PDEFontTextView, 0 is normal font
		if (fontType == 0) {
			setTypeface(PDEFontHelpers.getNormal().getTypeface());
		} else {
			setTypeface(PDEFontHelpers.getBold().getTypeface());
		}
		
		a.recycle();
	}

}
