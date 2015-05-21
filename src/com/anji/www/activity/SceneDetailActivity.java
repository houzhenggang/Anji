package com.anji.www.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anji.www.R;
import com.anji.www.adapter.SceneSensorAdapter;
import com.anji.www.adapter.SceneSensorAdapter.SensorItemEvent;
import com.anji.www.adapter.SceneSwitchAdapter;
import com.anji.www.adapter.SceneSwitchAdapter.SwitchItemEvent;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.SceneInfo;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;
import com.anji.www.view.CustomMultiChoiceDialog;
import com.remote.util.IPCameraInfo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * �龰Ԕ��ҳ��
 * 
 * @author Administrator
 */
public class SceneDetailActivity extends BaseActivity implements OnClickListener, SwitchItemEvent, SensorItemEvent
{
	private static final String TAG = SceneDetailActivity.class.getName();
	private View addView;
	private Button bt_back;
	private Button bt_right;
	private SceneDetailActivity mContext;

	private TextView tv_title;
	private EditText et_scene_name;
	private PopupWindow setIconType;
	private ImageView img_scene_icon;
	private static final String Tag = "AddGroupActivity";
	private int iconType;// 0������1Ӥ������2��ԡ�� 3������ 4������ 5�����˷� 6 �鷿
	private List<DeviceInfo> switchList;
	private List<DeviceInfo> sensorList;
	private List<DeviceInfo> switchSelectList;// ѡ����豸
	private List<DeviceInfo> sensorSelectList;
	private GridView gv_switch;
	private GridView gv_sensor;

	private SceneSwitchAdapter switchAdapter;
	private SceneSensorAdapter sensorAdapter;

	private boolean[] selectSwitchResult;
	private CustomMultiChoiceDialog.Builder switchDialogBuilder;
	private CustomMultiChoiceDialog switchDialog;

	private boolean[] selectSensorResult;
	private CustomMultiChoiceDialog.Builder sensorDialogBuilder;
	private CustomMultiChoiceDialog sensorDialog;

	private String sceneName;
	private Dialog progressDialog;
	private AddSceneTask addSceneTask;
	private SceneInfo responseBase;
	
