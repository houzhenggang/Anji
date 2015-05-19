package com.anji.www.util;

import android.util.Log;

public class LogUtil {

	private static boolean DEBUG = true;

	public static void LogD(String Tag, String message) {
		if (DEBUG) {
			Log.d(Tag, message);
		}
	}

	public static void LogE(String Tag, String message) {
		if (DEBUG) {
			Log.e(Tag, message);
		}
	}

	public static void LogI(String Tag, String message) {
		if (DEBUG) {
			Log.i(Tag, message);
		}
	}

	public static void LogV(String Tag, String message) {
		if (DEBUG) {
			Log.v(Tag, message);
		}
	}

	public static void LogW(String Tag, String message) {
		if (DEBUG) {
			Log.w(Tag, message);
		}
	}
}
