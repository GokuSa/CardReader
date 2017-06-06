package com.shine.a64cardreader;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mt3yreader.mt3yApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mt3yreader.mt3yApi.mt8aschex;
import static com.example.mt3yreader.mt3yApi.mt8devicebeep;
import static com.example.mt3yreader.mt3yApi.mt8deviceclose;
import static com.example.mt3yreader.mt3yApi.mt8deviceopen;
import static com.example.mt3yreader.mt3yApi.mt8deviceopenfd;
import static com.example.mt3yreader.mt3yApi.mt8hexasc;
import static com.example.mt3yreader.mt3yApi.mt8rfauthentication;
import static com.example.mt3yreader.mt3yApi.mt8rfcard;
import static com.example.mt3yreader.mt3yApi.mt8rfread;
import static java.lang.Integer.parseInt;

/**
 * 明泰在A64上读卡演示
 * 如果设备申请权限的时候不希望弹出对话框需要修改SystemUI
 * 只能读A64，参考mingtaicardreader 可以在多平台读取
 */
@Deprecated
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Bind(R.id.tv_status)
    TextView mTvStatus;
    @Bind(R.id.edit_section)
    EditText mEditSection;
    @Bind(R.id.edit_blockaddr)
    EditText mEditBlockaddr;
    @Bind(R.id.edit_secret)
    EditText mEditSecret;
    @Bind(R.id.tv_content)
    TextView mTextView;
    @Bind(R.id.activity_main)
    LinearLayout mActivityMain;
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init2();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        int st = mt8deviceclose();
        if (st == 0) {
            Log.d(TAG, "关闭设备成功");
        } else {
            Log.d(TAG, "关闭设备失败");
        }
    }

    //这个获取设备权限方法在6.0不起作用
    private void init() {
        new Thread() {
            @Override
            public void run() {
                new RootCommand().executeCommands("chmod 777 /dev/bus/usb/001/*");
                new Handler(Looper.getMainLooper()).post(getAuthorityResult);
            }
        }.start();

    }

    private void init2() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mBroadcastReceiver, intentFilter);
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        int number = 0;
        for (UsbDevice device : deviceList.values()) {
            if (device.getVendorId() == 0x23a4 && device.getProductId() == 0x0219) {
                mTvStatus.setText("请求权限");
                number++;
                usbManager.requestPermission(device, pendingIntent);
                break;
            }
        }
        if (number == 0) {
            mTextView.setText("没有找到明泰设备");
        }
    }

    @Deprecated
    private Runnable getAuthorityResult = new Runnable() {
        @Override
        public void run() {
            int mDev = mt8deviceopen(0, 115200);
            if (mDev > 0) {
                mTvStatus.setText("打开设备成功");
                Log.d(TAG, "打开设备成功" + mDev);
                int st = mt8devicebeep(258, 1);
                if (st == 0) {
                    mTvStatus.append(" 设备BEEP成功");
                } else {
                    mTvStatus.append(" 设备BEEP失败");
                }
            } else {
                mTvStatus.setText("打开设备失败 请允许应用程序访问设备");
            }

        }
    };
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                            UsbDeviceConnection connection = usbManager.openDevice(device);
                            if (connection != null) {
                                int fileDescriptor = connection.getFileDescriptor();
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

    @OnClick({R.id.btn_id, R.id.btn_social, R.id.btn_M1,
            R.id.btn_shanghai_social_card})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_id:
                mTextView.setText("开始读身份证\n");
                readId();
                break;
            case R.id.btn_social:
                mTextView.setText("开始读社保卡\n");
                readSocialCardInfo();
                break;
            case R.id.btn_M1:
                mTextView.setText("开始读M1卡\n");
                String secret = mEditSecret.getText().toString().trim();
                String blockAddress = mEditBlockaddr.getText().toString().trim();
                String section = mEditSection.getText().toString().trim();
                if (TextUtils.isEmpty(blockAddress) || TextUtils.isEmpty(section)) {
                    mTextView.append("扇区号或块地址不能为空");
                    return;
                }
                findM1();
                vertify(parseInt(blockAddress), parseInt(section), secret);
                read(parseInt(blockAddress), parseInt(section));
                break;
            case R.id.btn_shanghai_social_card:
                mTextView.setText("开始读上海社保卡\n");
                cpuReset();
                break;


        }
    }

    private void cpuReset() {
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
            String result = sendCMD("00A404000F7378312E73682EC9E7BBE1B1A3D5CF");
            if ("6A82".equalsIgnoreCase(result)) {
                result=sendCMD("00A4020002EF05" );
                if ("9000".equals(result)) {
                    result=sendCMD("00B207040b");
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

    //CPU操作卡片命令触发函数
    public String sendCMD(String strsend) {
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


    /**
     * 读身份证
     */
    private int readId() {
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

    //获取社保卡基本信息
    public void readSocialCardInfo() {
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
                Log.d(TAG, StrSocialCardBasicInfo);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "读取社保卡基本信息异常");
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

    private void findM1() {
        byte[] snr_asc = new byte[40];
        byte[] snr = new byte[20];
        byte[] cardtype = new byte[8];

        int ndelaytime = 0;
        int st = mt8rfcard(ndelaytime, cardtype, snr);
        if (st == 0) {
            mt8hexasc(snr, snr_asc, 4);
            String tip = new String(snr_asc);
            Log.d(TAG, tip);
            mTextView.append("寻卡成功\n");
        } else {
            mTextView.append("寻卡失败\n");
        }
    }

    private void vertify(int addr, int nSector, String str_key) {
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

    private void read(int addr, int nSector) {
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
                Log.d(TAG, "读数据成功" + str_data);
                String result = format(str_data.trim());
               /* int length = str_data.length();
                List<String> numbers = new ArrayList<>();
                int result = 0;
                //读取的字符串需要转化成卡号，按2个字符长度分隔，转化成整数减去30才是真正卡号数字
                for (int i = 0; i < length / 2; i++) {
                    String number = str_data.substring(2 * i, 2 * (i + 1));
                    if (TextUtils.isDigitsOnly(number)) {
                        result = Integer.parseInt(number) - 30;
                        if (result >= 0) {
                            numbers.add(result + "");
                        }
                    }
                }*/

                mTextView.append("卡号：" +result);
            } catch (UnsupportedEncodingException e) {
                mTextView.append("M1卡数据编码失败");
                e.printStackTrace();
            }
        } else {
            mTextView.append("M!读数据失败");
        }
    }
    //格式化卡号
    private String format(String primitive) {
        if (TextUtils.isEmpty(primitive)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        char c;
        int length=primitive.length();
        //读取的字符串需要转化成卡号，按2个字符长度分隔，转化成整数减去30才是真正卡号数字
        for (int i = 0; i < length / 2; i++) {
            String number = primitive.substring(2 * i, 2 * (i + 1));
            if (TextUtils.isDigitsOnly(number)) {
                c= (char) Integer.parseInt(number,16);
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
    /*
     * 获取性别信息
	 */
    public String getsexinfo(byte bsex[]) {
        String StrSexInfo = "";
        if (bsex[0] == 0x30) {
            StrSexInfo = "未知";
        } else if (bsex[0] == 0x31) {
            StrSexInfo = "男";
        } else if (bsex[0] == 0x32) {
            StrSexInfo = "女";
        } else if (bsex[0] == 0x39) {
            StrSexInfo = "未说明";
        } else {
            StrSexInfo = " ";
        }
        return StrSexInfo;
    }

    /*
     * 获取名族信息
	 */
    public String getnation(byte bNationinfo[]) {
        String StrNation = "";
        int nNationNo = 0;

        int nationcode = 0;
        nNationNo = (bNationinfo[0] - 0x30) * 10 + bNationinfo[2] - 0x30;
        switch (nNationNo) {
            case 1:
                StrNation = "汉";
                break;
            case 2:
                StrNation = "蒙古";
                break;
            case 3:
                StrNation = "回";
                break;
            case 4:
                StrNation = "藏";
                break;
            case 5:
                StrNation = "维吾尔";
                break;
            case 6:
                StrNation = "苗";
                break;
            case 7:
                StrNation = "彝";
                break;
            case 8:
                StrNation = "壮";
                break;
            case 9:
                StrNation = "布依";
                break;
            case 10:
                StrNation = "朝鲜";
                break;
            case 11:
                StrNation = "满";
                break;
            case 12:
                StrNation = "侗";
                break;
            case 13:
                StrNation = "瑶";
                break;
            case 14:
                StrNation = "白";
                break;
            case 15:
                StrNation = "土家";
                break;
            case 16:
                StrNation = "哈尼";
                break;
            case 17:
                StrNation = "哈萨克";
                break;
            case 18:
                StrNation = "傣";
                break;
            case 19:
                StrNation = "黎";
                break;
            case 20:
                StrNation = "傈僳";
                break;
            case 21:
                StrNation = "佤";
                break;
            case 22:
                StrNation = "畲";
                break;
            case 23:
                StrNation = "高山";
                break;
            case 24:
                StrNation = "拉祜";
                break;
            case 25:
                StrNation = "水";
                break;
            case 26:
                StrNation = "东乡";
                break;
            case 27:
                StrNation = "纳西";
                break;
            case 28:
                StrNation = "景颇";
                break;
            case 29:
                StrNation = "柯尔克孜";
                break;
            case 30:
                StrNation = "土";
                break;
            case 31:
                StrNation = "达斡尔";
                break;
            case 32:
                StrNation = "仫佬";
                break;
            case 33:
                StrNation = "羌";
                break;
            case 34:
                StrNation = "布朗";
                break;
            case 35:
                StrNation = "撒拉";
                break;
            case 36:
                StrNation = "毛南";
                break;
            case 37:
                StrNation = "仡佬";
                break;
            case 38:
                StrNation = "锡伯";
                break;
            case 39:
                StrNation = "阿昌";
                break;
            case 40:
                StrNation = "普米";
                break;
            case 41:
                StrNation = "塔吉克";
                break;
            case 42:
                StrNation = "怒";
                break;
            case 43:
                StrNation = "乌孜别克";
                break;
            case 44:
                StrNation = "俄罗斯";
                break;
            case 45:
                StrNation = "鄂温克";
                break;
            case 46:
                StrNation = "德昂";
                break;
            case 47:
                StrNation = "保安";
                break;
            case 48:
                StrNation = "裕固";
                break;
            case 49:
                StrNation = "京";
                break;
            case 50:
                StrNation = "塔塔尔";
                break;
            case 51:
                StrNation = "独龙";
                break;
            case 52:
                StrNation = "鄂伦春";
                break;
            case 53:
                StrNation = "赫哲";
                break;
            case 54:
                StrNation = "门巴";
                break;
            case 55:
                StrNation = "珞巴";
                break;
            case 56:
                StrNation = "基诺";
                break;
            case 57:
                StrNation = "其他";
                break;
            case 58:
                StrNation = "外国血统中国籍人士";
                break;
            default:
                StrNation = " ";
                break;
        }
        return StrNation;
    }

}
