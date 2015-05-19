package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.entry.Member;
import com.anji.www.util.MyActivityManager;
import com.anji.www.util.ToastUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 我的账户页面
 * @author Administrator
 *
 */
public class MyCountActivity extends BaseActivity implements OnClickListener
{

	private static final String TAG = "MyCountActivity";
	private Button bt_back;
	private TextView tv_title;
	private TextView tv_username;
	private TextView tv_phone_num;
	private Button bt_change_password;
	private Button bt_change_net;
	private Member member;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycount);
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
		bt_change_password = (Button) findViewById(R.id.bt_change_password);
		bt_change_net = (Button) findViewById(R.id.bt_change_net);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);

		tv_title.setText(getString(R.string.my_count));

		bt_back.setOnClickListener(this);
		bt_change_password.setOnClickListener(this);
		bt_change_net.setOnClickListener(this);

		if (member != null)
		{
			if (!TextUtils.isEmpty(member.getUsername()))
			{
				tv_username.setText(member.getUsername());
			}
			if (!TextUtils.isEmpty(member.getMobile()))
			{
				tv_phone_num.setText(member.getMobile());
			}
		}else {
			ToastUtils.show(this, getString(R.string.date_error));
		}
	}

	@Override
	public void onClick(View v)
	{
		Intent intent;
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_back:
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_change_password:
			//TODO 修改密码
			intent = new Intent(MyCountActivity.this,ChangePassActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.bt_change_net:
			//TODO 切换网关
			intent = new Intent(MyCountActivity.this,ChangeGatewayActitivy.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
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
}
