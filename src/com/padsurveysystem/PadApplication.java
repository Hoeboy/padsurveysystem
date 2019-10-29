package com.padsurveysystem;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import Jama.Matrix;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

public class PadApplication {
	private static Context mContext;
	private static MyDataBaseAdapter mDataBaseAdapter;

	public static String ProjectName = null;// 默认打开的工程名称
	public static String LastCode = "";// 最后使用的代码

	// 当前使用的坐标系统
	public static CoordinateSystem CurrentCoordinateSystem = new CoordinateSystem();
	
	// 是否使用工地校正，0-不使用，1-使用
	public static int UseCoordinateTransfomation = 0;
	//当前使用的七参数
	public static CoordTransSevenParam CoordinateTrans = new CoordTransSevenParam();
	// 数据点显示的坐标类型
	public static int PointViewFormat = 0;
	//放样点的数据显示类型
	public static int StakingoutPointViewFormat=0;

	// 第三方地图文件名
	public static String AnotherMapFileName = "";

	/* 构造函数-取得Context */
	public static void Initialize(Context context) {
		mContext = context;
		LoadStatus();
		// 初始化系统信息类
		if (PadApplication.ProjectName == null) {
			PadApplication.ProjectName = "Default";
		}
		// 读取配置文件，打开默认的工程
		/* 构造MyDataBaseAdapter对象 */
		mDataBaseAdapter = new MyDataBaseAdapter(context);
		/* 取得数据库对象 */
		MyDataBaseAdapter.open();
		// 初始化数据表
		SetProjectName(ProjectName);
		// 检查系统是否插入的SD卡，如果不存在则提示
		// 检查系统文件夹是否存在，如果不存在则自动创建
		CreateSDCardDir();
		// 初始化坐标系统、七参数
		GetCoordinateSystem();
		// 初始化工程信息：是否使用七参数数据点显示的坐标类型,放样点坐标显示类型
		GetProjectInformation();

	}

