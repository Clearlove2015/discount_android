package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount_android.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	// *************
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
	protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";
	protected boolean pauseOnScroll = true;
	protected boolean pauseOnFling = true;

	/** 图片加载监听事件 **/
	public static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
					displayedImages.add(imageUri); // 将图片uri添加到集合中
				}
			}
		}
	}

	// 图片设置
	public DisplayImageOptions setImg() {
		// *************
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.list_default)
				.showImageOnFail(R.drawable.list_default).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置为RGB565比起默认的ARGB_8888要节省大量的内存
				.delayBeforeLoading(50)// 载入图片前稍做延时可以提高整体滑动的流畅度
				.showImageForEmptyUri(R.drawable.list_default).build();
		// *************
		return options;
	}

	// 获得图片的连接地址
	public String[] getPath(ArrayList<SellerInfoItem> mdata) {
		String[] imgUri = new String[mdata.size()];
		if (mdata != null && mdata.size() > 0) {
			for (int i = 0; i < mdata.size(); i++) {
				SellerInfoItem item = mdata.get(i);
				imgUri[i] = item.getLogoPic();
			}
		}
		return imgUri;

	}

}
