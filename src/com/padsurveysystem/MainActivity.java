package com.padsurveysystem;

import java.text.SimpleDateFormat;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	// 坐标数据库操作

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_main);// 这里使用了上面创建的xml文件（Tab页面的布局）
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabSpec spec;
		Intent intent; // Reusable Intent for each tab
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd"); // ("yyyy-MM-dd hh:mm:ss");
		String sDate = sDateFormat.format(new java.util.Date());
		// 初始化系统信息类
		PadApplication.Initialize(this);
		PadApplication.ReGetInformation();

		setTitle("平板测量系统 - " + PadApplication.ProjectName);

		// 第一个TAB
		intent = new Intent(this, FileActivity.class);// 新建一个Intent用作Tab1显示的内容
		spec = tabHost
				.newTabSpec("tab1")
				// 新建一个 Tab
				.setIndicator("文件",
						getResources().getDrawable(R.drawable.tab_file))// 设置名称以及图标
				.setContent(intent);// 设置显示的intent，这里的参数也可以是R.id.xxx
		tabHost.addTab(spec);// 添加进tabHost

		// 第二个TAB
		intent = new Intent(this, SurveyActivity.class);// 新建一个Intent用作Tab1显示的内容
		spec = tabHost
				.newTabSpec("tab2")
				// 新建一个 Tab
				.setIndicator("测量",
						getResources().getDrawable(R.drawable.tab_survey))// 设置名称以及图标
				.setContent(intent);// 设置显示的intent，这里的参数也可以是R.id.xxx
		tabHost.addTab(spec);// 添加进tabHost

		// 第三个TAB
		intent = new Intent(this, ToolsActivity.class);// 新建一个Intent用作Tab1显示的内容
		spec = tabHost
				.newTabSpec("tab3")
				// 新建一个 Tab
				.setIndicator("工具",
						getResources().getDrawable(R.drawable.tab_tools))// 设置名称以及图标
				.setContent(intent);// 设置显示的intent，这里的参数也可以是R.id.xxx
		tabHost.addTab(spec);// 添加进tabHost

		// 第四个TAB
		intent = new Intent(this, SettingActivity.class);// 新建一个Intent用作Tab1显示的内容
		spec = tabHost
				.newTabSpec("tab4")
				// 新建一个 Tab
				.setIndicator("设置",
						getResources().getDrawable(R.drawable.tab_setting))// 设置名称以及图标
				.setContent(intent);// 设置显示的intent，这里的参数也可以是R.id.xxx
		tabHost.addTab(spec);// 添加进tabHost
		// 第一个TAB置为当前
		tabHost.setCurrentTab(0);
	}

	public void onResume() {
		super.onResume();
		setTitle("平板测量系统 - " + PadApplication.ProjectName);

	}
}
