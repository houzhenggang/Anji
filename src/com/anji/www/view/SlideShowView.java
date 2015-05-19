package com.anji.www.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.anji.www.R;
import com.anji.www.entry.AdInfo;
import com.anji.www.util.LogUtil;
import com.fos.sdk.ImgCmd;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ViewPager实现的轮播图广告自定义视图； 既支持自动轮播页面也支持手势滑动切换页面,可以动态设置图片的张数
 * 
 * @author Chao Gong
 */
@SuppressLint("HandlerLeak")
public class SlideShowView extends FrameLayout {

	private ImageLoader imageLoaderWraper;

	private final static boolean isAutoPlay = true;
	// private List<Integer> imageUris;
	private List<AdInfo> imageUris;
	private List<ImageView> imageViewsList;
	private List<ImageView> dotViewsList;
	private LinearLayout mLinearLayout;
	private Context mContext;

	private CustomViewPager mViewPager;
	private int currentItem = 0;
	private ScheduledExecutorService scheduledExecutorService;
	private String Tag = getClass().getSimpleName();

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			LogUtil.LogI(Tag, "handleMessage");
			super.handleMessage(msg);
			mViewPager.setCurrentItem(getCurrentItem());
		}

	};

	public SlideShowView(Context context) {
		this(context, null);
		mContext = context;
	}

	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
	}

	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		initUI(context);
		if (!(imageUris.size() <= 0)) {
			// System.out.println("XXXXXXXXXXXX");
			setImageUris(imageUris);
		}

		if (isAutoPlay && imageUris.size() > 1) {
			startPlay();
		}

	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mViewPager.setOnClickListener(l);
		super.setOnClickListener(l);
	}

	private void initUI(Context context) {
		imageViewsList = new ArrayList<ImageView>();
		dotViewsList = new ArrayList<ImageView>();
		imageUris = new ArrayList<AdInfo>();
		// imageLoaderWraper=ImageLoaderWraper.getInstance(context.getApplicationContext());
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
				true);
		mLinearLayout = (LinearLayout) findViewById(R.id.linearlayout);
		mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
	}

	// public void setImageUris(List<Integer> imageuris)
	// {
	//
	// for(int i=0;i<imageuris.size();i++)
	// {
	// imageUris.add(imageuris.get(i));
	// System.out.println("ＹＹＹＹＹＹＹＹＹＹ");
	// }
	// LinearLayout.LayoutParams lp=new
	// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	// lp.setMargins(5, 0, 0, 0);
	// for(int i=0;i<imageUris.size();i++){
	// ImageView imageView=new ImageView(getContext());
	// imageView.setScaleType(ScaleType.FIT_XY);//铺满屏幕
	// imageView.setBackgroundResource((int)imageuris.get(i));
	// // System.out.println("GGGGG:"+imageUris.get(i));
	// // imageLoaderWraper.displayImage(imageUris.get(i), imageView);
	// // System.out.println("JJJJJJ");
	// imageViewsList.add(imageView);
	// System.out.println("JJJJJJ55555");
	// ImageView viewDot = new ImageView(getContext());
	// System.out.println("JJJJJJ88888");
	// if(i == 0){
	// viewDot.setBackgroundResource(R.drawable.main_dot_white);
	// }else{
	// viewDot.setBackgroundResource(R.drawable.main_dot_light);
	// }
	// //viewDot.setImageResource(R.drawable.dot_white);
	// System.out.println("JJJJJJ9999");
	// viewDot.setLayoutParams(lp);
	// dotViewsList.add(viewDot);
	// mLinearLayout.addView(viewDot);
	//
	//
	// }
	// mViewPager.setFocusable(true);
	// mViewPager.setAdapter(new MyPagerAdapter());
	// mViewPager.setOnPageChangeListener(new MyPageChangeListener());
	// }

	// @Override
	// public void setOnClickListener(OnClickListener l)
	// {
	// // TODO Auto-generated method stub
	// mViewPager.setOnClickListener(l);
	// super.setOnClickListener(l);
	// }

	public void setImageUris(List<AdInfo> imageuris) {

		imageUris.clear();
		dotViewsList.clear();
		mLinearLayout.removeAllViews();
		for (int i = 0; i < imageuris.size(); i++) {
			imageUris.add(imageuris.get(i));
			// System.out.println("ＹＹＹＹＹＹＹＹＹＹ");
		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(5, 0, 0, 0);
		for (int i = 0; i < imageUris.size(); i++) {
			ImageView imageView = new ImageView(getContext());
			imageView.setScaleType(ScaleType.CENTER_CROP);// 铺满屏幕
			// System.out.println("GGGGG:" + imageUris.get(i));
			// imageLoaderWraper.displayImage(imageUris.get(i),
			// imageView);//用开源框架加载图片，读者可以自行更改，使用setsetBackgroundResource也行
			ImageLoader.getInstance().displayImage(
					imageUris.get(i).getImgPath(), imageView);
			// System.out.println("JJJJJJ");
			final int i2 = i;
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LogUtil.LogI("currentItem", "currentItem=" + currentItem);
					if (imageUris.get(i2) != null
							&& !TextUtils
									.isEmpty(imageUris.get(i2).getWeburl())) {
						Uri uri = Uri.parse(imageUris.get(i2).getWeburl());
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						mContext.startActivity(intent);
					}

				}
			});
			imageViewsList.add(imageView);
			// System.out.println("JJJJJJ55555");
			ImageView viewDot = new ImageView(getContext());
			// System.out.println("JJJJJJ88888");
			if (i == 0) {
				viewDot.setBackgroundResource(R.drawable.main_dot_white);
			} else {
				viewDot.setBackgroundResource(R.drawable.main_dot_light);
			}
			// viewDot.setImageResource(R.drawable.dot_white);
			// System.out.println("JJJJJJ9999");
			viewDot.setLayoutParams(lp);
			dotViewsList.add(viewDot);
			mLinearLayout.addView(viewDot);
		}
		mViewPager.setFocusable(true);
		mViewPager.setAdapter(new MyPagerAdapter());
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
		LogUtil.LogI(Tag, "imageUris.size()==" + imageUris.size());
		if (imageUris.size() == 1) {
			mViewPager.setPagingEnabled(false);
		}else {
			mViewPager.setPagingEnabled(true);
		}
	}

	public void setMyOnclck(OnClickListener listener) {
		LogUtil.LogI("slideShow", "setMyOnclck");
		mViewPager.setOnClickListener(listener);
	}

	private void startPlay() {
		LogUtil.LogI(Tag, "startPlay");
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4,
				TimeUnit.SECONDS);
	}

	@SuppressWarnings("unused")
	private void stopPlay() {
		LogUtil.LogI(Tag, "stopPlay");
		scheduledExecutorService.shutdown();
	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		LogUtil.LogI(Tag, "setImageBackground");
		for (int i = 0; i < dotViewsList.size(); i++) {
			if (i == selectItems) {
				dotViewsList.get(i).setBackgroundResource(
						R.drawable.main_dot_white);
			} else {
				dotViewsList.get(i).setBackgroundResource(
						R.drawable.main_dot_light);
			}
		}
	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			// ((ViewPag.er)container).removeView((View)object);
			LogUtil.LogI(Tag, "MyPagerAdapter");
			((ViewPager) container).removeView(imageViewsList.get(position));

		}

		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(imageViewsList.get(position));
			LogUtil.LogI(Tag, "instantiateItem");
			return imageViewsList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			LogUtil.LogI(Tag, "getCount");
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			LogUtil.LogI(Tag, "isViewFromObject");
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			LogUtil.LogI(Tag, "restoreState");
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			LogUtil.LogI(Tag, "saveState");
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			LogUtil.LogI(Tag, "startUpdate");
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			LogUtil.LogI(Tag, "finishUpdate");
		}

	}

	private class MyPageChangeListener implements OnPageChangeListener {

		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			LogUtil.LogI(Tag, "onPageScrollStateChanged  arg0 == " + arg0);
			// TODO Auto-generated method stub
			switch (arg0) {
			case 1:
				isAutoPlay = false;
				break;
			case 2:
				isAutoPlay = true;
				break;
			case 0:

				LogUtil.LogI(Tag, "imageUris.size() == " + imageUris.size());
				if (mViewPager.getCurrentItem() == mViewPager.getAdapter()
						.getCount() - 1 && !isAutoPlay) {
					mViewPager.setCurrentItem(0);
				}

				else if (mViewPager.getCurrentItem() == 0 && !isAutoPlay) {
					mViewPager.setCurrentItem(mViewPager.getAdapter()
							.getCount() - 1);
				}
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			LogUtil.LogI(Tag, "onPageScrolled  arg0 == " + arg0 + "arg1=="
					+ arg1 + "arg2==" + arg2);
		}

		@Override
		public void onPageSelected(int pos) {
			// TODO Auto-generated method stub
			setImageBackground(pos % imageUris.size());
			LogUtil.LogI(Tag, "onPageSelected");
			// currentItem = pos;
			// for(int i=0;i < dotViewsList.size();i++){
			// if(i == pos){
			// ((ImageView)dotViewsList.get(pos)).setBackgroundResource(R.drawable.dot_black);//R.drawable.main_dot_light
			// }else {
			// ((ImageView)dotViewsList.get(i)).setBackgroundResource(R.drawable.dot_white);
			// }
			// }
		}

	}

	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (mViewPager) {
				LogUtil.LogI(Tag, " SlideShowTask mViewPager");
				setCurrentItem((getCurrentItem() + 1) % imageViewsList.size());
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	@SuppressWarnings("unused")
	private void destoryBitmaps() {

		for (int i = 0; i < imageViewsList.size(); i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null) {

				drawable.setCallback(null);
			}
		}
	}

	public int getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(int currentItem) {
		this.currentItem = currentItem;
	}
}