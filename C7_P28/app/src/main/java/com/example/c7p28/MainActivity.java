package com.example.c7p28;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.graphics.Point;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    private TextView message;
    private GestureDetector mDetector;
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = (TextView)findViewById(R.id.message);
        mDetector = new GestureDetector(this, this);
        mDetector.setOnDoubleTapListener(this);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float xCoord, float yCoord) {

        // use Point and then getSize to get the screen size dimensions in pixels
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int displayWidth = point.x;
        int displayHeight = point.y;

        // Create new random parameters if user flings too hard
        if (Math.abs(xCoord) > 10000 || Math.abs(yCoord) > 10000) {

            float dx = (float) random.nextInt(displayWidth) * 7/8;
            float dy = (float) random.nextInt(displayHeight) * 4/5;
//            float dx = R.nextFloat() * displaymetrics.widthPixels * 8/10;
//            float dy = R.nextFloat() * displaymetrics.heightPixels * 8/10;

            message.setX(dx);
            message.setY(dy);
        }

        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {

        this.mDetector.onTouchEvent(event);
        if(!(event.getAction() == MotionEvent.ACTION_UP)) {
            message.setX(event.getX());
            message.setY(event.getY());
        }
        return super.onTouchEvent(event);

    }
}