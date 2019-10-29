package com.padsurveysystem;

import java.text.SimpleDateFormat;
import java.util.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.*;

//数据库操作类
public class MyDataBaseAdapter {
	// 工程名称
	private static String mProjectName;
	// 本地Context对象
	private static Context mContext = null;
	// 数据库名称为data
	private static final String DB_NAME = "PADSURVEY.db";
	// 数据库版本
	private static final int DB_VERSION = 1;

	// 数据库表名
	public static String AppInformationTable;// 应用程序信息表
	public static String ProjectInformationTable;// 工程信息表
	public static String PointTable;// 数据点表
	public static String CoordinateSystemTable;// 坐标系统表
	public static String CoordinatetransTormationTable;// 坐标转换表

	// 字段ID,通用字段
	public static final String KEY_ID = "ID";

	// 以下是工程信息表字段
	// 字段工程创建时间
	public static final String KEY_CREATETIME = "CREATETIME";
	// 是否使用工地校正
	public static final String KEY_USERCOORDINATETRANSFORMATION = "USERCOORDINATETRANSFORMATION";
	// 数据点显示的坐标类型
	public static final String KEY_POINTVIEWFORMAT = "POINTVIEWFORMAT";
	// 放样点坐标显示类型
	public static final String KEY_STAKINGOUTPOINTVIEWFORMAT = "STAKINGOUTPOINTVIEWFORMAT";

	// 以下是坐标系统表字段

	// 字段椭球的名称
	public static final String KEY_ENAME = "ENAME";
	// 字段椭球的长半轴
	public static final String KEY_EA = "EA";
	// 字段椭球扁率
	public static final String KEY_EF = "EF";
	// 字段中央子午线
	public static final String KEY_MIDDLELONGITUDE = "MIDDLELONGITUDE";
	// 字段北偏移
	public static final String KEY_OFFSETNORTH = "OFFSETNORTH";
	// 字段东偏移
	public static final String KEY_OFFSETEAST = "OFFSETEAST";
	// 字段北改正数
	public static final String KEY_CORRECTNORTH = "CORRECTNORTH";
	// 字段东改正数
	public static final String KEY_CORRECTEAST = "CORRECTEAST";
	// WGS84转网格七参数
	// X平移
	public static final String KEY_WDX = "WDX";
	// Y平移
	public static final String KEY_WDY = "WDY";
	// Z平移
	public static final String KEY_WDZ = "WDZ";
	// X旋转
	public static final String KEY_WRX = "WRX";
	// Y旋转
	public static final String KEY_WRY = "WRY";
	// Z旋转
	public static final String KEY_WRZ = "WRZ";
	// 比例系数
	public static final String KEY_WK = "WK";
	// 网格转WGS84七参数
	// X平移
	public static final String KEY_CDX = "CDX";
	// Y平移
	public static final String KEY_CDY = "CDY";
	// Z平移
	public static final String KEY_CDZ = "CDZ";
	// X旋转
	public static final String KEY_CRX = "CRX";
	// Y旋转
	public static final String KEY_CRY = "CRY";
	// Z旋转
	public static final String KEY_CRZ = "CRZ";
	// 比例系数
	public static final String KEY_CK = "CK";

	// 以下是数据点表字段
	// 字段点类型
	public static final String KEY_POINTFLAG = "POINTFLAG";
	// 字段点名
	public static final String KEY_NAME = "NAME";
	// 字段代码
	public static final String KEY_CODE = "CODE";
	// 字段X
	public static final String KEY_X = "X";
	// 字段Y
	public static final String KEY_Y = "Y";
	// 字段Z
	public static final String KEY_Z = "Z";

	// 以下是坐标转换表字段
	// 字段GPS点坐标点名
	public static final String KEY_WGSPOINTNAME = "WGSPOINTNAME";
	// 字段网格点坐标点名
	public static final String KEY_GRIDPOINTNAME = "GRIDPOINTNAME";
	// 字段水平残差
	public static final String KEY_HERROR = "HERROR";
	// 字段垂直残差
	public static final String KEY_VERROR = "VERROR";

	// 执行open（）打开数据库时，保存返回的数据库对象
	public static SQLiteDatabase mSQLiteDatabase = null;

