package com.padsurveysystem;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.*;

public class SetCoordTransSevenParamPointActivity extends Activity {
	int CoorStyle;

	TextView tvWGS84;
	TextView tvGrid;
	Button btnSelectWGS84;
	Button btnSelectGrid;
	Button btnAdd;
	Button btnEdit;
	Button btnRemove;
	ListView lsvCoordTrans;

	double[][] WGS84Coordinate;
	double[][] CartesianCoordinate;
	List<GeodeticCoordinatePoint> listW = new ArrayList<GeodeticCoordinatePoint>();
	List<CartesianCoordinatePoint> listC = new ArrayList<CartesianCoordinatePoint>();
	List<Integer> listIndex = new ArrayList<Integer>();
	List<String> listWName = new ArrayList<String>();
	List<String> listCName = new ArrayList<String>();
	int listPosition = -1;
	int selectListItemIndex = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("坐标转换");
		setContentView(R.layout.view_setting_coordinatetransformation_addpoint);

		tvWGS84 = (TextView) findViewById(R.id.edittext_wgs84_view_setting_coordinatetransformation_addpoint);
		tvGrid = (TextView) findViewById(R.id.edittext_grid_view_setting_coordinatetransformation_addpoint);
		btnAdd = (Button) findViewById(R.id.button_add_view_setting_coordinatetransformation_addpoint);
		btnAdd.setOnClickListener(new ButtonAddClickEvent());
		btnEdit = (Button) findViewById(R.id.button_edit_view_setting_coordinatetransformation_addpoint);
		btnEdit.setOnClickListener(new ButtonEditClickEvent());
		btnRemove = (Button) findViewById(R.id.button_remove_view_setting_coordinatetransformation_addpoint);
		btnRemove.setOnClickListener(new ButtonDeleteClickEvent());
		btnSelectWGS84 = (Button) findViewById(R.id.button_select84_view_setting_coordinatetransformation_addpoint);
		btnSelectWGS84.setOnClickListener(new buttonSelectWGS84ClickEvent());
		btnSelectGrid = (Button) findViewById(R.id.button_selectgrid_view_setting_coordinatetransformation_addpoint);
		btnSelectGrid.setOnClickListener(new buttonSelectGridClickEvent());
		lsvCoordTrans = (ListView) findViewById(R.id.listview_view_setting_coordinatetransformation_addpoint);

		CalculateSevenParam();
		inflateListView();

