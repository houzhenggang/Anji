package com.anji.www.activity;

import java.io.File;
import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anji.www.R;
import com.anji.www.camare.MyAudio;
import com.anji.www.camare.MyVideoView;
import com.anji.www.camare.Talk;
import com.anji.www.util.MyActivityManager;
import com.ipc.sdk.FSApi;
import com.ipc.sdk.StatusListener;
import com.remote.util.ActivtyUtil;
import com.remote.util.IPCameraInfo;
import com.remote.util.MyStatusListener;

public class CameraActivity extends BaseActivity implements OnClickListener
{

	private static final String TAG = "CameraActivity";
	/*
	 * sdk support max 4 channel ,this demo use only one ,so ID set 0. Record
	 * only support H264 model.
	 */
	Context mContext;
	boolean IsRun = false;
	boolean isInLiveViewPage = true;
	MyVideoView vv;
	MyAudio mAudio = new MyAudio();
	Talk mTalk = new Talk();
	View view_top_btn_array;
	View view_bottom_btn_array;

	boolean hasTouchMoved;
	float startX = 0;
	float startY = 0;
	float endX = 0;
	float endY = 0;
	float moveDistanceX = 0;
	float moveDistanceY = 0;
	double angel = 0.0;
	Handler ptzHandler = new Handler();
	Runnable ptzStopRunnable = new Runnable()
	{
		public void run()
		{
			FSApi.ptzStopRun(0);
		}
	};
	// private GestureDetector mGestureDetector;

	private SoundPool sp;
	private int music;

	private int dispMode = 0;

	private LinearLayout ll_back;// 返回按钮
	private TextView tv_title;// 摄像头标题
	private LinearLayout layout;
	private Button tv_record;// 录制
	private Button tv_talk;// 对讲
	private Button tv_snap;// 截图
	private Button tv_listen;// 监听
	private TextView zoom_in;// 放大
	private TextView zoom_out;// 缩小

	private Button btn_up;// 向上
	private Button btn_down;// 向下
	private Button btn_left;// 向左
	private Button btn_right;// 向右
	private Button btn_stop;// 停止
	private int audioState = 0;
	private int talkState = 0;
	private String uid;

	// Button btn_device;
	// Button btn_exit;

	// Button btn_Record;
	// TextView tv_devName;

	private Handler mStatusMsgHandler;

	private IPCameraInfo lastConnectIpcInfo = new IPCameraInfo();
	private IPCameraInfo lastConnectIpcInfoTemp = new IPCameraInfo();
	public static boolean hasConnected = false;
	private boolean IsRecord = false;

	private boolean isVVStarted = false;
	private boolean isVideoStreamStarted = false;

	int cursor_id = -1;

	private Button bt_back;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mContext = this;
		MyActivityManager.Add(TAG, this);
		// 无title
		// FSApi.Init();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		uid = getIntent().getStringExtra("uid");
		initData();

		initView();

		if (!isVVStarted)
		{
			isVVStarted = true;
			vv.start();
			mAudio.start();
			mTalk.start();

		}

		FSApi.stopAudioStream(0);
		vv.stopVideoStream();

		android.os.SystemClock.sleep(500);

		vv.clearScreen();

		hasConnected = true;
		vv.startVideoStream();

		// lastConnectIpcInfoTemp.devType = devType;
		// lastConnectIpcInfoTemp.devName = devName;
		// lastConnectIpcInfoTemp.streamType = streamType;
		// lastConnectIpcInfoTemp.ip = ip;
		// lastConnectIpcInfoTemp.webport = webPort;
		// lastConnectIpcInfoTemp.mediaport = mediaPort;
		// lastConnectIpcInfoTemp.uid = uid;
		// lastConnectIpcInfoTemp.userName = userName;
		// lastConnectIpcInfoTemp.password = password;

		isVideoStreamStarted = true;

