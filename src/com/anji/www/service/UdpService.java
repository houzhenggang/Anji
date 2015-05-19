package com.anji.www.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.anji.www.activity.MainActivity;
import com.anji.www.activity.TabSense;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.util.LogUtil;
import com.anji.www.util.Utils;

public class UdpService
{

	private static final String TAG = "UdpService";
	private static UdpService myService;
	private Handler myHandler;
	public static final int DEFAULT_PORT = 6000;
	public static final String broadcastIp = "255.255.255.255";
	private static final int MAX_DATA_PACKET_LENGTH = 255;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	private DatagramSocket udpSocket = null;
	private DatagramPacket udpPacket = null;
	private String deviceIp;

	public String getDeviceIp()
	{
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp)
	{
		this.deviceIp = deviceIp;
	}

	private String deviceMac;
	private byte[] deviceAddress;
	private boolean isListening = false;
	public final static int ORDRE_SREACH_GATEWAY = 0XF0;// ��������
	public final static int ORDRE_SREACH_DEVICE = 0XF1;// �����豸
	public final static int ORDRE_ONE_CONTROL = 0XF2;// ���豸����
	public final static int ORDRE_ONE_READ = 0XF3;// ���豸��ȡ
	public final static int ORDRE_ONE_SET = 0XF4;// ���豸����
	public final static int ORDRE_CAN_JOIN = 0XF5;// �������ؿ�ɨ��ͽ���
	public final static int SEND_ORDER_FAIL = 10;// ����ʧ�ܣ�
	public final static int SEND_NULL_FAIL = 11;// ip����updsocketΪ��

	// private ArrayList<DeviceInfo> switchDeviceList;
	// private ArrayList<DeviceInfo> senceDeviceList;
	// private ArrayList<DeviceInfo> TabSense.humitureList;;// ��ʪ��

	// private ArrayList<DeviceInfo> senceSomkeList; // ����
	// private ArrayList<DeviceInfo> senceInfraredList;// ����
	// private ArrayList<DeviceInfo> senceWearableList;// ����

