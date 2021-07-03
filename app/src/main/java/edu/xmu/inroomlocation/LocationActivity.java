package edu.xmu.inroomlocation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.xmu.inroomlocation.map.Keyan1_4Floor;
import edu.xmu.inroomlocation.step.StepSensorAcceleration;
import edu.xmu.inroomlocation.utils.NetworkUtil;
import edu.xmu.inroomlocation.utils.OrientSensor;
import edu.xmu.inroomlocation.utils.StorageUtils;
import edu.xmu.inroomlocation.utils.TimeUtils;
import edu.xmu.inroomlocation.view.StepView;

public class LocationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_GLLERY = 93;
    private static final int PICK_IMAGE_CAPTURE = 94;

    private OrientSensor mOrientSensor;
    private StepSensorAcceleration mStepSensor;
    private int mStepLen = 50; // 步长
    private Handler mHandler;

    private TextView mTvHint;
    private StepView mStepView;
    private int mCurrentTouchState = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initMainHandler();
        initLayout();

        initAutoFreshOrientation();
        initMap();
    }


    void navFromTo(int i, int j) {
        if (i == j) {
            return;
        }

        mStepView.navTo(j);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_to_setting:
                startSettingActivity();
                return true;
            case R.id.help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initMainHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        mStepView.setCurLoc(mStepView.getWifiLocByIdx(msg.arg1));

                        break;

                    case 3:
                        // 点击事件 回调
                        if (mCurrentTouchState == 1) {
                            mCurrentTouchState = 0;
                            mStepView.navTo((float[]) msg.obj);
                            mTvHint.setText("按任意位置修改当前位置");
                        } else if (mCurrentTouchState == 0) {
                            float[] xy = (float[]) msg.obj;
                            mStepView.setCurLoc(xy[0], xy[1]);
                        }

                        break;

                    case 999:

                        mStepView.setCurLoc(100, 200);
                        break;

                    default: {
                        break;
                    }
                }

            }
        };
    }

    private void initLayout() {
//        FrameLayout frameLayout = findViewById(R.id.layout_location_view);
//        LocationView locationView = new LocationView(LocationActivity.this);
//        frameLayout.addView(locationView);

        mTvHint = findViewById(R.id.tv_hint);
        mStepView = findViewById(R.id.v_stepview);
        mStepView.setMainHandler(mHandler);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentTouchState == 0) {
                    mTvHint.setText("按任意位置确定目标");
                    mCurrentTouchState = 1;
                } else if (mCurrentTouchState == 1) {
                    mTvHint.setText("按任意位置修改当前位置");
                    mCurrentTouchState = 0;
                }

            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(v -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImage, PICK_IMAGE_GLLERY);
        });

        FloatingActionButton fab3 = findViewById(R.id.fab3);
        fab3.setOnClickListener(v -> {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePicture.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                // createImageFile
                File saveImageFile = null;
                try {
                    saveImageFile = StorageUtils.getSaveImageFile("IMG_" + TimeUtils.getCurrentTimeString() + ".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                String imageAbsolutePath = saveImageFile.getAbsolutePath();

                Uri uriForFile = FileProvider.getUriForFile(this, "edu.xmu.inroomlocation.fileprovider", saveImageFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);

                startActivityForResult(takePicture, PICK_IMAGE_CAPTURE);
            }


//                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


//                String ai_ip = PreferenceManager.getDefaultSharedPreferences(LocationActivity.this).getString("ai_ip", "120.46.155.57");
//                NetworkUtil.sendAHelloMessageToServer(mHandler, ai_ip);
//
//                Random random = new Random();
//                int i = random.nextInt(9);
//                int j = random.nextInt(9);
//                navFromTo(i, j);
//                mStepView.invalidate();
        });


        mStepSensor = new StepSensorAcceleration(this, stepNum -> {
            mStepView.autoAddPoint();
        });
        if (!mStepSensor.registerStep()) {
            Toast.makeText(this, "计步功能不可用！", Toast.LENGTH_SHORT).show();
        }

//        mOrientSensor = new OrientSensor(this, orient -> {
//            mStepView.autoDrawArrow(orient);
//        });
//        if (!mOrientSensor.registerOrient()) {
//            Toast.makeText(this, "方向功能不可用！", Toast.LENGTH_SHORT).show();
//        }

    }

    private void initAutoFreshOrientation() {
        initOrientation();

    }

    void initMap() {
        this.mStepView.loadMap(new Keyan1_4Floor());

        mHandler.postDelayed(this::refreshCurrentNearestLoc, 1000);
    }

    void refreshCurrentNearestLoc() {
        mStepView.refreshCurrentNearestLoc();

        mHandler.postDelayed(this::refreshCurrentNearestLoc, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        String file_name;

        if (requestCode == PICK_IMAGE_CAPTURE && resultCode == RESULT_OK) {


//            Uri imageUri = data.getData();
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageUri);
//            Bitmap bitmap = null;
//            try {
//                bitmap = ImageDecoder.decodeBitmap(source);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }

            Uri uriForFile = FileProvider.getUriForFile(this, "edu.xmu.inroomlocation.fileprovider", StorageUtils.lastImageFileName);
            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uriForFile);
            Bitmap bitmap = null;
            try {
                bitmap = ImageDecoder.decodeBitmap(source);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            file_name = "IMG_small_" + TimeUtils.getCurrentTimeString() + ".jpg";
            StorageUtils.saveImg(bytes, file_name);

        } else if (requestCode == PICK_IMAGE_GLLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
//            MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageUri);
            Bitmap bitmap;
            try {
                bitmap = ImageDecoder.decodeBitmap(source);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

            file_name = "IMG_small_" + TimeUtils.getCurrentTimeString() + ".jpg";
            StorageUtils.saveImg(bytes, file_name);


            // send to server
//            StorageUtils.readPicToBytes(file_name);
        } else {
            return;
        }

        String ai_ip = PreferenceManager.getDefaultSharedPreferences(LocationActivity.this).getString("ai_ip", "120.46.155.57");

        HashMap<String, Integer> map = new HashMap<>();
        getWifiList().forEach(scanResult -> map.putIfAbsent(scanResult.SSID, scanResult.level));

        NetworkUtil.sendPicAndWifiStrengthToServer(mHandler, ai_ip, file_name, map);
    }


    private void initOrientation() {
        SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor managerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListener = new SensorEventListener() {
            private float mDegree = 0f;
            float[] acc = new float[3];
            float[] mag = new float[3];
            private float[] RR = new float[9];
            private float[] values = new float[3];
            private float getDegreeByAccMag() {
                SensorManager.getRotationMatrix(RR, null, acc, mag);
                SensorManager.getOrientation(RR, values);
                float degree = (float) Math.toDegrees(values[0]);

                return degree;
            }

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

                    // added
//                    Log.d("LLL", "onSensorChanged: " + (int) mDegree);
                    mStepView.autoDrawArrow((int) mDegree);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(sensorEventListener, managerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    // --------------- get wifi  -----------------
    private List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();

        return scanResults;
    }
    // --------------- get wifi -----------------


    private void startSettingActivity() {

        startActivity(new Intent(LocationActivity.this, SettingsActivity.class));

    }
}
