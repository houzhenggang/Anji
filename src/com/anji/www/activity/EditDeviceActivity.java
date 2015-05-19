package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.adapter.EditDevictGroupListAdapter;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class EditDeviceActivity extends BaseActivity implements OnClickListener
{
	private Context context;
	private Button bt_back;
	private Button bt_finish;
	private EditText et_device_name;
	private ListView lv_group;
	private EditDevictGroupListAdapter adapter;
	private int type;// 0为开关 ，1为传感,2为摄像头
	private String deviceName;
	private int groupId;
	private int deviceId;
	private Dialog progressDialog;
	private EditDeviceTask editDeviceTask;
	private static final String Tag = "EditDeviceActivity";
	private String deviceNewName;
	private int selectedItem;
	private String newGroupId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_device);
		context = this;
		selectedItem = -1;
		initData();
		initView();
		LogUtil.LogI(Tag, "selectedItem=" + selectedItem);
	}

	private void initData()
	{
		Intent intent = getIntent();
		type = intent.getIntExtra("deviceType", 0);
		deviceId = intent.getIntExtra("deviceId", 0);
		groupId = intent.getIntExtra("groupId", -1);
		if (groupId == 0)
		{
			groupId = -1;
		}
		deviceName = intent.getStringExtra("deviceName");
	}

	private void initView()
	{
		progressDialog = DisplayUtils.createDialog(this);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_finish = (Button) findViewById(R.id.bt_finish);
		et_device_name = (EditText) findViewById(R.id.et_device_name);
		lv_group = (ListView) findViewById(R.id.lv_group);

		et_device_name.setText(deviceName);
		bt_back.setOnClickListener(this);
		bt_finish.setOnClickListener(this);
		adapter = new EditDevictGroupListAdapter(context,
				MainActivity.groupList);
		lv_group.setAdapter(adapter);
		LogUtil.LogI(Tag, "groupId=" + groupId);
		for (int i = 0; i < MainActivity.groupList.size(); i++)
		{
			LogUtil.LogI(Tag, " groupList groupId="
					+ MainActivity.groupList.get(i).getGroupId());
			if (groupId != 0
					&& MainActivity.groupList.get(i).getGroupId() == groupId)
			{
				selectedItem = i;

			}
		}

		lv_group.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				selectedItem = position;
				adapter.setSelectedItem(selectedItem);
			}
		});
		adapter.setSelectedItem(selectedItem);
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
		case R.id.bt_finish:
			// TODO 完成
			deviceNewName = et_device_name.getText().toString().trim();

			if (TextUtils.isEmpty(deviceNewName))
			{
				ToastUtils.show(this, getString(R.string.new_device_name_null));
				return;
			}
			if (Utils.String_length(deviceNewName) < 2
					|| Utils.String_length(deviceNewName) > 10)
			{
				ToastUtils.show(this, getString(R.string.length_error3));
				return;
			}
			if (selectedItem != -1 && MainActivity.groupList != null
					&& MainActivity.groupList.size() > selectedItem)
			{
				newGroupId = MainActivity.groupList.get(selectedItem)
						.getGroupId() + "";
			}
			if (TextUtils.isEmpty(newGroupId))
			{
				// ToastUtils.show(this,
				// getString(R.string.new_device_id_null));
				// return;
				newGroupId = "-1";
			}
			startEditTask();
			break;

		default:
			break;
		}
	}

	private void startEditTask()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		editDeviceTask = new EditDeviceTask();
		editDeviceTask.execute();
	}

	private class EditDeviceTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			if (MyApplication.member != null)
			{
				if (type == 0)
				{
					TabSwtich.isNeedrefresh = true;
					responseBase = NetReq.editSwitch(deviceId + "", groupId
							+ "", newGroupId, deviceNewName,
							MyApplication.member.getSsuid(),
							MyApplication.member.getUsername(),
							MyApplication.member.getSessionId());
				}
				else if (type == 1)
				{
					TabSense.isNeedRefresh = true;
					responseBase = NetReq.editSensor(deviceId + "", groupId
							+ "", newGroupId, deviceNewName,
							MyApplication.member.getSsuid(),
							MyApplication.member.getUsername(),
							MyApplication.member.getSessionId());
				}
				else
				{
					TabCamera.isNeedRefresh = true;
					responseBase = NetReq.editCamera(deviceId + "", groupId
							+ "", newGroupId, deviceNewName,
							MyApplication.member.getSsuid(),
							MyApplication.member.getUsername(),
							MyApplication.member.getSessionId());
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
				 * 200：成功 300：系统异常 401：switchId不能为空 402：groupId不能为空
				 * 403：newGroupId不能为空 404：name不能为空 405：ssuid不能为空 406：用户名不能为空
				 * 407：sessionId不能为空 408：会话无效 409：开关已被删除 410：名称已存在
				 */
				LogUtil.LogI(Tag, "responseBase.getResponseStatus()="
						+ responseBase.getResponseStatus());
				if (responseBase.getResponseStatus() == 200)
				{
					// 注册成功 下一步
					ToastUtils.show(context,
							getString(R.string.edit_device_sucess));
					finish();
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
					if (type == 0)
					{
						TabSwtich.isNeedrefresh = true;
					}
					else if (type == 1)
					{
						TabSense.isNeedRefresh = true;
					}
					else if (type == 2)
					{
						TabCamera.isNeedRefresh = true;
					}
					// if (type == 0)
					// {
					// TabSwtich.isNeedrefresh = true;
					// }
					// else
					// {
					// TabSense.isNeedRefresh = true;
					// }
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(context,
							context.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					if (type == 0)
					{
						ToastUtils.show(context,
								context.getString(R.string.switch_id_null));
					}
					else if (type == 1)
					{
						ToastUtils.show(context,
								context.getString(R.string.sensor_id_null));
					}
					else
					{
						ToastUtils.show(context,
								context.getString(R.string.camera_id_null));
					}
					ToastUtils.show(context,
							context.getString(R.string.switch_id_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(context,
							context.getString(R.string.group_id_null));

				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(context,
							context.getString(R.string.new_group_id_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(context,
							context.getString(R.string.ssuid_null));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(context,
							context.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 407)
				{
					ToastUtils.show(context,
							context.getString(R.string.sessionId_null));
				}
				else if (responseBase.getResponseStatus() == 408)
				{
					ToastUtils.show(context,
							context.getString(R.string.sessionID_not_work));
				}
				else if (responseBase.getResponseStatus() == 409)
				{
					ToastUtils.show(context,
							context.getString(R.string.switch_had_delete));
				}
				else if (responseBase.getResponseStatus() == 410)
				{
					ToastUtils.show(context,
							context.getString(R.string.device_name_exist));
				}

			}
			else
			{
				// 网络请求失败
			}

		}
	}

}
