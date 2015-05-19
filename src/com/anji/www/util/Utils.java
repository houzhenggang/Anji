package com.anji.www.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;

import com.anji.www.constants.MyConstants;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utils
{
	/**
	 * Convert byte[] to hex
	 * string.�������ǿ��Խ�byteת����int��Ȼ������Integer.toHexString(int)��ת����16�����ַ�����
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
		{
			return null;
		}
		for (int i = 0; i < src.length; i++)
		{
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2)
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv.toUpperCase());
		}
		return stringBuilder.toString();
	}

	public static String intsToHexString(int[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
		{
			return null;
		}
		for (int i = 0; i < src.length; i++)
		{
			int v = src[i];
			String hv = Integer.toHexString(v);
			if (hv.length() < 2)
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString)
	{
		if (hexString == null || hexString.equals(""))
		{
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * ��byteת��Ϊһ������Ϊ8��byte���飬����ÿ��ֵ����bit
	 */
	public static byte[] getBooleanArray(byte b)
	{
		byte[] array = new byte[8];
		for (int i = 7; i >= 0; i--)
		{
			array[i] = (byte) (b & 1);
			b = (byte) (b >> 1);
		}

		return array;
	}

	/**
	 * ��byteת��Ϊһ������Ϊ8��byte���飬����ÿ��ֵ����bit
	 */
	public static byte getBitNum(byte b, int position)
	{
		return getBooleanArray(b)[position];
	}

	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dip2px(Context context, float dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * ��ȡ��ǰ���Ի���
	 * 
	 * @return
	 */
	public static boolean isZh(Context context)
	{
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("zh")) return true;
		else
			return false;
	}

	// �õ�����ip��ַ
	public static String getLocalHostIp()
	{
		String ipaddress = "";
		try
		{
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			// �������õ�����ӿ�
			while (en.hasMoreElements())
			{
				NetworkInterface nif = en.nextElement();// �õ�ÿһ������ӿڰ󶨵�����ip
				Enumeration<InetAddress> inet = nif.getInetAddresses();
				// ����ÿһ���ӿڰ󶨵�����ip
				while (inet.hasMoreElements())
				{
					InetAddress ip = inet.nextElement();
					if (!ip.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ip
									.getHostAddress()))
					{
						return ipaddress = ip.getHostAddress();
					}
				}

			}
		}
		catch (SocketException e)
		{
			Log.e("feige", "��ȡ����ip��ַʧ��");
			e.printStackTrace();
		}
		return ipaddress;

	}

	// �õ�����Mac��ַ
	public static String getLocalMac(Context context)
	{
		String mac = "";
		// ��ȡwifi������
		WifiManager wifiMng = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfor = wifiMng.getConnectionInfo();
		mac = "������mac��ַ�ǣ�" + wifiInfor.getMacAddress();
		return mac;
	}

	/**
	 * ��̬����listview�ĸ߶ȣ���������Ŀ�ĸ���
	 * 
	 * @param listView
	 */
	public static void setListViewHeight(ListView listView)
	{

		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null)
		{
			return;
		}

		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		// ((MarginLayoutParams) params).setMargins(10, 10, 10, 10); // ��ɾ��

		listView.setLayoutParams(params);
	}

	/**
	 * ���ر���ͼƬ http://bbs.3gstdy.com
	 * 
	 * @param url
	 * @return
	 */

	public static Bitmap getLoacalBitmap(String url)
	{

		try
		{

			FileInputStream fis = new FileInputStream(url);

			return BitmapFactory.decodeStream(fis);

		}
		catch (FileNotFoundException e)
		{

			e.printStackTrace();

			return null;

		}

	}

	/**
	 * �ж�������Ƿ����ֻ�����
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumberValid(String phoneNumber)
	{
		Pattern p = Pattern.compile("^1\\d{10}$");
		Matcher m = p.matcher(phoneNumber);
		return m.matches();
	}

	/**
	 * �ж�������Ƿ�6-10λ�����ֻ���ĸ
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isNumOrZValid(String phoneNumber)
	{
		Pattern p = Pattern.compile("^[A-Za-z0-9]{6,10}$");
		Matcher m = p.matcher(phoneNumber);
		return m.matches();

	}

	public static String load(Context context)
	{
		String jsonData = null;
		String saveDate = null;
		try
		{
			FileInputStream inStream = context
					.openFileInput(MyConstants.SAVE_JSON);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1)
			{
				stream.write(buffer, 0, length);
			}
			stream.close();
			inStream.close();
			saveDate = stream.toString();
			jsonData = EncyrptUtils.decrypt(MyConstants.PREFERENCE_NAME,
					saveDate);
			// text.setText(stream.toString());
			// Toast.makeText(MyActivity.this,��Loaded��,Toast.LENGTH_LONG).show();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return jsonData;
	}

	public static void saveData(String json, Context context)
	{
		FileOutputStream out = null;
		try
		{
			String encyrpStr = EncyrptUtils.encrypt(
					MyConstants.PREFERENCE_NAME, json);
			out = context.openFileOutput(MyConstants.SAVE_JSON,
					Context.MODE_PRIVATE);
			out.write(encyrpStr.getBytes("UTF-8"));
			out.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�ַ������ȣ�����������Ӣ��һ��
	 * 
	 * @param value
	 * @return
	 */
	public static int String_length(String value)
	{
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++)
		{
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese))
			{
				valueLength += 2;
			}
			else
			{
				valueLength += 1;
			}
		}
		return valueLength;
	}
	

	/**
	 * ��ȡApp��ǰ�汾��
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			return pinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
}
