/*
*  ProfileActivity.java
*  TMHeart
*
*  Created by Devanshu Shukla.
*  Copyright © 2018 Hackveda. All rights reserved.
*/

package hackveda.devanshu.tmheart.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.tabs.TabLayout;
import com.gusakov.library.PulseCountDown;
import com.gusakov.library.java.interfaces.OnCountdownCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import hackveda.devanshu.tmheart.R;
import hackveda.devanshu.tmheart.dialog.ProfileActivityGoalbtnButtonSheet;


public class ProfileActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {

	private static final int RESULT_LOAD_IMAGE = 1;
	private CharSequence[] zonename;
	private HashMap<String, Integer> ZoneColor;
	private String currentGoalValue = "72";
	private SharedPreferences goalPref;
	private AlertDialog.Builder alertDialog;
	private TextView tvHeartGoal;
	private String currentGoalName;
	private TextToSpeech tts;
	private String alarmStatus = "PERFECT";
	private SharedPreferences configPref;
	private TextView tvAge;
	private TextView tvHeight;
	private TextView tvWeight;
	private TextView tvWeightGoal;
	private TextView tvWeightCurrent;
	private TextView tvMaxRate;
	private TextView tvIdealWeight;
	private TMEquations tmeqns;
	private TextView tvWeightValue;
	private TextView tvWeightStatus;
	private View viewWeightStatus;
	private TextView tvWeightStatusStatement;
	private View viewMyWeight;
	private int highCounter;
	private int lowCounter;
	private TextView tvLows;
	private TextView tvHighs;
	private View viewLows;
	private View viewHighs;
	private View viewMaxRate;
	private TextView tvAlarm;
	private View btnEditProfile;

	private View viewHeartMessageBg;
	private View viewWeightMessageBg;
	private int maxrate;
	private TextView selectedlbl;
	private int maxtime;
	private int currenttime;
	private int timeincrement;
	private String oneMinHeartRate;
	private String twoMinHeartRate;
	private TextView oneMinTv;
	private TextView twoMinTv;
	private String currentHeartRate;
	private TextView recoveryAlarmTv;
	private View recoveryAlarmBg;
	private int recrate;
	private TextView recRateTv;
	private View recovery1MinBg;
	private View recovery2MinBg;
	private TextView recTimeTv;
	private View recTimeBg;
	private String weightstatus;
	private String username;
	private String goal;
	private int activityID = 0;
	private String hbstart = "";
	private String hbend = "";
	private String drop1m = "";
	private String drop2m = "";
	private String hbstatus = "";
	private long recTime = 0;
	private AlertDialog alertDialog2;
	private ProgressDialog measureRecoveryDialog;
	private AsyncTask<String, String, Long> recoveryAsync;
	private CountDownTimer countDownTimer1;
	private CountDownTimer countDownTimer2;
	private Boolean isActivityRunning = false;
	private CountDownTimer countDownTimer;


	public static Intent newIntent(Context context) {

		// Fill the created intent with the data you want to be passed to this Activity when it's opened.
		return new Intent(context, ProfileActivity.class);
	}

	private Button goalbtnButton;
	private Button tveditprofileButton;
	public String HeartData;

