package wsu_airpact_project.airpact_demo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 ** Created by Agent1729 on 4/18/2015.
 */
public class SiteList
{
	private static final String DEBUG_TAG = "SiteListClassTag";
	public ArrayList<Site> sites;
	//public TimeDate lastUpdated;

	private GoogleMap map;
	private Spinner spinner;
	private MyActivity activity;

	public SiteList()
	{
		sites = new ArrayList<>();
	}

	public void addToMap(GoogleMap m, ConnectivityManager connService)
	{
		map = m;
		checkSites("addToMap", connService);
	}

	//Call with checkSites((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
	public void checkSites(String type, ConnectivityManager connService)
	{
//		if(TimeDate.now-lastUpdated>(days)30)
//		{
		updateSites(type, connService);
//		}
	}

	public void updateSites(String type, ConnectivityManager connService)
	{

		String stringURL="http://lar.wsu.edu/airpact/AP4_mobile/monitors.aspx";
		//ConnectivityManager connService = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connService.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())
		{
			if(type.equals("update")) new UpdateSitesTask().execute(stringURL);
			else if(type.equals("addToMap")) new UpdateAddToMapTask().execute(stringURL);
			else if(type.equals("setDropdown")) new UpdateSetDropdownTask().execute(stringURL);
		}
		else
		{
			Log.d(DEBUG_TAG, "Unable to fetch sites...");
		}
	}

	public void setDropdown(Spinner s, MyActivity a, ConnectivityManager connService)
	{
		spinner = s;
		activity = a;
		checkSites("setDropdown", connService);
	}

	public Site getClosest(double lat, double lon)
	{
		Site closest = null;
		double closestDist = 99999;
		double dist;

		for(int i=0; i<sites.size(); i++)
		{
			if((dist=sites.get(i).getDistanceToLL(lat, lon))<closestDist)
			{
				closest=sites.get(i);
				closestDist=dist;
			}
		}

		return closest;
	}





	private void parseSites(String data, int preceedingLines)
	{
		//179 sites?
		int i=0;
		int siteNum=0;

		//Ignore first 11 lines, newlines are formatted /r/n
		String firstLine = "";
		for(int j=0; j<preceedingLines; j++) { while(data.charAt(i)!='\n') { firstLine=firstLine.concat("" + data.charAt(i++)); } i++; }

		Log.d(DEBUG_TAG, "Starting site parsing");
		try
		{
			while(i<data.length())
			{
				if(data.charAt(i)=='\r'||data.charAt(i)=='\n') break;

				//AQSID
				String aqsid = "";
				while(data.charAt(i)!=',') { aqsid=aqsid.concat(""+data.charAt(i++)); }
				i+=2;	//", "

				//SiteName
				String siteName = "";
				while(data.charAt(i)!=',') { siteName=siteName.concat(""+data.charAt(i++)); }
				i+=2;	//", "

				//State
				String state = "";
				while(data.charAt(i)!=',') { state=state.concat(""+data.charAt(i++)); }
				i+=2;	//", "

				//Region
				String region = "";
				while(data.charAt(i)!=',') { region=region.concat(""+data.charAt(i++)); }
				i+=2;	//", "

				//Latitude
				float latitude;
				String lat = "";
				while(data.charAt(i)!=',') { lat=lat.concat(""+data.charAt(i++)); }
				i+=2;	//", "

				//Longitude
				float longitude;
				String lon = "";
				while(data.charAt(i)!=',') { lon=lon.concat(""+data.charAt(i++)); }
				i+=2;	//", "

				//GMToff
				String gmtoff = "";
				while(data.charAt(i)!=',') { gmtoff=gmtoff.concat(""+data.charAt(i++)); }
				i+=2;	//", "

				//GMToffset
				String gmtoffset = "";
				while(data.charAt(i)!='<') { gmtoffset=gmtoffset.concat(""+data.charAt(i++)); }
				while(data.charAt(i)!='>') {  i++; } i++;

				try { latitude=Float.parseFloat(lat); } catch(NumberFormatException e) { latitude=0; }
				try { longitude=Float.parseFloat(lon); } catch(NumberFormatException e) { longitude=0; }

				sites.add(new Site(siteName, aqsid, latitude, longitude));
				siteNum++;
				Log.d(DEBUG_TAG, "Parsed site "+siteNum+": "+siteName);
			}
			Log.d(DEBUG_TAG, "Done parsing sites...");
		}
		catch(StringIndexOutOfBoundsException e)
		{
			Log.d(DEBUG_TAG, "Done parsing sites, index out of bounds at "+i);
		}
	}

	private class UpdateSitesTask extends AsyncTask<String, Void, String>
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
			parseSites(result, 11);
		}
	}

	private class UpdateAddToMapTask extends AsyncTask<String, Void, String>
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
			parseSites(result, 11);
			for (int i = 0; i < sites.size(); i++)
				sites.get(i).addSiteMarker(map);
			map = null;
		}
	}

	private class UpdateSetDropdownTask extends AsyncTask<String, Void, String>
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
			parseSites(result, 11);
			String[] items = new String[sites.size()];
			for (int i = 0; i < sites.size(); i++)
				items[i]=sites.get(i).Name;
			ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, items);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(activity);
			spinner = null;
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
		String result;
		Scanner s = new Scanner(stream).useDelimiter("\\A");
		if(s.hasNext()) { result=s.next(); }
		else { result = ""; }

		return result;
	}
}
