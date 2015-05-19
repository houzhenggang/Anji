package com.anji.www.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.anji.www.R;
import com.anji.www.adapter.CameraGridViewAdapter;
import com.anji.www.adapter.GroupListAdapter;
import com.anji.www.adapter.SensorGridViewAdapter;
import com.anji.www.adapter.SwitchGridViewAdapter;
import com.anji.www.db.service.AnjiGroupService;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.GroupInfo;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;
import com.anji.www.view.CustomMultiChoiceDialog;
import com.remote.util.IPCameraInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 添加分组页面
 * 
 * @author Administrator
 */
public class AddGroupActivity extends BaseActivity implements OnClickListener
{

	private View addView;
	private Button bt_back;
	private Button bt_right;
	private AddGroupActivity mContext;

	private TextView tv_title;
	private EditText et_group_name;
	private PopupWindow setIconType;
	private ImageView img_group_icon;
	private static final String Tag = "AddGroupActivity";
	private int iconType;// 0大厅，1婴儿房，2，浴室 3，卧室 4，厨房 5，老人房 6 书房
	private List<DeviceInfo> switchList;// 未分组的设备
	private List<DeviceInfo> sensorList;
	private List<IPCameraInfo> cameraList;
	private List<DeviceInfo> switchSelectList;// 选择的设备
	private List<DeviceInfo> sensorSelectList;
	private List<IPCameraInfo> cameraSelectList;
	private GridView gv_switch;
	private GridView gv_sensor;
	private GridView gv_camera;

	private SwitchGridViewAdapter switchAdapter;
	private SensorGridViewAdapter sensorAdapter;
	private CameraGridViewAdapter cameraAdapter;

	// private String[] showItem;
	private boolean[] selectSwitchResult;
	private CustomMultiChoiceDialog.Builder switchDialogBuilder;
	private CustomMultiChoiceDialog switchDialog;

	private boolean[] selectSensorResult;
	private CustomMultiChoiceDialog.Builder sensorDialogBuilder;
	private CustomMultiChoiceDialog sensorDialog;

