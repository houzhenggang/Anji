package com.anji.www.camera.view;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import com.anji.www.camera.util.Global;
import com.fos.sdk.FosSdkJNI;
import com.fos.sdk.FrameData;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 视频播放控件
 * 
 * @author FuS
 * @date 2014-5-5 下午8:52:01
 */
public class VideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	final static String TAG = "VideoSurfaceView";
	/** image width */
	final int maxWidth = 2000;
	/** image height */
	final int maxHeight = 2000;

	private SurfaceHolder sfh;
	private Canvas mCanvas;

	public int dlen = 0;
	/** ipc的连接的句柄 */
	public int cameraHandlerNo = 0;
	/** 视频数据 */
	private FrameData videoData = null;
	/** 视频数据Buff */
	private ByteBuffer buffer = null;

	/** lock the bitmap */
	public static Lock mLock = new ReentrantLock();

	private DrawThread mDrawThread;
	private boolean isDraw = false;
	/** 收到视频数据时，描述是否为第一次收到数据 */
	public boolean isFirstGetData = true;

	/** 要裁剪的图片区域，如果为Null，则默认显示整个图片 */
	private Rect src;
	/** 图片显示在画布上的区域（居中显示） */
	private Rect dst;
	private Bitmap mBit = null;
	public Bitmap getmBit() {
		return mBit;
	}

	public void setmBit(Bitmap mBit) {
		this.mBit = mBit;
	}

	private Bitmap mDrawBit = null;

	/** surface view width */
	public int surfaceWidth = 0;
	/** surface view height */
	public int surfaceHeight = 0;
	/** 最终显示图片的宽 */
	private int imgWidth = 0;
	/** 最终显示图片的高 */
	private int imgHeight = 0;

	public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoSurfaceView(context);
	}

	public VideoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initVideoSurfaceView(context);
	}

	public VideoSurfaceView(Context context) {
		super(context);
		initVideoSurfaceView(context);
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		startDraw();
	}

	public void startDraw() {
		isDraw = true;
		videoData = new FrameData();

		mDrawThread = new DrawThread();
		mDrawThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		initVideoSurfaceViewWH();
		if (mBit != null) {
			calcImgSize(mBit.getWidth(), mBit.getHeight());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		stopDraw();
	}

	public void stopDraw() {
		// 关闭线程
		if (videoData != null) {
			videoData.data = null;
			videoData = null;
		}
		isDraw = false;
		if (mDrawThread != null) {
			while (true) {
				try {
					mDrawThread.join();
					break;
				} catch (InterruptedException e) {
					// retry
				}
			}
		}
	}

	/** initialize surface view */
	private void initVideoSurfaceView(Context context) {
		sfh = this.getHolder();
		sfh.addCallback(this);
		mCanvas = new Canvas();
		mBit = null;
		src = new Rect();
		dst = new Rect();
		

		initVideoSurfaceViewWH();
	}

	/** 初始化控件的宽高 */
	private void initVideoSurfaceViewWH() {
		surfaceWidth = this.getWidth();
		surfaceHeight = this.getHeight();
	}

	/**
	 * 根据surface的实际宽高，设置显示图片的宽高
	 * <p>
	 * 对图片的宽高进行等比缩放后，有三种情况：<br>
	 * 1.图片的宽高等于surface的宽高；<br>
	 * 2.图片的宽比surface的宽 大于 图片的高比surface的高（此时应该以surface的宽为基础，对图片的宽高进行缩放）；<br>
	 * 3.图片的高比surface的高 大于 图片的宽比surface的宽（此时应该以surface的高为基础，对退片的宽高进行缩放）；
	 * 
	 * @param srcPicWidth
	 *            原始图片的宽
	 * @param srcPicHeight
	 *            原始图片的高
	 */
	private void calcImgSize(int srcPicWidth, int srcPicHeight) {
		if (surfaceHeight * srcPicWidth >= surfaceWidth * srcPicHeight) {// 按surfaceWidth来计算实际图片的高
			imgWidth = surfaceWidth;
			imgHeight = surfaceWidth * srcPicHeight / srcPicWidth;
		} else {// 按surfaceHeight来计算实际图片的高
			imgHeight = surfaceHeight;
			imgWidth = surfaceHeight * srcPicWidth / srcPicHeight;
		}
	}

	/** draw thread */
	class DrawThread extends Thread {
		@Override
		public void run() {
			
			while (isDraw) {
				if (!sfh.getSurface().isValid()) {
					break;
				}
				try {
					mCanvas = sfh.lockCanvas();
					dlen = FosSdkJNI.GetVideoData(Global.mHandlerNo, videoData, 2);
				//	if(dlen > 0)	{
				
				//	}
					if(videoData.len > 0)
					{
						
						buffer = ByteBuffer.wrap(videoData.data);
					//	Log.i(TAG, "buffer capacity"+ buffer.capacity());
						int videoDataLar = 0 ;
						if (mBit == null || mBit.getWidth() != videoData.picWidth || mBit.getHeight() != videoData.picHeight) 
						{
							mBit = Bitmap.createBitmap(videoData.picWidth, videoData.picHeight, Config.ARGB_8888);
						}
				//		Log.i(TAG, "buffer > videoData ? "+(( buffer.capacity()> videoDataLar) ? " true" : "false"));
						mBit.copyPixelsFromBuffer(buffer);
						buffer.rewind();
						synchronized (mBit) {
							if (mBit != null) {
								calcImgSize(mBit.getWidth(), mBit.getHeight());
							// 限定图片的最大宽高，避免OOM
								if (imgWidth <= maxWidth && imgHeight <= maxHeight) {
									mDrawBit = Bitmap.createScaledBitmap(mBit, imgWidth, imgHeight, false);
								}
							} else {
								mDrawBit = null;
							}
							if (mDrawBit != null && mCanvas != null) {
								initDrawRang();
								
								mCanvas.drawColor(Color.BLACK);
								mCanvas.drawBitmap(mDrawBit, src, dst, null);
							/*
							if (Global.mHandler != null) {
								if (isFirstGetData) {// 发消息通知界面更新（取消进度条等）
								//	Logs.i(TAG, "send  Msg.VIDEO_READY_PLAY , set Global.snapBmp = mDrawBit  , cameraHandlerNo = "+cameraHandlerNo);
									isFirstGetData = false;
								//	Global.snapBmp = mDrawBit;// 打开一次视频，就保存一张最新的图片作为设备列表中的ipc的背景图
								//	Global.mHandler.sendEmptyMessage(Msg.VIDEO_READY_PLAY);
								}
								
							}*/
						}
					 }
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
				//	if (mCanvas != null && videoData.len > 0) {
					if (mCanvas != null) {
						sfh.unlockCanvasAndPost(mCanvas);
					}
				}
				SystemClock.sleep(5);
			}
		}
	}

	/** 初始化显示的区域 */
	private void initDrawRang() {
		src.left = 0;
		src.top = 0;
		src.right = imgWidth;
		src.bottom = imgHeight;

		dst.left = (int) (surfaceWidth / 2 - imgWidth / 2);
		dst.top = (int) (surfaceHeight / 2 - imgHeight / 2);
		dst.right = imgWidth + dst.left;
		dst.bottom = imgHeight + dst.top;
	}
}
