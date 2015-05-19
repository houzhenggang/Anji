package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.entry.Member;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
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
 * 设置新密码
 * 
 * @author Ivan
 * @since 9,24
 */
public class SetPasswordActivity extends BaseActivity implements
		OnClickListener
{

	private static final String TAG = "SetPasswordActivity";
	// 返回
	private Button img_back;
	// 标题
	private TextView tv_title;
	// 注册完成
	private Button bt_right;
	// 输入新密码
	private EditText et_new_password;
	// 输入确认密码
	private EditText et_confirm_pass;
	private String memberId;
	private String newPass;
	private String confrimPass;
	private Context context;

	private Dialog progressDialog;
	private RestPasswordTask restPasswordTask;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_password);
		MyActivityManager.Add(TAG, this);
		context = this;
		memberId = getIntent().getStringExtra("memberId");
		findView();
		setLister();
	}

	private void setLister()
	{
		img_back.setOnClickListener(this);
		bt_right.setOnClickListener(this);
	}

	private void findView()
	{
		progressDialog = DisplayUtils.createDialog(this);
		img_back = (Button) findViewById(R.id.bt_back);
		et_new_password = (EditText) findViewById(R.id.et_new_password);
		et_confirm_pass = (EditText) findViewById(R.id.et_confirm_pass);
		tv_title = (TextView) findViewById(R.id.tv_title);
		bt_right = (Button) findViewById(R.id.bt_right);
		tv_title.setText(getString(R.string.set_password));
		bt_right.setVisibility(View.VISIBLE);
		bt_right.setText("");
		bt_right.setBackgroundResource(R.drawable.finish_button_selector);
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
			// intent = new Intent(SetPasswordActivity.this,
			// LoginActivity.class);
			// startActivity(intent);
			// finish();
			// overridePendingTransition(android.R.anim.slide_in_left,
			// android.R.anim.slide_out_right);
			onBackPressed();
			break;
		case R.id.bt_right:
			// TODO 完成后进入主页
			if (TextUtils.isEmpty(memberId))
			{
				ToastUtils.show(this, getString(R.string.date_error));
				return;
			}
			newPass = et_new_password.getText().toString().trim();
			confrimPass = et_confirm_pass.getText().toString().trim();
			if (TextUtils.isEmpty(newPass))
			{
				ToastUtils.show(this, getString(R.string.new_pass_null));
				return;
			}
			if (TextUtils.isEmpty(confrimPass))
			{
				ToastUtils.show(this, getString(R.string.confirmpass_null));
				return;
			}
			if (!newPass.equals(confrimPass))
			{
				ToastUtils.show(this, getString(R.string.pass_different));
				return;
			}
			startRestPass();
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

	private void startRestPass()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		restPasswordTask = new RestPasswordTask();
		restPasswordTask.execute();
	}

	private void cancelRestPass()
	{
		if (restPasswordTask != null)
		{
			restPasswordTask.cancel(true);
			restPasswordTask = null;
		}
	}

	private class RestPasswordTask extends AsyncTask<Object, Object, Void>
	{
		Member member;

		@Override
		protected Void doInBackground(Object... params)
		{
			member = NetReq.restPassword(memberId, newPass, context);
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
				 * 200：成功 300：系统异常 401：会员ID不能为空 402：密码不能为空 403：会员不存在
				 */
				if (member.getResponseStatus() == 200)
				{
					// 注册成功 下一步
					MyApplication app = (MyApplication) getApplication();
					app.setMember(member);
					Intent intent = new Intent(SetPasswordActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();

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
							context.getString(R.string.pass_null));
				}
				else if (member.getResponseStatus() == 403)
				{
					ToastUtils.show(context,
							context.getString(R.string.member_null));
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
		Intent intent = new Intent(SetPasswordActivity.this,
				LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
}
