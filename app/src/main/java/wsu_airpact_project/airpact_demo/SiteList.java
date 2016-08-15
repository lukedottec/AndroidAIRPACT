package wsu_airpact_project.airpact_demo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 ** Created by Agent1729 on 4/18/2015.
 */
public class SiteList
{
	private static final String DEBUG_TAG = "SiteListClassTag";
	public ArrayList<Site> sites;
	//public TimeDate lastUpdated;

	public GoogleMap map;
	private Spinner spinner;
	//private MyActivity activity;
	//private TabActivity tabActivity;

	public SiteList()
	{
		sites = new ArrayList<>();
	}

	public void addSite(String siteName, String aqsid, float latitude, float longitude, boolean hasOzone, boolean hasPM25)
	{
		Site s = getByName(siteName);
		if(s==null)
		{
			sites.add(new Site(siteName, aqsid, latitude, longitude, hasOzone, hasPM25));

		}
	}

	public boolean addToMap(GoogleMap m, ConnectivityManager connService)
	{
		map = m;
		if(!checkSites("addToMap", connService)) return false;
		return true;
	}

	//Call with checkSites((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
	public boolean checkSites(String type, ConnectivityManager connService)
	{
//		if(TimeDate.now-lastUpdated>(days)30)
//		{
		if(!updateSites(type, connService))
			return false;
//		}
		return true;
	}

	public boolean updateSites(String type, ConnectivityManager connService)
	{
		//String stringURL="http://lar.wsu.edu/airpact/AP4_mobile/monitors.aspx";
		//AP4: String stringURL="http://lar.wsu.edu/R_apps/2015ap4/data/mon2015.csv";
		// Confirmed: updated site lists to AP5
		String stringURL="http://lar.wsu.edu/R_apps/2016ap5/data/mon2016.csv";
		//ConnectivityManager connService = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connService.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())
		{
			if(type.equals("update")) new UpdateSitesTask().execute(stringURL);
			else if(type.equals("addToMap"))
			{
				try
				{
					new UpdateAddToMapTask().execute(stringURL);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					Log.d(DEBUG_TAG, "Error parsing sites for addToMap...");
					//DialogFragment dialog = new GoToLocationSettingsDialog();
					//dialog.show(getFragmentManager(), "turnOnLocation");
					return false;
				}
			}
			else if(type.equals("setDropdown"))
			{
				try
				{
					new UpdateSetDropdownTask().execute(stringURL);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					Log.d(DEBUG_TAG, "Error parsing sites for setDropdown...");
					//DialogFragment dialog = new GoToLocationSettingsDialog();
					//dialog.show(getFragmentManager(), "turnOnLocation");
					return false;
				}
			}
		}
		else
		{
			Log.d(DEBUG_TAG, "Unable to fetch sites...");
			return false;
		}
		return true;
	}

	/*public boolean setDropdown(Spinner s, MyActivity a, ConnectivityManager connService)
	{
		spinner = s;
		activity = a;
		if(!checkSites("setDropdown", connService)) return false;
		return true;
	}*/

	public boolean setDropdown(Spinner s, ConnectivityManager connService)
	{
		spinner = s;
		//tabActivity = a;
		if(!checkSites("setDropdown", connService)) return false;
		return true;
	}

	public Site getByName(String n)
	{
		for(int i=0; i<sites.size(); i++)
			if(sites.get(i).Name.equalsIgnoreCase(n))
				return sites.get(i);
		return null;
	}

	public Site getClosest(double lat, double lon)
	{
		/*
		Site closest = null;
		double closestDist = 99999;
		double dist;
		for(int i=0; i<sites.size(); i++)
		{
			*//*if((dist=sites.get(i).getDistanceToLL(lat, lon))<closestDist)
			{
				closest=sites.get(i);
				closestDist=dist;
			}
			sites.get(i).distance=dist;*//*
			if((dist=sites.get(i).distance)<closestDist)
			{
				closest=sites.get(i);
				closestDist=dist;
			}
		}
		return closest;*/

		setDistances(lat, lon);
		/*for(int i=0; i<sites.size(); i++)
		{
			Log.d("getClosest", "Site "+i+": "+sites.get(i).Name);
		}*/
		if(sites.size()>0)
		{
			Site s = this.sites.get(0);
			Log.d("getClosest", "GetClosest returning: "+s.Name);
			return s;
		}
		return null;
	}

	public void setDistances(double lat, double lon)
	{
		for(int i=0; i<sites.size(); i++)
			sites.get(i).distance = sites.get(i).getDistanceToLL(lat, lon);
		Collections.sort(this.sites);
	}