	private boolean[] selectCameraResult;
	private CustomMultiChoiceDialog.Builder cameraDialogBuilder;
	private CustomMultiChoiceDialog cameraDialog;
	private String groupName;
	private Dialog progressDialog;
	private AddGroupTask addGroupTask;
	private GroupInfo responseBase;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_add_group);
		switchList = new ArrayList<DeviceInfo>();
		sensorList = new ArrayList<DeviceInfo>();
		cameraList = new ArrayList<IPCameraInfo>();

		switchSelectList = new ArrayList<DeviceInfo>();
		sensorSelectList = new ArrayList<DeviceInfo>();
		cameraSelectList = new ArrayList<IPCameraInfo>();
		initData();
		initView();
		initSetIconTypePop();
		initSwitchChoiceDialog();
		initSensorChoiceDialog();
		initCameraChoiceDialog();
	}

	/**
	 * 初始化三个数据里列表
	 */
	private void initData()
	{
		for (int i = 0; i < MainActivity.switchList.size(); i++)
		{
			DeviceInfo info = MainActivity.switchList.get(i);
			// ID为0的表示还未分组
			if (info.getGroupID() == 0)
			{
				switchList.add(info);
			}
		}

		for (int i = 0; i < MainActivity.sensorList.size(); i++)
		{
			DeviceInfo info = MainActivity.sensorList.get(i);
			// ID为0的表示还未分组
			if (info.getGroupID() == 0)
			{
				sensorList.add(info);
			}
		}

		for (int i = 0; i < MainActivity.cameraList.size(); i++)
		{
			IPCameraInfo info = MainActivity.cameraList.get(i);
			// ID为0的表示还未分组
			if (info.groupId == 0)
			{
				cameraList.add(info);
			}
		}

	}

	private void initView()
	{
		progressDialog = DisplayUtils.createDialog(mContext);
		img_group_icon = (ImageView) findViewById(R.id.img_group_icon);
		tv_title = (TextView) findViewById(R.id.tv_title);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_right = (Button) findViewById(R.id.bt_right);
		bt_right.setText("");
		bt_right.setVisibility(View.VISIBLE);
		bt_right.setBackgroundResource(R.drawable.finish_button_selector);
		tv_title.setText(R.string.add_group_title);
		et_group_name = (EditText) findViewById(R.id.et_group_name);

		img_group_icon.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);

		gv_switch = (GridView) findViewById(R.id.gv_switch);
		gv_sensor = (GridView) findViewById(R.id.gv_sensor);
		gv_camera = (GridView) findViewById(R.id.gv_camera);
		switchAdapter = new SwitchGridViewAdapter(mContext, switchSelectList);
		sensorAdapter = new SensorGridViewAdapter(mContext, sensorSelectList);
		cameraAdapter = new CameraGridViewAdapter(mContext, cameraSelectList);
		gv_switch.setAdapter(switchAdapter);
		gv_sensor.setAdapter(sensorAdapter);
		gv_camera.setAdapter(cameraAdapter);

		gv_switch.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (switchSelectList != null)
				{

					if (switchSelectList.size() == 0)
					{
						showSwitchDialog();
					}
					else
					{
						if (position == switchSelectList.size())
						{
							showSwitchDialog();
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
				if (cameraSelectList != null)
				{

					if (cameraSelectList.size() == 0)
					{
						cameraDialog.show();
					}
					else
					{
						if (position == cameraSelectList.size())
						{
							cameraDialog.show();
						}
						else
						{
							// TODO 进入分组信息界面
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
				if (sensorSelectList != null)
				{

					if (sensorSelectList.size() == 0)
					{
						sensorDialog.show();
					}
					else
					{
						if (position == sensorSelectList.size())
						{
							sensorDialog.show();
						}
						else
						{
							// TODO 进入分组信息界面
						}
					}
				}
			}
		});
	}

	public void showSwitchDialog()
	{
		if (switchDialog != null && !switchDialog.isShowing())
		{
			switchDialog.show();
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

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_back:
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_right:
			// TODO完成，发送请求
			groupName = et_group_name.getText().toString().trim();

			if (TextUtils.isEmpty(groupName))
			{
				ToastUtils.show(mContext, getString(R.string.group_name_null));
				return;
			}
			if (Utils.String_length(groupName) < 6
					|| Utils.String_length(groupName) > 10)
			{
				ToastUtils.show(mContext, getString(R.string.length_error2));
				return;
			}
			startAddGroupTask();

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

	private void showPopupWindow(PopupWindow popupWindow, View view)
	{
		if (!popupWindow.isShowing())
		{
			popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
			// currentPopupWindow = popupWindow;
		}
	}

	private void dismissIconTypePop()
	{
		if (setIconType != null && setIconType.isShowing())
		{
			setIconType.dismiss();
		}
	}

	/**
	 * 初始化选择开关弹出框
	 */
	public void initSwitchChoiceDialog()
	{
		selectSwitchResult = new boolean[switchList.size()];
		switchDialogBuilder = new CustomMultiChoiceDialog.Builder(this);
		String[] switchArr = new String[switchList.size()];
		for (int i = 0; i < switchList.size(); i++)
		{
			switchArr[i] = switchList.get(i).getDeviceName();
		}
		switchDialog = switchDialogBuilder
				.setTitle(getString(R.string.please_select_switch))
				.setMultiChoiceItems(switchArr, selectSwitchResult, null, true)
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
										switchSelectList.add(switchList.get(i));
									}
								}
								switchAdapter.notifyDataSetChanged();
								LogUtil.LogI(Tag, "switchSelectList.size="
										+ switchSelectList.size());
							}
						}).setNegativeButton(getString(R.string.cancel), null)
				.create();
	}

	/**
	 * 初始化选择传感弹出框
	 */
	public void initSensorChoiceDialog()
	{
		selectSensorResult = new boolean[sensorList.size()];
		sensorDialogBuilder = new CustomMultiChoiceDialog.Builder(this);
		String[] sensroArr = new String[sensorList.size()];
		for (int i = 0; i < sensorList.size(); i++)
		{
			sensroArr[i] = sensorList.get(i).getDeviceName();
		}
		sensorDialog = sensorDialogBuilder
				.setTitle(getString(R.string.please_select_sensor))
				.setMultiChoiceItems(sensroArr, selectSensorResult, null, true)
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
										sensorSelectList.add(sensorList.get(i));
									}
								}
								sensorAdapter.notifyDataSetChanged();
								LogUtil.LogI(Tag, "sensorSelectList.size="
										+ sensorSelectList.size());
							}
						}).setNegativeButton(getString(R.string.cancel), null)
				.create();
	}

	/**
	 * 初始化选择摄像头弹出框
	 */
	public void initCameraChoiceDialog()
	{
		selectCameraResult = new boolean[cameraList.size()];
		cameraDialogBuilder = new CustomMultiChoiceDialog.Builder(this);
		String[] camerahArr = new String[cameraList.size()];
		for (int i = 0; i < cameraList.size(); i++)
		{
			camerahArr[i] = cameraList.get(i).devName;
		}
		cameraDialog = cameraDialogBuilder
				.setTitle(getString(R.string.please_select_camera))
				.setMultiChoiceItems(camerahArr, selectCameraResult, null, true)
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
										cameraSelectList.add(cameraList.get(i));
									}
								}
								cameraAdapter.notifyDataSetChanged();
								LogUtil.LogI(Tag, "cameraSelectList.size="
										+ cameraSelectList.size());
							}
						}).setNegativeButton(getString(R.string.cancel), null)
				.create();
	}

	private void startAddGroupTask()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		addGroupTask = new AddGroupTask();
		addGroupTask.execute();
	}

	private class AddGroupTask extends AsyncTask<Object, Object, Void>
	{

		@Override
		protected Void doInBackground(Object... params)
		{
			StringBuffer switchBuffer = new StringBuffer();
			if (switchSelectList != null && switchSelectList.size() > 0)
			{
				for (int i = 0; i < switchSelectList.size(); i++)
				{
					if (i != 0)
					{
						switchBuffer.append(",");
					}
					switchBuffer.append(switchSelectList.get(i).getDeviceId());
				}
			}

			StringBuffer sensorBuffer = new StringBuffer();
			if (sensorSelectList != null && sensorSelectList.size() > 0)
			{
				for (int i = 0; i < sensorSelectList.size(); i++)
				{
					if (i != 0)
					{
						sensorBuffer.append(",");
					}
					sensorBuffer.append(sensorSelectList.get(i).getDeviceId());
				}
			}

			StringBuffer cameraBuffer = new StringBuffer();
			if (cameraSelectList != null && cameraSelectList.size() > 0)
			{
				for (int i = 0; i < cameraSelectList.size(); i++)
				{
					if (i != 0)
					{
						cameraBuffer.append(",");
					}
					cameraBuffer.append(cameraSelectList.get(i).cameraId);
				}
			}
			String groupName2 = null;
			try
			{
				groupName2 = URLEncoder.encode(groupName, "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			responseBase = NetReq.addGroup(MyApplication.member.getMemberId(),
					groupName2, MyApplication.member.getSessionId(),
					switchBuffer.toString(), sensorBuffer.toString(),
					cameraBuffer.toString(), MyApplication.member.getSsuid(),
					iconType + "");
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
				 * 200：成功 300：系统异常 401：memberId不能为空 402：组名称不能为空
				 * 403：sessionID不能为空 404：switchs参数格式错误 405：sensors参数格式错误
				 * 406：cameras参数格式错误 407：会员不存在 408：无效的sessionID 409：组名称已存在
				 * 410：ssuid不能为空
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 添加分组成功

					if (switchSelectList != null && switchSelectList.size() > 0)
					{
						for (int i = 0; i < switchSelectList.size(); i++)
						{
							switchSelectList.get(i).setGroupID(
									responseBase.getGroupId());
							switchSelectList.get(i).setGroupName(groupName);
						}
					}

					if (sensorSelectList != null && sensorSelectList.size() > 0)
					{
						for (int i = 0; i < sensorSelectList.size(); i++)
						{
							sensorSelectList.get(i).setGroupID(
									responseBase.getGroupId());
							sensorSelectList.get(i).setGroupName(groupName);
						}
					}

					if (cameraSelectList != null && cameraSelectList.size() > 0)
					{
						for (int i = 0; i < cameraSelectList.size(); i++)
						{
							cameraSelectList.get(i).groupId = responseBase
									.getGroupId();
							cameraSelectList.get(i).groupName = groupName;
						}
					}

					responseBase.setGroupName(groupName);
					responseBase.setIconType(iconType + "");
					responseBase
							.setMemberId(MyApplication.member.getMemberId());
					responseBase.setSsuid(MyApplication.member.getSsuid());
					AnjiGroupService groupService = new AnjiGroupService(
							mContext);
					groupService.insertGroupData(responseBase);
					MainActivity.groupList.add(responseBase);
					ToastUtils.show(mContext,
							getString(R.string.add_group_sucess));

					onBackPressed();
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
							mContext.getString(R.string.group_name_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.switchs_agrs_error));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sensors_agrs_error));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.cameras_agrs_error));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.cameras_agrs_error));
				}
				else if (responseBase.getResponseStatus() == 407)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.member_null));
				}
				else if (responseBase.getResponseStatus() == 408)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.login_error));
				}
				else if (responseBase.getResponseStatus() == 409)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.groupName_exist));
				}
				else if (responseBase.getResponseStatus() == 410)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.ssuid_null));
				}

			}
			else
			{
				// 网络请求失败
			}

		}
	}

	@Override
	public void onBackPressed()
	{
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

}
