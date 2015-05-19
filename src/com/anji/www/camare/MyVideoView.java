package com.anji.www.camare;

import java.nio.ByteBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.view.View;

import com.decoder.util.DecH264;
import com.ipc.sdk.AVStreamData;
import com.ipc.sdk.FSApi;

public class MyVideoView extends View  implements Runnable{
	
	DecH264 decoder = new DecH264();
	
	AVStreamData videoStreamData = new AVStreamData();
    
    int videoWidth = 640;
    int videoHeight = 480;
    int vvWidth = 0;
    int vvHeight = 0;
    boolean isEnableVideoStream = false;
    
    boolean isThreadRun = true;
    boolean restartDecoder = false;
    

    byte [] mPixel = new byte[1280*720*2];
    int [] gotPicture = new int[4];
    
    ByteBuffer buffer = ByteBuffer.wrap( mPixel );
	Bitmap VideoBit = Bitmap.createBitmap(videoWidth, videoHeight, Config.RGB_565); 
	Bitmap bmpMJ = null;
	int videoFormat = 0; //H264
	
    
    public MyVideoView(Context context, int width, int height) {
        super(context);
        setFocusable(true);
        
       	int i = 0;
        for(i=0; i<mPixel.length; i++)
        {
        	mPixel[i]=(byte)0x00;
        }
        
		vvWidth = width;
		vvHeight = height;
    }
    
    public void setVVMetric(int width, int height)
    {
		vvWidth = width;
		vvHeight = height;
		
    }
           
    public void start()
    {
    	isThreadRun = true;
    	
    	try{
    		new Thread(this).start();
    	}catch( Exception e )
    	{
    	}
    }
    
    public void stop()
    {
    	isThreadRun = false;
    }
    
    public void startVideoStream()
    {
    	isEnableVideoStream = true;
    	FSApi.startVideoStream(0);
    }
    
    public void stopVideoStream()
    {
    	isEnableVideoStream = false;
    	FSApi.stopVideoStream(0);
    	restartDecoder = true;
    }
    
    public void clearScreen()
    {
       	int i = 0;
       	synchronized(this)
       	{
	        for(i=0; i<mPixel.length; i++)
	        {
	        	mPixel[i]=(byte)0x00;
	        }
       	}
        
        postInvalidate();  //使用postInvalidate可以直接在线程中更新界面   
    }
    
    
    protected Bitmap getScaleBmp(Bitmap src, float sx, float sy)
    {
  	  	Matrix matrix = new Matrix(); 
  	  	matrix.postScale(sx,sy); //长和宽放大缩小的比例
  	  	Bitmap resizeBmp = Bitmap.createBitmap( src, 0, 0, 
			  						src.getWidth(), src.getHeight(), matrix, true );
  	  	return resizeBmp;
    }
    
    protected Bitmap getHorizenBmp(Bitmap src)
    {
  	  	Matrix matrix = new Matrix(); 
  	  	matrix.postScale(vvHeight*1.0f/videoHeight,vvHeight*1.0f/videoHeight); //长和宽放大缩小的比例
  	  	Bitmap resizeBmp = Bitmap.createBitmap( src, 0, 0, 
			  						src.getWidth(), src.getHeight(), matrix, true );
  	  	return resizeBmp;
    }
    
    protected Bitmap getVerticalBmp(Bitmap src)
    {
  	  	Matrix matrix = new Matrix(); 
  	  	matrix.postScale(vvWidth*1.0f/videoWidth,vvWidth*1.0f/videoWidth); //长和宽放大缩小的比例
  	  	Bitmap resizeBmp = Bitmap.createBitmap( src, 0, 0, 
			  						src.getWidth(), src.getHeight(), matrix, true );
  	  	return resizeBmp;
    }
        
