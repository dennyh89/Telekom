package de.telekom.pde.codelibrary.ui.activity;


import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;


@SuppressLint("Registered")
public class PDEActivitySplash extends PDEActionBarActivity
{
	private PDETextView splashAppName;


	/**
	 * @return appName which is displayed below the icon of the splash screen
	 */
    @SuppressWarnings("unused")
    protected String getSplashAppName()	{
		return splashAppName.getText();
	}


	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		setupLayout();
	}


	/**
	 * @param splashAppName
	 *            which shall be displayed below the app icon on the splash screen
	 */
    @SuppressWarnings("unused")
	protected void setSplashAppName(final String splashAppName) {
		this.splashAppName.setText(splashAppName);
	}


	private void setupLayout() {
        View splashAppIcon;
        Display display;
		final PDETextView splashAppName;
        Typeface typeface;

        // remember
        typeface = PDEFontHelpers.getNormal().getTypeface();
        splashAppName = (PDETextView) findViewById(R.id.splash_activity_app_name);
        this.splashAppName = splashAppName;

        // get view and display
        splashAppIcon = findViewById(R.id.splash_activity_app_icon);
        display = PDEUtils.getDisplay(this);

        // align splash activity icon to golden ratio
        if(splashAppIcon != null && display != null) {
            de.telekom.pde.codelibrary.ui.helpers.PDEUtils.setGoldenRatioTo(splashAppIcon, display);
        }

        // set view font
        if (splashAppName != null && typeface != null) {
            PDEFontHelpers.setViewFontTo(splashAppName, typeface);
        }
	}
}
