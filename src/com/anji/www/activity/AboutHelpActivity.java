package com.anji.www.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.anji.www.R;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.entry.Version;
import com.anji.www.network.NetReq;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutHelpActivity extends BaseActivity implements OnClickListener
{

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Marker mMarkerA;
	private InfoWindow mInfoWindow;

	private RelativeLayout rl_web;
	private RelativeLayout rl_email;
	private RelativeLayout rl_webcat;
	private RelativeLayout rl_phone;
	private Button bt_back;
	private TextView tv_title;
	private Button button;
	private String Tag = "AboutHelpActivity";
	private boolean isShowInfoWindows;



	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding2);

	// BitmapDescriptor bd = BitmapDescriptorFactory
	// .fromResource(R.drawable.icon_gcoding);

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_help);
		OnInfoWindowClickListener listener = null;
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		initOverlay();
		initView();
		isShowInfoWindows = true;
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{

			public boolean onMarkerClick(final Marker marker)
			{
				// Button button = new Button(getApplicationContext());
				// button.setBackgroundResource(R.drawable.popup);
				// OnInfoWindowClickListener listener = null;
				LogUtil.LogI(Tag, "onMarkerClick");
				if (marker == mMarkerA)
				{
					if (isShowInfoWindows)
					{
						mBaiduMap.hideInfoWindow();
					}
					else
					{

						mBaiduMap.showInfoWindow(mInfoWindow);
					}
				}
				else
				{
					mBaiduMap.hideInfoWindow();
				}
				// LatLng ll = marker.getPosition();
				// mInfoWindow = new
				// InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47,
				// listener);
				// mBaiduMap.showInfoWindow(mInfoWindow);
				// }
				// else if (marker == mMarkerB) {
				// button.setText("更改图标");
				// button.setOnClickListener(new OnClickListener() {
				// public void onClick(View v) {
				// marker.setIcon(bd);
				// mBaiduMap.hideInfoWindow();
				// }
				// });
				// LatLng ll = marker.getPosition();
				// mInfoWindow = new InfoWindow(button, ll, -47);
				// mBaiduMap.showInfoWindow(mInfoWindow);
				// } else if (marker == mMarkerC) {
				// button.setText("删除");
				// button.setOnClickListener(new OnClickListener() {
				// public void onClick(View v) {
				// marker.remove();
				// mBaiduMap.hideInfoWindow();
				// }
				// });
				// LatLng ll = marker.getPosition();
				// mInfoWindow = new InfoWindow(button, ll, -47);
				// mBaiduMap.showInfoWindow(mInfoWindow);
				// }
				return true;
			}
		});

		button = new Button(getApplicationContext());
		// button.setBackgroundResource(R.drawable.default_busline_bubble_timebus);
		button.setBackgroundResource(R.drawable.pop_map);

		button.setText("深圳市岸基科技有限公司");
		button.setTextSize(14);
		button.setTextColor(R.color.item_text2);

		listener = new OnInfoWindowClickListener()
		{
			public void onInfoWindowClick()
			{
				// LatLng ll = mMarkerA.getPosition();
				// LatLng llNew = new LatLng(ll.latitude + 0.005,
				// ll.longitude + 0.005);
				// mMarkerA.setPosition(llNew);
				mBaiduMap.hideInfoWindow();
				isShowInfoWindows = false;
				// button.setVisibility(View.GONE);
			}
		};
		LatLng ll = mMarkerA.getPosition();
		mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button),
				ll, -77, listener);
		mBaiduMap.showInfoWindow(mInfoWindow);
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{

			@Override
			public boolean onMapPoiClick(MapPoi arg0)
			{
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0)
			{
				// TODO Auto-generated method stub
				mBaiduMap.hideInfoWindow();

			}
		});

	}

	private void initView()
	{
		rl_web = (RelativeLayout) findViewById(R.id.rl_web);
		rl_email = (RelativeLayout) findViewById(R.id.rl_email);
		rl_webcat = (RelativeLayout) findViewById(R.id.rl_webcat);
		rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
		rl_web.setOnClickListener(this);
		rl_email.setOnClickListener(this);
		rl_webcat.setOnClickListener(this);
		rl_phone.setOnClickListener(this);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.about_help));

	}

	public void initOverlay()
	{
		// add marker overlay
		LatLng llA = new LatLng(22.567304, 114.110799);

		OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
				.zIndex(9).draggable(true);
		mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

		LatLngBounds bounds = new LatLngBounds.Builder().include(llA).build();

		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
				bounds.getCenter(), 17);
		mBaiduMap.setMapStatus(u);

		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener()
		{
			public void onMarkerDrag(Marker marker)
			{
			}

			public void onMarkerDragEnd(Marker marker)
			{
				Toast.makeText(
						AboutHelpActivity.this,
						"拖拽结束，新位置：" + marker.getPosition().latitude + ", "
								+ marker.getPosition().longitude,
						Toast.LENGTH_LONG).show();
			}

			public void onMarkerDragStart(Marker marker)
			{
			}
		});
	}

	/**
	 * 清除所有Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view)
	{
		mBaiduMap.clear();
	}

	/**
	 * 重新添加Overlay
	 * 
	 * @param view
	 */
	public void resetOverlay(View view)
	{
		clearOverlay(null);
		initOverlay();
	}

	@Override
	protected void onPause()
	{
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
		// 回收 bitmap 资源
		bdA.recycle();
		// bd.recycle();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		Intent intent;
		Uri uri;
		switch (id)
		{
		case R.id.rl_web:
			uri = Uri.parse(getString(R.string.web_url));
			intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			break;
		case R.id.rl_email:
			// 需要 android.permission.SENDTO权限
			uri = Uri.parse("mailto:info@zean.co");
			Intent mailIntent = new Intent(Intent.ACTION_SENDTO, uri);
			startActivity(mailIntent);
			break;
		case R.id.rl_webcat:
			// intent = new Intent();
			// ComponentName cmp = new ComponentName(" com.tencent.mm ",
			// "com.tencent.mm.ui.LauncherUI");
			// intent.setAction(Intent.ACTION_MAIN);
			// intent.addCategory(Intent.CATEGORY_LAUNCHER);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.setComponent(cmp);
			// startActivity(intent);
			break;
		case R.id.rl_phone:
			// 不需要权限，跳转到"拔号"中。
			Intent callIntent = new Intent(Intent.ACTION_DIAL,
					Uri.parse("tel:075586503505"));
			startActivity(callIntent);
			break;
		case R.id.bt_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed()
	{
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
}
