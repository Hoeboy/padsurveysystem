package com.padsurveysystem;

import java.text.DecimalFormat;
import java.util.*;

import android.view.Gravity;
import android.view.View;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.*;
import android.widget.AdapterView.*;

public class CalculatetrigonometriclevelingActivity extends Activity {
	Button btnCalculate;
	TextView txtC;
	TextView txtR;
	TextView txtT;
	TextView txtP;
	TextView txtSD;
	TextView txtV;
	TextView txtVI;
	TextView txtHI;
	TextView txtHD;
	TextView txtVD;

	CheckBox chkTps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_tools_calculatetrigonometricleveling);
		setTitle("三角高程计算");
		btnCalculate = (Button) findViewById(R.id.button_calculateTtrigle_calculate);
		btnCalculate.setOnClickListener(new ButtonCalculateClickEvent());

		txtC = (TextView) findViewById(R.id.edittext_calculateTtrigle_c);
		txtR = (TextView) findViewById(R.id.edittext_calculateTtrigle_r);
		txtT = (TextView) findViewById(R.id.edittext_calculateTtrigle_t);
		txtP = (TextView) findViewById(R.id.edittext_calculateTtrigle_p);
		txtSD = (TextView) findViewById(R.id.edittext_calculateTtrigle_sd);
		txtV = (TextView) findViewById(R.id.edittext_calculateTtrigle_v);
		txtHI = (TextView) findViewById(R.id.edittext_calculateTtrigle_hi);
		txtVI = (TextView) findViewById(R.id.edittext_calculateTtrigle_vi);

		txtHD = (TextView) findViewById(R.id.textview_calculateTtrigle_hd);
		txtVD = (TextView) findViewById(R.id.textview_calculateTtrigle_vd);

		chkTps = (CheckBox) findViewById(R.id.checkbox_calculateTtrigle_tps);

	}

	// 计算平距高差
	private class ButtonCalculateClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				if (chkTps.isChecked()) {
					CalculateTps();
				} else {
					CalculateNormal();
				}
			} catch (NumberFormatException n) {
				final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(
						CalculatetrigonometriclevelingActivity.this);
				builder.setTitle("PadSurvey");
				builder.setMessage("请检查输入的数据是否正确！");
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});
				builder.create().show();

			}

		}
	}

	private void CalculateNormal() {
		double dC, dR, dSD, dV, dHI, dVI, dHD, dVD;
		double dS;
		dC = Double.valueOf(txtC.getText().toString());
		dR = Double.valueOf(txtR.getText().toString());
		dSD = Double.valueOf(txtSD.getText().toString());
		dV = Double.valueOf(txtV.getText().toString());
		dHI = Double.valueOf(txtHI.getText().toString());
		dVI = Double.valueOf(txtVI.getText().toString());

		dS = dSD + dC * 0.001 + dR * 0.000001 * dSD;
		dHD = dS
				* Math.sin(SurveyMath.DMSToRadian(dV)
						+ (0.87 * 206265 / 3600 / (2 * 6371000) * dS * Math
								.sin(SurveyMath.DMSToRadian(dV))) * Math.PI
						/ 180);
		dVD = dS * Math.cos(SurveyMath.DMSToRadian(dV)) + dHI - dVI + dS * dS
				* 0.00000006831;

		DecimalFormat df = new DecimalFormat("0.000"); // 创建一个格式化类f

		txtHD.setText("平距：" + df.format(dHD));
		txtVD.setText("高差：" + df.format(dVD));

	}

	private void CalculateTps() {
		double dC, dR, dT, dP, dSD, dV, dHI, dVI, dHD, dVD;
		double dS;
		dC = Double.valueOf(txtC.getText().toString());
		dR = Double.valueOf(txtR.getText().toString());
		dT = Double.valueOf(txtT.getText().toString());
		dP = Double.valueOf(txtP.getText().toString());
		dSD = Double.valueOf(txtSD.getText().toString());
		dV = Double.valueOf(txtV.getText().toString());
		dHI = Double.valueOf(txtHI.getText().toString());
		dVI = Double.valueOf(txtVI.getText().toString());

		dS = dSD + dC * 0.001 + (dR + (282 - (0.29 * dP) / (1 + 0.0037 * dT)))
				* 0.000001 * dSD;
		dHD = dS
				* Math.sin(SurveyMath.DMSToRadian(dV)
						+ (0.87 * 206265 / 3600 / (2 * 6371000) * dS * Math
								.sin(SurveyMath.DMSToRadian(dV))) * Math.PI
						/ 180);
		dVD = dS * Math.cos(SurveyMath.DMSToRadian(dV)) + dHI - dVI + dS * dS
				* 0.00000006831;

		DecimalFormat df = new DecimalFormat("0.000"); // 创建一个格式化类f

		txtHD.setText("平距：" + df.format(dHD));
		txtVD.setText("高差：" + df.format(dVD));
	}

}
