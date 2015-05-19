package com.anji.www.camera.util;

/**
 * 消息代号
 * 
 * @author FuS
 * @date 2014-4-23 下午1:39:37
 */
public class Msg {
	// //////////////////////////////////////
	// 从1000开始，每个部分占用20位，剩下的用作预留字段，方便扩展
	// //////////////////////////////////////
	/** 未知异常 */
	public static final int UNKNOW = 99999;
	// 搜索camera
	/** Camera搜索中 */
	public static final int CAMERA_SEARCHING = 1000;
	/** Camera搜索结束 */
	public static final int CAMERA_SEARCHFINISH = 1001;
	/** Camera搜索超时结束 */
	public static final int CAMERA_SEARCHTIMEOUT = 1002;
	/** mycameras界面中，通过点击列表项的加号添加新的摄像头 */
	public static final int CAMERA_NEW_ADD = 1003;
	/** Camera不在线 */
	public static final int CAMERA_DISCONNECTED = 1004;
	/** Camera在线 */
	public static final int CAMERA_CONNECTED = 1005;

	// Wifi
	/** 正在打开Wifi */
	public static final int WIFI_OPENING = 1020;
	/** 正在搜索Wifi */
	public static final int WIFI_SEARCHING = 1021;
	/** 搜索Wifi失败 */
	public static final int WIFI_SEARCH_FAIL = 1022;
	/** 正在连接Wifi */
	public static final int WIFI_CONNECTTING = 1023;
	/** 连接Wifi失败 */
	public static final int WIFI_CONNECT_FAIL = 1024;
	/** 连接Wifi成功 */
	public static final int WIFI_CONNECT_SUCC = 1025;

	// 自动配置Camear
	/** 正在登录Camear */
	public static final int CAMERA_LOGINING = 1040;
	/** 登录Camear失败 */
	public static final int CAMERA_LOGIN_FAIL = 1041;
	/** 登录Camear成功 */
	public static final int CAMERA_LOGIN_SUCC = 1042;
	/** 登录超时 */
	public static final int CAMERA_LOGIN_TIMEOUT = 1043;
	/** 设备信息的mac为空 */
	public static final int CAMERA_DEV_INFO_OR_MAC_IS_NULL = 1044;

	// 加载帧
	/** 正在加载帧 */
	public static final int FRAME_LOADING = 1060;
	/** 帧加载成功 */
	public static final int FRAME_LOADING_SUCC = 1061;
	/** 帧加载失败 */
	public static final int FRAME_LOADING_FAIL = 1062;

	// 摄像头设置
	/** wifi 没打开 */
	public static final int WIFI_NOT_OPEN = 1080;
	/** 修改密码 */
	public static final int MODIFY_PWD = 1081;

	// 其他
	/** 长按图片，准备删除图片 */
	public static final int OTHER_DELETE_PHOTO = 1100;
	/** 长按视频，准备删除视频 */
	public static final int OTHER_DELETE_VIDEO = 1101;
	/** 长按告警消息，准备删除告警消息 */
	public static final int OTHER_DELETE_WARNING_MSG = 1102;
	/** 长按系统消息，准备删除系统消息 */
	public static final int OTHER_DELETE_SYSTEM_MSG = 1103;
	/** 隐藏 删除布局 */
	public static final int OTHER_HIDE_CHECHBOX = 1104;
	/** 刷新camera列表 */
	public static final int OTHER_REFRESH_CAMERA_LIST = 1105;

	// 视频播放
	/** 已经接收到视频数据，准备播放 */
	public static final int VIDEO_READY_PLAY = 1120;
	/** 视频需要重新连接 */
	public static final int VIDEO_RECONNECTED = 1121;
	/** 视频播放的过程中，需要抓拍一张图片 */
	public static final int VIDEO_SNAP_SUCC = 1122;
	/** 视频播放的过程中，录像时需要抓拍一张图片 */
	public static final int RECORD_SNAP_SUCC = 1123;
	/** 抓取一帧，进入视频预览界面时，需要默认抓取一帧作为缓存 */
	public static final int SNAP_SUCC = 1124;

	/** 录像button 更新背景 */
	public static final int IMAGE_RECORD = 1140;
	public static final int IMAGE_UNRECORD = 1141;

