package com.shine.mingtaicardreader;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.mt3yreader.mt3yApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static com.example.mt3yreader.mt3yApi.mt8aschex;
import static com.example.mt3yreader.mt3yApi.mt8devicebeep;
import static com.example.mt3yreader.mt3yApi.mt8deviceclose;
import static com.example.mt3yreader.mt3yApi.mt8deviceopenfd;
import static com.example.mt3yreader.mt3yApi.mt8hexasc;
import static com.example.mt3yreader.mt3yApi.mt8rfauthentication;
import static com.example.mt3yreader.mt3yApi.mt8rfcard;
import static com.example.mt3yreader.mt3yApi.mt8rfread;

/**
 * Created by 李晓林 on 2017/2/22
 * qq:1220289215
 * A64设备读卡操作类 通过申请USB权限打开设备，如不希望弹出询问框，修改SystemUI
 */

public class DeviceReaderA extends CardReader{
    private static final String TAG = "DeviceReaderA";
    private TextView mTvStatus;
    private Context mContext;
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private UsbManager mUsbManager;


    public DeviceReaderA(Context context) {
        mContext = context;
        mUsbManager= (UsbManager)context.getSystemService(Context.USB_SERVICE);
    }

    @Override
    void init(TextView textView) {
        mTvStatus=textView;
        if (Build.VERSION.SDK_INT == 23) {
            mTvStatus.setText("当前设备A64 ");
        } else if (Build.VERSION.SDK_INT == 21) {
            mTvStatus.setText("当前设备828 ");
        }else{
            mTvStatus.setText("设备不能识别");
        }
        getAuthorityForDevice();

    }

