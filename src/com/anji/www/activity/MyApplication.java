package com.anji.www.activity;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.anji.www.entry.Member;
import com.anji.www.service.UdpService;
import com.baidu.mapapi.SDKInitializer;
import com.fos.sdk.FosSdkJNI;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * �ҵ�Ӧ�ó���
 * 
 * @author Administrator
 */
public class MyApplication extends Application
{
	private static Context ctx;
	public static Member member;
	private static final String TAG = "MyApplication";

	// public UdpService myService;

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		this.ctx = getApplicationContext();
		FosSdkJNI.Init();
		JPushInterface.setDebugMode(true); // ���ÿ�����־,����ʱ��ر���־
		JPushInterface.init(this);
		initImageLoader(getApplicationContext());
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		SDKInitializer.initialize(this);
		// ��ʼ�� JPush
	}

	public static Context getAppContext()
	{
		return ctx;
	}

	public Member getMember()
	{
		return member;
	}

	public void setMember(Member member)
	{
		this.member = member;
	}

	public static void initImageLoader(Context context)
	{
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
