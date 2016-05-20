package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.schytd.discount.ui.View.MyListView;
import com.schytd.discount_android.R;

public class ActivityCardList extends Activity {
	private MyListView mListView;
	private List<String> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discount_card_layout);
		init();
	}

	private void init() {
		mData = new ArrayList<String>();
		mData.add("1");
		mData.add("1");
		mData.add("1");
		mListView = (MyListView) this.findViewById(R.id.card_listview);
		mListView.setAdapter(mAdapter);
	}

	private BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_card_listview_item_layout, null);
			}
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

}
