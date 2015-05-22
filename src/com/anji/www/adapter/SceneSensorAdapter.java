package com.anji.www.adapter;

import java.util.List;

import com.anji.www.R;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.util.LogUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SceneSensorAdapter extends BaseAdapter
{
	private List<DeviceInfo> deviceList;
	private Context myContext;
	private final static String TAG = "SceneSensorAdapter";
	private boolean isEdit;// 是否是编辑模式
	private SensorItemEvent event;
	
	public void setEvent(SensorItemEvent event) {
		this.event = event;
	}

	public SceneSensorAdapter(Context myContext,
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
	
	public List<DeviceInfo> getList()
	{
		return this.deviceList;
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
		final int pos = position;
		ItemViewHolder listItemView = null;
		if (convertView == null)
		{
			listItemView = new ItemViewHolder();
			convertView = LayoutInflater.from(myContext).inflate(
					R.layout.item_scene_device_list, null);
			// 获得控件对象
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.tv_tempareture = (TextView) convertView
					.findViewById(R.id.tv_tempareture);
			listItemView.tv_humidity = (TextView) convertView
					.findViewById(R.id.tv_humidity);
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
			listItemView.tv_tempareture.setVisibility(View.GONE);
			listItemView.tv_humidity.setVisibility(View.GONE);
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
									if ( event != null )
									{
										event.onSensorDelete( pos );
									}
								}
							});

				}
				else
				{
					listItemView.img_delete.setVisibility(View.GONE);
				}

				if (item != null)
				{
					LogUtil.LogI(TAG, "设备名称=" + item.getDeviceName());
					LogUtil.LogI(TAG, "设备状态=" + item.getDeviceState());
					// 普通灯具
					listItemView.tv_group_name.setText(item.getDeviceName());

					if (item.getDeviceType().equals(
							MyConstants.HUMAN_BODY_SENSOR))
					{
						// 红外传感
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// 不能点击 离线 修改背景图
							LogUtil.LogI("switchListadapter", "离线");
							listItemView.img_group_head
									.setImageResource(R.drawable.group_hongwai_outline_selector);
							listItemView.img_group_head.setClickable(false);
							listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// 正常
							listItemView.img_group_head
									.setImageResource(R.drawable.group_hongwai_nor_selector);
							listItemView.img_group_head.setClickable(true);
							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// 异常
							listItemView.img_group_head
									.setImageResource(R.drawable.group_hongwai_abnor_selector);
							listItemView.img_group_head.setClickable(true);
							listItemView.img_group_head.setEnabled(true);
						}
					}
					else if (item.getDeviceType().equals(
							MyConstants.SMOKE_SENSOR))
					{
						// 烟雾
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// 不能点击 离线 修改背景图
							LogUtil.LogI("switchListadapter", "离线");
							listItemView.img_group_head
									.setImageResource(R.drawable.group_yanwu_outline_selector);
							listItemView.img_group_head.setClickable(false);
							listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// 正常
							listItemView.img_group_head
									.setImageResource(R.drawable.group_yanwu_nor_selector);
							listItemView.img_group_head.setClickable(true);
							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// 异常
							listItemView.img_group_head
									.setImageResource(R.drawable.group_yanwu_abnor_selector);
							listItemView.img_group_head.setClickable(true);
							listItemView.img_group_head.setEnabled(true);
						}

					}
					else if (item.getDeviceType().equals(
							MyConstants.TEMPARETRUE_SENSOR)
							|| item.getDeviceType().equals(
									MyConstants.HUMIDITY_SENSOR))
					{
						// 温湿度

						listItemView.tv_tempareture.setVisibility(View.VISIBLE);
						listItemView.tv_humidity.setVisibility(View.VISIBLE);
						listItemView.img_group_head
								.setImageResource(R.drawable.group_wenshidu_selector);
						listItemView.tv_tempareture.setText(item.getTempValue()
								+ "℃");
						listItemView.tv_humidity.setText(item.getHumValue()
								+ "%");

						if (item.getDeviceState() == (byte) 0xaa)
						{
							// 不能点击 离线 修改背景图
							LogUtil.LogI("switchListadapter", "离线");
							// 异常
							listItemView.tv_tempareture
									.setTextColor(myContext.getResources()
											.getColor(R.color.item_text));
							listItemView.tv_humidity
									.setTextColor(myContext.getResources()
											.getColor(R.color.item_text));
						}
						else if (item.getDeviceState() == 0)
						{
							// 正常
							listItemView.tv_tempareture.setTextColor(myContext
									.getResources().getColor(R.color.white));
							listItemView.tv_humidity.setTextColor(myContext
									.getResources().getColor(R.color.white));
						}
						else
						{
							// 异常
							listItemView.tv_tempareture.setTextColor(myContext
									.getResources().getColor(R.color.white));
							listItemView.tv_humidity.setTextColor(myContext
									.getResources().getColor(R.color.white));
						}

					}
					// else if
					// (item.getDeviceType().equals(MyConstants.LIGHT_SENSOR))
					// {
					// 光敏传感 暂时不做
					// if (TextUtils.isEmpty(item.getDeviceName()))
					// {
					// listItemView.tv_name.setText(myContext
					// .getString(R.string.smoke_sensor));
					// }
					// else
					// {
					// listItemView.tv_name.setText(item.getDeviceName());
					// }

					// }
					else if (item.getDeviceType().equals(MyConstants.BRACELET))
					{
						// 手环
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// 不能点击 离线 修改背景图
							LogUtil.LogI("switchListadapter", "离线");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_chuandai_click);
							listItemView.img_group_head.setClickable(false);
							listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// 正常
							listItemView.img_group_head
									.setImageResource(R.drawable.group_shouhuan_nor_selector);
							listItemView.img_group_head.setClickable(true);
							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// 异常
							listItemView.img_group_head
									.setImageResource(R.drawable.group_shouhuan_abnor_selector);
							listItemView.img_group_head.setClickable(true);
							listItemView.img_group_head.setEnabled(true);
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
			}
			
			 listItemView.img_group_head
			 .setOnClickListener(new OnClickListener()
			 {
			
			 @Override
			 public void onClick(View v)
			 {
				 LogUtil.LogI(TAG, "img_group_head onClick----");
				 if ( event != null )
				{
					event.onSensorAdd( pos );
				}
			 }
			 });
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
		TextView tv_tempareture; // 温度
		TextView tv_humidity; // 湿度
	}
	
	public interface SensorItemEvent
	{
		public void onSensorDelete( int position );
		public void onSensorAdd( int position );
	}
}
