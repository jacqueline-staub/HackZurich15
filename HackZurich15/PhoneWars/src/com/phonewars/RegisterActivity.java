package com.phonewars;

import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	//create Editor for shared preferences
	private SharedPreferences preferences = WelcomeActivity.prefs;
	private SharedPreferences.Editor editor = preferences.edit();
	private boolean pictureTaken = false;
	private String imgBase64;
	private JSONObject result;
	public static String playerID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		enableWifiIfDisabled();
	}

	private void enableWifiIfDisabled(){
		WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
	}

	public void takePicture(View view){
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, 1);
			pictureTaken = true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			
			//read display dimensions in pixels
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = Math.round(displaymetrics.heightPixels/3);
			int width = Math.round(displaymetrics.widthPixels/3);
			
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			Bitmap b = getResizedBitmap(imageBitmap, width, height);
			ImageView userPicture = (ImageView)findViewById(R.id.img);
			userPicture.setImageBitmap(b);
			imgBase64 = convertToBase64(b);
		}
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = (float) ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		bm.recycle();
		return resizedBitmap;
	}

	private String convertToBase64(Bitmap b){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.JPEG, 95, baos);
		byte[] byteArray = baos.toByteArray();
		return Base64.encodeToString(byteArray,Base64.DEFAULT);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void gotoGame(View view) throws Exception{
		enableWifiIfDisabled();
		if(!isNetworkAvailable()){
			Toast.makeText(this, "Connect to internet please", Toast.LENGTH_SHORT).show();
			return;
		}

		boolean fieldsAreSet = readEditTexts();
		if(fieldsAreSet){
			//all fields contain some information
			Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();

			playerID = Game.getPlayerId();
			editor.putString("playerId", playerID);
			editor.apply();

			//change to next Activity
			Intent intent = new Intent(this,GameActivity.class);
			startActivity(intent);
		}else{
			Toast.makeText(this, "Please provide your information first", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean readEditTexts() {
		EditText username = (EditText)findViewById(R.id.usernameEditText);
		EditText password = (EditText)findViewById(R.id.passwordEditText);
		EditText address = (EditText)findViewById(R.id.homeAdressEditText);
		EditText workaddress = (EditText)findViewById(R.id.workAdressEditText);
		EditText phone = (EditText)findViewById(R.id.phoneEditText);
		EditText mail = (EditText)findViewById(R.id.emailEditText);

		String sUsername = username.getText().toString();
		String sPassword = password.getText().toString();
		String sAddress = address.getText().toString();
		String sWorkaddress = workaddress.getText().toString();
		String sPhone = phone.getText().toString();
		String sMail = mail.getText().toString();

		if(sUsername.length()!=0 && sPassword.length()!=0 && sAddress.length()!=0 && sWorkaddress.length()!=0 && sPhone.length()!=0 && sMail.length()!=0 && pictureTaken){
			doJSONStuff(sUsername, sPassword, sAddress, sWorkaddress, sPhone, sMail);
			return true;
		}else{
			return false;		
		}

	}

	private void doJSONStuff(String sUsername, String sAddress, String sWorkaddress, String sPhone, String sMail, String sPassword){
		JSONObject request = new JSONObject();
		try{
			request.put("ImageBase64", imgBase64);
			request.put("Nickname", sUsername);
			request.put("HomeAddress", sAddress);
			request.put("WorkAddress", sWorkaddress);
			request.put("Phone", sPhone);
			request.put("Email", sMail);
			request.put("Password", sPassword);

			result = MyHttpConnection.post(this, request, "http://phonewars.azurewebsites.net/api/register");

			if(result == null)
				return;
			Game.state = result;
			
			Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();

			String id = Game.getPlayerId();
			editor.putString("playerId", id);
			editor.apply();

			//change to next Activity
			Intent intent = new Intent(this,GameActivity.class);
			startActivity(intent);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}