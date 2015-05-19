package com.anji.www.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.anji.www.R;
import com.anji.www.activity.ChangeGatewayActitivy;
import com.anji.www.activity.MainActivity;
import com.anji.www.activity.MyApplication;
import com.anji.www.adapter.SwitchListAdapter.ItemViewHolder;
import com.anji.www.adapter.SwitchListAdapter.MyClickListener;
import com.anji.www.constants.MyConstants;
import com.anji.www.db.DatabaseHelper;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.service.UdpService;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;
import com.ipc.sdk.DevInfo;
import com.remote.util.IPCameraInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CameraListAdapter extends BaseAdapter
{
	private List<IPCameraInfo> deviceList;
	private MainActivity myContext;
	private UploadCamerTask uploadCameraTask;
	private final static String TAG = "SwitchListAdapter";
	private DatabaseHelper dbHelp;
	private boolean isEditState;
	private MyClickListener listener;

	public CameraListAdapter(MainActivity myContext,
			List<IPCameraInfo> deviceList, DatabaseHelper helper,
			MyClickListener listener)
	{
		this.myContext = myContext;
		this.deviceList = deviceList;
		dbHelp = helper;
		this.listener = listener;
	}

	public void setList(List<IPCameraInfo> list)
	{
		this.deviceList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return (deviceList == null) ? 0 : deviceList.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ItemViewHolder listItemView = null;
		if (convertView == null)
		{
			listItemView = new ItemViewHolder();
			convertView = LayoutInflater.from(myContext).inflate(
					R.layout.item_camera_list, null);
			// 获得控件对象
			listItemView.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.img_thum = (ImageView) convertView
					.findViewById(R.id.img_thum);
			listItemView.img_group_type = (ImageView) convertView
					.findViewById(R.id.img_group_type);
			listItemView.rl_item = (RelativeLayout) convertView
					.findViewById(R.id.rl_item);
			listItemView.bt_upload = (Button) convertView
					.findViewById(R.id.bt_upload);
			listItemView.bt_delete = (Button) convertView
					.findViewById(R.id.bt_delete);
			// 设置空间集到convertView
			convertView.setTag(listItemView);
		}
		else
		{
			listItemView = (ItemViewHolder) convertView.getTag();
		}
		if (deviceList != null)
		{
			final IPCameraInfo item = deviceList.get(position);
			if (item != null)
			{
				// 普通灯具

				LogUtil.LogI(TAG, "item.devName=" + item.devName);
				LogUtil.LogI(TAG, "item.uid=" + item.uid);
				if (!TextUtils.isEmpty(item.devSetName))
				{
					listItemView.tv_name.setText(item.devSetName);
				}
				else
				{
					listItemView.tv_name.setText(item.devName);
				}
				if (!TextUtils.isEmpty(item.groupName))
				{
					listItemView.tv_group_name.setText(item.groupName);
				}
				else
				{
					listItemView.tv_group_name.setText(myContext
							.getString(R.string.group));
				}
				if (!TextUtils.isEmpty(item.thumPath))
				{
					listItemView.img_thum.setImageBitmap(Utils
							.getLoacalBitmap(item.thumPath));
				}
				if (item.isOnLine)
				{
					listItemView.rl_item
							.setBackgroundResource(R.drawable.bar_2);
				}
				else
				{
					listItemView.rl_item
							.setBackgroundResource(R.drawable.bar_6);
				}
				// if (item.getDeviceState() == (byte) 0xaa)
				// {
				// // TODO 不能点击 离线 修改背景图
				// LogUtil.LogI("switchListadapter", "离线");
				// listItemView.rl_item
				// .setBackgroundResource(R.drawable.bar_6);
				// listItemView.cb_item_switch.setChecked(false);
				//
				// }
				if (item.cameraId == 0)
				{
					// 未上报
					listItemView.bt_upload.setText(myContext
							.getString(R.string.up_to_service));
					listItemView.bt_upload
							.setOnClickListener(new OnClickListener()
							{

								@Override
								public void onClick(View v)
								{
									startUploadCamera(item);

								}
							});
					listItemView.bt_delete.setVisibility(View.GONE);
				}
				else
				{
					// 已上报
					if (isEditState)
					{
						listItemView.bt_delete.setVisibility(View.VISIBLE);
						listItemView.bt_delete.setTag(R.id.bt_delete, item);
						listItemView.bt_delete.setOnClickListener(listener);
						listItemView.bt_delete.setTag(position);
					}
					else
					{
						listItemView.bt_delete.setVisibility(View.GONE);
					}
					listItemView.bt_upload.setText(myContext
							.getString(R.string.had_upload));
					listItemView.bt_upload.setBackground(null);

				}

			}
		}
		return convertView;
	}

	class ItemViewHolder
	{
		ImageView img_thum; // 设备縮略圖
		TextView tv_name; // 设备名称
		ImageView img_group_type; // 分组类型图片
		TextView tv_group_name; // 分组名字
		RelativeLayout rl_item;
		Button bt_upload;
		Button bt_delete;
	}

	private void startUploadCamera(IPCameraInfo info)
	{
		if (myContext.getProgressDialog() != null
				&& !myContext.getProgressDialog().isShowing())
		{
			myContext.getProgressDialog().show();
		}
		uploadCameraTask = new UploadCamerTask();
		uploadCameraTask.execute(info);
	}

	public boolean isEditState()
	{
		return isEditState;
	}

	public void setEditState(boolean isEditState)
	{
		this.isEditState = isEditState;
		notifyDataSetChanged();
	}

	private class UploadCamerTask extends AsyncTask<IPCameraInfo, Object, Void>
	{
		ResponseBase responseBase;
		IPCameraInfo info;

		@Override
		protected Void doInBackground(IPCameraInfo... params)
		{
			info = params[0];
			if (MyApplication.member != null && info != null)
			{
				responseBase = NetReq.uploadCamera(
						MyApplication.member.getUsername(), info.userName,
						MyApplication.member.getSessionId(), info.uid,
						info.password, info.devName,
						MyApplication.member.getSsuid(), info.mediaPort + "",
						info.webPort + "");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (myContext.getProgressDialog() != null
					&& myContext.getProgressDialog().isShowing())
			{
				myContext.getProgressDialog().dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：uid不能为空 402：密码不能为空 403：名称不能为空
				 * 404：网关编号不能为空 405：用户名不能为空 406：会话ID不能为空 407：会话无效 408：摄像头名称已存在
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					info.cameraId = Integer
							.parseInt(responseBase.getMemberId());
					LogUtil.LogI(TAG, "info.cameraId=" + info.cameraId);
					ToastUtils.show(myContext,
							myContext.getString(R.string.camera_upload_sucess));
					dbHelp.update(myContext, info);
					notifyDataSetChanged();
				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.uid_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.pass_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.ssuid_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.sessionId_null));
				}
				else if (responseBase.getResponseStatus() == 407)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.login_error));
				}
				else if (responseBase.getResponseStatus() == 408)
				{
					ToastUtils.show(myContext,
							myContext.getString(R.string.camera_name_exist));
				}
			}
			else
			{
				// 网络请求失败
			}
		}
	}
}
