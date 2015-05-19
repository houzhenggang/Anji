package com.anji.www.camera.modl;

/**
 * 摄像头类
 * 
 * @author FuS
 * @date 2014-4-15 下午7:31:02
 */
public class Camera {
	/** camera的类型，MJ */
	public static final int TYPE_CAMERA_MJ = 0x0;
	/** camera的类型，H264 */
	public static final int TYPE_CAMERA_H264 = 0x1;
	/** camera的类型，P2P */
	public static final int TYPE_CAMERA_P2P = 0x2;
	/** 主码流 */
	public static final int TYPE_STREAM_MAIN = 0x1;
	/** 子码流 */
	public static final int TYPE_STREAM_SUB = 0x0;
	/** 红外模式：自动 */
	public static final int TYPE_IR_AUTO = 0x0;
	/** 红外模式：手动 */
	public static final int TYPE_IR_MANUAL = 0x1;
	/** 红外模式：开（手动） */
	public static final int TYPE_IR_ON = 0x2;
	/** 红外模式：关（手动） */
	public static final int TYPE_IR_OFF = 0x3;

	/** 数据库中seqno字段值 */
	private int dbid = -1;
	/** name */
	private String name = "";
	/** 类型 */
	private int type = TYPE_CAMERA_H264;
	/** ip */
	private String ip = "";
	/** ddns */
	private String ddns = "";
	/** http端口 */
	private int httpPort = 0;
	/** 媒体端口(已弃用) */
	private int mediaPort = 0;
	/** uid */
	private String uid = "";
	/** mac */
	private String mac = "";
	/** 是否在线 */
	private boolean isOnline = false;
	/** 0：子码流，1：主码流 */
	private int streamType = TYPE_STREAM_SUB;
	/** 摄像头登录账户名 */
	private String loginName = "";
	/** 摄像头登录密码 */
	private String loginPwd = "";
	/** 产品型号名称（FosBaby,FI9821P） */
	private String productName = "";
	/** 产品归一化信息 */
	private CameraProductAllInfo productAllInfo = null;
	/** 服务器上存储的ipc唯一标识 */
	private String ipcid = "";
	
	/** 有没有烧 usertag 
      * 2   为写usertag进IPC成功
	 *  1   为写usertag进IPC 不成功
	 *  0  没有写usertag进IPC 
	 * */
	private int hasUserTag   = 0;

	// 这部分字段的真实值需要实时从ipc端获取，所以不必存储到数据库中
	/** 红外模式：0自动；1手动；2开（手动模式下）；3关（手动模式下） */
	private int irmode = TYPE_IR_AUTO;
	/** 是否是连接着的 */
	private boolean isConnected = false;

	public Camera() {
	}

	/**
	 * 构造函数（表示camera是从数据库中读取的）
	 * 
	 * @param seqno
	 *            数据库中的seqno字段值
	 */
	public Camera(int seqno) {
		this.dbid = seqno;
	}

	/**
	 * 构造函数
	 * 
	 * @param name
	 *            设备名称
	 * @param type
	 *            设备类型（0：MJ；1：H264；2：P2P）
	 * @param ip
	 *            ip
	 * @param streamType
	 *            码流（0：子码流；1：主码流）
	 * @param httpPort
	 * @param mediaPort
	 * @param userName
	 * @param pwd
	 * @param uid
	 */
	public Camera(String name, int type, String ip, int streamType, int httpPort, int mediaPort, String userName, String pwd, String uid) {
		this.name = name;
		this.type = type;
		this.ip = ip;
		this.streamType = streamType;
		this.httpPort = httpPort;
		this.mediaPort = mediaPort;
		this.loginName = userName;
		this.loginPwd = pwd;
		this.uid = uid;
	}

	// set
	/** 设置摄像头的seqno */
	public void setDBID(int dbid) {
		this.dbid = dbid;
	}

	/** 设置摄像头的name */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 设置摄像头的type
	 * 
	 * @param type
	 *            0：MJ，1：H264；（2：P2P）
	 */
	public void setType(int type) {
		this.type = type;
	}

	/** 设置摄像头ip */
	public void setIP(String ip) {
		this.ip = ip;
	}

	/** 设置摄像头ddns */
	public void setDDNS(String ddns) {
		this.ddns = ddns;
	}

	/** 设置摄像头httpport */
	public void setHttpPort(int port) {
		if (port < 0) {
			port = 0;
		}
		this.httpPort = port;
	}

	/** 设置摄像头媒体port */
	public void setMediaPort(int port) {
		if (port < 0) {
			port = 0;
		}
		this.mediaPort = port;
	}

