package com.padsurveysystem;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.*;

public class ViewPointActivity extends Activity {
	int CoorStyle;
	Button btnEdit;
	Button btnDelete;
	ListView listPoint;
	View itemView = null;

	List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
	String selectPointName = "";
	MyAdapter adapter;
	int listPosition = -1;
	int selectListItemIndex = -1;

	String currentView="显示大地坐标";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("查看点");
		setContentView(R.layout.view_file_viewpoint);
		String[] slCoorType = { "网格坐标", "大地坐标" };

		btnEdit = (Button) findViewById(R.id.button_viewpoint_edit);
		btnEdit.setOnClickListener(new ButtonEditClickEvent());
		btnDelete = (Button) findViewById(R.id.button_viewpoint_remove);
		btnDelete.setOnClickListener(new ButtonDeleteClickEvent());

		listPoint = (ListView) findViewById(R.id.listview_viewpoint);

		inflateListView(1);// 默认显示网格坐标

		listPoint.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {

				selectPointName = listItems.get(position)
						.get("name").toString();

				adapter.setSelectItem(position);
				selectListItemIndex = position;
				adapter.notifyDataSetInvalidated();

			}
		});
		listPoint.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					listPosition = listPoint.getFirstVisiblePosition();

				}
			}

		});
	}

	// 填充点列表
	private void inflateListView(int CoorStyle) {
		// 创建一个List集合，List集合的元素是Map
		listItems.clear();

		DecimalFormat df0 = new DecimalFormat("00"); // 创建一个格式化类f
		DecimalFormat df3 = new DecimalFormat("0.000"); // 创建一个格式化类f
		DecimalFormat df5 = new DecimalFormat("00.00000"); // 创建一个格式化类f
		String X = null,Y= null,LAT = null,LON = null,Z = null;
		SurveyAngle sA=new SurveyAngle();
		String[] sCols = {"POINTFLAG","NAME", "CODE", "X", "Y", "Z" };
		Cursor mCursor = MyDataBaseAdapter.fetchAllData(
				MyDataBaseAdapter.PointTable, sCols);

		if (mCursor.moveToFirst() && mCursor.getCount() > 0) {
			do {
				Map<String, Object> listItem = new HashMap<String, Object>();
    			listItem.put("name", mCursor.getString(1));
    			listItem.put("code", mCursor.getString(2));
    			
				int PointFlag=mCursor.getInt(0);//原始坐标风格


				//用户要求显示大地坐标时
				if (CoorStyle == 0) {

					//当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
					if (PointFlag == 1 || PointFlag == 2) {
						sA.valueOfDEG(mCursor.getDouble(3));
						LAT = String.valueOf(sA.GetSubDegree()) + "°"
								+ df0.format(sA.GetSubMinute()) + "′"
								+ sA.GetSubSecond(5) + "″";
						sA.valueOfDEG(mCursor.getDouble(4));
						LON = String.valueOf(sA.GetSubDegree()) + "°"
								+ df0.format(sA.GetSubMinute()) + "′"
								+ sA.GetSubSecond(5) + "″";
						Z = df3.format(mCursor.getDouble(5));

					}
					//当原始坐标为用户输入的原始网格坐标和计算的交点时
					if (PointFlag == 3 || PointFlag == 4) {
     				    CartesianCoordinatePoint pcc=new CartesianCoordinatePoint();
						GeodeticCoordinatePoint pcg = new GeodeticCoordinatePoint();

						pcc.X=mCursor.getDouble(3);						
						pcc.Y=mCursor.getDouble(4);
						pcc.H=mCursor.getDouble(5);
						
						//当使用存大的坐标系统时，使用坐标反算
						if(PadApplication.UseCoordinateTransfomation == 0){
							CoordinateTransform ct =new CoordinateTransform();
							CoordinateSystem cs=PadApplication.CurrentCoordinateSystem;
							ct.CartesianToGeodetic(pcg, pcc, cs);

						}
						else//使用坐标转换（点校正）
						{
							double[] dC=new double[3];
							double[] dW=new double[3];
							dC[0]=pcc.X;
							dC[1]=pcc.Y;
							dC[2]=pcc.H;
							PadApplication.CoordinateTrans.CartesianTransWGS84(dC, dW);
							pcg.Latitude=dW[0];
							pcg.Longitude=dW[1];
							pcg.Height=dW[2];
						}

						sA.valueOfRadian(pcg.Latitude);
						LAT = String.valueOf(sA.GetSubDegree()) + "°"
								+ df0.format(sA.GetSubMinute()) + "′"
								+ sA.GetSubSecond(5)+ "″";
						
						sA.valueOfRadian(pcg.Longitude);						
						LON = String.valueOf(sA.GetSubDegree()) + "°"
								+ df0.format(sA.GetSubMinute()) + "′"
								+ sA.GetSubSecond(5) + "″";
						Z = df3.format(pcg.Height);
					}
					
					listItem.put("coor", LAT + " , " + LON + " , " + Z);
				}
			    //当用户要求显示网格坐标时
				if (CoorStyle == 1) {
					//当原始坐标为测量的大地坐标和用户输入的原始大地坐标时
					if (PointFlag == 1 || PointFlag == 2) {

						CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();
						GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();
						pgc.Latitude =SurveyMath.DEGToRadian(mCursor.getDouble(3));
						pgc.Longitude = SurveyMath.DEGToRadian(mCursor.getDouble(4));
						pgc.Height = mCursor.getDouble(5);
						
						//当使用存在的坐标系统时，使用坐标正算
						if(PadApplication.UseCoordinateTransfomation ==0){
							CoordinateTransform ct =new CoordinateTransform();
							CoordinateSystem cs=PadApplication.CurrentCoordinateSystem;
							ct.GeodeticToCartesian(pcc, pgc, cs);
						}
						else//使用坐标转换（点校正）
						{
							double[] dC=new double[3];
							double[] dW=new double[3];
							dW[0]=pgc.Latitude;
							dW[1]=pgc.Longitude;
							dW[2]=pgc.Height;
							PadApplication.CoordinateTrans.WGS84TransCartesian(dW, dC);
							pcc.X=dC[0];
							pcc.Y=dC[1];
							pcc.H=dC[2];
						}
						
						X = df3.format(pcc.X);
						Y = df3.format(pcc.Y);
						Z = df3.format(pcc.H);
					}
					//当原始坐标为用户输入的原始网格坐标和计算的交点时
					if (PointFlag == 3 || PointFlag == 4) {
						//坐标反算
						X= df3.format(mCursor.getDouble(3));
						
						Y= df3.format(mCursor.getDouble(4));

						Z = df3.format(mCursor.getDouble(5));
					}
					listItem.put("coor", X + " , " + Y + " , " + Z);
				}
				// 添加List项
				listItems.add(listItem);

			} while (mCursor.moveToNext());

		}
		adapter = new MyAdapter(this);
		listPoint.setAdapter(adapter);
		if (listPosition > -1 && selectListItemIndex > -1) {
			listPoint.setSelection(listPosition);
			adapter.setSelectItem(selectListItemIndex);
			adapter.notifyDataSetInvalidated();

		}

	}

	public final class ViewHolder {
		public TextView lName;
		public TextView lCode;
		public TextView lCoor;
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listItems.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.item_viewpoint_listview, null);
				holder.lName = (TextView) convertView
						.findViewById(R.id.textview_viewpoint_name);
				holder.lCode = (TextView) convertView
						.findViewById(R.id.textview_viewpoint_code);
				holder.lCoor = (TextView) convertView
						.findViewById(R.id.textview_viewpoint_coor);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.lName.setText((String) listItems.get(position).get("name"));
			holder.lCode.setText((String) listItems.get(position).get("code"));
			holder.lCoor.setText((String) listItems.get(position).get("coor"));

			if (position == selectItem) {
				convertView.setBackgroundColor(Color.rgb(255, 165, 0));
				holder.lName.setTextColor(Color.BLACK);
				holder.lCode.setTextColor(Color.BLACK);
				holder.lCoor.setTextColor(Color.BLACK);
			} else {
				holder.lName.setTextColor(Color.WHITE);
				holder.lCode.setTextColor(Color.WHITE);
				holder.lCoor.setTextColor(Color.WHITE);
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}

			// convertView.getBackground().setAlpha(80);

			return convertView;
		}

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		private int selectItem = -1;
	}

	// 编辑点
	private class ButtonEditClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (selectListItemIndex == -1 ) return;
			// 新建一个Intent
			Intent intent = new Intent();
			intent.putExtra("PointName", selectPointName);
			// 制定intent要启动的类
			intent.setClass(ViewPointActivity.this, EditPointActivity.class);
			// 启动一个新的Activity
			startActivityForResult(intent, 0);

		}
	}

	// 删除点
	private class ButtonDeleteClickEvent implements View.OnClickListener {
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			if (selectListItemIndex<0)
				return;

			AlertDialog dialogOK = new AlertDialog.Builder(
					ViewPointActivity.this).create();
			dialogOK.setTitle("删除点");// 设置标题
			dialogOK.setMessage("确定要删除点 " + selectPointName + " 吗?");// 设置内容
			dialogOK.setButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// 点击"确定"按钮之后删除点
					PadApplication.RemovePoint(selectPointName);
					// 如果是最后一条数据
					if (selectListItemIndex == listItems.size() - 1) {
						if (listItems.size() == 1) {
							selectListItemIndex = -1;
						}
						if (listItems.size() > 1) {
							selectListItemIndex = selectListItemIndex - 1;
						}
					}
					// 如果是第一条数据
					if (selectListItemIndex == 0) {
						if (listItems.size() == 1) {
							selectListItemIndex = -1;
						}
						if (listItems.size() > 1) {
							selectListItemIndex = 0;
						}
					}
					inflateListView(CoorStyle);
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
		if (requestCode == 0 && resultCode == 0) {
			inflateListView(CoorStyle);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, currentView);	
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);  
        menu.clear();  
        menu.add(0, 1, 1,currentView);  
        return super.onPrepareOptionsMenu(menu);  
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == 1) {
			// 显示设置视图			
			if (item.getTitle().equals("显示大地坐标")) {
				currentView="显示网格坐标";
				CoorStyle = 0;

			} else {
				currentView="显示大地坐标";
				CoorStyle = 1;
			}
			inflateListView(CoorStyle);
		}

		return super.onMenuItemSelected(featureId, item);
	}

}
