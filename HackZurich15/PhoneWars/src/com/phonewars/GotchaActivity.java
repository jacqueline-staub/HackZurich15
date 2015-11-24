package com.phonewars;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class GotchaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gotcha);
	}

	String imgBase64;
	String code;
	public void verify(View view){
		EditText codeEt = (EditText)findViewById(R.id.code);
		code = codeEt.getText().toString();

		JSONObject request = new JSONObject();
		try {

			String playerId = Game.getPlayerId();

			if(playerId == null){
				Toast.makeText(this, "no playerId found", Toast.LENGTH_SHORT).show();
			}
			request.put("PlayerId",playerId);
			request.put("SecretCode", code);

			JSONObject response = MyHttpConnection.post(this, request, "http://phonewars.azurewebsites.net/api/markkilled");
			if(response == null){
				//Toast.makeText(this, "Wrong Code", Toast.LENGTH_SHORT).show();
				return;
			}
			response = MyHttpConnection.get(this,"http://phonewars.azurewebsites.net/api/getgamedata?playerId=" + playerId);
			
			if(response!= null){
				Game.state = response;
				finish();
			}else{
				Toast.makeText(this, response.getString("ErrorMessage"), Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String convertToBase64(Bitmap b){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.JPEG, 95, baos);
		byte[] byteArray = baos.toByteArray();
		return Base64.encodeToString(byteArray,Base64.DEFAULT);
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
			ImageView userPicture = (ImageView)findViewById(R.id.image);
			userPicture.setImageBitmap(b);

			imgBase64 = convertToBase64(b);
		}
	}

	public void takePicture(View v) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, 1);
		}
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		bm.recycle();
		return resizedBitmap;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gotcha, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
