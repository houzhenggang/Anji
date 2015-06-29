package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.camera.util.Global;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;
import com.fos.sdk.ConnectType;
import com.fos.sdk.FosDiscovery_Node;
import com.fos.sdk.FosSdkJNI;
import com.fos.sdk.IPCType;
import com.fos.sdk.StrData;
import com.fos.sdk.StreamType;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
	public static short PORT = 88;
	public int LOGIN_STATE = -3;
	private Integer mPermissionFlag = new Integer(-1);
	public static final int OPNE_VIDEO_SUC = 10;
	public static final int OPNE_VIDEO_FAILED = 11;
	public static final int LOGIN_FAILED = 15;
	private Dialog dialog;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case OPNE_VIDEO_FAILED:
				Toast.makeText(getApplicationContext(), "打开视频失败！", 500).show();
			case LOGIN_FAILED:
				Toast.makeText(getApplicationContext(),
						"login失败,错误码：" + msg.arg1, 500).show();
			default:
				break;
			}
		};
	};

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
		dialog = DisplayUtils.createDialog(this);
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
		switch (id) {
		case R.id.bt_back:
			onBackPressed();
			break;
		case R.id.bt_connect:
			// TODO 连接
			final String wifiPass = et_wifi_password.getText().toString()
					.trim();
			final String userName = et_username.getText().toString().trim();
			final String newPass = et_new_pass.getText().toString().trim();
			String newPassAgain = et_new_pass_again.getText().toString().trim();

			if (TextUtils.isEmpty(wifiPass)) {
				ToastUtils.show(this, getString(R.string.wifi_pass_null));
				return;
			}
			if (TextUtils.isEmpty(userName)) {
				ToastUtils.show(this, getString(R.string.name_null));
				return;
			}
			if (!newPass.equals(newPassAgain)) {
				ToastUtils.show(this, getString(R.string.pass_different));
				return;
			}

			// FosDiscovery_Node getnode = new FosDiscovery_Node();
			// LogUtil.LogI(Tag, "camera_uid=="+camera_uid);
			// LogUtil.LogI(Tag, "connectSSID=="+connectSSID);
			// LogUtil.LogI(Tag, "password=="+66812800);
			// int result = FosSdkJNI.StartEZlink(camera_uid, connectSSID,
			// wifiPass, getnode, 12000);
			// Log.i(Tag, "连接  result==" + result);
			// FosSdkJNI.StopEZlink();
			// if (result == 0) {
			// FosSdkJNI.Create(getnode.ip, getnode.uid, "admin", "1234",
			// (short) 88, (short) 88, 0, 0);
			// }
			if (dialog != null && !dialog.isShowing()) {
				dialog.show();
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.i("StartEZlink ret", "StartEZlink ret==");
					FosDiscovery_Node getNode = new FosDiscovery_Node();
					Log.i("StartEZlink ret", "StartEZlink UID==" + camera_uid);
					Log.i("StartEZlink ret", "StartEZlink SSID==" + connectSSID);
					Log.i("StartEZlink ret", "StartEZlink PSK==" + wifiPass);
					int ret = FosSdkJNI.StartEZlink(camera_uid, connectSSID,
							wifiPass, getNode, 12000); // 2min
					Log.i("StartEZlink ret", "StartEZlink ret==" + ret);
					if (ret != 0) {
						Log.i("jerry", "EZ link failed");
					}
					FosSdkJNI.StopEZlink();
					Log.i("StartEZlink ret",
							"StartEZlink IPCType.FOSIPC_H264=="
									+ IPCType.FOSIPC_H264);
					Log.i("StartEZlink ret",
							"StartEZlink IPCType.getNode.type==" + getNode.type);
					Log.i("StartEZlink ret", "StartEZlink camera_uid=="
							+ camera_uid);
					Log.i("StartEZlink ret", "StartEZlink newPass==" + newPass);
					Global.mHandlerNo = FosSdkJNI.Create(getNode.ip,
							camera_uid, userName, newPass, PORT, PORT,
							IPCType.FOSIPC_H264, ConnectType.FOSCNTYPE_IP);
					if (Global.mHandlerNo > 0) {

						// LOGIN_STATE = FosSdkJNI.Login(Global.mHandlerNo,
						// mPermissionFlag, 3000);
						// Log.i("jerry", "Login State == " + LOGIN_STATE
						// + "privileg == " + mPermissionFlag);
						//
						// switch (LOGIN_STATE) {
						// case 0:
						// StrData dname = new StrData();
						// FosSdkJNI
						// .GetDevName(Global.mHandlerNo, 2000, dname);
						// Log.i("jerry", "dname = " + dname.str);
						//
						// Global.OPEN_VIDEO_STATE = FosSdkJNI.OpenVideo(
						// Global.mHandlerNo,
						// StreamType.FOSSTREAM_SUB, 1000);
						// if (Global.OPEN_VIDEO_STATE == 0) {
						// Global.isReceiveData = true;
						// // mVideoSurfaceView.startDraw();
						// } else {
						// Message msg = mHandler
						// .obtainMessage(OPNE_VIDEO_FAILED);
						// mHandler.sendMessage(msg);
						// }
						// Log.i("jerry", "OPEN_VIDEO_STATE------->"
						// + Global.OPEN_VIDEO_STATE);
						// break;
						// default:
						// Message msg = mHandler.obtainMessage(LOGIN_FAILED);
						// msg.arg1 = LOGIN_STATE;
						// mHandler.sendMessage(msg);
						// break;
						// }

						// Intent it = new Intent(EzlinkSetInfoActivity.this,
						// CameraNew.class);
						//
						// Bundle bundle = new Bundle();
						// /* 字符、字符串、布尔、字节数组、浮点数等等，都可以传 */
						//
						// bundle.putString("ip", "");
						// bundle.putString("uid", camera_uid);
						// bundle.putString("uname", userName);
						// // bundle.putString("cameraId", ipcInfo.cameraId +
						// "");
						// bundle.putString("pwd", newPass);
						// bundle.putShort("port", getNode.port);
						// bundle.putInt("type", getNode.type);
						// /* 把bundle对象assign给Intent */
						// it.putExtras(bundle);
						// startActivity(it);

						myHandler.obtainMessage(1).sendToTarget();
					} else {
						Log.i("sdk", "create failed!");
						myHandler.obtainMessage(0).sendToTarget();

					}
				}
			}).start();
			break;

		default:
			break;
		}
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			switch (msg.what) {
			case 1:
				EzlinkSetInfoActivity.this.finish();
				break;
			case 0:
				ToastUtils.show(EzlinkSetInfoActivity.this,
						getString(R.string.set_fail));
				break;

			default:
				break;
			}
		};

	};

	protected void onDestroy() {
		// Global.isIPCconect = false;
		// FosSdkJNI.Logout(Global.mHandlerNo, 1000);
		// FosSdkJNI.Release(Global.mHandlerNo);
		super.onDestroy();
	};

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
}
