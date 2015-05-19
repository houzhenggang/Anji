package com.remote.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;

public class ActivityManager {
	private static Map<String, Activity> activityMap = new HashMap<String,Activity>();
	
	public static void  Add(String activityName,Activity activity){
		activityMap.put(activityName, activity);
	}
	
	public static boolean  remove(String activityName){
		try {
			Activity activity = activityMap.remove(activityName);
			if (activity!=null) {
//				activity.finish();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void  finish(String activityName){
		try {
			Activity activity = activityMap.remove(activityName);
			if (activity!=null) {
				activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void finishAllActivity(){
		Iterator<String>  iter  = activityMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Activity activity = activityMap.get(key);
			if (activity!=null) {
				activity.finish();
			}
		}
		activityMap.clear();
	}
	
}