	private HashMap<String, String> ZoneLevel;
	private HashMap<String, String> GoalLevel;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profile_activity);
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

		// Configure goalBtn component
		goalbtnButton = this.findViewById(R.id.goalbtn_button);
		goalbtnButton.setOnClickListener((view) -> {
			this.onGoalBtnPressed();
		});

		// Configure tvEditProfile component
		tveditprofileButton = this.findViewById(R.id.tveditprofile_button);
		tveditprofileButton.setOnClickListener((view) -> {
			this.onTvEditProfilePressed();
		});

		tvAge = (TextView) findViewById(R.id.lblagevalue_text_view);
		tvHeight = (TextView) findViewById(R.id.lblheightvalue_text_view);
		tvWeight = (TextView) findViewById(R.id.lblweightvalue_text_view);
		tvWeightCurrent = (TextView) findViewById(R.id.lblcurrentweightvalu_text_view);
		viewMyWeight = findViewById(R.id.bgcurrentweight_constraint_layout);

		tvLows = (TextView) findViewById(R.id.lblavgheartratevalue_text_view);
		tvHighs = (TextView) findViewById(R.id.lblhighheartratevalue_text_view);
		viewLows = findViewById(R.id.bgavgheartrate_constraint_layout);
		viewHighs = findViewById(R.id.bghighheartrate_constraint_layout);
		viewMaxRate = findViewById(R.id.bgidealheartrate_constraint_layout);

		viewLows.setBackgroundResource(R.drawable.status_low);
		viewHighs.setBackgroundResource(R.drawable.status_high);
		viewMaxRate.setBackgroundResource(R.drawable.status_high);

		tvMaxRate = (TextView) findViewById(R.id.max_heart_rate_textview);
		tvIdealWeight = (TextView) findViewById(R.id.lblidealweightvalue_text_view);
		tvWeightValue = (TextView) findViewById(R.id.lblavgweightvalue_text_view);
		tvWeightStatus = (TextView) findViewById(R.id.lblavgweight_text_view);
		viewWeightStatus = findViewById(R.id.bgavgweight_constraint_layout);
		tvWeightStatusStatement = (TextView) findViewById(R.id.lbltargetmessage_text_view);

		viewHeartMessageBg = findViewById(R.id.sectionalarmraise_constraint_layout);
		viewWeightMessageBg = findViewById(R.id.sectiontargetmessage_constraint_layout);

		tvAlarm = (TextView) findViewById(R.id.lblalarmraised_text_view);

		oneMinTv = (TextView) findViewById(R.id.lblcurrentheartratev_text_view2);
		twoMinTv = (TextView) findViewById(R.id.lblavgheartratevalue_text_view2);
		recoveryAlarmTv = (TextView) findViewById(R.id.lblalarmraised_text_view2);
		recoveryAlarmBg = findViewById(R.id.sectionalarmraise_constraint_layout2);
		recovery1MinBg = findViewById(R.id.bgcurrentheartrate_constraint_layout2);
		recovery2MinBg = findViewById(R.id.bgavgheartrate_constraint_layout2);
		recRateTv = (TextView) findViewById(R.id.max_heart_rate_textview2);
		recrate = 0;

		recTimeTv = (TextView) findViewById(R.id.lblhighheartratevalue_text_view2);
		recTimeBg = findViewById(R.id.bghighheartrate_constraint_layout2);

		btnEditProfile = findViewById(R.id.backbtn_constraint_layout);
		btnEditProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ConfigureActivity.class);
				startActivity(intent);
				finish();
			}
		});

		ImageView profileImage = (ImageView) findViewById(R.id.profileimage_image_view);
		profileImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		tmeqns = new TMEquations();

		configPref = getSharedPreferences("ConfigPref", MODE_PRIVATE);

		if (configPref.contains("Name")) {
			String name = configPref.getString("Name", "NONAME");
			if (!name.matches("NONAME")) {
				String dob = configPref.getString("Age", "0");
				String height = configPref.getString("Height", "0");
				String weight = configPref.getString("Weight", "0");
				String sex = configPref.getString("Sex", "0");
				int heartHighs = configPref.getInt("HeartHighs", 0);
				int heartLows = configPref.getInt("HeartLows", 0);
				username = configPref.getString("Username", "0");
				String password = configPref.getString("Password", "0");

				maxrate = tmeqns.getMaxHeartRate(Integer.parseInt(dob));
				recrate = maxrate / 2;

				if (!name.matches("NONAME")) {

				}
				if (!dob.matches("0")) {
					tvAge.setText(dob);
					tvMaxRate.setText(Integer.toString(tmeqns.getMaxHeartRate(Integer.parseInt(dob))));
					recRateTv.setText(Integer.toString(recrate));
				}
				if (!height.matches("0")) {
					tvHeight.setText(height);
					tvIdealWeight.setText(String.format("%.1f", tmeqns.getIdealWeight(Float.parseFloat(height))));
				}
				if (!weight.matches("0")) {
					tvWeight.setText(weight);
					tvWeightCurrent.setText(weight);

					Bundle weightData = tmeqns.getWeightStatus(Float.parseFloat(weight), tmeqns.getIdealWeight(Float.parseFloat(height)));
					String weightvalue = String.format("%.1f", weightData.getFloat("WeightValue"));
					weightstatus = weightData.getString("WeightStatus");

					tvWeightValue.setText(weightvalue);

					if (weightstatus.matches("OverWeight")) {
						tvWeightStatus.setText("OverWt");
						viewWeightStatus.setBackgroundResource(R.drawable.status_high);
						viewMyWeight.setBackgroundResource(R.drawable.status_high);
						tvWeightStatusStatement.setText("You are OVERWEIGHT by " + weightvalue + " Kg");
						viewWeightMessageBg.setBackgroundColor(Color.RED);
					} else if (weightstatus.matches("UnderWeight")) {
						tvWeightStatus.setText("UnderWt");
						viewWeightStatus.setBackgroundResource(R.drawable.status_low);
						viewMyWeight.setBackgroundResource(R.drawable.status_low);
						tvWeightStatusStatement.setText("You are UNDERWEIGHT by " + weightvalue + " Kg");
						viewWeightMessageBg.setBackgroundColor(Color.BLUE);
					} else {
						tvWeightStatus.setText("PerfectWt");
						viewWeightStatus.setBackgroundResource(R.drawable.status_perfect);
						viewMyWeight.setBackgroundResource(R.drawable.status_perfect);
						tvWeightStatusStatement.setText("You have a perfect weight. Keep it up");
						viewWeightMessageBg.setBackgroundColor(Color.MAGENTA);
					}
				}

				if (!sex.matches("0")) {

				}
				if ((heartHighs != 0)) {
					highCounter = heartHighs;
					tvHighs.setText(Integer.toString(heartHighs));
				}
				if (heartLows != 0) {
					lowCounter = heartLows;
					tvLows.setText(Integer.toString(heartLows));
				}

				goalPref = getSharedPreferences("GoalPref", MODE_PRIVATE);
				goal = goalPref.getString("GoalName", "NOGOAL");
				goal.replace("&", "and");
				// Send data to server
				RequestQueue newRequestQueue1 = Volley.newRequestQueue(this);
				goal = goal.replace("&", "and");
				String url1 = "https://hackveda.in/tm_heart/configure.php?email="+username+"&name="+name+"&age="+dob+"&height="+height+"&weight="+weight+"&sex=M&weightstatus="+weightstatus+"&goal="+goal;
				StringRequest request1 = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						try {
							JSONObject responseObject1 = new JSONObject(response);
							String status = responseObject1.getString("Status");
							String message = responseObject1.getString("Message");

							if(status.matches("true")){
								// Do nothing
							}
							Log.i("ServerUpdate Weight: ", message);
							//Toast.makeText(getApplicationContext(), "Response: " + message, Toast.LENGTH_SHORT).show();

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
					}
				});

				newRequestQueue1.add(request1);
			}
		}

		/* Configuration 1
		zonename = new CharSequence[]{"Increase Speed, Tone and Inches", "Increase Endurance & Inches", "Weight Control & Decrease Inches",
				"Warm Up & Maintain Inches", "General Circulation"};

		ZoneLevel = new HashMap<String, String>();
		ZoneLevel.put("Increase Speed, Tone & Inches", "86");
		ZoneLevel.put("Increase Endurance & Inches", "76");
		ZoneLevel.put("Weight Control & Decrease Inches", "66");
		ZoneLevel.put("Warm Up & Maintain Inches", "56");
		ZoneLevel.put("General Circulation", "46");
		*/

		// Configuration 2
		zonename = new CharSequence[]{"Increase Speed, Tone & Inches", "Weight Control, Decrease Inches & Increase Endurance", "General Circulation, WarmUp & Maintain Inches"};

		ZoneLevel = new HashMap<String, String>();
		ZoneLevel.put("Increase Speed, Tone & Inches", "82");
		ZoneLevel.put("Weight Control, Decrease Inches & Increase Endurance", "64");
		ZoneLevel.put("General Circulation, WarmUp & Maintain Inches", "46");
		//ZoneLevel.put("Warm Up & Maintain Inches", "56");
		//ZoneLevel.put("General Circulation", "46");


		ZoneColor = new HashMap<String, Integer>();
		//ZoneColor.put("HIGH", R.color.profile_activity_bgavgheartrate_constraint_layout_background_color);
		//ZoneColor.put("LOW", R.color.low_color);
		//ZoneColor.put("PERFECT", R.color.profile_activity_bgcurrentheartrate_constraint_layout_background_color);

		ZoneColor.put("HIGH", R.drawable.status_high);
		ZoneColor.put("LOW", R.drawable.status_low);
		ZoneColor.put("PERFECT", R.drawable.status_perfect);


		goalPref = getSharedPreferences("GoalPref", MODE_PRIVATE);
		if (goalPref.contains("GoalName")) {
			String goalname = goalPref.getString("GoalName", "NOGOAL");
			if (goalname != "NOGOAL") {
				currentGoalValue = ZoneLevel.get(goalname);
				tvHeartGoal = (TextView) findViewById(R.id.tvgoalstatus_text_view);
				tvHeartGoal.setText(goalname);

				tvWeightGoal = (TextView) findViewById(R.id.tvgoalstatus_two_text_view);
				tvWeightGoal.setText(goalname);
				//Toast.makeText(getApplicationContext(), currentGoalValue, Toast.LENGTH_SHORT).show();
				//updateHeartZoneColor(currentGoalValue);
			}
		}
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setIcon(R.drawable.goal);

		tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {

			}
		});
		tts.setLanguage(Locale.ENGLISH);

	}

	public void onGoalBtnPressed() {

		alertDialog.setTitle("Pick a Goal").setItems(zonename, new DialogInterface.OnClickListener() {
			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(DialogInterface dialog, int which) {

				CharSequence zone = zonename[which];
				String zonelevel = ZoneLevel.get(zone);
				currentGoalValue = zonelevel;

				AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
				ViewGroup viewGroup = findViewById(android.R.id.content);
				View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.goal_activity, viewGroup, false);


					TextView secondlbl = (TextView) dialogView.findViewById(R.id.lblsecondsvalue_text_view);
					secondlbl.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							selectedlbl = secondlbl;
							secondlbl.setSelected(true);
							currenttime = Integer.parseInt(secondlbl.getText().toString());
							maxtime = 59;
							timeincrement = 10;
						}
					});

					secondlbl.performClick();

					TextView minuteslbl = (TextView) dialogView.findViewById(R.id.lblminsvalue_text_view);
					minuteslbl.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							selectedlbl = minuteslbl;
							minuteslbl.setSelected(true);
							currenttime = Integer.parseInt(minuteslbl.getText().toString());
							maxtime = 59;
							timeincrement = 1;

						}
					});

					TextView hourslbl = (TextView) dialogView.findViewById(R.id.lblhoursvalue_text_view);
					hourslbl.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							selectedlbl = hourslbl;
							hourslbl.setSelected(true);
							currenttime = Integer.parseInt(hourslbl.getText().toString());
							maxtime = 12;
							timeincrement = 1;
						}
					});

					TextView addTimeTv = (TextView) dialogView.findViewById(R.id.addtimebtn_text_view);
					addTimeTv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							currenttime = currenttime + timeincrement;

							if (currenttime >= maxtime) {
								currenttime = maxtime;
							}
							String appendZero = "0";

							String ctime = Integer.toString(currenttime);

							if (currenttime < 10) {
								ctime = appendZero + ctime;
							}

							selectedlbl.setText(ctime);

						}
					});

					TextView reduceTimeTv = (TextView) dialogView.findViewById(R.id.reducetimebtn_text_view);
					reduceTimeTv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							currenttime = currenttime - timeincrement;

							if (currenttime <= 0) {
								currenttime = 0;
							}

							String appendZero = "0";
							String ctime = Integer.toString(currenttime);

							if (currenttime < 10) {
								ctime = appendZero + ctime;
							}

							selectedlbl.setText(ctime);
						}
					});

					ConstraintLayout startActivityBtn = (ConstraintLayout) dialogView.findViewById(R.id.startactivitybtn_constraint_layout);
					ConstraintLayout stopActivityBtn = (ConstraintLayout) dialogView.findViewById(R.id.startactivitybtn_two_constraint_layout);

					startActivityBtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							isActivityRunning = true;

							SharedPreferences activityPref = getSharedPreferences("ActivityPref", MODE_PRIVATE);
							String activityUser = activityPref.getString("Username", "0");
							if(activityUser.matches(username)){
								activityID = activityPref.getInt("ActivityID", 0);
								activityID += 1;

								SharedPreferences.Editor editor = activityPref.edit();
								editor.putString("Username", username);
								editor.putInt("ActivityID", activityID);
								editor.commit();
							}else{
								SharedPreferences.Editor editor = activityPref.edit();
								editor.putString("Username", username);
								editor.putInt("ActivityID", 1);
								editor.commit();
								activityID = 1;
							}

							hbstart = HeartData;

							PulseCountDown p1 = (PulseCountDown) dialogView.findViewById(R.id.pulseCountDown);
							p1.setVisibility(View.VISIBLE);

							int hours = Integer.parseInt(hourslbl.getText().toString());
							int minutes = Integer.parseInt(minuteslbl.getText().toString());
							int seconds = Integer.parseInt(secondlbl.getText().toString());

							int hoursMillis = hours * 3600000;
							int minutesMillis = minutes * 60000;
							int secondsMillis = seconds * 1000;
							int bufferMillis = 5 * 1000;
							int timermillis = hoursMillis + minutesMillis + secondsMillis + bufferMillis;

							AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
							ViewGroup viewGroup = findViewById(android.R.id.content);
							View dialogViewTimer = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.timer_activity, viewGroup, false);

							TextView timerTv = (TextView) dialogViewTimer.findViewById(R.id.timertv_text_view);

							Button stopBtn = (Button) dialogViewTimer.findViewById(R.id.startactivitybtn_button);
							Button cancelBtn = dialogViewTimer.findViewById(R.id.cancelbutton_button);

							builder1.setView(dialogViewTimer);
							AlertDialog alertDialog1 = builder1.create();
							alertDialog1.setCancelable(false);

							p1.start(new OnCountdownCompleted() {
								@Override
								public void completed() {
									try {
										alertDialog1.show();
									}catch (Exception e){

									}

								}
							});

							countDownTimer = new CountDownTimer(timermillis, 1000) {
								@Override
								public void onTick(long millisUntilFinished) {

									long millis = millisUntilFinished;

									String hms = String.format("%02d:%02d:%02d",
											TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
											TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
													TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
											TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
													TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

									timerTv.setText(hms);

								}

								@Override
								public void onFinish() {
									isActivityRunning = false;

									timerTv.setText("Finish !");
									alertDialog1.dismiss();
									tts.speak("Activity Finished. Measuring recovery", TextToSpeech.QUEUE_ADD, null, null);

									measureRecoveryDialog = new ProgressDialog(ProfileActivity.this);
									measureRecoveryDialog.setTitle("Heart Rate Recovery");
									measureRecoveryDialog.setMessage("Measuring recovery in 1st minute ...");
									measureRecoveryDialog.show();
									//Toast.makeText(getApplicationContext(), "Activity Finished. Measuring recovery", Toast.LENGTH_SHORT).show();

									measureRecoveryRate();
								}
							};

							countDownTimer.start();

							stopBtn.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									isActivityRunning = false;
									countDownTimer.cancel();
									alertDialog1.dismiss();
									alertDialog2.dismiss();
									//Toast.makeText(getApplicationContext(), "Activity Stopped", Toast.LENGTH_LONG).show();
									measureRecoveryRate();
								}
							});

							cancelBtn.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialog1.dismiss();
									try{
									countDownTimer.cancel();
									countDownTimer1.cancel();
									countDownTimer2.cancel();
									recoveryAsync.cancel(true);
									}catch(Exception e){

									}
								}
							});

						}
					});

				builder.setView(dialogView);
				alertDialog2 = builder.create();
				alertDialog2.setCancelable(false);

				try {
					alertDialog2.show();
				}catch (Exception e){
					Log.e("Exception", "AlertDialog Exception");
				}

				stopActivityBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						isActivityRunning = false;
						try {
							countDownTimer.cancel();
						}catch (Exception e){

						}
						alertDialog2.dismiss();
					}
				});

				goalPref = getSharedPreferences("GoalPref", MODE_PRIVATE);
				SharedPreferences.Editor editor = goalPref.edit();
				editor.putString("GoalName", zone.toString());
				editor.commit();

				updateHighsLows(0, 0);

				tvHighs.setText("0");
				tvLows.setText("0");

				tvHeartGoal = (TextView) findViewById(R.id.tvgoalstatus_text_view);
				tvHeartGoal.setText(zone.toString());

				tvWeightGoal = (TextView) findViewById(R.id.tvgoalstatus_two_text_view);
				tvWeightGoal.setText(zone.toString());

				updateHeartZoneColor(zonelevel);
			}
		});
		alertDialog.create();
		alertDialog.show();
	}

	private void measureRecoveryRate() {

		hbend = HeartData;

		long oneminuteMillis = 1 * 60000;
		long twominuteMillis = 2 * 60000;

		currentHeartRate = HeartData;
		oneMinHeartRate = "0";
		twoMinHeartRate = "0";

		countDownTimer1 = new CountDownTimer(oneminuteMillis, 1000){

			@Override
			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {
				oneMinHeartRate = HeartData;
				HashMap<String, String> dropResult = calculateHeartDropRate(currentHeartRate, oneMinHeartRate, 1);
				oneMinTv.setText(dropResult.get("DropRate"));
				drop1m = dropResult.get("DropRate");
				hbstatus = dropResult.get("DropMessage");
				try {
					measureRecoveryDialog.setMessage(hbstatus + ". Measuring heart recovery in 2nd minute ...");
				}catch (Exception e){

				}
				updateHeartRecovery();
				recoveryAlarmTv.setText(dropResult.get("DropMessage"));
				tts.speak(dropResult.get("DropMessage"), TextToSpeech.QUEUE_FLUSH, null, null);
				recoveryAlarmBg.setBackgroundColor(Integer.parseInt(dropResult.get("DropColor")));

				switch(Integer.parseInt(dropResult.get("DropColor"))){
					case Color.RED:
						recovery1MinBg.setBackgroundResource(R.drawable.status_high);
						break;

					case Color.GREEN:
						recovery1MinBg.setBackgroundResource(R.drawable.status_green);
						break;

					case Color.BLUE:
						recovery1MinBg.setBackgroundResource(R.drawable.status_low);
						break;

					case Color.MAGENTA:
						recovery1MinBg.setBackgroundResource(R.drawable.status_perfect);
						break;
				}
			}
		}.start();

		countDownTimer2 = new CountDownTimer(twominuteMillis, 1000){

			@Override
			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {

				twoMinHeartRate = HeartData;
				HashMap<String, String> dropResult = calculateHeartDropRate(currentHeartRate, twoMinHeartRate, 2);
				twoMinTv.setText(dropResult.get("DropRate"));
				drop2m = dropResult.get("DropRate");
				hbstatus = dropResult.get("DropMessage");
				measureRecoveryDialog.dismiss();
				updateHeartRecovery();
				recoveryAlarmTv.setText(dropResult.get("DropMessage"));
				tts.speak(dropResult.get("DropMessage"), TextToSpeech.QUEUE_FLUSH, null, null);
				recoveryAlarmBg.setBackgroundColor(Integer.parseInt(dropResult.get("DropColor")));


				switch(Integer.parseInt(dropResult.get("DropColor"))){
					case Color.RED:
						recovery2MinBg.setBackgroundResource(R.drawable.status_high);
						recTimeBg.setBackgroundResource(R.drawable.status_high);
						break;

					case Color.GREEN:
						recovery2MinBg.setBackgroundResource(R.drawable.status_green);
						recTimeBg.setBackgroundResource(R.drawable.status_green);
						break;

					case Color.BLUE:
						recovery2MinBg.setBackgroundResource(R.drawable.status_low);
						recTimeBg.setBackgroundResource(R.drawable.status_low);
						break;

					case Color.MAGENTA:
						recovery2MinBg.setBackgroundResource(R.drawable.status_perfect);
						recTimeBg.setBackgroundResource(R.drawable.status_perfect);
						break;
				}
			}
		}.start();

		recoveryAsync = new AsyncTask<String, String, Long>() {
			@Override
			protected Long doInBackground(String... params) {

				long startTime = System.currentTimeMillis();
				long stopTime = 0;
				try{
				Log.i("Recovery Rate Check Start:", Integer.toString(recrate));

				while (Integer.parseInt(HeartData) > recrate) {
						// Do nothing
					Log.i("Recovery Rate Check:", Integer.toString(recrate));
				}

				Log.i("Recovery Rate Check End:", Integer.toString(recrate));

				stopTime = System.currentTimeMillis() - startTime;
				Log.i("Recovery Stop Time", Long.toString(stopTime));
					// Convert recovery time into seconds or minute
				}catch (Exception e){
					Log.e("TM Heart Exception", e.getMessage());
				}
				return stopTime;
			}

			@Override
			protected void onPostExecute(Long stopTime) {
				String result = "";
				recTime = 0;
				if(stopTime <= 60000){
					recTime = Math.floorDiv(stopTime, 1000);
					result = recTime + " seconds";
				}else if(stopTime <= 3600000){
					recTime = Math.floorDiv(stopTime,60000);
					result = recTime + " minutes";
				}else {
					recTime = Math.floorDiv(stopTime,360000);
					result = recTime + " hour";
				}
				Log.i("RecTime", Long.toString(recTime));
				Log.i("RecTime StopTime", Long.toString(stopTime));

				recTimeTv.setText(Long.toString(recTime));
				recoveryAlarmTv.setText("You have recovered in " + result);
				tts.speak("You have recovered in " + result, TextToSpeech.QUEUE_ADD, null, null);
				updateHeartRecovery();
			}
		}.execute();
	}

	private void updateHeartRecovery() {
		goal = goal.replace("&", "and");
		String url = "https://hackveda.in/tm_heart/heartrecovery.php?email="+username+"&hbstatus="+hbstatus+"&maxrate="+maxrate+"&goal="+goal+"&activityid="+activityID+"&hbstart="+hbstart+"&hbend="+hbend+"&hbend1m="+oneMinHeartRate+"&hbend2m="+twoMinHeartRate+"&drop1m="+drop1m+"&drop2m="+drop2m+"&recrate="+recrate+"&rectime="+recTime;
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject responseObject = new JSONObject(response);
					String status = responseObject.getString("HRStatus");
					String message = responseObject.getString("HRMessage");
					Log.i("ServerUpdate HeartRecovery: ", message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.i("ServerError HeartRecovery: ", error.getLocalizedMessage());
			}
		});
		requestQueue.add(request);
	}

	private HashMap<String, String> calculateHeartDropRate(String currentHeartRate, String oneMinHeartRate, int mins) {
		int droprate = 0;
		String dropmessage = "";
		int dropColor = 0;

		int heartrate1 = 0;
		int heartrate2 = 0;

		HashMap<String, String> result = new HashMap<String, String>();
		if(currentHeartRate != null && oneMinHeartRate != null) {
			heartrate1 = Integer.parseInt(currentHeartRate);
			heartrate2 = Integer.parseInt(oneMinHeartRate);
		}

		if(heartrate1 >= heartrate2){
			droprate = heartrate1 - heartrate2;
			String dropInfo = "";

			if(mins == 1) {
				if (droprate > 30) {
					dropInfo = "Excellent!";
					dropColor = Color.MAGENTA;

				} else if (droprate > 20) {
					dropInfo = "Good!";
					dropColor = Color.GREEN;

				} else if (droprate > 12) {
					dropInfo = "Borderline!";
					dropColor = Color.BLUE;

				} else {
					dropInfo = "Abnormal!";
					dropColor = Color.RED;

				}
			}else if(mins == 2){
				if(droprate > 60){
					dropInfo = "Excellent!";
					dropColor = Color.MAGENTA;

				}else if(droprate > 40){
					dropInfo = "Good!";
					dropColor = Color.GREEN;

				}else if(droprate > 24){
					dropInfo = "Borderline!";
					dropColor = Color.BLUE;

				}else{
					dropInfo = "Abnormal!";
					dropColor = Color.RED;

				}
			}

			dropmessage = dropInfo + " Your Heart rate dropped by " + droprate + " beats in " + mins +" minute";

		}else{
			droprate = heartrate2 - heartrate1;
			dropmessage = "Horrible. Heart rate increased by " + droprate + " beats in " + mins + " minute";
			dropColor = Color.RED;

		}

		result.put("DropRate", Integer.toString(droprate));
		result.put("DropMessage", dropmessage);
		result.put("DropColor", Integer.toString(dropColor));


		return result;
	}


	;
	private void updateHeartZoneColor(String zonelevel) {
		try {
			//Toast.makeText(getApplicationContext(), zonelevel, Toast.LENGTH_SHORT).show();
			int lowerlimit = Integer.parseInt(zonelevel);
			int upperlimit = lowerlimit + 18;


			lowerlimit = maxrate * lowerlimit / 100;
			upperlimit = maxrate * upperlimit / 100;

			int pickuplimit = (upperlimit + lowerlimit) / 2;
			int pickupby = pickuplimit * 5 / 100;
			pickuplimit = pickuplimit - pickupby;

			//Toast.makeText(getApplicationContext(), "MR: " + maxrate + " LL: "+lowerlimit + " UL: " + upperlimit, Toast.LENGTH_SHORT).show();
			tvMaxRate.setText(Integer.toString(upperlimit));


			int heartlimit = 0;

			heartlimit = Integer.parseInt(HeartData);

			int zonecolor = 0;
			String alarm = "";

			if (heartlimit > upperlimit) {
				zonecolor = ZoneColor.get("HIGH");
				alarm = "HIGH";
			} else if(heartlimit > lowerlimit && heartlimit <= pickuplimit){
				zonecolor = ZoneColor.get("PERFECT");
				alarm = "PICKUP";
			}else if (heartlimit <= upperlimit && heartlimit >= lowerlimit) {
				zonecolor = ZoneColor.get("PERFECT");
				alarm = "PERFECT";
			} else if (heartlimit < lowerlimit) {
				zonecolor = ZoneColor.get("LOW");
				alarm = "LOW";
			}

			View currentHeartRateView = findViewById(R.id.bgcurrentheartrate_constraint_layout);
			currentHeartRateView.setBackgroundResource(zonecolor);
			//Toast.makeText(getApplicationContext(), "ZoneColor" + zonecolor, Toast.LENGTH_SHORT).show();

			updateAlarm(alarm);
			updateHBServer(alarm);
		} catch (NumberFormatException e) {
			Toast.makeText(getApplicationContext(), "Please synchronize your watch", Toast.LENGTH_LONG).show();
			tvAlarm.setText("Please synchronize your watch");
		}
	}

	private void updateHBServer(String alarm) {
		goal = goal.replace("&", "and");
		String url = "https://hackveda.in/tm_heart/heartupdate.php?email="+username+"&hb="+HeartData+"&hbstatus="+alarm+"&goal="+goal+"&maxrate="+maxrate;
		RequestQueue requestQueue2 = Volley.newRequestQueue(this);
		StringRequest request2 = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject responseObject = new JSONObject(response);
					String status = responseObject.getString("HBStatus");
					String message = responseObject.getString("HBMessage");
					Log.i("ServerUpdate HeartBeat: ", message);
				} catch (JSONException e) {
					Log.e("ServerError HeartBeat: ", e.getMessage());
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
					Log.e("ServerError HeartBeat: ", error.getLocalizedMessage());
			}
		});
		requestQueue2.add(request2);
	}

	private void updateAlarm(String alarm) {

		String message = "";


		if (alarm.matches("HIGH")) {
			message = "Your heart rate is High. Decrease speed";

		} else if (alarm.matches("LOW")) {
			message = "Your heart rate is Low. Speed up";

		} else if (alarm.matches("PERFECT")) {
			message = "Your heart rate is Perfect. Keep it up";

		} else if(alarm.matches("PICKUP")){
			message = "Your heart rate is Reducing. Increase speed";
		}

		int queueMode = 0;
		if (!alarm.matches(alarmStatus)) {
			queueMode = TextToSpeech.QUEUE_FLUSH;
			tts.speak(message, queueMode, null, null);
			tvAlarm.setText(message);
			alarmStatus = alarm;

			if (alarm.matches("HIGH")) {
				highCounter++;
				tvHighs.setText(Integer.toString(highCounter));
				viewHeartMessageBg.setBackgroundColor(Color.RED);
			} else if (alarm.matches("LOW")) {
				lowCounter++;
				tvLows.setText(Integer.toString(lowCounter));
				viewHeartMessageBg.setBackgroundColor(Color.BLUE);
			} else if (alarm.matches("PICKUP")) {
				viewHeartMessageBg.setBackgroundColor(Color.CYAN);
			}else {
				viewHeartMessageBg.setBackgroundColor(Color.MAGENTA);
			}
			updateHighsLows(highCounter, lowCounter);
		} else if (alarm.matches("HIGH")) {
			queueMode = TextToSpeech.QUEUE_ADD;
			//tts.speak(message, queueMode, null, null);
			tvAlarm.setText(message);
			alarmStatus = alarm;
		} else if(alarm.matches("PICKUP")){
			queueMode = TextToSpeech.QUEUE_FLUSH;
			//tts.speak(message, queueMode, null, null);
			tvAlarm.setText(message);
			alarmStatus = alarm;
		}



		sendAlarmToWatch(queueMode, message, alarm);
	}

	private void updateHighsLows(int highCounter, int lowCounter) {
		configPref = getSharedPreferences("ConfigPref", MODE_PRIVATE);
		SharedPreferences.Editor editor = configPref.edit();
		editor.putInt("HeartHighs", highCounter);
		editor.putInt("HeartLows", lowCounter);
		editor.commit();

		sendHighsLowsToServer(highCounter, lowCounter);
	}

	private void sendHighsLowsToServer(int highCounter, int lowCounter) {
		// Start Here
	}

	public void sendAlarmToWatch(int queueMode, String message, String alarm) {
		DataClient dataclient = Wearable.getDataClient(getApplicationContext());

		PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/tmalarm");
		putDataMapReq.getDataMap().putString("queueMode", Integer.toString(queueMode));
		putDataMapReq.getDataMap().putString("message", message);
		putDataMapReq.getDataMap().putString("alarm", alarm);
		PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
		putDataReq.setUrgent();
		Task<DataItem> putDataTask = dataclient.putDataItem(putDataReq);
	}

	public void onTvEditProfilePressed() {

		this.startConfigureActivity();
	}

	private void startConfigureActivity() {

		this.startActivity(ConfigureActivity.newIntent(this));
	}


	@Override
	protected void onResume() {
		super.onResume();
		Wearable.getDataClient(this).addListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Wearable.getDataClient(this).removeListener(this);
		Wearable.getDataClient(this).addListener(this);
	}

	@Override
	public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
		for (DataEvent event : dataEventBuffer) {
			if (event.getType() == DataEvent.TYPE_CHANGED) {
				// DataItem changed
				DataItem item = event.getDataItem();
				if (item.getUri().getPath().compareTo("/tmheart") == 0) {
					DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
					HeartData = dataMap.getString("HeartRate");
					//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show()
					TextView tvHeartRate = (TextView) findViewById(R.id.lblcurrentheartratev_text_view);
					tvHeartRate.setText(HeartData);
					if(isActivityRunning) {
						updateHeartZoneColor(currentGoalValue);
					}
				}
			}
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			//String[] filePathColumn = {MediaStore.Images.Media.DATA};
			/*String[] filePathColumn = {};

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();*/

			ImageView imageView = (ImageView) findViewById(R.id.profileimage_image_view);
			//imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			imageView.setImageURI(Uri.parse(String.valueOf(selectedImage)));
			imageView.setVisibility(View.VISIBLE);

			Toast.makeText(getApplicationContext(), "ProfileImage saved", Toast.LENGTH_SHORT).show();

		}else{
			Toast.makeText(getApplicationContext(), "ProfileImage not selected", Toast.LENGTH_SHORT).show();
		}
	}
}
