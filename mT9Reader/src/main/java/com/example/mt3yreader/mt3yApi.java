package com.example.mt3yreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.example.mt3yreader.RootCmd;
import com.example.mt9reader.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class mt3yApi extends Activity {

	private View view1, view2, view3;
	private ViewPager viewPager;
	private PagerTitleStrip pagerTitleStrip;
	private PagerTabStrip pagerTabStrip;
	private List<View> viewList;
	private List<String> titleList;
	private ArrayAdapter<String> adapter;
	//private Button weibo_button;
	private Button btn_operate;//进入卡片操作
	private Button btn_openport;//open port
	private Button btn_closeport;//close port
	private Button btn_trackread;
	private Button btn_Finger;

	//private Button btn_readeprom;// read eeprom
	//private Button btn_writeeprom;// write eeprom
	//private Button btn_cardstate;// read cardstate
	private Button btn_beep; //beep
	//接触CPU卡
	private Button btn_icpoweron;
	private Button btn_iccommand;
	private Button btn_icpoweroff;
	private Intent intent;
	private int dev=0;
	String debugmsg="";
	int fd = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {//重载onCreate方法
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);//使用布局文件
		System.loadLibrary("mt3x32");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //文本输入框默认不获得焦点
		initView();
		//	init();
		//	InitItem();
		UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mt3yApi.this, 0,
				new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);
		while(deviceIterator.hasNext()){
			UsbDevice device = deviceIterator.next();
			//   Log.i(TAG, device.getDeviceName() + " " + Integer.toHexString(device.getVendorId()) +
			//             " " + Integer.toHexString(device.getProductId()));

			debugmsg=device.getDeviceName() + " " + Integer.toHexString(device.getVendorId()) +
					" " + Integer.toHexString(device.getProductId());
			Toast.makeText(mt3yApi.this,debugmsg,Toast.LENGTH_SHORT).show();
			//if(device.getVendorId()==0x23a4&&device.getProductId()==0x020c)
			if(device.getVendorId()==0x23a4&&device.getProductId()==0x0219)
			{
				manager.requestPermission(device, mPermissionIntent);
				UsbDeviceConnection connection = manager.openDevice(device);
				if(connection != null){
					fd  = connection.getFileDescriptor();
					// debugmsg="fd="+fd;
					// Toast.makeText(MainActivity.this,debugmsg,Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}

	}
	//设备操作函数
	public static native int mt8deviceopenfd(int fd);
	public static native int mt8deviceopen(int port,int baud);
	public static native int mt8deviceclose();
	public static native int mt8deviceversion( int module,byte verlen[],byte verdata[]);
	public static native int mt8devicebeep(int delaytime, int times);

	//CPU 卡操作函数
	public static native int mt8samsltreset(int delaytime, int cardno, byte natrlen[],byte atr[]);
	public static native int mt8samsltpowerdown(int cardno);
	public static native int mt8cardAPDU(int cardno, int sendApduLen,byte sendApdu[], int nrecvLen[], byte recvApdu[]);

	//非接CPU卡操作函数
	public static native int mt8opencard(int delaytime,byte cardtype[],byte snrlen[],byte snr[],byte atrlen[],byte atr[]);
	public static native int mt8rfhalt(int delaytime);

	//PBOC 金融IC卡
	//获取金融IC卡卡号、姓名
	public static native int ReadNAN (int nCardType,byte Cardno[],byte CardName[],byte lpErrMsg[]);

	//接触式CPU社保卡
	public static native int ReadSBInfo (byte lpSocialCardBasicinfo[],byte lpErrMsg[]);

	//非接触式CPU社保卡
	public static native int ReadHealthCard (byte Name[], byte Sex[], byte Nation[],byte IDCardNo[], byte Birth[], byte lpErrMsg[]);
	//M1卡操作函数
	public static native int mt8rfcard(int delaytime,byte cardtype[],byte cardID[]);
	public static native int mt8rfauthentication(int mode,int nsecno,byte key[]);
	public static native int mt8rfread(int nblock,byte readdata[]);
	public static native int mt8rfwrite(int nblock,byte writedata[]);
	public static native int mt8rfincrement(int nblock,int incvalue);
	public static native int mt8rfdecrement(int nblock,int decvalue);
	public static native int mt8rfinitval(int nblock,int writevalue);
	public static native int mt8rfreadval(int nblock,int readvalue[]);

	//磁条卡操作
	public static native int mt8magneticreadstart();
	public static native int mt8magneticread(int jtimeout,int jtrack1_len[],int jtrack2_len[],int jtrack3_len[],
											 byte jtrack1_data[],byte jtrack2_data[],byte jtrack3_data[]);

	//接触式存储函数
	public static native int mt8contactsettype(int cardno,int cardtype);
	public static native int mt8contactidentifytype(int cardno,byte cardtype[]);
	public static native int mt8contactpasswordinit(int cardno,int pinlen,byte pinstr[]);
	public static native int mt8contactpasswordcheck(int cardno,int pinlen,byte pinstr[]);
	public static native int mt8contactread(int cardno,int address,int rlen,byte readdata[]);
	public static native int mt8contactwrite(int jcardno,int address,int wlen,byte writedata[]);

	//AT88SC1604
	public static native int mt8srd1604(int zone,int offset,int len,byte read_data_buffer[]);
	public static native int mt8swr1604(int zone,int offset,int len,byte write_data_buffer[]);
	public static native int mt8csc1604(int zone,int len,byte passwd[]);
	public static native int mt8cesc1604(int zone,int len,byte passwd);
	public static native int mt8wsc1604(int zone,int len,byte passwd[]);
	public static native int mt8ser1604(int zone,int offset,int len);
	public static native int mt8fakefus1604(int mode);
	public static native int mt8psnl1604();

	//二代证
	public static native int mt8CLCardOpen(int delaytime,byte cardtype[],byte snrlen[],byte snr[],byte rlen[],byte recdata[]);
	public static native int mt8IDCardRead(
			byte name[],
			byte sex[],
			byte nation[],
			byte birth[],
			byte address[],
			byte idnum[],
			byte asigndepartment[],
			byte datestart[],
			byte dateend[],
			int rphotodatalen[],
			byte photodatainfo[]);

	public static native int mt8IDCardGetCardInfo(int index,byte infodata[],int infodatalen[]);
	public static native int mt8IDCardGetModeID(byte IDLen[],byte sIDData[]);
	public static native int mt8IDCardReadIDNUM(byte rlen[],byte receivedata[]);
	public static native int mt8IDCardReadFinger(
			byte name[],
			byte sex[],
			byte nation[],
			byte birth[],
			byte address[],
			byte idnum[],
			byte asigndepartment[],
			byte datestart[],
			byte dateend[],
			int rphotodatalen[],
			byte photodatainfo[], int fingerdatalen[], byte fingerdata[]);

	//密码键盘
	public static native int mt8keyopen();
	public static native int mt8keyclose();
	public static native int mt8getkeynum(byte status[],byte keynum[]);
	public static native int mt8getkeyplainpin(byte status[],byte keynum[], byte pin[]);
	public static native int mt8downmainkey(byte index, byte enMode,byte keyLen, byte key[]);
	public static native int mt8downpinkey(byte index, byte keyLen, byte key[]);
	public static native int mt8getkeyenpin(byte index, byte cardNo[],int pinLen[], byte pin[]);
	public static native int mt8getkeyversion(int verlen[],byte verdata[]);
	//指纹通道
	public static native int mt8fingersetbaudrate(byte baudrate);
	/* 0:(9600); 1: (19200); 2:(38400); 3: (57600);4:(115200); 5:(128000); 6:(256000);default: (115200);*/
	public static native int mt8fingerchannel(byte mode, int sendLen, byte sendData[], int recvLen[], byte recvData[]);

	//工具函数
	public static native int mt8hexasc(byte hex[],byte asc[],int len);
	public static native int mt8aschex(byte asc[],byte hex[],int len);
	public static native int mt8hexbase64(byte hex[],byte base64[],int hexlen);
	public static native int mt8base64hex(byte base64[],byte hex[],int base64len);
	public static native int mt8rfencrypt(byte key[],byte ptrSource[],int msgLen, byte ptrDest[]);
	public static native int mt8rfdecrypt(byte key[],byte ptrSource[],int msgLen, byte ptrDest[]);
	public static native int WriteBMP(byte pImage[], byte pBmp[], int iWidth, int iHeight);



	private void init()//操作设备文件时需要权限
	{

		RootCmd.execRootCmdSlient(

				"chmod 777 /dev/;"+
						"chmod 777 /dev/bus/;"+
						"chmod 777 /dev/bus/usb/;"+
						"chmod 777 /dev/bus/usb/0*;"+
						"chmod 777 /dev/bus/usb/001/*;"+
						"chmod 777 /dev/bus/usb/002/*;"+
						"chmod 777 /dev/bus/usb/003/*;"+
						"chmod 777 /dev/bus/usb/004/*;"+
						"chmod 777 /dev/bus/usb/005/*;");


		 	/*
	        try {

	        		File file = new File("/etc/udev/rules.d/70-ttyUSB.rules");
	        		if(!file.getParentFile().exists())
	        			file.getParentFile().mkdirs();

	        		file.createNewFile();
	        		FileWriter fw = null;
	        		try {
	        				fw = new FileWriter( "/etc/udev/rules.d/70-ttyUSB.rules",false); // 第二个参数 true 表示写入方式是追加方式
	        				String StrRootFile = "";
	        				StrRootFile = "KERNEL==\"ttyUSB*\", OWNER=\"root\", GROUP=\"root\", MODE=\"0666\"";

	        				fw.write(StrRootFile + "\r\n");
	        			}
	        		catch (Exception e)
	        		{
	        			System.out.println("书写日志发生错误：" + e.toString());
	        		}
	        		finally
	        		{
	        			try{
	        					fw.close();
	        				}

	        			catch (IOException e)
	        			{
	        				// TODO 自动生成 catch 块
	        				e.printStackTrace();
	        			}
	        		}

			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
	        */
	}
	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		pagerTabStrip=(PagerTabStrip) findViewById(R.id.pagertab);
		pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.gold));
		pagerTabStrip.setDrawFullUnderline(false);
		pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.azure));
		pagerTabStrip.setTextSpacing(50);
		view1 = findViewById(R.layout.layout1);
		//view2 = findViewById(R.layout.layout2);
		//view3 = findViewById(R.layout.layout3);
		LayoutInflater lf = getLayoutInflater().from(this);
		view1 = lf.inflate(R.layout.layout1, null);
		//view2 = lf.inflate(R.layout.layout2, null);
		//view3 = lf.inflate(R.layout.layout3, null);

		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(view1);
		//viewList.add(view2);
		//viewList.add(view3);

		titleList = new ArrayList<String>();// 每个页面的Title数据
		titleList.add("MT9");
		//titleList.add("MT8");
		//titleList.add("OTHERS");

		PagerAdapter pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {

				return arg0 == arg1;
			}

			@Override
			public int getCount() {

				return viewList.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
									Object object) {
				container.removeView(viewList.get(position));

			}

			@Override
			public int getItemPosition(Object object) {

				return super.getItemPosition(object);
			}

			@Override
			public CharSequence getPageTitle(int position) {

				return titleList.get(position);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(viewList.get(position));

				btn_operate=(Button) findViewById(R.id.btncardop);
				btn_operate.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						intent=new Intent(mt3yApi.this,Card.class);
						startActivity(intent);
					}
				});
				//创建按钮对象
				btn_openport = (Button)findViewById(R.id.btnopenport);
				btn_closeport= (Button)findViewById(R.id.btncloseport);
				btn_beep     = (Button)findViewById(R.id.btnbeep);
				btn_trackread = (Button)findViewById(R.id.btntrackread);
				btn_Finger = (Button)findViewById(R.id.btn_Finger);

				//CPU 卡
				// btn_icpoweron = (Button)findViewById(R.id.btncpureset);
				//设置监听
				btn_openport.setOnClickListener(new ButtonClickListener());
				btn_closeport.setOnClickListener(new ButtonClickListener());
				//btn_cardstate.setOnClickListener(new ButtonClickListener());
				btn_beep.setOnClickListener(new ButtonClickListener());
				btn_trackread.setOnClickListener(new ButtonClickListener());
				//btn_readeprom.setOnClickListener(new ButtonClickListener());
				//btn_writeeprom.setOnClickListener(new ButtonClickListener());
				// btn_icpoweron.setOnClickListener(new ButtonClickListener());
				btn_Finger.setOnClickListener(new ButtonClickListener());
				return viewList.get(position);
			}

		};
		viewPager.setAdapter(pagerAdapter);
	}
	/*以下是设备操作的触发*/
	class ButtonClickListener implements OnClickListener {
		private byte[] dev_version;

		@Override
		public void onClick(View v) {

			// TODO Auto-generated method stub
			switch (v.getId()) {

				case R.id.btnopenport:
					UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);
					HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
					Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
					PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mt3yApi.this, 0,
							new Intent(ACTION_USB_PERMISSION), 0);
					while(deviceIterator.hasNext()){
						UsbDevice device = deviceIterator.next();
						//   Log.i(TAG, device.getDeviceName() + " " + Integer.toHexString(device.getVendorId()) +
						//             " " + Integer.toHexString(device.getProductId()));

						debugmsg=device.getDeviceName() + " " + Integer.toHexString(device.getVendorId()) +
								" " + Integer.toHexString(device.getProductId());
						Toast.makeText(mt3yApi.this,debugmsg,Toast.LENGTH_SHORT).show();
						//if(device.getVendorId()==0x23a4&&device.getProductId()==0x020c)
						if(device.getVendorId()==0x23a4&&device.getProductId()==0x0219)
						{
							manager.requestPermission(device, mPermissionIntent);
							UsbDeviceConnection connection = manager.openDevice(device);
							if(connection != null){
								fd  = connection.getFileDescriptor();
								// debugmsg="fd="+fd;
								// Toast.makeText(MainActivity.this,debugmsg,Toast.LENGTH_SHORT).show();
							}
							break;
						}
					}
					int dev=mt8deviceopenfd(fd);



					//dev=mt8deviceopen(0,115200);
					if(dev>0)
					{
						str_in="打开设备成功";
						Tip.setText(str_in);

						/*
						String[] StrVersion = new String[]{"","",""};
						StrVersion[0] = new String(dev_version);
						ETversion.setText(StrVersion[0]);
						*/
					}
					else
					{
						str_in="打开设备失败"+dev;
						Tip.setText(str_in);
					}
					break;
				case R.id.btncloseport:

					st=mt8deviceclose();
					if(st==0)
					{
						dev=0;
						str_in="关闭设备成功";
						Tip.setText(str_in);
					}
					else
					{
						str_in="关闭设备失败"+st;
						Tip.setText(str_in);
					}
					break;

				case R.id.btnbeep:

					String Str = "";
					st = mt8devicebeep(258,1);
					if(st == 0)
					{
						str_in="设备BEEP成功";
					}
					else
					{
						str_in="设备BEEP失败" + st;
					}

					System.out.println(Str);
					str_in = Str;

					Tip.setText(str_in);

					break;

				case R.id.btntrackread:
					int st = mt8magneticreadstart();
					if (st!=0)
					{
						str_in="设置读磁条模式失败,st="+st;
						Tip.setText(str_in);
						break;
					}
					int track1len[] = new int[2];
					int track2len[] = new int[2];
					int track3len[] = new int[2];
					byte track1[] = new byte[128];
					byte track2[] = new byte[128];
					byte track3[] = new byte[128];

					String StrExternalTracks[] = new String[]{"","",""};
					int timeout = 10000;

					long time = System.currentTimeMillis();

					while (((System.currentTimeMillis())-time) < timeout)
					{
						st = mt8magneticread(0,track1len,track2len,track3len,track1,track2,track3);
						if(st == 0)
						{
							StrExternalTracks[0] = new String(track1);
							StrExternalTracks[1] = new String(track2);
							StrExternalTracks[2] = new String(track3);

							str_in="磁条卡读取成功" + "\r\n" + "一磁道:" +StrExternalTracks[0] + "\r\n" + "二磁道:" + StrExternalTracks[1] + "\r\n" + "三磁道:" + StrExternalTracks[2] + "\r\n";
							break;
						}
					}
					if (st != 0)
					{
						str_in = "磁条卡读取失败:"+st+ "\r\n";
					}

					Tip.setText(str_in);
					break;

				case R.id.btn_Finger:
					Intent intent = new Intent(mt3yApi.this,FingerActivity.class);
					startActivity(intent);
					break;

			}


		}

		//private final EditText ETwritedata=(EditText)findViewById(R.id.ETwritedata);//写EPROM数据
		//private final EditText ETadrress=(EditText)findViewById(R.id.ETadrress);//EPROM 地址
		//private final EditText ETlength=(EditText)findViewById(R.id.ETlength);//EPROM 地址
		private final EditText Tip=(EditText) findViewById(R.id.layout1_Tip); //提示信息框
		//private final EditText ETversion=(EditText) findViewById(R.id.ETversion);//版本信息框
		//private final EditText ETreaddata=(EditText)findViewById(R.id.ETreaddata);//EEROM信息框

		private String str_in = "";
		public	int st=0;
		public String lenstr;
		public String version;
	}
	public static int stringToInt(String intstr)
	{
		if(intstr.isEmpty())
		{
			return 0;
		}
		Integer integer;
		integer = Integer.valueOf(intstr);
		return integer.intValue();
	}

	 /*
	  * UNICODE转UTF-8
	  */

	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException(
										"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	/**
	 * 把字符串去空格后转换成byte数组。如"37 5a"转成[0x37][0x5A]
	 * @param s
	 * @return
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	/*private void InitItem()
	{
		 String[] m_arr = {"CPU卡","SAM1","SAM2","SAM3"};
		 Spinner spinner;
		 spinner = (Spinner) findViewById(R.id.spinner1);
		 spinner.setPrompt("请选择颜色" );
       //将可选内容与ArrayAdapter连接起来
		 adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m_arr);
		 //设置下拉列表的风格
		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 //将adapter 添加到spinner中
		 spinner.setAdapter(adapter);
		 //添加事件Spinner事件监听
		 spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		 //设置默认值
		 spinner.setVisibility(View.VISIBLE);
	}
	  class SpinnerSelectedListener implements OnItemSelectedListener{

	         public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	                  long arg3) {
	              //view.setText("你的血型是："+m[arg2]);
	        	 Toast.makeText(MainActivity.this, "yes", Toast.LENGTH_SHORT).show();
	         }   
	   
	         public void onNothingSelected(AdapterView<?> arg0) {   
	          }   
	     } */


	private static final String ACTION_USB_PERMISSION =
			"com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if(device != null){
							//call method to set up device communication
							//dev=opendevicefd(fd);
						}

					}
				}
			}
		}
	};
}
