package com.padsurveysystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import android.R.string;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.AdapterView.*;

public class SetCoordinateSystemActivity extends Activity {
	
	Button btnSetTrans;
	Button btnSave;

	TextView txtMiddleLongitulate;
	TextView txtOffsetEast;
	TextView txtOffsetNorth;
	TextView txtCorrectEast;
	TextView txtCorrectNorth;
	
	Spinner spnCS;
	RadioButton rbnUserTrans; 
	RadioButton rbnUserSystems; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(" 坐标系统");
		setContentView(R.layout.view_setting_coordinatesystem);

		String[] slCoordinateSystem = { "WGS84坐标系", "1954年北京坐标系", "1980西安坐标系",
				"国家2000坐标系" };
		ArrayAdapter<String> spAdapter;
		spAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, slCoordinateSystem);
		spAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnCS = (Spinner) findViewById(R.id.spinner_base_view_setting_coordinatesystem);
		spnCS.setAdapter(spAdapter);

		
		txtMiddleLongitulate=(TextView) findViewById(R.id.edittext_middlelongitulate_view_setting_coordinatesystem);
		txtOffsetEast=(TextView) findViewById(R.id.edittext_offseteast_view_setting_coordinatesystem);
		txtOffsetNorth=(TextView) findViewById(R.id.edittext_offsetnorth_view_setting_coordinatesystem);
		txtCorrectEast=(TextView) findViewById(R.id.edittext_addeast_view_setting_coordinatesystem);
		txtCorrectNorth=(TextView) findViewById(R.id.edittext_addnorth_view_setting_coordinatesystem);
		//设置当前坐标系统参数
		int iEID=0;
		if(PadApplication.CurrentCoordinateSystem.ReferenceEllipsoid.Name.equals("WGS84坐标系")){
			iEID=0;
		}
		if(PadApplication.CurrentCoordinateSystem.ReferenceEllipsoid.Name.equals("1954年北京坐标系")){
			iEID=1;
		}
		if(PadApplication.CurrentCoordinateSystem.ReferenceEllipsoid.Name.equals("1980西安坐标系")){
			iEID=2;
		}
		if(PadApplication.CurrentCoordinateSystem.ReferenceEllipsoid.Name.equals("国家2000坐标系")){
			iEID=3;
		}
		
		spnCS.setSelection(iEID);
		txtMiddleLongitulate.setText( String.valueOf( SurveyMath.RadianToDMS(PadApplication.CurrentCoordinateSystem.MiddleLongitude)));
		txtOffsetEast.setText( String.valueOf( PadApplication.CurrentCoordinateSystem.OffsetEast));
		txtOffsetNorth.setText( String.valueOf( PadApplication.CurrentCoordinateSystem.OffsetNorth));
		txtCorrectEast.setText( String.valueOf( PadApplication.CurrentCoordinateSystem.CorrectEast));
		txtCorrectNorth.setText( String.valueOf( PadApplication.CurrentCoordinateSystem.CorrectNorth));
		//
		
		btnSave = (Button) findViewById(R.id.button_save_view_setting_coordinatesystem);
		btnSave.setOnClickListener(new btnSaveClickEvent());
		
		btnSetTrans=(Button) findViewById(R.id.button_settrans_view_setting_coordinatesystem);
		btnSetTrans.setOnClickListener(new btnSetTransClickEvent());
		
		rbnUserTrans = (RadioButton) findViewById(R.id.radioButton_trans_view_setting_coordinatesystem);
		rbnUserSystems = (RadioButton) findViewById(R.id.radioButton_system_view_setting_coordinatesystem);; 
		
		if(PadApplication.UseCoordinateTransfomation==1){
			rbnUserTrans.setChecked(true);
			rbnUserSystems.setChecked(false);
		}
		else{
			rbnUserSystems.setChecked(true);
			rbnUserTrans.setChecked(false);
		}
		rbnUserTrans.setOnCheckedChangeListener(new OnCheckedChangeListener() {           
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked){
				rbnUserSystems.setChecked(false);
			}			
		}
		});
		rbnUserSystems.setOnCheckedChangeListener(new OnCheckedChangeListener() {           
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked){
				rbnUserTrans.setChecked(false);
			}			
		}
		});
	}
	// 设置点校正
	private class btnSetTransClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			// 制定intent要启动的类
			intent.setClass(SetCoordinateSystemActivity.this,
					SetCoordTransSevenParamPointActivity.class);
			// 启动一个新的Activity
			startActivity(intent);

		}
	}

	// 保存坐标系统 数据
	private class btnSaveClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			PadApplication.CurrentCoordinateSystem.ReferenceEllipsoid = CoordinateSystem.GetReferenceEllipsoid((int) spnCS
					.getSelectedItemId());
			PadApplication.CurrentCoordinateSystem.MiddleLongitude =SurveyMath.DEGToRadian(Double
					.valueOf(txtMiddleLongitulate.getText().toString()));
			PadApplication.CurrentCoordinateSystem.OffsetEast = Double
					.valueOf(txtOffsetEast.getText().toString());
			PadApplication.CurrentCoordinateSystem.OffsetNorth = Double
					.valueOf(txtOffsetNorth.getText().toString());
			PadApplication.CurrentCoordinateSystem.CorrectEast = Double
					.valueOf(txtCorrectEast.getText().toString());
			PadApplication.CurrentCoordinateSystem.CorrectNorth = Double
					.valueOf(txtCorrectNorth.getText().toString());
			
			if(rbnUserTrans.isChecked()){
				PadApplication.UseCoordinateTransfomation=1;
			}
			else{
				PadApplication.UseCoordinateTransfomation=0;
			}
			
			PadApplication.ReSetInformation();
			finish();

		}
	}
}
