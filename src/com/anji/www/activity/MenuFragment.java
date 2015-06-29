package com.anji.www.activity;

import java.util.ArrayList;
import java.util.List;

import com.anji.www.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

public class MenuFragment extends Fragment implements OnClickListener
{
	private FragmentActivity fragmentActivity; // Fragment所属的Activity
	private List<Fragment> fragments; // 一个tab页面对应一个Fragment
	private RadioButton tv_scene;
	private RadioButton tv_group;
	private RadioButton tv_switch;
	private RadioButton tv_sense;
	private RadioButton tv_camera;
	private RadioButton tv_telecontrol;
	private RadioButton tv_shop;
	private ArrayList<RadioButton> tvViews = new ArrayList<RadioButton>();
	
	private int currentTab;
	private int fragmentContentId;
	
	private MenuEvent event;
	
	public MenuFragment(FragmentActivity fragmentActivity, int fragmentContentId, MenuEvent event )
	{
		this.fragmentActivity = fragmentActivity;
		this.fragmentContentId = fragmentContentId;
		this.event = event;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) 
	{
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_left_fragment, container,
				false);
		
		tv_scene = (RadioButton) view.findViewById( R.id.tv_scene );
		tv_group = (RadioButton) view.findViewById( R.id.tv_group );
		tv_switch = (RadioButton) view.findViewById( R.id.tv_switch );
		tv_sense = (RadioButton) view.findViewById( R.id.tv_sense );
		tv_camera = (RadioButton) view.findViewById( R.id.tv_camera );
		tv_telecontrol = (RadioButton) view.findViewById( R.id.tv_telecontrol );
		tv_shop = (RadioButton) view.findViewById( R.id.tv_shop );
		
		tv_scene.setOnClickListener( this );
		tv_group.setOnClickListener( this );
		tv_switch.setOnClickListener( this );
		tv_sense.setOnClickListener( this );
		tv_camera.setOnClickListener( this );
		tv_telecontrol.setOnClickListener( this );
		tv_shop.setOnClickListener( this );
		
		tvViews.add( tv_scene );
		tvViews.add( tv_group );
		tvViews.add( tv_switch );
		tvViews.add( tv_sense );
		tvViews.add( tv_camera );
		tvViews.add( tv_telecontrol );
		tvViews.add( tv_shop );
		
		showTab( 0 );
		return view;
	}

	@Override
	public void onClick(View v) 
	{
//		if ( v.getId() == R.id.tv_shop )
//		{
//	        
//	        Intent intent = new Intent();        
//	        intent.setAction( Intent.ACTION_VIEW );    
//	        Uri content_url = Uri.parse( "http://weidian.com/s/333123846?sfr=c" );   
//	        intent.setData( content_url );  
//	        startActivity(intent);
//			return;
//		}
		String stag = (String) v.getTag();
		int tag = Integer.valueOf( stag );
		if ( fragments == null || fragments.isEmpty() || tag >= fragments.size() )
		{
			return;
		}
		
		if ( currentTab != tag )
		{
			Fragment fragment = fragments.get( tag );
			FragmentTransaction ft = obtainFragmentTransaction( tag );

			getCurrentFragment().onPause(); // 暂停当前tab
			// getCurrentFragment().onStop(); // 暂停当前tab

			if (fragment.isAdded())
			{
				// fragment.onStart(); // 启动目标tab的onStart()
				fragment.onResume(); // 启动目标tab的onResume()
			}
			else
			{
				ft.add(fragmentContentId, fragment);
			}
			showTab( tag ); // 显示目标tab
			ft.commit();
		}
	}
	
	/**
	 * 切换tab
	 * 
	 * @param idx
	 */
	private void showTab(int idx)
	{
		for (int i = 0; i < fragments.size(); i++)
		{
			Fragment fragment = fragments.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(idx);

			if (idx == i)
			{
				ft.show(fragment);
			}
			else
			{
				ft.hide(fragment);
			}
			ft.commit();
			tvViews.get( i ).setChecked( false );
		}
		currentTab = idx; // 更新目标tab为当前tab
		tvViews.get( currentTab ).setChecked( true );
		if ( event != null )
		{
			event.onSelect( currentTab );
		}
	}
	
	/**
	 * 获取一个带动画的FragmentTransaction
	 * 
	 * @param index
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index)
	{
		FragmentTransaction ft = fragmentActivity.getSupportFragmentManager()
				.beginTransaction();
//		// 设置切换动画
//		if (index > currentTab)
//		{
//			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
//		}
//		else
//		{
//			ft.setCustomAnimations(R.anim.slide_right_in,
//					R.anim.slide_right_out);
//		}
		return ft;
	}
	
	public Fragment getCurrentFragment()
	{
		return fragments.get(currentTab);
	}

	public void setFragments(List<Fragment> fragments) 
	{
		
		this.fragments = fragments;
	}
	
	
	interface MenuEvent
	{
		public void onSelect( int index );
	}
}
