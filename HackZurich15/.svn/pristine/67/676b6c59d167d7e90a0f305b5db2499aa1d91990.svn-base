package com.phonewars;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class GameOverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);

		startVibrating(1000);
	}

	//vibrate for duration milliseconds
	private void startVibrating(long duration){
		Log.d("PhoneWars","start vibrating");
		Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(duration);
	}

	public void gotcha(View view){
		
		JSONObject request = new JSONObject();
		try {
			request.put("PlayerId", Game.getPlayerId());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyHttpConnection.post(this, request, "http://phonewars.azurewebsites.net/api/resetdead");
		
		JSONObject response = MyHttpConnection.get(this,"http://phonewars.azurewebsites.net/api/getgamedata?playerId=" + Game.getPlayerId());
		if(response != null){
			Game.state = response;
			finish();
		}else{
			Toast.makeText(this, "error loading game state", Toast.LENGTH_SHORT).show();
		}
	}
}
