package com.anji.www.adapter;

import java.util.List;

import com.anji.www.R;
import com.anji.www.activity.AddGroupActivity;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.util.LogUtil;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SwitchGridViewAdapter extends BaseAdapter
{
	private List<DeviceInfo> deviceList;
	private Activity myContext;
	private final static String TAG = "GroupListAdapter";

	public SwitchGridViewAdapter(Activity myContext, List<DeviceInfo> deviceList)
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
				if (item != null)
				{
					// 普通灯具
					if (item.getDeviceType().equals(MyConstants.NORMAL_LIGHT))
					{
						// 普通灯
							// TODO 不能点击 离线 修改背景图
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_light_unclick);

					}
					else if (item.getDeviceType().equals(MyConstants.SOCKET))
					{
						// 排插
							// TODO 不能点击 离线 修改背景图
							LogUtil.LogI("switchListadapter", "离线");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_swith_unclick);
					}else {
						//可控制灯
						listItemView.img_group_head
						.setImageResource(R.drawable.icon1_light_unclick);

					}

					listItemView.tv_group_name.setText(item.getDeviceName());
				}
			}
			else
			{
				listItemView.img_group_head
						.setImageResource(R.drawable.add_button_selector);
//				listItemView.img_group_head
//				.setOnClickListener(new OnClickListener()
//				{
//
//					@Override
//					public void onClick(View v)
//					{
//						LogUtil.LogI(TAG, "img_group_head onClick");
//						myContext.showSwitchDialog();
//					}
//				});

			}
		}
		return convertView;
	}

	class ItemViewHolder
	{
		ImageView img_group_head; // 分组头像
		TextView tv_group_name; // 分组名称
	}
}
