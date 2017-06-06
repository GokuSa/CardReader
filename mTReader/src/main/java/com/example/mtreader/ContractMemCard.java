package com.example.mtreader;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.mtreader.MainActivity;

public class ContractMemCard extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contractcard);
	}
	
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
	
}
