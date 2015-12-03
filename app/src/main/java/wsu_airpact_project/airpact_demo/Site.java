package wsu_airpact_project.airpact_demo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 ** Created by Agent1729 on 4/8/2015.
 */
public class Site implements Comparable<Site>
{
	public String myDate = null;

	public String SiteName;		//25
	public String Name;			//25
	public String AQSID;		//9
	public double Latitude;		//8.4
	public double Longitude;	//8.4
	public double distance=1000;//
	public float OZONEavg_ap = -1;	//
	public float OZONEavg_an = -1;	//
	public float PM25avg_ap = -1;	//
	public float PM25avg_an = -1;	//
	public int hourUsed;
	public float[] OZONE8hr_ap;
	public float[] OZONE8hr_an;
	public float[] PM258hr_ap;
	public float[] PM258hr_an;
	public boolean hasOzone;
	public boolean hasPM25;
	//private static int startingHour = -12;
	private int hoursToRecord = 36;
//	public Time lastUpdate;
	//public int GMToff;			//smallint
	//public smalldatetime GMT;	//smalldatetime
	public String data;
//	private String dateString;
//	private String lastDateO3ap;

	private Marker marker = null;

	private static final String DEBUG_TAG = "SiteTag";
//	private static final String DEBUG_TAG2 = "APReading";

	//private TextView TVO3;
	//private TextView TVPM25;
	//private TextView TVSite;

	public Site(String _name, String _aqsid, double _lat, double _long, boolean _hasOzone, boolean _hasPM25)
	{
		Name=_name;
		AQSID=_aqsid;
		Latitude=_lat;
		Longitude=_long;
		OZONEavg_ap=-1;
		OZONEavg_an=-1;
		PM25avg_ap=-1;
		PM25avg_an=-1;
		hourUsed = -100;
		OZONE8hr_ap = new float[hoursToRecord]; for(int i=0; i<hoursToRecord; i++) OZONE8hr_ap[i]=-1;
		OZONE8hr_an = new float[hoursToRecord]; for(int i=0; i<hoursToRecord; i++) OZONE8hr_an[i]=-1;
		PM258hr_ap = new float[hoursToRecord]; for(int i=0; i<hoursToRecord; i++) PM258hr_ap[i]=-1;
		PM258hr_an = new float[hoursToRecord]; for(int i=0; i<hoursToRecord; i++) PM258hr_an[i]=-1;
		hasOzone = _hasOzone;
		hasPM25 = _hasPM25;
		//lastUpdate=Time.zero;

		SiteName=Name.replace('+', ' ');

		//TVO3=null;
		//TVPM25=null;
	}

	public boolean hasValues() { return OZONEavg_ap!=-1; }

	@Override
	public int compareTo(@NonNull Site s2)
	{
		return (int)(this.distance-s2.distance);
	}

