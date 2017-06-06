package com.example.mt3yreader;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mt9reader.R;
import com.synjones.bluetooth.BmpUtil;
import com.synjones.bluetooth.DecodeWlt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IdentifyActivity extends Activity {

    private Bitmap bmp;
    int result;

    /*public native int Wlt2Bmp(String wltPath, String bmpPath);

    static {
        System.loadLibrary("DecodeWlt");
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layidentify);
    }

    /*
     * 获取性别信息
     */
    public String getsexinfo(byte bsex[]) {
        String StrSexInfo = "";
        if (bsex[0] == 0x30) {
            StrSexInfo = "未知";
        } else if (bsex[0] == 0x31) {
            StrSexInfo = "男";
        } else if (bsex[0] == 0x32) {
            StrSexInfo = "女";
        } else if (bsex[0] == 0x39) {
            StrSexInfo = "未说明";
        } else {
            StrSexInfo = " ";
        }

        return StrSexInfo;
    }


    /*
     * 获取名族信息
     */
    public String getnation(byte bNationinfo[]) {
        String StrNation = "";
        int nNationNo = 0;

        int nationcode = 0;
        nNationNo = (bNationinfo[0] - 0x30) * 10 + bNationinfo[2] - 0x30;
        switch (nNationNo) {
            case 1:
                StrNation = "汉";
                break;
            case 2:
                StrNation = "蒙古";
                break;
            case 3:
                StrNation = "回";
                break;
            case 4:
                StrNation = "藏";
                break;
            case 5:
                StrNation = "维吾尔";
                break;
            case 6:
                StrNation = "苗";
                break;
            case 7:
                StrNation = "彝";
                break;
            case 8:
                StrNation = "壮";
                break;
            case 9:
                StrNation = "布依";
                break;
            case 10:
                StrNation = "朝鲜";
                break;
            case 11:
                StrNation = "满";
                break;
            case 12:
                StrNation = "侗";
                break;
            case 13:
                StrNation = "瑶";
                break;
            case 14:
                StrNation = "白";
                break;
            case 15:
                StrNation = "土家";
                break;
            case 16:
                StrNation = "哈尼";
                break;
            case 17:
                StrNation = "哈萨克";
                break;
            case 18:
                StrNation = "傣";
                break;
            case 19:
                StrNation = "黎";
                break;
            case 20:
                StrNation = "傈僳";
                break;
            case 21:
                StrNation = "佤";
                break;
            case 22:
                StrNation = "畲";
                break;
            case 23:
                StrNation = "高山";
                break;
            case 24:
                StrNation = "拉祜";
                break;
            case 25:
                StrNation = "水";
                break;
            case 26:
                StrNation = "东乡";
                break;
            case 27:
                StrNation = "纳西";
                break;
            case 28:
                StrNation = "景颇";
                break;
            case 29:
                StrNation = "柯尔克孜";
                break;
            case 30:
                StrNation = "土";
                break;
            case 31:
                StrNation = "达斡尔";
                break;
            case 32:
                StrNation = "仫佬";
                break;
            case 33:
                StrNation = "羌";
                break;
            case 34:
                StrNation = "布朗";
                break;
            case 35:
                StrNation = "撒拉";
                break;
            case 36:
                StrNation = "毛南";
                break;
            case 37:
                StrNation = "仡佬";
                break;
            case 38:
                StrNation = "锡伯";
                break;
            case 39:
                StrNation = "阿昌";
                break;
            case 40:
                StrNation = "普米";
                break;
            case 41:
                StrNation = "塔吉克";
                break;
            case 42:
                StrNation = "怒";
                break;
            case 43:
                StrNation = "乌孜别克";
                break;
            case 44:
                StrNation = "俄罗斯";
                break;
            case 45:
                StrNation = "鄂温克";
                break;
            case 46:
                StrNation = "德昂";
                break;
            case 47:
                StrNation = "保安";
                break;
            case 48:
                StrNation = "裕固";
                break;
            case 49:
                StrNation = "京";
                break;
            case 50:
                StrNation = "塔塔尔";
                break;
            case 51:
                StrNation = "独龙";
                break;
            case 52:
                StrNation = "鄂伦春";
                break;
            case 53:
                StrNation = "赫哲";
                break;
            case 54:
                StrNation = "门巴";
                break;
            case 55:
                StrNation = "珞巴";
                break;
            case 56:
                StrNation = "基诺";
                break;
            case 57:
                StrNation = "其他";
                break;
            case 58:
                StrNation = "外国血统中国籍人士";
                break;
            default:
                StrNation = " ";
                break;
        }
        return StrNation;
    }

    private void displayIDCard(String bmpPath) {
        TextView tv;
        int i = 0;
        ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
        if (i == 0) {

            try {

                if (result == 1) {
                    File f = new File(bmpPath);
                    if (bmp != null) {
                        if (!bmp.isRecycled()) {
                            bmp.recycle();
                        }
                        bmp = null;
                    }

                    if (f.exists()) {
                        bmp = BitmapFactory.decodeFile(bmpPath);
                        BmpUtil bu = new BmpUtil();
                        imageViewPhoto.setImageBitmap(bmp);
                    } else {
                        Resources res = getResources();
                        bmp = BitmapFactory.decodeResource(res,
                                R.drawable.photo);
                        imageViewPhoto.setImageBitmap(bmp);
                    }
                } else {
                    Resources res = getResources();
                    bmp = BitmapFactory.decodeResource(res, R.drawable.photo);
                    imageViewPhoto.setImageBitmap(bmp);
                }
                System.gc();

            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
        } else {

            imageViewPhoto.setImageResource(R.drawable.photo);
        }

    }

    public int bt_IdentifyRead(View source) {
        int nstatus = 0;
        int st = 0;
        int nRecLen[] = new int[8];
        byte szName[] = new byte[128];
        byte szSex[] = new byte[128];
        byte szNation[] = new byte[128];
        byte szBirth[] = new byte[128];
        byte szAddress[] = new byte[128];
        byte szIDNo[] = new byte[36];
        byte szDepartment[] = new byte[128];
        byte szDateStart[] = new byte[128];
        byte szDateEnd[] = new byte[128];
        byte szExpir[] = new byte[128];
        byte message[] = new byte[128];
        byte szdata[] = new byte[3072];

        int nNamelen[] = new int[2];
        int nSexlen[] = new int[2];
        int nNationlen[] = new int[2];
        int nBirthlen[] = new int[2];
        int nAddresslen[] = new int[2];
        int nIDNolen[] = new int[2];
        int nDepartmentlen[] = new int[2];
        int nDateStartlen[] = new int[2];
        int nDateEndlen[] = new int[2];
        int nExpirlen[] = new int[2];


        String StrErrMsg = "", StrName = "", StrSex = "", StrNation = "", StrBirth = "", StrAddress = "", StrDepartment = "";
        String StrDateStart = "", StrDateEnd = "", StrExpir = "", StrIDNo = "";
        EditText PUTip = (EditText) findViewById(R.id.editText_idcexecuteinfo); //提示信息框
        EditText EditName = (EditText) findViewById(R.id.editText_Name);
        EditText EditSex = (EditText) findViewById(R.id.editText_Sex);
        EditText EditNation = (EditText) findViewById(R.id.editText_Nation);
        EditText EditBirth = (EditText) findViewById(R.id.editText_Birth);

        EditText EditAddress = (EditText) findViewById(R.id.editText_Address);
        EditText EditIDNo = (EditText) findViewById(R.id.editText_IDNo);
        EditText EditDepartment = (EditText) findViewById(R.id.editText_Department);
        EditText EditDateStart = (EditText) findViewById(R.id.editText_DateStart);
        EditText EditDateEnd = (EditText) findViewById(R.id.editText_DateEnd);
        //EditText EditExpir=(EditText) findViewById(R.id.editText_Name);
        EditText Editmessage = (EditText) findViewById(R.id.editText_idcexecuteinfo);

        EditName.setText(StrName);
        EditSex.setText(StrSex);
        EditNation.setText(StrNation);
        EditBirth.setText(StrBirth);
        EditAddress.setText(StrAddress);
        EditIDNo.setText(StrIDNo);
        EditDepartment.setText(StrDepartment);
        EditDateStart.setText(StrDateStart);
        EditDateEnd.setText(StrDateEnd);

        String StrWltFilePath = "";
        String StrBmpFilePath = "";
        st = mt3yApi.mt8IDCardRead(
                szName,
                szSex,
                szNation,
                szBirth,
                szAddress,
                szIDNo,
                szDepartment,
                szDateStart,
                szDateEnd,
                nRecLen,
                szdata);

        if (st != 0) {
            result = -1;
            displayIDCard(StrBmpFilePath);//显示照片
            StrErrMsg = new String("身份证读卡失败");
            PUTip.setText(StrErrMsg);
            return -100;
        } else {
            try {
                StrName = new String(szName, "UTF-16LE");
                EditName.setText(StrName);

                StrSex = getsexinfo(szSex);
                EditSex.setText(StrSex);

                EditNation.setText(getnation(szNation));
                StrBirth = new String(szBirth, "UTF-16LE");
                EditBirth.setText(StrBirth);


                StrAddress = new String(szAddress, "UTF-16LE");
                EditAddress.setText(StrAddress);

                StrIDNo = new String(szIDNo, "UTF-16LE");
                EditIDNo.setText(StrIDNo);

                StrDepartment = new String(szDepartment, "UTF-16LE");
                EditDepartment.setText(StrDepartment);

                StrDateStart = new String(szDateStart, "UTF-16LE");
                EditDateStart.setText(StrDateStart);

                StrDateEnd = new String(szDateEnd, "UTF-16LE");
                EditDateEnd.setText(StrDateEnd);


                StrWltFilePath = getFileStreamPath("photo.wlt").getAbsolutePath();
                StrBmpFilePath = getFileStreamPath("photo.bmp").getAbsolutePath();

                File wltFile = new File(StrWltFilePath);
                FileOutputStream fos = new FileOutputStream(wltFile);
                fos.write(szdata, 0, nRecLen[0]);
                fos.close();

                DecodeWlt dw = new DecodeWlt();
                result = dw.Wlt2Bmp(StrWltFilePath, StrBmpFilePath);
                if (result == 1) {
                    displayIDCard(StrBmpFilePath);//显示照片
                    PUTip.setText("照片解码成功");
                    return 0;
                } else {
                    displayIDCard(StrBmpFilePath);//显示照片
                    PUTip.setText("照片解码失败");
                    return -300;
                }


            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
                PUTip.setText("照片解码异常");
                return -200;
            }

        }


    }

}
