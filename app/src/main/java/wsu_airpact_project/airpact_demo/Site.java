package wsu_airpact_project.airpact_demo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 ** Created by Agent1729 on 4/8/2015.
 */
public class Site implements Comparable<Site>
{
	public String SiteName;		//25
	public String Name;			//25
	public String AQSID;		//9
	public double Latitude;		//8.4
	public double Longitude;	//8.4
	public double distance=1000;//
	public float OZONEavg_ap;	//
	public float OZONEavg_an;	//
	public float PM25avg_ap;	//
	public float PM25avg_an;	//
//	public Time lastUpdate;
	//public int GMToff;			//smallint
	//public smalldatetime GMT;	//smalldatetime
	public String data;
//	private String dateString;
//	private String lastDateO3ap;
	private static final String DEBUG_TAG = "SiteTag";
//	private static final String DEBUG_TAG2 = "APReading";

	private TextView TVO3;
	private TextView TVPM25;
	private TextView TVSite;

	public Site(String _name, String _aqsid, double _lat, double _long)
	{
		Name=_name;
		AQSID=_aqsid;
		Latitude=_lat;
		Longitude=_long;
		OZONEavg_ap=0;
		OZONEavg_an=0;
		PM25avg_ap=0;
		PM25avg_an=0;
		//lastUpdate=Time.zero;

		SiteName=Name.replace('+', ' ');

		TVO3=null;
		TVPM25=null;
	}

	@Override
	public int compareTo(Site s2)
	{
		return (int)(this.distance-s2.distance);
	}

