package com.schytd.discount.ui.sortlistview;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schytd.discount_android.R;

public class GridViewAdapter extends BaseAdapter{
	private Context mContext;
	private List<String> list_index;

	public GridViewAdapter(Context mContext, List<String> list) {
		this.mContext = mContext;
		this.list_index=list;
	}
	
	public void updateListView(List<SortModel> list) {
		for (SortModel sortModel : list) {
			list_index.add(sortModel.getName());
		}
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list_index.size();
	}

	public Object getItem(int position) {
		return list_index.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		// gridview
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.activity_city_gridview_item, null);
			TextView mTextView_num = (TextView) view
					.findViewById(R.id.city_gridview_num);
			// 根据position获取分类的首字母的Char ascii值
			mTextView_num.setText(getAlpha(list_index.get(position)));
		}
		return view;
	}


	/**
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 锟斤拷锟斤拷锟斤拷式锟斤拷锟叫讹拷锟斤拷锟斤拷母锟角凤拷锟斤拷英锟斤拷锟斤拷母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
}