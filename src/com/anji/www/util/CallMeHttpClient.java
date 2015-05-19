package com.anji.www.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;



import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;




import com.anji.www.constants.Url;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @deprecation: 公用httpclient类
 * @author admin
 *
 */

public class CallMeHttpClient {

	private final HttpClient _theRealClient;
	private boolean stopped = false;

	public CallMeHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		//判断手机客户端连接的cmwap网络，设置代理
		
		if(isWap()){
			if (Url.DEBUG) {
				Log.i("CallMeHttpClient", "iswap=true ---");
			}
			Log.i("CallMeHttpClient", "iswap=false ---");
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			httpParameters.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		}
		
//		//判断手机客户端连接的cmwap网络，设置代理
//		if(isWap()){
//			String host=Proxy.getDefaultHost();
//			int port = Proxy.getPort(Application.this);  
//			HttpHost httpHost = new HttpHost(host, port); 
////			HttpHost proxy = new HttpHost("10.0.0.172", 80);
//			httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
//		}
		
//        HttpConnectionParams.setConnectionTimeout(httpParams, timeOut * 1000);
//        HttpConnectionParams.setSoTimeout(httpParams, timeOut * 1000);
//        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
//       
//        HttpClientParams.setRedirecting(httpParams, true);


		
		int timeoutConnection = 10 * 1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 30 * 1000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
        HttpProtocolParams.setUserAgent(httpParameters, userAgent);
        HttpClientParams.setRedirecting(httpParameters, true);
		_theRealClient = new DefaultHttpClient(httpParameters);
	}

	public String httpGet(String url) throws ClientProtocolException,
			URISyntaxException, IOException {
		Log.i("url:", url);
		return httpGet(url, null);
	}

	public String httpGet(String url, List<Header> headers)
			throws URISyntaxException, ClientProtocolException, IOException {
		String json=null;
		try {
			HttpGet request = new HttpGet();
//			request.addHeader("User-Agent", Constant.REQUEST_HEADER);
			request.addHeader("User-Agent",  "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6");
			request.setURI(new URI(url.replaceAll(" ", "%20")));

			// set headers, if there are
			if (headers != null) {
				for (Header header : headers) {
					request.addHeader(header);
				}
			}
			HttpResponse response = _theRealClient.execute(request);
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity=response.getEntity();
				json=EntityUtils.toString(entity,"UTF-8");
				json=java.net.URLDecoder.decode(json, "UTF-8");
				Log.i("json:", json);
			}
		} finally {
		
		}

		return json;
	}

	public List<Bitmap> downloadphoto(List<String> urls){
//		URL url=new URL("http://test.52callme.com:15987/upfiles/girls/27.png");
//		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
//		conn.setDoInput(true);
//		conn.connect();
//		InputStream input=conn.getInputStream();
//		bm=BitmapFactory.decodeStream(input);
		List<Bitmap> bms=new ArrayList<Bitmap>();
		BufferedInputStream bis = null;
		HttpGet request = new HttpGet();
		for(int i=0;i<urls.size();i++){
		try {
			String url=urls.get(i);
			request.setURI(new URI(url.replaceAll(" ", "%20")));
			HttpResponse response = _theRealClient.execute(request);
			bis = new BufferedInputStream(response.getEntity().getContent());
			Bitmap bm=BitmapFactory.decodeStream(bis);
			bms.add(bm);
		} catch (URISyntaxException e) {
			Log.i("test", "1234567890");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.i("test", "1234567890");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("test", "1234567890");
			e.printStackTrace();
		}
		}
		
		return bms;
		
	}
	
