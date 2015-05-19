package com.anji.www.adapter;

import java.util.List;

import com.anji.www.R;
import com.anji.www.entry.DeviceInfo;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * ¯´ æ˜Žï¼šA custom adapter inherits BaseAdapter.
 */
public class CheckAdapter extends BaseAdapter
{

	private class ViewHolder
	{
		public TextView name;
		public CheckBox checkBox;
	}

	private  String[]  names;
	private LayoutInflater mInflater;
	private Context mContext;
	private boolean[] checkedItem;

	public CheckAdapter(Context context, String[] items,
			boolean[] checkedItem)
	{
		this.names = items;
		this.checkedItem = checkedItem;
		this.mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount()
	{
		return names.length;
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

	public View getItemView(int position)
	{
		return lmap.get(position);
	}

	public boolean[] getCheckedItem()
	{

		for (int i = 0; i < getCount(); i++)
		{
			View view = this.getView(i, null, null);
			CheckBox box = (CheckBox) view.findViewById(R.id.chk_selectone);
			checkedItem[i] = box.isChecked();
		}

		return checkedItem;
	}

	SparseArray<View> lmap = new SparseArray<View>();

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		View view;
		if (lmap.get(position) == null)
		{
			view = mInflater.inflate(R.layout.dialog_multichoice_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.contact_name);
			holder.checkBox = (CheckBox) view.findViewById(R.id.chk_selectone);
			lmap.put(position, view);

			if (names != null && names.length > 0)
			{
				String name = (String) names[position];
				holder.name.setText(name);
				holder.checkBox.setChecked(checkedItem[position]);
			}

			view.setTag(holder);
		}
		else
		{
			view = lmap.get(position);
			holder = (ViewHolder) view.getTag();
		}
		return view;
	}

}