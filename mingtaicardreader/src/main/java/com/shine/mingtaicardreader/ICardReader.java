package com.shine.mingtaicardreader;

/**
 * Created by 李晓林 on 2017/2/22
 * qq:1220289215
 */

 interface ICardReader {
    void init();
    void open();
    void readId();
    void readSocialCard();
    void readM1();
}
