package com.anji.www.help;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class IpService {
	public static String getIp(Context context) {

		// ��ȡwifi����
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// �ж�wifi�Ƿ���
		if (!wifiManager.isWifiEnabled()) {
			//wifiManager.setWifiEnabled(true);
			return null;
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);

		return ip;
	}

	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)+ "." + (i >> 24 & 0xFF);
	}

}
