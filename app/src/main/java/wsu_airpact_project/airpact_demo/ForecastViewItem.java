package wsu_airpact_project.airpact_demo;

/**
 *  Created by Agent1729 on 11/12/2015.
 */
public class ForecastViewItem
{
	public String hour;
	public String aqi;
	public String ozone;
	public String pm25;
	public int clr;
	public Boolean isHeader;

	public ForecastViewItem(String _hour, String _aqi, String _ozone, String _pm25, int _clr, Boolean _isHeader)
	{
		hour = _hour;
		aqi = _aqi;
		ozone = _ozone;
		pm25 = _pm25;
		clr = _clr;
		isHeader = _isHeader;
	}
}
