package com.padsurveysystem;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
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

//文件操作活动
public class FileActivity extends Activity {
	ProgressDialog progressDialog;
	private GridView gridView;
	// 图片的文字标题
	private String[] titles = new String[] { "新建", "打开", "删除", "导入", "导出",
			"键入点", "查看数据" };
	// 图片ID数组
	private int[] images = new int[] { R.drawable.file_new,
			R.drawable.file_open, R.drawable.file_delete,
			R.drawable.file_import, R.drawable.file_export,
			R.drawable.file_inputpoint, R.drawable.file_viewpoint };

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
				if (position == 0)// 新建工程
				{
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(FileActivity.this,
							CreateNewProjectActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				if (position == 1)// 打开工程
				{
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(FileActivity.this,
							OpenProjectActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				if (position == 2)// 删除工程
				{
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(FileActivity.this,
							DeleteProjectActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				if (position == 3)// 导入点
				{
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(FileActivity.this,
							ImportPointActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				if (position == 4)// 导出点
				{				
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(FileActivity.this,
							ExportPointActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				if (position == 5)// 键入新点
				{
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(FileActivity.this, AddPointActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				if (position == 6)// 查看点
				{
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(FileActivity.this, ViewPointActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
			}
		});
	}

}
