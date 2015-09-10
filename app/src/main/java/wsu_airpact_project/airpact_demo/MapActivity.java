package wsu_airpact_project.airpact_demo;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends Activity implements OnMapReadyCallback
{
//	private static final String DEBUG_TAG = "MapActivityTag";

	protected GoogleMap mainMap=null;
	protected Double latitude;
	protected Double longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		//MapFragment mF = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		//mF.getMapAsync(this);

		//Intent intent = getIntent();
		//latitude = intent.getDoubleExtra(MyActivity.CUR_LAT, 0.0);
		//longitude = intent.getDoubleExtra(MyActivity.CUR_LON, 0.0);
		latitude = Globals.lastLatitude;
		longitude = Globals.lastLongitude;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_map, menu);
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
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMapReady(GoogleMap map)
	{
		mainMap=map;
		LatLng pullmanDefault = new LatLng(46.7338, -117.1673);
		LatLng currentLocation;
//		Site pullman = new Site("Pullman-Dexter Ave", "530750003", 46.7245, -117.1801);

		if(latitude==0.0&&longitude==0)
		{
			currentLocation=pullmanDefault;
		}
		else
		{
			currentLocation = new LatLng(latitude, longitude);
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
}
