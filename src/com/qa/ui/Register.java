package com.qa.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {
	private EditText userName = null;
	private EditText pwd = null;
	private EditText rePwd = null;
	private Button btn_reg = null;

	private String ip = "10.0.2.2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg);

		userName = (EditText) findViewById(R.id.username);
		pwd = (EditText) findViewById(R.id.pwd);
		rePwd = (EditText) findViewById(R.id.repwd);

		btn_reg = (Button) findViewById(R.id.btn_reg);

		//点击注册按钮时进行的数据检查
		btn_reg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = userName.getText().toString();
				String spwd = pwd.getText().toString();
				String srePwd = rePwd.getText().toString();
				
				//进行数据检查
				if (null != username && !"".equals(username)) {
					if (null != spwd && !"".equals(spwd)) {
						if (null != srePwd && !"".equals(srePwd)) {
							if(!"".equals(username.trim())&&!"".equals(spwd.trim())&&!"".equals(srePwd.trim())){
								if(spwd.equals(srePwd)){
									//将注册数据保存到缓存中
									Editor sh = getSharedPreferences(
											Register.this.getPackageName(), 0).edit();
									sh.putString("ip", ip);
									sh.putString("userName", username);
									sh.putString("password", spwd);
									sh.commit();
									//启动用于注册的异步任务
									new Reg().execute(username, spwd);
									return;
								}else{
									Toast.makeText(Register.this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
								}
							}
						}
					}
				}
				Toast.makeText(Register.this, "请正确填写信息", Toast.LENGTH_LONG).show();
			}
		});

	}
	
	
	//用于注册的异步任务
	private class Reg extends AsyncTask<String, Object, Boolean> {
		private ProgressDialog pDialog;

		@Override
		protected Boolean doInBackground(String... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", params[0]);
			map.put("password", params[1]);

			try {
				String result = HttpUtil.getResult2("http://" + ip
						+ "/Qa.asmx/Reg", map);
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

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			try {
				if (result) {
					Toast.makeText(Register.this, "注册成功",
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Register.this, Login.class);
					startActivity(intent);
					finish();
				} else {
					// if (isNetWorkConnected) {
					Toast.makeText(Register.this, "注册的用户名已经存在",
							Toast.LENGTH_LONG).show();
					// }
				}
			} catch (Exception e) {
			} finally {

				if (pDialog != null) {
					pDialog.dismiss();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Register.this);
			pDialog.setTitle("请稍等");
			pDialog.setMessage("正在获取数据...");
			pDialog.show();
		}
	}
}
