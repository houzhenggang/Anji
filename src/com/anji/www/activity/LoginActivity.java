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
 * 登陆页面
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
			// 以前登陆过
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
			// 分享
			showShare();
			break;
		case R.id.bt_login:
			// 登陆
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
			// 注册
			intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.tv_forget_pass:
			// 忘记密码
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
				 * 200：成功 300：系统异常 401：用户名不能为空 402：密码不能为空 403：用户名不存在 404：密码不正确
				 * 405:账号未成功注册
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
					// 注册成功 下一步
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
				// 网络请求失败
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
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://www.zean.co/_d276748171.htm");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(getString(R.string.share_text_title)+"http://www.zean.co/_d276748171.htm");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://www.zean.co/_d276748171.htm");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");
		// 令编辑页面显示为Dialog模式
		// oks.setDialogMode();
		// oks.setSilent(false);
		// 启动分享GUI
		oks.show(this);
	}

	// 使用快捷分享完成分享（请务必仔细阅读位于SDK解压目录下Docs文件夹中OnekeyShare类的JavaDoc）
	/**
	 * ShareSDK集成方法有两种</br>
	 * 1、第一种是引用方式，例如引用onekeyshare项目，onekeyshare项目再引用mainlibs库</br>
	 * 2、第二种是把onekeyshare和mainlibs集成到项目中，本例子就是用第二种方式</br> 请看“ShareSDK
	 * 使用说明文档”，SDK下载目录中 </br> 或者看网络集成文档
	 * http://wiki.mob.com/Android_%E5%BF%AB%E9%
	 * 80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
	 * 3、混淆时，把sample或者本例子的混淆代码copy过去，在proguard-project.txt文件中 平台配置信息有三种方式：
	 * 1、在我们后台配置各个微博平台的key
	 * 2、在代码中配置各个微博平台的key，http://mob.com/androidDoc/cn/sharesdk
	 * /framework/ShareSDK.html 3、在配置文件中配置，本例子里面的assets/ShareSDK.conf,
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
	// // 令编辑页面显示为Dialog模式
	// oks.setDialogMode();
	//
	// // 在自动授权时可以禁用SSO方式
	// if(!CustomShareFieldsPage.getBoolean("enableSSO", true))
	// oks.disableSSOWhenAuthorize();
	//
	// // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
	// // oks.setCallback(new OneKeyShareCallback());
	//
	// // 去自定义不同平台的字段内容
	// oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
	//
	// // 去除注释，演示在九宫格设置自定义的图标
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
	// // 去除注释，则快捷分享九宫格中将隐藏新浪微博和腾讯微博
	// // oks.addHiddenPlatform(SinaWeibo.NAME);
	// // oks.addHiddenPlatform(TencentWeibo.NAME);
	//
	// // 为EditPage设置一个背景的View
	// oks.setEditPageBackground(getPage());
	//
	// //设置kakaoTalk分享链接时，点击分享信息时，如果应用不存在，跳转到应用的下载地址
	// oks.setInstallUrl("http://www.mob.com");
	// //设置kakaoTalk分享链接时，点击分享信息时，如果应用存在，打开相应的app
	// oks.setExecuteUrl("kakaoTalkTest://starActivity");
	//
	// oks.show(context);
	// }
}
