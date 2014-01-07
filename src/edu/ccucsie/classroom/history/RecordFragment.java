package edu.ccucsie.classroom.history;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import edu.ccucsie.classroom.LoginActivity;
import edu.ccucsie.classroom.MyApplication;
import edu.ccucsie.classroom.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordFragment extends ListFragment {
	private String mResult;
	private TextView mTextView;
	private ProgressDialog mProgressDialog;

	private List<HashMap<String, Object>> mRecordInfos;

	private final String uriAPI = "http://www.cs.ccu.edu.tw/~lht100u/classroom/app/history.php";
	private final int REFRESH_DATA = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mResult = new String("");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_record, container, false);
		mTextView = (TextView) view.findViewById(R.id.recordTextView);

		// 在連上網路前先確認使用者是否連上網路。
		ConnectivityManager manager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() == null) {
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setTitle("連線錯誤");
			builder.setMessage("請確認是否已連上網路。");
			builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					getActivity().finish();
				}
			});
			builder.show();
		}

		mProgressDialog = ProgressDialog.show(getActivity(), "資料讀取中", "請稍後...");

		Thread thread = new Thread(new SendPostRunnable(""));
		thread.start();

		return view;
	}

	public class MyAdapter extends BaseAdapter {
		public class RecordInfo {
			public TextView classroomName;
			public TextView date;
			public TextView time;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mRecordInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			RecordInfo recordInfo = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_record, null);
				recordInfo = new RecordInfo();
				recordInfo.classroomName = (TextView) convertView.findViewById(R.id.classroomTextView);
				recordInfo.date = (TextView) convertView.findViewById(R.id.dateTextView);
				recordInfo.time = (TextView) convertView.findViewById(R.id.timeTextView);

				convertView.setTag(recordInfo);
			} else {
				recordInfo = (RecordInfo) convertView.getTag();
			}

			recordInfo.classroomName.setText(mRecordInfos.get(position).get("classroom").toString());
			recordInfo.date.setText(mRecordInfos.get(position).get("date").toString());
			recordInfo.time.setText(mRecordInfos.get(position).get("time").toString());

			return convertView;
		}
	}

	private void updateListView() {
		if (mResult.length() != 0) {
			mTextView.setText("以下為您成功借閱過的借用記錄：");
		} else {
			mTextView.setText("您沒有任何借用記錄。");
			return;
		}

		final String[] key = { "classroom", "date", "time" };

		mRecordInfos = new ArrayList<HashMap<String, Object>>();
		for (String line : mResult.split("\n")) {
			HashMap<String, Object> item = new HashMap<String, Object>();

			String[] s = line.split("\t");
			for (int i = 0; i < key.length; ++i) {
				item.put(key[i], s[i]);
			}
			mRecordInfos.add(item);
		}

		setListAdapter(new MyAdapter());
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
			mProgressDialog.dismiss();
			updateListView();
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

		private String sendPostDataToInternet(String request) {
			String result;

			/* 建立HTTP Post連線 */
			HttpPost httpRequest = new HttpPost(uriAPI);
			/*
			 * Post運作傳送變數必須用NameValuePair[]陣列儲存
			 */

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("ccupms_acc", ((MyApplication) (getActivity().getApplication()))
					.getAccount()));

			try {
				/* 發出HTTP request */
				httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				/* 取得HTTP response */
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
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
			} finally {
				mProgressDialog.dismiss();
			}
			return null;
		}
	}
}
