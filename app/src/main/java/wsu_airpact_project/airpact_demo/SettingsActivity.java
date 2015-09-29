package wsu_airpact_project.airpact_demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;


public class SettingsActivity extends ActionBarActivity {

	CheckBox s1;
	CheckBox s2;
	CheckBox s3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		s1 = (CheckBox)findViewById(R.id.checkBox);
		s2 = (CheckBox)findViewById(R.id.checkBox2);
		s3 = (CheckBox)findViewById(R.id.checkBox3);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		//getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
		getSupportActionBar().setDisplayOptions(0, android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

		s1.setChecked(Globals.setting1);
		s2.setChecked(Globals.setting2);
		s3.setChecked(Globals.setting3);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		Globals.setting1 = s1.isChecked();
		Globals.setting2 = s2.isChecked();
		Globals.setting3 = s3.isChecked();

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
