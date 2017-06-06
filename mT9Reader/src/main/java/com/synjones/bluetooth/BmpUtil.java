package com.synjones.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//import com.google.code.microlog4android.Logger;
//import com.google.code.microlog4android.LoggerFactory;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BmpUtil {
	//private static final Logger log = LoggerFactory.getLogger();

	public byte[] bmp2bytes(Bitmap bitmap) {

		int size = bitmap.getWidth() * bitmap.getHeight() * 4;

		ByteArrayOutputStream out = new ByteArrayOutputStream(size);

		try {

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

			out.close();

		} catch (IOException e) {

			e.printStackTrace();
//			log.debug("bmp2bytes error:"+e.getMessage());
		}

		return out.toByteArray();

	}

	public Bitmap getBmpFromCursor(byte[] photo) {
		try {

			return BitmapFactory.decodeByteArray(photo, 0, photo.length);

		} catch (Exception e) {
			//log.debug("getBmpFromCursor error:"+e.getMessage());
			return null;

		}

	}
}
