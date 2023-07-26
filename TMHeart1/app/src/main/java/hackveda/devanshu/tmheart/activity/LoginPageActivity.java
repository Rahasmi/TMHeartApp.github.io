/*
*  LoginPageActivity.java
*  TMHeart
*
*  Created by Devanshu Shukla.
*  Copyright © 2018 Hackveda. All rights reserved.
*/

package hackveda.devanshu.tmheart.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import hackveda.devanshu.tmheart.R;


public class LoginPageActivity extends AppCompatActivity {

	private GoogleApiClient googleApiClient;
	private EditText et1;
	private EditText et2;

	public static Intent newIntent(Context context) {
	
		// Fill the created intent with the data you want to be passed to this Activity when it's opened.
		return new Intent(context, LoginPageActivity.class);
	}
	
	private Button lblsignupButton;
	private Button loginbtnButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_page_activity);
		this.init();
	}
	
	private void init() {

		SharedPreferences loginPref = getSharedPreferences("ConfigPref", MODE_PRIVATE);
		if(!loginPref.getString("Name", "NONAME").matches("NONAME")){
			Intent intent = new Intent(LoginPageActivity.this, ProfileActivity.class);
			startActivity(intent);
			finish();
		}

		ImageView imageview = (ImageView) findViewById(R.id.appimage_image_view);
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageview, PropertyValuesHolder.ofFloat("scaleX", 0.8F),
				PropertyValuesHolder.ofFloat("scaleY", 0.8f));
		animator.setDuration(300);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.start();
	
		// Configure lblSignup component
		lblsignupButton = this.findViewById(R.id.lblsignup_button);
		lblsignupButton.setOnClickListener((view) -> {
	this.onLblSignupPressed();
});
		
		// Configure loginBtn component
		loginbtnButton = this.findViewById(R.id.loginbtn_button);
		loginbtnButton.setOnClickListener((view) -> {
	this.onLoginBtnPressed();
		});

		et1 = (EditText) findViewById(R.id.tvusername_edit_text);
		et2 = (EditText) findViewById(R.id.tvpassword_edit_text);

	}

	public void onConnected(Bundle bundle){

	}

	public void onLblSignupPressed() {
	
		this.startSignUpActivity();
	}
	
	public void onLoginBtnPressed() {

		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle("Login Request");
		dialog.setMessage("Connecting to server ...");
		dialog.show();

		String username = et1.getText().toString();
		String password = et2.getText().toString();

		if(!username.matches("") || !password.matches("")){
			String url = "https://hackveda.in/tm_heart/userauth.php?email="+username+"&password="+password;
			RequestQueue newRequestQueue = Volley.newRequestQueue(this);
			StringRequest newRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					dialog.dismiss();
					try {
						JSONObject responseObject = new JSONObject(response);
						String status = responseObject.getString("Status");
						String message = responseObject.getString("Message");
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

						if(status.matches("true")){

							String name = responseObject.getString("Name");
							String age = responseObject.getString("Age");
							String height = responseObject.getString("Height");
							String weight = responseObject.getString("Weight");
							String sex = responseObject.getString("Sex");
							if(sex.matches("M")){
								sex = "0";
							}else if(sex.matches("F")){
								sex = "1";
							}else {
								sex = "2";
							}

							String refcode = responseObject.getString("RefCode");


							SharedPreferences configPref = getSharedPreferences("ConfigPref", MODE_PRIVATE);
							SharedPreferences.Editor editor = configPref.edit();

							editor.putString("Username", username);
							editor.putString("Password", password);
							editor.putString("Name", name);
							editor.putString("Age", age);
							editor.putString("Height", height);
							editor.putString("Weight", weight);
							editor.putString("Sex", sex);
							editor.putString("RefCode", refcode);

							editor.commit();

							if(!age.matches("0") && !height.matches("0") && !weight.matches("0") && !sex.matches("0")){
								Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
								startActivity(intent);
							}else{
								Intent intent = new Intent(getApplicationContext(), ConfigureActivity.class);
								startActivity(intent);
							}
							finish();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
				}
			});

			newRequestQueue.add(newRequest);

		}else{
			Toast.makeText(getApplicationContext(), "Pleae enter username and password", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void startSignUpActivity() {
		this.startActivity(SignUpActivity.newIntent(this));
		finish();
	}
	
	private void startProfileActivity() {
	
		this.startActivity(ProfileActivity.newIntent(this));
	}

}
