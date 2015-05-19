package com.anji.www.activity;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anji.www.R;
import com.anji.www.adapter.GroupCameraAdapter;
import com.anji.www.adapter.GroupSensorAdapter;
import com.anji.www.adapter.GroupSwitchAdapter;
import com.anji.www.constants.MyConstants;
import com.anji.www.db.DatabaseHelper;
import com.anji.www.db.service.AnjiDBservice;
import com.anji.www.db.service.AnjiGroupService;
import com.anji.www.entry.AdInfo;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.GroupInfo;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.service.UdpService;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;
import com.anji.www.view.CustomMultiChoiceDialog;
import com.ipc.sdk.FSApi;
import com.ipc.sdk.StatusListener;
import com.remote.util.IPCameraInfo;
import com.remote.util.MyStatusListener;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 分组详情设备信息列表
 * 
 * @author Administrator
 */
public class GroupDeviceInfo extends BaseActivity implements OnClickListener
{
	private static final String TAG = "GroupDeviceInfo";
	private Button bt_back;
	private Button bt_right;
	private Button bt_delete_group;
	private GroupDeviceInfo mContext;
	private TextView tv_title;
	private Dialog progressDialog;
	private CheckBox cb_group_switch;
	private CheckBox cb_group_sensor;
	private String groupName;
	private int groupId;
	private List<DeviceInfo> switchList;// 分组内的开关设备
	private List<DeviceInfo> sensorList;// 分组内的传感设备
	private List<IPCameraInfo> cameraList;// 分组内的摄像头设备
	private List<DeviceInfo> switchNoGroupList;// 未分组内的开关设备
	private List<DeviceInfo> sensorNoGroupList;// 未分组内的传感设备
	private List<IPCameraInfo> cameraNoGroupList;// 未分组内的摄像头设备
	private List<DeviceInfo> switchSelectList;// 选择了的开关设备
	private List<DeviceInfo> sensorSelectList;// 选择了的传感设备
	private List<IPCameraInfo> cameraSelectList;// 选择了的摄像头设备
	private List<DeviceInfo> groupSwitchList;// 查询的到分组里面的开关数据
	private List<DeviceInfo> groupSensorList;// 查询到的分组里面的传感数据
	private List<IPCameraInfo> groupCameraList;// 查询到的摄像头数据

	private GridView gv_switch;
	private GridView gv_sensor;
	private GridView gv_camera;

	private ImageView img_group_icon;// 分组的头像
	private EditText et_group_name;// 分组名称
	private Button bt_edit_sure;// 分组确定按钮

	private GroupSwitchAdapter switchAdapter;
	private GroupSensorAdapter sensorAdapter;
	private GroupCameraAdapter cameraAdapter;
	private static final String Tag = "GroupDeviceInfo";
	private boolean isEditState;

	private boolean[] selectSwitchResult;// 选择结果
	private CustomMultiChoiceDialog.Builder switchDialogBuilder;// 创建器
	private CustomMultiChoiceDialog switchDialog;

	private boolean[] selectSensorResult;
	private CustomMultiChoiceDialog.Builder sensorDialogBuilder;
	private CustomMultiChoiceDialog sensorDialog;

	private boolean[] selectCameraResult;
	private CustomMultiChoiceDialog.Builder cameraDialogBuilder;
	private CustomMultiChoiceDialog cameraDialog;

	private AddSwtichToGroupTask addSwtichTask;
	private GetGroupInfoTask getGroupInfoTask;
	private DeleteGroupTask deleteGroupTask;
	private AddSensorToGroupTask addSensorTask;
	private AddCamereToGroupTask addCameraTask;
	private ControlSwitchTask controlTask;
	private DeleteSwtichToGroupTask deleteSwtichTask;
	private DeleteSensorToGroupTask deleteSensorTask;
	private DeleteCameraToGroupTask deleteCameraTask;
	private UdpService myUdpService;
	private Button bt_sure;
	private IPCameraInfo currentInfo;
	private Dialog inputDialog;
	private EditText et_username;
	private EditText et_password;
	private LinearLayout ll_group_edit;
	private String userName;
	private String password;
	private boolean isStop;
	private long lastStartCheck;
	private ControlSwitchGroupTask controlSwitchGroupTask;
	private DatabaseHelper dbHelp;
	private ControlGroupInfrared controlGroupInfraredTask;
	private EditGroupTask editGroupTask;
	private int position;
	private GroupInfo currentGroup;
	private int iconType;
	private String newGroupName;

