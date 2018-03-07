package com.shine.cardreaderswitch;

import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android_serialport_api.SerialPort;

/**
 * 读卡设备开关控制类
 * 1.确保连接正确的串口，并且线连接正确
 */

public class CardReaderOperator {
    private static final String TAG = CardReaderOperator.class.getSimpleName();
    private static final String PATH = "/dev/ttyS4";
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private volatile boolean mStart = true;
    //波特率
    private static final int BAUDRATE = 9600;
    //获取读卡器状态，返回值byte13:0x02 循环读卡关闭，0x01 循环读卡打开，打开、关闭返回判断相同
    private String KEY_STATUS = "7E10070000000000000000000000000000000000000000000000000000000069AA";
    //    循环读卡打开，返回值判断同上
    private String KEY_OPEN = "7E10040100604d414b454752070100000000000000000000000000000000001AAA";
    //    循环读卡关闭
    private String KEY_CLOSE = "7E10040100604d414b454752000200000000000000000000000000000000001EAA";
    private final byte[] mStatus;
    private final byte[] mOpen;
    private final byte[] mClose;
    private AppExecutors mAppExecutors;
    private final OperatorListener mOperatorListener;

    public CardReaderOperator(AppExecutors appExecutors, OperatorListener operatorListener) {
        mAppExecutors = appExecutors;
        mOperatorListener = operatorListener;
        mStatus = hex2Bytes(KEY_STATUS);
        mOpen = hex2Bytes(KEY_OPEN);
        mClose = hex2Bytes((KEY_CLOSE));
        mAppExecutors.networkExecutor().execute(this::initialize);
    }


    public void sendInstruction(boolean open) {
        mAppExecutors.networkExecutor().execute(() -> {
            try {
                //初始化可能失败 导致为空
                if (mOutputStream != null) {
                    mOutputStream.write(open ? mOpen : mClose);
                    if (open) {
                        Log.d(TAG, "mOpen " + Arrays.toString(mOpen));
                    } else {
                        Log.d(TAG, "mClose " + Arrays.toString(mClose));
                    }
                }

            } catch (IOException e) {
                Log.d(TAG, e.toString());
                onError();
            }
        });
    }

    //十六进制转字节数组
    private byte[] hex2Bytes(String src) {
        byte[] res = new byte[src.length() / 2];
        char[] chs = src.toCharArray();
        int[] b = new int[2];

        for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
            for (int j = 0; j < 2; j++) {
                if (chs[i + j] >= '0' && chs[i + j] <= '9') {
                    b[j] = (chs[i + j] - '0');
                } else if (chs[i + j] >= 'A' && chs[i + j] <= 'F') {
                    b[j] = (chs[i + j] - 'A' + 10);
                } else if (chs[i + j] >= 'a' && chs[i + j] <= 'f') {
                    b[j] = (chs[i + j] - 'a' + 10);
                }
            }
            b[0] = (b[0] & 0x0f) << 4;
            b[1] = (b[1] & 0x0f);
            res[c] = (byte) (b[0] | b[1]);
        }

        return res;
    }

    @WorkerThread
    private void initialize() {
        File file = new File(PATH);
        if (!file.canRead() || !file.canWrite()) {
            RootCommand rootCommand = new RootCommand();
            rootCommand.exeCommand("chmod 666 " + file.getAbsolutePath());
        }
        Log.d(TAG, "can Read " + file.canRead() + "canWrite" + file.canWrite());
        if (file.canRead() && file.canWrite()) {
            try {
                mSerialPort = new SerialPort(file, BAUDRATE, 0);
                mInputStream = mSerialPort.getInputStream();
                //开启新线程循环读，否则阻塞下面代码
                mAppExecutors.networkExecutor().execute(this::startRead);
                mOutputStream = mSerialPort.getOutputStream();
                //获取当前读卡状态
                mOutputStream.write(mStatus);
                Log.d(TAG, "mStatus " + Arrays.toString(mStatus));
            } catch (IOException e) {
                e.printStackTrace();
                onError();
            }
        } else {
            Log.e(TAG, "打开串口失败");
            onError();
        }
    }

    private void onError() {
        mAppExecutors.mainHandler().execute(() -> {
            if (mOperatorListener != null) {
                mOperatorListener.failToCheck();
            }
        });
    }

    //循环读取数据
    private void startRead() {
        mStart = true;
        while (mStart) {
            if (mInputStream != null) {
                try {
                    byte[] buffer = new byte[mInputStream.available()];
                    int len = mInputStream.read(buffer);
                    if (len > 0 && buffer.length == 33) {
                        handleRead(buffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError();
                }
            }
            SystemClock.sleep(200);
        }
    }

    private void handleRead(byte[] buffer) {
        Log.d(TAG, "result " + Arrays.toString(buffer));
        switch (buffer[31]) {
            //查询状态的回复，由返回的数据buffer[0-30],31个数据异或所得
            case 1:
            case 2:
                mAppExecutors.mainHandler().execute(() -> {
                    mOperatorListener.currentStatus(0x01 == buffer[13]);
                });
                break;
            //open 的回复
            case 11:
                mAppExecutors.mainHandler().execute(() -> {
                    //  如果是打开：检测第14个字节是否等于ox01，
                    mOperatorListener.operatorResult(0x01 == buffer[13]);
                });
                break;
//                                close 的回复
            case 15:
                mAppExecutors.mainHandler().execute(() -> {
                    //  如果是打开：检测第14个字节是否等于ox01，
                    mOperatorListener.operatorResult(0x02 == buffer[13]);
                });
                break;
        }
    }

    //退出时关闭窗口和线程池
    public void exit() {
        mStart = false;
        if (mSerialPort != null) {
            mSerialPort.close();
        }
    }


    public interface OperatorListener {
        //开关结果，是否操作成功
        void operatorResult(boolean success);

        //当前寻卡状态 是否开启
        void currentStatus(boolean isOn);

        //检测失败，可能串口问题等等
        void failToCheck();
    }

}