	// 由SQLiteOpenHelper继承过来
	public static DatabaseHelper mDatabaseHelper = null;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		/* 构造函数-创建一个数据库 */
		DatabaseHelper(Context context) {
			// 当调用getWritableDatabase()
			// 或 getReadableDatabase()方法时
			// 则创建一个数据库
			super(context, DB_NAME, null, DB_VERSION);
		}

		/* 创建一个表 */
		// 第一次创建数据库时，创建系统默认表
		@Override
		public void onCreate(SQLiteDatabase db) {
			String TABLE_INFORMATION = "CREATE TABLE PDefault_INFORMATION"
					+ " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CREATETIME
					+ " TEXT," + KEY_USERCOORDINATETRANSFORMATION + " INTEGER,"
					+ KEY_POINTVIEWFORMAT + " INTEGER,"
					+ KEY_STAKINGOUTPOINTVIEWFORMAT + " INTEGER)";

			String TABLE_POINTTABLE = "CREATE TABLE PDefault_POINTTABLE" + " ("
					+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_POINTFLAG
					+ " INTEGER," + KEY_NAME + " TEXT UNIQUE," + KEY_CODE
					+ " TEXT," + KEY_X + " REAL," + KEY_Y + " REAL," + KEY_Z
					+ " REAL)";

			String TABLE_SYSTEMTABLE = "CREATE TABLE PDefault_SYSTEMTABLE"
					+ " ("
					+ KEY_ID
					+ " INTEGER PRIMARY KEY,"
					+ KEY_ENAME
					+ " TEXT,"
					+ KEY_EA
					+ " REAL,"
					+ KEY_EF
					+ " REAL,"
					+ KEY_MIDDLELONGITUDE
					+ " REAL,"
					+ KEY_OFFSETNORTH
					+ " REAL,"
					+ KEY_OFFSETEAST
					+ " REAL,"
					+ KEY_CORRECTNORTH
					+ " REAL,"
					+ KEY_CORRECTEAST
					+ " REAL,"
					+ KEY_WDX
					+ " REAL,"
					+ KEY_WDY
					+ " REAL,"
					+ KEY_WDZ
					+ " REAL,"
					+ KEY_WRX
					+ " REAL,"
					+ KEY_WRY
					+ " REAL,"
					+ KEY_WRZ
					+ " REAL,"
					+ KEY_WK
					+ " REAL,"
					+ KEY_CDX
					+ " REAL,"
					+ KEY_CDY
					+ " REAL,"
					+ KEY_CDZ
					+ " REAL,"
					+ KEY_CRX
					+ " REAL,"
					+ KEY_CRY
					+ " REAL,"
					+ KEY_CRZ
					+ " REAL,"
					+ KEY_CK + " REAL)";

			String TABLE_CTTABLE = "CREATE TABLE PDefault_CTTABLE" + " ("
					+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_WGSPOINTNAME
					+ " TEXT," + KEY_GRIDPOINTNAME + " TEXT," + KEY_HERROR
					+ " REAL," + KEY_VERROR + " REAL)";

			db.execSQL(TABLE_INFORMATION);
			db.execSQL(TABLE_POINTTABLE);
			db.execSQL(TABLE_SYSTEMTABLE);
			db.execSQL(TABLE_CTTABLE);

			mSQLiteDatabase = db;
			ContentValues newValuesCoordinateSystem = new ContentValues();
			newValuesCoordinateSystem.put("ENAME", "WGS84坐标系");
			newValuesCoordinateSystem.put("EA", 6378137);
			newValuesCoordinateSystem.put("EF", 1 / 298.257223563);
			newValuesCoordinateSystem.put("MIDDLELONGITUDE", 117);
			newValuesCoordinateSystem.put("OFFSETNORTH", 0);
			newValuesCoordinateSystem.put("OFFSETEAST", 500000);
			newValuesCoordinateSystem.put("CORRECTNORTH", 0);
			newValuesCoordinateSystem.put("CORRECTEAST", 0);
			newValuesCoordinateSystem.put("WDX", 0);
			newValuesCoordinateSystem.put("WDY", 0);
			newValuesCoordinateSystem.put("WDZ", 0);
			newValuesCoordinateSystem.put("WRX", 0);
			newValuesCoordinateSystem.put("WRY", 0);
			newValuesCoordinateSystem.put("WRZ", 0);
			newValuesCoordinateSystem.put("WK", 0);
			newValuesCoordinateSystem.put("CDX", 0);
			newValuesCoordinateSystem.put("CDY", 0);
			newValuesCoordinateSystem.put("CDZ", 0);
			newValuesCoordinateSystem.put("CRX", 0);
			newValuesCoordinateSystem.put("CRY", 0);
			newValuesCoordinateSystem.put("CRZ", 0);
			newValuesCoordinateSystem.put("CK", 0);
			insertData("PDefault_SYSTEMTABLE", newValuesCoordinateSystem);

			ContentValues newValuesSystemInformation = new ContentValues();
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss"); // ("yyyy-MM-dd hh:mm:ss");
			String sDate = sDateFormat.format(new java.util.Date());
			newValuesSystemInformation.put("CREATETIME", sDate);
			newValuesSystemInformation.put("USERCOORDINATETRANSFORMATION", 0);
			newValuesSystemInformation.put("POINTVIEWFORMAT", 0);
			newValuesSystemInformation.put("STAKINGOUTPOINTVIEWFORMAT", 0);
			insertData("PDefault_INFORMATION", newValuesSystemInformation);

			PadApplication.ProjectName = "Default";
			PadApplication.SaveStauts();
		}

