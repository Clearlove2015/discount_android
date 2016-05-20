package com.schytd.discount.tools;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTools {
	@SuppressLint("SimpleDateFormat")
	public static String getDate(long time) {
		Date d = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}

}
