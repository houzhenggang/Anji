package com.anji.www.help;

import android.R.integer;

public class MyInteger {

	public static boolean isInteger(String recvStr) {
		try {
			Integer.parseInt(recvStr);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
