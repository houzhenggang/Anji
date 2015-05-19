package com.anji.www.db;

import java.util.List;

import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.GroupInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 分组数据库表
 * 
 * @author Administrator
 */
public class AnjiGroupDB extends AnjiDBHelper
{

	public AnjiGroupDB(Context context)
	{
		super(context);
	}

	/**
	 * 插入岸基设备分组数据
	 * 
	 * @param id
	 *            交友目的ID
	 * @param aimName
	 *            交友目的名称
	 * @return
	 */
	public synchronized long insertGroupData(GroupInfo info)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("groupID", info.getGroupId());
		cv.put("groupName", info.getGroupName());
		cv.put("memberId", info.getMemberId());
		cv.put("ssuid", info.getSsuid());
		cv.put("iconType", info.getIconType());

		return db.insert("tb_anji_group", null, cv);
	}

	/**
	 * 删除所有岸基设备分组的数据
	 * 
	 * @return
	 */
	public synchronized int deleteGroupData()
	{
		SQLiteDatabase db = getWritableDatabase();
		return db.delete("tb_anji_group", null, null);
	}

	/**
	 * 删除单个岸基分组的数据
	 * 
	 * @return
	 */
	public synchronized int deleteGroupByID(int groupID)
	{
		SQLiteDatabase db = null;
		try
		{
			db = getWritableDatabase();
			String table = "tb_anji_group";
			String whereClause = " groupID = ?";
			String[] whereArgs = new String[]
			{ String.valueOf(groupID) };
			Log.d("remote", "delete  whereClause=" + whereClause);
			Log.d("remote", "delete  whereArgs=" + whereArgs);
			return db.delete(table, whereClause, whereArgs);
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
	 * 查询所有所有岸基设备分组的数据
	 * 
	 * @return
	 */
	public synchronized Cursor selectAllGroup()
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cur = db.query("tb_anji_group", null, null, null, null, null,
				null);
		return cur;
	}

	/**
	 * 根据岸基设备分组的ID查询数据
	 * 
	 * @return
	 */
	public synchronized Cursor selectGroupById(int groupID)
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cur = db.query("tb_anji_group", null, "groupID=" + groupID,
				null, null, null, null);
		return cur;
	}

	/**
	 * 根据岸基设备分组的ID更新数据
	 * 
	 * @return
	 */
	public synchronized int updateGroupById(GroupInfo info, String memberId)
	{
		if (info.getMemberId().equals(memberId))
		{

			SQLiteDatabase db = getReadableDatabase();
			ContentValues cv = new ContentValues();

			cv.put("groupID", info.getGroupId());
			cv.put("groupName", info.getGroupName());
			cv.put("memberId", info.getMemberId());
			cv.put("ssuid", info.getSsuid());
			/*
			 * 第一个参数是：表名 第二个参数ContentValues对象 第三个参数where子句，里面的问题是一个占位符
			 * 第四个参数是占位符对应的值 注意：有几个占位符第四个参数里面就应该有几个值
			 */
			return db.update("tb_anji_group", cv, "groupID=?", new String[]
			{ info.getGroupId() + "" });
		}
		return -1;
	}
}
