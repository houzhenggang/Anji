package com.anji.www.activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.anji.www.R;
import com.anji.www.help.IpService;
import com.anji.www.help.MyLog;
import com.anji.www.help.SetupData;
import com.anji.www.help.WifiHelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class TabControl extends Fragment implements OnClickListener{
	private Activity a;
	private SetupData setupData;
	private String serverIp;
	private DatagramSocket server = null;
	private boolean isStartServiceThread;
	protected static final int ShowIp = 12;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_control, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		this.a = activity;
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	
		initView();
		initService();

		findThread();// 发送查找服务器信息
	}



	private void serviceThread() {
		if (isStartServiceThread) {
			return;
		}
		isStartServiceThread = true;
		new Thread() {
			public void run() {
				MyLog.log("run");
				try {
					server = new DatagramSocket(5050);
					byte[] recvBuf = new byte[100];
					DatagramPacket recvPacket = new DatagramPacket(recvBuf,
							recvBuf.length);
					while (true) {

						server.receive(recvPacket);
						String recvStr = new String(recvPacket.getData(), 0,
								recvPacket.getLength());

						MyLog.log("recvStr:" + recvStr);
						if (WifiHelp.isIP(recvStr)) {
							Message msg = new Message();
							msg.what = ShowIp;
							msg.obj = recvStr;
							mHandler.sendMessage(msg);
							break;
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					MyLog.log("close server");
					if (server != null) {
						server.close();
					}
					isStartServiceThread = false;
				}

			};
		}.start();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ShowIp:
				String ip = (String) msg.obj;
				// ip_editText.setText(ip);
				serverIp = ip;
				MyLog.log("serverIp:" + serverIp);
				
				break;

			default:
				break;
			}
		};
	};

	private void findThread() {
		final String ip = IpService.getIp(a);
		if (ip == null) {
			Toast.makeText(a, "请连接网络", Toast.LENGTH_LONG).show();
			return;
		}

		serviceThread();// 服务器线程 用来接受 服务器返回的 服务器ip地址
		new Thread() {
			@Override
			public void run() {
				MyLog.log("查询电视");
				String s = ip.substring(0, ip.lastIndexOf(".") + 1);
				for (int i = 0; i < 255; i++) {
					// MyLog.log("i:"+i);
					sendData(s + i, "0");
				}
				super.run();
			}
		}.start();

	}

	private void initService() {
		setupData = SetupData.getSetupData(a);
		// ip_editText.setText(setupData.read(SetupData.Ip));
		// serverIp=setupData.read(SetupData.Ip);
	}

	@Override
	public void onDestroy() {
		// setupData.save(SetupData.Ip,
		// ip_editText.getText().toString());serverIp
		setupData.save(SetupData.Ip, serverIp);
		if (server != null) {
			server.close();
		}
		super.onDestroy();
	}

	private void initView() {
		// ip_editText = (TextView) findViewById(R.id.ip_editText);
		a.findViewById(R.id.add_button).setOnClickListener(this);
		a.findViewById(R.id.cut_button6).setOnClickListener(this);
		a.findViewById(R.id.left_button).setOnClickListener(this);
		a.findViewById(R.id.right_button).setOnClickListener(this);
		a.findViewById(R.id.top_button).setOnClickListener(this);
		a.findViewById(R.id.bottom_button).setOnClickListener(this);
		a.findViewById(R.id.confrim_button).setOnClickListener(this);
		a.findViewById(R.id.back_button).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_button:
			confirm(1);
		//	 sendData("192.168.117.94", "0");
			break;
		case R.id.cut_button6:
			confirm(2);
			break;
		case R.id.top_button:
			confirm(3);
			break;
		case R.id.bottom_button:
			confirm(4);
			break;
		case R.id.left_button:
			confirm(5);
			break;
		case R.id.right_button:
			confirm(6);
			break;

		case R.id.confrim_button:
			confirm(7);
			break;
		case R.id.back_button:
			confirm(8);
			break;

		default:
			break;
		}

	}

	// private static String Ip = "192.168.117.167";
	// private static int port = 9876;

	// private static String Ip="127.0.0.1";
	private static int port = 5050;

	private void confirm(final int i) {
		final String ip = serverIp;
		if (ip == null || ip.equals("")) {
			// Toast.makeText(this, "未找到智能电视", Toast.LENGTH_LONG).show();
			new AlertDialog.Builder(a)
					.setTitle("未找到智能电视")
					.setMessage("是否再次搜索电视?")
					.setPositiveButton(
							"确定",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									findThread();

									dialog.dismiss();
								}
							})
					.setNegativeButton(
							"取消",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
			return;
		}

		new Thread() {
			@Override
			public void run() {
				String sendStr = "" + i;
				sendData(ip, sendStr);
				super.run();
			}

		}.start();

	}

	private void sendData(String ip, String sendStr) {
		try {

			DatagramSocket client = new DatagramSocket();
			byte[] sendBuf;
			sendBuf = sendStr.getBytes();
			InetAddress addr = InetAddress.getByName(ip);
			DatagramPacket sendPacket = new DatagramPacket(sendBuf,
					sendBuf.length, addr, port);

			client.send(sendPacket);
			// MyLog.log("send:"+ip+","+sendStr);
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MyLog.log("confrim", e);
		}
	}
}
