package org.mixare.map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.actionbarsherlock.view.SubMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.mixare.R;

import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;


public class GoogleMapV2 extends FragmentActivity implements OnMapClickListener{ 
	
	private UiSettings UISettings;
	private OnMapClickListener listener;

	final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap myMap;
	private int mapType = GoogleMap.MAP_TYPE_NORMAL;

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		  
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.googlemapsv2);
	    
	    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
	    myMap = mapFragment.getMap();	    
	    myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	    myMap.setOnMapClickListener(this);
	    
	    //location button
	    myMap.setMyLocationEnabled(true);
	    UISettings = myMap.getUiSettings();
	    UISettings.setMyLocationButtonEnabled(true);
	    
	    
	    UISettings.setCompassEnabled(true);
	    UISettings.setZoomGesturesEnabled(true);
	    UISettings.setScrollGesturesEnabled(true);
	    UISettings.setRotateGesturesEnabled(true);
	    
	    //click on map
	    myMap.setOnMapClickListener(listener); 
	   
	    Marker kiel = myMap.addMarker(new MarkerOptions()
	        .position(KIEL)
	        .title("Kiel")
	        .snippet("Kiel is cool")
	        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_link)));

	    // Move the camera instantly to hamburg with a zoom of 15.
	    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KIEL, 15));

	    // Zoom in, animating the camera.
	    myMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	    
	    // Instantiates a new Polygon object and adds points to define a rectangle
	    PolygonOptions polyOptions = new PolygonOptions()
	                  .add(new LatLng(55.735, 10.936),
	                       new LatLng(56.733, 11.093),
	                       new LatLng(53.968, 9.984),
	                       new LatLng(63.942, 11.733),
	                       new LatLng(55.735, 10.936));
	    
	    // Get back the mutable Polygon
	    Polygon polygon = myMap.addPolygon(polyOptions);
	    polygon.setStrokeWidth(3f);
	  }
	  
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.normal_map:
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                break;

            case R.id.satellite_map:
            	mapType = GoogleMap.MAP_TYPE_SATELLITE;
                break;

            case R.id.terrain_map:
            	mapType = GoogleMap.MAP_TYPE_TERRAIN;
                break;

            case R.id.hybrid_map:
            	mapType = GoogleMap.MAP_TYPE_HYBRID;
                break;
        }
        
        myMap.setMapType(mapType);
        return true;
    }

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
	}
} 
