package com.example.mtreader;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class PinPad extends Activity{
	
	private EditText PinPadExecuteInfo = null;
	private String StrTip=null;
	private int selectmkno=0,selectwkno=0,selectvoicetip=0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboard);
		initactivity();
	}
	public void initactivity()
	{
		PinPadExecuteInfo = (EditText)findViewById(R.id.editText_PinPadExecuteInfo);
		
		Spinner spinner=(Spinner)findViewById(R.id.spinner_SetVoiceNotice);
		Spinner spinner_wk=(Spinner)findViewById(R.id.spinner_WorkKeyNo);
		Spinner spinner_mk=(Spinner)findViewById(R.id.spinner_MasterNo);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.VoiceNotice_arry, 
				android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapter_mk = ArrayAdapter.createFromResource(this,
				R.array.MasterKeyNO_arry, 
				android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapter_wk = ArrayAdapter.createFromResource(this,
				R.array.WorkkeyNo_arry, 
				android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new PinPadSpinnerSelectenVoictNotice());
		adapter_mk.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_mk.setAdapter(adapter_mk);
		spinner_mk.setOnItemSelectedListener(new PinPadSpinnerSelectenMKNO());
		adapter_wk.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_wk.setAdapter(adapter_wk);
		spinner_wk.setOnItemSelectedListener(new PinPadSpinnerSelectenWKNO());
	}	
	public int bt_PinPad_SetTimeOut(View source)
	{
		int st=0;
		EditText edit_timeout=(EditText) findViewById(R.id.EditText_Timeout);
		String str_value=edit_timeout.getText().toString();
		int timeout=MainActivity.stringToInt(str_value);
		st=MainActivity.mt8mwkbsettimeout(timeout);
		if(st==0)
		{
			StrTip="���ó�ʱ�ɹ�";
		}
		else
		{
			StrTip="���ó�ʱʧ��,�����:"+st;
		}
		PinPadExecuteInfo.setText(StrTip);
		return 0;
	}
	public int bt_PinPad_SetPINLenth(View source)
	{
		
		int st=0;
		EditText edit_len=(EditText) findViewById(R.id.editText_SetPwdLen);
		String str_value=edit_len.getText().toString();
		int len=MainActivity.stringToInt(str_value);
		st=MainActivity.mt8mwkbsetpasslen(len);
		if(st==0)
		{
			StrTip="���ó��ȳɹ�";
		}
		else
		{
			StrTip="���ó���ʧ��,�����:"+st;
		}
		PinPadExecuteInfo.setText(StrTip);
		return 0;
	}

	public int bt_PinPad_DownMK(View source)
	{
		int st=0,destype=0;
		byte []b_mk_asc=new byte[257];
		EditText edit_mk=(EditText) findViewById(R.id.EditText_MasterKey);
		String str_value=edit_mk.getText().toString();
		if(edit_mk.length()==16)
			destype=0;
		else if(edit_mk.length()==32)
			destype=1;
		else
		{
			StrTip="����Կ�����в�Ϊ16����32";
			PinPadExecuteInfo.setText(StrTip);
			return 1;
		}
		
		b_mk_asc=str_value.getBytes();
		System.out.println(str_value);
		st=MainActivity.mwkbdownloadmainkey(destype,selectmkno,b_mk_asc);
		if(st==0)
		{
			StrTip="��������Կ�ɹ�";
		}
		else
		{
			StrTip="��������Կʧ��,�����:"+st;
		}
		PinPadExecuteInfo.setText(StrTip);
		return 0;
	}
	public int bt_PinPad_DownWK(View source)
	{
		
		int st=0,destype=0;
		byte []b_wk_asc=new byte[257];
		EditText edit_wk=(EditText) findViewById(R.id.EditText_WorkKey);
		String str_value=edit_wk.getText().toString();
		if(edit_wk.length()==16)
			destype=0;
		else if(edit_wk.length()==32)
			destype=1;
		else
		{
			StrTip="������Կ�����в�Ϊ16����32";
			PinPadExecuteInfo.setText(StrTip);
			return 1;
		}
		
		b_wk_asc=str_value.getBytes();
		st=MainActivity.mwkbdownloaduserkey(destype,selectmkno,selectwkno,b_wk_asc);
		if(st==0)
		{
			StrTip="���ع�����Կ�ɹ�";
		}
		else
		{
			StrTip="���ع�����Կʧ��,�����:"+st;
		}
		PinPadExecuteInfo.setText(StrTip);
		return 0;
	}
	public int bt_PinPad_ACTWK(View source)
	{
		int st=0;
		st=MainActivity.mt8mwkbactivekey(selectmkno,selectwkno);
		if(st==0)
		{
			StrTip="�������Կ�ɹ�";
		}
		else
		{
			StrTip="�������Կʧ��,�����:"+st;
		}
		PinPadExecuteInfo.setText(StrTip);
		return 0;
	}
	public int bt_PinPad_GetPin(View source)
	{
		int st=0;
		byte []planpin=new byte[65];
		st=MainActivity.mt8mwkbgetpin(selectvoicetip,planpin );
		if(st==0)
		{
			String str_data=new String(planpin);
			StrTip="�������� :"+str_data;
		}
		else
		{
			StrTip="��ȡ��������ʧ��,�����:"+st;
		}
		PinPadExecuteInfo.setText(StrTip);
		
		return 0;
	}
	public int bt_PinPad_GetEncryptPin(View source)
	{
		int st=0;
		byte []cardno=new byte[65];
		byte []planpin=new byte[65];
		EditText edit_cardno=(EditText) findViewById(R.id.EditText_CardNo);
		String str_value=edit_cardno.getText().toString();
		cardno=str_value.getBytes();
		st=MainActivity.mt8mwkbgetenpin(selectvoicetip, cardno,planpin);
		if(st==0)
		{
			String str_data=new String(planpin);
			StrTip="�������� :"+str_data;
		}
		else
		{
			StrTip="��ȡ��������ʧ��,�����:"+st;
		}
		PinPadExecuteInfo.setText(StrTip);
		return 0;
	}
	class PinPadSpinnerSelectenVoictNotice implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO �Զ����ɵķ������
			String selected = parent.getItemAtPosition(position).toString();
			if(selected.equals("0-����������"))
			{
				selectvoicetip=1;
				
			}
			else if(selected.equals("1-��������һ��"))
			{
				selectvoicetip=2;
			}
			else
			{
				return;
			}
			StrTip="ѡ:"+selectvoicetip;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO �Զ����ɵķ������
			
		}
		
	}
	class PinPadSpinnerSelectenWKNO implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO �Զ����ɵķ������
			String selected = parent.getItemAtPosition(position).toString();
			System.out.println(selected);
			if(selected.equals("0"))
			{
				selectwkno=0;
			}
			else if(selected.equals("1"))
			{
				selectwkno=1;
			}
			else if(selected.equals("2"))
			{
				selectwkno=2;
			}
			else if(selected.equals("3"))
			{
				selectwkno=3;
			}
			else if(selected.equals("4"))
			{
				selectwkno=4;
			}
			else if(selected.equals("5"))
			{
				selectwkno=5;
			}
			else if(selected.equals("6"))
			{
				selectwkno=6;
			}
			else
			{
				selectwkno=7;
			}
			StrTip="ѡ:"+selectwkno;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO �Զ����ɵķ������
			
		}
		
	}
	class PinPadSpinnerSelectenMKNO implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO �Զ����ɵķ������
			String selected = parent.getItemAtPosition(position).toString();
			System.out.println(selected);
			if(selected.equals("0"))
			{
				selectmkno=0;
			}
			else if(selected.equals("1"))
			{
				selectmkno=1;
			}
			else if(selected.equals("2"))
			{
				selectmkno=2;
			}
			else
			{
				selectmkno=3;
			}
			StrTip="ѡ:"+selectmkno;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO �Զ����ɵķ������
			
		}
		
	}
	
}


