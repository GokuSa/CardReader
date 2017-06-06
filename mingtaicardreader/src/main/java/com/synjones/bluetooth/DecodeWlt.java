package com.synjones.bluetooth;

import android.os.Build;

public class DecodeWlt {
	public native int Wlt2Bmp(String wltPath, String bmpPath);
	static{
		if (Build.VERSION.SDK_INT == 23) {
			System.loadLibrary("DecodeWlt64");
		}else{
			System.loadLibrary("DecodeWlt32");
		}
	}

}
