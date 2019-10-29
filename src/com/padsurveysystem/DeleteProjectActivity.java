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

public class DeleteProjectActivity extends Activity {
	Button btnDelete;
	Button btnCancle;
	private ListView listView;
	List<String> mData;
	String mDeleteProjectName = "NO";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_file_deletefile);
		listView = (ListView) findViewById(R.id.listview_deleteproject); // 工程列表
		mData = PadApplication.GetProjectList(PadApplication.ProjectName);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice, mData));
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		btnDelete = (Button) findViewById(R.id.button_deleteproject_delete);
		btnDelete.setOnClickListener(new ButtonDeleteClickEvent());

		/* 为m_ListView视图添加setOnItemClickListener监听 */
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				listView.setItemChecked(arg2, true);
				mDeleteProjectName = mData.get(arg2);
			}

		});
	}

	// 删除工程
	private class ButtonDeleteClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (mDeleteProjectName.equals("NO")) {
				AlertDialog dialog = new AlertDialog.Builder(
						DeleteProjectActivity.this).create();
				dialog.setTitle("删除工程");// 设置标题
				dialog.setMessage("请选择要删除的工程！");// 设置内容
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
			AlertDialog dialogOK = new AlertDialog.Builder(
					DeleteProjectActivity.this).create();
			dialogOK.setTitle("删除工程");// 设置标题
			dialogOK.setMessage("确定要删除工程 " + mDeleteProjectName + " 吗?");// 设置内容
			dialogOK.setButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// 点击"确定"按钮之后删除工程
					PadApplication.DeleteProject(mDeleteProjectName);
					mData = PadApplication
							.GetProjectList(PadApplication.ProjectName);
					listView.setAdapter(new ArrayAdapter<String>(
							DeleteProjectActivity.this,
							android.R.layout.simple_list_item_single_choice,
							mData));
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

}
