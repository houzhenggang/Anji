package com.anji.www.activity;

import java.util.ArrayList;
import java.util.List;

import com.anji.www.R;
import com.anji.www.adapter.GroupListAdapter;
import com.anji.www.entry.AdInfo;
import com.anji.www.util.LogUtil;
import com.anji.www.util.Utils;
import com.anji.www.view.SlideShowView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 主页页面
 * 
 * @author Ivan
 */
public class TabMain extends Fragment implements OnClickListener, BaseFragment
{

	private static final String TAG = TabMain.class.getName();

	MainActivity activity;
	private Button bt_more;
	private GridView gv_group;
	private GroupListAdapter groupAdapter;
	public static boolean isChangeName = false;
	private SlideShowView mSlideShowView;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		System.out.println("TabMain____onAttach");
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// db = new DbTool(getActivity());

		System.out.println("TabMain____onCreate");
		// byte a = (byte) 0xff;
		// LogUtil.LogI(TAG, "a= " + a);// -1
		// LogUtil.LogI(TAG, "a2= " + Utils.bytesToHexString(new byte[]
		// { a }));// ff
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		System.out.println("TabMain____onCreateView");
		return inflater.inflate(R.layout.tab_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView()
	{
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels; // 屏幕高度（像素）
		int vvHight = (int) (width * 150.00 / 640.00);
		bt_more = (Button) activity.findViewById(R.id.bt_more);
		gv_group = (GridView) activity.findViewById(R.id.gv_group);
		groupAdapter = new GroupListAdapter(activity, MainActivity.groupList);
		gv_group.setAdapter(groupAdapter);
		bt_more.setOnClickListener(this);
		gv_group.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (MainActivity.groupList != null)
				{

					if (MainActivity.groupList.size() == 0)
					{
						Intent intent = new Intent(activity,
								AddGroupActivity.class);
						activity.startActivity(intent);
					}
					else
					{
						if (position == MainActivity.groupList.size())
						{
							Intent intent = new Intent(activity,
									AddGroupActivity.class);
							activity.startActivity(intent);
						}
						else
						{
							// TODO 进入分组信息界面
							Intent intent = new Intent(activity,
									GroupDeviceInfo.class);
							intent.putExtra("position", position);
							// intent.putExtra("groupName",
							// MainActivity.groupList
							// .get(position).getGroupName());
							// intent.putExtra("groupId", MainActivity.groupList
							// .get(position).getGroupId());
							// intent.putExtra("infraredSwitch",
							// MainActivity.groupList.get(position)
							// .isInfraredSwitch());
							activity.startActivity(intent);
						}
					}

				}
				else
				{
					Intent intent = new Intent(activity, AddGroupActivity.class);
					activity.startActivity(intent);
				}
			}
		});

		List<String> imageUris = new ArrayList<String>();
		// imageUris.add("http://www.pc6.com/up/2010-10/2010107163659461.jpg");
		// imageUris
		// .add("http://cms.csdnimg.cn/articlev1/uploads/allimg/101231/79_101231140641_1.png");
		// imageUris
		// .add("http://tech.ccidnet.com/col/attachment/2010/8/2070133.jpg");
		/**
		 * 获取控件
		 */

		/**
		 * 为控件设置图片
		 */
		// AdInfo info = new AdInfo();
		// info.setImgPath("http://www.pc6.com/up/2010-10/2010107163659461.jpg");
		// info.setWeburl("http://www.baidu.com");
		// AdInfo info2 = new AdInfo();
		// info2.setImgPath("http://cms.csdnimg.cn/articlev1/uploads/allimg/101231/79_101231140641_1.png");
		// info2.setWeburl("http://www.hao123.com");
		// AdInfo info3 = new AdInfo();
		// info3.setImgPath("http://tech.ccidnet.com/col/attachment/2010/8/2070133.jpg");
		// info3.setWeburl("http://www.163.com/");
		// List<AdInfo> list = new ArrayList<AdInfo>();
		// list.add(info);
		// list.add(info3);
		// list.add(info2);
		mSlideShowView = (SlideShowView) activity
				.findViewById(R.id.slideshowView);
		if (MyApplication.member != null)
		{

			// MyApplication.member.setAdList(list);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					width, vvHight);
			mSlideShowView.setLayoutParams(params);
			mSlideShowView.setImageUris(MyApplication.member.getAdList());

		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		System.out.println("TabMain____onStart");
	}

	@Override
	public void onResume()
	{
		super.onResume();
		groupAdapter.notifyDataSetChanged();
		System.out.println("TabMain____onResume");
		if (isChangeName)
		{
			activity.startQurryGroupWithDialog();
		}
		// if (!activity.isFirstStart)
		// {
		// // activity.startq
		// activity.startQurrySwitch();
		// activity.startQurryCamera();
		// activity.startQurrySensor();
		// activity.startQurryGroup();
		// }
		// activity.isFirstStart = false;

	}

	@Override
	public void onPause()
	{
		super.onPause();
		System.out.println("TabMain____onPause");
	}

	@Override
	public void onStop()
	{
		super.onStop();
		System.out.println("TabMain____onStop");
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		System.out.println("TabMain____onDestroyView");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// if(null != db)
		// {
		// db.close();
		// }
		System.out.println("TabMain____onDestroy");
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		System.out.println("TabMain____onDetach");
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
		// case R.id.rl_NDS:
		// // TODO 模拟体验
		// Intent intent = new Intent(activity, SimulateExperience.class);
		// activity.startActivity(intent);
		// break;

		default:
			break;
		}
	}

	@Override
	public void refreshView()
	{
		groupAdapter.setList(MainActivity.groupList);
		if (MyApplication.member != null)
		{

			if (MyApplication.member.getAdList().size() > 0)
			{
				mSlideShowView.setImageUris(MyApplication.member.getAdList());
			}
			else
			{
				mSlideShowView.setBackgroundResource(R.drawable.guanggao);
			}
		}
	}
}
