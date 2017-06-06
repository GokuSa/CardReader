package com.shine.a64cardreader;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by 李晓林 on 2016/11/30
 * qq:1220289215
 * 获取系统权限工具
 * ROM的操作系统和普通手机获取超级权限的方式不一样
 *
 * 系统使用socket开启一个端口监听客户端指令输入，从而执行超级用户权限
 * 这个就是su_server，在开机脚本里启动，
 * 如果这个进程死掉无法连接,可以在shell里通过以下命令启动su_server
 * export CLASSPATH=/extdata/local/tmp/class.jar
 * app_process /extdata/loca/tmp/ cn.shine.suserver.Server &
 *
 * 需要在manifest中加入Internet权限
 */

public class RootCommand {
    private static final String TAG = "RootCommand";
    private static final String IP = "127.0.0.1";
    private static final int PORT = 4757;
    private static final String PWD = "SHINE_USER";
    private static final String NO_ERROR = "0";
    private static final String HEAD_EXEC = "EXEC_CMD|";
    private static final String HEAD_MOVE_FILE = "MOVE_FILE|";
    private DataOutputStream mDataOutputStream;
    private DataInputStream mDataInputStream;

    //获取超级管理员权限
    private boolean connect() {
        try {
            Socket socket = new Socket(IP, PORT);
            mDataOutputStream = new DataOutputStream(socket.getOutputStream());
            mDataInputStream = new DataInputStream(socket.getInputStream());
            mDataOutputStream.writeUTF(PWD);
            return NO_ERROR.equals(mDataInputStream.readUTF());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return false;
    }

    public boolean executeCommand(String command) {
        if (connect()) {
            try {
                mDataOutputStream.writeUTF(HEAD_EXEC+command);
                return NO_ERROR.equals(mDataInputStream.readUTF());
            } catch (IOException e) {
                Log.e(TAG,"get IOException while execute "+command);
            }finally {
                close(mDataInputStream,mDataOutputStream);
            }
        }else{
            close(mDataInputStream,mDataOutputStream);
        }
        return false;
    }

    /**
     * 执行多个命令
     * @param commands
     */
    public void executeCommands(String ...commands) {
        if (connect()) {
            boolean result=false;
            for (String command : commands) {
                try {
                    mDataOutputStream.writeUTF(HEAD_EXEC + command);
                    result=NO_ERROR.equals(mDataInputStream.readUTF());
                    Log.d(TAG, command+" 执行结果 "+result);
                } catch (IOException e) {
                    Log.d(TAG, command+" 执行中IOException");
                }
            }

        }
        close(mDataInputStream,mDataOutputStream);
    } /**
     * 执行多个命令
     * @param commands
     */
    public void executeCommands(List<String> commands) {
        if (connect()) {
            boolean result=false;
            for (String command : commands) {
                try {
                    mDataOutputStream.writeUTF(HEAD_EXEC + command);
                    result=NO_ERROR.equals(mDataInputStream.readUTF());
                    Log.d(TAG, command+" 执行结果 "+result);
                } catch (IOException e) {
                    Log.d(TAG, command+" 执行中IOException");
                }
            }

        }
        close(mDataInputStream,mDataOutputStream);
    }


    //关闭sockt流
    private void close(InputStream inputStream, OutputStream outputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
