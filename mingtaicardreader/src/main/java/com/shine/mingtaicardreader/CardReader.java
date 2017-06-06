package com.shine.mingtaicardreader;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by 李晓林 on 2017/2/22
 * qq:1220289215
 */

public abstract class CardReader {

    public static CardReader getInstance(Context context) {
        if (Build.VERSION.SDK_INT == 23||Build.VERSION.SDK_INT == 21) {
            return new DeviceReaderA(context);
        }else{
            return new DeviceReaderB(context);
        }
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
    protected String getnation(byte bNationinfo[]) {
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

    /**
     * 格式化卡号
     * @param primitive 设备返回的卡号，如果是ascii码即可格式化
     * @return
     */
    protected String format(String primitive) {
        if (TextUtils.isEmpty(primitive)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        char c;
        int length=primitive.length();
        //读取的字符串需要转化成卡号，按2个字符长度分隔
        for (int i = 0; i < length / 2; i++) {
            String number = primitive.substring(2 * i, 2 * (i + 1));
            if (TextUtils.isDigitsOnly(number)) {
                c= (char) Integer.parseInt(number,16);
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

  abstract void init(TextView textView);
  abstract int readId(TextView textView);
  abstract void readSocialCard(TextView textView);
  abstract void readM1(TextView textView,int addr, int nSector, String str_key);
  abstract void readShangHaiSocialCard(TextView textView);
  abstract void readSocialCardRegionCode(TextView textView);
  abstract void close();
}
