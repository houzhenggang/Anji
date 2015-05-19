package com.anji.www.db;

import java.util.ArrayList;
import java.util.List;

import com.anji.www.R;
import com.anji.www.util.LogUtil;
import com.remote.util.IPCameraInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends AnjiDBHelper
{

	private static String Tag = "DatabaseHelper";

	private Context context;
	protected SQLiteDatabase db;

	public DatabaseHelper(Context context)
	{
		super(context);
	}

	public IPCameraInfo query(Context context, int id) throws Exception
	{
//		SQLiteDatabase db = null;
//		try
//		{
//			db = getWritableDatabase();
//			String whereClause = " _id = ?";
//			String[] whereArgs = new String[]
//			{ String.valueOf(id) };
//			String[] columns = new String[]
//			{ "devType", "devName", "ip", "streamType", "webPort", "mediaPort",
//					"uid", "userName", "password", "devSetName", "groupName",
//					"groupId", "thumPath" };
//
//			Cursor cursor = db.query("tb_device_list", columns, whereClause,
//					whereArgs, null, null, null);
//			if (cursor.getCount() == 0)
//			{
//				throw new Exception("没有找到ID" + id + "的数据");
//			}
//
//			cursor.moveToFirst();
//			IPCameraInfo info = new IPCameraInfo();
//			info.id = cursor.getInt(cursor.getColumnIndex("_id"));
//			if (cursor.getColumnIndex("devType") != 0)
//			{
//
//				info.devType = cursor.getInt(cursor.getColumnIndex("devType"));
//			}
//			else
//			{
//				info.devType = 1;//默认设备类型为1
//			}
//			info.devName = cursor.getString(cursor.getColumnIndex("devName"));
//			info.ip = cursor.getString(cursor.getColumnIndex("ip"));
//			info.streamType = cursor
//					.getInt(cursor.getColumnIndex("streamType"));
//			info.webPort = cursor.getInt(cursor.getColumnIndex("webPort"));
//			info.mediaPort = cursor.getInt(cursor.getColumnIndex("mediaPort"));
//			info.uid = cursor.getString(cursor.getColumnIndex("uid"));
//			info.userName = cursor.getString(cursor.getColumnIndex("userName"));
//			info.password = cursor.getString(cursor.getColumnIndex("password"));
//			info.devSetName = cursor.getString(cursor
//					.getColumnIndex("devSetName"));
//			info.groupName = cursor.getString(cursor
//					.getColumnIndex("groupName"));
//			info.groupId = cursor.getInt(cursor.getColumnIndex("groupId"));
//			info.thumPath = cursor.getString(cursor.getColumnIndex("thumPath"));
//			info.cameraId = cursor.getInt(cursor.getColumnIndex("cameraId"));
//			info.isOnLine = false;
//			cursor.close();
//			return info;
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
		return null;
	}

	public List<IPCameraInfo> qurryAll(Context context)
	{
		SQLiteDatabase db = null;
		List<IPCameraInfo> list = new ArrayList<IPCameraInfo>();
//		try
//		{
//			LogUtil.LogI(Tag, "qurryAll");
//			String[] columns = new String[]
//			{ "_id", "devType", "devName", "ip", "streamType", "webPort",
//					"mediaPort", "uid", "userName", "password", "devSetName",
//					"groupName", "groupId", "thumPath" };
//			db = getWritableDatabase();
//			Cursor cursor = db.rawQuery("SELECT * FROM tb_device_list", null);
//			if (cursor.getCount() == 0)
//			{
//				// throw new Exception("没有找到数据");
//				LogUtil.LogI(Tag, "没有找到数据");
//			}
//			else
//			{
//				LogUtil.LogI(Tag, "cursor.getCount()=" + cursor.getCount());
//				while (cursor.moveToNext())
//				{
//					IPCameraInfo info = new IPCameraInfo();
//					info.id = cursor.getInt(cursor.getColumnIndex("_id"));
//					info.devType = cursor.getInt(cursor
//							.getColumnIndex("devType"));
//					info.devName = cursor.getString(cursor
//							.getColumnIndex("devName"));
//					info.ip = cursor.getString(cursor.getColumnIndex("ip"));
//					info.streamType = cursor.getInt(cursor
//							.getColumnIndex("streamType"));
//					info.webPort = cursor.getInt(cursor
//							.getColumnIndex("webPort"));
//					info.mediaPort = cursor.getInt(cursor
//							.getColumnIndex("mediaPort"));
//					info.uid = cursor.getString(cursor.getColumnIndex("uid"));
//					info.userName = cursor.getString(cursor
//							.getColumnIndex("userName"));
//					info.password = cursor.getString(cursor
//							.getColumnIndex("password"));
//					info.devSetName = cursor.getString(cursor
//							.getColumnIndex("devSetName"));
//					info.groupName = cursor.getString(cursor
//							.getColumnIndex("groupName"));
//					info.groupId = cursor.getInt(cursor
//							.getColumnIndex("groupId"));
//					info.thumPath = cursor.getString(cursor
//							.getColumnIndex("thumPath"));
//					info.cameraId = cursor.getInt(cursor
//							.getColumnIndex("cameraId"));
//					info.isOnLine = false;
//					list.add(info);
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			Log.d(Tag, e.getMessage());
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
		return list;
	}

	public long testInsert(Context context)
	{

//		SQLiteDatabase db = null;
//		try
//		{
//			db = getWritableDatabase();
//			ContentValues values = new ContentValues();
//			// values.put("_id", -1);
//			values.put("ip", "192.168.1.103");
//			values.put("username", "adnim");
//			values.put("password", "");
//			values.put("streamType", 0);
//			values.put("webPort", 88);
//			values.put("mediaPort", 888);
//			long num = db.insert("tb_device_list", null, values);
//			return num;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
		return 0;
	}

	public long insert(Context context, String table,
			ContentValues values) throws Exception
	{
//		SQLiteDatabase db = null;
//		try
//		{
//			db = getWritableDatabase();
//			long num = db.insert(table, null, values);
//			return num;
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
		return 0;
	}

	public long update(Context context, String table,
			ContentValues values, int id) throws Exception
	{
//		LogUtil.LogI(TAG, "camera update id="+id);
//		SQLiteDatabase db = null;
//		try
//		{
//			db = getWritableDatabase();
//			String whereClause = " _id = ?";
//			String[] whereArgs = new String[]
//			{ String.valueOf(id) };
//			long num = db.update(table, values, whereClause, whereArgs);
//			return num;
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
		return 0;
	}

	public void update(Context context, IPCameraInfo info)
	{
//		ContentValues contentValue = new ContentValues();
//		contentValue.put("devType", info.devType);
//		contentValue.put("devName", info.devName);
//		contentValue.put("ip", info.ip);
//		contentValue.put("streamType", 0);
//		contentValue.put("webPort", info.webPort);
//		contentValue.put("mediaPort", info.mediaPort);
//		contentValue.put("uid", info.uid);
//		contentValue.put("devSetName", "");
//		contentValue.put("groupName", info.groupName);
//		contentValue.put("groupId", info.groupId);// -1表示未分组
//		contentValue.put("thumPath", "");
//		contentValue.put("groupId", info.groupId);
//		contentValue.put("userName", info.userName);
//		contentValue.put("password", info.password);
//		contentValue.put("cameraId", info.cameraId);
//		try
//		{
//			LogUtil.LogI(Tag, "_id=" + info.id);
//			update(context, "tb_device_list", contentValue,
//					info.id);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	public void testDelete(Context context)
//	{
//		SQLiteDatabase db = null;
//		try
//		{
//			
//			db = getWritableDatabase();
//			db.execSQL("delete from tb_device_list;");
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}

	}

	public void delete(Context context, int id) throws Exception
	{
//		SQLiteDatabase db = null;
//		Log.d("remote", "delete  id =" + id);
//		try
//		{
//			db = getWritableDatabase();
//			String table = "tb_device_list";
//			String whereClause = " _id = ?";
//			String[] whereArgs = new String[]
//			{ String.valueOf(id) };
//			Log.d("remote", "delete  whereClause=" + whereClause);
//			Log.d("remote", "delete  whereArgs=" + whereArgs);
//			db.delete(table, whereClause, whereArgs);
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
	}
	
	public void deleteByCameraId(Context context, int cameraId) throws Exception
	{
//		SQLiteDatabase db = null;
//		Log.d("remote", "delete  id =" + cameraId);
//		try
//		{
//			db = getWritableDatabase();
//			String table = "tb_device_list";
//			String whereClause = " cameraId = ?";
//			String[] whereArgs = new String[]
//			{ String.valueOf(cameraId) };
//			Log.d("remote", "delete  whereClause=" + whereClause);
//			Log.d("remote", "delete  whereArgs=" + whereArgs);
//			db.delete(table, whereClause, whereArgs);
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
	}

	public void deleteAll(Context context) throws Exception
	{
//		SQLiteDatabase db = null;
//		try
//		{
//			db = getWritableDatabase();
//			db.execSQL("DELETE FROM tb_device_list");
//			Log.d("moon", "Delete all record in database");
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
	}

	public void drop(Context context)
	{
//		SQLiteDatabase db = null;
//		try
//		{
//			String sql = context.getString(R.string.drop_sql);
//			db.execSQL(sql);
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}

	}

	public  int getCount(Context context, String table) throws Exception
	{
//		SQLiteDatabase db = null;
//		int cnt = 0;
//		try
//		{
//			db = getReadableDatabase();
//			Cursor cur = db.query(table, new String[]
//			{ "_id", "ip" }, null, null, null, null, null);
//			cnt = cur.getCount();
//			cur.close();
//			return cnt;
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
		return 0;
	}

