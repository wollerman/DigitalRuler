package com.example.digitalmeasuringtape;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;

public class Calibrate extends Activity implements SensorEventListener, Runnable { 
	AlertDialog dialog;
	public CountDownLatch gate;
	public SharedPreferences settings;	
	public TailLinkedList measurements;
	private String pi_string;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        settings = getSharedPreferences("prefs", 0);
        
		Thread thread = new Thread(this);
		thread.start();
        
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("00:05").setTitle("CALIBRATING");
		dialog = builder.create();
		dialog.show();
		
		new CountDownTimer(6000,1000){
			@Override
			public void onTick(long millisUntilFinished){
				dialog.setMessage("00:" + (millisUntilFinished/1000));
			}
			
			@Override
			public void onFinish(){
				dialog.dismiss();
				System.out.println("calibration finished. dismiss and countdown");
				finish();
			}
		}.start();
//        setContentView(R.layout.calibrate);
		
    }
    

    
    //TODO add calibrate() function
	public void calibrate()
	{	
		//setting up sensor managers
		SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		System.out.println("Calibrate");
		//make a fresh list, set gate as closed, register listener
		measurements = new TailLinkedList();
		boolean worked = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);	
		System.out.println("Return from registerlistener: " + worked );
		
		try {
			Thread.sleep(4000);
			System.out.println("after sleep");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mSensorManager.unregisterListener(this, mAccelerometer);
		//mSensorManager.unregisterListener(this, mOrientation);
		ArrayList<Float> xData = measurements.getxData();
		ArrayList<Float> yData = measurements.getyData();
		ArrayList<Float> zData = measurements.getzData();
		
		float xAvg = 0, yAvg = 0, zAvg = 0;
		
		for(int i = 0; i < xData.size(); i ++)
		{
			xAvg += xData.get(i);
			yAvg += yData.get(i);
			zAvg += zData.get(i);
		}
		
		xAvg /= xData.size();
		yAvg /= yData.size();
		zAvg /= zData.size();
		
		System.out.println("Gravity_x: " + xAvg);
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("Gravity_x", xAvg);
		editor.putFloat("Gravity_y", yAvg);
		editor.putFloat("Gravity_z", zAvg);
		editor.putBoolean("CALIBRATED", true);
		
		editor.commit();
		
		
		float exval = settings.getFloat("Gravity_x", -1);
		System.out.println("exval: " + exval);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		//if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
		//return;
	
	switch(event.sensor.getType())
	{
	case Sensor.TYPE_ACCELEROMETER :
		System.out.println("Accel Sensor changed");
		float x = event.values[0]; 
		float y = event.values[1];
		float z = event.values[2];
		long t = event.timestamp; 
		pi_string = "x = " + x + "\ny = " + y + "\nz = " + z;
		System.out.println(pi_string);
//		handler.sendEmptyMessage(0);
		measurements.add(x, y, z, t); //record values.
		break;
	}
	}



	@Override
    public void run()
    {
    	System.out.println("running thread");
		calibrate();    	
		System.out.println("after calibrate");
    }
}