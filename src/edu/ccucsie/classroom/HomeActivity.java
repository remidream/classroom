package edu.ccucsie.classroom;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import edu.ccucsie.classroom.base.BaseActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {
	private String mResult;
	private TextView mTextView;
	private ProgressDialog mProgressDialog;

	private final String uriAPI = "http://www.cs.ccu.edu.tw/~lht100u/classroom/app/using.php";
	private final int REFRESH_DATA = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mTextView = (TextView) findViewById(R.id.homeTextView);
		mResult = new String("");

		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() == null) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("連線錯誤");
			builder.setMessage("請確認是否已連上網路。");
			builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			});
			builder.show();

			return;
		}
		
		mProgressDialog = ProgressDialog.show(this, "資料讀取中", "請稍後...");

		Thread thread = new Thread(new SendPostRunnable(""));
		thread.start();
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 顯示網路上抓取的資料
			case REFRESH_DATA:

				String result = null;
				if (msg.obj instanceof String)
					result = (String) msg.obj;

				if (result != null) {
					mResult += result;
				}
				break;
			}

			if (mResult.equals("")) {
				mResult += "（無）";
			}
			Log.d("HomeActivity mResult", "/" + mResult + "/");
			mTextView.setText(mResult);
			mProgressDialog.dismiss();
		}
	};

	class SendPostRunnable implements Runnable {
		String prompt;

		// 建構子，設定要傳的字串
		public SendPostRunnable(String prompt) {
			this.prompt = prompt;
		}

		@Override
		public void run() {
			String result = sendPostDataToInternet(prompt);
			mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
		}
	}

	private String sendPostDataToInternet(String request) {
		String result = null;

		/* 建立HTTP Post連線 */
		HttpPost httpRequest = new HttpPost(uriAPI);
		/*
		 * Post運作傳送變數必須用NameValuePair[]陣列儲存
		 */

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("", request));

		try {
			/* 發出HTTP request */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 取得HTTP response */
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			/* 若狀態碼為200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出回應字串 */
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream inputStream = httpEntity.getContent();

				BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
				StringBuilder builder = new StringBuilder();
				String line;
				while ((line = bufReader.readLine()) != null) {
					builder.append(line + "\n");
				}
				inputStream.close();
				result = builder.toString();
				// 回傳回應字串
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}