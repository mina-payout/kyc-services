package com.minanet.trulioo.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {
	private static final String pattern = "M-dd-yyyy HH:mm:ss";
	public static String getCurrentDateTime() {
		
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, new Locale("en", "US"));
		String date = simpleDateFormat.format(new Date());
		return date;

	}
}
