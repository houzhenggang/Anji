package com.anji.www.activity;

import com.anji.www.R;
import com.anji.www.util.LogUtil;
import com.anji.www.util.ToastUtils;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EzlinkAddActivity extends BaseActivity implements OnClickListener {

	private Button bt_back;
	private Button bt_next_step;
	private TextView tv_title;
	private ImageView img_scan;
	private EditText et_uid;
	private String Tag = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ezlink_add);
		initView();
	}

	private void initView() {
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_next_step = (Button) findViewById(R.id.bt_next_step);
		tv_title = (TextView) findViewById(R.id.tv_title);
		img_scan = (ImageView) findViewById(R.id.img_scan);
		et_uid = (EditText) findViewById(R.id.et_uid);
		tv_title.setText(getString(R.string.add_camera));
		bt_back.setOnClickListener(this);
		bt_next_step.setOnClickListener(this);
		img_scan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent;
		switch (id) {
		case R.id.bt_back:
			onBackPressed();
			break;
		case R.id.bt_next_step:
			String uid = et_uid.getText().toString().trim();
			if (uid.length() == 20) {

				intent = new Intent(this, EzlinkSetInfoActivity.class);
				intent.putExtra("camera_uid", uid);
				startActivity(intent);
			}

			break;
		case R.id.img_scan:
			// É¨Ãè¶þÎ¬Âë
			intent = new Intent(this, CaptureActivity.class);
			startActivityForResult(intent, 0);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 0) {
				String result = data.getStringExtra("result");
				if (result.contains("u=") && result.contains("e=")) {
					String realUid2 = result.substring(2, result.indexOf(";"));
					et_uid.setText(realUid2);
				} else {
					ToastUtils.show(this, getString(R.string.not_camera));
				}
				LogUtil.LogI(Tag, "result");
			}
		}
	}
}
