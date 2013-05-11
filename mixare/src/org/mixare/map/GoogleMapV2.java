package org.mixare.map;

import android.os.Bundle;
import java.util.Set;

import org.mixare.R;

import org.mixare.marker.POIMarker;
import org.mixare.MixView;
import org.mixare.DataView;

import android.support.v4.app.FragmentActivity;
import android.location.Location;
import android.view.Menu;
import android.view.MenuItem;

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



public class GoogleMapV2 extends FragmentActivity implements OnMapClickListener{ 
	
	private DataView dataView;
	private POIMarker markerV1;
	private Set<org.mixare.lib.marker.Marker> markersV1;
	
	private UiSettings UISettings;
	private OnMapClickListener listener;

	private GoogleMap myMap;
	private int mapType = GoogleMap.MAP_TYPE_NORMAL;


	protected void onCreate(Bundle savedInstanceState) {
		  
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.googlemapsv2);
	    
	    dataView = MixView.getDataView();
	    
	    // getting the map
	    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
	    myMap = mapFragment.getMap();	 
	    
	    // set map type
	    myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	    
	    // set click listener
	    myMap.setOnMapClickListener(this);
	    
	    // showing location button
	    UISettings = myMap.getUiSettings();
	    UISettings.setMyLocationButtonEnabled(true);
	    myMap.setMyLocationEnabled(true);
	    
	    // showing compass
	    UISettings.setCompassEnabled(true);
	    
	    // zoom gestures enabled
	    UISettings.setZoomGesturesEnabled(true);
	    
	    // scroll gestures enabled
	    UISettings.setScrollGesturesEnabled(true);
	    
	    // rotate gestures enabled
	    UISettings.setRotateGesturesEnabled(true);
	    
	    //click on map
	    myMap.setOnMapClickListener(listener); 
	   
	    final LatLng KIEL = new LatLng(53.551, 9.993);
	    Marker kiel = myMap.addMarker(new MarkerOptions()
	        .position(KIEL)
	        .title("Kiel")
	        .snippet("Kiel is cool")
	        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_link)));
	    
	    
	    POIMarker testMarker = getMarkerV1(0);
	    final LatLng positionMarker = new LatLng(testMarker.getLatitude(), testMarker.getLongitude());
	    Marker firstMarker = myMap.addMarker(new MarkerOptions()
	    		.position(positionMarker)
	    		.title("First POI")
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
	
	// private methods
	private Set<org.mixare.lib.marker.Marker> getMarkersListV1(){
		return markersV1 = dataView.getDataHandler().getMarkerList();
	}
	
	private POIMarker getMarkerV1(int index){
		return markerV1 = (POIMarker) dataView.getDataHandler().getMarker(index);
	}
}