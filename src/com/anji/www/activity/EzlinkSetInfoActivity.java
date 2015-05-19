package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.fos.sdk.FosDiscovery_Node;
import com.fos.sdk.FosSdkJNI;
import com.zxing.activity.CaptureActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EzlinkSetInfoActivity extends BaseActivity implements
		OnClickListener {

	private Button bt_back;
	private TextView tv_title;
	private TextView tv_uid;
	private TextView tv_wifi_ssid;
	private EditText et_wifi_password;
	private EditText et_username;
	private EditText et_old_pass;
	private EditText et_new_pass;
	private EditText et_new_pass_again;
	private Button bt_connect;
	private String camera_uid;
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private String connectSSID;// 记录已连接连接的ssid
	private String Tag = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ezlink_set);
		camera_uid = getIntent().getStringExtra("camera_uid");
		mWifiManager = (WifiManager) EzlinkSetInfoActivity.this
				.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
		connectSSID = mWifiInfo.getSSID();
		if (!TextUtils.isEmpty(connectSSID)) {
			connectSSID = connectSSID.replaceAll("\"", "");
		}
		initView();
	}

	private void initView() {
		bt_back = (Button) findViewById(R.id.bt_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.add_camera));
		tv_uid = (TextView) findViewById(R.id.tv_uid);
		tv_wifi_ssid = (TextView) findViewById(R.id.tv_wifi_ssid);
		tv_wifi_ssid.setText(connectSSID);
		tv_uid.setText(camera_uid);
		et_wifi_password = (EditText) findViewById(R.id.et_wifi_password);
		et_username = (EditText) findViewById(R.id.et_username);
		et_old_pass = (EditText) findViewById(R.id.et_old_pass);
		et_new_pass = (EditText) findViewById(R.id.et_new_pass);
		et_new_pass_again = (EditText) findViewById(R.id.et_new_pass_again);
		bt_connect = (Button) findViewById(R.id.bt_connect);

		bt_back.setOnClickListener(this);
		bt_connect.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent;
		switch (id) {
		case R.id.bt_back:
			onBackPressed();
			break;
		case R.id.bt_connect:
			// TODO 连接
			String wifiPass = et_wifi_password.getText().toString().trim();
			if (TextUtils.isEmpty(wifiPass)) {
				ToastUtils.show(this, getString(R.string.wifi_pass_null));
				return ;
			}
			FosDiscovery_Node getnode = new FosDiscovery_Node();
			LogUtil.LogI(Tag, "camera_uid=="+camera_uid);
			LogUtil.LogI(Tag, "connectSSID=="+connectSSID);
			LogUtil.LogI(Tag, "password=="+66812800);
			int result = FosSdkJNI.StartEZlink(camera_uid, connectSSID,
					wifiPass, getnode, 18000);
			Log.i(Tag, "连接  result==" + result);
			FosSdkJNI.StopEZlink();
			if (result == 0) {
				FosSdkJNI.Create(getnode.ip, getnode.uid, "admin", "1234",
						(short) 88, (short) 88, 0, 0);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
}
