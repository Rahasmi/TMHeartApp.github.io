/*
*  PaymentActivity.java
*  TMHeart
*
*  Created by Devanshu Shukla.
*  Copyright © 2018 Hackveda. All rights reserved.
*/

package hackveda.devanshu.tmheart.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import hackveda.devanshu.tmheart.R;


public class PaymentActivity extends AppCompatActivity {
	
	public static Intent newIntent(Context context) {
	
		// Fill the created intent with the data you want to be passed to this Activity when it's opened.
		return new Intent(context, PaymentActivity.class);
	}
	
	private WebView sectionwebviewWebView;
	private Button backbtnButton;
	private Button backbtnTwoButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.payment_activity);
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
	
		// Configure sectionWebView component
		sectionwebviewWebView = this.findViewById(R.id.sectionwebview_web_view);
		sectionwebviewWebView.loadUrl("https://rzp.io/l/tmheart");
		
		// Configure backBtn component
		backbtnButton = this.findViewById(R.id.backbtn_button);
		backbtnButton.setOnClickListener((view) -> {
	this.onBackBtnTwoPressed();
});
		
		// Configure backBtn component
		backbtnTwoButton = this.findViewById(R.id.backbtn_two_button);
		backbtnTwoButton.setOnClickListener((view) -> {
	this.onBackBtnPressed();
});
	}
	
	public void onBackBtnTwoPressed() {
	
		this.startConfigureActivity();
	}
	
	public void onBackBtnPressed() {
	
	}
	
	private void startConfigureActivity() {
	
		this.startActivity(ConfigureActivity.newIntent(this));
	}
}