//	public boolean downloadFile(String url, File destfile) {
//
//		boolean succeed = true;
//		BufferedOutputStream bos = null;
//		BufferedInputStream bis = null;
//
//		File tempFile = null;
//		try {
//
//			tempFile = new File(destfile.getAbsolutePath() + ".td");
//			if (!tempFile.exists())
//				tempFile.createNewFile();
//
//			System.out.println("existed:" + tempFile.exists() + " is file:"
//					+ tempFile.isFile());
//
//			bos = new BufferedOutputStream(
//					new FileOutputStream(tempFile, false));
//
//			HttpGet request = new HttpGet();
//			request.setURI(new URI(url.replaceAll(" ", "%20")));
//
//			HttpResponse response = _theRealClient.execute(request);
//
//			bis = new BufferedInputStream(response.getEntity().getContent());
//
//			byte[] buffer = new byte[1024 * 100];// write to file every 1M
//			int count = 0;
//			while ((count = bis.read(buffer, 0, buffer.length)) != -1) {
//				// System.out.println("data from server");
//				if (stopped) {
//					succeed = false;
//					break;
//				}
//
//				bos.write(buffer, 0, count);
//			}
//
//			System.out.println(tempFile.length());
//
//		} catch (Exception e) {
//			succeed = false;
//			e.printStackTrace();
//		} finally {
//			try {
//				if (bis != null)
//					bis.close();
//			} catch (IOException e1) {
//			}
//			try {
//				if (bis != null)
//					bos.close();
//			} catch (IOException e) {
//			}
//		}
//
//		if (tempFile.length() == 0)
//			succeed = false;
//
//		if (succeed) {
//			System.out.println("rename succeed:" + tempFile.renameTo(destfile));
//		}
//
//		return succeed;
//	}
//
//	public boolean uploadFile(String url, String filePath) {
//
//		boolean succeed = true;
//		File file = new File(filePath);
//
//		System.out.println("file length:" + file.length());
//		HttpPost httpPost = new HttpPost();
//		try {
//			httpPost.setURI(new URI(url));
//			InputStreamEntity reqEntity = new InputStreamEntity(
//					new FileInputStream(file), file.length());
//			reqEntity.setContentType("binary/octet-stream");
//			httpPost.setEntity(reqEntity);
//			HttpResponse response = _theRealClient.execute(httpPost);
//			int returnCode = response.getStatusLine().getStatusCode();
//			if (returnCode != 200)
//				succeed = false;
//			String reasonPhrase = response.getStatusLine().getReasonPhrase();
//
//			System.out.println("upload file at:" + filePath + "\nreturnCode:"
//					+ returnCode + "  reason:" + reasonPhrase);
//
//		} catch (Exception e) {
//			succeed = false;
//			e.printStackTrace();
//		}
//
//		return succeed;
//	}

	public String httpPost(String endpoint) throws ClientProtocolException,
			IOException, URISyntaxException {

		return httpPost(endpoint, null, null);
	}

	public String httpPost(String endpoint, List<Header> headers)
			throws ClientProtocolException, IOException, URISyntaxException {

		return httpPost(endpoint, headers, null);
	}

	public String httpPost(String endpoint, NameValuePair[] params)
			throws ClientProtocolException, IOException, URISyntaxException {

		return httpPost(endpoint, null, params);
	}

	public String httpPost(String endpoint, List<Header> headers,
			NameValuePair[] params) throws ClientProtocolException,
			IOException, URISyntaxException {

		String json = null;

		try {
			HttpPost request = new HttpPost();
			request.setURI(new URI(endpoint));

			// set headers, if there are
			if (headers != null) {
				for (Header header : headers) {
					request.addHeader(header);
				}
			}
//			request.setHeader("Range","bytes="+"");

			if (params != null) {
				HttpParams httpParams = new BasicHttpParams();
				for (NameValuePair nameValuePair : params) {
					httpParams.setParameter(nameValuePair.getName(),
							nameValuePair.getValue());
				}
				request.setParams(httpParams);
			}

			HttpResponse response = _theRealClient.execute(request);
			
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity=response.getEntity();
				json=EntityUtils.toString(entity,"UTF-8");
				json=java.net.URLDecoder.decode(json, "UTF-8");
				Log.i("test", "json="+json);
			}

		} finally {
		}

		return json;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

