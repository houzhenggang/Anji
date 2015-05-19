package com.anji.www.entry;

public class ResponseBase
{
	private int responseStatus;
	private String memberId;

	public int getResponseStatus()
	{
		return responseStatus;
	}

	public void setResponseStatus(int responseStatus)
	{
		this.responseStatus = responseStatus;
	}

	public String getMemberId()
	{
		return memberId;
	}

	public void setMemberId(String memberId)
	{
		this.memberId = memberId;
	}
}
