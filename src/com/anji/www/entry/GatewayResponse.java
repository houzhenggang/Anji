package com.anji.www.entry;

public class GatewayResponse extends ResponseBase
{

	private String ssuid;
	private boolean isCurrGateway;

	public String getSsuid()
	{
		return ssuid;
	}

	public void setSsuid(String ssuid)
	{
		this.ssuid = ssuid;
	}

	public boolean isCurrGateway()
	{
		return isCurrGateway;
	}

	public void setCurrGateway(boolean isCurrGateway)
	{
		this.isCurrGateway = isCurrGateway;
	}

}
