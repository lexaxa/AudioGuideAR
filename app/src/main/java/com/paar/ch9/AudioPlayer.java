package com.paar.ch9;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.paar.ch9.services.MediaService;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.paar.ch9.services.AudioPlayer.getInstance;

public class AudioPlayer {

    public static final String TAG = AudioPlayer.class.getSimpleName();
    private MediaService mediaService;

    private Intent mediaIntent;
    private boolean isBound;
    boolean isPlaying;

    private MediaService.MediaBinder mediaBinder;
    private Handler audioProgressUpdateHandler;
    private NotificationCompat.Builder mBuilder;
    Context context;

    AudioPlayer(Context context){
        this.context = context;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    private ServiceConnection mediaConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mediaBinder = (MediaService.MediaBinder) iBinder;
            setMediaService(mediaBinder.getService());
            Log.d(TAG, "onServiceConnected: mMediaBound");
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            isBound = false;
        }
    };

    public void play(String fileName){
        // Set application context.
        if(mBuilder == null){
            //buildNotify();
        }
        Log.d(TAG, "play: " + isPlaying + "=" + fileName + "=" + (mediaService!=null?mediaService.getTrack():"N/A"));
        if(isPlaying && mediaService != null && fileName.equalsIgnoreCase(mediaService.getTrack())) {
            mediaService.handleActionPause();
        }else {
            buildNotify();
            mediaService.setContext(context);
            createAudioProgressbarUpdater();
            mediaService.setAudioProgressUpdateHandler(audioProgressUpdateHandler);
//            mediaService.setTrack(fileName);
            mediaService.handleActionPlay(fileName);
        }

        isPlaying = !isPlaying;
    }
    public void pause(){
        isPlaying = false;
        getMediaService().handleActionPause();
    }
    public void seek(int time){
        getMediaService().handleActionSeek(time);
    }
    public void stop() {
        unBindAudioService();
    }

    // Create audio player progressbar updater.
    // This updater is used to update progressbar to reflect audio play process.
    @SuppressLint("HandlerLeak")
    private void createAudioProgressbarUpdater(){
        /* Initialize audio progress handler. */
        if(audioProgressUpdateHandler == null) {
            Log.d(TAG, "createAudioProgressbarUpdater: ");
            audioProgressUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // The update process message is sent from AudioServiceBinder class's thread object.
                    if (msg.what == mediaService.UPDATE_AUDIO_PROGRESS_BAR) {

                        if( mediaBinder != null) {
                            // Calculate the percentage.
                            int currProgress = mediaService.getAudioProgress();

                            // Update progressbar. Make the value 10 times to show more clear UI change.
                            mediaService.setProgress(currProgress);
                            //Log.d(TAG, "handleMessage: progress=" + currProgress);
                            if(mBuilder != null) {
//                                Log.d(TAG, "handleMessage: mBuilder " + currProgress);
                                try {
                                    mBuilder.setProgress(100, currProgress, false);
                                    NotificationManagerCompat.from(context).notify(1, mBuilder.build());
                                }catch (Exception e){
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }
//                    }else if(msg.what == mediaBinder.getService().STOP_AUDIO_PROGRESS_BAR){

                    }
                }
            };
        }
    }

    private void buildNotify(){
        Intent intentMain = new Intent(context , MainActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0 , intentMain, 0);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if(mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(context, "123")
                    .setSmallIcon(R.drawable.ic_play_circle_filled)
                    .setContentTitle("Play audio")
                    .setContentText("О Петербурге...")
                    .setProgress(100, 0, false)
//                    .setStyle(new NotificationCompat.BigTextStyle()
//                            .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

                    .setContentIntent(pendingIntent);
//        mBuilder.build();
            notificationManagerCompat.notify(1, mBuilder.build());
        }
    }

    public void bindAudioService(){
        if(mediaBinder == null) {
            Log.d(TAG, "bindAudioService: ");
            mediaIntent = new Intent(context, MediaService.class);
            context.bindService(mediaIntent, mediaConnection, BIND_AUTO_CREATE);
        }
    }

    public void unBindAudioService(){
        isPlaying = false;
        if(mBuilder != null){
            Log.d(TAG, "unBindAudioService: cancel notify");
            NotificationManagerCompat.from(context).cancel(1);
        }
        try {
            if (isBound) {
                isBound = false;
                context.unbindService(mediaConnection);
            }
        }catch (Exception e){
            Log.e(TAG, "unBindAudioService: ", e.getCause());
        }
    }

}
