package org.mixare.map;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mixare.R;
import org.mixare.data.convert.GloPolygonDataProcessor;
import org.mixare.lib.MixUtils;
import org.mixare.marker.POIMarker;
import org.mixare.MixListView;
import org.mixare.MixView;
import org.mixare.DataView;
import org.mixare.PluginListActivity;
import org.mixare.PluginLoaderActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.maps.GeoPoint;



public class GoogleMapV2 extends Activity implements OnMapClickListener, OnMarkerClickListener{ 
	
	private static Context context;
	private DataView dataView;

	private UiSettings UISettings;
    
	private GoogleMap myMap;
	List<PolygonOptions> polygonOptionsList = new ArrayList<PolygonOptions>();
	
	// creates the activity 
	protected void onCreate(Bundle savedInstanceState) {
		  
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.googlemapsv2);
	    
	    // getting the map
	    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
	    myMap = mapFragment.getMap();	 
	    
	    // set map type
	    myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	    
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
	    
	    //click on map enabled
	    myMap.setOnMapClickListener(this); 
	    
	    //click on marker enabled
	    myMap.setOnMarkerClickListener(this);
	    
	    
	    // move the camera instantly to my location with a zoom of 10
	    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
	    String provider = service.getBestProvider(new Criteria(), false);
	    Location location = service.getLastKnownLocation(provider);
	    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 10));

	    //drawing the points on map
	    drawPoints();
	    
	    // drawing the polygons on map 
    	drawPolygons();
	 }


	 // activity lifecycle methods
	 protected void onResume() {
		 super.onResume();
	 }
	 
	 // avoids that when screen rotates the activity is recreated
	 @Override
	 public void onConfigurationChanged(Configuration newConfig) {
	     super.onConfigurationChanged(newConfig);
	     if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	         onResume();
	     } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	         onResume();
	     }
	 }
	  
	// create options menu 
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

	// select menu option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

    	int mapType = GoogleMap.MAP_TYPE_NORMAL;
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
                
            case R.id.listview:
            	Intent showListMarkers = new Intent(this, MixListView.class);
    			showListMarkers.setAction(Intent.ACTION_VIEW);
    			startActivity(showListMarkers);
        }
        
        myMap.setMapType(mapType);
        return true;
    }
	
	// open web page of clicked marker
	public boolean onMarkerClick(Marker marker) {  
		marker.showInfoWindow();
		
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle(marker.getTitle());
		dialog.setMessage("Open WepPage?");
		dialog.setCancelable(false);

		String URL = marker.getSnippet().toString();
		final String newURL = MixUtils.parseAction(URL);
		dialog.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int whichButton) {
						try {
							dataView.getContext().getWebContentManager().loadWebPage(newURL, context);
						} catch (Exception e) {
							e.printStackTrace();
						}
						d.dismiss();
					}
				});

		dialog.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int whichButton) {
						d.dismiss();
					}
				});

		dialog.show();
		
		return true;
	}
	
	@Override
	public void onMapClick(LatLng point) {
		//Toast.makeText(getApplicationContext(),"The Location is inside of the Area", Toast.LENGTH_LONG).show();
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


	
	
	
	// PRIVATE METHODS
	
	// get number of markers v1
	private int getMarkersCountV1(){
		return dataView.getDataHandler().getMarkerCount();
	}
	
	// get list as set of markers v1
	@SuppressWarnings("unused")
	private Set<org.mixare.lib.marker.Marker> getMarkersListV1(){
		Set<org.mixare.lib.marker.Marker> markersV1;
		return markersV1 = dataView.getDataHandler().getMarkerList();
	}
	
	// get single marker v1
	@SuppressWarnings("deprecation")
	private POIMarker getMarkerV1(int index){
		@SuppressWarnings("unused")
		POIMarker markerV1;
		return markerV1 = (POIMarker) dataView.getDataHandler().getMarker(index);
	}
	
	 // creating the marker v2 based on marker v1
	private void drawPoints(){
		context = this;
	    dataView = MixView.getDataView();
		int numberMarkersV1 = getMarkersCountV1();
	    for (int i=0;i<numberMarkersV1; i++){
	    	POIMarker testMarker = getMarkerV1(i);
	        LatLng positionMarker = new LatLng(testMarker.getLatitude(), testMarker.getLongitude());
	        String url = testMarker.getURL();
	        String title = testMarker.getTitle();
	    	Marker markerV1 = myMap.addMarker(new MarkerOptions().position(positionMarker).snippet(url).title(title));
	    }
	}
   
		
	// create polygon options getting data from GloPolygonDataProcessor
	private List<PolygonOptions> createPolygonOptionsList(){
		List<PolygonOptions> polygonOptionsList = new ArrayList<PolygonOptions>();
		LatLng[] polygonCoordinates = new LatLng[50];
		List<LatLng[]> polygonCoordinatesList = new ArrayList<LatLng[]>();
		
		polygonCoordinatesList = GloPolygonDataProcessor.getPolygonCoordinates();
		
		for (int i=0; i<polygonCoordinatesList.size(); i++){
			PolygonOptions polygonOptions = new PolygonOptions();
			polygonCoordinates = polygonCoordinatesList.get(i);
			for (int j=0; j<polygonCoordinates.length; j++){
				if (polygonCoordinates[j] == null){
					break;
				}
				polygonOptions.add(new LatLng(polygonCoordinates[j].latitude,polygonCoordinates[j].longitude));
			}
			polygonOptionsList.add(polygonOptions);
		}
		return polygonOptionsList;	
	}
	
	// draw polygons with data received from createPolygonOptions()
	private void drawPolygons(){
		polygonOptionsList = createPolygonOptionsList();
		for (int i=0; i<polygonOptionsList.size(); i++){
    		PolygonOptions polygonOptions = polygonOptionsList.get(i);
		    polygonOptions.strokeColor(Color.RED);
		    polygonOptions.strokeWidth(5);
		    myMap.addPolygon(polygonOptions);
	    }
	}
}
 