	private UdpService(Handler handler)
	{
		try
		{

			// setswitchDeviceList(new ArrayList<DeviceInfo>());
			// setSenceDeviceList(new ArrayList<DeviceInfo>());
			// setSenceHumitureList(new ArrayList<DeviceInfo>());
			// setSenceSomkeList(new ArrayList<DeviceInfo>());
			// setSenceInfraredList(new ArrayList<DeviceInfo>());
			// setSenceWearableList(new ArrayList<DeviceInfo>());
			udpSocket = new DatagramSocket(DEFAULT_PORT);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		if (handler == null)
		{
			handler = new Handler();
		}
		setMyHandler(handler);
		isListening = true;
		if (udpSocket != null)
		{
			udpListerner.start();
		}

	}

	private Thread udpListerner = new Thread()
	{
		public void run()
		{
			if (udpSocket != null)
			{

				// byte[] data = new byte[MAX_DATA_PACKET_LENGTH];
				udpPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
				while (isListening)
				{
					try
					{

						udpSocket.receive(udpPacket);
						Log.i(TAG, "start-----receive");
					}
					catch (Exception e)
					{
						// ����ʧ��
						e.printStackTrace();
					}
					String myIp = Utils.getLocalHostIp();
					Log.i(TAG, "myIp=" + myIp);
					deviceIp = udpPacket.getAddress().toString();
					if (!TextUtils.isEmpty(deviceIp))
					{

						deviceIp = deviceIp.replaceAll("/", "");
						Log.i(TAG, "deviceIp=" + deviceIp);
						if (!deviceIp.equals(myIp))
						{
							Log.i(TAG,
									"udpPacket.getData();="
											+ Utils.bytesToHexString(udpPacket
													.getData()));
							Log.i(TAG,
									"udpPacket.getLength()="
											+ udpPacket.getLength());
							// byte[] arr = new byte[udpPacket.getLength()];
							// for (int i = 0; i < udpPacket.getLength(); i++)
							// {
							// arr[i] = udpPacket.getData()[i];
							// }
							byte[] arr = new byte[udpPacket.getLength()];
							for (int i = 0; i < udpPacket.getLength(); i++)
							{
								arr[i] = buffer[i];
							}
							Log.i(TAG, "arr=" + Utils.bytesToHexString(arr));
							LogUtil.LogI(TAG, "arr[0]=" + arr[0]);
							LogUtil.LogI(TAG, "arr[1]=" + arr[1]);
							if (arr[0] == 0x20)
							{
								switch (arr[1])
								{
								case (byte) 0xF0:
									if (arr.length == 15)
									{
										LogUtil.LogI(TAG, "0xF0");
										// ��ʾ����׼������
										try
										{
											Thread.sleep(300);
										}
										catch (InterruptedException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										byte[] macsAddress = new byte[8];
										for (int i = 6; i < 14; i++)
										{
											macsAddress[i - 6] = arr[i];
										}
										deviceAddress = macsAddress;
										LogUtil.LogI(
												TAG,
												"deviceAddress="
														+ Utils.bytesToHexString(macsAddress));
										setDeviceMac(Utils
												.bytesToHexString(macsAddress));
										myHandler.obtainMessage(
												ORDRE_SREACH_GATEWAY, arr)
												.sendToTarget();
									}
									break;
								case (byte) 0xF1:
									LogUtil.LogI(TAG, "0xF1");
									// �������ĳ���û������
									analysisF1Order(arr);
									myHandler.obtainMessage(
											ORDRE_SREACH_DEVICE, arr)
											.sendToTarget();
									break;
								case (byte) 0xF2:
									LogUtil.LogI(TAG, "0xF2");
									// F2�ǿ���ָ�� ���һλ��ʾ aa���� bb�޴��豸 cc����ͳɹ�
									// dd�����ʧ��
									if (arr.length == 18)
									{
										// ����Ϊ17����ʾ�յ�����ȷ�ķ��ص�����
										myHandler.obtainMessage(
												ORDRE_ONE_CONTROL, arr)
												.sendToTarget();
									}
									break;
								case (byte) 0xF3:
									LogUtil.LogI(TAG, "0xF3");
									if (arr[4] + 4 == arr.length)
									{
										// �����޴���
										analysisF3Order(arr);
									}
									myHandler
											.obtainMessage(ORDRE_ONE_READ, arr)
											.sendToTarget();
									break;
								case (byte) 0xF5:
									LogUtil.LogI(TAG, "0xF5");
									LogUtil.LogI(TAG, "arr[5]=" + arr[5]);
									if (arr[5] == 0)
									{
										// ִ�н���ɹ�
										myHandler.obtainMessage(ORDRE_CAN_JOIN,
												arr).sendToTarget();
									}
									break;

								default:
									break;
								}
							}
							// if
							// (Utils.bytesToHexString(arr).contains("20F50103000101"))
							// byte[] bytes = new byte[]
							// { (byte) 0x20, (byte) 0xF0, (byte) 0x01,
							// (byte) 0x9, (byte) 0x01, (byte) 0xff,
							// (byte) 0xff, (byte) 0xff, (byte) 0xff,
							// (byte) 0xff, (byte) 0xff, (byte) 0xff,
							// (byte) 0xff };
							// dataPacket = new DatagramPacket(buffer,
							// MAX_DATA_PACKET_LENGTH);
							// // byte[] data = dataString.getBytes();
							// dataPacket.setData(bytes);
							// dataPacket.setLength(bytes.length);
							// dataPacket.setPort(DEFAULT_PORT);
							//
							// InetAddress serviceAddr;
							//
							// try
							// {
							// serviceAddr = InetAddress.getByName(getIp);
							// dataPacket.setAddress(serviceAddr);
							// udpSocket.send(dataPacket);
							// sleep(10);
							// }
							// catch (Exception e)
							// {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }
							// }
							// label.post(new Runnable()
							// {
							//
							// @Override
							// public void run()
							// {
							// label.append("�յ����ԣ�" + quest_ip
							// + "UDP���󡣡���\n");
							// label.append("�������ݣ�" + codeString + "\n\n");
							//
							// }
							// });
						}
					}
				}
			}
		}

	};

	private void analysisF1Order(byte[] arr)
	{
		try
		{

			if (arr[6] * 12 + 8 == arr.length)
			{
				// TODO ����
				byte[][] data;
				int len = arr[6];
				// int len = (arr.length-7) / 8;
				data = new byte[len][12];
				for (int j = 7; j < arr.length - 1; j++)
				{
					if (j < 19)
					{
						data[0][j - 7] = arr[j];
					}
					else
					{
						data[(j - 7) / 12][(j - 7) % 12] = arr[j];
					}
				}
				for (int j2 = 0; j2 < data.length; j2++)
				{
					analysisDevice(data[j2]);
				}
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	};

	/**
	 * ������ѯ�����������ķ���ֵ
	 * 
	 * @param arr
	 */
	protected void analysisF3Order(byte[] arr)
	{
		try
		{

			DeviceInfo info;
			byte[] macAddress = new byte[8];
			for (int i = 6; i < 14; i++)
			{
				macAddress[i - 6] = arr[i];
			}
			String macStr = Utils.bytesToHexString(macAddress);
			int channel = arr[14];
			byte state = 0;
			if (arr.length >= 18)
			{
				state = arr[17];
			}
			if (state != (byte) 0XBB)// bbΪ�޴��豸��
			{

				String deviceType = Utils.bytesToHexString(new byte[]
				{ arr[15], arr[16] });
				LogUtil.LogI(TAG, "deviceType=" + deviceType);
				byte battery;
				if (deviceType.equals("0F02"))
				{
					LogUtil.LogI(TAG, "�����ֻ�");
					info = checkIsExist(MainActivity.sensorList, macStr, channel);
					if (null == info)
					{
						info = new DeviceInfo();
						MainActivity.sensorList.add(info);
					}
					// �����ֻ�
					battery = arr[20];
					if (arr[18] == 2)
					{

						info.setSensorState(arr[19]);
					}
				}
				else if (deviceType.equals("1001") || deviceType.equals("1002"))
				{
					// ��ʪ��
					LogUtil.LogI(TAG, "��ʪ��");
					info = checkIsExist2(MainActivity.sensorList, macStr, channel);
					if (null == info)
					{
						LogUtil.LogI(TAG, "�����ʪ��");
						info = new DeviceInfo();
						MainActivity.sensorList.add(info);
					}
					battery = arr[21];
					if (arr[18] == 3)
					{
						if (deviceType.equals("1001"))
						{
							float value = Float.parseFloat(String.valueOf(arr[19])
									+ "." + arr[20]);
							LogUtil.LogI(TAG, "1001  value = " + value);
							info.setHumValue(value);
							LogUtil.LogI(TAG,
									"info  HumValue = " + info.getHumValue());
							info.setDeviceChannel2(channel);
						}else if(deviceType.equals("1002")){

							float value = Float.parseFloat(String.valueOf(arr[19])
									+ "." + arr[20]);
							LogUtil.LogI(TAG, "1002  value = " + value);
							info.setTempValue(value);
							LogUtil.LogI(TAG,
									"info  temValue = " + info.getTempValue());
						}
					}
				}
				else if (deviceType.equals("1002"))
				{
					// �¶�
					LogUtil.LogI(TAG, "�¶�");
					info = checkIsExist(MainActivity.sensorList, macStr, channel);
					if (null == info)
					{
						info = new DeviceInfo();
						MainActivity.sensorList.add(info);
					}
					battery = arr[21];
					if (arr[18] == 3)
					{
						float value = Float.parseFloat(String.valueOf(arr[19])
								+ "." + arr[20]);
						LogUtil.LogI(TAG, "1002  value = " + value);
						info.setTempValue(value);
						LogUtil.LogI(TAG,
								"info  temValue = " + info.getTempValue());
					}
				}
				else if (deviceType.equals("1003"))
				{
					// �������
					LogUtil.LogI(TAG, "������� ״̬  arr[19]= " + arr[19]);

					info = checkIsExist(MainActivity.sensorList, macStr, channel);
					if (null == info)
					{
						info = new DeviceInfo();
						MainActivity.sensorList.add(info);
					}
					battery = arr[20];
					if (arr[18] == 2)
					{

						info.setSensorState(arr[19]);
					}
				}
				else
				{
					// ����
					LogUtil.LogI(TAG, "����");
					info = checkIsExist(MainActivity.sensorList, macStr, channel);
					if (null == info)
					{
						info = new DeviceInfo();
						MainActivity.sensorList.add(info);
					}
					battery = arr[20];
					if (arr[18] == 2)
					{
						info.setSensorState(arr[19]);
					}
				}
				info.setDeviceMac(macStr);
				info.setDeviceChannel(channel);
				info.setDeviceState(state);
				info.setDeviceType(deviceType);
				if (battery < 0)
				{
					info.setCharge(true);
				}
				else
				{
					info.setCharge(false);
				}
				battery = (byte) (battery & 0x7f);
				info.setDeviceBattery(battery);
				LogUtil.LogI(TAG, "info  getHumValue() = " + info.getHumValue());
				LogUtil.LogI(TAG, "info  TempValue() = " + info.getTempValue());
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	public static synchronized UdpService newInstance(Handler handler)
	{

		if (myService == null)
		{
			myService = new UdpService(handler);
		}
		Log.i(TAG, "socketClient =" + myService);
		return myService;
	}

	/***
	 * �����豸��Ϣ
	 */
	protected void analysisDevice(byte[] arr)
	{

		try
		{
			if (arr.length == 12)
			{
				DeviceInfo info = null;
				byte[] mac = new byte[8];
				for (int i = 0; i < 8; i++)
				{
					mac[i] = arr[i];
				}
				String macAddaress = Utils.bytesToHexString(mac);
				int channel = arr[8];
				String deviceType = Utils.bytesToHexString(new byte[]
				{ arr[9], arr[10] });
				LogUtil.LogI(TAG, "deviceType=" + deviceType);
				LogUtil.LogI(TAG, "deviceState=" + arr[11]);

				if (deviceType.equals("0001") || deviceType.equals("0002")
						|| deviceType.equals("0101"))
				{
					// setDeviceInof(switchDeviceList, arr, macAddaress,
					// channel,
					// deviceType, info);
					info = checkIsExist(MainActivity.switchList, macAddaress,
							channel);
					if (null == info)
					{
						// ��������ڣ��ͼӽ�ȥ
						// info = new DeviceInfo();
						// MainActivity.switchList.add(info);
					}
					else
					{
						info.setDeviceMac(macAddaress);
						info.setDeviceChannel(channel);
						info.setDeviceType(deviceType);
						info.setDeviceState(arr[11]);
						info.setType(0);
					}
				}
				else
				{
					// // setDeviceInof(senceDeviceList, arr, macAddaress,
					// channel,
					// // deviceType, info);
					// info = checkIsExist(MainActivity.sensorList, macAddaress,
					// channel);
					// if (null == info)
					// {
					// // ��������ڣ��ͼӽ�ȥ
					// info = new DeviceInfo();
					// // MainActivity.sensorList.add(info);
					// }
					// info.setDeviceMac(macAddaress);
					// info.setDeviceChannel(channel);
					// info.setDeviceType(deviceType);
					// info.setDeviceState(arr[11]);
					// info.setType(0);
					// if (deviceType.equals("0F02"))
					// {
					// if (null == checkIsExist(TabSense.wearableList,
					// macAddaress, channel))
					// {
					// // TabSense.wearableList.add(info);
					// }
					// // �����ֻ�
					// }
					// else if (deviceType.equals("1001")
					// || deviceType.equals("1002"))
					// {
					// // ��ʪ��
					// LogUtil.LogI(TAG, "TabSense.humitureList;.size="
					// + TabSense.humitureList.size());
					// if (null == checkIsExist(TabSense.humitureList,
					// macAddaress, channel))
					// {
					// // TabSense.humitureList.add(info);
					// }
					// }
					// else if (deviceType.equals("1003"))
					// {
					// // �������
					// if (null == checkIsExist(TabSense.infaredList,
					// macAddaress, channel))
					// {
					// // TabSense.infaredList.add(info);
					// }
					// }
					// else
					// {
					// // ����
					// // �������
					// if (null == checkIsExist(TabSense.somkeList,
					// macAddaress, channel))
					// {
					// // TabSense.somkeList.add(info);
					// }
					// }
				}
				LogUtil.LogI(TAG, "MainActivity.switchList.size="
						+ MainActivity.switchList.size());
				LogUtil.LogI(TAG, "MainActivity.sensorList.size="
						+ MainActivity.sensorList.size());
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	// private void setDeviceInof(ArrayList<DeviceInfo> list, byte[] arr,
	// String macAddaress, int channel, String deviceType, DeviceInfo info)
	// {
	// info = checkIsExist(list, macAddaress, channel);
	// if (null == info)
	// {
	// // ��������ڣ��ͼӽ�ȥ
	// info = new DeviceInfo();
	// list.add(info);
	// }
	// info.setDeviceMac(macAddaress);
	// info.setDeviceChannel(channel);
	// info.setDeviceType(deviceType);
	// info.setDeviceState(arr[11]);
	// info.setType(0);
	// }

	private DeviceInfo checkIsExist(List<DeviceInfo> list, String mac,
			int channel)
	{
		LogUtil.LogI(TAG, "list.size=" + list.size());
		if (list != null && list.size() > 0)
		{

			for (int i = 0; i < list.size(); i++)
			{
				LogUtil.LogI(TAG, "mac= " + mac);
				LogUtil.LogI(TAG, "list= " + list);
				LogUtil.LogI(TAG, "list.get(i)" + list.get(i));
				LogUtil.LogI(TAG, "list.get(i).getDeviceMac()"
						+ list.get(i).getDeviceMac());

				if (mac.equals(list.get(i).getDeviceMac())
						&& channel == list.get(i).getDeviceChannel())
				{

					return list.get(i);
				}
			}
		}
		return null;
	}

	private DeviceInfo checkIsExist2(List<DeviceInfo> list, String mac,
			int channel)
	{
		LogUtil.LogI(TAG, "list.size=" + list.size());
		if (list != null && list.size() > 0)
		{

			for (int i = 0; i < list.size(); i++)
			{
				LogUtil.LogI(TAG, "mac= " + mac);
				LogUtil.LogI(TAG, "channel= " + channel);
				LogUtil.LogI(TAG, "list.get(i)" + list.get(i));
				LogUtil.LogI(TAG, "list.get(i).getDeviceMac()"
						+ list.get(i).getDeviceMac());
				LogUtil.LogI(TAG,
						"list.get(i).getDeviceChannel2()"
								+ list.get(i).getDeviceChannel2());

				if (mac.equals(list.get(i).getDeviceMac()))
				{

					return list.get(i);
				}
			}
		}
		return null;
	}

	public Handler getMyHandler()
	{
		return myHandler;
	}

	public void setMyHandler(Handler myHandler)
	{
		this.myHandler = myHandler;
	}

	public void sendBroadCastUdp(final byte[] bytes)
	{
		sendOrders(bytes, broadcastIp);
	}

	public void sendOrders(final byte[] bytes)
	{
		sendOrders(bytes, deviceIp);
	}

	public void sendOrders(final byte[] bytes, final String ip)
	{
		new Thread()
		{
			public void run()
			{
				if (udpSocket != null && ip != null)
				{
					DatagramPacket dataPacket = null;
					// F5ָ�� �����¿ͻ����������غͼ�������
					// byte[] bytes = new byte[]
					// { (byte) 0x20, (byte) 0xF5, (byte) 0x01, (byte) 0x2,
					// (byte) 0x01,
					// (byte) 0x01 };
					dataPacket = new DatagramPacket(buffer,
							MAX_DATA_PACKET_LENGTH);
					// byte[] data = dataString.getBytes();
					dataPacket.setData(bytes);
					dataPacket.setLength(bytes.length);
					dataPacket.setPort(DEFAULT_PORT);

					InetAddress broadcastAddr;

					try
					{
						broadcastAddr = InetAddress.getByName(ip);
						dataPacket.setAddress(broadcastAddr);
						udpSocket.send(dataPacket);
						LogUtil.LogI(TAG, "��������broadcastAddr=" + broadcastAddr);
					}
					catch (Exception e)
					{
						// ����ʧ��
						LogUtil.LogI(TAG, "��������ʧ��");
						myHandler.sendEmptyMessage(SEND_ORDER_FAIL);
						e.printStackTrace();
					}
				}
				else
				{
					myHandler.sendEmptyMessage(SEND_NULL_FAIL);
				}
			};
		}.start();
	}

	// ��ȡ���ص�ַ
	private String getLocalIPAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch (SocketException ex)
		{
			Log.e(TAG, ex.toString());
		}
		return null;
	}

	public boolean isListening()
	{
		return isListening;
	}

	public void setListening(boolean isListening)
	{
		this.isListening = isListening;
	}

	public String getDeviceMac()
	{
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac)
	{
		this.deviceMac = deviceMac;
	}

	// public ArrayList<DeviceInfo> getSwitchDeviceList()
	// {
	// return switchDeviceList;
	// }
	//
	// public void setswitchDeviceList(ArrayList<DeviceInfo> deviceList)
	// {
	// this.switchDeviceList = deviceList;
	// }
	//
	// public ArrayList<DeviceInfo> getSenceDeviceList()
	// {
	// return senceDeviceList;
	// }
	//
	// public void setSenceDeviceList(ArrayList<DeviceInfo> senceDeviceList)
	// {
	// this.senceDeviceList = senceDeviceList;
	// }

	// public ArrayList<DeviceInfo> getSenceHumitureList()
	// {
	// return senceHumitureList;
	// }
	//
	// public void setSenceHumitureList(ArrayList<DeviceInfo> senceHumitureList)
	// {
	// this.senceHumitureList = senceHumitureList;
	// }
	//
	// public ArrayList<DeviceInfo> getSenceSomkeList()
	// {
	// return senceSomkeList;
	// }
	//
	// public void setSenceSomkeList(ArrayList<DeviceInfo> senceSomkeList)
	// {
	// this.senceSomkeList = senceSomkeList;
	// }
	//
	// public ArrayList<DeviceInfo> getSenceInfraredList()
	// {
	// return senceInfraredList;
	// }
	//
	// public void setSenceInfraredList(ArrayList<DeviceInfo> senceInfraredList)
	// {
	// this.senceInfraredList = senceInfraredList;
	// }
	//
	// public ArrayList<DeviceInfo> getSenceWearableList()
	// {
	// return senceWearableList;
	// }
	//
	// public void setSenceWearableList(ArrayList<DeviceInfo> senceWearableList)
	// {
	// this.senceWearableList = senceWearableList;
	// }

}
