package edu.ccucsie.classroom.history;

import edu.ccucsie.classroom.R;
import edu.ccucsie.classroom.base.BaseFragmentActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class HistoryFragmentActivity extends BaseFragmentActivity {
	private TabHost mTabHost;
	private TabManager mTabManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_tab_history);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
		// 設定一開始就跳到第一個分頁
		mTabHost.setCurrentTab(0);

		mTabManager.addTab(mTabHost.newTabSpec("Fragment1").setIndicator("歷史記錄"), RecordFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec("Fragment2").setIndicator("未審核"), UnverifiedFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec("Fragment3").setIndicator("已審核"), VerifiedFragment.class, null);
	}
}
