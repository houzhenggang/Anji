package com.anji.www.activity;


import com.anji.www.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class TabShop extends Fragment {
	private Activity activity;
	private WebView mWebView;
	private ProgressBar progressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_shop, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mWebView.clearCache( true );
		mWebView.destroy();
	}

	private void initView() {
		mWebView = (WebView) activity.findViewById(R.id.web_view);
		progressBar = (ProgressBar) activity.findViewById( R.id.progress_bar );
		
		WebSettings webSettings =   mWebView.getSettings();       
		webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);  
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Make the bar disappear after URL is loaded, and changes
				// string to Loading...
				progressBar.setProgress(progress); // Make the bar
														// disappear after URL
														// is loaded
				// Return the app name after finish loading
				if (progress >= 100)
				{
					progressBar.setVisibility( View.GONE );
				}
			}
		});
		
		
		mWebView.loadUrl( "http://weidian.com/s/333123846?sfr=c" );
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {       
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) 
        {       
            mWebView.goBack();       
                   return true;       
        }       
        return activity.onKeyDown(keyCode, event);       
    }  
}
