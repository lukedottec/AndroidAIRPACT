package wsu_airpact_project.airpact_demo;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TabActivity extends ActionBarActivity implements ActionBar.TabListener, OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener
		,TooFarDialog.TooFarDialogListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
		DrawerLayout.DrawerListener, ListView.OnItemClickListener
{
	public int startingHour = -12;

	//Map tab
	protected GoogleMap mainMap = null;
	//private static int zoomLevel = 10;
	protected Double latitudeMap;
	protected Double longitudeMap;
	protected boolean mapFound = false;
	protected boolean mainFound = false;

	protected MapInfoWindow miw = null;
	protected GroundOverlayOptions goo = null;
	protected GroundOverlay overlay = null;
	//protected ProgressDialog pDialog;
	protected int hourShown = startingHour-1;
	protected int maxHours = 24;
	//protected Bitmap bitmap;
	protected Bitmap[] bitmapsO3;
	protected Bitmap[] bitmapsPM25;
	protected boolean haveDownloadedAllPics = false;
	public String overlayType = "Ozone";
	public AnimationThread animThread = null;
	protected boolean animThreadHasStarted = false;
	public boolean playing = false;
	public String pinMode = "All";

	//NavDrawer Stuff
	protected DrawerLayout navDrawer = null;
	public Button[] navButtons;

	//Main tab
	protected MainTabFragment mainTabFragment;
	//private static final String DEBUG_TAG = "MainActivityTag";
	protected static final String TAG = "TabActivityTag";
	//protected static String CUR_LAT="wsu_airpact_project.airpact_demo.CUR_LAT";
	//protected static String CUR_LON="wsu_airpact_project.airpact_demo.CUR_LON";
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	protected Double latitude;
	protected Double longitude;
	protected Spinner dropDown;
	protected boolean showingForecast = false;
	protected ListView lv;
	public GeocoderThread citySearchThread = null;

	public Site currentSite;
	public static TextView aqiTextView;
	public static TextView o3TextView;
	public static TextView pm25TextView;
	public static TextView hourTextView;
	public static TextView farTextView;
	public static TextView useMethodTextView;
	public static Button farButton;
	public boolean haveSetDefaults = false;

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

		//Log.e("CrashBug", "In TabActivity.onCreate()");
		setContentView(R.layout.activity_tab);

		//Prevents the keyboard from popping up nonstop
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
		Globals.tabActivity = this;
		Globals.setTimeOriginal();
		/*
		Globals.tabActivity = this;
		latitude=0.0;
		longitude=0.0;
		o3TextView = (TextView) findViewById(R.id.textViewOzoneLabel);
		pm25TextView = (TextView) findViewById(R.id.textViewPM2_5Label);
		hourTextView = (TextView) findViewById(R.id.textViewSiteLabel);
		//Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);
		//pullman.getLatestData((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, hourTextView);
		dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
		if(!Globals.siteList.setDropdown(dropDown, this, (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)))
		{
			DialogFragment dialog = new GoToLocationSettingsDialog();
			dialog.show(getFragmentManager(), "turnOnLocation");
		}
		buildGoogleApiClient();
		//*/

		bitmapsO3 = new Bitmap[48];
		bitmapsPM25 = new Bitmap[48];
		//for(int i=0; i<48; i++)
		//	bitmapsO3[i] = null;
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

		if (mainFound)
			hideKeyboard();

		int i = tab.getPosition();
		if (i == 1 && !mapFound)
		{
			//Map stuff
			MapFragment mF = (MapFragment) getFragmentManager().findFragmentById(R.id.mapintab);
			mF.getMapAsync(this);
			mapFound = true;
			DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
			if(dl==null) Log.d("Drawer", "COULDN'T FIND IT");
			else Log.d("Drawer", "Found the drawer!");
			if(dl!=null)
			{
				//Also need to find its listview
				navDrawer = dl;
				navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				navDrawer.setDrawerListener(this);
				ListView drawerList = (ListView) findViewById(R.id.left_drawer);

				ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();
				navDrawerItems.add(new NavDrawerItem("AIRPACT Sites:", "Header"));
				navDrawerItems.add(new NavDrawerItem("All Sites", "Radio"));
				navDrawerItems.add(new NavDrawerItem("O3 Sites", "Radio"));
				navDrawerItems.add(new NavDrawerItem("PM2.5 Sites", "Radio"));
				navDrawerItems.add(new NavDrawerItem("No Sites", "Radio"));
				navDrawerItems.add(new NavDrawerItem("Forecast Overlay:", "Header"));
				navDrawerItems.add(new NavDrawerItem("O3 Overlay", "Radio"));
				navDrawerItems.add(new NavDrawerItem("PM2.5 Overlay", "Radio"));
				navDrawerItems.add(new NavDrawerItem("No Overlay", "Radio"));
				//navDrawerItems.add(new NavDrawerItem("Sites", new CheckBox(this), "Checkbox"));
				NavDrawerListAdapter adapter = new NavDrawerListAdapter(this, navDrawerItems);
				drawerList.setAdapter(adapter);


				//drawerList.setChoiceMode(ListView.CHOICE_MODE_NONE);
				drawerList.setOnItemClickListener(this);
				navButtons = new Button[navDrawerItems.size()];
			}

		}//*/
		if (i == 0 && !mainFound)
		{
			//Main tab
			//Globals.tabActivity = this;
			latitude=0.0;
			longitude=0.0;
			//aqiTextView = (TextView) findViewById(R.id.textViewAQIValue);
			//o3TextView = (TextView) findViewById(R.id.textViewOzoneLabel);
			//pm25TextView = (TextView) findViewById(R.id.textViewPM2_5Label);
			//hourTextView = (TextView) findViewById(R.id.textViewSiteLabel);
			//Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);
			//pullman.getLatestData((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, hourTextView);
			dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
			if(dropDown==null) Log.d(TAG, "dropDown==null");
			//mainTabFragment = mSectionsPagerAdapter.mtf;
			if(mainTabFragment!=null)
			{
				Log.d(TAG, "mainTabFragment!=null");
				dropDown = mainTabFragment.dropDown;
				if(dropDown==null) Log.d(TAG, "    dropDown==null");
			}
			else
			{
				Log.d(TAG, "mainTabFragment==null");
			}
			/*
			if(!Globals.siteList.setDropdown(dropDown, this, (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)))
			{
				DialogFragment dialog = new GoToLocationSettingsDialog();
				dialog.show(getFragmentManager(), "turnOnLocation");
			}//*/
			Log.d(TAG, "buildingGoogleApiClient");
			buildGoogleApiClient();
			mainFound=true;
		}
	}
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }

	public void selectTab(int n)
	{
		mViewPager.setCurrentItem(n);
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

			//Log.d(TAG, "in getItem("+position+")");
			TabFragment tf = TabFragment.newInstance(position + 1);
			if(position == 0) mtf = (MainTabFragment)tf;



			dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
			//if(dropDown==null) Log.d(TAG, "!!!dropDown==null");
			//mainTabFragment = mSectionsPagerAdapter.mtf;
			if(mainTabFragment!=null)
			{
				//Log.d(TAG, "!!!mainTabFragment!=null");
				dropDown = mainTabFragment.dropDown;
				//if(dropDown==null) Log.d(TAG, "asdf!!!dropDown==null");
			}
			/*else
			{
				Log.d(TAG, "!!!mainTabFragment==null");
			}*/


			return tf;
		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 3;
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
			//View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
			//return rootView;
			return inflater.inflate(R.layout.fragment_tab, container, false);
		}
	}

	// TODO
	@Override
	public void onMapReady(GoogleMap map)
	{
		Log.d("MapTag", "Starting onMapReady");
		map.setInfoWindowAdapter(miw);
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerClickListener(this);
		mainMap=map;

		// Draws the arc border
		PolylineOptions plo = drawAP4Boundary();
		map.addPolyline(plo);

		LatLng pullmanDefault = new LatLng(46.7338, -117.1673);
		LatLng currentLocation;
		//Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);

		latitudeMap = Globals.lastLatitude;
		longitudeMap = Globals.lastLongitude;
		if(latitudeMap==0.0&&longitudeMap==0)
		{
			Log.d("MapTag", "LL==0");
			currentLocation=pullmanDefault;
			Log.d(TAG, "no location was detected for pin?");
		}
		else
		{
			Log.d("MapTag", "LL!=0");
			currentLocation = new LatLng(latitudeMap, longitudeMap);
			map.addMarker(new MarkerOptions().title("Current Location").snippet("You are here").position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		}

		// DEBUG
		// NOTE: Made it here with correct coordinates
		Toast.makeText(getApplicationContext(), currentLocation.toString(), Toast.LENGTH_LONG).show();

		map.setMyLocationEnabled(true);
		int zoomLevel = 6;
		 map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));

		if(!Globals.siteList.addToMap(map, (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)))
		{
			Log.d("MapTag", "!siteList.addToMap");
			//DialogFragment dialog = new GoToLocationSettingsDialog();
			//dialog.show(getFragmentManager(), "turnOnLocation");
			addSitesToMap(map);
			DialogFragment dialog2 = new CannotConnectDialog();
			dialog2.show(getFragmentManager(), "couldNotConnect");
		}
	}
	// Draws that arc area
	public PolylineOptions drawAP4Boundary()
	{
		PolylineOptions plo = new PolylineOptions();
		plo.add(new LatLng(40.15336, -125.1509));
		plo.add(new LatLng(40.19574, -123.8900));
		plo.add(new LatLng(40.22295, -122.6276));
		plo.add(new LatLng(40.23495, -121.3644));
		plo.add(new LatLng(40.23173, -120.1011));
		plo.add(new LatLng(40.21329, -118.8382));
		plo.add(new LatLng(40.17966, -117.5764));
		plo.add(new LatLng(40.13086, -116.3162));
		plo.add(new LatLng(40.06693, -115.0584));
		plo.add(new LatLng(39.98792, -113.8035));
		plo.add(new LatLng(39.89389, -112.5522));
		plo.add(new LatLng(39.78939, -111.3529));
		plo.add(new LatLng(44.54784, -110.5424));
		plo.add(new LatLng(49.30697, -109.5843));
		plo.add(new LatLng(49.43092, -110.9990));
		plo.add(new LatLng(49.54248, -112.4769));
		plo.add(new LatLng(49.63625, -113.9607));
		plo.add(new LatLng(49.71214, -115.4493));
		plo.add(new LatLng(49.77009, -116.9418));
		plo.add(new LatLng(49.81002, -118.4371));
		plo.add(new LatLng(49.83192, -119.9342));
		plo.add(new LatLng(49.83574, -121.4321));
		plo.add(new LatLng(49.82149, -122.9296));
		plo.add(new LatLng(49.78918, -124.4258));
		plo.add(new LatLng(49.73885, -125.9197));
		plo.add(new LatLng(44.94428, -125.5027));
		plo.add(new LatLng(40.15336, -125.1509));
		plo.width(10);
		plo.color(Color.BLACK);
		return plo;
	}
	// Sets markers on map
	// TODO
	public void setSiteMarkers(Site siteUsing, GoogleMap map)
	{
		for (int i = 0; i < Globals.siteList.sites.size(); i++)
		{
			Site s = Globals.siteList.sites.get(i);
			float clr = .01f;

			if (s.Name.equals(siteUsing.Name))
				clr = BitmapDescriptorFactory.HUE_GREEN;
			else clr = BitmapDescriptorFactory.HUE_RED;

			s.addSiteMarker(map, clr);
			Log.d("MapTag", "   set site marker for "+s.Name);
		}
	}
	// TODO
	public void addSitesToMap(GoogleMap map)
	{
		Log.d("MapTag", "Starting addSitesToMap()");
		Site siteUsing;
		if(currentSite!=null) { siteUsing = currentSite; }
		else { siteUsing = Globals.siteList.getClosest(latitude, longitude); }
		//siteUsing = Globals.siteList.getClosest(latitude, longitude);
		setSiteMarkers(siteUsing, map);

		LatLngBounds bounds = new LatLngBounds(
				new LatLng(39.78939, -125.9197),				//39.79, -125.87
				new LatLng(49.83574, -109.5843));				//49.74, -111.35
		goo = new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(R.drawable.ic_information))
				.transparency(.5f)
				.positionFromBounds(bounds);

		//overlay = map.addGroundOverlay(goo);
		Log.d("ImageStuff", "Calling setImageOverlay(null)");
		setImageOverlay(" ", -100);
		Log.d("ImageStuff", "Calling LoadImage().execute");
		//overlayType = "Ozone";
		String oT;

		oT = "Ozone";
		LoadImage li = new LoadImage();
		li.setValues(oT, startingHour);
		li.execute(getOverlayURL(oT, startingHour));
		oT = "PM25";
		LoadImage li2 = new LoadImage();
		li2.setValues(oT, startingHour);
		li2.execute(getOverlayURL(oT, startingHour));

		oT = "Ozone";
		li = new LoadImage();
		li.setValues(oT, 0);
		li.execute(getOverlayURL(oT, 0));
		oT = "PM25";
		li2 = new LoadImage();
		li2.setValues(oT, 0);
		li2.execute(getOverlayURL(oT, 0));
		//new LoadImage().execute(url);
	}
	public void downloadPicsForHour(int hourDelta)
	{
		String oT;
		if(bitmapsO3[hourDelta-startingHour]==null)
		{
			oT = "Ozone";
			LoadImage li = new LoadImage();
			li.setValues(oT, hourDelta);
			li.execute(getOverlayURL(oT, hourDelta));
		}
		if(bitmapsPM25[hourDelta-startingHour]==null)
		{
			oT = "PM25";
			LoadImage li2 = new LoadImage();
			li2.setValues(oT, hourDelta);
			li2.execute(getOverlayURL(oT, hourDelta));
		}
	}
	public void downloadRestOfPics()
	{
		for(int i=startingHour; i<startingHour + maxHours; i++)
			downloadPicsForHour(i);
	}
	// Return URL of specific image type at specific time
	// Updated to AIRPACT-5
	public String getOverlayURL(String type, int hourDelta)
	{
		Calendar c = Globals.getTimeOriginal();
		int diff=0;
		c.add(Calendar.HOUR, hourDelta-diff);

		String url = null;

		/// Past code
//		if(type.equals("Ozone"))
//			url = "http://www.lar.wsu.edu/airpact/gmap/images/anim/species/"+
//					c.get(Calendar.YEAR)+"/"+c.get(Calendar.YEAR)+"_"+addZero(c.get(Calendar.MONTH)+1)+"_"+addZero(c.get(Calendar.DAY_OF_MONTH))+
//					"/airpact4_08hrO3_"+c.get(Calendar.YEAR)+addZero(c.get(Calendar.MONTH)+1)+addZero(c.get(Calendar.DAY_OF_MONTH))+addZero(c.get(Calendar.HOUR_OF_DAY))+".gif";
//		if(type.equals("PM25"))
//			url = "http://www.lar.wsu.edu/airpact/gmap/images/anim/species/"+
//					c.get(Calendar.YEAR)+"/"+c.get(Calendar.YEAR)+"_"+addZero(c.get(Calendar.MONTH)+1)+"_"+addZero(c.get(Calendar.DAY_OF_MONTH))+
//					"/airpact4_PM25_"+c.get(Calendar.YEAR)+addZero(c.get(Calendar.MONTH)+1)+addZero(c.get(Calendar.DAY_OF_MONTH))+addZero(c.get(Calendar.HOUR_OF_DAY))+".gif";
//		Log.d("ImageStuff", "     with \""+url+"\"");
//		//new LoadImage().execute("http://www.lar.wsu.edu/airpact/gmap/images/anim/species/2015/2015_10_02/airpact4_08hrO3_2015100215.gif");

		// URL head
		url = "http://lar.wsu.edu/airpact/gmap/ap5/images/anim/species" + "/" +
				c.get(Calendar.YEAR) + "/" +
				c.get(Calendar.YEAR) + "_" +
				addZero(c.get(Calendar.MONTH)+1) + "_" +
				addZero(c.get(Calendar.DAY_OF_MONTH));

		// GIF type
		if (type.equals("Ozone"))
			url += "/airpact5_O3_";
		if (type.equals("PM25"))
			url += "/airpact5_PM25_";

		// URL tail
		url += c.get(Calendar.YEAR) +
		addZero(c.get(Calendar.MONTH)+1) +
		addZero(c.get(Calendar.DAY_OF_MONTH)) +
		addZero(c.get(Calendar.HOUR_OF_DAY)) +
		".gif";

		// DEBUG
		Log.println(Log.DEBUG, "LukeDebug", url);

		return url;
	}

	// Utility function
	// e.g. 9 => "09"; 12 => "12"
	public String addZero(int i)
	{
		if(i>=10)
			return ""+i;
		return "0"+i;
	}

    // Called by LoadImage with the downloaded GIF
    // Stores given bitmap and starts forecast animation
	public void setOverlayBitmap(String type, int hourDelta, Bitmap b)
	{
		if (type.equals("Ozone"))
		{
            // Add 03 bitmap to array
			bitmapsO3[hourDelta-startingHour] = b;
			if(b == null)
			{
				//if(hourDelta==startingHour) Toast.makeText(this, "Could not find the overlays for Ozone", Toast.LENGTH_LONG).show();
				if(hourDelta==0) Toast.makeText(this, "Could not find the current overlay for Ozone", Toast.LENGTH_LONG).show();
				return;
			}
			if(hourDelta == 0
                    && overlayType.equals(type))
			{
				setImageOverlay(type, hourDelta);
                // Starts animation thread automatically
				if(animThread==null)
				{
					animThread = new AnimationThread();
					animThread.start();
					animThreadHasStarted = true;
				}
			}
		}
		if (type.equals("PM25"))
		{
			bitmapsPM25[hourDelta-startingHour] = b;
			if(b==null)
			{
				//if(hourDelta==startingHour) Toast.makeText(this, "Could not find the overlays for PM2.5", Toast.LENGTH_LONG).show();
				if(hourDelta==0) Toast.makeText(this, "Could not find the current overlay for PM2.5", Toast.LENGTH_LONG).show();
				return;
			}
			if(hourDelta == startingHour
                    && overlayType.equals(type))
			{
				setImageOverlay(type, hourDelta);
				if(animThread==null)
				{
					animThread = new AnimationThread();
					animThread.start();
					animThreadHasStarted = true;
				}
			}
		}
	}

	// Determines image overlay to display based on direction of animation
	public void advanceOverlay(String type) { advanceOverlay(type, 1); }
	public void advanceOverlay(String type, int dir)
	{
		Log.d("MapThreading", "In advanceOverlay: "+type+" dir: "+dir+"   hourShown="+hourShown);
		int nextHour = hourShown+dir;
		if(nextHour >= maxHours+startingHour)
			nextHour = startingHour;
		if(nextHour < startingHour)
			nextHour = maxHours+startingHour-1;

		if(type.equals("Ozone"))
		{
			if (bitmapsO3[nextHour-startingHour] == null)
			{
				Log.d("MapThreading", "The next image #" + nextHour + " is null");
				if(dir>=1)
				{
					hourShown = -100;
					if(bitmapsO3[0]!=null)
					{
						setImageOverlay(type, startingHour);
						hourShown = startingHour;
						Log.d("MapThreading", "Set next image1 #" + nextHour);
					}
				}
				else if(dir<=-1)
				{
					while(bitmapsO3[nextHour-startingHour]==null && nextHour>startingHour)
						nextHour--;
					hourShown=nextHour;
					if(bitmapsO3[nextHour-startingHour]!=null)
					{
						setImageOverlay(type, nextHour);
						hourShown = nextHour;
						Log.d("MapThreading", "Set next image2 #" + nextHour);
					}
				}
			} else
			{
				setImageOverlay(type, nextHour);
				hourShown = nextHour;
				Log.d("MapThreading", "Set next image3 #" + nextHour);
			}
		}
		else if(type.equals("PM25"))
		{
			if (bitmapsPM25[nextHour-startingHour] == null)
			{
				Log.d("MapThreading", "The next image #" + nextHour + " is null");
				if(dir>=1)
				{
					hourShown = -100;
					if(bitmapsPM25[0]!=null)
					{
						setImageOverlay(type, startingHour);
						hourShown = startingHour;
						Log.d("MapThreading", "Set next image1 #" + nextHour);
					}
				}
				else if(dir<=-1)
				{
					while(bitmapsPM25[nextHour-startingHour]==null && nextHour>startingHour)
						nextHour--;
					hourShown=nextHour;
					if(bitmapsPM25[nextHour-startingHour]!=null)
					{
						setImageOverlay(type, nextHour);
						hourShown = nextHour;
						Log.d("MapThreading", "Set next image2 #" + nextHour);
					}
				}
			} else
			{
				setImageOverlay(type, nextHour);
				hourShown = nextHour;
				Log.d("MapThreading", "Set next image3 #" + nextHour);
			}
		}
		else if(type.equals("None"))
		{
			setOverlayHour(0);
		}
	}

	// TODO: ----------------------------------------------
    public void setImageOverlay(String type, int hourDelta)
	{
		if(hourDelta==-100) return;
		Log.d("ImageStuff", "Setting Image overlay");
		if(goo!=null)
		{
			if (type.equals("Ozone"))
			{
				if (bitmapsO3[hourDelta-startingHour] != null)
				{
					if (overlay != null) overlay.remove();
					goo.image(BitmapDescriptorFactory.fromBitmap(bitmapsO3[hourDelta-startingHour]));
					overlay = mainMap.addGroundOverlay(goo);
					setOverlayHour(hourDelta);
				} else
				{
					Log.d("ImageStuff", "b==null");
				}
			}
			if (type.equals("PM25"))
			{
				if (bitmapsPM25[hourDelta-startingHour] != null)
				{
					if (overlay != null)
						overlay.remove();
					goo.image(BitmapDescriptorFactory.fromBitmap(bitmapsPM25[hourDelta-startingHour]));
					overlay = mainMap.addGroundOverlay(goo);
					setOverlayHour(hourDelta);
					//Toast.makeText(this, "Image changed", Toast.LENGTH_LONG).show();
				} else
				{
					Log.d("ImageStuff", "b==null");
				}
			}
		}
		else
		{
			Log.d("ImageStuff", "goo==null");
		}
	}
	// Sets hour drawable in bottom-right
	public void setOverlayHour(int hourDelta)
	{
		//TextView tv = (TextView)findViewById(R.id.textViewHourValue);
		//tv.setText(""+hourDelta);

		Calendar c = Globals.getTimeOriginal();
		//int dayOriginal = c.get(Calendar.DAY_OF_YEAR);
		c.add(Calendar.HOUR_OF_DAY, hourDelta);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		//int dayOfPic = c.get(Calendar.DAY_OF_YEAR);

		//int dayDelta = dayOfPic-dayOriginal;
		//if(dayOriginal<=1&&dayOfPic>360) dayDelta=-1;	//If New Year's
		//if(dayOfPic<=1&&dayOriginal>360) dayDelta=1;

		ImageView iv = (ImageView)findViewById(R.id.imageViewHourShown);
		if(hour==0)			iv.setImageResource(R.drawable.hr00);
		else if(hour==1)	iv.setImageResource(R.drawable.hr01);
		else if(hour==2)	iv.setImageResource(R.drawable.hr02);
		else if(hour==3)	iv.setImageResource(R.drawable.hr03);
		else if(hour==4)	iv.setImageResource(R.drawable.hr04);
		else if(hour==5)	iv.setImageResource(R.drawable.hr05);
		else if(hour==6)	iv.setImageResource(R.drawable.hr06);
		else if(hour==7)	iv.setImageResource(R.drawable.hr07);
		else if(hour==8)	iv.setImageResource(R.drawable.hr08);
		else if(hour==9)	iv.setImageResource(R.drawable.hr09);
		else if(hour==10)	iv.setImageResource(R.drawable.hr10);
		else if(hour==11)	iv.setImageResource(R.drawable.hr11);
		else if(hour==12)	iv.setImageResource(R.drawable.hr12);
		else if(hour==13)	iv.setImageResource(R.drawable.hr13);
		else if(hour==14)	iv.setImageResource(R.drawable.hr14);
		else if(hour==15)	iv.setImageResource(R.drawable.hr15);
		else if(hour==16)	iv.setImageResource(R.drawable.hr16);
		else if(hour==17)	iv.setImageResource(R.drawable.hr17);
		else if(hour==18)	iv.setImageResource(R.drawable.hr18);
		else if(hour==19)	iv.setImageResource(R.drawable.hr19);
		else if(hour==20)	iv.setImageResource(R.drawable.hr20);
		else if(hour==21)	iv.setImageResource(R.drawable.hr21);
		else if(hour==22)	iv.setImageResource(R.drawable.hr22);
		else if(hour==23)	iv.setImageResource(R.drawable.hr23);
		else if(hour==24)	iv.setImageResource(R.drawable.hr24);

		/*ImageView iv2 = (ImageView)findViewById(R.id.imageViewDayShown);
		if(dayDelta==-1)		iv2.setImageResource(R.drawable.yesterday);
		else if(dayDelta==0)	iv2.setImageResource(R.drawable.today);
		else if(dayDelta==1)	iv2.setImageResource(R.drawable.tomorrow);*/
		TextView tv = (TextView)findViewById(R.id.textViewDayShown);
		String dateStr = "" + (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH)
				+ "/" + c.get(Calendar.YEAR);
		tv.setText(dateStr);
		tv.setVisibility(View.VISIBLE);

		if(overlayType.equals("None"))
		{
			iv.setImageResource(0);
			//iv2.setImageResource(0);
			tv.setVisibility(View.INVISIBLE);
		}
	}

    // Increment time and run forecast animation
	private class AnimationThread extends Thread
	{
		public boolean shouldContinue = true;

		@Override
		public void run()
		{
			Log.d("MAPThreading", "In AnimationThread.run()");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.SECOND, 1);
			while(shouldContinue)
			{
				if(!playing)
				{
					c = Calendar.getInstance();
					c.add(Calendar.SECOND, 1);
					continue;
				}
				if(Calendar.getInstance().before(c))
				{
					continue;
				}
				Log.d("MAPThreading", "In loop now, adding second");
				c.add(Calendar.SECOND, 1);
				//Globals.tabActivity.advanceOverlay();
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						advanceOverlay(overlayType);
					}
				});
			}

			Log.d("MAPThreading", "SHOULDN'T CONTINUE!!!");
		}
	}

	// TODO
	private class LoadImage extends AsyncTask<String, String, Bitmap>
	{
		private int hourDelta = 0;
		private String type;
		private Bitmap bmap;
		public void setValues(String t, int hd)
		{
			type = t;
			hourDelta = hd;
		}
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			//pDialog = new ProgressDialog(TabActivity.this);
			//pDialog.setMessage("Loading image...");
			//pDialog.show();
		}
		protected Bitmap doInBackground(String... args)
		{
			try
			{
				bmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			// DEBUG
			// TODO: Find another way to find bitmap width
			Log.println(Log.DEBUG, "LukeDebug", "GIF image downloaded size is:" + bmap.getWidth());
//			Toast.makeText(getApplicationContext(), "bitmap width: " + bmap.getWidth(), Toast.LENGTH_SHORT).show();

			return bmap;
		}
		protected void onPostExecute(Bitmap image)
		{
			if(image!=null)
			{
				//Globals.tabActivity.setImageOverlay(image);
                Log.println(Log.DEBUG, "LukeDebug", "GIF is not null!");
				Globals.tabActivity.setOverlayBitmap(type, hourDelta, image);
				Log.d("ImageStuff", "LoadImage: image!=null");
				//pDialog.dismiss();
			}
			else
			{
				//pDialog.dismiss();
                Log.println(Log.DEBUG, "LukeDebug", "GIF is null!");
				Globals.tabActivity.setOverlayBitmap(type, hourDelta, null);
				Log.d("ImageStuff", "LoadImage: image==null with hour "+hourDelta);
				//Toast.makeText(TabActivity.this, "Overlay image not found for hour "+hourDelta, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void onInfoWindowClick(Marker m)
	{
		Site s = Globals.siteList.getByName(m.getTitle());
		if (s == null) return;
		setDropDownSelection(s.Name);
		mViewPager.setCurrentItem(0);
	}

	public boolean onMarkerClick(Marker m)
	{
		Site s = Globals.siteList.getByName(m.getTitle());
		if(s==null) return false;
		if(s.hasValues()) return false;
		Log.d("Map", "Marker was clicked with name "+s.Name);
		s.updatePin((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
		return false;
	}
	// TODO
	public void updateMarker(Site s)
	{
		if(s==null)
		{
			Log.d("TabActivityTag", "UpdateMarker(null)?");
			return;
		}

		Site siteUsing;
		if(currentSite!=null) { siteUsing = currentSite; }
		else { siteUsing = Globals.siteList.getClosest(latitude, longitude); }

		float clr;
		if(s.Name.equals(siteUsing.Name))
			clr = BitmapDescriptorFactory.HUE_GREEN;
		else clr = BitmapDescriptorFactory.HUE_RED;

		Marker m = s.addSiteMarker(mainMap, clr);
		if(m!=null) m.showInfoWindow();
		Log.d("TabActivityTag", "Updated site marker for "+s.Name);
	}

	// Map settings drawer
	public void onDrawerClosed(View drawer)
	{
		Log.d("Drawer", "Drawer closed, locking...");
		navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}
	public void onDrawerOpened(View drawer) { }
	public void onDrawerSlide(View drawer, float offset) { }
	public void onDrawerStateChanged(int newstate) { }

	// Map time controls
	public void playPausePressed(View view)
	{
		ImageButton ib = (ImageButton)findViewById(R.id.buttonPlayPause);
		if(playing)
		{
			playing=false;
			ib.setBackgroundResource(R.drawable.play);
		}
		else
		{
			playing = true;
			ib.setBackgroundResource(R.drawable.pause);

			//Download the rest of the overlay bitmaps if we need to
			if (!haveDownloadedAllPics)
			{
				hourShown = startingHour;
				haveDownloadedAllPics = true;
				downloadRestOfPics();
			}
			if (!animThreadHasStarted || animThread == null)
			{
				animThreadHasStarted=true;
				animThread = new AnimationThread();
				animThread.start();
			}
		}
	}
	public void leftArrowPressed(View view)
	{
		if(!playing) advanceOverlay(overlayType,-1);
		//Download the rest of the overlay bitmaps if we need to
		if (!haveDownloadedAllPics)
		{
			hourShown = startingHour;
			haveDownloadedAllPics = true;
			downloadRestOfPics();
		}
		if (!animThreadHasStarted || animThread == null)
		{
			animThreadHasStarted=true;
			animThread = new AnimationThread();
			animThread.start();
		}
	}
	public void rightArrowPressed(View view)
	{
		if(!playing) advanceOverlay(overlayType,1);
		//Download the rest of the overlay bitmaps if we need to
		if (!haveDownloadedAllPics)
		{
			hourShown = startingHour;
			haveDownloadedAllPics = true;
			downloadRestOfPics();
		}
		if (!animThreadHasStarted || animThread == null)
		{
			animThreadHasStarted=true;
			animThread = new AnimationThread();
			animThread.start();
		}
	}

	//Main Tab
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		//String city = (String)parent.getItemAtPosition(pos);
		//Log.d(DEBUG_TAG, "Dropdown selected"+pos+": "+city);
		if(mainMap!=null)
		{
			if(currentSite!=null)
				currentSite.addSiteMarker(mainMap, BitmapDescriptorFactory.HUE_RED);
		}

		Log.d(TAG, "onItemSelected()");
		Site currSite;
		Log.d("onItemSelected()", "pos=" + pos+"   siteList.sites.size()="+Globals.siteList.sites.size());
		if(pos>=Globals.siteList.sites.size()) { Log.d("onItemSelected()", "Returning early... pos="+pos+"   siteList.sites.size()="+Globals.siteList.sites.size()); return; }		//Temp fix until we can figure out why this is happening
		Log.d("onItemSelected()", "pos.Name="+parent.getItemAtPosition(pos).toString());
		currSite = Globals.siteList.getByName(parent.getItemAtPosition(pos).toString());
		if(currSite==null)
		{
			Log.d("onItemSelected()", parent.getItemAtPosition(pos).toString()+" doesn't exist?");
			return;
		}
		Log.d("onItemSelected()", "currSite.Name="+currSite.Name);
		currentSite = currSite;
		if(mainMap!=null)
		{
			currentSite.addSiteMarker(mainMap, BitmapDescriptorFactory.HUE_GREEN);
		}
		if(o3TextView==null) Log.d(TAG, "o3TextView==null");
		currSite.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));//, o3TextView, pm25TextView, hourTextView);
	}
	public void onNothingSelected(AdapterView<?> parent) {}

	public void populateDropdown(Spinner spinner)
	{
		//if(spinner==null) Log.e("CrashBug", "In populateDropdown spinner was null");
		//if(spinner==null) return;
		String[] items = new String[Globals.siteList.sites.size()];
		for (int i = 0; i < Globals.siteList.sites.size(); i++)
			items[i]=Globals.siteList.sites.get(i).Name;
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		Site closest = Globals.siteList.getClosest(Globals.lastLatitude, Globals.lastLongitude);
		int i=0;
		for(int j=0; j<spinner.getCount(); j++)
			if(spinner.getItemAtPosition(j).toString().equalsIgnoreCase(closest.Name))
			{
				i=j;
				break;
			}
		spinner.setSelection(i);
	}

	// User selects city on drop-down menu for forecast info
	public void setDropDownSelection(String city)
	{
		//if(dropDown==null) Log.e("CrashBug", "In setDropDownSelection dropDown was null");
		if(dropDown==null)
		{
			/*if(mainTabFragment==null)
			{
				//Log.e("CrashBug", "In setDropDownSelection mainTabfragment was null");
			}*/
			if(mainTabFragment!=null)
			{
				dropDown = mainTabFragment.dropDown;
				/*Log.e("CrashBug", "In setDropDownSelection dropDown was set to mtf.dropdown");
				if(dropDown==null)
				{
					Log.e("CrashBug", "In setDropDownSelection it's still null...");
				}
				else
				{
					Log.e("CrashBug", "In setDropDownSelection it's no longer null!");
				}*/
			}
		}
		if(dropDown==null) return;
		int i=0;
		Log.d(TAG, "in setDropDownSelection, dropDown.getCount=="+dropDown.getCount());
		for(int j=0; j<dropDown.getCount(); j++)
			if(dropDown.getItemAtPosition(j).toString().equalsIgnoreCase(city))
			{
				i=j;
				break;
			}
		dropDown.setSelection(i);
	}

	public void openFarInfo(View view)
	{
		Globals.siteList.setDistances(Globals.lastLatitude, Globals.lastLongitude);
		DialogFragment dialog = new TooFarDialog();
		dialog.show(getFragmentManager(), "tooFar");
	}

	public void onDialogUseSite(TooFarDialog dialog, String name)
	{
		int i;
		for(i=0; i<dropDown.getCount(); i++)
			if(dropDown.getItemAtPosition(i).toString().equalsIgnoreCase(name))
				break;
		dropDown.setSelection(i);
	}

	public void exitClicked(View view)
	{
		finish();
		System.exit(0);
	}
	public void helpQuestionmarkClicked(View view)
	{
		mViewPager.setCurrentItem(2);
	}

	public void setMainLabels(Site s)
	{
		if(aqiTextView==null) { Log.e("AQITextView", "AQITextView is null..."); }
		int aqi = s.getAQI(0, 0);
		aqiTextView.setText(""+aqi);
		if	   (aqi<=50)	aqiTextView.setTextColor(Color.rgb(0,160,0));		//0 228 0		Green
		else if(aqi<=100)	aqiTextView.setTextColor(Color.rgb(220,220,0));		//255 255 0		Yellow
		else if(aqi<=150)	aqiTextView.setTextColor(Color.rgb(255,126,0));		//255 126 0		Orange
		else if(aqi<=200)	aqiTextView.setTextColor(Color.rgb(220,0,0));		//255 0 0		Red
		else if(aqi<=300)	aqiTextView.setTextColor(Color.rgb(180,0,90));		//153 0 76		Purple
		else if(aqi<=500)	aqiTextView.setTextColor(Color.rgb(126,0,35));		//126 0 35		Maroon
		else				aqiTextView.setTextColor(Color.rgb(0,0,0));			//Shouldn't get here!!! (Black)
		if(Globals.useMethod.equals("AP"))
		{
			useMethodTextView.setText("Using AIRPACT");
			o3TextView.setText("Ozone:    " + s.OZONEavg_ap);
			if(s.OZONEavg_ap<0)
				o3TextView.setText("Ozone:    Unavailable");
			pm25TextView.setText("PM2.5:    " + s.PM25avg_ap);
			if(s.PM25avg_ap<0)
				pm25TextView.setText("PM2.5:    Unavailable");
		}
		else if(Globals.useMethod.equals("AN"))
		{
			useMethodTextView.setText("Using AIRNOW");
			o3TextView.setText("Ozone:    " + s.OZONEavg_an);
			if(s.OZONEavg_an<0)
				o3TextView.setText("Ozone:    Unavailable");
			pm25TextView.setText("PM2.5:    " + s.PM25avg_an);
			if(s.PM25avg_an<0)
				pm25TextView.setText("PM2.5:    Unavailable");
		}
		//hourTextView.setText("Site: "+s.Name);
		String hourUsed = "Time:    Varies";
		if(s.hourUsed!=-100)
		{
			Calendar c = Globals.getTimeOriginal();
			String hour = ""+c.get(Calendar.HOUR);
			if(hour.equals("0"))
				hour="12";
			String ampm = ":00 AM";
			if(c.get(Calendar.AM_PM)==1)
				ampm=":00 PM";
			hourUsed = "Time:    "+hour+ampm+
					" ("+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.YEAR)+")";
		}
		hourTextView.setText(hourUsed);
		if(s.distance>40)		//40km = 25mi
		{
			farTextView.setVisibility(View.VISIBLE);
			farButton.setVisibility(View.VISIBLE);
		}
		else
		{
			farTextView.setVisibility(View.INVISIBLE);
			farButton.setVisibility(View.INVISIBLE);
		}



		//Forecast view

		if(lv==null)
		{
			ListView lv2 = (ListView) findViewById(R.id.listViewForecast);
			if(lv2!=null) lv=lv2;
		}
		if(lv!=null)
			setForecastView(s);
	}

	// Populates "main page" forecast table
	public void setForecastView(Site s)
	{
		Calendar currentDay = Globals.getTimeOriginal();
		Calendar c = Globals.getTimeOriginal();
		//int startingHour = -12;
		c.add(Calendar.HOUR_OF_DAY, startingHour);
		ArrayList<ForecastViewItem> forecastViewItems = new ArrayList<>();
		//forecastViewItems.add(new ForecastViewItem("Hour", "AQI", "Ozone", "PM2.5", Color.rgb(128,128,128), false));
		int day = -2;
		int prevDay = -2;
		for (int i = 0; i < 36; i++)
		{
			int hour = c.get(Calendar.HOUR);
			String hourString = "" + hour;
			if (hour == 0) hourString = "12";

			int ampm = c.get(Calendar.AM_PM);
			String ampmString = "AM";
			if (ampm > 0) ampmString = "PM";

			int hourlyAQI = s.getAQI(i);
			String hourlyAQIString = "" + hourlyAQI;
			if (hourlyAQI <= 0) hourlyAQIString = "-";

			float o3 = -1;
			if(Globals.useMethod.equals("AP"))
				o3 = s.OZONE8hr_ap[i];
			else if(Globals.useMethod.equals("AN"))
				o3 = s.OZONE8hr_an[i];
			String o3String = "" + o3;
			if (o3 <= 0) o3String = "-";

			float pm25 = -1;
			if(Globals.useMethod.equals("AP"))
				pm25 = s.PM258hr_ap[i];
			else if(Globals.useMethod.equals("AN"))
				pm25 = s.PM258hr_an[i];
			String pm25String = "" + pm25;
			if (pm25 <= 0) pm25String = "-";

			int clr = 0;
			if(c.get(Calendar.DAY_OF_YEAR) < currentDay.get(
					Calendar.DAY_OF_YEAR) ||
					(currentDay.get(Calendar.DAY_OF_MONTH)==1
							&& currentDay.get(Calendar.MONTH)==Calendar.JANUARY)
							&& c.get(Calendar.MONTH)==Calendar.DECEMBER
					)
				//clr = Color.rgb(220,220,0);		//Yesterday
				day=-1;
			else if (c.get(Calendar.DAY_OF_YEAR)==currentDay.get(Calendar.DAY_OF_YEAR))
				//clr = Color.rgb(0,200,0);		//Today
				day=0;
			else
				//clr = Color.rgb(0,180,250);		//Tomorrow
				day=1;

			if(hourlyAQI<=0)		clr=Color.rgb(255,255,255);		//White
			else if(hourlyAQI<=50)	clr=Color.argb(64,0,255,0);		//Green
			else if(hourlyAQI<=100)	clr=Color.argb(64,220,220,0);	//Yellow
			else if(hourlyAQI<=150)	clr=Color.argb(64,255,126,0);	//Orange
			else if(hourlyAQI<=200)	clr=Color.argb(64,220,0,0);		//Red
			else if(hourlyAQI<=300)	clr=Color.argb(64,180,0,90);	//Purple
			else if(hourlyAQI<=500)	clr=Color.argb(96,126,0,35);	//Maroon
			else					clr=Color.rgb(255,255,255);		//Shouldn't get here!!! (Black)
			if(day>prevDay)
			{
				String dateString = ""+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.YEAR);
				if(day==-1)
					forecastViewItems.add(new ForecastViewItem("Yesterday", dateString, "", "", Color.rgb(187,187,187), true));
				else if(day==0)
					forecastViewItems.add(new ForecastViewItem("Today", dateString, "", "", Color.rgb(187,187,187), true));
				else if(day==1)
					forecastViewItems.add(new ForecastViewItem("Tomorrow", dateString, "", "", Color.rgb(187,187,187), true));
			}
			prevDay=day;
			forecastViewItems.add(new ForecastViewItem(hourString + ampmString, hourlyAQIString, o3String, pm25String, clr, false));
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		ForecastViewListAdapter adapter = new ForecastViewListAdapter(this, forecastViewItems);
		lv.setAdapter(adapter);
	}

	public void clearSearch(View view)
	{
		EditText et = (EditText)findViewById(R.id.editTextCitySearch);
		et.setText("");
	}
	public void searchCityName(View view)
	{
		hideKeyboard();
		EditText et = (EditText)findViewById(R.id.editTextCitySearch);
		String s = et.getText().toString();
		if(s.equals("")) return;
		//Site site = findCitynamesSite(s);
		if(citySearchThread==null)
		{
			citySearchThread = new GeocoderThread(s, this);
			citySearchThread.start();
		}
	}
	private class GeocoderThread extends Thread
	{
		public String s;
		public Site site;
		public Context context;

		public GeocoderThread(String _s, Context _context)
		{
			s=_s;
			context=_context;
		}

		@Override
		public void run()
		{
			site = findCitynamesSite(s);
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					if (site != null)
						setDropDownSelection(site.Name);
					if (citySearchThread != null)
					{
						try
						{
							citySearchThread.join();
							citySearchThread = null;
						}
						catch(InterruptedException e)
						{
							Log.d(TAG, "InterruptedException trying to citySearchThread.join()");
							citySearchThread = null;
						}
					}
				}
			});
		}

		public Site findCitynamesSite(String name)
		{
			Log.d("Geocoder", "starting");
			Geocoder g = new Geocoder(context);
			try
			{
				List<Address> l = g.getFromLocationName(name, 1);
				final Address a;
				try
				{
					a = l.get(0);
				}
				catch(IndexOutOfBoundsException e)
				{
					Log.d("Geocoder", "No results?");
					return null;
				}
				double lat = a.getLatitude();
				double lon = a.getLongitude();
				Log.d("Geocoder", lat + ","+lon);
				//Toast.makeText(this, "Lat "+lat+"\nLon "+lon, Toast.LENGTH_LONG).show();
				//Toast.makeText(this, "Address: "+a.toString(), Toast.LENGTH_LONG).show();
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						Toast.makeText(context, "City: "+a.getFeatureName()+"\nState: "+a.getAdminArea(), Toast.LENGTH_LONG).show();
					}
				});
				return Globals.siteList.getClosest(lat, lon);
			}
			catch (IOException e)
			{
				Log.d("Geocoder", "IOException");
				return null;
			}
		}
	}

	public void currentClicked(View view)
	{
		LinearLayout ll = (LinearLayout)findViewById(R.id.LayoutCurrent);
		ll.setVisibility(View.VISIBLE);
		LinearLayout ll2 = (LinearLayout)findViewById(R.id.LayoutForecast);
		ll2.setVisibility(View.INVISIBLE);
		showingForecast=false;
		ToggleButton tbC = (ToggleButton)findViewById(R.id.toggleButtonCurrent);
		tbC.setChecked(true);
		ToggleButton tbF = (ToggleButton)findViewById(R.id.toggleButtonForecast);
		tbF.setChecked(false);
	}
	public void forecastClicked(View view)
	{
		LinearLayout ll = (LinearLayout)findViewById(R.id.LayoutCurrent);
		ll.setVisibility(View.INVISIBLE);
		LinearLayout ll2 = (LinearLayout)findViewById(R.id.LayoutForecast);
		ll2.setVisibility(View.VISIBLE);
		showingForecast=true;
		ToggleButton tbC = (ToggleButton)findViewById(R.id.toggleButtonCurrent);
		tbC.setChecked(false);
		ToggleButton tbF = (ToggleButton)findViewById(R.id.toggleButtonForecast);
		tbF.setChecked(true);
	}

	/*
	public void updateAllSites(View view)
	{
		//Currently changes the main tab labels every time it gets a new site's updates
		for(int i=0; i<Globals.siteList.sites.size(); i++)
		{
			Site s = Globals.siteList.sites.get(i);
			s.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
		}
	}//*/

	public void togglePinMode(View view)
	{
		/*if(pinMode.equals("All"))
		{ pinMode = "Ozone"; }
		else if(pinMode.equals("Ozone"))
		{ pinMode = "PM25"; }
		else if(pinMode.equals("PM25"))
		{ pinMode = "None"; }
		else if(pinMode.equals("None"))
		{ pinMode = "All"; }*/

		Site siteUsing;
		if(currentSite!=null) { siteUsing = currentSite; }
		else { siteUsing = Globals.siteList.getClosest(latitude, longitude); }
		setSiteMarkers(siteUsing, mainMap);

		TextView tv = (TextView)view;
		if(tv!=null)
			tv.setText(pinMode);
	}
	public void openNavDrawer(View view)
	{
		Log.d("Open navdrawer", "Open navdrawer called");
		navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
		navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	public void swapO3PMOverlay(View view)
	{
		hourShown=startingHour-1;
		Log.d("MapStuff", "Looking to remove overlay...");
		if(overlay!=null)
		{
			overlay.remove();
			Log.d("MapStuff", "Overlay removed...");
		}
		advanceOverlay(overlayType, 1);
	}

	public void onItemClick(AdapterView parent, View view, int position, long id)
	{
		Log.d("Drawer", "Item #"+position+" selected");
		selectDrawerItem(position);
	}

	public void selectDrawerItem(int position)
	{
		if(position==1)
		{
			togglePinMode(null);
		}
	}

	public void setDrawerItemButton(int position, Button button)
	{
		final int p = position;
		if(navButtons[position]==null)
		{
			navButtons[position]=button;
			navButtons[position].setOnClickListener(new Button.OnClickListener()
			{
				public void onClick(View view)
				{
					Globals.tabActivity.navButtonClicked(p);
				}
			});
			if(position==1)		//All sites
			{
				RadioButton rb = (RadioButton)navButtons[position];
				rb.setChecked(true);
			}
			if(position==6&&overlayType.equals("Ozone"))		//Ozone
			{
				RadioButton rb = (RadioButton)navButtons[position];
				rb.setChecked(true);
			}
			if(position==7&&overlayType.equals("PM25"))		//PM2.5
			{
				RadioButton rb = (RadioButton)navButtons[position];
				rb.setChecked(true);
			}
			if(position==8&&overlayType.equals("None"))		//None
			{
				RadioButton rb = (RadioButton)navButtons[position];
				rb.setChecked(true);
			}

		}
	}

	public void navButtonClicked(int position)
	{
		Log.d("Drawer", "Button #"+position+" clicked!");
		int minSitesButtons=1;
		int maxSitesButtons=4;
		if(position>=minSitesButtons&&position<=maxSitesButtons)		//Sites shown
		{
			for(int i=minSitesButtons; i<=maxSitesButtons; i++)
			{
				RadioButton rb = (RadioButton)navButtons[i];
				rb.setChecked(false);
			}
			RadioButton rb = (RadioButton)navButtons[position];
			rb.setChecked(true);
			if(position==1) pinMode="All";
			if(position==2) pinMode="Ozone";
			if(position==3) pinMode="PM25";
			if(position==4) pinMode="None";
			togglePinMode(null);
		}
		int minOverlayButtons=6;
		int maxOverlayButtons=8;
		if(position>=minOverlayButtons&&position<=maxOverlayButtons)		//Overlay shown
		{
			for(int i=minOverlayButtons; i<=maxOverlayButtons; i++)
			{
				RadioButton rb = (RadioButton)navButtons[i];
				rb.setChecked(false);
			}
			RadioButton rb = (RadioButton)navButtons[position];
			rb.setChecked(true);
			if(position==6) overlayType="Ozone";
			if(position==7) overlayType="PM25";
			if(position==8) overlayType="None";
			swapO3PMOverlay(null);
		}
	}

	public void hideKeyboard()
	{
		EditText et = (EditText) findViewById(R.id.editTextCitySearch);
		if(et!=null)
			et.clearFocus();
		InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(inputManager!=null)
		{
			View view = getCurrentFocus();
			if(view!=null)
					inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	//Help tab
	public void webLinkClicked(View view)
	{
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://lar.wsu.edu/airpact/"));
		startActivity(browserIntent);
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
		if(animThreadHasStarted)
		{
			animThread = new AnimationThread();
			animThread.start();
			//animThreadHasStarted = true;
		}
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
		if(animThread!=null&&animThread.isAlive())
		{
			animThread.shouldContinue = false;
			try
			{
				animThread.join();
				animThread=null;
			}
			catch (InterruptedException e)
			{
				Log.d(TAG, "InterruptedException trying to animThread.join()");
				animThread=null;
			}
		}
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
			//findViewById(R.id.buttonGetCoords).setVisibility(View.INVISIBLE);

			Site closest = Globals.siteList.getClosest(latitude, longitude);
			//closest.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, hourTextView);
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

	// Sets up forecast dropdown, with default selection being closest city
	public void dropDownIsReady()
	{
		Log.d(TAG, "@@@In dropDownIsReady()");
		dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
		if(dropDown==null) Log.d(TAG, "@@@dropDown==null");
		//mainTabFragment = mSectionsPagerAdapter.mtf;
		//Log.e("CrashBug", "In dropDownIsReady() mainTabFragment was not set to mSectionsPagerAdapter.mtf");
		if(mainTabFragment!=null)
		{
			Log.d(TAG, "@@@mainTabFragment!=null");
			dropDown = mainTabFragment.dropDown;
			farTextView = mainTabFragment.farTextView;
			farButton = mainTabFragment.farButton;
			farTextView.setVisibility(View.INVISIBLE);
			farButton.setVisibility(View.INVISIBLE);
			aqiTextView = mainTabFragment.aqiTextView;
			o3TextView = mainTabFragment.o3TextView;
			pm25TextView = mainTabFragment.pm25TextView;
			hourTextView = mainTabFragment.siteTextView;
			useMethodTextView = mainTabFragment.useMethodTextView;
			TextView tv = mainTabFragment.questionmarkTextView;
			tv.setPaintFlags(tv.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
			if(dropDown==null) Log.d(TAG, "asdf@@@dropDown==null");

			//ToggleButton tbC = mainTabFragment.toggleButtonCurrent;
			//tbC.setChecked(true);
			currentClicked(null);
		}
		else
		{
			Log.d(TAG, "@@@mainTabFragment==null");
		}
		if(dropDown!=null)
		{
			Log.d(TAG, "DROPDOWN HAS BEEN SET!!!!!!!!!!");

			//Log.d(TAG, "Listener HAS BEEN SET!!!!!!!!!!");

			if(!Globals.siteList.setDropdown(dropDown, (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)))
			{
				populateDropdown(dropDown);
				DialogFragment dialog = new GoToLocationSettingsDialog();
				dialog.show(getFragmentManager(), "turnOnLocation");
			}
			//Log.d(TAG, "DROPDOWN HAS BEEN POPULATED!!!!!!!!!!");
			Site closest = Globals.siteList.getClosest(latitude, longitude);
			if (closest != null)
			{
				if(closest.distance>40)
					farTextView.setVisibility(View.VISIBLE);		//40km=25mi
				Log.d(TAG, "Closest=="+closest.Name);
				Log.d(TAG, "Closest.distance=="+closest.distance);
				setDropDownSelection(closest.Name);
			}
			else
			{
				Log.d(TAG, "Closest==NULL");
			}
		}
	}

	public void saveButtonClicked(View view) { save(); }
	public void loadButtonClicked(View view) { load(); }

	// TODO
	public void save()
	{
		String filename = "airpactData";
		String outputString;
		StringBuilder sb = new StringBuilder();

		sb.append(Globals.defaultOverlay); sb.append("\r\n");
		sb.append(Globals.useMethod); sb.append("\r\n");
		sb.append(Globals.lastLatitude); sb.append("\r\n");
		sb.append(Globals.lastLongitude); sb.append("\r\n");

		sb.append("SITELIST:\r\n");
		for(int i=0; i<Globals.siteList.sites.size(); i++)
		{
			Site s = Globals.siteList.sites.get(i);
			sb.append(s.SiteName); sb.append(",");
			sb.append(s.AQSID); sb.append(",");
			sb.append(s.Latitude); sb.append(",");
			sb.append(s.Longitude); sb.append(",");
			sb.append(s.hasOzone); sb.append(",");
			sb.append(s.hasPM25); sb.append("\r\n");
		}
		sb.append(":ENDSITELIST\r\n");


		outputString = sb.toString();
		Log.d("SaveLoad", "Saved, including sites...");

		//Log.d("GlobalsSave", "String to write is: "+outputString);

		try {
			FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream.write(outputString.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO
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
		if(!haveSetDefaults)
		{
			haveSetDefaults=true;
			overlayType=Globals.defaultOverlay;
		}
	}
}
