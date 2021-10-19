package com.example.workout_accel;

import android.Manifest;
import android.content.Context;
//import android.support.v7.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

//Imports for hardware sensors
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "BOSTON";

    private float lastX, lastY, lastZ;  //old coordinate positions from accelerometer, needed to calculate delta.
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;

    //Components in XML
    private ListView list_shake;
    private Button start_btn;
    private Button stop_btn;
    private TextView step_count;

    // value used to determine whether user shook the device "significantly"
    private static int SIGNIFICANT_SHAKE_EASY = 500;   //tweak this as necessary
    private static int SIGNIFICANT_SHAKE_MED = 1000;   //tweak this as necessary
    private static int SIGNIFICANT_SHAKE_HARD = 2000;   //tweak this as necessary
    private CameraManager CamManager;
    private String CamID;
    private DecimalFormat df;

    private String choose_workout;
    private int count;
    boolean programon = false;

    //variables for timer
    long millis;
    int seconds;
    int minutes;
    int hours;
    long startTime;
    long endTime;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Locks screen in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        disableAccelerometerListening();

        start_btn = (Button) findViewById(R.id.start_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);
        step_count = (TextView) findViewById(R.id.step_count);

        list_shake = (ListView) findViewById(R.id.list_shake);
        //list_shake.setAdapter(adapter);

        final String[] workout_types = {"Easy","Medium","Hard"}; //Raw Data, array of strings to put into our ListAdapter.
        //ArrayAdapter is the interface, the go between, between UI and Data
        ArrayAdapter ListAdapter = new ArrayAdapter<String>(MainActivity.this,           //Context
                android.R.layout.simple_list_item_activated_1, //type of list (simple)
                workout_types);                            //Data for the list

//3. ListViews work (display items) by binding themselves to an adapter.
        list_shake.setAdapter(ListAdapter);    //Let's put some things in our simple listview by binding it to our adaptor.

