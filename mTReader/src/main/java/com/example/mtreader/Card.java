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
	private ViewPager viewPager;//ҳ������
	private ImageView imageView;// ����ͼƬ
	private TextView textView1,textView2,textView3,textView4,textView5,textView6;
	private List<View> views;// Tabҳ���б�
	private int offset = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int bmpW;// ����ͼƬ���
	private View view1,view2,view3,view4,view5,view6;//����ҳ��
	private MainActivity Mactivity;//������ACTIVITY����
	private int st=0;//��������״̬��
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
	  *  ��ʼ��ͷ��
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
	 2      * ��ʼ������
	 3 */

	private void InitImageView() {
		imageView= (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();// ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
		offset = (screenW / 4 - bmpW) / 2;// ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// ���ö�����ʼλ��
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
		 * �󶨼�����
		 */
		spinner.setOnItemSelectedListener(new ConTractCPUCosSpinnerSelectenVoictNotice());
	}
	
	class ConTractCPUCosSpinnerSelectenVoictNotice implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO �Զ����ɵķ������
			System.out.println("COS��ʾ������");
			selected = parent.getItemAtPosition(position).toString();
			System.out.println(selected);
			if(selected.equals("ȡ�����08"))
				Send.setText("0084000008");
			else if(selected.equals("ѡ��MF"))
				Send.setText("00A40000023F00");
			else if(selected.equals("ѡ��MF����6117"))
				Send.setText("00C0000017");
			else if(selected.equals("ɾ��MF"))
				Send.setText("800E000008ffffffffffffffff");
			else if(selected.equals("����MF"))
				Send.setText("80E0000018FFFFFFFFFFFFFFFF0F01315041592E5359532E4444463031");
			else if(selected.equals("����EF01"))
				Send.setText("80E00200070001000f0f00ff");
			else if(selected.equals("����EF02"))
				Send.setText("80e00200070002000f0f00ff");							  	
			else if(selected.equals("��������MF"))
				Send.setText("80E00001023F00");
			else if(selected.equals("��EF01д74���ֽ�"))
				Send.setText("00D681007400112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233");
			else if(selected.equals("��EF01дF2���ֽ�"))
				Send.setText("00d68100f200112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff1122");
			else if(selected.equals("��EF01дF7���ֽ�"))
				Send.setText("00d68100f7000affffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
			else if(selected.equals("��EF01��76���ֽ�"))
				Send.setText("00B0810076");
			else if(selected.equals("��EF01��FF���ֽ�"))
				Send.setText("00B08100FF");
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO �Զ����ɵķ������
			
		}
		
	}
	
	//��ѡ��ť״̬ʵʱ��ȡ
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
	
	public void a_cpureset(View source)//CPU������Ƭ���������
	{
		/*
		String strsend="",data="",tip="";
		byte []szCardNo=new byte[512];
		byte []szName=new byte[512];
		byte []szErrinfo=new byte[1024];

		int nCardType = 0x00;
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //��ʾ��Ϣ��
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //��������
		
		st=MainActivity.mt8GetJINGRONGICCardNoAndName(nCardType, szCardNo, szName, szErrinfo);
		if(st==0)
		{	
			data=new String("����:" + szCardNo + "����:" + szName);
			
			Recv.setText(data);
			PUTip.setText("��ȡ����IC�����ż������ɹ�");
		}
		else
		{
			
			try
			{
				tip=new String(szErrinfo, "GB2312");//"��ȡ����IC�����ż�����ʧ��! ";
				Recv.setText("");
				PUTip.setText(tip);
			}
			catch(IOException e)
			{
				Recv.setText("");
				PUTip.setText("��ȡ����IC�����ż�������Ϣ�쳣");
				e.printStackTrace();
			}
			
		}
		*/
		
		
		initContractCPUCos();
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //��ʾ��Ϣ��
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //��������
		
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
			
			//PUTip.setText( "�ϵ縴λ�ɹ�");
			
			
			tip2 = "�ϵ縴λ�ɹ�" + len;
			PUTip.setText(tip2);
			
			
		}
		else
		{
			tip="�ϵ縴λʧ��!" + st;
			Recv.setText("");
			PUTip.setText(tip);
			
		}
		
	}
	public void a_sendCMD(View source)//CPU������Ƭ���������
	{
		String strsend="",data="",tip="";
		byte []send_hex=new byte[512];
		byte []resp_hex=new byte[512];
		byte []resp_asc=new byte[1024];
		int []resplen =new int[10];
		int cmdlen=0,v=0,cardtype = 0;
		
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //��ʾ��Ϣ��
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //��������
		Send=(EditText) findViewById(R.id.lay1_edit_send); //�������� 
		
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
			PUTip.setText("��������ɹ�");
		}
		else
		{
			tip="��������ʧ��!";
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
		 * �󶨼�����
		 */
		spinner.setOnItemSelectedListener(new ConTractlessCPUCosSpinnerSelectenVoictNotice());
	}
	
	class ConTractlessCPUCosSpinnerSelectenVoictNotice implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO �Զ����ɵķ������
			System.out.println("COS��ʾ������");
			String selected = parent.getItemAtPosition(position).toString();
			System.out.println(selected);
			if(selected.equals("ȡ�����08"))
				ContractLessSend.setText("0084000008");
			else if(selected.equals("ѡ��MF"))
				ContractLessSend.setText("00A40000023F00");
			else if(selected.equals("ѡ��MF����6117"))
				ContractLessSend.setText("00C0000017");
			else if(selected.equals("ɾ��DF"))
				ContractLessSend.setText("800E000000");
			//else if(selected.equals("����MF"))
			//	ContractLessSend.setText("80E0000018FFFFFFFFFFFFFFFF0F01315041592E5359532E4444463031");
			else if(selected.equals("����EF01"))
				ContractLessSend.setText("80E0000107280C00F0F0FFFF");
			else if(selected.equals("����EF02"))
				ContractLessSend.setText("80E0000207280C00F0F0FFFF");
			//else if(selected.equals("��������MF"))
			//	ContractLessSend.setText("80E00001023F00");
			else if(selected.equals("��EF01д74���ֽ�"))
				ContractLessSend.setText("00D681007400112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233");
			else if(selected.equals("��EF01дF2���ֽ�"))
				ContractLessSend.setText("00d68100f200112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff1122");
			else if(selected.equals("��EF01дF7���ֽ�"))
				ContractLessSend.setText("00d68100f7000affffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
			else if(selected.equals("��EF01��76���ֽ�"))
				ContractLessSend.setText("00B0810076");
			else if(selected.equals("��EF01��FF���ֽ�"))
				ContractLessSend.setText("00B08100FF");
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO �Զ����ɵķ������
			
		}
		
	}
	
	public void a_cpudown(View source)//CPU������Ƭ���������
	{
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //��ʾ��Ϣ��
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
			PUTip.setText("�µ�ɹ�");
		}
		else
		{
			tip="�µ�ʧ��!";
			PUTip.setText(tip);
		}
	}
	public void a_opencard(View source)//�ǽ�CPU���Ƭ��������
	{
		//initContractlessCPUCos();
		
		EditText PUTip=(EditText) findViewById(R.id.lay2_Tip); //��ʾ��Ϣ��
		EditText Recv=(EditText) findViewById(R.id.lay2_edit_recv); //��������
		
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
			tip2="�ɹ����Ƭ"+"UID:"+str;
			PUTip.setText(tip2);

		}
		else
		{
			tip="���Ƭʧ��!";
			Recv.setText("");
			PUTip.setText(tip);
			
		}
		
	}
	public void a_cmdsend(View source)//�ǽ�CPU������Ƭ���������
	{
		String strsend="",data="",tip="";
		byte []send_hex=new byte[512];
		byte []resp_hex=new byte[512];
		byte []resp_asc=new byte[1024];
		int []resplen =new int[10];
		int cmdlen=0,v=0;
		EditText PUTip=(EditText) findViewById(R.id.lay2_Tip); //��ʾ��Ϣ��
		EditText Recv=(EditText) findViewById(R.id.lay2_edit_recv); //��������
		EditText Send=(EditText) findViewById(R.id.lay2_edit_send); //�������� 
		
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
			PUTip.setText("��������ɹ�");
		}
		else
		{
			tip="��������ʧ��! ";
			Recv.setText("");
			PUTip.setText(tip);
		}
	}
	public void a_closecard(View source)//�ǽ�CPU���ϲ��տ�Ƭ��������
	{
		EditText PUTip=(EditText) findViewById(R.id.lay2_Tip); //��ʾ��Ϣ��
		String tip="";
		int delaytime = 0;
		int v=radiovalue();
		st=MainActivity.mt8rfhalt(delaytime);
		if(st==0)
		{
			PUTip.setText("�ɹ��رտ�Ƭ");
		}
		else
		{
			tip="�رտ�Ƭʧ��!";
			
			PUTip.setText(tip);
		}
	}
	
	//��ȡ�ǽӴ�ʽIC�����ż�����
	//a_getjingrongicCOntractlesscardnoandname
	public void a_getjingrongicCOntractlesscardnoandname(View source)
	{
		String strsend="",data="",tip="",strname = "";
		byte []szCardNo=new byte[512];
		byte []szName=new byte[512];
		byte []szErrinfo=new byte[1024];

		int nCardType = 0x01;
		EditText PUTip=(EditText) findViewById(R.id.lay2_Tip); //��ʾ��Ϣ��
		EditText Recv=(EditText) findViewById(R.id.lay2_edit_recv); //��������
		
		st=MainActivity.ReadNAN(nCardType, szCardNo, szName, szErrinfo);
		if(st==0)
		{	
			
			try
			{
				data=new String(szCardNo, "gb2312");
				strname = new String(szName, "gb2312");
				
				Recv.setText("����:" + data + "����:" + strname);
				PUTip.setText("��ȡ����IC�����ż������ɹ�");
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
				tip=new String(szErrinfo, "GB2312");//"��ȡ����IC�����ż�����ʧ��! ";
				Recv.setText("");
				PUTip.setText(tip);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}
	}
	
	//��ȡ�Ӵ�ʽIC�����ż�����
	public void a_getjingrongiccardnoandname(View source)
	{
		String strsend="",data="",tip="", strname = "";
		byte []szCardNo=new byte[512];
		byte []szName=new byte[512];
		byte []szErrinfo=new byte[1024];

		int nCardType = 0x00;
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //��ʾ��Ϣ��
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //��������
		
		st=MainActivity.ReadNAN(nCardType, szCardNo, szName, szErrinfo);
		if(st==0)
		{
			try
			{
				data=new String(szCardNo, "gb2312");
				strname = new String(szName, "gb2312");
				Recv.setText("����:" + data + "����:" + strname);
				PUTip.setText("��ȡ����IC�����ż������ɹ�");
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
				tip=new String(szErrinfo, "gb2312");//"��ȡ����IC�����ż�����ʧ��! ";
				Recv.setText("");
				PUTip.setText(tip);
			}
			catch(IOException e)
			{
				Recv.setText("");
				PUTip.setText("��ȡ����IC�����ż�������Ϣ�쳣");
				e.printStackTrace();
			}
			
		}
	}
	
	//��ȡ�籣��������Ϣ
	public void a_getsocialcardbasicinfo(View source)
	{
		String strsend="",data="",tip="";
		
		byte []szSocialCardBasicInfo=new byte[1024];
		byte []szErrinfo=new byte[1024];

		int nCardType = 0x00;
		EditText PUTip=(EditText) findViewById(R.id.lay1_Tip); //��ʾ��Ϣ��
		EditText Recv=(EditText) findViewById(R.id.lay1_edit_recv); //��������
		
		st = MainActivity.ReadSBInfo(szSocialCardBasicInfo, szErrinfo);
		if(st==0)
		{	
			try
			{		
				String StrSocialCardBasicInfo = "";
				StrSocialCardBasicInfo = new String(szSocialCardBasicInfo, "GB2312");
				Recv.setText(StrSocialCardBasicInfo);
				PUTip.setText("��ȡ�籣��������Ϣ�ɹ�");
			}
			catch(IOException e)
			{
				e.printStackTrace();
				Recv.setText("");
				PUTip.setText("��ȡ�籣��������Ϣ�쳣");
			}
			
		}
		else
		{
			try
			{
				tip=new String(szErrinfo, "gb2312");//"��ȡ����IC�����ż�����ʧ��! ";
				Recv.setText("");
				PUTip.setText(tip);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void bt_rfcard(View source)/*������M1��������������*/
	{
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_CardNo=(EditText) findViewById(R.id.lay3_edit_cardno); //��ʾ��Ϣ��
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
			PUTip.setText("Ѱ���ɹ�");
			
		}
		else
		{
			PUTip.setText("Ѱ��ʧ��");
		}
	}
	public void bt_rfhalt(View source)
	{
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_CardNo=(EditText) findViewById(R.id.lay3_edit_cardno); //��ʾ��Ϣ��
		
		int ndelaytime = 0;
		int st = 0;
				
		st=MainActivity.mt8rfhalt(ndelaytime);
		if(st==0)
		{
			PUTip.setText("HALT�ɹ�");
			
		}
		else
		{
			PUTip.setText("HALTʧ��");
		}
	}
	
	public void bt_rfincrement(View source)
	{
		int addr=0,nSector=0,value=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //��ʾ��Ϣ��
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //��ʾ��Ϣ��
		EditText E_Valueop=(EditText) findViewById(R.id.lay3_edit_valueop); //��ʾ��Ϣ��
		String str_value=E_Valueop.getText().toString();
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty()||str_value.isEmpty())
		{	PUTip.setText("������/���ַ/ֵ ����Ϊ��");
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

			PUTip.setText("��ֵ���ӳɹ�");
			
		}
		else
		{
			PUTip.setText("��ֵ����ʧ��");
		}
	}
	
	public void bt_rfdecrement(View source)
	{
		int addr=0,nSector=0,value=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //��ʾ��Ϣ��
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //��ʾ��Ϣ��
		EditText E_Valueop=(EditText) findViewById(R.id.lay3_edit_valueop); //��ʾ��Ϣ��
		String str_value=E_Valueop.getText().toString();
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty()||str_value.isEmpty())
		{	PUTip.setText("������/���ַ/ֵ ����Ϊ��");
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

			PUTip.setText("��ֵ���ٳɹ�");
			
		}
		else
		{
			PUTip.setText("��ֵ����ʧ��");
		}
	}
	
	public void bt_rfauth(View source)
	{
		byte[] key_asc=new byte[40];
		byte[] key=new byte[20];
		int addr=0,nSector=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //��ʾ��Ϣ��
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //��ʾ��Ϣ��
		EditText E_Secrect=(EditText) findViewById(R.id.lay3_edit_secrect); //��ʾ��Ϣ��
		int v=lay3radiovalue();
		String str_addr=E_Blockaddr.getText().toString();
		String str_key=E_Secrect.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("�����Ż���ַ����Ϊ��");
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
			PUTip.setText("��֤�ɹ�");
			
		}
		else
		{
			PUTip.setText("��֤ʧ��");
		}
	}
	public void bt_rfread(View source)
	{
		byte[] rdata_asc=new byte[64];
		byte[] rdata=new byte[32];
		int addr=0,nSector=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //��ʾ��Ϣ��
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //��ʾ��Ϣ��
		EditText E_Read=(EditText) findViewById(R.id.lay3_edit_read); //��ʾ��Ϣ��
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("�����Ż���ַ����Ϊ��");
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
			PUTip.setText("�����ݳɹ�");
			
		}
		else
		{
			E_Read.setText("");
			PUTip.setText("������ʧ��");
		}
	}
	public void bt_rfwrite(View source)
	{
		byte[] wdata_asc=new byte[64];
		byte[] wdata=new byte[32];
		int addr=0,nSector=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //��ʾ��Ϣ��
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //��ʾ��Ϣ��
		EditText E_Write=(EditText) findViewById(R.id.lay3_edit_write); //��ʾ��Ϣ��
		String str_wdata=E_Write.getText().toString();
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		wdata_asc=str_wdata.getBytes();
		len=str_wdata.length()/2;
		MainActivity.mt8aschex(wdata_asc, wdata, len);
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("�����Ż���ַ����Ϊ��");
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

			PUTip.setText("д���ݳɹ�");
			
		}
		else
		{
			PUTip.setText("д����ʧ��");
		}
	}
	public void bt_rfreadval(View source)
	{
		int []Ivalue=new int[50];
		int addr=0,nSector=0,value=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //��ʾ��Ϣ��
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //��ʾ��Ϣ��
		EditText E_Value=(EditText) findViewById(R.id.lay3_edit_blockvalue); //��ʾ��Ϣ��
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty())
		{	PUTip.setText("������/���ַ����Ϊ��");
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
			PUTip.setText("����ֵ�ɹ�");
			
		}
		else
		{
			E_Value.setText("");
			PUTip.setText("����ֵʧ��");
		}
	}
	public void bt_rfwriteval(View source)
	{
		int addr=0,nSector=0,value=0,len=0;
		EditText PUTip=(EditText) findViewById(R.id.lay3_edit_tip); //��ʾ��Ϣ��
		EditText E_Blockaddr=(EditText) findViewById(R.id.lay3_edit_blockaddr); //��ʾ��Ϣ��
		EditText E_Section=(EditText) findViewById(R.id.lay3_edit_section); //��ʾ��Ϣ��
		EditText E_Valueop=(EditText) findViewById(R.id.lay3_edit_valueop); //��ʾ��Ϣ��
		String str_value=E_Valueop.getText().toString();
		String str_addr=E_Blockaddr.getText().toString();
		String str_section=E_Section.getText().toString();
		if(str_addr.isEmpty()||str_section.isEmpty()||str_value.isEmpty())
		{	PUTip.setText("������/���ַ/ֵ ����Ϊ��");
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

			PUTip.setText("д��ֵ�ɹ�");
			
		}
		else
		{
			PUTip.setText("д��ֵʧ��");
		}
	}

	//�Ӵ�ʽ�洢��
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
			E_ExecuteInfo.setText("������'���ÿ�����',3:4428Card,4:4442Card,8:AT88SC1604Card");
			return;
		}
		
		
		nCardType = MainActivity.stringToInt(StrCardType);
		st = MainActivity.mt8contactsettype(0, nCardType);
		if(st != 0)
		{
			E_ExecuteInfo.setText("���ÿ�����ʧ��");
		}
		else
		{
			E_ExecuteInfo.setText("���ÿ����ͳɹ�");
		}
	}
	
	/*
	 * ʶ������
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
			E_ExecuteInfo.setText("ʶ������ʧ��");
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
				E_IdentifyCardType.setText("������δ֪");
			}
			
			E_ExecuteInfo.setText("ʶ�����ͳɹ�[3:4428Card,4:4442Card,8:AT88SC1604Card]");
		}
	}
	
	/*
	 * ������
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
			E_ExecuteInfo.setText("������'ƫ�Ƶ�ַ'");
			return;
		}
		
		if (Strlength.isEmpty())
		{
			E_ExecuteInfo.setText("������'����'");
			return;
		}
		
		noffset = MainActivity.stringToInt(Stroffset);
		nlength = MainActivity.stringToInt(Strlength);
		
		st = MainActivity.mt8contactread(0, noffset, nlength, breadbuf);
		if(st != 0)
		{
			E_ExecuteInfo.setText("����ʧ��");
		}
		else
		{
			MainActivity.mt8hexasc(breadbuf, breadbufasc, nlength);
			String StrReadData = new String(breadbufasc);
			E_ReadData.setText(StrReadData);
			E_ExecuteInfo.setText("�����ɹ�");
		}
	}
	
	/*
	 * д����
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
			E_ExecuteInfo.setText("ƫ�Ƶ�ַ�����ȡ�д�����ݶ�����Ϊ�գ�д�����ݳ��ȱ�������Ȳ���ƥ��");
			return;
		}
		
		bwritebuf = StrWriteData.getBytes();
		MainActivity.mt8aschex(bwritebuf, bwritebufhex, nlength);
		
		st = MainActivity.mt8contactwrite(0, noffset, nlength, bwritebufhex);
		if(st != 0)
		{
			E_ExecuteInfo.setText("д��ʧ��");
		}
		else
		{
			E_ExecuteInfo.setText("д���ɹ�");
		}
	}
	
	/*
	 * У������
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
			E_ExecuteInfo.setText("���볤�ȡ��������ݶ�����Ϊ�գ�д���������ݳ��ȱ�������볤�Ȳ���ƥ��");
			return;
		}
		
		bpin = StrPwdData.getBytes();
		MainActivity.mt8aschex(bpin, bpinhex, nPwdlength);
		
		st = MainActivity.mt8contactpasswordcheck(0, nPwdlength, bpinhex);
		if(st != 0)
		{
			E_ExecuteInfo.setText("У��pin��ʧ��");
		}
		else
		{
			E_ExecuteInfo.setText("У��pin��ɹ�");
		}
	}
	
	/*
	 * �޸�����
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
			E_ExecuteInfo.setText("���볤�ȡ��������ݶ�����Ϊ�գ�д���������ݳ��ȱ�������볤�Ȳ���ƥ��");
			return;
		}
		
		bpin = StrPwdData.getBytes();
		MainActivity.mt8aschex(bpin, bpinhex, nPwdlength);
		
		st = MainActivity.mt8contactpasswordinit(0, nPwdlength, bpinhex);
		if(st != 0)
		{
			E_ExecuteInfo.setText("�޸�Pin��ʧ��");
		}
		else
		{
			E_ExecuteInfo.setText("�޸�Pin��ɹ�");
		}
	}
	

	/////��
	/** 
	 *     
	 * ͷ�������� 3 */
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

    	int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����
		int two = one * 2;// ҳ��1 -> ҳ��3 ƫ����
		public void onPageScrollStateChanged(int arg0) {
			
			
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
			
		}

		public void onPageSelected(int arg0) {
			String strtip;
			Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(300);
			imageView.startAnimation(animation);
			//Toast.makeText(WeiBoActivity.this, "��ѡ����"+ viewPager.getCurrentItem()+"ҳ��", Toast.LENGTH_SHORT).show();
			int num=viewPager.getCurrentItem();
			if(num==0)
			{
				strtip="�����Բ���CPU��";
				initContractCPUCos();
			}
			else if(num==1)
			{	
				strtip="�����Բ����ǽ�CPU��";
				initContractlessCPUCos();
			}
			else if(num == 2)
			{
				strtip="�����Բ���M1��";
			}
			else if (num==3)
			{
				strtip="�����Բ����Ӵ��洢��";
			}
		/*	else if(num == 3)
			{
				strtip="�����Բ����������";
				Intent intent = new Intent();
				intent.setClass(Card.this, PinPad.class);
				startActivity(intent);
			}
			else if(num == 4)
			{
				strtip="�����Բ�������֤";
				Intent intent = new Intent();
				intent.setClass(Card.this, IdentifyActivity.class);
				startActivity(intent);
			}
			
		else if(num == 4)
			{
				strtip="�����Բ����Ӵ�ʽ�洢��";
				Intent intent = new Intent();
				intent.setClass(Card.this, ContractMemCard.class);
				startActivity(intent);
			}
			*/
			else
			{
				strtip="������ѡ���ܲ���";
			}
			
			Toast.makeText(Card.this, strtip, Toast.LENGTH_SHORT).show();
		}
    	
    }

}
