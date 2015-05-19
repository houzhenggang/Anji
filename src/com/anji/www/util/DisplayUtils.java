package com.anji.www.util;


import com.anji.www.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayUtils {


	// 创建提示框
	public static Dialog createDialog(Context context) {
		Dialog dialog = new Dialog(context, R.style.MyDialogStyle);
		dialog.setContentView(R.layout.custom_dialog_activity);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(keylistener);
		dialog.setCancelable(false);
		return dialog;
	}
	

	/**
	 * 创建加载对话框
	 * 
	 * @param ctx
	 * @param strResId
	 * @return
	 */
	public static Dialog createDialog(Context ctx, int strResId) {
		try {
			Dialog dialog = DisplayUtils.createDialog(ctx);
			TextView textView = (TextView) dialog
					.findViewById(R.id.progressbar_text);
			textView.setText(strResId);
			return dialog;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 创建加载对话框
	 * 
	 * @param ctx
	 * @param strResId
	 * @return
	 */
	public static Dialog createDialog(Context ctx, String str) {
		try {
			Dialog dialog = DisplayUtils.createDialog(ctx);
			TextView textView = (TextView) dialog
					.findViewById(R.id.progressbar_text);
			textView.setText(str);
			return dialog;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
            {
             return true;
            }
            else
            {
             return false;
            }
        }
    } ;
    
}

