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
 * 情景詳細页面
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
	private Button bt_delete_scene;
	private static final String Tag = "AddGroupActivity";
	private String iconType;// 0大厅，1婴儿房，2，浴室 3，卧室 4，厨房 5，老人房 6 书房
	private List<DeviceInfo> switchList;
	private List<DeviceInfo> sensorList;
	private List<DeviceInfo> switchSelectList;// 选择的设备
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
	private SceneInfo sceneInfo;
	
	private int position;
	private int sceneId;
	private SceneDetailTask sceneDetailTask;
	private boolean isEdit;
	private String responseBase;

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
		// 请求情景详情
		startSceneDetail();
	}

	/**
	 * 初始化三个数据里列表
	 */
	private void initData()
	{
		position = getIntent().getIntExtra( "position", 0 );
		sceneInfo = MainActivity.sceneList.get(position);
		sceneId = sceneInfo.getSceneId();
		sceneName = sceneInfo.getSceneName();
		iconType = sceneInfo.getIconType();
		
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
		bt_delete_scene = (Button) findViewById(R.id.bt_delete_scene);
		bt_right.setText(R.string.edit);
		bt_right.setVisibility(View.VISIBLE);
		tv_title.setText( sceneName );
		et_scene_name = (EditText) findViewById(R.id.et_scene_name);
		et_scene_name.setText( sceneName );
		
		et_scene_name.setVisibility( View.GONE );
		img_scene_icon.setVisibility( View.GONE );
		
		img_scene_icon.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);
		bt_delete_scene.setOnClickListener(this);

		gv_switch = (GridView) findViewById(R.id.gv_switch);
		gv_sensor = (GridView) findViewById(R.id.gv_sensor);
		switchAdapter = new SceneSwitchAdapter(mContext, switchSelectList);
		sensorAdapter = new SceneSensorAdapter(mContext, sensorSelectList);
		gv_switch.setAdapter(switchAdapter);
		gv_sensor.setAdapter(sensorAdapter);
		
		switchAdapter.setEvent( this );
		sensorAdapter.setEvent( this );
		
		gv_switch.setOnItemClickListener(new GridView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if ( !isEdit )
				{
					return;
				}
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
							DeviceInfo item = switchAdapter.getList().get(position);
							if (item.getDeviceState() == 0)
							{
								item.setDeviceState( (byte)0x01 );
							}
							else
							{
								item.setDeviceState( (byte)0x00 );
							}
							mHandler.sendEmptyMessage( 0x0003 );
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
			if ( isEdit )
			{
				et_scene_name.setVisibility( View.GONE );
				img_scene_icon.setVisibility( View.GONE );
				bt_delete_scene.setVisibility( View.GONE );
				bt_right.setText(R.string.edit);
				isEdit = !isEdit;
				if ( !TextUtils.isEmpty( responseBase ) )
				{
					parseData();
				}
				switchAdapter.setEdit( isEdit );
				sensorAdapter.setEdit( isEdit );
				mHandler.sendEmptyMessage( 0x0003 );
				mHandler.sendEmptyMessage( 0x0004 );
			}
			else
			{
				finish();
				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
			}
			break;
		case R.id.bt_right:
			if ( isEdit )
			{
				// TODO完成，发送请求
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
				//startAddGroupTask();
			}
			else
			{
				isEdit = !isEdit;
				setIcon();
				et_scene_name.setVisibility( View.VISIBLE );
				img_scene_icon.setVisibility( View.VISIBLE );
				bt_delete_scene.setVisibility( View.VISIBLE );
				bt_right.setText(R.string.finish);
				
				switchAdapter.setEdit( isEdit );
				sensorAdapter.setEdit( isEdit );
				
				mHandler.sendEmptyMessage( 0x0003 );
				mHandler.sendEmptyMessage( 0x0004 );
			}

			break;
		case R.id.img_scene_icon:
			showPopupWindow(setIconType, img_scene_icon);
			break;
		case R.id.img_livingroom:
			iconType = "0";
			img_scene_icon
					.setBackgroundResource(R.drawable.group_living_button_selector);
			dismissIconTypePop();
			break;
		case R.id.img_babyroom:
			img_scene_icon
					.setBackgroundResource(R.drawable.group_baby_button_selector);
			dismissIconTypePop();
			iconType = "1";
			break;
		case R.id.img_bathroom:
			img_scene_icon
					.setBackgroundResource(R.drawable.group_bathroom_button_selector);
			dismissIconTypePop();
			iconType = "2";
			break;
		case R.id.img_bedroom:
			img_scene_icon
					.setBackgroundResource(R.drawable.group_bedroom_button_selector);
			dismissIconTypePop();
			iconType = "3";
			break;
		case R.id.img_kitchenroom:
			dismissIconTypePop();
			img_scene_icon
					.setBackgroundResource(R.drawable.group_kitchen_button_selector);
			iconType = "4";
			break;
		case R.id.img_oldroom:
			dismissIconTypePop();
			img_scene_icon
					.setBackgroundResource(R.drawable.group_oldman_button_selector);
			iconType = "5";
			break;
		case R.id.img_readingroom:
			dismissIconTypePop();
			img_scene_icon
					.setBackgroundResource(R.drawable.group_readingroom_button_selector);
			iconType = "6";
			break;

		case R.id.bt_delete_scene: // 刪除
			break;
		}
	}
	
	private void setIcon()
	{
		if (iconType.equals("1"))
		{
			img_scene_icon.setImageResource(R.drawable.group_baby_button_selector);
		}
		else if (iconType.equals("2"))
		{
			img_scene_icon
			.setImageResource(R.drawable.group_bathroom_button_selector);
		}
		else if (iconType.equals("3"))
		{
			img_scene_icon
			.setImageResource(R.drawable.group_bedroom_button_selector);
		}
		else if (iconType.equals("4"))
		{
			img_scene_icon
			.setImageResource(R.drawable.group_kitchen_button_selector);
		}
		else if (iconType.equals("5"))
		{
			img_scene_icon
			.setImageResource(R.drawable.group_oldman_button_selector);
		}
		else if (iconType.equals("6"))
		{
			img_scene_icon
			.setImageResource(R.drawable.group_readingroom_button_selector);
		}
		else 
		{
			img_scene_icon
			.setImageResource(R.drawable.icon_livingroom);
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
								mHandler.sendEmptyMessage( 0x0001 );
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
			sceneInfo = NetReq.addScene(MyApplication.member.getMemberId(),
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
			if (sceneInfo != null)
			{
				/**
				 * 200：成功 300：系统异常 401：memberId不能为空 402：组名称不能为空
				 * 403：sessionID不能为空 404：switchs参数格式错误 405：sensors参数格式错误
				 * 406：cameras参数格式错误 407：会员不存在 408：无效的sessionID 409：组名称已存在
				 * 410：ssuid不能为空
				 */
				if (sceneInfo.getResponseStatus() == 200)
				{
					sceneInfo.setSceneName(sceneName);
					sceneInfo.setIconType(iconType + "");
					sceneInfo
							.setMemberId(MyApplication.member.getMemberId());
					sceneInfo.setSsuid(MyApplication.member.getSsuid());
					MainActivity.sceneList.add(sceneInfo);
					ToastUtils.show(mContext,
							getString(R.string.add_scene_sucess));

					onBackPressed();
				}
				else if (sceneInfo.getResponseStatus() == 300)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				}
				else if (sceneInfo.getResponseStatus() == 401)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				}
				else if (sceneInfo.getResponseStatus() == 402)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.scene_name_null));
				}
				else if (sceneInfo.getResponseStatus() == 403)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_null));
				}
				else if (sceneInfo.getResponseStatus() == 404)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.switchs_agrs_error));
				}
				else if (sceneInfo.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sensors_agrs_error));
				}
				else if (sceneInfo.getResponseStatus() == 405)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.cameras_agrs_error));
				}
				else if (sceneInfo.getResponseStatus() == 406)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.cameras_agrs_error));
				}
				else if (sceneInfo.getResponseStatus() == 407)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.member_null));
				}
				else if (sceneInfo.getResponseStatus() == 408)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.login_error));
				}
				else if (sceneInfo.getResponseStatus() == 409)
				{
					ToastUtils.show(mContext,
							mContext.getString(R.string.sceneName_exist));
				}
				else if (sceneInfo.getResponseStatus() == 410)
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
			case 0x0003:
				switchAdapter.notifyDataSetChanged();
				break;

			case 0x0004:
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
		if ( !isEdit )
		{
			return;
		}
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
					DeviceInfo item = sensorAdapter.getList().get(position);
					
					if (item.getDeviceState() == 0)
					{
						item.setDeviceState( (byte)0x01 );
					}
					else
					{
						item.setDeviceState( (byte)0x00 );
					}
					mHandler.sendEmptyMessage( 0x0004 );
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
		@Override
		protected Void doInBackground(Object... params)
		{
			responseBase = NetReq.getSceneDetail( sceneId + "");
			if ( TextUtils.isEmpty( responseBase ) )
			{
				return null;
			}
			parseData();
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
				mHandler.sendEmptyMessage( 0x0001 );
				mHandler.sendEmptyMessage( 0x0002 );
			}
			else
			{
				// 网络请求失败
			}

		}
	}
	
	private void parseData()
	{
		switchSelectList.clear();
		sensorSelectList.clear();
		try
		{
			if ( TextUtils.isEmpty( responseBase ) )
			{
				return;
			}
			JSONArray arr = new JSONArray(responseBase);
			for (int i = 0; i < arr.length(); i++)
			{
				DeviceInfo info = new DeviceInfo();
				
				JSONObject obj = arr.getJSONObject(i);
				
				info.setDeviceId( getInt(obj, "deviceId") );
				info.setDeviceName( getStr(obj, "deviceName") );
				// 1：开 0：关 2：离线
				byte state = (byte) getInt(obj, "deviceStatus");
				if (state == 2)
				{
					info.setDeviceState((byte) 0xAA);
				}
				else
				{
					info.setDeviceState( state );
				}
				
				info.setMemberId(MyApplication.member.getMemberId());
				info.setSsuid(MyApplication.member.getSsuid());
				
				int type = getInt(obj, "deviceType");
				
				// 0：插座, 1: 壁灯 ,2:红外
				if (type == 0)
				{
					
					info.setDeviceType(MyConstants.SOCKET);
					info.setType(0);// 0为开关，1为传感
					
					switchSelectList.add(info);
				}
				else if (type == 1)
				{
					info.setDeviceType(MyConstants.NORMAL_LIGHT);
					info.setType(0);// 0为开关，1为传感
					
					switchSelectList.add(info);
				}
				else
				{
					info.setDeviceType(MyConstants.HUMAN_BODY_SENSOR);
					info.setType(1);// 0为开关，1为传感
					
					sensorSelectList.add(info);
				}
			}

			
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
