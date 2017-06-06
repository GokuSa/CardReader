package com.example.mtreader;


 /* boolean isroot = (RootCmd.haveRoot());
        RootCmd.execRootCmdSlient(
                "chmod 777 /dev/ttyS0;" +
                        "chmod 777 /dev/ttyS1;" +
                        "chmod 777 /dev/ttyS2;" +
                        "chmod 777 /dev/ttyS3;" +
                        "chmod 777 /dev/bus/;" +
                        "chmod 777 /dev/bus/usb/;" +
                        "chmod 777 /dev/bus/usb/0*;" +
                        "chmod 777 /dev/bus/usb/001*//*;" +
                        "chmod 777 /dev/bus/usb/002*//*;" +
                        "chmod 777 /dev/bus/usb/003*//*;" +
                        "chmod 777 /dev/bus/usb/004*//*;" +
                        "chmod 777 /dev/bus/usb/005*//*;");*/
import android.util.Log;

import java.io.DataOutputStream;
import java.io.OutputStream;

public final class RootCmd {
    private static final String TAG = "RootCmd";
    private static boolean mHaveRoot = false;

    //执行linux命令并输出结果
    public static int execRootCmdSlient(String paramString) {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    (OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            //localObject = localProcess.exitValue();
            //return -1;
            return localProcess.exitValue();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return 0;
    }


    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSlient("echo test"); // 通过执行测试命令来检测
            if (ret != -1) {
                Log.i(TAG, "have root!");
                mHaveRoot = true;
            } else {
                Log.i(TAG, "not root!");
            }
        } else {
            Log.i(TAG, "mHaveRoot = true, have root!");
        }
        return mHaveRoot;
    }

}
