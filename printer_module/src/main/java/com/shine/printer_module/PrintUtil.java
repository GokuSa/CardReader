package com.shine.printer_module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by 李晓林 qq:1220289215 on 2016/11/3.
 */

class PrintUtil {
    private static final String TAG = "PrintUtil";
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;

    public PrintUtil() {
    }


    void set(UsbDeviceConnection connection, UsbEndpoint inEndpoint, UsbEndpoint outEndpoint, UsbInterface usbInterface) {
        this.connection = connection;
        this.inEndpoint = inEndpoint;
        this.outEndpoint = outEndpoint;
        this.usbInterface = usbInterface;
    }

    public static boolean isNum(byte temp) {
        return temp >= 48 && temp <= 57;
    }

    public void init_print() {
        setPrinter(PrinterConstants.Command.INIT_PRINTER);
    }

    public boolean setPrinter(int command) {
        byte[] arrayOfByte = null;
        switch (command) {
            case PrinterConstants.Command.INIT_PRINTER: // 初始化打印机
                arrayOfByte = new byte[2];
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 64;
                break;
            case PrinterConstants.Command.WAKE_PRINTER: // 锟斤拷锟窖达拷印锟斤拷
                arrayOfByte = new byte[1];
                arrayOfByte[0] = 0;
                break;
            case PrinterConstants.Command.PRINT_AND_RETURN_STANDARD: // 打印并走纸到下页首
                arrayOfByte = new byte[1];
                arrayOfByte[0] = 12;
                break;
            case PrinterConstants.Command.PRINT_AND_NEWLINE: // 打印并走纸一行
                arrayOfByte = new byte[1];
                arrayOfByte[0] = 10;
                break;
            case PrinterConstants.Command.PRINT_AND_ENTER: // 打印并回车
                arrayOfByte = new byte[1];
                arrayOfByte[0] = 13;
                break;
            case PrinterConstants.Command.MOVE_NEXT_TAB_POSITION: // 横向跳格
                arrayOfByte = new byte[1];
                arrayOfByte[0] = 9;
                break;
            case PrinterConstants.Command.DEF_LINE_SPACING: // 锟斤拷锟斤拷默锟斤拷锟叫革拷
                arrayOfByte = new byte[2];
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 50;
        }

        sendByteData(arrayOfByte);
        return true;
    }

    public boolean setPrinter(int command, int value) {
        byte[] arrayOfByte = new byte[3];
        switch (command) {
            case PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LNCH: // 打印并走纸 ESC J n
                arrayOfByte[0] = 27; // 16十六进制 1B 4A n
                arrayOfByte[1] = 74; // 10十进制 27 74 n
                break;
            case PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE: // 打印并向前走纸n行 ESC d n
                arrayOfByte[0] = 27; // 16十六进制 1B 64 n
                arrayOfByte[1] = 100; // 10十进制 27 100 n
                break;
            case 2: // 选择打印模式  根据n的值设置字符打印模式  ESC ! n
                arrayOfByte[0] = 27; //  16十六进制 1B 21 n
                arrayOfByte[1] = 33; //  10十进制   27 33 n
                break;
            case 3: // 锟斤拷锟斤拷糯锟絥锟斤拷 ESC U n //选择/取消顺时针旋转90度 ESC V n
                arrayOfByte[0] = 27; //  16十六进制 1B 55 n 1B 56 n
                arrayOfByte[1] = 85; // 10十进制    27 85 n 27 86 n
                break;
            case PrinterConstants.Command.CLOCKWISE_ROTATE_90: // 选择/取消顺时针旋转90度， 0 -false(default)锟斤拷
                // 1-true
                arrayOfByte[0] = 27; //  16十六进制 1B 21 n
                arrayOfByte[1] = 86; // 10十进制 27 33 n
                break;
            case 5: // 锟斤拷锟斤拷锟斤拷锟斤拷糯锟? ESC W n
                arrayOfByte[0] = 27; //  16十六进制 1B 57 n
                arrayOfByte[1] = 87; // 10十进制 27 87 n
                break;
            case 6: // 选择/取消下划线模式
                arrayOfByte[0] = 27; // 16十六进制  1B 2D n
                arrayOfByte[1] = 45; // 10十进制    	27 45 n
                break;
            case 7: // 锟斤拷锟斤拷/锟斤拷止锟较伙拷锟竭达拷印 ESC + n
                arrayOfByte[0] = 27; // 1B 2B n
                arrayOfByte[1] = 43; // 27 43 n
                break;
            case 8: // 锟斤拷锟斤拷/锟斤拷止锟斤拷锟阶达拷印 ESC i n
                arrayOfByte[0] = 27; // 1B 69 n
                arrayOfByte[1] = 105; // 27 105 n
                break;
            case 9: // 选择汉字代码系统 ESC c n
                arrayOfByte[0] = 27; // 1B 63 n
                arrayOfByte[1] = 99; // 27 99 n
                break;
            case PrinterConstants.Command.LINE_HEIGHT: // 设置行高 4mm
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 51;
                break;
            case PrinterConstants.Command.CHARACTER_RIGHT_MARGIN: // 设置字符右间距
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 32;
                break;
            case 12: // 打印预存储位图
                arrayOfByte[0] = 28;
                arrayOfByte[1] = 80;
                break;
            case 13: // 选择字符对齐模式 0-左对齐,1-中间对齐,2-右对齐
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 97;
                if (value > 2 || value < 0) {
                    return false;
                }
                break;
            case PrinterConstants.Command.FONT_SIZE: // Set font size
                arrayOfByte[0] = 29; //  16十六进制 1D 21 n
                arrayOfByte[1] = 33; // 10十进制 29 33 n
                break;
            case PrinterConstants.Command.FONT_MODE: // Set font face
                arrayOfByte[0] = 27; //  16十六进制 1B 21 n
                arrayOfByte[1] = 33; // 10十进制 27 33 n
                break;
        }
        arrayOfByte[2] = (byte) value;
        sendByteData(arrayOfByte);
        return true;
    }

