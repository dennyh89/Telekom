package de.telekom.pde.codelibrary.ui.actionbar;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import de.telekom.pde.codelibrary.ui.R;


/**
 * Helper class to display the {@link ActionMode} for an {@code Activity}.
 * It will use a custom view to display the title using the Telegrotesk font.
 * Use {@link #setTitle(String)} on this class, to set the title for this action mode.
 * <p/>
 * <b>Make sure to make the call to super() for {@link #onCreateActionMode(ActionMode, Menu)}, because this is where the
 * custom View gets set for this {@link ActionMode}.</b>
 */
public class PDEActionModeHelper implements ActionMode.Callback
{

	private final Context	context;
	private TextView		title;


	public PDEActionModeHelper(final Context ctx)
	{
		context = ctx;
	}


	@Override
	public boolean onActionItemClicked(final ActionMode mode, final MenuItem item)
	{
		return false;
	}


	@Override
	public boolean onCreateActionMode(final ActionMode mode, final Menu menu)
	{
		final View customView = LayoutInflater.from(context).inflate(R.layout.ab_actionmode_customview, null);
		title = (TextView) customView.findViewById(R.id.abActionModeTitle);
		mode.setCustomView(customView);
		return true;
	}


	@Override
	public void onDestroyActionMode(final ActionMode mode)
	{

	}


	@Override
	public boolean onPrepareActionMode(final ActionMode mode, final Menu menu)
	{
		return false;
	}


	public void setTitle(final String text)
	{
		title.setText(text);
	}
}
