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
	public static MyActivity myActivity;
	public static TabActivity tabActivity;
	//public static TabActivity.TabFragment myFragment;

	public static double lastLatitude = 0.0;
	public static double lastLongitude = 0.0;
	public static boolean setting1 = false;
	public static boolean setting2 = false;
	public static boolean setting3 = true;

	private static Calendar timeOriginal;

	public static void parseFile(String data)
	{
		int i=0;
		try
		{
			//Setting1
			String s1 = "";
			while(data.charAt(i)!='\r') { s1=s1.concat(""+data.charAt(i++)); }
			i+=2;

			//Setting2
			String s2 = "";
			while(data.charAt(i)!='\r') { s2=s2.concat(""+data.charAt(i++)); }
			i+=2;

			//Setting3
			String s3 = "";
			while(data.charAt(i)!='\r') { s3=s3.concat(""+data.charAt(i++)); }
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

			setting1=s1.equals("true");
			setting2=s2.equals("true");
			setting3=s3.equals("true");
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
