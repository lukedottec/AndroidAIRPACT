package wsu_airpact_project.airpact_demo;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Locale;


public class TabActivity extends ActionBarActivity implements ActionBar.TabListener, OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener
{
	//Map tab
	protected GoogleMap mainMap = null;
	protected Double latitudeMap;
	protected Double longitudeMap;
	protected boolean mapFound = false;


	//Main tab
	protected MainTabFragment mainTabFragment;
	//private static final String DEBUG_TAG = "MainActivityTag";
	protected static final String TAG = "basic-location-sample";
	//protected static String CUR_LAT="wsu_airpact_project.airpact_demo.CUR_LAT";
	//protected static String CUR_LON="wsu_airpact_project.airpact_demo.CUR_LON";
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	protected Double latitude;
	protected Double longitude;
	protected Spinner dropDown;

	public static TextView o3TextView;
	public static TextView pm25TextView;
	public static TextView siteTextView;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(
					actionBar.newTab()
							.setText(mSectionsPagerAdapter.getPageTitle(i))
							.setTabListener(this));
		}

		//Map stuff
		//MapFragment mF = (MapFragment) getFragmentManager().findFragmentById(R.id.mapintab);
		//mF.getMapAsync(this);
		//Intent intent = getIntent();
		//latitude = intent.getDoubleExtra(MyActivity.CUR_LAT, 0.0);
		//longitude = intent.getDoubleExtra(MyActivity.CUR_LON, 0.0);
		latitudeMap = Globals.lastLatitude;
		longitudeMap = Globals.lastLongitude;

		//Main Tab
		/*
		Globals.tabActivity = this;
		latitude=0.0;
		longitude=0.0;
		o3TextView = (TextView) findViewById(R.id.textViewOzoneLabel);
		pm25TextView = (TextView) findViewById(R.id.textViewPM2_5Label);
		siteTextView = (TextView) findViewById(R.id.textViewSiteLabel);
		//Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);
		//pullman.getLatestData((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, siteTextView);
		dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
		if(!Globals.siteList.setDropdown(dropDown, this, (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)))
		{
			DialogFragment dialog = new GoToLocationSettingsDialog();
			dialog.show(getFragmentManager(), "turnOnLocation");
		}
		buildGoogleApiClient();
		//*/
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_tab, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());

		int i = tab.getPosition();
		if (i == 1 && !mapFound)
		{
			//Map stuff
			MapFragment mF = (MapFragment) getFragmentManager().findFragmentById(R.id.mapintab);
			mF.getMapAsync(this);
			mapFound = true;
		}//*/
		if (i == 0)
		{
			//Main tab
			Globals.tabActivity = this;
			latitude=0.0;
			longitude=0.0;
			o3TextView = (TextView) findViewById(R.id.textViewOzoneLabel);
			pm25TextView = (TextView) findViewById(R.id.textViewPM2_5Label);
			siteTextView = (TextView) findViewById(R.id.textViewSiteLabel);
			//Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);
			//pullman.getLatestData((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, siteTextView);
			dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
			mainTabFragment = mSectionsPagerAdapter.mtf;
			if(mainTabFragment!=null) dropDown = mainTabFragment.dropDown;
			/*
			if(!Globals.siteList.setDropdown(dropDown, this, (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)))
			{
				DialogFragment dialog = new GoToLocationSettingsDialog();
				dialog.show(getFragmentManager(), "turnOnLocation");
			}//*/
			buildGoogleApiClient();
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		public MainTabFragment mtf;

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			TabFragment tf = TabFragment.newInstance(position + 1);
			if(position == 0) mtf = (MainTabFragment)tf;
			return tf;
		}

		@Override
		public int getCount()
		{
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
				case 2:
					return getString(R.string.title_section3).toUpperCase(l);
				case 3:
					return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class TabFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static TabFragment newInstance(int sectionNumber)
		{
			TabFragment fragment;
			Bundle args;
			switch(sectionNumber)
			{
				case 1:
					fragment = new MainTabFragment();
					break;
				case 2:
					fragment = new MapTabFragment();
					break;
				case 3:
					fragment = new NewsTabFragment();
					break;
				case 4:
					fragment = new HelpTabFragment();
					break;

				default:
					fragment = new TabFragment();
					break;
			}
			args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public TabFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
			return rootView;
		}
	}



	//Map Stuff
	@Override
	public void onMapReady(GoogleMap map)
	{
		mainMap=map;
		LatLng pullmanDefault = new LatLng(46.7338, -117.1673);
		LatLng currentLocation;
		//Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);

		if(latitudeMap==0.0&&longitudeMap==0)
		{
			currentLocation=pullmanDefault;
		}
		else
		{
			currentLocation = new LatLng(latitudeMap, longitudeMap);
			map.addMarker(new MarkerOptions().title("Current Location").snippet("You are here").position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		}

		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));

		if(!Globals.siteList.addToMap(map, (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)))
		{
			DialogFragment dialog = new GoToLocationSettingsDialog();
			dialog.show(getFragmentManager(), "turnOnLocation");
		}
	}



	//Main Tab
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		//String city = (String)parent.getItemAtPosition(pos);
		//Log.d(DEBUG_TAG, "Dropdown selected"+pos+": "+city);
		Site currSite;
		currSite = Globals.siteList.sites.get(pos);
		currSite.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, siteTextView);
	}
	public void onNothingSelected(AdapterView<?> parent) {}

	public void setDropDownSelection(String city)
	{
		int i=0;
		for(int j=0; j<dropDown.getCount(); j++)
			if(dropDown.getItemAtPosition(j).toString().equalsIgnoreCase(city))
			{
				i=j;
				break;
			}
		dropDown.setSelection(i);
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
		load();
	}
	@Override
	protected void onStop()
	{
		super.onStop();
		if ((mGoogleApiClient!=null)&&(mGoogleApiClient.isConnected()))
		{
			mGoogleApiClient.disconnect();
		}
		save();
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
			Globals.lastLatitude = latitude;
			Globals.lastLongitude = longitude;
			Globals.haveLocation = true;
			findViewById(R.id.buttonGetCoords).setVisibility(View.INVISIBLE);

			Site closest = Globals.siteList.getClosest(latitude, longitude);
			//closest.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, siteTextView);
			if(closest!=null) setDropDownSelection(closest.Name);
			//Toast.makeText(this, "Location updated...", Toast.LENGTH_LONG).show();
		}
		else
		{
			//Toast.makeText(this, "Location not found...", Toast.LENGTH_LONG).show();
			DialogFragment dialog = new GoToLocationSettingsDialog();
			dialog.show(getFragmentManager(), "turnOnLocation");
			DialogFragment dialog2 = new CannotConnectDialog();
			dialog2.show(getFragmentManager(), "couldNotConnect");
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


	public void saveButtonClicked(View view) { save(); }
	public void loadButtonClicked(View view) { load(); }

	public void save()
	{
		String filename = "airpactData";
		String outputString;
		StringBuilder sb = new StringBuilder();

		sb.append(Globals.setting1); sb.append("\r\n");
		sb.append(Globals.setting2); sb.append("\r\n");
		sb.append(Globals.setting3); sb.append("\r\n");
		sb.append(Globals.lastLatitude); sb.append("\r\n");
		sb.append(Globals.lastLongitude); sb.append("\r\n");
		outputString = sb.toString();

		//Log.d("GlobalsSave", "String to write is: "+outputString);

		try {
			FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream.write(outputString.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void load()
	{
		String filename = "airpactData";

		try {
			FileInputStream inputStream = openFileInput(filename);
			BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null)
			{
				total.append(line);
				total.append("\r\n");
			}
			r.close();
			inputStream.close();
			Globals.parseFile(total.toString());
			//Log.d("LoadSave", "File contents: " + total);
		} catch (Exception e)
		{
			e.printStackTrace();
			//Log.d("LoadSave", "File contents: NONE");
		}
	}
}
