package com.anji.www.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import com.anji.www.entry.GroupInfo;
import com.anji.www.service.UdpService;
import com.anji.www.util.LogUtil;
import com.anji.www.util.Utils;

public class EditDevictGroupListAdapter extends BaseAdapter
{

	private List<GroupInfo> deviceList;
	private Context myContext;
	private final static String TAG = "EditDevictGroupListAdapter";
	private int selectedItem;

	public EditDevictGroupListAdapter(Context myContext,
			List<GroupInfo> deviceList)
	{
		this.myContext = myContext;
		this.deviceList = deviceList;
	}

	public void setList(List<GroupInfo> list)
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
					R.layout.item_edit_group, null);
			// 获得控件对象
			listItemView.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);
			listItemView.img_select = (ImageView) convertView
					.findViewById(R.id.img_select);
			// 设置空间集到convertView
			convertView.setTag(listItemView);
		}
		else
		{
			listItemView = (ItemViewHolder) convertView.getTag();
		}
		if (deviceList != null)
		{
			final GroupInfo item = deviceList.get(position);
			if (item != null)
			{
				listItemView.tv_group_name.setText(item.getGroupName());
				if (selectedItem == position)
				{
					listItemView.img_select.setVisibility(View.VISIBLE);
				}
				else
				{
					listItemView.img_select.setVisibility(View.GONE);
				}
			}
		}
		return convertView;
	}

	public int getSelectedItem()
	{
		return selectedItem;
	}

	public void setSelectedItem(int selectedItem)
	{
		this.selectedItem = selectedItem;
		notifyDataSetChanged();
	}

	class ItemViewHolder
	{
		TextView tv_group_name; // 设备名称
		ImageView img_select; // 分组类型图片
	}
}
