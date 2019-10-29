package com.padsurveysystem;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class GVTable extends LinearLayout {
	protected GridView gvTable, gvPage;
	protected SimpleAdapter saPageID, saTable;// 适配器
	protected ArrayList<HashMap<String, String>> srcPageID, srcTable;// 数据源

	protected int TableRowCount = 10;// 分页时，每页的Row总数
	protected int TableColCount = 0;// 每页col的数量
	protected SQLiteDatabase db;
	protected String rawSQL = "";
	protected Cursor curTable;// 分页时使用的Cursor
	protected OnTableClickListener clickListener;// 整个分页控件被点击时的回调函数
	protected OnPageSwitchListener switchListener;// 分页切换时的回调函数

	public GVTable(Context context) {
		super(context);
		this.setOrientation(VERTICAL);// 垂直
		// ----------------------------------------
		gvTable = new GridView(context);
		addView(gvTable, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));// 宽长式样

		srcTable = new ArrayList<HashMap<String, String>>();
		saTable = new SimpleAdapter(context, srcTable,// 数据来源
				R.layout.items,// XML实现
				new String[] { "ItemText" },// 动态数组与ImageItem对应的子项
				new int[] { R.id.ItemText });
		// 添加并且显示
		gvTable.setAdapter(saTable);
		gvTable.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int y = arg2 / curTable.getColumnCount() - 1;// 标题栏的不算
				int x = arg2 % curTable.getColumnCount();
				if (clickListener != null// 分页数据被点击
						&& y != -1) {// 点中的不是标题栏时
					clickListener.onTableClickListener(x, y, curTable);
				}
			}
		});

		// ----------------------------------------
		gvPage = new GridView(context);
		gvPage.setColumnWidth(40);// 设置每个分页按钮的宽度
		gvPage.setNumColumns(GridView.AUTO_FIT);// 分页按钮数量自动设置
		addView(gvPage, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));// 宽长式样
		srcPageID = new ArrayList<HashMap<String, String>>();
		saPageID = new SimpleAdapter(context, srcPageID,// 数据来源
				R.layout.items,// XML实现
				new String[] { "ItemText" },// 动态数组与ImageItem对应的子项
				new int[] { R.id.ItemText });
		// 添加并且显示
		gvPage.setAdapter(saPageID);
		// 添加消息处理
		gvPage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				LoadTable(arg2);// 根据所选分页读取对应的数据
				if (switchListener != null) {// 分页切换时
					switchListener.onPageSwitchListener(arg2, srcPageID.size());
				}
			}
		});
	}

	/**
	 * 清除所有数据
	 */
	public void gvRemoveAll() {
		if (this.curTable != null)
			curTable.close();
		srcTable.clear();
		saTable.notifyDataSetChanged();

		srcPageID.clear();
		saPageID.notifyDataSetChanged();

	}

	/**
	 * 读取指定ID的分页数据,返回当前页的总数据 SQL:Select * From TABLE_NAME Limit 9 Offset 10;
	 * 表示从TABLE_NAME表获取数据，跳过10行，取9行
	 * 
	 * @param pageID
	 *            指定的分页ID
	 */
	protected void LoadTable(int pageID) {
		if (curTable != null)// 释放上次的数据
			curTable.close();

		String sql = rawSQL + " Limit " + String.valueOf(TableRowCount)
				+ " Offset " + String.valueOf(pageID * TableRowCount);
		curTable = db.rawQuery(sql, null);

		gvTable.setNumColumns(curTable.getColumnCount());// 表现为表格的关键点！
		TableColCount = curTable.getColumnCount();
		srcTable.clear();
		// 取得字段名称
		int colCount = curTable.getColumnCount();
		for (int i = 0; i < colCount; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemText", curTable.getColumnName(i));
			srcTable.add(map);
		}

		// 列举出所有数据
		int recCount = curTable.getCount();
		for (int i = 0; i < recCount; i++) {// 定位到一条数据
			curTable.moveToPosition(i);
			for (int ii = 0; ii < colCount; ii++)// 定位到一条数据中的每个字段
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("ItemText", curTable.getString(ii));
				srcTable.add(map);
			}
		}

		saTable.notifyDataSetChanged();
	}

	/**
	 * 设置表格的最多显示的行数
	 * 
	 * @param row
	 *            表格的行数
	 */
	public void gvSetTableRowCount(int row) {
		TableRowCount = row;
	}

	/**
	 * 取得表格的最大行数
	 * 
	 * @return 行数
	 */
	public int gvGetTableRowCount() {
		return TableRowCount;
	}

	/**
	 * 取得当前分页的Cursor
	 * 
	 * @return 当前分页的Cursor
	 */
	public Cursor gvGetCurrentTable() {
		return curTable;
	}

	/**
	 * 准备分页显示数据
	 * 
	 * @param rawSQL
	 *            sql语句
	 * @param db
	 *            数据库
	 */
	public void gvReadyTable(String rawSQL, SQLiteDatabase db) {
		this.rawSQL = rawSQL;
		this.db = db;
	}

	/**
	 * 刷新分页栏，更新按钮数量
	 * 
	 * @param sql
	 *            SQL语句
	 * @param db
	 *            数据库
	 */
	public void gvUpdatePageBar(String sql, SQLiteDatabase db) {
		Cursor rec = db.rawQuery(sql, null);
		rec.moveToLast();
		long recSize = rec.getLong(0);// 取得总数
		rec.close();
		int pageNum = (int) (recSize / TableRowCount) + 1;// 取得分页数

		srcPageID.clear();
		for (int i = 0; i < pageNum; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemText", "No." + String.valueOf(i));// 添加图像资源的ID
			srcPageID.add(map);
		}
		saPageID.notifyDataSetChanged();
	}

	// ---------------------------------------------------------
	/**
	 * 表格被点击时的回调函数
	 */
	public void setTableOnClickListener(OnTableClickListener click) {
		this.clickListener = click;
	}

	public interface OnTableClickListener {
		public void onTableClickListener(int x, int y, Cursor c);
	}

	// ---------------------------------------------------------
	/**
	 * 分页栏被点击时的回调函数
	 */
	public void setOnPageSwitchListener(OnPageSwitchListener pageSwitch) {
		this.switchListener = pageSwitch;
	}

	public interface OnPageSwitchListener {
		public void onPageSwitchListener(int pageID, int pageCount);
	}
}
