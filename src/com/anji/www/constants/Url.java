package com.anji.www.constants;

/**
 * @deprecation:
 * @author admin
 */

public class Url
{

	public static final boolean DEBUG = true;
	/** 服务器地址 */
	public static final String webUrl = "http://www.zean.cc/";
	// public static final String webUrl = "http://www.elifey.com/web/";
	public static final String webUrl2 = "www.zean.cc";
	/** 注册第一步 */
	public static final String registerOne = webUrl + "ws_50013001";
	/** 注册第二步 获取验证码 */
	public static final String registerTwo = webUrl + "ws_50013002";
	/** 注册第三步 验证手机 */
	public static final String registerThree = webUrl + "ws_50013003";
	/** 注册第四步 绑定网关 */
	public static final String registerFour = webUrl + "ws_50014001";

	/** 会员登陆 */
	public static final String login = webUrl + "ws_50013004";

	/** 修改密码 */
	public static final String changePassword = webUrl + "ws_50013008";

	/** 忘记密码，获取验证码 */
	public static final String getForgetCode = webUrl + "ws_50013005";

	/** 忘记密码，验证验证码 */
	public static final String verifyForgetCode = webUrl + "ws_50013006";

	/** 忘记密码，重置密码 */
	public static final String restPassword = webUrl + "ws_50013007";

	/** 查询所有摄像头 */
	public static final String qurryAllCamera = webUrl + "ws_50016002";

	/** 查询所有开关 */
	public static final String qurryAllSwitch = webUrl + "ws_50017001";

	/** 查询所有传感 */
	public static final String qurryAllSence = webUrl + "ws_50018001";

	/** 查询用户下的所有组 */
	public static final String qurryAllGroup = webUrl + "ws_50015003";

	/** 新增组 */
	public static final String addGroup = webUrl + "ws_50015001";

	/** 为分组添加开关 */
	public static final String addSwitchToGroup = webUrl + "ws_50017004";

	/** 为分组移除开关 */
	public static final String deleteSwitchToGroup = webUrl + "ws_50017005";

	/** 为分组添加传感 */
	public static final String addSensorToGroup = webUrl + "ws_50018005";

	/** 为分组移除传感 */
	public static final String deleteSensorToGroup = webUrl + "ws_50018006";

	/** 为分组添加摄像头 */
	public static final String addCameraToGroup = webUrl + "ws_50016005";

	/** 为分组移除摄像头 */
	public static final String deleteCameraToGroup = webUrl + "ws_50016006";

	/** 关闭/开启某一个开关 */
	public static final String switchCotrol = webUrl + "ws_50017006";

	/** 关闭/开启所有开关 */
	public static final String switchAllCotrol = webUrl + "ws_50017007";

	/** 查询用户下的网关 */
	public static final String getAllGateway = webUrl + "ws_50014002";

	/** 网关切换 */
	public static final String changeGateway = webUrl + "ws_50014003";

	/** 控制分组下所有开关 */
	public static final String controlGroupSwitch = webUrl + "ws_50017008";
	/** 删除分组 */
	public static final String deleteGroup = webUrl + "ws_50015002";

	/** 上报摄像头 */
	public static final String uploadCamera = webUrl + "ws_50016001";

	/** 删除设备 */
	public static final String deleteDevice = webUrl + "ws_50019001";
	/** 设备组网 */
	public static final String groupNet = webUrl + "ws_50019002";

	/** 控制红外所有开关 */
	public static final String controlAllInfrared = webUrl + "ws_50018007";

	/** 控制红外分组开关 */
	public static final String controlGroupInfrared = webUrl + "ws_50018008";

	/** 查询红外开关 */
	public static final String qurryAllInfrared = webUrl + "ws_50020002";

	/** 编辑组 */
	public static final String editGroup = webUrl + "ws_50015005";
	/** 编辑开关 */
	public static final String editSwitch = webUrl + "ws_50017002";

	/** 编辑传感器 */
	public static final String editSensor = webUrl + "ws_50018002";
	/** 编辑摄像头分组和名称 */
	public static final String editCamere = webUrl + "ws_50016003";
	public static final String editCamerePass = webUrl + "ws_50016007";

	/** 解绑网关 */
	public static final String removeGateway = webUrl + "ws_50014004";

	/** 解绑网关 */
	public static final String getGroupDeviceInfo = webUrl + "ws_50015004";
	/** 获取版本更新 */
	public static final String getUpdataInfo = webUrl + "ws_50012001";
	
	/** 查询用户下的所有情景  */
	public static final String qurryAllScene = webUrl + "ws_50021001";
	
	/** 查询指定情景模式信息  */
	public static final String qurryScene = webUrl + "ws_50021002";
	
	/** 查询情景模式下灯设备列表  */
	public static final String qurrySceneDevice = webUrl + "ws_50021003";
	
	/** 查询情景模式下传感器列表  */
	public static final String qurrySceneSensor = webUrl + "ws_50021004";
	
	/** 保存情景模式（新增/修改）  */
	public static final String saveOrUpdateScene = webUrl + "ws_50021005";
	
	/** 删除情景模式  */
	public static final String deleteScene = webUrl + "ws_50021006";
}
