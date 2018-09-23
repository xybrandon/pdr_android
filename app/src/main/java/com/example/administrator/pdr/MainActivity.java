package com.example.administrator.pdr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//public class MainActivity extends AppCompatActivity implements SensorEventListener{
public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManger;
    private Sensor mSensorAcc;
    private Sensor mSensorGyro;
    private Sensor mSensorMag;
    private static TextView Ax;
    private static TextView Ay;
    private static TextView Az;
    private static TextView Gx;
    private static TextView Gy;
    private static TextView Gz;
    private static TextView Mx;
    private static TextView My;
    private static TextView Mz;
    private static Timer mTimer;
    private static float[] accData;
    private static float[] gyroData;
    private static float[] magData;

    private static Lock lock = new ReentrantLock();
    private static ArrayList<NineAxisData> logDatas = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Ax =(TextView)findViewById(R.id.Ax);

        Ax = (TextView)findViewById(R.id.Ax);
        Ay = (TextView)findViewById(R.id.Ay);
        Az = (TextView)findViewById(R.id.Az);
        Gx = (TextView)findViewById(R.id.Gx);
        Gy = (TextView)findViewById(R.id.Gy);
        Gz = (TextView)findViewById(R.id.Gz);
        Mx = (TextView)findViewById(R.id.Mx);
        My = (TextView)findViewById(R.id.My);
        Mz = (TextView)findViewById(R.id.Mz);

        // 获取传感器服务
        mSensorManger = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 获取传感器类型
        mSensorAcc = mSensorManger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGyro = mSensorManger.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorMag = mSensorManger.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Button bt1 = (Button)findViewById(R.id.b1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 注册传感器
                mSensorManger.registerListener(mSensorEventListener, mSensorAcc, SensorManager.SENSOR_DELAY_GAME);
                mSensorManger.registerListener(mSensorEventListener, mSensorGyro, SensorManager.SENSOR_DELAY_GAME);
                mSensorManger.registerListener(mSensorEventListener, mSensorMag, SensorManager.SENSOR_DELAY_GAME);
                // 定时器
                mTimer = new Timer();
                mTimer.scheduleAtFixedRate(new Task(), 0, 50);
            }
        });

        Button bt2 = (Button)findViewById(R.id.b2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //注销传感器
                mSensorManger.unregisterListener(mSensorEventListener,mSensorAcc);
                mSensorManger.unregisterListener(mSensorEventListener,mSensorGyro);
                mSensorManger.unregisterListener(mSensorEventListener,mSensorMag);
                mTimer.cancel();
            }
        });
    }

    // 传感器监听处理
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            lock.lock();//获得锁
            try{
                //接收传感器数据
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        accData = Arrays.copyOf(sensorEvent.values, sensorEvent.values.length);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        gyroData = Arrays.copyOf(sensorEvent.values, sensorEvent.values.length);
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        magData = Arrays.copyOf(sensorEvent.values, sensorEvent.values.length);
                    default:
                        break;
                }
            }finally {
                lock.unlock(); //释放锁
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // 定时任务：定时输出传感器数据
    private static class Task extends TimerTask {
        @Override
        public void run() {
            if (accData != null && gyroData != null && magData != null) {
                NineAxisData pdrData = new NineAxisData();
                lock.lock(); // 获得锁
                try{
                    pdrData.setAccData(accData);
                    pdrData.setGyroData(gyroData);
                    pdrData.setMagData(magData);
                }finally {
                    lock.unlock();// 释放锁
                }

                Message msg = new Message();
                msg.what = 2;
                msg.obj = pdrData;
                mHandler.sendMessage(msg);
            }
        }
    }

    // 开启一个新线程用于UI显示
    private static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            NineAxisData pdrData = (NineAxisData) msg.obj;
            switch (msg.what) {
                case 1:
                    float[] accData = pdrData.getAccData();
                    float[] gyroData = pdrData.getGyroData();
                    float[] magData = pdrData.getMagData();
                    Ax.setText(new DecimalFormat("#0.000000").format(accData[0]));
                    Ay.setText(new DecimalFormat("#0.000000").format(accData[1]));
                    Az.setText(new DecimalFormat("#0.000000").format(accData[2]));
                    Gx.setText(new DecimalFormat("#0.000000").format(gyroData[0]));
                    Gy.setText(new DecimalFormat("#0.000000").format(gyroData[1]));
                    Gz.setText(new DecimalFormat("#0.000000").format(gyroData[2]));
                    Mx.setText(new DecimalFormat("#0.00").format(magData[0]));
                    My.setText(new DecimalFormat("#0.00").format(magData[1]));
                    Mz.setText(new DecimalFormat("#0.00").format(magData[2]));
                    break;
            }
        }
    };

    // 存入惯导数据

}

