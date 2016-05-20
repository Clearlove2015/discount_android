package com.schytd.discount.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount.ui.View.SystemBarTintManager;
import com.schytd.discount_android.R;

public class ActivityDetail extends FragmentActivity implements
		OnDismissListener {
	public static int REQUEST_SEARCH = 1212;
	// 列表模式
	private FragemntDetailListModel mFragment_list;
	private FragmentManager mFragmentManager;
	private static TextView mTextView_location;
	// 筛选
	private LinearLayout mLinearLayout_discount, mLinearLayout_distance,
			mLinearLayout_paixu;
	private TextView mTextView_discount, mTextView_distance,
			mTextView_shaixuan;
	private ImageView mImageView_discount, mImageView_distance,
			mImageView_shaixuan;
	private int screenWidth;
	private int screenHeight;
	// 筛选框的适配器
	private MyAdapter adapter;
	private SubAdapter subAdapter;
	private int idx;
	// 距离
	private String[] mDistanceArray = null;
	// 回调接口 用于传递筛选的值
	private OnGetChoiceListener mGetChoiceListener;
	// 分类数组
	private String classfiy[][];
	// 返回
	private ButtonIcon mImageView_back;
	// 地图
	private ButtonIcon mImageView_map;
	// 搜索
	private ButtonIcon mButtonIcon_search;
	// 图标
	private List<Integer> icons;
	private List<Integer> icons_2;
	private int[] icons_press = { R.drawable.icon_other, R.drawable.icon_eat2,
			R.drawable.icon_play2, R.drawable.icon_time, R.drawable.icon_walk2 };
	// 二级图标
	private int[][] icon_classfiy;
	// 类别
	private String condition = "0";
	// 经度
	private double lat = 0, lng = 0;
	// 分类
	private Map<String, String> mListTitle;
	// 地址
	private String nowAdd = "";
	// 区域代码
	private String direction = "0";

	public interface OnGetChoiceListener {
		public void onGetChoice(int type, int id, String condition, Boolean has);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		// 创建状态栏的管理实例
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// 激活状态栏设置
		tintManager.setStatusBarTintEnabled(true);
		// 激活导航栏设置
		tintManager.setNavigationBarTintEnabled(true);
		// 设置一个颜色给系统栏
		tintManager.setTintColor(getResources().getColor(R.color.Indigo_colorPrimary));
		setContentView(R.layout.activity_seller_classfiy_layout);
		init();
		// 默认加载列表模式
		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.beginTransaction()
				.add(R.id.detail_show_model, mFragment_list).commit();
		sendSearch();
	}

	private void init() {
		direction = ConstantData.district;
		mListTitle = new HashMap<String, String>();
		mListTitle.put("餐饮-全部", "100");
		mListTitle.put("娱乐-全部", "200");
		mListTitle.put("休闲-全部", "300");
		mListTitle.put("住行-全部", "400");
		mListTitle.put("中餐", "10001");
		mListTitle.put("火锅", "10002");
		mListTitle.put("烧烤", "10003");
		mListTitle.put("大排档", "10004");
		mListTitle.put("海鲜", "10005");
		mListTitle.put("西餐", "10006");
		mListTitle.put("料理", "10007");
		mListTitle.put("干锅", "10008");
		mListTitle.put("特色餐饮", "10009");
		mListTitle.put("餐饮-其他", "10099");
		mListTitle.put("KTV", "20001");
		mListTitle.put("酒吧", "20002");
		mListTitle.put("会所", "20003");
		mListTitle.put("娱乐-其他", "20099");
		mListTitle.put("茶楼", "30001");
		mListTitle.put("水吧", "30002");
		mListTitle.put("咖啡", "30003");
		mListTitle.put("休闲-其他", "30099");
		mListTitle.put("酒店", "40001");
		mListTitle.put("公寓", "40002");
		mListTitle.put("生活服务", "40003");
		mListTitle.put("住行-其他", "40099");

		mTextView_discount = (TextView) this.findViewById(R.id.tv_categry);
		mTextView_shaixuan = (TextView) this.findViewById(R.id.tv_shaixuan_);
		nowAdd=getIntent().getStringExtra("nowAdd");
		condition = getIntent().getStringExtra("condition");
		mDistanceArray = new String[] { "1000", "2000", "5000", "0" };
		String name = "";
		name = getIntent().getStringExtra("name");
		if (!StrTools.isNull(name)) {
			mTextView_discount.setText(name);
		}
		lat = getIntent().getDoubleExtra("lat", 0);
		lng = getIntent().getDoubleExtra("lng", 0);
		classfiy = new String[][] {
				null,
				this.getResources().getStringArray(R.array.array_subclassfiy_1),
				this.getResources().getStringArray(R.array.array_subclassfiy_2),
				this.getResources().getStringArray(R.array.array_subclassfiy_3),
				this.getResources().getStringArray(R.array.array_subclassfiy_4) };
		icons = new ArrayList<Integer>();
		icons.add(R.drawable.icon_other);
		icons.add(R.drawable.icon_eat);
		icons.add(R.drawable.icon_play);
		icons.add(R.drawable.icon_time);
		icons.add(R.drawable.icon_walk);
		icons.add(R.drawable.icon_other);
		icons_2 = new ArrayList<Integer>();
		icons_2.add(R.drawable.icon_distance);
		icons_2.add(R.drawable.icon_distance);
		icons_2.add(R.drawable.icon_distance);
		icons_2.add(R.drawable.icon_distance);
		// 二级菜单图标 吃
		int[] eat_icon = { R.drawable.icon_other,R.drawable.icon_eat_china_food,
				R.drawable.icon_eat_babicu, R.drawable.icon_eat_hot_pot,
				R.drawable.icon_eat_big_dang, R.drawable.icon_eat_sea_food,
				R.drawable.icon_eat_west_food, R.drawable.icon_eat_arrange,
				R.drawable.icon_eat_gan_guo, R.drawable.icon_eat_special,
				R.drawable.icon_other };
		// 娱乐 二级菜单图标
		int[] play_icon = { R.drawable.icon_other,R.drawable.icon_play_ktv,
				R.drawable.icon_play_jiu_bar, R.drawable.icon_play_club,
				R.drawable.icon_other };
		// 休闲 二级菜单图标
		int[] time_icon = { R.drawable.icon_other,R.drawable.icon_time_tea,
				R.drawable.icon_time_water_bar, R.drawable.icon_time_coffe,
				R.drawable.icon_other };
		// 住行二级菜单
		int[] walk_icon = { R.drawable.icon_other,R.drawable.icon_walk_hotal,
				R.drawable.icon_walk_partment,
				R.drawable.icon_walk_life_service, R.drawable.icon_other };
		icon_classfiy = new int[][] { null, eat_icon, play_icon, time_icon,
				walk_icon };
		initScreenWidth();
		mLinearLayout_paixu = (LinearLayout) this
				.findViewById(R.id.lin_shaixuan_);
		mLinearLayout_discount = (LinearLayout) this
				.findViewById(R.id.lin_shaixuan_discount);
		mLinearLayout_distance = (LinearLayout) this
				.findViewById(R.id.lin_shaixuan_distance);
		mTextView_distance = (TextView) this.findViewById(R.id.tv_distance);
		mImageView_discount = (ImageView) this.findViewById(R.id.icon_discount);
		mImageView_distance = (ImageView) this.findViewById(R.id.icon_distance);
		mImageView_shaixuan = (ImageView) this
				.findViewById(R.id.icon_shaixuan_);
		mFragment_list = new FragemntDetailListModel(condition, lat, lng,
				direction,nowAdd);
		// 得到回调接口实现类的引用
		mGetChoiceListener = (OnGetChoiceListener) mFragment_list;
		mImageView_back = (ButtonIcon) this.findViewById(R.id.list_btn_back);
		mImageView_back.setOnClickListener(mClickListener);
		mImageView_map = (ButtonIcon) this.findViewById(R.id.list_goto_map);
		mImageView_map.setOnClickListener(mClickListener);
		mButtonIcon_search = (ButtonIcon) this
				.findViewById(R.id.list_goto_search);
		mButtonIcon_search.setOnClickListener(mClickListener);
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		mLinearLayout_discount.setOnClickListener(mClickListener);
		mLinearLayout_distance.setOnClickListener(mClickListener);
		mLinearLayout_paixu.setOnClickListener(mClickListener);
		intent = new Intent(this, ActivityMap.class);
		// 向activity传递默认距离距离
		intent.putExtra("distance", mDistanceArray[0]);
	}

	String mSeatchtxt = "";

	private void sendSearch() {
		try {
			mSeatchtxt = getIntent().getStringExtra("search_txt");
			if (StrTools.isNull(mSeatchtxt)) {
				mSeatchtxt = "";
			}
		} catch (Exception e) {
			mSeatchtxt = "";
		}
		if (!StrTools.isNull(mSeatchtxt)) {
			mFragment_list.mSearchTxt = mSeatchtxt;
			mFragment_list.condition_one = "";
			mFragment_list.condition_two = "";
		}
	}

	// 清除搜索
	private void clearSearch() {
		mFragment_list.mSearchTxt = "";
	}

	private Intent intent;
	// 点击事件
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.list_goto_map:
				List<SellerInfoItem> data = mFragment_list.mData;
				intent.putExtra("data", (Serializable) data);
				intent.putExtra("district", direction);
				intent.putExtra("search", mSeatchtxt);
				startActivity(intent);
				break;
			case R.id.list_goto_search:
				// 跳转到搜索界面
				startActivityForResult(new Intent(ActivityDetail.this,
						ActivitySearch.class), REQUEST_SEARCH);
				break;
			// 返回
			case R.id.list_btn_back:
				mSeatchtxt = "";
				ConstantData.ISALL = false;
				finish();
				break;
			case R.id.lin_shaixuan_discount:
				idx = 1;
				mImageView_discount.setImageResource(R.drawable.icon_open);
				showPopupWindow(findViewById(R.id.ll_layout), 1);
				break;
			case R.id.lin_shaixuan_distance:
				idx = 2;
				mImageView_distance.setImageResource(R.drawable.icon_open);
				showPopupWindow(findViewById(R.id.ll_layout), 2);
				break;
			// 排序
			case R.id.lin_shaixuan_:
				idx = 3;
				mImageView_shaixuan.setImageResource(R.drawable.icon_open);
				showPopupWindow(findViewById(R.id.ll_layout), 3);
				break;
			// 关闭pop
			case R.id.pop_bg:
				popupWindow.dismiss();
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SEARCH) {
			if (resultCode == ActivitySearch.RESULT_SEARCH) {
				String search_txt = data.getStringExtra("search_txt");
				if (!StrTools.isNull(search_txt)) {
					mFragment_list.doSearch(search_txt);
				}
			}
		}
	}

	private ListView mlistView1, mlistView2;
	private LinearLayout mLinearLayout_con;
	private PopupWindow popupWindow;

	public void showPopupWindow(View anchor, final int flag) {
		popupWindow = new PopupWindow(this);
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.shaixuan_popupwindow, null);
		mlistView1 = (ListView) contentView.findViewById(R.id.lv1);
		mlistView2 = (ListView) contentView.findViewById(R.id.lv2);
		mLinearLayout_con = (LinearLayout) contentView
				.findViewById(R.id.lv_layout);
		LinearLayout pop_bg = (LinearLayout) contentView
				.findViewById(R.id.pop_bg);
		switch (flag) {
		case 1:
			adapter = new MyAdapter(this,
					initArrayData(R.array.array_classfiy), icons, icons_press);
			break;
		case 2:
			adapter = new MyAdapter(this,
					initArrayData(R.array.array_distance), null, icons_press);
			break;
		case 3:
			adapter = new MyAdapter(this,
					initArrayData(R.array.array_shaixuan), null, icons_press);
			break;
		}
		mlistView1.setAdapter(adapter);
		mlistView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (parent.getAdapter() instanceof MyAdapter) {
					// 设置选中项
					adapter.setSelectItem(position);
					adapter.notifyDataSetChanged();
					mlistView2.setVisibility(View.INVISIBLE);
					if (mlistView2.getVisibility() == View.INVISIBLE) {
						mlistView2.setVisibility(View.VISIBLE);
						LayoutParams params = mlistView2.getLayoutParams();
						params.height = mlistView1.getHeight();
						mlistView2.setLayoutParams(params);
						switch (idx) {
						case 1:
							mLinearLayout_con.getLayoutParams().width = 0;
							if (classfiy[position] != null) {
								subAdapter = new SubAdapter(
										ActivityDetail.this,
										classfiy[position],
										icon_classfiy[position]);
							} else {
								subAdapter = null;
							}
							break;
						case 2:
							mLinearLayout_con.getLayoutParams().width = 0;
							if (classfiy[position] != null) {
								subAdapter = new SubAdapter(
										ActivityDetail.this,
										classfiy[position],
										icon_classfiy[position]);
							} else {
								subAdapter = null;
							}

							break;
						case 3:
							mLinearLayout_con.getLayoutParams().width = 0;
							if (classfiy[position] != null) {
								subAdapter = new SubAdapter(
										ActivityDetail.this,
										classfiy[position],
										icon_classfiy[position]);
							} else {
								subAdapter = null;
							}

							break;
						}
						if (subAdapter != null && flag == 1) {
							// 存在下级
							mlistView2.setAdapter(subAdapter);
							subAdapter.notifyDataSetChanged();
							mlistView2
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {
											String name = (String) parent
													.getAdapter().getItem(
															position);
											setHeadText(idx, name, position,
													mListTitle.get(name), true);
											popupWindow.dismiss();
											subAdapter = null;
										}
									});
						} else {
							// 当没有下级时直接将信息设置textview中
							String name = (String) parent.getAdapter().getItem(
									position);
							setHeadText(idx, name, position,
									mListTitle.get(name), false);
							popupWindow.dismiss();

						}
					}

				}
			}
		});

		popupWindow.setOnDismissListener(this);
		popupWindow.setWidth(screenWidth);
		// popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(screenHeight);
		popupWindow.setContentView(contentView);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new PaintDrawable(Color.argb(120, 0,
				0, 0)));
		popupWindow.showAsDropDown(anchor);
		pop_bg.setOnClickListener(mClickListener);
	}

	private void setHeadText(int idx, String text, int position, String con,
			boolean has) {
		switch (idx) {
		case 1:
			// 传递类别
			clearSearch();
			intent.putExtra("condition", mListTitle.get(text));
//			Toast.makeText(ActivityDetail.this, mListTitle.get(text),
//					Toast.LENGTH_SHORT).show();
			mTextView_discount.setText(text);
			mGetChoiceListener.onGetChoice(1, position, con, has);
			break;
		case 2:
			mTextView_distance.setText(text);
			mGetChoiceListener.onGetChoice(2, position, con, has);
			// 向activity传递距离
			intent.putExtra("distance", mDistanceArray[position]);
			break;
		case 3:
			mTextView_shaixuan.setText(text);
			mGetChoiceListener.onGetChoice(3, position, "0", false);
			break;
		}
	}

	private void initScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		screenHeight = dm.heightPixels;
		screenWidth = dm.widthPixels;
	}

	private List<String> initArrayData(int id) {
		List<String> list = new ArrayList<String>();
		String[] array = this.getResources().getStringArray(id);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	@Override
	public void onDismiss() {
		mImageView_discount.setImageResource(R.drawable.icon_close);
		mImageView_distance.setImageResource(R.drawable.icon_close);
		mImageView_shaixuan.setImageResource(R.drawable.icon_close);
	}

	// 反编译监听
	String add = null;

	/** * 菜单、返回键响应 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mSeatchtxt = "";
			ConstantData.ISALL = false;
			finish();
		}
		return false;
	}
}

class MyAdapter extends BaseAdapter {
	private List<String> l;
	private List<Integer> icons;
	private Context context;
	private int selectItem = -1;
	private int[] icon_press;

	public MyAdapter(Context context, List<String> l, List<Integer> icons,
			int[] icons_press) {

		this.context = context;
		this.l = l;
		if (icons != null) {
			this.icons = icons;
		}
		this.icon_press = icons_press;
	}

	@Override
	public int getCount() {
		return l.size();
	}

	@Override
	public Object getItem(int position) {
		return l.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.shaixuan_popupwindow_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.name.setText(l.get(position).toString());
		if (icons != null) {
			holder.icon.setImageResource(icons.get(position));
		}
		if (position == selectItem) {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.Indigo_nav_color));
			holder.icon.setImageResource(icon_press[position]);
			holder.name.setTextColor(Color.WHITE);
		} else {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.white));
			if (icons != null) {
				holder.icon.setImageResource(icons.get(position));
			}
			holder.name.setTextColor(context.getResources().getColor(
					R.color.Indigo_nav_color));
		}
		return convertView;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	class Holder {
		TextView name;
		ImageView icon;
	}
}

class SubAdapter extends BaseAdapter {

	Context context;
	LayoutInflater layoutInflater;
	String[] citiy;
	int[] icons;
	public int foodpoition;

	public SubAdapter(Context context, String[] citiy, int[] icons) {
		this.context = context;
		this.citiy = citiy;
		this.icons = icons;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return citiy.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return citiy[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.shaixuan_popupwindow_item, null);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.name);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.textView.setText(citiy[position]);
		viewHolder.icon.setImageResource(icons[position]);
		return convertView;
	}

	public static class ViewHolder {
		public TextView textView;
		public ImageView icon;
	}

}
