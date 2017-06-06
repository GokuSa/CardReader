package com.example.mtreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mtreader.MainActivity;
import com.synjones.bluetooth.BmpUtil;
import com.synjones.bluetooth.DecodeWlt;

public class IdentifyActivity extends Activity {
	
	private Bitmap bmp;
	int result;
	public native int Wlt2Bmp(String wltPath, String bmpPath);
	static{
		System.loadLibrary("DecodeWlt");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //�ı������Ĭ�ϲ���ý���
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layidentify);
	}
	
	/*
	 * ��ȡ�Ա���Ϣ
	 */
	public String getsexinfo(byte bsex[])
	{
		String StrSexInfo = "";
		if(bsex[0] == 0x30)
		{
			StrSexInfo = "δ֪";
		}
		else if(bsex[0] == 0x31)
		{
			StrSexInfo = "��";
		}
		else if(bsex[0] == 0x32)
		{
			StrSexInfo = "Ů";
		}
		else if(bsex[0] == 0x39)
		{
			StrSexInfo = "δ˵��";
		}
		else
		{
			StrSexInfo = " ";
		}
		
		return StrSexInfo;
	}
	
	
	/*
	 * ��ȡ������Ϣ
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
			StrNation = "��";
			break;
		case 2:
			StrNation = "�ɹ�";
			break;
		case 3:
			StrNation = "��";
			break;
		case 4:
			StrNation = "��";
			break;
		case 5:
			StrNation = "ά���";
			break;
		case 6:
			StrNation = "��";
			break;
		case 7:
			StrNation = "��";
			break;
		case 8:
			StrNation = "׳";
			break;
		case 9:
			StrNation = "����";
			break;
		case 10:
			StrNation = "����";
			break;
		case 11:
			StrNation = "��";
			break;
		case 12:
			StrNation = "��";
			break;
		case 13:
			StrNation = "��";
			break;
		case 14:
			StrNation = "��";
			break;
		case 15:
			StrNation = "����";
			break;
		case 16:
			StrNation = "����";
			break;
		case 17:
			StrNation = "������";
			break;
		case 18:
			StrNation = "��";
			break;
		case 19:
			StrNation = "��";
			break;
		case 20:
			StrNation = "����";
			break;
		case 21:
			StrNation = "��";
			break;
		case 22:
			StrNation = "�";
			break;
		case 23:
			StrNation = "��ɽ";
			break;
		case 24:
			StrNation = "����";
			break;
		case 25:
			StrNation = "ˮ";
			break;
		case 26:
			StrNation = "����";
			break;
		case 27:
			StrNation = "����";
			break;
		case 28:
			StrNation = "����";
			break;
		case 29:
			StrNation = "�¶�����";
			break;
		case 30:
			StrNation = "��";
			break;
		case 31:
			StrNation = "���Ӷ�";
			break;
		case 32:
			StrNation = "����";
			break;
		case 33:
			StrNation = "Ǽ";
			break;
		case 34:
			StrNation = "����";
			break;
		case 35:
			StrNation = "����";
			break;
		case 36:
			StrNation = "ë��";
			break;
		case 37:
			StrNation = "����";
			break;
		case 38:
			StrNation = "����";
			break;
		case 39:
			StrNation = "����";
			break;
		case 40:
			StrNation = "����";
			break;
		case 41:
			StrNation = "������";
			break;
		case 42:
			StrNation = "ŭ";
			break;
		case 43:
			StrNation = "���α��";
			break;
		case 44:
			StrNation = "����˹";
			break;
		case 45:
			StrNation = "���¿�";
			break;
		case 46:
			StrNation = "�°�";
			break;
		case 47:
			StrNation = "����";
			break;
		case 48:
			StrNation = "ԣ��";
			break;
		case 49:
			StrNation = "��";
			break;
		case 50:
			StrNation = "������";
			break;
		case 51:
			StrNation = "����";
			break;
		case 52:
			StrNation = "���״�";
			break;
		case 53:
			StrNation = "����";
			break;
		case 54:
			StrNation = "�Ű�";
			break;
		case 55:
			StrNation = "���";
			break;
		case 56:
			StrNation = "��ŵ";
			break;
		case 57:
			StrNation = "����";
			break;
		case 58:
			StrNation = "���Ѫͳ�й�����ʿ";
			break;
		default:
			StrNation = " ";
			break;
		}
		return StrNation;
	}
	
	private void displayIDCard(String bmpPath) {
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
		EditText PUTip=(EditText) findViewById(R.id.editText_idcexecuteinfo); //��ʾ��Ϣ��
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
		st = MainActivity.mt8IDCardRead(
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
			displayIDCard(StrBmpFilePath);//��ʾ��Ƭ
			StrErrMsg = new String("���֤����ʧ��");
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
					displayIDCard(StrBmpFilePath);//��ʾ��Ƭ
					PUTip.setText("��Ƭ����ɹ�");
					return 0;
				}
				else
				{
					displayIDCard(StrBmpFilePath);//��ʾ��Ƭ
					PUTip.setText("��Ƭ����ʧ��");
					return -300;
				}
				
				
			}catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
				PUTip.setText("��Ƭ�����쳣");
				return -200;
			}
			
		}


	}

}
