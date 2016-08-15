package wsu_airpact_project.airpact_demo;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Agent1729 on 9/7/2015.
 */

public class HelpTabFragment extends TabActivity.TabFragment
{
	TextView textViewWebLink;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_help_tab, container, false);

		textViewWebLink = (TextView)rootView.findViewById(R.id.textViewWebLink);
		textViewWebLink.setPaintFlags(textViewWebLink.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

		return rootView;
	}
}
