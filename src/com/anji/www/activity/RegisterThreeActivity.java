package com.anji.www.activity;

import java.net.DatagramSocket;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anji.www.R;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.service.UdpService;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

/**
 * 注册页面，添加硬件
 * 
 * @author Ivan
 * @since 9,24
 */
public class RegisterThreeActivity extends BaseActivity implements
		OnClickListener
{
	private String TAG = "RegisterThreeActivity";
	// 返回
	private Button img_back;
	private TextView tv_title;
	// 注册完成
	private Button bt_right;
	// 扫描
	private Button bt_scan;
	// 自动添加
	private Button bt_auto_get;
	// 硬件地址
	private EditText et_mac_address;
	private DatagramSocket dSocket;
	// 端口号
	private int SERVER_PORT = 6000;

	private UdpService udpService;
	private String macAddress;
	private Context context;

	private Dialog progressDialog;
	private RegisterFourTask registerFourTask;
	private boolean isChangeNet;// 是否是切换网关，不是就是注册
	private Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			byte[] arr = (byte[]) msg.obj;
			switch (msg.what)
			{
			case UdpService.ORDRE_SREACH_GATEWAY:
				// 发送成功
				if (arr[arr.length - 1] == 0)
				{
					// 表示网关准备就绪
					byte[] macsAddress = new byte[8];
					for (int i = 6; i < 14; i++)
					{
						macsAddress[i - 6] = arr[i];
					}
					macAddress = Utils.bytesToHexString(macsAddress);
					LogUtil.LogI(TAG, "macAddress=" + macAddress);
					et_mac_address.setText(macAddress);
					et_mac_address.invalidate();
					// 发送成功 搜索网关

				}
				else
				{
					// 表示网关错误 错误码 arr[arr.length-1]
				}
				break;
			case UdpService.ORDRE_CAN_JOIN:
				// F5发送成功 广播已收到，执行成功 接下来发送搜索网关指令
				byte[] bytes = new byte[]
				{ (byte) 0x20, (byte) 0xF0, (byte) 0x00, (byte) 0x01,
						(byte) 0x9, (byte) 0x01, (byte) 0xff, (byte) 0xff,
						(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
						(byte) 0xff, (byte) 0xff };
				udpService.sendOrders(bytes);
				break;
			case UdpService.SEND_ORDER_FAIL:
				// TODO 发送失败
			case UdpService.SEND_NULL_FAIL:
				// TODO 发送失败
				ToastUtils.show(context,
						context.getString(R.string.order_send_fail));
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
		setContentView(R.layout.activity_register_three);
		context = this;
		isChangeNet = getIntent().getBooleanExtra("isChangeNet", false);
		findView();
		setLister();
	}

	private void setLister()
	{
		img_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);
		bt_scan.setOnClickListener(this);
		bt_auto_get.setOnClickListener(this);
	}

	private void findView()
	{
		img_back = (Button) findViewById(R.id.bt_back);
		bt_right = (Button) findViewById(R.id.bt_right);
		bt_scan = (Button) findViewById(R.id.bt_scan);
		bt_auto_get = (Button) findViewById(R.id.bt_auto_get);
		et_mac_address = (EditText) findViewById(R.id.et_mac_address);
		tv_title = (TextView) findViewById(R.id.tv_title);
		if (isChangeNet)
		{
			tv_title.setText(getString(R.string.bind_device));
		}
		else
		{
			tv_title.setText(getString(R.string.device_connect));
		}
		bt_right.setVisibility(View.VISIBLE);
		bt_right.setText("");
		bt_right.setBackgroundResource(R.drawable.finish_button_selector);
		progressDialog = DisplayUtils.createDialog(this);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		Intent intent;
		switch (id)
		{
		case R.id.bt_back:
			// 返回
			onBackPressed(); 
			break;
		case R.id.bt_scan:
			// 扫描
			break;
		case R.id.bt_right:
			// 注册完成
			LogUtil.LogI(TAG, "绑定新网关");
			macAddress = et_mac_address.getText().toString().trim();
			if (!TextUtils.isEmpty(macAddress))
			{
				if (macAddress.length() == 16)
				{
					LogUtil.LogI(TAG, "绑定新网关 硬件地址不为空");
					startRegisterFour();
				}
				else
				{
					ToastUtils.show(this,
							getString(R.string.mac_address_type_error));
				}

			}
			else
			{
				ToastUtils.show(this, getString(R.string.mac_address_null));
			}

			break;
		case R.id.bt_auto_get:
			// 自动获取本地网关
			udpService = UdpService.newInstance(myHandler);
			udpService.setMyHandler(myHandler);
			// F5指令 允许新客户端搜索网关和加入网关
			byte[] bytes = new byte[]
			{ (byte) 0x20, (byte) 0xF5, (byte) 0x00, (byte) 0x01, (byte) 0x2,
					(byte) 0x01, (byte) 0x01 };
			udpService.sendBroadCastUdp(bytes);
			break;
		default:
			break;
		}
	}

	private void startRegisterFour()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		registerFourTask = new RegisterFourTask();
		registerFourTask.execute();
	}

	private void cancelRegisterFour()
	{
		if (registerFourTask != null)
		{
			registerFourTask.cancel(true);
			registerFourTask = null;
		}
	}

	private class RegisterFourTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			LogUtil.LogI(TAG, "member是否为空="+(member != null));
			if (member != null)
			{
				responseBase = NetReq.registerFour(macAddress,
						member.getUsername(), member.getSessionId());
			}else {
				ToastUtils.show(context, getString(R.string.login_error));
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
				 * 200：成功 300：系统异常 401：ssuid不能为空 402：用户名不能为空 403：sessionID不能为空
				 * 404：会员不存在 405：无效的sessionID 406：设备未上报服务器
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 注册成功 下一步
					if (isChangeNet)
					{
						// 绑定网关
						onBackPressed();
					}
					else
					{
						// 注册第四不
						MyApplication app = (MyApplication) getApplication();
						app.getMember().setSsuid(macAddress);
						String json = Utils.load(RegisterThreeActivity.this);
						LogUtil.LogI(TAG, "json=" + json);
						MainActivity.isNeedRefresh = true;

						try
						{
							if (!TextUtils.isEmpty(json))
							{
								// 把硬件地址加到保存的json数据中。
								JSONObject obj = new JSONObject(json);
								JSONObject temObj = obj.getJSONObject("member");
								temObj.put("ssuid", macAddress);
								String saveData = obj.toString();
								LogUtil.LogI(TAG, "saveData=" + saveData);
								Utils.saveData(saveData,
										RegisterThreeActivity.this);
							}
						}
						catch (JSONException e)
						{
							e.printStackTrace();
						}
						Intent intent = new Intent(RegisterThreeActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
					}

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(context,
							context.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(context,
							context.getString(R.string.mac_address_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(context,
							context.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(context,
							context.getString(R.string.sessionID_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(context,
							context.getString(R.string.member_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(context,
							context.getString(R.string.sessionID_not_work));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(context,
							context.getString(R.string.device_not_up));
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
		if (isChangeNet)
		{
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		}
		else
		{
			Intent intent = new Intent(RegisterThreeActivity.this,
					RegisterTwoActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		}
	}
}
