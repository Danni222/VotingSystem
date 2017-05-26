package com.qa.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


public class HttpUtil {
	private static String cookie;

	public static String getResult2(String url, Map<String, String> keyMap) {
		// String m = getResult(url,keyMap);
		String result = "";
		try {
			HttpPost request = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (null != keyMap) {
				for (String key : keyMap.keySet()) {
					params.add(new BasicNameValuePair(key, keyMap.get(key)));
				}
			}

			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			//增加httpheader参数cookie
			if (cookie != null) {
				request.addHeader("Cookie", cookie);
			}
			
			//设置http头部参数
			request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);

			Header h1 = httpResponse.getFirstHeader("Set-Cookie");
			if (h1 != null) {
				cookie = getCookieValue(h1.getValue());
			}
			h1 = httpResponse.getLastHeader("Set-Cookie");
		

			if (httpResponse.getStatusLine().getStatusCode() != 404) {
				result = EntityUtils.toString(httpResponse.getEntity());
				// System.out.println(result);
			}
		} catch (Exception e) {
			result = "网络连接出现异常";
			e.printStackTrace();
			//System.out.println(result+Constant.REMOTE_URL + "Register");
		}

		return result;
	}

	private static String getCookieValue(String cookie) {
		try {
			int end = cookie.indexOf(";");
			return cookie.substring(0, end);
		} catch (Exception e) {
			System.out.println(cookie);
			e.printStackTrace();
		}
		return cookie;
	}

	public static void setCookieNull() {
		cookie = null;
	}
}