	/** 获取ipc 所配置的wifi */
	public static final int GET_WIFI_SUCC = 1160;
	public static final int GET_WIFI_FAIL = 1161;
	/**设置Fosbaby usertag***/
	public static final int SET_USER_TAG_SUCC = 1162;
	public static final int SET_USER_TAG_FAIL = 1163;
	/**设置Fosbaby usertag***/
	public static final int SET_PUSH_SERVER_SUCC = 1164;
	public static final int SET_PUSH_SERVER_FAIL = 1165;
	
	
	

	/** 刷新显示camara name的TextView **/
	public static final int NEW_NAME = 1180;

	/** 显示帮助UI popwindow **/
	public static final int SHOW_HELP_UI = 1200;

	// fosbaby wifi 连接
	/** 连接fosbaby wifi失败 */
	public static final int FOSBABY_WIFI_CONN_FAIL = 1220;
	/** 连接fosbaby wifi成功 */
	public static final int FOSBABY_WIFI_CONN_SUCC = 1221;
	/** 连接fosbaby wifi中 */
	public static final int FOSBABY_WIFI_CONNNING = 1222;

	// 云服务相关

	// /** 未知错误 */
	public static final int CLOUD_UNKNOW_ERR = 1240;
	// /** 成功 */
	// public static final int CLOUD_SUCCESS = 1241;
	// /** 失败 */
	// public static final int CLOUD_FAILED = 1242;
	// /** 临时token错误 */
	// public static final int CLOUD_TEMP_TOKEN_ERROR = 1243;
	// /** Access Token错误 */
	// public static final int CLOUD_ACCESS_TOKEN_ERROR = 1244;
	// /** 验证码错误 */
	// public static final int CLOUD_CAPTCHA_ERROR = 1245;
	// /** 参数为空 */
	// public static final int CLOUD_PARAMETER_EMPTY = 1246;
	// /** 无效参数 */
	// public static final int CLOUD_INVALID_PARAMETER = 1247;
	// /** Refresh Token错误 */
	// public static final int CLOUD_REFRESH_TOKEN_ERROR = 1248;
	// /** json字符串格式错误 */
	// public static final int CLOUD_JSON_FORMAT_ERROR = 1249;
	// /** Access Token已过期 */
	// public static final int CLOUD_ACCESS_TOKEN_EXPIRED = 1250;
	// /** 没有权限 */
	// public static final int CLOUD_NO_PERMISSIONS = 1251;
	// /** 文件上传失败 */
	// public static final int CLOUD_FILE_UPLOAD_FAILED = 1252;
	// /** 获取文件后缀名失败 */
	// public static final int CLOUD_GET_FILE_POSTFIX_FAILED = 1253;
	// /** 邮件发送成功 */
	// public static final int CLOUD_EMAIL_SUCCESS = 1254;
	// /** 邮件发送失败 */
	// public static final int CLOUD_EMAIL_FAILED = 1255;
	// /** 邮件标题为空 */
	// public static final int CLOUD_EMAIL_SUBJECT_EMPTY = 1256;
	// /** 邮件内容为空 */
	// public static final int CLOUD_EMAIL_MESSAGE_EMPTY = 1257;
	// /** SQL异常 */
	// public static final int CLOUD_SQLEXCEPTION = 1258;
	// /** SQL连接失败 */
	// public static final int CLOUD_CONNECT_FAILED = 1259;
	// /** IO异常 */
	// public static final int CLOUD_IOEXCEPTION = 1260;
	// /** 找不到class异常，比如说服务端缺库 */
	// public static final int CLOUD_CLASSNOTFOUNDEXCEPTION = 1261;
	// /** 服务正在处理，请稍后... */
	// public static final int CLOUD_SUBMISSION_PROCESSING = 1262;
	// /** 服务未响应 */
	// public static final int CLOUD_SERVICE_NOREPONSE = 1263;
	// /** 系统繁忙 */
	// public static final int CLOUD_SYSTEM_BUSY = 1264;
	// /** 该api尚未实现 */
	// public static final int CLOUD_SERVICE_NOT_IMPLEMENT = 1265;
	// /** 账号不存在 */
	// public static final int CLOUD_ACCOUNT_NOT_EXISTS = 1266;
	// /** 账号已存在 */
	// public static final int CLOUD_ACCOUNT_EXISTS = 1267;
	// /** 账号为空 */
	// public static final int CLOUD_ACCOUNT_EMPTY = 1268;
	// /** 账号格式错误 */
	// public static final int CLOUD_ACCOUNT_FORMAT_ERROR = 1269;
	// /** 密码为空 */
	// public static final int CLOUD_PASSWORD_EMPTY = 1270;
	// /** 密码错误 */
	// public static final int CLOUD_PASSWORD_ERROR = 1271;
	// /** 密码输入不一致 */
	// public static final int CLOUD_PWD_MATCH_ERROR = 1272;
	// /** 密码格式错误 */
	// public static final int CLOUD_PWD_FORMAT_ERROR = 1273;
	// /** 邮箱为空 */
	// public static final int CLOUD_EMAIL_EMPTY = 1274;
	// /** 邮箱格式错误 */
	// public static final int CLOUD_EMAIL_FORMAT_ERROR = 1275;
	// /** 邮箱不存在（账户注册） */
	// public static final int CLOUD_EMAIL_NOT_EXISTS = 1276;
	// /** 邮箱已存在（账户注册） */
	// public static final int CLOUD_EMAIL_EXISTS = 1277;
	// /** 账号与邮箱不匹配（重置密码） */
	// public static final int CLOUD_EMAIL_MATCH_ERROR = 1278;
	// /** 无效邀请码（账户注册） */
	// public static final int CLOUD_INVALID_INVITATIONCODE = 1279;
	// /** 账号已激活（账户激活） */
	// public static final int CLOUD_ACCOUNT_ALREADY_ACTIVATION = 1280;
	// /** 无效激活码（账户激活） */
	// public static final int CLOUD_INVALID_ACTIVATIONCODE = 1281;
	// /** 无效授权码（重置密码） */
	// public static final int CLOUD_INVALID_AUTHCODE = 1282;
	// /** 新旧密码未改变（重置密码） */
	// public static final int CLOUD_PWD_NO_CHANGE = 1283;
	// /** Uid为空（Uid登陆） */
	// public static final int CLOUD_UID_EMPTY = 1284;
	// /** Uid格式错误（Uid登陆） */
	// public static final int CLOUD_UID_FORMAT_ERROR = 1285;
	// /** 激活码已过期 */
	// public static final int CLOUD_ACTIVATIONCODE_EXPIRED = 1286;
	// /** 不支持的授权类型 */
	// public static final int CLOUD_UNSUPPORTED_GRANT_TYPE = 1287;
	// /** 开发者的appId不存在 */
	// public static final int CLOUD_CLIENT_ID_NOT_EXISTS = 1288;
	// /** 开发者的appId为空 */
	// public static final int CLOUD_CLIENT_ID_EMPTY = 1289;
	// /** 开发者的appId格式错误 */
	// public static final int CLOUD_CLIENT_ID_FORMAT_ERROR = 1290;
	// /** 开发者的secret不存在 */
	// public static final int CLOUD_CLIENT_SECRET_NOT_EXISTS = 1291;
	// /** 开发者的secret为空 */
	// public static final int CLOUD_CLIENT_SECRET_EMPTY = 1292;
	// /** 开发者的secret格式错误 */
	// public static final int CLOUD_CLIENT_SECRET_FORMAT_ERROR = 1293;
	// /** 开发者的secret错误 */
	// public static final int CLOUD_CLIENT_SECRET_ERROR = 1294;
	// /** 邀请码为空 */
	// public static final int CLOUD_INVITATIONCODE_EMPTY = 1295;
	// /** 邀请码格式错误 */
	// public static final int CLOUD_INVITATIONCODE_FORMAT_ERROR = 1296;
	// /** 邀请码过期 */
	// public static final int CLOUD_INVITATIONCODE_EXPIRED = 1297;
	// /** 激活码为空 */
	// public static final int CLOUD_ACTIVATIONCODE_EMPTY = 1298;
	// /** 激活码格式错误 */
	// public static final int CLOUD_ACTIVATIONCODE_FORMAT_ERROR = 1299;
	// /** 账号未激活 */
	// public static final int CLOUD_ACCOUNT_NOT_ACTIVATION = 1300;
	// /** 授权码为空 */
	// public static final int CLOUD_AUTHCODE_EMPTY = 1301;
	// /** 授权码格式错误 */
	// public static final int CLOUD_AUTHCODE_FORMAT_ERROR = 1302;
	// /** 授权码过期 */
	// public static final int CLOUD_AUTHCODE_EXPIRED = 1303;
	// /** 授权码存在 */
	// public static final int CLOUD_AUTHCODE_EXISTS = 1304;
	// /** 语言参数不存在 */
	// public static final int CLOUD_LANGUAGE_NOT_EXISTS = 1305;
	// /** 不支持的语言类型 */
	// public static final int CLOUD_UNSUPPORTED_LANGUAGE_TYPE = 1306;
	// /** 邮箱地址无效 */
	// public static final int CLOUD_INVALID_EMAIL = 1307;
	// /** 用户名密码错误 */
	// public static final int CLOUD_ACCOUNT_PASSWORD_ERROR = 1308;
	// /** 设备不存在 */
	// public static final int CLOUD_DEV_NOT_EXISTS = 1309;
	// /** 设备已存在 */
	// public static final int CLOUD_DEV_EXISTS = 1310;
	// /** 设备为空 */
	// public static final int CLOUD_DEV_EMPTY = 1311;
	// /** 设备格式错误 */
	// public static final int CLOUD_DEV_FORMAT_ERROR = 1312;
	// /** 设备账号为空 */
	// public static final int CLOUD_DEV_ACCOUNT_EMPTY = 1313;
	// /** 设备账号格式错误 */
	// public static final int CLOUD_DEV_ACCOUNT_FORMAT_ERROR = 1314;
	// /** 设备密码为空 */
	// public static final int CLOUD_DEV_PASSWORD_EMPTY = 1315;
	// /** 设备密码格式错误 */
	// public static final int CLOUD_DEV_PWD_FORMAT_ERROR = 1316;
	// /** 设备数达到上限 */
	// public static final int CLOUD_MAX_DEVS_REACHED = 1317;

