package shine.com.huadacardreader.util;

import android.content.Context;
import android.util.Log;

import com.hdos.usbdevice.HdosUsbDeviceLib;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 读卡的帮助类
 * 为了正常读取社保卡 注释了431到436行  ，修改了443行
 */
public class CardInfo {

	private HdosUsbDeviceLib HdosUsbDevice;

	private static boolean Tag = false;

	private static final String TAG = null;

	private static byte[] ATR = new byte[41];
	private static byte[] CityCode = new byte[33];
	private static byte[] Random = new byte[10];

	public CardInfo(Context act){
		HdosUsbDevice = new HdosUsbDeviceLib(act);
	}

	private boolean StringToHex(String strIn, byte[] strOut){
		int j = 0;
		byte strbuf[] = new byte[strIn.length()+1];
		byte pstrTemp[] = new byte[strIn.length()+1];

		strbuf = strIn.getBytes();

		for(int i=0; i<strIn.length()-1; i+=2, j++)
		{
			if(strbuf[i]>='0' && strbuf[i]<='9')
			{
				pstrTemp[i] = (byte) (strbuf[i] - '0');
			}
			else if(strbuf[i]>='A' && strbuf[i]<='F')
			{
				pstrTemp[i] = (byte) (strbuf[i] - 'A' + 10);
			}
			else if(strbuf[i]>='a' && strbuf[i]<='f')
			{
				pstrTemp[i] = (byte) (strbuf[i] - 'a' + 10);
			}
			else
			{
				return false;     //非十六进制字符
			}

			if(strbuf[i+1]>='0' && strbuf[i+1]<='9')
			{
				pstrTemp[i+1] = (byte) (strbuf[i+1] - '0');
			}
			else if(strbuf[i+1]>='A' && strbuf[i+1]<='F')
			{
				pstrTemp[i+1] = (byte) (strbuf[i+1] - 'A' + 10);
			}
			else if(strbuf[i+1]>='a' && strbuf[i+1]<='f')
			{
				pstrTemp[i+1] = (byte) (strbuf[i+1] - 'a' + 10);
			}
			else
			{
				return false;     //非十六进制字符
			}

			strOut[j] = (byte) ((pstrTemp[i] << 4) & 0xF0);
			strOut[j] += pstrTemp[i+1] & 0x0F;
		}

		return true;
	}

	private  boolean bytesToHexString(byte[] strIn, int lenIn, byte[] strOut){

		if (strIn == null || lenIn <= 0) {
			return false;
		}
		for (int i = 0; i < lenIn; i++) {
			int v = strIn[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				strOut[i*2] = '0';
				strOut[i*2+1] = (byte) hv.charAt(0);
			}else{
				strOut[i*2] = (byte) hv.charAt(0);
				strOut[i*2+1] = (byte) hv.charAt(1);
			}
		}

		return true;
	}

	private int ExterAuthen(byte []key, int lev){

		int ren = -100;
		byte[] PATR = new byte[41];
		byte[] cmd = new byte[256];
		byte[] resp = new byte[256];
		//byte[] temp = new byte[256];
		String strbuf = "";

		ren = HdosUsbDevice.ICC_Reader_pre_PowerOn((byte)0x11, PATR);
		if(ren <= 0){
			return -101;
		}

		Arrays.fill(cmd, (byte) 0);
		Arrays.fill(resp, (byte) 0);
		strbuf = "00A4020002DF01";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x11, strbuf.length()/2, cmd, resp);
		if(ren<2 && resp[ren-2]!=0x90){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -104;
		}

		Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
		Arrays.fill(resp, 0, ren, (byte) 0);
		strbuf = "BFDE670820";
		StringToHex(strbuf, cmd);
		cmd[2]=key[0];
		cmd[3]=key[1];

