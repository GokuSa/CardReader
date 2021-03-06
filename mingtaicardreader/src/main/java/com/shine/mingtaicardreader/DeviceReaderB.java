package com.shine.mingtaicardreader;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.mtreader.MainActivity;
import com.synjones.bluetooth.DecodeWlt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * Created by 李晓林 on 2017/2/22
 * qq:1220289215
 */

public class DeviceReaderB extends CardReader{
    private static final String TAG = "DeviceReaderR16_801";
    private Context mContext;

    public DeviceReaderB(Context context) {
        mContext = context;
    }

    @Override
    public void init(final TextView mTextViewStatus) {
        if (Build.VERSION.SDK_INT == 15) {
            mTextViewStatus.setText("当前设备801");
        } else if (Build.VERSION.SDK_INT == 19) {
            mTextViewStatus.setText("当前设备R16");
        } else  {
            mTextViewStatus.setText("设备不能识别");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT == 15) {
                    grant801Authority();
                }else if (Build.VERSION.SDK_INT == 19){
                    grantR16Authority();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int mDev = MainActivity.mt8deviceopen(0, 115200);
                        if (mDev > 0) {
                            mTextViewStatus.append("打开设备成功");
                            int st = MainActivity.mt8devicebeep(258, 1);//(258,1,unicode_data);
                            if (st == 0) {
                                mTextViewStatus.append(" 设备BEEP成功");
                            } else {
                                mTextViewStatus.append(" 设备BEEP失败");
                            }
                        } else {
                            mTextViewStatus.append("打开设备失败 请允许应用程序访问设备");
                        }
                    }
                });
            }
        }.start();
    }

    private void grant801Authority() {
        RootCmd.execRootCmdSlient(
                "chmod 777 /dev/bus/;"+
                        "chmod 777 /dev/bus/usb/;"+
                        "chmod 777 /dev/bus/usb/0*;"+
                        "chmod 777 /dev/bus/usb/001/*;"+
                        "chmod 777 /dev/bus/usb/002/*;"+
                        "chmod 777 /dev/bus/usb/003/*;"+
                        "chmod 777 /dev/bus/usb/004/*;"+
                        "chmod 777 /dev/bus/usb/005/*;");
    }

    private void grantR16Authority() {
          new RootCommand().executeCommands("chmod 777 /dev/bus/","chmod 777 /dev/bus/usb/",
                        "chmod 777 /dev/bus/usb/0*","chmod 777 /dev/bus/usb/001",
                        "chmod 777 /dev/bus/usb/002", "chmod 777 /dev/bus/usb/003"
                , "chmod 777 /dev/bus/usb/004", "chmod 777 /dev/bus/usb/005");
    }
    @Override
    public int readId(TextView mTextView) {
        mTextView.setText("开始读身份证\n");
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
        byte szdata[] = new byte[3072];


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

        if (st != 0) {
            //显示照片
//            displayIDCard(StrBmpFilePath);
//            StrErrMsg = new String();
            mTextView.append("身份证读卡失败");
            return -100;
        } else {
            try {
                String StrName = new String(szName, "UTF-16LE");
                String StrSex = getsexinfo(szSex);
                String StrNation = getnation(szNation);
                String StrBirth = new String(szBirth, "UTF-16LE");
                String StrAddress = new String(szAddress, "UTF-16LE");
                String StrIDNo = new String(szIDNo, "UTF-16LE");
                String StrDepartment = new String(szDepartment, "UTF-16LE");
                String StrDateStart = new String(szDateStart, "UTF-16LE");
                String StrDateEnd = new String(szDateEnd, "UTF-16LE");

                Log.d(TAG, StrName.trim());
                Log.d(TAG, StrBirth);
                Log.d(TAG, StrIDNo);
                mTextView.append(StrName.trim() + "\n");
                mTextView.append(StrSex.trim() + "\n");
                mTextView.append(StrNation.trim() + "\n");
                mTextView.append(StrIDNo.trim() + "\n");
                mTextView.append(StrDepartment.trim() + "\n");
                mTextView.append(StrAddress.trim() + "\n");

                StrWltFilePath = mContext.getFileStreamPath("photo.wlt").getAbsolutePath();
                StrBmpFilePath = mContext.getFileStreamPath("photo.bmp").getAbsolutePath();

                File wltFile = new File(StrWltFilePath);
                FileOutputStream fos = new FileOutputStream(wltFile);
                fos.write(szdata, 0, nRecLen[0]);
                fos.close();

                DecodeWlt dw = new DecodeWlt();
                int result = dw.Wlt2Bmp(StrWltFilePath, StrBmpFilePath);
                if (result == 1) {
//                    displayIDCard(StrBmpFilePath);//显示照片
                    Log.d(TAG, "照片解码成功");
                    return 0;
                } else {
//                    displayIDCard(StrBmpFilePath);//显示照片
                    Log.d(TAG, "照片解码失败");
                    return -300;
                }

            } catch (IOException e) {
                Log.d(TAG, "照片解码异常");
                return -200;
            }
        }
    }

    @Override
    public void readSocialCard(TextView mTextView) {
        mTextView.setText("开始读社保卡\n");
        byte[] szSocialCardBasicInfo = new byte[1024];
        byte[] szErrinfo = new byte[1024];
        int nCardType = 0x00;
        int st = MainActivity.ReadSBInfo(szSocialCardBasicInfo, szErrinfo);
        Log.d(TAG, "st:" + st);
        if (st == 0) {
            try {
                String StrSocialCardBasicInfo = new String(szSocialCardBasicInfo, "GB2312");
                Log.d(TAG, StrSocialCardBasicInfo);
                Log.d(TAG, "读取社保卡基本信息成功");
                mTextView.append(StrSocialCardBasicInfo.trim());
            } catch (IOException e) {
                e.printStackTrace();
                mTextView.append("读取社保卡基本信息异常");
            }
        } else {
            mTextView.append("读取社保卡失败");
        }
    }

    @Override
    public void readM1(TextView mTextView,int addr, int nSector, String str_key) {
        mTextView.setText("开始读M1卡\n");
        findM1(mTextView);
        vertify(mTextView,addr, nSector, str_key);
        read(mTextView,addr, nSector);
    }

    private void findM1(TextView mTextView) {
        byte[] snr_asc = new byte[40];
        byte[] snr = new byte[20];
        byte[] cardtype = new byte[8];

        int ndelaytime = 0;
        int st = MainActivity.mt8rfcard(ndelaytime, cardtype, snr);
        if (st == 0) {
            MainActivity.mt8hexasc(snr, snr_asc, 4);
            String tip = new String(snr_asc);
            Log.d(TAG, tip);
            mTextView.append("寻卡成功\n");
        } else {
            mTextView.append("寻卡失败\n");
        }
    }

    private void vertify(TextView mTextView,int addr, int nSector, String str_key) {
        byte[] key_asc = new byte[40];
        byte[] key = new byte[20];
//        int addr = 1;
//        int nSector = 1;
        int len = 0;
//        String str_key = "FFFFFFFFFFFF";
        len = (str_key.length() / 2);
        key_asc = str_key.getBytes();
        MainActivity.mt8aschex(key_asc, key, len);
        int st = MainActivity.mt8rfauthentication((char) 0, (char) nSector, key);
        if (st == 0) {
            mTextView.append("M1认证成功\n");
        } else {
            mTextView.append("M1认证失败\n");
        }
    }

    private void read(TextView mTextView,int addr, int nSector) {
        byte[] rdata_asc = new byte[64];
        byte[] rdata = new byte[32];
//        int addr = 1;
//             int   nSector = 1;
        int len = 0;

        if (nSector < 32) {
            addr = nSector * 4 + addr;
        } else {
            addr = (nSector - 32) * 16 + addr + 128;
        }
        int st = MainActivity.mt8rfread((char) addr, rdata);
        if (st == 0) {
            MainActivity.mt8hexasc(rdata, rdata_asc, 16);
            try {
                String str_data = new String(rdata_asc, "GB2312");
                Log.d(TAG, "读数据成功" + str_data);
                String result = format(str_data.trim());
                mTextView.append("卡号："+ result);
            } catch (UnsupportedEncodingException e) {
                mTextView.append("M1卡数据编码失败");
                e.printStackTrace();
            }
        } else {
            mTextView.append("M1读数据失败");
        }
    }

    @Override
    void readShangHaiSocialCard(TextView textView) {
        textView.setText("开始读上海社保卡\n");
        cpuReset(textView);
    }
    private void cpuReset(TextView mTextView) {
        int len = 0, v = 0, cardtype = 0;
        int ntimeout = 0;
        byte[] atr = new byte[200];
        byte[] atr_asc = new byte[400];
        byte[] atrlen = new byte[100];
        cardtype = 0x00;
        int st = MainActivity.mt8samsltreset(ntimeout, cardtype, atrlen, atr);
        if (st == 0) {
            len = atrlen[0];
            MainActivity.mt8hexasc(atr, atr_asc, len);
            mTextView.append("上电复位成功\n");
            //MainActivity.hex_asc(atr, atr_asc, len);
            String tip1 = new String(atr_asc);
            Log.d(TAG, tip1);
            String result = sendCMD(mTextView,"00A404000F7378312E73682EC9E7BBE1B1A3D5CF");
            if ("6A82".equalsIgnoreCase(result)) {
                result=sendCMD(mTextView,"00A4020002EF05" );
                if ("9000".equals(result)) {
                    result=sendCMD(mTextView,"00B207040b");
                    if (!TextUtils.isEmpty(result) && result.length() > 8) {
                        result = format(result.substring(4, result.length() - 4));
                        mTextView.append("卡号："+result);
                    }
                }
            }
        } else {
            mTextView.append("上电复位失败\n");
        }
    }

    //读社保卡区号
    @Override
    void readSocialCardRegionCode(TextView mTextView) {
        mTextView.setText("开始读社保卡区号\n");
        int len = 0, v = 0, cardtype = 0;
        int ntimeout = 0;
        byte[] atr = new byte[200];
        byte[] atr_asc = new byte[400];
        byte[] atrlen = new byte[100];
        cardtype = 0x00;
        int st = MainActivity.mt8samsltreset(ntimeout, cardtype, atrlen, atr);
        if (st == 0) {
            len = atrlen[0];
            MainActivity.mt8hexasc(atr, atr_asc, len);
            mTextView.append("上电复位成功\n");
            String tip1 = new String(atr_asc);
            Log.d(TAG, tip1);
             sendCMD(mTextView,"00A404000F7378312E73682EC9E7BBE1B1A3D5CF");
//            if ("611E".equalsIgnoreCase(result)) {
            String  result=sendCMD(mTextView,"00A4000002EF05" );
                if ("9000".equals(result)) {
                    result=sendCMD(mTextView,"00B2010400");
                    if (!TextUtils.isEmpty(result) && result.length() >= 10) {
//                        取4到10位
                        mTextView.append("卡号："+result.substring(4,10));
                    }

                }
//            }
        } else {
            mTextView.append("上电复位失败\n");
        }
    }

    //CPU操作卡片命令触发函数
    private String sendCMD(TextView mTextView,String strsend) {
        byte[] send_hex = new byte[512];
        byte[] resp_hex = new byte[512];
        byte[] resp_asc = new byte[1024];
        int[] resplen = new int[10];

        int cmdlen = strsend.length();
        byte send_asc[] = strsend.getBytes();
        int cardtype = 0x00;
        MainActivity.mt8aschex(send_asc, send_hex, cmdlen / 2);
        int st = MainActivity.mt8cardAPDU(cardtype, cmdlen / 2, send_hex, resplen, resp_hex);
        if (st == 0) {
            MainActivity.mt8hexasc(resp_hex, resp_asc, resplen[0]);
            String data = new String(resp_asc);
            Log.d(TAG, "rec "+data.trim());
            mTextView.append("发送命令成功,收到 "+data+"\n");
            return data.trim();

        } else {
            mTextView.append("发送命令失败");
        }
        return "";
    }
    @Override
    void close() {
        MainActivity.mt8deviceclose();
    }
}
