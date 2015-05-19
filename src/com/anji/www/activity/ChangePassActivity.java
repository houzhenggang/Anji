package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.entry.Member;
import com.anji.www.entry.ResponseBase;
import com.anji.www.network.NetReq;
import com.anji.www.util.MyActivityManager;
import com.anji.www.util.ToastUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 修改密码
 * 
 * @author Administrator
 */
public class ChangePassActivity extends BaseActivity implements OnClickListener
{

	private static final String TAG = "ChangePassActivity";
	private Button bt_back;
	private Button bt_right;
	private EditText et_old_password;
	private EditText et_new_password;
	private EditText et_confirm_pass;
	private Context context;
	private Dialog progressDialog;
	private ChangePassworTask changepasswordTask;
	private Member member;
	private String oldPass;
	private String newPass;
	private String confrimPass;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pass);
		context = this;
		MyActivityManager.Add(TAG, this);
		initData();
		initView();
	}

	private void initData()
	{
		MyApplication app = (MyApplication) getApplication();
		member = app.getMember();
	}

	private void initView()
	{
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_right = (Button) findViewById(R.id.bt_right);
		et_old_password = (EditText) findViewById(R.id.et_old_password);
		et_new_password = (EditText) findViewById(R.id.et_new_password);
		et_confirm_pass = (EditText) findViewById(R.id.et_confirm_pass);

		bt_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_back:
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_right:
			// TODO完成
			oldPass = et_old_password.getText().toString().trim();
			newPass = et_new_password.getText().toString().trim();
			confrimPass = et_confirm_pass.getText().toString().trim();

			if (member == null)
			{
				ToastUtils.show(this, getString(R.string.date_error));
				return;
			}
			if (TextUtils.isEmpty(oldPass))
			{
				ToastUtils.show(this, getString(R.string.old_pass_null));
				return;
			}
			if (TextUtils.isEmpty(newPass))
			{
				ToastUtils.show(this, getString(R.string.old_pass_null));
				return;
			}
			if (TextUtils.isEmpty(confrimPass))
			{
				ToastUtils.show(this, getString(R.string.confirmpass_null));
				return;
			}

			if (!oldPass.equals(member.getPassword()))
			{
				ToastUtils.show(this, getString(R.string.old_pass_error));
				return;
			}
			if (!newPass.equals(confrimPass))
			{
				ToastUtils.show(this, getString(R.string.pass_different));
				return;
			}
			startChangePass();
			break;
		default:
			break;
		}
	}

	private void startChangePass()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		changepasswordTask = new ChangePassworTask();
		changepasswordTask.execute();
	}

	private void cancelChangePass()
	{
		if (changepasswordTask != null)
		{
			changepasswordTask.cancel(true);
			changepasswordTask = null;
		}
	}

	private class ChangePassworTask extends AsyncTask<Object, Object, Void>
	{
		ResponseBase responseBase;

		@Override
		protected Void doInBackground(Object... params)
		{
			MyApplication app = (MyApplication) getApplication();
			Member member = app.getMember();
			if (member != null)
			{
				responseBase = NetReq.changePasswor(member.getMemberId(),
						oldPass, newPass, member.getSessionId());
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
				 * 200：成功 300：系统异常 401：会员ID不能为空 402：密码不能为空 403：新密码不能为空
				 * 404：会话ID不能为空 405：会员不存在 406：密码不正确 407：无效的sessionID
				 */
				if (responseBase.getResponseStatus() == 200)
				{
					// 注册成功 下一步
					// MyApplication app = (MyApplication) getApplication();
					// app.getMember().setSsuid(macAddress);
					ToastUtils
							.show(context,
									context.getString(R.string.pass_word_change_sucess));
					finish();
					Intent intent = new Intent(ChangePassActivity.this,
							LoginActivity.class);
					intent.putExtra("isFromChangePass", true);
					startActivity(intent);

				}
				else if (responseBase.getResponseStatus() == 300)
				{
					ToastUtils.show(context,
							context.getString(R.string.system_error));
				}
				else if (responseBase.getResponseStatus() == 401)
				{
					ToastUtils.show(context,
							context.getString(R.string.memberId_null));
				}
				else if (responseBase.getResponseStatus() == 402)
				{
					ToastUtils.show(context,
							context.getString(R.string.pass_null));
				}
				else if (responseBase.getResponseStatus() == 403)
				{
					ToastUtils.show(context,
							context.getString(R.string.new_pass_null));
				}
				else if (responseBase.getResponseStatus() == 404)
				{
					ToastUtils.show(context,
							context.getString(R.string.sessionId_null));
				}
				else if (responseBase.getResponseStatus() == 405)
				{
					ToastUtils.show(context,
							context.getString(R.string.member_null));
				}
				else if (responseBase.getResponseStatus() == 406)
				{
					ToastUtils.show(context,
							context.getString(R.string.password_error));
				}
				else if (responseBase.getResponseStatus() == 407)
				{
					ToastUtils.show(context,
							context.getString(R.string.login_error));
				}

			}
			else
			{
				// 网络请求失败
			}

		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		MyActivityManager.finish(TAG);
		super.onDestroy();
	}
}