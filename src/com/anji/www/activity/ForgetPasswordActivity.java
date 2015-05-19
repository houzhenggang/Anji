package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.MyActivityManager;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 忘记密码界面
 * 
 * @author Ivan
 * @since 9,24
 */
public class ForgetPasswordActivity extends BaseActivity implements
		OnClickListener
{

	private static final String TAG = "ForgetPasswordActivity";
	// 返回
	private Button img_back;
	// 标题右边注册按钮
	private Button tv_right_text;
	// 标题
	private TextView tv_title;
	// 获取验证码
	private Button bt_get_key;
	// 输入手机号
	private EditText et_phone_number;
	private Context context;
	private Dialog progressDialog;
	private getForgetCodeTask getCodeTask;
	private String mobile;
	private boolean isGet;
	private boolean isGetting;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		context = this;
		MyActivityManager.Add(TAG, this);
		findView();
		setLister();
	}

	private void setLister()
	{
		img_back.setOnClickListener(this);
		tv_right_text.setOnClickListener(this);
		bt_get_key.setOnClickListener(this);
	}

	private void findView()
	{
		progressDialog = DisplayUtils.createDialog(this);
		img_back = (Button) findViewById(R.id.bt_back);
		tv_right_text = (Button) findViewById(R.id.bt_right);
		bt_get_key = (Button) findViewById(R.id.bt_get_key);
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.forget_password));
		// tv_right_text.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		Intent intent;
		switch (id)
		{
		case R.id.bt_back:
			// 返回
			onBackPressed();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_get_key:
			// 获取验证码
			if (!isGetting)
			{

				mobile = et_phone_number.getText().toString().trim();

				if (TextUtils.isEmpty(mobile))
				{
					ToastUtils.show(this, getString(R.string.phone_null));
					return;
				}

				if (!Utils.isPhoneNumberValid(mobile))
				{
					ToastUtils.show(this, getString(R.string.phone_type_error));
					return;
				}
				startGetForgetCode();
			}
			else
			{
				ToastUtils.show(this, getString(R.string.getting_wait));
			}
			break;
		default:
			break;
		}
	}

	private void startGetForgetCode()
	{
		isGetting = true;
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		getCodeTask = new getForgetCodeTask();
		getCodeTask.execute();
	}

	private void cancelGetForgetCode()
	{
		if (getCodeTask != null)
		{
			getCodeTask.cancel(true);
			getCodeTask = null;
		}
	}

	private class getForgetCodeTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			responseBase = NetReq.getForgetCode(mobile);
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			isGetting = false;
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：手机号为空 402：手机号格式不正确 403：手机号未绑定注册账号
				 */
				if (responseBase.getResponseStatus() == 200)
				{

					Intent intent = new Intent(ForgetPasswordActivity.this,
							InputSecurityCode.class);
					intent.putExtra("mobile", mobile);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
					isGet = true;

					bt_get_key.setText(context
							.getString(R.string.get_code_again));
				}
				else
				{
					isGet = false;
					if (responseBase.getResponseStatus() == 300)
					{
						ToastUtils.show(context,
								context.getString(R.string.system_error));
					}
					else if (responseBase.getResponseStatus() == 401)
					{
						ToastUtils.show(context,
								context.getString(R.string.phone_null));
					}
					else if (responseBase.getResponseStatus() == 402)
					{
						ToastUtils.show(context,
								context.getString(R.string.phone_type_error));
					}
					else if (responseBase.getResponseStatus() == 403)
					{
						ToastUtils.show(context,
								context.getString(R.string.phone_had_no_bind));
					}
				}
			}
			else
			{
				// 网络请求失败
				isGet = false;
			}

		}
	}

	@Override
	protected void onDestroy()
	{
		MyActivityManager.finish(TAG);
		super.onDestroy();
	}

	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(ForgetPasswordActivity.this,
				LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
}
