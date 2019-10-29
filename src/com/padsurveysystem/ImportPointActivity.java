package com.padsurveysystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.*;
import android.widget.AdapterView.*;

public class ImportPointActivity extends Activity {

	Button btnImport;
	Button btnExit;
	Spinner spnCS;
	ListView listFile;
	CheckBox chkCover;
	ProgressDialog progressDialog;

	// List<Map<String, Object>> listItems = new ArrayList<Map<String,
	// Object>>();
	List<String> mData;
	String m_Name = "1";
	String m_Code = "";
	String m_X = "0";
	String m_Y = "0";
	String m_Z = "0";
	String m_FilePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("导入点");
		setContentView(R.layout.view_file_importpoint);
		btnImport = (Button) findViewById(R.id.button_import_view_file_importpoint);
		btnImport.setOnClickListener(new ButtonImportClickEvent());

		String[] slCoorType = { "网格坐标", "大地坐标" };
		ArrayAdapter<String> spAdapter;
		spAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, slCoorType);
		spAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnCS = (Spinner) findViewById(R.id.spinner_import_view_file_importpoint);
		spnCS.setAdapter(spAdapter);

		listFile = (ListView) findViewById(R.id.listView_importpoint);
		chkCover = (CheckBox) findViewById(R.id.checkBox_importpoint);
		GetImportFile();// 获取文件列表

		// 添加Spinner事件监听
		spnCS.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 设置显示当前选择的项
				if (arg0.getSelectedItem().toString() == "网格坐标") {

				} else {

				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
		// 为ListView的列表项的单击事件绑定监听器
		listFile.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				m_FilePath = "mnt/sdcard/PadSurveyData/Import/"
						+ mData.get(position);

			}
		});
	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// 关闭ProgressDialog
			progressDialog.dismiss();
			Toast toast = Toast.makeText(ImportPointActivity.this, "导入数据完毕",
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 100);
			toast.show();

		}
	};

	// 导入数据
	private class ButtonImportClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// 显示ProgressDialog
		progressDialog = ProgressDialog.show(ImportPointActivity.this,
					"数据导入中", "请稍候......", true, false);

			// 新建线程
			new Thread() {

				@Override
				public void run() {
					Looper.prepare(); 
					// 需要花时间计算的方法
					if (spnCS.getSelectedItem().toString() == "网格坐标") {
						ImportPoint(m_FilePath, "3", chkCover.isChecked());
					} else {
						ImportPoint(m_FilePath, "2", chkCover.isChecked());
					}

					// 向handler发消息

					handler.sendEmptyMessage(0);
					Looper.loop();
				}
			}.start();
			
		}
	}

	// 获取导入数据文件列表
	private void GetImportFile() {
		File[] FileList;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String pathImport = sdcardDir.getPath() + "/PadSurveyData/Import";
			File fImport = new File(pathImport);
			if (fImport.exists()) {
				FileList = fImport.listFiles();
				inflateListView(FileList);
			}

		} else {
			final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(
					ImportPointActivity.this);
			builder.setTitle("PadSurvey");
			builder.setMessage("没有检测到SD卡，系统无法导入导出数据！");
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {

				}
			});
			builder.create().show();
			return;
		}

	}

	// 填充文件列表
	private void inflateListView(File[] files) {
		// 创建一个List集合，List集合的元素是Map
		mData = new ArrayList();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				mData.add(files[i].getName());
			}
		}
		// 为ListView设置Adapter
		listFile.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice, mData));
		listFile.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

	}


	// 导入数据点文件
	public void ImportPoint(String path, String CoorStyle, boolean CoverFlag) {
		// 字段点类型
		String KEY_POINTFLAG = CoorStyle;
		// 字段点名
		String KEY_NAME = "";
		// 字段代码
		String KEY_CODE = "";
		// 字段X
		String KEY_X = "";
		// 字段Y
		String KEY_Y = "";
		// 字段Z
		String KEY_Z = "";
		SurveyAngle sA=new SurveyAngle();
		// 打开文件
		File file = new File(path);
		// 如果path是传递过来的参数，可以做一个非目录的判断
		if (file.isDirectory()) {
			Toast.makeText(ImportPointActivity.this, "没有指定坐标文件！", 1000).show();
		} else {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					List<String> PointString;
					//原始数据为大地坐标
					if (CoorStyle == "2") {
						// 分行读取
						while ((line = buffreader.readLine()) != null) {
							PointString = StringConver.GetSubString(line, ",");
							KEY_NAME = PointString.get(0);
							KEY_CODE = PointString.get(1);
							sA.valueOfDMS(Double.valueOf(PointString.get(2)));
							KEY_X =String.valueOf(sA.GetDEG());
							sA.valueOfDMS(Double.valueOf(PointString.get(3)));
							KEY_Y = String.valueOf(sA.GetDEG());
							KEY_Z = PointString.get(4);
							long lP = PadApplication.AddPoint(CoorStyle,KEY_NAME, KEY_CODE, KEY_X,KEY_Y, KEY_Z);
							if (lP < 0 && chkCover.isChecked()) {
								PadApplication.EditPoint(CoorStyle,KEY_NAME, KEY_CODE, KEY_X,KEY_Y, KEY_Z);
							}
						}
					}
					//原始数据为网格坐标文件
					if (CoorStyle == "3") {
						// 分行读取
						while ((line = buffreader.readLine()) != null) {
							PointString = StringConver.GetSubString(line, ",");
							KEY_NAME = PointString.get(0);
							KEY_CODE = PointString.get(1);
							KEY_X = PointString.get(2);
							KEY_Y = PointString.get(3);
							KEY_Z = PointString.get(4);
							long lP = PadApplication.AddPoint(CoorStyle,KEY_NAME, KEY_CODE, KEY_X, KEY_Y,KEY_Z);
							if (lP < 0 && chkCover.isChecked()) {
								PadApplication.EditPoint(CoorStyle,KEY_NAME, KEY_CODE, KEY_X, KEY_Y,KEY_Z);
							}
						}
					}
					instream.close();
				}
			} catch (java.io.FileNotFoundException e) {
				Toast.makeText(ImportPointActivity.this, "文件不存在",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(ImportPointActivity.this, "打开文件出错",
						Toast.LENGTH_SHORT).show();
			} catch (java.lang.IndexOutOfBoundsException e) {
				Toast.makeText(ImportPointActivity.this, "输入数据格式错误",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

}
