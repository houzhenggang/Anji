package com.anji.www.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.anji.www.activity.TabSwtich;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.service.UdpService;
import com.anji.www.util.LogUtil;
import com.anji.www.util.Utils;

public class SwitchListAdapter extends BaseAdapter
{

	private List<DeviceInfo> deviceList;
	private MainActivity myContext;
	private final static String TAG = "SwitchListAdapter";
	private TabSwtich fragment;
	private boolean isEditState;
	private MyClickListener listener;
	public SwitchListAdapter(MainActivity myContext, TabSwtich fragment,
			List<DeviceInfo> deviceList,MyClickListener listener)
	{
		this.myContext = myContext;
		this.deviceList = deviceList;
		this.fragment = fragment;
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
		return (deviceList == null) ? 0 : deviceList.size();
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
					R.layout.item_switch_list, null);
			// ��ÿؼ�����
			listItemView.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.img_type = (ImageView) convertView
					.findViewById(R.id.img_type);
			listItemView.img_group_type = (ImageView) convertView
					.findViewById(R.id.img_group_type);
			listItemView.cb_item_switch = (CheckBox) convertView
					.findViewById(R.id.cb_item_switch);
			listItemView.rl_item = (RelativeLayout) convertView
					.findViewById(R.id.rl_item);
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
				if (item.getDeviceType().equals(MyConstants.NORMAL_LIGHT))
				{
					// ��ͨ�ƾ�
					listItemView.img_type
							.setBackgroundResource(R.drawable.icon_light);
					if (TextUtils.isEmpty(item.getDeviceName()))
					{
						listItemView.tv_name.setText(myContext
								.getString(R.string.normal_light));
					}
					else
					{
						listItemView.tv_name.setText(item.getDeviceName());
					}

				}
				else if (item.getDeviceType().equals(MyConstants.SOCKET))
				{
					// �Ų�
					listItemView.img_type
							.setBackgroundResource(R.drawable.icon_socket);
					if (TextUtils.isEmpty(item.getDeviceName()))
					{
						listItemView.tv_name.setText(myContext
								.getString(R.string.socket));
					}
					else
					{
						listItemView.tv_name.setText(item.getDeviceName());
					}
				}
				else
				{
					// �ɿ����ȵƾ�
					listItemView.img_type
							.setBackgroundResource(R.drawable.icon_light);
					if (TextUtils.isEmpty(item.getDeviceName()))
					{
						listItemView.tv_name.setText(myContext
								.getString(R.string.control_light));
					}
					else
					{
						listItemView.tv_name.setText(item.getDeviceName());
					}
				}
				if (!TextUtils.isEmpty(item.getGroupName()))
				{
					listItemView.tv_group_name.setText(item.getGroupName());
				}
				else
				{
					listItemView.tv_group_name.setText(myContext
							.getString(R.string.group));
				}
				if (item.getDeviceState() == (byte) 0xaa)
				{
					LogUtil.LogI("switchListadapter", "����");
					listItemView.rl_item
							.setBackgroundResource(R.drawable.bar_6);
					listItemView.cb_item_switch.setChecked(false);
					listItemView.cb_item_switch.setClickable(false);
					listItemView.cb_item_switch.setEnabled(false);

				}
				else if (item.getDeviceState() == 0)
				{
					// ��
					listItemView.rl_item
							.setBackgroundResource(R.drawable.bar_2);
					LogUtil.LogI("switchListadapter", "��");
					listItemView.cb_item_switch.setChecked(false);
					listItemView.cb_item_switch.setClickable(true);
					listItemView.cb_item_switch.setEnabled(true);

				}
				else
				{
					// ��
					listItemView.rl_item
							.setBackgroundResource(R.drawable.bar_2);
					LogUtil.LogI("switchListadapter", "��");
					listItemView.cb_item_switch.setChecked(true);
					listItemView.cb_item_switch.setClickable(true);
					listItemView.cb_item_switch.setEnabled(true);
				}
				final CheckBox cb = listItemView.cb_item_switch;
				cb.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						// TODO ������������
						LogUtil.LogI(TAG, "onClick ����  isInNet="
								+ MainActivity.isInNet);
						if (MainActivity.isInNet)
						{
							inNetControlSwitch(item, cb.isChecked());
						}
						else
						{
							// ����
							fragment.startControlSwitch(item);
						}

					}

				});

			}
		}
		if (isEditState)
		{
			listItemView.cb_item_switch.setVisibility(View.GONE);
			listItemView.bt_delete.setVisibility(View.VISIBLE);
			listItemView.bt_delete.setOnClickListener(listener);
			listItemView.bt_delete.setTag(position);
		}
		else
		{
			listItemView.cb_item_switch.setVisibility(View.VISIBLE);
			listItemView.bt_delete.setVisibility(View.GONE);
		}
		return convertView;
	}

	private void inNetControlSwitch(final DeviceInfo item, boolean isOpne)
	{
		final String state;
		if (isOpne)
		{
			state = "FF";
		}
		else
		{
			state = "00";
		}
		LogUtil.LogI(TAG, "onCheckedChanged isChecked=" + isOpne);

		new Thread()
		{
			public void run()
			{

				byte[] bytes = Utils.hexStringToBytes("20F200010D01"
						+ item.getDeviceMac() + "0" + item.getDeviceChannel()
						+ item.getDeviceType() + state);
				// + item.getDeviceType() + "ee");
				UdpService.newInstance(null).sendOrders(bytes);
				try
				{
					Thread.sleep(700);
					myContext.qurryOnlyAll();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			};

		}.start();
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
		ImageView img_type; // ����ͼƬ
		TextView tv_name; // �豸����
		ImageView img_group_type; // ��������ͼƬ
		TextView tv_group_name; // ��������
		CheckBox cb_item_switch;// ����
		RelativeLayout rl_item;
		Button bt_delete;
	}
	

    /**
     * ���ڻص��ĳ�����
     * @author Ivan Xu
     * 2014-11-26
     */
    public static abstract class MyClickListener implements OnClickListener {
        /**
         * �����onClick����
         */
        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }
        public abstract void myOnClick(int position, View v);
    }
}
