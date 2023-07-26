package hackveda.devanshu.tmheart;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends WearableActivity implements SensorEventListener, DataClient.OnDataChangedListener {

    private TextView mTextView;
    private ObjectAnimator animator;
    private TextToSpeech tts;
    private View section1Layout;
    private View appLogoLayout;
    private boolean mutestatus = true;
    private int mutecounter = 0;
    private SensorManager sensorService;
    private Sensor heartSensor;
    final int min = 70;
    final int max = 75;
    private TextView tvHeartRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();


        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                != PackageManager.PERMISSION_GRANTED){

            String[] permissions = new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY };
            requestPermissions(permissions, 1);

        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED){

            String[] permissions = new String[]{Manifest.permission.VIBRATE };
            requestPermissions(permissions, 1);

        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            String[] permissions = new String[]{Manifest.permission.BODY_SENSORS};
            requestPermissions(permissions, 1);

        }else{
            getHeartRate();

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);

            AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, 0);
        }



        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
        tts.setLanguage(Locale.ENGLISH);

        section1Layout = findViewById(R.id.section1_constraint_layout);
        appLogoLayout = findViewById(R.id.applogo_constraint_layout);
        appLogoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mutestatus){
                    mutestatus = false;
                    Toast.makeText(getApplicationContext(), "Muted", Toast.LENGTH_SHORT).show();
                    tts.speak("T M Heart Muted", TextToSpeech.QUEUE_ADD, null, null);
                }else{
                    mutestatus = true;
                    Toast.makeText(getApplicationContext(), "Unmuted", Toast.LENGTH_SHORT).show();
                    tts.speak("T M Heart Unmuted", TextToSpeech.QUEUE_ADD, null, null);
                }
            }
        });

        tvHeartRate = (TextView) findViewById(R.id.lblapptitle_two_text_view);

        new CountDownTimer(216000000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                Random rand = new Random();

                int rate = ThreadLocalRandom.current().nextInt(min, max);
                sendData(Integer.toString(rate));
                tvHeartRate.setText(Integer.toString(rate));
                tvHeartRate.setTextSize(50);
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    void sendData(String heartrate){
        try {
            DataClient dataclient = Wearable.getDataClient(getApplicationContext());

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/tmheart");
            putDataMapReq.getDataMap().putString("HeartRate", heartrate);
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            putDataReq.setUrgent();
            Task<DataItem> putDataTask = dataclient.putDataItem(putDataReq);
            // Further event handling of task can be done here

        }catch (NumberFormatException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void vibrate(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getHeartRate();
        }else{
            Toast.makeText(getApplicationContext(), "Permission declined. App will not function", Toast.LENGTH_LONG).show();
        }
    }

    void getHeartRate(){
        Toast.makeText(getApplicationContext(), "Initialising heart sensor...", Toast.LENGTH_LONG).show();
        sensorService = (SensorManager) getSystemService(SENSOR_SERVICE);
        heartSensor = sensorService.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorService.registerListener(this, heartSensor, SensorManager.SENSOR_DELAY_NORMAL);

        ImageView imageview = (ImageView) findViewById(R.id.appimage_image_view);
        animator = ObjectAnimator.ofPropertyValuesHolder(imageview, PropertyValuesHolder.ofFloat("scaleX", 0.8F),
                PropertyValuesHolder.ofFloat("scaleY", 0.8f));
        animator.setDuration(300);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    if(event.sensor.getType() == Sensor.TYPE_HEART_RATE){
        int heart_rate = (int) event.values[0];
        //Toast.makeText(getApplicationContext(), Float.toString(heart_rate), Toast.LENGTH_LONG).show();
        animator.setDuration(60000/heart_rate);
        animator.start();
        tvHeartRate.setText(Integer.toString(heart_rate));
        tvHeartRate.setTextSize(50);

        TextView tvMessage = (TextView) findViewById(R.id.lblapptitle_three_text_view);
        tvMessage.setText("Real-time heart rate (bpm)");
        sendData(Integer.toString(heart_rate));

    }else{
        // Do nothing
    }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
        //sensorService.registerListener(this, heartSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
        //sensorService.unregisterListener(this, heartSensor);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/tmalarm") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    int queueMode = Integer.parseInt(dataMap.getString("queueMode"));
                    String alarm = dataMap.getString("alarm");
                    String message = dataMap.getString("message");
                    //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show()
                    if(mutestatus) {
                        tts.speak(message, queueMode, null, null);
                        vibrate();
                    }
                    TextView tvMessage = (TextView) findViewById(R.id.lblapptitle_three_text_view);
                    tvMessage.setText(message);

                    int color = 0;
                    if(alarm.matches("HIGH")){
                        color = Color.RED;
                    }else if(alarm.matches("LOW")){
                        color = Color.BLUE;
                    }else if(alarm.matches("PERFECT")){
                        color = Color.MAGENTA;
                    }else if(alarm.matches("PICKUP")){
                        color = Color.CYAN;
                    }

                    section1Layout.setBackgroundColor(color);
                    appLogoLayout.setBackgroundColor(color);
                }
            }
        }
    }
}
