package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.adapter.SceneAdapter;
import com.anji.www.adapter.SceneAdapter.ItemEvent;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.entry.ResponseBase;
import com.anji.www.entry.SceneInfo;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.ToastUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 情景模式页面
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
	private TextView tvTemperature;
	private TextView tvHumidity;
	
	private Dialog progressDialog;
	private SceneSwitchTask sceneSwitchTask;
	
	private int sceneId;
	private int position;
	private int status;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		progressDialog = DisplayUtils.createDialog( activity, "处理中..." );
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
		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}

	private void initView()
	{
		listView = (ListView) activity.findViewById( R.id.lv_scene );
		bt_more = (Button) activity.findViewById(R.id.bt_more);
		bt_more.setOnClickListener(this);
		tvAdd = (TextView) activity.findViewById(R.id.tv_add);
		tvAdd.setOnClickListener(this);
		tvTemperature = (TextView) activity.findViewById(R.id.tv_temperature);
		tvHumidity = (TextView) activity.findViewById(R.id.tv_humidity);
		
		mAdapter = new SceneAdapter( activity, MainActivity.sceneList );
		listView.setAdapter( mAdapter );
		mAdapter.setEvent( this );
		
		listView.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				Intent intent = new Intent( activity, SceneDetailActivity.class );
				intent.putExtra( "position", position );
				startActivity(intent);
			}
		});
	}
	
	private void initHumiture()
	{
		for (int i = 0; i < MainActivity.sensorList.size(); i++)
		{
			DeviceInfo info = MainActivity.sensorList.get(i);
			if (info.getDeviceType().equals(MyConstants.TEMPARETRUE_SENSOR))
			{
				tvTemperature.setText( getString( R.string.s_temperature, info.getTempValue() ) );
				tvHumidity.setText( info.getHumValue() + "%" );
				break;
			}
		}
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
		initHumiture();
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
		if (progressDialog != null )
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
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
			initHumiture();
		}
	}

	@Override
	public void onCheckChange(int position, boolean isChecked)
	{
		TabScene.this.position = position;
		
		SceneInfo item = MainActivity.sceneList.get( position );
		// 请求进行打开或关闭情景模式
		sceneId = item.getSceneId();
		status = item.isOn() ? 1 : 0;
		
		startSceneSwitch();
	}
	
	private void startSceneSwitch()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		sceneSwitchTask = new SceneSwitchTask();
		sceneSwitchTask.execute();
	}
	
	private class SceneSwitchTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase response;
		@Override
		protected Void doInBackground(Object... params)
		{
			response = NetReq.switchScene( sceneId + "", status + "", MyApplication.member.getSsuid() );
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}

			if (response != null)
			{
				/**
				 * 200：成功 300：系统异常
				 */
				if (response.getResponseStatus() == 200)
				{
					mHandler.sendEmptyMessage( 0x0001 );
				}
				else if (response.getResponseStatus() == 300)
				{
					mHandler.sendEmptyMessage( 0x0002 );
				}
			}
		}
	}
	
	
	
	Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch ( msg.what ) {
			case 0x0001:
				SceneInfo item = MainActivity.sceneList.get( position );
				// 操作开关
				item.setOn( !item.isOn() );
				if ( item.isOn() ) // 如果打开该情景模式，则关闭别的情景模式
				{
					int size = MainActivity.sceneList.size();
					for ( int i=0;i<size;i++ )
					{
						if ( i != position )
						{
							MainActivity.sceneList.get( i ).setOn( false );
						}
					}
				}
				if ( mAdapter != null)
				{
					mAdapter.setList( MainActivity.sceneList );
				}
				ToastUtils.show( activity,
						activity.getString(R.string.switch_scene_success));
				break;
			case 0x0002:
				if ( mAdapter != null)
				{
					mAdapter.setList( MainActivity.sceneList );
				}
				ToastUtils.show( activity,
						activity.getString(R.string.system_error));
				break;
			}
		}
	};
}
