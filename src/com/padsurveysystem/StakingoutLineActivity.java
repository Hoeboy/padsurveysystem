package com.padsurveysystem;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


//测量点操作类
public class StakingoutLineActivity extends Activity implements
		SensorEventListener {

	// 菜单视图控件
	Button btnEnter;
	RadioButton rbtGrid;
	RadioButton rbtLatlon;
	RadioButton rbtNoMap;
	RadioButton rbtGoogleMap;
	RadioButton rbtAnotherMap;

	// 位置视图控件
	MapFragment mapView;

	Button btnSave;

	GoogleMap googleMap;

	TextView tvStation;
	TextView tvOffset;
	TextView tvS;
	TextView tvD;
	// 定义显示指南针的图片
	ImageView CompassImage;
	// 记录指南针图片转过的角度
	float currentDegree = 0;

	// 定义真机的Sensor管理器
	SensorManager mSensorManager;
	// GPS位置服务
	LocationManager locationManager;
	double Latitude = 0;
	double Longitude = 0;
	double Altitude = 0;
	double Direction = 0;// 方向
	double Speed = 0;// 速度

	// 显示第三方地图
	MapTilesView ivMapView;
	ZoomControls zooMap;
	ImageButton imbLocation;
	boolean mFreshAnotherMap = true;// 当前位置设置在第三方地图的中心
	boolean mHaveLocation = false;// 是否有获取位置信息
	double mX = 0;
	double mY = 0;

	double targetX, targetY;
	double currentX, currentY;
	View viewSelectLine;
	View viewNormal;
	View viewGoogleMap;
	View viewAnotherMap;
	int currentView = 0;// 当前显示视图0-普通，1-googlemap,2-anothermap
	boolean isShowSetView = false;// 是否显示了设置视图

	String StartPointName;
	String EndPointName;
	String StartStation;
	LinePoint StartPoint;
	LinePoint EndPoint;
	double drawLineStartLat;
	double drawLineStartLon;
	double drawLineEndLat;
	double drawLineEndLon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// instance=SurveyPositionNormalActivity.this;

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		viewNormal = (View) inflater.inflate(
				R.layout.view_survey_stakingoutline_normal, null);
		viewGoogleMap = (View) inflater.inflate(
				R.layout.view_survey_stakingoutline_googlemap, null);
		viewAnotherMap = (View) inflater.inflate(
				R.layout.view_survey_stakingoutline_anothermap, null);
		Bundle bundle = this.getIntent().getExtras();
		StartPointName = bundle.getString("StartPointName");
		EndPointName = bundle.getString("EndPointName");
		StartStation = bundle.getString("StartStation");
		setTitle("放样线 " + StartPointName + " - " + EndPointName); // 设置窗口标题栏名称
		showNormalView();
		// 获取真机的传感器管理服务
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// GPS服务参数
		String serviceString = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(serviceString);

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, 1000, 0,
				locationListener);

		StartPoint = new LinePoint();
		EndPoint = new LinePoint();
		StartPoint.Name=StartPointName;
		EndPoint.Name=EndPointName;
		StartPoint.Station = Double.valueOf(StartStation);

		Cursor sCursor = PadApplication.FindPointByName(StartPointName);
		if (sCursor == null)
			return;
		int PointFlag = sCursor.getInt(0);
		// 当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
		if (PointFlag == 1 || PointFlag == 2) {
			CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
			GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
			pgc.Latitude = SurveyMath.DEGToRadian(sCursor.getDouble(3));
			pgc.Longitude = SurveyMath.DEGToRadian(sCursor.getDouble(4));
			pgc.Height = sCursor.getDouble(5);

			drawLineStartLat = sCursor.getDouble(3);
			drawLineStartLon = sCursor.getDouble(4);

			// 当使用存在的坐标系统时，使用坐标正算
			if (PadApplication.UseCoordinateTransfomation == 0) {
				CoordinateTransform ct = new CoordinateTransform();
				CoordinateSystem cs = PadApplication.CurrentCoordinateSystem;
				ct.GeodeticToCartesian(pcc, pgc, cs);
			} else// 使用坐标转换（点校正）
			{
				double[] dC = new double[3];
				double[] dW = new double[3];
				dW[0] = pgc.Latitude;
				dW[1] = pgc.Longitude;
				dW[2] = pgc.Height;
				PadApplication.CoordinateTrans.WGS84TransCartesian(dW, dC);
				pcc.X = dC[0];
				pcc.Y = dC[1];
				pcc.H = dC[2];
			}

			StartPoint.X = pcc.X;
			StartPoint.Y = pcc.Y;
		}
		// 当原始坐标为用户输入的原始网格坐标和计算的交点时
		if (PointFlag == 3 || PointFlag == 4) {
			// 坐标反算
			StartPoint.X = sCursor.getDouble(3);
			StartPoint.Y = sCursor.getDouble(4);

			// // 坐标反算,求出经纬度，在GOOGLEMAP上画出来

			CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
			GeodeticCoordinatePoint pcg = new GeodeticCoordinatePoint();

			pcc.X = sCursor.getDouble(3);
			pcc.Y = sCursor.getDouble(4);
			pcc.H = sCursor.getDouble(5);

			// 当使用存大的坐标系统时，使用坐标反算
			if (PadApplication.UseCoordinateTransfomation == 0) {
				CoordinateTransform ct = new CoordinateTransform();
				CoordinateSystem cs = PadApplication.CurrentCoordinateSystem;
				ct.CartesianToGeodetic(pcg, pcc, cs);
			} else// 使用坐标转换（点校正）
			{
				double[] dC = new double[3];
				double[] dW = new double[3];
				dC[0] = pcc.X;
				dC[1] = pcc.Y;
				dC[2] = pcc.H;
				PadApplication.CoordinateTrans.CartesianTransWGS84(dC, dW);
				pcg.Latitude = dW[0];
				pcg.Longitude = dW[1];
				pcg.Height = dW[2];
			}
			SurveyAngle saLat = new SurveyAngle(pcg.Latitude);
			SurveyAngle saLon = new SurveyAngle(pcg.Longitude);
			drawLineStartLat = saLat.GetDEG();
			drawLineStartLon = saLon.GetDEG();
		}

		Cursor eCursor = PadApplication.FindPointByName(EndPointName);
		if (eCursor == null)
			return;
		PointFlag = eCursor.getInt(0);
		// 当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
		if (PointFlag == 1 || PointFlag == 2) {
			CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
			GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
			pgc.Latitude = SurveyMath.DEGToRadian(eCursor.getDouble(3));
			pgc.Longitude = SurveyMath.DEGToRadian(eCursor.getDouble(4));
			pgc.Height = eCursor.getDouble(5);

			drawLineEndLat = eCursor.getDouble(3);
			drawLineEndLon = eCursor.getDouble(4);

			// 当使用存在的坐标系统时，使用坐标正算
			if (PadApplication.UseCoordinateTransfomation == 0) {
				CoordinateTransform ct = new CoordinateTransform();
				CoordinateSystem cs = PadApplication.CurrentCoordinateSystem;
				ct.GeodeticToCartesian(pcc, pgc, cs);
			} else// 使用坐标转换（点校正）
			{
				double[] dC = new double[3];
				double[] dW = new double[3];
				dW[0] = pgc.Latitude;
				dW[1] = pgc.Longitude;
				dW[2] = pgc.Height;
				PadApplication.CoordinateTrans.WGS84TransCartesian(dW, dC);
				pcc.X = dC[0];
				pcc.Y = dC[1];
				pcc.H = dC[2];
			}

			EndPoint.X = pcc.X;
			EndPoint.Y = pcc.Y;
		}
		// 当原始坐标为用户输入的原始网格坐标和计算的交点时
		if (PointFlag == 3 || PointFlag == 4) {
			// 坐标反算
			EndPoint.X = eCursor.getDouble(3);
			EndPoint.Y = eCursor.getDouble(4);

			// // 坐标反算,求出经纬度，在GOOGLEMAP上画出来
			CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
			GeodeticCoordinatePoint pcg = new GeodeticCoordinatePoint();

			pcc.X = eCursor.getDouble(3);
			pcc.Y = eCursor.getDouble(4);
			pcc.H = eCursor.getDouble(5);

			// 当使用存大的坐标系统时，使用坐标反算
			if (PadApplication.UseCoordinateTransfomation == 0) {
				CoordinateTransform ct = new CoordinateTransform();
				CoordinateSystem cs = PadApplication.CurrentCoordinateSystem;
				ct.CartesianToGeodetic(pcg, pcc, cs);
			} else// 使用坐标转换（点校正）
			{
				double[] dC = new double[3];
				double[] dW = new double[3];
				dC[0] = pcc.X;
				dC[1] = pcc.Y;
				dC[2] = pcc.H;
				PadApplication.CoordinateTrans.CartesianTransWGS84(dC, dW);
				pcg.Latitude = dW[0];
				pcg.Longitude = dW[1];
				pcg.Height = dW[2];
			}
			SurveyAngle eLat = new SurveyAngle(pcg.Latitude);
			SurveyAngle eLon = new SurveyAngle(pcg.Longitude);
			drawLineEndLat = eLat.GetDEG();
			drawLineEndLon = eLon.GetDEG();
		}

	}

	private void showNormalView() {
		setTitle("放样线:" + StartPointName + "-"+ EndPointName + ""); // 设置窗口标题栏名称
		setContentView(viewNormal);
		currentView = 0;
		// 初始化控件
		tvStation = (TextView) findViewById(R.id.textview_station_view_survey_stakingoutline_normal);
		tvOffset = (TextView) findViewById(R.id.textview_offset_view_survey_stakingoutline_normal);

		tvS = (TextView) findViewById(R.id.textview_speed_view_survey_stakingoutline_normal);
		tvD = (TextView) findViewById(R.id.textview_direction_view_survey_stakingoutline_normal);

		btnSave = (Button) this
				.findViewById(R.id.button_save_view_survey_stakingoutline_normal);
		btnSave.setOnClickListener(new ButtonSaveClickEvent());

		// 获取界面中显示指南针的图片
		CompassImage = (ImageView) findViewById(R.id.imageview_compass_view_survey_stakingoutline_normal);
	}

	private void showGoogleMapView() {
		setTitle("放样线:" + StartPointName + "-"+ EndPointName + ""); // 设置窗口标题栏名称
		setContentView(viewGoogleMap);
		currentView = 1;
		mapView = (MapFragment) getFragmentManager().findFragmentById(
				R.id.fragment_googlemap_view_survey_stakingoutline_googlemap);
		btnSave = (Button) findViewById(R.id.button_save_view_survey_stakingoutline_googlemap);
		btnSave.setOnClickListener(new ButtonSaveClickEvent());
		tvStation = (TextView) findViewById(R.id.textview_station_view_survey_stakingoutline_googlemap);
		tvOffset = (TextView) findViewById(R.id.textview_offset_view_survey_stakingoutline_googlemap);
		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else {

			// Getting GoogleMap object from the fragment
			googleMap = mapView.getMap();
			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);
			googleMap.setMapType(googleMap.MAP_TYPE_HYBRID);

			// 绘制放样线

			LatLng MELBOURNE = new LatLng(drawLineStartLat, drawLineStartLon);
			Marker sM = googleMap
					.addMarker(new MarkerOptions().position(MELBOURNE)
							.title("放样线起点").snippet(StartPointName));

			MELBOURNE = new LatLng(drawLineEndLat, drawLineEndLon);
			Marker eM = googleMap.addMarker(new MarkerOptions()
					.position(MELBOURNE).title("放样线终点").snippet(EndPointName));
			Polyline line = googleMap.addPolyline(new PolylineOptions()
					.add(new LatLng(drawLineStartLat, drawLineStartLon),
							new LatLng(drawLineEndLat, drawLineEndLon))
					.width(5).color(Color.BLUE));
		}

	}

	private void showAnotherMapView() {
		setTitle("放样线:" + StartPointName + "-"+ EndPointName + ""); // 设置窗口标题栏名称

		if (PadApplication.AnotherMapFileName.trim() == "") {
			final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(
					this);
			builder.setTitle("PadSurvey");
			builder.setMessage("请设置第三方地图！");
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
				}
			});
			builder.create().show();
			if (currentView == 1) {
				showGoogleMapView();
			}
			if (currentView == 1) {
				showNormalView();
			}
			return;
		}

		// 显示第三方地图
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		viewAnotherMap = (View) inflater.inflate(
				R.layout.view_survey_stakingoutline_anothermap, null);
		setContentView(viewAnotherMap);
		btnSave = (Button) findViewById(R.id.button_save_view_survey_stakingoutline_anothermap);
		btnSave.setOnClickListener(new ButtonSaveClickEvent());
		tvStation = (TextView) findViewById(R.id.textview_station_view_survey_stakingoutline_anothermap);
		tvOffset = (TextView) findViewById(R.id.textview_offset_view_survey_stakingoutline_anothermap);

		imbLocation = (ImageButton) findViewById(R.id.locationbutton_view_survey_stakingoutline_anothermap);
		ivMapView = (MapTilesView) findViewById(R.id.maptiles_view_survey_stakingoutline_anothermap);
		zooMap = (ZoomControls) findViewById(R.id.zoomcontrols_view_survey_stakingoutline_anothermap);
		ivMapView.drawLine(StartPoint, EndPoint);
		currentView = 2;
		ShowAnotherMap();

		zooMap.setOnZoomInClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ivMapView.getLevelNumber() == ivMapView.getLevelCount()) {
					return;
				}
				int iNumber = ivMapView.getLevelNumber() + 1;
				ivMapView.ZoomMap(iNumber);
			}
		});

		zooMap.setOnZoomOutClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ivMapView.getLevelNumber() == 1) {
					return;
				}
				int iNumber = ivMapView.getLevelNumber() - 1;
				ivMapView.ZoomMap(iNumber);
			}

		});
		imbLocation.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mHaveLocation) {
					ivMapView.setViewCenter(currentY, currentX);
				}
			}

		});
	}

	private void ShowAnotherMap() {
		String strFilePath = "mnt/sdcard/PadSurveyData/AnotherMap/"
				+ PadApplication.AnotherMapFileName;
		ivMapView.initializeMap(strFilePath);
	}

	private void showSetView() {

		setContentView(R.layout.view_survey_stakingoutpoint_viewstyle);
		setTitle("显示设置");
		isShowSetView = true;
		btnEnter = (Button) findViewById(R.id.button_save_survey_stakingoutpoint_viewstyle);
		btnEnter.setOnClickListener(new ButtonEnterClickEvent());

		rbtNoMap = (RadioButton) findViewById(R.id.radiobutton_nomap_survey_stakingoutpoint_viewstyle);
		rbtGoogleMap = (RadioButton) findViewById(R.id.radiobutton_googlemap_survey_stakingoutpoint_viewstyle);
		rbtAnotherMap = (RadioButton) findViewById(R.id.radiobutton_anothermap_survey_stakingoutpoint_viewstyle);

		if (currentView == 0) {
			rbtNoMap.setChecked(true);
			rbtGoogleMap.setChecked(false);
			rbtAnotherMap.setChecked(false);
		}
		if (currentView == 1) {
			rbtNoMap.setChecked(false);
			rbtGoogleMap.setChecked(true);
			rbtAnotherMap.setChecked(false);
		}
		if (currentView == 2) {
			rbtNoMap.setChecked(false);
			rbtGoogleMap.setChecked(false);
			rbtAnotherMap.setChecked(true);
		}

	}

	// GPS监听器
	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {

			getLocationInfomation(location);
			if (currentView == 1) {
				locationMap(location);
			}
			if (currentView == 2) {
				locationAnotherMap(location);
			}
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

	// 获取经坐标
	private void getLocationInfomation(Location location) {
		DecimalFormat df = new DecimalFormat("0.0"); // 创建一个格式化类f
		if (location != null) {
			mHaveLocation = true;
			Latitude = location.getLatitude();
			Longitude = location.getLongitude();
			Altitude = location.getAltitude();
			Direction = location.getBearing();// 方向
			Speed = location.getSpeed() * 3.6f;// 速度
		} else {
			mHaveLocation = false;
		}
		setLocationInformation();
	}

	private void setLocationInformation() {

		DecimalFormat df = new DecimalFormat("0.0"); // 创建一个格式化类f
		SurveyAngle bA = new SurveyAngle();
		SurveyAngle lA = new SurveyAngle();
		bA.valueOfDEG(Latitude);
		lA.valueOfDEG(Longitude);

		// 使用已经存在的坐标系统
		if (PadApplication.UseCoordinateTransfomation == 0) {

			CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
			GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
			pgc.Latitude = bA.GetRadian();
			pgc.Longitude = lA.GetRadian();
			CoordinateTransform.GeodeticToCartesian(pcc, pgc,
					PadApplication.CurrentCoordinateSystem);
			currentX = pcc.X;
			currentY = pcc.Y;
		}
		// 使用工地校正七参数
		if (PadApplication.UseCoordinateTransfomation == 1) {
			double[] WGS84Coordinate = new double[3];
			double[] UserCoordinate = new double[3];
			WGS84Coordinate[0] = bA.GetRadian();
			WGS84Coordinate[1] = lA.GetRadian();
			WGS84Coordinate[2] = Altitude;
			PadApplication.CoordinateTrans.WGS84TransCartesian(WGS84Coordinate,
					UserCoordinate);
			currentX = UserCoordinate[0];
			currentY = UserCoordinate[1];
		}

		LinePoint tP = new LinePoint();
		tP.X = currentX;
		tP.Y = currentY;

		android.util.Log.i("StartPoint", String.valueOf(StartPoint.X));
		SurveyMath.GetStationOffset(StartPoint, EndPoint, tP);

		tvStation.setText("里程：" + df.format(tP.Station));
		tvOffset.setText("偏距：" + df.format(tP.Offset));
		if (tvS != null) {
			tvS.setText(String.valueOf("速度：" + df.format(Speed)) + "公里/小时");
		}
		if (tvD != null) {
			tvD.setText(String.valueOf("方向：" + df.format(Direction)));
		}

	}

	private void locationMap(Location location) {
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	}

	// 载入 第三方地图
	private void locationAnotherMap(Location location) {

		double aX = 0;
		double aY = 0;

		SurveyAngle bA = new SurveyAngle();
		SurveyAngle lA = new SurveyAngle();
		bA.valueOfDEG(Latitude);
		lA.valueOfDEG(Longitude);

		// 使用已经存在的坐标系统
		if (PadApplication.UseCoordinateTransfomation == 0) {

			CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
			GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
			pgc.Latitude = bA.GetRadian();
			pgc.Longitude = lA.GetRadian();
			CoordinateTransform.GeodeticToCartesian(pcc, pgc,
					PadApplication.CurrentCoordinateSystem);

			aX = pcc.X;
			aY = pcc.Y;
		}
		// 使用工地校正七参数
		if (PadApplication.UseCoordinateTransfomation == 1) {
			double[] WGS84Coordinate = new double[3];
			double[] UserCoordinate = new double[3];
			WGS84Coordinate[0] = bA.GetRadian();
			WGS84Coordinate[1] = lA.GetRadian();
			WGS84Coordinate[2] = Altitude;
			PadApplication.CoordinateTrans.WGS84TransCartesian(WGS84Coordinate,
					UserCoordinate);
			aX = UserCoordinate[0];
			aY = UserCoordinate[1];
		}
		mX = aX;
		mY = aY;
		if (mFreshAnotherMap) {
			if (mHaveLocation) {
				ivMapView.setViewCenter(mY, mX);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		// 为系统的方向传感器注册监听器
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 0, locationListener);

	}

	@Override
	protected void onPause() {
		// 取消注册
		mSensorManager.unregisterListener(this);

		super.onPause();
		// 取消GPS服务
		locationManager.removeUpdates(locationListener);
	}

	@Override
	protected void onStop() {
		// 取消注册
		mSensorManager.unregisterListener(this);
		super.onStop();
		locationManager.removeUpdates(locationListener);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		// 真机上获取触发event的传感器类型
		int sensorType = event.sensor.getType();
		// // 模拟器上获取触发event的传感器类型
		// int sensorType = event.type;
		switch (sensorType) {
		case Sensor.TYPE_ORIENTATION:
			// 获取绕Z轴转过的角度。
			float degree = event.values[0];
			// 创建旋转动画（反向转过degree度）
			RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			// 设置动画的持续时间
			ra.setDuration(200);
			// 运行动画
			CompassImage.startAnimation(ra);
			currentDegree = -degree;
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	// 保存观测数据
	private class ButtonSaveClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// 制定intent要启动的类
			Intent intent = new Intent(StakingoutLineActivity.this,
					SavePointActivity.class);
			intent.putExtra("X", Latitude);
			intent.putExtra("Y", Longitude);
			intent.putExtra("Z", Altitude);
			// 启动一个新的Activity
			startActivity(intent);
		}
	}

	// 菜单的操作
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!isShowSetView) {
				super.openOptionsMenu();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isShowSetView) {
				if (currentView == 0) {
					showNormalView();
				}
				if (currentView == 1) {
					showGoogleMapView();
				}
				if (currentView == 2) {
					showAnotherMapView();
				}
				isShowSetView = false;
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
		// 按下键盘上返回按钮

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, "设置");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == 1) {
			// 显示设置视图
			showSetView();
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private class ButtonEnterClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				if (rbtNoMap.isChecked()) {
					currentView = 0;
					// 无图模式
					showNormalView();
				}
				if (rbtGoogleMap.isChecked()) {
					currentView = 1;
					showGoogleMapView();

					// 显示带谷歌地图的位置视图
				}
				if (rbtAnotherMap.isChecked()) {
					currentView = 2;
					// 显示带第三方地图的位置视图
					showAnotherMapView();
				}
				isShowSetView = false;
				PadApplication.ReSetInformation();
				setLocationInformation();

			} catch (NumberFormatException n) {

			}

		}
	}

	// 选择要放榜的点
	private class buttonSelectPointClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// 新建一个Intent
			Intent intent = new Intent();
			intent.putExtra("CoorStyle", 3);
			// 制定intent要启动的类
			intent.setClass(StakingoutLineActivity.this,
					SelectPointActivity.class);
			// 启动一个新的Activity
			startActivityForResult(intent, 0);
		}
	}
}