    public int printText(String content) {
        byte data[] = null;
        try {
            data = content.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sendByteData(data);
    }

    /**
     * set character enlargement multiple.<br/>
     * 0 is normal, 1 is two multiple of the old.
     *
     * @param x , landscape , 0 <= x <= 7
     * @param y , portrait , 0 <= y <= 7
     */
    public void setCharacterMultiple(int x, int y) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 29;
        arrayOfByte[1] = 33;

        if (0 <= x && x <= 7 && 0 <= y && y <= 7) {
            arrayOfByte[2] = (byte) (x * 16 + y);
            sendByteData(arrayOfByte);
        }
    }

    public int sendByteData(byte data[]) {
        if (data != null) {
            Log.d(TAG, "sendByteData length is: " + data.length);
            Log.d(TAG, Arrays.toString(data));
            return write(data);
        }
        return -1;
    }

    public int write(byte[] data) {
        if (connection != null) {
            return connection.bulkTransfer(outEndpoint, data, data.length, 3000);
        } else {
            return -1;
        }
    }

    /**
     * Define the font style which is used till the new font style is set.
     *
     * @param mWidth     defines the font width in n times against the normal font
     *                   width. 0 ~ 7, and 0 indicates the normal width.
     * @param mHeight    defines the font height in n times against the normal font
     *                   height. 0 ~ 7, and 0 indicates the normal height.
     * @param mBold      defines whether the bold or normal font face is used. 0 -
     *                   normal, and 1 - bold.
     * @param mUnderline defines whether the underline is used. 0 - normal, and 1 -
     *                   underlined.
     * @return 0 indicates success, and other indicates failure.
     */
    public int setFont(int mWidth, int mHeight, int mBold, int mUnderline) {
        int mFontSize = 0;
        int mFontMode = 0;
        int mRetVal = 0;

        if (mBold == 0 || mBold == 1) {
            mFontMode |= (mBold << 3);
        } else {
            mRetVal = 3;
        }

        if (mUnderline == 0 || mUnderline == 1) {
            mFontMode |= (mUnderline << 7);
        } else {
            mRetVal = 4;
        }

        setPrinter(PrinterConstants.Command.FONT_MODE, mFontMode);

        if (mWidth >= 0 && mWidth <= 7) {
            mFontSize |= (mWidth << 4);
        } else {
            mRetVal = 1;
        }

        if (mHeight >= 0 && mHeight <= 7) {
            mFontSize |= mHeight;
        } else {
            mRetVal = 2;
        }
        Log.e("jiangcy", "mWidth  = " + mWidth + "  ,mHeight = " + mHeight + "   Command.FONT_SIZE = " + mFontSize);
        setPrinter(PrinterConstants.Command.FONT_SIZE, mFontSize);

        return mRetVal;
    }


    public int printBarCode(Barcode barcode) {
        return sendByteData(barcode.getBarcodeData());
    }

