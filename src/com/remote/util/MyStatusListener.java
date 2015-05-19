package com.remote.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.anji.www.activity.TabCamera;
import com.ipc.sdk.StatusListener;

public class MyStatusListener implements StatusListener
{

	private static Handler myHandler;

	public MyStatusListener()
	{
		// TODO Auto-generated constructor stub
		if (myHandler == null)
		{
			myHandler = new Handler();
		}
	}

	@Override
	public void OnStatusCbk(int statusID, int reserve1, int reserve2,
			int reserve3, int reserve4)
	{
		Log.d("moon", "callback, statusID:" + statusID);

		if (statusID != -1)
		{
			Message msg = myHandler.obtainMessage();
			msg.arg1 = statusID;
			myHandler.sendMessage(msg);
		}

		// if (statusID == StatusListener.STATUS_LOGIN_SUCCESS)
		// {
		//
		// Message msg = myHandler.obtainMessage();
		// msg.arg1 = 1000;
		// myHandler.sendMessage(msg);
		//
		// }
		// else if (statusID == StatusListener.STATUS_LOGIN_FAIL_USR_PWD_ERROR)
		// {
		// Message msg = myHandler.obtainMessage();
		// msg.arg1 = 1001;
		// myHandler.sendMessage(msg);
		//
		// }
		// else if (statusID == StatusListener.STATUS_LOGIN_FAIL_ACCESS_DENY)
		// {
		// Message msg = myHandler.obtainMessage();
		// msg.arg1 = 1001;
		// myHandler.sendMessage(msg);
		//
		// }
		// else if (statusID ==
		// StatusListener.STATUS_LOGIN_FAIL_EXCEED_MAX_USER)
		// {
		// Message msg = myHandler.obtainMessage();
		// msg.arg1 = 1001;
		// myHandler.sendMessage(msg);
		//
		// }
		// else if (statusID == StatusListener.STATUS_LOGIN_FAIL_CONNECT_FAIL)
		// {
		// Message msg = myHandler.obtainMessage();
		// msg.arg1 = 1001;
		// myHandler.sendMessage(msg);
		//
		// }

	}

	public static Handler getMyHandler()
	{
		return myHandler;
	}

	public static void setMyHandler(Handler myHandler)
	{
		MyStatusListener.myHandler = myHandler;
	}
}
