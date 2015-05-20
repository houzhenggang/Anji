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
	 * ע���һ��
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
	 * ע��ڶ���
	 */
	public static ResponseBase registerTwo(String phoneNmu, String username)
	{
		return CMHttpManager.post(Url.registerTwo, ResponseBase.class,
				"mobile", phoneNmu, "username", username);
	}

	/**
	 * ע�����������֤��֤�롣
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
	 * ע����Ĳ��������ء�
	 */
	public static ResponseBase registerFour(String ssuid, String username,
			String sessionId)
	{
		return CMHttpManager.post(Url.registerFour, ResponseBase.class,
				"ssuid", ssuid, "username", username, "sessionId", sessionId);

	}

	/**
	 * ��½��
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
	 * �޸����롣
	 */
	public static ResponseBase changePasswor(String memberId, String password,
			String newPassword, String sessionId)
	{
		return CMHttpManager.post(Url.changePassword, ResponseBase.class,
				"memberId", memberId, "password", password, "newPassword",
				newPassword, "sessionId", sessionId);

	}

	/**
	 * �������룬��ȡ��֤�롣
	 */
	public static ResponseBase getForgetCode(String mobile)
	{
		return CMHttpManager.post(Url.getForgetCode, ResponseBase.class,
				"mobile", mobile);
	}

	/**
	 * ��֤����������֤��
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
	 * �������롣
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
	 * ��ѯ���п��ء�
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
	 * ��ѯ���д��С�
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
	 * ��ѯ��������ͷ��
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
	 * ��ѯ���з��顣
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
	 * �����顣
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
	 * Ϊ������ӿ���
	 */
	public static ResponseBase addSwtichToGroup(String username,
			String sessionId, String switchId, String groupId)
	{
		return CMHttpManager.post(Url.addSwitchToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "switchId",
				switchId, "groupId", groupId);
	}

	/**
	 * Ϊ���Ƴ�����
	 */
	public static ResponseBase deleteSwtichToGroup(String username,
			String sessionId, String switchId, String groupId)
	{
		return CMHttpManager.post(Url.deleteSwitchToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "switchId",
				switchId, "groupId", groupId);
	}

	/**
	 * Ϊ������Ӵ���
	 */
	public static ResponseBase addSensorToGroup(String username,
			String sessionId, String sensorId, String groupId)
	{
		return CMHttpManager.post(Url.addSensorToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "sensorId",
				sensorId, "groupId", groupId);
	}

	/**
	 * Ϊ���Ƴ�����
	 */
	public static ResponseBase deleteSensorToGroup(String username,
			String sessionId, String sensorId, String groupId)
	{
		return CMHttpManager.post(Url.deleteSensorToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "sensorId",
				sensorId, "groupId", groupId);
	}

	/**
	 * Ϊ�����������ͷ
	 */
	public static ResponseBase addCameraToGroup(String username,
			String sessionId, String camerarId, String groupId)
	{
		return CMHttpManager.post(Url.addCameraToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "cameraId",
				camerarId, "groupId", groupId);
	}

	/**
	 * Ϊ���Ƴ�����ͷ
	 */
	public static ResponseBase deleteCameraToGroup(String username,
			String sessionId, String camerarId, String groupId)
	{
		return CMHttpManager.post(Url.deleteCameraToGroup, ResponseBase.class,
				"username", username, "sessionId", sessionId, "cameraId",
				camerarId, "groupId", groupId);
	}

	/**
	 * �ر�/����ĳһ������ 1���� 0����
	 */
	public static ResponseBase controlSwitch(String username, String sessionId,
			String switchId, String operType)
	{
		return CMHttpManager.post(Url.switchCotrol, ResponseBase.class,
				"username", username, "sessionId", sessionId, "switchId",
				switchId, "operType", operType);
	}

	/**
	 * �ر�/�������п��� 1���� 0����
	 */
	public static ResponseBase controlAllSwitch(String username,
			String sessionId, String ssuid, String operType)
	{
		return CMHttpManager.post(Url.switchAllCotrol, ResponseBase.class,
				"username", username, "sessionId", sessionId, "ssuid", ssuid,
				"operType", operType);
	}

	/**
	 * ��ѯ�û��µ���������
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
	 * �л�����
	 */
	public static ResponseBase switchSsuid(String memberId, String ssuid)
	{
		return CMHttpManager.post(Url.changeGateway, ResponseBase.class,
				"memberId", memberId, "ssuid", ssuid);
	}

	/**
	 * ɾ������
	 */
	public static ResponseBase deleteGroup(String memberId, String groupId,
			String sessionId)
	{
		return CMHttpManager.post(Url.deleteGroup, ResponseBase.class,
				"memberId", memberId, "groupId", groupId, "sessionId",
				sessionId);
	}

	/**
	 * �ر�/�������п��� 1���� 0����
	 */
	public static ResponseBase controlGroupSwitch(String username,
			String sessionId, String ssuid, String groupId, String operType)
	{
		return CMHttpManager.post(Url.switchAllCotrol, ResponseBase.class,
				"username", username, "sessionId", sessionId, "ssuid", ssuid,
				"groupId", groupId, "operType", operType);
	}

	/**
	 * �������ͷ
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
	 * ɾ���豸 deviceType 1:���� 2�������� 3������ͷ
	 */
	public static ResponseBase deleteDevice(String ssuid, String deviceId,
			String deviceType)
	{
		return CMHttpManager.post(Url.deleteDevice, ResponseBase.class,
				"ssuid", ssuid, "deviceId", deviceId, "deviceType", deviceType);
	}

	/**
	 * ����
	 */
	public static ResponseBase groupNet(String ssuid)
	{
		return CMHttpManager.post(Url.groupNet, ResponseBase.class, "ssuid",
				ssuid);
	}

	/**
	 * ���ƺ����ܿ��� 1����������� 2���أ��������
	 */
	public static ResponseBase ControlAllInfrared(String username,
			String sessionId, String operType, String ssuid)
	{
		return CMHttpManager.post(Url.controlAllInfrared, ResponseBase.class,
				"username", username, "sessionId", sessionId, "operType",
				operType, "ssuid", ssuid);
	}

	/**
	 * ���ƺ�����鿪�� 1����������� 2���أ��������
	 */
	public static ResponseBase ControlGroupInfrared(String username,
			String sessionId, String operType, String groupId)
	{
		return CMHttpManager.post(Url.controlGroupInfrared, ResponseBase.class,
				"username", username, "sessionId", sessionId, "operType",
				operType, "groupId", groupId);
	}

	/**
	 * ��ѯ�����ܿ��ء�
	 */
	public static ResponseBase qurryAllInfrared(String ssuid)
	{
		return CMHttpManager.post(Url.qurryAllInfrared, ResponseBase.class,
				"ssuid", ssuid);
	}

	/**
	 * �༭�������ƺ�ͷ��
	 */
	public static ResponseBase editGroup(String groupId, String newGroupName,
			String iconType)
	{
		return CMHttpManager.post(Url.editGroup, ResponseBase.class, "groupId",
				groupId, "newGroupName", newGroupName, "iconType", iconType);
	}

	/**
	 * �༭���ء�
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
	 * �༭����ͷ��
	 */
	public static ResponseBase editCameraPass(String cameraId, String username,
			String sessionId, String ssuid, String password)
	{
		return CMHttpManager.post(Url.editCamerePass, ResponseBase.class,
				"cameraId", cameraId, "username", username, "sessionId",
				sessionId, "ssuid", ssuid, "password", password);
	}

	/**
	 * �༭���С�
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
	 * �༭����ͷ��
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
	 * �༭����ͷ��
	 */
	public static ResponseBase removeGateway(String username, String ssuid)
	{
		return CMHttpManager.post(Url.removeGateway, ResponseBase.class,
				"username", username, "ssuid", ssuid);
	}

	/**
	 * ��ѯ�����µ�������Ϣ
	 */
	public static String getAllGoupInfo(String groupId)
	{
		return CMHttpManager.post(Url.getGroupDeviceInfo, "groupId", groupId);
	}

	/**
	 * ��ȡ�汾������Ϣ
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
	 * ��ѯ�����龰��
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
	 * ��ѯ�龰ģʽ���п��ء�
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
	 * ��ѯ�龰ģʽ���д��С�
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
