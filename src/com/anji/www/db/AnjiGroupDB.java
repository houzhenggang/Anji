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
 * �������ݿ��
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
	 * ���밶���豸��������
	 * 
	 * @param id
	 *            ����Ŀ��ID
	 * @param aimName
	 *            ����Ŀ������
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
	 * ɾ�����а����豸���������
	 * 
	 * @return
	 */
	public synchronized int deleteGroupData()
	{
		SQLiteDatabase db = getWritableDatabase();
		return db.delete("tb_anji_group", null, null);
	}

	/**
	 * ɾ�������������������
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
	 * ��ѯ�������а����豸���������
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
	 * ���ݰ����豸�����ID��ѯ����
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
	 * ���ݰ����豸�����ID��������
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
			 * ��һ�������ǣ����� �ڶ�������ContentValues���� ����������where�Ӿ䣬�����������һ��ռλ��
			 * ���ĸ�������ռλ����Ӧ��ֵ ע�⣺�м���ռλ�����ĸ����������Ӧ���м���ֵ
			 */
			return db.update("tb_anji_group", cv, "groupID=?", new String[]
			{ info.getGroupId() + "" });
		}
		return -1;
	}
}
