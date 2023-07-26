/*
*  SubscriptionActivity.java
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

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import hackveda.devanshu.tmheart.R;


public class SubscriptionActivity extends AppCompatActivity {
	
	public static Intent newIntent(Context context) {
	
		// Fill the created intent with the data you want to be passed to this Activity when it's opened.
		return new Intent(context, SubscriptionActivity.class);
	}
	
	private TextView lblvalidmonthTextView;
	private Button tvchooseplaneButton;
	private TextView lblvalidmonthTwoTextView;
	private Button tvchooseplanButton;
	private TextView lblvalidmonthThreeTextView;
	private Button tvchooseplanTwoButton;
	private TextView lblvalidyearTextView;
	private Button tvchooseplanThreeButton;
	private Button signupbtnButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.subscription_activity);
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
	
		// Configure lblValidMOnth component
		lblvalidmonthTextView = this.findViewById(R.id.lblvalidmonth_text_view);
		
		// Configure tvChoosePlane component
		tvchooseplaneButton = this.findViewById(R.id.tvchooseplane_button);
		tvchooseplaneButton.setOnClickListener((view) -> {
	this.onTvChoosePlanePressed();
});
		
		// Configure lblValidMonth component
		lblvalidmonthTwoTextView = this.findViewById(R.id.lblvalidmonth_two_text_view);
		
		// Configure tvChoosePlan component
		tvchooseplanButton = this.findViewById(R.id.tvchooseplan_button);
		tvchooseplanButton.setOnClickListener((view) -> {
	this.onTvChoosePlanPressed();
});
		
		// Configure lblValidMonth component
		lblvalidmonthThreeTextView = this.findViewById(R.id.lblvalidmonth_three_text_view);
		
		// Configure tvChoosePlan component
		tvchooseplanTwoButton = this.findViewById(R.id.tvchooseplan_two_button);
		tvchooseplanTwoButton.setOnClickListener((view) -> {
	this.onTvChoosePlanTwoPressed();
});
		
		// Configure lblValidYear component
		lblvalidyearTextView = this.findViewById(R.id.lblvalidyear_text_view);
		
		// Configure tvChoosePlan component
		tvchooseplanThreeButton = this.findViewById(R.id.tvchooseplan_three_button);
		tvchooseplanThreeButton.setOnClickListener((view) -> {
	this.onTvChoosePlanThreePressed();
});
		
		// Configure signupBtn component
		signupbtnButton = this.findViewById(R.id.signupbtn_button);
		signupbtnButton.setOnClickListener((view) -> {
	this.onSignupBtnPressed();
});
	}
	
	public void onTvChoosePlanePressed() {
	
		this.startPaymentActivity();
	}
	
	public void onTvChoosePlanPressed() {
	
		this.startPaymentActivity();
	}
	
	public void onTvChoosePlanTwoPressed() {
	
		this.startPaymentActivity();
	}
	
	public void onTvChoosePlanThreePressed() {
	
		this.startPaymentActivity();
	}
	
	public void onSignupBtnPressed() {
	
	}
	
	private void startPaymentActivity() {
	
		this.startActivity(PaymentActivity.newIntent(this));
	}
}
