/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ipc.sdk;

import android.util.Log;

import com.ipc.sdk.AVStreamData;
import com.ipc.sdk.StatusListener;


public class FSApi 
{	
	
	private static StatusListener mListener;
	
	/**
	 * You must start a new thread to get the statusCallback constantly.
	 * 
	 */
	
	
	private static void StatusCbk(int statusID, int reserve1, int reserve2, int reserve3, int reserve4)
	{
		
		mListener.OnStatusCbk( statusID, reserve1, reserve2, reserve3, reserve4 );
	}
	
	public static void setStatusListener(StatusListener listener)
	{
		mListener = listener;
	}
	/**
	   * Initialize SDK when application start.
	   *
	   */
	public static native int Init();
	
	/**
	 * Release the SDK resources.
	 */
	public static native int Uninit();
    
    /**
     * Start search cameras in your LAN .
     */
    public static native int searchDev();
   
    /**
     * Get the Device List in you LAN.
     * @return the DevInfo Array.
     */
    public static native DevInfo[] getDevList();
    
    public static native int getStatusId(int id);
	
	/**Login to Camera.
     * 
     * @param devType     the device type(0 is MJPEG,1 is H264).
     * @param ip          the device IP address.
     * @param userName    the device UserName.
     * @param password    the device PassWord.
     * @param streamType  the StreamType you want.(0 is sub , 1 is main)
     * @param webPort     the device WebPort.
     * @param mediaPort   the device MediaPort.(In new models ,this port is canceled)
     * @param uid         the device UID.
     * @param id          the device channel id.
     * @return   Return immediately, login result will be found in StatusCallback
     */
	public static native int usrLogIn(int devType, String ip, String userName, String password, int streamType, int webPort, int mediaPort, String uid,int id);
	
	/**Logout to Camera.
	 * 
	 * @param id   the device channel id.
	 */
    public static native int  usrLogOut(int id);
    
    /**Control the camera to move up.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzMoveUp(int id);
    
    /**Control the camera to move down.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzMoveDown(int id);
  
    /**Control the camera to move left.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzMoveLeft(int id);
  
    /**Control the camera to move right.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzMoveRight(int id);
  
    /**Control the camera to move topLeft.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzMoveTopLeft(int id);
  
    /**Control the camera to move topRight.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzMoveTopRight(int id);
  
    /**Control the camera to move bottomLeft.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzMoveBottomLeft(int id);
   
    /**Control the camera to move bottomRight.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzMoveBottomRight(int id);
   
/**Stop the camera move.
     * 
     * @param id   the device channel id.
     */
    public static native int  ptzStopRun(int id);
    
    /**Start  video .
     * 
     * @param id the device channel id.
     */
    public static native int startVideoStream(int id);
   
    /**Get the video stream data.
     * 
     * @param streamData   the AVStreamData object to store the video data.
     * @param id           the device channel id.
     */
    public static native int getVideoStreamData(AVStreamData streamData,int id);
   
    /**Stop  video .
     * 
     * @param id  the device channel id.
     */
    public static native int stopVideoStream(int id);
  
    /**start  audio .
     * 
     * @param id the device channel id.
     */
    public static native int startAudioStream(int id);
  
    /**Get the audio stream data .
     * 
     * @param streamData  the AVStreamData object to store the audio data.
     * @param id          the device channel id.
     */
    public static native int getAudioStreamData(AVStreamData streamData,int id);
   
    /**Stop  audio .
     * 
     * @param id  the device channel id.
     */
    public static native int stopAudioStream(int id);
   
    /**Start  talk .
     * 
     * @param id the device channel id.
     */
    public static native int startTalk(int id);
  
    /**Send the talk Frame to device.
     * 
     * @param frame      the talk data.
     * @param frameLen   the length of talk data.
     * @param id         the device channel id.
     */
    public static native int sendTalkFrame(byte[] frame, int frameLen,int id);
    
    /**Stop  Talk .
     * 
     * @param id  the device channel id.
     */
    public static native int stopTalk(int id);
    
    /**Snap picture and save to SD card.
     * 
     * @param saveDir   the snap save path(must contains the picture suffix).
     * @param id        the device channel id.
     */
    public static native int snapPic(String saveDir,int id);
    
    
    
    /**Start Record .
     * 
     * @param dir       the save folder where you will save.(The SD card available  size must be more than 256 MB)
     * @param saveDir   the Record file name.(Should be like *.AVI)
     * @param id        the device channel id.
     */
    public static native int StartRecord(String dir,String fileName,int id);
    
    /**Stop Record.
     * 
     * @param id        the device channel id.
     * 
     */
    public static native int StopRecord(int id);
   
   static {
    	try{
    		System.loadLibrary("IOTCAPIs"); 
    	}catch(UnsatisfiedLinkError ule)
    	{
    		Log.d("moon", ule.getMessage() );
    	}
    	try{
    		System.loadLibrary("RDTAPIs"); 
    	}catch(UnsatisfiedLinkError ule){
    		Log.d("moon", ule.getMessage() );
    	}
    	try{
    		System.loadLibrary("FSApi"); 
    	}catch(UnsatisfiedLinkError ule){
    		Log.d("moon", ule.getMessage() );
    	}
    }

}
