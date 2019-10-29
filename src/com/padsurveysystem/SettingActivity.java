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
public class SettingActivity extends Activity {
	private GridView gridView;
	// 图片的文字标题
	private String[] titles = new String[] { "坐标系统", "坐标转换", "第三方地图" };
	// 图片ID数组
	private int[] images = new int[] { R.drawable.setting_cs,
			R.drawable.setting_ct, R.drawable.setting_map };

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
					intent.setClass(SettingActivity.this,
							SetCoordinateSystemActivity.class);
					// 启动一个新的Activity
					startActivity(intent);

				}
				if (position == 1) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(SettingActivity.this,
							SetCoordTransSevenParamPointActivity.class);
					// 启动一个新的Activity
					startActivity(intent);

				}
				if (position == 2) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(SettingActivity.this,
							SetAnotherMapActivity.class);
					// 启动一个新的Activity
					startActivity(intent);

				}
			}
		});
	}
}
