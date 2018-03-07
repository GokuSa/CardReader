package com.shine.printer_module;

/**
 * Created by 李晓林 qq:1220289215 on 2016/11/2.
 * 远程视界小票打印内容
 */

public class PrintContent2 {
     String appInfo;
     String appTime;
    String appNum;
    String barCode;
    String name;
    String sex;

    public PrintContent2() {
    }

    public PrintContent2(String appInfo, String appTime, String appNum, String barCode, String name, String sex) {
        this.appInfo = appInfo;
        this.appTime = appTime;
        this.appNum = appNum;
        this.barCode = barCode;
        this.name = name;
        this.sex = sex;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public String getAppTime() {
        return appTime;
    }

    public void setAppTime(String appTime) {
        this.appTime = appTime;
    }

    public String getAppNum() {
        return appNum;
    }

    public void setAppNum(String appNum) {
        this.appNum = appNum;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "PrintContent2{" +
                "appInfo='" + appInfo + '\'' +
                ", appTime='" + appTime + '\'' +
                ", appNum='" + appNum + '\'' +
                ", barCode='" + barCode + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
