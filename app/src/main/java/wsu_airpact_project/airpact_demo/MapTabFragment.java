package wsu_airpact_project.airpact_demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Agent1729 on 9/7/2015.
 */

public class MapTabFragment extends TabActivity.TabFragment
{
	View rv;
	boolean hasInflated=false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		if(!hasInflated)
		{
			View rootView = inflater.inflate(R.layout.fragment_map_tab, container, false);
			hasInflated=true;
			rv = rootView;
		}
		return rv;
	}
}
