package edu.xmu.inroomlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.LocationLayer;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.layer.RouteLayer;
import com.onlylemi.mapview.library.utils.MapUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import edu.xmu.inroomlocation.map.AbstractInRoomMap;
import edu.xmu.inroomlocation.map.AbstractRoomMap_2;
import edu.xmu.inroomlocation.map.Keyan1_4Floor;
import edu.xmu.inroomlocation.map.Keyan1_4Floor_2;
import edu.xmu.inroomlocation.map.TestData;
import edu.xmu.inroomlocation.step.StepSensorAcceleration;
import edu.xmu.inroomlocation.utils.NetworkUtil;
import edu.xmu.inroomlocation.utils.StorageUtils;
import edu.xmu.inroomlocation.utils.TimeUtils;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapActivity";

    private int mCurrentTouchState = CURRENT_TOUCH_STATE_NONE;
    public static final int CURRENT_TOUCH_STATE_NONE = 0;
    public static final int CURRENT_TOUCH_STATE_SELECT_CURRENT = 1;
    public static final int CURRENT_TOUCH_STATE_SELECT_TARGET = 2;

    public static final int PICK_IMAGE_GLLERY = 93;
    public static final int PICK_IMAGE_CAPTURE = 94;

    public static final int TRANSFER_FAILED = 999;
    public static final int LOCATION_SET = 11;


    private MapView mapView;
    private TextView mTvHint;
    private ProgressDialog mDialog;
    private Handler mHandler;

    private MarkLayer markLayer;
    private RouteLayer routeLayer;
    private LocationLayer locationLayer;

    private List<PointF> nodes;
    private List<PointF> nodesContract;
    private List<PointF> marks;
    private List<String> marksName;

    private PointF mTarget;

    private AbstractRoomMap_2 keyan1;

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_layer_test);

        keyan1 = new Keyan1_4Floor_2();

