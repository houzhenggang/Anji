package com.anji.www.activity;

import java.util.Set;

import org.json.JSONException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import com.anji.www.R;
import com.anji.www.constants.MyConstants;
import com.anji.www.entry.Member;
import com.anji.www.network.JsonParserFactory;
import com.anji.www.network.NetReq;
import com.anji.www.util.DisplayUtils;
import com.anji.www.util.MyActivityManager;
import com.anji.www.util.ToastUtils;
import com.anji.www.util.Utils;

/**
 * ��½ҳ��
 * 
 * @author Ivan
 * @since 9,24
 */
public class LoginActivity extends InstrumentedActivity implements
		OnClickListener
{

	private ImageView img_shared;
	private EditText et_usrename;
	private EditText et_password;
	private Button bt_login;
	private Button bt_register;
	private TextView tv_forget_pass;
	private Dialog progressDialog;
	private LoginTask loginTask;
	private Context context;
	private String username;
	private String password;
	private static final String TAG = "LoginActivity";
	private boolean isFromChangePass;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		isFromChangePass = getIntent().getBooleanExtra("isFromChangePass",
				false);
		context = this;
		findView();
		setLister();
		isLogined();
	}

	private void isLogined()
	{
		String json = Utils.load(this);
		if (!TextUtils.isEmpty(json) && json.length() > 0)
		{
			// ��ǰ��½��
			try
			{
				// String jsonData = EncyrptUtils.decrypt(
				// MyConstants.PREFERENCE_NAME, json);
				Member member = JsonParserFactory.parseMemberInfo(json,
						context, false);
				if (member != null && !TextUtils.isEmpty(member.getSsuid())
						&& !TextUtils.isEmpty(member.getMemberId()))
				{
					MyApplication application = (MyApplication) getApplication();
					application.setMember(member);
					// Intent intent = new Intent(this, MainActivity.class);
					// startActivity(intent);
					// finish();
					// overridePendingTransition(android.R.anim.slide_in_left,
					// android.R.anim.slide_out_right);
					et_usrename.setText(member.getUsername());
					if (isFromChangePass)
					{
						et_password.setText("");
					}
					else
					{
						et_password.setText(member.getPassword());
					}
				}
				// else {
				//
				// }
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// MyActivityManager.finishAllActivity();
	}

	private void findView()
	{
		img_shared = (ImageView) findViewById(R.id.img_shared);
		et_usrename = (EditText) findViewById(R.id.et_usrename);
		et_password = (EditText) findViewById(R.id.et_password);
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_register = (Button) findViewById(R.id.bt_register);
		tv_forget_pass = (TextView) findViewById(R.id.tv_forget_pass);
		progressDialog = DisplayUtils.createDialog(this);
	}

	private void setLister()
	{
		img_shared.setOnClickListener(this);
		bt_login.setOnClickListener(this);
		bt_register.setOnClickListener(this);
		tv_forget_pass.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		Intent intent;
		switch (id)
		{
		case R.id.img_shared:
			// ����
			showShare();
			break;
		case R.id.bt_login:
			// ��½
			username = et_usrename.getText().toString().trim();
			password = et_password.getText().toString().trim();
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
			startLogin();
			break;
		case R.id.bt_register:
			// ע��
			intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.tv_forget_pass:
			// ��������
			intent = new Intent(LoginActivity.this,
					ForgetPasswordActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;

		default:
			break;
		}
	}

	private void startLogin()
	{
		if (progressDialog != null && !progressDialog.isShowing())
		{
			progressDialog.show();
		}
		loginTask = new LoginTask();
		loginTask.execute();
	}

	private void cancelLogin()
	{
		if (loginTask != null)
		{
			loginTask.cancel(true);
			loginTask = null;
		}
	}

	private class LoginTask extends AsyncTask<Object, Object, Void>
	{
		Member member;

		@Override
		protected Void doInBackground(Object... params)
		{
			member = NetReq.Login(username, password, context);
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
				 * 200���ɹ� 300��ϵͳ�쳣 401���û�������Ϊ�� 402�����벻��Ϊ�� 403���û��������� 404�����벻��ȷ
				 * 405:�˺�δ�ɹ�ע��
				 */
				if (member.getResponseStatus() == 200)
				{
					JPushInterface.setAlias(LoginActivity.this,
							MyConstants.ALIAS_HEAD + member.getMemberId(),
							new TagAliasCallback()
							{
								@Override
								public void gotResult(int arg0, String arg1,
										Set<String> arg2)
								{
									Log.i(TAG, "respone arg0 = " + arg0);
								}
							});
					Intent intent;
					// ע��ɹ� ��һ��
					MyApplication app = (MyApplication) getApplication();
					app.setMember(member);
					if (!TextUtils.isEmpty(member.getSsuid())
							&& member.getSsuid().length() == 16)
					{

						intent = new Intent(LoginActivity.this,
								MainActivity.class);
						MainActivity.isNeedRefresh = true;
						startActivity(intent);
						finish();
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
					}
					else
					{
						intent = new Intent(LoginActivity.this,
								ChangeGatewayActitivy.class);
						intent.putExtra("isLogin", true);
						startActivity(intent);
						finish();
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
					}

				}
				else if (member.getResponseStatus() == 300)
				{
					ToastUtils.show(context,
							context.getString(R.string.system_error));
				}
				else if (member.getResponseStatus() == 401)
				{
					ToastUtils.show(context,
							context.getString(R.string.name_null));
				}
				else if (member.getResponseStatus() == 402)
				{
					ToastUtils.show(context,
							context.getString(R.string.pass_null));
				}
				else if (member.getResponseStatus() == 403)
				{
					ToastUtils.show(context,
							context.getString(R.string.name_not_exist));
				}
				else if (member.getResponseStatus() == 404)
				{
					ToastUtils.show(context,
							context.getString(R.string.password_error));
				}
				else if (member.getResponseStatus() == 405)
				{
					ToastUtils.show(context,
							context.getString(R.string.register_no_sucess));
				}
			}
			else
			{
				// ��������ʧ��
			}

		}
	}

	@Override
	public void onBackPressed()
	{
		// System.exit(0);
		MyActivityManager.finish("MainActivity");
		super.onBackPressed();
		// int nPid = android.os.Process.myPid();
		// android.os.Process.killProcess(nPid);
	}

	private void showShare()
	{
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// �ر�sso��Ȩ
		oks.disableSSOWhenAuthorize();

		// ����ʱNotification��ͼ�������
		oks.setNotification(R.drawable.ic_launcher,
				getString(R.string.app_name));
		// title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		oks.setTitle(getString(R.string.share));
		// titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		oks.setTitleUrl("http://www.zean.co/_d276748171.htm");
		// text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		oks.setText(getString(R.string.share_text_title)+"http://www.zean.co/_d276748171.htm");
		// imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		// oks.setImagePath("/sdcard/test.jpg");// ȷ��SDcard������ڴ���ͼƬ
		// url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		oks.setUrl("http://www.zean.co/_d276748171.htm");
		// comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
//		oks.setComment("���ǲ��������ı�");
		// site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		oks.setSite(getString(R.string.app_name));
		// siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		oks.setSiteUrl("http://sharesdk.cn");
		// ��༭ҳ����ʾΪDialogģʽ
		// oks.setDialogMode();
		// oks.setSilent(false);
		// ��������GUI
		oks.show(this);
	}

	// ʹ�ÿ�ݷ�����ɷ����������ϸ�Ķ�λ��SDK��ѹĿ¼��Docs�ļ�����OnekeyShare���JavaDoc��
	/**
	 * ShareSDK���ɷ���������</br>
	 * 1����һ�������÷�ʽ����������onekeyshare��Ŀ��onekeyshare��Ŀ������mainlibs��</br>
	 * 2���ڶ����ǰ�onekeyshare��mainlibs���ɵ���Ŀ�У������Ӿ����õڶ��ַ�ʽ</br> �뿴��ShareSDK
	 * ʹ��˵���ĵ�����SDK����Ŀ¼�� </br> ���߿����缯���ĵ�
	 * http://wiki.mob.com/Android_%E5%BF%AB%E9%
	 * 80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
	 * 3������ʱ����sample���߱����ӵĻ�������copy��ȥ����proguard-project.txt�ļ��� ƽ̨������Ϣ�����ַ�ʽ��
	 * 1�������Ǻ�̨���ø���΢��ƽ̨��key
	 * 2���ڴ��������ø���΢��ƽ̨��key��http://mob.com/androidDoc/cn/sharesdk
	 * /framework/ShareSDK.html 3���������ļ������ã������������assets/ShareSDK.conf,
	 */
	// private void showShare(boolean silent, String platform, boolean
	// captureView) {
	// Context context = this;
	// final OnekeyShare oks = new OnekeyShare();
	//
	// oks.setNotification(R.drawable.ic_launcher,
	// context.getString(R.string.app_name));
	// //oks.setAddress("12345678901");
	// oks.setTitle(CustomShareFieldsPage.getString("title",
	// context.getString(R.string.evenote_title)));
	// oks.setTitleUrl(CustomShareFieldsPage.getString("titleUrl",
	// "http://mob.com"));
	// String customText = CustomShareFieldsPage.getString( "text", null);
	// if (customText != null) {
	// oks.setText(customText);
	// } else if (MainActivity.TEST_TEXT != null &&
	// MainActivity.TEST_TEXT.containsKey(0)) {
	// oks.setText(MainActivity.TEST_TEXT.get(0));
	// } else {
	// oks.setText(context.getString(R.string.share_content));
	// }
	//
	// if (captureView) {
	// oks.setViewToShare(getPage());
	// } else {
	// oks.setImagePath(CustomShareFieldsPage.getString("imagePath",
	// MainActivity.TEST_IMAGE));
	// oks.setImageUrl(CustomShareFieldsPage.getString("imageUrl",
	// MainActivity.TEST_IMAGE_URL));
	// oks.setImageArray(new String[]{MainActivity.TEST_IMAGE,
	// MainActivity.TEST_IMAGE_URL});
	// }
	// oks.setUrl(CustomShareFieldsPage.getString("url", "http://www.mob.com"));
	// oks.setFilePath(CustomShareFieldsPage.getString("filePath",
	// MainActivity.TEST_IMAGE));
	// oks.setComment(CustomShareFieldsPage.getString("comment",
	// context.getString(R.string.share)));
	// oks.setSite(CustomShareFieldsPage.getString("site",
	// context.getString(R.string.app_name)));
	// oks.setSiteUrl(CustomShareFieldsPage.getString("siteUrl",
	// "http://mob.com"));
	// oks.setVenueName(CustomShareFieldsPage.getString("venueName",
	// "ShareSDK"));
	// oks.setVenueDescription(CustomShareFieldsPage.getString("venueDescription",
	// "This is a beautiful place!"));
	// oks.setLatitude(23.056081f);
	// oks.setLongitude(113.385708f);
	// oks.setSilent(silent);
	// oks.setShareFromQQAuthSupport(shareFromQQLogin);
	// String theme = CustomShareFieldsPage.getString("theme", null);
	// if(OnekeyShareTheme.SKYBLUE.toString().toLowerCase().equals(theme)){
	// oks.setTheme(OnekeyShareTheme.SKYBLUE);
	// }else{
	// oks.setTheme(OnekeyShareTheme.CLASSIC);
	// }
	//
	// if (platform != null) {
	// oks.setPlatform(platform);
	// }
	//
	//
	// // ��༭ҳ����ʾΪDialogģʽ
	// oks.setDialogMode();
	//
	// // ���Զ���Ȩʱ���Խ���SSO��ʽ
	// if(!CustomShareFieldsPage.getBoolean("enableSSO", true))
	// oks.disableSSOWhenAuthorize();
	//
	// // ȥ��ע�ͣ����ݷ���Ĳ��������ͨ��OneKeyShareCallback�ص�
	// // oks.setCallback(new OneKeyShareCallback());
	//
	// // ȥ�Զ��岻ͬƽ̨���ֶ�����
	// oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
	//
	// // ȥ��ע�ͣ���ʾ�ھŹ��������Զ����ͼ��
	// // Bitmap logo = BitmapFactory.decodeResource(menu.getResources(),
	// R.drawable.ic_launcher);
	// // String label = menu.getResources().getString(R.string.app_name);
	// // OnClickListener listener = new OnClickListener() {
	// // public void onClick(View v) {
	// // String text = "Customer Logo -- ShareSDK " +
	// ShareSDK.getSDKVersionName();
	// // Toast.makeText(menu.getContext(), text, Toast.LENGTH_SHORT).show();
	// // oks.finish();
	// // }
	// // };
	// // oks.setCustomerLogo(logo, label, listener);
	//
	// // ȥ��ע�ͣ����ݷ���Ź����н���������΢������Ѷ΢��
	// // oks.addHiddenPlatform(SinaWeibo.NAME);
	// // oks.addHiddenPlatform(TencentWeibo.NAME);
	//
	// // ΪEditPage����һ��������View
	// oks.setEditPageBackground(getPage());
	//
	// //����kakaoTalk��������ʱ�����������Ϣʱ�����Ӧ�ò����ڣ���ת��Ӧ�õ����ص�ַ
	// oks.setInstallUrl("http://www.mob.com");
	// //����kakaoTalk��������ʱ�����������Ϣʱ�����Ӧ�ô��ڣ�����Ӧ��app
	// oks.setExecuteUrl("kakaoTalkTest://starActivity");
	//
	// oks.show(context);
	// }
}
