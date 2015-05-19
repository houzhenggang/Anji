package com.anji.www.activity;

import java.io.UnsupportedEncodingException;

import com.anji.www.R;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 注册页面，输入用户名和密码
 * 
 * @author Ivan
 * @since 9,24
 */
public class RegisterActivity extends BaseActivity implements OnClickListener
{
	private RegisterActivity context;

	// 返回
	private Button img_back;
	// 下一步
	private Button bt_next_step;
	// 输入用户名
	private EditText et_username;
	// 输入密码
	private EditText et_password;
	// 确认密码
	private EditText et_confirm_pass;

	private String username;
	private String password;
	private RegisterOneTask registerTask;
	private ResponseBase responseBase;
	private Dialog progressDialog;

	private static final String Tag = "RegisterActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		context = this;
		findView();
		setLister();
		initView();
	}

	private void initView()
	{
		progressDialog = DisplayUtils.createDialog(context);
	}

	private void setLister()
	{
		img_back.setOnClickListener(this);
		bt_next_step.setOnClickListener(this);
	}

	private void findView()
	{
		img_back = (Button) findViewById(R.id.bt_back);
		bt_next_step = (Button) findViewById(R.id.bt_next_step);
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		et_confirm_pass = (EditText) findViewById(R.id.et_confirm_pass);
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
			break;
		case R.id.bt_next_step:
			String confrimPass = et_confirm_pass.getText().toString().trim();
			username = et_username.getText().toString().trim();
			password = et_password.getText().toString().trim();

			LogUtil.LogI(Tag, "username=" + username);
			LogUtil.LogI(Tag, "password=" + password);
			if (TextUtils.isEmpty(username))
			{
				ToastUtils.show(this, getString(R.string.name_null));
				return;
			}

			if (TextUtils.isEmpty(password))
			{
				ToastUtils.show(this, getString(R.string.pass_null));
				return;
			}

			if (TextUtils.isEmpty(confrimPass))
			{
				ToastUtils.show(this, getString(R.string.confirmpass_null));
				return;
			}
			// LogUtil.LogI(Tag, "username=" + username);
			// LogUtil.LogI(Tag, "username.length=" + username.length());
			// LogUtil.LogI(Tag,
			// " username.getBytes().length=" + username.getBytes().length);
			// LogUtil.LogI(
			// Tag,
			// " Utils.String_length(username)="
			// + Utils.String_length(username));
			if (!Utils.isNumOrZValid(username))
			{
				ToastUtils.show(this, getString(R.string.length_error));
				return;
			}

			if (!Utils.isNumOrZValid(password))
			{
				ToastUtils.show(this, getString(R.string.length_error));
				return;
			}

			if (!password.equals(confrimPass))
			{
				ToastUtils.show(this, getString(R.string.different_pass));
				return;
			}
			startRegisterOne();
			break;

		default:
			break;
		}
	}

	private void startRegisterOne()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		registerTask = new RegisterOneTask();
		registerTask.execute();
	}

	private void cancelRegisterOne()
	{
		if (registerTask != null)
		{
			registerTask.cancel(true);
			registerTask = null;
		}
	}

	private class RegisterOneTask extends AsyncTask<Object, Object, Void>
	{

		@Override
		protected Void doInBackground(Object... params)
		{
			responseBase = NetReq.registerOne(username, password);
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
				 * 200：成功 300：系统异常 401：用户名不能为空 402：密码不能为空 403：用户名已存在
				 */
				LogUtil.LogI(Tag, "responseBase.getResponseStatus()="
						+ responseBase.getResponseStatus());
				if (responseBase.getResponseStatus() == 200)
				{
					// 注册成功 下一步
					Intent intent = new Intent(RegisterActivity.this,
							RegisterTwoActivity.class);
					LogUtil.LogI(Tag, "responseBase.getMemberId()="
							+ responseBase.getMemberId());
					intent.putExtra("memberId", responseBase.getMemberId());
					intent.putExtra("username", username);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);

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
							context.getString(R.string.pass_null));

				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(context,
							context.getString(R.string.name_exist));
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
		Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

}
