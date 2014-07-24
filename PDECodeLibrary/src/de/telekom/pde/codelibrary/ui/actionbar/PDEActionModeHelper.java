package de.telekom.pde.codelibrary.ui.actionbar;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.R;
import android.support.v7.view.ActionMode;


/**
 * Helper class to display the {@link ActionMode} for an {@code Activity}.
 * It will use a custom view to display the title using the Telegrotesk font.
 * Use {@link #setTitle(String)} on this class, to set the title for this action mode.
 * <p/>
 * <b>Make sure to make the call to super() for {@link #onCreateActionMode(ActionMode, Menu)}, because this is where the
 * custom View gets set for this {@link ActionMode}.</b>
 */
public class PDEActionModeHelper implements ActionMode.Callback {

    private TextView title;


    @Override
    public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
        return false;
    }


    @SuppressLint("InflateParams")
    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        //  inflate layout from xml without root view
        // ( i think this is intended, so we add @SuppressLint("InflateParams"))
        @SuppressLint("InflateParams") final View customView
                = LayoutInflater.from(PDECodeLibrary.getInstance().getApplicationContext())
                                .inflate(R.layout.ab_actionmode_customview,
                                         null);
        if (customView != null) {
            title = (TextView) customView.findViewById(R.id.abActionModeTitle);
            mode.setCustomView(customView);
        }
        return true;
    }


    @Override
    public void onDestroyActionMode(final ActionMode mode) {

    }


    @Override
    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
        return false;
    }


    public void setTitle(final String text) {
        title.setText(text);
    }
}
