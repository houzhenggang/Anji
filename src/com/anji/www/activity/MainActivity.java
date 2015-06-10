package com.anji.www.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.service.PushService;

import com.anji.www.R;
import com.anji.www.activity.MenuFragment.MenuEvent;
import com.anji.www.adapter.FragmentTabAdapter;
import com.anji.www.db.DatabaseHelper;
import com.anji.www.db.service.AnjiDBservice;
import com.anji.www.db.service.AnjiGroupService;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.GroupInfo;
import com.anji.www.entry.Member;
import com.anji.www.entry.SceneInfo;
import com.anji.www.network.NetReq;
import com.anji.www.service.UdpService;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.MyActivityManager;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;
import com.decoder.util.ExampleUtil;
import com.fos.sdk.FosSdkJNI;
import com.ipc.sdk.FSApi;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.remote.util.IPCameraInfo;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 主控制页面
 * 
 * @author Administrator
 */
public class MainActivity extends SlidingFragmentActivity implements MenuEvent
{
	public List<Fragment> fragments;
	private RadioGroup rgs;
	private static final String TAG = "MainActivity";
	private static final int UPDATE = 100;
//	private RadioButton tab_rb_main;
//	private RadioButton tab_rb_bluetooth;
//	private RadioButton tab_rb_set;
//	private RadioButton tab_rb_more;
	// private Dialog alertRestDialog;
	// 连接成功或是失败的广播
	public static int currentIndex;
	private int sysVersion = Integer.parseInt(VERSION.SDK);
	private TabScene tabScene;
	private TabMain tabMain;
	private TabSwtich tabSwtich;
	private TabSense tabSense;
	private TabCamera tabCamera;
	private static UdpService myUdpService;
	private Dialog progressDialog;
	public static boolean isNeedRefresh;

	public boolean isFirstStart;
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.anji.www.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public Dialog getProgressDialog()
	{
		return progressDialog;
	}

	public void setProgressDialog(Dialog progressDialog)
	{
		this.progressDialog = progressDialog;
	}

	private static final long delayMillis = 5000;
	private MainActivity context;
	private Member member;
	public static boolean isInNet = false;// 是否是内网，默认为外网

