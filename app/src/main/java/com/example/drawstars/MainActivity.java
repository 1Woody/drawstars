package com.example.drawstars;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FrameLayout fralay;
    ImageView imageview;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    Bitmap[] bitmapobjects;
    Button bt;
    Handler handler;
    Runnable runnable;

    final int NUMSTARS=30;
    Star[] vstar;

    long curtime;
    double speed;
    final int WAITTIME=10;
    int width, height;

    private class Star {
        int size;
        double x,y;
        int randColor;
        private void setSize(){
            size = width/((int)(10+Math.random()*30));
        }
        private void setRandom(){
            Random r = new Random();
            randColor = r.nextInt(4);
        }

        private void setX(){
            x = (int) (Math.random() * (width - size));
        }
        private void setY(){
            y = (int) (Math.random() * (height - size));
        }

        public void setRandomValues(int i) {
            setSize();
            setRandom();
            setX();
            setY();
        }

        public void draw() {
            canvas.drawBitmap(bitmapobjects[randColor],
                    new Rect(0,0,bitmapobjects[randColor].getWidth(), bitmapobjects[randColor].getHeight()),
                    new Rect((int)x,(int)y, (int)x+size, (int)y+size),paint);
        }

        public void move(double delta){
            y+=(speed*size/(((double)width)/10))*delta;
            if (y>height) {
                setSize();
                setRandom();
                y = -size;
                setX();
            }

        }
    }

    void setRandomValues() {
        for( int i=0; i<NUMSTARS; i++){
            vstar[i].setRandomValues(i);
        }
    }

    public static void insertSort(Star[] vstar) {
        for (int i = 1; i < vstar.length; i++) {
            Star current = vstar[i];
            int j = i - 1;
            while(j >= 0 && current.size < vstar[j].size) {
                vstar[j+1] = vstar[j];
                j--;
            }
            vstar[j+1] = current;
        }
    }

    void draw() {
        //change the order of index to order of size
        //add a sorting function
        canvas.drawColor(Color.BLACK);
        insertSort(vstar);
        for( int i=0; i<NUMSTARS; i++){
            vstar[i].draw();
        }
    }

    void move(double delta){
        for( int i=0; i<NUMSTARS; i++){
            vstar[i].move(delta);
        }
    }

    public double distance2p( int x1, int y1, int x2, int y2) {
        double dist = Math.sqrt(Math.pow((x2 - x1),2) + Math.pow((y2 - y1),2));
        System.out.println("x1: " + x1 + "\n" +
                           "y1: " + y1 + "\n"+
                           "x2: " + x2 + "\n" +
                           "y2: " + y2 + "\n" +
                           "dist: " + dist + "\n" +
                           "maxdist: " + width/6);
        return dist;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove
        // Or for "sticky innersive, " replace it with
        View decorView = getWindow().getDecorView();
        decorView. setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize *'hen the system bars hide and show.
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    // Hide the nav bar and status bar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        speed = height/10;
        fralay = new FrameLayout(this);
        fralay.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        imageview = new ImageView(this);
        imageview.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        fralay.addView(imageview);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        imageview.setImageBitmap(bitmap);
        canvas =  new Canvas(bitmap);
        paint = new Paint();

        bitmapobjects = new Bitmap[4];
        bitmapobjects[0] = BitmapFactory.decodeResource(getResources(),
                R.drawable.sun);
        bitmapobjects[1] = BitmapFactory.decodeResource(getResources(),
                R.drawable.moon);
        bitmapobjects[2] = BitmapFactory.decodeResource(getResources(),
                R.drawable.earth);
        bitmapobjects[3] = BitmapFactory.decodeResource(getResources(),
                R.drawable.astronaut);

        vstar = new Star[NUMSTARS];
        for( int i=0; i<NUMSTARS; i++){
            vstar[i] = new Star();
        }
        setRandomValues();
        draw();
        bt = new Button(this);
        bt.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT ));
        bt.setText("Reset");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomValues();
                draw();
            }
        });
        fralay.addView(bt);
        setContentView(fralay);

        curtime = System.currentTimeMillis();
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                long newtime = System.currentTimeMillis();
                double delta = ((double)(newtime-curtime))/1000;
                curtime=newtime;
                move(delta);
                draw();
                imageview.invalidate();
                handler.postDelayed(runnable, WAITTIME);
            }
        };
        handler.postDelayed(runnable, WAITTIME);
    }
}

/* old Random  values avoid overlapping

    public void setRandomValues(int i) {
        //callrandom color
        setSize();
        setX();
        setY();
            /*
            boolean correct;
            setSize();
            do {
                setX();
                setY();
                correct = true;
                for (int j = i-1; j >= 0; j--) {
                    if (distance2p((int)x, (int)y, (int)vstar[j].x, (int)vstar[j].y) < size+vstar[j].size) correct = false;
                }
            } while (!correct);

    }
*/
