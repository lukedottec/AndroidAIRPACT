package wsu_airpact_project.airpact_demo;

import android.util.Log;

import java.util.Calendar;

/**
 ** Created by Agent1729 on 4/18/2015.
 */
public class Globals
{
	public static String DEBUG_TAG = "Globals";

	public static SiteList siteList = new SiteList();

	public static boolean haveLocation = false;
	//public static MyActivity myActivity;
	public static TabActivity tabActivity;
	//public static TabActivity.TabFragment myFragment;

	public static double lastLatitude = 0.0;
	public static double lastLongitude = 0.0;

	public static String defaultOverlay = "Ozone";
	public static String useMethod = "AP";

	private static Calendar timeOriginal;

	public static void parseFile(String data)
	{
		int i=0;
		try
		{
			//defaultOverlay
			String dO = "";
			while(data.charAt(i)!='\r') { dO=dO.concat(""+data.charAt(i++)); }
			i+=2;

			//useMethod
			String uM = "";
			while(data.charAt(i)!='\r') { uM=uM.concat(""+data.charAt(i++)); }
			i+=2;

			//Latitude
			String lat = "";
			while(data.charAt(i)!='\r') { lat=lat.concat(""+data.charAt(i++)); }
			i+=2;

			//Longitude
			String lon = "";
			while(data.charAt(i)!='\r') { lon=lon.concat(""+data.charAt(i++)); }
			i+=2;

			//Sites
			while(data.charAt(i)!='\r') { i++; }	i+=2;		//"SITELIST:"
			//String read = "";
			while(data.charAt(i)!=':')
			{
				String siteName = "";
				while(data.charAt(i)!=',') { siteName=siteName.concat(""+data.charAt(i++)); }
				i+=1;
				String aqsid = "";
				while(data.charAt(i)!=',') { aqsid=aqsid.concat(""+data.charAt(i++)); }
				i+=1;
				String siteLat = "";
				while(data.charAt(i)!=',') { siteLat=siteLat.concat(""+data.charAt(i++)); }
				i+=1;
				String siteLon = "";
				while(data.charAt(i)!=',') { siteLon=siteLon.concat(""+data.charAt(i++)); }
				i+=1;
				String hasOzoneStr = "";
				while(data.charAt(i)!=',') { hasOzoneStr=hasOzoneStr.concat(""+data.charAt(i++)); }
				i+=1;
				String hasPM25Str = "";
				while(data.charAt(i)!='\r') { hasPM25Str=hasPM25Str.concat(""+data.charAt(i++)); }
				i+=2;
				float sLat;
				float sLon;
				boolean hasOzone;
				boolean hasPM25;
				try { sLat=Float.parseFloat(siteLat); } catch(NumberFormatException e) { sLat=0; }
				try { sLon=Float.parseFloat(siteLon); } catch(NumberFormatException e) { sLon=0; }
				try { hasOzone=Boolean.parseBoolean(hasOzoneStr); } catch(Exception e) { hasOzone=true; }
				try { hasPM25=Boolean.parseBoolean(hasPM25Str); } catch(Exception e) { hasPM25=true; }
				siteList.addSite(siteName, aqsid, sLat, sLon, hasOzone, hasPM25);
				Log.d("SaveLoad", "Added site "+siteName+"at "+sLat+","+sLon+","+hasOzone+","+hasPM25);
			}
			while(data.charAt(i)!='\r') { i++; }	i+=2;		//":ENDSITELIST"

			defaultOverlay=dO;
			useMethod=uM;
			Log.d("SaveLoad", "Got \""+uM+"\" for uM");
			try { lastLatitude=Float.parseFloat(lat); } catch(NumberFormatException e) { lastLatitude=0; }
			try { lastLongitude=Float.parseFloat(lon); } catch(NumberFormatException e) { lastLongitude=0; }

			Log.d("SaveLoad", "Loaded, including sites...");

			//siteList.parseSites(data, 5);
			//Log.d(DEBUG_TAG, "Done parsing globals...");
		}
		catch(StringIndexOutOfBoundsException e)
		{
			Log.d(DEBUG_TAG, "index out of bounds at "+i);
		}
	}

	public static void setTimeOriginal()
	{
		timeOriginal = Calendar.getInstance();
		//timeOriginal.add(Calendar.DAY_OF_YEAR, -2);
		//timeOriginal.add(Calendar.HOUR_OF_DAY, -12);
	}
	public static Calendar getTimeOriginal() { return (Calendar)timeOriginal.clone(); }
}
