package com.schytd.discount.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ImageUtils {
	private String url;
	// MD5加密处理图片文件名
	private String mUrlMD5;
	// 图片
	private Bitmap mBitmap;
	// 存储路径
	private File mSavePath;

	/**
	 * 图片名.Aasdfhaskdfhas
	 * 
	 * @param context
	 * @param url
	 */
	public ImageUtils(Context context, String url) {
		this.url = url;
		this.mUrlMD5 = "." + this.getStringMD5(url);
		// data/data/com.schytd.discount_android......../cache
		this.mSavePath = context.getCacheDir();
	}

	public ImageUtils() {

	}

	private class SaveCacheThread extends Thread {
		@Override
		public void run() {
			OutputStream out = null;
			try {
				File file = new File(mSavePath, mUrlMD5);
				out = new BufferedOutputStream(new FileOutputStream(file));
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * 从磁盘缓存中读取图片
	 * 
	 * @return
	 */
	public Bitmap getImageFromCache()throws Exception {
		// data/data/com.schytd.discount_android...../cache/.dahskfkljasdhfklahsdjkl
		this.mBitmap = BitmapFactory.decodeFile(mSavePath + File.separator
				+ mUrlMD5);
		if (mBitmap==null) {
			return null;
		}
		return this.mBitmap;
	}

	// 清除图片缓存
	public Boolean deleteImageFromCache() {
		try {
			Log.d("ImageUtils", "deleteImageFromCache called...");
			searchFiles(mSavePath);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 查找图片文件并删除
	 * 
	 * @param file
	 */
	public static void searchFiles(File file) {
		if (file.isFile()) {
			String fileName = file.getName();
			if (fileName.endsWith(".jpg")) {
				Log.d("ImageUtils", "path = " + file.getAbsolutePath());
				file.delete();
			}
		} else {
			File[] files = file.listFiles();
			if (files != null) {
				for (File children : files) {
					searchFiles(children);
				}
			}
		}
	}

	/**
	 * asdfhksadhfksadfka.jpg ------------> .daskfaskldjfklasdhfk
	 * 
	 * @param input
	 * @return
	 */
	private String getStringMD5(String input) {
		try {
			if (input == null) {
				return null;
			}
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] inputByteArray = input.getBytes();
			messageDigest.update(inputByteArray);
			byte[] resultByteArray = messageDigest.digest();
			return this.byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String byteArrayToHex(byte[] byteArray) {

		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		char[] resultCharArray = new char[byteArray.length * 2];

		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}

		return new String(resultCharArray);

	}

	public void onLoadImage(final OnLoadImageListener onLoadImageListener) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				onLoadImageListener.OnLoadImage((Bitmap) msg.obj, url);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				try {
					mBitmap = getImageFromCache();
				} catch (Exception e1) {
				}
				if (mBitmap != null) {
					msg.obj = mBitmap;
					handler.sendMessage(msg);
					return;
				}
				try {
					if (StrTools.isNull(url)) {
						return;
					}
					URL imageUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) imageUrl
							.openConnection();
					InputStream inputStream = conn.getInputStream();
					// Bitmap bitmap = imageHandleSize(inputStream);
					mBitmap = BitmapFactory.decodeStream(inputStream);
					msg.obj = mBitmap;
					handler.sendMessage(msg);
					// 缓存到磁盘上
					// 耗时的任务，那么应该并发执行
					// 开副线程去专门存储
					new SaveCacheThread().start();
				} catch (IOException e) {
				}
			}

		}).start();
	}

	public interface OnLoadImageListener {
		public void OnLoadImage(Bitmap bitmap, String bitmapPath);
	}

	// 按图片的比例压缩
	private static Bitmap imageHandleSize(InputStream inputStream) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 72f;
		float ww = 120f;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
			be = 1;
		}
		newOpts.inSampleSize = be;// 设置缩放比例
		bitmap = BitmapFactory.decodeStream(inputStream, null, newOpts);
		return bitmap;
	}

}