		/* 升级数据库 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	/* 构造函数-取得Context */
	public MyDataBaseAdapter(Context context) {
		mContext = context;
	}

	// 初始化数据表名
	public static void SetTableName(String projectName) {
		mProjectName = projectName;
		ProjectInformationTable = "P" + mProjectName + "_INFORMATION";// 工程信息表
		PointTable = "P" + mProjectName + "_POINTTABLE";// 数据点表
		CoordinateSystemTable = "P" + mProjectName + "_SYSTEMTABLE";// 坐标系统表
		CoordinatetransTormationTable = "P" + mProjectName + "_CTTABLE";// 坐标转换表
	}

	// 打开数据库，返回数据库对象
	public static void open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * 查找表，获取所有工程名
	 * 
	 * @param tabName
	 *            表名
	 * @return
	 */
	public static List<String> GetTables() {
		List<String> strList = new ArrayList<String>();
		Cursor cursor = null;
		cursor = mSQLiteDatabase
				.rawQuery(
						"select name from sqlite_master where type='table' order by name",
						null);
		String sName = "";
		while (cursor.moveToNext()) {
			// 遍历出表名
			String name = cursor.getString(0);
			int iFlag = name.indexOf("_");
			if (!name.substring(0, 1).equals("P")) {
				continue;
			}
			String sP = name.substring(1, iFlag);
			if (!sName.equals(sP)) {
				sName = sP;
				strList.add(sP);
			}
		}
		return strList;
	}

