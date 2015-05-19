package com.anji.www.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.os.Build;

/**
 * @author 0x0001
 */
public final class HttpRequest {
	private final Map<String, List<String>> attrs = new HashMap<String, List<String>>();
	private final Map<String, List<File>> files = new HashMap<String, List<File>>();
	private final Map<String, String> requestProperty;

	private String encoder = "UTF-8";

	private String url;
	private boolean isPost = false;

	private SignCallback callback;

	public static interface SignCallback {
		void sign(HttpRequest request, Map<String, List<String>> attrs);
	}

	private HttpRequest(String url) {
		requestProperty = new HashMap<String, String>();

		// requestProperty.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");
		// requestProperty.put("User-Agent",
		// "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.52 Safari/537.17");
		// requestProperty.put("Accept",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		this.url = url;
	}

	public static HttpRequest get(String url) {
		return new HttpRequest(url).setGet();
	}

	public static HttpRequest post(String url) {
		return new HttpRequest(url).setPost();
	}

	public HttpRequest setSignCallback(SignCallback callback) {
		this.callback = callback;
		return this;
	}

	public HttpRequest addAttr(String key, String value) {
		List<String> list = attrs.get(key);
		if (null == list) {
			list = new ArrayList<String>();
			attrs.put(key, list);
		}
		list.add(String.valueOf(value));
		return this;
	}

	public HttpRequest addAttr(String key, File file) {
		if (!file.exists()) {
			throw new IllegalArgumentException("file not exists : "
					+ file.getName());
		}
		List<File> list = files.get(key);
		if (null == list) {
			list = new ArrayList<File>();
			files.put(key, list);
		}
		list.add(file);
		return this;
	}

	public HttpRequest setReferer(String referer) {
		requestProperty.put("Referer", referer);
		return this;
	}

	public HttpRequest setEncoder(String encoder) {
		this.encoder = encoder;
		return this;
	}

	public HttpRequest setRequestWithAjax() {
		requestProperty.put("X-Requested-With", "XMLHttpRequest");
		return this;
	}

	/**
	 * 请求服务器获取返回的字符�?每次调用都会重新发�?请求)
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getResponse() throws IOException {
		InputStream in = getInputStream();
		StringBuilder response = new StringBuilder();
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(in,
				encoder));
		while ((line = br.readLine()) != null) {
			response.append(line).append("\n");
		}
		return response.toString();
	}

	/**
	 * 消�?请求返回的内�?	 * 
	 * @throws IOException
	 */
	public void consume() throws IOException {
		InputStream in = getInputStream();
		while (in.read() != -1)
			;
		in.close();
	}

	/**
	 * 只读取head,不读取内�?	 * 
	 * @throws IOException
	 */
	public void consumeHead() throws IOException {
		getInputStream().close();
	}

	/**
	 * 每次调用都会重新发起请求 (如果响应代码不为200则返回错误流)
	 * 
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		// if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
		// System.setProperty("http.keepAlive", "false");
		// }
		// if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO)
		// {
		//
		// System.setProperty("http.keepAlive", "false");
		//
		// }

		HttpURLConnection con;

		if (!isPost && isFileUpLoad()) {
			throw new IllegalArgumentException(
					"GET requests can not support file uploads!");
		}
		if (null != callback) {
			callback.sign(this, attrs);
		}
		String requestArgs = null;
		if (!isFileUpLoad()) {
			requestArgs = attr2UrlString();
		}
		if (isPost) {
			con = newConnection(url);
			// con.setDefaultRequestProperty(field, value);
			// con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestMethod("POST");
			// con.setRequestProperty("RANGE", "bytes="+"");
			if (isFileUpLoad()) {
				mutilPartPost(con);
			} else {
				plainPost(con, requestArgs);
			}
		} else {
			con = getRequest(requestArgs);
		}
		if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
			return con.getErrorStream();
		}
		return con.getInputStream();
	}

	public String getUrl() {
		return url;
	}

	// ---------------------------------------------------------- Private
	// Methods

	private HttpURLConnection getRequest(String requestArgs) throws IOException {
		HttpURLConnection con;
		if (requestArgs.length() > 0) {
			if (url.indexOf("?") >= 0) {
				url = url + "&" + requestArgs;
			} else {
				url = url + "?" + requestArgs;
			}
		}
		con = newConnection(url);
		con.setRequestMethod("GET");
		return con;
	}

	private void plainPost(HttpURLConnection con, String requestArgs)
			throws IOException {
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");
		con.setRequestProperty("accept", "*/*");
		con.setRequestProperty("connection", "Keep-Alive");
		con.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(50000);
		con.setReadTimeout(50000);
		// con.setRequestProperty("Connection", "keep-alive");