	/** 未处理 */
	public static final int CLOUD_UNPROCESS = 1400;
	/** 成功 */
	public static final int CLOUD_SUCCESS = 1401;
	/** 失败 */
	public static final int CLOUD_FAILED = 1402;
	/** 处理中 */
	public static final int CLOUD_PROCESSING = 1403;
	/** Access Token错误 */
	public static final int CLOUD_ACCESS_TOKEN_ERROR = 1404;
	/** 参数为空 */
	public static final int CLOUD_PARAMETER_EMPTY = 1405;
	/** 无效参数 */
	public static final int CLOUD_INVALID_PARAMETER = 1406;
	/** Refresh Token错误 */
	public static final int CLOUD_REFRESH_TOKEN_ERROR = 1407;
	/** json字符串格式错误 */
	public static final int CLOUD_JSON_FORMAT_ERROR = 1408;
	/** Access Token已过期 */
	public static final int CLOUD_ACCESS_TOKEN_EXPIRED = 1409;
	/** 没有权限 */
	public static final int CLOUD_NO_PERMISSIONS = 1410;
	/** 授权认证失败 */
	public static final int CLOUD_AUTH_CERTIFICATION_FAILED = 1411;
	/** 临时token错误 */
	public static final int CLOUD_TEMP_TOKEN_ERROR = 1412;
	/** 验证码错误 */
	public static final int CLOUD_CAPTCHA_ERROR = 1413;
	/** 福斯康姆内部错误 */
	public static final int CLOUD_SYSTEM_ERROR = 1414;
	/** http请求contentType错误 */
	public static final int CLOUD_HTTP_CONTENT_TYPE_IMPROPER = 1415;
	/** http请求的GET/POST方式错误 */
	public static final int CLOUD_HTTP_REQUEST_MODE_IMPROPER = 1416;
	/** 邮件发送成功 */
	public static final int CLOUD_EMAIL_SUCCESS = 1417;
	/** 邮件发送失败 */
	public static final int CLOUD_EMAIL_FAILED = 1418;
	/** 邮件标题为空 */
	public static final int CLOUD_EMAIL_SUBJECT_EMPTY = 1419;
	/** 邮件内容为空 */
	public static final int CLOUD_EMAIL_MESSAGE_EMPTY = 1420;
	/** SQL异常 */
	public static final int CLOUD_SQLEXCEPTION = 1421;
	/** SQL连接失败 */
	public static final int CLOUD_CONNECT_FAILED = 1422;
	/** IO异常 */
	public static final int CLOUD_IOEXCEPTION = 1423;
	/** 找不到class异常，比如说服务端缺库 */
	public static final int CLOUD_CLASSNOTFOUNDEXCEPTION = 1424;
	/** 服务正在处理，请稍后... */
	public static final int CLOUD_SUBMISSION_PROCESSING = 1425;
	/** 服务未响应 */
	public static final int CLOUD_SERVICE_NOREPONSE = 1426;
	/** 系统繁忙 */
	public static final int CLOUD_SYSTEM_BUSY = 1427;
	/** 该api尚未实现 */
	public static final int CLOUD_SERVICE_NOT_IMPLEMENT = 1428;
	// ################## 门户返回码定义 ##################
	/** 网盘异常 */
	public static final int CLOUD_PT_ONLINE_DISK_EXCEPTION = 1429;
	/** 名称已存在 */
	public static final int CLOUD_PT_DUPLICATE_NAME = 1430;
	// ################## 账户返回码定义 ##################
	/** username not exists */
	public static final int CLOUD_ACCOUNT_USERNAEM_NOT_EXISTS = 1431;
	/** username already exists */
	public static final int CLOUD_ACCOUNT_USERNAME_ALREADY_EXISTS = 1432;
	/** userId not match */
	public static final int CLOUD_ACCOUNT_USERID_NOT_EXISTS = 1433;
	/** password not match */
	public static final int CLOUD_ACCOUNT_PASSWORD_NOT_MATCH = 1434;
	/** user ipcsetting already exists */
	public static final int CLOUD_ACCOUNT_USER_IPC_SETTING_ALREADY_EXISTS = 1435;
	/** user ipcsetting not exists */
	public static final int CLOUD_ACCOUNT_USER_IPC_SETTING_NOT_EXISTS = 1436;
	/** user invitationcode invalid */
	public static final int CLOUD_ACCOUNT_USER_INVITATION_CODE_INVALID = 1437;
	/** resetpwd code invalid */
	public static final int CLOUD_ACCOUNT_USER_RESET_PASSWORD_CODE_INVALID = 1438;
	/** activation code invalid */
	public static final int CLOUD_ACCOUNT_USER_ACTIVATION_CODE_INVALID = 1439;
	/** user account has not be activated */
	public static final int CLOUD_ACCOUNT_NOT_ACTIVATED = 1456;
	/** user account already activated */
	public static final int CLOUD_ACCOUNT_ALREADY_ACTIVATED = 1457;
	/** email send error */
	public static final int CLOUD_EMAIL_SEND_ERROR = 1458;