    public int getPrinterStatus() {

        int Ret = 0;

        byte data = getData(4);//20060308
        Log.w("sprt", data + "");

        if ((data & 0x60) == 0x60) {
            Ret = 1;
        }//out of paper,halt

        if ((data & 0x0C) == 0x0c) {
            Ret = 2;
        }//paper will be out

        byte retData = getData(2);

        if ((retData & 0x08) != 0) {
            Ret = 3;//auto cutter has something wrong
        }
        return Ret;
    }

    public byte getData(int statusType) {
        byte data = -1;
        byte[] command = new byte[3];
        command[0] = 0x10;
        command[1] = 0x04;
        byte[] retData0 = new byte[8];// 返回的只有一个数据
        // 纸传感器状态
        try {
            switch (statusType) {
                case 4:
                    command[2] = 0x04;//n = 4: 纸传感器状态
                    break;
                case 3://脱机状态
                    command[2] = 0x03;//n = 4: 切刀状态
                default:
                    command[2] = 0x02;//n = 4: 脱机状态
                    break;
            }
            if (write1(command) == -1) {
                Log.w(TAG, "write error!");
                return -1;
            }
            Thread.sleep(50);

            byte[] retData;

            for (int i = 0; i < 2; i++) {

                retData = read1();//第一次读不到数据，需要读取两次
                if (retData == null || retData.length == 0) {
                    continue;
                } else {
                    return retData[retData.length - 1];
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    public int write1(byte[] data) {
        if (connection != null) {
            return connection.bulkTransfer(outEndpoint, data, data.length, 3000);
        } else {
            return -1;
        }
    }

    public byte[] read1() {
        // 读取数据1 两种方法读取数据
        if (connection != null) {
            byte[] retData = new byte[64];
            int readLen = connection.bulkTransfer(inEndpoint, retData, retData.length, 2000);
            Log.w(TAG, "read length:" + readLen);
            if (readLen > 0) {
                if (readLen == 64) {
                    return retData;
                } else {
                    byte[] realData = new byte[readLen];
                    System.arraycopy(retData, 0, realData, 0, readLen);
                    return realData;
                }
            }
        }

        return null;
    }

    //关闭连接
    void closeConnection() {
        Log.d(TAG, "closeConnection()");
        if (connection != null) {
            connection.releaseInterface(usbInterface);
            connection.close();
            connection = null;
        }
    }

    void executePrint(@NonNull PrintContent printContent) {
        init_print();
        setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
        setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        setFont(0, 0, 0, 0);
        setCharacterMultiple(1, 1);
        printText(printContent.getHospital());
        setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

        setFont(0, 0, 1, 0);
        setCharacterMultiple(0, 1);
        setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        printText("【科室名称】 " + printContent.getDepartment());
        setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        printText("【取号时间】 " + printContent.getTime());
        setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

        if (!TextUtils.isEmpty(printContent.getNumber())) {
            setFont(2, 2, 0, 0);
//            setCharacterMultiple(1, 0);
            setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
            printText(printContent.getNumber());
            setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        }

        setFont(0, 0, 1, 0);
        printText(String.format(Locale.CHINA, "等待人数（%s人）", printContent.getPeopleAhead()));
        setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        if (!TextUtils.isEmpty(printContent.getBarCode())) {
            Barcode barcode = new Barcode(PrinterConstants.BarcodeType.CODE128, 3, 60, 2, printContent.getBarCode());
            printBarCode(barcode);
            setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        }

        setFont(0, 0, 0, 0);
        printText("温馨提示：无证取号，过号后\n请重新取号，叫号时请出示此小票");
        setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 4);

        sendByteData(new byte[]{0x1B, 0x4A, (byte) 0xF0, 0x1D, 0x56,
                0x00, 0x1B, 0x4B, 0x00, (byte) 0xB0});
    }

    /**
     *
     * @param mContext
     * @param  printContent
     * @param
     */
    public  void printInfo(final Context mContext,PrintContent2 printContent)
    {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/huaweixinwei.ttf");
        init_print();
        printImage(getTicketOne(mContext, typeface,
                printContent.getAppInfo(),printContent.getAppTime(),
                printContent.getName(),printContent.getSex()));
       setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        sendByteData(new byte[]{0x1B, 0x61, 0x01});
       printText("--------------------------------------------");
        setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
       sendByteData(new byte[]{0x1B, 0x61, 0x01});
        Bitmap bitmaptwo = getTicketTwo(printContent.getAppNum());
        printImage(bitmaptwo);
       setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.CODE128, 3, 60, 2, printContent.getBarCode());
        Log.e(TAG,"barCode "+printContent.getBarCode());
        sendByteData(new byte[]{0x1B, 0x61, 0x01});
        printBarCode(barcode);
        setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        printImage(getTicketThree(typeface));
        setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        sendByteData(new byte[]{0x1B, 0x61, 0x01});
        printText("--------------------------------------------");
        setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        printImage(getTicketFour(mContext, typeface));
        setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE,2);
        sendByteData(new byte[]{0x1B, 0x4A, (byte) 0xF0, 0x1D, 0x56,
                0x00, 0x1B, 0x4B, 0x00, (byte) 0xB0});
    }

    public int printImage(Bitmap bitmap) {
        return sendByteData1(bitmap2PrinterBytes(bitmap, 0));
    }
    int count = 0;
    public int sendByteData1(byte data[]) {
        count += data.length;
        int packetSize = 1024*2;
        int num = data.length / packetSize;
        byte pack[] = new byte[packetSize];
        byte temp[] = new byte[data.length - packetSize * num];
        try {
            if (num >= 1) {
                for (int i = 0; i <= num - 1; i++) {
                    System.arraycopy(data, i * packetSize, pack, 0, packetSize);
                   write(pack);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.arraycopy(data, num * packetSize, temp, 0, data.length
                        - packetSize * num);
               write(temp);
            } else {
               write(data);
            }
        } catch (Exception e) {

            e.printStackTrace();
            return -1;
        }
        return 0;
    }
    /**
     * translate image to bytes that the printer can distinguish <br/>
     *
     * @param bitmap
     */
    public static byte[] bitmap2PrinterBytes(Bitmap bitmap, int left) {
        // 锟斤拷取实锟绞筹拷锟饺碉拷图片
        //Bitmap nbm = Bitmap.createBitmap(this.bm, 0, 0, this.width, this.getLength());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //
        byte[] imgbuf = new byte[((width / 8) + left / 8 + 4) * height];
        // 锟斤拷锟酵硷拷锟矫恳伙拷械锟斤拷纸锟斤拷锟�
        byte[] bitbuf = new byte[width / 8];
        int[] p = new int[8];
        int s = 0;

        System.out.println("+++++++++++++++ Total Bytes: " + ((width / 8) + 4) * height);


        // 锟斤拷图锟斤拷锟矫匡拷锟斤拷锟斤拷亟锟斤拷懈锟绞阶拷锟�
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width / 8; x++) {
                for (int m = 0; m < 8; m++) {
                    if (bitmap.getPixel(x * 8 + m, y) == -1) {
                        p[m] = 0;
                    } else {
                        p[m] = 1;
                    }
                }

                int value = p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8
                        + p[5] * 4 + p[6] * 2 + p[7];
                bitbuf[x] = (byte) value;

            }

            if (y != 0) {
                imgbuf[++s] = (byte) 0x16;
            } else {
                imgbuf[s] = (byte) 0x16;
            }
            imgbuf[++s] = (byte) (width / 8 + left / 8);

            for (int j = 0; j < left / 8; j++) {
                imgbuf[++s] = 0;
            }

            for (int n = 0; n < width / 8; n++) {
                imgbuf[++s] = bitbuf[n];
            }
            imgbuf[++s] = (byte) 0x15;
            imgbuf[++s] = (byte) 0x01;


        }
        Log.v("imgbuf", "数据长度:" + imgbuf.length);
        return imgbuf;
    }
    /**
     * 高度最高为186
     *
     * @param context
     * @return
     */
    private  Bitmap getTicketOne(Context context, Typeface typeface,String appInfo,String appTime,String name,String sex) {
        int w = 670;
        String logo_en = context.getString(R.string.logo_en);
        String logo_ch = context.getString(R.string.logo_ch);
        String appInfotitle = "预约信息";
        String apptimetitle = "预约时间";
        Bitmap ticket_logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.ticket_logo);
        Bitmap bitmap = Bitmap.createBitmap(w, w * 5, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        // 设置画布颜色为白色
        canvas.drawColor(Color.WHITE);
        Paint mPaint = new Paint();
        // 消除锯齿
        mPaint.setAntiAlias(false);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(40);
        mPaint.setTypeface(typeface);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(appInfotitle, 5, 95, mPaint);
        canvas.drawText(apptimetitle, 5, 140, mPaint);
        canvas.drawText("患者信息", 5, 185, mPaint);

        mPaint.setTextSize(22);
        canvas.drawText(logo_en, 415, 158, mPaint);
        canvas.drawText(logo_ch, 450, 185, mPaint);
        mPaint.setTextSize(25);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(appInfo, 180, 85, mPaint);
        canvas.drawText(appTime, 180, 130, mPaint);
        canvas.drawText(name+"     "+sex, 180, 175, mPaint);
        canvas.drawBitmap(ticket_logo, 420, 30, mPaint);
        return Bitmap.createBitmap(bitmap, 0, 0, w, 185);
    }

