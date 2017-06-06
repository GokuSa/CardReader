package shine.com.huataicard;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mtreader.MainActivity;
import com.example.mtreader.RootCommand;
import com.synjones.bluetooth.DecodeWlt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * 明泰在801和R16上读卡演示
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";
    private TextView mTextView;
    private TextView mTextViewStatus;
    private EditText mEditTextBlockAddress;
    private EditText mEditTextSecret;
    private EditText mEditTextSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_id).setOnClickListener(this);
        findViewById(R.id.btn_social).setOnClickListener(this);
        findViewById(R.id.btn_M1).setOnClickListener(this);
        findViewById(R.id.btn_list_devices).setOnClickListener(this);
        mTextView = (TextView) findViewById(R.id.tv_content);
        mTextViewStatus = (TextView) findViewById(R.id.tv_status);
        mEditTextBlockAddress = (EditText) findViewById(R.id.edit_blockaddr);
        mEditTextSecret = (EditText) findViewById(R.id.edit_secret);
        mEditTextSection = (EditText) findViewById(R.id.edit_section);
        init();
    }


    //操作设备文件时需要权限
    private void init() {
        new Thread() {
            @Override
            public void run() {
                new RootCommand().executeCommands("chmod 777 /dev/bus/usb/001/*");
                new Handler(Looper.getMainLooper()).post(getAuthorityResult);
            }
        }.start();
    }

    private Runnable getAuthorityResult = new Runnable() {
        @Override
        public void run() {
            int mDev = MainActivity.mt8deviceopen(0, 115200);
            if (mDev > 0) {
                mTextViewStatus.setText("打开设备成功");
                Log.d(TAG, "打开设备成功" + mDev);
                int st = MainActivity.mt8devicebeep(258, 1);//(258,1,unicode_data);
                if (st == 0) {
                    mTextViewStatus.append(" 设备BEEP成功");
                } else {
                    mTextViewStatus.append(" 设备BEEP失败");
                }
            } else {
                mTextViewStatus.setText("打开设备失败 请允许应用程序访问设备");
            }

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_id:
                mTextView.setText("开始读身份证\n");
                readId();
                break;
            case R.id.btn_social:
                mTextView.setText("开始读社保卡\n");
                readSocialCard();
                break;
            case R.id.btn_M1:
                mTextView.setText("开始读M1卡\n");
                findM1();
                String secret = mEditTextSecret.getText().toString().trim();
                String blockAddress = mEditTextBlockAddress.getText().toString().trim();
                String section = mEditTextSection.getText().toString().trim();
                if (TextUtils.isEmpty(blockAddress) || TextUtils.isEmpty(section)) {
                    mTextView.append("扇区号或块地址不能为空");
                    return;
                }
                vertify(parseInt(blockAddress), parseInt(section), secret);
                read(parseInt(blockAddress), parseInt(section));
                break;
            case R.id.btn_list_devices:
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
//                ArrayList<UsbDevice> usbDevices = new ArrayList<>();
                for (UsbDevice usbDevice : deviceList.values()) {
                    Log.d(TAG, "use devices:" + usbDevice);
                }
                break;
        }
    }


    private int readId() {
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
                String StrSex = getSexinfo(szSex);
                String StrNation = getNation(szNation);
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

                StrWltFilePath = getFileStreamPath("photo.wlt").getAbsolutePath();
                StrBmpFilePath = getFileStreamPath("photo.bmp").getAbsolutePath();

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

    private void readSocialCard() {
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

    private void findM1() {
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

    private void vertify(int addr, int nSector, String str_key) {
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
        int st = MainActivity.mt8rfread((char) addr, rdata);
        if (st == 0) {
            MainActivity.mt8hexasc(rdata, rdata_asc, 16);
            try {
                String str_data = new String(rdata_asc, "GB2312");
                Log.d(TAG, "读数据成功" + str_data);
                int length=str_data.length();
                List<String> numbers=new ArrayList<>();
                int result=0;
                //读取的字符串需要转化成卡号，按2个字符长度分隔，转化成整数减去30才是真正卡号数字
                for (int i = 0; i < length / 2; i++) {
                    String number=str_data.substring(2*i,2*(i+1));
                    if (TextUtils.isDigitsOnly(number)) {
                        result= Integer.parseInt(number)-30;
                        if (result >= 0) {
                            numbers.add(result+"");
                        }
                    }
                }

                mTextView.append("卡号："+TextUtils.join("",numbers));
            } catch (UnsupportedEncodingException e) {
                mTextView.append("M1卡数据编码失败");
                e.printStackTrace();
            }
        } else {
            mTextView.append("M!读数据失败");
        }
    }


    /*
         * 获取性别信息
         */
    public String getSexinfo(byte bsex[]) {
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
    public String getNation(byte bNationinfo[]) {
        String StrNation = "";
        int nNationNo = (bNationinfo[0] - 0x30) * 10 + bNationinfo[2] - 0x30;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int st = MainActivity.mt8deviceclose();
        if (st == 0) {
            Log.d(TAG, "关闭设备成功");
        } else {
            Log.d(TAG, "关闭设备失败");
        }
    }
}
