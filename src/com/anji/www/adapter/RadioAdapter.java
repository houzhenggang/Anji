package com.anji.www.adapter;

import java.util.List;

import com.anji.www.R;
import com.anji.www.entry.DeviceInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class RadioAdapter extends BaseAdapter
{
	private List<DeviceInfo> items;
	private LayoutInflater mInflater;
	private int curPos;

	public RadioAdapter( Context context, List<DeviceInfo> list, int curPos ) {
		this.mInflater = LayoutInflater.from(context);
		this.items = list;
		this.curPos = curPos;
	}
	
	public void setCurPos(int curPos) {
		this.curPos = curPos;
		this.notifyDataSetChanged();
	}
	
	public int getCurPos() {
		return curPos;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获得并构造帐号界面view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		convertView = mInflater.inflate(R.layout.dialog_singlechoice_item, null);

		TextView tvContactName = (TextView) convertView
				.findViewById(R.id.contact_name);
		RadioButton radioSelectone = (RadioButton) convertView.findViewById( R.id.radio_selectone );

		DeviceInfo data = items.get(position);
		
		tvContactName.setText( data.getDeviceName() );
		
		if ( curPos == position )
		{
			radioSelectone.setChecked( true );
		}
		
		return convertView;
	}

}