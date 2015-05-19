package com.anji.www.db;

import com.anji.www.activity.MyApplication;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.util.LogUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 创建数据库的接口
 * 
 * @author Administrator
 */
public class AnjiDB extends AnjiDBHelper
{

	public AnjiDB(Context context)
	{
		super(context);
	}

	/**
	 * 插入岸基设备数据
	 * 
	 * @param id
	 *            交友目的ID
	 * @param aimName
	 *            交友目的名称
	 * @return
	 */
	public synchronized long insertDeviceData(DeviceInfo info)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		putValue(info, cv);
		return db.insert("tb_anji_device", null, cv);
	}

	/**
	 * 把设备信息保存到键值对，进行数据操作
	 * 
	 * @param info
	 * @param cv
	 */
	private void putValue(DeviceInfo info, ContentValues cv)
	{
		cv.put("memberId", info.getMemberId());
		cv.put("ssuid", MyApplication.member.getSsuid());
		cv.put("deviceMac", info.getDeviceMac());
		cv.put("deviceId", info.getDeviceId());
		cv.put("deviceChannel", info.getDeviceChannel());
		cv.put("deviceChannel2", info.getDeviceChannel2());
		cv.put("deviceType", info.getDeviceType());
		cv.put("type", info.getType());
		cv.put("deviceState", info.getDeviceState());
		cv.put("sensorState", info.getSensorState());
		cv.put("deviceName", info.getDeviceName());
		cv.put("groupName", info.getGroupName());
		cv.put("groupID", info.getGroupID());
		cv.put("deviceBattery", info.getDeviceBattery());
		if (info.isCharge())
		{
			cv.put("isCharge", 1);
		}
		else
		{
			cv.put("isCharge", 0);
		}
		cv.put("tempValue", info.getTempValue());
		cv.put("humValue", info.getHumValue());
		if (info.isInfraredSwitch())
		{
			cv.put("infraredSwitch", 1);
		}
		else
		{
			cv.put("infraredSwitch", 0);
		}
	}

	/**
	 * 删除所有岸基设备的数据
	 * 
	 * @return
	 */
	public synchronized int deleteDeviceData()
	{
		SQLiteDatabase db = getWritableDatabase();
		return db.delete("tb_anji_device", null, null);
	}

	/**
	 * 删除单个岸基设备的数据
	 * 
	 * @return
	 */
	public synchronized int deleteDeviceDataByID(int deviceId, int deviceType)
	{
		SQLiteDatabase db = null;
		try
		{
			db = getWritableDatabase();
			 String table = "tb_anji_device";
			 String whereClause = "deviceId = ? and type = ?";
			 String[] whereArgs = new String[]
			 { String.valueOf(deviceId), String.valueOf(deviceType) };
			 Log.d("remote", "delete  whereClause=" + whereClause);
			 Log.d("remote", "delete  whereArgs=" + whereArgs);
			
			 return db.delete(table, whereClause, whereArgs);

//			String sql = "delete from tb_anji_device where deviceId="
//					+ deviceId + " and deviceType=" + deviceType;// 删除操作的SQL语句
//
//			db.execSQL(sql);// 执行删除操作
//			return 1;
		}
		catch (Exception e)
		{
			Log.d("moon", e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (db != null)
			{
				db.close();
			}
		}
		return -1;
	}

	/**
	 * 查询所有所有岸基设备的数据
	 * 
	 * @return
	 */
	public synchronized Cursor selectAllDeviceData()
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cur = db.query("tb_anji_device", null, null, null, null, null,
				null);
		return cur;
	}

	/**
	 * 根据岸基设备的ID查询数据
	 * 
	 * @return
	 */
	public Cursor selectDeviceDataById(DeviceInfo device)
	{
		SQLiteDatabase db = getReadableDatabase();
		String where = "memberId = ? and  deviceChannel = ? and deviceId = ? and type = ?";
		// LogUtil.LogI(TAG, " qurry db ===MyApplication.member.getMemberId()="
		// + MyApplication.member.getMemberId());
		// LogUtil.LogI(TAG, " qurry db ===MyApplication.member.getSsuid()="
		// + MyApplication.member.getSsuid());
		// LogUtil.LogI(
		// TAG,
		// " qurry db === device.getDeviceChannel()="
		// + device.getDeviceChannel());
		// LogUtil.LogI(TAG,
		// " qurry db === device.getDeviceId()=" + device.getDeviceId());
		// LogUtil.LogI(
		// TAG,
		// " qurry db === device.getDeviceMac()()="
		// + device.getDeviceMac());
		// LogUtil.LogI(TAG,
		// " qurry db === device.getType()()=" + device.getType());
		String[] selectionArgs = new String[]
		{ MyApplication.member.getMemberId(),
				String.valueOf(device.getDeviceChannel()),
				String.valueOf(device.getDeviceId()),
				String.valueOf(device.getType()) };
		Cursor cur = db.query("tb_anji_device", null, where, selectionArgs,
				null, null, null);

		// Cursor rec = db.rawQuery(sql, null);
		// Cursor cur = db.query("tb_anji_device", null,
		// "memberId = " + MyApplication.member.getMemberId() + " and "
		// + "type = " + device.getType() + " and "
		// + "deviceId = " + device.getDeviceId() + " and "
		// + "deviceChannel = " + device.getDeviceChannel(), null,
		// null, null, null);
		// "select * from tb_anji_device where memberId=? and ssuid=? and deviceMac=? and deviceId=? and deviceChannel=?";
		// Cursor mCursor = db
		// .rawQuery(
		// "select * from tb_anji_device where memberId='103' and ssuid='807EF704004B1200' and deviceMac='01F42604004B1200' and deviceId='1' and deviceChannel='1'",
		// null);

		LogUtil.LogI(TAG, "cur.getCount= " + cur.getCount());

		// String sql =
		// "select * from tb_anji_device where memberId=? and ssuid=? and deviceMac=? and deviceId=? and deviceChannel=?";
		// Cursor mCursor = db.rawQuery(
		// sql,
		// new String[]
		// { MyApplication.member.getMemberId(),
		// MyApplication.member.getSsuid(), device.getDeviceMac(),
		// device.getDeviceId() + "",
		// device.getDeviceChannel() + "" });
		// LogUtil.LogI(TAG, "cur.getCount=" + mCursor.getCount());
		// cur.moveToFirst();
		return cur;
	}

	/**
	 * 根据岸基设备的ID更新数据
	 * 
	 * @return
	 */
	public synchronized int updateDeviceById(DeviceInfo info, String memberId)
	{
		if (info.getMemberId().equals(memberId)
				&& info.getSsuid().equals(MyApplication.member.getSsuid()))
		{

			SQLiteDatabase db = getReadableDatabase();
			ContentValues cv = new ContentValues();

			putValue(info, cv);
			/*
			 * 第一个参数是：表名 第二个参数ContentValues对象 第三个参数where子句，里面的问题是一个占位符
			 * 第四个参数是占位符对应的值 注意：有几个占位符第四个参数里面就应该有几个值
			 */
			return db.update("tb_anji_device", cv, "deviceId=? and type =?",
					new String[]
					{ info.getDeviceId() + "", info.getType() + "" });
		}
		return -1;
	}

}
