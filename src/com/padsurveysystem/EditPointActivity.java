package com.padsurveysystem;

import java.text.DecimalFormat;
import java.util.*;

import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import android.widget.AdapterView.*;

public class EditPointActivity extends Activity {


	Button btnSave;

	TextView lableX;
	TextView lableY;
	TextView lableCoorStyle;
	TextView lableName;

	TextView txtCode;
	TextView txtX;
	TextView txtY;
	TextView txtZ;

	double X,Y,Z;
	// 字段点类型
	String KEY_POINTFLAG = "POINTFLAG";
	// 字段点名
	String KEY_NAME = "";
	// 字段代码
	String KEY_CODE = "";
	// 字段X
	String KEY_X = "";
	// 字段Y
	String KEY_Y = "";
	// 字段海 跋高
	String KEY_Z = "";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("编辑点");
		setContentView(R.layout.view_file_editpoint);
		DecimalFormat df9 = new DecimalFormat("0.0000000000"); // 创建一个格式化类f
		DecimalFormat df3 = new DecimalFormat("0.000"); // 创建一个格式化类f
		KEY_NAME = getIntent().getStringExtra("PointName");
		Cursor cP = PadApplication
				.FindPointByName(KEY_NAME);
		KEY_POINTFLAG = cP.getString(0);
		KEY_CODE = cP.getString(2);
		X = cP.getDouble(3);
		Y = cP.getDouble(4);
		Z = cP.getDouble(5);

		btnSave = (Button) findViewById(R.id.button_addpoint_save);
		btnSave.setOnClickListener(new ButtonSaveClickEvent());

		lableCoorStyle = (TextView) findViewById(R.id.TextView_editpoint_coorstyle);

		lableName = (TextView) findViewById(R.id.textView_editpoint_name);
		lableX = (TextView) findViewById(R.id.textview_editpoint_x);
		lableY = (TextView) findViewById(R.id.textview_editpoint_y);

		txtCode = (TextView) findViewById(R.id.edittext_editpoint_code);
		txtX = (TextView) findViewById(R.id.edittext_editpoint_x);
		txtY = (TextView) findViewById(R.id.edittext_editpoint_y);
		txtZ = (TextView) findViewById(R.id.edittext_editpoint_z);

		lableName.setText("点名：" + KEY_NAME);
		txtCode.setText(KEY_CODE);
		if (KEY_POINTFLAG.equals("1")) {
			lableCoorStyle.setText("坐标类型：GPS原始测量数据");
			lableX.setText("纬度：");
			lableY.setText("经度：");

			txtX.setText(df9.format(SurveyMath.DEGToDMS(X)));
			txtY.setText(df9.format(SurveyMath.DEGToDMS(Y)));
			txtZ.setText(df3.format(Z));
			txtX.setEnabled(false);
			txtY.setEnabled(false);
			txtZ.setEnabled(false);
		}
		if (KEY_POINTFLAG.equals("2")) {
			lableCoorStyle.setText("坐标类型：用户输入的大地坐标");
			lableX.setText("纬度：");
			lableY.setText("经度：");
			txtX.setText(df9.format(SurveyMath.DEGToDMS(X)));
			txtY.setText(df9.format(SurveyMath.DEGToDMS(Y)));
			txtZ.setText(df3.format(Z));
		}
		if (KEY_POINTFLAG.equals("3")) {
			lableCoorStyle.setText("坐标类型：用户输入的网格坐标");
			lableX.setText("X：");
			lableY.setText("Y：");
			txtX.setText(df3.format(X));
			txtY.setText(df3.format(Y));
			txtZ.setText(df3.format(Z));

		}

	}

	// 导入数据
	private class ButtonSaveClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (!CheckEmptyPointCoor()) {
				Toast.makeText(EditPointActivity.this, "坐标值不能为空!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			EditPoint(KEY_POINTFLAG);
			finish();
		}
	}

	// 保存修改后的数据点
	public void EditPoint(String CoorStyle) {

		if (CoorStyle.equals("1") || CoorStyle.equals("2")) {
			KEY_CODE = txtCode.getText().toString();
			KEY_X = String.valueOf(SurveyMath.DMSToDEG(Double.valueOf(txtX.getText().toString())));
			KEY_Y = String.valueOf(SurveyMath.DMSToDEG(Double.valueOf(txtY.getText().toString())));
			KEY_Z = txtZ.getText().toString();
		}
		if (CoorStyle.equals("3") || CoorStyle.equals("4")) {
			KEY_CODE = txtCode.getText().toString();
			KEY_X = txtX.getText().toString();
			KEY_Y = txtY.getText().toString();
			KEY_Z = txtZ.getText().toString();
		}
		PadApplication.EditPoint(CoorStyle, KEY_NAME, KEY_CODE, KEY_X, KEY_Y,KEY_Z);
	}

	// 检查用户输入的坐标是否合法
	private boolean CheckEmptyPointCoor() {
		if (txtX.getText().length() < 1 || txtY.getText().length() < 1
				|| txtZ.getText().length() < 1) {
			return false;
		}
		return true;
	}

}