	public void getLatestData(ConnectivityManager connService, TextView tvO3, TextView tvpm25, TextView tvsite)
	{
		TVO3=tvO3;
		TVPM25=tvpm25;
		TVSite=tvsite;

		//String stringURL="http://lar.wsu.edu/airpact/AP4_mobile/default.aspx?aqsid="+AQSID+"&format=csv";
		String stringURL="http://www.aeolus.wsu.edu:3838/mobile_data/tmp/"+AQSID+".csv";
		NetworkInfo networkInfo = connService.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())
		{
			Log.d(DEBUG_TAG, "Downloading: "+Name);
			new DownloadWebpageTask().execute(stringURL);
		}
		else
		{
			data = "No network connection available.";
			parseData(data);
		}
	}

	public void parseData(String d)
	{
		data=d;
		int i=0;
		OZONEavg_ap=1;

		//Ignore first line, newlines are formatted /r/n
		String firstLine = "";
		try
		{
			while (d.charAt(i) != '\n')
			{
				firstLine = firstLine.concat("" + d.charAt(i++));
			}
		}
		catch (StringIndexOutOfBoundsException e) { Log.d(DEBUG_TAG, "ERROR PARSING FIRST LINE!"); return; }
		i++;

		Log.d(DEBUG_TAG, "Parsing: "+Name);
		try
		{
			while(i<d.length())
			{
				//DateLocal
				String dateLocal = "";
				while(d.charAt(i)!=',') { dateLocal=dateLocal.concat(""+d.charAt(i++)); }
				i++;

				/*//AQSID
				String aqsid = "";
				while(d.charAt(i)!=',') { aqsid=aqsid.concat(""+d.charAt(i++)); }
				i++;*/

				/*//SiteName
				String siteName = "";
				while(d.charAt(i)!=',') { siteName=siteName.concat(""+d.charAt(i++)); }
				i++;*/

				//OZONEavg_ap
				float o3_ap;
				String ozoneavg_ap = "";
				while(d.charAt(i)!=',') { ozoneavg_ap=ozoneavg_ap.concat(""+d.charAt(i++)); }
				i++;

				//OZONEavg_an
				float o3_an;
				String ozoneavg_an = "";
				while(d.charAt(i)!=',') { ozoneavg_an=ozoneavg_an.concat(""+d.charAt(i++)); }
				i++;

				//PM25avg_ap
				float pm_ap;
				String pm25avg_ap = "";
				while(d.charAt(i)!=',') { pm25avg_ap=pm25avg_ap.concat(""+d.charAt(i++)); }
				i++;

				//PM25avg_an
				float pm_an;
				String pm25avg_an = "";
				while(d.charAt(i)!='\r'&&d.charAt(i)!='\n') { pm25avg_an=pm25avg_an.concat(""+d.charAt(i++)); }
				i++;

				try { o3_ap=Float.parseFloat(ozoneavg_ap); } catch(NumberFormatException e) { o3_ap=0; }
				try { o3_an=Float.parseFloat(ozoneavg_an); } catch(NumberFormatException e) { o3_an=0; }
				try { pm_ap=Float.parseFloat(pm25avg_ap); } catch(NumberFormatException e) { pm_ap=0; }
				try { pm_an=Float.parseFloat(pm25avg_an); } catch(NumberFormatException e) { pm_an=0; }

				if(o3_ap!=0) { OZONEavg_ap=o3_ap; }
				if(o3_an!=0) { OZONEavg_an=o3_an; }
				if(pm_ap!=0) { PM25avg_ap=pm_ap; }
				if(pm_an!=0) { PM25avg_an=pm_an; }
			}
		}
		catch(StringIndexOutOfBoundsException e)
		{
		}
		OZONEavg_ap = Math.round(OZONEavg_ap*10)/10;
		OZONEavg_an = Math.round(OZONEavg_an*10)/10;
		PM25avg_ap = Math.round(PM25avg_ap*10)/10;
		PM25avg_an = Math.round(PM25avg_an*10)/10;
	}

	public void addSiteMarker(GoogleMap map, float clr)
	{
		map.addMarker(new MarkerOptions()
				.title(Name)
				.snippet("AN Site")
				.position(new LatLng(Latitude, Longitude))
				.icon(BitmapDescriptorFactory.defaultMarker(clr)));
	}

	public double getDistanceToLL(double lat, double lon)
	{
		double radius = 6371;	//Earth's radius in km
		double angLat = degToRad(Latitude-lat);
		double angLon = degToRad(Longitude-lon);
		double a =	Math.sin(angLat/2) * Math.sin(angLat/2) +
					Math.cos(degToRad(lat)) * Math.cos(degToRad(Latitude)) *
					Math.sin(angLon/2) * Math.sin(angLon/2);
		double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return radius*c;	//Distance in km
	}

	public void setDistance(double lat, double lon)
	{
		distance = getDistanceToLL(lat, lon);
	}

	public static double degToRad(double deg) { return deg*(Math.PI/180.0); }

	public void setMainLabels()
	{
		setDistance(Globals.lastLatitude, Globals.lastLongitude);
		Globals.tabActivity.setMainLabels(this);
	}




	private class DownloadWebpageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			try
			{
				return downloadUrl(urls[0]);
			}
			catch (IOException e)
			{
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		@Override
		protected void onPostExecute(String result)
		{
			data = result;
			parseData(data);
			Log.d(DEBUG_TAG, "Trying to set labels...");
			//if(TVO3!=null) { Log.d(DEBUG_TAG, "Set labels..."+TVO3.getText()); TVO3.setText("Ozone: "+OZONEavg_ap); TVO3=null; }
			//if(TVPM25!=null) { TVPM25.setText("PM2.5: "+PM25avg_ap); TVPM25=null; }
			//if(TVSite!=null) { TVSite.setText("Site: "+Name); TVSite=null; }
			//Globals.tabActivity.setMainLabels(OZONEavg_ap, PM25avg_ap, Name, View.INVISIBLE);
			setMainLabels();
			//Log.d(DEBUG_TAG, "Set labels for site "+Name);
		}
	}

	private String downloadUrl(String myurl) throws IOException
	{
		InputStream is = null;
		try
		{
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);

			//Start query
			conn.connect();
			int response = conn.getResponseCode();	//200 = success
			Log.d(DEBUG_TAG, "The response is: " + response);
			is = conn.getInputStream();

			//Convert inputstream into string
			return readIt(is);
		}
		finally
		{
			if(is!=null)
				is.close();
		}
	}

	public String readIt(InputStream stream) throws IOException
	{
		String result = "";
		Scanner s = new Scanner(stream).useDelimiter("\\A");
		if(s.hasNext()) { result=s.next(); }
		else { result = ""; }

		return result;
	}
}
