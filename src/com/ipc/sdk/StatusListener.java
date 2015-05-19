package com.ipc.sdk;

public interface StatusListener {
	public static final int STATUS_LOGIN_SUCCESS = 0x00;
	public static final int STATUS_LOGIN_FAIL_USR_PWD_ERROR = 0x01;
	public static final int STATUS_LOGIN_FAIL_ACCESS_DENY = 0x02;
	public static final int STATUS_LOGIN_FAIL_EXCEED_MAX_USER = 0x03;
	public static final int STATUS_LOGIN_FAIL_CONNECT_FAIL = 0x04;
	
	// talk
	public static final int FS_API_STATUS_OPEN_TALK_SUCCESS = 0x30;
	public static final int FS_API_STATUS_OPEN_TALK_FAIL_ACCESS_DENY = 0x31;
	public static final int FS_API_STATUS_OPEN_TALK_FAIL_USED_BY_ANOTHER_USER = 0x32;
	public static final int FS_API_STATUS_CLOSE_TALK_SUCCESS = 0x33;
	public static final int FS_API_STATUS_CLOSE_TALK_FAIL = 0x34;
	

	
	public void OnStatusCbk(int statusID, int reserve1, int reserve2, int reserve3, int reserve4);
}
