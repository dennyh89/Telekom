package de.telekom.pde.codelibrary.ui.inflater;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.widget.TextView;
import com.actionbarsherlock.view.MenuItem;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;


/**
 * A custom {@link LayoutInflater.Factory} that will use the Telegrotesk font when creating {@link TextView}s and
 * {@link MenuItem}s.<br/>
 * This will also set a custom background for the 'regular' menu items, displayed on pre-Honeycomb, on hardware press.
 */
public class PDEFontLayoutFactory implements Factory
{

	@Override
	public View onCreateView(final String name, final Context context, final AttributeSet attrs)
	{
		if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")
				|| name.equalsIgnoreCase("TextView"))
		{
			try
			{
				final LayoutInflater li = LayoutInflater.from(context);
				final View view = li.createView(name, null, attrs);
                //set default font to textviews
                ((TextView) view).setTypeface(PDETypeface.sDefaultFont.getTypeface());
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                {
                    // If pre-honeycomb device set the background programatically,
                    view.setBackgroundResource(R.drawable.selectable_background_pde);
                }
				/*new Handler().post(new Runnable()
				{

					@Override
					public void run()
					{
						// here we can change the font!
						((TextView) view).setTypeface(PDEFontHelpers.getTeleGroteskNormal(context).getTypeface());
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
						{
							// If pre-honeycomb device set the background programatically,
							view.setBackgroundResource(R.drawable.selectable_background_pde);
						}
					}
				});*/
				return view;
			} catch (final InflateException e)
			{
				e.printStackTrace();
				// Handle any inflation exception here
			} catch (final ClassNotFoundException e)
			{
				e.printStackTrace();
				// Handle any ClassNotFoundException here
			}
		}
		return null;
	}

}
