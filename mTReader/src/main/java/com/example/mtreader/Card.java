package com.example.mtreader;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import com.example.mtreader.MainActivity;
//import com.example.mtreader.PinPad.PinPadSpinnerSelectenVoictNotice;

public class Card extends Activity {

	private Button btn_cpureset,btn_sendCMD,btn_cpudown;
	private Button btn_opencard,btn_CMDsend,btn_closecard;  
	//private ArrayAdapter<String> adapter;  
	private ViewPager viewPager;//页卡内容
	private ImageView imageView;// 动画图片
	private TextView textView1,textView2,textView3,textView4,textView5,textView6;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private View view1,view2,view3,view4,view5,view6;//各个页卡
	private MainActivity Mactivity;//声明主ACTIVITY对象
	private int st=0;//函数返回状态码
	private EditText Send = null;
	private EditText ContractLessSend = null;
	private Spinner spinner = null;
	private String selected = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card);
		// View contentView = LayoutInflater.from(this.getParent()).inflate(R.layout.lay1, null);
		  //setContentView(R.layout.search_activity);
		//  setContentView(contentView);
		 //AlertDialog.Builder(Card.this)AlertDialog.Builder(this.getParent()) 
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		InitImageView();
		InitTextView();
		InitViewPager();
	}

	private void InitViewPager() {
		viewPager=(ViewPager) findViewById(R.id.vPager);
		views=new ArrayList<View>();
		LayoutInflater inflater=getLayoutInflater();
		view1=inflater.inflate(R.layout.lay1, null);
		view2=inflater.inflate(R.layout.lay2, null);
		view3=inflater.inflate(R.layout.lay3, null);
		view4=inflater.inflate(R.layout.contractcard, null);
	//	view4 = inflater.inflate(R.layout.keyboard, null);
	//	view5 = inflater.inflate(R.layout.layidentify, null);
	//	view6 = inflater.inflate(R.layout.keyboard, null);
		
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
	//	views.add(view4);
	//	views.add(view5);
	//	views.add(view6);
		
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
	}
	 /**
	  *  初始化头标
	  */

	private void InitTextView() {
		textView1 = (TextView) findViewById(R.id.text1);
		textView2 = (TextView) findViewById(R.id.text2);
		textView3 = (TextView) findViewById(R.id.text3);
		textView4 = (TextView) findViewById(R.id.text4);
		//textView4 = (TextView) findViewById(R.id.text4);
		//textView5 = (TextView) findViewById(R.id.text5);
		//textView6 = (TextView) findViewById(R.id.text6);

		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
		textView3.setOnClickListener(new MyOnClickListener(2));
		textView4.setOnClickListener(new MyOnClickListener(3));
	//	textView4.setOnClickListener(new MyOnClickListener(3));
		//textView5.setOnClickListener(new MyOnClickListener(4));
	//	textView6.setOnClickListener(new MyOnClickListener(5));
	}

	/**
	 2      * 初始化动画
	 3 */

	private void InitImageView() {
		imageView= (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}
	
	private void initContractCPUCos()
	{
		spinner=(Spinner)findViewById(R.id.spinner_ContractCPU_COS);
		Send = (EditText)findViewById(R.id.lay1_edit_send);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.ContractCPUCardCOS_arry, 
				android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		/*
		 * 绑定监听器
		 */
		spinner.setOnItemSelectedListener(new ConTractCPUCosSpinnerSelectenVoictNotice());
	}
	
	class ConTractCPUCosSpinnerSelectenVoictNotice implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO 自动生成的方法存根
			System.out.println("COS提示下拉框");
			selected = parent.getItemAtPosition(position).toString();
			System.out.println(selected);
			if(selected.equals("取随机数08"))
				Send.setText("0084000008");
			else if(selected.equals("选择MF"))
				Send.setText("00A40000023F00");
			else if(selected.equals("选择MF返回6117"))
				Send.setText("00C0000017");
			else if(selected.equals("删除MF"))
				Send.setText("800E000008ffffffffffffffff");
			else if(selected.equals("建立MF"))
				Send.setText("80E0000018FFFFFFFFFFFFFFFF0F01315041592E5359532E4444463031");
			else if(selected.equals("建立EF01"))
				Send.setText("80E00200070001000f0f00ff");
			else if(selected.equals("建立EF02"))
				Send.setText("80e00200070002000f0f00ff");							  	
			else if(selected.equals("结束建立MF"))
				Send.setText("80E00001023F00");
			else if(selected.equals("向EF01写74个字节"))
				Send.setText("00D681007400112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233");
			else if(selected.equals("向EF01写F2个字节"))
				Send.setText("00d68100f200112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff1122");
			else if(selected.equals("向EF01写F7个字节"))
				Send.setText("00d68100f7000affffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
			else if(selected.equals("从EF01读76个字节"))
				Send.setText("00B0810076");
			else if(selected.equals("从EF01读FF个字节"))
				Send.setText("00B08100FF");
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO 自动生成的方法存根
			
		}
		
	}
	
	//单选按钮状态实时获取
	private int radiovalue()
	{
		int i =0;String msg="";
		RadioGroup radioGroup;
		radioGroup = (RadioGroup) this.findViewById(R.id.idGroup);
		int radioCount = radioGroup.getChildCount();
		
		RadioButton radioButton;
		
		for (i= 0; i < radioCount; i++) {
			radioButton =(RadioButton) radioGroup.getChildAt(i) ;
			if(radioButton.isChecked()){
				//msg = radioButton.getText().toString();
				break;
			}
		}
		msg=""+i;
		Toast.makeText(Card.this,msg,Toast.LENGTH_SHORT).show();
		return i;
			
	}
	private int lay3radiovalue()
	{
		int i =0;String msg="";
		RadioGroup radioGroup;
		radioGroup = (RadioGroup) this.findViewById(R.id.lay3_radio_Group);
		int radioCount = radioGroup.getChildCount();
		RadioButton radioButton;
		
		for (i= 0; i < radioCount; i++) {
			radioButton =(RadioButton) radioGroup.getChildAt(i) ;
			if(radioButton.isChecked()){
				//msg = radioButton.getText().toString();
				break;
			}
		}
		//msg=""+i;
		//Toast.makeText(Card.this,msg,Toast.LENGTH_SHORT).show();
		return i;
			
	}
	
	public void a_cpureset(View source)//CPU操作卡片命令触发函数
	{
		/*
		String strsend="",data="",tip="";
		byte []szCardNo=new byte[512];
		byte []szName=new byte[512];
		byte []szErrinfo=new byte[1024];

		int nCardType = 0x00;
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //提示信息框
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //返回数据
		
		st=MainActivity.mt8GetJINGRONGICCardNoAndName(nCardType, szCardNo, szName, szErrinfo);
		if(st==0)
		{	
			data=new String("卡号:" + szCardNo + "姓名:" + szName);
			
			Recv.setText(data);
			PUTip.setText("读取金融IC卡卡号及姓名成功");
		}
		else
		{
			
			try
			{
				tip=new String(szErrinfo, "GB2312");//"读取金融IC卡卡号及姓名失败! ";
				Recv.setText("");
				PUTip.setText(tip);
			}
			catch(IOException e)
			{
				Recv.setText("");
				PUTip.setText("读取金融IC卡卡号及姓名信息异常");
				e.printStackTrace();
			}
			
		}
		*/
		
		
		initContractCPUCos();
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //提示信息框
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //返回数据
		
		String tip1="",tip2="",tip="",str="";
		int len=0,v=0,cardtype = 0;
		int ntimeout = 0;
		byte []atr=new byte[200];
		byte []atr_asc=new byte[400];
		byte []atrlen =new byte[100];
		v=radiovalue();
		if(v == 0)//CPU
		{
			cardtype = 0x00;
		}
		else//default sam1
		{
			cardtype = 0x10 + v -1;
		}
		
		st=MainActivity.mt8samsltreset(ntimeout,cardtype,atrlen,atr);
		if(st==0)
		{
			
			len=atrlen[0];
	
			MainActivity.mt8hexasc(atr, atr_asc, len);
			//MainActivity.hex_asc(atr, atr_asc, len);
			tip1=new String(atr_asc);
			Recv.setText(tip1);
			
			//PUTip.setText( "上电复位成功");
			
			
			tip2 = "上电复位成功" + len;
			PUTip.setText(tip2);
			
			
		}
		else
		{
			tip="上电复位失败!" + st;
			Recv.setText("");
			PUTip.setText(tip);
			
		}
		
	}
	public void a_sendCMD(View source)//CPU操作卡片命令触发函数
	{
		String strsend="",data="",tip="";
		byte []send_hex=new byte[512];
		byte []resp_hex=new byte[512];
		byte []resp_asc=new byte[1024];
		int []resplen =new int[10];
		int cmdlen=0,v=0,cardtype = 0;
		
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //提示信息框
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //返回数据
		Send=(EditText) findViewById(R.id.lay1_edit_send); //发送数据 
		
		strsend=Send.getText().toString();
		cmdlen=strsend.length();
		byte send_asc[]=strsend.getBytes();
		MainActivity.mt8aschex(send_asc, send_hex, cmdlen/2);
		v=radiovalue();
		if(v == 0)//CPU
		{
			cardtype = 0x00;
		}
		
		else//default sam1
		{
			cardtype = 0x10 + v -1;
		}
		
		st=MainActivity.mt8cardAPDU(cardtype,cmdlen/2,send_hex,resplen,resp_hex);
		if(st==0)
		{	
			MainActivity.mt8hexasc(resp_hex, resp_asc, resplen[0]);
			data=new String(resp_asc);
			Recv.setText(data);
			PUTip.setText("发送命令成功");
		}
		else
		{
			tip="发送命令失败!";
			Recv.setText("");
			PUTip.setText(tip);
		}
	}
	
	private void initContractlessCPUCos()
	{
		Spinner spinner=(Spinner)findViewById(R.id.spinner_ContractLessCpuCos);
		ContractLessSend = (EditText)findViewById(R.id.lay2_edit_send);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.ContractLessCPUCardCOS_arry, 
				android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		/*
		 * 绑定监听器
		 */
		spinner.setOnItemSelectedListener(new ConTractlessCPUCosSpinnerSelectenVoictNotice());
	}
	
	class ConTractlessCPUCosSpinnerSelectenVoictNotice implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO 自动生成的方法存根
			System.out.println("COS提示下拉框");
			String selected = parent.getItemAtPosition(position).toString();
			System.out.println(selected);
			if(selected.equals("取随机数08"))
				ContractLessSend.setText("0084000008");
			else if(selected.equals("选择MF"))
				ContractLessSend.setText("00A40000023F00");
			else if(selected.equals("选择MF返回6117"))
				ContractLessSend.setText("00C0000017");
			else if(selected.equals("删除DF"))
				ContractLessSend.setText("800E000000");
			//else if(selected.equals("建立MF"))
			//	ContractLessSend.setText("80E0000018FFFFFFFFFFFFFFFF0F01315041592E5359532E4444463031");
			else if(selected.equals("建立EF01"))
				ContractLessSend.setText("80E0000107280C00F0F0FFFF");
			else if(selected.equals("建立EF02"))
				ContractLessSend.setText("80E0000207280C00F0F0FFFF");
			//else if(selected.equals("结束建立MF"))
			//	ContractLessSend.setText("80E00001023F00");
			else if(selected.equals("向EF01写74个字节"))
				ContractLessSend.setText("00D681007400112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233");
			else if(selected.equals("向EF01写F2个字节"))
				ContractLessSend.setText("00d68100f200112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff1122");
			else if(selected.equals("向EF01写F7个字节"))
				ContractLessSend.setText("00d68100f7000affffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
			else if(selected.equals("从EF01读76个字节"))
				ContractLessSend.setText("00B0810076");
			else if(selected.equals("从EF01读FF个字节"))
				ContractLessSend.setText("00B08100FF");
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO 自动生成的方法存根
			
		}
		
	}
	
	public void a_cpudown(View source)//CPU操作卡片命令触发函数
	{
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //提示信息框
		String tip="";
		int cardtype = 0;
		int v=radiovalue();
		if(v == 0)//CPU
		{
			cardtype = 0x00;
		}
		else//default sam1
		{
			cardtype = 0x10 + v -1;
		}
		
		st=MainActivity.mt8samsltpowerdown(cardtype);
		if(st==0)
		{
			PUTip.setText("下电成功");
		}
		else
		{
			tip="下电失败!";
			PUTip.setText(tip);
		}
	}
	public void a_opencard(View source)//非接CPU激活卡片触发函数
	{
		//initContractlessCPUCos();
		
		EditText PUTip=(EditText) findViewById(R.id.lay2_Tip); //提示信息框
		EditText Recv=(EditText) findViewById(R.id.lay2_edit_recv); //返回数据
		
		String tip1="",tip2="",tip="",str="";
		int len=0,v=0;
		int delaytime = 0;
		byte []cardtype=new byte[8];
		byte []snrlen = new byte[8];
		byte []snr =new byte[10];
		byte []cardinfo=new byte[200];
		byte []cardinfo_asc=new byte[400];
		byte []infolen =new byte[10];
		byte []carduidasc = new byte[32];
		
		st=MainActivity.mt8opencard(delaytime,cardtype,snrlen,snr,infolen,cardinfo);
		if(st==0)
		{
			len=infolen[0];
			MainActivity.mt8hexasc(cardinfo, cardinfo_asc, len);
			tip1=new String(cardinfo_asc);
			Recv.setText(tip1);
			
			MainActivity.mt8hexasc(snr, carduidasc, snrlen[0]);
			str=new String(carduidasc);
			tip2="成功激活卡片"+"UID:"+str;
			PUTip.setText(tip2);

		}
		else
		{
			tip="激活卡片失败!";
			Recv.setText("");
			PUTip.setText(tip);
			
		}
		
	}
	public void a_cmdsend(View source)//非接CPU操作卡片命令触发函数
	{
		String strsend="",data="",tip="";
		byte []send_hex=new byte[512];
		byte []resp_hex=new byte[512];
		byte []resp_asc=new byte[1024];
		int []resplen =new int[10];
		int cmdlen=0,v=0;
		EditText PUTip=(EditText) findViewById(R.id.lay2_Tip); //提示信息框
		EditText Recv=(EditText) findViewById(R.id.lay2_edit_recv); //返回数据
		EditText Send=(EditText) findViewById(R.id.lay2_edit_send); //发送数据 
		
		strsend=Send.getText().toString();
		cmdlen=strsend.length();
		byte send_asc[]=strsend.getBytes();
		MainActivity.mt8aschex(send_asc, send_hex, cmdlen/2);
		v=radiovalue();
		st=MainActivity.mt8cardAPDU(0xff,cmdlen/2,send_hex,resplen,resp_hex);
		if(st==0)
		{	
			MainActivity.mt8hexasc(resp_hex, resp_asc, resplen[0]);
			data=new String(resp_asc);
			Recv.setText(data);
			PUTip.setText("发送命令成功");
		}
		else
		{
			tip="发送命令失败! ";
			Recv.setText("");
			PUTip.setText(tip);
		}
	}
	public void a_closecard(View source)//非接CPU关老并闭卡片触发函数
	{
		EditText PUTip=(EditText) findViewById(R.id.lay2_Tip); //提示信息框
		String tip="";
		int delaytime = 0;
		int v=radiovalue();
		st=MainActivity.mt8rfhalt(delaytime);
		if(st==0)
		{
			PUTip.setText("成功关闭卡片");
		}
		else
		{
			tip="关闭卡片失败!";
			
			PUTip.setText(tip);
		}
	}
	
	//获取非接触式IC卡卡号及姓名
	//a_getjingrongicCOntractlesscardnoandname
	public void a_getjingrongicCOntractlesscardnoandname(View source)
	{
		String strsend="",data="",tip="",strname = "";
		byte []szCardNo=new byte[512];
		byte []szName=new byte[512];
		byte []szErrinfo=new byte[1024];

		int nCardType = 0x01;
		EditText PUTip=(EditText) findViewById(R.id.lay2_Tip); //提示信息框
		EditText Recv=(EditText) findViewById(R.id.lay2_edit_recv); //返回数据
		
		st=MainActivity.ReadNAN(nCardType, szCardNo, szName, szErrinfo);
		if(st==0)
		{	
			
			try
			{
				data=new String(szCardNo, "gb2312");
				strname = new String(szName, "gb2312");
				
				Recv.setText("卡号:" + data + "姓名:" + strname);
				PUTip.setText("读取金融IC卡卡号及姓名成功");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}
		else
		{
			try
			{
				tip=new String(szErrinfo, "GB2312");//"读取金融IC卡卡号及姓名失败! ";
				Recv.setText("");
				PUTip.setText(tip);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}
	}
	
	//读取接触式IC卡卡号及姓名
	public void a_getjingrongiccardnoandname(View source)
	{
		String strsend="",data="",tip="", strname = "";
		byte []szCardNo=new byte[512];
		byte []szName=new byte[512];
		byte []szErrinfo=new byte[1024];

		int nCardType = 0x00;
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //提示信息框
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //返回数据
		
		st=MainActivity.ReadNAN(nCardType, szCardNo, szName, szErrinfo);
		if(st==0)
		{
			try
			{
				data=new String(szCardNo, "gb2312");
				strname = new String(szName, "gb2312");
				Recv.setText("卡号:" + data + "姓名:" + strname);
				PUTip.setText("读取金融IC卡卡号及姓名成功");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}
		else
		{
			try
			{
				tip=new String(szErrinfo, "gb2312");//"读取金融IC卡卡号及姓名失败! ";
				Recv.setText("");
				PUTip.setText(tip);
			}
			catch(IOException e)
			{
				Recv.setText("");
				PUTip.setText("读取金融IC卡卡号及姓名信息异常");
				e.printStackTrace();
			}
			
		}
	}
	
	//获取社保卡基本信息
	public void a_getsocialcardbasicinfo(View source)
	{
		String strsend="",data="",tip="";
		
		byte []szSocialCardBasicInfo=new byte[1024];
		byte []szErrinfo=new byte[1024];

		int nCardType = 0x00;
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //提示信息框
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //返回数据
		
		st = MainActivity.ReadSBInfo(szSocialCardBasicInfo, szErrinfo);
		if(st==0)
		{	
			try
			{		
				String StrSocialCardBasicInfo = "";
				StrSocialCardBasicInfo = new String(szSocialCardBasicInfo, "GB2312");
				Recv.setText(StrSocialCardBasicInfo);
				PUTip.setText("读取社保卡基本信息成功");
			}
			catch(IOException e)
			{
				e.printStackTrace();
				Recv.setText("");
				PUTip.setText("读取社保卡基本信息异常");
			}
			
		}
		else
		{
			try
			{
				tip=new String(szErrinfo, "gb2312");//"读取金融IC卡卡号及姓名失败! ";
				Recv.setText("");
				PUTip.setText(tip);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void bt_rfcard(View source)/*以下是M1卡操作触发函数*/
	{
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_CardNo=(EditText) findViewById(R.id.lay3_edit_cardno); //提示信息框
		byte[] snr_asc=new byte[40];
		byte[] snr=new byte[20];
		byte[] cardtype = new byte[8];
		
		int ndelaytime = 0;
		st=MainActivity.mt8rfcard(ndelaytime,cardtype,snr);
		if(st==0)
		{
			MainActivity.mt8hexasc(snr, snr_asc, 4);
			String tip=new String(snr_asc);
			E_CardNo.setText(tip);
			PUTip.setText("寻卡成功");
			
		}
		else
		{
			PUTip.setText("寻卡失败");
		}
	}
	public void bt_rfhalt(View source)
	{
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_CardNo=(EditText) findViewById(R.id.lay3_edit_cardno); //提示信息框
		
		int ndelaytime = 0;
		int st = 0;
				
		st=MainActivity.mt8rfhalt(ndelaytime);
		if(st==0)
		{
			PUTip.setText("HALT成功");
			
		}
		else
		{
			PUTip.setText("HALT失败");
		}
	}
	
	public void bt_rfincrement(View source)
	{
		int addr=0,nSector=0,value=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //提示信息框
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //提示信息框
		EditText E_Valueop=(EditText) findViewById(R.id.lay3_edit_valueop); //提示信息框
		String str_value=E_Valueop.getText().toString();
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty()||str_value.isEmpty())
		{	PUTip.setText("扇区号/块地址/值 不能为空");
			return;
		}
		addr=MainActivity.stringToInt(str_addr);
		nSector=MainActivity.stringToInt(str_section);
		value=MainActivity.stringToInt(str_value);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		} 
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=MainActivity.mt8rfincrement((char)addr,value);
		if(st==0)
		{

			PUTip.setText("块值增加成功");
			
		}
		else
		{
			PUTip.setText("块值增加失败");
		}
	}
	
	public void bt_rfdecrement(View source)
	{
		int addr=0,nSector=0,value=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //提示信息框
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //提示信息框
		EditText E_Valueop=(EditText) findViewById(R.id.lay3_edit_valueop); //提示信息框
		String str_value=E_Valueop.getText().toString();
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty()||str_value.isEmpty())
		{	PUTip.setText("扇区号/块地址/值 不能为空");
			return;
		}
		addr=MainActivity.stringToInt(str_addr);
		nSector=MainActivity.stringToInt(str_section);
		value=MainActivity.stringToInt(str_value);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		} 
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=MainActivity.mt8rfdecrement((char)addr,value);
		if(st==0)
		{

			PUTip.setText("块值减少成功");
			
		}
		else
		{
			PUTip.setText("块值减少失败");
		}
	}
	
	public void bt_rfauth(View source)
	{
		byte[] key_asc=new byte[40];
		byte[] key=new byte[20];
		int addr=0,nSector=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //提示信息框
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //提示信息框
		EditText E_Secrect=(EditText) findViewById(R.id.lay3_edit_secrect); //提示信息框
		int v=lay3radiovalue();
		String str_addr=E_Blockaddr.getText().toString();
		String str_key=E_Secrect.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("扇区号或块地址不能为空");
			return;
		}
		len=(str_key.length()/2);
		key_asc=str_key.getBytes();
		MainActivity.mt8aschex(key_asc, key, len);
		addr=MainActivity.stringToInt(str_addr);
		nSector=MainActivity.stringToInt(str_section);
		
		//MainActivity.dcloadkey((char)0,(char)nSector,key);
		/*
		if (nSector<32)
		{
			addr=nSector*4+addr;
		} 
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		*/
		
		st=MainActivity.mt8rfauthentication((char)v,(char)nSector, key);
		if(st==0)
		{
			PUTip.setText("认证成功");
			
		}
		else
		{
			PUTip.setText("认证失败");
		}
	}
	public void bt_rfread(View source)
	{
		byte[] rdata_asc=new byte[64];
		byte[] rdata=new byte[32];
		int addr=0,nSector=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //提示信息框
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //提示信息框
		EditText E_Read=(EditText) findViewById(R.id.lay3_edit_read); //提示信息框
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("扇区号或块地址不能为空");
			return;
		}
		addr=MainActivity.stringToInt(str_addr);
		nSector=MainActivity.stringToInt(str_section);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		} 
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=MainActivity.mt8rfread((char)addr,rdata);
		if(st==0)
		{
			MainActivity.mt8hexasc(rdata,rdata_asc, 16);
			String str_data=new String(rdata_asc);
			E_Read.setText(str_data);
			PUTip.setText("读数据成功");
			
		}
		else
		{
			E_Read.setText("");
			PUTip.setText("读数据失败");
		}
	}
	public void bt_rfwrite(View source)
	{
		byte[] wdata_asc=new byte[64];
		byte[] wdata=new byte[32];
		int addr=0,nSector=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //提示信息框
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //提示信息框
		EditText E_Write=(EditText) findViewById(R.id.lay3_edit_write); //提示信息框
		String str_wdata=E_Write.getText().toString();
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		wdata_asc=str_wdata.getBytes();
		len=str_wdata.length()/2;
		MainActivity.mt8aschex(wdata_asc, wdata, len);
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("扇区号或块地址不能为空");
			return;
		}
		addr=MainActivity.stringToInt(str_addr);
		nSector=MainActivity.stringToInt(str_section);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		} 
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=MainActivity.mt8rfwrite((char)addr,wdata);
		if(st==0)
		{

			PUTip.setText("写数据成功");
			
		}
		else
		{
			PUTip.setText("写数据失败");
		}
	}
	public void bt_rfreadval(View source)
	{
		int []Ivalue=new int[50];
		int addr=0,nSector=0,value=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //提示信息框
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //提示信息框
		EditText E_Value=(EditText) findViewById(R.id.lay3_edit_blockvalue); //提示信息框
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("扇区号/块地址不能为空");
			return;
		}
		addr=MainActivity.stringToInt(str_addr);
		nSector=MainActivity.stringToInt(str_section);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		} 
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=MainActivity.mt8rfreadval((char)addr,Ivalue);
		
		if(st==0)
		{
			
			value=Ivalue[0];
			String str=""+value;
			E_Value.setText(str);
			PUTip.setText("读块值成功");
			
		}
		else
		{
			E_Value.setText("");
			PUTip.setText("读块值失败");
		}
	}
	public void bt_rfwriteval(View source)
	{
		int addr=0,nSector=0,value=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //提示信息框
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //提示信息框
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //提示信息框
		EditText E_Valueop=(EditText) findViewById(R.id.lay3_edit_valueop); //提示信息框
		String str_value=E_Valueop.getText().toString();
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty()||str_value.isEmpty())
		{	PUTip.setText("扇区号/块地址/值 不能为空");
			return;
		}
		addr=MainActivity.stringToInt(str_addr);
		nSector=MainActivity.stringToInt(str_section);
		value=MainActivity.stringToInt(str_value);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		} 
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=MainActivity.mt8rfinitval((char)addr,value);
		if(st==0)
		{

			PUTip.setText("写块值成功");
			
		}
		else
		{
			PUTip.setText("写块值失败");
		}
	}

	//接触式存储卡
	public void bt_SetCardType(View source)
	{
		int nCardType = 0;
		int st = 0;
		String StrCardType = "";
		
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_contractmemcardexcuteinfo);
		EditText E_SetCardType = (EditText) findViewById(R.id.editText_CardType);
		StrCardType = E_SetCardType.getText().toString();
		
		if (StrCardType.isEmpty())
		{
			E_ExecuteInfo.setText("请输入'设置卡类型',3:4428Card,4:4442Card,8:AT88SC1604Card");
			return;
		}
		
		
		nCardType = MainActivity.stringToInt(StrCardType);
		st = MainActivity.mt8contactsettype(0, nCardType);
		if(st != 0)
		{
			E_ExecuteInfo.setText("设置卡类型失败");
		}
		else
		{
			E_ExecuteInfo.setText("设置卡类型成功");
		}
	}
	
	/*
	 * 识别卡种类
	 */
	public void bt_IdentifyCard(View source)
	{
		byte nCardType[] = new byte[2];
		
		int st = 0;
		String StrCardType = "";
		
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_contractmemcardexcuteinfo);
		EditText E_IdentifyCardType = (EditText) findViewById(R.id.editText_IdentifyCardType);
		EditText E_SetCardType = (EditText) findViewById(R.id.editText_CardType);
		EditText E_PwdData = (EditText) findViewById(R.id.editText_CardPasswd);
		EditText E_PwdLen = (EditText) findViewById(R.id.editText_passwdlen);
		
		nCardType[0] = 0x00;
		st = MainActivity.mt8contactidentifytype(0, nCardType);
		if(st != 0)
		{
			E_ExecuteInfo.setText("识别卡类型失败");
		}
		else
		{
			if(nCardType[0] == 0x03)//4428
			{
				E_IdentifyCardType.setText("4428Card");
				E_SetCardType.setText("3");
				E_PwdData.setText("FFFF");
				E_PwdLen.setText("2");
			}
			else if(nCardType[0] == 0x04)//4442
			{
				E_IdentifyCardType.setText("4442Card");
				E_SetCardType.setText("4");
				E_PwdData.setText("FFFFFF");
				E_PwdLen.setText("3");
			}
			else if(nCardType[0] == 0x08)//1604
			{
				E_IdentifyCardType.setText("AT88SC1604Card");
				E_SetCardType.setText("8");
			}
			else
			{
				E_IdentifyCardType.setText("卡类型未知");
			}
			
			E_ExecuteInfo.setText("识别卡类型成功[3:4428Card,4:4442Card,8:AT88SC1604Card]");
		}
	}
	
	/*
	 * 读数据
	 */
	public void bt_ContractRead(View source)
	{
		byte breadbuf[] = new byte[3073];
		byte breadbufasc[] = new byte[3073];
		int st = 0;
		int noffset = 0;
		int nlength = 0;
		
		String Stroffset = "";
		String Strlength = "";
		
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_contractmemcardexcuteinfo);
		EditText E_offset = (EditText) findViewById(R.id.editText_offset);
		EditText E_length = (EditText) findViewById(R.id.editText_length);
		EditText E_ReadData = (EditText) findViewById(R.id.editText_Data);
		
		
		
		Stroffset = E_offset.getText().toString();
		Strlength = E_length.getText().toString();
		
		if (Stroffset.isEmpty())
		{
			E_ExecuteInfo.setText("请输入'偏移地址'");
			return;
		}
		
		if (Strlength.isEmpty())
		{
			E_ExecuteInfo.setText("请输入'长度'");
			return;
		}
		
		noffset = MainActivity.stringToInt(Stroffset);
		nlength = MainActivity.stringToInt(Strlength);
		
		st = MainActivity.mt8contactread(0, noffset, nlength, breadbuf);
		if(st != 0)
		{
			E_ExecuteInfo.setText("读卡失败");
		}
		else
		{
			MainActivity.mt8hexasc(breadbuf, breadbufasc, nlength);
			String StrReadData = new String(breadbufasc);
			E_ReadData.setText(StrReadData);
			E_ExecuteInfo.setText("读卡成功");
		}
	}
	
	/*
	 * 写数据
	 */
	public void bt_WriteData(View source)
	{
		byte bwritebuf[] = new byte[3073];
		byte bwritebufhex[] = new byte[3073];
		
		int st = 0;
		int noffset = 0;
		int nlength = 0;
		
		String Stroffset = "";
		String Strlength = "";
		String StrWriteData = "";
		
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_contractmemcardexcuteinfo);
		EditText E_offset = (EditText) findViewById(R.id.editText_offset);
		EditText E_length = (EditText) findViewById(R.id.editText_length);
		EditText E_WriteData = (EditText) findViewById(R.id.editText_WriteData);
		
		Stroffset = E_offset.getText().toString();
		Strlength = E_length.getText().toString();
		
		noffset = MainActivity.stringToInt(Stroffset);
		nlength = MainActivity.stringToInt(Strlength);
		
		StrWriteData = E_WriteData.getText().toString();
		if(Stroffset.isEmpty() || Strlength.isEmpty() || StrWriteData.isEmpty() || nlength*2 != StrWriteData.length())
		{
			E_ExecuteInfo.setText("偏移地址、长度、写入数据都不能为空，写入数据长度必须跟长度参数匹配");
			return;
		}
		
		bwritebuf = StrWriteData.getBytes();
		MainActivity.mt8aschex(bwritebuf, bwritebufhex, nlength);
		
		st = MainActivity.mt8contactwrite(0, noffset, nlength, bwritebufhex);
		if(st != 0)
		{
			E_ExecuteInfo.setText("写卡失败");
		}
		else
		{
			E_ExecuteInfo.setText("写卡成功");
		}
	}
	
	/*
	 * 校验密码
	 */
	public void bt_CheckPasswd(View source)
	{
		byte bpin[] = new byte[32];
		byte bpinhex[] = new byte[32];
		
		int st = 0;
		int nPwdlength = 0;
		
		String StrPwdlength = "";
		String StrPwdData = "";
		
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_contractmemcardexcuteinfo);
		EditText E_PwdData = (EditText) findViewById(R.id.editText_CardPasswd);
		EditText E_PwdLen = (EditText) findViewById(R.id.editText_passwdlen);
		
		StrPwdData = E_PwdData.getText().toString();
		StrPwdlength = E_PwdLen.getText().toString();
		
		nPwdlength = MainActivity.stringToInt(StrPwdlength);
		if(StrPwdData.isEmpty() || StrPwdlength.isEmpty() || nPwdlength*2 != StrPwdData.length())
		{
			E_ExecuteInfo.setText("密码长度、密码数据都不能为空，写入密码数据长度必须跟密码长度参数匹配");
			return;
		}
		
		bpin = StrPwdData.getBytes();
		MainActivity.mt8aschex(bpin, bpinhex, nPwdlength);
		
		st = MainActivity.mt8contactpasswordcheck(0, nPwdlength, bpinhex);
		if(st != 0)
		{
			E_ExecuteInfo.setText("校验pin码失败");
		}
		else
		{
			E_ExecuteInfo.setText("校验pin码成功");
		}
	}
	
	/*
	 * 修改密码
	 */
	public void bt_ModefyPasswd(View source)
	{
		byte bpin[] = new byte[32];
		byte bpinhex[] = new byte[32];
		
		int st = 0;
		int nPwdlength = 0;
		
		String StrPwdlength = "";
		String StrPwdData = "";
		
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_contractmemcardexcuteinfo);
		EditText E_PwdData = (EditText) findViewById(R.id.editText_CardPasswd);
		EditText E_PwdLen = (EditText) findViewById(R.id.editText_passwdlen);
		
		StrPwdData = E_PwdData.getText().toString();
		StrPwdlength = E_PwdLen.getText().toString();
		
		nPwdlength = MainActivity.stringToInt(StrPwdlength);
		if(StrPwdData.isEmpty() || StrPwdlength.isEmpty() || nPwdlength*2 != StrPwdData.length())
		{
			E_ExecuteInfo.setText("密码长度、密码数据都不能为空，写入密码数据长度必须跟密码长度参数匹配");
			return;
		}
		
		bpin = StrPwdData.getBytes();
		MainActivity.mt8aschex(bpin, bpinhex, nPwdlength);
		
		st = MainActivity.mt8contactpasswordinit(0, nPwdlength, bpinhex);
		if(st != 0)
		{
			E_ExecuteInfo.setText("修改Pin码失败");
		}
		else
		{
			E_ExecuteInfo.setText("修改Pin码成功");
		}
	}
	

	/////、
	/** 
	 *     
	 * 头标点击监听 3 */
	private class MyOnClickListener implements OnClickListener{
        private int index=0;
        public MyOnClickListener(int i){
        	index=i;
        }
		public void onClick(View v) {
			viewPager.setCurrentItem(index);			
		}
		
	}
	
	
	public class MyViewPagerAdapter extends PagerAdapter{
		private List<View> mListViews;
		
		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) 	{	
			container.removeView(mListViews.get(position));
		}


		@Override
		public Object instantiateItem(ViewGroup container, int position) {	
			 container.addView(mListViews.get(position), 0); 
			 return mListViews.get(position);
		}
		
		@Override
		public int getCount() {			
			return  mListViews.size();
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {			
			return arg0==arg1;
		}
	}

    public class MyOnPageChangeListener implements OnPageChangeListener{

    	int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		public void onPageScrollStateChanged(int arg0) {
			
			
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
			
		}

		public void onPageSelected(int arg0) {
			String strtip;
			Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);
			//Toast.makeText(WeiBoActivity.this, "您选择了"+ viewPager.getCurrentItem()+"页卡", Toast.LENGTH_SHORT).show();
			int num=viewPager.getCurrentItem();
			if(num==0)
			{
				strtip="您可以操作CPU卡";
				initContractCPUCos();
			}
			else if(num==1)
			{	
				strtip="您可以操作非接CPU卡";
				initContractlessCPUCos();
			}
			else if(num == 2)
			{
				strtip="您可以操作M1卡";
			}
			else if (num==3)
			{
				strtip="您可以操作接触存储卡";
			}
		/*	else if(num == 3)
			{
				strtip="您可以操作密码键盘";
				Intent intent = new Intent();
				intent.setClass(Card.this, PinPad.class);
				startActivity(intent);
			}
			else if(num == 4)
			{
				strtip="您可以操作二代证";
				Intent intent = new Intent();
				intent.setClass(Card.this, IdentifyActivity.class);
				startActivity(intent);
			}
			
		else if(num == 4)
			{
				strtip="您可以操作接触式存储卡";
				Intent intent = new Intent();
				intent.setClass(Card.this, ContractMemCard.class);
				startActivity(intent);
			}
			*/
			else
			{
				strtip="您可以选择功能操作";
			}
			
			Toast.makeText(Card.this, strtip, Toast.LENGTH_SHORT).show();
		}
    	
    }

}
