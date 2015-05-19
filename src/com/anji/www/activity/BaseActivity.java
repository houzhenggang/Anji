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
			// app �����̨

			// ȫ�ֱ���isActive = false ��¼��ǰ�Ѿ������̨
			Log.i("BaseActivity", "app �����̨----");
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
			// app �Ӻ�̨���ѣ�����ǰ̨
			Log.i("BaseActivity", "app �Ӻ�̨���ѣ�����ǰ̨----");
			isActive = true;
//			Intent intent = new Intent(this, MainActivity.class);
//			startActivity(intent);
		}
	}

	/**
	 * �����Ƿ���ǰ̨����
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
