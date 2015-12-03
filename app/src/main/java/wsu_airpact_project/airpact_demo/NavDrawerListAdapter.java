package wsu_airpact_project.airpact_demo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *  Created by Agent1729 on 11/12/2015.
 */
public class NavDrawerListAdapter extends BaseAdapter
{
	public Context context;
	public ArrayList<NavDrawerItem> navDrawerItems;

	public NavDrawerListAdapter(Context c, ArrayList<NavDrawerItem> ndi)
	{
		context = c;
		navDrawerItems = ndi;
	}

	@Override
	public int getCount()
	{
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		String type = navDrawerItems.get(position).type;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if(type.equals("Radio"))
				convertView = mInflater.inflate(R.layout.drawer_list_item_radio, null);
			else if(type.equals("Checkbox"))
				convertView = mInflater.inflate(R.layout.drawer_list_item_checkbox, null);
			else
				convertView = mInflater.inflate(R.layout.drawer_list_item_header, null);
		}

		if(!type.equals("Header"))
		{
			Button button = (Button) convertView.findViewById(R.id.navbutton);
			button.setText(navDrawerItems.get(position).text);
			Globals.tabActivity.setDrawerItemButton(position, button);
		}
		else
		{
			TextView txtTitle = (TextView) convertView.findViewById(R.id.navtitle);
			txtTitle.setText(navDrawerItems.get(position).text);
		}

		return convertView;
	}
}
