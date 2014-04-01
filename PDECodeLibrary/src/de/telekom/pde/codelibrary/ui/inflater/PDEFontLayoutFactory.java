package de.telekom.pde.codelibrary.ui.inflater;


import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import android.view.MenuItem;


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
                || name.equalsIgnoreCase("com.android.internal.view.menu.ActionMenuItem")
				|| name.equalsIgnoreCase("TextView")
                || name.equals(TextView.class.getName())
                )
		{
            try
			{
				final LayoutInflater li = LayoutInflater.from(context);
                View view;

                // this solution prevents an exception...
                if (name.equalsIgnoreCase("TextView")){
                    view = li.createView(TextView.class.getName(), null, attrs);
                } else {
                    view = li.createView(name, null, attrs);
                }

                //set default font for TextViews to telegrotesk font
                if (view instanceof TextView) {
                    ((TextView) view).setTypeface(PDETypeface.sDefaultFont.getTypeface());
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
							// If pre-honeycomb device set the background programmatically,
							view.setBackgroundResource(R.drawable.selectable_background_pde);
						}
					}
				});*/
				return view;
			} catch (final InflateException e) {
				e.printStackTrace();
				// Handle any inflation exception here
			} catch (final ClassNotFoundException e) {
				e.printStackTrace();
				// Handle any ClassNotFoundException here
			}
		}

        // this part of the code was done for all text views before - heaven might know why (I don't)
        // so we only set this now for some very special case (don't know if it worth it)
        if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            {
                try
                {
                    View view;
                    final LayoutInflater li = LayoutInflater.from(context);

                    view = li.createView(name, null, attrs);


                    // If pre-honeycomb device set the background programmatically,
                    view.setBackgroundResource(R.drawable.selectable_background_pde);

                } catch (final InflateException e) {
                    e.printStackTrace();
                    // Handle any inflation exception here
                } catch (final ClassNotFoundException e) {
                    e.printStackTrace();
                    // Handle any ClassNotFoundException here
                }
            }
        }

		return null;
	}

}