	public void updatePin(ConnectivityManager connService)//, TextView tvO3, TextView tvpm25, TextView tvsite)
	{
		//TVO3=tvO3;
		//TVPM25=tvpm25;
		//TVSite=tvsite;

		//String stringURL="http://lar.wsu.edu/airpact/AP4_mobile/default.aspx?aqsid="+AQSID+"&format=csv";
		String stringURL="http://www.aeolus.wsu.edu:3838/mobile_data/tmp/"+AQSID+".csv";
		NetworkInfo networkInfo = connService.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())
		{
			Log.d(DEBUG_TAG, "Downloading: "+Name);
			DownloadWebpageTask dwt = new DownloadWebpageTask();
			dwt.setParent(this);
			dwt.setType("updatePin");
			dwt.execute(stringURL);
			//new DownloadWebpageTask().setType("updatePin").execute(stringURL);
		}
		else
		{
			data = "No network connection available.";
			parseData(data);
		}
	}

	public void getLatestData(ConnectivityManager connService)//, TextView tvO3, TextView tvpm25, TextView tvsite)
	{
		//TVO3=tvO3;
		//TVPM25=tvpm25;
		//TVSite=tvsite;

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

	public void parseData(String d) { parseData(d, Globals.getTimeOriginal()); }
	public void parseData(String d, Calendar current)
	{
		int startingHour = Globals.tabActivity.startingHour;
		data=d;
		int i=0;
		OZONEavg_ap=-1;
		OZONEavg_an=-1;
		PM25avg_ap=-1;
		PM25avg_an=-1;
		hourUsed = -100;
		//Calendar current = Globals.getTimeOriginal();
		current.set(Calendar.MINUTE, 0);
		current.set(Calendar.SECOND, 0);
		current.set(Calendar.MILLISECOND, 0);

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

				//AQSID
				String aqsid = "";
				while(d.charAt(i)!=',') { aqsid=aqsid.concat(""+d.charAt(i++)); }
				i++;

				/*//SiteName
				String siteName = "";
				while(d.charAt(i)!=',') { siteName=siteName.concat(""+d.charAt(i++)); }
				i++;*/

				//OZONEavg_ap
				float o3_ap;
				String ozoneavg_ap = "";
				while(d.charAt(i)!=',') { ozoneavg_ap=ozoneavg_ap.concat(""+d.charAt(i++)); }
				i++;

				//PM25avg_ap
				float pm_ap;
				String pm25avg_ap = "";
				while(d.charAt(i)!=',') { pm25avg_ap=pm25avg_ap.concat(""+d.charAt(i++)); }
				i++;

				//OZONEavg_an
				float o3_an;
				String ozoneavg_an = "";
				while(d.charAt(i)!=',') { ozoneavg_an=ozoneavg_an.concat(""+d.charAt(i++)); }
				i++;

				//PM25avg_an
				float pm_an;
				String pm25avg_an = "";
				while(d.charAt(i)!='\r'&&d.charAt(i)!='\n') { pm25avg_an=pm25avg_an.concat(""+d.charAt(i++)); }
				i++;

				long diff = -999;
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = sdf.parse(dateLocal);
					Calendar c = Globals.getTimeOriginal();
					c.setTimeInMillis(date.getTime());
					Log.d("TimeParse", ""+Name+"   Year = "+c.get(Calendar.YEAR)+" Month = "+c.get(Calendar.MONTH)+" Day = "+c.get(Calendar.DAY_OF_MONTH)+" Hour = "+c.get(Calendar.HOUR_OF_DAY)+" Minute = "+c.get(Calendar.MINUTE)+" Second = "+c.get(Calendar.SECOND)+" MS = "+c.get(Calendar.MILLISECOND));
					Log.d("TimeParse", ""+Name+"   Year = "+current.get(Calendar.YEAR)+" Month = "+current.get(Calendar.MONTH)+" Day = "+current.get(Calendar.DAY_OF_MONTH)+" Hour = "+current.get(Calendar.HOUR_OF_DAY)+" Minute = "+current.get(Calendar.MINUTE)+" Second = "+current.get(Calendar.SECOND)+" MS = "+current.get(Calendar.MILLISECOND));
					diff = c.getTimeInMillis()-current.getTimeInMillis();
					diff/=1000*60*60;
					Log.d("TimeParse", "     +"+diff);
				}
				catch(ParseException e)
				{
					Log.d("TimeParse", "FAILED TO PARSE \""+dateLocal+"\"");
				}

				try { o3_ap=Float.parseFloat(ozoneavg_ap); } catch(NumberFormatException e) { o3_ap=-1; }
				try { o3_an=Float.parseFloat(ozoneavg_an); } catch(NumberFormatException e) { o3_an=-1; }
				try { pm_ap=Float.parseFloat(pm25avg_ap); } catch(NumberFormatException e) { pm_ap=-1; }
				try { pm_an=Float.parseFloat(pm25avg_an); } catch(NumberFormatException e) { pm_an=-1; }

				if(o3_ap!=-1) { OZONEavg_ap=o3_ap; }
				if(o3_an!=-1) { OZONEavg_an=o3_an; }
				if(pm_ap!=-1) { PM25avg_ap=pm_ap; }
				if(pm_an!=-1) { PM25avg_an=pm_an; }

				if(diff>=startingHour&&diff<startingHour+hoursToRecord)
				{
					if((int)diff-startingHour==0)
					{
						myDate = dateLocal;
					}
					if(o3_ap!=-1) OZONE8hr_ap[(int)diff-startingHour]=o3_ap;
					if(o3_an!=-1) OZONE8hr_an[(int)diff-startingHour]=o3_an;
					if(pm_ap!=-1) PM258hr_ap[(int)diff-startingHour]=pm_ap;
					if(pm_an!=-1) PM258hr_an[(int)diff-startingHour]=pm_an;
					Log.d("TimeParse", "     Set array values for hour "+diff);
				}
			}
			if(startingHour<=0)
			{
				if (OZONE8hr_ap[-startingHour] != -1) OZONEavg_ap = OZONE8hr_ap[-startingHour];
				if (OZONE8hr_an[-startingHour] != -1) OZONEavg_an = OZONE8hr_an[-startingHour];
				if (PM258hr_ap[-startingHour] != -1) PM25avg_ap = PM258hr_ap[-startingHour];
				if (PM258hr_an[-startingHour] != -1) PM25avg_an = PM258hr_an[-startingHour];
				hourUsed=0;
			}
		}
		catch(StringIndexOutOfBoundsException e)
		{
		}
		//OZONEavg_ap = Math.round(OZONEavg_ap*10)/10;
		//OZONEavg_an = Math.round(OZONEavg_an*10)/10;
		//PM25avg_ap = Math.round(PM25avg_ap*10)/10;
		//PM25avg_an = Math.round(PM25avg_an*10)/10;

		hasOzone = (OZONEavg_an!=-1);
		hasPM25 = (PM25avg_an!=-1);
	}

	public Marker addSiteMarker(GoogleMap map, float clr)
	{
		if(marker!=null)
			marker.remove();

		if(Globals.tabActivity.pinMode.equals("None")
				||(Globals.tabActivity.pinMode.equals("Ozone")&&!hasOzone)
				||(Globals.tabActivity.pinMode.equals("PM25")&&!hasPM25))
		{
			return null;
		}

		MarkerOptions mo = new MarkerOptions()
				.title(Name)
				.snippet("AN Site")
				.position(new LatLng(Latitude, Longitude))
				.icon(BitmapDescriptorFactory.defaultMarker(clr));
		if(OZONEavg_ap!=0)
		{
			mo.snippet("AQI: Unknown  \r\nOzone: "+OZONEavg_ap+"  \r\nPM2.5: "+PM25avg_ap);
		}
		/*else
		{
			//updateSiteMarker
		}*/
		return marker = map.addMarker(mo);
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
		Globals.tabActivity.setMainLabels(this);
	}

	public int getAQI(int hour)
	{
		return getAQI(OZONE8hr_ap[hour],PM258hr_ap[hour]);
	}
	public int getAQI(float o3_toUse, float pm25_toUse)
	{
		if(!hasValues()) return -1;
		if(o3_toUse==0&&pm25_toUse==0)
		{
			o3_toUse=OZONEavg_ap;
			pm25_toUse=PM25avg_ap;
		}
		if(o3_toUse==-1&&pm25_toUse==-1)
			return 0;
		int aqi = -1;

		//Values for breakpoints taken 10/27 from http://www3.epa.gov/airnow/aqi-technical-assistance-document-dec2013.pdf
		//Uses 8hr, unless AQI=100-300, then use max of both, if AQI>300, use just 1hr
		int aqiO3 = -1;
		int O3 = (int)o3_toUse;
		float I_lo = -1;
		float I_hi = -1;
		float BP_lo = -1;
		float BP_hi = -1;
		if(O3>=0&&O3<=59)
		{
			I_lo=0; I_hi=50;
			BP_lo=0; BP_hi=59;
		}
		else if (O3>=60&&O3<=75)
		{
			I_lo=51; I_hi=100;
			BP_lo=60; BP_hi=75;
		}
		else if (O3>=76&&O3<=95)
		{
			I_lo=101; I_hi=150;
			BP_lo=76; BP_hi=95;
		}
		else if (O3>=96&&O3<=115)
		{
			I_lo=151; I_hi=200;
			BP_lo=96; BP_hi=115;
		}
		else if (O3>=116&&O3<=374)
		{
			I_lo=201; I_hi=300;
			BP_lo=116; BP_hi=374;
		}
		else if (O3>=375)	//Calculate this with 1hr O3 only?
		{
			I_lo=301; I_hi=500;
			BP_lo=375; BP_hi=604;
		}
		else	//SHOULDN'T GET HERE!!!
		{
			Log.d(DEBUG_TAG, "AQI error in values for O3!");
		}
		aqiO3 = (int)(((I_hi-I_lo)/(BP_hi-BP_lo))*(o3_toUse-BP_lo)+I_lo);

		//Values for breakpoints taken 10/27 from http://www3.epa.gov/airquality/particlepollution/2012/decfsstandards.pdf
		//Uses 24hr, unless AQI=100-300, then use max of both, if AQI>300, use just 1hr
		int aqiPM25 = -1;
		int PM25 = (int)pm25_toUse;
		I_lo = -1;
		I_hi = -1;
		BP_lo = -1;
		BP_hi = -1;
		if(PM25>=0&&PM25<=12.0f)
		{
			I_lo=0; I_hi=50;
			BP_lo=0; BP_hi=12.0f;
		}
		else if (PM25>12.0f&&PM25<=35.4f)
		{
			I_lo=51; I_hi=100;
			BP_lo=12.1f; BP_hi=35.4f;
		}
		else if (PM25>35.4f&&PM25<=55.4f)
		{
			I_lo=101; I_hi=150;
			BP_lo=35.5f; BP_hi=55.4f;
		}
		else if (PM25>55.4f&&PM25<=150.4f)
		{
			I_lo=151; I_hi=200;
			BP_lo=55.5f; BP_hi=150.4f;
		}
		else if (PM25>150.4f&&PM25<=250.4f)
		{
			I_lo=201; I_hi=300;
			BP_lo=150.5f; BP_hi=250.4f;
		}
		else if (PM25>250.4f)
		{
			I_lo=301; I_hi=500;
			BP_lo=250.5f; BP_hi=500.4f;
		}
		else	//SHOULDN'T GET HERE!!!
		{
			Log.d(DEBUG_TAG, "AQI error in values for PM25!");
		}
		aqiPM25 = (int)(((I_hi-I_lo)/(BP_hi-BP_lo))*(pm25_toUse-BP_lo)+I_lo);



		aqi=Math.max(aqiO3, aqiPM25);
		return aqi;
	}



	private class DownloadWebpageTask extends AsyncTask<String, Void, String>
	{
		protected Site parent = null;
		protected String type = "None";
		public void setParent(Site s) { parent=s; }
		public void setType(String t) { type=t; }

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
			if(type.equals("None"))
			{
				setDistance(Globals.lastLatitude, Globals.lastLongitude);
				setMainLabels();
			}
			else if(type.equals("updatePin"))
			{
				setDistance(Globals.lastLatitude, Globals.lastLongitude);
				Globals.tabActivity.updateMarker(parent);
			}
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
