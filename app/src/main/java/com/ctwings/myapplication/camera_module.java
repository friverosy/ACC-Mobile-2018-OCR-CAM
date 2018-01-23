package com.ctwings.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Juan Morales on 15/01/2018.
 */

public class camera_module extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

    private static final String FLASH_STATE = "FLASH_STATE";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private Vibrator mVibrator;
    MediaPlayer mp3Dennied;
    MediaPlayer mp3Permitted;
    MediaPlayer mp3Error;
    private TransitionDrawable trans;
    private ImageView image;
    private boolean is_input;
    private ArrayList<Integer> mSelectedIndices;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);
        //setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp3Dennied = MediaPlayer.create(camera_module.this, R.raw.bad);
        mp3Permitted = MediaPlayer.create(camera_module.this, R.raw.good);
        mp3Error = MediaPlayer.create(camera_module.this, R.raw.error);
        image= (ImageView) findViewById(R.id.image_validation);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        // You can optionally set aspect ratio tolerance level
        // that is used in calculating the optimal Camera preview size
        mScannerView.setAspectTolerance(0.2f);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }

    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(camera_module.this);
            }
        }, 2000);
        /*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(camera_module.this);
            }
        }, 2000);
         */
    }

    public void toggleFlash(View v) {
        mFlash = !mFlash;
        mScannerView.setFlash(mFlash);
    }


private class loadSound extends AsyncTask<Void, Void, Void> {
    private int typeSound = -1;

    private loadSound(int typeSound) {
        this.typeSound = typeSound;
    }

    @Override
    protected Void doInBackground(Void... params) {
        switch (typeSound) {
            case 1:
                if (mp3Error.isPlaying()) mp3Error.pause();
                mp3Error.seekTo(0);
                mp3Error.start();
                break;
            case 2:
                if (mp3Permitted.isPlaying()) mp3Permitted.pause();
                mp3Permitted.seekTo(0);
                mp3Permitted.start();
                break;
            case 3:
                if (mp3Dennied.isPlaying()) mp3Dennied.pause();
                mp3Dennied.seekTo(0);
                mp3Dennied.start();
                break;
            case 4:
                if (mp3Error.isPlaying()) mp3Error.pause();
                mp3Error.seekTo(0);
                if (mp3Dennied.isPlaying()) mp3Dennied.pause();
                mp3Dennied.seekTo(0);
                if (mp3Permitted.isPlaying()) mp3Permitted.pause();
                mp3Permitted.seekTo(0);
                break;
        }
        return null;
    }

}
    public void getPerson(String rut) {
        log_app log = new log_app();
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Cursor person = db.get_one_person(rut);
        Resources res = this.getResources();
        LinearLayout layout = (LinearLayout) findViewById(R.id.camera_ticks);

        try {
            //build object with that values, then send to registerTarsk()
            Record record = new Record();

            // If has not a person.
            if (person.getCount() < 1) {
                new camera_module.loadSound(3).execute();
                record.setRecord_person_rut(rut);
                trans = (TransitionDrawable) res.getDrawable(R.drawable.transition_color_denied);
                layout.setBackgroundDrawable(trans);
                trans.reverseTransition(150);
                image.setImageResource(R.drawable.xbutton);
            } else {
                if (person.getString(person.getColumnIndex("person_active")).equals("true")) {
                    new camera_module.loadSound(2).execute();
                    record.setPerson_mongo_id(person.getString(person.getColumnIndex("person_mongo_id")));
                    trans = (TransitionDrawable) res.getDrawable(R.drawable.transition_color_true);
                    layout.setBackgroundDrawable(trans);
                    trans.reverseTransition(150);
                    image.setImageResource(R.drawable.checked);
                } else {
                    new camera_module.loadSound(3).execute();
                    trans = (TransitionDrawable) res.getDrawable(R.drawable.transition_color_denied);
                    record.setRecord_person_rut(rut);
                    layout.setBackgroundDrawable(trans);
                    trans.reverseTransition(150);
                    image.setImageResource(R.drawable.xbutton);
                }
            }

            record.setRecord_sync(0);
            record.setRecord_date(new Date().getTime());
            if (is_input) record.setRecord_type("entry");
            else record.setRecord_type("depart");

            if (person != null)
                person.close();

            // Save record on local database
            db.add_record(record);
        } catch (Exception e) {
            e.printStackTrace();
            new camera_module.loadSound(1).execute(); // Error sound.
        }
    }

}
