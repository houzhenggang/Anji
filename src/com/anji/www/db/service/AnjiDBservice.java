package com.anji.www.db.service;

import java.util.ArrayList;
import java.util.List;

import com.anji.www.activity.MyApplication;
import com.anji.www.db.AnjiDB;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.Member;
import com.anji.www.util.LogUtil;
import com.anji.www.util.MyActivityManager;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

public class AnjiDBservice
{
	protected static final String Tag = "AnjiDBservice";
	private Context mContext;
	private AnjiDB aserviceDB = null;
	private Cursor cursor = null;

	public AnjiDBservice(Context context)
	{
		this.mContext = context;
		aserviceDB = new AnjiDB(mContext);
	}

	/**
	 * ���밶���豸����
	 * 
	 * @param cacheData
	 * @param dateTime
	 * @return
	 */
	public long insertDeviceData(DeviceInfo info)
	{

//		try
//		{
//			return aserviceDB.insertDeviceData(info);
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
	public void deleteDeviceData()
	{
//		try
//		{
//			aserviceDB.deleteDeviceData();
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
	 * ɾ�������豸���ݸ���ID��type
	 * 
	 * @param requestType
	 */
	public int deleteDeviceByIDandType(int id, int type)
	{
//		try
//		{
//			return aserviceDB.deleteDeviceDataByID(id, type);
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
	 * ��ѯ�������а����豸����������
	 * 
	 * @return
	 */
	public int getServiceDataCount(Context context)
	{
//		try
//		{
//			cursor = aserviceDB.selectAllDeviceData();
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
	 * �����豸ID��ѯ�����豸��Ϣ
	 * 
	 * @return
	 */
	public synchronized DeviceInfo getDeviceByID(DeviceInfo device)
	{
		DeviceInfo info = null;
//		try
//		{
//			cursor = aserviceDB.selectDeviceDataById(device);
//			if (cursor.moveToNext())
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
//						info = new DeviceInfo();
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
//			// if (cursor != null)
//			// {
//			//
//			// cursor.close();
//			// }
//		}
		return info;
	}

	/**
	 * ����ѯ�����������õ�DeviceInfo�Ķ�����ȥ
	 * 
	 * @param info
	 * @param ssuid
	 */
	private void setInfo(DeviceInfo info, String ssuid)
	{
		info.setDeviceMac(cursor.getString(cursor.getColumnIndex("deviceMac")));
		info.setSsuid(ssuid);
		info.setMemberId(cursor.getString(cursor.getColumnIndex("memberId")));
		info.setDeviceId(cursor.getInt(cursor.getColumnIndex("deviceId")));
		info.setDeviceChannel(cursor.getInt(cursor
				.getColumnIndex("deviceChannel")));
		info.setDeviceChannel2(cursor.getInt(cursor
				.getColumnIndex("deviceChannel2")));
		info.setDeviceType(cursor.getString(cursor.getColumnIndex("deviceType")));
		info.setType(cursor.getInt(cursor.getColumnIndex("type")));
		info.setDeviceState((byte) cursor.getInt(cursor
				.getColumnIndex("deviceState")));
		info.setSensorState((byte) cursor.getInt(cursor
				.getColumnIndex("sensorState")));
		info.setDeviceName(cursor.getString(cursor.getColumnIndex("deviceName")));
		info.setGroupName(cursor.getString(cursor.getColumnIndex("groupName")));
		info.setGroupID(cursor.getInt(cursor.getColumnIndex("groupID")));
		info.setDeviceBattery(cursor.getInt(cursor
				.getColumnIndex("deviceBattery")));
		int isCharge = cursor.getInt(cursor.getColumnIndex("isCharge"));
		if (isCharge == 0)
		{
			info.setCharge(false);
		}
		else
		{
			info.setCharge(true);
		}
		info.setTempValue(cursor.getFloat(cursor.getColumnIndex("tempValue")));
		info.setHumValue(cursor.getFloat(cursor.getColumnIndex("humValue")));
		int isInfraredSwitch = cursor.getInt(cursor
				.getColumnIndex("infraredSwitch"));
		if (isInfraredSwitch == 0)
		{
			info.setInfraredSwitch(false);
		}
		else
		{
			info.setInfraredSwitch(true);
		}
	}

	/**
	 * ��ѯ���а����豸����
	 * 
	 * @return
	 */
	public synchronized List<DeviceInfo> getAllDeviceData(Context context)
	{
		List<DeviceInfo> aimList = new ArrayList<DeviceInfo>();
		DeviceInfo info = null;
//		try
//		{
//			LogUtil.LogI(Tag, "getAllDeviceData");
//			cursor = aserviceDB.selectAllDeviceData();
//			while (cursor.moveToNext())
//			{
//				if (MyApplication.member != null)
//				{
//					String memberId = cursor.getString(cursor
//							.getColumnIndex("memberId"));
//					String ssuid = cursor.getString(cursor
//							.getColumnIndex("ssuid"));
//					// LogUtil.LogI(Tag, "memberId=" + memberId);
//					// LogUtil.LogI(Tag, "ssuid=" + ssuid);
//					// LogUtil.LogI(Tag, "MyApplication.member.getMemberId()="
//					// + MyApplication.member.getMemberId());
//					// LogUtil.LogI(Tag, "MyApplication.member.getSsuid()="
//					// + MyApplication.member.getSsuid());
//
//					if (memberId.equals(MyApplication.member.getMemberId())
//							&& ssuid.equals(MyApplication.member.getSsuid()))
//					{
//						info = new DeviceInfo();
//						setInfo(info, ssuid);
//						LogUtil.LogI(Tag, "info=" + info.hashCode());
//						info.setDeviceState((byte) 0xaa);// ���� ���ݿ���������ݶ���Ϊ����
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
	 * ���°����豸������Ϣ �о͸��£�û�оͲ���
	 * 
	 * @return
	 */
	public synchronized void updateDeviceData(final List<DeviceInfo> list)
	{
//		// new Thread()
//		// {
//		// public void run()
//		// {
//		try
//		{
//			for (int i = 0; i < list.size(); i++)
//			{
//				// LogUtil.LogI(Tag, "updateDeviceData i=" + i);
//				DeviceInfo info = getDeviceByID(list.get(i));
//				// LogUtil.LogI(Tag, "updateDeviceData info.getDeviceId=="
//				// + list.get(i).getDeviceId());
//				// LogUtil.LogI(Tag, "updateDeviceData info.getDeviceChannel=="
//				// + list.get(i).getDeviceChannel());
//				// LogUtil.LogI(Tag, "updateDeviceData info.getDeviceMac=="
//				// + list.get(i).getDeviceMac());
//				// LogUtil.LogI(Tag, "updateDeviceData info.getMemberId=="
//				// + list.get(i).getMemberId());
//				// LogUtil.LogI(Tag, "updateDeviceData info.getSsuid=="
//				// + list.get(i).getSsuid());
//				// LogUtil.LogI(Tag,
//				// "updateDeviceData info.getType=="
//				// + list.get(i).getType());
//				// LogUtil.LogI(Tag, "info�Ƿ���ڿ�"+(info != null));
//				if (info != null)
//				{
//					 LogUtil.LogI(Tag,
//					 "device updateDeviceData  updateDeviceById");
//					aserviceDB.updateDeviceById(list.get(i),
//							MyApplication.member.getMemberId());
//				}
//				else
//				{
//					 LogUtil.LogI(Tag,
//					 "device updateDeviceData  insertDeviceData");
//					aserviceDB.insertDeviceData(list.get(i));
//				}
//			}
//		}
//		catch (SQLiteException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			// closeDB();
//		}
//		// };
//		// }.start();

	}

	/**
	 * �����豸ID��ѯ�����豸��Ϣ
	 * 
	 * @return
	 */
	public int updataDeviceByID(DeviceInfo info, String memberId)
	{
//		if (info != null)
//		{
//
//			try
//			{
//				return aserviceDB.updateDeviceById(info,
//						MyApplication.member.getMemberId());
//			}
//			catch (SQLiteException e)
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				closeDB();
//			}
//		}
		return -1;
	}

	/**
	 * �ر����ݿ�
	 */
	public void closeDB()
	{
		// if (cursor != null)
		// {
		// cursor.close();
		// }
		// if (aserviceDB != null)
		// {
		// aserviceDB.close();
		// }
	}

	public void closeDB2()
	{
		// if (cursor != null)
		// {
		// cursor.close();
		// }
		if (aserviceDB != null)
		{
			aserviceDB.close();
		}
	}
}