// 4. Create an onClick Handler.  Not for the ListView, but for its items!
        list_shake.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String getworkout;

                getworkout = String.valueOf(parent.getItemAtPosition(position));  //Parent refers to the parent of the item, the ListView.  position is the index of the item clicked.

                //If statement of possible workouts
                if (getworkout == "Easy") {
                    choose_workout = getworkout; //choose_workout is a global variable
                    mp = MediaPlayer.create(MainActivity.this, R.raw.superman); //prevents mp from being set to null
                    disableAccelerometerListening();
                } else if (getworkout == "Medium") {
                    choose_workout = getworkout;
                    mp = MediaPlayer.create(MainActivity.this, R.raw.chariots_of_fire); //prevents mp from being set to null
                    disableAccelerometerListening();
                } else if (getworkout == "Hard") {
                    choose_workout = getworkout;
                    mp = MediaPlayer.create(MainActivity.this, R.raw.rocky); //prevents mp from being set to null
                    disableAccelerometerListening();
                }


                CamManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    CamID = CamManager.getCameraIdList()[0];  //rear camera is at index 0
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }



                // initialize acceleration values
                acceleration = 0.00f;                                         //Initializing Acceleration data.
                currentAcceleration = SensorManager.GRAVITY_EARTH;            //We live on Earth.
                lastAcceleration = SensorManager.GRAVITY_EARTH;               //Ctrl-Click to see where else we could use our phone.

            }
        });

        //Intializing the start button
        start_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                count = 0;
                step_count.setText(count + " Steps");
                startTime = System.currentTimeMillis(); //gets current time in millis
                start_btn.setEnabled(false);
                list_shake.setEnabled(false);
                programon = true;
                enableAccelerometerListening(); //enable the accelerometer to start listening
            }
        });

        //Initializing the stop button to turn off all functions except for enabling the start button and listview
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                stopmp();
                disableAccelerometerListening();
                LightOff();
                programon = false;
                start_btn.setEnabled(true);
                list_shake.setEnabled(true);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart Triggered.");
    }

    //restores the necessary values to maintain the app
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int savedcounter = savedInstanceState.getInt("counter");
        count = savedcounter;
        boolean programstatus = savedInstanceState.getBoolean("Programstate");
        programon = programstatus;
        String workouttype = savedInstanceState.getString("Workout");
        choose_workout = workouttype;
        long timemillis = savedInstanceState.getLong("Millis");
        millis = timemillis;
        int timeseconds = savedInstanceState.getInt("Seconds");
        seconds = timeseconds;
        int timeminutes = savedInstanceState.getInt("Minutes");
        minutes = timeminutes;
        int timehours = savedInstanceState.getInt("Hours");
        hours = timehours;
        long timestarttime = savedInstanceState.getLong("Starttime");
        startTime = timestarttime;
        long timeendtime = savedInstanceState.getLong("Endtime");
        endTime = timeendtime;
    }


    @Override
    protected void onResume() {
        enableAccelerometerListening();
        super.onResume();
    }

    //saves the necessary values
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", count);
        outState.putBoolean("Programstate", programon);
        outState.putString("Workout", choose_workout);
        outState.putLong("Millis", millis);
        outState.putInt("Seconds", seconds);
        outState.putInt("Minutes", minutes);
        outState.putInt("Hours", hours);
        outState.putLong("Starttime",startTime);
        outState.putLong("Endtime",endTime);
    }

    @Override
    protected void onPause() {
        enableAccelerometerListening();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop Triggered.");
        enableAccelerometerListening();
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
        @RequiresApi(api = Build.VERSION_CODES.M)
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
            acceleration = currentAcceleration *  (currentAcceleration - lastAcceleration);

            // if the acceleration is above a certain threshold
            if (choose_workout == "Easy") {
                if (acceleration > SIGNIFICANT_SHAKE_EASY) {
                    Log.e(TAG, "delta x = " + (x - lastX));
                    Log.e(TAG, "delta y = " + (y - lastY));
                    Log.e(TAG, "delta z = " + (z - lastZ));

                    count++;
                }
                //blinking flash turns on
                step_count.setText(count + " Steps");
                if (count >= 20){
                    LightOn();
                    LightOff();
                }
                //superman song plays
                if (count == 30){
                    mp.start();
                }
                //stops the accelerometer at 100
                if (count == 100){
                    endTime = System.currentTimeMillis();
                    run();
                    System.out.println(endTime);
                    Toast.makeText(getApplicationContext(), String.format("Time Elapsed: %02d:%02d:%02d", hours, minutes, seconds), Toast.LENGTH_LONG).show();
                    disableAccelerometerListening();
                    stopmp();
                    programon = false;
                    start_btn.setEnabled(true);
                    list_shake.setEnabled(true);
                }
            }
            else if (choose_workout == "Medium") {
                if(acceleration > SIGNIFICANT_SHAKE_MED) {
                    Log.e(TAG, "delta x = " + (x - lastX));
                    Log.e(TAG, "delta y = " + (y - lastY));
                    Log.e(TAG, "delta z = " + (z - lastZ));

                    count++;
                }
                step_count.setText(count + " Steps");

                //blinking flash turns on
                if (count >= 40) {
                    LightOn();
                    LightOff();
                }

                //chariots of fire music plays
                if (count == 45) {
                    mp.start();
                }

                //stops the accelerometer at 100
                if (count == 100){
                    endTime = System.currentTimeMillis();
                    run();
                    Toast.makeText(getApplicationContext(), String.format("Time Elapsed: %02d:%02d:%02d", hours, minutes, seconds), Toast.LENGTH_LONG).show();
                    disableAccelerometerListening();
                    stopmp();
                    programon = false;
                    start_btn.setEnabled(true);
                    list_shake.setEnabled(true);
                }
            }
            else if (choose_workout == "Hard") {
                if (acceleration > SIGNIFICANT_SHAKE_HARD) {
                    Log.e(TAG, "delta x = " + (x - lastX));
                    Log.e(TAG, "delta y = " + (y - lastY));
                    Log.e(TAG, "delta z = " + (z - lastZ));

                    count++;
                }
                step_count.setText(count + " Steps");

                //blinking flash turns on
                if (count >= 40) {
                    LightOn();
                    LightOff();
                }

                //rocky song plays
                if (count == 60) {
                    mp.start();
                }

                //stops the accelerometer at 100
                if (count == 100) {
                    endTime = System.currentTimeMillis();
                    run();
                    Toast.makeText(getApplicationContext(), String.format("Time Elapsed: %02d:%02d:%02d", hours, minutes, seconds), Toast.LENGTH_LONG).show();
                    disableAccelerometerListening();
                    stopmp();
                    programon = false;
                    start_btn.setEnabled(true);
                    list_shake.setEnabled(true);
                }
            }
            else

            lastX = x;
            lastY = y;
            lastZ = z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    //function to calculate the time elapsed
    private void run () {
        millis = endTime - startTime;
        System.out.println(millis);
        seconds = (int) (millis / 1000);
        minutes = seconds / 60;
        hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 60;
    }

    //function to stop music player
    private void stopmp() {
        try {
            if (mp != null) {
                if (mp.isPlaying())
                    mp.stop();
                mp.reset();
                mp.release();
                mp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //turns flash on
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void LightOn()
    {
        try {
            CamManager.setTorchMode(CamID, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //turns flash off
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void LightOff()
    {
        try {
            CamManager.setTorchMode(CamID, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}