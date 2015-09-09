package wsu_airpact_project.airpact_demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Agent1729 on 9/7/2015.
 */

public class NewsTabFragment extends TabActivity.TabFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_news_tab, container, false);
		return rootView;
	}
}
