package com.shine.printer_module;

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
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;

/**
 * author:
 * 时间:2017/5/16
 * qq:1220289215
 * 类描述：打印机助手 连接打印机设备，记录打印机连接状态，打印纸的情况
 */

public class PrinterHelper {
    private static final String TAG = PrinterHelper.class.getSimpleName();
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
    private volatile int mDeviceStatus = PrinterConstants.Connect.CLOSED;


    public PrinterHelper(Context context) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }
        mContext = context.getApplicationContext();
    }

    /**
     * 不能多次初始化，否则PrintUtil会重新初始化
     */
    public void init() {
        Log.d(TAG, "init() called");
        if (mPrintUtil == null) {
            mPrintUtil=new PrintUtil();
        }
        //监听设备卸载
        registerPrinterListener();
    }

    public void exit() {
        Log.d(TAG, "exit() called");
        mPrintUtil.closeConnection();
        mPrintUtil=null;
        mContext.unregisterReceiver(mPrintMountReceiver);
    }
    /**
     *在子线程中调用
     * 如打印机已连接，检测打印纸状态,如果正常则打印否则通过回调返回错误
     * 如果没有先通过回调返回结果再重连接
     * @param printContent 要打印的内容
     */

    public int print(@NonNull PrintContent2 printContent) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("can not call mothod in UI Thread");
        }
        if (mPrintUtil == null) {
            throw new NullPointerException("must call init() first");
        }
        //如果当前打印机状态不是成功
        if (mDeviceStatus != PrinterConstants.Connect.SUCCESS) {
            if (isConnecting) {
                Log.d(TAG, "正在连接打印机");
                return -1;
            }
            isConnecting = true;
            UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> devices = usbManager.getDeviceList();
//            找到打印机设备
            for (UsbDevice device : devices.values()) {
                int vendorId = device.getVendorId();
                int productId = device.getProductId();
                Log.d(TAG, "device:" + device);
                if ((0x0483 == vendorId && 0x5720 == productId) || (0x067B == vendorId && 0x2305 == productId)) {
                    mDevice = device;
                    break;
                }
            }
//            找到后连接
            if (mDevice != null) {
                if (mDeviceStatus != PrinterConstants.Connect.CLOSED) {
                    mPrintUtil.closeConnection();
                    setState(PrinterConstants.Connect.CLOSED);
                }
//                暂不考虑没有权限，不管是chmod还是修改SystemUI都让其默认有权限
                if (usbManager.hasPermission(mDevice)) {
                    boolean hasError = true;
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
                        UsbDeviceConnection connection = usbManager.openDevice(mDevice);
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
                }else {
                    // 没有权限询问用户是否授予权限
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    mContext.registerReceiver(mUsbReceiver, filter);
                    usbManager.requestPermission(mDevice, pendingIntent); // 该代码执行后，系统弹出一个对话框
                }

            } else {
                Log.d(TAG, "no printer device found");
                isConnecting=false;
                setState(PrinterConstants.Connect.NODEVICE);

            }
        }
//        连接打印机过程结束
        isConnecting=false;
        if (mDeviceStatus == PrinterConstants.Connect.SUCCESS) {
            int printerStatus = mPrintUtil.getPrinterStatus();
            if (PrinterConstants.Paper.NORMAL == printerStatus) {
                mPrintUtil.printInfo(mContext,printContent);
            }
            return printerStatus;
        }

        return mDeviceStatus;
    }
    private void registerPrinterListener() {
        Log.d(TAG, "registerPrinterListener() called");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(mPrintMountReceiver, filter);
    }



    /**
     * 设置当前打印机状态
     * @param currentState
     */
    private void setState(int currentState) {
        mDeviceStatus =currentState;
    }



    /**
     * 监听设备挂载和卸载
     * 挂载监听不到
     */
    private final BroadcastReceiver mPrintMountReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "receiver is: " + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.d(TAG, "printer attached");
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.d(TAG, "printer detached");
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null && mDeviceStatus == PrinterConstants.Connect.SUCCESS && device.equals(mDevice)) {
                    mPrintUtil.closeConnection();
                    setState(PrinterConstants.Connect.CLOSED);
                    if (mDevice != null) {
                        mDevice=null;
                    }
                }
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
//                        new Thread(mConnectRunnable).start();
                    } else {
                        setState(PrinterConstants.Connect.FAILED);
                        Log.e(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };
}
