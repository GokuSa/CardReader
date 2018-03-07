package com.shine.printer_module;

/**
 * Created by 李晓林 qq:1220289215 on 2016/11/2.
 */

public interface PrintListener {
    void onError(int code, String msg);
    void onConnectSucceed();
    void onPrintSucceed();
}
