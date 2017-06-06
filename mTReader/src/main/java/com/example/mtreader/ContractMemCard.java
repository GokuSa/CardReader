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
	 * ���ÿ�����
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
				E_IdentifyCardType.setText("������δ֪");
			}
			
			E_ExecuteInfo.setText("ʶ�����ͳɹ�");
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
	
}
