package com.shine.cardreaderswitch;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private String KEY_STATUS = "7E 10 07 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 69 AA";
    private String KEY_OPEN = "7E 10 04 01 00 60 4d 41 4b 45 47 52 07 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1A AA";
    private String KEY_CLOSE = "7E 10 04 01 00 60 4d 41 4b 45 47 52 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1E AA";

    @Test
    public void test() {
        byte a = (byte) 234;
    }
    @Test
    public void addition_isCorrect() throws Exception {
        String[] sample = {KEY_OPEN, KEY_STATUS, KEY_CLOSE};
        StringBuilder stringBuilder=new StringBuilder();
        for (String s : sample) {
            stringBuilder.setLength(0);
            char[] chars = s.toCharArray();

            for (char ch : chars) {
                if (' ' != ch) {
                    stringBuilder.append(ch);
                }
            }
            System.out.println(stringBuilder.toString());
        }
    }
}