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
			StrTip="设置超时成功";
		}
		else
		{
			StrTip="设置超时失败,错误号:"+st;
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
			StrTip="设置长度成功";
		}
		else
		{
			StrTip="设置长度失败,错误号:"+st;
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
			StrTip="主密钥长度有不为16或者32";
			PinPadExecuteInfo.setText(StrTip);
			return 1;
		}
		
		b_mk_asc=str_value.getBytes();
		System.out.println(str_value);
		st=MainActivity.mwkbdownloadmainkey(destype,selectmkno,b_mk_asc);
		if(st==0)
		{
			StrTip="下载主密钥成功";
		}
		else
		{
			StrTip="下载主密钥失败,错误号:"+st;
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
			StrTip="工作密钥长度有不为16或者32";
			PinPadExecuteInfo.setText(StrTip);
			return 1;
		}
		
		b_wk_asc=str_value.getBytes();
		st=MainActivity.mwkbdownloaduserkey(destype,selectmkno,selectwkno,b_wk_asc);
		if(st==0)
		{
			StrTip="下载工作密钥成功";
		}
		else
		{
			StrTip="下载工作密钥失败,错误号:"+st;
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
			StrTip="激活工作密钥成功";
		}
		else
		{
			StrTip="激活工作密钥失败,错误号:"+st;
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
			StrTip="明文密码 :"+str_data;
		}
		else
		{
			StrTip="获取明文密码失败,错误号:"+st;
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
			StrTip="密文密码 :"+str_data;
		}
		else
		{
			StrTip="获取密文密码失败,错误号:"+st;
		}
		PinPadExecuteInfo.setText(StrTip);
		return 0;
	}
	class PinPadSpinnerSelectenVoictNotice implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO 自动生成的方法存根
			String selected = parent.getItemAtPosition(position).toString();
			if(selected.equals("0-请输入密码"))
			{
				selectvoicetip=1;
				
			}
			else if(selected.equals("1-请再输入一次"))
			{
				selectvoicetip=2;
			}
			else
			{
				return;
			}
			StrTip="选:"+selectvoicetip;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO 自动生成的方法存根
			
		}
		
	}
	class PinPadSpinnerSelectenWKNO implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO 自动生成的方法存根
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
			StrTip="选:"+selectwkno;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO 自动生成的方法存根
			
		}
		
	}
	class PinPadSpinnerSelectenMKNO implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO 自动生成的方法存根
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
			StrTip="选:"+selectmkno;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO 自动生成的方法存根
			
		}
		
	}
	
}


