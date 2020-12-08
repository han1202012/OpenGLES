package kim.hsl.opengl.utils;

import android.util.Log;
import kim.hsl.opengl.Constants;

public class L {

	final static boolean DEBUG = true;

	public static void d(String tag, String msg) {
		if (Constants.DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (Constants.DEBUG) {
			Log.e(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (Constants.DEBUG) {
			Log.i(tag, msg);
		}
	}
}
