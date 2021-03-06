package edu.ccucsie.classroom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	SharedPreferences mPreferences;
	private EditText mAccountEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	private CheckBox mCheckBox;

	ProgressDialog mProgressDialog;

	// DEBUG 專用，如果設為 true，就算無法登入還是可以測試後面的功能。
	private static final boolean DEBUG_WITHOUT_LOGIN = false;
	private static final String uriAPI = "http://www.cs.ccu.edu.tw/~lht100u/classroom/app/login_check.php";

	protected static final int REFRESH_DATA = 0x00000001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mPreferences = getPreferences(MODE_PRIVATE);

		mAccountEditText = (EditText) findViewById(R.id.accountEditView);
		mAccountEditText.setText(mPreferences.getString("ACCOUNT", ""));

		mPasswordEditText = (EditText) findViewById(R.id.passwordEditView);
		mPasswordEditText.setText(mPreferences.getString("PASSWORD", ""));

		mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setOnClickListener(this);

		mCheckBox = (CheckBox) findViewById(R.id.rememberCheckBox);
		mCheckBox.setChecked(mPreferences.getBoolean("REMEMBER", false));
	}

	public void onClick(View v) {
		if (v == mLoginButton) {
			final String account = mAccountEditText.getEditableText().toString();
			final String password = mPasswordEditText.getEditableText().toString();

			ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			if (manager.getActiveNetworkInfo() == null) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("連線錯誤");
				builder.setMessage("請確認是否已連上網路。");
				builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
					}
				});
				builder.show();

				return;
			}

			mProgressDialog = ProgressDialog.show(LoginActivity.this, "登入中", "請稍後...");

			// 啟動一個執行緒，將要傳送的資料放進 Runnable 中，讓Thread執行
			Thread thread = new Thread(new SendPostRunnable(account, password));
			thread.start();

			SharedPreferences.Editor editor = mPreferences.edit();
			if (mCheckBox.isChecked()) {
				editor.putString("ACCOUNT", mAccountEditText.getText().toString());
				editor.putString("PASSWORD", mPasswordEditText.getText().toString());
				editor.putBoolean("REMEMBER", true);
			} else {
				editor.clear();
			}
			editor.commit();
		}
	}

	class SendPostRunnable implements Runnable {
		String mAccountText = null;
		String mPasswordText = null;

		public SendPostRunnable(String accountText, String passwordText) {
			mAccountText = accountText;
			mPasswordText = passwordText;
		}

		@Override
		public void run() {
			String result = sendPostDataToInternet();
			mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
		}

		private String sendPostDataToInternet() {
			HttpPost httpRequest = new HttpPost(uriAPI);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Ccu_CR_acc", mAccountText));
			params.add(new BasicNameValuePair("Ccu_CR_PWD", mPasswordText));

			try {
				httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

				// 如果 HTTP 狀態碼為 200 表示成功連上。
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String strResult = EntityUtils.toString(httpResponse.getEntity());
					return strResult;
				}
			} catch (ClientProtocolException e) {
				Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (Exception e) {
				Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} finally {
				mProgressDialog.dismiss();
			}

			// 連線失敗則執行至此，回傳 null 字串。
			return null;
		}
	}

	// 使用 Handler 更新 UI
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA:
				String result = null;
				if (msg.obj instanceof String)
					result = (String) msg.obj;

				if (result != null) {

					Log.d("LoginActivity", result);
					if (result.equals("success")) {
						Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_LONG).show();

						((MyApplication) getApplication()).setAccount(mAccountEditText.getText().toString());

						// 登入成功以後，進入 HOME 頁面。
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, HomeActivity.class);
						LoginActivity.this.startActivity(intent);

					} else if (DEBUG_WITHOUT_LOGIN) {
						Toast.makeText(LoginActivity.this, "Login failed! Debug mode!", Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, HomeActivity.class);
						LoginActivity.this.startActivity(intent);

					} else if (result.equals("failure")) {
						AlertDialog.Builder builder = new Builder(LoginActivity.this);
						builder.setTitle("登入失敗");
						builder.setMessage("請確認帳號密碼是否正確。");
						builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
							}
						});
						builder.show();
					}
				}
				break;
			}
		}
	};
}