	// ################## 授权认证返回码定义 ##################
	/** appkey not exists */
	public static final int CLOUD_AUTH_APPKEY_NOT_EXISTS = 1440;
	/** appsecret not match appkey */
	public static final int CLOUD_AUTH_APPSECRET_NOT_MATCH = 1441;
	/** openid not exists */
	public static final int CLOUD_AUTH_OPENID_NOT_EXISTS = 1442;
	/** openid not match appkey */
	public static final int CLOUD_AUTH_OPENID_NOT_MATCH = 1443;
	/** accesstoken not match openid */
	public static final int CLOUD_AUTH_ACCESSTOKEN_NOT_MATCH = 1444;
	/** accesstoken expired */
	public static final int CLOUD_AUTH_ACCESSTOKEN_EXPIRED = 1445;
	/** refreshtoken not match openid */
	public static final int CLOUD_AUTH_REFRESHTOEKN_NOT_MATCH = 1446;
	/** refreshtoken expired */
	public static final int CLOUD_AUTH_REFRESHTOEKN_EXPIRED = 1447;
	/** username not exists */
	public static final int CLOUD_AUTH_USERNAME_NOT_EXISTS = 1448;
	/** password not match */
	public static final int CLOUD_AUTH_PASSWORD_NOT_MATCH = 1449;
	/** api has not be authorized to current user */
	public static final int CLOUD_AUTH_API_HAS_NOT_BE_AUTHORIZED = 1450;
	/** user account has not be activated */
	public static final int CLOUD_AUTH_USER_ACCOUNT_HAS_NOT_BE_ACTIVATED = 1451;
	// ################## 推送返回码定义 ##################
	/** 设备已订阅 */
	public static final int CLOUD_IPC_ALREADY_SUBSCRIBED = 1452;
	/** 设备未订阅 */
	public static final int CLOUD_IPC_NOT_SUBSCRIBED = 1453;
	/** 用户已生成tag */
	public static final int CLOUD_USER_TAG_ALREADY_EXISTS = 1454;
	/** 用户tag不存在 */
	public static final int CLOUD_USER_TAG_NOT_EXISTS = 1455;
	/** 订阅操作超时 **/
	public static final int GET_PUSH_MSG_TIMEOUT = 1456;
	/** 要作订阅操作的mac 为空， 不能订阅 **/
	public static final int CAMERA_MAC_IS_NULL = 1457;
	/** 要作订阅操作的mac 为空， 不能订阅 **/
	public static final int PARSE_CAMERA_MAC_ERROR = 1458;
	/**获取user tag,消息订阅的时候用到**/
	public static final int GET_USER_TAG = 1459;
	/**返回的usertag 为空**/
	public static final int USER_TAG_IS_NULL  = 1460;
	/**成功返回的usertag **/
	public static final int GET_USER_TAG_SUCC  = 1461;
	/**获取usertag 返回未知错误 **/
	public static final int GET_USER_TAG_UNKNOWN_ERR  = 1462;
	/**获取订阅状态 返回未知错误 **/
	public static final int GET_SUBSCRIBESTATUS_UNKNOW_ERR  = 1463;
	/**获取订阅状态 成功 **/
	public static final int GET_SUBSCRIBESTATUS_SUCC  = 1464;
	/**获取订阅状态 失败 **/
	public static final int GET_SUBSCRIBESTATUS_FAIL  = 1465;
	/**mac list 为空 **/
	public static final int MAC_LIST_IS_NULL  = 1466;
	/**g_cameras 为空 **/
	public static final int GLOBAL_CAMERA_SIZE_IS_ZERO  = 1467;

