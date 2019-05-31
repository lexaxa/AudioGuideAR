package com.paar.ch9;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.paar.ch9.camera.CameraSurface;
import com.paar.ch9.services.MediaService;

public class AugmentedActivity extends SensorsActivity implements OnTouchListener {
    private static final String TAG = AugmentedActivity.class.getSimpleName();
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private static final int ZOOMBAR_BACKGROUND_COLOR = Color.argb(125,55,55,55);
    private static final int DESC_BACKGROUND_COLOR = Color.argb(125,55,55,55);
    private static final String END_TEXT = FORMAT.format(AugmentedActivity.MAX_ZOOM)+" km";
    private static final int END_TEXT_COLOR = Color.WHITE;

    protected static WakeLock wakeLock = null;
    protected static CameraSurface camScreen = null;
    protected static VerticalSeekBar myZoomBar = null;
    protected static TextView endLabel = null;
    protected static LinearLayout zoomLayout = null;
    protected static LinearLayout descLayout = null;
    protected static AugmentedView augmentedView = null;

    public static final float MAX_ZOOM = 10; //in KM
    public static final float ONE_PERCENT = MAX_ZOOM/100f;
    public static final float TEN_PERCENT = 10f*ONE_PERCENT;
    public static final float TWENTY_PERCENT = 2f*TEN_PERCENT;
    public static final float EIGHTY_PERCENT = 4f*TWENTY_PERCENT;

    public static boolean useCollisionDetection = true;
    public static boolean showRadar = true;
    public static boolean showZoomBar = true;
    public static boolean showDescLayout = false;
    protected static TextView descLabel;
    protected static ImageView descImage;
    protected static ConstraintLayout consLayout;
    private ImageView btnMediaPlay;
    private ImageView btnMediaRew;
    private ImageView btnMediaFF;
    private boolean isPlaying;
    private Marker activeMarker;
    private AudioPlayer audioPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        camScreen = new CameraSurface(this);
        setContentView(camScreen);

        augmentedView = new AugmentedView(this);
        augmentedView.setOnTouchListener(this);
        LayoutParams augLayout = new LayoutParams(  LayoutParams.WRAP_CONTENT, 
                                                    LayoutParams.WRAP_CONTENT);
        addContentView(augmentedView,augLayout);

        zoomLayout = new LinearLayout(this);
        zoomLayout.setVisibility((showZoomBar)?LinearLayout.VISIBLE:LinearLayout.GONE);
        zoomLayout.setOrientation(LinearLayout.VERTICAL);
        zoomLayout.setPadding(5, 5, 5, 5);
        zoomLayout.setBackgroundColor(ZOOMBAR_BACKGROUND_COLOR);

        endLabel = new TextView(this);
        endLabel.setText(END_TEXT);
        endLabel.setTextColor(END_TEXT_COLOR);
        LinearLayout.LayoutParams zoomTextParams =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        zoomLayout.addView(endLabel, zoomTextParams);

        myZoomBar = new VerticalSeekBar(this);
        myZoomBar.setMax(100);
        myZoomBar.setProgress(70);
        myZoomBar.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);
        LinearLayout.LayoutParams zoomBarParams =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT);
        zoomBarParams.gravity = Gravity.CENTER_HORIZONTAL;
        zoomLayout.addView(myZoomBar, zoomBarParams);

        setupDescLayout();
//        descLayout = new LinearLayout(this);
//        descLayout.setVisibility((showDescLayout)?LinearLayout.VISIBLE:LinearLayout.GONE);
//        descLayout.setOrientation(LinearLayout.HORIZONTAL);
//        descLayout.setPadding(5, 5, 5, 5);
//        descLayout.setBackgroundColor(DESC_BACKGROUND_COLOR);
//
//        descLabel = new TextView(this);
//        descLabel.setText("");
//        descLabel.setTextColor(END_TEXT_COLOR);
//        LinearLayout.LayoutParams descTextParams =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//        descLayout.addView(descLabel, descTextParams);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(  LayoutParams.WRAP_CONTENT, 
                                                                                    LayoutParams.FILL_PARENT, 
                                                                                    Gravity.END);
        addContentView(zoomLayout,frameLayoutParams);
//        addContentView(descLayout, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

        updateDataOnZoom();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ProAndroidAR9:DimScreen");

        audioPlayer = new AudioPlayer(this);
        audioPlayer.bindAudioService();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
    }

    public void setupDescLayout(){

//        consLayout = new ConstraintLayout(this);

//        addContentView(descLayout, new ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

//    <LinearLayout
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:orientation="horizontal">

        descLayout = new LinearLayout(this);
        descLayout.setVisibility((showDescLayout)?LinearLayout.VISIBLE:LinearLayout.GONE);
        descLayout.setOrientation(LinearLayout.HORIZONTAL);
        descLayout.setPadding(5, 5, myZoomBar.getWidth(), 5);
        Log.d(TAG, "setupDescLayout: " + myZoomBar.getWidth());
        descLayout.setBackgroundColor(DESC_BACKGROUND_COLOR);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(  LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);

//        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

        addContentView(descLayout,frameLayoutParams);

//        <ImageView
//            android:id="@+id/imageView"
//            android:layout_width="96dp"
//            android:layout_height="96dp"
//            app:srcCompat="@android:drawable/ic_menu_mapmode" />

        descImage = new ImageView(this);
        descImage.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_mapmode));
        descImage.setMinimumWidth(96);
        descLayout.addView(descImage, new LinearLayout.LayoutParams(96,96, 1));

