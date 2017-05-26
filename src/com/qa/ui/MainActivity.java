package com.qa.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MainActivity extends Activity {

	private RadioGroup rg = null;
	private RadioButton a = null;
	private RadioButton b = null;
	private RadioButton c = null;
	private RadioButton d = null;
	
	private TextView content = null;
	
	private String ip = "10.0.2.2"; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		rg = (RadioGroup) findViewById(R.id.rdo_group);
		a = (RadioButton) findViewById(R.id.rdb_a);
		b = (RadioButton) findViewById(R.id.rdb_b);
		c = (RadioButton) findViewById(R.id.rdb_c);
		d = (RadioButton) findViewById(R.id.rdb_d);
		
		content = (TextView)findViewById(R.id.txt_content);

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb = (RadioButton)group.findViewById(checkedId);
				String name = rb.getContentDescription().toString();
				new Submit().execute(name);
			}
		});

		Intent intent = getIntent();

		ip = intent.getStringExtra("ip");
		if (null != ip && !"".equals(ip)) {
			//启动抓取问卷数据的异步任务
			new FetchData().execute(ip);
		}
	}
	

	private void disableView() {
		rg.setVisibility(View.GONE);
		content.setText("感谢你的投票");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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

	//绘制问卷内容界面
	private void initView(DataResult result) {
		//绘制选项1
		a.setText(result.getList().get(0).getName() + "."
				+ result.getList().get(0).getContent());
		a.setContentDescription(result.getList().get(0).getName() );
		
		//绘制选项2
		b.setText(result.getList().get(1).getName() + "."
				+ result.getList().get(1).getContent());
		b.setContentDescription(result.getList().get(1).getName() );
		
		//绘制选项3
		c.setText(result.getList().get(2).getName() + "."
				+ result.getList().get(2).getContent());
		c.setContentDescription(result.getList().get(2).getName() );
		
		//绘制选项4
		d.setText(result.getList().get(3).getName() + "."
				+ result.getList().get(3).getContent());
		d.setContentDescription(result.getList().get(3).getName() );
		
		//绘制问题内容
		content.setText(result.getContent());
	}

	
	//用于从服务器取得问卷数据
	private class FetchData extends AsyncTask<String, Object, DataResult> {
		private boolean isNetWorkConnected = false;
		private ProgressDialog pDialog;

		@Override
		protected DataResult doInBackground(String... params) {
//			if (isNetWorkConnected) {
				try {
					//访问服务器getOa接口
					String result = HttpUtil.getResult2("http://" + params[0]
							+ "/Qa.asmx/GetQa", null);
					result = XMLParse.formatXml(result);

					//反序列化成对象，将xml转换成对象
					XStream xStream = new XStream(new DomDriver());
					xStream.alias("string", DataResult.class);
					xStream.aliasField("List", DataResult.class, "list");
					xStream.alias("Item", Item.class);

					DataResult res = (DataResult) xStream.fromXML(result);
					return res;
				} catch (Exception e) {
				}
//			}
			return null;
		}

		//在doinbackgroud后执行，用于根据请求后服务器返回的结果数据生成界面
		@Override
		protected void onPostExecute(DataResult result) {
			super.onPostExecute(result);
//			if (isNetWorkConnected) {
				try {
					if (result != null) {
						//绘制界面
						initView(result);
					}
				} catch (Exception e) {
				} finally {
					pDialog.dismiss();
				}
//			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			isNetWorkConnected = isNetworkAvailable();
//			if (isNetWorkConnected) {
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setTitle("请稍等");
				pDialog.setMessage("正在获取数据...");
				pDialog.show();

//			}
		}
	}

	
	//用于提交选项的异步任务
	private class Submit extends AsyncTask<String, Object, Boolean> {
		private boolean isNetWorkConnected = false;
		private ProgressDialog pDialog;
		@Override
		protected Boolean doInBackground(String... params) {
//			if (isNetWorkConnected) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("itemname", params[0]);
				try {
					//访问Select接口
					String result = HttpUtil.getResult2("http://" + ip
							+ "/Qa.asmx/Select", map);
					result = XMLParse.formatXml(result);
					
					//判断服务器是否执行成功
					if(result.indexOf("true")>0){
						return true;
					}
				} catch (Exception e) {
				}
//			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			try {
				if(result){
					Toast.makeText(MainActivity.this, "投票成功", Toast.LENGTH_LONG).show();
					disableView();
				}else{
					Toast.makeText(MainActivity.this, "投票失败", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
			}finally{
				if(pDialog!=null){
					pDialog.dismiss();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			isNetWorkConnected = isNetworkAvailable();
//			if (isNetWorkConnected) {
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setTitle("请稍等");
				pDialog.setMessage("正在提交数据...");
				pDialog.show();
//			}
		}
	}
}
