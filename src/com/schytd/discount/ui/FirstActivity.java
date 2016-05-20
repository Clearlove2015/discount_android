package com.schytd.discount.ui;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.schytd.discount.enties.IndexImage;
import com.schytd.discount_android.R;

public class FirstActivity extends FragmentActivity {
	private ViewPager mViewPager_index;
	private int[] icons = { R.drawable.app_icon, R.drawable.app_icon,
			R.drawable.app_icon, R.drawable.app_icon };
	private List<IndexImage> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_layout);
		init();
	}
	@SuppressWarnings("unchecked")
	private void init() {
		mData=(List<IndexImage>) getIntent().getSerializableExtra("path");
		mViewPager_index = (ViewPager) this.findViewById(R.id.first_viewpager);
		mViewPager_index.setAdapter(mAdapter);
	}
	private FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(
			getSupportFragmentManager()) {
		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new FragmentFirst(icons[position], 1);
				break;
			case 1:
				fragment = new FragmentFirst(icons[position], 1);
				break;
			case 2:
				fragment = new FragmentFirst(icons[position], 1);
				break;
			case 3:
				fragment = new FragmentFirst(icons[position], 2,mData);
				break;
			}
			return fragment;
		}
	};
}
