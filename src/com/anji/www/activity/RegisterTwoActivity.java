package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 注册页面，填写验证码
 * 
 * @author Ivan
 * @since 9,24
 */
public class RegisterTwoActivity extends BaseActivity implements
		OnClickListener
{
	private RegisterTwoActivity context;
	// 返回
	private Button img_back;
	// 标题右边注册按钮
	private Button bt_register;
	private TextView tv_title;
	// 后去验证码
	private Button bt_get_key;
	// 输入手机号
	private EditText et_phone_number;
	// 输入验证码
	private EditText et_security_code;

	private TextView tv_not_accept;
	private Dialog progressDialog;
	private RegisterTwoTask registerTwoTask;
	private RegisterThreeTask registerThreeTask;
	private String phoneNum;
	private ResponseBase responseBase;
	private Member member;
	private boolean isSendSucess;// 验证码是否发送成功
	private String viladCode;
	private String memberId;
	private String username;
	private Dialog sendAgainDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_two);
		memberId = getIntent().getStringExtra("memberId");
		username = getIntent().getStringExtra("username");
		context = this;
		progressDialog = DisplayUtils.createDialog(context);
		findView();
		setLister();
		initSendAgainDialog();
	}

	private void setLister()
	{
		img_back.setOnClickListener(this);
		bt_register.setOnClickListener(this);
		bt_get_key.setOnClickListener(this);
		tv_not_accept.setOnClickListener(this);
	}

	private void findView()
	{
		img_back = (Button) findViewById(R.id.bt_back);
		bt_register = (Button) findViewById(R.id.bt_register);
		bt_get_key = (Button) findViewById(R.id.bt_get_key);
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		et_security_code = (EditText) findViewById(R.id.et_security_code);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.cell_phone_verify));
		tv_not_accept = (TextView) findViewById(R.id.tv_not_accept);
		// bt_right.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_back:
			onBackPressed();
			break;
		case R.id.bt_register:
			// 注册下一步
			if (isSendSucess)
			{
				viladCode = et_security_code.getText().toString().trim();
				if (TextUtils.isEmpty(viladCode))
				{
					ToastUtils
							.show(context, getString(R.string.validCode_null));
					return;
				}
				startRegisterThree();

			}
			else
			{
				ToastUtils.show(context, getString(R.string.get_validCode));
			}

			break;
		case R.id.bt_get_key:
			// 获取验证码
			getValidCode();
			break;

		case R.id.tv_not_accept:
			if (sendAgainDialog != null && !sendAgainDialog.isShowing())
			{
				sendAgainDialog.show();
			}
			break;
		default:
			break;
		}
	}

	private void getValidCode()
	{
		phoneNum = et_phone_number.getText().toString().trim();
		if (TextUtils.isEmpty(phoneNum))
		{
			ToastUtils.show(context, getString(R.string.phone_null));
			return;
		}

		if (!Utils.isPhoneNumberValid(phoneNum))
		{
			ToastUtils.show(context, getString(R.string.phone_error));
			return;
		}
		startRegisterTwo();
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
				getValidCode();
			}
		});
	}

	private void startRegisterTwo()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		registerTwoTask = new RegisterTwoTask();
		registerTwoTask.execute();
	}

	private void cancelRegisterTwo()
	{
		if (registerTwoTask != null)
		{
			registerTwoTask.cancel(true);
			registerTwoTask = null;
		}
	}

	private class RegisterTwoTask extends AsyncTask<Object, Object, Void>
	{

		@Override
		protected Void doInBackground(Object... params)
		{
			responseBase = NetReq.registerTwo(phoneNum, username);
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
				 * 200：成功 300：系统异常 401：手机号为空 402：手机号格式不正确403：用户名为空 404：该手机号已注册使用
				 * 405：该用户不存在
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 注册成功 下一步
					isSendSucess = true;
					bt_get_key.setText(getString(R.string.get_code_again));
					ToastUtils.show(context,
							context.getString(R.string.send_validCode_sucess));

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(context,
							context.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(context,
							context.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(context,
							context.getString(R.string.phone_type_error));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(context,
							context.getString(R.string.name_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(context,
							context.getString(R.string.phone_userd));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(context,
							context.getString(R.string.name_not_exit));
				}

			}
			else
			{
				// 网络请求失败
			}

		}
	}

	private void startRegisterThree()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		registerThreeTask = new RegisterThreeTask();
		registerThreeTask.execute();
	}

	private void cancelRegisterThree()
	{
		if (registerThreeTask != null)
		{
			registerThreeTask.cancel(true);
			registerThreeTask = null;
		}
	}

	private class RegisterThreeTask extends AsyncTask<Object, Object, Void>
	{

		@Override
		protected Void doInBackground(Object... params)
		{
			member = NetReq.registerThree(memberId, phoneNum, viladCode,
					context, true);
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			if (member != null)
			{
				/**
				 * 200：成功 300：系统异常 401：会员ID不能为空 402：手机号不能为空 403：手机号格式错误
				 * 404：验证码不能为空 405：验证码不存在 406：验证码不正确 407：验证码已失效 408：手机号已被绑定
				 */
				if (member.getResponseStatus() == 200)
				{
					// 注册成功 下一步
					// 保存member到MyApplication
					MyApplication app = (MyApplication) getApplication();
					app.setMember(member);
					Intent intent = new Intent(RegisterTwoActivity.this,
							RegisterThreeActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);

				}
				else if (member.getResponseStatus() == 300)
				{
					ToastUtils.show(context,
							context.getString(R.string.system_error));
				}
				else if (member.getResponseStatus() == 401)
				{
					ToastUtils.show(context,
							context.getString(R.string.memberId_null));
				}
				else if (member.getResponseStatus() == 402)
				{
					ToastUtils.show(context,
							context.getString(R.string.phone_null));
				}
				else if (member.getResponseStatus() == 403)
				{
					ToastUtils.show(context,
							context.getString(R.string.phone_type_error));
				}
				else if (member.getResponseStatus() == 404)
				{
					ToastUtils.show(context,
							context.getString(R.string.validCode_null));
				}
				else if (member.getResponseStatus() == 405)
				{
					ToastUtils.show(context,
							context.getString(R.string.validCode_not_exist));
				}
				else if (member.getResponseStatus() == 406)
				{
					ToastUtils.show(context,
							context.getString(R.string.validCode_error));
				}
				else if (member.getResponseStatus() == 407)
				{
					ToastUtils.show(context,
							context.getString(R.string.validCode_not_work));
				}
				else if (member.getResponseStatus() == 408)
				{
					ToastUtils.show(context,
							context.getString(R.string.phone_had_bind));
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
		// 返回
		Intent intent = new Intent(RegisterTwoActivity.this,
				RegisterActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

}
