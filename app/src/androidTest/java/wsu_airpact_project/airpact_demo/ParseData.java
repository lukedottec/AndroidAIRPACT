package wsu_airpact_project.airpact_demo;

import java.util.Calendar;

/**
 *  Created by Agent1729 on 11/1/2015.
 */
public class ParseData
{
	public Site site;

	public ParseData(String data, int curHour, int curDay, int curMonth, int curYear)
	{
		site = new Site("SiteTest", "123456789", 2, 3, true, true);
		Calendar current = Calendar.getInstance();
		current.set(Calendar.HOUR_OF_DAY, curHour);	current.set(Calendar.DAY_OF_MONTH, curDay);	current.set(Calendar.MONTH, curMonth-1);	current.set(Calendar.YEAR, curYear);
		site.parseData(data, current);
	}

	public boolean checkValsAtRelativeHour(int hour, float o3ap, float pmap, float o3an, float pman)
	{
		//assertEquals(wrongString, -1f, site.OZONE8hr_ap[0]);
		if(o3ap!=site.OZONE8hr_ap[hour]) return false;
		if(pmap!=site.PM258hr_ap[hour]) return false;
		if(o3an!=site.OZONE8hr_an[hour]) return false;
		if(pman!=site.PM258hr_an[hour]) return false;
		return true;
	}
}
