package com.anji.www.entry;

import android.text.TextUtils;

public class DeviceInfo implements Comparable<DeviceInfo>
{
	private int deviceId;// 设备的ID；
	private String deviceMac;// 设备硬件地址
	private String ssuid;// 网关硬件地址
	private int deviceChannel;// 设备通道号 和温度通道号
	private int deviceChannel2;// 设备通通道号2，温湿度提供 湿度通道号
	private String deviceType;// 设备类型详情
	private int type; // 设备类型 开关或者传感 0为开关 1为传感器
	private byte deviceState;// 设备状态
	private byte sensorState;// 传感设备数据
	private String deviceName;// 设备名称
	private String groupName;// 分组名称
	private int groupID;// 分组id
	private int deviceBattery;// 电量
	private boolean isCharge;// 是否在充电
	private float tempValue;// 温度
	private float humValue;// 湿度
	private boolean infraredSwitch;// 红外推送开关
	private String memberId;// 所在会员ID

	public String getDeviceMac()
	{
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac)
	{
		this.deviceMac = deviceMac;
	}

	public int getDeviceChannel()
	{
		return deviceChannel;
	}

	public void setDeviceChannel(int deviceChannel)
	{
		this.deviceChannel = deviceChannel;
	}

	public String getDeviceType()
	{
		return deviceType;
	}

	public void setDeviceType(String deviceType)
	{
		this.deviceType = deviceType;
	}

	public byte getDeviceState()
	{
		return deviceState;
	}

	public void setDeviceState(byte deviceState)
	{
		this.deviceState = deviceState;
	}

	public String getDeviceName()
	{
		return TextUtils.isEmpty( deviceName ) ? "" : deviceName;
	}

	public void setDeviceName(String deviceName)
	{
		this.deviceName = deviceName;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	public int getGroupID()
	{
		return groupID;
	}

	public void setGroupID(int groupID)
	{
		this.groupID = groupID;
	}

	public int getDeviceBattery()
	{
		return deviceBattery;
	}

	public void setDeviceBattery(int deviceBattery)
	{
		this.deviceBattery = deviceBattery;
	}

	public float getTempValue()
	{
		return tempValue;
	}

	public void setTempValue(float tempValue)
	{
		this.tempValue = tempValue;
	}

	public float getHumValue()
	{
		return humValue;
	}

	public void setHumValue(float humValue)
	{
		this.humValue = humValue;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public boolean isCharge()
	{
		return isCharge;
	}

	public void setCharge(boolean isCharge)
	{
		this.isCharge = isCharge;
	}

	public byte getSensorState()
	{
		return sensorState;
	}

	public void setSensorState(byte sensorState)
	{
		this.sensorState = sensorState;
	}

	public boolean isInfraredSwitch()
	{
		return infraredSwitch;
	}

	public void setInfraredSwitch(boolean infraredSwitch)
	{
		this.infraredSwitch = infraredSwitch;
	}

	public int getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(int deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getMemberId()
	{
		return memberId;
	}

	public void setMemberId(String memberId)
	{
		this.memberId = memberId;
	}

	public String getSsuid()
	{
		return ssuid;
	}

	public void setSsuid(String ssuid)
	{
		this.ssuid = ssuid;
	}

	public int getDeviceChannel2()
	{
		return deviceChannel2;
	}

	public void setDeviceChannel2(int deviceChannel2)
	{
		this.deviceChannel2 = deviceChannel2;
	}

	@Override
	public int compareTo(DeviceInfo another)
	{
		if (another.deviceState == (byte) 0xaa)
		{
			return -1;
		}
		else
		{
			return 1;
		}

	}

}