	// 在SD卡上创建系统数据文件夹
	public static void CreateSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String pathImport = sdcardDir.getPath() + "/PadSurveyData/Import";
			String pathExport = sdcardDir.getPath() + "/PadSurveyData/Export";
			String pathAnotherMap = sdcardDir.getPath()
					+ "/PadSurveyData/AnotherMap";
			File fImport = new File(pathImport);
			File fExport = new File(pathExport);
			File fAnotherMap = new File(pathAnotherMap);
			// 若不存在，创建目录，可以在应用启动的时候创建
			if (!fImport.exists()) {
				fImport.mkdirs();
			}
			if (!fExport.exists()) {
				fExport.mkdirs();
			}
			if (!fAnotherMap.exists()) {
				fAnotherMap.mkdirs();
			}
		} else {
			final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(
					mContext);
			builder.setTitle("PadSurvey");
			builder.setMessage("没有检测到SD卡，系统将无法导入导出数据！");
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {

				}
			});
			builder.create().show();
			return;
		}
	}

	//设置打开新的工程
	public static void SetProjectName(String projectName) {
		ProjectName = projectName;
		MyDataBaseAdapter.SetTableName(projectName);
		ReGetInformation();
		SaveStauts();
	}

	public static List<String> GetProjectList(String projectName) {
		return MyDataBaseAdapter.GetTables(projectName);
	}

	public static void DeleteProject(String projectName) {
		MyDataBaseAdapter.DeleteTable(projectName);
	}

	public static boolean CreateNewProject(String projectName,
			int ellipsoidbase, double middlelongitude, double offsetnorth,
			double offseteeast, double correctnorth, double correcteast,
			int coordinateview) {

		// 创建新工程
		if (!MyDataBaseAdapter.CreateNewTable(projectName)) {
			return false;
		}

		ProjectName = projectName;
		SaveStauts();
		MyDataBaseAdapter.SetTableName(projectName);
		ContentValues newValuesCoordinateSystem = new ContentValues();

		ReferenceEllipsoid tR=CoordinateSystem.GetReferenceEllipsoid(ellipsoidbase);
		newValuesCoordinateSystem.put("ENAME", tR.Name);
		newValuesCoordinateSystem.put("EA", tR.a());
		newValuesCoordinateSystem.put("EF", tR.f());
		newValuesCoordinateSystem.put("MIDDLELONGITUDE", SurveyMath.DMSToDEG(middlelongitude));
		newValuesCoordinateSystem.put("OFFSETNORTH", offsetnorth);
		newValuesCoordinateSystem.put("OFFSETEAST", offseteeast);
		newValuesCoordinateSystem.put("CORRECTNORTH", correctnorth);
		newValuesCoordinateSystem.put("CORRECTEAST", correcteast);

		MyDataBaseAdapter.insertData(MyDataBaseAdapter.CoordinateSystemTable,
				newValuesCoordinateSystem);

		ContentValues newValuesProjectInformation = new ContentValues();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss"); // ("yyyy-MM-dd hh:mm:ss");
		String sDate = sDateFormat.format(new java.util.Date());
		newValuesProjectInformation.put("CREATETIME", sDate);
		newValuesProjectInformation.put("USERCOORDINATETRANSFORMATION", 0);
		newValuesProjectInformation.put("POINTVIEWFORMAT", coordinateview);
		PointViewFormat =coordinateview;
		newValuesCoordinateSystem.put("STAKINGOUTPOINTVIEWFORMAT", 0);
		MyDataBaseAdapter.insertData(MyDataBaseAdapter.ProjectInformationTable,
				newValuesProjectInformation);
		ReGetInformation();
		
		return true;
		
		
	}

	/* 装载、读取数据，最后一次打开的工程 */
	public static void LoadStatus() {
		/* 构建Properties对对象 */
		Properties properties = new Properties();

		try {
			/* 开发文件 */
			FileInputStream stream = mContext.openFileInput("app.cfg");

			/* 读取文件内容 */
			properties.load(stream);
			/* 取得数据 */
			ProjectName = properties.get("projectname").toString();

		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}

	}

	/* 保存数据 ，最后一次打开的工程 */
	public static boolean SaveStauts() {
		Properties properties = new Properties();

		/* 将数据打包成Properties */
		properties.put("projectname", String.valueOf(ProjectName));
		try {
			FileOutputStream stream = mContext.openFileOutput("app.cfg",
					Context.MODE_PRIVATE);

			/* 将打包好的数据写入文件中 */
			properties.store(stream, "");
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	// 向数据表中增加点
	public static long AddPoint(String Flag, String Name, String Code,
			String X, String Y, String Z) {
		ContentValues newValues = new ContentValues();
		newValues.put("POINTFLAG", Flag);
		newValues.put("NAME", Name);
		newValues.put("CODE", Code);
		newValues.put("X", X);
		newValues.put("Y", Y);
		newValues.put("Z", Z);
		return MyDataBaseAdapter.insertData(MyDataBaseAdapter.PointTable,
				newValues);
	}

	// 修改数据表中存在的点
	public static void EditPoint(String Flag, String Name, String Code,
			String X, String Y, String Z) {
		ContentValues newValues = new ContentValues();
		newValues.put("POINTFLAG", Flag);
		newValues.put("NAME", Name);
		newValues.put("CODE", Code);
		newValues.put("X", X);
		newValues.put("Y", Y);
		newValues.put("Z", Z);
		MyDataBaseAdapter.updateDataByPointName(MyDataBaseAdapter.PointTable,
				Name, newValues);
	}

	// 删除数据表中存在的点
	public static void RemovePoint(String Name) {
		MyDataBaseAdapter.deleteDataByName(MyDataBaseAdapter.PointTable, Name);
	}

	// 通过点名查找数据点
	public static Cursor FindPointByName(String PointName)throws SQLException {
		String[] sCols = {"POINTFLAG", "NAME", "CODE", "X", "Y", "Z"};
		return MyDataBaseAdapter.fetchData(MyDataBaseAdapter.PointTable,
				"NAME", PointName, sCols);
	}

	// 获取坐标系统, 包括椭球基准和七参数
	private static void SetCoordinateSystem() {
		
		ContentValues newValuesCoordinateSystem = new ContentValues();
		newValuesCoordinateSystem.put("ENAME", CurrentCoordinateSystem.ReferenceEllipsoid.Name);
		newValuesCoordinateSystem.put("EA", CurrentCoordinateSystem.ReferenceEllipsoid.a());
		newValuesCoordinateSystem.put("EF", CurrentCoordinateSystem.ReferenceEllipsoid.f());
		newValuesCoordinateSystem.put("MIDDLELONGITUDE", SurveyMath.RadianToDEG(CurrentCoordinateSystem.MiddleLongitude));
		newValuesCoordinateSystem.put("OFFSETNORTH", CurrentCoordinateSystem.OffsetNorth);
		newValuesCoordinateSystem.put("OFFSETEAST", CurrentCoordinateSystem.OffsetEast);
		newValuesCoordinateSystem.put("CORRECTNORTH", CurrentCoordinateSystem.CorrectNorth);
		newValuesCoordinateSystem.put("CORRECTEAST", CurrentCoordinateSystem.CorrectEast);
		newValuesCoordinateSystem.put("WDX", CoordinateTrans.WDX);
		newValuesCoordinateSystem.put("WDY", CoordinateTrans.WDY);		
		newValuesCoordinateSystem.put("WDZ", CoordinateTrans.WDZ);	
		newValuesCoordinateSystem.put("WRX", CoordinateTrans.WRX);			
		newValuesCoordinateSystem.put("WRY", CoordinateTrans.WRY);	
		newValuesCoordinateSystem.put("WRZ", CoordinateTrans.WRZ);		
		newValuesCoordinateSystem.put("WK", CoordinateTrans.WK);		
		newValuesCoordinateSystem.put("CDX", CoordinateTrans.CDX);
		newValuesCoordinateSystem.put("CDY", CoordinateTrans.CDY);		
		newValuesCoordinateSystem.put("CDZ", CoordinateTrans.CDZ);	
		newValuesCoordinateSystem.put("CRX", CoordinateTrans.CRX);			
		newValuesCoordinateSystem.put("CRY", CoordinateTrans.CRY);	
		newValuesCoordinateSystem.put("CRZ", CoordinateTrans.CRZ);		
		newValuesCoordinateSystem.put("CK", CoordinateTrans.CK);
		
		MyDataBaseAdapter.updateData(MyDataBaseAdapter.CoordinateSystemTable,1, newValuesCoordinateSystem);
	}
	// 获取坐标系统, 包括椭球基准和七参数
	private static void GetCoordinateSystem() {

		String[] sCols = { "ENAME", "EA", "EF", "MIDDLELONGITUDE",
				"OFFSETNORTH", "OFFSETEAST", "CORRECTNORTH", "CORRECTEAST",
				"WDX", "WDY", "WDZ", "WRX", "WRY", "WRZ", "WK", "CDX", "CDY",
				"CDZ", "CRX", "CRY", "CRZ", "CK" };
		Cursor tCursor = MyDataBaseAdapter.fetchAllData(
				MyDataBaseAdapter.CoordinateSystemTable, sCols);
		tCursor.moveToFirst();

		CurrentCoordinateSystem.ReferenceEllipsoid=new ReferenceEllipsoid(tCursor.getString(0),
				tCursor.getDouble(1),tCursor.getDouble(2));

		CurrentCoordinateSystem.MiddleLongitude = SurveyMath.DEGToRadian(tCursor.getDouble(3));
		CurrentCoordinateSystem.OffsetNorth = tCursor.getDouble(4);
		CurrentCoordinateSystem.OffsetEast = tCursor.getDouble(5);
		CurrentCoordinateSystem.CorrectNorth = tCursor.getDouble(6);
		CurrentCoordinateSystem.CorrectEast = tCursor.getDouble(7);
		
		CoordinateTrans.setWGS84TransCartesianSevenParameter(
				tCursor.getDouble(8), tCursor.getDouble(9),
				tCursor.getDouble(10), tCursor.getDouble(11),
				tCursor.getDouble(12), tCursor.getDouble(13),
				tCursor.getDouble(14));
		CoordinateTrans.setCartesianTransWGS84SevenParameter(
				tCursor.getDouble(15), tCursor.getDouble(16),
				tCursor.getDouble(17), tCursor.getDouble(18),
				tCursor.getDouble(19), tCursor.getDouble(20),
				tCursor.getDouble(21));
	}

	// 获取工程信息
	private static void GetProjectInformation() {
		String[] sCols = { "CREATETIME", "USERCOORDINATETRANSFORMATION",
				"POINTVIEWFORMAT", "STAKINGOUTPOINTVIEWFORMAT" };
		Cursor tCursor = MyDataBaseAdapter.fetchAllData(
				MyDataBaseAdapter.ProjectInformationTable, sCols);
		tCursor.moveToFirst();
		try {
			UseCoordinateTransfomation = tCursor.getInt(1); // 是否使用工地校正
			PointViewFormat = tCursor.getInt(2); // 数据点显示的坐标类型
		} catch (Exception e) {
			UseCoordinateTransfomation = 0; // 是否使用工地校正
			PointViewFormat = 0; // 数据点显示的坐标类型
		}
		// StakingoutPointViewFormat = tCursor.getInt(3); // 放样点坐标显示类型
	}
	// 设置工程信息
	private static void SetProjectInformation() {
				
		ContentValues newValuesCoordinateSystem = new ContentValues();
		newValuesCoordinateSystem.put("USERCOORDINATETRANSFORMATION", UseCoordinateTransfomation);
		newValuesCoordinateSystem.put("POINTVIEWFORMAT", PointViewFormat);
		newValuesCoordinateSystem.put("STAKINGOUTPOINTVIEWFORMAT", StakingoutPointViewFormat);

		
		MyDataBaseAdapter.updateData(MyDataBaseAdapter.ProjectInformationTable,1, newValuesCoordinateSystem);
	}
	//重新获取各种数据
	public static void ReGetInformation(){
		GetCoordinateSystem();
		GetProjectInformation();
	}
	//重新设置各种数据
	public static void ReSetInformation(){
		SetCoordinateSystem();
		SetProjectInformation();
	}
}
