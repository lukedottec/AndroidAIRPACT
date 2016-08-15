package wsu_airpact_project.airpact_demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;


public class SettingsActivity extends ActionBarActivity {

	RadioButton defaultOzone;
	RadioButton defaultPM25;
	RadioButton defaultNone;

	RadioButton useAP;
	RadioButton useAN;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		defaultOzone = (RadioButton)findViewById(R.id.radioButtonOzone);
		defaultPM25 = (RadioButton)findViewById(R.id.radioButtonPM25);
		defaultNone = (RadioButton)findViewById(R.id.radioButtonNone);
		if(Globals.defaultOverlay.equals("Ozone")) defaultOzone.setChecked(true);
		else if(Globals.defaultOverlay.equals("PM25")) defaultPM25.setChecked(true);
		else if(Globals.defaultOverlay.equals("None")) defaultNone.setChecked(true);

		useAP = (RadioButton)findViewById(R.id.radioButtonAP);
		useAN = (RadioButton)findViewById(R.id.radioButtonAN);
		if(Globals.useMethod.equals("AP")) useAP.setChecked(true);
		else if(Globals.useMethod.equals("AN")) useAN.setChecked(true);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		//getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
		getSupportActionBar().setDisplayOptions(0, android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

		if(Globals.defaultOverlay.equals("Ozone")) defaultOzone.setChecked(true);
		else if(Globals.defaultOverlay.equals("PM25")) defaultPM25.setChecked(true);
		else if(Globals.defaultOverlay.equals("None")) defaultNone.setChecked(true);

		if(Globals.useMethod.equals("AP")) useAP.setChecked(true);
		else if(Globals.useMethod.equals("AN")) useAN.setChecked(true);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		if(defaultOzone.isChecked()) Globals.defaultOverlay="Ozone";
		else if(defaultPM25.isChecked()) Globals.defaultOverlay="PM25";
		else if(defaultNone.isChecked()) Globals.defaultOverlay="None";

		if(useAP.isChecked()) Globals.useMethod="AP";
		else if(useAN.isChecked()) Globals.useMethod="AN";

		Globals.tabActivity.save();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
