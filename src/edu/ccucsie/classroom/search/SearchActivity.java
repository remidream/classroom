package edu.ccucsie.classroom.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ccucsie.classroom.R;
import edu.ccucsie.classroom.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.SimpleExpandableListAdapter;

public class SearchActivity extends BaseActivity {
	private static final String KEY1 = "GROUP";
	private static final String KEY2 = "CHILD";

	private static final String[] GROUPS = { "地下室", "一樓", "二樓", "三樓", "五樓" };
	private static final String[][][] CHILDREN = { { { "EA 001", "120人" }, { "EA 003C", "15人" } }, // 地下室
			{ { "EA 101", "90人" }, { "EA 103", "20人" }, { "EA 104", "65人" }, { "EA 105", "40人" } }, // 一樓
			{ { "EA 204", "40人" }, { "EA 205", "65人" }, { "EA 206", "60人" } }, // 二樓
			{ { "EA 307", "7人" }, }, // 三樓
			{ { "EA 509", "30人" } } // 五樓
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();

		// 塞進 Data Structure
		for (int i = 0; i < GROUPS.length; i++) {
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(KEY1, GROUPS[i]);
			curGroupMap.put(KEY2, "");

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			if (CHILDREN.length > i) {
				for (int j = 0; j < CHILDREN[i].length; j++) {
					Map<String, String> curChildMap = new HashMap<String, String>();
					children.add(curChildMap);
					curChildMap.put(KEY1, CHILDREN[i][j][0]);
					curChildMap.put(KEY2, CHILDREN[i][j][1]);
				}
			}
			childData.add(children);
		}

		ExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, groupData,
				android.R.layout.simple_expandable_list_item_1, new String[] { KEY1, KEY2 }, new int[] {
						android.R.id.text1, android.R.id.text2 }, childData,
				android.R.layout.simple_expandable_list_item_2, new String[] { KEY1, KEY2 }, new int[] {
						android.R.id.text1, android.R.id.text2 });

		ExpandableListView listView = (ExpandableListView) findViewById(R.id.ExpandableListView01);
		listView.setAdapter(adapter);

		// 樓層
		listView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return false;
			}
		});

		// 教室
		listView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(SearchActivity.this, ChooseTimeActivity.class);
				Bundle bundle = new Bundle();
				// 地下室
				if (groupPosition == 0) {
					if (childPosition == 0) { // 001 教室
						bundle.putString("Classroom", "EA 101");
					} else if (childPosition == 1) { // 003C 教室
						bundle.putString("Classroom", "EA 003C");
					}
				}
				// 一樓
				else if (groupPosition == 1) {
					if (childPosition == 0) { // 101 教室
						bundle.putString("Classroom", "EA 101");
					} else if (childPosition == 1) { // 103 教室
						bundle.putString("Classroom", "EA 103");
					} else if (childPosition == 2) { // 104 教室
						bundle.putString("Classroom", "EA 104");
					} else if (childPosition == 3) { // 105 教室
						bundle.putString("Classroom", "EA 105");
					}
				}
				// 二樓
				else if (groupPosition == 2) {
					if (childPosition == 0) { // 204 教室
						bundle.putString("Classroom", "EA 204");
					} else if (childPosition == 1) { // 205 教室
						bundle.putString("Classroom", "EA 205");
					} else if (childPosition == 2) { // 206 教室
						bundle.putString("Classroom", "EA 206");
					}
				}
				// 三樓
				else if (groupPosition == 3) {
					if (childPosition == 0) { // 307 教室
						bundle.putString("Classroom", "EA 307");
					}
				}
				// 五樓
				else if (groupPosition == 4) {
					if (childPosition == 0) {
						bundle.putString("Classroom", "EA 509");
					}
				}

				intent.putExtras(bundle);
				intent.setClass(SearchActivity.this, ChooseTimeActivity.class);
				SearchActivity.this.startActivity(intent);
				return false;
			}
		});
	}
}
