package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.util.MyActivityManager;
import com.anji.www.util.ToastUtils;

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
 * 输入验证码界面
 * 
 * @author Administrator
 */
public class InputSecurityCode extends BaseActivity implements OnClickListener
{
	private static final String TAG = "InputSecurityCode";
	// 返回键
	private Button bt_back;
	// 标题
	private TextView tv_title;
	// 电话号码
	private TextView tv_phone_number;
	// 标题右边按键，完成
	private Button bt_right;
	// 验证码
	private EditText et_security_code;
	// 没有收到验证码
	private TextView tv_isget_code;
	private String mobile;// 手机号
	private String validCode;// 验证码
	private Context context;
	private Dialog progressDialog;
	private getForgetCodeTask getCodeTask;
	private GetForgetVailCodeTask getVailCodeTask;
	private Dialog sendAgainDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_secutitycode);
		context = this;
		mobile = getIntent().getStringExtra("mobile");
		MyActivityManager.Add(TAG, this);
		findView();
		setListener();
		initSendAgainDialog();
	}

	private void setListener()
	{
		bt_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);
		tv_isget_code.setOnClickListener(this);
	}

	private void findView()
	{
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_right = (Button) findViewById(R.id.bt_right);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
		tv_isget_code = (TextView) findViewById(R.id.tv_isget_code);
		et_security_code = (EditText) findViewById(R.id.et_security_code);
		tv_title.setText(getString(R.string.inputsecurity_title));
		bt_right.setVisibility(View.VISIBLE);
		bt_right.setText("");
		bt_right.setBackgroundResource(R.drawable.finish_button_selector);
		tv_phone_number.setText(mobile);
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
			intent = new Intent(InputSecurityCode.this,
					ForgetPasswordActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_right:
			// 完成后进入密码设置
			validCode = et_security_code.getText().toString().trim();
			if (TextUtils.isEmpty(validCode))
			{
				ToastUtils.show(context, getString(R.string.validCode_null));
				return;
			}
			startVerifyForgetCode();
			break;
		case R.id.tv_isget_code:
			if (sendAgainDialog != null && !sendAgainDialog.isShowing())
			{
				sendAgainDialog.show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy()
	{
		MyActivityManager.finish(TAG);
		super.onDestroy();
	}

	private void startVerifyForgetCode()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		getCodeTask = new getForgetCodeTask();
		getCodeTask.execute();
	}

	private void cancelVerifyForgetCode()
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
			if (!TextUtils.isEmpty(validCode))
			{
				responseBase = NetReq.verifyForgetCode(mobile, validCode);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (responseBase != null)
			{
				/**
				 * 200：成功 300：系统异常 401：手机号为空 402：手机号格式不正确 403：验证码不能为空 404：无效验证码
				 * 405：验证码不正确 406：验证码已失效 407：手机号未绑定会员
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					Intent intent = new Intent(InputSecurityCode.this,
							SetPasswordActivity.class);
					intent.putExtra("memberId", responseBase.getMemberId());
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);

				}
				else
				{
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
								context.getString(R.string.validCode_null));
					}
					else if (responseBase.getResponseStatus() == 404)
					{
						ToastUtils
								.show(context,
										context.getString(R.string.validCode_not_work2));
					}
					else if (responseBase.getResponseStatus() == 405)
					{
						ToastUtils.show(context,
								context.getString(R.string.validCode_error));
					}
					else if (responseBase.getResponseStatus() == 406)
					{
						ToastUtils.show(context,
								context.getString(R.string.validCode_not_work));
					}
					else if (responseBase.getResponseStatus() == 407)
					{
						ToastUtils.show(context,
								context.getString(R.string.phone_had_no_bind));
					}
				}
			}
			else
			{
				// 网络请求失败
			}

		}
	}

	/**
	 * 再次发生提示框
	 */
	private void initSendAgainDialog()
	{
		sendAgainDialog = new Dialog(this, R.style.MyDialogStyle);
		sendAgainDialog.setContentView(R.layout.alert_hint_dialog);
		sendAgainDialog.setCancelable(false);

		Button bt_sure = (Button) sendAgainDialog.findViewById(R.id.bt_sure);
		Button bt_cancel = (Button) sendAgainDialog
				.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				sendAgainDialog.dismiss();
			}
		});

		bt_sure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				sendAgainDialog.dismiss();
				startGetForgetCode();
			}
		});
	}

	private void startGetForgetCode()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		getVailCodeTask = new GetForgetVailCodeTask();
		getVailCodeTask.execute();
	}

	private class GetForgetVailCodeTask extends AsyncTask<Object, Object, Void>
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

					// Intent intent = new Intent(InputSecurityCode.this,
					// InputSecurityCode.class);
					// intent.putExtra("mobile", mobile);
					// startActivity(intent);
					// finish();
					// overridePendingTransition(android.R.anim.slide_in_left,
					// android.R.anim.slide_out_right);
					ToastUtils.show(context,
							context.getString(R.string.send_validCode_sucess));
				}
				else
				{
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
			}

		}
	}
	
	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(InputSecurityCode.this,
				ForgetPasswordActivity.class);
//		intent.putExtra("mobile", mobile);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

}
