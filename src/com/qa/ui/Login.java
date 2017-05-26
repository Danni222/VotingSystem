package com.qa.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private Button btn_login = null;
	private Button btn_reg = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
//		Date dt = new Date();
//		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(dt);

		// int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		// if (w < 0)
		// w = 0;
		// if(w!=4){
		// finish();
		// }

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_reg = (Button)findViewById(R.id.btn_reg);

		EditText edt = ((EditText) findViewById(R.id.edt_server_ip));
		EditText udt = ((EditText) findViewById(R.id.edt_username));
		EditText pdt = ((EditText) findViewById(R.id.edt_password));

		SharedPreferences sh = getSharedPreferences(this.getPackageName(), 0);
		String ip = sh.getString("ip", "");
		String username = sh.getString("userName", "");
		String password = sh.getString("password", "");

		edt.setText(ip);
		udt.setText(username);
		pdt.setText(password);

		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String server = ((EditText) findViewById(R.id.edt_server_ip))
						.getText().toString();
				String username = ((EditText) findViewById(R.id.edt_username))
						.getText().toString();
				String passwd = ((EditText) findViewById(R.id.edt_password))
						.getText().toString();
				if (null != server && !"".equals(server)) {
					if (null != username && !"".equals(username)) {
						if (null != passwd && !"".equals(passwd)) {
							Editor sh = getSharedPreferences(
									Login.this.getPackageName(), 0).edit();
							sh.putString("ip", server);
							sh.putString("userName", username);
							sh.putString("password", passwd);
							sh.commit();
							new LoginTask().execute(server, username, passwd);
							return;
						}
					}
				}
				Toast.makeText(Login.this, "请正确填写信息", Toast.LENGTH_LONG).show();
			}
		});
		
		btn_reg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//启动一个activity
				Intent intent = new Intent(Login.this,Register.class);
				startActivity(intent);
				finish();
			}
		});
	}

	public boolean isNetworkAvailable() {
		boolean blnReturn = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			// if no network is available networkInfo will be null, otherwise
			// check
			// if we are connected
			if (networkInfo != null && networkInfo.isConnected()
					&& networkInfo.isAvailable()) {
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					blnReturn = true;
				}
			} else {

				blnReturn = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return blnReturn;
	}
	
	
	//启动一个异步任务，以便进行网络访问
	private class LoginTask extends AsyncTask<String, Object, Boolean> {
		private boolean isNetWorkConnected = false;
		private ProgressDialog pDialog;
		private String ip;

		//后台运行,执行中
		@Override
		protected Boolean doInBackground(String... params) {
			// if (isNetWorkConnected) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", params[1]);
			map.put("password", params[2]);

			ip = params[0];
			try {
				String result = HttpUtil.getResult2("http://" + ip
						+ "/Qa.asmx/Login", map);
				//获取登陆结果
				result = XMLParse.formatXml(result);

				if (result.indexOf("true") > 0) {
					return true;
				}
				return false;
			} catch (Exception e) {
			}
			// }
			return false;
		}

		//异步任务执行完成后执行，根据网络返回结果决定是登陆成功还是提示失败
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			try {
				if (result) {
					Intent intent = new Intent(Login.this, MainActivity.class);
					intent.putExtra("ip", ip);
					startActivity(intent);
					finish();
				} else {
//					if (isNetWorkConnected) {
						Toast.makeText(Login.this, "服务器地址、用户名或者密码不正确",
								Toast.LENGTH_LONG).show();
//					}
				}
			} catch (Exception e) {
			} finally {

				if (pDialog != null) {
					pDialog.dismiss();
				}
			}

		}
		
		//异步任务启动前执行,生成进度条
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// isNetWorkConnected = isNetworkAvailable();
			// if (isNetWorkConnected) {
			pDialog = new ProgressDialog(Login.this);
			pDialog.setTitle("请稍等");
			pDialog.setMessage("正在获取数据...");
			pDialog.show();
			// } else {
			//
			// Toast.makeText(Login.this, "请连接网络", Toast.LENGTH_LONG).show();
			// }
			// }
		}
	}
}
