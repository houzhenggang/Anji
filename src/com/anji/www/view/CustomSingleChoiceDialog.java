package com.anji.www.view;

import java.util.ArrayList;
import java.util.List;

import com.anji.www.R;
import com.anji.www.adapter.RadioAdapter;
import com.anji.www.entry.DeviceInfo;
import com.anji.www.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CustomSingleChoiceDialog extends Dialog 
{
	private Context mContext;
	private ListView listView;
	private List<DeviceInfo> list;
	private RadioAdapter mAdapter;
	private TextView tvTitle;
	private Button btnNegative;
	private Button btnPositive;

	private String title;
	private int index;
	
	private ButtonClickEvent event;
	
	public CustomSingleChoiceDialog( Context context, List<DeviceInfo> list, 
			String title, int index, ButtonClickEvent event ) 
	{
		super( context );
		
		this.mContext = context;
		this.list = list;
		this.title = title;
		this.index = index;
		if ( this.list == null )
		{
			this.list = new ArrayList<DeviceInfo>();
		}
		this.event = event;
	 	
		init();
	}
	
	private void init()
	{
		LayoutInflater inflater = LayoutInflater.from( mContext );
        View view = inflater.inflate( R.layout.dialog_singlechoice_layout, null );
        setContentView(view);
        
        listView = (ListView) view.findViewById(R.id.singlechoiceList );
        tvTitle = (TextView) view.findViewById(R.id.singlechoic_title);
        btnNegative = (Button) view.findViewById( R.id.negativeButton );
        btnPositive = (Button) view.findViewById( R.id.positiveButton );
        if ( !TextUtils.isEmpty( title ) )
        {
        	tvTitle.setText( title );
        }
        
        mAdapter = new RadioAdapter( mContext, list, index );
        listView.setAdapter(mAdapter);
        
        listView.setSelection( index );
        
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); 
        lp.width = (int) (d.widthPixels * 0.8); 
        lp.height = Utils.dip2px(mContext, 300);
        dialogWindow.setAttributes(lp);
        
        setListener();
	}

	private void setListener()
	{
		listView.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long arg3) 
			{
				mAdapter.setCurPos( position );
			}
			
		});
		
		btnNegative.setOnClickListener( new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				CustomSingleChoiceDialog.this.dismiss();
			}
		});
		
		btnPositive.setOnClickListener( new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if ( event != null )
				{
					event.onOkClick( mAdapter.getCurPos() );
				}
			}
		});
	}
	
	public interface ButtonClickEvent
	{
		public void onOkClick( int which );
	}
}
