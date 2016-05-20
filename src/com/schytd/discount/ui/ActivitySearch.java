package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.ButtonFlat;
import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount.ui.View.LayoutRipple;
import com.schytd.discount_android.R;

public class ActivitySearch extends Activity {
	public static int RESULT_SEARCH = 113;

	private ButtonIcon mImageViewSearch;
	private EditText mEditTextSearch;
	private ButtonFlat mButtonFlat_search;
	// 搜索历史
	private ListView mListView;
	private List<String> mData;
	// 清空历史
	private LayoutRipple mLayoutRipple;
	private SharedPreferences sp;
	// 热门搜索
	private TextView mTextView_one, mTextView_two, mTextView_three,
			mTextView_four, mTextView_five, mTextView_six;
	private LayoutRipple mClear_Search_His;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_layout);
		init();
	}

	private void init() {
		mClear_Search_His = (LayoutRipple) this
				.findViewById(R.id.clear_search_his);
		mTextView_one = (TextView) this.findViewById(R.id.search_tv_one);
		mTextView_two = (TextView) this.findViewById(R.id.search_tv_two);
		mTextView_three = (TextView) this.findViewById(R.id.search_tv_three);
		mTextView_four = (TextView) this.findViewById(R.id.search_tv_four);
		mTextView_five = (TextView) this.findViewById(R.id.search_tv_five);
		mTextView_six = (TextView) this.findViewById(R.id.search_tv_six);
		mTextView_one.setOnClickListener(mClickListener);
		mTextView_two.setOnClickListener(mClickListener);
		mTextView_three.setOnClickListener(mClickListener);
		mTextView_four.setOnClickListener(mClickListener);
		mTextView_five.setOnClickListener(mClickListener);
		mTextView_six.setOnClickListener(mClickListener);
		mClear_Search_His.setOnClickListener(mClickListener);
		sp = getSharedPreferences("search_his", Context.MODE_PRIVATE);
		mLayoutRipple = (LayoutRipple) this.findViewById(R.id.clear_search_his);
		mData = new ArrayList<String>();
		// 判断存在搜索历史的时候才显示 清空搜索历史
		int count = sp.getInt("count", 0);
		String one = "", two = "", three = "", four = "";
		switch (count) {
		case 0:
			one = sp.getString("one", "");
			if (!StrTools.isNull(one)) {
				mData.add(one);
			}
			break;
		case 1:
			one = sp.getString("one", "");
			two = sp.getString("two", "");
			if (!StrTools.isNull(two)) {
				mData.add(two);
				Toast.makeText(ActivitySearch.this, "++++++",
						Toast.LENGTH_SHORT).show();
			}
			if (!StrTools.isNull(one)) {
				mData.add(one);
			}
			break;
		case 2:
			one = sp.getString("one", "");
			two = sp.getString("two", "");
			three = sp.getString("three", "");
			if (!StrTools.isNull(three)) {
				mData.add(three);
			}
			if (!StrTools.isNull(two)) {
				mData.add(two);
			}
			if (!StrTools.isNull(one)) {
				mData.add(one);
			}
			break;
		case 3:
			one = sp.getString("one", "");
			two = sp.getString("two", "");
			three = sp.getString("three", "");
			four = sp.getString("four", "");
			if (!StrTools.isNull(four)) {
				mData.add(four);
			}
			if (!StrTools.isNull(three)) {
				mData.add(three);
			}
			if (!StrTools.isNull(two)) {
				mData.add(two);
			}
			if (!StrTools.isNull(one)) {
				mData.add(one);
			}
			break;
		default:
			one = sp.getString("one", "");
			two = sp.getString("two", "");
			three = sp.getString("three", "");
			four = sp.getString("four", "");
			if (!StrTools.isNull(four)) {
				mData.add(four);
			}
			if (!StrTools.isNull(three)) {
				mData.add(three);
			}
			if (!StrTools.isNull(two)) {
				mData.add(two);
			}
			if (!StrTools.isNull(one)) {
				mData.add(one);
			}
			break;
		}
		if (mData.size() < 1) {
			mLayoutRipple.setVisibility(View.GONE);
		} else {
			mLayoutRipple.setVisibility(View.VISIBLE);
		}
		mListView = (ListView) this.findViewById(R.id.search_history);
		mImageViewSearch = (ButtonIcon) this.findViewById(R.id.search_back);
		mEditTextSearch = (EditText) this.findViewById(R.id.et_search);
		mButtonFlat_search = (ButtonFlat) this.findViewById(R.id.search_btn);
		mButtonFlat_search.setOnClickListener(mClickListener);
		mImageViewSearch.setOnClickListener(mClickListener);
		mEditTextSearch.setOnEditorActionListener(mEditorActionListener);
		mListView.setAdapter(mAdapter);
	}

	private OnEditorActionListener mEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// 搜索内容
			String searchTxt = mEditTextSearch.getText().toString();
			searchBack(searchTxt);
			return true;
		}
	};
	// listview适配器
	private BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater;
			if (convertView == null) {
				mInflater = getLayoutInflater();
				convertView = mInflater.inflate(
						R.layout.search_history_listview_item, null);
			}
			TextView textView = (TextView) convertView
					.findViewById(R.id.tv_search_content);
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.iv_search_icon);
			textView.setText(mData.get(position));
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					searchBack(mData.get(position));
				}
			});
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public int getCount() {
			return mData.size();
		}
	};

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.search_back:
				finish(); // 反向动画
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
				break;
			case R.id.search_btn:
				// 搜索内容
				String searchTxt = mEditTextSearch.getText().toString();
				// 搜索
				searchBack(searchTxt);
				break;
			case R.id.search_tv_one:
				searchBack(mTextView_one.getText().toString());
				break;
			case R.id.search_tv_two:
				searchBack(mTextView_two.getText().toString());
				break;
			case R.id.search_tv_three:
				searchBack(mTextView_three.getText().toString());
				break;
			case R.id.search_tv_four:
				searchBack(mTextView_four.getText().toString());
				break;
			case R.id.search_tv_five:
				searchBack(mTextView_five.getText().toString());
				break;
			case R.id.search_tv_six:
				searchBack(mTextView_six.getText().toString());
				break;
			case R.id.clear_search_his:
				Editor editor = sp.edit();
				editor.clear();
				editor.commit();
				mData.clear();
				mAdapter.notifyDataSetChanged();
				mLayoutRipple.setVisibility(View.GONE);
				break;
			}
		}
	};

	// 搜索返回事件
	private void searchBack(String search) {
		if (StrTools.isNull(search)) {
			Toast.makeText(ActivitySearch.this, "请输入搜索内容~", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Editor editor = sp.edit();
		int count = sp.getInt("count", 0);
		switch (count) {
		case 0:
			editor.putString("one", search);
			count += 1;
			editor.putInt("count", count);
			editor.commit();
			break;
		case 1:
			editor.putString("two", search);
			count += 1;
			editor.putInt("count", count);
			editor.commit();
			break;
		case 2:
			editor.putString("three", search);
			count += 1;
			editor.putInt("count", count);
			editor.commit();
			break;
		case 3:
			editor.putString("four", search);
			count += 1;
			editor.putInt("count", count);
			editor.commit();
			break;
		default:
			editor.putString("one", sp.getString("two", ""));
			editor.putString("two", sp.getString("three", ""));
			editor.putString("three", sp.getString("four", ""));
			editor.putString("four", search);
			count += 1;
			editor.commit();
			break;
		}
		Intent i = new Intent();
		i.putExtra("search_txt", search);
		setResult(RESULT_SEARCH, i);
		// 关闭搜索
		finish();
	}

	// 重写返回键事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
