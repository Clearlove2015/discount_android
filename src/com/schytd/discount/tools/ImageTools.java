package com.schytd.discount.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.schytd.discount_android.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ListView;

public class ImageTools {
	private static int BUFFER_SIZE = 100;
	/***
	 * 加密
	 */
	public static String getFileToByte(String path) {
		File file = new File(path);
		String string = null;
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		int off = 0;
		int r = 0;
		byte[] data = new byte[BUFFER_SIZE * 10];
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			ReadableByteChannel Channel = Channels.newChannel(in);
			while (true) {
				buffer.clear();
				r = Channel.read(buffer);
				if (r == -1)
					break;
				if ((off + r) > data.length) {
					data = grow(data, BUFFER_SIZE * 10);
				}
				byte[] buf = buffer.array();
				System.arraycopy(buf, 0, data, off, r);
				off += r;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] req = new byte[off];
		System.arraycopy(data, 0, req, 0, off);
		string = Base64.encodeToString(req, Base64.DEFAULT);
		return string;
	}

	public static byte[] grow(byte[] src, int size) {
		byte[] tmp = new byte[src.length + size];
		System.arraycopy(src, 0, tmp, 0, src.length);
		return tmp;
	}

	/*
	 * 解密
	 */
	public static Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {

			e.printStackTrace();

		}
		return bitmap;

	}
}
