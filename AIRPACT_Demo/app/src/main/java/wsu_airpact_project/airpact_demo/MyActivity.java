package wsu_airpact_project.airpact_demo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


public class MyActivity extends ActionBarActivity  implements
		ConnectionCallbacks, OnConnectionFailedListener, AdapterView.OnItemSelectedListener
{

	private static final String DEBUG_TAG = "MainActivityTag";

	protected static final String TAG = "basic-location-sample";
	protected static String CUR_LAT="wsu_airpact_project.airpact_demo.CUR_LAT";
	protected static String CUR_LON="wsu_airpact_project.airpact_demo.CUR_LON";
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	protected TextView mLatitudeText;
	protected TextView mLongitudeText;
	protected Double latitude;
	protected Double longitude;
	protected Spinner dropDown;

	public static TextView o3TextView;
	public static TextView pm25TextView;
	public static TextView siteTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);

		mLatitudeText = (TextView) findViewById(R.id.textViewLatitudeCoordinate);
		mLongitudeText = (TextView) findViewById(R.id.textViewLongitudeCoordinate);
		latitude=0.0;
		longitude=0.0;

		o3TextView = (TextView) findViewById(R.id.textViewOzoneLabel);
		pm25TextView = (TextView) findViewById(R.id.textViewPM2_5Label);
		siteTextView = (TextView) findViewById(R.id.textViewSiteLabel);
		//Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);
		//pullman.getLatestData((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, siteTextView);

		dropDown = (Spinner)findViewById(R.id.spinner);
		Globals.siteList.setDropdown(dropDown, this, (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		String city = (String)parent.getItemAtPosition(pos);
		Log.d(DEBUG_TAG, "Dropdown selected"+pos+": "+city);
		Site currSite;
		currSite = Globals.siteList.sites.get(pos);
		currSite.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, siteTextView);
	}
	public void onNothingSelected(AdapterView<?> parent) {}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_my, menu);
		return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 		// Handle action bar item clicks here. The action bar will
 		// automatically handle clicks on the Home/Up button, so long
 		// as you specify a parent activity in AndroidManifest.xml.
 		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void openMap(View view)
	{
		Intent intent = new Intent(this, MapActivity.class);

		intent.putExtra(CUR_LAT, latitude);
		intent.putExtra(CUR_LON, longitude);

		startActivity(intent);
	}

	public void getCoords(View view)
	{
		buildGoogleApiClient();
	}

	public void removeCoords(View view)
	{
		latitude=0.0;
		longitude=0.0;
		updateLatLongLabels();
	}

	public void updateLatLongLabels()
	{
		if(latitude==0.0&&longitude==0.0)
		{
			mLatitudeText.setText("Unknown");
			mLongitudeText.setText("Unknown");
		}
		else {
			mLatitudeText.setText(String.valueOf(latitude));
			mLongitudeText.setText(String.valueOf(longitude));
		}
	}

	public void openNews(View view)
	{
		Intent intent = new Intent(this, NewsActivity.class);
		startActivity(intent);
	}

	public void openHelp(View view)
	{
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}

	public void openAdvancedInfo(View view)
	{
		Intent intent = new Intent(this, AdvancedInfoActivity.class);
		startActivity(intent);
	}

	public void exitClicked(View view)
	{
		finish();
		System.exit(0);
	}

	protected synchronized void buildGoogleApiClient()
	{
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onStop() {
		if ((mGoogleApiClient!=null)&&(mGoogleApiClient.isConnected()))
		{
			mGoogleApiClient.disconnect();
		}
		super.onStop();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
	// Provides a simple way of getting a device's location and is well suited for
	// applications that do not require a fine-grained location and that do not need location
	// updates. Gets the best and most recent location currently available, which may be null
	// in rare cases when a location is not available.
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null)
		{
			latitude=mLastLocation.getLatitude();
			longitude=mLastLocation.getLongitude();
			updateLatLongLabels();

			Site closest = Globals.siteList.getClosest(latitude, longitude);
			closest.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, siteTextView);
			Toast.makeText(this, "Location updated...", Toast.LENGTH_LONG).show();
		}
		else
		{
			//Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
			Toast.makeText(this, "Location not found...", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	// Refer to the javadoc for ConnectionResult to see what error codes might be returned in
	// onConnectionFailed.
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
	}

	@Override
	public void onConnectionSuspended(int cause) {
	// The connection to Google Play services was lost for some reason. We call connect() to
	// attempt to re-establish the connection.
		Log.i(TAG, "Connection suspended");
		mGoogleApiClient.connect();
	}
}