	public static List<DeviceInfo> allDeviceList = new ArrayList<DeviceInfo>();// 所有开关列表
	public static List<DeviceInfo> switchList = new ArrayList<DeviceInfo>();// 所有开关列表
	public static List<DeviceInfo> sensorList = new ArrayList<DeviceInfo>();// 所有传感列表
	public static List<GroupInfo> groupList = new ArrayList<GroupInfo>();// 所有分组列表
	public static List<IPCameraInfo> cameraList = new ArrayList<IPCameraInfo>();// 所有摄像头列表
	public static List<SceneInfo> sceneList = new ArrayList<SceneInfo>();// 所有情景列表
	public static List<DeviceInfo> sceneSwitchList = new ArrayList<DeviceInfo>();// 所有情景开关列表
	public static List<DeviceInfo> sceneSensorList = new ArrayList<DeviceInfo>();// 所有情景传感列表
	private List<IPCameraInfo> saveCameraList;// 保存的摄像头的列表
	private SwitchTask switchTask;
	private SensorTask sensorTask;
	private CameraTask cameraTask;
	private GroupTask groupTask;
	private SceneTask sceneTask;
	private SceneSwitchTask sceneSwitchTask;
	private SceneSensorTask sceneSensorTask;
	public static boolean isForeground = false;
	private AnjiGroupService groupService;
	private DatabaseHelper dbHelp;
	private AnjiDBservice dbService;
	private Dialog exitDialog;
	// private SwitchTask registerTask;
	// private RegisterOneTask registerTask;
	// private RegisterOneTask registerTask;
	private Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			byte[] arr = (byte[]) msg.obj;
			switch (msg.what)
			{
			case UdpService.ORDRE_SREACH_GATEWAY:
				// 发送成功 搜索网关
				if (myUdpService.getDeviceMac().equals(member.getSsuid()))
				{
					delayHandler.removeCallbacks(cancelDialog);
					// 表示内网可用。
					isInNet = true;
					// qurryAll();
				}
				else
				{
					// 外网
					LogUtil.LogI(TAG, "Main isInNet false ORDRE_SREACH_GATEWAY");
					isInNet = false;
				}

				break;
			case UdpService.ORDRE_CAN_JOIN:
				// F5发送成功 广播已收到，执行成功 接下来发送搜索网关指令
				// byte[] bytes = new byte[]
				// { (byte) 0x20, (byte) 0xF0, (byte) 0x01, (byte) 0x9,
				// (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				// (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				// (byte) 0xff };
				myUdpService.sendOrders(Utils.hexStringToBytes("20F0000100901"
						+ member.getSsuid()));
				break;
			case UdpService.SEND_ORDER_FAIL:

			case UdpService.SEND_NULL_FAIL:
				// TODO 发送失败
				break;
			case UdpService.ORDRE_ONE_CONTROL:
				// TODO 单设备控制
				// F2是控制指令 最后一位表示 aa离线 bb无此设备 cc命令发送成功 dd命令发送失败
				switch (arr[17])
				{
				case (byte) 0xaa:
					ToastUtils.show(MainActivity.this,
							context.getString(R.string.device_out_line));
					break;
				case (byte) 0xbb:
					ToastUtils.show(MainActivity.this,
							context.getString(R.string.device_none));
					break;
				case (byte) 0xcc:
					ToastUtils.show(MainActivity.this,
							context.getString(R.string.order_send_sucess));
					// qurryOnlyAll();
					break;
				case (byte) 0xdd:
					ToastUtils.show(MainActivity.this,
							context.getString(R.string.order_send_fail));
					break;

				default:
					break;
				}
				break;
			case UdpService.ORDRE_SREACH_DEVICE:
				// TODO 搜索到设备
				LogUtil.LogI(TAG, "搜索到设备");
				progressDialog.dismiss();
				tabMain.refreshView();
				tabSwtich.refreshView();
				tabSense.refreshView();
				tabCamera.refreshView();
				break;
			case UdpService.ORDRE_ONE_READ:
				// TODO 搜索到设备
				LogUtil.LogI(TAG, "获取到设备信息");
				tabMain.refreshView();
				tabSwtich.refreshView();
				tabSense.refreshView();
				tabCamera.refreshView();
				break;

			case UPDATE:
				// TODO 搜索到设备
				LogUtil.LogI(TAG, "刷新数据");
				tabMain.refreshView();
				tabSwtich.refreshView();
				tabSense.refreshView();
				tabCamera.refreshView();
				break;

			default:
				break;
			}
		};
	};

	// private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	// {
	// @SuppressLint("NewApi")
	// @Override
	// public void onReceive(Context context, Intent intent)
	// {
	// }
	// };

	public class MessageReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction()))
			{
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
				if (!ExampleUtil.isEmpty(extras))
				{
					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
				}
				// setCostomMsg(showMsg.toString());
				LogUtil.LogI(TAG, "用户点击了MESSAGE_RECEIVED_ACTION");
				Intent intent2 = new Intent(MainActivity.this,
						MainActivity.class);
				MainActivity.this.startActivity(intent2);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		isFirstStart = true;
		// FSApi.Init();
		MyActivityManager.Add(TAG, this);
		fragments = new ArrayList<Fragment>();
		myUdpService = UdpService.newInstance(myHandler);
		context = this;
		dbService = new AnjiDBservice(context);
		MyApplication app = (MyApplication) getApplication();
		groupService = new AnjiGroupService(context);
		dbHelp = new DatabaseHelper(context);
		member = app.getMember();
		if (member == null)
		{
			ToastUtils.show(this, getString(R.string.date_error));
		}
		else
		{
			// myUdpService.setDeviceMac(member.getSsuid());
		}
		if (isInNet)
		{
			// F5指令 允许新客户端搜索网关和加入网关
			byte[] bytes = new byte[]
			{ (byte) 0x20, (byte) 0xF5, (byte) 0x00, (byte) 0x01, (byte) 0x2,
					(byte) 0x01, (byte) 0x01 };
			myUdpService.sendBroadCastUdp(bytes);

		}
		
		initView();
		initSlidingMenu();
		initAlertDialog();
		initData();
		registerMessageReceiver();
		initLogoutDialog();
	}

	/**
	 * 获取数据的数据
	 */
	private void initData()
	{
		new Thread()
		{
			public void run()
			{
				allDeviceList = dbService.getAllDeviceData(context);
				groupList = groupService.getAlDeviceData(context);
				saveCameraList = dbHelp.qurryAll(context);
				// LogUtil.LogI(TAG, "allDeviceList.size=" +
				// allDeviceList.size());
				// LogUtil.LogI(TAG, "groupList.size=" + groupList.size());
				// LogUtil.LogI(TAG,
				// "saveCameraList.size=" + saveCameraList.size());
				switchList.clear();
				sensorList.clear();
				LogUtil.LogI(TAG, "allDeviceList.size=" + allDeviceList.size());
				if (allDeviceList != null && allDeviceList.size() > 0)
				{

					for (int i = 0; i < allDeviceList.size(); i++)
					{
						DeviceInfo info = allDeviceList.get(i);
						LogUtil.LogI(TAG, "info = " + info.hashCode());
						LogUtil.LogI(TAG,
								"info.getDeviceId() = " + info.getDeviceId());
						LogUtil.LogI(TAG,
								"info.getMemberId() = " + info.getMemberId());
						LogUtil.LogI(TAG,
								"info.getDeviceMac() = " + info.getDeviceMac());
						LogUtil.LogI(
								TAG,
								"info.getDeviceChannel() = "
										+ info.getDeviceChannel());
						LogUtil.LogI(TAG,
								"info.getSsuid() = " + info.getSsuid());

						if (info != null)
						{
							if (info.getType() == 0)
							{
								// 0为开关，1为传感
								switchList.add(info);
							}
							else
							{
								sensorList.add(info);
							}
						}
					}
				}
				cameraList.clear();
				cameraList.addAll(saveCameraList);
				for (int i = 0; i < saveCameraList.size(); i++)
				{
					LogUtil.LogI(TAG, "saveCameraList devName i=" + i
							+ saveCameraList.get(i).devName);
					LogUtil.LogI(TAG, "saveCameraList uid i=" + i
							+ saveCameraList.get(i).uid);
					LogUtil.LogI(TAG, "saveCameraList groupId i=" + i
							+ saveCameraList.get(i).groupId);
					LogUtil.LogI(TAG, "saveCameraList username i=" + i
							+ saveCameraList.get(i).userName);
					LogUtil.LogI(TAG, "saveCameraList password i=" + i
							+ saveCameraList.get(i).password);
				}
				myHandler.obtainMessage(UPDATE).sendToTarget();
			};
		}.start();
		startQurryScene();
		startQurrySensor();
		startQurrySceneSwitch();
		startQurrySceneSensor();
		startQurrySwitch();
		startQurryCamera();
		startQurryGroup();
	}

	public void qurryAllSwtich()
	{
		if (isInNet)
		{

			qurryInNet();
		}
		else
		{
			// 外网
			if (progressDialog != null && !progressDialog.isShowing())
			{
				progressDialog.show();
			}
			startQurrySwitch();
		}
	}

	public void qurryInNet()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
			delayHandler.postDelayed(cancelDialog, delayMillis);
		}
		LogUtil.LogI(TAG, "qurry=" + "20F10001010B" + member.getSsuid()
				+ "01FFFF");
		myUdpService.sendOrders(Utils.hexStringToBytes("20F100010B"
				+ member.getSsuid() + "01FFFF"));
	}

	public static void qurryOnlyAll()
	{
		myUdpService.sendOrders(Utils.hexStringToBytes("20F100010B"
				+ myUdpService.getDeviceMac() + "01FFFF"));

	}

	private Handler delayHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == 1)
			{
				if (progressDialog != null && progressDialog.isShowing())
				{
					progressDialog.dismiss();
				}
			}
		};
	};

	private Runnable cancelDialog = new Runnable()
	{

		@Override
		public void run()
		{
			delayHandler.obtainMessage(1).sendToTarget();
			LogUtil.LogI(TAG, "isInNet=false cancelDialog");
			// isInNet = false;
		}
	};
	
	
	private SlidingMenu mSlidingMenu;
	private MenuFragment mMenuFrag;
	
	private void addFragment(Fragment fragment, int resId, String tag) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(resId, fragment, tag);
		transaction.commit();
	}

	// 初始化左侧菜单
	private void initSlidingMenu() {
		mSlidingMenu = getSlidingMenu();
		
		setBehindContentView(R.layout.main_left_layout);// 设置左边的菜单布局
		
		
		mMenuFrag = new MenuFragment( this, R.id.content_frame, this );
		mMenuFrag.setFragments(fragments);
		
		addFragment(mMenuFrag, R.id.main_left_fragment, "menu");
		addFragment(tabScene, R.id.content_frame, "home");

		// customize the SlidingMenu
//		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//		mSlidingMenu.setShadowWidthRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeEnabled(false);
		mSlidingMenu.setFadeDegree(0.25f);
		mSlidingMenu.setMode(SlidingMenu.LEFT);
//		mSlidingMenu.setBackgroundImage(R.drawable.slidingmenu_bg);
		mSlidingMenu.setBackgroundColor( getResources().getColor(R.color.white ) );
		
//		mSlidingMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
//			@Override
//			public void transformCanvas(Canvas canvas, float percentOpen) {
//				float scale = (float) (percentOpen * 0.25 + 0.75);
//				canvas.scale(scale, scale, -canvas.getWidth() / 2,
//						canvas.getHeight() / 2);
//			}
//		});
//
//		mSlidingMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
//			@Override
//			public void transformCanvas(Canvas canvas, float percentOpen) {
//				float scale = (float) (1 - percentOpen * 0.25);
//				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
//			}
//		});
	}


	private void initView()
	{
//		tab_rb_main = (RadioButton) findViewById(R.id.tab_rb_main);
//		tab_rb_bluetooth = (RadioButton) findViewById(R.id.tab_rb_bluetooth);
//		tab_rb_set = (RadioButton) findViewById(R.id.tab_rb_set);
//		tab_rb_more = (RadioButton) findViewById(R.id.tab_rb_more);
		tabScene = new TabScene();
		tabMain = new TabMain();
		tabSwtich = new TabSwtich();
		tabSense = new TabSense();
		tabCamera = new TabCamera();
		fragments.add( tabScene );
		fragments.add(tabMain);
		fragments.add(tabSwtich);
		fragments.add(tabSense);
		fragments.add(tabCamera);
		
//		tab_rb_main.setTextColor(getResources().getColor(
//				R.color.title_background));
//		rgs = (RadioGroup) findViewById(R.id.tabs_rg);
//		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments,
//				R.id.tab_content, rgs);
//		tabAdapter
//				.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener()
//				{
//					@Override
//					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
//							int checkedId, int index)
//					{
//						System.out.println("Extra---- " + index
//								+ " checked!!! ");
//						currentIndex = index;
////						updataTextColor(index);
//
//					}
//				});
		// IntentFilter filter = new IntentFilter(CONNECTED_SUCESS);
		// registerReceiver(mReceiver, filter);
		// filter = new IntentFilter(CONNECTED_FAIL);
		// registerReceiver(mReceiver, filter);
		progressDialog = DisplayUtils.createDialog(this, R.string.search);
		progressDialog.setCancelable(true);
	}

