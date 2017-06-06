package com.example.mtreader;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.OutputStream;

public final class RootCmd {
	private static final String TAG = "RootCmd"; 
    private static boolean mHaveRoot = false; 
	//执行linux命令并输出结果
	protected static int execRootCmdSlient(String paramString)
	{
		try{			
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
		}catch (Exception localException){
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
