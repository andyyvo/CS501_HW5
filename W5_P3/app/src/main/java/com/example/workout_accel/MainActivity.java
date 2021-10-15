package com.example.workout_accel;

import android.content.Context;
//import android.support.v7.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
    private int count = 0;
    boolean programon = false;

    long millis;
    int seconds;
    int minutes;
    int hours;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disableAccelerometerListening();
        start_btn = (Button) findViewById(R.id.start_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);
        step_count = (TextView) findViewById(R.id.step_count);

        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_main, workout_types);

        ListView list_shake = (ListView) findViewById(R.id.list_shake);
        //list_shake.setAdapter(adapter);

        final String[] workout_types = {"Easy","Medium","Hard"}; //Raw Data, array of strings to put into our ListAdapter.
        //ArrayAdapter is the interface, the go between, between UI and Data
        ArrayAdapter ListAdapter = new ArrayAdapter<String>(MainActivity.this,           //Context
                android.R.layout.simple_list_item_activated_1, //type of list (simple)
                workout_types);                            //Data for the list
        //We will see much more complex Adapters as we go.
//3. ListViews work (display items) by binding themselves to an adapter.
        list_shake.setAdapter(ListAdapter);    //Let's put some things in our simple listview by binding it to our adaptor.

// 4. Create an onClick Handler.  Not for the ListView, but for its items!
        list_shake.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String getworkout;

                getworkout = String.valueOf(parent.getItemAtPosition(position));  //Parent refers to the parent of the item, the ListView.  position is the index of the item clicked.
                if (getworkout == "Easy") {
                    choose_workout = getworkout;
                } else if (getworkout == "Medium") {
                    choose_workout = getworkout;
                } else if (getworkout == "Hard") {
                    choose_workout = getworkout;
                }

                //Toast.makeText(MainActivity.this, "You Clicked on " + , Toast.LENGTH_LONG).show();
                //df = new DecimalFormat("0.00");

                //flash

                CamManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    CamID = CamManager.getCameraIdList()[0];  //rear camera is at index 0
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }


                //flash


                // initialize acceleration values
                acceleration = 0.00f;                                         //Initializing Acceleration data.
                currentAcceleration = SensorManager.GRAVITY_EARTH;            //We live on Earth.
                lastAcceleration = SensorManager.GRAVITY_EARTH;               //Ctrl-Click to see where else we could use our phone.

            }
        });
        start_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                count = 0;
                run();
                if (choose_workout == "Easy"){
                    start_btn.setEnabled(false);
                    list_shake.setEnabled(false);
                    enableAccelerometerListening();
                    programon = true;
                    System.out.println(count);
                    while (programon == true){
                        step_count.setText(String.valueOf(count));
                        if (count == 20){
                            while (count > 20 && count < 101) {
                                LightOn();
                                LightOff();
                            }
                        }
                        else if (count == 30){
                            mp = MediaPlayer.create(MainActivity.this, R.raw.superman);
                            mp.start();
                        }
                        else if (count >= 100){
                            Toast.makeText(getApplicationContext(), String.format("%d:%02d:%02d", hours, minutes, seconds), Toast.LENGTH_LONG).show();
                            disableAccelerometerListening();
                            mp.stop();
                            programon = false;
                            start_btn.setEnabled(true);
                            list_shake.setEnabled(true);
                        }
                    }
                }
                else if(choose_workout == "Medium"){
                    start_btn.setEnabled(false);
                    list_shake.setEnabled(false);
                    enableAccelerometerListening();
                    programon = true;
                    while (programon == true) {
                        step_count.setText(count + "Steps");
                        if (count >= 40) {
                            while (count > 40 && count < 101) {
                                LightOn();
                                LightOff();
                            }
                        }
                        else if (count == 45) {
                            mp = MediaPlayer.create(MainActivity.this, R.raw.chariots_of_fire);
                            mp.start();
                        }
                        else if (count >= 100){
                            Toast.makeText(getApplicationContext(), String.format("%d:%02d:%02d", hours, minutes, seconds), Toast.LENGTH_LONG).show();
                            disableAccelerometerListening();
                            mp.stop();
                            programon = false;
                            start_btn.setEnabled(true);
                            list_shake.setEnabled(true);
                        }
                    }
                }
                else if(choose_workout == "Hard"){
                    start_btn.setEnabled(false);
                    list_shake.setEnabled(false);
                    programon = true;
                    while (programon == true) {
                        step_count.setText(count + "Steps");
                        if (count == 40) {
                            while (count > 40 && count < 101) {
                                LightOn();
                                LightOff();
                            }
                        }
                        else if (count == 60) {
                            mp = MediaPlayer.create(MainActivity.this, R.raw.rocky);
                            mp.start();
                        }
                        else if (count >= 100){
                            Toast.makeText(getApplicationContext(), String.format("%d:%02d:%02d", hours, minutes, seconds), Toast.LENGTH_LONG).show();
                            disableAccelerometerListening();
                            mp.stop();
                            programon = false;
                            start_btn.setEnabled(true);
                            list_shake.setEnabled(true);
                        }
                    }
                }
            }
        });
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                disableAccelerometerListening();
                LightOff();
                mp.stop();
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

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int savedcounter = savedInstanceState.getInt("counter");
        count = savedcounter;
        boolean programstatus = savedInstanceState.getBoolean("Programstate");
        programon = programstatus;
        String workouttype = savedInstanceState.getString("Workout");
        choose_workout = workouttype;
        int timemillis = savedInstanceState.getInt("Millis");
        millis = timemillis;
        int timeseconds = savedInstanceState.getInt("Seconds");
        seconds = timeseconds;
        int timeminutes = savedInstanceState.getInt("Minutes");
        minutes = timeminutes;
        int timehours = savedInstanceState.getInt("Hours");
        hours = timehours;
    }

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
            acceleration = currentAcceleration *  (currentAcceleration - lastAcceleration);

            // if the acceleration is above a certain threshold
            if (acceleration > SIGNIFICANT_SHAKE_EASY) {
                Log.e(TAG, "delta x = " + (x-lastX));
                Log.e(TAG, "delta y = " + (y-lastY));
                Log.e(TAG, "delta z = " + (z-lastZ));
                Toast.makeText(getBaseContext(), "SIGNIFICANT SHAKE!", Toast.LENGTH_SHORT).show();


                //tvDeltaX.setText(df.format(x-lastX));
                ///tvDeltaY.setText(df.format(y-lastY));
                //tvDeltaZ.setText(df.format(z-lastZ));

//                    tvDeltaX.setText(df.format(x));
//                    tvDeltaY.setText(df.format(y));
//                    tvDeltaZ.setText(df.format(z-9.8));
                count++;
            }
            else if(acceleration > SIGNIFICANT_SHAKE_MED) {
                Log.e(TAG, "delta x = " + (x-lastX));
                Log.e(TAG, "delta y = " + (y-lastY));
                Log.e(TAG, "delta z = " + (z-lastZ));
                Toast.makeText(getBaseContext(), "SIGNIFICANT SHAKE!", Toast.LENGTH_SHORT).show();


                //tvDeltaX.setText(df.format(x-lastX));
                ///tvDeltaY.setText(df.format(y-lastY));
                //tvDeltaZ.setText(df.format(z-lastZ));

//                    tvDeltaX.setText(df.format(x));
//                    tvDeltaY.setText(df.format(y));
//                    tvDeltaZ.setText(df.format(z-9.8));
                count++;
            }
            else if (acceleration > SIGNIFICANT_SHAKE_HARD) {
                Log.e(TAG, "delta x = " + (x-lastX));
                Log.e(TAG, "delta y = " + (y-lastY));
                Log.e(TAG, "delta z = " + (z-lastZ));
                Toast.makeText(getBaseContext(), "SIGNIFICANT SHAKE!", Toast.LENGTH_SHORT).show();


                //tvDeltaX.setText(df.format(x-lastX));
                ///tvDeltaY.setText(df.format(y-lastY));
                //tvDeltaZ.setText(df.format(z-lastZ));

//                    tvDeltaX.setText(df.format(x));
//                    tvDeltaY.setText(df.format(y));
//                    tvDeltaZ.setText(df.format(z-9.8));
                count++;
            }
            else