//	public boolean downloadFile0(String url, File destfile) {
//
//		boolean succeed = true;
//		BufferedOutputStream bos = null;
//		BufferedInputStream bis = null;
//
//		File tempFile = null;
//		// int contentLength = -1;
//		try {
//
//			tempFile = new File(destfile.getAbsolutePath() + ".td");
//			if (!tempFile.exists())
//				tempFile.createNewFile();
//
//			System.out.println("existed:" + tempFile.exists() + " is file:"
//					+ tempFile.isFile());
//
//			bos = new BufferedOutputStream(
//					new FileOutputStream(tempFile, false));
//
//			URLConnection connection = new URL(url).openConnection();
//			connection.setConnectTimeout(10 * 1000);
//			connection.setReadTimeout(30 * 1000);
//			InputStream response = connection.getInputStream();
//			// String contenLengthStr =
//			// connection.getHeaderField("Content-Length");
//			// contentLength = Integer.parseInt(contenLengthStr);
//			bis = new BufferedInputStream(response);
//
//			byte[] buffer = new byte[1024 * 100];// write to file every 1M
//			int count = 0;
//			while ((count = bis.read(buffer, 0, buffer.length)) != -1) {
//				if (stopped) {
//					succeed = false;
//					break;
//				}
//				bos.write(buffer, 0, count);
//			}
//
//			System.out.println(tempFile.length());
//
//		} catch (Exception e) {
//			succeed = false;
//			e.printStackTrace();
//		} finally {
//			try {
//				if (bis != null)
//					bis.close();
//			} catch (IOException e1) {
//			}
//			try {
//				if (bos != null)
//					bos.close();
//			} catch (IOException e) {
//			}
//		}
//
//		if (tempFile.length() == 0)
//			succeed = false;
//
//		if (succeed) {
//			System.out.println("rename succeed:" + tempFile.renameTo(destfile));
//		}
//
//		return succeed;
//	}
//
//	public boolean uploadFile0(String url, String filePath) {
//
//		boolean succeed = true;
//		File file = new File(filePath);
//		InputStream fin = null;
//		OutputStream output = null;
//
//		System.out.println("file length:" + file.length());
//		try {
//
//			URLConnection connection = new URL(url).openConnection();
//			connection.setDoOutput(true); // Triggers POST.
//			connection
//					.setRequestProperty("Content-Type", "binary/octet-stream");
//			connection.setRequestProperty("Content-Length", "" + file.length());
//
//			output = connection.getOutputStream();
//
//			fin = new FileInputStream(file);
//			byte[] buffer = new byte[1024 * 100];// write to file every 1M
//			int count = 0;
//			while ((count = fin.read(buffer, 0, buffer.length)) != -1) {
//				output.write(buffer, 0, count);
//			}
//
//			HttpURLConnection httpConn = ((HttpURLConnection) connection);
//			int status = httpConn.getResponseCode();
//			if (status != 200)
//				succeed = false;
//
//			System.out.println("upload file at:" + filePath + "\nreturnCode:"
//					+ status);
//
//		} catch (Exception e) {
//			succeed = false;
//			e.printStackTrace();
//		} finally {
//			try {
//				if (output != null)
//					output.close();
//			} catch (IOException e1) {
//			}
//			try {
//				if (fin != null)
//					fin.close();
//			} catch (IOException e) {
//			}
//		}
//
//		return succeed;
//	}
//
//	public static String postRequestXml(String requstUrl, String requestXml) {
//		String responseCode = "0";
//		HttpURLConnection conn = null;
//		OutputStream outStream = null;
//		try {
//			byte[] data = requestXml.getBytes();
//			URL url = new URL(requstUrl);
//
//			conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
//			conn.setRequestMethod("POST");
//			conn.setConnectTimeout(30 * 1000);
//
//			conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
//			conn.setRequestProperty("Content-Length",
//					Integer.toString(data.length));
//			outStream = conn.getOutputStream();
//			outStream.write(data);
//			if (conn.getResponseCode() == 200) {
//
//				responseCode = readXml(conn.getInputStream());
//			} else {
//				responseCode = "-1";
//			}
//			Log.v("responseCode", conn.getResponseCode() + "");
//		} catch (Exception ex) {
//			responseCode = "-1";
//			ex.printStackTrace();
//			Log.v("error", ex.getMessage());
//		} finally {
//			try {
//				if (outStream != null)
//					outStream.flush();
//				outStream.close();
//			} catch (IOException ix) {
//				ix.printStackTrace();
//			}
//			if (conn != null)
//				conn.disconnect();
//		}
//
//		return responseCode;
//	}
//
//	public static String uploadPhoto(String requstUrl, byte[] photoByte) {
//		String responseCode = "0";
//		try {
//			if (photoByte != null) {
//				URL url = new URL(requstUrl);
//				HttpURLConnection conn = (HttpURLConnection) url
//						.openConnection();
//				conn.setRequestMethod("POST");
//				conn.setConnectTimeout(30 * 1000);
//				conn.setDoOutput(true);
//				conn.setRequestProperty("Content-Type", "binary/octet-stream");
//				conn.setRequestProperty("Content-Length", "" + photoByte.length);
//				OutputStream outStream = conn.getOutputStream();
//				int count = photoByte.length;
//				// while ((count = photoByte.length) != -1) {
//				// outStream.write(photoByte, 0, count);
//				// }
//				outStream.write(photoByte, 0, count);
//				if (conn.getResponseCode() == 200) {
//					responseCode = readXml(conn.getInputStream());
//				} else {
//					responseCode = "-1";
//				}
//
//				if (outStream != null) {
//					outStream.flush();
//					outStream.close();
//				}
//
//				if (conn != null)
//					conn.disconnect();
//			} else {
//				responseCode = "-1";
//			}
//		} catch (Exception ex) {
//			responseCode = "-1";
//			ex.printStackTrace();
//		}
//
//		return responseCode;
//	}
//
//	private static String readXml(InputStream inStream) {
//		String code = "0";
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		try {
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			Document dom = builder.parse(inStream);
//			Element root = dom.getDocumentElement();
//			Element node = (Element) root.getElementsByTagName("Result")
//					.item(0);
//			code = node.getAttribute("Code");
//		} catch (Exception ex) {
//			code = "-1";
//			ex.printStackTrace();
//		}
//
//		return code;
//	}
	
	// 判断网络类型
	public static boolean isWap() {
		String proxyHost = android.net.Proxy.getDefaultHost();
		if (proxyHost != null) {
			return true;
		} else {
			return false;
		}
	}

}
