package com.anji.www.adapter;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.test.UiThreadTest;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.anji.www.R;
import com.anji.www.activity.MainActivity;
import com.anji.www.adapter.SwitchListAdapter.MyClickListener;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.util.LogUtil;
import com.anji.www.util.Utils;

public class SensorListAdapter extends BaseAdapter
{

	private List<DeviceInfo> deviceList;
	private MainActivity myContext;
	private final static String TAG = "SensorListAdapter";
	private MyClickListener listener;
	private boolean isEditState;

	public SensorListAdapter(MainActivity myContext,
			List<DeviceInfo> deviceList, MyClickListener listener)
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
					R.layout.item_sensor_list, null);
			// ��ÿؼ�����
			listItemView.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.img_group_type = (ImageView) convertView
					.findViewById(R.id.img_group_type);
			listItemView.img_state = (ImageView) convertView
					.findViewById(R.id.img_state2);
			listItemView.img_battery = (ImageView) convertView
					.findViewById(R.id.img_battery);
			listItemView.bt_delete = (Button) convertView
					.findViewById(R.id.bt_delete);
			// ���ÿռ伯��convertView
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
					listItemView.bt_delete.setTag(R.id.bt_delete, item);
					listItemView.bt_delete.setOnClickListener(listener);
					listItemView.bt_delete.setTag(position);
					listItemView.img_battery.setVisibility(View.GONE);
				}
				else
				{
					listItemView.img_battery.setVisibility(View.VISIBLE);
					listItemView.bt_delete.setVisibility(View.GONE);
				}

				int battery = item.getDeviceBattery();
				if (item.getDeviceState() == 0)
				{
					// ����
					LogUtil.LogI(TAG, "item.getDeviceState() == 0 ����");
					// LayoutParams params = new LayoutParams(Utils.dip2px(
					// myContext, 30), Utils.dip2px(myContext, 14));
					// listItemView.img_battery.setLayoutParams(params);
					LogUtil.LogI(TAG, "item.isCharge()=="+item.isCharge());
					LogUtil.LogI(TAG, "battery=="+battery);
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
						else if (battery > 30 && battery <= 60)
						{
							listItemView.img_battery
									.setBackgroundResource(R.drawable.icon_battery_mid);
						}
						else if (battery > 60) {
							
						
							listItemView.img_battery
									.setBackgroundResource(R.drawable.icon_battery_high);
						}
					}
				}
				else if (item.getDeviceState() == (byte) 0xaa)
				{
					// ����
					LogUtil.LogI(TAG, "item.getDeviceState() == aa ����");
					listItemView.img_battery
							.setBackgroundResource(R.drawable.icon_battery_off);
				}
				else
				{
					// ����
					LogUtil.LogI(TAG, "item.getDeviceState() == 0 ����");
				}

				if (item.getDeviceType().equals(MyConstants.HUMAN_BODY_SENSOR))
				{
					// ���⴫��

					// if (item.getDeviceState() == 0)
					// {
					// // ����
					// LogUtil.LogI(TAG, "item.getDeviceState() == 0 ����");
					// listItemView.img_battery
					// .setBackgroundResource(R.drawable.tongdian);
					// }
					// else if (item.getDeviceState() == (byte) 0xaa)
					// {
					// // ����
					// LogUtil.LogI(TAG, "item.getDeviceState() == aa ����");
					// listItemView.img_battery
					// .setBackgroundResource(R.drawable.duandian);
					// }
					// else
					// {
					// // ����
					// LogUtil.LogI(TAG, "item.getDeviceState() == 0 ����");
					// }

					if (TextUtils.isEmpty(item.getDeviceName()))
					{
						listItemView.tv_name.setText(myContext
								.getString(R.string.infrared_sensor));
					}
					else
					{
						listItemView.tv_name.setText(item.getDeviceName());
					}
					LogUtil.LogI(TAG,
							"item.getSensorState()=" + item.getSensorState());
					LogUtil.LogI(TAG, "(byte)0x10" + 0x10);
					if (item.getSensorState() == 0x10)
					{
						// ����
						LogUtil.LogI(TAG, "����");
						listItemView.img_state
								.setBackgroundResource(R.drawable.icon_hongwai_abnor);
						// listItemView.img_state.setImageResource(R.drawable.icon_hongwai_no);
					}
					else
					{
						LogUtil.LogI(TAG, "û��");
						listItemView.img_state
								.setBackgroundResource(R.drawable.icon_hongwai_nor);
						// listItemView.img_state.setImageResource(R.drawable.icon_hongwai_nor);
					}
				}
				else if (item.getDeviceType().equals(MyConstants.SMOKE_SENSOR))
				{
					// ����
					if (TextUtils.isEmpty(item.getDeviceName()))
					{
						listItemView.tv_name.setText(myContext
								.getString(R.string.smoke_sensor));
					}
					else
					{
						listItemView.tv_name.setText(item.getDeviceName());
					}

					if (item.getSensorState() == (byte) 0x10)
					{
						// ������
						listItemView.img_state
								.setBackgroundResource(R.drawable.icon_yanwu_abnor);
					}
					else
					{
						listItemView.img_state
								.setBackgroundResource(R.drawable.icon_yanwu_nor);
					}
					if (item.getDeviceState() == 0)
					{
						// ����
						LogUtil.LogI(TAG, "item.getDeviceState() == 0 ����");
						listItemView.img_battery
								.setBackgroundResource(R.drawable.tongdian);
					}
					else if (item.getDeviceState() == (byte) 0xaa)
					{
						// ����
						LogUtil.LogI(TAG, "item.getDeviceState() == aa ����");
						listItemView.img_battery
								.setBackgroundResource(R.drawable.duandian);
					}
					else
					{
						// ����
						LogUtil.LogI(TAG, "item.getDeviceState() == 0 ����");
					}
				}
				// else if
				// (item.getDeviceType().equals(MyConstants.LIGHT_SENSOR))
				// {
				// �������� ��ʱ����
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
					// �ֻ�
					if (TextUtils.isEmpty(item.getDeviceName()))
					{
						listItemView.tv_name.setText(myContext
								.getString(R.string.bracelet));
					}
					else
					{
						listItemView.tv_name.setText(item.getDeviceName());
					}
					if (item.getSensorState() >= 0
							&& item.getSensorState() <= 100)
					{
						//
						listItemView.img_state
								.setBackgroundResource(R.drawable.icon_chuandai_nor);
					}
					else
					{
						listItemView.img_state
								.setBackgroundResource(R.drawable.icon_chuandai_abnor);
					}

				}

				if (TextUtils.isEmpty(item.getGroupName()))
				{
					listItemView.tv_group_name.setText(myContext
							.getString(R.string.group));
				}
				else
				{
					listItemView.tv_group_name.setText(item.getGroupName());
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
		TextView tv_name; // �豸����
		ImageView img_group_type; // ��������ͼƬ
		ImageView img_battery; // ��������ͼƬ
		ImageView img_state; // ��������ͼƬ
		TextView tv_group_name; // ��������
		Button bt_delete;
	}
}
