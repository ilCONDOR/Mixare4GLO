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

import android.text.TextUtils;
import android.util.Log;

public class GloPointDataProcessor extends DataHandler implements DataProcessor {

	@Override
	public String[] getUrlMatch() {
		String[] str = {"points"};
		return str;
	}

	@Override
	public String[] getDataMatch() {
		String[] str = {"gml:Point"};
		return str;
	}
	
	@Override
	public boolean matchesRequiredType(String type) {
		if(type.equals(DataSource.TYPE.GloPoint.name())){
			return true;
		}
		return false;
	}

	@Override
	public List<Marker> load(String rawData, int taskId, int colour){
		
		List<Marker> markers = new ArrayList<Marker>();
	    Marker ma = null;
		
		String[] latlng = new String[2];
		Double latAsDouble = 0.00, lngAsDouble = 0.00, altAsDouble = 0.00;
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
		        	    descriptions.append(parser.nextText()+"\n");         // not used by Mixare
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
 	        String[] singleAltitude = altitudes.toString().split("\n");
	        String[] singleID = IDs.toString().split("\n");
	        String[] singleTitle = titles.toString().split("\n");
	        String[] singleDescription = descriptions.toString().split("\n");
	        String[] singleMeaning = meanings.toString().split("\n");
	        String[] singleURL = URLs.toString().split("\n");
	        
	        // filter out only coordinates of real geographic object (discard coordinates of bounding box)
	        String[] singleCoordinatesFiltered = new String[singleID.length];
	        int j = 0;
	        for (int i = 2; i < singleCoordinates.length; i=i+2){
	        	  singleCoordinatesFiltered[j] = singleCoordinates[i];
	        	  j++;
	        }
 
	        //logging for debugging 
	        /*Log.v("singleCoordinates", TextUtils.join("\n", singleCoordinatesFiltered));
	        Log.v("singleAltitude", altitudes.toString());
	        Log.v("singleID", IDs.toString());
	        Log.v("singleTitle", titles.toString());
	        Log.v("singleDescription", descriptions.toString());
	        Log.v("singleMeaning", meanings.toString());
	        Log.v("singleURL", URLs.toString());*/    

	        
	        // creating the POIs
	        for (int i = 0; i < singleID.length; i++){
	        	    latlng = singleCoordinatesFiltered[i].toString().split(",");
		        	latAsDouble = Double.parseDouble(latlng[1]);                     // long before lat because of server 
		        	lngAsDouble = Double.parseDouble(latlng[0]);
		        	altAsDouble = Double.parseDouble(singleAltitude[i]);

		        	ma = new POIMarker(
							singleID[i],
							HtmlUnescape.unescapeHTML(singleTitle[i]),
							singleMeaning[i],
							latAsDouble, 
							lngAsDouble, 
							altAsDouble, 
							singleURL[i], 
							taskId, colour);
					
					markers.add(ma);
	        }
	        
		} catch (Exception e) {
			Log.e("exception",e.toString());
		}
		return markers;
	}
}
			