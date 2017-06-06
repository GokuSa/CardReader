package com.example.mtreader;

/**
 * Created by Administrator on 2016/10/26.
 * andrioid 6.0以下设备调用
 */

public class MainActivity {

    static {
        System.loadLibrary("mt3x32");
    }
    //设备操作函数
    public static native int mt8deviceopen(int port,int baud);
    public static native int mt8serialopen(String port, int baud);
    public static native int mt8deviceopenfd(int fd);

    public static native int mt8deviceclose();
    public static native int mt8deviceversion( int module,byte verlen[],byte verdata[]);
    public static native int mt8devicebeep(int delaytime, int times);

    //CPU 卡操作函数
    public static native int mt8samsltreset(int delaytime, int cardno, byte natrlen[],byte atr[]);
    public static native int mt8samsltpowerdown(int cardno);
    public static native int mt8cardAPDU(int cardno, int sendApduLen,byte sendApdu[], int nrecvLen[], byte recvApdu[]);

    //非接CPU卡操作函数
    public static native int mt8opencard(int delaytime,byte cardtype[],byte snrlen[],byte snr[],byte atrlen[],byte atr[]);
    public static native int mt8rfhalt(int delaytime);
    public static native int mt8GetCardType(int isContact[],byte Contact_Atr[],byte atrLen[],
                                            int isContactLess[],byte ContactLess_Snr[],byte snrLen[],
                                            int isIDcard[],int isMag[],int jtrack1_len[],int jtrack2_len[],int jtrack3_len[],
                                            byte jtrack1_data[],byte jtrack2_data[],byte jtrack3_data[]);

    //PBOC 金融IC卡
    //获取金融IC卡卡号、姓名
    public static native int ReadNAN (int nCardType,byte Cardno[],byte CardName[],byte lpErrMsg[]);

    //接触式CPU社保卡
    public static native int ReadSBInfo (byte lpSocialCardBasicinfo[],byte lpErrMsg[]);

    //M1卡操作函数
    public static native int mt8rfcard(int delaytime,byte cardtype[],byte cardID[]);
    public static native int mt8rfauthentication(int mode,int nsecno,byte key[]);
    public static native int mt8rfread(int nblock,byte readdata[]);
    public static native int mt8rfwrite(int nblock,byte writedata[]);
    public static native int mt8rfincrement(int nblock,int incvalue);
    public static native int mt8rfdecrement(int nblock,int decvalue);
    public static native int mt8rfinitval(int nblock,int writevalue);
    public static native int mt8rfreadval(int nblock,int readvalue[]);

    //磁条卡操作
    public static native int mt8magneticmode();
    public static native int mt8magneticread(int jtimeout,int jtrack1_len[],int jtrack2_len[],int jtrack3_len[],
                                             byte jtrack1_data[],byte jtrack2_data[],byte jtrack3_data[]);

    //接触式存储函数
    public static native int mt8contactsettype(int cardno,int cardtype);
    public static native int mt8contactidentifytype(int cardno,byte cardtype[]);
    public static native int mt8contactpasswordinit(int cardno,int pinlen,byte pinstr[]);
    public static native int mt8contactpasswordcheck(int cardno,int pinlen,byte pinstr[]);
    public static native int mt8contactread(int cardno,int address,int rlen,byte readdata[]);
    public static native int mt8contactwrite(int jcardno,int address,int wlen,byte writedata[]);

    //AT88SC1604
    public static native int mt8srd1604(int zone,int offset,int len,byte read_data_buffer[]);
    public static native int mt8swr1604(int zone,int offset,int len,byte write_data_buffer[]);
    public static native int mt8csc1604(int zone,int len,byte passwd[]);
    public static native int mt8cesc1604(int zone,int len,byte passwd);
    public static native int mt8wsc1604(int zone,int len,byte passwd[]);
    public static native int mt8ser1604(int zone,int offset,int len);
    public static native int mt8fakefus1604(int mode);
    public static native int mt8psnl1604();

    //二代证
    public static native int mt8CLCardOpen(int delaytime,byte cardtype[],byte snrlen[],byte snr[],byte rlen[],byte recdata[]);
    public static native int mt8IDCardRead(
            byte name[],
            byte sex[],
            byte nation[],
            byte birth[],
            byte address[],
            byte idnum[],
            byte asigndepartment[],
            byte datestart[],
            byte dateend[],
            int rphotodatalen[],
            byte photodatainfo[]);

    public static native int mt8IDCardGetCardInfo(int index,byte infodata[],int infodatalen[]);
    public static native int mt8IDCardGetModeID(byte IDLen[],byte sIDData[]);
    public static native int mt8IDCardReadIDNUM(byte rlen[],byte receivedata[]);

    //工具函数
    public static native int mt8hexasc(byte hex[],byte asc[],int len);
    public static native int mt8aschex(byte asc[],byte hex[],int len);
    public static native int mt8hexbase64(byte hex[],byte base64[],int hexlen);
    public static native int mt8base64hex(byte base64[],byte hex[],int base64len);
    public static native int mt8rfencrypt(byte key[],byte ptrSource[],int msgLen, byte ptrDest[]);
    public static native int mt8rfdecrypt(byte key[],byte ptrSource[],int msgLen, byte ptrDest[]);
}
