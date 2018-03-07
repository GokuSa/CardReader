package com.shine.printer_module;

/**
 * Created by 李晓林 qq:1220289215 on 2016/11/2.
 * 黄田社康服务中心小票打印内容
 */

public class PrintContent {
   private String mHospital="黄田社康服务中心";
   private String mDepartment;
   private String mTime;
   private String mNumber;
   private String mBarCode;
   private String mPeopleAhead;

    public PrintContent() {
    }

    public PrintContent(String barCode, String department, String number, String peopleAhead, String time) {
        mBarCode = barCode;
        mDepartment = department;
        mNumber = number;
        mPeopleAhead = peopleAhead;
        mTime = time;
    }

    public String getBarCode() {
        return mBarCode;
    }

    public void setBarCode(String barCode) {
        mBarCode = barCode;
    }

    public String getDepartment() {
        return mDepartment;
    }

    public void setDepartment(String department) {
        mDepartment = department;
    }

    public String getHospital() {
        return mHospital;
    }

    public void setHospital(String hospital) {
        mHospital = hospital;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getPeopleAhead() {
        return mPeopleAhead;
    }

    public void setPeopleAhead(String peopleAhead) {
        mPeopleAhead = peopleAhead;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
