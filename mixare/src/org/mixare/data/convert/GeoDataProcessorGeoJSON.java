/*
 * Copyright (C) 2012- Peer internet solutions & Finalist IT Group
 * 
 * This file is part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package org.mixare.data.convert;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.mixare.marker.POIMarker;
import org.mixare.data.DataHandler;
import org.mixare.lib.HtmlUnescape;

import org.mixare.lib.marker.Marker;

import android.util.Log;

/**
 * Data processor for the geographical learning object. Converts the raw data to JSON and then to marker data.
 * @author Luca Geraci
 */
public class GeoDataProcessorGeoJSON extends DataHandler implements DataProcessor{

	public static final int MAX_JSON_OBJECTS = 1000;
	
	@Override
	public String[] getUrlMatch() {
		String[] str = new String[0]; 
		return str;
	}

	@Override
	public String[] getDataMatch() {
		String[] str = new String[0]; 
		return str;
	}
	
	@Override
	public boolean matchesRequiredType(String type) {
		return true; //this datasources has no required type, it will always match.
	}

	@Override
	public List<Marker> load(String rawData, int taskId, int colour) throws JSONException {
		String id = "";
		String title = "";
		String description = "";  //not used by Mixare
		String URL = "";
		String meaning = "";      //not used by Mixare
		JSONObject geometry = null;
		String type = "";         //not used by Mixare
		JSONArray coordinates = null;
		double lat = 0, lng = 0;
		
		List<Marker> markers = new ArrayList<Marker>();
		JSONObject root = convertToJSON(rawData);
		Log.v("root", root.toString());
		JSONArray dataArray = root.getJSONArray("features");
		int top = Math.min(MAX_JSON_OBJECTS, dataArray.length());
		
		Marker ma = null;

		for (int i = 0; i < top; i++) {
			JSONObject object = dataArray.getJSONObject(i);
			JSONObject properties = object.getJSONObject("properties");
			
			
			if (properties.has("obj_id")){
				 id = properties.getString("obj_id");
			}
			
			if (properties.has("title")){
				title = properties.getString("title");
			}
			
			if (properties.has("description")){
				description = properties.getString("description");
			}
			
			if (properties.has("URL")){
				URL = properties.getString("URL");
			}
			
			if (properties.has("meaning")){
				meaning = properties.getString("meaning");
			}
			
			geometry = object.getJSONObject("geometry");
			
			if (geometry.has("type")){
				type = geometry.getString("type");
			}
			
			if (geometry.has("coordinates")){
				coordinates = geometry.getJSONArray("coordinates");
			}
			
			if (type.equals("Point")){
				
				lat = (Double) coordinates.get(0); 
				lng = (Double) coordinates.get(1);

				ma = new POIMarker(
						id,
						HtmlUnescape.unescapeHTML(title),
						meaning,
						lat, 
						lng, 
						0, // elevation to be implemented, 
						URL, 
						taskId, colour);
				
				markers.add(ma);
			}
			
			if (type.equals("MultiPoint")){
				int length = coordinates.length();
				for (int j=0;j<length;j++){
					JSONArray sub_coordinates = coordinates.getJSONArray(j);
					lat = (Double) sub_coordinates.get(0);
					lng = (Double) sub_coordinates.get(1);
					
					ma = new POIMarker(
							id,
							HtmlUnescape.unescapeHTML(title),
							meaning,
							lat, 
							lng, 
							0, // elevation to be implemented, 
							URL, 
							taskId, colour);
					
					markers.add(ma);			
				}	
		    }	
		}
		
		return markers;
	}
	
	private JSONObject convertToJSON(String rawData){
		try {
			Log.v("rawData", rawData.toString());
			return new JSONObject(rawData);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}	
}