	public void parseSites(String data, int preceedingLines)
	{
		//179 sites?
		//sites = new ArrayList<>();
		int i=0;
		int siteNum=0;

		//Ignore first 11 lines, newlines are formatted \r\n
		String firstLine = "";
		try
		{
			for (int j = 0; j < preceedingLines; j++)
			{
				while (data.charAt(i) != '\n')
				{
					firstLine = firstLine.concat("" + data.charAt(i++));
				}
				i++;
			}
		}
		catch (StringIndexOutOfBoundsException e)
		{
			Log.d(DEBUG_TAG, "Parse first line failed...");
			//DialogFragment dialog = new CannotConnectDialog();
			//dialog.show(getFragmentManager(), "couldNotConnect");
			return;
		}

		Log.d(DEBUG_TAG, "Starting site parsing");
		try
		{
			Log.d(DEBUG_TAG, "Starting site parsing2");
			while(i<data.length())
			{
				Log.d(DEBUG_TAG, "Starting site parsing at i="+i+", data.length()="+data.length());
				if(data.charAt(i)=='\r'||data.charAt(i)=='\n') { Log.d(DEBUG_TAG, "BREAKING"); break; }

				//AQSID
				String aqsid = "";
				while(data.charAt(i)!=',') { aqsid=aqsid.concat(""+data.charAt(i++)); }
				i++;	//", "

				//SiteName
				String siteName = "";
				while(data.charAt(i)!=',') { siteName=siteName.concat(""+data.charAt(i++)); }
				i++;	//", "

				/*//State
				String state = "";
				while(data.charAt(i)!=',') { state=state.concat(""+data.charAt(i++)); }
				i+=2;	//", "*/

				/*//Region
				String region = "";
				while(data.charAt(i)!=',') { region=region.concat(""+data.charAt(i++)); }
				i+=2;	//", "*/

				//Latitude
				float latitude;
				String lat = "";
				while(data.charAt(i)!=',') { lat=lat.concat(""+data.charAt(i++)); }
				i++;	//", "

				//Longitude
				float longitude;
				String lon = "";
				while(data.charAt(i)!=',') { lon=lon.concat(""+data.charAt(i++)); }
				i++;	//", "

				//GMToff
				String gmtoff = "";
				while(data.charAt(i)!=',') { gmtoff=gmtoff.concat(""+data.charAt(i++)); }
				i++;	//", "

				/*//GMToffset
				String gmtoffset = "";
				while(data.charAt(i)!='<') { gmtoffset=gmtoffset.concat(""+data.charAt(i++)); }
				while(data.charAt(i)!='>') {  i++; } i++;*/
				String gmtoffset = "";
				while(data.charAt(i)!='\n'&&data.charAt(i)!='\r') { gmtoffset=gmtoffset.concat(""+data.charAt(i++)); }

				try { latitude=Float.parseFloat(lat); } catch(NumberFormatException e) { latitude=0; }
				try { longitude=Float.parseFloat(lon); } catch(NumberFormatException e) { longitude=0; }

				addSite(siteName, aqsid, latitude, longitude, true, true);

				siteNum++;
				Log.d(DEBUG_TAG, "Parsed site "+siteNum+": "+siteName);
				while(data.charAt(i)=='\n'||data.charAt(i)=='\r') i++;
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
			//Site closest = getClosest(Globals.lastLatitude, Globals.lastLongitude);
			setDistances(Globals.lastLatitude, Globals.lastLongitude);
			parseSites(result, 11);
			Globals.tabActivity.addSitesToMap(map);
			/*for (int i = 0; i < sites.size(); i++)
			{
				Site s = sites.get(i);
				if(s.Name.equals(closest.Name)) { s.addSiteMarker(map, BitmapDescriptorFactory.HUE_GREEN); }
				else { s.addSiteMarker(map, BitmapDescriptorFactory.HUE_RED); }
			}*/
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
			Log.d(DEBUG_TAG, "The string to parse is: \"" + result + "\"");
			Log.d(DEBUG_TAG, "The string length is: "+result.length());
			parseSites(result, 1);
			Log.d(DEBUG_TAG, "The number of sites is: "+sites.size());
			//Log.d(DEBUG_TAG, "Attempting arrayadapter thingy");
			//if(tabActivity == null) Log.d(DEBUG_TAG, "tabActivity null");

			Globals.tabActivity.populateDropdown(spinner);
			/*String[] items = new String[sites.size()];
			for (int i = 0; i < sites.size(); i++)
				items[i]=sites.get(i).Name;
			ArrayAdapter<String> adapter = new ArrayAdapter<>(tabActivity, android.R.layout.simple_spinner_item, items);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(tabActivity);

			Site closest = Globals.siteList.getClosest(Globals.lastLatitude, Globals.lastLongitude);
			int i=0;
			for(int j=0; j<spinner.getCount(); j++)
				if(spinner.getItemAtPosition(j).toString().equalsIgnoreCase(closest.Name))
				{
					i=j;
					break;
				}
			spinner.setSelection(i);*/
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
