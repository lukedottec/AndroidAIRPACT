package wsu_airpact_project.airpact_demo;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 *  Created by Agent1729 on 10/5/2015 .
 */
public class MapInfoWindow implements GoogleMap.InfoWindowAdapter
{
	LayoutInflater inflater = null;

	public MapInfoWindow(LayoutInflater li)
	{ inflater = li; }

	public View getInfoWindow(Marker m)
	{
		View view = inflater.inflate(R.layout.info_window, null);
		Site s = Globals.siteList.getByName(m.getTitle());
		if(s==null) return null;
		if(!s.hasValues())
		{
			TextView name = (TextView) view.findViewById(R.id.textViewSitenameInfoWindow);
			name.setText(s.Name);
			TextView aqi = (TextView) view.findViewById(R.id.textViewAQIInfoWindow);
			aqi.setText("AQI: ...");
			TextView o3 = (TextView) view.findViewById(R.id.textViewO3InfoWindow);
			o3.setText("Ozone: ...");
			TextView pm25 = (TextView) view.findViewById(R.id.textViewPM25InfoWindow);
			pm25.setText("PM2.5: ...");
			return view;
		}
		else
		{
			TextView name = (TextView) view.findViewById(R.id.textViewSitenameInfoWindow);
			name.setText(s.Name);
			TextView aqi = (TextView) view.findViewById(R.id.textViewAQIInfoWindow);
			aqi.setText("AQI: " + s.getAQI(0, 0));

			TextView o3 = (TextView) view.findViewById(R.id.textViewO3InfoWindow);
			float o3val = -1;
			if(Globals.useMethod.equals("AP"))		o3val = s.OZONEavg_ap;
			else if(Globals.useMethod.equals("AN"))	o3val = s.OZONEavg_an;
			else									o3val = s.OZONEavg_ap;
			if(o3val>=0)	o3.setText("Ozone: " + o3val);
			else 			o3.setText("Ozone: Unavailable");

			TextView pm25 = (TextView) view.findViewById(R.id.textViewPM25InfoWindow);
			float pm25val = -1;
			if(Globals.useMethod.equals("AP"))		pm25val = s.PM25avg_ap;
			else if(Globals.useMethod.equals("AN"))	pm25val = s.PM25avg_an;
			else									pm25val = s.PM25avg_ap;
			if(pm25val>=0)	pm25.setText("Ozone: " + pm25val);
			else 			pm25.setText("Ozone: Unavailable");

			return view;
		}
	}

	public View getInfoContents(Marker m)
	{
		return null;
	}
}
