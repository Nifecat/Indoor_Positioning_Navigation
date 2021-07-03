package edu.xmu.inroomlocation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// 指南针示例
public class Main3Activity extends AppCompatActivity {
    private TextView tv_x,tv_y,tv_z,tv_orientation,tv_orientation_valus,tv_acc_valus,tv_mag_valus,tv_degree;
    private SensorManager sensorManager;
    private Sensor orientationsensor,accSensor,magSensor;
    public final static String TAG = "logd";
    private float range=22.5f;

    private float[] acc = new float[3];
    private float[] mag = new float[3];

    private ImageView imageView;
    private RotateAnimation rotateAnimation;//旋转动画
    float lastDegree;//上一次旋转角度

    private Vibrator mVibrator;//震动 ,方向转动到正东、西、南、北会震动一下

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initView();
    }

    private void initView() {
        tv_x = (TextView)findViewById(R.id.tv_x);
        tv_y = (TextView)findViewById(R.id.tv_y);
        tv_z = (TextView)findViewById(R.id.tv_z);

        tv_orientation = (TextView)findViewById(R.id.tv_orientation);
        tv_orientation_valus = (TextView)findViewById(R.id.tv_orientation_value);

        tv_acc_valus = (TextView)findViewById(R.id.tv_acc_valus);
        tv_mag_valus = (TextView)findViewById(R.id.tv_mag_valus);

        tv_degree = (TextView)findViewById(R.id.tv_degree);

        //传感器初始化
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        orientationsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        imageView = (ImageView)findViewById(R.id.iv_compass);

        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        Log.d(TAG, "initView: "+range);
    }

    String orientation = "";
    void getDegreeByOrientation(float degree){
        //isRange 是否在偏差内

        //正北范围 为了方便只管判断，这里把偏差设置22.5°
        if(degree<range || degree>360-range){
            tv_orientation.setText("正北");

            if(!orientation.equals("正北")){
                //如果上次还停留再正北方向，不需要重复震动，下同
                setVibrator(true);
            }
            orientation = "正北";

        }
        //正东范围90°-偏差值----90°+偏差值
        if(degree<90+range && degree>90-range){
            tv_orientation.setText("正东");
            if(!orientation.equals("正东")){
                setVibrator(true);
            }
            orientation = "正东";
        }
        //正南范围180°-偏差值----180°+偏差值
        if(degree<180+range && degree>180-range){
            tv_orientation.setText("正南");
            if(!orientation.equals("正南")){
                setVibrator(true);
            }
            orientation = "正南";
        }
        //正西范围270°-偏差值----270°+偏差值
        if(degree<270+range && degree>270-range){
            tv_orientation.setText("正西");
            if(!orientation.equals("正西")){
                setVibrator(true);
            }
            orientation = "正西";
        }
        //同上  东北
        if(degree<45+range && degree>45-range){
            tv_orientation.setText("东北");
        }
        //同上  东南
        if(degree<135+range && degree>135-range){
            tv_orientation.setText("东南");

        }
        //同上  西南
        if(degree<225+range && degree>225-range){
            tv_orientation.setText("西南");

        }
        //同上  西北
        if(degree<315+range && degree>315-range){
            tv_orientation.setText("西北");

        }
    }

    float getDegreeByAccMag(){
        float[] R = new float[9];
        float[] values = new float[3];
        SensorManager.getRotationMatrix(R,null,acc,mag);
        SensorManager.getOrientation(R,values);
        float degree = (float) Math.toDegrees(values[0]);

        if(degree>=0-range&&degree<range){
            tv_acc_valus.setText("北");
            tv_mag_valus.setText(degree+"");
        }
        if(degree>=45-range&&degree<45+range){
            tv_acc_valus.setText("东北");
            tv_mag_valus.setText(degree+"");

        }

        if(degree>=90-range&&degree<90+range){
            tv_acc_valus.setText("东");
            tv_mag_valus.setText(degree+"");
        }

        if(degree>=135-range&&degree<135+range){
            tv_acc_valus.setText("东南");
            tv_mag_valus.setText(degree+"");
        }

        if(degree>=180-range||degree<-180+range){
            tv_acc_valus.setText("南");
            tv_mag_valus.setText(degree+"");
        }

        if(degree>=-135-range&&degree<-135+range){
            tv_acc_valus.setText("西南");
            tv_mag_valus.setText(degree+"");
        }

        if(degree>=-90-range&&degree<-90+range){
            tv_acc_valus.setText("西");
            tv_mag_valus.setText(degree+"");
        }

        if(degree>=-45-range&&degree<-45+range){
            tv_acc_valus.setText("西北");
            tv_mag_valus.setText(degree+"");
        }

        return degree;
    }

    //设置震动
    private void setVibrator(boolean b) {
        if (b){
            if(Build.VERSION.SDK_INT>=26) {
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, 10);
                mVibrator.vibrate(vibrationEffect);
                Log.d(TAG, "setVibrator: ");
            }
            mVibrator.vibrate(100);
        }
    }

    //监听接口
    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                float z = event.values[0];
                float x = event.values[1];
                float y = event.values[2];
                tv_z.setText(z + "");
                tv_x.setText(x + "");
                tv_y.setText(y + "");
                tv_orientation_valus.setText(z + "");
                getDegreeByOrientation(z);

            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                acc = event.values.clone();
                /*clone()克隆，深度复制*/
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mag = event.values.clone();

            }
            startRotateAnimation(getDegreeByAccMag());

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    //动画实现 指南针旋转
    void startRotateAnimation(float rotateDegree){

        if(rotateDegree>lastDegree){
            if(Math.abs(rotateDegree - lastDegree)>3){
                rotateAnimation = new RotateAnimation(lastDegree,-rotateDegree,
                        Animation.RELATIVE_TO_SELF,0.5f,
                        Animation.RELATIVE_TO_SELF,0.5f);
                LinearInterpolator lin = new LinearInterpolator();//线性
                rotateAnimation.setInterpolator(lin);
                rotateAnimation.setDuration(1500);//设置动画持续时间
                rotateAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                imageView.startAnimation(rotateAnimation);
                lastDegree = -rotateDegree;
            }
        }else {
            if(Math.abs( lastDegree - rotateDegree)>3){
                rotateAnimation = new RotateAnimation(lastDegree,-rotateDegree,
                        Animation.RELATIVE_TO_SELF,0.5f,
                        Animation.RELATIVE_TO_SELF,0.5f);
                LinearInterpolator lin = new LinearInterpolator();//线性
                rotateAnimation.setInterpolator(lin);
                rotateAnimation.setDuration(1500);//设置动画持续时间
                rotateAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                imageView.startAnimation(rotateAnimation);
                lastDegree = -rotateDegree;
            }
        }

        if(rotateDegree>=0){
            //设置指南针图中间的值
            tv_degree.setText(((int)rotateDegree)+"°");
        }else {
            tv_degree.setText(((int)(180+(180- Math.abs(rotateDegree))))+"°");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册
        if (orientationsensor!=null){
            sensorManager.registerListener(listener,orientationsensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(accSensor!=null){
            sensorManager.registerListener(listener,accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(magSensor!=null){
            sensorManager.registerListener(listener,magSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消注册
        if (sensorManager!=null){
            sensorManager.unregisterListener(listener);
        }
    }




}
