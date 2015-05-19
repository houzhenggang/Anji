package com.anji.www.camera.util;


import com.fos.sdk.FosSdkJNI;
import com.fos.sdk.FrameData;
import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 声音播放线程
 * 
 * @author FuS
 * @date 2014-5-8 下午5:43:40
 */
@SuppressLint("NewApi")
public class AudioThread extends Thread {
	private AudioTrack mAudioTrack;
	/** 底层获取的数据（包含了音频、视频、图片数据） */
	private FrameData mFramedata;

	public boolean isPlayAudio = false;
	/** 音频率 */
	final int sampleRateInHz = 8000;
	/** 默认的音频数据buff大小 */
	final int defalutBufSize = 1486;

	private Handler mHandler ;
	
	public Integer len = 0;
	
	public AudioThread() {
	}

	/** 初始化 */
	public void init() {	
		Global.mHandler = mHandler;
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == Msg.GET_AUDIO_DATA_SUCC){
					mFramedata = (FrameData) msg.obj;
				}
				super.handleMessage(msg);
			}
		};
		mFramedata = new FrameData();
		mFramedata.data = new byte[1024*1024*3];
		int minBufSize = AudioTrack.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

		// "getMinBufferSize(): error querying hardware";
		if ((minBufSize == -1) || (minBufSize == 0)) {
			minBufSize = defalutBufSize;
		}

	//	mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize * 4, AudioTrack.MODE_STREAM);

		if (android.os.Build.VERSION.SDK_INT >= 16 && Global.mAudioRecord != null) 
		{ 
			mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize * 4, AudioTrack.MODE_STREAM, Global.mAudioRecord.getAudioSessionId()); 
		} 
		else 
		{ 
			mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize * 4, AudioTrack.MODE_STREAM); 
		} 
		
		if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
			this.init();
			mAudioTrack.play();
		} else {
			mAudioTrack.play();
		//	PausePlay();
		}
	}
	
	
	

	@Override
	public void run() {
		while (Global.isAudioOpenOrNot) {
			FosSdkJNI.GetAudioData(Global.mHandlerNo, mFramedata);
	    	Log.i("jerry", "I  get len ============ "+ len);
			if(mFramedata != null){
					// 声音播放一段时间后，会有延迟，随时间长短，延迟有大小。考虑清理缓存数据
					if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED) {
			    			mAudioTrack.write(mFramedata.data, 0, mFramedata.len);
			    			Log.i("jerry", "wanwan..............======");				    
					}		
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.run();
	}

	public void PausePlay() {
		if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
			mAudioTrack.pause();
		}
	}

	public void ResumePlay() {
		if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
			mAudioTrack.play();
		}
	}

	public void StopRun() {
		if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
			mAudioTrack.stop();
			mAudioTrack.release();
		}
	}
}
