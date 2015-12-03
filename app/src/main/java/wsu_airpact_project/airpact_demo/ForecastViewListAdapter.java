package wsu_airpact_project.airpact_demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *  Created by Agent1729 on 11/12/2015.
 */
public class ForecastViewListAdapter extends BaseAdapter
{
	public Context context;
	public ArrayList<ForecastViewItem> forecastViewItems;
	public ArrayList<View> convertViews;

	public ForecastViewListAdapter(Context c, ArrayList<ForecastViewItem> fvi)
	{
		context = c;
		forecastViewItems = fvi;
		convertViews = new ArrayList<>();
		for(int i=0; i<fvi.size(); i++)
			convertViews.add(null);
	}

	@Override
	public int getCount()
	{
		return forecastViewItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		return forecastViewItems.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView2, ViewGroup parent)
	{
		String hour = forecastViewItems.get(position).hour;
		String aqi = forecastViewItems.get(position).aqi;
		String ozone = forecastViewItems.get(position).ozone;
		String pm25 = forecastViewItems.get(position).pm25;
		Boolean isHeader = forecastViewItems.get(position).isHeader;
		int clr = forecastViewItems.get(position).clr;
		if (convertViews.get(position) == null) {
			//LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if(isHeader)
				//convertViews.set(position, mInflater.inflate(R.layout.forecast_view_header, null));
				convertViews.set(position, View.inflate(context, R.layout.forecast_view_header, null));
			else
				//convertViews.set(position, mInflater.inflate(R.layout.forecast_view_item, null));
				convertViews.set(position, View.inflate(context, R.layout.forecast_view_item, null));
		}

		if(!isHeader)
		{
			TextView txtHour = (TextView) convertViews.get(position).findViewById(R.id.textViewForecastHour);
			TextView txtAQI = (TextView) convertViews.get(position).findViewById(R.id.textViewForecastAQI);
			TextView txtOzone = (TextView) convertViews.get(position).findViewById(R.id.textViewForecastOzone);
			TextView txtPM25 = (TextView) convertViews.get(position).findViewById(R.id.textViewForecastPM25);
			LinearLayout ll = (LinearLayout) convertViews.get(position).findViewById(R.id.LayoutForecastItem);

			txtHour.setText(hour);
			txtAQI.setText(aqi);
			txtOzone.setText(ozone);
			txtPM25.setText(pm25);
			ll.setBackgroundColor(clr);
		}
		else
		{
			TextView txtDay = (TextView) convertViews.get(position).findViewById(R.id.textViewForecastDay);
			TextView txtDate = (TextView) convertViews.get(position).findViewById(R.id.textViewForecastDate);

			txtDay.setText(hour);
			txtDate.setText(aqi);
		}

		return convertViews.get(position);
	}
}