	public Handler loginListener = new Handler()
	{

		public void handleMessage(android.os.Message msg)
		{
			switch (msg.arg1)
			{
			case StatusListener.STATUS_LOGIN_SUCCESS:
				LogUtil.LogI(TAG, "STATUS_LOGIN_SUCCESS");
				// // 登陆成功
				// // 更新数据库
				// try
				// {
				// if (currentInfo != null)
				// {
				//
				// new Thread()
				// {
				// public void run()
				// {
				// if (TextUtils.isEmpty(currentInfo.userName)
				// || TextUtils
				// .isEmpty(currentInfo.password))
				// {
				// if (!TextUtils.isEmpty(userName)
				// && !TextUtils.isEmpty(password))
				// {
				// currentInfo.userName = userName;
				// currentInfo.password = password;
				// ContentValues contentValue = new ContentValues();
				//
				// contentValue.put("devType",
				// currentInfo.devType);
				// contentValue.put("devName",
				// currentInfo.devName);
				// contentValue.put("ip", currentInfo.ip);
				// contentValue.put("streamType",
				// currentInfo.streamType);
				// contentValue.put("webPort",
				// currentInfo.webPort);
				// contentValue.put("mediaPort",
				// currentInfo.mediaPort);
				// contentValue
				// .put("uid", currentInfo.uid);
				// contentValue.put("userName",
				// currentInfo.userName);
				// contentValue.put("password",
				// currentInfo.password);
				// LogUtil.LogI(TAG, "currentInfo.id="
				// + currentInfo.id);
				//
				// try
				// {
				// LogUtil.LogI(TAG, "currentInfo.id="
				// + currentInfo.id);
				// dbHelp.update(mContext,
				// "tb_device_list",
				// contentValue,
				// currentInfo.id);
				// }
				// catch (Exception e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				//
				// }
				//
				// };
				// }.start();
				//
				// // TODO 编辑摄像头
				//
				// Intent intent = new Intent(mContext,
				// CameraActivity.class);
				// intent.putExtra("uid", currentInfo.uid);
				// mContext.startActivity(intent);
				// }
				// }
				// catch (Exception e)
				// {
				// Log.e("moon", e.getMessage(), e);
				// }
				break;
			case StatusListener.STATUS_LOGIN_FAIL_USR_PWD_ERROR:
				ToastUtils.show(mContext,
						getString(R.string.camera_login_error));
				LogUtil.LogI(TAG, "STATUS_LOGIN_FAIL_USR_PWD_ERROR ");
				break;
			case StatusListener.STATUS_LOGIN_FAIL_ACCESS_DENY:
				ToastUtils.show(mContext,
						getString(R.string.camera_login_error2));
				LogUtil.LogI(TAG, "loginListener 1001");
				break;
			case StatusListener.STATUS_LOGIN_FAIL_EXCEED_MAX_USER:
				ToastUtils.show(mContext,
						getString(R.string.camera_login_error3));
				LogUtil.LogI(TAG, "loginListener 1001");
				break;
			case StatusListener.STATUS_LOGIN_FAIL_CONNECT_FAIL:
				ToastUtils.show(mContext,
						getString(R.string.camera_login_error4));
				LogUtil.LogI(TAG, "loginListener 1001");
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_info);
		mContext = this;
		isEditState = false;
		myUdpService = UdpService.newInstance(null);
		myUdpService.setMyHandler(myHandler);
		initData();
		dbHelp = new DatabaseHelper(mContext);
		// showSwitchChoiceDialog();
		// showSensorChoiceDialog();
		// showCameraChoiceDialog();
		groupSwitchList = new ArrayList<DeviceInfo>();
		groupSensorList = new ArrayList<DeviceInfo>();
		groupCameraList = new ArrayList<IPCameraInfo>();
		initInputDialog();
		initSetIconTypePop();
		initView();
		startGetGroupInfo();
		// isStop = true;
		// MyStatusListener.setMyHandler(loginListener);
		// final MyStatusListener statusListener = new MyStatusListener();
		//
		// FSApi.setStatusListener(statusListener);
		// new Thread(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// int id;
		// int StatusID;
		// while (isStop)
		// {
		// for (id = 0; id < 4; id++)
		// {
		// StatusID = FSApi.getStatusId(id);
		//
		// if (StatusID < 0)
		// {
		// try
		// {
		// Thread.sleep(50);
		// }
		// catch (InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		// }
		// else
		// {
		// statusListener.OnStatusCbk(StatusID, id, 0, 0, 0);
		// }
		// }
		// }
		//
		// }
		// }).start();
	}

	/**
	 * 断开连接提示框
	 */
	private void initInputDialog()
	{
		inputDialog = new Dialog(this, R.style.MyDialogStyle);
		inputDialog.setContentView(R.layout.alert_camera_dialog);
		inputDialog.setCancelable(false);
		et_username = (EditText) inputDialog.findViewById(R.id.et_username);
		et_password = (EditText) inputDialog.findViewById(R.id.et_password);

		bt_sure = (Button) inputDialog.findViewById(R.id.bt_sure);
		Button bt_cancel = (Button) inputDialog.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				inputDialog.dismiss();
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		isStop = true;
	}

	@Override
	protected void onDestroy()
	{
		isStop = false;
		if (null != dbHelp)
		{
			dbHelp.close();
		}
		super.onDestroy();
	}

	private void initData()
	{
		// groupName = getIntent().getStringExtra("groupName");
		// groupId = getIntent().getIntExtra("groupId", 0);
		position = getIntent().getIntExtra("position", 0);
		if (MainActivity.groupList != null
				&& MainActivity.groupList.size() > position)
		{
			currentGroup = MainActivity.groupList.get(position);
			groupName = currentGroup.getGroupName();
			groupId = currentGroup.getGroupId();
			iconType = Integer.parseInt(currentGroup.getIconType());

		}
		LogUtil.LogI(Tag, "groupId=" + groupId);
		switchList = new ArrayList<DeviceInfo>();
		sensorList = new ArrayList<DeviceInfo>();
		cameraList = new ArrayList<IPCameraInfo>();
		switchNoGroupList = new ArrayList<DeviceInfo>();
		sensorNoGroupList = new ArrayList<DeviceInfo>();
		cameraNoGroupList = new ArrayList<IPCameraInfo>();
		switchSelectList = new ArrayList<DeviceInfo>();
		sensorSelectList = new ArrayList<DeviceInfo>();
		cameraSelectList = new ArrayList<IPCameraInfo>();
		if (groupId == 0)
		{
			ToastUtils.show(this, getString(R.string.gourpId_error));
		}
		else
		{

			initListData();
		}

	}

	private void initListData()
	{
		switchList.clear();
		switchNoGroupList.clear();
		for (int i = 0; i < MainActivity.switchList.size(); i++)
		{
			DeviceInfo info = MainActivity.switchList.get(i);
			if (info != null)
			{
				if (info.getGroupID() == 0)
				{
					switchNoGroupList.add(info);
				}
				else if (info.getGroupID() == groupId)
				{
					// switchList.add(info);
				}
			}
		}

		sensorList.clear();
		sensorNoGroupList.clear();
		for (int i = 0; i < MainActivity.sensorList.size(); i++)
		{
			DeviceInfo info = MainActivity.sensorList.get(i);
			if (info != null)
			{
				if (info.getGroupID() == 0)
				{
					sensorNoGroupList.add(info);
				}
				else if (info.getGroupID() == groupId)
				{
					// sensorList.add(info);
				}
			}
		}
		cameraList.clear();
		cameraNoGroupList.clear();
		for (int i = 0; i < MainActivity.cameraList.size(); i++)
		{
			IPCameraInfo info = MainActivity.cameraList.get(i);
			if (info != null)
			{

				if (info.groupId == 0)
				{
					cameraNoGroupList.add(info);
				}
				else if (info.groupId == groupId)
				{
					// cameraList.add(info);
				}
			}
		}
		LogUtil.LogI(Tag, "cameraList.size=" + cameraList.size());
	}

	private void initView()
	{
		progressDialog = DisplayUtils.createDialog(mContext);
		tv_title = (TextView) findViewById(R.id.tv_title);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_right = (Button) findViewById(R.id.bt_right);
		bt_right.setText(getString(R.string.edit));
		bt_right.setVisibility(View.VISIBLE);
		tv_title.setText(groupName);
		ll_group_edit = (LinearLayout) findViewById(R.id.ll_group_edit);

		img_group_icon = (ImageView) findViewById(R.id.img_group_icon);
		et_group_name = (EditText) findViewById(R.id.et_group_name);
		bt_edit_sure = (Button) findViewById(R.id.bt_edit_sure);

		cb_group_switch = (CheckBox) findViewById(R.id.cb_group_switch);
		cb_group_sensor = (CheckBox) findViewById(R.id.cb_group_sensor);
		bt_delete_group = (Button) findViewById(R.id.bt_delete_group);
		cb_group_sensor.setChecked(currentGroup.isInfraredSwitch());
		cb_group_switch.setOnClickListener(this);
		cb_group_sensor.setOnClickListener(this);
		img_group_icon.setOnClickListener(this);
		bt_edit_sure.setOnClickListener(this);
		setIconImage(img_group_icon, iconType);
		et_group_name.setText(groupName);

		gv_switch = (GridView) findViewById(R.id.gv_switch);
		gv_sensor = (GridView) findViewById(R.id.gv_sensor);
		gv_camera = (GridView) findViewById(R.id.gv_camera);
		switchAdapter = new GroupSwitchAdapter(mContext, switchList);
		sensorAdapter = new GroupSensorAdapter(mContext, sensorList);
		cameraAdapter = new GroupCameraAdapter(mContext, cameraList);
		gv_switch.setAdapter(switchAdapter);
		gv_sensor.setAdapter(sensorAdapter);
		gv_camera.setAdapter(cameraAdapter);
		bt_right.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		bt_delete_group.setOnClickListener(this);

		gv_switch.setOnItemClickListener(new GridView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				LogUtil.LogI(Tag, "switchList.size=" + switchList.size());
				LogUtil.LogI(Tag, "position=" + position);
				if (switchList != null)
				{

					if (switchList.size() == 0)
					{
						showSwitchChoiceDialog();
					}
					else
					{
						if (position == switchList.size())
						{
							showSwitchChoiceDialog();
						}
						else
						{
							// TODO 控制开关
							// 编辑状态不能控制
							LogUtil.LogI(Tag, "setOnItemClickListener");
							if (!isEditState)
							{
								if (switchList.get(position).getDeviceState() != (byte) 0xAA)
								{
									if (MainActivity.isInNet)
									{
										inNetControlSwitch(switchList
												.get(position));
									}
									else
									{
										LogUtil.LogI(Tag, "startControlSwitch");
										startControlSwitch(switchList
												.get(position));
									}
								}
							}

						}
					}
				}
			}
		});

