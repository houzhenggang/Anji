package com.anji.www.activity;


import com.anji.www.R;
import com.anji.www.adapter.SceneAdapter;
import com.anji.www.adapter.SceneAdapter.ItemEvent;
import com.anji.www.entry.SceneInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 主页页面
 * 
 * @author moon
 */
public class TabScene extends Fragment implements OnClickListener, BaseFragment, ItemEvent
{
	private static final String TAG = TabScene.class.getName();
	
	private Button bt_more;
	MainActivity activity;
	private SceneAdapter mAdapter;
	private ListView listView;
	private TextView tvAdd;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.tab_scene, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView()
	{
		listView = (ListView) activity.findViewById( R.id.lv_scene );
		bt_more = (Button) activity.findViewById(R.id.bt_more);
		bt_more.setOnClickListener(this);
		tvAdd = (TextView) activity.findViewById(R.id.tv_add);
		tvAdd.setOnClickListener(this);
		
		mAdapter = new SceneAdapter( activity, MainActivity.sceneList );
		listView.setAdapter( mAdapter );
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_more:
			// 更多
			Intent intent = new Intent(activity, MoreActivity.class);
			activity.startActivity(intent);
			activity.overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.tv_add:
			intent = new Intent(activity, AddSceneActivity.class);
			activity.startActivity(intent);
			break;
		}
	}

	@Override
	public void refreshView()
	{
		if ( mAdapter != null)
		{
			mAdapter.setList( MainActivity.sceneList );
		}
	}

	@Override
	public void onCheckChange(int position, boolean isChecked)
	{
		SceneInfo item = MainActivity.sceneList.get( position );
		// 操作开关
		item.setOn( !item.isOn() );
		if ( mAdapter != null)
		{
			mAdapter.setList( MainActivity.sceneList );
		}
		// 请求进行打开或关闭情景模式
		
	}
}
