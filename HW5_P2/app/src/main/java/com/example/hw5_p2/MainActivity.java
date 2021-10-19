package com.example.hw5_p2;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity{

    private String TAG = "BOSTON";

    private float lastX, lastY, lastZ;  //old coordinate positions from accelerometer, needed to calculate delta.
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    //webview
    private WebView wv1;

    private SeekBar seekBar;

    // value used to determine whether user shook the device "significantly"
    private static int SIGNIFICANT_SHAKE = 50;   //tweak this as necessary
    private CameraManager CamManager;
    private String CamID;
    private DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //webview
        wv1=(WebView)findViewById(R.id.webview);
        wv1.setWebViewClient(new MyBrowser());

        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        df = new DecimalFormat("0.00");

        // initialize acceleration values
        acceleration = 0.00f;                                         //Initializing Acceleration data.
        currentAcceleration = SensorManager.GRAVITY_EARTH;            //We live on Earth.
        lastAcceleration = SensorManager.GRAVITY_EARTH;               //Ctrl-Click to see where else we could use our phone.

        seekBar = (SeekBar)findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SIGNIFICANT_SHAKE = progress;
                Log.i(TAG, "New SIG SHAKE = " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart Triggered.");
        enableAccelerometerListening();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop Triggered.");
        disableAccelerometerListening();
        super.onStop();
    }

    // enable listening for accelerometer events
    private void enableAccelerometerListening() {
        // The Activity has a SensorManager Reference.
        // This is how we get the reference to the device's SensorManager.
        SensorManager sensorManager =
                (SensorManager) this.getSystemService(
                        Context.SENSOR_SERVICE);    //The last parm specifies the type of Sensor we want to monitor


        //Now that we have a Sensor Handle, let's start "listening" for movement (accelerometer).
        //3 parms, The Listener, Sensor Type (accelerometer), and Sampling Frequency.
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);   //don't set this too high, otw you will kill user's battery.
    }



    // disable listening for accelerometer events
    private void disableAccelerometerListening() {

//Disabling Sensor Event Listener is two step process.
        //1. Retrieve SensorManager Reference from the activity.
        //2. call unregisterListener to stop listening for sensor events
        //THis will prevent interruptions of other Apps and save battery.

        // get the SensorManager
        SensorManager sensorManager =
                (SensorManager) this.getSystemService(
                        Context.SENSOR_SERVICE);

        // stop listening for accelerometer events
        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // get x, y, and z values for the SensorEvent
            //each time the event fires, we have access to three dimensions.
            //compares these values to previous values to determine how "fast"
            // the device was shaken.
            //Ref: http://developer.android.com/reference/android/hardware/SensorEvent.html

            float x = event.values[0];   //obtaining the latest sensor data.
            float y = event.values[1];   //sort of ugly, but this is how data is captured.
            float z = event.values[2];

            // save previous acceleration value
            lastAcceleration = currentAcceleration;

            // calculate the current acceleration
            currentAcceleration = x * x + y * y + z * z;   //This is a simplified calculation, to be real we would need time and a square root.

            // calculate the change in acceleration        //Also simplified, but good enough to determine random shaking.
            acceleration = Math.abs((currentAcceleration *  (currentAcceleration - lastAcceleration)) * 100);


            // if the acceleration is above a certain threshold
            if (acceleration > SIGNIFICANT_SHAKE) {
                Log.i(TAG, "delta x = " + (x - lastX));
                Log.i(TAG, "delta y = " + (y - lastY));
                Log.i(TAG, "delta z = " + (z - lastZ));
                Toast.makeText(getBaseContext(), "SIGNIFICANT SHAKE!", Toast.LENGTH_SHORT).show();
                float deltax = x - lastX;
                float deltay = y - lastY;
                float deltaz = z - lastZ;
                float maxmov = deltax * deltax + deltay * deltay + deltaz * deltaz;
                if (maxmov > SIGNIFICANT_SHAKE + 25) {
                    wv1.loadUrl("https://jumpingjaxfitness.files.wordpress.com/2010/07/dizziness.jpg");
                }
                else {
                    if (deltax > deltay && deltax > deltaz) {
                        wv1.loadUrl("https://www.ecosia.org/");
                    }
                    if (deltay > deltax && deltay > deltaz) {
                        wv1.loadUrl("https://www.dogpile.com/");
                    }
                    if (deltaz > deltax && deltaz > deltay) {
                        wv1.loadUrl("https://buzzsumo.com/");
                    }

                }
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}