    private void getAuthorityForDevice() {
        //使用chmod改变设备权限的方法在6.0无效
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
        UsbManager manager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        int number = 0;
        for (UsbDevice usbDevice : deviceList.values()) {
            Log.d(TAG, "device:" + usbDevice);
            if(usbDevice.getVendorId()==0x23a4&&usbDevice.getProductId()==0x0219){
                mTvStatus.append("请求权限");
                number++;
                manager.requestPermission(usbDevice, pendingIntent);
                break;
            }
        }
        if (number == 0) {
            mTvStatus.append("没有找到明泰设备");
        }
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            UsbDeviceConnection connection = mUsbManager.openDevice(device);
                            if (connection != null) {
                                int fileDescriptor = connection.getFileDescriptor();
                                Log.d(TAG, "fileDescriptor:" + fileDescriptor);
                                mTvStatus.append(" 成功获取权限");
                                int dev = mt8deviceopenfd(fileDescriptor);
                                if (dev > 0) {
                                    mTvStatus.append(" 成功打开设备");
                                    int st = mt8devicebeep(258, 1);
                                } else {
                                    mTvStatus.append(" 打开设备失败");
                                }
                            }
                        }
                    } else {
                        mTvStatus.append(" 获取权限失败");
                    }
                }
            }
        }
    };


    @Override
    public int readId(TextView mTextView) {
        mTextView.setText("开始读身份证\n");
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

        int result = 0;
        String StrErrMsg = "", StrName = "", StrSex = "", StrNation = "", StrBirth = "", StrAddress = "", StrDepartment = "";
        String StrDateStart = "", StrDateEnd = "", StrExpir = "", StrIDNo = "";

        String StrWltFilePath = "";
        String StrBmpFilePath = "";
        st = mt3yApi.mt8IDCardRead(
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
          /*  result = -1;
            displayIDCard(StrBmpFilePath);//显示照片
            StrErrMsg = new String("身份证读卡失败");
            PUTip.setText(StrErrMsg);*/
            mTextView.append("身份证读卡失败\n");
            return -100;
        } else {
            try {
                StrName = new String(szName, "UTF-16LE");
                mTextView.append(StrName.trim() + "\n");
                StrSex = getsexinfo(szSex);
                mTextView.append(StrSex.trim() + "\n");
                StrBirth = new String(szBirth, "UTF-16LE");
                mTextView.append(getnation(szNation) + "\n");
                mTextView.append(StrBirth.trim() + "\n");
                mTextView.append(StrAddress.trim() + "\n");
                StrAddress = new String(szAddress, "UTF-16LE");
                mTextView.append(StrAddress.trim() + "\n");
                StrIDNo = new String(szIDNo, "UTF-16LE");
                mTextView.append(StrIDNo.trim() + "\n");

                StrDepartment = new String(szDepartment, "UTF-16LE");
                mTextView.append(StrDepartment.trim() + "\n");

                StrDateStart = new String(szDateStart, "UTF-16LE");
                mTextView.append(StrDateStart.trim() + "\n");

                StrDateEnd = new String(szDateEnd, "UTF-16LE");
                mTextView.append(StrDateEnd.trim() + "\n");

//                StrWltFilePath = getFileStreamPath("photo.wlt").getAbsolutePath();
//                StrBmpFilePath = getFileStreamPath("photo.bmp").getAbsolutePath();
//
//                File wltFile = new File(StrWltFilePath);
//                FileOutputStream fos = new FileOutputStream(wltFile);
//                fos.write(szdata, 0, nRecLen[0]);
//                fos.close();
//
//                DecodeWlt dw = new DecodeWlt();
//                result = dw.Wlt2Bmp(StrWltFilePath, StrBmpFilePath);
////                result = dw.Wlt2Bmp(StrWltFilePath, StrBmpFilePath);
//                if (result == 1) {
//                    Log.d(TAG, "照片解码成功");
//                   //displayIDCard(StrBmpFilePath);//显示照片
//                    //PUTip.setText("照片解码成功");
//                    return 0;
//                } else {
//                    Log.d(TAG, "照片解码失败");
////                 displayIDCard(StrBmpFilePath);//显示照片
////                    PUTip.setText("照片解码失败");
//                    return -300;
//                }


            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "照片解码异常");
//                PUTip.setText("照片解码异常");
                return -200;
            }
            return 0;

        }


    }

    @Override
    public void readSocialCard(TextView mTextView) {
        mTextView.setText("开始读社保卡\n");
        String strsend = "", data = "", tip = "";
        byte[] szSocialCardBasicInfo = new byte[1024];
        byte[] szErrinfo = new byte[1024];

        int nCardType = 0x00;

        int st = mt3yApi.ReadSBInfo(szSocialCardBasicInfo, szErrinfo);
        if (st == 0) {
            try {
                String StrSocialCardBasicInfo = "";
                StrSocialCardBasicInfo = new String(szSocialCardBasicInfo, "GB2312");
                mTextView.append(StrSocialCardBasicInfo + "\n");
                Log.d(TAG, "SocialCard "+StrSocialCardBasicInfo);
            } catch (IOException e) {
                e.printStackTrace();
                mTextView.append("读取社保卡基本信息异常\n");
            }

        } else {
            try {
                tip = new String(szErrinfo, "gb2312");//"读取金融IC卡卡号及姓名失败! ";
                Log.d(TAG, tip);
                mTextView.append("读取社保卡失败\n");

            } catch (IOException e) {
                e.printStackTrace();
            }

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
        int st = mt8rfcard(ndelaytime, cardtype, snr);
        if (st == 0) {
            mt8hexasc(snr, snr_asc, 4);
            String tip = new String(snr_asc);
            Log.d(TAG, "tip "+tip);
            mTextView.append("寻卡成功:");
            mTextView.append(tip.trim());
            mTextView.append("\n");
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
        mt8aschex(key_asc, key, len);
        int st = mt8rfauthentication((char) 0, (char) nSector, key);
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
        int st = mt8rfread((char) addr, rdata);
        if (st == 0) {
            mt8hexasc(rdata, rdata_asc, 16);
            try {
                String str_data = new String(rdata_asc, "GB2312");
                Log.d(TAG, "读数据成功" + str_data.trim());
                mTextView.append("读数据成功：" +str_data+"\n");
                String result = format(str_data.trim());
                mTextView.append("如果数据是ASCII码，卡号：" +result);
            } catch (UnsupportedEncodingException e) {
                mTextView.append("M1卡数据编码失败");
                e.printStackTrace();
            }
        } else {
            mTextView.append("M!读数据失败");
        }
    }

    @Override
   public void readShangHaiSocialCard(TextView textView) {
        textView.setText("开始读上海社保卡\n");
        cpuReset(textView);
    }

    @Override
    void readSocialCardRegionCode(TextView mTextView) {
        mTextView.setText("开始读社保卡区号\n");

        int len = 0, v = 0, cardtype = 0;
        int ntimeout = 0;
        byte[] atr = new byte[200];
        byte[] atr_asc = new byte[400];
        byte[] atrlen = new byte[100];
        cardtype = 0x00;
        int st = mt3yApi.mt8samsltreset(ntimeout, cardtype, atrlen, atr);
        if (st == 0) {
            len = atrlen[0];
            mt8hexasc(atr, atr_asc, len);
            mTextView.append("上电复位成功\n");
            //MainActivity.hex_asc(atr, atr_asc, len);
            String tip1 = new String(atr_asc);
            Log.d(TAG, tip1);
            String result = sendCMD(mTextView,"00A404000F7378312E73682EC9E7BBE1B1A3D5CF");
            if ("611E".equalsIgnoreCase(result)) {
                result=sendCMD(mTextView,"00A4020002EF05" );
                if ("9000".equals(result)) {
                    result=sendCMD(mTextView,"00B2010400");
                    if (!TextUtils.isEmpty(result) && result.length() >= 10) {
//                        取4到10位
                        mTextView.append("卡号："+result.substring(4,10));
                    }
                }
            }
        } else {
            mTextView.append("上电复位失败\n");

        }
    }


    private void cpuReset(TextView mTextView) {
        int len = 0, v = 0, cardtype = 0;
        int ntimeout = 0;
        byte[] atr = new byte[200];
        byte[] atr_asc = new byte[400];
        byte[] atrlen = new byte[100];
        cardtype = 0x00;
        int st = mt3yApi.mt8samsltreset(ntimeout, cardtype, atrlen, atr);
        if (st == 0) {
            len = atrlen[0];
            mt8hexasc(atr, atr_asc, len);
            mTextView.append("上电复位成功\n");
            //MainActivity.hex_asc(atr, atr_asc, len);
            String tip1 = new String(atr_asc);
            Log.d(TAG, tip1);
            sendCMD(mTextView,"00A404000F7378312E73682EC9E7BBE1B1A3D5CF");
//            if ("6A82".equalsIgnoreCase(result)) {
            String  result=sendCMD(mTextView,"00A4020002EF05" );
                if ("9000".equals(result)) {
                    result=sendCMD(mTextView,"00B207040b");
                    if (!TextUtils.isEmpty(result) && result.length() > 8) {
                        result = format(result.substring(4, result.length() - 4));
                        mTextView.append("卡号："+result);
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
        mt3yApi.mt8aschex(send_asc, send_hex, cmdlen / 2);
        int st = mt3yApi.mt8cardAPDU(cardtype, cmdlen / 2, send_hex, resplen, resp_hex);
        if (st == 0) {
            mt3yApi.mt8hexasc(resp_hex, resp_asc, resplen[0]);
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
        mContext.unregisterReceiver(mBroadcastReceiver);
        int st = mt8deviceclose();
        if (st == 0) {
            Log.d(TAG, "关闭设备成功");
        } else {
            Log.d(TAG, "关闭设备失败");
        }
    }
}