	/** 设置摄像头mac地址 */
	public void setMAC(String mac) {
		this.mac = mac;
	}

	/** 设置摄像头uid */
	public void setUID(String uid) {
		this.uid = uid;
	}

	/** 设置摄像头在线or离线 */
	public void setOnline(boolean online) {
		this.isOnline = online;
	}

	/**
	 * 设置摄像头码流类型
	 * 
	 * @param streamType
	 *            0：子码流，1：主码流
	 */
	public void setStreamType(int streamType) {
		this.streamType = streamType;
	}

	/** 设置摄像头的登录账户名 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/** 设置摄像头的登录密码 */
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	/**
	 * 设置红外模式
	 * 
	 * @param irMode
	 *            0自动；1手动；2开；3关
	 */
	public void setIRMode(int irMode) {
		this.irmode = irMode;
	}

	/**
	 * 设置ipc连接与否
	 * 
	 * @param connected
	 *            true：连接；false：断开
	 */
	public void setIsConnected(boolean connected) {
		this.isConnected = connected;
	}

	/** 设置设备的产品名称（比如FosBaby,FI9821P） */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/** 设备的版本归一化信息（如果不支持，则为null） */
	public void setProductAllInfo(CameraProductAllInfo productAllInfo) {
		this.productAllInfo = productAllInfo;
	}

	/** 设置ipc的ipcid，ipcid表示该ipc存储在云服务器的唯一标识（由于云服务器允许同一个ipc以多种形态（比如uid,ip:port,ddns:port）共存，所以允许存在一个ipc有多个ipcid） */
	public void setIpcID(String ipcID) {
		this.ipcid = ipcID;
	}
	/**
	 * 
	 * @param 要烧的usertag  
	 *    2   为写usertag进IPC成功
	 *    1   为写usertag进IPC 不成功
	 *    0  没有写usertag进IPC 
	 */
	public void setHasUserTag(int hasUserTag) {
		this.hasUserTag = hasUserTag;
	}

	// get
	/** 获取摄像头的name */
	public String getName() {
		return this.name;
	}

	/**
	 * 获取摄像头的type
	 * 
	 * @return 0：MJ，1：H264，（2：P2P）
	 */
	public int getType() {
		return this.type;
	}

	/** 获取摄像头ip和port */
	public String getIP() {
		return this.ip;
	}

	/** 获取摄像头ddns和port */
	public String getDDNS() {
		return this.ddns;
	}

	public int getHttpPort() {
		return this.httpPort;
	}

	public int getMediaPort() {
		return this.mediaPort;
	}

	/** 获取摄像头uid */
	public String getUID() {
		return this.uid;
	}

	/** 获取摄像头mac地址 */
	public String getMAC() {
		return this.mac;
	}

	/** 摄像头是否在线 */
	public boolean isOnline() {
		return this.isOnline;
	}

	/**
	 * 获取摄像头码流类型
	 * 
	 * @return 0：子码流，1：主码流
	 */
	public int getStreamType() {
		return this.streamType;
	}

	/** 获取摄像头的登录账户名 */
	public String getLoginName() {
		return this.loginName;
	}

	/** 获取摄像头的登录密码 */
	public String getLoginPwd() {
		return this.loginPwd;
	}

	/** 获取数据库中的seqno字段值（等于-1表示该摄像头不是从数据库中获取的） */
	public int getDBID() {
		return this.dbid;
	}

	/**
	 * 获取红外模式
	 * 
	 * @return 0自动；1手动；2开；3关
	 */
	public int getIRMode() {
		return this.irmode;
	}

	/** 获取ipc是否连接 */
	public boolean getIsConnected() {
		return this.isConnected;
	}

	/** 获取设备的产品名称（比如FosBaby,FI9821P） */
	public String getProductName() {
		return this.productName;
	}

	/** 设备的版本归一化信息（如果不支持，则为null） */
	public CameraProductAllInfo getProductAllInfo() {
		return this.productAllInfo;
	}

	/** 获取ipc的ipcid，ipcid表示该ipc存储在云服务器的唯一标识（由于云服务器允许同一个ipc以多种形态（比如uid,ip:port,ddns:port）共存，所以允许存在一个ipc有多个ipcid） */
	public String getIpcID() {
		return this.ipcid;
	}

	/**
	 * @return
	 *  2   为写usertag进IPC成功
	 *  1   为写usertag进IPC 不成功
	 *  0  没有写usertag进IPC 
	 */
	public int getHasUserTag() {
		return hasUserTag;
	}

}
