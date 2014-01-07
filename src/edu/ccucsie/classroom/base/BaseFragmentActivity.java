package edu.ccucsie.classroom.base;

import edu.ccucsie.classroom.LoginActivity;
import edu.ccucsie.classroom.R;
import edu.ccucsie.classroom.history.HistoryFragmentActivity;
import edu.ccucsie.classroom.search.SearchActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 所有 FragmentActivity 繼承 BaseActivity，使 ActionBar 可以在所有 FragmentActivity 出現。
 * 在修改 onOptionsItemSelected() 的同時必須也要修改 BaseActivity.java， 兩者才能實現相同的 action bar
 * 功能。
 */
public class BaseFragmentActivity extends FragmentActivity {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * actionBar 在點選以後會進入的頁面。
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();

		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			intent.setClass(this, SearchActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			return true;

		case R.id.action_history:
			intent.setClass(this, HistoryFragmentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			return true;

		case R.id.action_logout:
			intent.setClass(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			final Intent INTENT = intent;	// 如果不換成 final, 在 DialogInterface.OnClickListener() 裡會報錯。

			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("登出");
			builder.setMessage("確定要登出嗎？");
			builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					startActivity(INTENT);
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
				}
			});
			builder.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
