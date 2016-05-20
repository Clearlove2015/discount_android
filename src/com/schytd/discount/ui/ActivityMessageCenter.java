package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.schytd.discount.bussiness.PushBussiness;
import com.schytd.discount.bussiness.impl.PushBussinessImp;
import com.schytd.discount.enties.PushItem;
import com.schytd.discount.ui.ImageActivity.AnimateFirstDisplayListener;
import com.schytd.discount.ui.View.swipelistview.SwipeMenu;
import com.schytd.discount.ui.View.swipelistview.SwipeMenuCreator;
import com.schytd.discount.ui.View.swipelistview.SwipeMenuItem;
import com.schytd.discount.ui.View.swipelistview.SwipeMenuListView;
import com.schytd.discount.ui.View.swipelistview.SwipeMenuListView.OnMenuItemClickListener;
import com.schytd.discount.ui.View.swipelistview.SwipeMenuListView.OnSwipeListener;
import com.schytd.discount_android.R;

public class ActivityMessageCenter extends ImageActivity implements
		SwipeRefreshLayout.OnRefreshListener, OnClickListener {

	private SwipeRefreshLayout mSwipeLayout;
	private SwipeMenuListView mListView;
	private List<PushItem> mData;
	private ProgressBar loading;
	private ImageView mImageView_all, mImageView_readed, mImageView_no_read;
	private static MessageListTask mTask;
	private PushBussiness mPushBussiness;
	private int flag = 0;
	// 图片相关
	private DisplayImageOptions options;
	private String[] imageUriArray;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_layout);
		init();
	}

	private void init() {
		mPushBussiness = new PushBussinessImp(this);
		mTask = new MessageListTask();
		mData = new ArrayList<PushItem>();
		mImageView_all = (ImageView) this.findViewById(R.id.iv_all);
		mImageView_readed = (ImageView) this.findViewById(R.id.iv_readed);
		mImageView_no_read = (ImageView) this.findViewById(R.id.iv_no_read);
		loading = (ProgressBar) findViewById(R.id.loading);
		mListView = (SwipeMenuListView) findViewById(R.id.listView);
		mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
				true, true));
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mSwipeLayout.setEnabled(false);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		// 侧滑删除
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(232, 120,
						118)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setIcon(R.drawable.read_icon);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(222, 169,
						127)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.delete_icon);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				PushItem item = mData.get(position);
				switch (index) {
				case 0:
					// open 点击打开
					break;
				case 1:
					// delete 删除
					// delete(item);
					mData.remove(position);
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		});
		// set SwipeListener
		mListView.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 滚动时
				case OnScrollListener.SCROLL_STATE_FLING:
					mSwipeLayout.setEnabled(false);
					break;
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到顶部
					if (mListView.getFirstVisiblePosition() == 0) {
						mSwipeLayout.setEnabled(true);
					}
					// 判断滚动到底部
					if (mListView.getLastVisiblePosition() == (mListView
							.getCount() - 1)) {
						// 加载数据
						// if (mCurrentPageNum + 1 <= mTotalPageNum) {
						// mCurrentPageNum++;
						// if (isNetworkConnected()) {
						// new GetDataTask().execute(mLat + "", mLng + "",
						// distance, mSearchTxt, 1000 + "",
						// mCurrentPageNum + "", "", "", "");
						// } else {
						// mLinearLaout_noNet.setVisibility(View.VISIBLE);
						// Toast.makeText(getActivity(), "网络不给力",
						// Toast.LENGTH_SHORT).show();
						// }
						//
						// } else {
						// // 没有数据 不加载
						// mSwipeLayout.setEnabled(false);
						// mSwipeLayout.setRefreshing(false);
						// }
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		// 绑定listview点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long duritaion) {
				// 点击消息进入消息详情
				Intent i = new Intent(ActivityMessageCenter.this,
						ActivityMessageDetail.class);
				String id = mData.get(position).getId();
				try {
					mPushBussiness.refreshPushData(id);
				} catch (Exception e) {
					e.printStackTrace();
				}
				i.putExtra("id", id);
				startActivity(i);
			}
		});
		mListView.setAdapter(mAdapter);
		mTask.execute("0");
	}

	private class MessageListTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String type = params[0];
			try {
				if (type.equals("0")) {
					mData = mPushBussiness.getPushData();
				} else {
					mData = mPushBussiness.getPushDataByType(type);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			loading.setVisibility(ProgressBar.GONE);
			mSwipeLayout.setEnabled(true);
			mSwipeLayout.setRefreshing(false);
			if (mData == null) {
				mData = new ArrayList<PushItem>();

			} else {
				getImgPath();
			}
			mAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			setImg();
			mData.clear();
			mSwipeLayout.setRefreshing(true);
			loading.setVisibility(ProgressBar.VISIBLE);
		}

	}

	// 得到图片的路径
	private void getImgPath() {
		for (int i = 0; i < mData.size(); i++) {
			PushItem item = mData.get(i);
			imageUriArray[i] = item.getImgs();
		}
	}

	BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_list_app, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			if (imageUriArray != null) {
				imageLoader.displayImage(imageUriArray[position],
						holder.iv_icon, options, animateFirstListener);
			}
			holder.tv_name.setText(mData.get(position).getTitle());
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_name;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(this);
			}
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	};

	@Override
	public void onRefresh() {
		mTask = new MessageListTask();
		if (flag == 0) {
			mTask.execute("0");
		} else if (flag == 1) {
			mTask.execute("1");
		} else {
			mTask.execute("2");
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_back:
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			break;
		// 全部
		case R.id.btn_all:
			flag = 0;
			new MessageListTask().execute("0");
			mImageView_all.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_open));
			mImageView_readed.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_close));
			mImageView_no_read.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_close));
			break;
		// 已读
		case R.id.btn_readed:
			flag = 2;
			new MessageListTask().execute("2");
			mImageView_all.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_close));
			mImageView_readed.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_open));
			mImageView_no_read.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_close));

			break;
		// 未读
		case R.id.btn_no_read:
			flag = 1;
			new MessageListTask().execute("1");
			mImageView_all.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_close));
			mImageView_readed.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_close));
			mImageView_no_read.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_open));
			break;
		default:
			break;
		}

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

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