//	public Cursor loadAllName() throws Exception
//	{
//		try
//		{
//			Cursor cur = db.query("tb_device_list", new String[]
//			{ "_id", "devName", "ip", "uid" }, null, null, null, null,
//					"_id DESC");
//			return cur;
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
		
//	}

//	public Cursor QueryDevice(Context context, String ip, int webPort)
//			throws Exception
//	{
//		SQLiteDatabase db = null;
//		try
//		{
//			db = getReadableDatabase();
//			String sql = "SELECT * FROM tb_device_list WHERE ip=" + "'" + ip
//					+ "'" + " AND " + "webPort=" + webPort;
//			Log.d("moon", "Exec sql:" + sql);
//			Cursor cur = db.rawQuery(sql, null);
//			return cur;
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
		
//	}

	public Cursor QueryDevice(Context context, String uid)
			throws Exception
	{
//		try
//		{
//			SQLiteDatabase db = null;
//			db = getReadableDatabase();
//			String sql = "SELECT * FROM tb_device_list WHERE uid=" + "'" + uid
//					+ "'";
//			Cursor cur = db.rawQuery(sql, null);
//			return cur;
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
		return null;
	}

	public void close()
	{
		if (this.db != null)
		{
			this.db.close();
		}
	}

//	public List<String> loadName(Context context) throws Exception
//	{
//		SQLiteDatabase db = null;
//		List<String> rst = new ArrayList<String>();
//		try
//		{
//			db = getReadableDatabase();
//			Cursor cur = db.query("tb_device_list", new String[]
//			{ "_id", "ip" }, null, null, null, null, null);
//			cur.moveToFirst();
//			for (int i = 0; i < cur.getCount(); i++)
//			{
//				String s = cur.getString(1);
//				rst.add(s);
//				cur.moveToNext();
//			}
//			cur.close();
//			return rst;
//		}
//		catch (Exception e)
//		{
//			throw e;
//		}
//		finally
//		{
//			if (db != null)
//			{
//				db.close();
//			}
//		}
//	}

//	public Cursor query(int id) throws Exception
//	{
//		try
//		{
//			String whereClause = " _id = ?";
//			String[] whereArgs = new String[]
//			{ String.valueOf(id) };
//			Cursor cur = db.query("tb_device_list", new String[]
//			{ "devType", "devName", "ip", "streamType", "webPort", "mediaPort",
//					"uid", "userName", "password" }, whereClause, whereArgs,
//					null, null, "_id DESC");
//			return cur;
//		}
//		catch (Exception e)
//		{
//			Log.d("moon", e.getMessage());
//			throw e;
//		}
//	}
}
