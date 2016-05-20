package com.schytd.discount.ui;

import com.schytd.discount.tools.ImageUtils;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.tools.ImageUtils.OnLoadImageListener;
import com.schytd.discount_android.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentSellerImg extends Fragment {
	String path = null;

	public FragmentSellerImg(String path) {
		this.path = path;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_seller_img_layout,
				container, false);
		init(v);
		return v;
	}

	private void init(View v) {
		final ImageView imageView = (ImageView) v.findViewById(R.id.seller_img);
		if (!StrTools.isNull(path)) {
			ImageUtils imageUtils = new ImageUtils(getActivity(), path);
			imageUtils.onLoadImage(new OnLoadImageListener() {
				@Override
				public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
					if (bitmap != null) {
						imageView.setImageBitmap(bitmap);
					}
				}
			});
		}
	}
}
