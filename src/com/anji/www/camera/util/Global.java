package com.anji.www.camera.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.anji.www.camera.modl.Camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioRecord;
import android.os.Handler;


/**
 * 系统全局类
 * 
 * @author FuS
 * @date 2014-4-9 上午10:27:30
 */
public class Global {
	/** 主界面的容器高 */
	public static int containerHeight = 0;
	/** 主界面的容器宽 */
	public static int containerWidth = 0;

	/** 屏幕高（像素） */
	public static int screenHeight = 0;
	/** 屏幕宽（像素） */
	public static int screenWidth = 0;

	
	public static AudioRecord mAudioRecord;
	/** 用户所拥有的camera列表 */
	public static ArrayList<Camera> g_camera = new ArrayList<Camera>();
	// public static Map<String, Camera> g_camera = new LinkedHashMap<String,
	// Camera>();

	/** 历史activiy(目前只记录了视频播放的界面，如果要删除camera时，需要将视频播放的activity finish) */
	public static ArrayList<Activity> activityTask = new ArrayList<Activity>();

	/** 程序是否正在运行 */
	public static boolean ISRUNNING = false;
	
	/** 创建ipc链接后 返回的 handler flag */
	public static int mHandlerNo = -3;

	/** 全局handler，得到底层返回的消息后，传递该消息到对应的界面 */
	public static Handler mHandler = null;

	/** 当前使用的视频摄像头 */
	public static Camera currentPlayCamera = null;

	/** 文件（快照，录像，日志等）存放的目录（sd目录+/FoscamApp/） */
	public static String SDPath = "";
	
	/** 当前音频是否打开 */
	public static boolean isAudioOpenOrNot = false;
	
	/** 视频播放控件的宽 */
	public static boolean isRecordOrNot = false; 
	/** 视频播放控件的宽 */
	public static int videoScreenWidth = 0;
	/** 视频播放控件的高 */
	public static int videoScreenHeight = 0;
	/** 是否横屏 */
	public static boolean isLandspace = false;
	
	/** 一次连接中，是否已经接收到过视频数据 ,断开后算另外一次 */
	public static int OPEN_VIDEO_STATE = -1;
	/** 一次连接中，是否已经接收到过视频数据 ,断开后算另外一次 */
	public static boolean isReceiveData = false;
	/** 是否需要抓拍图片（点击抓拍按钮时） */
	public static boolean isNeedSnap = false;
	/** 是否需要抓拍图片（录像时，需要抓拍一张图片作为录像文件的缩略图） */
	public static boolean isNeedSnapRecord = false;
	/** 是否需要抓拍图片（进入视频预览界面后，需要抓拍一张图片作为缓存图片） */
	public static boolean isNeedSnaps = false;

	/** 用于保存camera list 获取的 帧 **/
	public static HashMap<Camera, Drawable> FRAME_IMAGE = new HashMap<Camera, Drawable>();
	/** 要抓拍的图片 */
	public static Bitmap snapBmp = null;
	/*** 屏幕密度，用于从 dip和 px之间的转换 **/
	public static float scale = 0f;

	public static boolean isIPCconect = false;
	/** 云平台采用的语言 */
	public static String cloudPlatLanguage = "CHS";
	/** 注册云账户的时候，是否需要输入邀请码。这个标识是从云服务器上获取的。前期会要求使用邀请码，后期可能会移除。 */
	public static boolean isNeedInvitCode = false;
	/** 云账户登录后返回的临时token */
	public static String accessToken = "";
	/** 刷新登录令牌需要的令牌 */
	public static String refreshToken = "";
	/** OPENID（用户身份标示） */
	public static String openId = "";
	/** 登录令牌的过期时间（秒） */
	public static int tokenExpires = 0;
	/** 是否已经登陆 */
	public static boolean isLogin = false;
	/** android设备的唯一标识，优先mac地址，如果mac为空，则取一个uuid值 */
	public static String deviceUnique = "";
	/** 订阅消息用到 */
	public static String userTag = "";

	/**** IPCamera1 IP地址界面的帮助UI有没有显示过 ***/
	public static boolean IPCamera1_hasHelpIPShow = false;
	/**** IPCamera1 UID界面的帮助UI有没有显示过 ***/
	public static boolean IPCamera1_hasHelpUIDShow = false;
	/**** Fosbaby_conn_2 界面的帮助UI有没有显示过 ***/
	public static boolean Fosbaby_conn2_hasHelpShow = false;
	/**** Fosbaby_disconn2 界面的帮助UI有没有显示过 ***/
	public static boolean Fosbaby_disconn2_hasHelpShow = false;
	/**** Fosbaby_disconn3 界面的帮助UI有没有显示过 ***/
	public static boolean Fosbaby_disconn3_hasHelpShow = false;

	/** 是否需要打印日志 */
	public static boolean isNeedLog = true;

	// ########### SharedPreferences配置信息 begin ###########
	/**
	 * SharedPreferences中保存的配置信息，在内存中也保存一份，方便取用（在程序入口应该读取到内存中）。<br>
	 * 注意:修改SharedPreferences配置信息时，要同时修改内存中的数据。<br>
	 * SharedPreferences在内存中保存的变量统一用sp_作为前缀
	 */
	/** 是否只有在wifi环境下预览视频 */
	public static boolean sp_isWifiOnly = false;
	/** 是否显示帮助界面 */
	public static boolean sp_showHelpUI = true;
	/** 是否需要接收推送消息 */
	public static boolean sp_isOpenMsgPush = true;
	/** 是否删除历史图片和录像（在删除ipc的时候） */
	public static boolean sp_isDelHistoryFile = false;
	/** 镜像和翻转 */
	public static boolean sp_horizontalChecked = false;
	/** 镜像和翻转 */
	public static boolean sp_verticalChecked = false;
	/** 添加camera的方式 0：uid；1：ddns；2：ip */
	public static int sp_ipcAddType = 0;
	// ########### SharedPreferences配置信息 end ###########
}
