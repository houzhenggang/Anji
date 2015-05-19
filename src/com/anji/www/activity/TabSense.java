package com.anji.www.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ScrollView;

import com.anji.www.R;
import com.anji.www.adapter.HumitureListAdapter;
import com.anji.www.adapter.SensorListAdapter;
import com.anji.www.adapter.SwitchListAdapter.MyClickListener;
import com.anji.www.constants.MyConstants;
import com.anji.www.db.service.AnjiDBservice;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.service.UdpService;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

/**
 * 传感页面
 * 
 * @author Ivan
 */
public class TabSense extends Fragment implements OnClickListener, BaseFragment
{

	private static final String TAG = TabSense.class.getName();
	MainActivity activity;
	private ScrollView sv_sensor;
	private Button bt_search;
	private ListView lv_humiture;// 温湿度传感
	private ListView lv_smoke;// 烟雾传感
	private ListView lv_infrared;// 红外传感
	private ListView lv_wearable;// 穿戴设备传感
	private CheckBox cb_infrared_switch;
	// private List<DeviceInfo> sensorList;// 传感器列表列表
	public static List<DeviceInfo> humitureList = new ArrayList<DeviceInfo>();// 温湿度列表
	public static List<DeviceInfo> somkeList = new ArrayList<DeviceInfo>();// 烟雾列表
	public static List<DeviceInfo> infaredList = new ArrayList<DeviceInfo>();// 红外传感列表
	public static List<DeviceInfo> wearableList = new ArrayList<DeviceInfo>();// 穿戴设备列表
	private SensorListAdapter somkeAdapter;
	private SensorListAdapter infaredAdapter;
	private SensorListAdapter wearableAdapter;
	private UdpService service;
	private HumitureListAdapter humitureListAdapter;
	private Dialog progressDialog;
	private DeleteDeviceTask deleteTask;
	private ControlAllInfrared controlAllInfraredTask;
	private QurryAllInfrared qurryAllInfrared;
	private AnjiDBservice dbService;
	private Button bt_sensor_edit;
	private boolean isEditState;
	public static boolean isNeedRefresh;
	private boolean isFristResume;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		System.out.println("TabSense____onAttach");
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// db = new DbTool(getActivity());
		dbService = new AnjiDBservice(activity);
		isFristResume = true;
		System.out.println("TabSense____onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		System.out.println("TabSense____onCreateView");
		progressDialog = activity.getProgressDialog();
		return inflater.inflate(R.layout.tab_sense, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
		startQurryAllInfrared();

	}

	private void initView()
	{
		bt_sensor_edit = (Button) activity.findViewById(R.id.bt_sensor_edit);
		sv_sensor = (ScrollView) activity.findViewById(R.id.sv_sensor);
		bt_search = (Button) activity.findViewById(R.id.bt_search);
		lv_humiture = (ListView) activity.findViewById(R.id.lv_humiture);
		lv_smoke = (ListView) activity.findViewById(R.id.lv_smoke);
		lv_infrared = (ListView) activity.findViewById(R.id.lv_infrared);
		lv_wearable = (ListView) activity.findViewById(R.id.lv_wearable);

		lv_humiture.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (isEditState)
				{
					Intent intent = new Intent(activity,
							EditDeviceActivity.class);
					intent.putExtra("deviceType", 1);
					intent.putExtra("deviceId", humitureList.get(position)
							.getDeviceId());
					intent.putExtra("groupId", humitureList.get(position)
							.getGroupID());
					intent.putExtra("deviceName", humitureList.get(position)
							.getDeviceName());
					activity.startActivity(intent);

				}
			}
		});

		lv_smoke.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (isEditState)
				{
					Intent intent = new Intent(activity,
							EditDeviceActivity.class);
					intent.putExtra("deviceType", 1);
					intent.putExtra("deviceId", somkeList.get(position)
							.getDeviceId());
					intent.putExtra("groupId", somkeList.get(position)
							.getGroupID());
					intent.putExtra("deviceName", somkeList.get(position)
							.getDeviceName());
					activity.startActivity(intent);

				}
			}
		});

		lv_infrared.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (isEditState)
				{
					Intent intent = new Intent(activity,
							EditDeviceActivity.class);
					intent.putExtra("deviceType", 1);
					intent.putExtra("deviceId", infaredList.get(position)
							.getDeviceId());
					intent.putExtra("groupId", infaredList.get(position)
							.getGroupID());
					intent.putExtra("deviceName", infaredList.get(position)
							.getDeviceName());
					activity.startActivity(intent);

				}
			}
		});
		// lv_humiture.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id)
		// {
		// if (humitureList != null && humitureList.size() >= position)
		// {
		//
		// startDeleteDevice(humitureList.get(position));
		// }
		// return true;
		// }
		// });

		// lv_smoke.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id)
		// {
		// if (somkeList != null && somkeList.size() >= position)
		// {
		//
		// startDeleteDevice(somkeList.get(position));
		// }
		// return true;
		// }
		// });
		//
		// lv_infrared.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id)
		// {
		// if (infaredList != null && infaredList.size() >= position)
		// {
		//
		// startDeleteDevice(infaredList.get(position));
		// }
		// return true;
		// }
		// });

		cb_infrared_switch = (CheckBox) activity
				.findViewById(R.id.cb_infrared_switch);

		bt_search.setOnClickListener(this);
		bt_sensor_edit.setOnClickListener(this);
		// cb_infrared_switch
		// .setOnCheckedChangeListener(new OnCheckedChangeListener()
		// {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked)
		// {
		// // TODO 红外开关
		// startControlAllInfrared(isChecked);
		// }
		// });
		cb_infrared_switch.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startControlAllInfrared(cb_infrared_switch.isChecked());
			}
		});
	}

	@Override
	public void onStart()
	{
		super.onStart();
		System.out.println("TabSense____onStart");

	}

	private void initData()
	{
		service = UdpService.newInstance(null);
		// if (MainActivity.isInNet)
		// {
		// //内网
		// // sensorList = service.getSenceDeviceList();
		// humitureList = service.getSenceHumitureList();
		// somkeList = service.getSenceSomkeList();
		// infaredList = service.getSenceInfraredList();
		// wearableList = service.getSenceWearableList();
		// }else {
		// 外网

		humitureList = new ArrayList<DeviceInfo>();
		somkeList = new ArrayList<DeviceInfo>();
		infaredList = new ArrayList<DeviceInfo>();
		wearableList = new ArrayList<DeviceInfo>();

		initSensorData();

		// }
		humitureListAdapter = new HumitureListAdapter(activity, humitureList,
				mListener);
		lv_humiture.setAdapter(humitureListAdapter);
		somkeAdapter = new SensorListAdapter(activity, somkeList, mListener);
		infaredAdapter = new SensorListAdapter(activity, infaredList, mListener);
		wearableAdapter = new SensorListAdapter(activity, wearableList,
				mListener);
		lv_smoke.setAdapter(somkeAdapter);
		lv_infrared.setAdapter(infaredAdapter);
		lv_wearable.setAdapter(wearableAdapter);
		Utils.setListViewHeight(lv_humiture);
		Utils.setListViewHeight(lv_smoke);
		Utils.setListViewHeight(lv_infrared);
		Utils.setListViewHeight(lv_wearable);
	}

	private void initSensorData()
	{
		if (MainActivity.sensorList != null
				&& MainActivity.sensorList.size() > 0)
		{
			humitureList.clear();
			somkeList.clear();
			infaredList.clear();
			wearableList.clear();
			for (int i = 0; i < MainActivity.sensorList.size(); i++)
			{
				DeviceInfo info = MainActivity.sensorList.get(i);
				if (info.getDeviceType().equals(MyConstants.TEMPARETRUE_SENSOR))
				{
					DeviceInfo info2 = new DeviceInfo();
					info2.setDeviceType(MyConstants.HUMIDITY_SENSOR);
					info2.setCharge(info.isCharge());
					info2.setDeviceBattery(info.getDeviceBattery());
					info2.setDeviceChannel(info.getDeviceChannel2());
					info2.setDeviceChannel2(info.getDeviceChannel2());
					info2.setDeviceId(info.getDeviceId());
					info2.setDeviceMac(info.getDeviceMac());
					info2.setDeviceName(info.getDeviceName());
					info2.setDeviceState(info.getDeviceState());
					info2.setGroupID(info.getGroupID());
					info2.setGroupName(info.getGroupName());
					info2.setMemberId(info.getMemberId());
					info2.setHumValue(info.getHumValue());
					info2.setSensorState(info.getSensorState());
					info2.setSsuid(info.getSsuid());
					info2.setType(1);
					humitureList.add(info);
					humitureList.add(info2);

				}
				else if (info.getDeviceType().equals(MyConstants.SMOKE_SENSOR))
				{
					somkeList.add(info);
				}
				else if (info.getDeviceType().equals(
						MyConstants.HUMAN_BODY_SENSOR))
				{
					infaredList.add(info);
				}
				else
				{
					wearableList.add(info);
				}
			}

		}
	}

	@Override
	public void onResume()
	{

		// qurrySensor();
		super.onResume();
		// if (isNeedRefresh)
		// {
		// isNeedRefresh = false;
		// // 外网获取传感
		if (!isFristResume)
		{

			activity.startQurrySensor();
			if (MainActivity.isInNet)
			{

				if (MainActivity.sensorList != null
						&& MainActivity.sensorList.size() > 0)
				{
					qurryData();
				}
			}
		}
		isFristResume = false;
		// qurrySensor();
		// }
		if (isEditState)
		{
			isEditState = false;
			bt_sensor_edit.setText(getString(R.string.edit));
			cb_infrared_switch.setClickable(true);

		}
		somkeAdapter.setEditState(isEditState);
		infaredAdapter.setEditState(isEditState);
		humitureListAdapter.setEditState(isEditState);
		refreshView();
		System.out.println("TabSense____onResume");
	}

	private void qurrySensor()
	{
		if (MainActivity.isInNet)
		{

			if (MainActivity.sensorList != null
					&& MainActivity.sensorList.size() > 0)
			{
				qurryData();
			}
		}
		else
		{
			// 外网获取传感
			activity.startQurrySensorWithDialog();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		activity.cancelQurrySensor();
		System.out.println("TabSense____onPause");
	}

	@Override
	public void onStop()
	{
		super.onStop();
		System.out.println("TabSense____onStop");
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		System.out.println("TabSense____onDestroyView");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (null != dbService)
		{
			dbService.closeDB2();
		}
		System.out.println("TabSense____onDestroy");
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		System.out.println("TabSense____onDetach");
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_search:
			// 搜索按钮
			if (isEditState)
			{
				isEditState = false;
				bt_sensor_edit.setText(getString(R.string.edit));
				cb_infrared_switch.setClickable(true);

			}
//			else
//			{
//				isEditState = true;
//				bt_sensor_edit.setText(getString(R.string.finish));
//				cb_infrared_switch.setClickable(false);
//			}
			somkeAdapter.setEditState(isEditState);
			infaredAdapter.setEditState(isEditState);
			humitureListAdapter.setEditState(isEditState);
			LogUtil.LogI(TAG, "MainActivity.isInNet=" + MainActivity.isInNet);
			// if (MainActivity.isInNet)
			// {
			qurrySensor();
			// }
			// else
			// {
			// // TODO外网
			// }
			break;

		case R.id.bt_sensor_edit:
			// TODO 编辑按钮
			if (isEditState)
			{
				isEditState = false;
				bt_sensor_edit.setText(getString(R.string.edit));
				cb_infrared_switch.setClickable(true);

			}
			else
			{
				isEditState = true;
				bt_sensor_edit.setText(getString(R.string.finish));
				cb_infrared_switch.setClickable(false);
			}
			somkeAdapter.setEditState(isEditState);
			infaredAdapter.setEditState(isEditState);
			humitureListAdapter.setEditState(isEditState);

			break;
		// case R.id.rl_NDS:
		// 模拟体验
		// Intent intent = new Intent(activity, SimulateExperience.class);
		// activity.startActivity(intent);
		// break;

		default:
			break;
		}
	}

	private void qurryData()
	{
		new Thread()
		{
			public void run()
			{
				DeviceInfo item;
				DeviceInfo humituerItem;
				// 湿度传感需要单独发
				LogUtil.LogI(TAG, "humitureList.size=" + humitureList.size());
				LogUtil.LogI(TAG, "MainActivity.sensorList.size()="
						+ MainActivity.sensorList.size());
				for (int i = 0; i < humitureList.size(); i++)
				{
					humituerItem = humitureList.get(i);
					if (humituerItem.getDeviceType().equals(
							MyConstants.HUMIDITY_SENSOR))
					{
						byte[] orders = Utils.hexStringToBytes("20F300010C01"
								+ humituerItem.getDeviceMac() + "0"
								+ humituerItem.getDeviceChannel2()
								+ humituerItem.getDeviceType());
						LogUtil.LogI(TAG,
								"orders=" + Utils.bytesToHexString(orders));
						service.sendOrders(orders);
					}
				}
				for (int i = 0; i < MainActivity.sensorList.size(); i++)
				{
					item = MainActivity.sensorList.get(i);
					byte[] orders = Utils.hexStringToBytes("20F300010C01"
							+ item.getDeviceMac() + "0"
							+ item.getDeviceChannel() + item.getDeviceType());
					LogUtil.LogI(TAG,
							"orders=" + Utils.bytesToHexString(orders));
					service.sendOrders(orders);
					// try
					// {
					// Thread.sleep(200);
					// }
					// catch (InterruptedException e)
					// {
					// // Auto-generated catch block
					// e.printStackTrace();
					// }
				}
				// LogUtil.LogI(TAG,
				// "orders=20F3010C016DF22604004B1200011002" );
				// service.sendOrders(Utils.hexStringToBytes("20F3010C016DF22604004B1200011002"));
			};

		}.start();
	}

	@Override
	public void refreshView()
	{
		// Auto-generated method stub
		if (humitureListAdapter != null)
		{
			initSensorData();
			// LogUtil.LogI(TAG, "refreshView  somkeList.size=" +
			// somkeList.size());
			// LogUtil.LogI(TAG,
			// "refreshView  infaredList.size=" + infaredList.size());
			// LogUtil.LogI(TAG,
			// "refreshView  wearableList.size=" + wearableList.size());
			// LogUtil.LogI(TAG,
			// "refreshView  humitureList.size=" + humitureList.size());
			// sensorList = service.getSenceDeviceList();
			// humitureList = service.getSenceHumitureList();
			// somkeList = service.getSenceSomkeList();
			// infaredList = service.getSenceInfraredList();
			// wearableList = service.getSenceWearableList();
			// for (int i = 0; i < humitureList.size(); i++)
			// {
			// LogUtil.LogI(TAG, "refreshView  humitureList.getHumValue="
			// + humitureList.get(i).getHumValue());
			// LogUtil.LogI(TAG, "refreshView  humitureList.getTempValue="
			// + humitureList.get(i).getTempValue());
			// }

			Collections.sort(humitureList);
			Collections.sort(somkeList);
			Collections.sort(infaredList);
			Collections.sort(wearableList);

			humitureListAdapter.setList(humitureList);
			somkeAdapter.setList(somkeList);
			infaredAdapter.setList(infaredList);
			wearableAdapter.setList(wearableList);
			Utils.setListViewHeight(lv_humiture);
			Utils.setListViewHeight(lv_smoke);
			Utils.setListViewHeight(lv_infrared);
			Utils.setListViewHeight(lv_wearable);
			humitureListAdapter.notifyDataSetChanged();
			somkeAdapter.notifyDataSetChanged();
			infaredAdapter.notifyDataSetChanged();
			wearableAdapter.notifyDataSetChanged();
			lv_humiture.invalidate();
			lv_smoke.invalidate();
			lv_infrared.invalidate();
			lv_wearable.invalidate();
			sv_sensor.invalidate();

		}
	}

	/**
	 * 删除设备
	 * 
	 * @param switchInfo
	 */
	public void startDeleteDevice(DeviceInfo switchInfo)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		deleteTask = new DeleteDeviceTask();
		deleteTask.execute(switchInfo);
	}

	private class DeleteDeviceTask extends
			AsyncTask<DeviceInfo, Object, ResponseBase>
	{
		ResponseBase responseBase;
		DeviceInfo info;

		@Override
		protected ResponseBase doInBackground(DeviceInfo... params)
		{
			info = params[0];

			if (info != null)
			{
				responseBase = NetReq.deleteDevice(info.getSsuid(),
						info.getDeviceId() + "", 2 + "");
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
				 * 200：成功 300：系统异常 401：设备类型为空或取值返回不正确（取值范围1、2、3）
				 * 402：deviceId不能为空 403：ssuid参数不能为空 404：不存在ssuid网关
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 控制成功

					// MainActivity.sensorList.remove(info);
					for (int i = 0; i < MainActivity.sensorList.size(); i++)
					{
						if (MainActivity.sensorList.get(i).getDeviceMac()
								.equals(info.getDeviceMac()))
						{
							MainActivity.sensorList.remove(i);
						}
					}
					initSensorData();
					LogUtil.LogI(TAG, "deviceID=" + info.getDeviceId());
					LogUtil.LogI(TAG, "getType=" + info.getType());
					int reslut = dbService.deleteDeviceByIDandType(
							info.getDeviceId(), info.getType());
					LogUtil.LogI(TAG, "删除数据库结果=" + reslut);
					Utils.setListViewHeight(lv_humiture);
					Utils.setListViewHeight(lv_infrared);
					Utils.setListViewHeight(lv_smoke);
					somkeAdapter.notifyDataSetChanged();
					infaredAdapter.notifyDataSetChanged();
					wearableAdapter.notifyDataSetChanged();
					sv_sensor.invalidate();
					ToastUtils.show(activity,
							activity.getString(R.string.delete_device_sucess));
				}
				else if (responseBase.getResponseStatus() == 201)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.operation_failed));
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.devicetype_error));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.sensor_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.ssuid_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.delete_outline));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.ssiu_not_up));
				}
				else if (responseBase.getResponseStatus() == 407)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.ssiu_not_exist));
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
	public void startControlAllInfrared(boolean isOpen)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		controlAllInfraredTask = new ControlAllInfrared();
		controlAllInfraredTask.execute(isOpen);
	}

	private class ControlAllInfrared extends
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
					responseBase = NetReq.ControlAllInfrared(
							MyApplication.member.getUsername(),
							MyApplication.member.getSessionId(), 1 + "",
							MyApplication.member.getSsuid());
				}
				else
				{
					responseBase = NetReq.ControlAllInfrared(
							MyApplication.member.getUsername(),
							MyApplication.member.getSessionId(), 2 + "",
							MyApplication.member.getSsuid());
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
				 * 200：成功 300：系统异常 401：username不能为空 402：sessionId不能为空
				 * 403：ssuid不能为空 404：operType不能为空 405：operType取值范围错误 406：会话ID无效
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 控制成功

				}
				else
				{
					cb_infrared_switch.setChecked(!isOpen);
					if (responseBase.getResponseStatus() == 201)
					{
						ToastUtils.show(activity,
								activity.getString(R.string.operation_failed));
					}
					else if (responseBase.getResponseStatus() == 300)
					{
						ToastUtils.show(activity,
								activity.getString(R.string.system_error));
					}
					else if (responseBase.getResponseStatus() == 401)
					{
						ToastUtils.show(activity,
								activity.getString(R.string.name_null));
					}
					else if (responseBase.getResponseStatus() == 402)
					{
						ToastUtils.show(activity,
								activity.getString(R.string.sensor_id_null));
					}
					else if (responseBase.getResponseStatus() == 403)
					{
						ToastUtils.show(activity,
								activity.getString(R.string.ssuid_null));
					}
					else if (responseBase.getResponseStatus() == 404)
					{
						ToastUtils.show(activity,
								activity.getString(R.string.oper_type_null));
					}
					else if (responseBase.getResponseStatus() == 405)
					{
						ToastUtils.show(activity,
								activity.getString(R.string.oper_type_error));
					}
					else if (responseBase.getResponseStatus() == 406)
					{
						ToastUtils.show(activity,
								activity.getString(R.string.login_error));
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
	 * 查询红外推送总开关
	 * 
	 * @param isOpen
	 */
	public void startQurryAllInfrared()
	{
		// if (progressDialog != null && !progressDialog.isShowing())
		// {
		// progressDialog.show();
		// }
		qurryAllInfrared = new QurryAllInfrared();
		qurryAllInfrared.execute();
	}

	private class QurryAllInfrared extends
			AsyncTask<Void, Object, ResponseBase>
	{
		ResponseBase responseBase;

		@Override
		protected ResponseBase doInBackground(Void... params)
		{

			if (MyApplication.member != null)
			{
				responseBase = NetReq.qurryAllInfrared(MyApplication.member
						.getSsuid());
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
				 * 300：系统异常 1:开 2：关
				 */
				if (responseBase.getResponseStatus() == 1)
				{
					// 总开关 开
					cb_infrared_switch.setChecked(true);

				}
				else if (responseBase.getResponseStatus() == 2)
				{
					// 总开关 关
					cb_infrared_switch.setChecked(false);

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.system_error));
				}
			}
			else
			{
				// 网络请求失败
			}
		}
	}

	/**
	 * 实现类，响应按钮点击事件
	 */
	private MyClickListener mListener = new MyClickListener()
	{
		@Override
		public void myOnClick(int position, View v)
		{
			// Toast.makeText(
			// MainActivity.this,
			// "listview的内部的按钮被点击了！，位置是-->" + position + ",内容是-->"
			// + contentList.get(position), Toast.LENGTH_SHORT)
			// .show();
			DeviceInfo info = (DeviceInfo) v.getTag(R.id.bt_delete);
			startDeleteDevice(info);
		}
	};

}
