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
			// ��ÿؼ�����
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.img_group_head = (ImageView) convertView
					.findViewById(R.id.img_group_head);
			// ���ÿռ伯��convertView
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
					// ��ͨ�ƾ�
					if (item.getDeviceType().equals(MyConstants.NORMAL_LIGHT))
					{
						// ��ͨ��
							// TODO ���ܵ�� ���� �޸ı���ͼ
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_light_unclick);

					}
					else if (item.getDeviceType().equals(MyConstants.SOCKET))
					{
						// �Ų�
							// TODO ���ܵ�� ���� �޸ı���ͼ
							LogUtil.LogI("switchListadapter", "����");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_swith_unclick);
					}else {
						//�ɿ��Ƶ�
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
		ImageView img_group_head; // ����ͷ��
		TextView tv_group_name; // ��������
	}
}
