/*
*  SignUpActivity.java
*  TMHeart
*
*  Created by Devanshu Shukla.
*  Copyright © 2018 Hackveda. All rights reserved.
*/

package hackveda.devanshu.tmheart.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.JsonReader;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import hackveda.devanshu.tmheart.R;


public class SignUpActivity extends AppCompatActivity {

	private EditText etName;
	private EditText etEmail;
	private EditText etPass;
	private EditText etMobile;
	private EditText etRefCode;
	private ProgressDialog dialog;
	private String response;

	public static Intent newIntent(Context context) {
	
		// Fill the created intent with the data you want to be passed to this Activity when it's opened.
		return new Intent(context, SignUpActivity.class);
	}
	
	private Button lblloginButton;
	private Button signupbtnButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sign_up_activity);
		this.init();
	}
	
	private void init() {

		ImageView imageview = (ImageView) findViewById(R.id.applogo_image_view);
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageview, PropertyValuesHolder.ofFloat("scaleX", 0.8F),
				PropertyValuesHolder.ofFloat("scaleY", 0.8f));
		animator.setDuration(300);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.start();
	
		// Configure lblLogin component
		lblloginButton = this.findViewById(R.id.lbllogin_button);
		lblloginButton.setOnClickListener((view) -> {
	this.onLblLoginPressed();
});
		
		// Configure signupBtn component
		signupbtnButton = this.findViewById(R.id.signupbtn_button);
		signupbtnButton.setOnClickListener((view) -> {
	this.onSignupBtnPressed();
});

		etName = (EditText) findViewById(R.id.tvname_edit_text);
		etEmail = (EditText) findViewById(R.id.tvemail_edit_text);
		etPass = (EditText)findViewById(R.id.tvpassword_edit_text);
		etMobile = (EditText) findViewById(R.id.tvmobile_edit_text);
		etRefCode = (EditText) findViewById(R.id.tvrefcode_edit_text);

	}
	
	public void onLblLoginPressed() {

		this.startLoginPageActivity();
	}

	public void onSignupBtnPressed() {

		dialog = new ProgressDialog(this);
		dialog.setTitle("Sign Up");
		dialog.setMessage("Sign up in progress ...");
		dialog.create();
		dialog.show();

		String name = etName.getText().toString().trim();
		String email = etEmail.getText().toString().trim();
		String password = etPass.getText().toString().trim();
		String mobile = etMobile.getText().toString().trim();
		String friendrefcode = etRefCode.getText().toString().trim();

		if(!name.matches("") || !email.matches("") || !mobile.matches("") || !password.matches("")) {

			String url = "https://hackveda.in/tm_heart/createuser.php?name="+name+"&email="+email+"&password="+password+"&mobile="+mobile+"&friendrefcode="+friendrefcode+"&secret=abcde";
			//String url = "httpw://www.hackveda.in";

			sendRequest(url);


			//Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Mandatory fields cannot be empty", Toast.LENGTH_SHORT).show();
			}

	}

	private void sendRequest(String url) {
		RequestQueue queue = Volley.newRequestQueue(this);
		StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dialog.dismiss();


				try {

					JSONObject responseObject = new JSONObject(response);
					String message = responseObject.getString("Message");
					Toast.makeText(getApplicationContext(), "Response: " + message, Toast.LENGTH_LONG).show();

					if(message.matches("Signup successful")) {
						Intent intent = new Intent(SignUpActivity.this, LoginPageActivity.class);
						startActivity(intent);
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
				Toast.makeText(getApplicationContext(), "Error: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			}
		});

		// Add the request to the RequestQueue.
		queue.add(request);

	}

	private void startLoginPageActivity() {

		this.startActivity(LoginPageActivity.newIntent(this));
	}

	private void startSubscriptionActivity() {

		this.startActivity(SubscriptionActivity.newIntent(this));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(), ConfigureActivity.class);
		startActivity(intent);
		finish();
	}

}
