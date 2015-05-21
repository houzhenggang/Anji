package com.anji.www.network;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.anji.www.activity.MyApplication;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.AdInfo;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.GatewayResponse;
import com.anji.www.entry.GroupInfo;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.entry.SceneInfo;
import com.anji.www.entry.Version;
import com.anji.www.util.EncyrptUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.Utils;
import com.remote.util.IPCameraInfo;

public class JsonParserFactory
{

	/**
	 * 解析注册第三步的信息
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static Member parseMemberInfo(String json, Context context,
			boolean isSaveData) throws JSONException
	{
		if (json == null) return null;
		Member member = new Member();
		JSONObject obj = new JSONObject(json);
		member.setResponseStatus(getInt(obj, "responseStatus"));

		if (getInt(obj, "responseStatus") == 200)
		{
			if (isSaveData)
			{
				Utils.saveData(json, context);
			}

			JSONObject memberObj = obj.getJSONObject("member");
			member.setMemberId(getStr(memberObj, "memberId"));
			member.setUsername(getStr(memberObj, "username"));
			member.setPassword(getStr(memberObj, "password"));
			member.setMobile(getStr(memberObj, "mobile"));
			member.setSsuid(getStr(memberObj, "ssuid"));
			member.setSessionId(getStr(memberObj, "sessionId"));

			JSONArray vsAdObj = obj.getJSONArray("adList");
			for (int i = 0; i < vsAdObj.length(); i++)
			{
				AdInfo info = new AdInfo();
				JSONObject tempobj2 = vsAdObj.getJSONObject(i);
				info.setWeburl(getStr(tempobj2, "weburl"));
				info.setImgPath(getStr(tempobj2, "imgPath"));
				member.getAdList().add(info);
			}

			JSONArray vsAdObj2 = obj.getJSONArray("groupList");
			for (int i = 0; i < vsAdObj2.length(); i++)
			{
				GroupInfo info = new GroupInfo();
				JSONObject tempobj2 = vsAdObj2.getJSONObject(i);
				info.setGroupId(getInt(tempobj2, "groupId"));
				info.setGroupName(getStr(tempobj2, "groupName"));
				info.setIconType(getStr(tempobj2, "iconType"));
				member.getGroupList().add(info);
			}
		}
		return member;
	}

	/**
	 * 解析获取所有开关的数据
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static List<DeviceInfo> parseSwitchList(String json)
			throws JSONException
	{
		if (json == null) return null;
		ArrayList<DeviceInfo> list = new ArrayList<DeviceInfo>();
		JSONArray arr = new JSONArray(json);
		for (int i = 0; i < arr.length(); i++)
		{
			DeviceInfo info = new DeviceInfo();
			JSONObject obj = arr.getJSONObject(i);
			info.setDeviceChannel(getInt(obj, "channel"));
			info.setDeviceMac(getStr(obj, "code"));
			info.setGroupID(getInt(obj, "groupId"));
			info.setGroupName(getStr(obj, "groupName"));
			info.setDeviceName(getStr(obj, "name"));
			// 1：开 0：关 2：离线
			byte state = (byte) getInt(obj, "status");
			if (state == 2)
			{
				info.setDeviceState((byte) 0xAA);
			}
			else
			{
				info.setDeviceState(state);
			}
			info.setDeviceId(getInt(obj, "switchId"));
			int type = getInt(obj, "type");
			if (type == 1)
			{
				// 壁灯 1：壁灯，2：插座
				info.setDeviceType(MyConstants.NORMAL_LIGHT);
			}
			else
			{
				// 2：插座
				info.setDeviceType(MyConstants.SOCKET);
			}
			info.setType(0);// 0为开关，1为传感
			info.setMemberId(MyApplication.member.getMemberId());
			info.setSsuid(MyApplication.member.getSsuid());
			list.add(info);
		}
		return list;
	}

	/**
	 * 解析获取所有传感的json的数据
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<DeviceInfo> parseSenceList(String json)
			throws JSONException
	{
		if (json == null) return null;
		ArrayList<DeviceInfo> list = new ArrayList<DeviceInfo>();
		JSONArray arr = new JSONArray(json);
		for (int i = 0; i < arr.length(); i++)
		{
			DeviceInfo info = new DeviceInfo();
			// DeviceInfo info2; // 如果是温湿度就得分为两个
			JSONObject obj = arr.getJSONObject(i);
			int sensorState;
			int type = getInt(obj, "type");
			// 类型，1：温度湿度，2:烟雾3：红外4：穿戴

			byte deviceState = (byte) getInt(obj, "deviceStatus");
			// 设备状态 1：正常（0） 2：离线（AA） 3：错误（非零）
			if (deviceState == 2)
			{
				info.setDeviceState((byte) 0xAA);
			}
			else if (deviceState == 1)
			{
				info.setDeviceState((byte) 0);
			}
			else
			{
				info.setDeviceState((byte) deviceState);
			}
			info.setSsuid(MyApplication.member.getSsuid());
			info.setMemberId(MyApplication.member.getMemberId());
			switch (type)
			{
			case 1:
				// 类型，1：温度湿度，2:烟雾3：红外4：穿戴

				// info2 = new DeviceInfo();

				info.setMemberId(MyApplication.member.getMemberId());
				info.setDeviceType(MyConstants.TEMPARETRUE_SENSOR);
				info.setDeviceBattery((int) getDouble(obj, "battery"));
				info.setDeviceMac(getStr(obj, "code"));

				info.setGroupID(getInt(obj, "groupId"));
				info.setGroupName(getStr(obj, "groupName"));
				info.setTempValue((float) getDouble(obj, "temp"));
				info.setDeviceChannel(getInt(obj, "tempNo"));// 温度通道号
				info.setDeviceName(getStr(obj, "name"));
				info.setDeviceId(getInt(obj, "sensorId"));
				info.setHumValue((float) getDouble(obj, "hum"));
				if (!TextUtils.isEmpty(getStr(obj, "humNo")))
				{
					info.setDeviceChannel2(getInt(obj, "humNo"));// 湿度通道号
				}
				info.setType(1);// 0为开关，1为传感

				// info2.setMemberId(MyApplication.member.getMemberId());
				// info2.setDeviceType(MyConstants.HUMIDITY_SENSOR);
				// info2.setDeviceBattery((int) getDouble(obj, "battery"));
				// info2.setDeviceMac(getStr(obj, "code"));
				// byte deviceState2 = (byte) getInt(obj, "deviceStatus");
				// // 设备状态 1：正常（0） 2：离线（AA） 3：错误（非零）
				// if (deviceState2 == 2)
				// {
				// info2.setDeviceState((byte) 0xAA);
				// }
				// else
				// {
				// info2.setDeviceState(deviceState2);
				// }
				// info2.setGroupID(getInt(obj, "groupId"));
				// info2.setGroupName(getStr(obj, "groupName"));
				// info2.setHumValue((float) getDouble(obj, "hum"));
				// info2.setDeviceChannel(getInt(obj, "humNo"));// 湿度度通道号
				// info2.setDeviceName(getStr(obj, "name"));
				// info2.setDeviceId(getInt(obj, "sensorId"));
				// info2.setType(1);// 0为开关，1为传感
				list.add(info);
				// list.add(info2);
				break;
			case 2:
				// 类型，1：温度湿度，2:烟雾3：红外4：穿戴
				info.setType(1);// 0为开关，1为传感
				info.setMemberId(MyApplication.member.getMemberId());
				info.setDeviceType(MyConstants.SMOKE_SENSOR);
				info.setDeviceBattery((int) getDouble(obj, "battery"));
				info.setDeviceMac(getStr(obj, "code"));
				info.setGroupID(getInt(obj, "groupId"));
				info.setGroupName(getStr(obj, "groupName"));
				sensorState = getInt(obj, "smogStatus");
				if (sensorState == 20)
				{
					info.setSensorState((byte) 0x20);
				}
				else
				{
					info.setSensorState((byte) 0x10);
				}
				info.setDeviceChannel(getInt(obj, "channel"));// 通道号
				info.setDeviceName(getStr(obj, "name"));
				info.setDeviceId(getInt(obj, "sensorId"));
				list.add(info);
				break;
			case 3:
				// 类型，1：温度湿度，2:烟雾3：红外4：穿戴
				info.setType(1);// 0为开关，1为传感
				info.setMemberId(MyApplication.member.getMemberId());
				info.setDeviceType(MyConstants.HUMAN_BODY_SENSOR);
				info.setDeviceBattery((int) getDouble(obj, "battery"));
				info.setDeviceMac(getStr(obj, "code"));
				info.setGroupID(getInt(obj, "groupId"));
				info.setGroupName(getStr(obj, "groupName"));
				sensorState = getInt(obj, "infraredStatus");
				if (sensorState == 20)
				{
					info.setSensorState((byte) 0x20);
				}
				else
				{
					info.setSensorState((byte) 0x10);
				}
				int infraredSwitch = getInt(obj, "infraredSwitch");
				if (infraredSwitch == 1)
				{
					// 1开2关
					info.setInfraredSwitch(true);
				}
				else
				{
					info.setInfraredSwitch(false);
				}
				info.setDeviceChannel(getInt(obj, "channel"));// 通道号
				info.setDeviceName(getStr(obj, "name"));
				info.setDeviceId(getInt(obj, "sensorId"));
				list.add(info);
				break;
			case 4:
				// 类型，1：温度湿度，2:烟雾3：红外4：穿戴
				info.setType(1);// 0为开关，1为传感
				info.setMemberId(MyApplication.member.getMemberId());
				info.setDeviceType(MyConstants.BRACELET);
				info.setDeviceBattery((int) getDouble(obj, "battery"));
				info.setDeviceMac(getStr(obj, "code"));
				info.setGroupID(getInt(obj, "groupId"));
				info.setGroupName(getStr(obj, "groupName"));
				info.setDeviceChannel(getInt(obj, "channel"));// 通道号
				info.setDeviceName(getStr(obj, "name"));
				info.setDeviceId(getInt(obj, "sensorId"));
				list.add(info);
				break;

			default:
				break;
			}
		}
		return list;
	}

	/**
	 * 解析获取所有摄像头的数据
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<IPCameraInfo> parseCameraList(String json)
			throws JSONException
	{
		if (json == null) return null;
		ArrayList<IPCameraInfo> list = new ArrayList<IPCameraInfo>();
		JSONArray arr = new JSONArray(json);
		for (int i = 0; i < arr.length(); i++)
		{
			IPCameraInfo info = new IPCameraInfo();
			JSONObject obj = arr.getJSONObject(i);
			info.devType = 0;// 默认为0
			info.cameraId = getInt(obj, "cameraId");
			info.userName = getStr(obj, "account");
			info.groupId = getInt(obj, "groupId");
			info.groupName = getStr(obj, "groupName");
			info.ip = getStr(obj, "ip1");
			info.ip2 = getStr(obj, "ip2");
			info.devName = getStr(obj, "name");
			info.password = getStr(obj, "password");
			info.mediaPort = getInt(obj, "port1");
			info.webPort = getInt(obj, "port2");
			info.uid = getStr(obj, "uid");
			info.password = getStr(obj, "password");
			list.add(info);
		}
		return list;
	}

	/**
	 * 解析获取所有摄像头的数据
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<GroupInfo> parseGroupList(String json)
			throws JSONException
	{
		if (json == null) return null;
		ArrayList<GroupInfo> list = new ArrayList<GroupInfo>();
		JSONArray arr = new JSONArray(json);
		for (int i = 0; i < arr.length(); i++)
		{
			GroupInfo info = new GroupInfo();
			JSONObject obj = arr.getJSONObject(i);
			info.setGroupId(getInt(obj, "groupId"));
			info.setGroupName(getStr(obj, "groupName"));
			info.setIconType(getStr(obj, "iconType"));
			int infraredSwitch = getInt(obj, "infraredSwitch");
			info.setInfraredSwitch(infraredSwitch == 1);
			info.setMemberId(MyApplication.member.getMemberId());
			info.setSsuid(MyApplication.member.getSsuid());
			list.add(info);
		}
		return list;
	}

	/**
	 * 解析获取所有用户绑定的网关的数据
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<GatewayResponse> parseGatewayList(String json)
			throws JSONException
	{
		if (json == null) return null;
		ArrayList<GatewayResponse> list = new ArrayList<GatewayResponse>();
		JSONArray arr = new JSONArray(json);
		for (int i = 0; i < arr.length(); i++)
		{
			GatewayResponse info = new GatewayResponse();
			JSONObject obj = arr.getJSONObject(i);
			info.setMemberId(MyApplication.member.getMemberId());
			info.setSsuid(getStr(obj, "ssuid"));
			int CurrGateway = getInt(obj, "isCurrGateway");
			info.setCurrGateway(CurrGateway == 1);
			list.add(info);
		}
		return list;
	}

	/**
	 * 解析添加分组数据
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static GroupInfo parseAddGroup(String json) throws JSONException
	{
		if (json == null) return null;
		JSONObject obj = new JSONObject(json);
		GroupInfo info = new GroupInfo();
		info.setResponseStatus(getInt(obj, "responseStatus"));
		info.setGroupId(getInt(obj, "groupId"));
		return info;
	}

	/**
	 * 解析注册第一步的信息
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ResponseBase parseBaseInfo(String json) throws JSONException
	{
		if (json == null) return null;
		ResponseBase member = new ResponseBase();
		JSONObject obj = new JSONObject(json);
		// obj.keys();
		member.setResponseStatus(getInt(obj, "responseStatus"));
		member.setMemberId(getStr(obj, "memberId"));

		return member;
	}

	/**
	 * 解析版本信息
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static Version parseVersionInfo(String json) throws JSONException
	{
		if (json == null) return null;
		Version member = new Version();
		JSONObject obj = new JSONObject(json);
		JSONObject obj2 = obj.getJSONObject("appVerion");
		// obj.keys();
		member.setPath(getStr(obj2, "path"));
		member.setVersionNo(getStr(obj2, "versionNo"));

		return member;
	}

	/**
	 * 解析上报摄像头
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ResponseBase parseUploadCamera(String json)
			throws JSONException
	{
		if (json == null) return null;
		ResponseBase member = new ResponseBase();
		JSONObject obj = new JSONObject(json);
		obj.keys();
		member.setResponseStatus(getInt(obj, "responseStatus"));
		member.setMemberId(getStr(obj, "cameraId"));

		return member;
	}
	
	/**
	 * 解析获取所有情景的数据
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<SceneInfo> parseSceneList(String json)
			throws JSONException
	{
		if (json == null) return null;
		ArrayList<SceneInfo> list = new ArrayList<SceneInfo>();
		JSONArray arr = new JSONArray(json);
		for (int i = 0; i < arr.length(); i++)
		{
			SceneInfo info = new SceneInfo();
			JSONObject obj = arr.getJSONObject(i);
			info.setSceneId(getInt(obj, "id"));
			info.setSceneName(getStr(obj, "sceneName"));
			info.setIconType(getStr(obj, "iconType"));
			int status = getInt(obj, "status");
			info.setOn( status == 1 );
			info.setSsuid( getStr(obj, "ssuid") );
			list.add(info);
		}
		return list;
	}

	private static String getStr(JSONObject o, String key) throws JSONException
	{
		if (o.has(key))
		{
			try
			{
				return URLDecoder.decode(o.getString(key), "UTF-8");
			}
			catch (Exception e)
			{
				return o.getString(key);
			}
		}
		return null;
	}

	private static int getInt(JSONObject o, String key) throws JSONException
	{
		if (o.has(key))
		{
			return o.getInt(key);
		}
		return 0;
	}

	private static double getDouble(JSONObject o, String key)
			throws JSONException
	{
		if (o.has(key))
		{
			return o.getDouble(key);
		}
		return 0;
	}
	
	/**
	 * 解析添加情景数据
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static SceneInfo parseAddScene(String json) throws JSONException
	{
		if (json == null) return null;
		JSONObject obj = new JSONObject(json);
		SceneInfo info = new SceneInfo();
		info.setResponseStatus(getInt(obj, "responseStatus"));
		info.setSceneId(getInt(obj, "groupId"));
		return info;
	}
}
