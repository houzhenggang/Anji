package com.anji.www.adapter;

import java.util.List;

import com.anji.www.R;
import com.anji.www.activity.GroupDeviceInfo;
import com.anji.www.activity.MainActivity;
import com.anji.www.entry.SceneInfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SceneAdapter extends BaseAdapter
{
	private List<SceneInfo> sceneList;
	private MainActivity myContext;
	private final static String TAG = "SceneAdapter";
	private ItemEvent event;

	public void setEvent(ItemEvent event) {
		this.event = event;
	}

	public SceneAdapter(MainActivity myContext,
			List<SceneInfo> sceneList)
	{
		this.myContext = myContext;
		this.sceneList = sceneList;
	}

	public void setList(List<SceneInfo> list)
	{
		this.sceneList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return (sceneList == null) ? 0 : sceneList.size();
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
					R.layout.item_scene_list, null);
			// 获得控件对象
			listItemView.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			listItemView.img_type = (ImageView) convertView
					.findViewById(R.id.img_type);
			listItemView.cb_item_switch = (CheckBox) convertView
					.findViewById(R.id.cb_item_switch);
			// 设置空间集到convertView
			convertView.setTag(listItemView);
		}
		else
		{
			listItemView = (ItemViewHolder) convertView.getTag();
		}
		
		if ( sceneList != null )
		{
			if (position >= 0 && position < sceneList.size())
			{

				final SceneInfo item = sceneList.get( position );
				if (item != null)
				{
					String iconType = item.getIconType();
					listItemView.tv_name.setText(item.getSceneName());
					if (iconType.equals("1"))
					{
						listItemView.img_type
						.setImageResource(R.drawable.group_baby_button_selector);
					}
					else if (iconType.equals("2"))
					{
						listItemView.img_type
						.setImageResource(R.drawable.group_bathroom_button_selector);
					}
					else if (iconType.equals("3"))
					{
						listItemView.img_type
						.setImageResource(R.drawable.group_bedroom_button_selector);
					}
					else if (iconType.equals("4"))
					{
						listItemView.img_type
						.setImageResource(R.drawable.group_kitchen_button_selector);
					}
					else if (iconType.equals("5"))
					{
						listItemView.img_type
						.setImageResource(R.drawable.group_oldman_button_selector);
					}
					else if (iconType.equals("6"))
					{
						listItemView.img_type
						.setImageResource(R.drawable.group_readingroom_button_selector);
					}
					else 
					{
						listItemView.img_type
						.setImageResource(R.drawable.icon_livingroom);
					}
					
					listItemView.cb_item_switch.setChecked( item.isOn() );
					
					final int pos = position;
					final boolean isChecked = item.isOn();
					listItemView.cb_item_switch.setOnClickListener( new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if ( event != null )
							{
								event.onCheckChange( pos, isChecked );
							}
						}
					});
				}
			}
		}
		
		return convertView;
	}

	class ItemViewHolder
	{
		ImageView img_type; // 分组头像
		CheckBox cb_item_switch; // 开关按钮
		TextView tv_name; // 分组名称
	}
	
	public interface ItemEvent
	{
		public void onCheckChange( int position, boolean isChecked );
	}
}