		lsvCoordTrans.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				selectListItemIndex = position;
			}
		});
		lsvCoordTrans.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					listPosition = lsvCoordTrans.getFirstVisiblePosition();

				}
			}

		});
	}

	// 填充点列表
	@SuppressLint("InlinedApi")
	private void inflateListView() {

		DecimalFormat df3 = new DecimalFormat("0.000"); // 创建一个格式化类f

		String[] sCols = { "WGSPOINTNAME", "GRIDPOINTNAME", "HERROR", "VERROR" };
		Cursor mCursor = MyDataBaseAdapter.fetchAllData(
				MyDataBaseAdapter.CoordinatetransTormationTable, sCols);

		List<String> strList = new ArrayList<String>();

		if (mCursor.moveToFirst() && mCursor.getCount() > 0) {
			do {
				String sItem = mCursor.getString(0) + ":"
						+ mCursor.getString(1) + " "
						+ df3.format(mCursor.getDouble(2)) + " "
						+ df3.format(mCursor.getDouble(3));
				strList.add(sItem);
			} while (mCursor.moveToNext());
		}
		lsvCoordTrans.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice, strList));
		lsvCoordTrans.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		if (listPosition > -1 && selectListItemIndex > -1) {
			lsvCoordTrans.setSelection(listPosition);
		}

	}

	// 选择WGS84点
	private class buttonSelectWGS84ClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			CoorStyle = 0;
			// 新建一个Intent
			Intent intent = new Intent();
			intent.putExtra("CoorStyle", 0);
			// 制定intent要启动的类
			intent.setClass(SetCoordTransSevenParamPointActivity.this,
					SelectPointActivity.class);
			// 启动一个新的Activity
			startActivityForResult(intent, 0);
		}
	}

	// 选择网格点
	private class buttonSelectGridClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			CoorStyle = 1;
			// 新建一个Intent
			Intent intent = new Intent();
			intent.putExtra("CoorStyle", 1);
			// 制定intent要启动的类
			intent.setClass(SetCoordTransSevenParamPointActivity.this,
					SelectPointActivity.class);
			// 启动一个新的Activity
			startActivityForResult(intent, 0);
		}
	}

	// 增加点
	private class ButtonAddClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (tvWGS84.getText().toString().trim().length() < 1
					|| tvGrid.getText().toString().trim().length() < 1) {
				Toast toast = Toast.makeText(
						SetCoordTransSevenParamPointActivity.this, "点名不能为空！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}

			Cursor cWP = PadApplication.FindPointByName(tvWGS84.getText()
					.toString().trim());
			Cursor cGP = PadApplication.FindPointByName(tvGrid.getText()
					.toString().trim());
			
			if (cWP.getCount()<1) {
				Toast toast = Toast.makeText(
						SetCoordTransSevenParamPointActivity.this, "点"
								+ tvWGS84.getText().toString().trim() + "不存在！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			if (cGP.getCount()<1) {
				Toast toast = Toast.makeText(
						SetCoordTransSevenParamPointActivity.this, "点"
								+ tvGrid.getText().toString().trim() + "不存在！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}

			ContentValues newValues = new ContentValues();
			newValues.put("WGSPOINTNAME", tvWGS84.getText().toString());
			newValues.put("GRIDPOINTNAME", tvGrid.getText().toString());
			newValues.put("HERROR", 0);
			newValues.put("VERROR", 0);

			if (btnAdd.getText().equals("增加")) {
				MyDataBaseAdapter.insertData(
						MyDataBaseAdapter.CoordinatetransTormationTable,
						newValues);
			}
			if (btnAdd.getText().equals("确定修改")) {
				MyDataBaseAdapter.updateData(
						MyDataBaseAdapter.CoordinatetransTormationTable,
						listIndex.get(selectListItemIndex), newValues);
				btnAdd.setText("增加");
				btnRemove.setEnabled(true);
			}
			tvWGS84.setText("");
			tvGrid.setText("");
			CalculateSevenParam();
			inflateListView();
		}
	}

	// 编辑点
	private class ButtonEditClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if(lsvCoordTrans.getCount()<1){
				Toast toast = Toast.makeText(
						SetCoordTransSevenParamPointActivity.this, "请选择需要编辑的点！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			if (selectListItemIndex < 0){
				Toast toast = Toast.makeText(
						SetCoordTransSevenParamPointActivity.this, "请选择需要编辑的点！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			tvWGS84.setText(listWName.get(selectListItemIndex));
			tvGrid.setText(listCName.get(selectListItemIndex));
			btnAdd.setText("确定修改");
			btnRemove.setEnabled(false);
		}
	}

	// 删除点
	private class ButtonDeleteClickEvent implements View.OnClickListener {
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			if(lsvCoordTrans.getCount()<1){
				Toast toast = Toast.makeText(
						SetCoordTransSevenParamPointActivity.this, "请选择需要删除的点！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}
			if (selectListItemIndex < 0){
				Toast toast = Toast.makeText(
						SetCoordTransSevenParamPointActivity.this, "请选择需要删除的点！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 100);
				toast.show();
				return;
			}

			AlertDialog dialogOK = new AlertDialog.Builder(
					SetCoordTransSevenParamPointActivity.this).create();
			dialogOK.setTitle("删除点");// 设置标题
			dialogOK.setMessage("确定要删除坐标转换点 "
					+ listWName.get(selectListItemIndex) + ":"
					+ listCName.get(selectListItemIndex) + " 吗?");// 设置内容
			dialogOK.setButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					// 更新列表
					MyDataBaseAdapter.deleteDataByID(
							MyDataBaseAdapter.CoordinatetransTormationTable,
							listIndex.get(selectListItemIndex));
					CalculateSevenParam();
					inflateListView();
					selectListItemIndex = -1;
				}
			});
			dialogOK.setButton2("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// 点击"退出"按钮之后推出程序
					dialog.cancel();
				}
			});
			// 显示对话框
			dialogOK.show();

		}
	}

	// 重写该方法，该方法以回调的方式来获取指定Activity返回的结果
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// 当requestCode、resultCode同时为0，也就是处理特定的结果
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (CoorStyle == 0) {
				tvWGS84.setText(intent.getStringExtra("PointName"));
			} else {
				tvGrid.setText(intent.getStringExtra("PointName"));
			}
		}
	}

	// 计算七参数
	private void CalculateSevenParam() {

		DecimalFormat df3 = new DecimalFormat("0.000"); // 创建一个格式化类f

		String sWGSName = "";
		String sGridName = "";
		String[] sCols = { "ID", "WGSPOINTNAME", "GRIDPOINTNAME", "HERROR",
				"VERROR" };
		Cursor mCursor = MyDataBaseAdapter.fetchAllData(
				MyDataBaseAdapter.CoordinatetransTormationTable, sCols);
		// 初始化各个集合
		listW.clear();
		listC.clear();
		listIndex.clear();
		listWName.clear();
		listCName.clear();
		WGS84Coordinate = new double[mCursor.getCount()][3];
		CartesianCoordinate = new double[mCursor.getCount()][3];
		if (mCursor.moveToFirst() && mCursor.getCount() > 0) {
			do {

				sWGSName = mCursor.getString(1);
				sGridName = mCursor.getString(2);
				listIndex.add(mCursor.getInt(0));
				listWName.add(sWGSName);
				listCName.add(sGridName);

				GeodeticCoordinatePoint pcg = new GeodeticCoordinatePoint();
				CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
				Cursor cWP = PadApplication.FindPointByName(sWGSName);
				Cursor cGP = PadApplication.FindPointByName(sGridName);
				SurveyAngle sA = new SurveyAngle();
				sA.valueOfDEG(cWP.getDouble(3));
				pcg.Latitude = sA.GetRadian();
				sA.valueOfDEG(cWP.getDouble(4));
				pcg.Longitude = sA.GetRadian();
				pcg.Height = cWP.getDouble(5);
				WGS84Coordinate[mCursor.getPosition()][0] = pcg.Latitude;
				WGS84Coordinate[mCursor.getPosition()][1] = pcg.Longitude;
				WGS84Coordinate[mCursor.getPosition()][2] = pcg.Height;

				pcc.X = cGP.getDouble(3);
				pcc.Y = cGP.getDouble(4);
				pcc.H = cGP.getDouble(5);
				CartesianCoordinate[mCursor.getPosition()][0] = pcc.X;
				CartesianCoordinate[mCursor.getPosition()][1] = pcc.Y;
				CartesianCoordinate[mCursor.getPosition()][2] = pcc.H;

				listW.add(pcg);
				listC.add(pcc);
			} while (mCursor.moveToNext());
			if (listW.size() < 3)
				return;// 不足三个点时退出

			// 求七参数,并写入系统信息库
			CoordTransSevenParam ct = new CoordTransSevenParam(WGS84Coordinate,
					CartesianCoordinate);
			PadApplication.CoordinateTrans = ct;
			PadApplication.ReSetInformation();
			// 求残差
			for (int i = 0; i < listW.size(); i++) {
				double dW[] = new double[3];
				double dC[] = new double[3];
				dW[0] = listW.get(i).Latitude;
				dW[1] = listW.get(i).Longitude;
				dW[2] = listW.get(i).Height;
				PadApplication.CoordinateTrans.WGS84TransCartesian(dW, dC);
				double hError = Math.sqrt((dC[0] - listC.get(i).X)
						* (dC[0] - listC.get(i).X) + (dC[1] - listC.get(i).Y)
						* (dC[1] - listC.get(i).Y));
				double vError = dC[2] - listC.get(i).H;
				ContentValues newValues = new ContentValues();
				newValues.put("HERROR", hError);
				newValues.put("VERROR", vError);
				// 更新列表
				MyDataBaseAdapter.updateData(
						MyDataBaseAdapter.CoordinatetransTormationTable,
						listIndex.get(i), newValues);
			}

		}
	}
}
