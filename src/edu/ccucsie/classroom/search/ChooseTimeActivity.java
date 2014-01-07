package edu.ccucsie.classroom.search;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import edu.ccucsie.classroom.LoginActivity;
import edu.ccucsie.classroom.R;
import edu.ccucsie.classroom.base.BaseActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ChooseTimeActivity extends BaseActivity {
	private Button[] mTimeButtons = new Button[32];
	private Button mCancelButton;
	private Button mDoneButton;

	private int mYear, mMonth, mDay;
	private int mPickTimeNum;

	private Boolean[] mTimeIsAvailable = new Boolean[32];
	private Boolean mTimeIsPicked, mDateIsPicked;

	private String mRoom, mDate, mTime;

	private TextView mDateTextView;
	private TextView mTimeTextView;
	private TextView mTipsTextView;
	
//	private ProgressDialog mProgressDialog;

	private List<String> mTimePickedList = new ArrayList<String>();
	private List<String> mTimeNameList = new ArrayList<String>();
	private List<String> mTimeList = new ArrayList<String>();

	private static HashMap<String, Integer> mTimeToIndex = new HashMap<String, Integer>();

	private static final int ID_DATEPICKER = 0;
	private static final int COLOR_GREEN = 0xFF00FF00;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_YELLOW = 0xFFFFFF00;
	private static final int STATE_AVAILABLE = 0;
	private static final int STATE_UNVERIFIED = 1;
	private static final int STATE_VERIFIED = 2;
	private static final int STATE_UNAVAILABLE = 3;

	private String mResult = new String("");
	private final String uriAPI = "http://www.cs.ccu.edu.tw/~lht100u/classroom/app/info.php";
	private final int REFRESH_DATA = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_time);

		Button datePickerButton = (Button) findViewById(R.id.datepickerbutton);
		datePickerButton.setOnClickListener(datePickerListener);

		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(mListener);

		mDoneButton = (Button) findViewById(R.id.done);
		mDoneButton.setOnClickListener(mListener);

		mDateTextView = (TextView) findViewById(R.id.tipsDateTextView);
		mTimeTextView = (TextView) findViewById(R.id.TimeView);
		mTipsTextView = (TextView) findViewById(R.id.tips);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mRoom = bundle.getString("Classroom");

		setTimeList();
		setTimeButtons();
		initialize();
	}

	private void initialize() {
		mTimeIsPicked = mDateIsPicked = false;
		mTimePickedList.clear();
		mTimeTextView.setText(R.string.color_tip);
		mDateTextView.setText(R.string.date_tip);
		mTipsTextView.setText(R.string.flow_tip);

		for (int i = 0; i < 32; ++i) {
			mTimeButtons[i].getBackground().setColorFilter(COLOR_GREEN, android.graphics.PorterDuff.Mode.MULTIPLY); // 綠色
			mTimeIsAvailable[i] = true;
		}
	}

	private void setTimeList() {
		for (int i = 0; i < 33; i++) {
			if (i % 2 == 0) {
				mTimeNameList.add(String.valueOf(7 + i / 2) + " : 00~" + String.valueOf(7 + i / 2) + " : 30");
				mTimeList.add(String.valueOf(7 + i / 2) + ":00");

				mTimeToIndex.put(String.format("%02d", 7 + i / 2) + ":00", i);
			} else {
				mTimeNameList.add(String.valueOf(7 + i / 2) + " : 30~" + String.valueOf(8 + i / 2) + " : 00");
				mTimeList.add(String.valueOf(7 + i / 2) + ":30");

				mTimeToIndex.put(String.format("%02d", 7 + i / 2) + ":30", i);
			}
		}
	}

	private void setTimeButtons() {
		mTimeButtons[0] = (Button) findViewById(R.id.timebut1);
		mTimeButtons[1] = (Button) findViewById(R.id.timebut2);
		mTimeButtons[2] = (Button) findViewById(R.id.timebut3);
		mTimeButtons[3] = (Button) findViewById(R.id.timebut4);
		mTimeButtons[4] = (Button) findViewById(R.id.timebut5);
		mTimeButtons[5] = (Button) findViewById(R.id.timebut6);
		mTimeButtons[6] = (Button) findViewById(R.id.timebut7);
		mTimeButtons[7] = (Button) findViewById(R.id.timebut8);
		mTimeButtons[8] = (Button) findViewById(R.id.timebut9);
		mTimeButtons[9] = (Button) findViewById(R.id.timebut10);
		mTimeButtons[10] = (Button) findViewById(R.id.timebut11);
		mTimeButtons[11] = (Button) findViewById(R.id.timebut12);
		mTimeButtons[12] = (Button) findViewById(R.id.timebut13);
		mTimeButtons[13] = (Button) findViewById(R.id.timebut14);
		mTimeButtons[14] = (Button) findViewById(R.id.timebut15);
		mTimeButtons[15] = (Button) findViewById(R.id.timebut16);
		mTimeButtons[16] = (Button) findViewById(R.id.timebut17);
		mTimeButtons[17] = (Button) findViewById(R.id.timebut18);
		mTimeButtons[18] = (Button) findViewById(R.id.timebut19);
		mTimeButtons[19] = (Button) findViewById(R.id.timebut20);
		mTimeButtons[20] = (Button) findViewById(R.id.timebut21);
		mTimeButtons[21] = (Button) findViewById(R.id.timebut22);
		mTimeButtons[22] = (Button) findViewById(R.id.timebut23);
		mTimeButtons[23] = (Button) findViewById(R.id.timebut24);
		mTimeButtons[24] = (Button) findViewById(R.id.timebut25);
		mTimeButtons[25] = (Button) findViewById(R.id.timebut26);
		mTimeButtons[26] = (Button) findViewById(R.id.timebut27);
		mTimeButtons[27] = (Button) findViewById(R.id.timebut28);
		mTimeButtons[28] = (Button) findViewById(R.id.timebut29);
		mTimeButtons[29] = (Button) findViewById(R.id.timebut30);
		mTimeButtons[30] = (Button) findViewById(R.id.timebut31);
		mTimeButtons[31] = (Button) findViewById(R.id.timebut32);

		for (int i = 0; i < 32; i++) {
			mTimeButtons[i].setText(mTimeNameList.get(i));
			mTimeButtons[i].setOnClickListener(timeButtonsOnClickListener);
		}
	}

	private void setTimeButtonsColor() {
		String[] timeSection = mResult.split("\n");
		if (timeSection.length == 0) {
			return;
		}

		int state = STATE_AVAILABLE;
		for (String s : timeSection[0].split("\t")) {
			if (s.equals("w")) {
				state = STATE_UNAVAILABLE;
				mDateTextView.append("\n（該日期不在學期範圍內，無法借用。）");
			} else if (s.equals("u")) {
				state = STATE_UNVERIFIED;
			} else if (s.equals("v")) {
				state = STATE_VERIFIED;
			} else if (Pattern.matches("[0-2][0-9]:[03]0", s)) {
				int index = mTimeToIndex.get(s);

				if (state == STATE_UNVERIFIED) {
					mTimeButtons[index].getBackground().setColorFilter(COLOR_YELLOW,
							android.graphics.PorterDuff.Mode.MULTIPLY); // 黃色
				} else if (state == STATE_VERIFIED) {
					mTimeButtons[index].getBackground().setColorFilter(COLOR_RED,
							android.graphics.PorterDuff.Mode.MULTIPLY); // 紅色
					mTimeIsAvailable[index] = false;
				}
			}
		}
	}

	private Button.OnClickListener datePickerListener = new Button.OnClickListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.datepickerbutton:
				initialize();

				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);
				showDialog(ID_DATEPICKER);
				break;
			}
		}
	};

	private Button.OnClickListener mListener = new Button.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cancel:
				initialize();
				break;

			case R.id.done:
				if (mPickTimeNum > 5) {
					Toast.makeText(getApplicationContext(), "請不要選擇太多不連續時段", Toast.LENGTH_SHORT).show();
				} else {
					if (mTimeIsPicked && mDateIsPicked) {
						// 傳遞資料給下個頁面
						Intent intent = new Intent(ChooseTimeActivity.this, SubmitFormActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("Classroom", mRoom);
						bundle.putString("Date", mDate);
						bundle.putString("Time", mTime);
						intent.putExtras(bundle);

						intent.setClass(ChooseTimeActivity.this, SubmitFormActivity.class);
						ChooseTimeActivity.this.startActivity(intent);
					} else
						mTipsTextView.setText(R.string.flow_tip);
				}
				break;

			default:
				break;
			}
		}
	};

	private Button.OnClickListener timeButtonsOnClickListener = new Button.OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.timebut1:
				pickTime(0);
				break;
			case R.id.timebut2:
				pickTime(1);
				break;
			case R.id.timebut3:
				pickTime(2);
				break;
			case R.id.timebut4:
				pickTime(3);
				break;
			case R.id.timebut5:
				pickTime(4);
				break;
			case R.id.timebut6:
				pickTime(5);
				break;
			case R.id.timebut7:
				pickTime(6);
				break;
			case R.id.timebut8:
				pickTime(7);
				break;
			case R.id.timebut9:
				pickTime(8);
				break;
			case R.id.timebut10:
				pickTime(9);
				break;
			case R.id.timebut11:
				pickTime(10);
				break;
			case R.id.timebut12:
				pickTime(11);
				break;
			case R.id.timebut13:
				pickTime(12);
				break;
			case R.id.timebut14:
				pickTime(13);
				break;
			case R.id.timebut15:
				pickTime(14);
				break;
			case R.id.timebut16:
				pickTime(15);
				break;
			case R.id.timebut17:
				pickTime(16);
				break;
			case R.id.timebut18:
				pickTime(17);
				break;
			case R.id.timebut19:
				pickTime(18);
				break;
			case R.id.timebut20:
				pickTime(19);
				break;
			case R.id.timebut21:
				pickTime(20);
				break;
			case R.id.timebut22:
				pickTime(21);
				break;
			case R.id.timebut23:
				pickTime(22);
				break;
			case R.id.timebut24:
				pickTime(23);
				break;
			case R.id.timebut25:
				pickTime(24);
				break;
			case R.id.timebut26:
				pickTime(25);
				break;
			case R.id.timebut27:
				pickTime(26);
				break;
			case R.id.timebut28:
				pickTime(27);
				break;
			case R.id.timebut29:
				pickTime(28);
				break;
			case R.id.timebut30:
				pickTime(29);
				break;
			case R.id.timebut31:
				pickTime(30);
				break;
			case R.id.timebut32:
				pickTime(31);
			default:
				break;
			}
		}
	};

	private void pickTime(int timeIndex) {
		// 按一次加入選擇的時間，再按一次拿掉
		if (mDateIsPicked) {
			if (!mTimeIsAvailable[timeIndex]) {
				mTipsTextView.setText("無法選擇已審核的時段。");
				return;
			}

			Boolean isExist = false;
			for (int i = 0; i < mTimePickedList.size(); i++) {
				if (mTimeNameList.get(timeIndex) == mTimePickedList.get(i)) {
					mTimePickedList.remove(i);
					isExist = true;
				}
			}
			if (!isExist) {
				mTimePickedList.add(mTimeNameList.get(timeIndex));
			}

			int[] time = new int[50];
			Arrays.fill(time, -1);

			for (int i = 0, k = 0; i < mTimePickedList.size(); i++) {
				for (int j = 0; j < mTimeNameList.size(); j++) {
					if (mTimePickedList.get(i) == mTimeNameList.get(j)) {
						time[k++] = j;
					}
				}
			}

			Arrays.sort(time);

			// 經連續的時段連在一起輸出
			int[] start = new int[20];
			int[] finish = new int[20];
			int pickTimeIndex = 0;
			Boolean isContinuous = false;
			for (int i = 0; i < 50; i++) {
				if (time[i] != -1) {
					if (!isContinuous) {
						if (pickTimeIndex == 0) {
							start[pickTimeIndex] = time[i];
							finish[pickTimeIndex] = time[i] + 1;
						}
						isContinuous = true;
					}

					if (time[i] - time[i - 1] == 1 && time[i - 1] != -1) {
						finish[pickTimeIndex] = time[i] + 1;
					}

					if (time[i] - time[i - 1] != 1 && time[i - 1] != -1) {
						isContinuous = false;
						pickTimeIndex++;
						start[pickTimeIndex] = time[i];
						finish[pickTimeIndex] = time[i] + 1;
					}
				}
			}

			mPickTimeNum = pickTimeIndex + 1;

			StringBuffer stringBuffer = new StringBuffer();
			Boolean isClear = false;
			for (int i = 0; i < mPickTimeNum; i++) {
				if (start[i] != 0 || finish[i] != 0) {
					stringBuffer.append(String.valueOf(mTimeList.get(start[i])) + "~"
							+ String.valueOf(mTimeList.get(finish[i])) + "\n");
				} else
					isClear = true;
			}
			mTime = stringBuffer.toString();

			if (isClear)
				mTimeTextView.setText("請選擇要預借的時段，綠色為無人預借、黃色為已有人預借在審核中、紅色為已有人預借成功。");
			else
				mTimeTextView.setText("您所選取的時段有：\n" + mTime);

			mTimeIsPicked = true;
		} else {
			mTipsTextView.setText("請先選擇日期再選時段");
			Toast.makeText(getApplicationContext(), "請先選擇日期再選時段", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case ID_DATEPICKER:
			Calendar c = Calendar.getInstance();
			DatePickerDialog datePickerDialog;
			datePickerDialog = new DatePickerDialog(ChooseTimeActivity.this, myDateSetListener, mYear, mMonth, mDay);
			datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis()); // 設定能選取的時間的最小值

			return datePickerDialog;
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			mDate = String.valueOf(year) + "-" + String.format("%02d", monthOfYear + 1) + "-"
					+ String.format("%02d", dayOfMonth);

			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			if (manager.getActiveNetworkInfo() == null) {
				AlertDialog.Builder builder = new Builder(ChooseTimeActivity.this);
				builder.setTitle("連線錯誤");
				builder.setMessage("請確認是否已連上網路。");
				builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(ChooseTimeActivity.this, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
					}
				});
				builder.show();

				return;
			}
			
			Thread thread = new Thread(new SendPostRunnable(mRoom + "\t" + mDate + "\n"));
			thread.start();
		}
	};

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

				if (result != null) {
					mResult = result;
				}

				// 網路正常連接才設定已選擇日期
				mDateIsPicked = true;
				mDateTextView.setText(mDate);
				setTimeButtonsColor();
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
		params.add(new BasicNameValuePair("classroom_and_date", request));

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
				
				Log.e("ChooseTimeActivity.java", result);

				// 回傳回應字串
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}