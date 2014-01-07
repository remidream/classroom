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

import android.content.Context;
import edu.ccucsie.classroom.LoginActivity;
import edu.ccucsie.classroom.MyApplication;
import edu.ccucsie.classroom.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class UnverifiedFragment extends ListFragment {
	private Boolean[] mItemIsChecked;
	private TextView mTextView;
	private Button mCancelButton;
	private String mResult;
	private ProgressDialog mProgressDialog;

	private List<HashMap<String, Object>> mRecordInfos;
	private MyAdapter mAdapter;

	private static final String URI = "http://www.cs.ccu.edu.tw/~lht100u/classroom/app/unverified.php";
	private static final String CANCEL_URI = "http://www.cs.ccu.edu.tw/~lht100u/classroom/app/cancel_unverified.php";

	protected static final int REFRESH_DATA = 0x00000001;
	protected static final int CANCEL_DATA = 0x00000002;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mResult = new String("");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_unverified, container, false);
		mTextView = (TextView) view.findViewById(R.id.unverifiedTextView);
		mCancelButton = (Button) view.findViewById(R.id.cancelButton);
		mCancelButton.setOnClickListener(cancelClickListener);

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

		Thread thread = new Thread(new SendPostRunnable());
		thread.start();

		return view;
	}

	private Button.OnClickListener cancelClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setTitle("提示");
			builder.setMessage("是否要取消這筆資料？");
			DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub

					Boolean isCheckedFlag = false;
					for (int i = 0; i < mItemIsChecked.length; ++i) {
						if (mItemIsChecked[i]) {
							isCheckedFlag = true;
							break;
						}
					}

					if (isCheckedFlag) {
						mProgressDialog = ProgressDialog.show(getActivity(), "資料傳送中", "請稍後...");

						Thread thread = new Thread(new SendCancelPostRunnable());
						thread.start();
					} else {
						Toast.makeText(getActivity(), "取消預約失敗，\n請至少選擇一項要刪除的記錄。", Toast.LENGTH_LONG).show();
					}
				}
			};
			builder.setPositiveButton("確定", positiveListener);
			DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub

				}
			};
			builder.setNegativeButton("取消", negativeListener);
			builder.show();
		}
	};

	public class MyAdapter extends BaseAdapter {
		public class RecordInfo {
			// public String classroomID;
			public CheckBox cancelCheckBox;
			public TextView date;
			public TextView time;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			Log.d("getCount", "" + mRecordInfos.size());
			return mRecordInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			RecordInfo recordInfo = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_unverified, null);

				recordInfo = new RecordInfo();
				recordInfo.cancelCheckBox = (CheckBox) convertView.findViewById(R.id.cancelCheckBox);
				recordInfo.date = (TextView) convertView.findViewById(R.id.dateTextView);
				recordInfo.time = (TextView) convertView.findViewById(R.id.timeTextView);

				convertView.setTag(recordInfo);
			} else {
				recordInfo = (RecordInfo) convertView.getTag();
			}

			recordInfo.cancelCheckBox.setText(mRecordInfos.get(position).get("classroom").toString());
			recordInfo.date.setText(mRecordInfos.get(position).get("date").toString());
			recordInfo.time.setText(mRecordInfos.get(position).get("time").toString());

			final int index = position;
			recordInfo.cancelCheckBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox checkBox = (CheckBox) v;
					mItemIsChecked[index] = checkBox.isChecked();
				}
			});

			return convertView;
		}
	}

	private void updateListView() {
		if (mResult.length() != 0) {
			mTextView.setText("以下為您未審核通過的詳細資料：");
		} else {
			mTextView.setText("您沒有任何未審核通過的借用記錄。");
			return;
		}

		final String[] key = { "recordID", "classroom", "date", "time" };
		mRecordInfos = new ArrayList<HashMap<String, Object>>();
		for (String line : mResult.split("\n")) {
			HashMap<String, Object> item = new HashMap<String, Object>();

			String[] s = line.split("\t");
			for (int i = 0; i < key.length; ++i) {
				item.put(key[i], s[i]);
			}
			mRecordInfos.add(item);
		}
		mItemIsChecked = new Boolean[mRecordInfos.size()];
		for (int i = 0; i < mItemIsChecked.length; ++i) {
			mItemIsChecked[i] = false;
		}

		mAdapter = new MyAdapter();
		setListAdapter(mAdapter);
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String result = null;
			switch (msg.what) {
			// 顯示網路上抓取的資料
			case REFRESH_DATA:
				if (msg.obj instanceof String)
					result = (String) msg.obj;

				if (result != null) {
					mResult = result;
				}

				updateListView();
				break;

			case CANCEL_DATA:
				if (msg.obj instanceof String)
					result = (String) msg.obj;

				if (result != null) {
					Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
					mProgressDialog = ProgressDialog.show(getActivity(), "資料更新中", "請稍後...");

					Thread thread = new Thread(new SendPostRunnable());
					thread.start();
				}

				break;
			}
		}
	};

	class SendPostRunnable implements Runnable {
		@Override
		public void run() {
			String result = sendPostDataToInternet();
			mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
		}

		private String sendPostDataToInternet() {
			String result;

			/* 建立HTTP Post連線 */
			HttpPost httpRequest = new HttpPost(URI);
			/*
			 * Post運作傳送變數必須用NameValuePair[]陣列儲存
			 */

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("ccupms_acc", ((MyApplication) getActivity().getApplication())
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

	@SuppressLint("HandlerLeak")
	Handler mCancelHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 顯示網路上抓取的資料
			}
		}
	};

	class SendCancelPostRunnable implements Runnable {
		@Override
		public void run() {
			String result = sendPostDataToInternet();
			mHandler.obtainMessage(CANCEL_DATA, result).sendToTarget();
		}

		private String sendPostDataToInternet() {
			String result;

			/* 建立HTTP Post連線 */
			HttpPost httpRequest = new HttpPost(CANCEL_URI);
			/*
			 * Post運作傳送變數必須用NameValuePair[]陣列儲存
			 */

			String dataID = new String("");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (int i = 0; i < mItemIsChecked.length; ++i) {
				if (mItemIsChecked[i]) {
					if (i != 0) {
						dataID += "\t";
					}
					dataID += mRecordInfos.get(i).get("recordID").toString();
				}
			}
			params.add(new BasicNameValuePair("dataID", dataID));
			params.add(new BasicNameValuePair("ccupms_acc", ((MyApplication) getActivity().getApplication())
					.getAccount()));

			try {
				/* 發出HTTP request */
				httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				/* 取得HTTP response */
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
				/* 若狀態碼為200 ok */
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