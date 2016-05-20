package com.schytd.discount.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.schytd.discount_android.R;

public class StrTools {
	// 判断字符串是否有空格
	public static boolean hasNull(String str) {
		if (str.length() > str.trim().length()) {
			return true;
		} else {
			return false;
		}
	}

	// 判断字符串是否为空
	public static boolean isNull(String str) {
		if (str == null || str.equals("") || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	// 判断电话号码长度
	public static boolean isPhoneNum(String str) {
		if (str.length() == 11) {
			return true;
		}
		return false;
	}

	// 判断两个字符串是否相等
	public static boolean isEqual(String str1, String str2) {
		if (isNull(str1) || isNull(str2)) {
			return false;
		}
		if (str1.equals(str2)) {
			return true;
		}
		return false;
	}

	// 字符串的长度大于6
	public static boolean isPassword(String str) {
		if (isNull(str)) {
			return false;
		}
		if (str.trim().length() >= 6) {
			return true;
		}
		return false;
	}

	/**
	 * 生成带图片的二维码
	 * 
	 * @param 字符串
	 *            添加的图片
	 * @return Bitmap
	 * @throws WriterException
	 */
	public static Bitmap cretaeQrCodeBitmap(String str, Drawable drawable)
			throws WriterException {
		// 图片宽度的一般
		int IMAGE_HALFWIDTH = 32;
		// 构造需要插入的图片对象
		// // 插入到二维码里面的图片对象
		BitmapDrawable map = (BitmapDrawable) drawable;
		Bitmap mBitmap = map.getBitmap();
		// // 缩放图片
		Matrix m = new Matrix();
		float sx = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getWidth();
		float sy = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getHeight();
		m.setScale(sx, sy);
		// 重新构造一个40*40的图片
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), m, false);
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, 400, 400);// 如果要指定二维码的边框以及容错率，最好给encode方法增加一个参数：hints
													// 一个Hashmap
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int halfW = width / 2;
		int halfH = height / 2;
		int[] pixels_now = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
						&& y > halfH - IMAGE_HALFWIDTH
						&& y < halfH + IMAGE_HALFWIDTH) {
					pixels_now[y * width + x] = mBitmap.getPixel(x - halfW
							+ IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
				} else {
					// 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
					pixels_now[y * width + x] = matrix.get(x, y) ? 0xff000000
							: 0xfffffff;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap
		bitmap.setPixels(pixels_now, 0, width, 0, 0, width, height);
		return bitmap;
	}

	// 生成密码
	public static String getRightPassword(String num, String psd) {
		String str = null;
		if (isNull(num) || isNull(psd)) {
			return null;
		} else {
			str = num + psd;
		}
		str = NetTools.getMD5Code(str.getBytes());
		return str;
	};

	// 解析本地json
	public static ArrayList<String> getJsonData(Context context) {
		ArrayList<String> dataList = new ArrayList<String>();
		// 将json文件读取到buffer数组中
		InputStream is = context.getResources().openRawResource(R.raw.region);
		byte[] buffer = null;
		String json = null;
		try {
			buffer = new byte[is.available()];
			is.read(buffer);
			// 将字节数组转换为以UTF-8编码的字符串
			json = new String(buffer, "UTF-8");
			JSONObject result = new JSONObject(json);
			JSONArray array = result.getJSONArray("RECORDS");
			for (int i = 0; i < array.length(); i++) {
				if (i == 0) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				dataList.add(object.getString("name"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataList;
	}

	public static long ELFHash(String str) {
		long hash = 0;
		long x = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash << 4) + str.charAt(i);
			if ((x = hash & 0xF0000000L) != 0)
				hash ^= (x >> 24);
			hash &= ~x;
		}
		return hash;
	}

	// 字符串截取
	public static String subLatLng(String str) {
		int length = str.length();
		return str.substring(0, length - 4);
	}

}