		gv_sensor.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				LogUtil.LogI(Tag, "sensorList.size=" + sensorList.size());
				LogUtil.LogI(Tag, "position=" + position);
				if (sensorList != null)
				{

					if (sensorList.size() == 0)
					{
						showSensorChoiceDialog();
					}
					else
					{
						if (position == sensorList.size())
						{
							showSensorChoiceDialog();
						}
						else
						{
							// TODO 进入分组信息界面
						}
					}
				}
			}
		});

		gv_camera.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (cameraList.size() > 0)
				{
					currentInfo = cameraList.get(position);
				}
				LogUtil.LogI(Tag, "cameraList.size=" + cameraList.size());
				LogUtil.LogI(Tag, "position=" + position);
				if (cameraList != null)
				{

					if (cameraList.size() == 0)
					{
						showCameraChoiceDialog();
					}
					else
					{
						if (position == cameraList.size())
						{
							showCameraChoiceDialog();
						}
						else
						{
							// TODO 进入分组信息界面

							// if (CameraActivity.hasConnected)
							// {
							// Intent intent = new Intent(mContext,
							// CameraActivity.class);
							// mContext.startActivity(intent);
							// }
							// else
							// {
							// loginCamera(position);
							// }

							final IPCameraInfo ipcInfo = cameraList
									.get(position);
							if (TextUtils.isEmpty(cameraList.get(position).userName)
									|| TextUtils.isEmpty(cameraList
											.get(position).password))
							{
								if (inputDialog != null
										&& !inputDialog.isShowing())
								{

									inputDialog.show();
									bt_sure.setOnClickListener(new OnClickListener()
									{

										@Override
										public void onClick(View v)
										{
											inputDialog.dismiss();

											userName = et_username.getText()
													.toString();
											password = et_password.getText()
													.toString();
											currentInfo.userName = userName;
											currentInfo.password = password;
											if ("".equals(userName))
											{
												// 提示用户名不能为空
												Toast.makeText(
														GroupDeviceInfo.this,
														"User name empty",
														Toast.LENGTH_LONG)
														.show();
											}
											else
											{
												Intent it = new Intent(
														GroupDeviceInfo.this,
														CameraNew.class);

												Bundle bundle = new Bundle();
												/* 字符、字符串、布尔、字节数组、浮点数等等，都可以传 */

												bundle.putString("ip", "");
												bundle.putString("uid",
														ipcInfo.uid);
												bundle.putString("uname",
														userName);
												bundle.putString("pwd",
														password);
												bundle.putShort("port",
														Short.parseShort("88"));
												bundle.putInt("type",
														ipcInfo.devType);
												/* 把bundle对象assign给Intent */
												it.putExtras(bundle);
												startActivity(it);

											}

										}
									});
								}
							}
							else
							{
								// FSApi.usrLogOut(0);
								LogUtil.LogI(TAG, "ipcInfo.devType="
										+ ipcInfo.devType);
								LogUtil.LogI(TAG, "userName="
										+ ipcInfo.userName.trim());
								LogUtil.LogI(TAG, "password="
										+ ipcInfo.password.trim());
								// LogUtil.LogI(TAG, "ipcInfo.streamType="
								// + ipcInfo.streamType);
								LogUtil.LogI(TAG, "ipcInfo.webPort="
										+ ipcInfo.webPort);
								// LogUtil.LogI(TAG, "ipcInfo.mediaPort="
								// + ipcInfo.mediaPort);
								LogUtil.LogI(TAG, "ipcInfo.uid=" + ipcInfo.uid);
								// FSApi.usrLogIn(ipcInfo.devType, "0",
								// ipcInfo.userName.trim(),
								// ipcInfo.password.trim(), ipcInfo.streamType,
								// ipcInfo.webPort, ipcInfo.mediaPort,
								// ipcInfo.uid, 0);

								Intent it = new Intent(GroupDeviceInfo.this,
										CameraNew.class);

								Bundle bundle = new Bundle();
								/* 字符、字符串、布尔、字节数组、浮点数等等，都可以传 */

								bundle.putString("ip", "");
								bundle.putString("uid", ipcInfo.uid);
								bundle.putString("uname",
										ipcInfo.userName.trim());
								bundle.putString("pwd", ipcInfo.password.trim());
								bundle.putShort("port", (short) ipcInfo.webPort);
								bundle.putInt("type", ipcInfo.devType);
								/* 把bundle对象assign给Intent */
								it.putExtras(bundle);
								startActivity(it);

							}
						}
					}
				}
			}

		});

	}

	private void loginCamera(int position)
	{
		final IPCameraInfo ipcInfo = cameraList.get(position);
		if (TextUtils.isEmpty(cameraList.get(position).userName)
				|| TextUtils.isEmpty(cameraList.get(position).password))
		{
			if (inputDialog != null && !inputDialog.isShowing())
			{

				inputDialog.show();
				bt_sure.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						inputDialog.dismiss();

						String userName = et_username.getText().toString();
						String password = et_password.getText().toString();

						if ("".equals(userName))
						{
							// 提示用户名不能为空
							Toast.makeText(mContext, "User name empty",
									Toast.LENGTH_LONG).show();
						}
						else
						{
							currentInfo = ipcInfo;
							currentInfo.userName = userName;
							currentInfo.password = password;
							// 保存配置
							SharedPreferences sharedPreferences = mContext
									.getSharedPreferences("CONNECT_DEV_INFO", 0);
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putInt("DEV_TYPE", ipcInfo.devType);
							editor.putString("DEV_NAME", ipcInfo.devName);
							editor.putString("IP", ipcInfo.ip);
							editor.putInt("STREAM_TYPE", ipcInfo.streamType);
							editor.putInt("WEB_PORT", ipcInfo.webPort);
							editor.putInt("MEDIA_PORT", ipcInfo.mediaPort);
							editor.putString("USER_NAME", userName);
							editor.putString("PASSWORD", password);
							editor.putString("UID", ipcInfo.uid);
							editor.commit();
							//
							// dialog.dismiss();
							//
							// // 跳转到LiveView页面，开始连接视频
							// setResult(Activity.RESULT_OK);
							//
							// DeviceList.this.finish();
							//
							// overridePendingTransition(R.anim.zoomin,
							// R.anim.zoomout);
							// 登陆
							FSApi.usrLogOut(0);
							FSApi.usrLogIn(ipcInfo.devType, "0", userName,
									password, ipcInfo.streamType,
									ipcInfo.webPort, ipcInfo.mediaPort,
									ipcInfo.uid, 0);
						}

					}
				});
			}
		}
		else
		{
			LogUtil.LogI(Tag, "userName=" + ipcInfo.userName);
			LogUtil.LogI(Tag, "password=" + ipcInfo.password);
			LogUtil.LogI(Tag, "uid=" + ipcInfo.uid);
			FSApi.usrLogOut(0);
			FSApi.usrLogIn(ipcInfo.devType, "0", ipcInfo.userName,
					ipcInfo.password, ipcInfo.streamType, ipcInfo.webPort,
					ipcInfo.mediaPort, ipcInfo.uid, 0);

		}
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_edit_sure:
			newGroupName = et_group_name.getText().toString().trim();
			if (TextUtils.isEmpty(newGroupName))
			{
				ToastUtils.show(this, getString(R.string.new_group_name_null));
				return;
			}
			startEditGroup();
			break;
		case R.id.bt_back:
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;

		case R.id.bt_delete_group:
			// 删除分组
			startDeleteGroup();
			break;

		case R.id.bt_right:
			if (isEditState)
			{
				isEditState = false;
				bt_right.setText(getString(R.string.edit));
				bt_delete_group.setVisibility(View.GONE);
				ll_group_edit.setVisibility(View.GONE);
				cb_group_switch.setClickable(true);
				cb_group_sensor.setClickable(true);
			}
			else
			{
				bt_right.setText(getString(R.string.finish));
				isEditState = true;
				bt_delete_group.setVisibility(View.VISIBLE);
				ll_group_edit.setVisibility(View.VISIBLE);
				cb_group_switch.setClickable(false);
				cb_group_sensor.setClickable(false);
			}
			switchAdapter.setEdit(isEditState);
			switchAdapter.notifyDataSetChanged();
			sensorAdapter.setEdit(isEditState);
			sensorAdapter.notifyDataSetChanged();
			cameraAdapter.setEdit(isEditState);
			cameraAdapter.notifyDataSetChanged();
			break;
		case R.id.cb_group_switch:
			// TODO 分组总开关
			if (MainActivity.isInNet)
			{
				inNetControlAll();
			}
			else
			{
				startControlAllSwitch(cb_group_switch.isChecked());
			}

			break;
		case R.id.cb_group_sensor:
			// TODO 分组红外总开关
			startControlGroupInfrared(cb_group_sensor.isChecked());
			break;
		case R.id.img_group_icon:
			showPopupWindow(setIconType, img_group_icon);
			break;
		case R.id.img_livingroom:
			iconType = 0;
			img_group_icon
					.setBackgroundResource(R.drawable.group_living_button_selector);
			dismissIconTypePop();
			break;
		case R.id.img_babyroom:
			img_group_icon
					.setBackgroundResource(R.drawable.group_baby_button_selector);
			dismissIconTypePop();
			iconType = 1;
			break;
		case R.id.img_bathroom:
			img_group_icon
					.setBackgroundResource(R.drawable.group_bathroom_button_selector);
			dismissIconTypePop();
			iconType = 2;
			break;
		case R.id.img_bedroom:
			img_group_icon
					.setBackgroundResource(R.drawable.group_bedroom_button_selector);
			dismissIconTypePop();
			iconType = 3;
			break;
		case R.id.img_kitchenroom:
			dismissIconTypePop();
			img_group_icon
					.setBackgroundResource(R.drawable.group_kitchen_button_selector);
			iconType = 4;
			break;
		case R.id.img_oldroom:
			dismissIconTypePop();
			img_group_icon
					.setBackgroundResource(R.drawable.group_oldman_button_selector);
			iconType = 5;
			break;
		case R.id.img_readingroom:
			dismissIconTypePop();
			img_group_icon
					.setBackgroundResource(R.drawable.group_readingroom_button_selector);
			iconType = 6;
			break;
		default:
			break;
		}
	}

	private void setIconImage(ImageView view, int iconType)
	{
		switch (iconType)
		{
		case 0:
			img_group_icon
					.setBackgroundResource(R.drawable.group_living_button_selector);
			break;
		case 1:
			img_group_icon
					.setBackgroundResource(R.drawable.group_baby_button_selector);
			break;

		case 2:
			img_group_icon
					.setBackgroundResource(R.drawable.group_bathroom_button_selector);
			break;

		case 3:
			img_group_icon
					.setBackgroundResource(R.drawable.group_bedroom_button_selector);
			break;

		case 4:
			img_group_icon
					.setBackgroundResource(R.drawable.group_kitchen_button_selector);
			break;

		case 5:
			img_group_icon
					.setBackgroundResource(R.drawable.group_oldman_button_selector);
			break;

		case 6:
			img_group_icon
					.setBackgroundResource(R.drawable.group_readingroom_button_selector);
			break;

		default:
			break;
		}

	}

	private void dismissIconTypePop()
	{
		if (setIconType != null && setIconType.isShowing())
		{
			setIconType.dismiss();
		}
	}

	private void showPopupWindow(PopupWindow popupWindow, View view)
	{
		if (!popupWindow.isShowing())
		{
			popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
			// currentPopupWindow = popupWindow;
		}
	}

	/**
	 * 初始化选择开关弹出框
	 */
	public void showSwitchChoiceDialog()
	{
		selectSwitchResult = new boolean[switchNoGroupList.size()];
		switchDialogBuilder = new CustomMultiChoiceDialog.Builder(this);
		String[] switchArr = new String[switchNoGroupList.size()];
		for (int i = 0; i < switchNoGroupList.size(); i++)
		{
			switchArr[i] = switchNoGroupList.get(i).getDeviceName();
		}
		switchDialog = switchDialogBuilder
				.setTitle(getString(R.string.please_select_switch))
				.setMultiChoiceItems(switchArr, selectSwitchResult,
						new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id)
							{
								int count = parent.getAdapter().getCount();
								if (count > 0)
								{
									for (int i = 0; i < count; i++)
									{
										CheckBox itemCheckBox = (CheckBox) parent
												.getAdapter()
												.getView(i, null, null)
												.findViewById(
														R.id.chk_selectone);
										if (position == i)
										{
											itemCheckBox.setChecked(true);
										}
										else
										{
											itemCheckBox.setChecked(false);
										}
									}
								}
							}
						}, false)
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								// TODO Auto-generated method stub
								String s = "您选择了:";
								selectSwitchResult = switchDialogBuilder
										.getCheckedItems();
								switchSelectList.clear();

								for (int i = 0; i < selectSwitchResult.length; i++)
								{
									if (selectSwitchResult[i])
									{
										switchSelectList.add(switchNoGroupList
												.get(i));
									}
								}
								// switchAdapter.notifyDataSetChanged();
								if (switchSelectList.size() > 0)
								{
									startAddSwtichToGroup();
								}
								LogUtil.LogI(Tag, "switchSelectList.size="
										+ switchList.size());
							}
						}).setNegativeButton(getString(R.string.cancel), null)
				.create();
		switchDialog.show();
	}

	/**
	 * 初始化选择传感弹出框
	 */
	public void showSensorChoiceDialog()
	{
		selectSensorResult = new boolean[sensorNoGroupList.size()];
		sensorDialogBuilder = new CustomMultiChoiceDialog.Builder(this);
		String[] sensroArr = new String[sensorNoGroupList.size()];
		for (int i = 0; i < sensorNoGroupList.size(); i++)
		{
			sensroArr[i] = sensorNoGroupList.get(i).getDeviceName();
			LogUtil.LogI(Tag, "sensorNoGroupList name=" + sensroArr[i]);
		}
		sensorDialog = sensorDialogBuilder
				.setTitle(getString(R.string.please_select_sensor))
				.setMultiChoiceItems(sensroArr, selectSensorResult,
						new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id)
							{
								int count = parent.getAdapter().getCount();
								if (count > 0)
								{
									for (int i = 0; i < count; i++)
									{
										CheckBox itemCheckBox = (CheckBox) parent
												.getAdapter()
												.getView(i, null, null)
												.findViewById(
														R.id.chk_selectone);
										if (position == i)
										{
											itemCheckBox.setChecked(true);
										}
										else
										{
											itemCheckBox.setChecked(false);
										}
									}
								}
							}
						}, false)
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								// TODO Auto-generated method stub
								String s = "您选择了:";
								selectSensorResult = sensorDialogBuilder
										.getCheckedItems();
								sensorSelectList.clear();
								for (int i = 0; i < selectSensorResult.length; i++)
								{
									if (selectSensorResult[i])
									{
										sensorSelectList.add(sensorNoGroupList
												.get(i));
									}
								}
								sensorAdapter.notifyDataSetChanged();
								LogUtil.LogI(Tag, "sensorSelectList.size="
										+ sensorSelectList.size());
								if (sensorSelectList.size() > 0)
								{
									startAddSensorToGroup();
								}
							}
						}).setNegativeButton(getString(R.string.cancel), null)
				.create();
		sensorDialog.show();
	}

	/**
	 * 初始化选择摄像头弹出框
	 */
	public void showCameraChoiceDialog()
	{
		selectCameraResult = new boolean[cameraNoGroupList.size()];
		cameraDialogBuilder = new CustomMultiChoiceDialog.Builder(this);
		String[] camerahArr = new String[cameraNoGroupList.size()];
		for (int i = 0; i < cameraNoGroupList.size(); i++)
		{
			camerahArr[i] = cameraNoGroupList.get(i).devName;
		}
		cameraDialog = cameraDialogBuilder
				.setTitle(getString(R.string.please_select_camera))
				.setMultiChoiceItems(camerahArr, selectCameraResult,
						new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id)
							{
								int count = parent.getAdapter().getCount();
								if (count > 0)
								{
									for (int i = 0; i < count; i++)
									{
										CheckBox itemCheckBox = (CheckBox) parent
												.getAdapter()
												.getView(i, null, null)
												.findViewById(
														R.id.chk_selectone);
										if (position == i)
										{
											itemCheckBox.setChecked(true);
										}
										else
										{
											itemCheckBox.setChecked(false);
										}
									}
								}
							}
						}, false)
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								// TODO Auto-generated method stub
								String s = "您选择了:";
								selectCameraResult = cameraDialogBuilder
										.getCheckedItems();
								cameraSelectList.clear();
								for (int i = 0; i < selectCameraResult.length; i++)
								{
									if (selectCameraResult[i])
									{
										cameraSelectList.add(cameraNoGroupList
												.get(i));
									}
								}
								cameraAdapter.notifyDataSetChanged();
								if (cameraSelectList.size() > 0)
								{
									startAddCameraToGroup();
								}
								LogUtil.LogI(Tag, "cameraSelectList.size="
										+ cameraSelectList.size());
							}
						}).setNegativeButton(getString(R.string.cancel), null)
				.create();
		cameraDialog.show();
	}

	private void startAddSwtichToGroup()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		addSwtichTask = new AddSwtichToGroupTask();
		addSwtichTask.execute();
	}

	private class AddSwtichToGroupTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			if (switchSelectList != null && switchSelectList.size() == 1)
			{
				String switchId = switchSelectList.get(0).getDeviceId() + "";

				if (member != null)
				{
					responseBase = NetReq.addSwtichToGroup(
							member.getUsername(), member.getSessionId(),
							switchId, groupId + "");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：username不能为空 402：switchId不能为空
				 * 403：sessionID不能为空 404：groupId不能为空 405：sessionId无效 406：分组已被删除
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 添加成功
					for (int i = 0; i < switchSelectList.size(); i++)
					{
						DeviceInfo info = switchSelectList.get(i);
						info.setGroupID(groupId);
						info.setGroupName(groupName);
						switchList.add(info);
						switchNoGroupList.remove(info);
					}
					switchAdapter.notifyDataSetChanged();
					AnjiDBservice dbService = new AnjiDBservice(mContext);
					dbService.updateDeviceData(switchList);

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.switch_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_not_work));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_delete));
				}
			}
			else
			{
				// 网络请求失败
			}

		}
	}

	public void startDeleteSwtichToGroup(DeviceInfo info)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		deleteSwtichTask = new DeleteSwtichToGroupTask();
		deleteSwtichTask.execute(info);
	}

	private class DeleteSwtichToGroupTask extends
			AsyncTask<DeviceInfo, Object, ResponseBase>
	{
		ResponseBase responseBase;
		DeviceInfo info;

		@Override
		protected ResponseBase doInBackground(DeviceInfo... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			info = params[0];
			if (info != null)
			{
				responseBase = NetReq.deleteSwtichToGroup(member.getUsername(),
						member.getSessionId(), info.getDeviceId() + "", groupId
								+ "");
			}
			return responseBase;
		}

		@Override
		protected void onPostExecute(ResponseBase result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：username不能为空 402：switchId不能为空
				 * 403：sessionID不能为空 404：groupId不能为空 405：sessionId无效 406：分组已被删除
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 删除成功
					if (info != null)
					{
						info.setGroupID(0);
						info.setGroupName("");
						switchList.remove(info);

						for (int i = 0; i < MainActivity.allDeviceList.size(); i++)
						{
							if (MainActivity.allDeviceList.get(i).getDeviceId() == info
									.getDeviceId())
							{
								MainActivity.allDeviceList.get(i).setGroupID(0);
								MainActivity.allDeviceList.get(i).setGroupName(
										"");
							}
						}
						// initListData();
						switchNoGroupList.add(info);
						switchAdapter.notifyDataSetChanged();
						final AnjiDBservice dbService = new AnjiDBservice(
								mContext);
						new Thread()
						{
							public void run()
							{
								dbService.updataDeviceByID(info,
										MyApplication.member.getMemberId());
							};
						}.start();

					}
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.switch_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_not_work));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_delete));
				}
			}
			else
			{
				// 网络请求失败

			}

		}
	}

	public void startDeleteSensorToGroup(DeviceInfo info)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		deleteSensorTask = new DeleteSensorToGroupTask();
		deleteSensorTask.execute(info);
	}

	private class DeleteSensorToGroupTask extends
			AsyncTask<DeviceInfo, Object, ResponseBase>
	{
		ResponseBase responseBase;
		DeviceInfo info;

		@Override
		protected ResponseBase doInBackground(DeviceInfo... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			info = params[0];
			if (info != null)
			{
				responseBase = NetReq.deleteSensorToGroup(member.getUsername(),
						member.getSessionId(), info.getDeviceId() + "", groupId
								+ "");
			}
			return responseBase;
		}

		@Override
		protected void onPostExecute(ResponseBase result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：username不能为空 402：sensorId不能为空
				 * 403：sessionID不能为空 404：groupId不能为空 405：sessionId无效 406：分组已被删除
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 删除成功
					if (info != null)
					{
						info.setGroupID(0);
						info.setGroupName("");
						sensorList.remove(info);

						for (int i = 0; i < MainActivity.allDeviceList.size(); i++)
						{
							if (MainActivity.allDeviceList.get(i).getDeviceId() == info
									.getDeviceId())
							{
								MainActivity.allDeviceList.get(i).setGroupID(0);
								MainActivity.allDeviceList.get(i).setGroupName(
										"");
							}
						}
						// initListData();
						sensorNoGroupList.add(info);
						sensorAdapter.notifyDataSetChanged();
						final AnjiDBservice dbService = new AnjiDBservice(
								mContext);
						new Thread()
						{
							public void run()
							{
								dbService.updataDeviceByID(info,
										MyApplication.member.getMemberId());
							};
						}.start();

					}
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sensor_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_not_work));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_delete));
				}
			}
			else
			{
				// 网络请求失败

			}

		}
	}

	private void startAddSensorToGroup()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		addSensorTask = new AddSensorToGroupTask();
		addSensorTask.execute();
	}

	private class AddSensorToGroupTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			if (sensorSelectList != null && sensorSelectList.size() == 1)
			{
				String sensorId = sensorSelectList.get(0).getDeviceId() + "";

				if (member != null)
				{
					responseBase = NetReq.addSensorToGroup(
							member.getUsername(), member.getSessionId(),
							sensorId, groupId + "");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：username不能为空 402：sensorId不能为空
				 * 403：sessionID不能为空 404：groupId不能为空 405：sessionId无效 406：分组已被删除
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 添加成功
					for (int i = 0; i < sensorSelectList.size(); i++)
					{
						DeviceInfo info = sensorSelectList.get(i);
						info.setGroupID(groupId);
						info.setGroupName(groupName);
						sensorList.add(info);
						sensorNoGroupList.remove(info);
					}
					sensorAdapter.notifyDataSetChanged();
					AnjiDBservice dbService = new AnjiDBservice(mContext);
					dbService.updateDeviceData(sensorList);

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sensor_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_not_work));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_delete));
				}
			}
			else
			{
				// 网络请求失败
			}
		}
	}

	public void startDeleteCameraToGroup(IPCameraInfo info)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		deleteCameraTask = new DeleteCameraToGroupTask();
		deleteCameraTask.execute(info);
	}

	private class DeleteCameraToGroupTask extends
			AsyncTask<IPCameraInfo, Object, ResponseBase>
	{
		ResponseBase responseBase;
		IPCameraInfo info;

		@Override
		protected ResponseBase doInBackground(IPCameraInfo... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			info = params[0];
			if (info != null)
			{
				responseBase = NetReq
						.deleteCameraToGroup(member.getUsername(),
								member.getSessionId(), info.cameraId + "",
								groupId + "");
			}
			return responseBase;
		}

		@Override
		protected void onPostExecute(ResponseBase result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：username不能为空 402：cameraId不能为空
				 * 403：sessionID不能为空 404：groupId不能为空 405：sessionId无效 406：分组已被删除
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 删除成功
					if (info != null)
					{
						info.groupId = 0;
						info.groupName = "";
						cameraList.remove(info);

						for (int i = 0; i < MainActivity.cameraList.size(); i++)
						{
							if (MainActivity.cameraList.get(i).cameraId == info.cameraId)
							{
								MainActivity.cameraList.get(i).groupId = 0;
								MainActivity.cameraList.get(i).groupName = "";
							}
						}
						// initListData();
						cameraNoGroupList.add(info);
						cameraAdapter.notifyDataSetChanged();
						new Thread()
						{
							public void run()
							{
								dbHelp.update(mContext, info);
							};
						}.start();

					}
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.camare_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_not_work));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_delete));
				}
			}
			else
			{
				// 网络请求失败

			}

		}
	}

	private void startAddCameraToGroup()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		addCameraTask = new AddCamereToGroupTask();
		addCameraTask.execute();
	}

	private class AddCamereToGroupTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			if (cameraSelectList != null && cameraSelectList.size() == 1)
			{
				String cameraId = cameraSelectList.get(0).cameraId + "";

				if (member != null)
				{
					responseBase = NetReq.addCameraToGroup(
							member.getUsername(), member.getSessionId(),
							cameraId, groupId + "");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：username不能为空 402：cameraId不能为空
				 * 403：sessionID不能为空 404：groupId不能为空 405：sessionId无效 406：分组已被删除
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 添加成功
					for (int i = 0; i < cameraSelectList.size(); i++)
					{
						final IPCameraInfo info = cameraSelectList.get(i);
						info.groupId = groupId;
						info.groupName = groupName;
						cameraList.add(info);
						cameraNoGroupList.remove(info);
						new Thread()
						{
							public void run()
							{
								dbHelp.update(mContext, info);
							};
						}.start();
					}
					cameraAdapter.notifyDataSetChanged();

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.camare_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_not_work));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_delete));
				}
			}
			else
			{
				// 网络请求失败
			}
		}
	}

	/**
	 * 外网控制开关
	 * 
	 * @param switchInfo
	 */
	private void startControlSwitch(DeviceInfo switchInfo)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		controlTask = new ControlSwitchTask();
		controlTask.execute(switchInfo);
	}

	private class ControlSwitchTask extends
			AsyncTask<DeviceInfo, Object, ResponseBase>
	{
		ResponseBase responseBase;
		DeviceInfo info;

		@Override
		protected ResponseBase doInBackground(DeviceInfo... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			info = params[0];
			int controlState;
			if (info != null && info.getDeviceState() != (byte) 0xAA)
			{
				// 不为离线状态
				if (info.getDeviceState() == 0)
				{
					// 0表示当前为关闭状态
					controlState = 1;
				}
				else
				{
					controlState = 0;
				}
				if (member != null)
				{
					responseBase = NetReq.controlSwitch(member.getUsername(),
							member.getSessionId(), info.getDeviceId() + "",
							controlState + "");
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(ResponseBase result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 201：操作失败 300：系统异常 401：username不能为空 402：switchId不能为空
				 * 403：sessionID不能为空 404：operType不能为空 405：operType取值范围错误
				 * 406：会话ID无效 407：设备已删除 408：设备已离线(未加入网关)
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 添加成功

					if (info.getDeviceState() == 0)
					{
						info.setDeviceState((byte) 1);
					}
					else
					{
						info.setDeviceState((byte) 0);
					}

					switchAdapter.notifyDataSetChanged();

				}
				else if (responseBase.getResponseStatus() == 201)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.operation_failed));
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.switch_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.control_type_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.control_type_error));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.login_error));
				}
				else if (responseBase.getResponseStatus() == 407)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.device_delete));
				}
				else if (responseBase.getResponseStatus() == 408)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.device_out_line));
				}
			}
			else
			{
				// 网络请求失败
			}
		}
	}

	/**
	 * 内网控制开关
	 * 
	 * @param item
	 * @param isOpne
	 */
	private void inNetControlSwitch(final DeviceInfo item)
	{
		final String state;
		if (item.getDeviceState() == 0)
		{
			state = "FF";
		}
		else
		{
			state = "00";
		}

		new Thread()
		{
			public void run()
			{

				byte[] bytes = Utils.hexStringToBytes("20F200010D01"
						+ item.getDeviceMac() + "0" + item.getDeviceChannel()
						+ item.getDeviceType() + state);
				// + item.getDeviceType() + "ee");
				UdpService.newInstance(null).sendOrders(bytes);
				try
				{
					Thread.sleep(700);
					MainActivity.qurryOnlyAll();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};

		}.start();
	}

	private Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			byte[] arr = (byte[]) msg.obj;
			switch (msg.what)
			{

			case UdpService.SEND_NULL_FAIL:
				// TODO 发送失败
				break;
			case UdpService.ORDRE_ONE_CONTROL:
				// TODO 单设备控制
				// F2是控制指令 最后一位表示 aa离线 bb无此设备 cc命令发送成功 dd命令发送失败
				switch (arr[17])
				{
				case (byte) 0xaa:
					ToastUtils.show(mContext,
							mContext.getString(R.string.device_out_line));
					break;
				case (byte) 0xbb:
					ToastUtils.show(mContext,
							mContext.getString(R.string.device_none));
					break;
				case (byte) 0xcc:
					ToastUtils.show(mContext,
							mContext.getString(R.string.order_send_sucess));
					MainActivity.qurryOnlyAll();

					break;
				case (byte) 0xdd:
					ToastUtils.show(mContext,
							mContext.getString(R.string.order_send_fail));
					break;

				default:
					break;
				}
				break;
			case UdpService.ORDRE_SREACH_DEVICE:
				switchAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	private View addView;
	private PopupWindow setIconType;

	public void onBackPressed()
	{
		if (isEditState)
		{
			isEditState = false;
			bt_right.setText(getString(R.string.edit));
			switchAdapter.setEdit(isEditState);
			switchAdapter.notifyDataSetChanged();
			sensorAdapter.setEdit(isEditState);
			sensorAdapter.notifyDataSetChanged();
			cameraAdapter.setEdit(isEditState);
			cameraAdapter.notifyDataSetChanged();
			ll_group_edit.setVisibility(View.GONE);
		}
		else
		{
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		}
	};

	/**
	 * 外网控制所有开关
	 * 
	 * @param switchInfo
	 */
	public void startControlAllSwitch(boolean isOpen)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		controlSwitchGroupTask = new ControlSwitchGroupTask();
		controlSwitchGroupTask.execute(isOpen);
	}

	private class ControlSwitchGroupTask extends
			AsyncTask<Boolean, Object, ResponseBase>
	{
		ResponseBase responseBase;
		boolean isOpen;

		@Override
		protected ResponseBase doInBackground(Boolean... params)
		{
			MyApplication app = (MyApplication) mContext.getApplication();
			Member member = app.getMember();
			isOpen = params[0];
			int controlState;
			// 不为离线状态
			if (isOpen)
			{
				// 0表示当前为关闭状态
				controlState = 1;
			}
			else
			{
				controlState = 0;
			}
			if (member != null)
			{
				responseBase = NetReq.controlGroupSwitch(member.getUsername(),
						member.getSessionId(), member.getSsuid() + "", groupId
								+ "", controlState + "");
			}

			return null;
		}

		@Override
		protected void onPostExecute(ResponseBase result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：username不能为空 402：ssuid不能为空
				 * 403：groupId不能为空 404：sessionID不能为空 405：operType不能为空
				 * 406：operType取值范围错误 407：会话ID无效
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 控制成功
					for (int i = 0; i < switchList.size(); i++)
					{
						DeviceInfo info = switchList.get(i);

						if (info.getDeviceState() != (byte) 0xAA)
						{

							if (isOpen)
							{
								info.setDeviceState((byte) 1);
							}
							else
							{
								info.setDeviceState((byte) 0);
							}
						}
					}

					switchAdapter.notifyDataSetChanged();

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.ssuid_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.control_type_null));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.control_type_error));
				}
				else if (responseBase.getResponseStatus() == 407)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.login_error));
				}
			}
			else
			{
				// 网络请求失败
			}
		}
	}

	private void inNetControlAll()
	{
		final String state;
		if (System.currentTimeMillis() - lastStartCheck > 1000)
		{
			if (cb_group_switch.isChecked())
			{
				state = "FF";
			}
			else
			{
				state = "00";
			}

			LogUtil.LogI(Tag,
					"System.currentTimeMillis()=" + System.currentTimeMillis());
			LogUtil.LogI(Tag, "lastStartCheck=" + lastStartCheck);

			lastStartCheck = System.currentTimeMillis();

			new Thread()
			{
				public void run()
				{
					for (int i = 0; i < switchList.size(); i++)
					{
						if (switchList.get(i).getDeviceState() != (byte) 0xaa)
						{
							// 不为离线状态

							byte[] bytes = Utils
									.hexStringToBytes("20F200010D01"
											+ switchList.get(i).getDeviceMac()
											+ "0"
											+ switchList.get(i)
													.getDeviceChannel()
											+ switchList.get(i).getDeviceType()
											+ state);
							// + item.getDeviceType() + "ee");
							UdpService.newInstance(null).sendOrders(bytes);
							try
							{
								Thread.sleep(1000);
								MainActivity.qurryOnlyAll();
							}
							catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				};
			}.start();
		}
	}

	private void startDeleteGroup()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		deleteGroupTask = new DeleteGroupTask();
		deleteGroupTask.execute();
	}

	private class DeleteGroupTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();

			if (member != null && groupId != 0)
			{
				responseBase = NetReq.deleteGroup(member.getMemberId() + "",
						groupId + "", member.getSessionId());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：groupId不能为空 402：memberId不能为空
				 * 403：sessionID不能为空 404：会员不存在 405：sessionId无效
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 添加成功
					for (int i = 0; i < MainActivity.groupList.size(); i++)
					{
						GroupInfo info = MainActivity.groupList.get(i);
						if (info.getGroupId() == groupId)
						{
							MainActivity.groupList.remove(info);
							break;
						}
					}
					AnjiGroupService dbService = new AnjiGroupService(mContext);
					dbService.deleteGroupDById(groupId);
					finish();
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.gourpId_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.memberId_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.member_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.login_error));
				}
			}
			else
			{
				// 网络请求失败
			}

		}
	}

	/**
	 * 控制红外推送总开关
	 * 
	 * @param isOpen
	 */
	public void startControlGroupInfrared(boolean isOpen)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		controlGroupInfraredTask = new ControlGroupInfrared();
		controlGroupInfraredTask.execute(isOpen);
	}

	private class ControlGroupInfrared extends
			AsyncTask<Boolean, Object, ResponseBase>
	{
		ResponseBase responseBase;
		boolean isOpen;

		@Override
		protected ResponseBase doInBackground(Boolean... params)
		{
			isOpen = params[0];

			if (MyApplication.member != null)
			{
				if (isOpen)
				{
					responseBase = NetReq.ControlGroupInfrared(
							MyApplication.member.getUsername(),
							MyApplication.member.getSessionId(), 1 + "",
							groupId + "");
				}
				else
				{
					responseBase = NetReq.ControlGroupInfrared(
							MyApplication.member.getUsername(),
							MyApplication.member.getSessionId(), 2 + "",
							groupId + "");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBase result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：username不能为空 402：sessionID不能为空
				 * 403：operType不能为空 404：operType取值范围错误 405：会话ID无效
				 * 406：groupId不能为空 407：分组已被删除
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 控制成功
				}
				else
				{
					cb_group_sensor.setChecked(!isOpen);
					if (responseBase.getResponseStatus() == 201)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.operation_failed));
					}
					else if (responseBase.getResponseStatus() == 300)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.system_error));
					}
					else if (responseBase.getResponseStatus() == 401)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.name_null));
					}
					else if (responseBase.getResponseStatus() == 402)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.sensor_id_null));
					}
					else if (responseBase.getResponseStatus() == 403)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.oper_type_null));
					}
					else if (responseBase.getResponseStatus() == 404)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.oper_type_error));
					}
					else if (responseBase.getResponseStatus() == 405)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.login_error));
					}
					else if (responseBase.getResponseStatus() == 406)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.group_id_null));
					}
					else if (responseBase.getResponseStatus() == 405)
					{
						ToastUtils.show(mContext,
								mContext.getString(R.string.group_had_delete));
					}
				}

			}
			else
			{
				// 网络请求失败
			}
		}
	}

	/**
	 * 控制红外推送总开关
	 * 
	 * @param isOpen
	 */
	public void startEditGroup()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		editGroupTask = new EditGroupTask();
		editGroupTask.execute();
	}

	private class EditGroupTask extends AsyncTask<Void, Object, ResponseBase>
	{
		ResponseBase responseBase;

		@Override
		protected ResponseBase doInBackground(Void... params)
		{

			if (MyApplication.member != null)
			{
				responseBase = NetReq.editGroup(groupId + "", newGroupName,
						iconType + "");
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBase result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 控制成功
					TabMain.isChangeName = true;
					tv_title.setText(newGroupName);
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
			}
			else
			{
				// 网络请求失败
			}
		}
	}

	private void initSetIconTypePop()
	{
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		addView = layoutInflater.inflate(R.layout.pop_select_group, null);

		setIconType = new PopupWindow(addView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		setIconType.setAnimationStyle(android.R.style.Animation_InputMethod);
		setIconType.update();
		setIconType.setOutsideTouchable(true);
		setIconType.setTouchable(true);
		setIconType.setFocusable(true);
		setIconType.setBackgroundDrawable(new BitmapDrawable());
		Button bt_cancel = (Button) addView.findViewById(R.id.bt_cancel);

		ImageView img_livingroom = (ImageView) addView
				.findViewById(R.id.img_livingroom);
		ImageView img_babyroom = (ImageView) addView
				.findViewById(R.id.img_babyroom);
		ImageView img_bathroom = (ImageView) addView
				.findViewById(R.id.img_bathroom);
		ImageView img_bedroom = (ImageView) addView
				.findViewById(R.id.img_bedroom);
		ImageView img_kitchenroom = (ImageView) addView
				.findViewById(R.id.img_kitchenroom);
		ImageView img_oldroom = (ImageView) addView
				.findViewById(R.id.img_oldroom);
		ImageView img_readingroom = (ImageView) addView
				.findViewById(R.id.img_readingroom);
		img_livingroom.setOnClickListener(this);
		img_babyroom.setOnClickListener(this);
		img_bathroom.setOnClickListener(this);
		img_bedroom.setOnClickListener(this);
		img_kitchenroom.setOnClickListener(this);
		img_oldroom.setOnClickListener(this);
		img_readingroom.setOnClickListener(this);
		bt_cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setIconType.dismiss();
			}
		});
		// btnadd_fromcontacts.setOnClickListener(this);
		// Button btnadd_fromhand = (Button) addView
		// .findViewById(R.id.btnadd_fromhand);
		// btnadd_fromhand.setOnClickListener(this);
		// Button btn_Cancel = (Button) addView.findViewById(R.id.btn_Cancel);
		// btn_Cancel.setOnClickListener(this);
	}

	private void startGetGroupInfo()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		getGroupInfoTask = new GetGroupInfoTask();
		getGroupInfoTask.execute();
	}

	private class GetGroupInfoTask extends AsyncTask<Object, Object, Void>
	{
		String responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			responseBase = NetReq.getAllGoupInfo(groupId + "");
			try
			{

				if (responseBase == null) return null;
				JSONObject myObj = new JSONObject(responseBase);
				switchList.clear();
				sensorList.clear();
				cameraList.clear();

				JSONArray vsAdObj = myObj.getJSONArray("switchs");
				LogUtil.LogI(TAG, "switchs==" + vsAdObj.length());
				for (int i = 0; i < vsAdObj.length(); i++)
				{
					DeviceInfo info = new DeviceInfo();

					JSONObject tempobj2 = vsAdObj.getJSONObject(i);
					info.setDeviceChannel(getInt(tempobj2, "channel"));
					info.setDeviceMac(getStr(tempobj2, "code"));
					info.setGroupID(getInt(tempobj2, "groupId"));
					info.setGroupName(getStr(tempobj2, "groupName"));
					info.setDeviceName(getStr(tempobj2, "name"));
					// 1：开 0：关 2：离线
					byte state = (byte) getInt(tempobj2, "status");
					if (state == 2)
					{
						info.setDeviceState((byte) 0xAA);
					}
					else
					{
						info.setDeviceState(state);
					}
					info.setDeviceId(getInt(tempobj2, "switchId"));
					int type = getInt(tempobj2, "type");
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
					LogUtil.LogI(TAG, "groupSwitchList.add");
					switchList.add(info);

				}

				JSONArray vsAdObj2 = myObj.getJSONArray("sensors");
				LogUtil.LogI(TAG, "sensors==" + vsAdObj2.length());
				for (int i = 0; i < vsAdObj2.length(); i++)
				{
					DeviceInfo info = new DeviceInfo();
					// DeviceInfo info2; // 如果是温湿度就得分为两个
					JSONObject obj = vsAdObj2.getJSONObject(i);
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
						LogUtil.LogI(TAG, "1groupSensorList.add");
						sensorList.add(info);
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
						LogUtil.LogI(TAG, "2groupSensorList.add");
						sensorList.add(info);
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
						LogUtil.LogI(TAG, "3groupSensorList.add");
						sensorList.add(info);
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
						LogUtil.LogI(TAG, "4groupSensorList.add");
						sensorList.add(info);
						break;

					default:
						break;
					}
				}

				JSONArray vsAdObj3 = myObj.getJSONArray("cameras");
				LogUtil.LogI(TAG, "cameras==" + vsAdObj3.length());
				for (int i = 0; i < vsAdObj3.length(); i++)
				{
					LogUtil.LogI(TAG, "cameras i==" + i);
					IPCameraInfo info = new IPCameraInfo();
					JSONObject obj = vsAdObj3.getJSONObject(i);
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
					LogUtil.LogI(TAG, "groupCameraList.add");
					cameraList.add(info);
				}
			}
			catch (Exception e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (!TextUtils.isEmpty(responseBase))
			{
				LogUtil.LogI(TAG,
						"groupSwitchList.size=" + groupSwitchList.size());
				LogUtil.LogI(TAG,
						"groupSensorList.size=" + groupSensorList.size());
				LogUtil.LogI(TAG,
						"groupCameraList.size=" + groupCameraList.size());
				switchAdapter.notifyDataSetChanged();
				sensorAdapter.notifyDataSetChanged();
				cameraAdapter.notifyDataSetChanged();
			}
			else
			{
				// 网络请求失败
			}

		}
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
			if (!TextUtils.isEmpty(getStr(o, key))
					&& !getStr(o, key).equals("null"))
			{
				return o.getInt(key);
			}
			else
			{
				return 0;
			}
		}
		return 0;
	}

	private static double getDouble(JSONObject o, String key)
			throws JSONException
	{
		if (o.has(key))
		{
			if (!TextUtils.isEmpty(getStr(o, key))
					&& !getStr(o, key).equals("null"))
			{
				return o.getDouble(key);
			}
			else
			{
				return 0;
			}
		}
		return 0;
	}

}
