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
	 * 插入岸基设备数据
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
	 * 插入岸基设备数据
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
	 * 删除所有岸基设备数据
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
	 * 查询所有所有岸基设备的数据数量
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
	 * 根据设备ID查询岸基分组信息
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
//					LogUtil.LogI(Tag, "保存的memberId=" + memberId);
//					LogUtil.LogI(Tag,
//							"登陆的memberId=" + MyApplication.member.getMemberId());
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
	 * 将查询到的数据设置到DeviceInfo的对象中去
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
	 * 查询所有岸基设备数据
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
	 * 更新岸基设备数据信息,存在则更新，不存在则插入
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
	 * 关闭数据库
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
	 * 关闭数据库
	 */
	public void closeDBService()
	{
		if (aserviceDB != null)
		{
			aserviceDB.close();
		}
	}
}
