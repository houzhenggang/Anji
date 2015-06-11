package com.anji.www.help;
import android.util.Log;
public class MyLog {

	public static void log(String msg, Exception e) {
		Log.v("tag", msg, e);
		
	}

	public static void log(String string) {
		if(string!=null){
			Log.v("tag", string);
		}
		
		
	}

}
