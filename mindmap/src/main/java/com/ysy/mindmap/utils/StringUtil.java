package com.ysy.mindmap.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.security.Key;

import javax.crypto.Cipher;

import java.util.regex.Pattern;

public class StringUtil {

    private final static long minute = 60 * 1000; // 1分钟
    private final static long hour = 60 * minute; // 1小时
    private final static long day = 24 * hour; // 1天
    private final static long month = 31 * day; // 1月
    private final static long year = 12 * month; // 1年

    public static String getTimeFormatText(String originTimeString) {
        SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long originDayTime = 0;
        try {
            originDayTime = sDF.parse(originTimeString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return originTimeString;
        }
        long diff = new Date().getTime() - originDayTime;
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    public static String zipBlank(String str) {
        String dest = "";
        if (str != null) {
            String[] temp = str.split("\n");
            for (int i = 0; i < temp.length; i++) {
                if (i > 0 && !temp[i - 1].equals(""))
                    dest = dest + "\n";
                if (!temp[i].equals(""))
                    dest = dest + temp[i];
            }
        }
        return dest;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static boolean isHavingBlank(String str) {
        String dest;
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
            return str.length() > dest.length();
        }
        return true;
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static int getChineseCount(String words) {
        int wordsLength = words.length();
        int count = 0;
        for (int i = 0; i < wordsLength; ++i)
            if (isChinese(words.charAt(i)))
                ++count;
        return count;
    }

    public String shortNickname(String nickname) {
        if (nickname != null) {
            float count = 0.f;
            for (int i = 0; i < nickname.length(); i++) {
                if (i < 8) {
                    if (isChinese(nickname.charAt(i)))
                        count += 1.0;
                    else
                        count += 0.5;
                }
            }
            if (count > 7)
                nickname = nickname.substring(0, 7) + "…";
            else {
                if (nickname.length() > 8)
                    nickname = nickname.substring(0, 8) + "…";
            }
            return nickname;
        } else
            return "加载中…";
    }

    private Cipher decryptCipher = null;

    private static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    private byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }

    private String decrypt(String strIn) throws Exception {
        return new String(decrypt(hexStr2ByteArr(strIn)));
    }

    private Key getKey(byte[] arrBTmp) throws Exception {
        byte[] arrB = new byte[8];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++)
            arrB[i] = arrBTmp[i];
        return new javax.crypto.spec.SecretKeySpec(arrB, "DES");
    }

    public String[] eU(String[] strings) throws Exception {
        Key key = getKey(strings[0].getBytes());
        Cipher encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        String[] temp = new String[5];
        temp[0] = decrypt(strings[1]);
        temp[1] = decrypt(strings[2]);
        temp[2] = decrypt(strings[3]);
        temp[3] = decrypt(strings[4]);
        temp[4] = decrypt(strings[5]);
        return temp;
    }

    public String[] uE(String[] strings) throws Exception {
        Key key = getKey(strings[0].getBytes());
        Cipher encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        String[] temp = new String[2];
        temp[0] = decrypt(strings[2]);
        temp[1] = decrypt(strings[1]);
        return temp;
    }
}
