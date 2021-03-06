package com.anji.www.adapter;

import java.util.ArrayList;
import java.util.List;

import com.anji.www.R;
import com.anji.www.activity.GroupDeviceInfo;
import com.anji.www.activity.MainActivity;
import com.anji.www.adapter.SwitchListAdapter.ItemViewHolder;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.GroupInfo;
import com.anji.www.service.UdpService;
import com.anji.www.util.LogUtil;
import com.anji.www.util.Utils;
import com.ipc.sdk.DevInfo;
import com.remote.util.IPCameraInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class GroupSwitchAdapter extends BaseAdapter
{
	private List<DeviceInfo> deviceList;
	private GroupDeviceInfo myContext;
	private final static String TAG = "GroupSwitchAdapter";
	private boolean isEdit;// 是否是编辑模式

	public GroupSwitchAdapter(GroupDeviceInfo myContext,
			List<DeviceInfo> deviceList)
	{
		this.myContext = myContext;
		this.deviceList = deviceList;
	}

	public void setList(List<DeviceInfo> list)
	{
		this.deviceList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		int count;
		if (isEdit)
		{
			count = (deviceList == null) ? 1 : deviceList.size() + 1;
		}
		else
		{
			count = (deviceList == null) ? 0 : deviceList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ItemViewHolder listItemView = null;
		if (convertView == null)
		{
			listItemView = new ItemViewHolder();
			convertView = LayoutInflater.from(myContext).inflate(
					R.layout.item_groupinfo_list, null);
			// 获得控件对象
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.img_group_head = (ImageView) convertView
					.findViewById(R.id.img_group_head);
			listItemView.img_delete = (ImageView) convertView
					.findViewById(R.id.img_delete);
			// 设置空间集到convertView
			convertView.setTag(listItemView);
		}
		else
		{
			listItemView = (ItemViewHolder) convertView.getTag();
		}
		if (deviceList != null)
		{
			if (position >= 0 && position < deviceList.size())
			{
				final DeviceInfo item = deviceList.get(position);
				// 设备信息
				if (isEdit)
				{
					listItemView.img_delete.setVisibility(View.VISIBLE);
					listItemView.img_delete
							.setOnClickListener(new OnClickListener()
							{

								@Override
								public void onClick(View v)
								{
									 myContext.startDeleteSwtichToGroup(item);
								}
							});

				}
				else
				{
					listItemView.img_delete.setVisibility(View.GONE);
				}

				if (item != null)
				{
					// 普通灯具
					listItemView.tv_group_name.setText(item.getDeviceName());
					if (item.getDeviceType().equals(MyConstants.NORMAL_LIGHT))
					{
						// 普通灯
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// TODO 不能点击 离线 修改背景图
							LogUtil.LogI("switchListadapter", "离线");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_light_unclick);
							// listItemView.img_group_head.setClickable(false);
							// listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// 关
							listItemView.img_group_head
									.setImageResource(R.drawable.group_light_close_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// 开
							listItemView.img_group_head
									.setImageResource(R.drawable.group_light_open_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);
						}
					}
					else if (item.getDeviceType().equals(MyConstants.SOCKET))
					{
						// 排插
						// 普通灯
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// TODO 不能点击 离线 修改背景图
							LogUtil.LogI("switchListadapter", "离线");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_swith_unclick);
//							listItemView.img_group_head.setClickable(false);
//							listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// 关
							listItemView.img_group_head
									.setImageResource(R.drawable.group_socket_close_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// 开
							listItemView.img_group_head
									.setImageResource(R.drawable.group_socket_open_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);
						}

					}
					else
					{
						// 可控制灯
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// TODO 不能点击 离线 修改背景图
							LogUtil.LogI("switchListadapter", "离线");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_light_unclick);
//							listItemView.img_group_head.setClickable(false);
//							listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// 关
							listItemView.img_group_head
									.setImageResource(R.drawable.group_light_close_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// 开
							listItemView.img_group_head
									.setImageResource(R.drawable.group_light_open_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);
						}
					}
				}
			}
			else
			{
				// 添加设备
				listItemView.img_group_head
						.setImageResource(R.drawable.add_button_selector);
				listItemView.tv_group_name.setText("");
				listItemView.img_delete.setVisibility(View.GONE);
				// listItemView.img_group_head
				// .setOnClickListener(new OnClickListener()
				// {
				//
				// @Override
				// public void onClick(View v)
				// {
				// LogUtil.LogI(TAG, "img_group_head onClick");
				// myContext.showSwitchDialog();
				// }
				// });

			}
		}
		return convertView;
	}

	public boolean isEdit()
	{
		return isEdit;
	}

	public void setEdit(boolean isEdit)
	{
		this.isEdit = isEdit;
		notifyDataSetChanged();
	}

	class ItemViewHolder
	{
		ImageView img_group_head; // 分组头像
		ImageView img_delete; // 删除按钮
		TextView tv_group_name; // 分组名称
	}
}
