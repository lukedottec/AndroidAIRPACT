package wsu_airpact_project.airpact_demo;

import android.util.Log;

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
			//i+=2;

			setting1=s1.equals("true");
			setting2=s2.equals("true");
			setting3=s3.equals("true");
			try { lastLatitude=Float.parseFloat(lat); } catch(NumberFormatException e) { lastLatitude=0; }
			try { lastLongitude=Float.parseFloat(lon); } catch(NumberFormatException e) { lastLongitude=0; }

			//siteList.parseSites(data, 5);
			//Log.d(DEBUG_TAG, "Done parsing globals...");
		}
		catch(StringIndexOutOfBoundsException e)
		{
			Log.d(DEBUG_TAG, "index out of bounds at "+i);
		}
	}
}
