package com.anji.www.db.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.anji.www.activity.MyApplication;
import com.anji.www.db.AnjiGroupDB;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.GroupInfo;
import com.anji.www.entry.Member;
import com.anji.www.util.LogUtil;

public class AnjiGroupService
{
	protected static final String Tag = "AnjiGroupService";
	private Context mContext;
	private AnjiGroupDB aserviceDB = null;
	private Cursor cursor = null;

	public AnjiGroupService(Context context)
	{
		this.mContext = context;
		aserviceDB = new AnjiGroupDB(mContext);
	}

	/**
	 * ���밶���豸����
	 * 
	 * @param cacheData
	 * @param dateTime
	 * @return
	 */
	public long insertGroupData(GroupInfo info)
	{

//		try
//		{
//			return aserviceDB.insertGroupData(info);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			closeDB();
//		}
		return -1;
	}

	/**
	 * ���밶���豸����
	 * 
	 * @param cacheData
	 * @param dateTime
	 * @return
	 */
	public long deleteGroupDById(int groupId)
	{

//		try
//		{
//			return aserviceDB.deleteGroupByID(groupId);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			closeDB();
//		}
		return -1;
	}

	/**
	 * ɾ�����а����豸����
	 * 
	 * @param requestType
	 */
	public void deleteGroupData()
	{
//		try
//		{
//			aserviceDB.deleteGroupData();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			closeDB();
//		}
	}

	/**
	 * ��ѯ�������а����豸����������
	 * 
	 * @return
	 */
	public int getGroupCount(Context context)
	{
//		try
//		{
//			cursor = aserviceDB.selectAllGroup();
//			return cursor != null ? cursor.getCount() : 0;
//		}
//		catch (SQLiteException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			closeDB();
//		}
		return 0;
	}

	/**
	 * �����豸ID��ѯ����������Ϣ
	 * 
	 * @return
	 */
	public GroupInfo getGroupID(int groupId)
	{
		GroupInfo info = null;
//		try
//		{
//			cursor = aserviceDB.selectGroupById(groupId);
//			if (cursor.moveToNext())
//			{
//				if (MyApplication.member != null)
//				{
//					String memberId = cursor.getString(cursor
//							.getColumnIndex("memberId"));
//					String ssuid = cursor.getString(cursor
//							.getColumnIndex("ssuid"));
//					LogUtil.LogI(Tag, "�����memberId=" + memberId);
//					LogUtil.LogI(Tag,
//							"��½��memberId=" + MyApplication.member.getMemberId());
//
//					if (memberId.equals(MyApplication.member.getMemberId())
//							&& ssuid.equals(MyApplication.member.getSsuid()))
//					{
//						info = new GroupInfo();
//						setInfo(info, ssuid);
//					}
//				}
//			}
//		}
//		catch (SQLiteException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			closeDB();
//		}
		return info;
	}

	/**
	 * ����ѯ�����������õ�DeviceInfo�Ķ�����ȥ
	 * 
	 * @param info
	 * @param ssuid
	 */
	private void setInfo(GroupInfo info, String ssuid)
	{
		info.setSsuid(ssuid);
		info.setGroupId(cursor.getInt(cursor.getColumnIndex("groupID")));
		info.setGroupName(cursor.getString(cursor.getColumnIndex("groupName")));
		info.setMemberId(cursor.getString(cursor.getColumnIndex("memberId")));
		info.setIconType(cursor.getString(cursor.getColumnIndex("iconType")));
	}

	/**
	 * ��ѯ���а����豸����
	 * 
	 * @return
	 */
	public synchronized List<GroupInfo> getAlDeviceData(Context context)
	{
		List<GroupInfo> aimList = new ArrayList<GroupInfo>();
//		GroupInfo info = null;
//		try
//		{
//			cursor = aserviceDB.selectAllGroup();
//			while (cursor.moveToNext())
//			{
//				if (MyApplication.member != null)
//				{
//					String memberId = cursor.getString(cursor
//							.getColumnIndex("memberId"));
//					String ssuid = cursor.getString(cursor
//							.getColumnIndex("ssuid"));
//					if (memberId.equals(MyApplication.member.getMemberId())
//							&& ssuid.equals(MyApplication.member.getSsuid()))
//					{
//						info = new GroupInfo();
//						setInfo(info, ssuid);
//						aimList.add(info);
//					}
//				}
//			}
//		}
//		catch (SQLiteException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			closeDB();
//		}
		return aimList;
	}

	/**
	 * ���°����豸������Ϣ,��������£������������
	 * 
	 * @return
	 */
	public synchronized void updateDeviceData(final List<GroupInfo> list)
	{
//		new Thread()
//		{
//			public void run()
//			{
//				try
//				{
//					for (int i = 0; i < list.size(); i++)
//					{
//						GroupInfo info = getGroupID(list.get(i).getGroupId());
//						if (info != null)
//						{
//							LogUtil.LogI(Tag,
//									"group updateDeviceData  updateGroupById");
//							aserviceDB.updateGroupById(list.get(i),
//									MyApplication.member.getMemberId());
//						}
//						else
//						{
//							LogUtil.LogI(Tag,
//									"group updateDeviceData  insertGroupData");
//							aserviceDB.insertGroupData(list.get(i));
//						}
//					}
//				}
//				catch (SQLiteException e)
//				{
//					e.printStackTrace();
//				}
//				finally
//				{
//					closeDB();
//				}
//			};
//		}.start();

	}

	/**
	 * �ر����ݿ�
	 */
	public void closeDB()
	{
		if (cursor != null)
		{
			cursor.close();
		}
		if (aserviceDB != null)
		{
			aserviceDB.close();
		}
	}
	/**
	 * �ر����ݿ�
	 */
	public void closeDBService()
	{
		if (aserviceDB != null)
		{
			aserviceDB.close();
		}
	}
}