		if(lev==1){
			cmd[4]=0x20;
			cmd[5]=ATR[0];
			cmd[6]=ATR[1];
			cmd[7]=ATR[2];
			cmd[8]=ATR[3];
			cmd[9]=ATR[4];
			cmd[10]=ATR[5];
			cmd[11]=ATR[6];
			cmd[12]=ATR[7];

			cmd[13]=CityCode[0];
			cmd[14]=CityCode[1];
			cmd[15]=CityCode[2];
			cmd[16]=CityCode[3];
			cmd[17]=CityCode[4];
			cmd[18]=CityCode[5];
			cmd[19]=0x73;
			cmd[20]=0x78;

			cmd[21]=CityCode[0];
			cmd[22]=CityCode[1];
			cmd[23]=0x30;
			cmd[24]=0x30;
			cmd[25]=0x30;
			cmd[26]=0x30;
			cmd[27]=0x73;
			cmd[28]=0x68;

			cmd[29]=Random[0];
			cmd[30]=Random[1];
			cmd[31]=Random[2];
			cmd[32]=Random[3];
			cmd[33]=Random[4];
			cmd[34]=Random[5];
			cmd[35]=Random[6];
			cmd[36]=Random[7];

			ren = HdosUsbDevice.ICC_Reader_Application((byte)0x11, 37, cmd, resp);
			if(ren<2 && resp[ren-2]!=0x90){

				HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
				return -104;
			}
		}
		else if(lev ==2){
			cmd[4]=0x18;
			cmd[5]=ATR[0];
			cmd[6]=ATR[1];
			cmd[7]=ATR[2];
			cmd[8]=ATR[3];
			cmd[9]=ATR[4];
			cmd[10]=ATR[5];
			cmd[11]=ATR[6];
			cmd[12]=ATR[7];

			cmd[13]=CityCode[0];
			cmd[14]=CityCode[1];
			cmd[15]=CityCode[2];
			cmd[16]=CityCode[3];
			cmd[17]=CityCode[4];
			cmd[18]=CityCode[5];
			cmd[19]=0x73;
			cmd[20]=0x78;
			cmd[21]=Random[0];
			cmd[22]=Random[1];
			cmd[23]=Random[2];
			cmd[24]=Random[3];
			cmd[25]=Random[4];
			cmd[26]=Random[5];
			cmd[27]=Random[6];
			cmd[28]=Random[7];

			ren = HdosUsbDevice.ICC_Reader_Application((byte)0x11, 29, cmd, resp);
			if(ren<2 && resp[ren-2]!=0x90){

				HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
				return -104;
			}
		}
		else if(lev == 3){
			cmd[4]=0x10;
			cmd[5]=ATR[0];
			cmd[6]=ATR[1];
			cmd[7]=ATR[2];
			cmd[8]=ATR[3];
			cmd[9]=ATR[4];
			cmd[10]=ATR[5];
			cmd[11]=ATR[6];
			cmd[12]=ATR[7];

			cmd[13]=Random[0];
			cmd[14]=Random[1];
			cmd[15]=Random[2];
			cmd[16]=Random[3];
			cmd[17]=Random[4];
			cmd[18]=Random[5];
			cmd[19]=Random[6];
			cmd[20]=Random[7];

			ren = HdosUsbDevice.ICC_Reader_Application((byte)0x11, 21, cmd, resp);
			if(ren<2 && resp[ren-2]!=0x90){

				HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
				return -104;
			}
		}
		else{
			return -111;
		}

