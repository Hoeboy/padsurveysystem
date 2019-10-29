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
public class CalculateTurnAngleActivity extends Activity {
	
	Button btnEnter;
	Button btnTurn;
	Button btnBack;
	Button btnFront;
	TextView tvT;
	TextView tvB;
	TextView tvF;
	TextView tvD;
    String selectPointName;
    int selectPointFlag;
    LinePoint pointTurn;
    LinePoint pointBack;
    LinePoint pointFront;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_tools_calculateangle);
		btnEnter=(Button) this.findViewById(R.id.button_enter_view_tools_calculateangle);
		btnEnter.setOnClickListener(new ButtonEnterClickEvent());
		btnTurn=(Button) this.findViewById(R.id.button_turn_view_tools_calculateangle);
		btnTurn.setOnClickListener(new ButtonTurnClickEvent());
		btnBack=(Button) this.findViewById(R.id.button_back_view_tools_calculateangle);
		btnBack.setOnClickListener(new ButtonBackClickEvent());
		btnFront=(Button) this.findViewById(R.id.button_front_view_tools_calculateangle);
		btnFront.setOnClickListener(new ButtonFrontClickEvent());		
		tvT=(TextView) this.findViewById(R.id.edittext_turn_view_tools_calculateangle);
		tvB=(TextView) this.findViewById(R.id.edittext_back_view_tools_calculateangle);
		tvF=(TextView) this.findViewById(R.id.edittext_front_view_tools_calculateangle);
		tvD=(TextView) this.findViewById(R.id.textview_trun_view_tools_calculateangle);
	}
	// 计算转角
	private class ButtonEnterClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			pointTurn=new LinePoint();
			Cursor tCursor = PadApplication.FindPointByName(tvT.getText().toString());
			if(tCursor.getCount()<1){
				Toast toast = Toast.makeText(
						CalculateTurnAngleActivity.this, "点"
								+tvT.getText().toString() + "不存在！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			int PointFlag = tCursor.getInt(0);
			// 当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
			if (PointFlag == 1 || PointFlag == 2) {
				CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
				GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
				pgc.Latitude = SurveyMath.DEGToRadian(tCursor.getDouble(3));
				pgc.Longitude = SurveyMath.DEGToRadian(tCursor.getDouble(4));
				pgc.Height = tCursor.getDouble(5);

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

				pointTurn.X = pcc.X;
				pointTurn.Y = pcc.Y;
			}
			// 当原始坐标为用户输入的原始网格坐标和计算的交点时
			if (PointFlag == 3 || PointFlag == 4) {
				// 坐标反算
				pointTurn.X = tCursor.getDouble(3);
				pointTurn.Y = tCursor.getDouble(4);
			}
			pointBack=new LinePoint();
			Cursor bCursor = PadApplication.FindPointByName(tvB.getText().toString());
			if(bCursor.getCount()<1){
				Toast toast = Toast.makeText(
						CalculateTurnAngleActivity.this, "点"
								+tvB.getText().toString() + "不存在！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			PointFlag = bCursor.getInt(0);
			// 当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
			if (PointFlag == 1 || PointFlag == 2) {
				CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
				GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
				pgc.Latitude = SurveyMath.DEGToRadian(bCursor.getDouble(3));
				pgc.Longitude = SurveyMath.DEGToRadian(bCursor.getDouble(4));
				pgc.Height = bCursor.getDouble(5);

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

				pointBack.X = pcc.X;
				pointBack.Y = pcc.Y;
			}
			// 当原始坐标为用户输入的原始网格坐标和计算的交点时
			if (PointFlag == 3 || PointFlag == 4) {
				// 坐标反算
				pointBack.X = bCursor.getDouble(3);
				pointBack.Y = bCursor.getDouble(4);
			}
			pointFront=new LinePoint();
			Cursor fCursor = PadApplication.FindPointByName(tvF.getText().toString());
			if(fCursor.getCount()<1){
				Toast toast = Toast.makeText(
						CalculateTurnAngleActivity.this, "点"
								+tvF.getText().toString() + "不存在！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			PointFlag = fCursor.getInt(0);
			// 当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
			if (PointFlag == 1 || PointFlag == 2) {
				CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
				GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
				pgc.Latitude = SurveyMath.DEGToRadian(fCursor.getDouble(3));
				pgc.Longitude = SurveyMath.DEGToRadian(fCursor.getDouble(4));
				pgc.Height = fCursor.getDouble(5);

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

				pointFront.X = pcc.X;
				pointFront.Y = pcc.Y;
			}
			// 当原始坐标为用户输入的原始网格坐标和计算的交点时
			if (PointFlag == 3 || PointFlag == 4) {
				// 坐标反算
				pointFront.X = fCursor.getDouble(3);
				pointFront.Y = fCursor.getDouble(4);
			}
			tvD.setText(SurveyMath.GetTurnAngle(pointTurn,pointBack,pointFront));
		}
	}
	// 选择转角点
	private class ButtonTurnClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// 新建一个Intent
			selectPointFlag=0;
			Intent intent = new Intent();
			intent.putExtra("CoorStyle", 3);
			// 制定intent要启动的类
			intent.setClass(CalculateTurnAngleActivity.this,
					SelectPointActivity.class);
			// 启动一个新的Activity
			startActivityForResult(intent, 0);
		}
	}
	// 选择后视点
	private class ButtonBackClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// 新建一个Intent
			selectPointFlag=1;
			Intent intent = new Intent();
			intent.putExtra("CoorStyle", 3);
			// 制定intent要启动的类
			intent.setClass(CalculateTurnAngleActivity.this,
					SelectPointActivity.class);
			// 启动一个新的Activity
			startActivityForResult(intent, 0);
		}
	}
	// 选择前视点
	private class ButtonFrontClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// 新建一个Intent
			selectPointFlag=2;
			Intent intent = new Intent();
			intent.putExtra("CoorStyle", 3);
			// 制定intent要启动的类
			intent.setClass(CalculateTurnAngleActivity.this,
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
				tvT.setText(selectPointName);
			}
			if(selectPointFlag==1){
				tvB.setText(selectPointName);
			}
			if(selectPointFlag==2){
				tvF.setText(selectPointName);
			}

		}
	}

	
}
