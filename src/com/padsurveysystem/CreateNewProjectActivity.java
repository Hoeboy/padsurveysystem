package com.padsurveysystem;

import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class CreateNewProjectActivity extends Activity {
	String mNewProjectName;
	String mEllipsoidbase;
	String mMiddleLongitulate;
	String mOffsetEast;
	String mOffsetNorth;

	Button btnSave;

	TextView txtNewProjectName;
	TextView txtEllipsoidbase;
	TextView txtMiddleLongitulate;
	TextView txtOffsetEast;
	TextView txtOffsetNorth;
	TextView txtCorrectEast;
	TextView txtCorrectNorth;
	Spinner spnCS;
	Spinner spnCV;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_file_newfile);
		setTitle("新建工程");
		String[] slCoordinateSystem = { "WGS84坐标系", "1954年北京坐标系", "1980西安坐标系",
				"国家2000坐标系" };
		ArrayAdapter<String> spAdapter;
		spAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, slCoordinateSystem);
		spAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnCS = (Spinner) findViewById(R.id.spinner_newprojectcs);
		spnCS.setAdapter(spAdapter);

		String[] slCoordinateView = { "大地坐标", "网格坐标" };
		ArrayAdapter<String> spAdapterSV;
		spAdapterSV = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, slCoordinateView);
		spAdapterSV
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnCV = (Spinner) findViewById(R.id.Spinner_coordinateview);
		spnCV.setAdapter(spAdapterSV);

		SimpleDateFormat sDateFormat = new SimpleDateFormat("MMddhhmmss"); // ("yyyy-MM-dd hh:mm:ss");
		String sDate = sDateFormat.format(new java.util.Date());
		
		txtNewProjectName = (TextView) findViewById(R.id.edittext_newproject_name);
		txtNewProjectName.setText(sDate);
		txtMiddleLongitulate = (TextView) findViewById(R.id.edittext_newproject_middlelongitulate);
		txtOffsetEast = (TextView) findViewById(R.id.edittext_newproject_offseteast);
		txtOffsetNorth = (TextView) findViewById(R.id.edittext_newproject_offsetnorth);
		txtCorrectEast = (TextView) findViewById(R.id.edittext_newproject_correcteast);
		txtCorrectNorth = (TextView) findViewById(R.id.edittext_newproject_correctnorth);
		
		btnSave = (Button) findViewById(R.id.button_newproject_save);
		btnSave.setOnClickListener(new ButtonSaveClickEvent());
	}

	// 保存新建工程 的数据
	private class ButtonSaveClickEvent implements View.OnClickListener {
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {

			String sProjectName=txtNewProjectName.getText().toString();
			int iBase=spnCS.getSelectedItemPosition();
			double dMline=Double.valueOf(txtMiddleLongitulate.getText().toString());

			double dON=Double.valueOf(txtOffsetNorth.getText().toString());
			double dOE=Double.valueOf(txtOffsetEast.getText().toString());
			double dCE=Double.valueOf(txtCorrectEast.getText().toString());
			double dCN=Double.valueOf(txtCorrectNorth.getText().toString());
			int iCoorFlag=spnCV.getSelectedItemPosition();
			

			
			boolean bCreat = PadApplication.CreateNewProject(sProjectName, iBase,dMline, dON,dOE,dCN,dCE,iCoorFlag);

			if (!bCreat) {
				AlertDialog dialog = new AlertDialog.Builder(
						CreateNewProjectActivity.this).create();
				dialog.setTitle("新建工程");// 设置标题
				dialog.setMessage("工程" + txtNewProjectName.getText().toString()
						+ "已存在！");// 设置内容
				dialog.setButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 点击"退出"按钮之后推出程序
						dialog.cancel();
					}
				});
				// 显示对话框
				dialog.show();
				return;
			} else {
				finish();
				Toast toast = Toast.makeText(CreateNewProjectActivity.this,
						"新建工程成功！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 100);
				toast.show();
			}

		}
	}

}
