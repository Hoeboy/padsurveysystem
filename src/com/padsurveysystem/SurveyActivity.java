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
public class SurveyActivity extends Activity {
	private GridView gridView;
	// 图片的文字标题
	private String[] titles = new String[] { "位置", "放样点", "放样线" };
	// 图片ID数组
	private int[] images = new int[] { R.drawable.survey_position,
			R.drawable.survey_stakingoutpoint, R.drawable.survey_stakingoutline };

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
					intent.setClass(SurveyActivity.this,
							SurveyPositionActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				if (position == 1) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(SurveyActivity.this,
							StakingoutPointActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
				if (position == 2) {
					// 新建一个Intent
					Intent intent = new Intent();
					// 制定intent要启动的类
					intent.setClass(SurveyActivity.this,
							SelectStakingoutLineActivity.class);
					// 启动一个新的Activity
					startActivity(intent);
				}
			}
		});
	}
}
