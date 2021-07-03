package edu.xmu.inroomlocation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import edu.xmu.inroomlocation.utils.StorageUtils;

public class CollectingDataActivity extends AppCompatActivity {
    public static final String TAG = "CollectingDataActivity";

    TextView mTvLog;
    Button mBtnScan, mBtnSave;
    EditText mEtFilename;

    float mDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collecting_data);

        this.init();
        this.initOrientation();
        this.initStepDetector();

    }


    private void init() {
        this.mTvLog = findViewById(R.id.tv_log);
        this.mBtnScan = findViewById(R.id.btn_scan);
        this.mBtnSave = findViewById(R.id.btn_save);
        this.mEtFilename = findViewById(R.id.et_filename);

        this.mBtnScan.setOnClickListener(v -> {
            updateCompassAndWifiList();
            updateShowLog();
        });

        this.mBtnSave.setOnClickListener(v -> {
            this.saveLog();
        });

    }


    private float mLastSavedCompassOrientation = 0;
    private List<ScanResult> mLastSavedWifiList = null;

    private void updateCompassAndWifiList() {
        mLastSavedCompassOrientation = this.getCompassOrientation();
        mLastSavedWifiList = getWifiList();
    }

    private void updateShowLog() {
        StringBuilder log = new StringBuilder();

        log.append(mLastSavedCompassOrientation).append('\n');

        for (int i = 0; i < mLastSavedWifiList.size() && i < 10; i++) {
            ScanResult scanResult = mLastSavedWifiList.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(scanResult.SSID).append(' ')
                    .append(scanResult.level);

            log.append(sb.toString()).append('\n');
        }

        this.mTvLog.setText(log);
    }

    // --------------- step -----------------
    private int mSteps = 0;
    private void initStepDetector() {
        SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                mSteps++;
                mTvLog.setText("step: " + mSteps);
                Log.d(TAG, "onSensorChanged: ");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d(TAG, "onSensorChanged: ");

            }
        };

        sensorManager.registerListener(sensorEventListener, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    // --------------- step -----------------


    // --------------- get orientation -----------------
    private float[] acc = new float[3];
    private float[] mag = new float[3];

    private void initOrientation() {
        SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor managerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                boolean hasChanged = false;
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                    acc = event.values.clone();
                    acc[0] = event.values[0];
                    acc[1] = event.values[1];
                    acc[2] = event.values[2];
                    hasChanged = true;
                }
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//                    mag = event.values.clone();
                    mag[0] = event.values[0];
                    mag[1] = event.values[1];
                    mag[2] = event.values[2];
                    hasChanged = true;
                }
                if (hasChanged) {
                    mDegree = getDegreeByAccMag();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(sensorEventListener, managerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private float[] RR = new float[9];
    private float[] values = new float[3];

    private float getDegreeByAccMag() {
        SensorManager.getRotationMatrix(RR, null, acc, mag);
        SensorManager.getOrientation(RR, values);
        float degree = (float) Math.toDegrees(values[0]);

        return degree;
    }

    private float getCompassOrientation() {
        return mDegree;
    }
    // --------------- get orientation -----------------


    // --------------- get wifi  -----------------
    private List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();

        return scanResults;
    }
    // --------------- get wifi -----------------


    // --------------- save file -----------------
    @SuppressLint("DefaultLocale")
    private void saveLog() {
        if (mLastSavedWifiList == null) {
            return;
        }

//        checkExternalMedia();
        StringBuilder log = new StringBuilder();

        log.append(mLastSavedCompassOrientation).append('\n');

        for (int i = 0; i < mLastSavedWifiList.size(); i++) {
            ScanResult scanResult = mLastSavedWifiList.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(scanResult.SSID).append(' ')
                    .append(scanResult.level);

            log.append(sb.toString()).append('\n');
        }

        Editable filename = this.mEtFilename.getText();

        Date date = new Date();
        StorageUtils.saveTxt(log.toString(), String.format("%s_%02d%02d%02d.txt", filename, date.getHours(), date.getMinutes(), date.getSeconds()));
    }

//    private void saveTxt(String s, String filename) {
//        File root = Environment.getExternalStorageDirectory();
//        File myDir = new File(root.getAbsolutePath() + "/inroomlocation");
//        if (!myDir.exists()) {
//            myDir.mkdirs();
//        }
//
//        File newFile = new File(myDir, filename);
//        try {
//            FileOutputStream fs = new FileOutputStream(newFile);
//            PrintWriter pw = new PrintWriter(fs);
//            pw.print(s);
//            pw.flush();
//            pw.close();
//            fs.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;

    private boolean checkExternalMedia() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        Log.d(TAG, "checkExternalMedia: " + "\n\nExternal Media: readable="
                + mExternalStorageAvailable + " writable=" + mExternalStorageWriteable);

        return mExternalStorageAvailable && mExternalStorageWriteable;
    }
    // --------------- save file -----------------

}