		// con.setRequestProperty("", newValue);
		if (requestArgs.length() > 0) {
			con.setDoInput(true);
			con.setDoOutput(true);
			con.getOutputStream().write(requestArgs.getBytes());
		}
	}

	private void mutilPartPost(HttpURLConnection con) throws IOException {
		String boundary = "--------" + UUID.randomUUID().toString();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ boundary);
		con.setRequestProperty("accept", "*/*");
		con.setRequestProperty("connection", "Keep-Alive");
		con.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(100000);
		con.setReadTimeout(100000);

		LengthOutputStream lengthOut = new LengthOutputStream();
		writeMultiPart(lengthOut, boundary);
		con.setFixedLengthStreamingMode(lengthOut.getLength());
		con.setDoInput(true);
		con.setDoOutput(true);

		OutputStream out = con.getOutputStream();
		writeMultiPart(out, boundary);

	}

	private void writeMultiPart(OutputStream out, String boundary)
			throws IOException {

		byte[] buf = new byte[512];
		int bufLen = 0;
		for (Entry<String, List<File>> entry : files.entrySet()) {
			String key = entry.getKey();
			for (File file : entry.getValue()) {
				String contentType = "application/octet-stream";// "text/x-"+file.getName();
				out.write(("--" + boundary + "\r\n").getBytes());
				out.write(("Content-Disposition: form-data; name=\"" + key
						+ "\"; filename=\"" + file.getName() + "\"\r\n")
						.getBytes());
				out.write(("Content-Type: " + contentType + "\r\n\r\n")
						.getBytes());

				InputStream in = null;
				try {
					in = new FileInputStream(file);
					while ((bufLen = in.read(buf)) != -1) {
						out.write(buf, 0, bufLen);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					if (null != in) {
						in.close();
					}
				}
				out.write("\r\n".getBytes());
			}
		}

		if (!attrs.isEmpty()) {
			for (Entry<String, List<String>> entry : attrs.entrySet()) {
				String key = entry.getKey();
				for (String value : entry.getValue()) {
					out.write(("--" + boundary + "\r\n").getBytes());
					out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n")
							.getBytes());
					out.write((value + "\r\n").getBytes());
				}
			}
		}
		out.write(("--" + boundary + "--").getBytes());
	}

	private boolean isFileUpLoad() {
		return !files.isEmpty();
	}

	private HttpURLConnection newConnection(String url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) new URL(url)
				.openConnection();
		for (Entry<String, String> entry : requestProperty.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}
		if (url.toLowerCase().startsWith("https")) {
			final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(final X509Certificate[] chain,
						final String authType) {
				}

				@Override
				public void checkServerTrusted(final X509Certificate[] chain,
						final String authType) {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} };
			SSLContext sslContext;
			try {
				sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustAllCerts,
						new java.security.SecureRandom());
				final SSLSocketFactory sslSocketFactory = sslContext
						.getSocketFactory();
				((HttpsURLConnection) con)
						.setSSLSocketFactory(sslSocketFactory);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
		}
		return con;
	}

	private HttpRequest setGet() {
		isPost = false;
		return this;
	}

	private HttpRequest setPost() {
		isPost = true;
		return this;
	}

	private String attr2UrlString() {
		if (attrs.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Entry<String, List<String>> entry : attrs.entrySet()) {
			String key = entry.getKey();
			for (String value : entry.getValue()) {
				if (first) {
					first = false;
				} else {
					builder.append("&");
				}
				builder.append(key).append("=").append(encode(value));
			}
		}
		return builder.toString();
	}

	private String encode(String value) {
		try {
			return URLEncoder.encode(value, encoder);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return value;
	}

	private static final class LengthOutputStream extends OutputStream {
		private int length = 0;

		@Override
		public void write(int b) throws IOException {
			length++;
		}

		public int getLength() {
			return length;
		}
	}
}
