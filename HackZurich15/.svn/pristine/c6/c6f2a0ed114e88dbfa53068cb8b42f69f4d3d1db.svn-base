package com.phonewars;

import org.json.JSONException;
import org.json.JSONObject;

public class Game {

	public static JSONObject state;
	public static JSONObject history;
	
	public static String getPlayerId(){
		try {
			JSONObject temp  =state.getJSONObject("Player");
			String temp2 = temp.getString("Id");
			return temp2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getHunterId(){
		try {
			return state.getJSONObject("Hunter").getString("Id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getVictimId(){
		try {
			return state.getJSONObject("Victim").getString("Id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
