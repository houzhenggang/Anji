package com.anji.www.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.database.MergeCursor;
import android.text.TextUtils;

import com.anji.www.activity.MyApplication;
import com.anji.www.constants.Url;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.GatewayResponse;
import com.anji.www.entry.GroupInfo;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.entry.SceneInfo;
import com.anji.www.entry.Version;
import com.anji.www.util.CMHttpManager;
import com.remote.util.IPCameraInfo;

public class NetReq
{
	/**
	 * 注册第一步
	 */
	public static ResponseBase registerOne(String name, String password)
	{
		String json = CMHttpManager.post(Url.registerOne, "username", name,
				"password", password);
		try
		{
			ResponseBase member = JsonParserFactory.parseBaseInfo(json);
			return member;
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 注册第二步
	 */
	public static ResponseBase registerTwo(String phoneNmu, String username)
	{
		return CMHttpManager.post(Url.registerTwo, ResponseBase.class,
				"mobile", phoneNmu, "username", username);
	}

	/**
	 * 注册第三步，验证验证码。
	 */
	public static Member registerThree(String memberId, String mobile,
			String validCode, Context context, boolean isSave)
	{
		String json = CMHttpManager.post(Url.registerThree, "memberId",
				memberId, "mobile", mobile, "validCode", validCode);

		try
		{
			Member member = JsonParserFactory.parseMemberInfo(json, context,
					isSave);
			return member;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 注册第四步，绑定网关。
	 */
	public static ResponseBase registerFour(String ssuid, String username,
			String sessionId)
	{
		return CMHttpManager.post(Url.registerFour, ResponseBase.class,
				"ssuid", ssuid, "username", username, "sessionId", sessionId);

	}

	/**
	 * 登陆。
	 */
	public static Member Login(String username, String password, Context context)
	{
		String json = CMHttpManager.post(Url.login, "username", username,
				"password", password);
		try
		{
			Member member = JsonParserFactory.parseMemberInfo(json, context,
					true);

			return member;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 修改密码。
	 */
	public static ResponseBase changePasswor(String memberId, String password,
			String newPassword, String sessionId)
	{
		return CMHttpManager.post(Url.changePassword, ResponseBase.class,
				"memberId", memberId, "password", password, "newPassword",
				newPassword, "sessionId", sessionId);

	}

	/**
	 * 忘记密码，获取验证码。
	 */
	public static ResponseBase getForgetCode(String mobile)
	{
		return CMHttpManager.post(Url.getForgetCode, ResponseBase.class,
				"mobile", mobile);
	}

	/**
	 * 验证忘记密码验证码
	 */
	public static ResponseBase verifyForgetCode(String mobile, String validCode)
	{
		String json = CMHttpManager.post(Url.verifyForgetCode, "mobile",
				mobile, "validCode", validCode);
		try
		{
			ResponseBase member = JsonParserFactory.parseBaseInfo(json);
			return member;
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 重置密码。
	 */
	public static Member restPassword(String memberId, String password,
			Context context)
	{
		String json = CMHttpManager.post(Url.restPassword, "memberId",
				memberId, "password", password);
		try
		{
			Member member = JsonParserFactory.parseMemberInfo(json, context,
					true);

			return member;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询所有开关。
	 */
	public static List<DeviceInfo> qurryAllSwitch(String memberId, String ssuid)
	{
		String json = CMHttpManager.post(Url.qurryAllSwitch, "memberId",
				memberId, "ssuid", ssuid);
		try
		{
			List<DeviceInfo> deviceList = JsonParserFactory
					.parseSwitchList(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询所有传感。
	 */
	public static List<DeviceInfo> qurryAllSence(String memberId, String ssuid)
	{
		String json = CMHttpManager.post(Url.qurryAllSence, "memberId",
				memberId, "ssuid", ssuid);
		try
		{
			List<DeviceInfo> deviceList = JsonParserFactory
					.parseSenceList(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询所有摄像头。
	 */
	public static ArrayList<IPCameraInfo> qurryAllCamera(String memberId,
			String ssuid)
	{
		String json = CMHttpManager.post(Url.qurryAllCamera, "memberId",
				memberId, "ssuid", ssuid);
		try
		{
			ArrayList<IPCameraInfo> deviceList = JsonParserFactory
					.parseCameraList(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询所有分组。
	 */
	public static ArrayList<GroupInfo> qurryAllGroup(String memberId,
			String ssuid)
	{
		String json = CMHttpManager.post(Url.qurryAllGroup, "memberId",
				memberId, "ssuid", ssuid);
		try
		{
			ArrayList<GroupInfo> deviceList = JsonParserFactory
					.parseGroupList(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 新增组。
	 */
	public static GroupInfo addGroup(String memberId, String groupName,
			String sessionId, String switchs, String sensors, String cameras,
			String ssuid, String iconType)
	{
		StringBuffer buffer = new StringBuffer(Url.addGroup);
		buffer.append("?").append("memberId=").append(memberId).append("&")
				.append("groupName=").append(groupName).append("&")
				.append("sessionId=").append(sessionId).append("&")
				.append("ssuid=").append(ssuid).append("&").append("iconType=")
				.append(iconType).append("&");
		if (!TextUtils.isEmpty(switchs))
		{
			buffer.append("switchs=").append(switchs).append("&");
		}
		if (!TextUtils.isEmpty(sensors))
		{
			buffer.append("sensors=").append(sensors).append("&");
		}
		if (!TextUtils.isEmpty(cameras))
		{
			buffer.append("cameras=").append(cameras);
		}
		String json = CMHttpManager.post(buffer.toString());
		try
		{
			GroupInfo deviceList = JsonParserFactory.parseAddGroup(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 为分组添加开关
	 */
	public static ResponseBase addSwtichToGroup(String username,
			String sessionId, String switchId, String groupId)
	{
		return CMHttpManager.post(Url.addSwitchToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "switchId",
				switchId, "groupId", groupId);
	}

	/**
	 * 为分移除开关
	 */
	public static ResponseBase deleteSwtichToGroup(String username,
			String sessionId, String switchId, String groupId)
	{
		return CMHttpManager.post(Url.deleteSwitchToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "switchId",
				switchId, "groupId", groupId);
	}

	/**
	 * 为分组添加传感
	 */
	public static ResponseBase addSensorToGroup(String username,
			String sessionId, String sensorId, String groupId)
	{
		return CMHttpManager.post(Url.addSensorToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "sensorId",
				sensorId, "groupId", groupId);
	}

	/**
	 * 为分移除传感
	 */
	public static ResponseBase deleteSensorToGroup(String username,
			String sessionId, String sensorId, String groupId)
	{
		return CMHttpManager.post(Url.deleteSensorToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "sensorId",
				sensorId, "groupId", groupId);
	}

	/**
	 * 为分组添加摄像头
	 */
	public static ResponseBase addCameraToGroup(String username,
			String sessionId, String camerarId, String groupId)
	{
		return CMHttpManager.post(Url.addCameraToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "cameraId",
				camerarId, "groupId", groupId);
	}

	/**
	 * 为分移除摄像头
	 */
	public static ResponseBase deleteCameraToGroup(String username,
			String sessionId, String camerarId, String groupId)
	{
		return CMHttpManager.post(Url.deleteCameraToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "cameraId",
				camerarId, "groupId", groupId);
	}

	/**
	 * 关闭/开启某一个开关 1：开 0：关
	 */
	public static ResponseBase controlSwitch(String username, String sessionId,
			String switchId, String operType)
	{
		return CMHttpManager.post(Url.switchCotrol, ResponseBase.class,
				"username", username, "sessionId", sessionId, "switchId",
				switchId, "operType", operType);
	}

	/**
	 * 关闭/开启所有开关 1：开 0：关
	 */
	public static ResponseBase controlAllSwitch(String username,
			String sessionId, String ssuid, String operType)
	{
		return CMHttpManager.post(Url.switchAllCotrol, ResponseBase.class,
				"username", username, "sessionId", sessionId, "ssuid", ssuid,
				"operType", operType);
	}

	/**
	 * 查询用户下的所有网关
	 */
	public static ArrayList<GatewayResponse> getAllSsiud(String memberId)
	{
		String json = CMHttpManager.post(Url.getAllGateway, "memberId",
				memberId);
		try
		{
			ArrayList<GatewayResponse> deviceList = JsonParserFactory
					.parseGatewayList(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 切换网关
	 */
	public static ResponseBase switchSsuid(String memberId, String ssuid)
	{
		return CMHttpManager.post(Url.changeGateway, ResponseBase.class,
				"memberId", memberId, "ssuid", ssuid);
	}

	/**
	 * 删除分组
	 */
	public static ResponseBase deleteGroup(String memberId, String groupId,
			String sessionId)
	{
		return CMHttpManager.post(Url.deleteGroup, ResponseBase.class,
				"memberId", memberId, "groupId", groupId, "sessionId",
				sessionId);
	}

	/**
	 * 关闭/开启所有开关 1：开 0：关
	 */
	public static ResponseBase controlGroupSwitch(String username,
			String sessionId, String ssuid, String groupId, String operType)
	{
		return CMHttpManager.post(Url.switchAllCotrol, ResponseBase.class,
				"username", username, "sessionId", sessionId, "ssuid", ssuid,
				"groupId", groupId, "operType", operType);
	}

	/**
	 * 添加摄像头
	 */
	public static ResponseBase uploadCamera(String username, String account,
			String sessionId, String uid, String password, String name,
			String ssuid, String port1, String port2)
	{
		// String json = CMHttpManager.post(Url.uploadCamera, "username",
		// username, "sessionId", sessionId, "uid", uid, "password",
		// password, "name", name, "ssuid", ssuid, "port1", port1,
		// "port2", port2);

		StringBuffer buffer = new StringBuffer(Url.uploadCamera);
		buffer.append("?").append("sessionId=").append(sessionId).append("&")
				.append("uid=").append(uid).append("&").append("name=")
				.append(name).append("&").append("ssuid=").append(ssuid)
				.append("&").append("port1=").append(port1).append("&")
				.append("port2=").append(port2).append("&").append("username=")
				.append(username).append("&");
		if (!TextUtils.isEmpty(account))
		{
			buffer.append("account=").append(account).append("&");
		}
		if (!TextUtils.isEmpty(password))
		{
			buffer.append("password=").append(password);
		}
		String json = CMHttpManager.post(buffer.toString());

		try
		{
			ResponseBase member = JsonParserFactory.parseUploadCamera(json);
			return member;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除设备 deviceType 1:开关 2：传感器 3：摄像头
	 */
	public static ResponseBase deleteDevice(String ssuid, String deviceId,
			String deviceType)
	{
		return CMHttpManager.post(Url.deleteDevice, ResponseBase.class,
				"ssuid", ssuid, "deviceId", deviceId, "deviceType", deviceType);
	}

	/**
	 * 组网
	 */
	public static ResponseBase groupNet(String ssuid)
	{
		return CMHttpManager.post(Url.groupNet, ResponseBase.class, "ssuid",
				ssuid);
	}

	/**
	 * 控制红外总开关 1：开（设防） 2：关（不设防）
	 */
	public static ResponseBase ControlAllInfrared(String username,
			String sessionId, String operType, String ssuid)
	{
		return CMHttpManager.post(Url.controlAllInfrared, ResponseBase.class,
				"username", username, "sessionId", sessionId, "operType",
				operType, "ssuid", ssuid);
	}

	/**
	 * 控制红外分组开关 1：开（设防） 2：关（不设防）
	 */
	public static ResponseBase ControlGroupInfrared(String username,
			String sessionId, String operType, String groupId)
	{
		return CMHttpManager.post(Url.controlGroupInfrared, ResponseBase.class,
				"username", username, "sessionId", sessionId, "operType",
				operType, "groupId", groupId);
	}

	/**
	 * 查询红外总开关。
	 */
	public static ResponseBase qurryAllInfrared(String ssuid)
	{
		return CMHttpManager.post(Url.qurryAllInfrared, ResponseBase.class,
				"ssuid", ssuid);
	}

	/**
	 * 编辑分组名称和头像。
	 */
	public static ResponseBase editGroup(String groupId, String newGroupName,
			String iconType)
	{
		return CMHttpManager.post(Url.editGroup, ResponseBase.class, "groupId",
				groupId, "newGroupName", newGroupName, "iconType", iconType);
	}

	/**
	 * 编辑开关。
	 */
	public static ResponseBase editSwitch(String switchId, String groupId,
			String newGroupId, String name, String ssuid, String username,
			String sessionId)
	{
		return CMHttpManager.post(Url.editSwitch, ResponseBase.class,
				"switchId", switchId, "groupId", groupId, "newGroupId",
				newGroupId, "name", name, "ssuid", ssuid, "username", username,
				"sessionId", sessionId);
	}
	/**
	 * 编辑摄像头。
	 */
	public static ResponseBase editCameraPass(String cameraId, String username,
			String sessionId, String ssuid, String password)
	{
		return CMHttpManager.post(Url.editCamerePass, ResponseBase.class,
				"cameraId", cameraId, "username", username, "sessionId",
				sessionId, "ssuid", ssuid, "password", password);
	}

	/**
	 * 编辑传感。
	 */
	public static ResponseBase editSensor(String sensorId, String groupId,
			String newGroupId, String name, String ssuid, String username,
			String sessionId)
	{
		return CMHttpManager.post(Url.editSensor, ResponseBase.class,
				"sensorId", sensorId, "groupId", groupId, "newGroupId",
				newGroupId, "name", name, "ssuid", ssuid, "username", username,
				"sessionId", sessionId);
	}

	/**
	 * 编辑摄像头。
	 */
	public static ResponseBase editCamera(String cameraId, String groupId,
			String newGroupId, String name, String ssuid, String username,
			String sessionId)
	{
		return CMHttpManager.post(Url.editCamere, ResponseBase.class,
				"cameraId", cameraId, "groupId", groupId, "newGroupId",
				newGroupId, "name", name, "ssuid", ssuid, "username", username,
				"sessionId", sessionId);
	}

	/**
	 * 编辑摄像头。
	 */
	public static ResponseBase removeGateway(String username, String ssuid)
	{
		return CMHttpManager.post(Url.removeGateway, ResponseBase.class,
				"username", username, "ssuid", ssuid);
	}

	/**
	 * 查询分组下的所有信息
	 */
	public static String getAllGoupInfo(String groupId)
	{
		return CMHttpManager.post(Url.getGroupDeviceInfo, "groupId", groupId);
	}

	/**
	 * 获取版本更新信息
	 */
	public static Version getUpdateInfo()
	{
		String json = CMHttpManager.post(Url.getUpdataInfo);
		try
		{
			Version member = JsonParserFactory.parseVersionInfo(json);
			return member;
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询所有情景。
	 */
	public static ArrayList<SceneInfo> qurryAllScene( String ssuid )
	{
		String json = CMHttpManager.post(Url.qurryAllScene, "ssuid", ssuid);
		try
		{
			ArrayList<SceneInfo> deviceList = JsonParserFactory
					.parseSceneList(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询情景模式所有开关。
	 */
	public static List<DeviceInfo> qurrySceneSwitch(String memberId, String ssuid)
	{
		String json = CMHttpManager.post(Url.qurrySceneSwitch, "memberId",
				memberId, "ssuid", ssuid);
		try
		{
			List<DeviceInfo> deviceList = JsonParserFactory
					.parseSwitchList(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询情景模式所有传感。
	 */
	public static List<DeviceInfo> qurrySceneSensor(String memberId, String ssuid)
	{
		String json = CMHttpManager.post(Url.qurrySceneSensor, "memberId",
				memberId, "ssuid", ssuid);
		try
		{
			List<DeviceInfo> deviceList = JsonParserFactory
					.parseSenceList(json);

			return deviceList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