		Arrays.fill(cmd, (byte) 0);
		Arrays.fill(resp, (byte) 0);
		strbuf = "80FA0000080102030405060708";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x11, strbuf.length()/2, cmd, resp);
		if(ren<2 && resp[ren-2]!=0x61 && resp[ren-2]!=0x90){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -104;
		}

		Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
		Arrays.fill(resp, 0, ren, (byte) 0);
		strbuf = "00C0000008";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x11, strbuf.length()/2, cmd, resp);
		if(ren<2 && resp[ren-2]!=0x90){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -104;
		}

		byte[] result = new byte[10];
		System.arraycopy(resp, 0, result, 0, 8);

		Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
		Arrays.fill(resp, 0, ren, (byte) 0);
		cmd[0] = (byte)0x00;
		cmd[1] = (byte)0x88;
		cmd[2] = (byte)0x00;
		cmd[3] = (byte)0x00;
		cmd[4] = (byte)0x10;
		System.arraycopy(cmd, 5, Random, 0, 8);
		cmd[13] = (byte)0x01;
		cmd[14] = (byte)0x02;
		cmd[15] = (byte)0x03;
		cmd[16] = (byte)0x04;
		cmd[17] = (byte)0x05;
		cmd[18] = (byte)0x06;
		cmd[19] = (byte)0x07;
		cmd[20] = (byte)0x08;

		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, 21, cmd, resp);
		if(ren<2 && resp[ren-2]!=0x61 && resp[ren-2]!=0x90){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -104;
		}


		Arrays.fill(cmd, 0, 21, (byte) 0);
		Arrays.fill(resp, 0, ren, (byte) 0);
		strbuf = "00C0000008";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x11, strbuf.length()/2, cmd, resp);
		if(ren<2 && resp[ren-2]!=0x90){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -104;
		}

		for(int i=0;i<8;i++)
		{
			if(result[i]!=resp[i])
			{
				return -111;
			}
		}

		return 0;
	}

	//打开设备
	public boolean Open(){

		Tag = HdosUsbDevice.openDevice();
		//	Log.d("open","1");
		return Tag;
	}

	//关闭设备
	public void Close(){

		HdosUsbDevice.closeDevice();
		Tag = false;
	}

	//检测是否有社保卡
	public boolean CheckSSCard(){
		int ren = -100;
		Log.d("CardInfo", "Tag:" + Tag);
		if(!Tag){
			return false;
		}
		ren = HdosUsbDevice.ICC_Reader_pre_PowerOn((byte)0x01, ATR);
		Log.d("CardInfo", "检测 社保卡 ren:" + ren);
		if(ren <= 0){
			return false;
		}
		return true;
	}

	public int ReadSSCard(byte[] xm, byte[] shbzhm, byte[] shbzkh){
		Log.d("CardInfo","begin to read 社保卡");
		int ren = -100;
		//byte[] ATR = new byte[41];
		byte[] cmd = new byte[256];
		byte[] resp = new byte[256];
		byte[] temp = new byte[256];
		String strbuf = "";
		boolean aut = true;

		if(!Tag){

			return -1;
		}

		//HdosUsbDevice.openDevice();

		/*ren = HdosUsbDevice.ICC_Reader_pre_PowerOn((byte)0x01, ATR);
		if(ren <= 0){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -2;
		}*/

		strbuf = "00A404000F7378312E73682EC9E7BBE1B1A3D5CF";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, strbuf.length()/2, cmd, resp);
		Log.d("CardInfo"," read 社保卡"+ren);

		if(ren<=0){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -3;
		}

		if(resp[ren-2]!=0x61 && resp[ren-2]!=0x90){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -4;
		}

		Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
		Arrays.fill(resp, 0, ren, (byte) 0);
		strbuf = "00A4020002EF05";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, strbuf.length()/2, cmd, resp);
		Log.d("CardInfo"," read 社保卡2"+ren);

		if(ren<=0 && resp[ren-2]!=0x90){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -4;
		}

		if(aut){
			Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
			Arrays.fill(resp, 0, ren, (byte) 0);
			strbuf = "00B2010012";
			StringToHex(strbuf, cmd);
			ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, strbuf.length()/2, cmd, resp);
            Log.d(TAG, "ren414:" + ren);
            if(ren<=0 && resp[ren-2]!=0x90){

				HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
				return -4;
			}
			bytesToHexString(resp, ren, temp);
			System.arraycopy(temp, 0, CityCode, 0, ren*2-8);
			//Log.d(TAG, "CityCode");

			Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
			Arrays.fill(resp, 0, ren, (byte) 0);
			strbuf = "0084000008";
			StringToHex(strbuf, cmd);
			ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, strbuf.length()/2, cmd, resp);
			if(ren<=0 && resp[ren-2]!=0x90){

				HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
				return -4;
			}
			System.arraycopy(resp, 0, Random, 0, 8);
			//Log.d(TAG, "Random");

			byte[] key = {0x67,0x08,0x00};
            //注释 影响身份证读取
//			ren = ExterAuthen(key, 1);
			Log.d("CardInfo", "ren:" + ren);
			//先注释
			/*if(ren != 0){
				return ren;
			}*/
			//Log.d(TAG, "ExterAuthen success");
		}

		//卡号
		Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
		//ren为负值 改成resp的大小
