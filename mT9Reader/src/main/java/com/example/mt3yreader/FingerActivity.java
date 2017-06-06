package com.example.mt3yreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.mt9reader.R;

public class FingerActivity extends Activity {


	private EditText SendCtrl;
	private EditText RecvCtrl;
	private EditText TipCtrl;
	private EditText DelayCtrl;
	private RadioGroup radioGroup;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transferchannel);


		SendCtrl = (EditText) findViewById(R.id.editText1);
		RecvCtrl = (EditText) findViewById(R.id.editText2);
		TipCtrl = (EditText) findViewById(R.id.editText3);
		//DelayCtrl = (EditText) findViewById(R.id.editText4);
		radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup1);
		//SendCtrl.setText("5b0006120000000000145d");
		SendCtrl.setText("02303030343039303030303030303D03");
	}

	public void GetFinger(View source)
	{
		int st;
		byte bDelay=7;
		byte [] AscData1 = new byte[3072*3*2];

		byte []send_hex=new byte[512];

		int []recvLen ={0};
		byte []resp_hex=new byte[3072*3];


		String strsend, str, strDelay,str_Data;
		strsend=SendCtrl.getText().toString();
		int cmdlen=strsend.length();
		byte send_asc[]=strsend.getBytes();
		mt3yApi.mt8aschex(send_asc, send_hex, cmdlen/2);

		for(int i = 0; i < radioGroup.getChildCount(); i++)
		{
			RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
			if (rb.isChecked())
			{
				bDelay = (byte) i;
				break;
			}
		}

		long time=0,timeD=0;
		time = System.currentTimeMillis();
		st = mt3yApi.mt8fingerchannel(bDelay, cmdlen/2, send_hex, recvLen, resp_hex);

		if(st==0)
		{
			timeD= System.currentTimeMillis() - time;
			mt3yApi.mt8hexasc(resp_hex, AscData1, recvLen[0]);
			str = new String(AscData1);

			RecvCtrl.setText(str);
			TipCtrl.setText("透传数据成功["+ timeD + " ms]");

		}
		else
		{
			RecvCtrl.setText("");
			TipCtrl.setText("透传数据失败！["+st+"]");
		}

	}

	public void FingerApp(View source)
	{
		Intent intent=new Intent(FingerActivity.this,wlFingerActivity.class);
		startActivity(intent);
	}




}
