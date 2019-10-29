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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

//测量点操作类
public class CalculateDistanceActivity extends Activity {
	
	Button btnEnter;
	Button btnStart;
	Button btnEnd;
	TextView tvStart;
	TextView tvEnd;
	TextView tvH;
	TextView tvV;
	TextView tvA;
	TextView tvP;
	
    String selectPointName;
    int selectPointFlag;
    SurveyPoint pointStart;
    SurveyPoint pointEnd;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_tools_calculatedistance);
		btnEnter=(Button) this.findViewById(R.id.button_enter_view_tools_calculatedistance);
		btnEnter.setOnClickListener(new ButtonEnterClickEvent());
		btnStart=(Button) this.findViewById(R.id.button_start_view_tools_calculatedistance);
		btnStart.setOnClickListener(new ButtonStartClickEvent());
		btnEnd=(Button) this.findViewById(R.id.button_end_view_tools_calculatedistance);
		btnEnd.setOnClickListener(new ButtonEndClickEvent());
		
		tvStart=(TextView) this.findViewById(R.id.edittext_start_view_tools_calculatedistance);
		tvEnd=(TextView) this.findViewById(R.id.edittext_end_view_tools_calculatedistance);
	
		tvH=(TextView) this.findViewById(R.id.textview_h_view_tools_calculatedistance);
		tvV=(TextView) this.findViewById(R.id.textview_v_view_tools_calculatedistance);
		tvA=(TextView) this.findViewById(R.id.textview_a_view_tools_calculatedistance);
		tvP=(TextView) this.findViewById(R.id.textview_p_view_tools_calculatedistance);
	}
	// 计算转角
	private class ButtonEnterClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			pointStart=new SurveyPoint();
			Cursor sCursor = PadApplication.FindPointByName(tvStart.getText().toString());
			if(sCursor.getCount()<1){
				Toast toast = Toast.makeText(
						CalculateDistanceActivity.this, "点"
								+ tvStart.getText().toString().trim() + "不存在！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			int PointFlag = sCursor.getInt(0);
			// 当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
			if (PointFlag == 1 || PointFlag == 2) {
				CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
				GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
				pgc.Latitude = SurveyMath.DEGToRadian(sCursor.getDouble(3));
				pgc.Longitude = SurveyMath.DEGToRadian(sCursor.getDouble(4));
				pgc.Height = sCursor.getDouble(5);

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

				pointStart.X = pcc.X;
				pointStart.Y = pcc.Y;
				pointStart.Elevation=pcc.H;
			}
			// 当原始坐标为用户输入的原始网格坐标和计算的交点时
			if (PointFlag == 3 || PointFlag == 4) {
				// 坐标反算
				pointStart.X = sCursor.getDouble(3);
				pointStart.Y = sCursor.getDouble(4);
				pointStart.Elevation = sCursor.getDouble(5);
			}
			
			pointEnd=new SurveyPoint();
			Cursor eCursor = PadApplication.FindPointByName(tvEnd.getText().toString());
			if(eCursor.getCount()<1){
				Toast toast = Toast.makeText(
						CalculateDistanceActivity.this, "点"
								+ tvEnd.getText().toString().trim() + "不存在！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			PointFlag = eCursor.getInt(0);
			// 当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
			if (PointFlag == 1 || PointFlag == 2) {
				CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
				GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
				pgc.Latitude = SurveyMath.DEGToRadian(eCursor.getDouble(3));
				pgc.Longitude = SurveyMath.DEGToRadian(eCursor.getDouble(4));
				pgc.Height = eCursor.getDouble(5);

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

				pointEnd.X = pcc.X;
				pointEnd.Y = pcc.Y;
				pointEnd.Elevation=pcc.H;
			}
			// 当原始坐标为用户输入的原始网格坐标和计算的交点时
			if (PointFlag == 3 || PointFlag == 4) {
				// 坐标反算
				pointEnd.X = eCursor.getDouble(3);
				pointEnd.Y = eCursor.getDouble(4);
				pointEnd.Elevation= eCursor.getDouble(5);
			}
			
			double dH,dV,dP;
			DecimalFormat df = new DecimalFormat("0.000"); // 创建一个格式化类f
			dH=SurveyMath.GetDistance(pointStart, pointEnd);
			dV=Math.abs(pointStart.Elevation-pointEnd.Elevation);
			tvH.setText("平距：" + df.format(dH));
			tvV.setText("高差：" + df.format(dV));
			SurveyAngle sA=new SurveyAngle(SurveyMath.GetAzimuth(pointStart, pointEnd));
			tvA.setText("方位角：" + String.valueOf( sA.GetSubDegree()+"度" +
			sA.GetSubMinute()+"分" + sA.GetSubSecond(0))+"秒");
			dP=Math.tan(dV/dH);
			sA.valueOfRadian(dP);
			tvP.setText("坡度：" + df.format(sA.GetDEG())+ "度（百分之" + df.format(dV/dH*100) + "）");
			
		};
	}
	// 选择转角点
	private class ButtonStartClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// 新建一个Intent
			
			selectPointFlag=0;
			Intent intent = new Intent();
			intent.putExtra("CoorStyle", 3);
			// 制定intent要启动的类
			intent.setClass(CalculateDistanceActivity.this,
					SelectPointActivity.class);
			// 启动一个新的Activity
			startActivityForResult(intent, 0);
		}
	}
	// 选择后视点
	private class ButtonEndClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// 新建一个Intent
			selectPointFlag=1;
			Intent intent = new Intent();
			intent.putExtra("CoorStyle", 3);
			// 制定intent要启动的类
			intent.setClass(CalculateDistanceActivity.this,
					SelectPointActivity.class);
			// 启动一个新的Activity
			startActivityForResult(intent, 0);
		}
	}

	// 重写该方法，该方法以回调的方式来获取指定Activity返回的结果
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// 当requestCode、resultCode同时为0，也就是处理特定的结果
		if (requestCode == 0 && resultCode == RESULT_OK) {
			selectPointName =intent.getStringExtra("PointName");
			if(selectPointFlag==0){
				tvStart.setText(selectPointName);
			}
			if(selectPointFlag==1){
				tvEnd.setText(selectPointName);
			}


		}
	}

	
}
