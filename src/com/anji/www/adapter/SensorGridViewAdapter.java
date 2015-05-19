package com.anji.www.adapter;

import java.util.ArrayList;
import java.util.List;

import com.anji.www.R;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SensorGridViewAdapter extends BaseAdapter
{
	private List<DeviceInfo> deviceList;
	private Context myContext;
	private final static String TAG = "GroupListAdapter";

	public SensorGridViewAdapter(Context myContext, List<DeviceInfo> deviceList)
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
		// TODO Auto-generated method stub
		return (deviceList == null) ? 1 : deviceList.size() + 1;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ItemViewHolder listItemView = null;
		if (convertView == null)
		{
			listItemView = new ItemViewHolder();
			convertView = LayoutInflater.from(myContext).inflate(
					R.layout.item_add_list, null);
			// 获得控件对象
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.tv_tempareture = (TextView) convertView
					.findViewById(R.id.tv_tempareture);
			listItemView.tv_humidity = (TextView) convertView
					.findViewById(R.id.tv_humidity);
			listItemView.img_group_head = (ImageView) convertView
					.findViewById(R.id.img_group_head);
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
				listItemView.tv_tempareture.setVisibility(View.GONE);
				listItemView.tv_humidity.setVisibility(View.GONE);
				if (item != null)
				{
					
					if (item.getDeviceType().equals(MyConstants.HUMAN_BODY_SENSOR))
					{
						// 红外传感
						listItemView.img_group_head
						.setImageResource(R.drawable.icon1_hongwai_unclick);
					}
					else if (item.getDeviceType().equals(MyConstants.SMOKE_SENSOR))
					{
						//烟雾
						listItemView.img_group_head.setImageResource(R.drawable.icon1_yanwu_click);
						
					}
					else if (item.getDeviceType().equals(MyConstants.TEMPARETRUE_SENSOR)||item.getDeviceType().equals(MyConstants.HUMIDITY_SENSOR))
					{
						//温湿度
						listItemView.tv_tempareture.setVisibility(View.VISIBLE);
						listItemView.tv_humidity.setVisibility(View.VISIBLE);
						listItemView.img_group_head.setImageResource(R.drawable.icon_blue);
						listItemView.tv_tempareture.setText(item.getTempValue() + "℃");
						listItemView.tv_humidity.setText(item.getHumValue() + "%");
						
					}
//					else if (item.getDeviceType().equals(MyConstants.LIGHT_SENSOR))
//					{
						//光敏传感   暂时不做
//						if (TextUtils.isEmpty(item.getDeviceName()))
//						{
//							listItemView.tv_name.setText(myContext
//									.getString(R.string.smoke_sensor));
//						}
//						else
//						{
//							listItemView.tv_name.setText(item.getDeviceName());
//						}

//					}
					else if (item.getDeviceType().equals(MyConstants.BRACELET))
					{
						//手环
						listItemView.img_group_head.setImageResource(R.drawable.icon1_chuandai_click);

					}
				}
			}
			else
			{
				listItemView.img_group_head
						.setImageResource(R.drawable.add_button_selector);
			}
		}
		return convertView;
	}

	class ItemViewHolder
	{
		ImageView img_group_head; // 分组头像
		TextView tv_group_name; // 分组名称
		TextView tv_tempareture; // 温度
		TextView tv_humidity; // 湿度
	}
}
