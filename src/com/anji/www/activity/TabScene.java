package com.anji.www.activity;

import java.util.ArrayList;

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
import com.anji.www.view.CustomSingleChoiceDialog;
import com.anji.www.view.CustomSingleChoiceDialog.ButtonClickEvent;

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
 * �龰ģʽҳ��
 * 
 * @author moon
 */
public class TabScene extends Fragment implements OnClickListener, 
					BaseFragment, ItemEvent, ButtonClickEvent
{
	private static final String TAG = TabScene.class.getName();
	
	private Button bt_more;
	MainActivity activity;
	private SceneAdapter mAdapter;
	private ListView listView;
	private TextView tvAdd;
	private TextView tvTemperature;
	private TextView tvHumidity;
	private TextView tvHumitureName;
	private TextView tvHumidityDescribe;
	private TextView tvTemperatureDescribe;
	private View layoutHumiture;
	
	private Dialog progressDialog;
	private SceneSwitchTask sceneSwitchTask;
	
	private int sceneId;
	private int position;
	private int status;
	private ArrayList<DeviceInfo> humitures = new ArrayList<DeviceInfo>();
	private int curHumiturePos;
	private CustomSingleChoiceDialog mDialog;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		progressDialog = DisplayUtils.createDialog( activity, "������..." );
		curHumiturePos = 0;
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
		layoutHumiture = activity.findViewById(R.id.layout_humiture);
		tvHumitureName = (TextView) activity.findViewById(R.id.tv_humiture_name);
		tvHumidityDescribe = (TextView) activity.findViewById( R.id.tv_humidity_describe );
		tvTemperatureDescribe = (TextView) activity.findViewById( R.id.tv_temperature_describe );
		
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
		
		tvHumitureName.setOnClickListener( this );
	}
	
	private void initHumiture()
	{
		int size = MainActivity.sensorList.size();
		if ( size != 0 )
		{
			humitures.clear();
			for (int i = 0; i < size; i++)
			{
				DeviceInfo info = MainActivity.sensorList.get(i);
				if (info.getDeviceType().equals(MyConstants.TEMPARETRUE_SENSOR))
				{
					humitures.add( info );
				}
			}
			
			if ( curHumiturePos < humitures.size() )
			{
				updateHumiture();
			}
			layoutHumiture.setVisibility( View.VISIBLE );
			if ( humitures.size() == 1 )
			{
				tvHumitureName.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0 );
			}
			else
			{
				mDialog = new CustomSingleChoiceDialog( getActivity(), humitures, "", curHumiturePos, this );
			}
		}
		else
		{
			layoutHumiture.setVisibility( View.GONE );
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
		
		if ( mDialog != null )
		{
			mDialog.dismiss();
			mDialog = null;
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
			// ����
			Intent intent = new Intent(activity, MoreActivity.class);
			activity.startActivity(intent);
			activity.overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.tv_add:
			intent = new Intent(activity, AddSceneActivity.class);
			activity.startActivity(intent);
			break;
		case R.id.tv_humiture_name:
			if ( humitures.size() >= 1 && mDialog != null )
			{
				mDialog.show();
			}
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
		// ������д򿪻�ر��龰ģʽ
		sceneId = item.getSceneId();
		// ȡ�෴��״̬
		status = item.isOn() ? 0 : 1;
		
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
				 * 200���ɹ� 300��ϵͳ�쳣
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
				// ��������
				item.setOn( !item.isOn() );
				if ( item.isOn() ) // ����򿪸��龰ģʽ����رձ���龰ģʽ
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

	@Override
	public void onOkClick(int which) 
	{
		mDialog.hide();
		curHumiturePos = which;
		updateHumiture();
	}
	
	private void updateHumiture()
	{
		float humitidy = humitures.get( curHumiturePos ).getHumValue();
		float temperature = humitures.get( curHumiturePos ).getTempValue();
		
		// ����ʪ��
		String describe = null;
		if (humitidy <= 30)
		{
			tvHumidity.setTextColor( getResources().getColor( R.color.dry ) );
			tvHumidityDescribe.setTextColor( getResources().getColor( R.color.dry ) );
			describe = getString( R.string.dry );
		}
		else if (humitidy > 30 && humitidy <= 45)
		{
			tvHumidity.setTextColor( getResources().getColor( R.color.slightly_dry ) );
			tvHumidityDescribe.setTextColor( getResources().getColor( R.color.slightly_dry ) );
			describe = getString(R.string.slightly_dry);
		}
		else if (humitidy > 45 && humitidy <= 60)
		{
			tvHumidity.setTextColor( getResources().getColor(R.color.comfortable ) );
			tvHumidityDescribe.setTextColor( getResources().getColor(R.color.comfortable ) );
			describe = getString(R.string.comfortable);
		}
		else if (humitidy > 60 && humitidy <= 80)
		{
			tvHumidity.setTextColor( getResources().getColor(R.color.mild_and_wet ) );
			tvHumidityDescribe.setTextColor( getResources().getColor(R.color.mild_and_wet ) );
			describe = getString(R.string.mild_and_wet);
		}
		else if (humitidy > 80)
		{
			tvHumidity.setTextColor( getResources().getColor(R.color.wet_color ) );
			tvHumidityDescribe.setTextColor( getResources().getColor(R.color.wet_color ) );
			describe = getString(R.string.wet);
		}
		tvHumidity.setText( humitidy + "%" );
		tvHumidityDescribe.setText( describe );
		
		// �����¶�
		if (temperature <= 5)
		{
			tvTemperature.setTextColor( getResources().getColor(R.color.cold ) );
			tvTemperatureDescribe.setTextColor( getResources().getColor(R.color.cold ) );
			describe = getString(R.string.cold);
		}
		else if (temperature > 6 && temperature <= 15)
		{
			tvTemperature.setTextColor( getResources().getColor(
							R.color.low_temperature ) );
			tvTemperatureDescribe.setTextColor( getResources().getColor(R.color.low_temperature ) );
			describe = getString(R.string.low_temperature);
		}
		else if (temperature > 15 && temperature <= 25)
		{
			tvTemperature.setTextColor( getResources().getColor(R.color.comfortable ) );
			tvTemperatureDescribe.setTextColor( getResources().getColor(R.color.comfortable ) );
			describe = getString(R.string.comfortable);
		}
		else if (temperature > 25 && temperature <= 32)
		{
			tvTemperature.setTextColor( getResources().getColor(R.color.hot ) );
			tvTemperatureDescribe.setTextColor( getResources().getColor(R.color.hot ) );
			describe = getString(R.string.hot);
		}
		else if (temperature > 32)
		{
			tvTemperature.setTextColor( getResources().getColor(
							R.color.high_temperature ) );
			tvTemperatureDescribe.setTextColor( getResources().getColor(
					R.color.high_temperature ) );
			describe = getString(R.string.high_temperature);
		}
		tvTemperature.setText( getString( R.string.s_temperature, temperature ) );
		tvTemperatureDescribe.setText( describe );
		
		tvHumitureName.setText( humitures.get( curHumiturePos ).getDeviceName() );
		
	}
}