//        <TextView
//            android:id="@+id/textView"
//            android:layout_width="wrap_content"
//            android:layout_height="wrap_content"
//            android:layout_weight="3"
//            android:text="Description" />

        descLabel = new TextView(this);
        //descLabel.setText(activeMarker.getName());
        descLabel.setTextColor(END_TEXT_COLOR);
        descLayout.addView(descLabel, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 3));

//        <LinearLayout
//            android:layout_width="96dp"
//            android:layout_height="wrap_content"
//            android:orientation="vertical">

        LinearLayout audioLayout = new LinearLayout(this);
        audioLayout.setOrientation(LinearLayout.VERTICAL);
//        audioLayout.setPadding(5, 5, 5, 5);

        descLayout.addView(audioLayout, new LinearLayout.LayoutParams( 96, LayoutParams.WRAP_CONTENT, 0));

//
//            <ImageButton
//                android:id="@+id/imageButton"
//                android:layout_width="match_parent"
//                android:layout_height="wrap_content"
//                app:srcCompat="@android:drawable/ic_media_play" />

        btnMediaPlay = new ImageView(this);
        btnMediaPlay.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        btnMediaPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClickPlay: ");
                Intent intent = new Intent(getApplicationContext(), MediaService.class);
                intent.putExtra(MediaService.EXTRA_FILENAME, activeMarker.getAudioFileName());
                if(isPlaying){
                    ((ImageView) v).setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                    audioPlayer.pause();
                }else {
                    ((ImageView) v).setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    audioPlayer.play(activeMarker.getAudioFileName());
                }
                isPlaying = !isPlaying;
            }
        });
        audioLayout.addView(btnMediaPlay, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

//            <LinearLayout
//                android:layout_width="wrap_content"
//                android:layout_height="match_parent"
//                android:orientation="horizontal">

        LinearLayout audioBtnLayout = new LinearLayout(this);
        audioBtnLayout.setOrientation(LinearLayout.HORIZONTAL);
//        audioBtnLayout.setPadding(5, 5, 5, 5);
        audioLayout.addView(audioBtnLayout, new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
//                <ImageButton
//                    android:id="@+id/imageButton3"
//                    android:layout_width="wrap_content"
//                    android:layout_height="wrap_content"
//                    android:layout_weight="1"
//                    app:srcCompat="@android:drawable/ic_media_rew" />

        btnMediaRew = new ImageView(this);
        btnMediaRew.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_rew));
        btnMediaRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClickRev: ");
                if(isPlaying){
                    audioPlayer.seek(-5000);
                }
            }
        });
        audioBtnLayout.addView(btnMediaRew, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
//
//                <ImageButton
//                    android:id="@+id/imageButton2"
//                    android:layout_width="wrap_content"
//                    android:layout_height="wrap_content"
//                    android:layout_weight="1"
//                    app:srcCompat="@android:drawable/ic_media_ff" />
        btnMediaFF = new ImageView(this);
        btnMediaFF.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_ff));
        btnMediaFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClickFF: ");
                if(isPlaying){
                    audioPlayer.seek(5000);
                }
            }
        });
        audioBtnLayout.addView(btnMediaFF, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));

//            </LinearLayout>
//        </LinearLayout>
//    </LinearLayout>

    }
	@Override
	public void onResume() {
		super.onResume();

		wakeLock.acquire();//2*60*1000L /*2 minutes*/);
	}

	@Override
	public void onPause() {
		super.onPause();

		wakeLock.release();
	}

    @Override
    protected void onDestroy() {
        audioPlayer.unBindAudioService();

        super.onDestroy();

    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (    evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || 
                evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            augmentedView.postInvalidate();
        }
    }
    
    private OnSeekBarChangeListener myZoomBarOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateDataOnZoom();
            camScreen.invalidate();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            //Not used
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            updateDataOnZoom();
            camScreen.invalidate();
        }
    };

    private static float calcZoomLevel(){
        int myZoomLevel = myZoomBar.getProgress();
        float out;

        float percent;
        if (myZoomLevel <= 25) {
            percent = myZoomLevel/25f;
            out = ONE_PERCENT*percent;
        } else if (myZoomLevel > 25 && myZoomLevel <= 50) {
            percent = (myZoomLevel-25f)/25f;
            out = ONE_PERCENT+(TEN_PERCENT*percent);
        } else if (myZoomLevel > 50 && myZoomLevel <= 75) {
            percent = (myZoomLevel-50f)/25f;
            out = TEN_PERCENT+(TWENTY_PERCENT*percent);
        } else {
            percent = (myZoomLevel-75f)/25f;
            out = TWENTY_PERCENT+(EIGHTY_PERCENT*percent);
        }
        return out;
    }

    protected void updateDataOnZoom() {
        float zoomLevel = calcZoomLevel();
        ARData.setRadius(zoomLevel);
        ARData.setZoomLevel(FORMAT.format(zoomLevel));
        ARData.setZoomProgress(myZoomBar.getProgress());
    }

	public boolean onTouch(View view, MotionEvent me) {
        view.performClick();
	    for (Marker marker : ARData.getMarkers()) {
	        if (marker.handleClick(me.getX(), me.getY())) {
	            if (me.getAction() == MotionEvent.ACTION_UP){
	                activeMarker = marker;
	                markerTouched(marker);
                }
	            return true;
	        }
	    }
		return super.onTouchEvent(me);
	}
	
	protected void markerTouched(Marker marker) {
		Log.w(TAG,"markerTouched() not implemented.");
	}
}