	private int position;
	private int sceneId;
	private SceneDetailTask sceneDetailTask;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_add_scene);
		switchList = new ArrayList<DeviceInfo>();
		sensorList = new ArrayList<DeviceInfo>();

		switchSelectList = new ArrayList<DeviceInfo>();
		sensorSelectList = new ArrayList<DeviceInfo>();
		initData();
		initView();
		initSetIconTypePop();
		initSwitchChoiceDialog();
		initSensorChoiceDialog();
		// �����龰����
		startSceneDetail();
	}

	/**
	 * ��ʼ�������������б�
	 */
	private void initData()
	{
		position = getIntent().getIntExtra( "position", 0 );
		SceneInfo sceneInfo = MainActivity.sceneList.get(position);
		sceneId = sceneInfo.getSceneId();
		
		for (int i = 0; i < MainActivity.sceneSwitchList.size(); i++)
		{
			DeviceInfo info = MainActivity.sceneSwitchList.get(i);
			switchList.add(info);
		}

		for (int i = 0; i < MainActivity.sceneSensorList.size(); i++)
		{
			DeviceInfo info = MainActivity.sceneSensorList.get(i);
			sensorList.add(info);
		}
	}

	private void initView()
	{
		progressDialog = DisplayUtils.createDialog(mContext);
		img_scene_icon = (ImageView) findViewById(R.id.img_scene_icon);
		tv_title = (TextView) findViewById(R.id.tv_title);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_right = (Button) findViewById(R.id.bt_right);
		bt_right.setText("");
		bt_right.setVisibility(View.VISIBLE);
		bt_right.setBackgroundResource(R.drawable.finish_button_selector);
		tv_title.setText(R.string.add_group_title);
		et_scene_name = (EditText) findViewById(R.id.et_scene_name);

		img_scene_icon.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);

		gv_switch = (GridView) findViewById(R.id.gv_switch);
		gv_sensor = (GridView) findViewById(R.id.gv_sensor);
		switchAdapter = new SceneSwitchAdapter(mContext, switchSelectList);
		sensorAdapter = new SceneSensorAdapter(mContext, sensorSelectList);
		gv_switch.setAdapter(switchAdapter);
		gv_sensor.setAdapter(sensorAdapter);
		
		switchAdapter.setEdit( true );
		sensorAdapter.setEdit( true );
		switchAdapter.setEvent( this );
		sensorAdapter.setEvent( this );
		
		gv_switch.setOnItemClickListener(new GridView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				LogUtil.LogI(Tag, "switchSelectList.size=" + switchSelectList.size());
				LogUtil.LogI(Tag, "position=" + position);
				if (switchSelectList != null)
				{

					if (switchSelectList.size() == 0)
					{
						switchDialog.show();
					}
					else
					{
						if ( position == switchSelectList.size() )
						{
							switchDialog.show();
						}
						else
						{
							LogUtil.LogI(Tag, "setOnItemClickListener");
							DeviceInfo item = switchSelectList.get(position);
							if (item.getDeviceState() == 0)
							{
								item.setDeviceState( (byte)0x01 );
							}
							else
							{
								item.setDeviceState( (byte)0x00 );
							}
							mHandler.sendEmptyMessage( 0x0001 );
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
			// TODO��ɣ���������
			sceneName = et_scene_name.getText().toString().trim();

			if (TextUtils.isEmpty(sceneName))
			{
				ToastUtils.show(mContext, getString(R.string.scene_name_null));
				return;
			}
			if (Utils.String_length(sceneName) < 6
					|| Utils.String_length(sceneName) > 10)
			{
				ToastUtils.show(mContext, getString(R.string.length_error2));
				return;
			}
			startAddGroupTask();

			break;
		case R.id.img_scene_icon:
			showPopupWindow(setIconType, img_scene_icon);
			break;
		case R.id.img_livingroom:
			iconType = 0;
			img_scene_icon
					.setBackgroundResource(R.drawable.group_living_button_selector);
			dismissIconTypePop();
			break;
		case R.id.img_babyroom:
			img_scene_icon
					.setBackgroundResource(R.drawable.group_baby_button_selector);
			dismissIconTypePop();
			iconType = 1;
			break;
		case R.id.img_bathroom:
			img_scene_icon
					.setBackgroundResource(R.drawable.group_bathroom_button_selector);
			dismissIconTypePop();
			iconType = 2;
			break;
		case R.id.img_bedroom:
			img_scene_icon
					.setBackgroundResource(R.drawable.group_bedroom_button_selector);
			dismissIconTypePop();
			iconType = 3;
			break;
		case R.id.img_kitchenroom:
			dismissIconTypePop();
			img_scene_icon
					.setBackgroundResource(R.drawable.group_kitchen_button_selector);
			iconType = 4;
			break;
		case R.id.img_oldroom:
			dismissIconTypePop();
			img_scene_icon
					.setBackgroundResource(R.drawable.group_oldman_button_selector);
			iconType = 5;
			break;
		case R.id.img_readingroom:
			dismissIconTypePop();
			img_scene_icon
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
	 * ��ʼ��ѡ�񿪹ص�����
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
								String s = "��ѡ����:";
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
								mHandler.sendEmptyMessage( 0x0001 );
								LogUtil.LogI(Tag, "switchSelectList.size="
										+ switchSelectList.size());
							}
						}).setNegativeButton(getString(R.string.cancel), null)
				.create();
	}

	/**
	 * ��ʼ��ѡ�񴫸е�����
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
								String s = "��ѡ����:";
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
								mHandler.sendEmptyMessage( 0x0002 );
								LogUtil.LogI(Tag, "sensorSelectList.size="
										+ sensorSelectList.size());
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
		addSceneTask = new AddSceneTask();
		addSceneTask.execute();
	}

	private class AddSceneTask extends AsyncTask<Object, Object, Void>
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
					switchBuffer.append(switchSelectList.get(i).getDeviceId() + "-" + switchSelectList.get(i).getDeviceState() );
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
					sensorBuffer.append(sensorSelectList.get(i).getDeviceId() + "-" + sensorSelectList.get(i).getDeviceState());
				}
			}

			String groupName2 = null;
			try
			{
				groupName2 = URLEncoder.encode(sceneName, "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			responseBase = NetReq.addScene(MyApplication.member.getMemberId(),
					groupName2, MyApplication.member.getSessionId(),
					switchBuffer.toString(), sensorBuffer.toString(),
					MyApplication.member.getSsuid(),
					iconType + "" );
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
				 * 200���ɹ� 300��ϵͳ�쳣 401��memberId����Ϊ�� 402�������Ʋ���Ϊ��
				 * 403��sessionID����Ϊ�� 404��switchs������ʽ���� 405��sensors������ʽ����
				 * 406��cameras������ʽ���� 407����Ա������ 408����Ч��sessionID 409���������Ѵ���
				 * 410��ssuid����Ϊ��
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					responseBase.setSceneName(sceneName);
					responseBase.setIconType(iconType + "");
					responseBase
							.setMemberId(MyApplication.member.getMemberId());
					responseBase.setSsuid(MyApplication.member.getSsuid());
					MainActivity.sceneList.add(responseBase);
					ToastUtils.show(mContext,
							getString(R.string.add_scene_sucess));

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
							mContext.getString(R.string.scene_name_null));
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
							mContext.getString(R.string.sceneName_exist));
				}
				else if (responseBase.getResponseStatus() == 410)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.ssuid_null));
				}

			}
			else
			{
				// ��������ʧ��
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
	
	Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch ( msg.what ) {
			case 0x0001:
				switchAdapter.setList(switchSelectList);
				switchAdapter.notifyDataSetChanged();
				break;

			case 0x0002:
				sensorAdapter.setList(sensorSelectList);
				sensorAdapter.notifyDataSetChanged();
				break;
			}
		};
	};

	@Override
	public void onSwitchDelete(int position) 
	{
		switchSelectList.remove( position );
		mHandler.sendEmptyMessage( 0x0001 );
	}
	
	@Override
	public void onSensorDelete(int position) 
	{
		sensorSelectList.remove( position );
		mHandler.sendEmptyMessage( 0x0002 );
	}

	@Override
	public void onSensorAdd( int position ) 
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
					DeviceInfo item = sensorSelectList.get(position);
					if (item.getDeviceState() == 0)
					{
						item.setDeviceState( (byte)0x01 );
					}
					else
					{
						item.setDeviceState( (byte)0x00 );
					}
					mHandler.sendEmptyMessage( 0x0002 );
				}
			}
		}
	}
	
	private void startSceneDetail()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		sceneDetailTask = new SceneDetailTask();
		sceneDetailTask.execute();
	}
	
	private class SceneDetailTask extends AsyncTask<Object, Object, Void>
	{
		String responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			responseBase = NetReq.getSceneDetail( sceneId + "");
			try
			{

				if (responseBase == null) return null;
				JSONObject myObj = new JSONObject(responseBase);
				switchSelectList.clear();
				sensorSelectList.clear();

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
					// 1���� 0���� 2������
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
						// �ڵ� 1���ڵƣ�2������
						info.setDeviceType(MyConstants.NORMAL_LIGHT);
					}
					else
					{
						// 2������
						info.setDeviceType(MyConstants.SOCKET);
					}
					info.setType(0);// 0Ϊ���أ�1Ϊ����
					info.setMemberId(MyApplication.member.getMemberId());
					info.setSsuid(MyApplication.member.getSsuid());
					LogUtil.LogI(TAG, "groupSwitchList.add");
					switchSelectList.add(info);

				}

				JSONArray vsAdObj2 = myObj.getJSONArray("sensors");
				LogUtil.LogI(TAG, "sensors==" + vsAdObj2.length());
				for (int i = 0; i < vsAdObj2.length(); i++)
				{
					DeviceInfo info = new DeviceInfo();
					// DeviceInfo info2; // �������ʪ�Ⱦ͵÷�Ϊ����
					JSONObject obj = vsAdObj2.getJSONObject(i);
					int sensorState;
					int type = getInt(obj, "type");
					// ���ͣ�1���¶�ʪ�ȣ�2:����3������4������

					byte deviceState = (byte) getInt(obj, "deviceStatus");
					// �豸״̬ 1��������0�� 2�����ߣ�AA�� 3�����󣨷��㣩
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
						// ���ͣ�1���¶�ʪ�ȣ�2:����3������4������

						// info2 = new DeviceInfo();

						info.setMemberId(MyApplication.member.getMemberId());
						info.setDeviceType(MyConstants.TEMPARETRUE_SENSOR);
						info.setDeviceBattery((int) getDouble(obj, "battery"));
						info.setDeviceMac(getStr(obj, "code"));

						info.setGroupID(getInt(obj, "groupId"));
						info.setGroupName(getStr(obj, "groupName"));
						info.setTempValue((float) getDouble(obj, "temp"));
						info.setDeviceChannel(getInt(obj, "tempNo"));// �¶�ͨ����
						info.setDeviceName(getStr(obj, "name"));
						info.setDeviceId(getInt(obj, "sensorId"));
						info.setHumValue((float) getDouble(obj, "hum"));
						if (!TextUtils.isEmpty(getStr(obj, "humNo")))
						{
							info.setDeviceChannel2(getInt(obj, "humNo"));// ʪ��ͨ����
						}
						info.setType(1);// 0Ϊ���أ�1Ϊ����
						LogUtil.LogI(TAG, "1groupSensorList.add");
						sensorSelectList.add(info);
						// list.add(info2);
						break;
					case 2:
						// ���ͣ�1���¶�ʪ�ȣ�2:����3������4������
						info.setType(1);// 0Ϊ���أ�1Ϊ����
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
						info.setDeviceChannel(getInt(obj, "channel"));// ͨ����
						info.setDeviceName(getStr(obj, "name"));
						info.setDeviceId(getInt(obj, "sensorId"));
						LogUtil.LogI(TAG, "2groupSensorList.add");
						sensorSelectList.add(info);
						break;
					case 3:
						// ���ͣ�1���¶�ʪ�ȣ�2:����3������4������
						info.setType(1);// 0Ϊ���أ�1Ϊ����
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
							// 1��2��
							info.setInfraredSwitch(true);
						}
						else
						{
							info.setInfraredSwitch(false);
						}
						info.setDeviceChannel(getInt(obj, "channel"));// ͨ����
						info.setDeviceName(getStr(obj, "name"));
						info.setDeviceId(getInt(obj, "sensorId"));
						LogUtil.LogI(TAG, "3groupSensorList.add");
						sensorSelectList.add(info);
						break;
					case 4:
						// ���ͣ�1���¶�ʪ�ȣ�2:����3������4������
						info.setType(1);// 0Ϊ���أ�1Ϊ����
						info.setMemberId(MyApplication.member.getMemberId());
						info.setDeviceType(MyConstants.BRACELET);
						info.setDeviceBattery((int) getDouble(obj, "battery"));
						info.setDeviceMac(getStr(obj, "code"));
						info.setGroupID(getInt(obj, "groupId"));
						info.setGroupName(getStr(obj, "groupName"));
						info.setDeviceChannel(getInt(obj, "channel"));// ͨ����
						info.setDeviceName(getStr(obj, "name"));
						info.setDeviceId(getInt(obj, "sensorId"));
						LogUtil.LogI(TAG, "4groupSensorList.add");
						sensorSelectList.add(info);
						break;

					default:
						break;
					}
				}

				
			}
			catch (Exception e)
			{
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
				switchAdapter.notifyDataSetChanged();
				sensorAdapter.notifyDataSetChanged();
			}
			else
			{
				// ��������ʧ��
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