    @Override
    protected void onDraw(Canvas canvas) {
    	{ 
    		super.onDraw(canvas); 
    		
    		if( videoFormat == 0 )
    		{
	    		VideoBit.copyPixelsFromBuffer(buffer);
	    		buffer.position(0);//将buffer的下一读写位置置为0。
	    		if( vvWidth > vvHeight ) //横屏
	    		{
	    			if( vvHeight > videoHeight*(vvWidth*1.0f/videoWidth) )
	    			{
	    				canvas.drawBitmap(getVerticalBmp(VideoBit), 0, (vvHeight-vvWidth*1.0f/videoWidth*videoHeight)/2, null); 
	    			}
	    			else
	    			{
	    				canvas.drawBitmap(getHorizenBmp(VideoBit), (vvWidth-vvHeight*1.0f/videoHeight*videoWidth)/2, 0, null); 
	    			}
	    		}
	    		else // 竖屏
	    		{
	    			canvas.drawBitmap(getVerticalBmp(VideoBit), 0, (vvHeight-vvWidth*1.0f/videoWidth*videoHeight)/2, null); 
	    		}
    		}
    		else
    		{
    			if( bmpMJ != null )
    			{
		    		if( vvWidth > vvHeight ) //横屏
		    		{
		    			if( vvHeight > videoHeight )
		    			{
		    				canvas.drawBitmap(getVerticalBmp(bmpMJ), 0, (vvHeight-vvWidth*1.0f/videoWidth*videoHeight)/2, null); 
		    			}
		    			else
		    			{
		    				canvas.drawBitmap(getHorizenBmp(bmpMJ), (vvWidth-vvHeight*1.0f/videoHeight*videoWidth)/2, 0, null); 
		    			}
		    		}
		    		else // 竖屏
		    		{
		    			canvas.drawBitmap(getVerticalBmp(bmpMJ), 0, (vvHeight-vvWidth*1.0f/videoWidth*videoHeight)/2, null); 
		    		}
    			}
    		}
    	}
    }
    
    
    public void run()   
    {   
  
    	decoder.InitDecoder();
    	
        while ( isThreadRun )   
        {
        	if( isEnableVideoStream )
        	{
        		try{
        			FSApi.getVideoStreamData( videoStreamData ,0);
        		}catch(Exception e)
        		{
        			continue;
        		}
        		
	       	if( videoStreamData.dataLen > 0)
	        	{
	        		if( videoStreamData.videoFormat == 0 ) // H264
	        		{
	        			videoFormat = 0; 
	        			decoder.DecoderNal( videoStreamData.data, videoStreamData.dataLen, gotPicture, mPixel );
	        		}
	        		else if( videoStreamData.videoFormat == 1 )//MJ
	        		{
	        			videoFormat = 1;
	        			gotPicture[0] = 0;
	        			bmpMJ = BitmapFactory.decodeByteArray(videoStreamData.data, 0, videoStreamData.dataLen);
	        			videoWidth = bmpMJ.getWidth();
	        			videoHeight = bmpMJ.getHeight();
	        			postInvalidate();  //使用postInvalidate可以直接在线程中更新界面   
	        		}
	        		else
	        		{
	        			gotPicture[0] = 0;
	        		}
	        		
	        	    if( gotPicture[0] > 0 )
	        	    {
	        	    	if( (gotPicture[2]!=videoWidth) || (gotPicture[3]!=videoHeight) )
	        	    	{
	        	    		VideoBit.recycle();
	        	    		videoWidth = gotPicture[2];
	        	    		videoHeight = gotPicture[3];
	        	    		VideoBit = Bitmap.createBitmap(videoWidth, videoHeight, Config.RGB_565); 
	        	    	}
		            	postInvalidate();  //使用postInvalidate可以直接在线程中更新界面   
	        	    }
	        	}
	        	else
	        	{
	        		try {
	        			Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	}
	        	
	        	if( restartDecoder )
	        	{
	        		decoder.UninitDecoder();
	        		decoder.InitDecoder();
	        		restartDecoder = false;
	        	}
        	}
        	else
        	{
        		try {
        			Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        }
        decoder.UninitDecoder();
    }
}