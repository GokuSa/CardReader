package com.example.mt3yreader;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
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

import com.example.mt9reader.R;
import com.synjones.bluetooth.BmpUtil;
import com.synjones.bluetooth.DecodeWlt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private mt3yApi Mactivity;//声明主ACTIVITY对象
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
		view4 = inflater.inflate(R.layout.layidentify, null);
		view5 = inflater.inflate(R.layout.contractcard, null);
		view6 = inflater.inflate(R.layout.keyboard, null);

		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		views.add(view5);
		views.add(view6);

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
		textView5 = (TextView) findViewById(R.id.text5);
		textView6 = (TextView) findViewById(R.id.text6);

		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
		textView3.setOnClickListener(new MyOnClickListener(2));
		textView4.setOnClickListener(new MyOnClickListener(3));
		textView5.setOnClickListener(new MyOnClickListener(4));
		textView6.setOnClickListener(new MyOnClickListener(5));
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
		offset = (screenW / 6 - bmpW) / 2;// 计算偏移量
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

		st=mt3yApi.mt8samsltreset(ntimeout,cardtype,atrlen,atr);
		if(st==0)
		{

			len=atrlen[0];

			mt3yApi.mt8hexasc(atr, atr_asc, len);
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
		mt3yApi.mt8aschex(send_asc, send_hex, cmdlen/2);
		v=radiovalue();
		if(v == 0)//CPU
		{
			cardtype = 0x00;
		}

		else//default sam1
		{
			cardtype = 0x10 + v -1;
		}

		st=mt3yApi.mt8cardAPDU(cardtype,cmdlen/2,send_hex,resplen,resp_hex);
		if(st==0)
		{
			mt3yApi.mt8hexasc(resp_hex, resp_asc, resplen[0]);
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

		st=mt3yApi.mt8samsltpowerdown(cardtype);
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

		st=mt3yApi.mt8opencard(delaytime,cardtype,snrlen,snr,infolen,cardinfo);
		if(st==0)
		{
			len=infolen[0];
			mt3yApi.mt8hexasc(cardinfo, cardinfo_asc, len);
			tip1=new String(cardinfo_asc);
			Recv.setText(tip1);

			mt3yApi.mt8hexasc(snr, carduidasc, snrlen[0]);
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
		mt3yApi.mt8aschex(send_asc, send_hex, cmdlen/2);
		v=radiovalue();
		st=mt3yApi.mt8cardAPDU(0xff,cmdlen/2,send_hex,resplen,resp_hex);
		if(st==0)
		{
			mt3yApi.mt8hexasc(resp_hex, resp_asc, resplen[0]);
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
		st=mt3yApi.mt8rfhalt(delaytime);
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

		st=mt3yApi.ReadNAN(nCardType, szCardNo, szName, szErrinfo);
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


		st=mt3yApi.ReadNAN(nCardType, szCardNo, szName, szErrinfo);
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

		st = mt3yApi.ReadSBInfo(szSocialCardBasicInfo, szErrinfo);
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
		st=mt3yApi.mt8rfcard(ndelaytime,cardtype,snr);
		if(st==0)
		{
			mt3yApi.mt8hexasc(snr, snr_asc, 4);
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

		st=mt3yApi.mt8rfhalt(ndelaytime);
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
		addr=mt3yApi.stringToInt(str_addr);
		nSector=mt3yApi.stringToInt(str_section);
		value=mt3yApi.stringToInt(str_value);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		}
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=mt3yApi.mt8rfincrement((char)addr,value);
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
		addr=mt3yApi.stringToInt(str_addr);
		nSector=mt3yApi.stringToInt(str_section);
		value=mt3yApi.stringToInt(str_value);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		}
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=mt3yApi.mt8rfdecrement((char)addr,value);
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
		mt3yApi.mt8aschex(key_asc, key, len);
		addr=mt3yApi.stringToInt(str_addr);
		nSector=mt3yApi.stringToInt(str_section);

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

		st=mt3yApi.mt8rfauthentication((char)v,(char)nSector, key);
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
		addr=mt3yApi.stringToInt(str_addr);
		nSector=mt3yApi.stringToInt(str_section);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		}
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=mt3yApi.mt8rfread((char)addr,rdata);
		if(st==0)
		{
			mt3yApi.mt8hexasc(rdata,rdata_asc, 16);
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
		mt3yApi.mt8aschex(wdata_asc, wdata, len);
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("扇区号或块地址不能为空");
			return;
		}
		addr=mt3yApi.stringToInt(str_addr);
		nSector=mt3yApi.stringToInt(str_section);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		}
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=mt3yApi.mt8rfwrite((char)addr,wdata);
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
		addr=mt3yApi.stringToInt(str_addr);
		nSector=mt3yApi.stringToInt(str_section);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		}
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=mt3yApi.mt8rfreadval((char)addr,Ivalue);

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
		addr=mt3yApi.stringToInt(str_addr);
		nSector=mt3yApi.stringToInt(str_section);
		value=mt3yApi.stringToInt(str_value);
		if (nSector<32)
		{
			addr=nSector*4+addr;
		}
		else
		{
			addr=(nSector-32)*16+addr+128;
		}
		st=mt3yApi.mt8rfinitval((char)addr,value);
		if(st==0)
		{

			PUTip.setText("写块值成功");

		}
		else
		{
			PUTip.setText("写块值失败");
		}
	}

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
			else if(num == 3)
			{
				strtip="您可以操作二代证";
//				Intent intent = new Intent();
//				intent.setClass(Card.this, IdentifyActivity.class);
//				startActivity(intent);
			}
			else if(num == 4)
			{
				strtip="您可以操作接触式存储卡";
//				Intent intent = new Intent();
//				intent.setClass(Card.this, ContractMemCard.class);
//				startActivity(intent);
			}
			else if(num == 5)
			{
				strtip="您可以操作密码键盘";
//				Intent intent = new Intent();
//				intent.setClass(Card.this, PinPad.class);
//				startActivity(intent);
			}
			else
			{
				strtip="您可以选择功能操作";
			}

			Toast.makeText(Card.this, strtip, Toast.LENGTH_SHORT).show();
		}

	}

	/////二代证
	int result = 0;
	/*
     * 获取性别信息
     */
	public String getsexinfo(byte bsex[])
	{
		String StrSexInfo = "";
		if(bsex[0] == 0x30)
		{
			StrSexInfo = "未知";
		}
		else if(bsex[0] == 0x31)
		{
			StrSexInfo = "男";
		}
		else if(bsex[0] == 0x32)
		{
			StrSexInfo = "女";
		}
		else if(bsex[0] == 0x39)
		{
			StrSexInfo = "未说明";
		}
		else
		{
			StrSexInfo = " ";
		}

		return StrSexInfo;
	}


	/*
	 * 获取名族信息
	 */
	public String getnation(byte bNationinfo[])
	{
		String StrNation = "";
		int nNationNo = 0;

		int nationcode = 0;
		nNationNo = (bNationinfo[0]-0x30)*10 + bNationinfo[2]-0x30;
		switch(nNationNo)
		{
			case 1:
				StrNation = "汉";
				break;
			case 2:
				StrNation = "蒙古";
				break;
			case 3:
				StrNation = "回";
				break;
			case 4:
				StrNation = "藏";
				break;
			case 5:
				StrNation = "维吾尔";
				break;
			case 6:
				StrNation = "苗";
				break;
			case 7:
				StrNation = "彝";
				break;
			case 8:
				StrNation = "壮";
				break;
			case 9:
				StrNation = "布依";
				break;
			case 10:
				StrNation = "朝鲜";
				break;
			case 11:
				StrNation = "满";
				break;
			case 12:
				StrNation = "侗";
				break;
			case 13:
				StrNation = "瑶";
				break;
			case 14:
				StrNation = "白";
				break;
			case 15:
				StrNation = "土家";
				break;
			case 16:
				StrNation = "哈尼";
				break;
			case 17:
				StrNation = "哈萨克";
				break;
			case 18:
				StrNation = "傣";
				break;
			case 19:
				StrNation = "黎";
				break;
			case 20:
				StrNation = "傈僳";
				break;
			case 21:
				StrNation = "佤";
				break;
			case 22:
				StrNation = "畲";
				break;
			case 23:
				StrNation = "高山";
				break;
			case 24:
				StrNation = "拉祜";
				break;
			case 25:
				StrNation = "水";
				break;
			case 26:
				StrNation = "东乡";
				break;
			case 27:
				StrNation = "纳西";
				break;
			case 28:
				StrNation = "景颇";
				break;
			case 29:
				StrNation = "柯尔克孜";
				break;
			case 30:
				StrNation = "土";
				break;
			case 31:
				StrNation = "达斡尔";
				break;
			case 32:
				StrNation = "仫佬";
				break;
			case 33:
				StrNation = "羌";
				break;
			case 34:
				StrNation = "布朗";
				break;
			case 35:
				StrNation = "撒拉";
				break;
			case 36:
				StrNation = "毛南";
				break;
			case 37:
				StrNation = "仡佬";
				break;
			case 38:
				StrNation = "锡伯";
				break;
			case 39:
				StrNation = "阿昌";
				break;
			case 40:
				StrNation = "普米";
				break;
			case 41:
				StrNation = "塔吉克";
				break;
			case 42:
				StrNation = "怒";
				break;
			case 43:
				StrNation = "乌孜别克";
				break;
			case 44:
				StrNation = "俄罗斯";
				break;
			case 45:
				StrNation = "鄂温克";
				break;
			case 46:
				StrNation = "德昂";
				break;
			case 47:
				StrNation = "保安";
				break;
			case 48:
				StrNation = "裕固";
				break;
			case 49:
				StrNation = "京";
				break;
			case 50:
				StrNation = "塔塔尔";
				break;
			case 51:
				StrNation = "独龙";
				break;
			case 52:
				StrNation = "鄂伦春";
				break;
			case 53:
				StrNation = "赫哲";
				break;
			case 54:
				StrNation = "门巴";
				break;
			case 55:
				StrNation = "珞巴";
				break;
			case 56:
				StrNation = "基诺";
				break;
			case 57:
				StrNation = "其他";
				break;
			case 58:
				StrNation = "外国血统中国籍人士";
				break;
			default:
				StrNation = " ";
				break;
		}
		return StrNation;
	}

	private void displayIDCard(String bmpPath) {

		Bitmap bmp = null;
		TextView tv;
		int i = 0;
		ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
		if (i == 0) {

			try {

				if (result == 1) {
					File f = new File(bmpPath);
					if (bmp != null) {
						if (!bmp.isRecycled()) {
							bmp.recycle();
						}
						bmp = null;
					}

					if (f.exists()) {
						bmp = BitmapFactory.decodeFile(bmpPath);
						BmpUtil bu = new BmpUtil();
						imageViewPhoto.setImageBitmap(bmp);
					} else {
						Resources res = getResources();
						bmp = BitmapFactory.decodeResource(res,
								R.drawable.photo);
						imageViewPhoto.setImageBitmap(bmp);
					}
				} else {
					Resources res = getResources();
					bmp = BitmapFactory.decodeResource(res, R.drawable.photo);
					imageViewPhoto.setImageBitmap(bmp);
				}
				System.gc();

			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		} else {

			imageViewPhoto.setImageResource(R.drawable.photo);
		}

	}

	public int bt_IdentifyRead(View source)
	{

		int nstatus = 0;
		int st = 0;
		int nRecLen[] = new int[8];
		byte szName[] = new byte[128];
		byte szSex[] = new byte[128];
		byte szNation[] = new byte[128];
		byte szBirth[] = new byte[128];
		byte szAddress[] = new byte[128];
		byte szIDNo[] = new byte[36];
		byte szDepartment[] = new byte[128];
		byte szDateStart[] = new byte[128];
		byte szDateEnd[] = new byte[128];
		byte szExpir[] = new byte[128];
		byte message[] = new byte[128];
		byte szdata[] = new byte[3072];

		int nNamelen[] = new int[2];
		int nSexlen[] = new int[2];
		int nNationlen[] = new int[2];
		int nBirthlen[] = new int[2];
		int nAddresslen[] = new int[2];
		int nIDNolen[] = new int[2];
		int nDepartmentlen[] = new int[2];
		int nDateStartlen[] = new int[2];
		int nDateEndlen[] = new int[2];
		int nExpirlen[] = new int[2];


		String StrErrMsg = "",StrName = "",StrSex = "",StrNation = "",StrBirth = "",StrAddress = "",StrDepartment = "";
		String StrDateStart = "",StrDateEnd = "",StrExpir = "",StrIDNo = "";
		EditText PUTip=(EditText) findViewById(R.id.editText_idcexecuteinfo); //提示信息框
		EditText EditName=(EditText) findViewById(R.id.editText_Name);
		EditText EditSex=(EditText) findViewById(R.id.editText_Sex);
		EditText EditNation=(EditText) findViewById(R.id.editText_Nation);
		EditText EditBirth=(EditText) findViewById(R.id.editText_Birth);

		EditText EditAddress=(EditText) findViewById(R.id.editText_Address);
		EditText EditIDNo=(EditText) findViewById(R.id.editText_IDNo);
		EditText EditDepartment=(EditText) findViewById(R.id.editText_Department);
		EditText EditDateStart=(EditText) findViewById(R.id.editText_DateStart);
		EditText EditDateEnd=(EditText) findViewById(R.id.editText_DateEnd);
		//EditText EditExpir=(EditText) findViewById(R.id.editText_Name);
		EditText Editmessage=(EditText) findViewById(R.id.editText_idcexecuteinfo);

		EditName.setText(StrName);
		EditSex.setText(StrSex);
		EditNation.setText(StrNation);
		EditBirth.setText(StrBirth);
		EditAddress.setText(StrAddress);
		EditIDNo.setText(StrIDNo);
		EditDepartment.setText(StrDepartment);
		EditDateStart.setText(StrDateStart);
		EditDateEnd.setText(StrDateEnd);

		String StrWltFilePath = "";
		String StrBmpFilePath = "";
		st = mt3yApi.mt8IDCardRead(
				szName,
				szSex,
				szNation,
				szBirth,
				szAddress,
				szIDNo,
				szDepartment,
				szDateStart,
				szDateEnd,
				nRecLen,
				szdata);

		if(st != 0)
		{
			result = -1;
			displayIDCard(StrBmpFilePath);//显示照片
			StrErrMsg = new String("身份证读卡失败["+ st + "]");
			PUTip.setText(StrErrMsg);
			return -100;
		}
		else
		{
			try
			{
				StrName = new String(szName,"UTF-16LE");
				EditName.setText(StrName);

				StrSex = getsexinfo(szSex);
				EditSex.setText(StrSex);

				EditNation.setText(getnation(szNation));
				StrBirth = new String(szBirth,"UTF-16LE");
				EditBirth.setText(StrBirth);


				StrAddress = new String(szAddress,"UTF-16LE");
				EditAddress.setText(StrAddress);

				StrIDNo = new String(szIDNo,"UTF-16LE");
				EditIDNo.setText(StrIDNo);

				StrDepartment = new String(szDepartment,"UTF-16LE");
				EditDepartment.setText(StrDepartment);

				StrDateStart = new String(szDateStart,"UTF-16LE");
				EditDateStart.setText(StrDateStart);

				StrDateEnd = new String(szDateEnd,"UTF-16LE");
				EditDateEnd.setText(StrDateEnd);



				StrWltFilePath =  getFileStreamPath("photo.wlt").getAbsolutePath();
				StrBmpFilePath =  getFileStreamPath("photo.bmp").getAbsolutePath();

				File wltFile = new File(StrWltFilePath);
				FileOutputStream fos = new FileOutputStream(wltFile);
				fos.write(szdata,0,nRecLen[0]);
				fos.close();

				DecodeWlt dw = new DecodeWlt();
				result = dw.Wlt2Bmp(StrWltFilePath, StrBmpFilePath);
				if(result == 1)
				{
					displayIDCard(StrBmpFilePath);//显示照片
					PUTip.setText("照片解码成功");
					return 0;
				}
				else
				{
					displayIDCard(StrBmpFilePath);//显示照片
					PUTip.setText("照片解码失败");
					return -300;
				}


			}catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				PUTip.setText("照片解码异常");
				return -200;
			}

		}


	}


	public void bt_IDReadFinger(View source)
	{
		int st = 0;
		int nRecLen[] = new int[8];
		int nfingerLen[] = new int[2];
		byte szName[] = new byte[128];
		byte szSex[] = new byte[128];
		byte szNation[] = new byte[128];
		byte szBirth[] = new byte[128];
		byte szAddress[] = new byte[128];
		byte szIDNo[] = new byte[36];
		byte szDepartment[] = new byte[128];
		byte szDateStart[] = new byte[128];
		byte szDateEnd[] = new byte[128];
		byte szdata[] = new byte[3072];
		byte fingerdata[] = new byte[3072];

		String StrErrMsg = "",StrName = "",StrSex = "",StrNation = "",StrBirth = "",StrAddress = "",StrDepartment = "";
		String StrDateStart = "",StrDateEnd = "",StrIDNo = "";
		EditText PUTip=(EditText) findViewById(R.id.editText_idcexecuteinfo); //提示信息框
		EditText EditName=(EditText) findViewById(R.id.editText_Name);
		EditText EditSex=(EditText) findViewById(R.id.editText_Sex);
		EditText EditNation=(EditText) findViewById(R.id.editText_Nation);
		EditText EditBirth=(EditText) findViewById(R.id.editText_Birth);

		EditText EditAddress=(EditText) findViewById(R.id.editText_Address);
		EditText EditIDNo=(EditText) findViewById(R.id.editText_IDNo);
		EditText EditDepartment=(EditText) findViewById(R.id.editText_Department);
		EditText EditDateStart=(EditText) findViewById(R.id.editText_DateStart);
		EditText EditDateEnd=(EditText) findViewById(R.id.editText_DateEnd);
		EditName.setText(StrName);
		EditSex.setText(StrSex);
		EditNation.setText(StrNation);
		EditBirth.setText(StrBirth);
		EditAddress.setText(StrAddress);
		EditIDNo.setText(StrIDNo);
		EditDepartment.setText(StrDepartment);
		EditDateStart.setText(StrDateStart);
		EditDateEnd.setText(StrDateEnd);

		String StrWltFilePath = "";
		String StrBmpFilePath = "";


		st = mt3yApi.mt8IDCardReadFinger(
				szName,
				szSex,
				szNation,
				szBirth,
				szAddress,
				szIDNo,
				szDepartment,
				szDateStart,
				szDateEnd,
				nRecLen,
				szdata,
				nfingerLen, fingerdata);

		if(st != 0)
		{
			result = -1;
			displayIDCard(StrBmpFilePath);//显示照片
			StrErrMsg = new String("身份证读卡失败["+ st + "]");
			PUTip.setText(StrErrMsg);
			return;
		}
		else
		{
			try
			{
				StrName = new String(szName,"UTF-16LE");
				EditName.setText(StrName);

				StrSex = getsexinfo(szSex);
				EditSex.setText(StrSex);

				EditNation.setText(getnation(szNation));
				StrBirth = new String(szBirth,"UTF-16LE");
				EditBirth.setText(StrBirth);

				StrAddress = new String(szAddress,"UTF-16LE");
				EditAddress.setText(StrAddress);

				StrIDNo = new String(szIDNo,"UTF-16LE");
				EditIDNo.setText(StrIDNo);

				StrDepartment = new String(szDepartment,"UTF-16LE");
				EditDepartment.setText(StrDepartment);

				StrDateStart = new String(szDateStart,"UTF-16LE");
				EditDateStart.setText(StrDateStart);

				StrDateEnd = new String(szDateEnd,"UTF-16LE");
				EditDateEnd.setText(StrDateEnd);

				StrWltFilePath =  getFileStreamPath("photo.wlt").getAbsolutePath();
				StrBmpFilePath =  getFileStreamPath("photo.bmp").getAbsolutePath();

				File wltFile = new File(StrWltFilePath);
				FileOutputStream fos = new FileOutputStream(wltFile);
				fos.write(szdata,0,nRecLen[0]);
				fos.close();

				DecodeWlt dw = new DecodeWlt();
				result = dw.Wlt2Bmp(StrWltFilePath, StrBmpFilePath);

				byte szFingerAsc[] = new byte[3072];
				mt3yApi.mt8hexasc(fingerdata, szFingerAsc , nfingerLen[0]);
				if(result == 1)
				{
					displayIDCard(StrBmpFilePath);//显示照片
					PUTip.setText("照片解码成功\r\n" + "指纹特征:\n" + new String(szFingerAsc));
					return;
				}
				else
				{
					displayIDCard(StrBmpFilePath);//显示照片
					PUTip.setText("照片解码失败");
					return;
				}


			}catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				PUTip.setText("照片解码异常");
				return;
			}

		}

	}
	///////////////
	/*
	 * 设置卡种类
	 */
	public void bt_SetCardType(View source)
	{
		int nCardType = 0;
		int st = 0;
		String StrCardType = "";

		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_contractmemcardexcuteinfo);
		EditText E_SetCardType = (EditText) findViewById(R.id.editText_CardType);
		StrCardType = E_SetCardType.getText().toString();

		nCardType = mt3yApi.stringToInt(StrCardType);
		st = mt3yApi.mt8contactsettype(0, nCardType);
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

		nCardType[0] = 0x00;
		st = mt3yApi.mt8contactidentifytype(0, nCardType);
		if(st != 0)
		{
			E_ExecuteInfo.setText("识别卡类型失败");
		}
		else
		{
			if(nCardType[0] == 0x03)//4428
			{
				E_IdentifyCardType.setText("4428Card");
			}
			else if(nCardType[0] == 0x04)//4442
			{
				E_IdentifyCardType.setText("4442Card");
			}
			else if(nCardType[0] == 0x08)//1604
			{
				E_IdentifyCardType.setText("AT88SC1604Card");
			}
			else
			{
				E_IdentifyCardType.setText("卡类型未知");
			}

			E_ExecuteInfo.setText("识别卡类型成功");
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

		noffset = mt3yApi.stringToInt(Stroffset);
		nlength = mt3yApi.stringToInt(Strlength);

		st = mt3yApi.mt8contactread(0, noffset, nlength, breadbuf);
		if(st != 0)
		{
			E_ExecuteInfo.setText("读卡失败");
		}
		else
		{
			mt3yApi.mt8hexasc(breadbuf, breadbufasc, nlength);
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

		noffset = mt3yApi.stringToInt(Stroffset);
		nlength = mt3yApi.stringToInt(Strlength);

		StrWriteData = E_WriteData.getText().toString();
		if(Stroffset.isEmpty() || Strlength.isEmpty() || StrWriteData.isEmpty() || nlength*2 != StrWriteData.length())
		{
			E_ExecuteInfo.setText("偏移地址、长度、写入数据都不能为空，写入数据长度必须跟长度参数匹配");
			return;
		}

		bwritebuf = StrWriteData.getBytes();
		mt3yApi.mt8aschex(bwritebuf, bwritebufhex, nlength);

		st = mt3yApi.mt8contactwrite(0, noffset, nlength, bwritebufhex);
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

		nPwdlength = mt3yApi.stringToInt(StrPwdlength);
		if(StrPwdData.isEmpty() || StrPwdlength.isEmpty() || nPwdlength*2 != StrPwdData.length())
		{
			E_ExecuteInfo.setText("密码长度、密码数据都不能为空，写入密码数据长度必须跟密码长度参数匹配");
			return;
		}

		bpin = StrPwdData.getBytes();
		mt3yApi.mt8aschex(bpin, bpinhex, nPwdlength);

		st = mt3yApi.mt8contactpasswordcheck(0, nPwdlength, bpinhex);
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

		nPwdlength = mt3yApi.stringToInt(StrPwdlength);
		if(StrPwdData.isEmpty() || StrPwdlength.isEmpty() || nPwdlength*2 != StrPwdData.length())
		{
			E_ExecuteInfo.setText("密码长度、密码数据都不能为空，写入密码数据长度必须跟密码长度参数匹配");
			return;
		}

		bpin = StrPwdData.getBytes();
		mt3yApi.mt8aschex(bpin, bpinhex, nPwdlength);

		st = mt3yApi.mt8contactpasswordinit(0, nPwdlength, bpinhex);
		if(st != 0)
		{
			E_ExecuteInfo.setText("修改Pin码失败");
		}
		else
		{
			E_ExecuteInfo.setText("修改Pin码成功");
		}
	}

	////////////
	public void bt_Key_Open(View source)
	{
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_PinPadExecuteInfo);
		int st = mt3yApi.mt8keyopen();
		if(st != 0)
		{
			E_ExecuteInfo.setText("打开密码键盘失败");
		}
		else
		{
			E_ExecuteInfo.setText("打开密码键盘成功");
		}
	}

	public void bt_Key_Close(View source)
	{
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_PinPadExecuteInfo);
		int st = mt3yApi.mt8keyclose();
		if(st != 0)
		{
			E_ExecuteInfo.setText("关闭密码键盘失败");
		}
		else
		{
			E_ExecuteInfo.setText("关闭密码键盘成功");
		}
	}

	public void bt_load_MasterKey(View source)
	{
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_PinPadExecuteInfo);
		Spinner spinner_MainKeyNo = (Spinner)findViewById(R.id.spinner_MasterNo);
		int mainKeyNo = spinner_MainKeyNo.getSelectedItemPosition();
		Spinner spinner_EncryAlg  = (Spinner)findViewById(R.id.spinner_EncryAlg);
		int encryAlg = spinner_EncryAlg.getSelectedItemPosition();
		EditText et_Key = (EditText) findViewById(R.id.EditText_MasterKey);
		String strKey = et_Key.getText().toString();
		if (strKey.isEmpty()){
			E_ExecuteInfo.setText("主密钥不能为空");
			return;
		}
		if (strKey.length() % 2 != 0){
			E_ExecuteInfo.setText("主密钥长度有错");
			return;
		}
		byte hexKey[] = new byte[256];
		mt3yApi.mt8aschex(strKey.getBytes(), hexKey, strKey.length() / 2);
		int st = mt3yApi.mt8downmainkey((byte)mainKeyNo, (byte)encryAlg, (byte) (strKey.length() / 2), hexKey);
		if(st != 0)
		{
			E_ExecuteInfo.setText("下载主密钥失败");
		}
		else
		{
			E_ExecuteInfo.setText("下载主密钥成功");
		}
	}


	public void bt_load_PinKey(View source)
	{
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_PinPadExecuteInfo);
		Spinner spinner_MainKeyNo = (Spinner)findViewById(R.id.spinner_MasterNo);
		int mainKeyNo = spinner_MainKeyNo.getSelectedItemPosition();
		EditText et_Key = (EditText) findViewById(R.id.EditText_WorkKey);
		String strKey = et_Key.getText().toString();
		if (strKey.isEmpty()){
			E_ExecuteInfo.setText("Pin密钥不能为空");
			return;
		}
		if (strKey.length() % 2 != 0){
			E_ExecuteInfo.setText("Pin密钥长度有错");
			return;
		}
		byte hexKey[] = new byte[256];
		mt3yApi.mt8aschex(strKey.getBytes(), hexKey, strKey.length() / 2);
		int st = mt3yApi.mt8downpinkey((byte)mainKeyNo, (byte) (strKey.length() / 2), hexKey);
		if(st != 0)
		{
			E_ExecuteInfo.setText("下载Pin密钥失败[" + st + "]");
		}
		else
		{
			E_ExecuteInfo.setText("下载Pin密钥成功");
		}
	}

	public void bt_Get_KeyNum(View source)
	{
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_PinPadExecuteInfo);

		byte status[] = new byte[1];
		byte keynum[] = new byte[1];
		int st = mt3yApi.mt8getkeynum(status, keynum);
		if(st != 0)
		{
			E_ExecuteInfo.setText("获取按键个数失败[" + st + "]");
		}
		else
		{
			E_ExecuteInfo.setText("status:" + getStatusText(status[0]) + " 按键个数:" + keynum[0]);
		}
	}

	public void bt_Get_KeyVersion(View source)
	{
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_PinPadExecuteInfo);

		int verlen[] = new int[10];
		byte verdata[] = new byte[64];
		int st = mt3yApi.mt8getkeyversion(verlen, verdata);
		WlFingerUtil.writeFileToSD("mt8getkeyversion return st = " + st );
		if(st != 0)
		{
			E_ExecuteInfo.setText("获取版本号失败[" + st + "]");
		}
		else
		{
			E_ExecuteInfo.setText("版本号:" + new String(verdata));
		}
	}

	public void bt_GetPin(View source)
	{
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_PinPadExecuteInfo);

		byte status[] = new byte[1];
		byte keynum[] = new byte[1];
		byte pin[]    = new byte[256];
		int st = mt3yApi.mt8getkeyplainpin(status, keynum, pin);
		if(st != 0)
		{
			E_ExecuteInfo.setText("获取明文密码失败[" + st + "]");
		}
		else
		{
			if (status[0] == 0 || status[0] == 1)
			{
				byte ascPin[] = new byte[18];
				mt3yApi.mt8hexasc(pin, ascPin, 7);
				byte resPin[] = new byte[keynum[0]];
				System.arraycopy(ascPin, 0, resPin, 0, keynum[0]);
				E_ExecuteInfo.setText("明文密码,状态:"+getStatusText(status[0]) + ",个数:"+keynum[0]+",按键值:" + new String(resPin));
			}
			else
				E_ExecuteInfo.setText("明文密码,状态:"+status[0]);
		}
	}


	public void bt_GetEnPin(View source)
	{
		EditText E_ExecuteInfo = (EditText) findViewById(R.id.editText_PinPadExecuteInfo);
		Spinner spinner_MainKeyNo = (Spinner)findViewById(R.id.spinner_MasterNo);
		int mainKeyNo = spinner_MainKeyNo.getSelectedItemPosition();
		EditText et_CardNo = (EditText) findViewById(R.id.EditText_CardNo);
		String strCardNo = et_CardNo.getText().toString();
		if (strCardNo.length() < 16){
			E_ExecuteInfo.setText("卡号长度有误");
			return;
		}
		int  pinLen[] = new int[1];
		byte pin[]    = new byte[256];


		String psCardNo = "";
		String pattern = "0000000000000000";
		if (strCardNo.length() > 0)
		{
			int start = strCardNo.length() - 13;
			if (start < 0) start = 0;
			psCardNo = strCardNo.substring(start, strCardNo.length() - 1);
		}
		psCardNo = pattern.substring(0, pattern.length() - psCardNo.length()) +  psCardNo;

		byte hexCardNo[] = new byte[psCardNo.length() / 2];
		mt3yApi.mt8aschex(psCardNo.getBytes(), hexCardNo, psCardNo.length() / 2);

		int st = mt3yApi.mt8getkeyenpin((byte)mainKeyNo, hexCardNo, pinLen, pin);
		if(st != 0)
		{
			E_ExecuteInfo.setText("获取密文密码失败[" + st + "]");
		}
		else
		{
			byte ascPin[] = new byte[pinLen[0] * 2];
			mt3yApi.mt8hexasc(pin, ascPin, pinLen[0]);
			E_ExecuteInfo.setText("密文密码:" + new String(ascPin));
		}
	}

	private String getStatusText(byte status) {
		// TODO Auto-generated method stub
		switch(status)
		{
			case 0x00:
				return "初始值";
			case 0x01:
				return "确定按下";
			case 0x02:
				return "取消按下";
		}
		return "";
	}
}
