package com.padsurveysystem;

import java.util.*;

import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.widget.AdapterView.*;

public class AddCoordTransSevenParamPointActivity extends Activity {

	String m_Name = "1";
	String m_Code = "";
	String m_X = "0";
	String m_Y = "0";
	String m_Z = "0";

	Button btnSave;

	TextView lableX;
	TextView lableY;

	TextView txtName;
	TextView txtCode;
	TextView txtX;
	TextView txtY;
	TextView txtZ;

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
		setTitle("增加校正点");
		setContentView(R.layout.view_file_addpoint);
		btnSave = (Button) findViewById(R.id.button_addpoint_save);
		btnSave.setOnClickListener(new ButtonAddClickEvent());

		txtName = (TextView) findViewById(R.id.edittext_addpoint_name);
		txtCode = (TextView) findViewById(R.id.edittext_addpoint_code);
		txtX = (TextView) findViewById(R.id.edittext_addpoint_x);
		txtY = (TextView) findViewById(R.id.edittext_addpoint_y);
		txtZ = (TextView) findViewById(R.id.edittext_addpoint_z);
	}

	// 导入数据
	private class ButtonAddClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {

		}
	}

	// 导入数据点文件
	public void AddPoint(String CoorStyle) {

		if (txtName.getText().length() < 1) {
			Toast.makeText(this, "点名不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!CheckEmptyPointCoor()) {
			Toast.makeText(this, "坐标值不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (CoorStyle == "2") {
			KEY_NAME = txtName.getText().toString();
			KEY_CODE = txtCode.getText().toString();
			KEY_X = txtX.getText().toString();
			KEY_Y = txtY.getText().toString();
			KEY_Z = txtZ.getText().toString();
			long lP = PadApplication.AddPoint(CoorStyle, KEY_NAME, KEY_CODE,KEY_X, KEY_Y, KEY_Z);
			if (lP < 0) {
				Toast.makeText(this, "点" + KEY_NAME + "已经存在",
						Toast.LENGTH_SHORT).show();
			} else {
				SetNextPointName();

			}
		}
		if (CoorStyle == "3") {
			KEY_NAME = txtName.getText().toString();
			KEY_CODE = txtCode.getText().toString();
			KEY_X = txtX.getText().toString();
			KEY_Y = txtY.getText().toString();
			KEY_Z = txtZ.getText().toString();
			long lP = PadApplication.AddPoint(CoorStyle, KEY_NAME, KEY_CODE,
					KEY_X, KEY_Y, KEY_Z);
			if (lP < 0) {
				Toast.makeText(this, "点" + KEY_NAME + "已经存在",
						Toast.LENGTH_SHORT).show();
			} else {
				SetNextPointName();
			}
		}
	}

	// 检查用房输入的坐标是否合法
	private boolean CheckEmptyPointCoor() {
		if (txtX.getText().length() < 1 || txtY.getText().length() < 1
				|| txtZ.getText().length() < 1) {
			return false;
		}
		return true;
	}

	// 自动设置点名
	private void SetNextPointName() {
		// 如果最后一位不是数字
		if ((int) (KEY_NAME.charAt(KEY_NAME.length() - 1)) < 48
				|| (int) (KEY_NAME.charAt(KEY_NAME.length() - 1)) > 57) {
			txtName.setText(KEY_NAME + "1");
		} else// 如果是数字
		{
			for (int i = KEY_NAME.length() - 1; i > -1; i--) {
				if (((int) KEY_NAME.charAt(i)) < 48
						|| ((int) KEY_NAME.charAt(i)) > 57) {
					int tNumber = Integer.valueOf(KEY_NAME.substring(i + 1)) + 1;
					txtName.setText(KEY_NAME.substring(0, i + 1) + tNumber);
					break;
				}
				if (i == 0) {

					int tNumber = Integer.valueOf(KEY_NAME) + 1;
					txtName.setText(String.valueOf(tNumber));
				}
			}
		}
		txtX.setText("");
		txtY.setText("");
		txtZ.setText("");
	}
}
