package com.anji.www.activity;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.anji.www.R;
import com.anji.www.adapter.CameraListAdapter;
import com.anji.www.adapter.SwitchListAdapter.MyClickListener;
import com.anji.www.db.DatabaseHelper;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.fos.sdk.FosDiscovery_Node;
import com.fos.sdk.FosSdkJNI;
import com.ipc.sdk.DevInfo;
import com.ipc.sdk.FSApi;
import com.ipc.sdk.StatusListener;
import com.remote.util.IPCameraInfo;
import com.remote.util.MyStatusListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract.Helpers;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 摄像头页面
 * 
 * @author Ivan
 */
public class TabCamera extends Fragment implements OnClickListener,
		BaseFragment {

	private static final String TAG = TabCamera.class.getName();
	MainActivity activity;
	private LinearLayout ll_camare;
	private Button bt_camare_search;
	private Button bt_more_camera;
	// private Button bt_browse;
	private TextView tv_camera_edit;
	private TextView tv_browse;
	private TextView tv_add;
	private ListView lv_camare;
	private Dialog progressDialog;
	private DevInfo[] deviceSearched;
	private CameraListAdapter cameraListAdapter;
	private List<IPCameraInfo> saveList;
	private List<IPCameraInfo> showList;
	private List<IPCameraInfo> localList;
	private Button bt_sure;
	private EditText et_username;
	private EditText et_password;
	private Dialog inputDialog;
	private String userName;
	private String password;
	private IPCameraInfo currentInfo;
	private boolean isStop;// 是否要停止数据监控
	private static final int UPDATE = 100;
	private DatabaseHelper dbHelp;
	private boolean isEditState;
	public static boolean isNeedRefresh;
	private boolean isLoginCamera;
	private boolean isFirstResume;

	private PopupWindow popupWindow;
	private View popupView;
	public static boolean isEditPass;

	public Handler loginListener = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.arg1) {
			case StatusListener.STATUS_LOGIN_SUCCESS:
				LogUtil.LogI(TAG, "STATUS_LOGIN_SUCCESS");
				// // 登陆成功
				// // 更新数据库
				// try
				// {
				// if (currentInfo != null && isLoginCamera)
				// {
				//
				// new Thread()
				// {
				// public void run()
				// {
				// if (TextUtils.isEmpty(currentInfo.userName)
				// || TextUtils
				// .isEmpty(currentInfo.password))
				// {
				// if (!TextUtils.isEmpty(userName)
				// && !TextUtils.isEmpty(password))
				// {
				// currentInfo.userName = userName;
				// currentInfo.password = password;
				// ContentValues contentValue = new ContentValues();
				//
				// contentValue.put("devType",
				// currentInfo.devType);
				// contentValue.put("devName",
				// currentInfo.devName);
				// contentValue.put("ip", currentInfo.ip);
				// contentValue.put("streamType",
				// currentInfo.streamType);
				// contentValue.put("webPort",
				// currentInfo.webPort);
				// contentValue.put("mediaPort",
				// currentInfo.mediaPort);
				// contentValue
				// .put("uid", currentInfo.uid);
				// contentValue.put("userName",
				// currentInfo.userName);
				// contentValue.put("password",
				// currentInfo.password);
				// LogUtil.LogI(TAG, "currentInfo.id="
				// + currentInfo.id);
				//
				// try
				// {
				// LogUtil.LogI(TAG, "currentInfo.id="
				// + currentInfo.id);
				// dbHelp.update(activity,
				// "tb_device_list",
				// contentValue,
				// currentInfo.id);
				// }
				// catch (Exception e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				//
				// }
				//
				// };
				// }.start();
				//
				// // TODO 编辑摄像头
				//
				// Intent intent = new Intent(activity,
				// CameraActivity.class);
				// intent.putExtra("uid", currentInfo.uid);
				// activity.startActivity(intent);
				// }
				// }
				// catch (Exception e)
				// {
				// Log.e("moon", e.getMessage(), e);
				// }
				break;
			case StatusListener.STATUS_LOGIN_FAIL_USR_PWD_ERROR:
				ToastUtils.show(activity,
						getString(R.string.camera_login_error));
				LogUtil.LogI(TAG, "STATUS_LOGIN_FAIL_USR_PWD_ERROR ");
				break;
			case StatusListener.STATUS_LOGIN_FAIL_ACCESS_DENY:
				ToastUtils.show(activity,
						getString(R.string.camera_login_error2));
				LogUtil.LogI(TAG, "loginListener 1001");
				break;
			case StatusListener.STATUS_LOGIN_FAIL_EXCEED_MAX_USER:
				ToastUtils.show(activity,
						getString(R.string.camera_login_error3));
				LogUtil.LogI(TAG, "loginListener 1001");
				break;
			case StatusListener.STATUS_LOGIN_FAIL_CONNECT_FAIL:
				ToastUtils.show(activity,
						getString(R.string.camera_login_error4));
				LogUtil.LogI(TAG, "loginListener 1001");
				break;
			case UPDATE:
				// showList = MainActivity.cameraList;
				updataCameraListInfo();
				cameraListAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};

	private Handler cameraHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				if (saveList != null && showList != null) {

					// TODO 查询本地保存的设备完毕
					for (int i = 0; i < saveList.size(); i++) {
						IPCameraInfo info1 = saveList.get(i);
						for (int j = 0; j < showList.size(); j++) {
							IPCameraInfo info2 = showList.get(i);
							if (info1.uid.equals(info2.uid)) {
								info2.id = info1.id;
							}
						}
					}
				}
			}
		};
	};
	private DeleteDeviceTask deleteTask;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		System.out.println("TabCamera____onAttach");
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// db = new DbTool(getActivity());
		dbHelp = new DatabaseHelper(activity);
		isFirstResume = true;
		isEditPass = false;
		System.out.println("TabCamera____onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("TabCamera____onCreateView");
		return inflater.inflate(R.layout.tab_camare, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showList = new ArrayList<IPCameraInfo>();
		localList = new ArrayList<IPCameraInfo>();
		updataCameraListInfo();
		initData();
		initView();
		isStop = true;
		// MyStatusListener.setMyHandler(loginListener);
		// final MyStatusListener statusListener = new MyStatusListener();
		//
		// FSApi.setStatusListener(statusListener);
		// new Thread(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// int id;
		// int StatusID;
		// while (isStop)
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

	private void initView() {
		initInputDialog();
		initPopUpWindow();
		bt_more_camera = (Button) activity.findViewById(R.id.bt_more_camera);
		bt_more_camera.setOnClickListener(this);
		progressDialog = DisplayUtils.createDialog(activity, R.string.search);
		progressDialog.setCancelable(true);
		ll_camare = (LinearLayout) activity.findViewById(R.id.ll_camare);
		bt_camare_search = (Button) activity
				.findViewById(R.id.bt_camare_search);
		// bt_browse = (Button) activity.findViewById(R.id.bt_browse);
		lv_camare = (ListView) activity.findViewById(R.id.lv_camare);

		bt_camare_search.setOnClickListener(this);
		// bt_browse.setOnClickListener(this);
		cameraListAdapter = new CameraListAdapter(activity, showList, dbHelp,
				mListener);
		lv_camare.setAdapter(cameraListAdapter);
		lv_camare.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				final IPCameraInfo ipcInfo = showList.get(position);
				currentInfo = ipcInfo;
				if (isEditState) {
					if (ipcInfo.cameraId == 0) {
						ToastUtils.show(activity,
								getString(R.string.camera_no_upload));
					} else {
						Intent intent = new Intent(activity,
								EditDeviceActivity.class);
						intent.putExtra("deviceType", 2);
						intent.putExtra("deviceId", ipcInfo.cameraId);
						intent.putExtra("groupId", ipcInfo.groupId);
						intent.putExtra("deviceName", ipcInfo.devName);

						activity.startActivity(intent);
					}
				} else {
					isLoginCamera = true;

					if (TextUtils.isEmpty(showList.get(position).userName)) {
						if (inputDialog != null && !inputDialog.isShowing()) {

							inputDialog.show();
							bt_sure.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									inputDialog.dismiss();

									userName = et_username.getText().toString();
									password = et_password.getText().toString();
									currentInfo.userName = userName;
									currentInfo.password = password;
									if ("".equals(userName)) {
										// 提示用户名不能为空
										Toast.makeText(activity,
												"User name empty",
												Toast.LENGTH_LONG).show();
									} else {
										currentInfo = ipcInfo;
										// currentInfo.userName = userName;
										// currentInfo.password = password;

										// // 保存配置
										// SharedPreferences sharedPreferences =
										// activity
										// .getSharedPreferences(
										// "CONNECT_DEV_INFO", 0);
										// SharedPreferences.Editor editor =
										// sharedPreferences
										// .edit();
										// editor.putInt("DEV_TYPE",
										// ipcInfo.devType);
										// editor.putString("DEV_NAME",
										// ipcInfo.devName);
										// editor.putString("IP", ipcInfo.ip);
										// editor.putInt("STREAM_TYPE",
										// ipcInfo.streamType);
										// editor.putInt("WEB_PORT",
										// ipcInfo.webPort);
										// editor.putInt("MEDIA_PORT",
										// ipcInfo.mediaPort);
										// editor.putString("USER_NAME",
										// userName);
										// editor.putString("PASSWORD",
										// password);
										// editor.putString("UID", ipcInfo.uid);
										// editor.commit();
										Intent it = new Intent(activity,
												CameraNew.class);

										Bundle bundle = new Bundle();
										/* 字符、字符串、布尔、字节数组、浮点数等等，都可以传 */

										bundle.putString("ip", "");
										bundle.putString("uid", ipcInfo.uid);
										bundle.putString("cameraId",
												ipcInfo.cameraId + "");
										bundle.putString("uname", userName);
										bundle.putString("pwd", password);
										bundle.putShort("port",
												Short.parseShort("88"));
										bundle.putInt("type", ipcInfo.devType);
										/* 把bundle对象assign给Intent */
										it.putExtras(bundle);
										startActivity(it);

										//
										// dialog.dismiss();
										//
										// // 跳转到LiveView页面，开始连接视频
										// setResult(Activity.RESULT_OK);
										//
										// DeviceList.this.finish();
										//
										// overridePendingTransition(R.anim.zoomin,
										// R.anim.zoomout);
										// 登陆
										// FSApi.usrLogOut(0);
										// LogUtil.LogI(TAG, "ipcInfo.devType="
										// + ipcInfo.devType);
										// LogUtil.LogI(TAG, "userName="
										// + userName);
										// LogUtil.LogI(TAG, "password="
										// + password);
										// LogUtil.LogI(TAG,
										// "ipcInfo.streamType="
										// + ipcInfo.streamType);
										// LogUtil.LogI(TAG, "ipcInfo.webPort="
										// + ipcInfo.webPort);
										// LogUtil.LogI(TAG,
										// "ipcInfo.mediaPort="
										// + ipcInfo.mediaPort);
										// LogUtil.LogI(TAG, "ipcInfo.uid="
										// + ipcInfo.uid);
										// FSApi.usrLogIn(ipcInfo.devType, 0 +
										// "",
										// userName, password,
										// ipcInfo.streamType,
										// ipcInfo.webPort,
										// ipcInfo.mediaPort, ipcInfo.uid,
										// 0);
									}

								}
							});
						}
					} else {
						// FSApi.usrLogOut(0);
						LogUtil.LogI(TAG, "ipcInfo.devType=" + ipcInfo.devType);
						LogUtil.LogI(TAG, "userName=" + ipcInfo.userName.trim());
						LogUtil.LogI(TAG, "password=" + ipcInfo.password.trim());
						// LogUtil.LogI(TAG, "ipcInfo.streamType="
						// + ipcInfo.streamType);
						LogUtil.LogI(TAG, "ipcInfo.webPort=" + ipcInfo.webPort);
						// LogUtil.LogI(TAG, "ipcInfo.mediaPort="
						// + ipcInfo.mediaPort);
						LogUtil.LogI(TAG, "ipcInfo.uid=" + ipcInfo.uid);
						// FSApi.usrLogIn(ipcInfo.devType, "0",
						// ipcInfo.userName.trim(),
						// ipcInfo.password.trim(), ipcInfo.streamType,
						// ipcInfo.webPort, ipcInfo.mediaPort,
						// ipcInfo.uid, 0);

						Intent it = new Intent(activity, CameraNew.class);

						Bundle bundle = new Bundle();
						/* 字符、字符串、布尔、字节数组、浮点数等等，都可以传 */

						bundle.putString("ip", "");
						bundle.putString("uid", ipcInfo.uid);
						bundle.putString("uname", ipcInfo.userName.trim());
						bundle.putString("cameraId", ipcInfo.cameraId + "");
						bundle.putString("pwd", ipcInfo.password.trim());
						bundle.putShort("port", (short) ipcInfo.webPort);
						bundle.putInt("type", ipcInfo.devType);
						/* 把bundle对象assign给Intent */
						it.putExtras(bundle);
						startActivity(it);
					}
					// }
				}
			}
		});

		// lv_camare.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id)
		// {
		// if (showList != null && showList.size() >= position)
		// {
		// startDeleteDevice(showList.get(position));
		// }
		// return true;
		// }
		// });

	}

	private void initData() {
		new Thread() {
			public void run() {
				try {
					saveList = dbHelp.qurryAll(activity);
					cameraHandler.obtainMessage(0).sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};

		}.start();
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("TabCamera____onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		isLoginCamera = false;
		// if (isNeedRefresh)
		// {
		// isNeedRefresh = false;
		if (!isFirstResume || isEditPass) {
			activity.startQurryCamera();
			if (isEditPass) {
				isEditPass = false;
			}
		}
		// }
		isFirstResume = false;

		if (isEditState) {
			isEditState = false;
			tv_camera_edit.setText(getString(R.string.edit));
		}
		cameraListAdapter.setEditState(isEditState);

		isStop = true;
		// MyStatusListener.setMyHandler(loginListener);
		// final MyStatusListener statusListener = new MyStatusListener();
		//
		// FSApi.setStatusListener(statusListener);
		// new Thread(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// int id;
		// int StatusID;
		// while (isStop)
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
		System.out.println("TabCamera____onResume");
		refreshView();
		// if (showList != null && showList.size() >0)
		// {
		// ll_camare.setVisibility(View.VISIBLE);
		// }else {
		// ll_camare.setVisibility(View.GONE);
		// }

	}

	@Override
	public void onPause() {
		super.onPause();
		if (!isFirstResume) {
			activity.cancelQurryCamera();
		}

		System.out.println("TabCamera____onPause");
	}

	@Override
	public void onStop() {
		super.onStop();

		System.out.println("TabCamera____onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		System.out.println("TabCamera____onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (null != dbHelp) {
			dbHelp.close();
		}
		isStop = false;
		System.out.println("TabCamera____onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		System.out.println("TabCamera____onDetach");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent;
		switch (id) {
		case R.id.bt_more_camera:
			if (!popupWindow.isShowing()) {
				popupWindow.showAsDropDown(bt_more_camera);
			}
			break;
		case R.id.tv_browse:
			// TODO 浏览
			intent = new Intent(activity, FileDirActiviy.class);
			activity.startActivity(intent);
			break;
		case R.id.tv_add:
			// TODO 添加摄像头
			intent = new Intent(activity, EzlinkAddActivity.class);
			activity.startActivity(intent);
			break;
		case R.id.tv_camera_edit:
			if (isEditState) {
				isEditState = false;
				tv_camera_edit.setText(getString(R.string.edit));
			} 
			else {
				isEditState = true;
				tv_camera_edit.setText(getString(R.string.finish));
			}
			cameraListAdapter.setEditState(isEditState);
			break;
		// case R.id.bt_browse:
		// // TODO 浏览
		// Intent intent = new Intent(activity, FileDirActiviy.class);
		// activity.startActivity(intent);
		// break;
		case R.id.bt_camare_search:
			// TODO 搜索摄像头
			if (isEditState) {
				isEditState = false;
				tv_camera_edit.setText(getString(R.string.edit));
			} 
//			else {
//				isEditState = true;
//				tv_camera_edit.setText(getString(R.string.finish));
//			}
			cameraListAdapter.setEditState(isEditState);
			isLoginCamera = false;
			// 搜索设备，5s后获取设备列表
			activity.startQurryCamera();
			// showList = MainActivity.cameraList;
			MainActivity.cameraList.clear();
			showList.clear();
			localList.clear();
			// FSApi.searchDev();
			//
			// if (progressDialog != null && !progressDialog.isShowing())
			// {
			// progressDialog.show();
			// }
			// new Handler().postDelayed(new Runnable()
			// {
			//
			// @Override
			// public void run()
			// {
			// // TODO Auto-generated method stub
			//
			// getDeviceList();
			// progressDialog.dismiss();
			// }
			// }, 5000);

			FosDiscovery_Node[] node2;
			Integer ct2 = -1;
			node2 = FosSdkJNI.Discovery(ct2, 500);
			if (ct2 > 0) {
				for (int j = 0; j < ct2; j++) {
					Log.i("jerry", "ccccct22222=====:" + ct2);
					Log.i("jerry", "node22222222[0]=====ip:" + node2[j].ip);
					Log.i("jerry", "node22222222[0]=====name:" + node2[j].name);
					Log.i("jerry", "node22222222[0]=====uid:" + node2[j].uid);
					Log.i("jerry", "node22222222[0]=====web port:"
							+ node2[j].port);
					Log.i("jerry", "node22222222[0]=====type:" + node2[j].type);
					Log.i("jerry", "node22222222[0]=====media Port:"
							+ node2[j].mediaPort);
				}
			}
			getSeardDeviceList(ct2, node2);
			break;

		default:
			break;
		}
	}

	private void getDeviceList() {
		// deviceSearched = FSApi.getDevList();

		// String[] devices = new String[deviceSearched.length];
		// boolean[] status = new boolean[deviceSearched.length];
		// showList.clear();
		// if (saveList != null && saveList.size() > 0)
		// {
		// showList.addAll(saveList);
		// }
		for (int i = 0; i < showList.size(); i++) {
			showList.get(i).isOnLine = false;
		}

		if (deviceSearched.length > 0) {
			// int i = 0;
			// for( i = 0; i < deviceSearched.length; i++ )
			// {
			// if( !"".equals(deviceSearched[i].uid))
			// {
			// devices[i] =
			// deviceSearched[i].devName+"("+deviceSearched[i].uid+")";
			// }
			// else
			// {
			// devices[i] =
			// deviceSearched[i].devName+"("+deviceSearched[i].ip+")";
			// }
			// status[i] = false;
			// }

			// 扫描所有的列表项
			int existingCnt = 0;
			for (int i = 0; i < deviceSearched.length; i++) {
				IPCameraInfo temp = checkIsExist(showList,
						deviceSearched[i].uid);
				LogUtil.LogI(TAG, "temp是否等于空" + (temp == null));
				if (temp == null) {
					IPCameraInfo info = new IPCameraInfo();
					info.devType = deviceSearched[i].devType;
					info.devName = deviceSearched[i].devName;
					info.ip = deviceSearched[i].ip;
					info.streamType = 0;
					info.webPort = deviceSearched[i].webPort;
					info.mediaPort = deviceSearched[i].mediaPort;
					info.uid = deviceSearched[i].uid;
					info.groupId = 0;
					info.isOnLine = true;
					showList.add(info);
				} else {
					temp.isOnLine = true;
					temp.devType = deviceSearched[i].devType;
					temp.ip = deviceSearched[i].ip;
					temp.mediaPort = deviceSearched[i].mediaPort;
					temp.webPort = deviceSearched[i].webPort;
					if (!TextUtils.isEmpty(deviceSearched[i].uid)) {
						temp.uid = deviceSearched[i].uid;
					}
				}
				// 查看是否已经存在该记录
				// try
				// {
				// Cursor cursour;
				// if (!"".equals(deviceSearched[i].uid))
				// {
				// // cursour = DatabaseHelper
				// // .QueryDevice(activity, deviceSearched[i].ip,
				// // deviceSearched[i].webPort);
				// // }
				// // else
				// // {
				// cursour = dbHelp.QueryDevice(activity,
				// deviceSearched[i].uid);
				// if (cursour != null)
				// {
				// existingCnt = cursour.getCount();
				// cursour.close();
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
				// // TODO 上报到服务器
				// ContentValues contentValue = new ContentValues();
				// IPCameraInfo info = new IPCameraInfo();
				//
				// info.devType = deviceSearched[i].devType;
				// info.devName = deviceSearched[i].devName;
				// info.ip = deviceSearched[i].ip;
				// info.streamType = 0;
				// info.webPort = deviceSearched[i].webPort;
				// info.mediaPort = deviceSearched[i].mediaPort;
				// info.uid = deviceSearched[i].uid;
				// info.groupId = 0;
				// info.isOnLine = true;
				// showList.add(info);
				// LogUtil.LogI(TAG, "showList.add(info);");
				// contentValue.put("devType", deviceSearched[i].devType);
				// contentValue.put("devName", deviceSearched[i].devName);
				// contentValue.put("ip", deviceSearched[i].ip);
				// contentValue.put("streamType", 0);
				// contentValue.put("webPort", deviceSearched[i].webPort);
				// contentValue.put("mediaPort", deviceSearched[i].mediaPort);
				// contentValue.put("uid", deviceSearched[i].uid);
				// contentValue.put("devSetName", "");
				// contentValue.put("groupName", "");
				// contentValue.put("groupId", -1);// -1表示未分组
				// contentValue.put("thumPath", "");
				// try
				// {
				// dbHelp.insert(activity, "tb_device_list", contentValue);
				// // fillDataToCursor(); // 更新
				// initData();
				// }
				// catch (Exception e)
				// {
				// e.printStackTrace();
				// }
				//
				// }
				// else
				// {
				// if (showList != null && showList.size() > 0)
				// {
				// for (int j = 0; j < showList.size(); j++)
				// {
				// if (showList.get(j).devName
				// .equals(deviceSearched[i].devName)
				// && showList.get(j).uid
				// .equals(deviceSearched[i].uid))
				// {
				// showList.get(j).isOnLine = true;
				// showList.get(j).devType = deviceSearched[i].devType;
				// showList.get(j).ip = deviceSearched[i].ip;
				// showList.get(j).mediaPort = deviceSearched[i].mediaPort;
				// showList.get(j).webPort = deviceSearched[i].webPort;
				// if (!TextUtils.isEmpty(deviceSearched[i].uid))
				// {
				// showList.get(j).uid = deviceSearched[i].uid;
				// }
				// LogUtil.LogI(
				// TAG,
				// "showList.get isonline="
				// + showList.get(showList.size() - 1).isOnLine);
				// }
				// }
				// }
				// }

			}
			LogUtil.LogI(TAG, "deviceSearched.length=" + deviceSearched.length);
			LogUtil.LogI(TAG, "showList.size=" + showList.size());
			LogUtil.LogI(TAG, "saveList.size=" + saveList.size());

			cameraListAdapter.setList(showList);
			loginListener.obtainMessage(UPDATE).sendToTarget();
		}
	}

	private void getSeardDeviceList(int size, FosDiscovery_Node[] node2) {

		for (int i = 0; i < showList.size(); i++) {
			showList.get(i).isOnLine = false;
		}

		if (size > 0) {
			// int i = 0;
			// for( i = 0; i < deviceSearched.length; i++ )
			// {
			// if( !"".equals(deviceSearched[i].uid))
			// {
			// devices[i] =
			// deviceSearched[i].devName+"("+deviceSearched[i].uid+")";
			// }
			// else
			// {
			// devices[i] =
			// deviceSearched[i].devName+"("+deviceSearched[i].ip+")";
			// }
			// status[i] = false;
			// }

			// 扫描所有的列表项
			for (int i = 0; i < size; i++) {
				IPCameraInfo temp = checkIsExist(showList, node2[i].uid);
				LogUtil.LogI(TAG, "temp是否等于空" + (temp == null));
				if (temp == null) {
					IPCameraInfo info = new IPCameraInfo();
					info.devType = node2[i].type;
					info.devName = node2[i].name;
					info.ip = node2[i].ip;
					info.streamType = 0;
					info.webPort = node2[i].port;
					info.mediaPort = node2[i].mediaPort;
					info.uid = node2[i].uid;
					info.groupId = 0;
					info.isOnLine = true;
					showList.add(info);
				} else {
					temp.isOnLine = true;
					temp.devType = node2[i].type;
					temp.ip = node2[i].ip;
					temp.mediaPort = node2[i].mediaPort;
					temp.webPort = node2[i].port;
					if (!TextUtils.isEmpty(node2[i].uid)) {
						temp.uid = node2[i].uid;
					}
				}
				// 查看是否已经存在该记录
				// try
				// {
				// Cursor cursour;
				// if (!"".equals(deviceSearched[i].uid))
				// {
				// // cursour = DatabaseHelper
				// // .QueryDevice(activity, deviceSearched[i].ip,
				// // deviceSearched[i].webPort);
				// // }
				// // else
				// // {
				// cursour = dbHelp.QueryDevice(activity,
				// deviceSearched[i].uid);
				// if (cursour != null)
				// {
				// existingCnt = cursour.getCount();
				// cursour.close();
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
				// // TODO 上报到服务器
				// ContentValues contentValue = new ContentValues();
				// IPCameraInfo info = new IPCameraInfo();
				//
				// info.devType = deviceSearched[i].devType;
				// info.devName = deviceSearched[i].devName;
				// info.ip = deviceSearched[i].ip;
				// info.streamType = 0;
				// info.webPort = deviceSearched[i].webPort;
				// info.mediaPort = deviceSearched[i].mediaPort;
				// info.uid = deviceSearched[i].uid;
				// info.groupId = 0;
				// info.isOnLine = true;
				// showList.add(info);
				// LogUtil.LogI(TAG, "showList.add(info);");
				// contentValue.put("devType", deviceSearched[i].devType);
				// contentValue.put("devName", deviceSearched[i].devName);
				// contentValue.put("ip", deviceSearched[i].ip);
				// contentValue.put("streamType", 0);
				// contentValue.put("webPort", deviceSearched[i].webPort);
				// contentValue.put("mediaPort", deviceSearched[i].mediaPort);
				// contentValue.put("uid", deviceSearched[i].uid);
				// contentValue.put("devSetName", "");
				// contentValue.put("groupName", "");
				// contentValue.put("groupId", -1);// -1表示未分组
				// contentValue.put("thumPath", "");
				// try
				// {
				// dbHelp.insert(activity, "tb_device_list", contentValue);
				// // fillDataToCursor(); // 更新
				// initData();
				// }
				// catch (Exception e)
				// {
				// e.printStackTrace();
				// }
				//
				// }
				// else
				// {
				// if (showList != null && showList.size() > 0)
				// {
				// for (int j = 0; j < showList.size(); j++)
				// {
				// if (showList.get(j).devName
				// .equals(deviceSearched[i].devName)
				// && showList.get(j).uid
				// .equals(deviceSearched[i].uid))
				// {
				// showList.get(j).isOnLine = true;
				// showList.get(j).devType = deviceSearched[i].devType;
				// showList.get(j).ip = deviceSearched[i].ip;
				// showList.get(j).mediaPort = deviceSearched[i].mediaPort;
				// showList.get(j).webPort = deviceSearched[i].webPort;
				// if (!TextUtils.isEmpty(deviceSearched[i].uid))
				// {
				// showList.get(j).uid = deviceSearched[i].uid;
				// }
				// LogUtil.LogI(
				// TAG,
				// "showList.get isonline="
				// + showList.get(showList.size() - 1).isOnLine);
				// }
				// }
				// }
				// }

			}
			LogUtil.LogI(TAG, "node2.length=" + node2.length);
			LogUtil.LogI(TAG, "showList.size=" + showList.size());
			LogUtil.LogI(TAG, "saveList.size=" + saveList.size());

			cameraListAdapter.setList(showList);
			loginListener.obtainMessage(UPDATE).sendToTarget();
		}
	}

	private IPCameraInfo checkIsExist(List<IPCameraInfo> showList, String uid) {
		IPCameraInfo info = null;
		for (int i = 0; i < showList.size(); i++) {
			LogUtil.LogI(TAG, "showList.get(i).uid=" + showList.get(i).uid);
			LogUtil.LogI(TAG, "uid=" + uid);
			if (showList.get(i).uid.equals(uid)) {
				info = showList.get(i);
			}
		}
		return info;
	}

	private DevInfo checkIsExist(DevInfo[] deviceSearched, String uid) {
		DevInfo device = null;
		for (int i = 0; i < deviceSearched.length; i++) {
			if (deviceSearched[i].uid.equals(uid)) {
				device = deviceSearched[i];
			}
		}
		return device;
	}

	@Override
	public void refreshView() {
		updataCameraListInfo();
		if (cameraListAdapter != null) {
			cameraListAdapter.setList(showList);
		}

	}

	public void refreshListView() {
		// showList = MainActivity.cameraList;

		updataCameraListInfo();
		if (cameraListAdapter != null) {
			cameraListAdapter.setList(showList);
		}

	}

	private void updataCameraListInfo() {
		if (MainActivity.cameraList != null && showList != null
				&& MainActivity.cameraList.size() > 0) {

			// if (showList != null
			// && checkIsExist(showList,
			// MainActivity.cameraList.get(0).uid) == null) {
			// showList.addAll(MainActivity.cameraList);
			// }

			for (int i = 0; i < MainActivity.cameraList.size(); i++) {
				IPCameraInfo info = checkIsExist(showList,
						MainActivity.cameraList.get(i).uid);
				if (info != null) {

					showList.remove(info);
					showList.add(MainActivity.cameraList.get(i));
				}else {
					showList.add(MainActivity.cameraList.get(i));
				}
			}
		}
	}

	/**
	 * 输入账号密码输入框
	 */
	private void initInputDialog() {
		inputDialog = new Dialog(activity, R.style.MyDialogStyle);
		inputDialog.setContentView(R.layout.alert_camera_dialog);
		inputDialog.setCancelable(false);
		et_username = (EditText) inputDialog.findViewById(R.id.et_username);
		et_password = (EditText) inputDialog.findViewById(R.id.et_password);

		bt_sure = (Button) inputDialog.findViewById(R.id.bt_sure);
		Button bt_cancel = (Button) inputDialog.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				inputDialog.dismiss();
			}
		});
	}

	/**
	 * 删除设备
	 * 
	 * @param switchInfo
	 */
	public void startDeleteDevice(IPCameraInfo cameraInfo) {
		if (progressDialog != null && !progressDialog.isShowing()) {
			progressDialog.show();
		}
		deleteTask = new DeleteDeviceTask();
		deleteTask.execute(cameraInfo);
	}

	private class DeleteDeviceTask extends
			AsyncTask<IPCameraInfo, Object, ResponseBase> {
		ResponseBase responseBase;
		IPCameraInfo info;

		@Override
		protected ResponseBase doInBackground(IPCameraInfo... params) {
			info = params[0];

			if (info != null) {
				responseBase = NetReq.deleteDevice(
						MyApplication.member.getSsuid(), info.cameraId + "",
						3 + "");
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
				 * 200：成功 300：系统异常 401：设备类型为空或取值返回不正确（取值范围1、2、3）
				 * 402：deviceId不能为空 403：ssuid参数不能为空 404：不存在ssuid网关
				 */
				if (responseBase.getResponseStatus() == 200) {
					// 控制成功

					showList.remove(info);
					if (MainActivity.cameraList.indexOf(info) != -1) {
						MainActivity.cameraList.remove(info);
					}

					try {
						dbHelp.deleteByCameraId(activity, info.cameraId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cameraListAdapter.notifyDataSetChanged();
					ToastUtils.show(activity,
							activity.getString(R.string.delete_device_sucess));
				} else if (responseBase.getResponseStatus() == 201) {
					ToastUtils.show(activity,
							activity.getString(R.string.operation_failed));
				} else if (responseBase.getResponseStatus() == 300) {
					ToastUtils.show(activity,
							activity.getString(R.string.system_error));
				} else if (responseBase.getResponseStatus() == 401) {
					ToastUtils.show(activity,
							activity.getString(R.string.devicetype_error));
				} else if (responseBase.getResponseStatus() == 402) {
					ToastUtils.show(activity,
							activity.getString(R.string.sensor_id_null));
				} else if (responseBase.getResponseStatus() == 403) {
					ToastUtils.show(activity,
							activity.getString(R.string.sessionID_null));
				} else if (responseBase.getResponseStatus() == 404) {
					ToastUtils.show(activity,
							activity.getString(R.string.ssuid_null));
				} else if (responseBase.getResponseStatus() == 405) {
					ToastUtils.show(activity,
							activity.getString(R.string.delete_outline));
				} else if (responseBase.getResponseStatus() == 406) {
					ToastUtils.show(activity,
							activity.getString(R.string.ssiu_not_up));
				} else if (responseBase.getResponseStatus() == 407) {
					ToastUtils.show(activity,
							activity.getString(R.string.ssiu_not_exist));
				}
			} else {
				// 网络请求失败
			}
		}
	}

	/**
	 * 实现类，响应按钮点击事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			// Toast.makeText(
			// MainActivity.this,
			// "listview的内部的按钮被点击了！，位置是-->" + position + ",内容是-->"
			// + contentList.get(position), Toast.LENGTH_SHORT)
			// .show();
			IPCameraInfo info = (IPCameraInfo) v.getTag(R.id.bt_delete);
			startDeleteDevice(info);
		}
	};

	private void initPopUpWindow() {
		LayoutInflater layoutInflater = (LayoutInflater) activity
				.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.pop_camera_menu, null);

		// LinearLayout llPicture = (LinearLayout) popupView
		// .findViewById(R.id.llPicture);
		// tv_popwindow_title = (TextView) popupView
		// .findViewById(R.id.tv_popwindow_title);
		// llPicture.getBackground().setAlpha(200);
		// llPicture.setOnClickListener(this);

		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
		popupWindow.update();
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0));
		tv_camera_edit = (TextView) popupView.findViewById(R.id.tv_camera_edit);
		tv_browse = (TextView) popupView.findViewById(R.id.tv_browse);
		tv_add = (TextView) popupView.findViewById(R.id.tv_add);
		tv_camera_edit.setOnClickListener(this);
		tv_browse.setOnClickListener(this);
		tv_add.setOnClickListener(this);

		// Button btnPhotograph = (Button) popupView
		// .findViewById(R.id.btnPhotograph);
		// btnPhotograph.setOnClickListener(this);
		//
		// Button btnAlbums = (Button) popupView.findViewById(R.id.btnAlbums);
		// btnAlbums.setOnClickListener(this);
		//
		// Button btnEditCancel = (Button) popupView
		// .findViewById(R.id.btnEditCancel);
		// btnEditCancel.setOnClickListener(this);
	}

}
