package com.anji.www.activity;

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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.anji.www.R;
import com.anji.www.adapter.SwitchListAdapter;
import com.anji.www.adapter.SwitchListAdapter.MyClickListener;
import com.anji.www.db.service.AnjiDBservice;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.service.UdpService;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

/**
 * ����ҳ��
 * 
 * @author Ivan
 */
public class TabSwtich extends Fragment implements OnClickListener,
		BaseFragment
{

	private static final String TAG = TabSwtich.class.getName();
	private Button bt_switch_sreach;
	private Button bt_switch_edit;
	private TextView tv_title;
	private CheckBox cb_main_switch;
	private ListView lv_switch;
	private SwitchListAdapter listAdapter;
	MainActivity activity;
	private String state;
	private long lastStartCheck;
	private long lastfinshCheck;
	private Dialog progressDialog;
	private ControlSwitchTask controlTask;
	private DeleteDeviceTask deleteTask;
	private ControlAllSwitchTask controlAllTask;
	private AnjiDBservice dbService;
	private boolean editState;
	public static boolean isNeedrefresh;
	private boolean isFirstResume;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		System.out.println("TabSwtich____onAttach");
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		System.out.println("TabSwtich____onCreate");
		isFirstResume = true;
		dbService = new AnjiDBservice(activity);
		// switchList = UdpService.newInstance(null).getSwitchDeviceList();
		// switchList = MainActivity.switchList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		System.out.println("TabSwtich____onCreateView");
		return inflater.inflate(R.layout.tab_switch, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView()
	{
		progressDialog = activity.getProgressDialog();
		bt_switch_edit = (Button) activity.findViewById(R.id.bt_switch_edit);
		bt_switch_sreach = (Button) activity
				.findViewById(R.id.bt_switch_sreach);
		tv_title = (TextView) activity.findViewById(R.id.tv_regiest_title);
		tv_title.setText(getString(R.string.tab_switch));
		cb_main_switch = (CheckBox) activity.findViewById(R.id.cb_main_switch);
		lv_switch = (ListView) activity.findViewById(R.id.lv_switch);
		bt_switch_sreach
				.setBackgroundResource(R.drawable.search_button_selector);
		// listAdapter = new SwitchListAdapter(activity, UdpService.newInstance(
		// null).getSwitchDeviceList());
		listAdapter = new SwitchListAdapter(activity, this,
				MainActivity.switchList, mListener);
		lv_switch.setAdapter(listAdapter);
		lv_switch.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (editState)
				{
					Intent intent = new Intent(activity,
							EditDeviceActivity.class);
					intent.putExtra("deviceType", 0);
					intent.putExtra("deviceId",
							MainActivity.switchList.get(position).getDeviceId());
					intent.putExtra("groupId",
							MainActivity.switchList.get(position).getGroupID());
					intent.putExtra("deviceName",
							MainActivity.switchList.get(position)
									.getDeviceName());
					activity.startActivity(intent);
				}
			}
		});
		// lv_switch.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id)
		// {
		// if (MainActivity.switchList != null
		// && MainActivity.switchList.size() > 0)
		// {
		//
		// startDeleteDevice(MainActivity.switchList.get(position));
		// }
		// return true;
		// }
		// });
		// Utils.setListViewHeight(lv_switch);
		setListener();
	}

	private void setListener()
	{
		bt_switch_sreach.setOnClickListener(this);
		bt_switch_edit.setOnClickListener(this);
		cb_main_switch.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (MainActivity.isInNet)
				{

					inNetControlAll();
				}
				else
				{
					// ��������ȫ��
					startControlAllSwitch(cb_main_switch.isChecked());
				}
			}

		});
	}

	private void inNetControlAll()
	{
		if (System.currentTimeMillis() - lastStartCheck > 1000)
		{
			if (cb_main_switch.isChecked())
			{
				state = "FF";
			}
			else
			{
				state = "00";
			}

			LogUtil.LogI(TAG,
					"System.currentTimeMillis()=" + System.currentTimeMillis());
			LogUtil.LogI(TAG, "lastStartCheck=" + lastStartCheck);

			lastStartCheck = System.currentTimeMillis();

			new Thread()
			{
				public void run()
				{
					for (int i = 0; i < MainActivity.switchList.size(); i++)
					{
						if (MainActivity.switchList.get(i).getDeviceState() != (byte) 0xaa)
						{
							// ��Ϊ����״̬

							byte[] bytes = Utils
									.hexStringToBytes("20F200010D01"
											+ MainActivity.switchList.get(i)
													.getDeviceMac()
											+ "0"
											+ MainActivity.switchList.get(i)
													.getDeviceChannel()
											+ MainActivity.switchList.get(i)
													.getDeviceType() + state);
							// + item.getDeviceType() + "ee");
							UdpService.newInstance(null).sendOrders(bytes);
							try
							{
								Thread.sleep(1000);
								// activity.qurryOnlyAll();
							}
							catch (InterruptedException e)
							{
								// Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					activity.qurryOnlyAll();
					lastfinshCheck = System.currentTimeMillis();
				};
			}.start();
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		System.out.println("TabSwtich____onStart");
	}

	@Override
	public void onResume()
	{
		super.onResume();
		refreshView();
		// if (isNeedrefresh)
		// {
		// isNeedrefresh = false;
		// activity.qurryAllSwtich();
		if (!isFirstResume)
		{
			activity.startQurrySwitch();
			if (MainActivity.isInNet)
			{
				activity.qurryInNet();
			}
		}
		isFirstResume = false;
		// }
		System.out.println("TabSwtich____onResume");
		if (editState)
		{
			editState = false;
			bt_switch_edit.setText(getString(R.string.edit));
			listAdapter.setEditState(editState);
			cb_main_switch.setClickable(true);
			listAdapter.setEditState(editState);
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		System.out.println("TabSwtich____onPause");
		activity.cancelQurrySwitch();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		System.out.println("TabSwtich____onStop");
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		System.out.println("TabSwtich____onDestroyView");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (dbService != null)
		{
			dbService.closeDB2();
		}
		System.out.println("TabSwtich____onDestroy");
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		System.out.println("TabSwtich____onDetach");
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_switch_sreach:
			// ����
			if (editState)
			{
				editState = false;
				bt_switch_edit.setText(getString(R.string.edit));
				listAdapter.setEditState(editState);
				cb_main_switch.setClickable(true);
			}
//			else
//			{
//				editState = true;
//				bt_switch_edit.setText(getString(R.string.finish));
//				cb_main_switch.setClickable(false);
//			}
			listAdapter.setEditState(editState);
			activity.qurryAllSwtich();
			break;
		case R.id.bt_switch_edit:
			// �༭
			if (editState)
			{
				editState = false;
				bt_switch_edit.setText(getString(R.string.edit));
				listAdapter.setEditState(editState);
				cb_main_switch.setClickable(true);
			}
			else
			{
				editState = true;
				bt_switch_edit.setText(getString(R.string.finish));
				cb_main_switch.setClickable(false);
			}
			listAdapter.setEditState(editState);
			break;

		// case R.id.rl_NDS:
		// // ģ������
		// Intent intent = new Intent(activity, SimulateExperience.class);
		// activity.startActivity(intent);
		// break;

		default:
			break;
		}
	}

	@Override
	public void refreshView()
	{
		if (listAdapter != null)
		{
			Collections.sort(MainActivity.switchList);
			listAdapter.setList(MainActivity.switchList);
			// Utils.setListViewHeight(lv_switch);
			LogUtil.LogI(TAG, "refreshView");
			// boolean isMainOpen = true;
			// for (int i = 0; i < switchList.size(); i++)
			// {
			// if (switchList.get(i).getDeviceState() == 0 ||
			// switchList.get(i).getDeviceState() == (byte)0xaa)
			// {
			//
			// isMainOpen = false;
			// }
			// }
			// cb_main_switch.setChecked(isMainOpen);
		}
	}

	/**
	 * �����л�����
	 * 
	 * @param switchInfo
	 */
	public void startControlSwitch(DeviceInfo switchInfo)
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
			MyApplication app = (MyApplication) activity.getApplication();
			Member member = app.getMember();
			info = params[0];
			int controlState;
			if (info != null && info.getDeviceState() != (byte) 0xAA)
			{
				// ��Ϊ����״̬
				if (info.getDeviceState() == 0)
				{
					// 0��ʾ��ǰΪ�ر�״̬
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
				 * 200���ɹ� 201������ʧ�� 300��ϵͳ�쳣 401��username����Ϊ�� 402��switchId����Ϊ��
				 * 403��sessionID����Ϊ�� 404��operType����Ϊ�� 405��operTypeȡֵ��Χ����
				 * 406���ỰID��Ч 407���豸��ɾ�� 408���豸������(δ��������)
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// ���Ƴɹ�

					if (info.getDeviceState() == 0)
					{
						info.setDeviceState((byte) 1);
					}
					else
					{
						info.setDeviceState((byte) 0);
					}

					listAdapter.notifyDataSetChanged();

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
							activity.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.switch_id_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.control_type_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.control_type_error));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.login_error));
				}
				else if (responseBase.getResponseStatus() == 407)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.device_delete));
				}
				else if (responseBase.getResponseStatus() == 408)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.device_out_line));
				}
			}
			else
			{
				// ��������ʧ��
			}
		}
	}

	/**
	 * �����л�����
	 * 
	 * @param item
	 * @param isOpne
	 */
	private void inNetControlSwitch(final DeviceInfo item, boolean isOpne)
	{
		final String state;
		if (isOpne)
		{
			state = "FF";
		}
		else
		{
			state = "00";
		}
		LogUtil.LogI(TAG, "onCheckedChanged isChecked=" + isOpne);

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
					e.printStackTrace();
				}
			};

		}.start();
	}

	/**
	 * �����������п���
	 * 
	 * @param switchInfo
	 */
	public void startControlAllSwitch(boolean isOpen)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		controlAllTask = new ControlAllSwitchTask();
		controlAllTask.execute(isOpen);
	}

	private class ControlAllSwitchTask extends
			AsyncTask<Boolean, Object, ResponseBase>
	{
		ResponseBase responseBase;
		boolean isOpen;

		@Override
		protected ResponseBase doInBackground(Boolean... params)
		{
			MyApplication app = (MyApplication) activity.getApplication();
			Member member = app.getMember();
			isOpen = params[0];
			int controlState;
			// ��Ϊ����״̬
			if (isOpen)
			{
				// 0��ʾ��ǰΪ�ر�״̬
				controlState = 1;
			}
			else
			{
				controlState = 0;
			}
			if (member != null)
			{
				responseBase = NetReq.controlAllSwitch(member.getUsername(),
						member.getSessionId(), member.getSsuid() + "",
						controlState + "");
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
				 * 200���ɹ� 300��ϵͳ�쳣 401��username����Ϊ�� 402��ssuid����Ϊ��
				 * 403��sessionID����Ϊ�� 404��operType����Ϊ�� 405��operTypeȡֵ��Χ����
				 * 406���ỰID��Ч
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// ���Ƴɹ�
					for (int i = 0; i < MainActivity.switchList.size(); i++)
					{
						DeviceInfo info = MainActivity.switchList.get(i);

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

					listAdapter.notifyDataSetChanged();

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
							activity.getString(R.string.ssuid_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.control_type_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.control_type_error));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(activity,
							activity.getString(R.string.login_error));
				}
			}
			else
			{
				// ��������ʧ��
			}
		}
	}

	/**
	 * ɾ���豸
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
						info.getDeviceId() + "", 1 + "");
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
				 * 200���ɹ� 300��ϵͳ�쳣 401���豸����Ϊ�ջ�ȡֵ���ز���ȷ��ȡֵ��Χ1��2��3��
				 * 402��deviceId����Ϊ�� 403��ssuid��������Ϊ�� 404��������ssuid����
				 * 405���豸�������ߣ��޷�ɾ�� 406������δ�ϱ� 407�������ڸ��豸
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// ɾ���ɹ�

					MainActivity.switchList.remove(info);
					for (int i = 0; i < MainActivity.switchList.size(); i++)
					{
						if (MainActivity.switchList.get(i).getDeviceMac()
								.equals(info.getDeviceMac()))
						{
							MainActivity.switchList.remove(i);
						}
					}
					LogUtil.LogI(TAG, "deviceID=" + info.getDeviceId());
					LogUtil.LogI(TAG, "getType=" + info.getType());
					int reslut = dbService.deleteDeviceByIDandType(
							info.getDeviceId(), info.getType());
					LogUtil.LogI(TAG, "ɾ�����ݿ���=" + reslut);
					listAdapter.notifyDataSetChanged();
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
							activity.getString(R.string.switch_id_null));
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
				// ��������ʧ��
			}
		}
	}

	/**
	 * ʵ���࣬��Ӧ��ť����¼�
	 */
	private MyClickListener mListener = new MyClickListener()
	{
		@Override
		public void myOnClick(int position, View v)
		{
			// Toast.makeText(
			// MainActivity.this,
			// "listview���ڲ��İ�ť������ˣ���λ����-->" + position + ",������-->"
			// + contentList.get(position), Toast.LENGTH_SHORT)
			// .show();
			if (MainActivity.switchList != null
					&& MainActivity.switchList.size() > 0)
			{

				startDeleteDevice(MainActivity.switchList.get(position));
			}
		}
	};

}
