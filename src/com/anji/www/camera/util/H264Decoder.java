package com.anji.www.camera.util;

import java.nio.ByteBuffer;

import android.util.Log;

public class H264Decoder {
	public static final int COLOR_FORMAT_YUV420 = 0;
	public static final int COLOR_FORMAT_RGB565LE = 1;
	public static final int COLOR_FORMAT_BGR32 = 2;
	/** 是否已经初始化了 */
	private boolean isInited = false;

	public H264Decoder() {
	}

	public void DecoderInit(int colorFormat, int id) {
		nativeInit(colorFormat, id);
		isInited = true;
	}

	public void Destory(int id) {
		if (isInited) {
			isInited = false;
			nativeDestroy(id);
		}
	}

	private int cdata;

	public native void nativeInit(int colorFormat, int id);

	private native void nativeDestroy(int id);

	public native int consumeNalUnitsFromDirectBuffer(byte[] nalUnits,
			int numBytes, long packetPTS, int id);

	public native boolean isFrameReady(int id);

	public native int getWidth(int id);

	public native int getHeight(int id);

	public native int getOutputByteSize(int id);

	public native long decodeFrameToDirectBuffer(ByteBuffer buffer, int id);

	static {
		try {
			System.loadLibrary("videodecoder");
		} catch (Exception e) {
			Log.e("SDK", "Load videodecoder fail:" + e.getMessage());
		}
	}
}
