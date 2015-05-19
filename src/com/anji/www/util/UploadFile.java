package com.anji.www.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.protocol.HTTP;

import android.util.Log;

public class UploadFile {
	private URL url;
	public HttpURLConnection conn;
	private String boundary = "--------------------123post654";
	private Map<String, String> textParams = new HashMap<String, String>();
	private Map<String, String> fileparams = new HashMap<String, String>();
	public DataOutputStream ds;
	private String fileName = null;
	private File file = null;

	public UploadFile(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	// 增加一个文本数据到form表单数据中
	public void addTextParameter(String name, String value) {
		textParams.put(name, value);
	}

	// 增加一个文件到form表单数据中
	public void addFileParameter(String name, String value) {
		fileparams.put(name, value);
	}

	// 发送数据到服务器
	public String send() throws Exception {
		conn = (HttpURLConnection) this.url.openConnection();
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(120000); // 连接超时为120秒
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
		conn.addRequestProperty("Accept-Charset", "utf-8");
		conn.connect();

		ds = new DataOutputStream(conn.getOutputStream());
		// 表单文本数据writeStringParams();
		Set<String> keySet = textParams.keySet();
		String name = null;
		String textValue;
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			name = it.next();
			ds.writeBytes("--" + boundary + "\r\n");
			ds.writeBytes("Content-Disposition: form-data; name=\"" + name
					+ "\"\r\n");
			ds.writeBytes("\r\n");
			Log.i("------>name--->", name + "-----" + textParams.get(name));
			textValue = textParams.get(name);
			if (textValue != null) {
				ds.write(textValue.getBytes(HTTP.UTF_8));
			}
			ds.writeBytes("\r\n");
		}
		textParams.clear();

		// 表单文件数据writeFileParams();
		keySet = fileparams.keySet();
		it = keySet.iterator();
		//File file;
		FileInputStream in2;
		byte[] bt;
		while (it.hasNext()) {
			name = it.next();
			Log.i("------>key name--->", name + "-----");
			if (!"".equals(fileparams.get(name))) {
				file = new File(fileparams.get(name));
				fileName = file.getName();
				Log.i("------>name--->", fileparams.get(name) + "-----");
				ds.writeBytes("--" + boundary + "\r\n");
				Log.i("------>http--->", "--" + boundary + "\r\n");
				ds.writeBytes("Content-Disposition: form-data; name=\""
						+ name + "\"; filename=\"" + fileName + "\"\r\n");
				Log.i("------>http--->","Content-Disposition: form-data; name=\""
						+ name + "\"; filename=\"" + fileName + "\"\r\n");
				ds.writeBytes("Content-Type: " + "audio/mpeg" + "\r\n");
				Log.i("------>http--->", "Content-Type: " + "audio/mpeg" + "\r\n");
				ds.writeBytes("\r\n");
				Log.i("------>http--->","\r\n");
				// 把文件转换成字节数组
				in2 = new FileInputStream(file);
				bt = new byte[1024];
				int n;
				while ((n = in2.read(bt)) != -1) {
					ds.write(bt, 0, n);// out.toByteArray());
				}
				in2.close();
			}
			ds.writeBytes("\r\n");
		}
		bt = null;
		fileparams.clear();
		paramsEnd();
		if(null != conn)
			return readStream(conn.getInputStream());
		return null;
	}
	
	// 添加结尾数据
	private void paramsEnd() throws IOException {
		ds.writeBytes("--" + boundary + "--" + "\r\n");
		ds.writeBytes("\r\n");
	}
	
	/**
	 * readStream
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public String readStream(InputStream in) throws Exception{
		ByteArrayOutputStream bis = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = in.read(buffer)) != -1){
			bis.write(buffer, 0, len);
		}
		bis.close();
		in.close();
		return new String(bis.toByteArray(), "UTF-8");
	}
}
