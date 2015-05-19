package com.remote.util;

public class IPCameraInfo {
	public int id;//本地数据库ID
	public int cameraId;//服务器ID
	public int devType;
	public String devName;
	public String ip;//内网ip
	public String ip2;//外网ip
	
	public int streamType;
	public int webPort;
	public int mediaPort;
	public String userName;
	public String password;
	public String uid;
	public String devSetName;//自己设置的名称
	public String groupName;//分组名称
	public int groupId;//分组ID
	public String thumPath;//缩略图
	public boolean isOnLine = false;
}
