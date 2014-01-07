package edu.ccucsie.classroom.search;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import edu.ccucsie.classroom.LoginActivity;
import edu.ccucsie.classroom.MyApplication;
import edu.ccucsie.classroom.R;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SubmitFormActivity extends BaseActivity {
	private String mClassroomID;
	private String mDate;
	private String mTime;
	private String mUserName;
	private String mPhoneNumber;
	private String mEmail;
	public static String mReason;

	private static final String[] mClassroomName = { "EA 001", "EA 101", "EA 103", "EA 104", "EA 105", "EA 204",
			"EA 205", "EA 206", "EA 307", "EA 003C", "EA 509" };
	private static HashMap<String, String> mClassroomNameToID;

	private Button mRewriteButton;
	private Button mDoneButton;
	private Button mReasonButton;

	private TextView mClassroomIDTextView;
	private TextView mPickDateTextView;
	private TextView mPickTimeTextView;

	private EditText mPhoneEditText;
	private EditText mEmailEditText;

	private ProgressDialog mProgressDialog;

	public static EditText mNameEditText;
	public static TextView mReasonTextView;

	private final String uriAPI = "http://www.cs.ccu.edu.tw/~lht100u/classroom/app/send.php";
	private final int REFRESH_DATA = 1;

	/*
	 * private static final String[] mReason = new String[] { " meeting",
	 * " 上課 ", " 演講 ", " 研究生口試 ", " 開會", " 其他" };
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_form);

		mClassroomNameToID = new HashMap<String, String>();
		for (int i = 0; i < mClassroomName.length; ++i) {
			mClassroomNameToID.put(mClassroomName[i], String.valueOf(i + 1));
		}

		mRewriteButton = (Button) findViewById(R.id.rewrite);
		mRewriteButton.setOnClickListener(ButtonOnClickListener);
		mDoneButton = (Button) findViewById(R.id.done);
		mDoneButton.setOnClickListener(ButtonOnClickListener);
		mReasonButton = (Button) findViewById(R.id.reason);
		mReasonButton.setOnClickListener(ButtonOnClickListener);

		mReasonTextView = (TextView) findViewById(R.id.reasonTextView);
		mClassroomIDTextView = (TextView) findViewById(R.id.ClassroomID);
		mPickDateTextView = (TextView) findViewById(R.id.SelectDate);
		mPickTimeTextView = (TextView) findViewById(R.id.SelectTime);

		mNameEditText = (EditText) findViewById(R.id.EditName);
		mPhoneEditText = (EditText) findViewById(R.id.EditPhone);
		mEmailEditText = (EditText) findViewById(R.id.EditEmail);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		String classroom = bundle.getString("Classroom");
		mClassroomID = mClassroomNameToID.get(classroom);
		mDate = bundle.getString("Date");
		mTime = bundle.getString("Time");

		mClassroomIDTextView.append(classroom);
		mPickDateTextView.append(mDate);
		mPickTimeTextView.append(mTime);
	}

	private void initialize() {
		mNameEditText.setText("");
		mPhoneEditText.setText("");
		mEmailEditText.setText("");
		mReasonTextView.setText("");
		mReason = null;
	}

	@SuppressLint("NewApi")
	private Button.OnClickListener ButtonOnClickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.rewrite:
				initialize();
				break;

			case R.id.reason:
				intent.setClass(SubmitFormActivity.this, ReasonActivity.class);
				SubmitFormActivity.this.startActivity(intent);
				break;

			case R.id.done:
				SubmitDialogFragment dialogFragment = SubmitDialogFragment.newInstance("確認送出？");
				dialogFragment.show(getFragmentManager(), "dialog");
				break;
			}
		}
	};

	public void doPositiveClick() {
		// ---perform steps when user clicks on OK---
		// Log.d("UserDate", "User clicks on OK");
		// Toast.makeText(SubmitFormActivity.this, "OK",
		// Toast.LENGTH_SHORT).show();

		mUserName = mNameEditText.getText().toString();
		mPhoneNumber = mPhoneEditText.getText().toString();
		mEmail = mEmailEditText.getText().toString();

		if (mUserName.equals("") || mPhoneNumber.equals("") || mEmail.equals("") || mReason == null
				|| mReason.equals("")) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("送出失敗。");
			builder.setMessage("請確實填寫所有欄位，不可空白。");
			builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
				}
			});
			builder.show();
			return;
		}

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
					intent.setClass(SubmitFormActivity.this, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			});
			builder.show();

			return;
		}

		mProgressDialog = ProgressDialog.show(this, "請稍後", "表單傳送中...");

		Thread thread = new Thread(new SendPostRunnable(mClassroomID + "\t" + mDate + "\t" + mTime.replace('\n', '-')
				+ "\t" + mUserName + "\t" + mPhoneNumber + "\t" + mEmail + "\t" + mReason));
		thread.start();
	}

	public void doNegativeClick() {
		// ---perform steps when user clicks on Cancel---
		// Log.d("UserDate", "User clicks on Cancel");
	}

	// 接收網路回傳字串
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

				Toast.makeText(SubmitFormActivity.this, result, Toast.LENGTH_LONG).show();
/*
				if (result.equals("success\n")) {
					Toast.makeText(SubmitFormActivity.this, "借用成功。", Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.setClass(SubmitFormActivity.this, HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else {
					Toast.makeText(SubmitFormActivity.this, "借用失敗。", Toast.LENGTH_LONG).show();
				}
*/
				break;
			}
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
		params.add(new BasicNameValuePair("result", request));
		params.add(new BasicNameValuePair("ccupms_acc", ((MyApplication) getApplication()).getAccount()));

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
			Toast.makeText(SubmitFormActivity.this, "網路連線出現異常，表單送出失敗。", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} finally {
			mProgressDialog.dismiss();
		}
		return null;
	}
}