//		Arrays.fill(resp, 0, ren, (byte) 0);
        Arrays.fill(resp, 0, resp.length, (byte) 0);

        strbuf = "00B207000B";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, strbuf.length()/2, cmd, resp);
		if(ren<=0 ||resp[ren-2]!=-112){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -4;
		}
		System.arraycopy(resp, 2, shbzkh, 0, ren-4);

		Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
		Arrays.fill(resp, 0, ren, (byte) 0);
		strbuf = "00A4020002EF06";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, strbuf.length()/2, cmd, resp);
		if(ren<=0 ||resp[ren-2]!=-112){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -4;
		}

		//社会保障号码
		Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
		Arrays.fill(resp, 0, ren, (byte) 0);
		strbuf = "00B2080014";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, strbuf.length()/2, cmd, resp);
		if(ren<=0 ||resp[ren-2]!=-112){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -4;
		}
		System.arraycopy(resp, 2, shbzhm, 0, ren-4);


		//姓名
		Arrays.fill(cmd, 0, strbuf.length()/2, (byte) 0);
		Arrays.fill(resp, 0, ren, (byte) 0);
		strbuf = "00B2090020";
		StringToHex(strbuf, cmd);
		ren = HdosUsbDevice.ICC_Reader_Application((byte)0x01, strbuf.length()/2, cmd, resp);
		if(ren<=0 || resp[ren-2]!=-112){

			HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
			return -4;
		}
		System.arraycopy(resp, 2, xm, 0, ren-4);
		HdosUsbDevice.ICC_Reader_PowerOff((byte)0x01);
		return 0;
	}

	//检测是否有M1卡
	public boolean CheckM1(){
		int ren = -100;
		byte[] UID = new byte[41];

		if(!Tag){

			return false;
		}

		ren = HdosUsbDevice.PICC_Reader_Request();
		if(0 != ren){

			return false;
		}

		ren = HdosUsbDevice.PICC_Reader_anticoll(UID);
		if(0 != ren){

			return false;
		}

		ren = HdosUsbDevice.PICC_Reader_Select((byte)0x41);
		if(0 != ren){

			return false;
		}

		return true;
	}

	public int  ReadM1(byte[] key, byte mode, byte secnr, byte addr, byte[] data){

		int ren = -100;
		byte[] buf = new byte[100];

		if(!Tag){

			return -1;
		}

		ren = HdosUsbDevice.readMifareCard(mode, addr, secnr, key, buf);
		if(0 != ren){

			return -2;
		}

		bytesToHexString(buf, 16, data);

		return 0;
	}

	public int DevStatus(){

		byte[] temp = new byte[256];
		//byte[] tempoffset = new byte[256];
		byte[] OutReport = new byte[256];
		int ren = -10;

		if(!Tag){

			return -1;
		}

		Arrays.fill(temp, (byte) 0);
		OutReport[0] = 0;
		temp[1] = (byte)0x02;
		temp[2] = (byte)0x08;
		temp[3] = (byte)0x00;
		temp[4] = (byte)0x01;
		temp[5] = (byte)0x00;
		temp[6] = (byte)0x10;

		byte xorbuffer = 0;
		for (int i = 2; i < 7; i++) {
			xorbuffer = (byte)(xorbuffer ^ temp[i]);
		}
		temp[7] = xorbuffer;

		ren = HdosUsbDevice.HidD_SetFeature(temp, 33);
		if(ren <= 0){
			return -2;
		}

		HdosUsbDevice.HidD_GetFeature(OutReport, 33);
		if(ren <= 0){
			return -2;
		}

		if (OutReport[3] != 0) {
			return OutReport[3];
		}

		return 0;
	}

	public int ReadCert(byte[] pBmpFile, byte[] pName, byte[] pSex, byte[] pNation, byte[] pBirth, byte[] pAddress, byte[] pIDNo, byte[] pDepartment, byte[] pEffectDate, byte[] pExpireDate, byte[] pErrMsg, String pkName){
		int ren = -1;
		try {
			ren = HdosUsbDevice.PICC_Reader_ReadIDMsg(pBmpFile, pName, pSex, pNation, pBirth, pAddress, pIDNo, pDepartment, pEffectDate, pExpireDate, pErrMsg, pkName);
		} catch (UnsupportedEncodingException | InterruptedException e) {
			Log.e("CardInfo", e.toString());
			e.printStackTrace();
		}
		return ren;
	}

	public int[] convertByteToColor(byte[] data){
		return HdosUsbDevice.convertByteToColor(data);
	}
}
