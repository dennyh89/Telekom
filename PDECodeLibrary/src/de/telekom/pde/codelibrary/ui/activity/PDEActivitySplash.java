package de.telekom.pde.codelibrary.ui.activity;


import android.os.Bundle;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.elements.wrapper.PDELayerTextView;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;


public class PDEActivitySplash extends PDESherlockActivity
{
	private PDELayerTextView	splashAppName;


	/**
	 * @return appName which is displayed below the icon of the splash screen
	 */
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
	protected void setSplashAppName(final String splashAppName) {
		this.splashAppName.setText(splashAppName);
	}


	private void setupLayout() {

		final PDELayerTextView splashAppName = (PDELayerTextView) findViewById(R.id.splash_activity_app_name);
        // remember
        this.splashAppName = splashAppName;

        // align splash activity icon to golden ratio
		de.telekom.pde.codelibrary.ui.helpers.PDEUtils.setGoldenRatioTo(findViewById(R.id.splash_activity_app_icon), PDEUtils.getDisplay(this));

		PDEFontHelpers.setViewFontTo(splashAppName, PDEFontHelpers.getTeleGroteskNormal(this).getTypeface());

	}
}
