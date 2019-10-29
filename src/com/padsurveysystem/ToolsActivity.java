package com.padsurveysystem;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//文件操活动
public class ToolsActivity extends Activity {
	private GridView gridView;
	// 图片的文字标题
	private String[] titles = new String[] { "距离计算", "交点计算", "转角计算", "里程偏距线高",
			"三角高程", "坐标正算", "坐标反算" };
	// 图片ID数组
	private int[] images = new int[] { R.drawable.tools_distance,
			R.drawable.tools_point, R.drawable.tools_stationoffset,
			R.drawable.tools_changezone, R.drawable.tools_height,
			R.drawable.tools_gtoc, R.drawable.tools_ctog };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_gridview);
		gridView = (GridView) findViewById(R.id.tab_gridview); // 九宫格GRIDVIEW
		PictureAdapter adapter = new PictureAdapter(titles, images, this); // 自定义适配器
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (position == 0) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(ToolsActivity.this,
							CalculateDistanceActivity.class);
					// 启动一个新的Activity
					startActivity(intent);

				}
				if (position == 1) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(ToolsActivity.this,
							CalculatePointActivity.class);
					// 启动一个新的Activity
					startActivity(intent);

				}
				if (position == 2) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(ToolsActivity.this,
							CalculateTurnAngleActivity.class);
					// 启动一个新的Activity
					startActivity(intent);

				}
				// 启动里程偏距线高计算界面
				if (position == 3) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(ToolsActivity.this,
							CalculateHighActivity.class);
					// 启动一个新的Activity
					startActivity(intent);

				}
				// 启动三角高程计算界面
				if (position == 4) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(ToolsActivity.this,
							CalculatetrigonometriclevelingActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				// 启动 坐标正算界面
				if (position == 5) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(ToolsActivity.this,
							GeodeticToCartesianActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				// 启动坐标反算界面
				if (position == 6) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(ToolsActivity.this,
							CartesianToGeodeticActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
			}
		});
	}
}
