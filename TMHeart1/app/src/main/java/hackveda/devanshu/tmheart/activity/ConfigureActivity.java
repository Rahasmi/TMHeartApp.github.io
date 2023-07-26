/*
*  ConfigureActivity.java
*  TMHeart
*
*  Created by Devanshu Shukla.
*  Copyright © 2018 Hackveda. All rights reserved.
*/

package hackveda.devanshu.tmheart.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import hackveda.devanshu.tmheart.R;


public class ConfigureActivity extends AppCompatActivity {

	private SharedPreferences configPref;
	private EditText etName;
	private EditText etDob;
	private EditText etHeight;
	private EditText etWeight;
	private TabLayout tbSex;

	public static Intent newIntent(Context context) {
	
		// Fill the created intent with the data you want to be passed to this Activity when it's opened.
		return new Intent(context, ConfigureActivity.class);
	}
	
	private Button lbldeleteButton;
	private TabLayout tvsexTabBar;
	private Button btnsavedetailsButton;
	private Button lblreferfriendButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.configure_activity);
		this.init();
		showDisclaimer();
	}

	private void showDisclaimer() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ConfigureActivity.this);
		ViewGroup viewGroup = findViewById(android.R.id.content);
		View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.disclaimer_activity, viewGroup, false);
		builder.setView(dialogView);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void init() {
	
		// Configure lblDelete component
		
		// Configure tvSex component
		tvsexTabBar = this.findViewById(R.id.tvsex_tab_bar);
		tvsexTabBar.setOnClickListener((view) -> {
	this.onTvSexValueChanged();
});
		
		// Configure btnSaveDetails component
		btnsavedetailsButton = this.findViewById(R.id.btnsavedetails_button);
		btnsavedetailsButton.setOnClickListener((view) -> {
	this.onBtnSaveDetailsPressed();
});
		
		// Configure lblReferFriend component
		lblreferfriendButton = this.findViewById(R.id.lblreferfriend_button);
		lblreferfriendButton.setOnClickListener((view) -> {
	this.onLblReferFriendPressed();
});

		ImageView imageview = (ImageView) findViewById(R.id.applogo_image_view);
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageview, PropertyValuesHolder.ofFloat("scaleX", 0.8F),
				PropertyValuesHolder.ofFloat("scaleY", 0.8f));
		animator.setDuration(300);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.start();

		etName = (EditText) findViewById(R.id.tvname_edit_text);
		etDob = (EditText) findViewById(R.id.tvdob_edit_text);
		etHeight = (EditText) findViewById(R.id.tvheight_edit_text);
		etWeight = (EditText)findViewById(R.id.tvweight_edit_text);
		tbSex = (TabLayout) findViewById(R.id.tvsex_tab_bar);

		configPref = getSharedPreferences("ConfigPref", MODE_PRIVATE);

		if(configPref.contains("Name")){
			String name = configPref.getString("Name", "NONAME");
			if(!name.matches("NONAME")){
				String dob = configPref.getString("Age", "0");
				String height = configPref.getString("Height", "0");
				String weight = configPref.getString("Weight", "0");
				String sex = configPref.getString("Sex", "0");

				if(!name.matches("NONAME")) {
					etName.setText(name);
				}
				if(!dob.matches("0")){
					etDob.setText(dob);
				}
				if(!height.matches("0")){
					etHeight.setText(height);
				}
				if(!weight.matches("0")){
					etWeight.setText(weight);
				}
				if(!sex.matches("0")){
					TabLayout.Tab tab = tbSex.getTabAt(Integer.parseInt(sex));
					tab.select();
				}
			}
		}
	}
	
	public void onLblDeletePressed() {
	
	}
	
	public void onTvSexValueChanged() {
	
	}
	
	public void onBtnSaveDetailsPressed() {

		String name = etName.getText().toString().trim();
		String age = etDob.getText().toString().trim();
		String height = etHeight.getText().toString().trim();
		String weight = etWeight.getText().toString().trim();
		int sex = tbSex.getSelectedTabPosition();

		if(!name.matches("") || !age.matches("") || !height.matches("") || !weight.matches("")){

			SharedPreferences.Editor editor = configPref.edit();
			editor.putString("Name", name);
			editor.putString("Age", age);
			editor.putString("Height", height);
			editor.putString("Weight", weight);
			editor.putString("Sex", Integer.toString(sex));
			editor.commit();

			Toast.makeText(getApplicationContext(), "Configuration saved", Toast.LENGTH_LONG).show();

		}else{
			Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
		}

		Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(profile);
		finish();
	}

	public void onLblReferFriendPressed() {
		Intent intent = new Intent(getApplicationContext(), ReferActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void startProfileActivity() {
	
		this.startActivity(ProfileActivity.newIntent(this));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(intent);
		finish();
	}
}
