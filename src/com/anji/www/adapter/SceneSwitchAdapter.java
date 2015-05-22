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

public class SceneSwitchAdapter extends BaseAdapter
{
	private List<DeviceInfo> deviceList;
	private Context myContext;
	private boolean isEdit;// �Ƿ��Ǳ༭ģʽ
	private SwitchItemEvent event;
	
	public void setEvent(SwitchItemEvent event) {
		this.event = event;
	}

	public SceneSwitchAdapter( Context myContext,
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
		ItemViewHolder listItemView = null;
		if (convertView == null)
		{
			listItemView = new ItemViewHolder();
			convertView = LayoutInflater.from(myContext).inflate(
					R.layout.item_scene_device_list, null);
			// ��ÿؼ�����
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.img_group_head = (ImageView) convertView
					.findViewById(R.id.img_group_head);
			listItemView.img_delete = (ImageView) convertView
					.findViewById(R.id.img_delete);
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
				final int pos = position;
				// �豸��Ϣ
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
										event.onSwitchDelete( pos );
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
					// ��ͨ�ƾ�
					listItemView.tv_group_name.setText(item.getDeviceName());
					if (item.getDeviceType().equals(MyConstants.NORMAL_LIGHT))
					{
						// ��ͨ��
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// TODO ���ܵ�� ���� �޸ı���ͼ
							LogUtil.LogI("switchListadapter", "����");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_light_unclick);
							// listItemView.img_group_head.setClickable(false);
							// listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// ��
							listItemView.img_group_head
									.setImageResource(R.drawable.group_light_close_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// ��
							listItemView.img_group_head
									.setImageResource(R.drawable.group_light_open_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);
						}
					}
					else if (item.getDeviceType().equals(MyConstants.SOCKET))
					{
						// �Ų�
						// ��ͨ��
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// TODO ���ܵ�� ���� �޸ı���ͼ
							LogUtil.LogI("switchListadapter", "����");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_swith_unclick);
//							listItemView.img_group_head.setClickable(false);
//							listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// ��
							listItemView.img_group_head
									.setImageResource(R.drawable.group_socket_close_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// ��
							listItemView.img_group_head
									.setImageResource(R.drawable.group_socket_open_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);
						}

					}
					else
					{
						// �ɿ��Ƶ�
						if (item.getDeviceState() == (byte) 0xaa)
						{
							// TODO ���ܵ�� ���� �޸ı���ͼ
							LogUtil.LogI("switchListadapter", "����");
							listItemView.img_group_head
									.setImageResource(R.drawable.icon1_light_unclick);
//							listItemView.img_group_head.setClickable(false);
//							listItemView.img_group_head.setEnabled(false);

						}
						else if (item.getDeviceState() == 0)
						{
							// ��
							listItemView.img_group_head
									.setImageResource(R.drawable.group_light_close_selector);
//							listItemView.img_group_head.setClickable(true);
//							listItemView.img_group_head.setEnabled(true);

						}
						else
						{
							// ��
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
				// ����豸
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
		ImageView img_group_head; // ����ͷ��
		ImageView img_delete; // ɾ����ť
		TextView tv_group_name; // ��������
	}
	
	public interface SwitchItemEvent
	{
		public void onSwitchDelete( int position );
	}
}
