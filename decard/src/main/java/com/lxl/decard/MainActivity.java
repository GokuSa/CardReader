package com.lxl.decard;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.decard.NDKMethod.BasicOper;

import static com.lxl.decard.MDSEUtils.isSucceed;
import static com.lxl.decard.MDSEUtils.returnResult;

/**
 * 使用德卡读取聊城东昌妇幼保健医院M1卡 示例
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    /**
     * A套密码
     */
    int keyType=0;
    /**
     * 磁卡数据在扇区6，
     */
    int sector=6;
    /**
     * 磁卡特有的密钥
     */
    private String key = "DCFEAD195209";

    /**
     * 连接方式,android usb
     * 除此有蓝牙和串口
     */
    private String connectType = "AUSB";
    private TextView mStatus;
    private TextView mResult;

    //801设备上需要这两个库
    static {
        if (Build.VERSION.SDK_INT == 15) {
            System.loadLibrary("wlt2bmp");
            System.loadLibrary("dcrf32");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatus = (TextView) findViewById(R.id.tv_status);
        mResult = (TextView) findViewById(R.id.tv_result);
        BasicOper.dc_WakeDevice();
        BasicOper.dc_AUSB_ReqPermission(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BasicOper.dc_RestDevice();
    }

    /**
     * 第一步连接
     * @param view
     */
    public void connect(View view) {
        Log.d(TAG, "connect() called with");
        int portSate = BasicOper.dc_open( connectType, this, "", keyType);
        Log.d(TAG, "portSate:" + portSate);
        if (portSate >= 0) {
            mStatus.setText("连接成功");
            BasicOper.dc_beep(5);
            //加载密钥
            String keyHex = BasicOper.dc_load_key_hex(keyType, sector, key);
            boolean succeed = isSucceed(keyHex);
            if (succeed) {
                mStatus.append(" 密钥加载成功 ");
            }
            Log.d(TAG, "succeed:" + succeed);
        }
    }


    /**
     * 第二部读卡
     * @param view
     */
    public void readM1(View view) {
        //认证
        if (!MDSEUtils.isSucceed(BasicOper.dc_card_hex(0))) {
            Log.d(TAG, "nocard");
            mResult.setText("没有发现M1卡");
            return;
        }
        String dcAuthentication = BasicOper.dc_authentication(keyType, sector);
        boolean succeed = isSucceed(dcAuthentication);
        if (succeed) {
            mResult.setText("验证成功");
        }else{
            mResult.setText("验证失败");
        }
        Log.d(TAG, "succeed:" + succeed);
        String pieceData = returnResult(BasicOper.dc_read_hex(sector*4));
        Log.d(TAG,"pieceData"+ pieceData);
        if (!TextUtils.isEmpty(pieceData) && pieceData.length() > 10) {
            String id=String.format(" 卡号为：ZL%s %s %s",
                    pieceData.substring(0,2),
                    pieceData.substring(2,6),
                    pieceData.substring(6,10));
            mResult.append(id);
        }

    }



}
