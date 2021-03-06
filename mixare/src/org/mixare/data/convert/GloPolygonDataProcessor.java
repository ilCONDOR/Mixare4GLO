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


import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import org.mixare.data.DataHandler;
import org.mixare.data.DataSource;

import org.mixare.lib.HtmlUnescape;
import org.mixare.lib.marker.Marker;
import org.mixare.marker.POIMarker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.android.gms.maps.model.LatLng;

import android.text.TextUtils;
import android.util.Log;

public class GloPolygonDataProcessor extends DataHandler implements DataProcessor {
	
	private static List<LatLng[]> polygonCoordinatesList = new ArrayList<LatLng[]>();
	private static String[] singleID;
	private static String[] singleTitle;
	private static String[] singleDescription;
	private static String[] singleMeaning;
	private static String[] singleURL;

	@Override
	public String[] getUrlMatch() {
		String[] str = {"learning_object_poly", "polygons"};
		return str;
	}

	@Override
	public String[] getDataMatch() {
		String[] str = {"gml:Polygon"};
		return str;
	}
	
	@Override
	public boolean matchesRequiredType(String type) {
		if(type.equals(DataSource.TYPE.GloPolygon.name())){
			return true;
		}
		return false;
	}

	@Override
	public List<Marker> load(String rawData, int taskId, int colour){
		
		StringBuilder coordinates = new StringBuilder();
		StringBuilder altitudes = new StringBuilder();
		StringBuilder IDs = new StringBuilder();
		StringBuilder titles = new StringBuilder();
		StringBuilder descriptions = new StringBuilder();
		StringBuilder URLs = new StringBuilder();
		StringBuilder meanings = new StringBuilder();
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
	        parser.setInput(new StringReader(rawData.replaceAll("&", "amp;")));   // special character '&' makes trouble for xml pull parser
	        int eventType = parser.getEventType();
	        
	        while(eventType!=XmlPullParser.END_DOCUMENT){  	
	        	if(eventType==XmlPullParser.START_TAG){		  
	        		if(parser.getName().equals("gml:coordinates")){
		        	    coordinates.append(parser.nextText()+"\n");
	        		}
	        		if(parser.getName().equals("ms:altitude")){
	        			altitudes.append(parser.nextText()+"\n");
	        		}
	        	    if(parser.getName().equals("ms:obj_id")){
		        	    IDs.append(parser.nextText()+"\n");
		            }
	        	    if(parser.getName().equals("ms:title")){
		        	    titles.append(parser.nextText()+"\n");
		            }
	        	    if(parser.getName().equals("ms:description")){
		        	    descriptions.append(parser.nextText()+"\n");
	        	    }
	        	    if(parser.getName().equals("ms:url")){
		        	    URLs.append(parser.nextText()+"\n");
		            }
	        	    if(parser.getName().equals("ms:meaning")){
		        	    meanings.append(parser.nextText()+"\n");
		            }
	        	  }
	        	  eventType=parser.next();
	        }
	        
	        String[] singleCoordinates = coordinates.toString().split("\n");
	        singleID = IDs.toString().split("\n");
	        singleTitle = titles.toString().split("\n");
	        singleDescription = descriptions.toString().split("\n");
	        singleMeaning = meanings.toString().split("\n");
	        singleURL = URLs.toString().split("\n");
	        
	        
	        // filter out only coordinates of real geographic object (discard other coordinates)
	        String[] effectiveCoordinates = new String[singleID.length];
	        int j = 0;
	        for (int i = 2; i < singleCoordinates.length; i=i+2){
	        	  effectiveCoordinates[j] = singleCoordinates[i];
	        	  j++;
	        }    
	        
	        for (int i = 0; i < effectiveCoordinates.length; i++){
	        	String[] temp = effectiveCoordinates[i].toString().split(" ");
	        	singleURL[i] = "webpage:"+singleURL[i];                           // for the GLO list to display url
	        	for (int k = 0; k < singleID.length; k++){
	        		LatLng[] polygonCoordinates = new LatLng[50];
			        for (int l = 0; l < temp.length; l++){
			        	String[] coordinateString = new String[2];
	 		        	coordinateString = temp[l].split(",");
			        	Double lng = Double.parseDouble(coordinateString[0]);
			        	Double lat = Double.parseDouble(coordinateString[1]);
			        	LatLng tempLatLng = new LatLng(lat,lng);
			        	polygonCoordinates[l] = tempLatLng;
			        	if (l == temp.length -1){
			        		polygonCoordinatesList.add(polygonCoordinates);
			        	}
			        }
	        	}
	        }
      
	        //logging for debugging 
	        /*Log.v("singleCoordinates", TextUtils.join("\n", effectiveCoordinates));
	        Log.v("singleID", IDs.toString());
	        Log.v("singleTitle", titles.toString());
	        Log.v("singleDescription", descriptions.toString());
	        Log.v("singleMeaning", meanings.toString());
	        Log.v("singleURL", URLs.toString());*/
	        
		} catch (Exception e) {
			Log.e("exception",e.toString());
		}
		return null;
	}
	
	// returns the list of LatLng coordinates of all polygons
	public static List<LatLng[]> getPolygonCoordinates(){
		return polygonCoordinatesList;
	}
	
	public static String[] getIDList(){
		return singleID;
	}
	
	public static String[] getTitleList(){
		return singleTitle;
	}
	
	public static String[] getDescriptionList(){
		return singleDescription;
	}
	
	public static String[] getMeaningList(){
		return singleMeaning;
	}
	
	public static String[] getURLList(){
		return singleURL;
	}
}
			