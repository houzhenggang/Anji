package com.anji.www.entry;

public class GroupInfo extends ResponseBase
{

	private int groupId;
	private String groupName;
	private String memberId;//���ڻ�ԱID
	private boolean infraredSwitch;//������⿪��
	
	/**
	 * ��Ӳ���ĵ�ַ
	 */
	private String ssuid;
	private String iconType;

	public int getGroupId()
	{
		return groupId;
	}

	public void setGroupId(int groupId)
	{
		this.groupId = groupId;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
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

	public String getIconType()
	{
		return iconType;
	}

	public void setIconType(String iconType)
	{
		this.iconType = iconType;
	}

	public boolean isInfraredSwitch()
	{
		return infraredSwitch;
	}

	public void setInfraredSwitch(boolean infraredSwitch)
	{
		this.infraredSwitch = infraredSwitch;
	}
}