//	private void updataTextColor(int index)
//	{
//		tab_rb_main.setTextColor(getResources().getColor(R.color.regiest_text));
//		tab_rb_bluetooth.setTextColor(getResources().getColor(
//				R.color.regiest_text));
//		tab_rb_set.setTextColor(getResources().getColor(R.color.regiest_text));
//		tab_rb_more.setTextColor(getResources().getColor(R.color.regiest_text));
//		switch (index)
//		{
//		case 0:
//			tab_rb_main.setTextColor(getResources().getColor(
//					R.color.title_background));
//			currentIndex = 0;
//			break;
//		case 1:
//			tab_rb_bluetooth.setTextColor(getResources().getColor(
//					R.color.title_background));
//			currentIndex = 1;
//			break;
//		case 2:
//			tab_rb_set.setTextColor(getResources().getColor(
//					R.color.title_background));
//			currentIndex = 2;
//			break;
//		case 3:
//			tab_rb_more.setTextColor(getResources().getColor(
//					R.color.title_background));
//			currentIndex = 3;
//			break;
//
//		default:
//			break;
//		}
//	}

	@Override
	protected void onResume()
	{
		super.onResume();
		isForeground = true;
		UdpService.newInstance(myHandler).setMyHandler(myHandler);
		if (isNeedRefresh)
		{
			initData();
			tabScene.refreshView();
			tabMain.refreshView();
			tabSwtich.refreshView();
			tabSense.refreshView();
			tabCamera.refreshView();
			isNeedRefresh = false;
		}
		if (!isFirstStart)
		{
			startQurryScene();
		}
		isFirstStart = false;
		// qurryAll();
	}

	@Override
	protected void onPause()
	{
		isForeground = true;
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		LogUtil.LogI(TAG, "onDestroy");
		// unregisterReceiver(mReceiver);
		if (groupService != null)
		{
			groupService.closeDBService();
		}
		if (null != dbHelp)
		{
			dbHelp.close();
		}
		if (dbService != null)
		{
			dbService.closeDB2();
		}
		unregisterReceiver(mMessageReceiver);
		// FSApi.Uninit();
		FosSdkJNI.DeInit();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

	@Override
	public void onBackPressed()
	{
		// super.onBackPressed();
		if (exitDialog != null && !exitDialog.isShowing())
		{
			exitDialog.show();
		}
		// System.exit(0);
		// alertRestDialog.show();
	}

	private void initAlertDialog()
	{
		// alertRestDialog = new Dialog(this, R.style.MyDialogStyle);
		// alertRestDialog.setContentView(R.layout.alert_dialog);
		// Button bt_cancel = (Button) alertRestDialog
		// .findViewById(R.id.bt_cancel);
		// Button bt_sure = (Button) alertRestDialog.findViewById(R.id.bt_sure);
		// TextView tv_title = (TextView) alertRestDialog
		// .findViewById(R.id.tv_title);
		// TextView tv_info = (TextView) alertRestDialog
		// .findViewById(R.id.tv_info);
		// tv_title.setText(getString(R.string.hint));
		// tv_info.setText(getString(R.string.exit_hint));
		// bt_sure.setText(getString(R.string.sure));
		// bt_cancel.setOnClickListener(new OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v)
		// {
		// alertRestDialog.dismiss();
		// }
		// });
		//
		// bt_sure.setOnClickListener(new OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v)
		// {
		// alertRestDialog.dismiss();
		// finish();
		// }
		// });
	}

	/**
	 * 再次发生提示框
	 */
	private void initLogoutDialog()
	{
		exitDialog = new Dialog(this, R.style.MyDialogStyle);
		exitDialog.setContentView(R.layout.alert_hint_dialog);
		exitDialog.setCancelable(false);

		Button bt_sure = (Button) exitDialog.findViewById(R.id.bt_sure);
		Button bt_cancel = (Button) exitDialog.findViewById(R.id.bt_cancel);
		TextView tv_info = (TextView) exitDialog.findViewById(R.id.tv_info);
		tv_info.setText(getString(R.string.exit_hint2));
		bt_cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				exitDialog.dismiss();
			}
		});

		bt_sure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				exitDialog.dismiss();
				finish();
			}
		});
	}

	/**
	 * 以下的几个方法用来，让fragment能够监听touch事件
	 */
	private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>(
			10);

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev)
	// {
	// // for (MyOnTouchListener listener : onTouchListeners)
	// // {
	// // listener.onTouch(ev);
	// // }
	// return super.dispatchTouchEvent(ev);
	// }

	public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener)
	{
		onTouchListeners.add(myOnTouchListener);
	}

	public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener)
	{
		onTouchListeners.remove(myOnTouchListener);
	}

	public interface MyOnTouchListener
	{
		public boolean onTouch(MotionEvent ev);
	}

	public void startQurrySwitch()
	{
		// if (progressDialog != null && !progressDialog.isShowing())
		// {
		// progressDialog.show();
		// }
		switchTask = new SwitchTask();
		switchTask.execute();
	}
	
	public void cancelQurrySwitch()
	{
		if (switchTask != null)
		{
			switchTask.cancel(true);
			switchTask = null;
		}
	}

	private class SwitchTask extends AsyncTask<Object, Object, Void>
	{
		private List<DeviceInfo> switchTempList;

		@Override
		protected Void doInBackground(Object... params)
		{
			if (member != null)
			{
				switchTempList = NetReq.qurryAllSwitch(member.getMemberId(),
						member.getSsuid());
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
			if (switchTempList != null && switchTempList.size() > 0)
			{
				switchList = switchTempList;
				myaHandler2.obtainMessage(2).sendToTarget();
				// dbService = new AnjiDBservice(context);
				// dbService.updateDeviceData(switchList);
				// tabMain.refreshView();
				// tabSwtich.refreshView();
				// tabSwtich.refreshView();
				// tabCamera.refreshView();
			}

		}
	}

	public void startQurrySensor()
	{
		// if (progressDialog != null && !progressDialog.isShowing())
		// {
		// progressDialog.show();
		// }
		sensorTask = new SensorTask();
		sensorTask.execute();
	}
	
	public void cancelQurrySensor()
	{
		if (sensorTask != null)
		{
			sensorTask.cancel(true);
			sensorTask = null;
		}
	}


	public void startQurrySensorWithDialog()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		sensorTask = new SensorTask();
		sensorTask.execute();
	}

	private class SensorTask extends AsyncTask<Object, Object, Void>
	{
		private List<DeviceInfo> sensorTempList;

		@Override
		protected Void doInBackground(Object... params)
		{
			if (member != null)
			{
				sensorTempList = NetReq.qurryAllSence(member.getMemberId(),
						member.getSsuid());
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
			if (sensorTempList != null && sensorTempList.size() > 0)
			{

				sensorList = sensorTempList;
				myaHandler2.obtainMessage(1).sendToTarget();

			}

		}
	}

	private Handler myaHandler2 = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			LogUtil.LogI(TAG, "myaHandler msg.what=" + msg.what);
			switch (msg.what)
			{
			case 1:
				LogUtil.LogI(TAG,
						"myaHandler2 111 dbService=" + dbService.hashCode());
				dbService.updateDeviceData(sensorList);
				tabMain.refreshView();
				// tabSwtich.refreshView();
				tabSense.refreshView();
				tabScene.refreshView();
				// tabCamera.refreshView();
				break;
			case 2:
				dbService.updateDeviceData(switchList);
				LogUtil.LogI(TAG,
						"myaHandler2 222 dbService=" + dbService.hashCode());
				// tabMain.refreshView();
				// tabSwtich.refreshView();
				tabSwtich.refreshView();
				// tabCamera.refreshView();
				break;

			default:
				break;
			}
		};
	};

	public void startQurryCamera()
	{
		// if (progressDialog != null && !progressDialog.isShowing())
		// {
		// progressDialog.show();
		// }
		cameraTask = new CameraTask();
		cameraTask.execute();
	}

	public void cancelQurryCamera()
	{
		if (cameraTask != null)
		{
			cameraTask.cancel(true);
			cameraTask = null;
		}
	}

	private class CameraTask extends AsyncTask<Object, Object, Void>
	{
		private List<IPCameraInfo> cameraTempList;
		int existingCnt;

		@Override
		protected Void doInBackground(Object... params)
		{
			if (member != null)
			{
				cameraTempList = NetReq.qurryAllCamera(member.getMemberId(),
						member.getSsuid());
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
			if (cameraTempList != null && cameraTempList.size() > 0)
			{
				cameraList.clear();
				cameraList.addAll(cameraTempList);
				// int _id = 0;
				// for (int i = 0; i < cameraTempList.size(); i++)
				// {

				// try
				// {
				// Cursor cursour;
				//
				// if (!TextUtils.isEmpty(cameraTempList.get(i).uid))
				// {
				// // cursour = DatabaseHelper
				// // .QueryDevice(activity, deviceSearched[i].ip,
				// // deviceSearched[i].webPort);
				// // }
				// // else
				// // {
				// cursour = dbHelp.QueryDevice(context,
				// cameraTempList.get(i).uid);
				// if (cursour != null)
				// {
				// existingCnt = cursour.getCount();
				//
				// if (existingCnt > 0)
				// {
				// cursour.moveToFirst();
				// _id = cursour.getInt(cursour
				// .getColumnIndex("_id"));
				// cursour.close();
				// }
				// }
				// }
				// }
				// catch (Exception e)
				// {
				// Log.d("moon", e.getMessage());
				// }
				// LogUtil.LogI(TAG, "existingCnt=" + existingCnt);
				// // 数据库中不存在该数据，保存数据库
				// if (existingCnt == 0)
				// {
				//
				// ContentValues contentValue = new ContentValues();
				// IPCameraInfo info = new IPCameraInfo();
				// cameraList.add(cameraTempList.get(i));
				// info.devType = cameraTempList.get(i).devType;
				// info.devName = cameraTempList.get(i).devName;
				// info.ip = cameraTempList.get(i).ip;
				// info.streamType = 0;
				// info.webPort = cameraTempList.get(i).webPort;
				// info.mediaPort = cameraTempList.get(i).mediaPort;
				// info.uid = cameraTempList.get(i).uid;
				// contentValue.put("devType",
				// cameraTempList.get(i).devType);
				// contentValue.put("devName",
				// cameraTempList.get(i).devName);
				// contentValue.put("ip", cameraTempList.get(i).ip);
				// contentValue.put("streamType", 0);
				// contentValue.put("webPort",
				// cameraTempList.get(i).webPort);
				// contentValue.put("mediaPort",
				// cameraTempList.get(i).mediaPort);
				// contentValue.put("uid", cameraTempList.get(i).uid);
				// contentValue.put("userName",
				// cameraTempList.get(i).userName);
				// contentValue.put("devSetName", "");
				// contentValue.put("groupName",
				// cameraTempList.get(i).groupName);
				// contentValue.put("groupId",
				// cameraTempList.get(i).groupId);// -1表示未分组
				// contentValue.put("password",
				// cameraTempList.get(i).password);
				// contentValue.put("thumPath", "");
				// contentValue.put("cameraId",
				// cameraTempList.get(i).cameraId);
				//
				// try
				// {
				// dbHelp.insert(context, "tb_device_list",
				// contentValue);
				// // fillDataToCursor(); // 更新
				// // initData();
				// }
				// catch (Exception e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				// else
				// {
				// for (int j = 0; j < cameraList.size(); j++)
				// {
				// if (cameraList.get(j).uid.equals(cameraTempList
				// .get(i).uid))
				// {
				// cameraList.get(j).cameraId = cameraTempList
				// .get(i).cameraId;
				// cameraList.get(j).devName = cameraTempList
				// .get(i).devName;
				// cameraList.get(j).groupId = cameraTempList
				// .get(i).groupId;
				// cameraList.get(j).groupName = cameraTempList
				// .get(i).groupName;
				// cameraList.get(j).webPort = cameraTempList
				// .get(i).webPort;
				// cameraList.get(j).mediaPort = cameraTempList
				// .get(i).mediaPort;
				// cameraList.get(j).userName = cameraTempList
				// .get(i).userName;
				// cameraList.get(j).password = cameraTempList
				// .get(i).password;
				// cameraList.get(j).isOnLine = true;
				// }
				// }
				// ContentValues contentValue = new ContentValues();
				// contentValue.put("devType",
				// cameraTempList.get(i).devType);
				// contentValue.put("devName",
				// cameraTempList.get(i).devName);
				// contentValue.put("ip", cameraTempList.get(i).ip);
				// contentValue.put("streamType", 0);
				// contentValue.put("webPort",
				// cameraTempList.get(i).webPort);
				// contentValue.put("mediaPort",
				// cameraTempList.get(i).mediaPort);
				// contentValue.put("uid", cameraTempList.get(i).uid);
				// contentValue.put("devSetName", "");
				// contentValue.put("groupName",
				// cameraTempList.get(i).groupName);
				// // contentValue.put("groupId", -1);// -1表示未分组
				// contentValue.put("thumPath", "");
				// contentValue.put("groupId",
				// cameraTempList.get(i).groupId);
				// contentValue.put("userName",
				// cameraTempList.get(i).userName);
				// contentValue.put("password",
				// cameraTempList.get(i).password);
				// contentValue.put("cameraId",
				// cameraTempList.get(i).cameraId);
				// try
				// {
				// LogUtil.LogI(TAG, "_id=" + _id);
				// dbHelp.update(context, "tb_device_list",
				// contentValue, _id);
				// }
				// catch (Exception e)
				// {
				// e.printStackTrace();
				// }
				// }

				// }
//				tabCamera.refreshView();
				tabCamera.refreshListView();
			}

		}
	}

	public void startQurryGroup()
	{
		// if (progressDialog != null && !progressDialog.isShowing())
		// {
		// progressDialog.show();
		// }
		groupTask = new GroupTask();
		groupTask.execute();
	}
	
	public void cancelQurryGroup()
	{
		if (groupTask != null)
		{
			groupTask.cancel(true);
			groupTask = null;
		}
	}

	public void startQurryGroupWithDialog()
	{
		// if (progressDialog != null && !progressDialog.isShowing())
		// {
		// progressDialog.show();
		// }
		groupTask = new GroupTask();
		groupTask.execute();
	}
	
	private void QurryQurryGroup()
	{
		if (groupTask != null)
		{
			groupTask.cancel(true);
			groupTask = null;
		}
	}

	private class GroupTask extends AsyncTask<Object, Object, Void>
	{
		private List<GroupInfo> groupTempList;

		@Override
		protected Void doInBackground(Object... params)
		{
			if (member != null)
			{
				groupTempList = NetReq.qurryAllGroup(member.getMemberId(),
						member.getSsuid());
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
			if (groupTempList != null && groupTempList.size() > 0)
			{
				groupList = groupTempList;

				groupService.updateDeviceData(groupList);
				// tabMain.refreshView();
				tabMain.refreshView();
				// tabSense.refreshView();
				// tabCamera.refreshView();
			}

		}
	}
	
	public void startQurryScene()
	{
		sceneTask = new SceneTask();
		sceneTask.execute();
	}
	
	public void cancelQurryScene()
	{
		if (sceneTask != null)
		{
			sceneTask.cancel(true);
			sceneTask = null;
		}
	}

	public void startQurrySceneWithDialog()
	{
		sceneTask = new SceneTask();
		sceneTask.execute();
	}
	
	private class SceneTask extends AsyncTask<Object, Object, Void>
	{
		private List<SceneInfo> sceneTempList;

		@Override
		protected Void doInBackground(Object... params)
		{
			if (member != null)
			{
				sceneTempList = NetReq.qurryAllScene(member.getSsuid());
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
			if (sceneTempList != null && sceneTempList.size() > 0)
			{
				sceneList = sceneTempList;

				tabScene.refreshView();
			}

		}
	}
	
	public void startQurrySceneSwitch()
	{
		// if (progressDialog != null && !progressDialog.isShowing())
		// {
		// progressDialog.show();
		// }
		sceneSwitchTask = new SceneSwitchTask();
		sceneSwitchTask.execute();
	}
	
	public void cancelQurrySceneSwitch()
	{
		if (sceneSwitchTask != null)
		{
			sceneSwitchTask.cancel(true);
			sceneSwitchTask = null;
		}
	}

	private class SceneSwitchTask extends AsyncTask<Object, Object, Void>
	{
		private List<DeviceInfo> switchTempList;

		@Override
		protected Void doInBackground(Object... params)
		{
			if (member != null)
			{
				switchTempList = NetReq.qurrySceneSwitch(member.getMemberId(),
						member.getSsuid());
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
			if (switchTempList != null && switchTempList.size() > 0)
			{
				sceneSwitchList = switchTempList;
			}
		}
	}
	
	public void startQurrySceneSensor()
	{
		sceneSensorTask = new SceneSensorTask();
		sceneSensorTask.execute();
	}
	
	public void cancelQurrySceneSensor()
	{
		if (sceneSensorTask != null)
		{
			sceneSensorTask.cancel(true);
			sceneSensorTask = null;
		}
	}


	public void startQurrySceneSensorWithDialog()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		sceneSensorTask = new SceneSensorTask();
		sceneSensorTask.execute();
	}

	private class SceneSensorTask extends AsyncTask<Object, Object, Void>
	{
		private List<DeviceInfo> sensorTempList;

		@Override
		protected Void doInBackground(Object... params)
		{
			if (member != null)
			{
				sensorTempList = NetReq.qurrySceneSensor(member.getMemberId(),
						member.getSsuid());
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
			if (sensorTempList != null && sensorTempList.size() > 0)
			{
				sceneSensorList = sensorTempList;
			}
		}
	}

	public void registerMessageReceiver()
	{
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	@Override
	public void onSelect(int index) 
	{
		mSlidingMenu.toggle( true );
	}

}
