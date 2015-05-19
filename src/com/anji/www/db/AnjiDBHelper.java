package com.anji.www.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anji.www.R;
import com.anji.www.util.LogUtil;

public class AnjiDBHelper extends SQLiteOpenHelper
{

	protected final static String TAG = "DatabaseHelper";
	private final static String DATABASE_NAME = "DEVICE_LIST";
	private final static int DATABASE_VERSION = 1;
	private Context context;

	public AnjiDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public void onCreate(SQLiteDatabase db)
	{
		Log.d("moon", "=========create db tbl");
		try
		{
			String sql = context
					.getString(R.string.sql_create_camere_list_tbl);
			db.execSQL(sql);
			LogUtil.LogI(TAG, "sql=" + sql);
			String sql_create_device_tbl = context
					.getString(R.string.sql_create_device_tbl);
			db.execSQL(sql_create_device_tbl);
			LogUtil.LogI(TAG, "sql_create_device_tbl="
					+ sql_create_device_tbl);
			String sql_create_group_tbl = context
					.getString(R.string.sql_create_group_tbl);
			db.execSQL(sql_create_group_tbl);
			LogUtil.LogI(TAG, "sql_create_group_tbl="
					+ sql_create_group_tbl);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			Log.d("moon", e.getMessage());
		}
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		String sql = context.getString(R.string.drop_sql);
		db.execSQL(sql);
		String sql_device = context.getString(R.string.drop_sql_device);
		db.execSQL(sql_device);
		String sql_group = context.getString(R.string.drop_sql_group);
		db.execSQL(sql_group);
		this.onCreate(db);

	}

}