	// 从1459开始

	/** 显示进度框 */
	public static final int SHOW_OPEN_MSGPUSH_PROGRESS_DIALOG = 1460;
	/** 需要显示进度框 */
	public static final int SHOW_CLOSE_MSGPUSH_PROGRESS_DIALOG = 1461;

	// ################## 订阅返回码定义 ##################
	/** 设备已订阅 */
	public static final int FC_IPC_ALREADY_SUBSCRIBED = 1480;
	/** 设备未订阅 */
	public static final int FC_IPC_NOT_SUBSCRIBED = 1481;
	/** 用户已生成tag */
	public static final int FC_USER_TAG_ALREADY_EXISTS = 1482;
	/** 用户tag不存在 */
	public static final int FC_USER_TAG_NOT_EXISTS = 1483;
	/** 订阅成功 ***/
	public static final int OPEN_MSGPUSH_SUCC = 1484;
	/** 订阅失败 ***/
	public static final int OPEN_MSGPUSH_FAIL = 1485;
	/*** 取消订阅成功 **/
	public static final int CLOSE_MSGPUSH_SUCC = 1486;
	/*** 取消订阅失败 **/
	public static final int CLOSE_MSGPUSH_FAIL = 1487;

	// 百度云摄像机
	/** 成功 */
	public static final int BDC_SUCCESS = 1600;
	/** 失败 */
	public static final int BDC_FAIL = 1601;
	/** 参数错误 */
	public static final int BDC_PARAM_ERR = 1602;
	/** 网络问题 */
	public static final int BDC_NETWORK_ERR = 1603;
	/** 获取bms服务器失败 */
	public static final int BDC_BMS_SERVER_FAIL = 1604;
	/** 当前用户没有该设备的权限 */
	public static final int BDC_NO_PERMISSION = 1605;
	/** 签名错误 */
	public static final int BDC_SIGNATURES_ERR = 1606;
	/** 设备不存在 */
	public static final int BDC_DEVICE_NOT_EXIST = 1607;
	/** 设备不存在 */
	public static final int GET_AUDIO_DATA_SUCC = 1608;

}