//        nodes = TestData.getNodesList();
//        nodesContract = TestData.getNodesContactList();
//        marks = TestData.getMarks();
//        marksName = TestData.getMarksName();
        nodes = new ArrayList<>(keyan1.getNavReferenceNodes());
        nodesContract = new ArrayList<>(keyan1.getNavReferenceNodesConnections());
        marks = keyan1.getAnchorPoints();
        marksName = keyan1.getAnchorNames();
        MapUtils.init(nodes.size(), nodesContract.size());


        mapView = (MapView) findViewById(R.id.mapview);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open(keyan1.getMapFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapView.loadMap(bitmap);
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {

                routeLayer = new RouteLayer(mapView);
                mapView.addLayer(routeLayer);

                markLayer = new MarkLayer(mapView, marks, marksName);
                mapView.addLayer(markLayer);
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num) {
                        // hack into markLayer not show the icon
                        markLayer.setNum(-100);

                        mTarget = new PointF(marks.get(num).x, marks.get(num).y);
                        navTo(mTarget);
//                        Toast.makeText(MapActivity.this, String.format("%s is clicked!", marksName.get(num)), Toast.LENGTH_SHORT).show();
                    }
                });
                mapView.refresh();

                locationLayer = new LocationLayer(mapView, new PointF(400, 400), true);
                mapView.addLayer(locationLayer);

                // init other thing

                initOrientation();
                initMyTouchEvent();

            }

            @Override
            public void onMapLoadFail() {
            }

        });

        initLayoutEvent();
        initMainHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mapView.isMapLoadFinish()) {
//            switch (item.getItemId()) {
////                case R.id.route_layer_tsp:
////                    List<PointF> list = new ArrayList<>();
////                    list.add(marks.get(39));
////                    list.add(marks.get(new Random().nextInt(10)));
////                    list.add(marks.get(new Random().nextInt(10) + 10));
////                    list.add(marks.get(new Random().nextInt(10) + 20));
////                    list.add(marks.get(new Random().nextInt(10) + 9));
////                    List<Integer> routeList = MapUtils.getBestPathBetweenPoints(list, nodes,
////                            nodesContract);
////                    routeLayer.setNodeList(nodes);
////                    routeLayer.setRouteList(routeList);
////                    mapView.refresh();
////                    break;
//                default:
//                    break;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

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


    public void navTo() {
        navTo(mTarget);
    }

    public void navTo(PointF target) {
        if (target == null) {
            routeLayer.setNodeList(null);
            routeLayer.setRouteList(null);
        } else {
            PointF currentPosition = locationLayer.getCurrentPosition();
            List<Integer> routeList = MapUtils.getShortestDistanceBetweenTwoPoints(currentPosition, target, nodes, nodesContract);
            routeLayer.setNodeList(nodes);
            routeLayer.setRouteList(routeList);
        }
        mapView.refresh();
    }

    public void navTo(PointF from, PointF target) {
        if (target == null) {
            routeLayer.setNodeList(null);
            routeLayer.setRouteList(null);
        } else {
            List<Integer> routeList = MapUtils.getShortestDistanceBetweenTwoPoints(from, target, nodes, nodesContract);
            routeLayer.setNodeList(nodes);
            routeLayer.setRouteList(routeList);
        }
        mapView.refresh();
    }

    public void checkIsArrived() {
        if (mTarget == null) {
            return;
        }

        PointF currentPosition = locationLayer.getCurrentPosition();
        Log.i(TAG, String.format("checkIsArrived: %.2f", (mTarget.x - currentPosition.x) * (mTarget.x - currentPosition.x) + (mTarget.y - currentPosition.y) * (mTarget.y - currentPosition.y)));

        if ((mTarget.x - currentPosition.x) * (mTarget.x - currentPosition.x) + (mTarget.y - currentPosition.y) * (mTarget.y - currentPosition.y) < 1000) {
            mHandler.post(() -> {
                new AlertDialog.Builder(MapActivity.this).setTitle("到了").setPositiveButton("确定", null).create().show();
                mTarget = null;
            });

//            mapView.refresh();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        String file_name;

        if (requestCode == PICK_IMAGE_CAPTURE && resultCode == RESULT_OK) {

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
            Log.d(TAG, "onActivityResult: ImageDecoder createSource!!!");

            Bitmap bitmap;
            try {
                bitmap = ImageDecoder.decodeBitmap(source);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

            Log.d(TAG, "onActivityResult: Bitmap compress!!!");

            file_name = "IMG_small_" + TimeUtils.getCurrentTimeString() + ".jpg";
            StorageUtils.saveImg(bytes, file_name);

            Log.d(TAG, "onActivityResult: Image saved!!!");

//             send to server
//            StorageUtils.readPicToBytes(file_name);


        } else {
            return;
        }

        String ai_ip = PreferenceManager.getDefaultSharedPreferences(MapActivity.this).getString("ai_ip", "120.46.155.57");

        HashMap<String, Integer> map = new HashMap<>();
        NetworkUtil.getWifiList(getApplicationContext()).forEach(scanResult -> map.putIfAbsent(scanResult.SSID, scanResult.level));

        NetworkUtil.sendPicAndWifiStrengthToServer(mHandler, ai_ip, file_name, map);


        mDialog = new ProgressDialog(this);
        mDialog.setMessage("图片上传中...");
        mDialog.show();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseOrientationSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initOrientation();
    }

    // -------------- init main handler-----------------------------
    private void initMainHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case LOCATION_SET:
                        if (mDialog != null) {
                            mDialog.hide();
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                        builder.setMessage(String.format("你的位置在:%s", keyan1.getAnchorNames().get(msg.arg1)));
                        builder.setPositiveButton("确定", (dialog, which) -> {
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        break;

                    case 3:

                        break;

                    case TRANSFER_FAILED:
                        Toast.makeText(MapActivity.this, "上传图片失败，选择随机位置", Toast.LENGTH_SHORT).show();

                        if (mDialog != null) {
                            mDialog.hide();
                        }

//                        locationLayer.setCurrentPosition(new PointF(random.nextFloat() * mapView.getMapWidth(), random.nextFloat() * mapView.getMapHeight()));
                        locationLayer.setCurrentPosition(keyan1.getNavReferenceNodes().get(random.nextInt(keyan1.getNavReferenceNodes().size())));
                        mapView.mapCenterWithPoint(locationLayer.getCurrentPosition().x, locationLayer.getCurrentPosition().y);
                        mapView.refresh();

                        break;

                    default: {
                        break;
                    }
                }
            }
        };
    }

    // -----------------------------------------------------------------------

    // ---------------init layout---------------------------------------------

    private void initLayoutEvent() {
        mTvHint = findViewById(R.id.tv_hint);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentTouchState == CURRENT_TOUCH_STATE_NONE) {
                    List<String> goWhereItems = new ArrayList<>(keyan1.getAnchorNames());
                    goWhereItems.add(0, "点击屏幕选择");
                    AlertDialog goWhereDialog = new AlertDialog.Builder(MapActivity.this)
                            .setTitle("去哪？")
                            .setItems(goWhereItems.toArray(new String[goWhereItems.size()]), (DialogInterface dialog, int which) -> {
                                        if (which == 0) {
                                            mTvHint.setText("按任意位置确定目标");
                                            mCurrentTouchState = CURRENT_TOUCH_STATE_SELECT_TARGET;
                                        } else {
//                                            mStepView.navToWifiLoc(which - 1);
                                            // TODO here
                                            mTarget = marks.get(which - 1);
                                            navTo(mTarget);
                                        }
                                    }
                            ).create();

                    goWhereDialog.show();

                } else if (mCurrentTouchState != CURRENT_TOUCH_STATE_NONE) {
                    mTvHint.setText("------");
                    mCurrentTouchState = CURRENT_TOUCH_STATE_NONE;
                }

//                else if (mCurrentTouchState == CURRENT_TOUCH_STATE_SELECT_CURRENT) {
//                    mTvHint.setText("按任意位置修改当前位置");
//                    mCurrentTouchState = CURRENT_TOUCH_STATE_NONE;
//                } else if (mCurrentTouchState == CURRENT_TOUCH_STATE_SELECT_TARGET) {
//                    mTvHint.setText("------");
//                    mCurrentTouchState = CURRENT_TOUCH_STATE_NONE;
//                }

//                mapView.mapCenterWithPoint(random.nextFloat() * 400, random.nextFloat() * 400);
//
////                locationLayer.setCurrentPosition(new PointF(random.nextFloat() * 400, random.nextFloat() * 400));
//                mapView.refresh();
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(v -> {

            List<String> selectLocItems = List.of(
                    "点击屏幕选择",
                    "拍照识别",
                    "上传图片识别"
            );
            AlertDialog currentWhere = new AlertDialog.Builder(MapActivity.this)
                    .setTitle("设置当前位置")
                    .setItems(selectLocItems.toArray(new String[0]), (DialogInterface dialog, int which) -> {
                                switch (which) {
                                    case 0: {
                                        mCurrentTouchState = CURRENT_TOUCH_STATE_SELECT_CURRENT;
                                        break;
                                    }
                                    case 1: {
                                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        if (takePicture.resolveActivity(getPackageManager()) != null) {
                                            // createImageFile
                                            File saveImageFile = null;
                                            try {
                                                saveImageFile = StorageUtils.getSaveImageFile("IMG_" + TimeUtils.getCurrentTimeString() + ".jpg");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            Uri uriForFile = FileProvider.getUriForFile(this, "edu.xmu.inroomlocation.fileprovider", saveImageFile);
                                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);

                                            startActivityForResult(takePicture, PICK_IMAGE_CAPTURE);
                                        }
                                        break;
                                    }
                                    case 2: {
                                        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(pickImage, PICK_IMAGE_GLLERY);
                                        break;
                                    }

                                }

                            }
                    ).create();
            currentWhere.show();
        });

    }

    // -----------------------------------------------------------------------

    // ---------------orientation---------------------------------------------

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
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

                // hack to give step sensor an event
                mStepSensor.onSensorChanged(event);
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

                // add my own code here
                float mapRotationOffset = keyan1.getMapRotation();
                mapRotationOffset += mapView.getCurrentRotateDegrees();

                locationLayer.setCompassIndicatorArrowRotateDegree(mDegree + mapRotationOffset);
                locationLayer.setCompassIndicatorCircleRotateDegree(mDegree + mapRotationOffset);
                mapView.refresh();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private StepSensorAcceleration mStepSensor;
    private boolean mHasSensorRegistered = false;

    private void initOrientation() {
        if (mStepSensor == null) {
            initStepSensor();
        }

        if (mapView != null && !mHasSensorRegistered) {
            SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

            Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor managerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            sensorManager.registerListener(sensorEventListener, managerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_GAME);
//            mStepSensor.registerStep();
            mHasSensorRegistered = true;
        }
    }

    private void pauseOrientationSensor() {
        SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);

        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor managerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.unregisterListener(sensorEventListener, accSensor);
        sensorManager.unregisterListener(sensorEventListener, managerSensor);
//        mStepSensor.unregisterStep();
        mHasSensorRegistered = false;
    }

    private void initStepSensor() {
        mStepSensor = new StepSensorAcceleration(this, stepNum -> {
//            Log.i(TAG, String.format("initStepSensor: %d", stepNum));

            float compassIndicatorArrowRotateDegree = locationLayer.getCompassIndicatorArrowRotateDegree();
            float dx = (float) (keyan1.getStepLength() * Math.sin(Math.toRadians(compassIndicatorArrowRotateDegree)));
            float dy = (float) (keyan1.getStepLength() * Math.cos(Math.toRadians(compassIndicatorArrowRotateDegree)));
            PointF currentPosition = locationLayer.getCurrentPosition();
            PointF pointF = new PointF(currentPosition.x + dx, currentPosition.y - dy);
//            Log.i(TAG, String.format("initStepSensor: \n" +
//                            "compassIndicatorArrowRotateDegree:%.2f\n" +
//                            "dx:%.2f\ndy:%.2f\n",
//                    compassIndicatorArrowRotateDegree, dx, dy));

            locationLayer.setCurrentPosition(pointF);
            navTo();

            checkIsArrived();

            mapView.refresh();
        });
    }

    // -----------------------------------------------------------------------

    // ---------------init touch event--------------------------------------------

    @SuppressLint("ClickableViewAccessibility")
    private void initMyTouchEvent() {
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return mCurrentTouchState != CURRENT_TOUCH_STATE_NONE;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mCurrentTouchState == CURRENT_TOUCH_STATE_SELECT_CURRENT) {
                        float[] goal = mapView.convertMapXYToScreenXY(event.getX(), event.getY());
                        // Log.d(TAG, String.format("onTouch: %.2f %.2f", goal[0], goal[1]));

                        // add my own code here
                        locationLayer.setCurrentPosition(new PointF(goal[0], goal[1]));

                        navTo();
                        mapView.refresh();

                        mCurrentTouchState = CURRENT_TOUCH_STATE_NONE;
                    } else if (mCurrentTouchState == CURRENT_TOUCH_STATE_SELECT_TARGET) {
                        float[] goal = mapView.convertMapXYToScreenXY(event.getX(), event.getY());

                        mTarget = new PointF(goal[0], goal[1]);
                        navTo(mTarget);

                        mCurrentTouchState = CURRENT_TOUCH_STATE_NONE;
                    }

                }
                return false;
            }
        });
    }

    // -----------------------------------------------------------------------

    private void startSettingActivity() {

        startActivity(new Intent(MapActivity.this, SettingsActivity.class));

    }
}