package com.shine.printer_module;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 李晓林 qq:1220289215 on 2016/11/2.
 * 连接打印机设备，记录打印机连接状态，打印纸的情况
 * 使用PrinterHelper替代此类
 */
@Deprecated
public class ShineUsbOperation {
    private static final String TAG = "ShineUsbOperation";
    /**
     * 当设备无权限时需向system申请
     */
    private static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
    /**
     * 上下文 一般是application context 防止内存泄漏
     */
    private Context mContext;
    /**
     * 连接的打印机设备
     */
    private UsbDevice mDevice;
    /**
     * 是否正在连接，防止重复连接
     */
    private boolean isConnecting = false;
    /**
     * 负责与打印相关的重要类
     */
    private PrintUtil mPrintUtil;
    /**
     * 当期打印机状态
     */
    private int status = PrinterConstants.Connect.CLOSED;
    @SuppressLint("StaticFieldLeak")
    /**
     * 向外提供的操作打印的单例
     */
    private static ShineUsbOperation sShineUsbOperation;
    /**
     * 连接和打印状态监听
     */
    private PrintListener mPrintListener;
    /**
     * 由于连接打印是异步的所以需要Handler向主线程汇报结果
     */
    @SuppressWarnings("handlerleak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    Log.d(TAG, "connect success");
                    isConnecting=false;
                    status = msg.what;
                    if (mPrintListener != null) {
                        mPrintListener.onConnectSucceed();
                    }else{
                        Toast.makeText(mContext, "连接打印机成功", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnecting=false;
                    status = msg.what;
                    if (mPrintListener != null) {
                        mPrintListener.onError(status,"连接打印机失败 请检查设备");
                    }else{
                        Toast.makeText(mContext, "连接打印机失败 请检查设备", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "failed:");
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnecting=false;
                    status = msg.what;
                    if (mPrintListener != null) {
                        mPrintListener.onError(status,"打印机关闭或断开连接，请检查");
                    }else{
                        Toast.makeText(mContext, "打印机关闭或断开连接，请检查", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "R.string.conn_closed:");
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isConnecting=false;
                    status = msg.what;
                    if (mPrintListener != null) {
                        mPrintListener.onError(status,"没有打印设备");
                    }else{
                        Toast.makeText(mContext, "没有打印设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PrinterConstants.Connect.RECONNECT:
                    searchDeviceToConnect();
                    break;
                case PrinterConstants.Paper.CONNECT_EXCEPTION:
                    mPrintUtil.closeConnection();
                    if (mPrintListener != null) {
                        mPrintListener.onError(status,"打印机异常，请检查");
                    }else{
                        Toast.makeText(mContext, "打印机打印机异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PrinterConstants.Paper.NORMAL:
                    PrintContent2 printContent= (PrintContent2) msg.obj;
//                    mPrintUtil.executePrint(printContent);
                    mPrintUtil.printInfo(mContext,printContent);
                    if (mPrintListener != null) {
                        mPrintListener.onPrintSucceed();
                    }
                    break;
                case PrinterConstants.Paper.OUT_OF_PAPER:
                    if (mPrintListener != null) {
                        mPrintListener.onError(msg.what,"缺纸，请联系工作人员");
                    }else{
                        Toast.makeText(mContext, "缺纸，请联系工作人员", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PrinterConstants.Paper.PAPER_WILL_BE_OUT:
                    Log.d(TAG, "paper will be out!");
                    break;
                case PrinterConstants.Paper.COVER_OPEN:
                    Log.d(TAG, "cover opened!");
                    break;
            }
        }
    };

    private ShineUsbOperation(Context context) {
        mContext = context.getApplicationContext();
    }

    public static synchronized ShineUsbOperation getInstance(Context context) {
        if (sShineUsbOperation == null) {
            sShineUsbOperation = new ShineUsbOperation(context);
        }

        return sShineUsbOperation;
    }

    /**
     * 不能多次初始化，否则PrintUtil会重新初始化
     */
    public void init() {
        Log.d(TAG, "init() called");
        mPrintUtil=new PrintUtil();
        registerPrinterListener();
        searchDeviceToConnect();
    }

    private void registerPrinterListener() {
        Log.d(TAG, "registerPrinterListener() called");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(myReceiver, filter);
    }

    public void setListener(@NonNull PrintListener printListener) {
        mPrintListener=printListener;
    }

    public void cancelListener() {
        mPrintListener=null;
    }
    //自动连接打印机
    private void searchDeviceToConnect() {
        Log.d(TAG, "connect() called");
        if (isConnecting||status== PrinterConstants.Connect.SUCCESS) {
            Log.d(TAG, "正在连接打印机或已经连接");
            return;
        }
        isConnecting = true;
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = usbManager.getDeviceList();
        List<UsbDevice> deviceList = new ArrayList<>();
        for (UsbDevice device : devices.values()) {
            if (isUsbPrinter(device)) {
                deviceList.add(device);
            }
        }

        if (!deviceList.isEmpty()) {
            mDevice = deviceList.get(0);
        }
        if (mDevice != null) {
            connectPrinter();
        } else {
            setState(PrinterConstants.Connect.NODEVICE);
            Log.d(TAG, "no printer device found");
        }
    }

    /**
     * 判读是否是本厂打印机设备
     * @param device 打印机设备
     * @return
     */
    private boolean isUsbPrinter(UsbDevice device) {

        int vendorId = device.getVendorId();
        int productId = device.getProductId();
        Log.d(TAG, "device name: " + device.getDeviceName());
        Log.d(TAG, "vid:" + vendorId + " pid:" + productId);
        return (0x0483 == vendorId && 0x5720 == productId) || (0x067B == vendorId && 0x2305 == productId);
    }

    /**
     * 连接本厂的打印机设备
     * 如果没有权限会申请，一般会修改SystemUI 直接授权
     */
    private void connectPrinter() {
        if (status != PrinterConstants.Connect.CLOSED) {
            mPrintUtil.closeConnection();
            setState(PrinterConstants.Connect.CLOSED);
        }
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (isUsbPrinter(mDevice)) {
            if (usbManager.hasPermission(mDevice)) {
                new Thread(mConnectRunnable).start();
            } else {
                // 没有权限询问用户是否授予权限
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                mContext.registerReceiver(mUsbReceiver, filter);
                usbManager.requestPermission(mDevice, pendingIntent); // 该代码执行后，系统弹出一个对话框
            }
        } else {
            setState(PrinterConstants.Connect.FAILED);
        }
    }

    private  void setState(int state) {
        mHandler.obtainMessage(state).sendToTarget();

    }

    public void exit() {
        Log.d(TAG, "exit() called");
        mPrintUtil.closeConnection();
        mPrintUtil=null;
        mContext.unregisterReceiver(myReceiver);
    }

    /**
     *
     * 如打印机已连接，检测打印纸状态,如果正常则打印否则通过回调返回错误
     * 如果没有先通过回调返回结果再重连接
     * @param printContent 要打印的内容
     */
    public void print(@NonNull PrintContent2 printContent) {
        if (status == PrinterConstants.Connect.SUCCESS) {
            int ret = mPrintUtil.getPrinterStatus();
            mHandler.obtainMessage(ret,printContent).sendToTarget();
        } else {
            mHandler.obtainMessage(status).sendToTarget();
            mHandler.obtainMessage(PrinterConstants.Connect.RECONNECT).sendToTarget();
        }
    }

    private Runnable mConnectRunnable=new Runnable() {
        @Override
        public void run() {
            boolean hasError = true;
            UsbManager mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
            UsbEndpoint inEndpoint=null;
            UsbEndpoint outEndpoint=null;
            try {
                UsbInterface usbInterface = mDevice.getInterface(0);
                for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                    // USBEndpoint为读写数据所需的节点
                    UsbEndpoint ep = usbInterface.getEndpoint(i);
                    if (ep.getType() == 2) {
                        if (ep.getDirection() == 0) {
                            outEndpoint = ep;
                        } else {
                            inEndpoint = ep;
                        }
                    }
                }

                UsbDeviceConnection connection = mUsbManager.openDevice(mDevice);
                if (connection != null && connection.claimInterface(usbInterface, true)) {
                    hasError = false;
                    mPrintUtil.set(connection,inEndpoint,outEndpoint, usbInterface);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (hasError) {
                mPrintUtil.closeConnection();
                setState(PrinterConstants.Connect.CLOSED);
            } else {
                Log.d(TAG, "连接成功");
                setState(PrinterConstants.Connect.SUCCESS);
            }
        }
    };
    /**
     * 申请权限的结果回调
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.w(TAG, "receiver action: " + action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    mContext.unregisterReceiver(mUsbReceiver);
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mDevice.equals(device)) {
                        new Thread(mConnectRunnable).start();
                    } else {
                        setState(PrinterConstants.Connect.FAILED);
                        Log.e(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };
    /**
     * 监听设备挂载和卸载
     * 挂载监听不到
     */
    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "receiver is: " + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.d(TAG, "printer attached");
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.d(TAG, "printer detached");
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null && status== PrinterConstants.Connect.SUCCESS && device.equals(mDevice)) {
                    mPrintUtil.closeConnection();
                    setState(PrinterConstants.Connect.CLOSED);
                    if (mDevice != null) {
                        mDevice=null;
                    }
                }
            }
        }
    };
}
