package wsu_airpact_project.airpact_demo;

import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

/**
 *   Created by Agent1729 on 11/1/2015.
 */
public class TabActivityTest extends ActivityInstrumentationTestCase2<TabActivity>
{
	private TabActivity tabActivity;
	//private EditText editText;

	public TabActivityTest()
	{
		super(TabActivity.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		tabActivity = getActivity();
		//editText = (EditText) tabActivity.findViewById(R.id.editText);
	}
/*
	public void testPreconditions()
	{
		assertNotNull("tabActivity is null", tabActivity);
		assertNotNull("editText is null", editText);
	}//*/
/*
	public void testGetClosestSite_FromLatLong1()
	{
		double lat = 46.731367;
		double lon = -117.177109;
		SiteList sl = Globals.siteList;
		Site closest = sl.getClosest(lat, lon);
		assertNotNull("Site was null!", closest);
		assertEquals("Wrong site name:", "Pullman-Dexter Ave", closest.Name);
	}//*/
/*
	public void testGetClosestSite_FromLatLong2()
	{
		double lat = 47.024892;
		double lon = -120.550386;
		SiteList sl = Globals.siteList;
		Site closest = sl.getClosest(lat, lon);
		assertNotNull("Site was null!", closest);
		assertEquals("Wrong site name:", "Ellensburg-Ruby St", closest.Name);
	}//*/
/*
	public void testGetClosestSite_FromLatLong3()
	{
		double lat = 48.315640;
		double lon = -120.675037;
		SiteList sl = Globals.siteList;
		Site closest = sl.getClosest(lat, lon);
		assertNotNull("Site was null!", closest);
		assertEquals("Wrong site name:", "Winthrop-Chewuch Rd", closest.Name);
	}//*/
/*
	public void testGetClosestSite_FromLatLong4()
	{
		double lat = 0;
		double lon = 0;
		SiteList sl = Globals.siteList;
		Site closest = sl.getClosest(lat, lon);
		assertNotNull("Site was null!", closest);
		assertEquals("Wrong site name:", "Great Falls Overlook Park", closest.Name);
	}//*/
/*
	public void testGetClosestSite_FromName1()
	{
		String cityName = "Boise, ID";
		Site s = tabActivity.findCitynamesSite(cityName);
		assertNotNull("Site was null!", s);
		assertEquals("Wrong site name:", "White Pine", s.Name);
	}//*/
/*
	public void testGetClosestSite_FromName2()
	{
		String cityName = "Vancouver, WA";
		Site s = tabActivity.findCitynamesSite(cityName);
		assertNotNull("Site was null!", s);
		assertEquals("Wrong site name:", "Vancouver-NE 84th Ave", s.Name);
	}//*/
/*
	public void testGetClosestSite_FromName3()
	{
		String cityName = "Othello, WA";
		Site s = tabActivity.findCitynamesSite(cityName);
		assertNotNull("Site was null!", s);
		assertEquals("Wrong site name:", "Mesa-Pepoit Way", s.Name);
	}//*/
/*
	public void testGetClosestSite_FromName4()
	{
		String cityName = "San Francisco, CA";
		Site s = tabActivity.findCitynamesSite(cityName);
		assertNotNull("Site was null!", s);
		assertEquals("Wrong site name:", "Tuscan Butte (Seasonal)", s.Name);
	}//*/

/*
	public void testTabActivity_EditTextLabel()
	{
		final String expected = "Boise, ID";
		final String actual = editText.getText().toString();
		assertEquals("WRONG TEXT!!", expected, actual);
	}//*/

	public void testSite_ParseData1()
	{
		Site site = new Site("SiteTest", "123456789", 2, 3, true, true);

		String data = "\"DateTime\",\"AQSID\",\"O3_mod\",\"PM2.5_mod\",\"O3_obs\",\"PM2.5_obs\"\n" +
				"2015-10-19 00:00:00,\"000100119\",0,44.8,11,NA\n" +
				"2015-10-19 01:00:00,\"000100119\",0.3,38.9,11,NA\n" +
				"2015-10-19 02:00:00,\"000100119\",2.7,32.6,7,NA\n" +
				"2015-10-19 03:00:00,\"000100119\",5.2,28.7,6,NA\n" +
				"2015-10-19 04:00:00,\"000100119\",5.1,32.6,4,NA\n" +
				"2015-10-19 05:00:00,\"000100119\",2.7,34.7,1,NA\n" +
				"2015-10-19 06:00:00,\"000100119\",0.8,35.9,2,NA\n" +
				"2015-10-19 07:00:00,\"000100119\",0.6,42.6,4,NA\n" +
				"2015-10-19 08:00:00,\"000100119\",1.1,44.4,4,NA\n" +
				"2015-10-19 09:00:00,\"000100119\",2,35.6,2,NA\n" +
				"2015-10-19 10:00:00,\"000100119\",2.8,16.8,3,NA\n" +
				"2015-10-19 11:00:00,\"000100119\",2.5,12.4,2,NA\n" +
				"2015-10-19 12:00:00,\"000100119\",1.6,17.4,1,NA\n" +
				"2015-10-19 13:00:00,\"000100119\",0.9,14,4,NA\n" +
				"2015-10-19 14:00:00,\"000100119\",1.1,13.1,9,NA\n" +
				"2015-10-19 15:00:00,\"000100119\",1,14.3,10,NA\n" +
				"2015-10-19 16:00:00,\"000100119\",0.9,16,13,NA\n" +
				"2015-10-19 17:00:00,\"000100119\",0.9,14,10,NA\n" +
				"2015-10-19 18:00:00,\"000100119\",1.5,13.1,20,NA\n" +
				"2015-10-19 19:00:00,\"000100119\",2.7,12.2,20,NA\n" +
				"2015-10-19 20:00:00,\"000100119\",3.1,12.3,20,NA\n" +
				"2015-10-19 21:00:00,\"000100119\",11.2,7.8,12,NA\n" +
				"2015-10-19 22:00:00,\"000100119\",19.7,6.1,8,NA\n" +
				"2015-10-19 23:00:00,\"000100119\",18.1,8.6,8,NA\n" +
				"2015-10-20 00:00:00,\"000100119\",9.1,17.7,8,NA\n" +
				"2015-10-20 01:00:00,\"000100119\",1.9,23.4,8,NA\n" +
				"2015-10-20 02:00:00,\"000100119\",5.1,22.5,6,NA\n" +
				"2015-10-20 03:00:00,\"000100119\",4.9,22.8,10,NA\n" +
				"2015-10-20 04:00:00,\"000100119\",2.5,23,17,NA\n" +
				"2015-10-20 05:00:00,\"000100119\",3.3,22.5,14,NA\n" +
				"2015-10-20 06:00:00,\"000100119\",4,23,5,NA\n" +
				"2015-10-20 07:00:00,\"000100119\",2.4,24.2,2,NA\n" +
				"2015-10-20 08:00:00,\"000100119\",7,22.1,4,NA\n" +
				"2015-10-20 09:00:00,\"000100119\",10.8,22.1,7,NA\n" +
				"2015-10-20 10:00:00,\"000100119\",11.4,25.3,13,NA\n" +
				"2015-10-20 11:00:00,\"000100119\",10.6,27.6,10,NA\n" +
				"2015-10-20 12:00:00,\"000100119\",10,26.4,9,NA\n" +
				"2015-10-20 13:00:00,\"000100119\",9.8,24,11,NA\n" +
				"2015-10-20 14:00:00,\"000100119\",8.3,22.4,10,NA\n" +
				"2015-10-20 15:00:00,\"000100119\",6.2,20.9,9,NA\n" +
				"2015-10-20 16:00:00,\"000100119\",2.6,22.1,13,NA\n" +
				"2015-10-20 17:00:00,\"000100119\",0.3,26.2,9,NA\n" +
				"2015-10-20 18:00:00,\"000100119\",0,27.9,4,NA\n" +
				"2015-10-20 19:00:00,\"000100119\",0,27.8,1,NA\n" +
				"2015-10-20 20:00:00,\"000100119\",2.9,21.6,0,NA\n" +
				"2015-10-20 21:00:00,\"000100119\",11.9,15.2,1,NA\n" +
				"2015-10-20 22:00:00,\"000100119\",16.2,13.8,0,NA\n" +
				"2015-10-20 23:00:00,\"000100119\",20.1,12.8,1,NA\n" +
				"2015-10-21 00:00:00,\"000100119\",23,12.9,NA,NA\n" +
				"2015-10-21 01:00:00,\"000100119\",25.3,11.7,NA,NA\n" +
				"2015-10-21 02:00:00,\"000100119\",26.1,11.3,NA,NA\n" +
				"2015-10-21 03:00:00,\"000100119\",25,11.4,NA,NA\n" +
				"2015-10-21 04:00:00,\"000100119\",20.7,11.5,NA,NA\n" +
				"2015-10-21 05:00:00,\"000100119\",14.7,12.5,NA,NA\n" +
				"2015-10-21 06:00:00,\"000100119\",9.7,13.3,NA,NA\n" +
				"2015-10-21 07:00:00,\"000100119\",7.4,14.1,NA,NA\n" +
				"2015-10-21 08:00:00,\"000100119\",7.9,15.2,NA,NA\n" +
				"2015-10-21 09:00:00,\"000100119\",7.1,16,NA,NA\n" +
				"2015-10-21 10:00:00,\"000100119\",3.9,14.9,NA,NA\n" +
				"2015-10-21 11:00:00,\"000100119\",4.3,8.3,NA,NA\n" +
				"2015-10-21 12:00:00,\"000100119\",4.5,8.9,NA,NA\n" +
				"2015-10-21 13:00:00,\"000100119\",5.3,10.7,NA,NA\n" +
				"2015-10-21 14:00:00,\"000100119\",13.4,9.4,NA,NA\n" +
				"2015-10-21 15:00:00,\"000100119\",16.2,9.4,NA,NA\n" +
				"2015-10-21 16:00:00,\"000100119\",7.5,12.4,NA,NA\n" +
				"2015-10-21 17:00:00,\"000100119\",0.7,16.9,NA,NA\n" +
				"2015-10-21 18:00:00,\"000100119\",0.4,16.9,NA,NA\n" +
				"2015-10-21 19:00:00,\"000100119\",0.7,15.5,NA,NA\n" +
				"2015-10-21 20:00:00,\"000100119\",3.6,12.9,NA,NA\n" +
				"2015-10-21 21:00:00,\"000100119\",7.8,12.3,NA,NA\n" +
				"2015-10-21 22:00:00,\"000100119\",6.9,14.2,NA,NA\n" +
				"2015-10-21 23:00:00,\"000100119\",8.3,16.8,NA,NA\n";
		Calendar current = Globals.getTimeOriginal();
		current.set(Calendar.HOUR_OF_DAY, 8);
		current.set(Calendar.DAY_OF_MONTH, 20);
		current.set(Calendar.MONTH, 10-1);
		current.set(Calendar.YEAR, 2015);

		site.parseData(data, current);
		String wrongString = "Wrong value at date "+current.get(Calendar.DAY_OF_MONTH)+":"+current.get(Calendar.HOUR_OF_DAY)+":  "+site.myDate;

		//	"2015-10-20 08:00:00,\"000100119\",7,22.1,4,NA\n" +
		assertEquals(wrongString, 7f, site.OZONE8hr_ap[12]);
		assertEquals(wrongString, 22.1f, site.PM258hr_ap[12]);
		assertEquals(wrongString, 4f, site.OZONE8hr_an[12]);
		assertEquals(wrongString, -1f, site.PM258hr_an[12]);

		//"2015-10-20 16:00:00,\"000100119\",2.6,22.1,13,NA\n" +
		assertEquals(wrongString, 2.6f, site.OZONE8hr_ap[20]);
		assertEquals(wrongString, 22.1f, site.PM258hr_ap[20]);
		assertEquals(wrongString, 13f, site.OZONE8hr_an[20]);
		assertEquals(wrongString, -1f, site.PM258hr_an[20]);
	}//*/

	public void testSite_ParseData2()
	{
		Site site = new Site("SiteTest", "123456789", 2, 3, true, true);

		String data = "\"DateTime\",\"AQSID\",\"O3_mod\",\"PM2.5_mod\",\"O3_obs\",\"PM2.5_obs\"\n" +
				"2015-10-19 00:00:00,\"000100119\",0,44.8,11,NA\n" +
				"2015-10-19 01:00:00,\"000100119\",0.3,38.9,11,NA\n" +
				"2015-10-19 02:00:00,\"000100119\",2.7,32.6,7,NA\n" +
				"2015-10-19 03:00:00,\"000100119\",5.2,28.7,6,NA\n" +
				"2015-10-19 04:00:00,\"000100119\",5.1,32.6,4,NA\n" +
				"2015-10-19 05:00:00,\"000100119\",2.7,34.7,1,NA\n" +
				"2015-10-19 06:00:00,\"000100119\",0.8,35.9,2,NA\n" +
				"2015-10-19 07:00:00,\"000100119\",0.6,42.6,4,NA\n" +
				"2015-10-19 08:00:00,\"000100119\",1.1,44.4,4,NA\n" +
				"2015-10-19 09:00:00,\"000100119\",2,35.6,2,NA\n";
		Calendar current = Globals.getTimeOriginal();
		current.set(Calendar.HOUR_OF_DAY, 0);	current.set(Calendar.DAY_OF_MONTH, 19);	current.set(Calendar.MONTH, 10-1);	current.set(Calendar.YEAR, 2015);

		site.parseData(data, current);
		String wrongString = "Wrong value at date "+site.myDate;

		assertEquals(wrongString, 0f, site.OZONE8hr_ap[12]);
		assertEquals(wrongString, 44.8f, site.PM258hr_ap[12]);
		assertEquals(wrongString, 11f, site.OZONE8hr_an[12]);
		assertEquals(wrongString, -1f, site.PM258hr_an[12]);

		assertEquals(wrongString, -1f, site.OZONE8hr_ap[22]);
		assertEquals(wrongString, -1f, site.PM258hr_ap[22]);
		assertEquals(wrongString, -1f, site.OZONE8hr_an[22]);
		assertEquals(wrongString, -1f, site.PM258hr_an[22]);
	}//*/

	public void testSite_ParseData3()
	{
		Site site = new Site("SiteTest", "123456789", 2, 3, true, true);

		String data = "\"DateTime\",\"AQSID\",\"O3_mod\",\"PM2.5_mod\",\"O3_obs\",\"PM2.5_obs\"\n";
		Calendar current = Globals.getTimeOriginal();
		current.set(Calendar.HOUR_OF_DAY, 0);	current.set(Calendar.DAY_OF_MONTH, 19);	current.set(Calendar.MONTH, 10-1);	current.set(Calendar.YEAR, 2015);

		site.parseData(data, current);
		String wrongString = "Wrong value at date "+site.myDate;

		assertEquals(wrongString, -1f, site.OZONE8hr_ap[0]);
		assertEquals(wrongString, -1f, site.PM258hr_ap[0]);
		assertEquals(wrongString, -1f, site.OZONE8hr_an[0]);
		assertEquals(wrongString, -1f, site.PM258hr_an[0]);

		assertEquals(wrongString, -1f, site.OZONE8hr_ap[20]);
		assertEquals(wrongString, -1f, site.PM258hr_ap[20]);
		assertEquals(wrongString, -1f, site.OZONE8hr_an[20]);
		assertEquals(wrongString, -1f, site.PM258hr_an[20]);
	}//*/

	public void testSite_ParseData4()
	{
		Site site = new Site("SiteTest", "123456789", 2, 3, true, true);

		String data = "";
		Calendar current = Globals.getTimeOriginal();
		current.set(Calendar.HOUR_OF_DAY, 0);	current.set(Calendar.DAY_OF_MONTH, 19);	current.set(Calendar.MONTH, 10-1);	current.set(Calendar.YEAR, 2015);

		site.parseData(data, current);
		String wrongString = "Wrong value at date "+site.myDate;

		assertEquals(wrongString, -1f, site.OZONE8hr_ap[0]);
		assertEquals(wrongString, -1f, site.PM258hr_ap[0]);
		assertEquals(wrongString, -1f, site.OZONE8hr_an[0]);
		assertEquals(wrongString, -1f, site.PM258hr_an[0]);

		assertEquals(wrongString, -1f, site.OZONE8hr_ap[20]);
		assertEquals(wrongString, -1f, site.PM258hr_ap[20]);
		assertEquals(wrongString, -1f, site.OZONE8hr_an[20]);
		assertEquals(wrongString, -1f, site.PM258hr_an[20]);
	}//*/

	public void testSite_ParseData5()
	{
		String data = "\"DateTime\",\"AQSID\",\"O3_mod\",\"PM2.5_mod\",\"O3_obs\",\"PM2.5_obs\"\n" +
				"2015-10-19 00:00:00,\"000100119\",0,44.8,11,NA\n" +
				"2015-10-19 01:00:00,\"000100119\",0.3,38.9,11,NA\n" +
				"2015-10-19 02:00:00,\"000100119\",2.7,32.6,7,NA\n" +
				"2015-10-19 03:00:00,\"000100119\",5.2,28.7,6,NA\n" +
				"2015-10-19 04:00:00,\"000100119\",5.1,32.6,4,NA\n" +
				"2015-10-19 05:00:00,\"000100119\",2.7,34.7,1,NA\n" +
				"2015-10-19 06:00:00,\"000100119\",0.8,35.9,2,NA\n" +
				"2015-10-19 07:00:00,\"000100119\",0.6,42.6,4,NA\n" +
				"2015-10-19 08:00:00,\"000100119\",1.1,44.4,4,NA\n" +
				"2015-10-19 09:00:00,\"000100119\",2,35.6,2,NA\n" +
				"2015-10-19 10:00:00,\"000100119\",2.8,16.8,3,NA\n" +
				"2015-10-19 11:00:00,\"000100119\",2.5,12.4,2,NA\n" +
				"2015-10-19 12:00:00,\"000100119\",1.6,17.4,1,NA\n" +
				"2015-10-19 13:00:00,\"000100119\",0.9,14,4,NA\n" +
				"2015-10-19 14:00:00,\"000100119\",1.1,13.1,9,NA\n" +
				"2015-10-19 15:00:00,\"000100119\",1,14.3,10,NA\n" +
				"2015-10-19 16:00:00,\"000100119\",0.9,16,13,NA\n" +
				"2015-10-19 17:00:00,\"000100119\",0.9,14,10,NA\n" +
				"2015-10-19 18:00:00,\"000100119\",1.5,13.1,20,NA\n" +
				"2015-10-19 19:00:00,\"000100119\",2.7,12.2,20,NA\n" +
				"2015-10-19 20:00:00,\"000100119\",3.1,12.3,20,NA\n" +
				"2015-10-19 21:00:00,\"000100119\",11.2,7.8,12,NA\n" +
				"2015-10-19 22:00:00,\"000100119\",19.7,6.1,8,NA\n" +
				"2015-10-19 23:00:00,\"000100119\",18.1,8.6,8,NA\n" +
				"2015-10-20 00:00:00,\"000100119\",9.1,17.7,8,NA\n" +
				"2015-10-20 01:00:00,\"000100119\",1.9,23.4,8,NA\n" +
				"2015-10-20 02:00:00,\"000100119\",5.1,22.5,6,NA\n" +
				"2015-10-20 03:00:00,\"000100119\",4.9,22.8,10,NA\n" +
				"2015-10-20 04:00:00,\"000100119\",2.5,23,17,NA\n" +
				"2015-10-20 05:00:00,\"000100119\",3.3,22.5,14,NA\n" +
				"2015-10-20 06:00:00,\"000100119\",4,23,5,NA\n" +
				"2015-10-20 07:00:00,\"000100119\",2.4,24.2,2,NA\n" +
				"2015-10-20 08:00:00,\"000100119\",7,22.1,4,NA\n" +
				"2015-10-20 09:00:00,\"000100119\",10.8,22.1,7,NA\n" +
				"2015-10-20 10:00:00,\"000100119\",11.4,25.3,13,NA\n" +
				"2015-10-20 11:00:00,\"000100119\",10.6,27.6,10,NA\n" +
				"2015-10-20 12:00:00,\"000100119\",10,26.4,9,NA\n" +
				"2015-10-20 13:00:00,\"000100119\",9.8,24,11,NA\n" +
				"2015-10-20 14:00:00,\"000100119\",8.3,22.4,10,NA\n" +
				"2015-10-20 15:00:00,\"000100119\",6.2,20.9,9,NA\n" +
				"2015-10-20 16:00:00,\"000100119\",2.6,22.1,13,NA\n" +
				"2015-10-20 17:00:00,\"000100119\",0.3,26.2,9,NA\n" +
				"2015-10-20 18:00:00,\"000100119\",0,27.9,4,NA\n" +
				"2015-10-20 19:00:00,\"000100119\",0,27.8,1,NA\n" +
				"2015-10-20 20:00:00,\"000100119\",2.9,21.6,0,NA\n" +
				"2015-10-20 21:00:00,\"000100119\",11.9,15.2,1,NA\n" +
				"2015-10-20 22:00:00,\"000100119\",16.2,13.8,0,NA\n" +
				"2015-10-20 23:00:00,\"000100119\",20.1,12.8,1,NA\n" +
				"2015-10-21 00:00:00,\"000100119\",23,12.9,NA,NA\n" +
				"2015-10-21 01:00:00,\"000100119\",25.3,11.7,NA,NA\n" +
				"2015-10-21 02:00:00,\"000100119\",26.1,11.3,NA,NA\n" +
				"2015-10-21 03:00:00,\"000100119\",25,11.4,NA,NA\n" +
				"2015-10-21 04:00:00,\"000100119\",20.7,11.5,NA,NA\n" +
				"2015-10-21 05:00:00,\"000100119\",14.7,12.5,NA,NA\n" +
				"2015-10-21 06:00:00,\"000100119\",9.7,13.3,NA,NA\n" +
				"2015-10-21 07:00:00,\"000100119\",7.4,14.1,NA,NA\n" +
				"2015-10-21 08:00:00,\"000100119\",7.9,15.2,NA,NA\n" +
				"2015-10-21 09:00:00,\"000100119\",7.1,16,NA,NA\n" +
				"2015-10-21 10:00:00,\"000100119\",3.9,14.9,NA,NA\n" +
				"2015-10-21 11:00:00,\"000100119\",4.3,8.3,NA,NA\n" +
				"2015-10-21 12:00:00,\"000100119\",4.5,8.9,NA,NA\n" +
				"2015-10-21 13:00:00,\"000100119\",5.3,10.7,NA,NA\n" +
				"2015-10-21 14:00:00,\"000100119\",13.4,9.4,NA,NA\n" +
				"2015-10-21 15:00:00,\"000100119\",16.2,9.4,NA,NA\n" +
				"2015-10-21 16:00:00,\"000100119\",7.5,12.4,NA,NA\n" +
				"2015-10-21 17:00:00,\"000100119\",0.7,16.9,NA,NA\n" +
				"2015-10-21 18:00:00,\"000100119\",0.4,16.9,NA,NA\n" +
				"2015-10-21 19:00:00,\"000100119\",0.7,15.5,NA,NA\n" +
				"2015-10-21 20:00:00,\"000100119\",3.6,12.9,NA,NA\n" +
				"2015-10-21 21:00:00,\"000100119\",7.8,12.3,NA,NA\n" +
				"2015-10-21 22:00:00,\"000100119\",6.9,14.2,NA,NA\n" +
				"2015-10-21 23:00:00,\"000100119\",8.3,16.8,NA,NA\n";

		ParseData pd = new ParseData(data, 20, 20, 10, 2015);
		assertEquals("Something was wrong", true, pd.checkValsAtRelativeHour(0, 7f, 22.1f, 4f, -1f));
		assertEquals("Something was wrong", true, pd.checkValsAtRelativeHour(20, 20.7f, 11.5f, -1f, -1f));
		assertEquals("Something was wrong", true, pd.checkAfterVals());
	}//*/
}
