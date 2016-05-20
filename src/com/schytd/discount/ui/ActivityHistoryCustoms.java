package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.ConsumHistory;
import com.schytd.discount.enties.ConsumHistoryItem;
import com.schytd.discount.enties.ConsumptionTimesAndAmount;
import com.schytd.discount.enties.FinalValue;
import com.schytd.discount.tools.DateTools;
import com.schytd.discount.ui.progress.ProgressLayout;
import com.schytd.discount_android.R;

public class ActivityHistoryCustoms extends FragmentActivity implements
		SwipeRefreshLayout.OnRefreshListener, OnClickListener {
	private List<ConsumHistoryItem> mData_history;
	private ListView mListView;
	private int mTotalPage;
	private int mCurrentPage=1;
	// 用户消费总次数
	private TextView mTextView_times;
	// 用户消费总金额
	private TextView mTextView_total;
	private UserBussiness mUserBussiness;
	private ConsumHistory mConsumHistroy;
	// 异步任务
	private GetDataTask mTask;
	// swiplayout
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ProgressLayout mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_customs);
		init();
	}

	private class TimeAndAmoutTask extends
			AsyncTask<Void, Void, ConsumptionTimesAndAmount> {

		@Override
		protected ConsumptionTimesAndAmount doInBackground(Void... arg0) {
			ConsumptionTimesAndAmount timesAndAmount = null;
			try {
				timesAndAmount = mUserBussiness.getConsumInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return timesAndAmount;
		}

		protected void onPostExecute(ConsumptionTimesAndAmount result) {
			if (result != null) {
				mTextView_times.setText(result.getTimes());
				mTextView_total.setText(result.getTotal());
			}

		};
	};
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

	public void init() {
		mTextView_times = (TextView) this.findViewById(R.id.cus_times);
		mTextView_total = (TextView) this.findViewById(R.id.cus_total);
		mUserBussiness = new UserBussinessImpl(this);
		mData_history = new ArrayList<ConsumHistoryItem>();
		// 获取数据
		mListView = (ListView) findViewById(R.id.fragment_history_constoms_listview);
		mListView.setAdapter(mAdapter);
		new TimeAndAmoutTask().execute();
		mSwipeRefreshLayout = (SwipeRefreshLayout) this
				.findViewById(R.id.list_swipe_container);
		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mProgress = (ProgressLayout) this.findViewById(R.id.history_progress);
		mProgress.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mListView.setOnScrollListener(mScrollListener);
		// 加载数据
		mTask = new GetDataTask();
		mTask.execute(mCurrentPage);
	}

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
					if (mCurrentPage + 1 <= mTotalPage) {
						mCurrentPage++;
						new GetDataTask().execute(mCurrentPage);
					} else {
						// 没有数据 不加载
						mSwipeRefreshLayout.setEnabled(false);
						mSwipeRefreshLayout.setRefreshing(false);
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

	private class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress.setRefreshing(true);
		}

		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
			mProgress.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
			mSwipeRefreshLayout.setRefreshing(false);
		}
		@Override
		protected Void doInBackground(Integer... params) {
			try {
				mConsumHistroy = mUserBussiness.getConsumHistory("10",
						params[0].toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayList<ConsumHistoryItem> data = null;
			if (mConsumHistroy != null) {
				data = mConsumHistroy.getResultList();
			}
			if (data != null) {
				for (ConsumHistoryItem consumHistoryItem : data) {
					mData_history.add(consumHistoryItem);
				}
				mTotalPage = FinalValue.CONSUM_TOTAL_PAGE;
			}
			return null;
		}
	};

	private static class ViewHolder {
		TextView tv_name;
		TextView tv_date;
		TextView tv_total;
	}

	private ViewHolder mHolder;
	private BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				mHolder = new ViewHolder();
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(
						R.layout.fragment_customs_listview_item, null);
				mHolder.tv_name = (TextView) convertView
						.findViewById(R.id.listview_name);
				mHolder.tv_date = (TextView) convertView
						.findViewById(R.id.listview_date);
				mHolder.tv_total = (TextView) convertView
						.findViewById(R.id.listview_total);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			ConsumHistoryItem item = mData_history.get(position);
			if (item != null) {
				mHolder.tv_name.setText(item.getBussinessName());
				String date = DateTools.getDate(Long.parseLong(item
						.getConsumptionTime()));
				mHolder.tv_date.setText(date);
				mHolder.tv_total.setText(String.valueOf(item.getAmount()));
			}
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return mData_history.get(position);
		}

		@Override
		public int getCount() {
			return mData_history.size();
		}
	};

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_back:
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		mTask.cancel(true);
		// 关闭异步任务
		super.onDestroy();
	}

	@Override
	public void onRefresh() {
		// 下拉刷新
		if (mCurrentPage + 1 <= mTotalPage) {
			mCurrentPage++;
			new GetDataTask().execute(mCurrentPage);
		} else {
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

}
