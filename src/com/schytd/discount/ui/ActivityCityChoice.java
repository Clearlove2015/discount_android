package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.sortlistview.CharacterParser;
import com.schytd.discount.ui.sortlistview.GridViewAdapter;
import com.schytd.discount.ui.sortlistview.PinyinComparator;
import com.schytd.discount.ui.sortlistview.SortAdapter;
import com.schytd.discount.ui.sortlistview.SortModel;
import com.schytd.discount_android.R;

public class ActivityCityChoice extends Activity {
	public static int RESULT_CITY_CHOICE = 333;
	private ListView sortListView;
	private SortAdapter adapter;
	private GridViewAdapter adapter_gridview;

	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	private PinyinComparator pinyinComparator;
	// 当前定位城市
	private TextView mTextView_position;
	// gridview
	private GridView mGridView_num;
	// list_view
	private LinearLayout mList_View;
	private List<String> list_index = new ArrayList<String>();
	// 城市 代码
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_choice);
		initViews();
	}

	private void initViews() {
		sp = getSharedPreferences("city_code", Context.MODE_PRIVATE);
		mList_View = (LinearLayout) this.findViewById(R.id.list_view);
		mTextView_position = (TextView) this
				.findViewById(R.id.location_position);
		String add = getIntent().getStringExtra("location");
		if (StrTools.isNull(add)) {
			mTextView_position.setText("定位失败");
		} else {
			mTextView_position.setText(add + "市");
		}
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String city = ((SortModel) adapter.getItem(position)).getName();
				Intent i = new Intent();
				i.putExtra("city", city);
				i.putExtra("city_code", sp.getString(city, "510100000000"));
				Toast.makeText(ActivityCityChoice.this,
						sp.getString(city, "510100000000"), Toast.LENGTH_SHORT)
						.show();
				setResult(RESULT_CITY_CHOICE, i);
				finish();
			}
		});

		SourceDateList = filledData(getResources().getStringArray(R.array.city));
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);
		// gridView
		mGridView_num = (GridView) this.findViewById(R.id.city_index);
		// 字母集合
		for (SortModel sortModel : SourceDateList) {
			list_index.add(sortModel.getSortLetters().toUpperCase());
		}
		// 去重复
		removeDuplicate();

		adapter_gridview = new GridViewAdapter(this, list_index);
		mGridView_num.setAdapter(adapter_gridview);
		mGridView_num.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long durition) {
				mGridView_num.setVisibility(View.GONE);
				mList_View.setVisibility(View.VISIBLE);
				filterData(list_index.get(position));
			}
		});
	}

	public void removeDuplicate() {
		for (int i = 0; i < list_index.size() - 1; i++) {
			for (int j = list_index.size() - 1; j > i; j--) {
				if (list_index.get(j).equals(list_index.get(i))) {
					list_index.remove(j);
				}
			}
		}
	}

	/**
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			// 锟斤拷锟斤拷转锟斤拷锟斤拷拼锟斤拷
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 锟斤拷锟斤拷锟斤拷式锟斤拷锟叫讹拷锟斤拷锟斤拷母锟角凤拷锟斤拷英锟斤拷锟斤拷母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;
	}

	// 点击事件
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.city_btn_back:
			finish();
			break;
		case R.id.city_shaixuan:
			if (mList_View.getVisibility() == View.VISIBLE) {
				mList_View.setVisibility(View.GONE);
				mGridView_num.setVisibility(View.VISIBLE);
			} else {
				mList_View.setVisibility(View.VISIBLE);
				mGridView_num.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}

	}

	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getSortLetters();
				if (name.equals(filterStr)) {
					filterDateList.add(sortModel);
				}
			}
		}
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	// 重写返回键事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mList_View.getVisibility() != View.VISIBLE) {
				mList_View.setVisibility(View.VISIBLE);
				mGridView_num.setVisibility(View.GONE);
			} else {
				finish();
			}
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
