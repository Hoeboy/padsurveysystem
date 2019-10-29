package com.padsurveysystem;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

//测量点操作类
public class LocationServe {
	public LocationManager locationManager;
	public String Latitude = "0";
	public String Longitude = "0";
	public String Altitude = "0";
	public String Direction = "0";// 方向
	public String Speed = "0";// 速度
	public double[] LocationValues;

	private TextView tvX;
	private TextView tvY;
	private TextView tvZ;
	private TextView tvS;
	private TextView tvD;
	private Context mContext;

	public LocationServe(Activity activity) {
		// 获取位置服务
		String serviceString = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) activity
				.getSystemService(serviceString);
		String provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 1000, 0,
				locationListener);
		mContext = activity;
		LocationValues = new double[5];
	}

	// 设置要显示经纬度的五个TextView
	public void setLocationView(TextView ViewX, TextView ViewY, TextView ViewZ,
			TextView ViewS, TextView ViewD) {
		tvX = ViewX;
		tvY = ViewY;
		tvZ = ViewZ;
		tvX = ViewX;
		tvS = ViewS;
		tvD = ViewD;
	}

	// 设置要显示经纬度的三个TextView
	public void getLocation(double[] agrs) {
		agrs = LocationValues;

	}

	// 获取经纬度
	private void getLocationInfo(Location location) {

		if (location != null) {
			LocationValues[0] = location.getLatitude();
			LocationValues[1] = location.getLongitude();
			LocationValues[2] = location.getAltitude();
			LocationValues[3] = location.getBearing();
			LocationValues[4] = location.getSpeed();
			DecimalFormat df = new DecimalFormat("0.0000000000"); // 创建一个格式化类f
			Latitude = df.format(LocationValues[0]);
			Longitude = df.format(LocationValues[1]);
			Altitude = df.format(LocationValues[2]);
			Direction = df.format(LocationValues[3]);// 方向
			Speed = df.format(LocationValues[4] * 1.852 * 1.852);// 速度
			if (tvX != null) {
				tvX.setText(Latitude);
			}
			if (tvY != null) {
				tvY.setText(Longitude);
			}
			if (tvZ != null) {
				tvZ.setText(Altitude);
			}
			if (tvS != null) {
				tvS.setText(String.valueOf(Speed));
			}
			if (tvD != null) {
				tvD.setText(String.valueOf(Direction));
			}
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			getLocationInfo(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	};

	public void StopGPS() {
		locationManager.removeUpdates(locationListener);
	}

	public boolean EnableGPS() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}
}
