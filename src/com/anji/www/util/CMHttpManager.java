package com.anji.www.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map.Entry;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anji.www.constants.Url;

/**
 * @Deprecation ： http请求管理類
 * @author 0x0001
 * 
 */
public class CMHttpManager {

	/**
	 * POST請求
	 * 
	 * @Description:@param <T>
	 * @Description:@param url
	 * @Description:@param clzz
	 * @Description:@param args
	 * @Description:@return
	 * @author:lin
	 * @time:2013-11-12 下午09:36:25
	 */
	public static <T> T post(String url, Class<T> clzz, String... args) {
		String response = post(url, args);
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		T t = null;
		try {
			if (Url.DEBUG) {
				Log.i("response", response + "-----");
			}
			JSONObject o = JSON.parseObject(removeBOM(response));
			JSONObject convertObj = parseObject(o);
			t = JSON.parseObject(convertObj.toJSONString(), clzz);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Url.DEBUG) {
			Log.i("response-object", String.valueOf(t));
		}
		return t;
	}
	
	public static <T> T post(boolean isShowToast,String url, Class<T> clzz, String... args) {
		String response;
		if (isShowToast) {
			response = post(url, args);
		}else {
			response = postNotoast(url, args);
		}
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		T t = null;
		try {
			if (Url.DEBUG) {
				Log.i("response", response + "-----");
			}
			JSONObject o = JSON.parseObject(removeBOM(response));
			JSONObject convertObj = parseObject(o);
			t = JSON.parseObject(convertObj.toJSONString(), clzz);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Url.DEBUG) {
			Log.i("response-object", String.valueOf(t));
		}
		return t;
	}

	public static final String removeBOM(String data) {
		if (TextUtils.isEmpty(data)) {
			return data;
		}

		if (data.startsWith("\ufeff")) {
			return data.substring(1);
		} else {
			return data;
		}
	}

	public static String post(String url, String... args) {
		HttpRequest request = request(url, args);
		try {
			String response = request.getResponse();
			response = response.replaceAll("%u(\\w{4})", "\\\\u$1");
			if (Url.DEBUG) {
				Log.i("CMHttpManager", response);
			}
			return response;
		} catch (Exception e) {
			// ToastKit.debugExceptionToast(e);
			ToastKit.showLongToast("网络不给力哦，再试一下吧！");
			e.printStackTrace();
		}
		return null;
	}
	
	public static String postNotoast(String url, String... args) {
		HttpRequest request = request(url, args);
		try {
			String response = request.getResponse();
			response = response.replaceAll("%u(\\w{4})", "\\\\u$1");
			if (Url.DEBUG) {
				Log.i("CMHttpManager", response);
			}
			return response;
		} catch (IOException e) {
			// ToastKit.debugExceptionToast(e);
			e.printStackTrace();
		}
		return null;
	}

	private static HttpRequest request(String url, String... args) {
		StringBuilder builder = null;

		if (Url.DEBUG) {
			builder = new StringBuilder(url);
			if (url.indexOf('?') == -1) {
				builder.append("?");
			} else {
				builder.append("&");
			}
		}

		HttpRequest request = HttpRequest.post(url);
//		request.addAttr("imei", MyDevice.Device_IMEI);
//		if (Url.DEBUG) {
//			builder.append("imei=").append(encode(MyDevice.Device_IMEI));
//			builder.append("&");
//		}
//		if (!StringUtils.isEmpty(Customer.ident)) {
//			request.addAttr("ident", Customer.ident);
//			if (Url.DEBUG) {
//				builder.append("ident=").append(encode(Customer.ident));
//				builder.append("&");
//			}
//		}

		for (int i = 0, len = args.length / 2; i < len; i++) {
			request.addAttr(args[i * 2], args[i * 2 + 1]);

			if (Url.DEBUG) {
				builder.append(args[i * 2]).append("=")
						.append(encode(args[i * 2 + 1]));
				builder.append("&");
			}
		}

		if (Url.DEBUG) {
			if (builder.length() > 0
					&& builder.charAt(builder.length() - 1) == '&') {
				builder.deleteCharAt(builder.length() - 1);
			}
			Log.i("request", builder.toString());
		}
		return request;
	}

	private static String encode(String str) {
		try {
			return URLEncoder.encode(String.valueOf(str), "UTF-8");
		} catch (Exception ex) {
			ToastKit.debugExceptionToast(ex);
			ex.printStackTrace();
			return str;
		}
	}

	public static JSONObject parseObject(JSONObject obj) throws Exception {
		JSONObject retVal = new JSONObject();

		for (Entry<String, Object> entry : obj.entrySet()) {
			Object o = entry.getValue();
			if (o instanceof JSONObject) {
				retVal.put(entry.getKey(), parseObject((JSONObject) o));
			} else if (o instanceof String) {
				retVal.put(entry.getKey(),
						URLDecoder.decode((String) o, "UTF-8"));
			} else if (o instanceof JSONArray) {
				JSONArray arr1 = parseArray((JSONArray) entry.getValue());
				retVal.put(entry.getKey(), arr1);
			}
		}
		return retVal;
	}

	private static JSONArray parseArray(JSONArray arr) throws Exception,
			UnsupportedEncodingException {
		JSONArray arr1 = new JSONArray();
		for (Object o1 : arr) {
			if (o1 instanceof JSONObject) {
				arr1.add(parseObject((JSONObject) o1));
			} else if (o1 instanceof String) {
				arr1.add(URLDecoder.decode((String) o1, "UTF-8"));
			} else if (o1 instanceof JSONArray) {
				arr1.add(parseArray((JSONArray) o1));
			}
		}
		return arr1;
	}

	public static String toString(Object obj) {
		if (null == obj) {
			return "null";
		}
		return obj.getClass().getName() + "@"
				+ Integer.toHexString(obj.hashCode()) + ":"
				+ JSON.toJSONString(obj);
	}
}
