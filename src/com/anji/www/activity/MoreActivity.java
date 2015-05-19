package com.anji.www.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.anji.www.R;
import com.anji.www.constants.Url;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.ResponseBase;
import com.anji.www.entry.Version;
import com.anji.www.network.NetReq;
import com.anji.www.service.UdpService;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.MyActivityManager;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ����������
 * 
 * @author Administrator
 */
public class MoreActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "MoreActivity";
	private Button bt_back;
	private Button bt_right;
	private TextView tv_title;
	private TextView tv_net_type;
	private TextView tv_net_address;
	private TextView tv_version;
	private RelativeLayout rl_change_net;
	private RelativeLayout rl_my_count;
	private RelativeLayout rl_check_version;
	private RelativeLayout rl_about_help;
	private RelativeLayout rl_exit;
	private Dialog exitDialog;
	private Dialog changeNetDialog;
	private UdpService myUdpService;
	private Dialog progressDialog;
	// ��ʱʱ��10�룬�ж������Ƿ����
	private long delayMillis = 10000;
	private Context context;
	private GetVersionInfoTask getVersionInfo;
	private final int DOWN_ERROR = 2;
	private ImageView img_update_new;
	private Version versionInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		MyActivityManager.Add(TAG, this);
		context = this;
		myUdpService = UdpService.newInstance(null);
		initView();
		initLogoutDialog();
		initChangeNetDialog();
		startGetVersionInfo(false);
	}

	private void initView()
	{
		img_update_new = (ImageView) findViewById(R.id.img_update_new);
		progressDialog = DisplayUtils.createDialog(this);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_right = (Button) findViewById(R.id.bt_right);
		tv_title = (TextView) findViewById(R.id.tv_title);
		rl_change_net = (RelativeLayout) findViewById(R.id.rl_change_net);
		rl_my_count = (RelativeLayout) findViewById(R.id.rl_my_count);
		rl_check_version = (RelativeLayout) findViewById(R.id.rl_check_version);
		rl_about_help = (RelativeLayout) findViewById(R.id.rl_about_help);
		rl_exit = (RelativeLayout) findViewById(R.id.rl_exit);
		tv_net_type = (TextView) findViewById(R.id.tv_net_type);
		tv_net_address = (TextView) findViewById(R.id.tv_net_address);
		tv_version = (TextView) findViewById(R.id.tv_version);
		
		if (MainActivity.isInNet)
		{
			tv_net_type.setText(getString(R.string.in_net));
			tv_net_address.setText(myUdpService.getDeviceIp());
			bt_right.setVisibility(View.VISIBLE);
		}
		else
		{
			tv_net_type.setText(getString(R.string.out_net));
			tv_net_address.setText(Url.webUrl2);
			bt_right.setVisibility(View.VISIBLE);
			// bt_right.setVisibility(View.GONE);
		}
		tv_title.setText(getString(R.string.more));

		bt_right.setText(getString(R.string.group_net));

		bt_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);
		rl_change_net.setOnClickListener(this);
		rl_my_count.setOnClickListener(this);
		rl_check_version.setOnClickListener(this);
		rl_about_help.setOnClickListener(this);
		rl_exit.setOnClickListener(this);
		tv_version.setText(getString(R.string.check_update)+"("+Utils.getAppVersion(MoreActivity.this)+")");
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		int id = v.getId();
		switch (id) {
		case R.id.bt_back:
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_right:
			// TODO ����
			if (MainActivity.isInNet) {
				sendF5Order();
			} else {
				startGroupNet();
			}

			break;
		case R.id.rl_change_net:
			// TODO �л�����
			if (changeNetDialog != null && !changeNetDialog.isShowing()) {
				changeNetDialog.show();
			}
			break;
		case R.id.rl_my_count:
			// TODO �ҵ��˺�
			intent = new Intent(MoreActivity.this, MyCountActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.rl_check_version:
			// TODO ������

			if (versionInfo == null) {
				startGetVersionInfo(true);
			} else {
				checkVersion();
			}
			break;
		case R.id.rl_about_help:
			// TODO ���ڰ���
			intent = new Intent(MoreActivity.this, AboutHelpActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.rl_exit:
			// TODO �˳�
			if (exitDialog != null && !exitDialog.isShowing()) {
				exitDialog.show();
			}
			break;

		default:
			break;
		}
	}

	private void checkVersion() {
		String currentVersion = Utils.getAppVersion(MoreActivity.this);

		if (!TextUtils.isEmpty(currentVersion)) {
			if (versionInfo.getVersionNo().equals(currentVersion)) {
				ToastUtils.show(context, getString(R.string.upgrade_isnew));
			} else {
				showUpgradeDialog(versionInfo.getPath(),
						getString(R.string.upgrade_hasNew));
			}
		}
	}

	/**
	 * �ٴη�����ʾ��
	 */
	private void initLogoutDialog() {
		exitDialog = new Dialog(this, R.style.MyDialogStyle);
		exitDialog.setContentView(R.layout.alert_hint_dialog);
		exitDialog.setCancelable(false);

		Button bt_sure = (Button) exitDialog.findViewById(R.id.bt_sure);
		Button bt_cancel = (Button) exitDialog.findViewById(R.id.bt_cancel);
		TextView tv_info = (TextView) exitDialog.findViewById(R.id.tv_info);
		tv_info.setText(getString(R.string.exit_hint));
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitDialog.dismiss();
			}
		});

		bt_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitDialog.dismiss();
				Intent intent = new Intent(MoreActivity.this,
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				MainActivity.isNeedRefresh = true;
				JPushInterface.setAlias(MoreActivity.this, "",
						new TagAliasCallback() {
							@Override
							public void gotResult(int arg0, String arg1,
									Set<String> arg2) {

								Log.i(TAG, "respone arg0 = " + arg0);
							}
						});
				// MyActivityManager.finish("MainActivity");
				finish();
				// overridePendingTransition(android.R.anim.slide_in_left,
				// android.R.anim.slide_out_right);
				new Thread() {
					public void run() {
						Utils.saveData("", MoreActivity.this);
					};
				}.start();
			}
		});
	}

	/**
	 * �л�������ʾ��
	 */
	private void initChangeNetDialog() {
		changeNetDialog = new Dialog(this, R.style.MyDialogStyle);
		changeNetDialog.setContentView(R.layout.alert_hint_dialog);
		changeNetDialog.setCancelable(false);

		Button bt_sure = (Button) changeNetDialog.findViewById(R.id.bt_sure);
		Button bt_cancel = (Button) changeNetDialog
				.findViewById(R.id.bt_cancel);
		TextView tv_info = (TextView) changeNetDialog
				.findViewById(R.id.tv_info);
		tv_info.setText(getString(R.string.change_net_hint));
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeNetDialog.dismiss();
			}
		});

		bt_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeNetDialog.dismiss();
				// TODO �л�����
				if (MainActivity.isInNet) {
					MainActivity.isInNet = false;
					tv_net_type.setText(getString(R.string.out_net));
					tv_net_address.setText(Url.webUrl2);
				} else {
					sendF5Order();
				}

			}

		});
	}

	private Runnable cancelDialog = new Runnable() {

		@Override
		public void run() {
			delayHandler.obtainMessage(1).sendToTarget();
			LogUtil.LogI(TAG, "isInNet=false cancelDialog");
			MainActivity.isInNet = false;
		}
	};

	private Handler delayHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				ToastUtils.show(context, getString(R.string.change_net_fail));
				tv_net_type.setText(getString(R.string.out_net));
				tv_net_address.setText(Url.webUrl2);
			}
		};
	};

	@Override
	protected void onDestroy() {
		MyActivityManager.finish(TAG);
		super.onDestroy();
	}

	private void sendF5Order() {
		if (myUdpService != null) {
			myUdpService.setMyHandler(myHandler);
			// F5ָ�� �����¿ͻ����������غͼ�������
			byte[] bytes = new byte[] { (byte) 0x20, (byte) 0xF5, (byte) 0x00,
					(byte) 0x01, (byte) 0x2, (byte) 0x01, (byte) 0x01 };
			myUdpService.sendBroadCastUdp(bytes);
			if (progressDialog != null && !progressDialog.isShowing()) {
				progressDialog.show();
				LogUtil.LogI(TAG, "progressDialog.show();");
				LogUtil.LogI(TAG, "postDelayed.cancelDialog();");
				delayHandler.postDelayed(cancelDialog, delayMillis);
			}

		}
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			byte[] arr = (byte[]) msg.obj;
			switch (msg.what) {
			case UdpService.ORDRE_SREACH_GATEWAY:
				// ���ͳɹ� ��������
				if (myUdpService.getDeviceMac().equals(
						MyApplication.member.getSsuid())) {
					progressDialog.dismiss();
					delayHandler.removeCallbacks(cancelDialog);
					// ��ʾ�������á�
					MainActivity.isInNet = true;
					// ToastUtils.show(context,
					// getString(R.string.change_net_sucess));
					tv_net_type.setText(getString(R.string.in_net));
					tv_net_address.setText(myUdpService.getDeviceIp());
					bt_right.setVisibility(View.VISIBLE);
					// qurryAll();
				}
				// else
				// {
				// // ����
				// LogUtil.LogI(TAG, "Main isInNet false ORDRE_SREACH_GATEWAY");
				// MainActivity.isInNet = false;
				// }

				break;
			case UdpService.ORDRE_CAN_JOIN:
				// F5���ͳɹ� �㲥���յ���ִ�гɹ� ������������������ָ��
				// byte[] bytes = new byte[]
				// { (byte) 0x20, (byte) 0xF0, (byte) 0x01, (byte) 0x9,
				// (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				// (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				// (byte) 0xff };
				myUdpService.sendOrders(Utils.hexStringToBytes("20F0000100901"
						+ MyApplication.member.getSsuid()));
				break;
			case UdpService.SEND_ORDER_FAIL:

			case UdpService.SEND_NULL_FAIL:
				// TODO ����ʧ��
				break;

			default:
				break;
			}
		};
	};
	private GroupNetTask groupNet;

	/**
	 * ɾ���豸
	 * 
	 * @param switchInfo
	 */
	public void startGroupNet() {
		if (progressDialog != null && !progressDialog.isShowing()) {
			progressDialog.show();
		}
		groupNet = new GroupNetTask();
		groupNet.execute();
	}

	private class GroupNetTask extends
			AsyncTask<DeviceInfo, Object, ResponseBase> {
		ResponseBase responseBase;

		@Override
		protected ResponseBase doInBackground(DeviceInfo... params) {

			if (MyApplication.member != null) {
				responseBase = NetReq.groupNet(MyApplication.member.getSsuid());
			}

			return null;
		}

		@Override
		protected void onPostExecute(ResponseBase result) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (responseBase != null) {
				/**
				 * 200���ɹ� 300��ϵͳ�쳣 401��ssuid��������Ϊ�� 402��������ssuid����
				 */
				if (responseBase.getResponseStatus() == 200) {
					// ���Ƴɹ�
					ToastUtils.show(context,
							getString(R.string.group_net_sucess));
				} else if (responseBase.getResponseStatus() == 201) {
					ToastUtils.show(context,
							context.getString(R.string.operation_failed));
				} else if (responseBase.getResponseStatus() == 300) {
					ToastUtils.show(context,
							context.getString(R.string.system_error));
				} else if (responseBase.getResponseStatus() == 401) {
					ToastUtils.show(context,
							context.getString(R.string.ssuid_null));
				} else if (responseBase.getResponseStatus() == 402) {
					ToastUtils.show(context,
							context.getString(R.string.ssiu_not_exist));
				}
			} else {
				// ��������ʧ��
			}
		}
	}

	/**
	 * �汾������ʾ��
	 * 
	 * @param upgradeUrl
	 * @param upgradeContent
	 */
	public void showUpgradeDialog(final String upgradeUrl, String upgradeContent) {
		new AlertDialog.Builder(this)
				.setTitle(this.getString(R.string.upgrade_Reminder))
				.setMessage(upgradeContent)
				.setNeutralButton(this.getString(R.string.upgrade_Now),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								downLoadApk(upgradeUrl);
								// Uri uri = Uri.parse(upgradeUrl);
								// Intent it = new Intent(Intent.ACTION_VIEW,
								// uri);
								// context.startActivity(it);
							}
						})
				.setNegativeButton(this.getString(R.string.upgrade_Later),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// ������Ժ���˵���󣬹�����������
							}
						}).show();
	}

	/*
	 * �ӷ�����������APK
	 */
	protected void downLoadApk(final String uri) {
		final ProgressDialog pd; // �������Ի���
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage(getString(R.string.upgrade_dowing));
		pd.setCancelable(true);// ���ý������Ƿ���԰��˻ؼ�ȡ��
		pd.setCanceledOnTouchOutside(false);// ���õ�����ȶԻ����������Ի�����ʧ
		pd.show();
		new Thread() {

			@Override
			public void run() {
				try {
					File file = getFileFromServer(uri, pd);
					sleep(3000);
					installApk(file);
					pd.dismiss(); // �������������Ի���
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = DOWN_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	// �ӷ���������apk:
	public static File getFileFromServer(String path, ProgressDialog pd)
			throws Exception {
		// �����ȵĻ���ʾ��ǰ��sdcard�������ֻ��ϲ����ǿ��õ�
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// ��ȡ���ļ��Ĵ�С
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(),
					"anjiupdata.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// ��ȡ��ǰ������
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			// case UPDATA_CLIENT:
			// // �Ի���֪ͨ�û���������
			// showUpdataDialog();
			// break;
			// case GET_UNDATAINFO_ERROR:
			// // ��������ʱ
			// showToast("��ȡ������������Ϣʧ��");
			// break;
			case DOWN_ERROR:
				// ����apkʧ��
				ToastUtils.show(MoreActivity.this,
						getString(R.string.upgrade_download_fail));
				break;
			// case UPDATA_NO:
			// // Logger.i("UPDATA_NO", "UPDATA_NO");
			// showAlertDialog("��ܰ��ʾ", "��ǰ�������°汾", null, null);
			// break;
			default:
				break;
			}
		}
	};

	// ��װapk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// ִ�ж���
		intent.setAction(Intent.ACTION_VIEW);
		// ִ�е���������
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");// ���߰����˴�AndroidӦΪandroid��������ɰ�װ����
		startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private void startGetVersionInfo(Boolean isShowDialog) {
		if (progressDialog != null && !progressDialog.isShowing()) {
			progressDialog.show();
		}
		getVersionInfo = new GetVersionInfoTask();
		getVersionInfo.execute(isShowDialog);
	}

	private class GetVersionInfoTask extends AsyncTask<Boolean, Object, Void> {
		private boolean isShowDialog;

		@Override
		protected Void doInBackground(Boolean... params) {
			isShowDialog = params[0];
			versionInfo = NetReq.getUpdateInfo();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (versionInfo != null) {
				LogUtil.LogI(
						TAG,
						"versionInfo.getVersionNo()=="
								+ versionInfo.getVersionNo());
				LogUtil.LogI(TAG,
						"��ǰ�汾==" + Utils.getAppVersion(MoreActivity.this));
				if (versionInfo.getVersionNo().equals(
						Utils.getAppVersion(MoreActivity.this))) {
					img_update_new.setVisibility(View.GONE);
				} else {
					img_update_new.setVisibility(View.VISIBLE);
				}

				if (isShowDialog) {
					checkVersion();
				}
			} else {
				// ��������ʧ��
			}
		}
	}

}
