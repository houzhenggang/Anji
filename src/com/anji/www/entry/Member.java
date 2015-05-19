package com.anji.www.entry;

import java.util.ArrayList;
import java.util.List;

/**
 * ��Ա��Ϣ��
 * 
 * @author Administrator
 */
public class Member extends ResponseBase
{

	/**
	 * ��Աid
	 */
	private String memberId;
	/**
	 * ��Ա����
	 */
	private String username;
	/**
	 * ��Ա����
	 */
	private String password;
	/**
	 * ��Ա�绰
	 */
	private String mobile;
	/**
	 * ��Ӳ���ĵ�ַ
	 */
	private String ssuid;
	/**
	 * �Ի�id
	 */
	private String sessionId;

	private List<AdInfo> adList = new ArrayList<AdInfo>();

	private List<GroupInfo> groupList= new ArrayList<GroupInfo>();

	public List<AdInfo> getAdList()
	{
		return adList;
	}

	public void setAdList(List<AdInfo> adList)
	{
		this.adList = adList;
	}

	public List<GroupInfo> getGroupList()
	{
		return groupList;
	}

	public void setGroupList(List<GroupInfo> groupList)
	{
		this.groupList = groupList;
	}

	public String getMemberId()
	{
		return memberId;
	}

	public void setMemberId(String memberId)
	{
		this.memberId = memberId;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getMobile()
	{
		return mobile;
	}

	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	public String getSsuid()
	{
		return ssuid;
	}

	public void setSsuid(String ssuid)
	{
		this.ssuid = ssuid;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

}
