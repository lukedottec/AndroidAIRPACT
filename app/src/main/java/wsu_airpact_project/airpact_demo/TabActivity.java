package wsu_airpact_project.airpact_demo;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
		DrawerLayout.DrawerListener
{
	//Map tab
	protected GoogleMap mainMap = null;
	private static int zoomLevel = 10;
	protected Double latitudeMap;
	protected Double longitudeMap;
	protected boolean mapFound = false;
	protected boolean mainFound = false;

	protected MapInfoWindow miw = null;
	protected GroundOverlayOptions goo = null;
	protected GroundOverlay overlay = null;
	//protected ProgressDialog pDialog;
	protected int hourShown;
	protected int maxHours = 24;
	//protected Bitmap bitmap;
	protected Bitmap[] bitmapsO3;
	protected Bitmap[] bitmapsPM25;
	public String overlayType = "Ozone";
	public AnimationThread animThread = null;
	protected boolean animThreadHasStarted = false;
	public String pinMode = "All";
	protected DrawerLayout navDrawer = null;


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

	public Site currentSite;
	public static TextView aqiTextView;
	public static TextView o3TextView;
	public static TextView pm25TextView;
	public static TextView siteTextView;
	public static TextView farTextView;
	public static Button farButton;
	private int startingHour = -12;

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
		Globals.tabActivity = this;
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
			//siteTextView = (TextView) findViewById(R.id.textViewSiteLabel);
			//Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);
			//pullman.getLatestData((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE), o3TextView, pm25TextView, siteTextView);
			dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
			if(dropDown==null) Log.d(TAG, "dropDown==null");
			mainTabFragment = mSectionsPagerAdapter.mtf;
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

			//Log.d(TAG, "in getItem("+position+")");
			TabFragment tf = TabFragment.newInstance(position + 1);
			if(position == 0) mtf = (MainTabFragment)tf;



			dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
			//if(dropDown==null) Log.d(TAG, "!!!dropDown==null");
			mainTabFragment = mSectionsPagerAdapter.mtf;
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
			//View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
			//return rootView;
			return inflater.inflate(R.layout.fragment_tab, container, false);
		}
	}



	//Map Stuff
	@Override
	public void onMapReady(GoogleMap map)
	{
		Log.d("MapTag", "Starting onMapReady");
		map.setInfoWindowAdapter(miw);
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerClickListener(this);
		mainMap=map;
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

		map.setMyLocationEnabled(true);
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
				.image(BitmapDescriptorFactory.fromResource(R.mipmap.ic_information))
				.transparency(.5f)
				.positionFromBounds(bounds);

		//overlay = map.addGroundOverlay(goo);
		Log.d("ImageStuff", "Calling setImageOverlay(null)");
		setImageOverlay(" ", -1);
		Log.d("ImageStuff", "Calling LoadImage().execute");
		overlayType = "Ozone";
		for(int i=0; i<maxHours; i++)
		{
			LoadImage li = new LoadImage();
			li.setValues(overlayType, i);
			li.execute(getOverlayURL(overlayType, i));
		}
		//new LoadImage().execute(url);
	}

	public String getOverlayURL(String type, int hourDelta)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, hourDelta-10);
		String url = null;
		if(type.equals("Ozone"))
			url = "http://www.lar.wsu.edu/airpact/gmap/images/anim/species/"+
					c.get(Calendar.YEAR)+"/"+c.get(Calendar.YEAR)+"_"+addZero(c.get(Calendar.MONTH)+1)+"_"+addZero(c.get(Calendar.DAY_OF_MONTH))+
					"/airpact4_08hrO3_"+c.get(Calendar.YEAR)+addZero(c.get(Calendar.MONTH)+1)+addZero(c.get(Calendar.DAY_OF_MONTH))+addZero(c.get(Calendar.HOUR_OF_DAY))+".gif";
		if(type.equals("PM25"))
			url = "http://www.lar.wsu.edu/airpact/gmap/images/anim/species/"+
					c.get(Calendar.YEAR)+"/"+c.get(Calendar.YEAR)+"_"+addZero(c.get(Calendar.MONTH)+1)+"_"+addZero(c.get(Calendar.DAY_OF_MONTH))+
					"/airpact4_24hrPM25_"+c.get(Calendar.YEAR)+addZero(c.get(Calendar.MONTH)+1)+addZero(c.get(Calendar.DAY_OF_MONTH))+addZero(c.get(Calendar.HOUR_OF_DAY))+".gif";
		Log.d("ImageStuff", "     with \""+url+"\"");
		//new LoadImage().execute("http://www.lar.wsu.edu/airpact/gmap/images/anim/species/2015/2015_10_02/airpact4_08hrO3_2015100215.gif");
		return url;
	}

	public String addZero(int i)
	{
		if(i>=10)
			return ""+i;
		return "0"+i;
	}

	public void setOverlayBitmap(String type, int hourDelta, Bitmap b)
	{
		if (type.equals("Ozone"))
		{
			bitmapsO3[hourDelta] = b;
			if(hourDelta == 0)
			{
				setImageOverlay(type, 0);
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
			bitmapsPM25[hourDelta] = b;
			if(hourDelta == 0)
			{
				setImageOverlay(type, 0);
				if(animThread==null)
				{
					animThread = new AnimationThread();
					animThread.start();
					animThreadHasStarted = true;
				}
			}
		}
	}

	public void advanceOverlay(String type)
	{
		int nextHour = hourShown+1;
		if(nextHour >= maxHours)
			nextHour = 0;
		if(type.equals("Ozone"))
		{
			if (bitmapsO3[nextHour] == null)
			{
				Log.d("MapThreading", "The next image #" + nextHour + " is null");
				hourShown = -1;
				if(bitmapsO3[0]!=null)
				{
					setImageOverlay(type, 0);
					hourShown = 0;
					Log.d("MapThreading", "Set next image #" + nextHour);
				}
			} else
			{
				setImageOverlay(type, nextHour);
				hourShown = nextHour;
				Log.d("MapThreading", "Set next image #" + nextHour);
			}
		}
		if(type.equals("PM25"))
		{
			if (bitmapsPM25[nextHour] == null)
			{
				Log.d("MapThreading", "The next image #" + nextHour + " is null");
				hourShown = -1;
				if(bitmapsPM25[0]!=null)
				{
					setImageOverlay(type, 0);
					hourShown = 0;
					Log.d("MapThreading", "Set next image #" + nextHour);
				}
			} else
			{
				setImageOverlay(type, nextHour);
				hourShown = nextHour;
				Log.d("MapThreading", "Set next image #" + nextHour);
			}
		}
	}

	public void setImageOverlay(String type, int hourDelta)
	{
		if(hourDelta==-1) return;
		Log.d("ImageStuff", "Setting Image overlay");
		if(goo!=null)
		{
			if (type.equals("Ozone"))
			{
				if (bitmapsO3[hourDelta] != null)
				{
					if (overlay != null)
						overlay.remove();
					goo.image(BitmapDescriptorFactory.fromBitmap(bitmapsO3[hourDelta]));
					overlay = mainMap.addGroundOverlay(goo);
					//Toast.makeText(this, "Image changed", Toast.LENGTH_LONG).show();
				} else
				{
					Log.d("ImageStuff", "b==null");
				}
			}
			if (type.equals("PM25"))
			{
				if (bitmapsPM25[hourDelta] != null)
				{
					if (overlay != null)
						overlay.remove();
					goo.image(BitmapDescriptorFactory.fromBitmap(bitmapsPM25[hourDelta]));
					overlay = mainMap.addGroundOverlay(goo);
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
		//overlay.setImage();
	}

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
				if(Calendar.getInstance().before(c))
					continue;
				Log.d("MAPThreading", "In loop now, adding seconds");
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
		}
	}

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
			return bmap;
		}
		protected void onPostExecute(Bitmap image)
		{
			if(image!=null)
			{
				//Globals.tabActivity.setImageOverlay(image);
				Globals.tabActivity.setOverlayBitmap(type, hourDelta, image);
				Log.d("ImageStuff", "LoadImage: image!=null");
				//pDialog.dismiss();
			}
			else
			{
				//pDialog.dismiss();
				Log.d("ImageStuff", "LoadImage: image==null with hour "+hourDelta);
				//Toast.makeText(TabActivity.this, "Overlay image not found for hour "+hourDelta, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void onInfoWindowClick(Marker m)
	{
		Site s = Globals.siteList.getByName(m.getTitle());
		if(s==null) return;
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

	public void onDrawerClosed(View drawer)
	{
		Log.d("Drawer", "Drawer closed, locking...");
		navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}
	public void onDrawerOpened(View drawer) { }
	public void onDrawerSlide(View drawer, float offset) { }
	public void onDrawerStateChanged(int newstate) { }


	//Main Tab
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		//String city = (String)parent.getItemAtPosition(pos);
		//Log.d(DEBUG_TAG, "Dropdown selected"+pos+": "+city);
		if(mainMap!=null)
		{
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
		currSite.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));//, o3TextView, pm25TextView, siteTextView);
	}
	public void onNothingSelected(AdapterView<?> parent) {}

	public void populateDropdown(Spinner spinner)
	{
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

	public void setDropDownSelection(String city)
	{
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
		o3TextView.setText("Ozone: "+s.OZONEavg_ap);
		pm25TextView.setText("PM2.5: "+s.PM25avg_ap);
		siteTextView.setText("Site: "+s.Name);
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
		ListView lv = (ListView)findViewById(R.id.listViewForecast);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, startingHour);
		ArrayList<String> strings1 = new ArrayList<>();
		strings1.add("Hour     AQI    Ozone     PM2.5");
		for(int i=0; i<36; i++)
		{
			int hour = c.get(Calendar.HOUR);
			String hourString = ""+hour;
			if(hour==0) hourString = "12";
			int ampm=c.get(Calendar.AM_PM);
			String ampmString = "AM";
			if(ampm>0) ampmString = "PM";
			int hourlyAQI = s.getAQI(i);
			String hourlyAQIString = ""+hourlyAQI;
			if(hourlyAQI<=0) hourlyAQIString = "-";
			float o3 = s.OZONE8hr_ap[i];
			String o3String = ""+o3;
			if(o3<=0) o3String = "-";
			float pm25 = s.PM258hr_ap[i];
			String pm25String = ""+pm25;
			if(pm25<=0) pm25String = "-";

			//strings1.add(""+hourString+ampmString+"      "+hourlyAQIString+"       "+o3String+"        "+pm25String);
			String str = String.format("%-10s %-10s %-10s %-10s",hourString+ampmString, hourlyAQIString, o3String, pm25String);
			strings1.add(str);
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		String[] strings = new String[strings1.size()];
		strings = strings1.toArray(strings);
		lv.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, strings));
	}

	public void findCitysLatLon(View view)
	{
		Log.d("Geocoder", "starting");
		EditText et = (EditText)findViewById(R.id.editText);
		Geocoder g = new Geocoder(this);
		String s = et.getText().toString();
		try
		{
			List<Address> l = g.getFromLocationName(s, 1);
			Address a = l.get(0);
			double lat = a.getLatitude();
			double lon = a.getLongitude();
			Log.d("Geocoder", lat + ","+lon);
			//Toast.makeText(this, "Lat "+lat+"\nLon "+lon, Toast.LENGTH_LONG).show();
			//Toast.makeText(this, "Address: "+a.toString(), Toast.LENGTH_LONG).show();
			Toast.makeText(this, "City: "+a.getFeatureName()+"\nState: "+a.getAdminArea(), Toast.LENGTH_LONG).show();
			Site site = Globals.siteList.getClosest(lat, lon);
			if(site!=null)
			{
				setDropDownSelection(site.Name);
			}
		}
		catch (IOException e)
		{
			Log.d("Geocoder", "IOException");
		}
	}

	public void goToMap(View view)
	{
		/*mViewPager.setCurrentItem(1);
		if(mainMap==null)
			return;

		LatLng markerLocation = new LatLng(currentSite.Latitude, currentSite.Longitude);
		mainMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, zoomLevel));*/


		//updateAllSites(view);
		//togglePinMode(view);
		switchForecastMode(view);
	}

	public void updateAllSites(View view)
	{
		//Currently changes the main tab labels every time it gets a new site's updates
		for(int i=0; i<Globals.siteList.sites.size(); i++)
		{
			Site s = Globals.siteList.sites.get(i);
			s.getLatestData((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
		}
	}

	public void togglePinMode(View view)
	{
		if(pinMode.equals("All"))
		{ pinMode = "Ozone"; }
		else if(pinMode.equals("Ozone"))
		{ pinMode = "PM25"; }
		else if(pinMode.equals("PM25"))
		{ pinMode = "None"; }
		else if(pinMode.equals("None"))
		{ pinMode = "All"; }

		Site siteUsing;
		if(currentSite!=null) { siteUsing = currentSite; }
		else { siteUsing = Globals.siteList.getClosest(latitude, longitude); }
		setSiteMarkers(siteUsing, mainMap);

		TextView tv = (TextView)view;
		tv.setText(pinMode);
	}

	public void openNavDrawer(View view)
	{
		Log.d("Open navdrawer", "Open navdrawer called");
		navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
		navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	public void switchForecastMode(View view)
	{
		if(!showingForecast)
		{
			FrameLayout fl = (FrameLayout)findViewById(R.id.layoutCurrent);
			fl.setVisibility(View.INVISIBLE);
			ListView lv = (ListView)findViewById(R.id.listViewForecast);
			lv.setVisibility(View.VISIBLE);
			showingForecast=true;
		}
		else
		{
			FrameLayout fl = (FrameLayout)findViewById(R.id.layoutCurrent);
			fl.setVisibility(View.VISIBLE);
			ListView lv = (ListView)findViewById(R.id.listViewForecast);
			lv.setVisibility(View.INVISIBLE);
			showingForecast=false;
		}
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

	public void dropDownIsReady()
	{
		Log.d(TAG, "@@@In dropDownIsReady()");
		dropDown = (Spinner)findViewById(R.id.spinnerinmaintab);
		if(dropDown==null) Log.d(TAG, "@@@dropDown==null");
		mainTabFragment = mSectionsPagerAdapter.mtf;
		if(mainTabFragment!=null)
		{
			Log.d(TAG, "@@@mainTabFragment!=null");
			dropDown = mainTabFragment.dropDown;
			if(dropDown==null) Log.d(TAG, "asdf@@@dropDown==null");
		}
		else
		{
			Log.d(TAG, "@@@mainTabFragment==null");
		}
		farTextView = mainTabFragment.farTextView;
		farButton = mainTabFragment.farButton;
		farTextView.setVisibility(View.INVISIBLE);
		farButton.setVisibility(View.INVISIBLE);
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
				if(closest.distance>0)
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


		aqiTextView = mainTabFragment.aqiTextView;
		o3TextView = mainTabFragment.o3TextView;
		pm25TextView = mainTabFragment.pm25TextView;
		siteTextView = mainTabFragment.siteTextView;
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
