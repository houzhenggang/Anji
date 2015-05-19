package com.anji.www.entry;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员信息类
 * 
 * @author Administrator
 */
public class Member extends ResponseBase
{

	/**
	 * 会员id
	 */
	private String memberId;
	/**
	 * 会员名称
	 */
	private String username;
	/**
	 * 会员密码
	 */
	private String password;
	/**
	 * 会员电话
	 */
	private String mobile;
	/**
	 * 绑定硬件的地址
	 */
	private String ssuid;
	/**
	 * 对话id
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
