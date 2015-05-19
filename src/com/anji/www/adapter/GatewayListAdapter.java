package com.anji.www.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.anji.www.R;
import com.anji.www.activity.TabSwtich;
import com.anji.www.entry.GatewayResponse;

public class GatewayListAdapter extends BaseAdapter
{

	private List<GatewayResponse> deviceList;
	private Context myContext;
	private final static String TAG = "SwitchListAdapter";
	private int currentPosition;

	public GatewayListAdapter(Context myContext,
			List<GatewayResponse> deviceList)
	{
		this.myContext = myContext;
		this.deviceList = deviceList;
	}

	public void setList(List<GatewayResponse> list)
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
					R.layout.item_gateway_list, null);
			// 获得控件对象
			listItemView.contact_name = (TextView) convertView
					.findViewById(R.id.contact_name);
			listItemView.chk_selectone = (CheckBox) convertView
					.findViewById(R.id.chk_selectone);
			// 设置空间集到convertView
			convertView.setTag(listItemView);
		}
		else
		{
			listItemView = (ItemViewHolder) convertView.getTag();
		}
		if (deviceList != null)
		{
			final GatewayResponse item = deviceList.get(position);
			if (item != null)
			{
				listItemView.contact_name.setText(item.getSsuid());
				// if (item.isCurrGateway())
				// {
				// listItemView.chk_selectone.setChecked(true);
				// }
				// else
				// {
				// listItemView.chk_selectone.setChecked(false);
				// }
				if (position == currentPosition)
				{
					listItemView.chk_selectone.setChecked(true);
				}
				else
				{
					listItemView.chk_selectone.setChecked(false);
				}
			}
		}
		return convertView;
	}

	public int getCurrentPosition()
	{
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition)
	{
		this.currentPosition = currentPosition;
		notifyDataSetChanged();
	}

	class ItemViewHolder
	{
		TextView contact_name; // 设备名称
		CheckBox chk_selectone;// 开关
	}
}
