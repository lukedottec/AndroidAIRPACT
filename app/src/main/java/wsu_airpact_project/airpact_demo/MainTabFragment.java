package wsu_airpact_project.airpact_demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 *  Created by Agent1729 on 9/7/2015.
 */

public class MainTabFragment extends TabActivity.TabFragment //implements AdapterView.OnItemSelectedListener
{
	View rv;
	boolean hasInflated=false;
	protected Spinner dropDown;
	protected TextView aqiTextView;
	protected TextView o3TextView;
	protected TextView pm25TextView;
	protected TextView siteTextView;
	protected TextView farTextView;
	protected TextView questionmarkTextView;
	protected TextView useMethodTextView;
	protected Button farButton;
	protected ToggleButton toggleButtonCurrent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		//Log.e("CrashBug", "In MainTabFragment.onCreateView()");
		if(Globals.tabActivity.miw==null)
			Globals.tabActivity.miw = new MapInfoWindow(inflater);

		if(!hasInflated)
		{
			View rootView = inflater.inflate(R.layout.fragment_main_tab, container, false);

			dropDown = (Spinner)rootView.findViewById(R.id.spinnerinmaintab);
			aqiTextView = (TextView)rootView.findViewById(R.id.textViewAQIValueInTab);
			if(aqiTextView==null) { Log.d("WTF", "WTF"); }
			o3TextView = (TextView)rootView.findViewById(R.id.textViewOzoneLabelInTab);
			pm25TextView = (TextView)rootView.findViewById(R.id.textViewPM2_5LabelInTab);
			siteTextView = (TextView)rootView.findViewById(R.id.textViewSiteLabelInTab);
			farTextView = (TextView)rootView.findViewById(R.id.textViewFarInTab);
			questionmarkTextView = (TextView)rootView.findViewById(R.id.textViewHelpButton);
			useMethodTextView = (TextView)rootView.findViewById(R.id.textViewUseMethod);
			farButton = (Button)rootView.findViewById(R.id.buttonTooFarInfo);
			toggleButtonCurrent = (ToggleButton)rootView.findViewById(R.id.toggleButtonCurrent);
			Log.d("MainTabFragmentTag","dropDown set in MainTabFragment");
			if(dropDown==null) Log.d("MainTabFragmentTag","dropDown==null");
			Globals.tabActivity.dropDownIsReady();
			hasInflated=true;
			rv = rootView;
		}
		return rv;

	}

	@Override
	public void onStart()
	{
		super.onStart();
		//Log.e("CrashBug", "In MainTabFragment.onStart()");
		Globals.tabActivity.mainTabFragment = this;
		Globals.tabActivity.dropDownIsReady();
	}

}
