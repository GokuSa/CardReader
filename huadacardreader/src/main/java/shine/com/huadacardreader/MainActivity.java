package shine.com.huadacardreader;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import shine.com.huadacardreader.util.CardInfo;

/**
 * 华大设备号mVendorId=1839,mProductId=37077
 * 此demo用来读取华大的身份证 社保 和M1卡
 * 修改SystemUI的UsbPermissionActivity，通过判断华大生产商id和产品id 直接允许华大设备连接
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    /**
     * 华大提供的读卡封装类
     */
    private CardInfo mCardInfo;
    /**
     * 显示连接和读卡结果
     */
    private TextView mTextView;
    /**
     * 连接次数计数器
     * 最多自动连接3次，如果都失败就放弃
     */
    private Button mButtonM1;
    private int count = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //打开设备，向系统申请连接权限，更改system ui后直接同意
                    mCardInfo.Open();
                    //查看是否连接成功
                    int ren = mCardInfo.DevStatus();
                    //如果连接不成功 2s后再连接一次，连续3次不成功就放弃
                    if (0 != ren) {
                        count++;
                        mTextView.setText("设备未连接成功");
                        if (count <= 3) {
                            mHandler.sendEmptyMessageDelayed(0, 2 * 1000);
                        }
                    } else {
                        count = 0;
                        mTextView.setText("设备连接成功");
                        onClick(mButtonM1);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonM1 = (Button) findViewById(R.id.btnReadM1);
        findViewById(R.id.btnReadM1).setOnClickListener(this);
        findViewById(R.id.btnReadSSC).setOnClickListener(this);
        findViewById(R.id.btnReadID).setOnClickListener(this);
        mTextView = (TextView) findViewById(R.id.tv_result);

        mCardInfo = new CardInfo(this);
        //连接读卡设备并检验结果
        mHandler.sendEmptyMessage(0);

    }

    @Override
    public void onClick(View v) {
        //先判断设备是否连接
        int ren = mCardInfo.DevStatus();
        if (0 != ren) {
            mHandler.sendEmptyMessage(0);
            return;
        }
        //用来清空身份证图片，否则读社保卡将依然存在
        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        switch (v.getId()) {
            case R.id.btnReadID:
                readID();
                break;
            case R.id.btnReadSSC:
                if (!mCardInfo.CheckSSCard()) {
                    mTextView.setText("检测不到社保卡");
                    break;
                }

                byte[] xm = new byte[100];
                byte[] shbzhm = new byte[100];
                byte[] shbzkh = new byte[100];

                ren = mCardInfo.ReadSSCard(xm, shbzhm, shbzkh);
                Log.d(TAG, "读社保卡ren:" + ren);
                if (ren != 0) {
                    mTextView.setText("读社保卡失败");
                    break;
                }
                String name = "";
                try {
                    name = new String(xm, "GBK");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, e.toString());
                }
                String socialSecurityNumber = new String(shbzhm);
                String cardNumber = new String(shbzkh);
                String socialSecurityCardInfo =
                        String.format(Locale.CHINA, "姓名 = %s\n 社会保障号码 = %s\n 卡号 = %s",
                                name, socialSecurityNumber, cardNumber);

                mTextView.setText(socialSecurityCardInfo);

                break;
            case R.id.btnReadM1:
                if (!mCardInfo.CheckM1()) {
                    mTextView.setText("检测不到M1卡");
                    break;
                }
                byte[] data = new byte[50];

                byte[] key = { (byte)0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
                //M1卡第一块第一扇区
                ren = mCardInfo.ReadM1(key, (byte) 0x01, (byte) 0x01, (byte) 0x05, data);
//                ren = mCardInfo.ReadM1(key, (byte) 0x01, (byte) 0x00, (byte) 0x00, data);
                if (0 != ren) {
                    mTextView.setText("读M1卡失败");
                    break;
                }
                String primary = new String(data);
                Log.d(TAG, "result--"+primary);
//                String result = format(primary.trim());
                mTextView.setText("M1卡第1块：--" +primary );
                break;
        }
    }
    //读身份证信息
    private void readID() {
        byte[] name = new byte[32];
        byte[] sex = new byte[6];
        byte[] birth = new byte[18];
        byte[] nation = new byte[12];
        byte[] address = new byte[72];
        byte[] Department = new byte[32];
        byte[] IDNo = new byte[38];
        byte[] EffectDate = new byte[18];
        byte[] ExpireDate = new byte[18];
        byte[] pErrMsg = new byte[20];
        byte[] BmpFile = new byte[38556];
        int retval;
        //识别身份证头像的库在包名下的lib文件夹中
        String pkName = getFilesDir().getParent() + "/lib/libwlt2bmp.so";
        try {
            retval = mCardInfo.ReadCert(BmpFile, name, sex, nation, birth, address, IDNo, Department,
                    EffectDate, ExpireDate, pErrMsg, pkName);
            Log.d(TAG, " 身份证 retval:" + retval);
            if (retval < 0) {
                mTextView.setText("读卡错误，原因：" + new String(pErrMsg, "Unicode"));
            } else {
                int[] colors = mCardInfo.convertByteToColor(BmpFile);
                Bitmap bm = Bitmap.createBitmap(colors, 102, 126, Bitmap.Config.ARGB_8888);
                //这里你可以自定义它的大小
                // Bitmap bm1=Bitmap.createScaledBitmap(bm, (int)(102*1),(int)(126*1), false);
                //使用imageview控件显示头像
//                ImageView imageView = new ImageView(this);
//                imageView.setScaleType(ImageView.ScaleType.MATRIX);
//                imageView.setImageBitmap(bm);
                //为了方便起见，这里直接使用textview显示头像 实际项目 根据需求显示
                BitmapDrawable drawable = new BitmapDrawable(bm);
                mTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                mTextView.setText("");
                mTextView.append("名字=" + new String(name, "Unicode"));
                mTextView.append("\n性别=" + new String(sex, "Unicode"));
                mTextView.append("\n民族=" + new String(nation, "Unicode"));
                mTextView.append("\n生日=" + new String(birth, "Unicode"));
                mTextView.append("\n地址=" + new String(address, "Unicode"));
                mTextView.append("\n身份证号=" + new String(IDNo, "Unicode"));
                mTextView.append("\n发卡机构=" + new String(Department, "Unicode"));
                mTextView.append("\n有效日期=" + new String(EffectDate, "Unicode") + "至" + new String(ExpireDate, "Unicode"));
            }

        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "读身份证异常" + e.toString());
            e.printStackTrace();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (mCardInfo != null) {
            mCardInfo.Close();
        }
    }
}