//                    Toast.makeText(getBaseContext(), "NOT A SIGNIFICANT SHAKE!", Toast.LENGTH_LONG).show();

            lastX = x;
            lastY = y;
            lastZ = z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    //MENU STUFF, SAVE FOR NEXT WEEK
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id =  item.getItemId();
//
//        if(id == R.id.call_your_mom_menuitem){
//            Toast.makeText(getBaseContext(), "Ring, ring, ring...", Toast.LENGTH_LONG).show();
//            return true;
//        } else if (id == R.id.happy_menuitem){
//            Toast.makeText(getBaseContext(), "I am a Happy Camper!", Toast.LENGTH_LONG).show();
//            return true;
//        } else if (id == R.id.Lets_Goto_Aruba) {
//            Toast.makeText(getBaseContext(), "It's always Sunny in Aruba.", Toast.LENGTH_LONG).show();
//            return true;
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }

    ////Menu Inflation and Binding
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//
//        if (id == R.id.call_your_mom_menuitem) {
//            Toast.makeText(getBaseContext(), "Ring ring, Hi Mom.", Toast.LENGTH_LONG).show();
//            return true;
//        } else if (id == R.id.happy_menuitem){
//            Toast.makeText(getBaseContext(), "You clicked the happy camper icon", Toast.LENGTH_LONG).show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    private void run () {
        millis = System.currentTimeMillis() - 0;
        seconds = (int) (millis / 1000);
        minutes = seconds / 60;
        hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void LightOn()
    {
        try {
            CamManager.setTorchMode(CamID, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

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