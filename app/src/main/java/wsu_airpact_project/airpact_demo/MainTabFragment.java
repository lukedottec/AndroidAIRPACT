package wsu_airpact_project.airpact_demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

/**
 * Created by Agent1729 on 9/7/2015.
 */

public class MainTabFragment extends TabActivity.TabFragment
{
	protected Spinner dropDown;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_main_tab, container, false);
		dropDown = (Spinner)rootView.findViewById(R.id.spinnerinmaintab);
		return rootView;
	}
}
