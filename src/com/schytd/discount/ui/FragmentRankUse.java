package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.FinalValue;
import com.schytd.discount.enties.UseScoreHistory;
import com.schytd.discount.enties.UseScoreHistoryItem;
import com.schytd.discount.tools.DateTools;
import com.schytd.discount.ui.progress.ProgressLayout;
import com.schytd.discount_android.R;

public class FragmentRankUse extends Fragment implements
		SwipeRefreshLayout.OnRefreshListener {
	private List<UseScoreHistoryItem> mData;
	private ListView mListView;
	private UserBussiness mUserBussiness;
	private UseScoreHistory mUseScoreHistory;
	private int totalPage;
	private int mCurrentPage = 1;
	public static GetDataTask mTask;
	// 上下文
	private Context c;
	// swiplayout
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ProgressLayout mProgress;

	public FragmentRankUse(Context c) {
		this.c = c;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_rank_use_layout, container,
				false);
		init(v);
		return v;
	}

	private void init(View v) {
		mUserBussiness = new UserBussinessImpl(getActivity());
		mData = new ArrayList<UseScoreHistoryItem>();
		mListView = (ListView) v.findViewById(R.id.fragment_rank_listview);
		mListView.setAdapter(mAdapter);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v
				.findViewById(R.id.rank_swipe_container);
		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mProgress = (ProgressLayout) v.findViewById(R.id.rank_progress);
		mProgress.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mListView.setOnScrollListener(mScrollListener);
		// 加载数据
		mTask = new GetDataTask();
		mTask.execute(mCurrentPage);
	}

	class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress.setRefreshing(true);

		}

		@Override
		protected void onPostExecute(Void result) {
			mProgress.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
			mSwipeRefreshLayout.setRefreshing(false);
			ArrayList<UseScoreHistoryItem> data = null;
			if (mUseScoreHistory != null) {
				data = mUseScoreHistory.getResultList();
			}
			if (data != null) {
				for (UseScoreHistoryItem useScoreHistoryItem : data) {
					mData.add(useScoreHistoryItem);
				}
			}
			mAdapter.notifyDataSetChanged();
			totalPage = FinalValue.SCORE_USE_TOTAL_PAGE;
		}

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				mUseScoreHistory = mUserBussiness.getScoreUseHistory("10",
						params[0].toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	};

	private BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(
						R.layout.fragment_rank_use_listview_item, null);
			}
			TextView textView_name = (TextView) convertView
					.findViewById(R.id.listview_use_name);
			TextView textView_date = (TextView) convertView
					.findViewById(R.id.listview_use_date);
			TextView textView_total = (TextView) convertView
					.findViewById(R.id.listview_use_total);
			UseScoreHistoryItem item = mData.get(position);
			int type = item.getUseType();
			if (type == 1) {
				textView_name.setText("升级用户");
			} else {
				textView_name.setText("兑换礼物");
			}
			textView_total.setText(item.getScore());
			String date = DateTools.getDate(Long.parseLong(item.getUseTime()));
			textView_date.setText(date);

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
	// listview滚动数据
	private OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			switch (scrollState) {
			// 滚动时
			case OnScrollListener.SCROLL_STATE_FLING:
				mSwipeRefreshLayout.setEnabled(false);

				// 当不滚动时
			case OnScrollListener.SCROLL_STATE_IDLE:
				// 判断滚动到顶部
			    if(mListView.getFirstVisiblePosition() == 0){
			    	mSwipeRefreshLayout.setEnabled(true);
			    }
				// 判断滚动到底部
				if (mListView.getLastVisiblePosition() == (mListView.getCount() - 1)) {
					// 加载数据
					if (mCurrentPage + 1 <= totalPage) {
						mCurrentPage++;
						new GetDataTask().execute(mCurrentPage);
					} else {
						// 没有数据 不加载
						mSwipeRefreshLayout.setEnabled(false);
						mSwipeRefreshLayout.setRefreshing(false);
						Toast.makeText(getActivity(), "没有更多啦~",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};

	// 下拉刷新
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		// 下拉刷新
		if (mCurrentPage + 1 <= totalPage) {
			mCurrentPage++;
			new GetDataTask().execute(mCurrentPage);
		} else {
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

}
