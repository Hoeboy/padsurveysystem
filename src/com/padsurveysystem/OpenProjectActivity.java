package com.padsurveysystem;

import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class OpenProjectActivity extends Activity {
	Button btnOpen;
	Button btnCancle;
	private ListView listView;
	List<String> mData;
	String mSelectProjectName = "NO";

	// private List<String> data = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_file_openfile);
		listView = (ListView) findViewById(R.id.listview_openproject); // 工程列表
		mData = PadApplication.GetProjectList(PadApplication.ProjectName);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice, mData));
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		btnOpen = (Button) findViewById(R.id.button_openproject_open);
		btnOpen.setOnClickListener(new ButtonOpenClickEvent());

		/* 为m_ListView视图添加setOnItemClickListener监听 */
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				listView.setItemChecked(arg2, true);
				mSelectProjectName = mData.get(arg2);
			}

		});
	}

	// 打开工程
	private class ButtonOpenClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (mSelectProjectName.equals("NO")) {
				AlertDialog dialog = new AlertDialog.Builder(
						OpenProjectActivity.this).create();
				dialog.setTitle("打开工程");// 设置标题
				dialog.setMessage("请选择要打开的工程！");// 设置内容
				dialog.setButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 点击"退出"按钮之后推出程序
						dialog.cancel();
					}
				});
				// 显示对话框
				dialog.show();
				return;
			}
			PadApplication.SetProjectName(mSelectProjectName);
			
			finish();
		}
	}

}
