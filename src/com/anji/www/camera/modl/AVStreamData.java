package com.anji.www.camera.modl;

public class AVStreamData {
	public int frameType; // 0=video 1=audio 3=snap
	public int videoFormat; // 0=H264 1=MJPEG
	public byte[] data;
	public int dataLen;
	/** 1关键帧，0非关键帧（每隔一定时间会有一个关键帧，此时间间隔可设置，每隔关键帧后面都是若干个非关键帧，H264才有关键帧与非关键帧） */
	public int isKeyFrame;
	public int videoWidth;
	public int videoHeight;
	public int reserve1;
	public int reserve2;
	public int reserve3;
	public int reserve4;
}
