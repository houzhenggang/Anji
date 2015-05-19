package com.anji.www.activity;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BaseActivity extends Activity
{

	private boolean isActive = true;

	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();

		if (!isAppOnForeground())
		{
			// app 进入后台

			// 全局变量isActive = false 记录当前已经进入后台
			Log.i("BaseActivity", "app 进入后台----");
			isActive = false;
		}
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();

		if (!isActive)
		{
			// app 从后台唤醒，进入前台
			Log.i("BaseActivity", "app 从后台唤醒，进入前台----");
			isActive = true;
//			Intent intent = new Intent(this, MainActivity.class);
//			startActivity(intent);
		}
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground()
	{
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null) return false;

		for (RunningAppProcessInfo appProcess : appProcesses)
		{
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
			{
				return true;
			}
		}

		return false;
	}
}