	public static List<String> GetTables(String ExceptionTable) {
		List<String> strList = new ArrayList<String>();
		Cursor cursor = null;
		cursor = mSQLiteDatabase
				.rawQuery(
						"select name from sqlite_master where type='table' order by name",
						null);
		String sName = "";
		while (cursor.moveToNext()) {
			// 遍历出表名
			String name = cursor.getString(0);
			int iFlag = name.indexOf("_");
			if (!name.substring(0, 1).equals("P")) {
				continue;
			}
			String sP = name.substring(1, iFlag);
			if (!sName.equals(sP) & !sP.equals(ExceptionTable)) {
				sName = sP;
				strList.add(sP);
			}
		}
		return strList;
	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tabName
	 *            表名
	 * @return
	 */
	private static boolean TabbleIsExist(String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	// 新建工程表
	public static boolean CreateNewTable(String projectName) {
		if (TabbleIsExist("P" + projectName + "_INFORMATION")) {
			return false;// 表存在时则退出
		}

		SetTableName(projectName);
		String DB_INFORMATION = "CREATE TABLE " + ProjectInformationTable
				+ " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CREATETIME
				+ " TEXT NOT NULL," + KEY_USERCOORDINATETRANSFORMATION
				+ " INTEGER," + KEY_POINTVIEWFORMAT + " INTEGER,"
				+ KEY_STAKINGOUTPOINTVIEWFORMAT + " INTEGER)";

		String DB_POINTTABLE = "CREATE TABLE " + PointTable + " (" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_POINTFLAG + " INTEGER,"
				+ KEY_NAME + " TEXT UNIQUE," + KEY_CODE + " TEXT," + KEY_X
				+ " REAL," + KEY_Y + " REAL," + KEY_Z + " REAL)";

		String DB_SYSTEMTABLE = "CREATE TABLE " + CoordinateSystemTable + " ("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ENAME + " TEXT,"
				+ KEY_EA + " REAL," + KEY_EF + " REAL," + KEY_MIDDLELONGITUDE
				+ " REAL," + KEY_OFFSETNORTH + " REAL," + KEY_OFFSETEAST
				+ " REAL," + KEY_CORRECTNORTH + " REAL," + KEY_CORRECTEAST
				+ " REAL," + KEY_WDX + " REAL," + KEY_WDY + " REAL," + KEY_WDZ
				+ " REAL," + KEY_WRX + " REAL," + KEY_WRY + " REAL," + KEY_WRZ
				+ " REAL," + KEY_WK + " REAL," + KEY_CDX + " REAL," + KEY_CDY
				+ " REAL," + KEY_CDZ + " REAL," + KEY_CRX + " REAL," + KEY_CRY
				+ " REAL," + KEY_CRZ + " REAL," + KEY_CK + " REAL)";

		String DB_CTTABLE = "CREATE TABLE " + CoordinatetransTormationTable
				+ " (" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WGSPOINTNAME
				+ " TEXT," + KEY_GRIDPOINTNAME + " TEXT," + KEY_HERROR
				+ " REAL," + KEY_VERROR + " REAL)";

		mSQLiteDatabase.execSQL(DB_INFORMATION);
		mSQLiteDatabase.execSQL(DB_POINTTABLE);
		mSQLiteDatabase.execSQL(DB_SYSTEMTABLE);
		mSQLiteDatabase.execSQL(DB_CTTABLE);
		return true;
	}

	// 删除工程表
	public static void DeleteTable(String DeleteProjectName) {
		String DB_INFORMATION = "DROP TABLE P" + DeleteProjectName
				+ "_INFORMATION";
		String DB_POINTTABLE = "DROP TABLE P" + DeleteProjectName
				+ "_POINTTABLE";
		String DB_SYSTEMTABLE = "DROP TABLE P" + DeleteProjectName
				+ "_SYSTEMTABLE";
		String DB_CTTABLE = "DROP TABLE P" + DeleteProjectName + "_CTTABLE";
		mSQLiteDatabase.execSQL(DB_INFORMATION);
		mSQLiteDatabase.execSQL(DB_POINTTABLE);
		mSQLiteDatabase.execSQL(DB_SYSTEMTABLE);
		mSQLiteDatabase.execSQL(DB_CTTABLE);
	}

	// 关闭数据库
	public static void close() {
		mDatabaseHelper.close();
	}

	/* 插入一条数据 */
	public static long insertData(String TableName, ContentValues insertValues) {
		return mSQLiteDatabase.insert(TableName, KEY_ID, insertValues);
	}

	/* 按ID更新一条数据 */
	public static boolean updateData(String TableName, long rowId,
			ContentValues insertValues) {
		return mSQLiteDatabase.update(TableName, insertValues, KEY_ID + "="
				+ rowId, null) > 0;
	}

	/* 按点名更新一条数据 */
	public static boolean updateDataByPointName(String TableName,
			String PointName, ContentValues insertValues) {
		String[] args = { PointName };
		return mSQLiteDatabase.update(TableName, insertValues, KEY_NAME + "=?",
				args) > 0;
	}

	/* 删除一条数据 */
	public static boolean deleteDataByID(String TableName, long rowId) {
		return mSQLiteDatabase.delete(TableName, KEY_ID + "=" + rowId, null) > 0;
	}

	/* 删除一条数据 */
	public static boolean deleteDataByName(String TableName, String PointName) {
		String[] args = { PointName };
		return mSQLiteDatabase.delete(TableName, KEY_NAME + "=?", args) > 0;
	}

	/* 通过Cursor查询所有数据 */
	public static Cursor fetchAllData(String TableName, String[] Columns) {
		return mSQLiteDatabase.query(TableName, Columns, null, null, null,
				null, null);
	}

	/* 按ID查询数据 */
	public static Cursor fetchData(String TableName, long rowId,
			String[] Columns) throws SQLException {

		Cursor mCursor = mSQLiteDatabase.query(true, TableName, Columns, KEY_ID
				+ "=" + rowId, null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/* 查询指定字段数据 */
	public static Cursor fetchData(String TableName, String Key,
			String KeyValue, String[] Columns) throws SQLException {
		Cursor mCursor = mSQLiteDatabase.query(true, TableName, Columns, Key
				+ "= '" + KeyValue + "'", null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
}
