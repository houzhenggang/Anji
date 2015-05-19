package com.anji.www.adapter;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anji.www.R;
import com.anji.www.activity.MainActivity;
import com.anji.www.adapter.SwitchListAdapter.MyClickListener;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.service.UdpService;
import com.anji.www.util.LogUtil;
import com.anji.www.util.Utils;

public class HumitureListAdapter extends BaseAdapter
{

	private List<DeviceInfo> deviceList;
	private MainActivity myContext;
	private final static String TAG = "HumitureListAdapter";
	private MyClickListener listener;
	private boolean isEditState;

	public HumitureListAdapter(MainActivity myContext,
			List<DeviceInfo> deviceList,MyClickListener listener)
	{
		this.myContext = myContext;
		this.deviceList = deviceList;
		this.listener = listener;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ItemViewHolder listItemView = null;
		if (convertView == null)
		{
			listItemView = new ItemViewHolder();
			convertView = LayoutInflater.from(myContext).inflate(
					R.layout.item_humiture_list, null);
			// 获得控件对象
			listItemView.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.tv_value = (TextView) convertView
					.findViewById(R.id.tv_value);
			listItemView.tv_describe = (TextView) convertView
					.findViewById(R.id.tv_describe);
			listItemView.img_group_type = (ImageView) convertView
					.findViewById(R.id.img_group_type);
			listItemView.img_battery = (ImageView) convertView
					.findViewById(R.id.img_battery);
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
			final DeviceInfo item = deviceList.get(position);
			if (item != null)
			{
				
				if (isEditState)
				{
					listItemView.bt_delete.setVisibility(View.VISIBLE);
					listItemView.bt_delete.setTag(R.id.bt_delete,item);
					listItemView.bt_delete.setOnClickListener(listener);
					listItemView.bt_delete.setTag(position);
					listItemView.img_battery.setVisibility(View.GONE);
				}else {
					listItemView.img_battery.setVisibility(View.VISIBLE);
					listItemView.bt_delete.setVisibility(View.GONE);
				}
				
				if (item.getDeviceType().equals(MyConstants.HUMIDITY_SENSOR))
				{
					// 湿度
					if (TextUtils.isEmpty(item.getDeviceName()))
					{
						listItemView.tv_name.setText(myContext
								.getString(R.string.humidity));
					}
					else
					{
						listItemView.tv_name.setText(item.getDeviceName());
					}

					listItemView.tv_value.setText(item.getHumValue() + "%");
					float humitidy = item.getHumValue();
					LogUtil.LogI(TAG, "humitidy=" + humitidy);
					String describe = null;
					// if (humitidy != 0)
					// {
					if (humitidy <= 30)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(R.color.dry));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(R.color.dry));
						describe = myContext.getString(R.string.dry);
					}
					else if (humitidy > 30 && humitidy <= 45)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(R.color.slightly_dry));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(R.color.slightly_dry));
						describe = myContext.getString(R.string.slightly_dry);
					}
					else if (humitidy > 45 && humitidy <= 60)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(R.color.comfortable));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(R.color.comfortable));
						describe = myContext.getString(R.string.comfortable);
					}
					else if (humitidy > 60 && humitidy <= 80)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(R.color.mild_and_wet));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(R.color.mild_and_wet));
						describe = myContext.getString(R.string.mild_and_wet);
					}
					else if (humitidy > 80)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(R.color.wet_color));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(R.color.wet_color));
						describe = myContext.getString(R.string.wet);
					}
					// }
					// else
					// {
					// describe = myContext.getString(R.string.unknow);
					// }
					listItemView.tv_describe.setText(describe);

				}
				else if (item.getDeviceType().equals(
						MyConstants.TEMPARETRUE_SENSOR))
				{
					// 温度
					if (TextUtils.isEmpty(item.getDeviceName()))
					{
						listItemView.tv_name.setText(myContext
								.getString(R.string.temperature));
					}
					else
					{
						listItemView.tv_name.setText(item.getDeviceName());
					}
					listItemView.tv_value.setText(item.getTempValue() + "℃");
					float humitidy = item.getTempValue();
					LogUtil.LogI(TAG, "humitidy=" + humitidy);
					String describe = null;
					// if (humitidy != 0)
					// {
					if (humitidy <= 5)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(R.color.cold));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(R.color.cold));
						describe = myContext.getString(R.string.cold);
					}
					else if (humitidy > 6 && humitidy <= 15)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(
										R.color.low_temperature));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(
										R.color.low_temperature));
						describe = myContext
								.getString(R.string.low_temperature);
					}
					else if (humitidy > 15 && humitidy <= 25)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(R.color.comfortable));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(R.color.comfortable));
						describe = myContext.getString(R.string.comfortable);
					}
					else if (humitidy > 25 && humitidy <= 32)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(R.color.hot));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(R.color.hot));
						describe = myContext.getString(R.string.hot);
					}
					else if (humitidy > 32)
					{
						listItemView.tv_describe.setTextColor(myContext
								.getResources().getColor(
										R.color.high_temperature));
						listItemView.tv_value.setTextColor(myContext
								.getResources().getColor(
										R.color.high_temperature));
						describe = myContext
								.getString(R.string.high_temperature);
					}
					// }
					// else
					// {
					// describe = myContext.getString(R.string.unknow);
					// }
					listItemView.tv_describe.setText(describe);
				}
				if (TextUtils.isEmpty(item.getGroupName()))
				{
					listItemView.tv_group_name.setVisibility(View.GONE);
				}
				else
				{
					listItemView.tv_group_name.setVisibility(View.VISIBLE);
					listItemView.tv_group_name.setText(item.getGroupName());
				}
				int battery = item.getDeviceBattery();
				if (item.getDeviceState() == 0)
				{
					// 正常
					if (item.isCharge())
					{
						listItemView.img_battery
								.setBackgroundResource(R.drawable.icon_battery_charging);
					}
					else
					{
						if (battery <= 30)
						{
							listItemView.img_battery
									.setBackgroundResource(R.drawable.icon_battery_low);
						}
						else if (battery <= 60)
						{
							listItemView.img_battery
									.setBackgroundResource(R.drawable.icon_battery_mid);
						}
						else
						{
							listItemView.img_battery
									.setBackgroundResource(R.drawable.icon_battery_high);
						}
					}
				}
				else if (item.getDeviceState() == (byte) 0xaa)
				{
					// 离线
					listItemView.img_battery
							.setBackgroundResource(R.drawable.icon_battery_off);
				}
				else
				{
					// 错误
				}
			}
		}
		return convertView;
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

	class ItemViewHolder
	{
		TextView tv_name; // 设备名称
		TextView tv_value; // 设备数据
		TextView tv_describe; // 设备数据描述
		ImageView img_group_type; // 分组类型图片
		ImageView img_battery; // 分组类型图片
		TextView tv_group_name; // 分组名字
		Button bt_delete;
	}
}