		// save device info
		lastConnectIpcInfo.devType = lastConnectIpcInfoTemp.devType;
		lastConnectIpcInfo.devName = lastConnectIpcInfoTemp.devName;
		lastConnectIpcInfo.streamType = lastConnectIpcInfoTemp.streamType;
		lastConnectIpcInfo.ip = lastConnectIpcInfoTemp.ip;
		lastConnectIpcInfo.webPort = lastConnectIpcInfoTemp.webPort;
		lastConnectIpcInfo.mediaPort = lastConnectIpcInfoTemp.mediaPort;
		lastConnectIpcInfo.uid = lastConnectIpcInfoTemp.uid;
		lastConnectIpcInfo.userName = lastConnectIpcInfoTemp.userName;
		lastConnectIpcInfo.password = lastConnectIpcInfoTemp.password;

	}

	private void initView()
	{
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels; // 屏幕高度（像素）

		layout = new LinearLayout(this);
		setContentView(layout);

		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量

		music = sp.load(this, R.raw.camera_click, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级

		// 获取status高度
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try
		{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = getResources().getDimensionPixelSize(x);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		layout.setOrientation(LinearLayout.VERTICAL);

		int btn_array_height = (int) (0.08 * height);

		// 上方按钮
		view_top_btn_array = LayoutInflater.from(this).inflate(
				R.layout.title_camera, null);
		layout.addView(view_top_btn_array, width, btn_array_height);

		int vvHight = (int) (width * 240.00 / 320.00);

		// vv = new VideoView(this, width, height
		// -btn_array_height-btn_array_height-statusBarHeight );
		// layout.addView(vv, width, height
		// -btn_array_height-btn_array_height-statusBarHeight );
		vv = new MyVideoView(this, width, vvHight);
		layout.addView(vv, width, vvHight);

		view_bottom_btn_array = LayoutInflater.from(this).inflate(
				R.layout.camera_activity, null);
		// layout.addView(view_bottom_btn_array, width, btn_array_height);
		// layout.addView(view_bottom_btn_array);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layout.addView(view_bottom_btn_array, params);
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		bt_back = (Button) findViewById(R.id.bt_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_record = (Button) findViewById(R.id.tv_record);
		tv_talk = (Button) findViewById(R.id.tv_talk);
		tv_snap = (Button) findViewById(R.id.tv_snap);
		tv_listen = (Button) findViewById(R.id.tv_listen);
		zoom_in = (TextView) findViewById(R.id.zoom_in);
		zoom_out = (TextView) findViewById(R.id.zoom_out);

		btn_up = (Button) findViewById(R.id.btn_up);
		btn_down = (Button) findViewById(R.id.btn_down);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_stop = (Button) findViewById(R.id.btn_stop);

		ll_back.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		// tv_title.setOnClickListener(this);
		tv_record.setOnClickListener(this);
		tv_talk.setOnClickListener(this);
		tv_snap.setOnClickListener(this);
		tv_listen.setOnClickListener(this);
		zoom_in.setOnClickListener(this);
		zoom_out.setOnClickListener(this);
		btn_up.setOnClickListener(this);
		btn_down.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_up.setOnClickListener(this);

	}

	private void initData()
	{
		IsRun = true;

		isInLiveViewPage = true;

		lastConnectIpcInfo.devType = 0;
		lastConnectIpcInfo.devName = "";
		lastConnectIpcInfo.ip = "";
		lastConnectIpcInfo.streamType = 0;
		lastConnectIpcInfo.webPort = 0;
		lastConnectIpcInfo.mediaPort = 0;
		lastConnectIpcInfo.uid = "";
		lastConnectIpcInfo.userName = "";
		lastConnectIpcInfo.password = "";

		lastConnectIpcInfoTemp.devType = 0;
		lastConnectIpcInfoTemp.devName = "";
		lastConnectIpcInfoTemp.ip = "";
		lastConnectIpcInfoTemp.streamType = 0;
		lastConnectIpcInfoTemp.webPort = 0;
		lastConnectIpcInfoTemp.mediaPort = 0;
		lastConnectIpcInfoTemp.uid = "";
		lastConnectIpcInfoTemp.userName = "";
		lastConnectIpcInfoTemp.password = "";

		mStatusMsgHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				String promoteString = "";
				switch (msg.arg1)
				{
				case StatusListener.STATUS_LOGIN_SUCCESS:
					promoteString = mContext.getResources().getString(
							R.string.login_promote_success);
					break;
				case StatusListener.STATUS_LOGIN_FAIL_USR_PWD_ERROR:
					promoteString = mContext.getResources().getString(
							R.string.login_promote_fail_usr_pwd_error);
					break;
				case StatusListener.STATUS_LOGIN_FAIL_ACCESS_DENY:
					promoteString = mContext.getResources().getString(
							R.string.login_promote_fail_access_deny);
					break;
				case StatusListener.STATUS_LOGIN_FAIL_EXCEED_MAX_USER:
					promoteString = mContext.getResources().getString(
							R.string.login_promote_fail_exceed_max_user);
					break;
				case StatusListener.STATUS_LOGIN_FAIL_CONNECT_FAIL:
					promoteString = mContext.getResources().getString(
							R.string.login_promote_fail_connect_fail);
					break;
				case StatusListener.FS_API_STATUS_OPEN_TALK_SUCCESS:
					promoteString = mContext.getResources().getString(
							R.string.open_talk_promote_success);
					break;
				case StatusListener.FS_API_STATUS_OPEN_TALK_FAIL_USED_BY_ANOTHER_USER:
					promoteString = mContext.getResources().getString(
							R.string.open_talk_promote_fail);
					break;
				case StatusListener.FS_API_STATUS_CLOSE_TALK_SUCCESS:
					if (hasConnected)
					{
						promoteString = mContext.getResources().getString(
								R.string.close_talk_promote_success);
					}
					break;
				case StatusListener.FS_API_STATUS_CLOSE_TALK_FAIL:
					promoteString = mContext.getResources().getString(
							R.string.close_talk_promote_fail);
					break;
				case 1000:
					hasConnected = true;
					vv.startVideoStream();

					// save device info
					lastConnectIpcInfo.devType = lastConnectIpcInfoTemp.devType;
					lastConnectIpcInfo.devName = lastConnectIpcInfoTemp.devName;
					lastConnectIpcInfo.streamType = lastConnectIpcInfoTemp.streamType;
					lastConnectIpcInfo.ip = lastConnectIpcInfoTemp.ip;
					lastConnectIpcInfo.webPort = lastConnectIpcInfoTemp.webPort;
					lastConnectIpcInfo.mediaPort = lastConnectIpcInfoTemp.mediaPort;
					lastConnectIpcInfo.uid = lastConnectIpcInfoTemp.uid;
					lastConnectIpcInfo.userName = lastConnectIpcInfoTemp.userName;
					lastConnectIpcInfo.password = lastConnectIpcInfoTemp.password;

					break;
				case 1001:

					hasConnected = false;
					vv.clearScreen();

					break;

				default:
					promoteString = "";
					break;
				}

				if (isInLiveViewPage)
				{
					if (!"".equals(promoteString))
					{
						ActivtyUtil.openToast(mContext, promoteString);
					}
				}
			}
		};
		MyStatusListener.setMyHandler(mStatusMsgHandler);
		// final MyStatusListener statusListener = new MyStatusListener();
		//
		// new Thread(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// int id;
		// int StatusID;
		// while (IsRun)
		// {
		// for (id = 0; id < 4; id++)
		// {
		// StatusID = FSApi.getStatusId(id);
		//
		// if (StatusID < 0)
		// {
		// try
		// {
		// Thread.sleep(50);
		// }
		// catch (InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		// }
		// else
		// {
		// statusListener.OnStatusCbk(StatusID, id, 0, 0, 0);
		// }
		// }
		// }
		//
		// }
		// }).start();
	}

	//
	// class ClickEvent implements View.OnClickListener{
	//
	// @Override
	// public void onClick(View v) {
	// if( v == btn_snap )
	// {
	// if( hasConnected )
	// {
	// // 确认存放目录是否存在，不存在，先创建该目录
	// // 已连接视频才可以抓拍(temp)
	// sp.play(music, 1, 1, 0, 0, 1);
	//
	// String picSaveDir =
	// Environment.getExternalStorageDirectory().getPath()+"/IPC/snap";
	// File file = new File( picSaveDir );
	// if( !file.exists() )
	// {
	// file.mkdirs();
	// }
	// File f=new File(picSaveDir+"/"+System.currentTimeMillis()+".jpg");
	// FSApi.snapPic(f.getAbsolutePath(),0);
	// Toast.makeText(CameraActivity.this, "snap ok ",
	// Toast.LENGTH_SHORT).show();
	//
	// }
	// }
	// else if(v == btn_audio )//监听
	// {
	// if( hasConnected )
	// {
	// if( audioState == 0 )
	// {
	// // btn_audio.setBackgroundResource(R.drawable.d1);
	// FSApi.startAudioStream(0);
	// audioState = 1;
	// ActivtyUtil.openToast(CameraActivity.this,
	// getResources().getString(R.string.open_audio_promote_success) );
	// }
	// else
	// {
	// // btn_audio.setBackgroundResource(R.drawable.d9);
	// FSApi.stopAudioStream(0);
	// audioState = 0;
	// ActivtyUtil.openToast(CameraActivity.this,
	// getResources().getString(R.string.close_audio_promote_success) );
	// }
	// }
	// }
	// else if( v == btn_talk ) // 对讲
	// {
	// if( hasConnected )
	// {
	// if( talkState == 0 )
	// {
	// // btn_talk.setBackgroundResource(R.drawable.d2);
	// mTalk.startTalk(lastConnectIpcInfoTemp.devType);
	// talkState = 1;
	// }
	// else
	// {
	// // btn_talk.setBackgroundResource(R.drawable.d8);
	// mTalk.stopTalk();
	// talkState = 0;
	// }
	// }
	// }
	// // else if(v == btn_device)// 设备
	// // {
	// // Intent mainIntent = new Intent( CameraActivity.this, DeviceList.class
	// );
	// //
	// // startActivityForResult(mainIntent, 0);
	// //
	// // overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
	// //
	// // isInCameraActivityPage = false;
	// // }
	// else if(v == btn_Record)
	// {
	// if(!IsRecord)
	// {
	// IsRecord=true;
	// // btn_Record.setBackgroundResource(R.drawable.d5);
	// String SDPATH=Environment.getExternalStorageDirectory().toString();
	// String filepath=SDPATH+"/IPC/Video";
	// File file = new File(filepath);
	// if( !file.exists() )
	// {
	// file.mkdirs();
	// }
	// String fileName=System.currentTimeMillis()+".avi";
	//
	// FSApi.StartRecord(filepath+"/", fileName, 0);
	// Toast.makeText(CameraActivity.this, "start record",
	// Toast.LENGTH_SHORT).show();
	// }
	// else
	// {
	// IsRecord=false;
	// // btn_Record.setBackgroundResource(R.drawable.d4);
	// FSApi.StopRecord(0);
	// Toast.makeText(CameraActivity.this, "stop record",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// else if(v == btn_exit)
	// {
	// exit();
	// }
	// }
	// }

	public void exit()
	{
		new AlertDialog.Builder(this)
				// .setTitle(getResources().getString(R.string.app_exit_title))
				// .setMessage(getResources().getString(R.string.app_exit_warning))
				.setPositiveButton(getResources().getString(R.string.btn_OK),
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{

								vv.stopVideoStream();

								vv.clearScreen();
								if (IsRecord)
								{
									FSApi.StopRecord(0);
								}
								FSApi.usrLogOut(0);

								vv.stop();
								mAudio.stop();
								mTalk.stop();

								FSApi.Uninit();

								CameraActivity.this.finish();
								isInLiveViewPage = false;
							}
						})
				.setNegativeButton(
						getResources().getString(R.string.btn_Cancel),
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								dialog.cancel();
							}
						}).show();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.ll_back:
			// 返回
			// finish();
			onBackPressed();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_back:
			// 返回
			// finish();
			onBackPressed();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.tv_record:
			// 录制
			if (!IsRecord)
			{
				IsRecord = true;
				tv_record.setText(getString(R.string.stop));
				// btn_Record.setBackgroundResource(R.drawable.d5);
				String SDPATH = Environment.getExternalStorageDirectory()
						.toString();
				String filepath = SDPATH + "/anji/" + uid + "/Video";
				File file = new File(filepath);
				if (!file.exists())
				{
					file.mkdirs();
				}
				String fileName = System.currentTimeMillis() + ".avi";

				FSApi.StartRecord(filepath + "/", fileName, 0);
				Toast.makeText(this, "start record", Toast.LENGTH_SHORT).show();
			}
			else
			{
				IsRecord = false;
				tv_record.setText(getString(R.string.rescord));
				// btn_Record.setBackgroundResource(R.drawable.d4);
				FSApi.StopRecord(0);
				Toast.makeText(this, "stop record", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_talk:
			// 对讲
			if (hasConnected)
			{
				if (talkState == 0)
				{
					tv_talk.setText(getString(R.string.stop));
					mTalk.startTalk(lastConnectIpcInfoTemp.devType);
					talkState = 1;
				}
				else
				{
					tv_talk.setText(getString(R.string.talk));
					mTalk.stopTalk();
					talkState = 0;
				}
			}
			break;
		case R.id.tv_snap:
			// 截图
			if (hasConnected)
			{
				// 确认存放目录是否存在，不存在，先创建该目录
				// 已连接视频才可以抓拍(temp)
				sp.play(music, 1, 1, 0, 0, 1);

				String picSaveDir = Environment.getExternalStorageDirectory()
						.getPath() + "/anji/" + uid + "/snap";
				File file = new File(picSaveDir);
				if (!file.exists())
				{
					file.mkdirs();
				}
				File f = new File(picSaveDir + "/" + System.currentTimeMillis()
						+ ".jpg");
				FSApi.snapPic(f.getAbsolutePath(), 0);
				Toast.makeText(CameraActivity.this, "snap ok ",
						Toast.LENGTH_SHORT).show();

			}
			break;
		case R.id.tv_listen:
			// 监听
			if (hasConnected)
			{
				if (audioState == 0)
				{
					tv_listen.setText(getString(R.string.stop));
					FSApi.startAudioStream(0);
					audioState = 1;
					// ActivtyUtil.openToast(LiveView.this,
					// getResources().getString(R.string.open_audio_promote_success)
					// );
				}
				else
				{
					tv_listen.setText(getString(R.string.listen));
					FSApi.stopAudioStream(0);
					audioState = 0;
					// ActivtyUtil.openToast(LiveView.this,
					// getResources().getString(R.string.close_audio_promote_success)
					// );
				}
			}
			break;
		case R.id.zoom_in:
			// 放大
			break;
		case R.id.zoom_out:
			// 缩小
			break;
		case R.id.btn_up:
			// 向上
			ptzHandler.removeCallbacks(ptzStopRunnable);
			FSApi.ptzMoveUp(0);
			ptzHandler.postDelayed(ptzStopRunnable, 1000);
			break;
		case R.id.btn_down:
			// 向下
			ptzHandler.removeCallbacks(ptzStopRunnable);
			FSApi.ptzMoveDown(0);
			ptzHandler.postDelayed(ptzStopRunnable, 1000);
			break;
		case R.id.btn_left:
			// 向左
			ptzHandler.removeCallbacks(ptzStopRunnable);
			FSApi.ptzMoveLeft(0);
			ptzHandler.postDelayed(ptzStopRunnable, 1000);
			break;
		case R.id.btn_right:
			// 向右
			ptzHandler.removeCallbacks(ptzStopRunnable);
			FSApi.ptzMoveRight(0);
			ptzHandler.postDelayed(ptzStopRunnable, 1000);
			break;
		case R.id.btn_stop:
			// 停止
			FSApi.ptzStopRun(0);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy()
	{
		cancel();
		MyActivityManager.finish(TAG);
		super.onDestroy();
	}

	private void cancel()
	{
		vv.stopVideoStream();

		vv.clearScreen();
		if (IsRecord)
		{
			FSApi.StopRecord(0);
		}
		FSApi.usrLogOut(0);

		vv.stop();
		mAudio.stop();
		mTalk.stop();
		// FSApi.Uninit();

		this.finish();
		isInLiveViewPage = false;
	}

	@Override
	public void onBackPressed()
	{
		// moveTaskToBack(true);//切换到后台
		super.onBackPressed();
	}
}