    /**
     * 高度最高为186
     *
     * @return
     */
    private  Bitmap getTicketTwo(String appNum) {
        int w = 576;
        String appNumtitle = "预约号";
        Rect mTextBound1 = new Rect();
        Rect mTextBound2 = new Rect();
        Bitmap bitmap = Bitmap.createBitmap(w, w * 5, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        // 设置画布颜色为白色
        canvas.drawColor(Color.WHITE);
        Paint mPaint = new Paint();;
        // 消除锯齿
        mPaint.setAntiAlias(false);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(false);
        mPaint.setTypeface(Typeface.SANS_SERIF);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(35);
        mPaint.setTextAlign(Paint.Align.CENTER);
//        mPaint.getTextBounds(appNumtitle, 0, appNumtitle.length(),
//                mTextBound1);
        canvas.drawText(appNumtitle,w/2, 35, mPaint);
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mPaint.setTextSize(140);
//        mPaint.getTextBounds(appNumtitle, 0, appNumtitle.length(),
//                mTextBound2);
        canvas.drawText(appNum,w/2, 180, mPaint);
        return Bitmap.createBitmap(bitmap, 0, 0,w, 185);
    }

    private  Bitmap getTicketThree(Typeface typeface) {
        int w = 600;
        String info1 = "请在就诊当天凭此条码";
        String info2 = "在诊室门口进行签到";
        String info3 = "否则预约无效";
        Rect mTextBound1 = new Rect();
        Rect mTextBound2 = new Rect();
        Rect mTextBound3 = new Rect();
        Bitmap bitmap = Bitmap.createBitmap(w, w * 5, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        // 设置画布颜色为白色
        canvas.drawColor(Color.WHITE);
        Paint mPaint = new Paint();
        // 消除锯齿
        mPaint.setAntiAlias(false);
        mPaint.setColor(Color.BLACK);
        mPaint.setTypeface(typeface);
        mPaint.setTextSize(30);
        mPaint.getTextBounds(info1, 0, info1.length(),
                mTextBound1);
        mPaint.getTextBounds(info2, 0, info2.length(),
                mTextBound2);
        mPaint.getTextBounds(info3, 0, info3.length(),
                mTextBound3);
        canvas.drawText(info1,(w-mTextBound1.width())/2,40,mPaint);
        canvas.drawText(info2,(w-mTextBound2.width())/2,80,mPaint);
        canvas.drawText(info3,(w-mTextBound3.width())/2,120,mPaint);
        return Bitmap.createBitmap(bitmap, 0, 0, w, 125);
    }
    private static Bitmap getTicketFour(Context context, Typeface typeface) {
        int w = 600;
        String info1 = "微信扫一扫";
        String info2 = "排队信息轻松查";
        String info3 = context.getString(R.string.tel);
        Rect mTextBound1 = new Rect();
        Rect mTextBound2 = new Rect();
        Rect mTextBound3 = new Rect();
        Bitmap bitmap = Bitmap.createBitmap(w, w * 5, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Bitmap ticket_erwei = BitmapFactory.decodeResource(context.getResources(), R.drawable.qr_code);
        // 设置画布颜色为白色
        canvas.drawColor(Color.WHITE);
        Paint mPaint = new Paint();
        // 消除锯齿
        mPaint.setAntiAlias(false);
        mPaint.setColor(Color.BLACK);
        mPaint.setTypeface(typeface);
        mPaint.setTextSize(35);
        mPaint.getTextBounds(info1, 0, info1.length(),
                mTextBound1);
        mPaint.getTextBounds(info2, 0, info2.length(),
                mTextBound2);
        mPaint.getTextBounds(info3, 0, info3.length(),
                mTextBound3);
        canvas.drawText(info1,235,40,mPaint);
        canvas.drawText(info2,235,80,mPaint);
        mPaint.setTextSize(23);
        canvas.drawText(info3,235,150,mPaint);
        canvas.drawBitmap(ticket_erwei,15,0,mPaint);
        return Bitmap.createBitmap(bitmap, 0, 0, w,190);
    }
}
