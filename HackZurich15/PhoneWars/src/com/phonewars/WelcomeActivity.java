package com.phonewars;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

public class WelcomeActivity extends Activity {

	public static SharedPreferences prefs;
	private boolean seenBefore = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		checkPlayerId();
		enableWifiIdDisabled();
	}

	public static String playerId;

	private void checkPlayerId() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		playerId = prefs.getString("playerId", "");
		if (playerId.length() == 0 ) {
			seenBefore = false;
		} else {
			if (isNetworkAvailable()) {
				loading = true;
				loadGameState();
			} else {
				//TODO was sonst?
			}
		}
	}


	private void loadGameState(){
		JSONObject response = MyHttpConnection.get(this,"http://phonewars.azurewebsites.net/api/getgamedata?playerId=" + playerId);
		if(response != null){
			Game.state = response;
		}else{
			loading = false;
			Toast.makeText(this, "error loading game state", Toast.LENGTH_SHORT).show();
		}
	}

	private void enableWifiIdDisabled() {
		WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	boolean loading = false;

	public void toRegistration(View view) {
		if (seenBefore) {
			if (!isNetworkAvailable()) {
				Toast.makeText(this, "Connect to internet please", Toast.LENGTH_SHORT).show();
				return;
			}

			if (Game.state != null) {
				Intent i = new Intent(WelcomeActivity.this, GameActivity.class);
				startActivity(i);
			} else {
				if(!loading){
					loading = true;
					loadGameState();
				}else{
					Toast.makeText(this, "The game is loading", Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			Intent i = new Intent(WelcomeActivity.this, RegisterActivity.class);
			startActivity(i);
		}
	}
}