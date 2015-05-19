package com.anji.www.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.anji.www.activity.MyApplication;
import com.anji.www.constants.Url;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;


public final class ToastKit {
	private static final int TYPE_SHORT_RES = 1;
	private static final int TYPE_LONG_RES = 2;
	private static final int TYPE_SHORT_STR = 3;
	private static final int TYPE_LONG_STR = 4;
	
	private static final Handler toastHandler = new Handler(Looper.getMainLooper()){
		public void dispatchMessage(android.os.Message msg) {
			int what = msg.what;
			
			if(what != TYPE_LONG_RES && what != TYPE_LONG_RES && 
					what != TYPE_SHORT_STR && what != TYPE_LONG_STR) {
				return;
			}
			
			Context context = MyApplication.getAppContext();
			
			String toastMsg = null;
			if(what == TYPE_SHORT_RES || what == TYPE_LONG_RES) {
				toastMsg = context.getResources().getString((Integer)msg.obj);
			} else {
				toastMsg = (String) msg.obj;
			}
			if(null != toastMsg) {
				if(what == TYPE_SHORT_RES || what == TYPE_SHORT_STR) {
					Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show();
				}
			}
		};
	};
	
	public static void showLongToast(String text) {
		Message msg = toastHandler.obtainMessage(TYPE_LONG_STR);
		msg.obj = text;
		msg.sendToTarget();
	}
	public static void showLongToast(int strId) {
		Message msg = toastHandler.obtainMessage(TYPE_LONG_RES);
		msg.obj = strId;
		msg.sendToTarget();
	}
	public static void showShortToast(String text) {
		Message msg = toastHandler.obtainMessage(TYPE_SHORT_STR);
		msg.obj = text;
		msg.sendToTarget();
	}
	public static void showShortToast(int strId) {
		Message msg = toastHandler.obtainMessage(TYPE_SHORT_RES);
		msg.obj = strId;
		msg.sendToTarget();
	}
	
	/**
	 * 用toast的方式弹出异常堆栈信息, 只有在 BuildConfig.DEBUG == true 的时候才会提示
	 * Create Date: 2013-11-14 上午11:17:22
	 * @param th
	 */
	public static void debugExceptionToast(Throwable th) {
		if(Url.DEBUG) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(out);
			th.printStackTrace(ps);
			ToastKit.showLongToast(new String(out.toByteArray()));
		} else {
			th.printStackTrace();
		}
	}
}
