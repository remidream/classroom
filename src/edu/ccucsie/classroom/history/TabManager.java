package edu.ccucsie.classroom.history;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

public class TabManager implements TabHost.OnTabChangeListener {
	private final FragmentActivity mActivity;
	private final TabHost mTabHost;
	private final int mContainerId;
	private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
	TabInfo mLastTab;

	/**
	 * 分面資訊，包含該分頁的名稱、傳入哪個類別、以及所帶的 Bundle。
	 */
	static final class TabInfo {
		private final String tag;
		private final Class<?> clss;
		private final Bundle args;
		private Fragment fragment;

		TabInfo(String _tag, Class<?> _class, Bundle _args) {
			tag = _tag;
			clss = _class;
			args = _args;
		}
	}

	/**
	 * 創造一個分頁所形成的 View, 當你按下任一分頁, 它就會根據對應的 View 來進行切換。
	 */
	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View view = new View(mContext);
			view.setMinimumWidth(0);
			view.setMinimumHeight(0);
			return view;
		}
	}

	public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
		mActivity = activity;
		mTabHost = tabHost;
		mContainerId = containerId;
		mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * 新增一個分頁，就必須傳入TabHost.TabSpec 物件、以及要轉跳的 fragment、以及所帶的 Bundle。 如果沒有設參數，則
	 * Bundle 可以傳入 null。
	 */
	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
		// 將 TabSpec 的物件帶入 new 出來的 DummyTabFactory, 方便這個分頁的內容呈現一個 View。
		tabSpec.setContent(new DummyTabFactory(mActivity));

		String tag = tabSpec.getTag();
		TabInfo info = new TabInfo(tag, clss, args);

		// 要進行切換 fragment 的時候,
		// 就會利用 FragmentTransaction 的物件來進行調度,而當 commit 出去的時候, 就可以進行變更。
		info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
		if (info.fragment != null && !info.fragment.isDetached()) {
			FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();

			// 進行 detach 時, 指定該 fragment 從該 View 當中分離。
			fragmentTransaction.detach(info.fragment);
			fragmentTransaction.commit();
		}

		mTabs.put(tag, info);
		mTabHost.addTab(tabSpec);
	}

	@Override
	public void onTabChanged(String tabId) {
		TabInfo newTab = mTabs.get(tabId);

		// 如果按下的分頁是目前的分頁，則不做任何處理。
		if (newTab != mLastTab) {
			FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
			if (mLastTab != null) {

				// 將前一個分頁從 fragmentTransaction 中去除。
				if (mLastTab.fragment != null) {
					fragmentTransaction.detach(mLastTab.fragment);
				}
			}

			if (newTab != null) {
				newTab.fragment = Fragment.instantiate(mActivity, newTab.clss.getName(), newTab.args);
				fragmentTransaction.add(mContainerId, newTab.fragment, newTab.tag);

				// 如果新分頁沒有內容，則新增一個新的空間給它，並且加入 fragmentTransaction 之中。
				// 如果新分頁有內容，則直接指派該分頁成為目前要呈現的分頁。
				if (newTab.fragment == null) {
					fragmentTransaction.detach(mLastTab.fragment);
				} else {
					mActivity.getSupportFragmentManager().popBackStack();
					fragmentTransaction.replace(mContainerId, newTab.fragment);
					fragmentTransaction.attach(newTab.fragment);
				}
			}

			// 更新分頁，執行分頁轉換。
			mLastTab = newTab;
			fragmentTransaction.commit();
			mActivity.getSupportFragmentManager().executePendingTransactions();
		}
	}
}