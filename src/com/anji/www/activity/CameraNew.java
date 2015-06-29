package com.anji.www.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.anji.www.R;
import com.anji.www.camera.util.AudioThread;
import com.anji.www.camera.util.Global;
import com.anji.www.camera.util.TalkThread;
import com.anji.www.camera.view.VideoSurfaceView;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.fos.sdk.FosResult;
import com.fos.sdk.FosSdkJNI;
import com.fos.sdk.StreamType;

import com.fos.sdk.*;
import com.ipc.sdk.FSApi;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CameraNew extends Activity implements View.OnClickListener,
		View.OnTouchListener {
	public static short PORT = 88;
	public static String IP_OR_DNS = "172.16.0.41";
	public static String UID = "DFJTA1RY8WUF8G6PSZYS";
	public static String LOGNAME = "admin";
	public static String LOGPD = "a";
	private String newPass;
	public static int STREAM_TYPE = 0;
	public static int CONNET_TYPE = 1;
	public static int con_type = 1;
	public byte[] snapData = new byte[512 * 1024];
	public static Integer dataLen = 512 * 1024;

	public static final int OPNE_VIDEO_SUC = 10;
	public static final int OPNE_VIDEO_FAILED = 11;
	private static final String Tag = "CameraNew";

	private VideoSurfaceView mVideoSurfaceView;

	public boolean isRun = true;
	public int OPEN_VIDEO_STATE = -1;
	public int LOGIN_STATE = -3;

	private Integer mPermissionFlag = new Integer(-1);
	private Integer mGetDataLength = new Integer(-1);

	private RelativeLayout rl_videoSurface;
	private LinearLayout ll_back;
	private Button bt_back;
	private Button bt_edit;
	private Button tv_listen;
	private TextView et_username;
	private EditText et_password;
	private Dialog inputDialog;
	private String userName;
	private String password;
	private boolean isLoginSucess;
	private Dialog progressDialog;
	private Context mContext;
	private String cameraId;
	double nLenStart = 0;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case OPNE_VIDEO_SUC:
				while (isRun) {
					// Log.i("jerry", "getDecodeData------->aa      ");
					// int show =
					// FosSdkJNI.GetDecodeData(Global.mHandlerNo,
					// mFramedata,0,mGetDataLength,2,0);
					// Log.i("jerry", "getDecodeData------->"+show);
					// SystemClock.sleep(30);
				}
			default:
				break;
			}
		};
	};

	/*** 显示正在连接界面 **/
	/*
	 * private void showConnectingView() { if (mVideoSurfaceView.hasReceivedData
	 * == false) { if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
	 * mVideoSurfaceView.setBackground(null); } else {
	 * mVideoSurfaceView.setBackgroundDrawable(null); } } }
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_activity_new);
		isLoginSucess = false;
		mContext = this;
		/* 获取Intent中的Bundle对象 */
		Bundle bundle = this.getIntent().getExtras();
		/* 获取Bundle中的数据，注意类型和key */
		IP_OR_DNS = bundle.getString("ip");
		UID = bundle.getString("uid");
		LOGNAME = bundle.getString("uname");
		LOGPD = bundle.getString("pwd");
		PORT = bundle.getShort("port");
		con_type = bundle.getInt("type");
		cameraId = bundle.getString("cameraId");

		Log.i("jerry", IP_OR_DNS + " " + UID + " " + LOGNAME + " " + LOGPD
				+ "  " + PORT);
		mVideoSurfaceView = (VideoSurfaceView) findViewById(R.id.live_surface_view);

		// showConnectingView();

		initView();

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (con_type > 1) {
					con_type = 1;
				}

				Global.mHandlerNo = FosSdkJNI.Create(IP_OR_DNS, UID, LOGNAME,
						LOGPD, PORT, PORT, IPCType.FOSIPC_H264, con_type);

				if (Global.mHandlerNo > 0) {

					LOGIN_STATE = FosSdkJNI.Login(Global.mHandlerNo,
							mPermissionFlag, 3000);
					Log.i("jerry", "privileg-------->" + mPermissionFlag);
					Log.i("jerry", "LOGIN_STATE-------->" + LOGIN_STATE);
					/*
					 * Integer b = new Integer(-1);
					 * FosSdkJNI.CheckHandle(Global.mHandlerNo, b);
					 * Log.i("jerry", "check state -------->" + b);
					 * ResetPointList presetPointList = new ResetPointList();
					 * FosSdkJNI.PTZGetPresetPointList(Global.mHandlerNo, 2000,
					 * presetPointList); Log.i("jerry", "???????????? "+
					 * presetPointList.pointName[0]+"  "+
					 * presetPointList.pointName[1]); CruiseMapList
					 * cruiseMapList = new CruiseMapList();
					 * FosSdkJNI.PTZGetCruiseMapList(Global.mHandlerNo, 2000,
					 * cruiseMapList); Log.i("jerry", "222222222 "+
					 * cruiseMapList.cruiseMapName[0]+"  "+
					 * cruiseMapList.cruiseMapName[1]);
					 */
					/*
					 * FosSdkJNI.Logout(Global.mHandlerNo, 3000);
					 * FosSdkJNI.Release(Global.mHandlerNo); Global.mHandlerNo =
					 * FosSdkJNI.Create(IP_OR_DNS, UID, LOGNAME, LOGPD, PORT,
					 * PORT, IPCType.FOSIPC_H264, ConnectType.FOSCNTYPE_IP);
					 * LOGIN_STATE = FosSdkJNI.Login(Global.mHandlerNo,
					 * mPermissionFlag, 3000); Log.i("jerry",
					 * "privileg2-------->" + mPermissionFlag); Log.i("jerry",
					 * "LOGIN_STATE2-------->" + LOGIN_STATE);
					 */

					/**
					 * * 0 0 - - Login Login success success * - -1 1 - -
					 * Parameter Parameter check check error error * - -2 2 - -
					 * Exceed Exceed max max user user * - -4 4 - - User User
					 * not not exist exist * - -5 5 - - Password Password * - -6
					 * 6 - - Password Password error error * - -7 7 - - Access
					 * Access deny deny * - -8 8 - - Already Already login login
					 */
					LoginResultHandler.obtainMessage(LOGIN_STATE)
							.sendToTarget();

				} else {

					// Toast.makeText(getApplicationContext(), "ipc链接失败",
					// 1000).show();
				}
			}
		}).start();

	}

	private Handler LoginResultHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				StrData dname = new StrData();
				FosSdkJNI.GetDevName(Global.mHandlerNo, 2000, dname);
				Log.i("jerry", "dname = " + dname.str);
				Global.OPEN_VIDEO_STATE = FosSdkJNI.OpenVideo(
						Global.mHandlerNo, StreamType.FOSSTREAM_SUB, 1000);
				if (Global.OPEN_VIDEO_STATE == 0) {
					Global.isReceiveData = true;
				}
				Log.i("jerry", "OPEN_VIDEO_STATE------->"
						+ Global.OPEN_VIDEO_STATE);
				isLoginSucess = true;
				Bitmap bit = mVideoSurfaceView.getmBit();
				LogUtil.LogI(Tag, "bit != null==="+(bit != null));
				if (bit != null) {
					int width = bit.getWidth();
					int hight = bit.getHeight();
					LogUtil.LogI(Tag, "图像宽width =="+width);
					LogUtil.LogI(Tag, "图像高hight=="+hight);
				}
				break;
			case FosResult.FOSCMDRET_FAILD:
				/** failed */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			case FosResult.FOSUSRRET_USRNAMEORPWD_ERR:
				/** username or password error */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail_usr_pwd_error));
				break;
			case FosResult.FOSCMDRET_EXCEEDMAXUSR:
				/** exceed max user number */
				ToastUtils.show(CameraNew.this, getString(R.string.exceed_max));
				break;
			case FosResult.FOSCMDRET_NO_PERMITTION:
				/** no permit */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			case FosResult.FOSCMDRET_UNSUPPORT:
				/** not support */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			case FosResult.FOSCMDRET_BUFFULL:
				/** buf is full */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			case FosResult.FOSCMDRET_ARGS_ERR:
				/** args error */
				ToastUtils.show(CameraNew.this, getString(R.string.args_error));
				break;
			case FosResult.FOSCMDRET_UNKNOW:
				/** unknow */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			case FosResult.FOSCMDRET_NOLOGIN:
				/** user no login */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			case FosResult.FOSCMDRET_NOONLINE:
				/** the device is no online */
				ToastUtils.show(CameraNew.this,
						getString(R.string.device_not_online));
				break;
			case FosResult.FOSCMDRET_ACCESSDENY:
				/** the access deny */
				ToastUtils.show(CameraNew.this,
						getString(R.string.the_access_deny));
				break;
			case FosResult.FOSCMDRET_DATAPARSEERR:
				/** parse data error */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			case FosResult.FOSCMDRET_APITIMEERR:
				/** api time error */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			case FosResult.FOSCMDRET_INTERFACE_CANCEL_BYUSR:
				/** cancle */
				break;
			case FosResult.FOSCMDRET_TIMEOUT:
				/** time out */
				ToastUtils.show(CameraNew.this, getString(R.string.tiem_out));
				break;
			case FosResult.FOSCMDRET_HANDLEERR:
				/** handle error */
				ToastUtils.show(CameraNew.this,
						getString(R.string.login_promote_fail));
				break;
			default:
				break;
			}
		};
	};

	private Button video;
	private Button audio;
	private Button talk;
	private Button recode;
	private Button capture;
	private Button left_up;
	private Button up_btn;
	private Button right_up;
	private Button left;
	private Button reset;
	private Button right;
	private Button left_down;
	private Button down;
	private Button right_down;
	private Button confirm;
	private Button gotobutton;
	private EditText editText;
	private Button ptz_stop;
	private TalkThread mTalkThread;
	public AudioThread mAudioThread = new AudioThread();

	private void initView() {
		
		WindowManager wm = this.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();

	     int height = wm.getDefaultDisplay().getHeight();
//		DisplayMetrics metric = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metric);
//		int width = metric.widthPixels; // 屏幕宽度（像素）
//		int height = metric.heightPixels; // 屏幕高度（像素）
//		int vvHight = (int) (height * 2 / 5);
	     
		progressDialog = DisplayUtils.createDialog(mContext);
		initInputDialog();
		width = (int) (width*1.2);
		rl_videoSurface = (RelativeLayout) findViewById(R.id.rl_videoSurface);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				width,(int)(width * 240.00 / 420.00));
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				width, (int) (width * 240.00 / 420.00));
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				width,  (int) (width * 315.00 / 420.00));
		rl_videoSurface.setLayoutParams(params);

		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		// video = (Button) findViewById(R.id.video_btn);
		// audio = (Button) findViewById(R.id.audio_btn);
		talk = (Button) findViewById(R.id.tv_talk);
		recode = (Button) findViewById(R.id.tv_record);
		capture = (Button) findViewById(R.id.tv_snap);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_edit = (Button) findViewById(R.id.bt_edit);
		bt_edit.setOnClickListener(this);
		// confirm = (Button) findViewById(R.id.confirm_btn);
		// gotobutton = (Button) findViewById(R.id.goto_btn);

		// video.setOnClickListener(this);
		// audio.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		talk.setOnClickListener(this);
		recode.setOnClickListener(this);
		capture.setOnClickListener(this);
		ll_back.setOnClickListener(this);

		// confirm.setOnClickListener(this);
		// gotobutton.setOnClickListener(this);
		// video.setOnClickListener(this);

		// left_up = (Button) findViewById(R.id.left_up_btn);
		up_btn = (Button) findViewById(R.id.btn_up);
		// right_up = (Button) findViewById(R.id.right_up_btn);
		left = (Button) findViewById(R.id.btn_left);
		// reset = (Button) findViewById(R.id.reset_btn);
		right = (Button) findViewById(R.id.btn_right);
		// left_down = (Button) findViewById(R.id.left_down_btn);
		down = (Button) findViewById(R.id.btn_down);
		// right_down = (Button) findViewById(R.id.right_down_btn);
		ptz_stop = (Button) findViewById(R.id.btn_stop);
		tv_listen = (Button) findViewById(R.id.tv_listen);

		// left_up.setOnTouchListener(this);
		// up_btn.setOnTouchListener(this);
		up_btn.setOnClickListener(this);
		left.setOnClickListener(this);
		right.setOnClickListener(this);
		down.setOnClickListener(this);
		ptz_stop.setOnClickListener(this);
		tv_listen.setOnClickListener(this);
		// right_up.setOnTouchListener(this);
		// left.setOnTouchListener(this);
		// // reset.setOnTouchListener(this);
		// right.setOnTouchListener(this);
		// // left_down.setOnTouchListener(this);
		// down.setOnTouchListener(this);
		// // right_down.setOnTouchListener(this);
		// ptz_stop.setOnTouchListener(this);
		mVideoSurfaceView.setOnTouchListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.i(Tag, "onDestroy()");
		cancelCamera();
		isRun = false;
		FosSdkJNI.Logout(Global.mHandlerNo, 1000);
		Log.i(Tag, "Logout");
		FosSdkJNI.Release(Global.mHandlerNo);
		Log.i(Tag, "Release");
		// FosSdkJNI.DeInit();
		Log.i(Tag, "DeInit");
		super.onDestroy();
		Log.i(Tag, "super.onDestroy()");
		// FosSdkJNI.Init();
		Log.i(Tag, "Init");
	}

	@Override
	public void onBackPressed() {
		Global.isIPCconect = false;
		CameraNew.this.finish();
		Log.i(Tag, "onBackPressed");

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		LogUtil.LogI(Tag, "onTouch  getAction==" + event.getAction());
		LogUtil.LogI(Tag, "View  getId==" + v.getId());
		switch (v.getId()) {
		// case R.id.left_up_btn:
		// if(Global.isReceiveData){
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 4, 1000);
		// }
		// break;
		case R.id.btn_up:
			if (Global.isReceiveData) {
				FosSdkJNI.PtzCmd(Global.mHandlerNo, 0, 500);
			}
			break;
		// case R.id.right_up_btn:
		// if(Global.isReceiveData){
		//
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 6, 1000);
		// }
		// break;
		case R.id.btn_left:
			if (Global.isReceiveData) {

				FosSdkJNI.PtzCmd(Global.mHandlerNo, 2, 500);
			}
			break;
		// case R.id.reset_btn:
		// if(Global.isReceiveData){
		//
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 8, 1000);
		// }
		// break;
		case R.id.btn_right:
			if (Global.isReceiveData) {

				FosSdkJNI.PtzCmd(Global.mHandlerNo, 3, 500);
			}
			break;
		// case R.id.left_down_btn:
		// if(Global.isReceiveData){
		//
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 5, 1000);
		// }
		// break;
		case R.id.btn_down:
			if (Global.isReceiveData) {

				FosSdkJNI.PtzCmd(Global.mHandlerNo, 1, 500);
			}
			break;
		// case R.id.right_down_btn:
		// if(Global.isReceiveData){
		//
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 7, 1000);
		// }
		// break;
		case R.id.btn_stop:
			if (Global.isReceiveData) {
				LogUtil.LogI(Tag, "btn_stop");
				FosSdkJNI.PtzCmd(Global.mHandlerNo, 9, 500);
			}
			break;
		case R.id.live_surface_view:

			int nCnt = event.getPointerCount();
			LogUtil.LogI(Tag, "View  getPointerCount==" + nCnt);
			int n = event.getAction();
			if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN
					&& 2 == nCnt)// <span
									// style="color:#ff0000;">2表示两个手指</span>
			{

				for (int i = 0; i < nCnt; i++) {
					float x = event.getX(i);
					float y = event.getY(i);

					Point pt = new Point((int) x, (int) y);

				}

				int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
				int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

				nLenStart = Math.sqrt((double) xlen * xlen + (double) ylen
						* ylen);

			} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP
					&& 2 == nCnt) {

				for (int i = 0; i < nCnt; i++) {
					float x = event.getX(i);
					float y = event.getY(i);

					Point pt = new Point((int) x, (int) y);

				}

				int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
				int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

				double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen
						* ylen);
				LogUtil.LogI(Tag, "ontouch  nLenEnd==" + nLenEnd);
				LogUtil.LogI(Tag, "ontouch  nLenStart==" + nLenStart);
				int result;
				FosSdkJNI.PTZSetZoomSpeed(Global.mHandlerNo, 1, 3000);
				if (nLenEnd > nLenStart)// 通过两个手指开始距离和结束距离，来判断放大缩小
				{
					// Toast.makeText(getApplicationContext(), "放大",
					// 3000).show();
					result = FosSdkJNI.PTZZoom(Global.mHandlerNo, 1, 3000);
				} else {
					result = FosSdkJNI.PTZZoom(Global.mHandlerNo, 0, 3000);
					// Toast.makeText(getApplicationContext(), "缩小",
					// 3000).show();
				}
				LogUtil.LogI(Tag, "ontouch result=" + result);
			}

			// return super.onTouchEvent(event);
			break;
		default:
			break;
		}

		// return super.onTouchEvent(event);
		return true;
	}

	Handler ptzHandler = new Handler();
	Runnable ptzStopRunnable = new Runnable() {
		public void run() {
			if (Global.isReceiveData) {
				LogUtil.LogI(Tag, "btn_stop");
				FosSdkJNI.PtzCmd(Global.mHandlerNo, 9, 500);
			}
		}
	};

	private boolean isTalkOpen = false;
	private EditGroupTask editGroupTask;

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.video_btn:
		// if(Global.OPEN_VIDEO_STATE == 0){
		// Global.OPEN_VIDEO_STATE = FosSdkJNI.CloseVideo(Global.mHandlerNo,
		// 1000);
		// video.setText("开启视频");
		// }else{
		// Global.OPEN_VIDEO_STATE =
		// FosSdkJNI.OpenVideo(Global.mHandlerNo,StreamType.FOSSTREAM_SUB,
		// 1000);
		// video.setText("关闭视频");
		// }
		// break;
		// case R.id.audio_btn:
		// if(Global.isReceiveData){
		// if(!Global.isAudioOpenOrNot){
		// audio.setText("停止音频");
		// int openRet = FosSdkJNI.OpenAudio
		// (Global.mHandlerNo, StreamType.FOSSTREAM_SUB, 2000); //0 main 1 sub
		// if(openRet == 0)
		// {
		// Log.i("jerry", "open audio  success");
		// Toast.makeText(getApplicationContext(),
		// "已开启音频", 500).show();
		// mAudioThread.init();
		// mAudioThread.start();
		// Global.isAudioOpenOrNot = true;
		// }
		// }else{
		// audio.setText("开始音频");
		// int audioRet = FosSdkJNI.CloseAudio(Global.mHandlerNo, 2000);
		// if(audioRet == 0)
		// {
		// Toast.makeText(getApplicationContext(),
		// "已关闭音频", 500).show();
		// mAudioThread.StopRun();
		// Global.isAudioOpenOrNot = false;
		// }
		// }
		// }

		// break;
		case R.id.bt_edit:
			if (inputDialog != null && !inputDialog.isShowing()) {
				inputDialog.show();
			}
			break;
		case R.id.ll_back:
			onBackPressed();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_back:
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;

		case R.id.tv_talk:
			if (Global.isReceiveData) {
				if (!isTalkOpen) {
					int show = FosSdkJNI.OpenTalk(Global.mHandlerNo, 2000);
					talk.setText("停止对讲");
					if (mTalkThread == null) {
						mTalkThread = new TalkThread();
						mTalkThread.isRunTalk = true;
						mTalkThread.init();
						mTalkThread.start();

					}
					mTalkThread.startTalk();
					// 如果这一句在talkThrea创建前调用，会把程序的按键声传递过去
					isTalkOpen = true;
				} else {
					talk.setText("开始对讲");
					if (mTalkThread != null) {
						mTalkThread.isRunTalk = false;
						mTalkThread.StopTalk();
						mTalkThread = null;
						isTalkOpen = false;
					}
					FosSdkJNI.CloseTalk(Global.mHandlerNo, 2000);
				}
			}

			break;
		case R.id.tv_record: // 录像模块
			if (Global.isReceiveData) {
				if (!Global.isRecordOrNot) {

					new Thread(new Runnable() {

						@Override
						public void run() {
							String SDPATH = Environment
									.getExternalStorageDirectory().toString();
							String filepath = SDPATH + "/anji/" + UID
									+ "/Video";
							File file = new File(filepath);
							if (!file.exists()) {
								file.mkdirs();
							}
							String fileName = System.currentTimeMillis()
									+ ".mp4";

							// FSApi.StartRecord(filepath + "/", fileName, 0);
							LogUtil.LogI(Tag, "filepath=" + filepath);
							LogUtil.LogI(Tag, "fileName=" + fileName);
							LogUtil.LogI(Tag, "fileName+name=" + filepath
									+ "//" + fileName);
							int recordRet = FosSdkJNI.StartRecord(
									Global.mHandlerNo, 1, filepath + "/"
											+ fileName);
							// "//sdcard//DCIM//record.mp4");
							Log.i("jerry", "recordRet===" + recordRet);
						}
					}).start();
					recode.setText("停止录像");
					Global.isRecordOrNot = true;
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							FosSdkJNI.StopRecord(Global.mHandlerNo);
						}
					}).start();
					recode.setText("开始录像");
					Global.isRecordOrNot = false;
				}
			}
			break;
		case R.id.tv_snap:
			if (Global.isReceiveData) {
				// int ret = FosSdkJNI.NetSnapPicture(Global.mHandlerNo, 3000,
				// "//sdcard//DCIM//Camera//mpeg.jpeg");
				int ret = FosSdkJNI.NetSnap(Global.mHandlerNo, 3000, snapData,
						dataLen);
				if (ret == 0) {
					Toast.makeText(getApplicationContext(), "截取图片成功", 500)
							.show();
					FileOutputStream out = null;
					try {
						String picSaveDir = Environment
								.getExternalStorageDirectory().getPath()
								+ "/anji/" + UID + "/snap";
						File file = new File(picSaveDir);
						if (!file.exists()) {
							file.mkdirs();
						}
						File f = new File(picSaveDir + "/"
								+ System.currentTimeMillis() + ".jpeg");

						out = new FileOutputStream(f);
						// out = new FileOutputStream(new File(
						// "//sdcard//DCIM//Camera//mpeg.jpeg"));
						try {
							out.write(snapData);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}

			}
			break;
			
		case R.id.tv_listen:
//			if (Global.isReceiveData) {
//				if (!Global.isAudioOpenOrNot) {
//					tv_listen.setText("停止音频");
//					int openRet = FosSdkJNI.OpenAudio(Global.mHandlerNo,
//							StreamType.FOSSTREAM_SUB, 2000); // 0 main 1 sub
//					if (openRet == 0) {
//						Log.i("jerry", "open audio  success");
//						Toast.makeText(getApplicationContext(), "已开启音频", 500)
//								.show();
//						mAudioThread.init();
//						mAudioThread.start();
//						Global.isAudioOpenOrNot = true;
//					}
//				} else {
//					tv_listen.setText("开始音频");
//					int audioRet = FosSdkJNI
//							.CloseAudio(Global.mHandlerNo, 2000);
//					if (audioRet == 0) {
//						Toast.makeText(getApplicationContext(), "已关闭音频", 500)
//								.show();
//						mAudioThread.StopRun();
//						Global.isAudioOpenOrNot = false;
//					}
//				}
//			}
			break;

		case R.id.btn_up:
			if (Global.isReceiveData) {
				FosSdkJNI.PtzCmd(Global.mHandlerNo, 0, 500);
				ptzHandler.removeCallbacks(ptzStopRunnable);
				ptzHandler.postDelayed(ptzStopRunnable, 1000);
			}
			break;
		// case R.id.right_up_btn:
		// if(Global.isReceiveData){
		//
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 6, 1000);
		// }
		// break;
		case R.id.btn_left:
			if (Global.isReceiveData) {

				FosSdkJNI.PtzCmd(Global.mHandlerNo, 2, 500);
				ptzHandler.removeCallbacks(ptzStopRunnable);
				ptzHandler.postDelayed(ptzStopRunnable, 1000);
			}
			break;
		// case R.id.reset_btn:
		// if(Global.isReceiveData){
		//
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 8, 1000);
		// }
		// break;
		case R.id.btn_right:
			if (Global.isReceiveData) {

				FosSdkJNI.PtzCmd(Global.mHandlerNo, 3, 500);
				ptzHandler.removeCallbacks(ptzStopRunnable);
				ptzHandler.postDelayed(ptzStopRunnable, 1000);
			}
			break;
		// case R.id.left_down_btn:
		// if(Global.isReceiveData){
		//
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 5, 1000);
		// }
		// break;
		case R.id.btn_down:
			if (Global.isReceiveData) {

				FosSdkJNI.PtzCmd(Global.mHandlerNo, 1, 500);
				ptzHandler.removeCallbacks(ptzStopRunnable);
				ptzHandler.postDelayed(ptzStopRunnable, 1000);
			}
			break;
		// case R.id.right_down_btn:
		// if(Global.isReceiveData){
		//
		// FosSdkJNI.PtzCmd(Global.mHandlerNo, 7, 1000);
		// }
		// break;
		case R.id.btn_stop:
			if (Global.isReceiveData) {
				LogUtil.LogI(Tag, "btn_stop");
				FosSdkJNI.PtzCmd(Global.mHandlerNo, 9, 500);
			}
			break;
		// case R.id.confirm_btn:
		//
		// if(Global.isReceiveData){
		//
		// editText =(EditText)findViewById(R.id.input_edit);
		// String nodeName = editText.getText().toString();
		// Log.i("jerry", "node name = "+nodeName);
		// if(nodeName.length() <= 0 )
		// {
		// Toast.makeText(getApplicationContext(),
		// "预置点名字不能为空", 500).show();
		// }else{
		// ResetPointList plist = new ResetPointList();
		// int ret = FosSdkJNI.PTZAddPresetPoint(Global.mHandlerNo, nodeName,
		// 2000, plist);
		// if(ret == 0)
		// {
		// Toast.makeText(getApplicationContext(),
		// "添加预置点成功", 500).show();
		// }else{
		// Toast.makeText(getApplicationContext(),
		// "添加失败，错误码："+ ret, 500).show();
		// }
		// }
		// }
		// break;
		// case R.id.goto_btn:
		// if(Global.isReceiveData){
		// editText =(EditText)findViewById(R.id.input_edit);
		// String nodeName = editText.getText().toString();
		// if(nodeName.length() <= 0 )
		// {
		// Toast.makeText(getApplicationContext(),
		// "预置点不存在", 500).show();
		// break;
		// }
		// ResetPointList PointList = new ResetPointList();
		// int ret = FosSdkJNI.PTZGoToPresetPoint(Global.mHandlerNo, nodeName,
		// 2000);
		// }
		// break;
		}
	}

	private void cancelCamera() {
		LogUtil.LogI(Tag, "cancelCamera");
		LogUtil.LogI(Tag, "isTalkOpen  ==" + isTalkOpen);
		if (isTalkOpen) {
			if (mTalkThread != null) {
				mTalkThread.isRunTalk = false;
				mTalkThread.StopTalk();
				mTalkThread = null;
				isTalkOpen = false;
			}
			isTalkOpen = false;
			LogUtil.LogI(Tag, "CloseTalk  ==");
			FosSdkJNI.CloseTalk(Global.mHandlerNo, 2000);
		}
		LogUtil.LogI(Tag, "isRecordOrNot  ==" + Global.isRecordOrNot);
		if (Global.isRecordOrNot) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					LogUtil.LogI(Tag, "StopRecord  ==");
					FosSdkJNI.StopRecord(Global.mHandlerNo);
				}
			}).start();
			// recode.setText("开始录像");
			Global.isRecordOrNot = false;
		}
	}

	/**
	 * 输入账号密码输入框
	 */
	private void initInputDialog() {
		inputDialog = new Dialog(this, R.style.MyDialogStyle);
		inputDialog.setContentView(R.layout.alert_camera_dialog2);
		inputDialog.setCancelable(false);
		et_username = (TextView) inputDialog.findViewById(R.id.et_username);
		et_password = (EditText) inputDialog.findViewById(R.id.et_password);
		et_username.setText(LOGNAME);
		Button bt_sure = (Button) inputDialog.findViewById(R.id.bt_sure);
		Button bt_cancel = (Button) inputDialog.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				inputDialog.dismiss();
			}
		});
		bt_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 点击确定
				inputDialog.dismiss();
				if (isLoginSucess) {
					newPass = et_password.getText().toString().trim();
					int result = FosSdkJNI.ChangePassword(Global.mHandlerNo,
							1500, LOGNAME, LOGPD, newPass);
					LogUtil.LogI(Tag, "result==" + result);
					if (result == 0) {
						ToastUtils.show(CameraNew.this,
								getString(R.string.edit_device_sucess));
						TabCamera.isEditPass = true;
						startEditGroup();
					} else {
						ToastUtils.show(CameraNew.this,
								getString(R.string.edit_device_fail));
					}
				}
			}
		});
	}

	/**
	 * 控制红外推送总开关
	 * 
	 * @param isOpen
	 */
	public void startEditGroup() {
		if (progressDialog != null && !progressDialog.isShowing()) {
			progressDialog.show();
		}
		editGroupTask = new EditGroupTask();
		editGroupTask.execute();
	}

	private class EditGroupTask extends AsyncTask<Void, Object, ResponseBase> {
		ResponseBase responseBase;

		@Override
		protected ResponseBase doInBackground(Void... params) {

			if (MyApplication.member != null) {
				responseBase = NetReq.editCameraPass(cameraId, LOGNAME,
						MyApplication.member.getSessionId(),
						MyApplication.member.getSsuid(), newPass);
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
				 * 200：成功 300：系统异常 401：cameraId不能为空 402：username不能为空
				 * 403：sessionId不能为空 404：ssuid不能为空 405：会话无效 406：摄像头已删除
				 * 407：uid已存在
				 */
				if (responseBase.getResponseStatus() == 200) {
					// 控制成功
				} else if (responseBase.getResponseStatus() == 300) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				} else if (responseBase.getResponseStatus() == 300) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.system_error));
				} else if (responseBase.getResponseStatus() == 401) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.camare_id_null));
				} else if (responseBase.getResponseStatus() == 402) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.name_null));
				} else if (responseBase.getResponseStatus() == 403) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionId_null));
				} else if (responseBase.getResponseStatus() == 404) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.ssuid_null));
				} else if (responseBase.getResponseStatus() == 405) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.sessionID_not_work));
				} else if (responseBase.getResponseStatus() == 406) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.camera_had_delete));
				} else if (responseBase.getResponseStatus() == 407) {

					ToastUtils.show(mContext,
							mContext.getString(R.string.uid_null));
				}
			} else {
				// 网络请求失败
			}
		}
	}

}
