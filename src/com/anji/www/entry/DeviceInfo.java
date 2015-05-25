package com.anji.www.entry;

import android.text.TextUtils;

public class DeviceInfo implements Comparable<DeviceInfo>
{
	private int deviceId;// �豸��ID��
	private String deviceMac;// �豸Ӳ����ַ
	private String ssuid;// ����Ӳ����ַ
	private int deviceChannel;// �豸ͨ���� ���¶�ͨ����
	private int deviceChannel2;// �豸ͨͨ����2����ʪ���ṩ ʪ��ͨ����
	private String deviceType;// �豸��������
	private int type; // �豸���� ���ػ��ߴ��� 0Ϊ���� 1Ϊ������
	private byte deviceState;// �豸״̬
	private byte sensorState;// �����豸����
	private String deviceName;// �豸����
	private String groupName;// ��������
	private int groupID;// ����id
	private int deviceBattery;// ����
	private boolean isCharge;// �Ƿ��ڳ��
	private float tempValue;// �¶�
	private float humValue;// ʪ��
	private boolean infraredSwitch;// �������Ϳ���
	private String memberId;// ���ڻ�ԱID

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
