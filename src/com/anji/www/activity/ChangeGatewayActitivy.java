package com.anji.www.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.anji.www.R;
import com.anji.www.adapter.GatewayListAdapter;
import com.anji.www.constants.Url;
import com.anji.www.entry.GatewayResponse;
import com.anji.www.entry.Member;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 切换网关
 * 
 * @author Administrator
 */
public class ChangeGatewayActitivy extends BaseActivity implements
		OnClickListener
{

	public static final String TAG = "ChangeGatewayActitivy";
	private Button bt_back;
	private Button bt_right;
	private Button bt_add_mac;
	private TextView tv_title;
	private ListView lv_gateway;
	private List<GatewayResponse> gatewayList;
	private DeleteSsuidTask deleteSsuidTask;
	private Dialog progressDialog;
	private getAllSsuidTask getAllSsuidTask;
	private GatewayListAdapter gatewayListAdapter;
	private Context myContext;
	private int currentSetPosition;
	private int currentDeletePositon;
	private ChangeSsuidTask changeSsuidTask;
	private boolean isLogin;
	private Dialog deleteConfirmDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_net);
		myContext = this;
		isLogin = getIntent().getBooleanExtra("isLogin", false);
		initView();
		initDeleteConfirmDialog();
	}

	private void initView()
	{
		progressDialog = DisplayUtils.createDialog(this);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_right = (Button) findViewById(R.id.bt_right);
		bt_add_mac = (Button) findViewById(R.id.bt_add_mac);
		tv_title = (TextView) findViewById(R.id.tv_title);
		lv_gateway = (ListView) findViewById(R.id.lv_gateway);

		bt_right.setVisibility(View.VISIBLE);
		bt_right.setBackgroundResource(R.drawable.finish_button_selector);
		bt_right.setText("");
		tv_title.setText(getString(R.string.change_device));
		bt_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);
		bt_add_mac.setOnClickListener(this);

		gatewayListAdapter = new GatewayListAdapter(myContext, gatewayList);
		lv_gateway.setAdapter(gatewayListAdapter);

		lv_gateway.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				currentSetPosition = position;
				gatewayListAdapter.setCurrentPosition(currentSetPosition);
				// int count = parent.getAdapter().getCount();
				// if (count > 0)
				// {
				// for (int i = 0; i < count; i++)
				// {
				// CheckBox itemCheckBox = (CheckBox) parent.getAdapter()
				// .getView(i, null, null)
				// .findViewById(R.id.chk_selectone);
				// if (position == i)
				// {
				// itemCheckBox.setChecked(true);
				// }
				// else
				// {
				// itemCheckBox.setChecked(false);
				// }
				// }
				// }
			}
		});
		lv_gateway.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (gatewayList != null && gatewayList.size() > position)
				{
					currentDeletePositon = position;
					if (gatewayList.get(currentDeletePositon).isCurrGateway())
					{
						ToastUtils.show(myContext, getString(R.string.remove_gateway_fail));
					}
					else
					{
						if (deleteConfirmDialog != null
								&& !deleteConfirmDialog.isShowing())
						{
							deleteConfirmDialog.show();
						}
					}
				}
				return true;
			}
		});

	}

	/**
	 * 切换网络提示框
	 */
	private void initDeleteConfirmDialog()
	{
		deleteConfirmDialog = new Dialog(this, R.style.MyDialogStyle);
		deleteConfirmDialog.setContentView(R.layout.alert_hint_dialog);
		deleteConfirmDialog.setCancelable(false);

		Button bt_sure = (Button) deleteConfirmDialog
				.findViewById(R.id.bt_sure);
		Button bt_cancel = (Button) deleteConfirmDialog
				.findViewById(R.id.bt_cancel);
		TextView tv_info = (TextView) deleteConfirmDialog
				.findViewById(R.id.tv_info);
		tv_info.setText(getString(R.string.delete_gateway_hint));
		bt_cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				deleteConfirmDialog.dismiss();
			}
		});

		bt_sure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				deleteConfirmDialog.dismiss();
				startDeleteGateway(gatewayList.get(currentDeletePositon)
						.getSsuid());
			}

		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		startGetSsuid();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		Intent intent;
		switch (id)
		{
		case R.id.bt_back:
			// TODO 返回
			onBackPressed();
			break;
		case R.id.bt_right:
			// TODO 完成
			if (gatewayList != null
					&& gatewayList.size() >= currentSetPosition + 1)
			{
				if (!MyApplication.member.getSsuid().equals(
						gatewayList.get(currentSetPosition).getSsuid()))
				{
					startChangeGateway();
				}
				else
				{
					ToastUtils.show(this,
							getString(R.string.gateway_is_current));
				}
			}
			break;
		case R.id.bt_add_mac:
			// TODO 绑定新硬件
			// TODO 切换网关
			intent = new Intent(ChangeGatewayActitivy.this,
					RegisterThreeActivity.class);
			intent.putExtra("isChangeNet", true);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;

		default:
			break;
		}
	}

	/**
	 * 获取用户绑定的所有的网关
	 */
	private void startGetSsuid()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		getAllSsuidTask = new getAllSsuidTask();
		getAllSsuidTask.execute();
	}

	private class getAllSsuidTask extends AsyncTask<Object, Object, Void>
	{

		@Override
		protected Void doInBackground(Object... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			if (member != null)
			{
				gatewayList = NetReq.getAllSsiud(member.getMemberId());

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
			if (gatewayList != null)
			{
				LogUtil.LogI(TAG, "gatewayListl.size=" + gatewayList.size());
				for (int i = 0; i < gatewayList.size(); i++)
				{
					if (gatewayList.get(i).isCurrGateway())
					{
						currentSetPosition = i;
						gatewayListAdapter
								.setCurrentPosition(currentSetPosition);
					}
				}
				gatewayListAdapter.setList(gatewayList);
				gatewayListAdapter.notifyDataSetChanged();
			}
			else
			{
				// 网络请求失败
			}

		}
	}

	private void startChangeGateway()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		changeSsuidTask = new ChangeSsuidTask();
		changeSsuidTask.execute();
	}

	private class ChangeSsuidTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			if (gatewayList != null
					&& gatewayList.size() >= currentSetPosition + 1)
			{
				if (gatewayList.get(currentSetPosition) != null)
				{
					String ssuid = gatewayList.get(currentSetPosition)
							.getSsuid();

					if (member != null)
					{
						responseBase = NetReq.switchSsuid(member.getMemberId(),
								ssuid);
					}
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
				 * 2200：成功 300：系统异常 401：memberId不能为空 402：ssuid不能为空 403：会员不存在
				 * 404：网关未上报服务器
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					MainActivity.isNeedRefresh = true;
					MyApplication app = (MyApplication) getApplication();
					app.getMember().setSsuid(
							gatewayList.get(currentSetPosition).getSsuid());
					String json = Utils.load(ChangeGatewayActitivy.this);
					LogUtil.LogI(TAG, "json=" + json);

					try
					{
						if (!TextUtils.isEmpty(json))
						{
							// 把硬件地址加到保存的json数据中。
							JSONObject obj = new JSONObject(json);
							JSONObject temObj = obj.getJSONObject("member");
							temObj.put("ssuid",
									gatewayList.get(currentSetPosition)
											.getSsuid());
							String saveData = obj.toString();
							LogUtil.LogI(TAG, "saveData=" + saveData);
							Utils.saveData(saveData, ChangeGatewayActitivy.this);
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
					Intent intent = new Intent(ChangeGatewayActitivy.this,
							MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					ChangeGatewayActitivy.this.startActivity(intent);
					finish();
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.memberId_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.ssuid_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.member_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.ssiu_not_up));
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
		if (isLogin)
		{
			Intent intent = new Intent(ChangeGatewayActitivy.this,
					LoginActivity.class);
			startActivity(intent);
		}
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	private void startDeleteGateway(String ssuid)
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		deleteSsuidTask = new DeleteSsuidTask();
		deleteSsuidTask.execute(ssuid);
	}

	private class DeleteSsuidTask extends AsyncTask<String, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(String... params)
		{
			String ssuid = params[0];
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			if (member != null)
			{
				responseBase = NetReq
						.removeGateway(member.getUsername(), ssuid);
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
				 * 200：成功 300：系统异常 401：ssuid不能为空 402：username不能为空 403：会员不存在
				 * 404：网关未上报服务器
				 */
				if (responseBase.getResponseStatus() == 200)
				{

					gatewayList.remove(currentDeletePositon);
					gatewayListAdapter.notifyDataSetChanged();
					ToastUtils
							.show(myContext, myContext
									.getString(R.string.remove_gateway_sucess));
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.ssuid_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.member_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.ssiu_not_up));
				}
			}
			else
			{
				// 网络请求失败
			}
		}
	}

}
