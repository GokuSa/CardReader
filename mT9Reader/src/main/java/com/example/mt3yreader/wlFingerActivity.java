package com.example.mt3yreader;



import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mt9reader.R;

public class wlFingerActivity extends Activity {
	private EditText TipCtrl;
	private EditText Delay;
	private int iDelay = 30;
	private fingerPrint finger = new fingerPrint();



	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welfinger);

		TipCtrl = (EditText) findViewById(R.id.editText1);
		Delay = (EditText) findViewById(R.id.editText2);
	}


	public void RegFinger(View source)
	{
		String str="";
		final int st;
		final byte [] AscData1 = new byte[3072];

		final int []recvLen ={0};
		final byte []resp_hex=new byte[3072];

		String Delay_Data;
		Delay_Data = Delay.getText().toString();
		if (Delay_Data.isEmpty()) return;
		iDelay = Integer.valueOf(Delay_Data).intValue();
		CheckDelayTime();

		st = WlFingerUtil.mt8_Wel_REGI_Send(recvLen, resp_hex);

		if(st!=0)
		{
			str = "发送注册指纹指令失败" + st ;//new String(AscData1);
			TipCtrl.setText(str);
		}
		else
		{
			//查询指令
			/*try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			final long time1 = System.currentTimeMillis();
			//new Thread(
//					new Runnable(){
//					public void run()
//					{
			int st1=0;
			while(System.currentTimeMillis() - time1 < iDelay * 1000)
			{
				st1 = WlFingerUtil.mt8_Wel_REGI_Query(recvLen, resp_hex);
				if (st1 == 0)
				{
					mt3yApi.mt8hexasc(resp_hex, AscData1, recvLen[0]);
					Message msg = new Message();
					msg.what = 1;
					msg.obj = new String(AscData1);
					handler.sendMessage(msg);
					return;
				}
//							try {
//								Thread.sleep(10);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
			}
			Message msg = new Message();
			msg.what = 2;
			msg.obj = st1;
			handler.sendMessage(msg);
		}
//			}).start();
//		}

	}


	private void CheckDelayTime() {
		// TODO Auto-generated method stub
		if (iDelay < 15)
		{
			iDelay = 15;
			Delay.setText("15");
			Toast.makeText(this, "超时时间应不小于15s", Toast.LENGTH_LONG).show();
		}
	}


	public void CollectFinger(View source)
	{
		String str="";
		final int st;
		final byte [] AscData1 = new byte[3072];

		final int []recvLen ={0};
		final byte []resp_hex=new byte[3072];

		String Delay_Data;
		Delay_Data = Delay.getText().toString();
		if (Delay_Data.isEmpty()) return;
		iDelay = Integer.valueOf(Delay_Data).intValue();
		CheckDelayTime();

		st = WlFingerUtil.mt8_Wel_Samp_Send(recvLen, resp_hex);

		if(st!=0)
		{
			str = "发送采集指纹指令失败" + st ;//new String(AscData1);
			TipCtrl.setText(str);
		}
		else
		{
			//查询指令
			/*try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			final long time1 = System.currentTimeMillis();
//			new Thread(
//					new Runnable(){
//					public void run()
//					{
			int st1=0;
			while(System.currentTimeMillis() - time1 < iDelay * 1000)
			{
				st1 = WlFingerUtil.mt8_Wel_REGI_Query(recvLen, resp_hex);
				if (st1 == 0)
				{
					mt3yApi.mt8hexasc(resp_hex, AscData1, recvLen[0]);
					Message msg = new Message();
					msg.what = 3;
					msg.obj = new String(AscData1);
					handler.sendMessage(msg);
					return;
				}
//							try {
//								Thread.sleep(10);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
			}
			Message msg = new Message();
			msg.what = 4;
			msg.obj = st1;
			handler.sendMessage(msg);
		}
//			}).start();
//		}
	}


	public void ReadVer(View source)
	{
		String str="";
		final int st;
		final byte [] AscData1 = new byte[3072];

		final int []readLen ={0};
		final byte []readBuf=new byte[3072];

		final int []recvLen ={0};
		final byte []resp_hex=new byte[3072];

		String Delay_Data;
		Delay_Data = Delay.getText().toString();
		if (Delay_Data.isEmpty()) return;
		iDelay = Integer.valueOf(Delay_Data).intValue();
		CheckDelayTime();

		st = WlFingerUtil.mt8_Wel_DeviceInfo_Send(recvLen, resp_hex);

		if(st!=0)
		{
			str = "发送读版本号指令失败" + st ;//new String(AscData1);
			TipCtrl.setText(str);
		}
		else
		{
			//查询指令
			final long time1 = System.currentTimeMillis();
			//new Thread(
			//		new Runnable(){
			//		public void run()
			//		{
			int st1=0;
			while(System.currentTimeMillis() - time1 < iDelay * 1000)
			{
				st1 = WlFingerUtil.mt8_Wel_DeviceInfo_Query(readLen, readBuf);
				if (st1 == 0)
				{
					int st2 = WlFingerUtil.parseWelFingerData(readLen, readBuf, recvLen, resp_hex);
					if (st2 == 0)
					{
						//mt8.hex_asc(resp_hex, AscData1, recvLen[0]);
						Message msg = new Message();
						msg.what = 5;
						msg.obj = new String(resp_hex);
						handler.sendMessage(msg);
					}
					else
					{
						Message msg = new Message();
						msg.what = 6;
						msg.obj = st2;
						handler.sendMessage(msg);
					}

					return;
				}
			}
			Message msg = new Message();
			msg.what = 6;
			msg.obj = st1;
			handler.sendMessage(msg);
			//}
			//}).start();
		}

	}

	public void GetImageUpload(View source)
	{

		String str="";
		int st;
		final byte [] AscData1 = new byte[3072];

		final int []recvLen ={0};
		final byte []resp_hex=new byte[3072];

		String Delay_Data;
		Delay_Data = Delay.getText().toString();
		if (Delay_Data.isEmpty()) return;
		iDelay = Integer.valueOf(Delay_Data).intValue();
		CheckDelayTime();

		st = WlFingerUtil.mt8_Wel_GetImage_Send(recvLen, resp_hex);
		if(st!=0)
		{
			str = "发送采集指纹图像指令失败" + st ;//new String(AscData1);
			TipCtrl.setText(str);
		}
		else
		{
			//查询指令
			/*try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			final long time1 = System.currentTimeMillis();
//			new Thread(
//					new Runnable(){
//					public void run()
//					{
			int st1=0;
			while(System.currentTimeMillis() - time1 < iDelay * 1000)
			{
				st1 = WlFingerUtil.mt8_Wel_GetImage_Query(finger);
				if (st1 == 0)
				{
					break;
				}
//							try {
//								Thread.sleep(10);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
			}
			if (st1 == 0)
			{
				st = WlFingerUtil.mt8_Wel_Get_Image( iDelay * 1000, finger);
				if (st == 0)
				{
					Message msg = new Message();
					msg.what = 7;
					msg.obj = System.currentTimeMillis() - time1;
					handler.sendMessage(msg);

				}
				else
				{
					Message msg = new Message();
					msg.what = 4;
					msg.obj = st;
					handler.sendMessage(msg);

				}
				return;
			}
			///
			Message msg = new Message();
			msg.what = 4;
			msg.obj = st1;
			handler.sendMessage(msg);
		}
//			}).start();
//		}
	}


	final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			String str_in;
			int st = 0;
			super.handleMessage(msg);
			//handler处理消息
			if(msg.what==1)
			{
				str_in=(String)msg.obj;
				TipCtrl.setText("指纹特征码:\r\n"+str_in);
				Toast.makeText(wlFingerActivity.this, "注册指纹成功！", Toast.LENGTH_LONG).show();
			}
			else if (msg.what == 2)
			{
				st = (Integer) msg.obj;
				TipCtrl.setText("注册指纹失败！["+st+"]");
			}
			else if (msg.what == 3)
			{
				str_in=(String)msg.obj;
				TipCtrl.setText("指纹特征码:\r\n"+str_in);
				Toast.makeText(wlFingerActivity.this, "采集指纹成功！", Toast.LENGTH_LONG).show();
			}
			else if (msg.what == 4)
			{
				st = (Integer) msg.obj;
				if (st < 0)
					TipCtrl.setText("采集指纹失败！["+String.format("-0x%02x",-st)+"]");
				else
					TipCtrl.setText("采集指纹失败！["+String.format("0x%02x",st)+"]");
			}
			else if (msg.what == 5)
			{
				str_in=(String)msg.obj;
				TipCtrl.setText("版本号:\r\n"+str_in);
				Toast.makeText(wlFingerActivity.this, "读版本号成功！", Toast.LENGTH_LONG).show();
			}
			else if (msg.what == 6)
			{
				st = (Integer) msg.obj;
				TipCtrl.setText("读版本号失败！["+String.format("-0x%02x",-st)+"]");
			}
			else if (msg.what == 7)
			{
				Bitmap bmFinger = BitmapFactory.decodeFile("/sdcard/finger.bmp");
				ImageView imageViewFinger = (ImageView) findViewById(R.id.imageView);
				Matrix matrix = new Matrix();
				matrix.postRotate(180);
				Bitmap rotFinger = Bitmap.createBitmap(bmFinger, 0, 0, bmFinger.getWidth(), bmFinger.getHeight(), matrix, true);
				imageViewFinger.setImageBitmap(rotFinger);
				TipCtrl.setText("采集指纹图像成功!用时:[" + (Long)(msg.obj) + "ms]");
			}
		}
	};


}
