package com.shine.cardreaderswitch;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by 李晓林 on 2016/11/30
 * qq:1220289215
 * 获取系统权限工具
 * ROM的操作系统和普通手机获取超级权限的方式不一样
 *
 * 系统使用socket开启一个端口监听客户端指令输入，从而执行超级用户权限
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
    private OnResultListener mOnResultListener;

    private void setListener(OnResultListener onResultListener) {
        this.mOnResultListener=onResultListener;

    }
    //给指定文件路径授权
    public boolean grand(String path) {
        DataOutputStream dos=null;
        DataInputStream dis=null;
        boolean result=false;
        try {
            Socket socket = new Socket(IP, PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            //输入协议密码登录
            dos.writeUTF(PWD);
           result= dis.readUTF().equals(NO_ERROR);
            Log.d(TAG, "login result :" + result);
            if (result) {
                dos.writeUTF(HEAD_EXEC+"chmod 666 " +path);
                result = dis.readUTF().equals(NO_ERROR);
                Log.d(TAG, "grant result "+result);
                if (mOnResultListener != null) {
                    mOnResultListener.onResult(result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            close(dis,dos);
        }

        return result;
    }

    //同步硬件时钟
    public  boolean checkTime() {
        DataOutputStream dos=null;
        DataInputStream dis=null;
        boolean result=false;
        try {
            Socket socket = new Socket(IP, PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            //输入协议密码登录
            dos.writeUTF(PWD);
            result= dis.readUTF().equals(NO_ERROR);
            Log.d(TAG, "login result :" + result);
            if (result) {
                dos.writeUTF(HEAD_EXEC+" busybox hwclock -f /dev/rtc1 -w");
                result = dis.readUTF().equals(NO_ERROR);
                Log.d(TAG, "hwclock1 result "+result);
                dos.writeUTF(HEAD_EXEC+" busybox hwclock -f /dev/rtc0 -w");
                result = dis.readUTF().equals(NO_ERROR);
                Log.d(TAG, "hwclock0 result "+result);
                if (mOnResultListener != null) {
                    mOnResultListener.onResult(result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            close(dis,dos);
        }

        return result;
    }

    public boolean exeCommand(String command) {
        DataOutputStream dos = null;
        DataInputStream dis = null;
        boolean result = false;
        try {
            Socket socket = new Socket(IP, PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            //输入协议密码登录
            dos.writeUTF(PWD);
            result = dis.readUTF().equals(NO_ERROR);
            Log.d(TAG, "login result :" + result);
            if (result) {
                dos.writeUTF(HEAD_EXEC + command);
                result = dis.readUTF().equals(NO_ERROR);
                Log.d(TAG, "execute result " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(dis, dos);
        }

        return result;
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


    interface OnResultListener{
        void onResult(boolean result);
    }
}
