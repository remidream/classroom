package edu.ccucsie.classroom.search;

import edu.ccucsie.classroom.R;
import edu.ccucsie.classroom.base.BaseActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ReasonActivity extends BaseActivity {
	private AlertDialog.Builder mBuilder;

	private View mMeetingView;
	private View mLessonView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reason);

		final String[] reason = { "meeting", "上課 ", "演講 ", "研究生口試 ", "開會", "其他" };
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reason);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LayoutInflater inflater = LayoutInflater.from(ReasonActivity.this);
				mMeetingView = inflater.inflate(R.layout.meeting, null);
				mLessonView = inflater.inflate(R.layout.lesson, null);

				setAlertDialogBuilder(position);
				mBuilder.show();
				Toast.makeText(getApplicationContext(), "你選擇的是：" + reason[position], Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setAlertDialogBuilder(int index) {
		switch (index) {
		case 0:
			mBuilder = new AlertDialog.Builder(ReasonActivity.this).setTitle("請輸入老師姓名")
					.setView(mMeetingView).setPositiveButton("確定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EditText editText = (EditText) (mMeetingView
									.findViewById(R.id.teacherNameEditTextInMeeting));

							SubmitFormActivity.mReasonTextView.setText("事由：meeting\n" + "老師姓名："
									+ editText.getText().toString());
							SubmitFormActivity.mReason = "1\t" + editText.getText().toString();

							ReasonActivity.this.finish();
						}
					});

			break;
		case 1:
			mBuilder = new AlertDialog.Builder(ReasonActivity.this).setTitle("請輸入老師姓名和課程名稱")
					.setView(mLessonView).setPositiveButton("確定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EditText editText = (EditText) (mLessonView.findViewById(R.id.teacherNameEditText));
							EditText editText2 = (EditText) (mLessonView.findViewById(R.id.lessonNameEditText));

							SubmitFormActivity.mReasonTextView.setText("事由：上課\n老師姓名：" + editText.getText().toString()
									+ "\n課程名稱：" + editText2.getText().toString());
							SubmitFormActivity.mReason = "2\t" + editText.getText().toString() + "\t"
									+ editText2.getText().toString();

							ReasonActivity.this.finish();
						}
					});

			break;
		case 2:
			mBuilder = new AlertDialog.Builder(ReasonActivity.this).setTitle("請輸入演講者姓名")
					.setView(mMeetingView).setPositiveButton("確定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EditText editText = (EditText) (mMeetingView
									.findViewById(R.id.teacherNameEditTextInMeeting));

							SubmitFormActivity.mReasonTextView.setText("事由：演講\n" + "演講者姓名："
									+ editText.getText().toString());
							SubmitFormActivity.mReason = "3\t" + editText.getText().toString();

							ReasonActivity.this.finish();
						}
					});

			break;
		case 3:
			mBuilder = new AlertDialog.Builder(ReasonActivity.this).setTitle("請輸入研究生姓名")
					.setView(mMeetingView).setPositiveButton("確定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EditText editText = (EditText) (mMeetingView
									.findViewById(R.id.teacherNameEditTextInMeeting));
							SubmitFormActivity.mReasonTextView.setText("事由：研究生口試\n" + "研究生姓名："
									+ editText.getText().toString());
							SubmitFormActivity.mReason = "4\t" + editText.getText().toString();

							ReasonActivity.this.finish();
						}
					});

			break;
		case 4:
			mBuilder = new AlertDialog.Builder(ReasonActivity.this).setTitle("請輸入會議名稱")
					.setView(mMeetingView).setPositiveButton("確定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EditText editText = (EditText) (mMeetingView
									.findViewById(R.id.teacherNameEditTextInMeeting));

							SubmitFormActivity.mReasonTextView.setText("事由：開會\n" + "會議名稱："
									+ editText.getText().toString());
							SubmitFormActivity.mReason = "5\t" + editText.getText().toString();

							ReasonActivity.this.finish();
						}
					});

			break;
		case 5:
			mBuilder = new AlertDialog.Builder(ReasonActivity.this).setTitle("請輸入事由細節")
					.setView(mMeetingView).setPositiveButton("確定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EditText editText = (EditText) (mMeetingView
									.findViewById(R.id.teacherNameEditTextInMeeting));

							SubmitFormActivity.mReasonTextView.setText("事由：其他\n" + "事由細節："
									+ editText.getText().toString());
							SubmitFormActivity.mReason = "6\t" + editText.getText().toString();

							ReasonActivity.this.finish();
						}
					});
			break;

		default:
			break;
		}

	}
}
