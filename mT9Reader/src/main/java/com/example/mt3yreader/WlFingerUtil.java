package com.example.mt3yreader;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class WlFingerUtil {
	private static final int ERR_SET_BAUD       = -70;

	/*指纹业务*/
	public static int mt8_Wel_REGI_Send(int[] revLen, byte[] revData)
	{
		int stBd = mt3yApi.mt8fingersetbaudrate((byte) 0);
		if (stBd != 0)
		{
			return ERR_SET_BAUD;
		}

		byte[] writeBuf = new byte[20];
		byte[] temp = new byte[10];
		byte[] splitdata = new byte[20];

		temp[0] = 0x00;
		temp[1] = 0x04;
		temp[2] = 0x0B;
		temp[3] = 0;
		temp[4] = 0;
		temp[5] = 0;
		temp[6] = (byte) cr_bcc(6,temp);
		splitFun(temp, (byte)7, splitdata, (byte)14);

		writeBuf[0] = 0x02;
		System.arraycopy(splitdata, 0, writeBuf, 1, 14);
		writeBuf[15] = 0x03;

		return mt3yApi.mt8fingerchannel((byte) 0, 16, writeBuf, revLen, revData);
	}

	public static int mt8_Wel_REGI_Query( int[] revLen, byte[] revData)
	{
		byte[] writeBuf = new byte[10];
		byte[] readBuf = new byte[3072];
		int[] readLen = {0};

		int st = mt3yApi.mt8fingerchannel((byte) 1, 0, writeBuf, readLen, readBuf);
		writeFileToSD("mt8_Wel_REGI_Query mt8fingerchannel return st=" + st);
		if (st != 0)
			return st;
		int st1 = parseWelFingerData(readLen, readBuf, revLen, revData);
		writeFileToSD("mt8_Wel_REGI_Query parseWelFingerData return st1=" + st1);
		return st1;
	}

	public static int mt8_Wel_Samp_Send(int[] revLen, byte[] revData)
	{
		int stBd = mt3yApi.mt8fingersetbaudrate((byte) 0);
		if (stBd != 0)
		{
			return ERR_SET_BAUD;
		}
		byte[] writeBuf = new byte[20];
		byte[] temp = new byte[10];
		byte[] splitdata = new byte[20];

		temp[0] = 0x00;
		temp[1] = 0x04;
		temp[2] = 0x0C;
		temp[3] = 0;
		temp[4] = 0;
		temp[5] = 0;
		temp[6] = (byte) cr_bcc(6,temp);
		splitFun(temp, (byte)7, splitdata, (byte)14);

		writeBuf[0] = 0x02;
		System.arraycopy(splitdata, 0, writeBuf, 1, 14);
		writeBuf[15] = 0x03;

		return mt3yApi.mt8fingerchannel((byte) 0, 16, writeBuf, revLen, revData);
	}

	public static int mt8_Wel_DeviceInfo_Send(int[] revLen, byte[] revData)
	{
		int stBd = mt3yApi.mt8fingersetbaudrate( (byte) 0);
		if (stBd != 0)
		{
			return ERR_SET_BAUD;
		}

		byte[] writeBuf = new byte[20];
		byte[] temp = new byte[10];
		byte[] splitdata = new byte[20];

		temp[0] = 0x00;
		temp[1] = 0x04;
		temp[2] = 9;
		temp[3] = 0;
		temp[4] = 0;
		temp[5] = 0;
		temp[6] = (byte) cr_bcc(6,temp);
		splitFun(temp, (byte)7, splitdata, (byte)14);

		writeBuf[0] = 0x02;
		System.arraycopy(splitdata, 0, writeBuf, 1, 14);
		writeBuf[15] = 0x03;

		return mt3yApi.mt8fingerchannel((byte) 0, 16, writeBuf, revLen, revData);
	}

	public static int mt8_Wel_DeviceInfo_Query(int[] revLen, byte[] revData)
	{
		byte[] writeBuf = new byte[10];

		byte[] readBuf = new byte[200];

		int[] readLen = {0};

		int st = mt3yApi.mt8fingerchannel((byte) 1, 0, writeBuf, revLen, revData);
		return st;

		//return parseWelFingerData(readLen, readBuf, revLen, revData);
	}

	public static int mt8_Wel_GetImage_Send(int[] revLen, byte[] revData)
	{
		int stBd = mt3yApi.mt8fingersetbaudrate( (byte) 0);
		if (stBd != 0)
		{
			return ERR_SET_BAUD;
		}
		byte[] writeBuf = new byte[20];
		byte[] temp = new byte[10];
		byte[] splitdata = new byte[20];

		temp[0] = 0x00;
		temp[1] = 0x04;
		temp[2] = 0x18;
		temp[3] = 0;
		temp[4] = 1;
		temp[5] = 0;
		temp[6] = (byte) cr_bcc(6,temp);

		splitFun(temp, (byte)7, splitdata, (byte)14);

		writeBuf[0] = 0x02;
		System.arraycopy(splitdata, 0, writeBuf, 1, 14);
		writeBuf[15] = 0x03;

		return mt3yApi.mt8fingerchannel( (byte) 0, 16, writeBuf, revLen, revData);
	}

	public static int mt8_Wel_GetImage_Query(fingerPrint finger) {
		// TODO Auto-generated method stub
		final int []recvLen ={0};
		final byte []resp_hex=new byte[30];
		int st1 = mt8_Wel_REGI_Query(recvLen, resp_hex);
		if (st1 == 0)
		{
			if (finger != null)
			{
				int len1 = 0, len2 = 0;
				if(resp_hex[0] < 0)
					len1 = resp_hex[0] + 256;
				else
					len1 = resp_hex[0];
				if(resp_hex[1] < 0)
					len2 = resp_hex[1] + 256;
				else
					len2 = resp_hex[1];
				finger.setWidth(len1 * 256 + len2);
				if(resp_hex[2] < 0)
					len1 = resp_hex[2] + 256;
				else
					len1 = resp_hex[2];
				if(resp_hex[3] < 0)
					len2 = resp_hex[3] + 256;
				else
					len2 = resp_hex[3];
				finger.setHeight(len1 * 256 + len2);
			}
		}
		return st1;
	}



	public static int mt8_Wel_UploadImage_Send(short num, int[] revLen, byte[] revData)
	{
		byte[] writeBuf = new byte[20];
		byte[] temp = new byte[10];
		byte[] splitdata = new byte[20];

		temp[0] = 0x00;
		temp[1] = 0x06;
		temp[2] = 0x19;
		temp[3] = 0;
		temp[4] = 0;
		temp[5] = 0;
		temp[6] = (byte) (num / 256);
		temp[7] = (byte) (num % 256);
		temp[8] = (byte) cr_bcc(8,temp);

		splitFun(temp, (byte)9, splitdata, (byte)18);

		writeBuf[0] = 0x02;
		System.arraycopy(splitdata, 0, writeBuf, 1, 18);
		writeBuf[19] = 0x03;

		return mt3yApi.mt8fingerchannel( (byte) 0, 20, writeBuf, revLen, revData);
	}

	public static int mt8_Wel_Get_Image(int timeout,
										fingerPrint finger) {
		// TODO Auto-generated method stub
		byte[] pBmp = new byte[3073 * 11];
		int st = -1;
		if (finger != null)
		{
			st = mt8_Wel_Get_UploadImage( timeout, finger.getImageBufferData(), finger.getImageBufferLength());
			writeFileToSD("mt8_Wel_Get_UploadImage  return st=" + st);
			if (st == 0)
			{
				writeFileToSD("WriteBMP  pimg size=" + finger.getImageBufferData().length + ",finger.getWidth()= "+finger.getWidth() + ",  finger.getHeight()="+ finger.getHeight());
				st = mt3yApi.WriteBMP(finger.getImageBufferData(), pBmp, finger.getWidth(), finger.getHeight());
				writeFileToSD("WriteBMP  return st=" + st);
			}
		}
		return st;
	}

	public static int mt8_Wel_Get_UploadImage(int timeout,
											  byte[] imageBufferData, int[] imageBufferLength) {
		// TODO Auto-generated method stub
		short num = 0;
		byte[] resp_hex = new byte[3072];
		int[] recvLen = {0};
		boolean isFinish = true;
		int st = 0;

		Arrays.fill(imageBufferData, (byte) 0);
		Arrays.fill(imageBufferLength, 0);
		int stBd = mt3yApi.mt8fingersetbaudrate((byte) 4);
		if (stBd != 0)
		{
			return ERR_SET_BAUD;
		}
		while (isFinish)
		{
			st = mt8_Wel_GetImageByNum(timeout, num, resp_hex, recvLen);
			if (st == 0)
			{
				System.arraycopy(resp_hex, 0, imageBufferData, imageBufferLength[0], recvLen[0]);
				imageBufferLength[0] = imageBufferLength[0] + recvLen[0];

				if (recvLen[0] < 1024)
				{

					mt3yApi.mt8fingersetbaudrate((byte) 0);
					return 0;
				}
				num++;
			}
			else
				break;
		}
		mt3yApi.mt8fingersetbaudrate((byte) 0);
		return st;
	}

	private static int mt8_Wel_GetImageByNum(int timeout,
											 short num, byte[] resp_hex, int[] recvLen) {
		int st = mt8_Wel_UploadImage_Send( num, recvLen, resp_hex);
		long time1 = System.currentTimeMillis();
		if (st == 0)
		{
			Arrays.fill(resp_hex, (byte) 0);
			Arrays.fill(recvLen, 0);
			while(System.currentTimeMillis() - time1 < timeout)
			{
				int st1 = mt8_Wel_REGI_Query(recvLen, resp_hex);
				writeFileToSD("mt8_Wel_REGI_Query  return st1=" + st1);
				if (st1 == 0 || st1 == -0x13 || st1 > 0)
				{
					return st1;
				}
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
		return -1;
	}

	public static void writeFileToSD(String context) {
		//if (!isLog) return;

		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Log.d("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}
		try {
			String pathName = "/sdcard/";
			String fileName = "mtdevicelog";
			File path = new File(pathName);
			File file = new File(pathName + fileName);
			if (!path.exists()) {
				Log.d("TestFile", "Create the path:" + pathName);
				path.mkdir();
			}
			if (!file.exists()) {
				Log.d("TestFile", "Create the file:" + fileName);
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			context = context + "\r\n";
			raf.write(context.getBytes());
			raf.close();

		} catch (Exception e) {
			Log.e("TestFile", "Error on writeFilToSD.");
		}
	}

	public static int parseWelFingerData(int[] readLen, byte[] readBuf,
										 int[] revLen, byte[] revData) {
		// TODO Auto-generated method stub
		byte[] tempBuf = new byte[1524];
		byte[] crcBuf = new byte[1524];
		int[] mergeLen = {0};
		int st = mergeData(readBuf, readLen[0], tempBuf, mergeLen);
		if (st != 0)
			return st;
		int allLen = mergeLen[0];

		if (tempBuf[0] != (byte)0x02)
		{
			return 0x61;
		}
		if (tempBuf[allLen - 1] != (byte)0x03)
		{
			return 0x62;
		}
		int len1 = 0, len2 = 0;
		if(tempBuf[1] < 0)
			len1 = tempBuf[1] + 256;
		else
			len1 = tempBuf[1];
		if(tempBuf[2] < 0)
			len2 = tempBuf[2] + 256;
		else
			len2 = tempBuf[2];
		if (len1 * 256 + len2 + 5 != allLen)
		{
			return 0x64;
		}
		if (tempBuf[3] != 0)
		{
			System.out.println(" tempBuf[3]" + tempBuf[3]);
			return (tempBuf[3]);
		}
		System.arraycopy(tempBuf, 1, crcBuf, 0, allLen-2);
		if((cr_bcc(allLen-2, crcBuf)) != 0)
			return 0x63;
		revLen[0] = allLen - 5 - 2;
		System.arraycopy(tempBuf, 5, revData, 0, revLen[0]);
		return 0;
	}

	private static int mergeData(byte[] readBuf, int allLen, byte[] mergeBuf,
								 int[] mergeLen) {
		// TODO Auto-generated method stub
		byte[] temp = new byte[3072];
		if (allLen < 2)
			return 0x71;
		if (allLen % 2 != 0)
			return 0x70;
		System.arraycopy(readBuf, 1, temp, 0, allLen - 2);
		mergeBuf[0] = readBuf[0];
		int i = 0;
		for(i = 0; i < (allLen-2) / 2; i++)
		{
			mergeBuf[1 + i] = (byte) ((temp[i * 2] - 48) * 16 + temp[i * 2 + 1] - 48);
		}
		mergeBuf[1 + (allLen-2) / 2] = readBuf[allLen - 1];
		mergeLen[0] = 2 + (allLen-2) / 2;
		return 0;
	}


	private static void splitFun(byte usplitdata[], byte ulen, byte splitdata[], byte slen)
	{
		//cmddata
		for(int nI=0,nJ=0;nI<ulen*2;nI=nI+2,nJ++)
		{
			splitdata[nI]=(byte) (((usplitdata[nJ]&0xF0)>>4)+48);
			splitdata[nI+1]=(byte) ((usplitdata[nJ]&0x0F)+48);
		}

	}


	private static int cr_bcc(final int len, final byte[] data )
	{
		int temp=0,i;
		for(i=0;i<len;i++)
			temp=temp^data[i];
		return temp;
	}


}
