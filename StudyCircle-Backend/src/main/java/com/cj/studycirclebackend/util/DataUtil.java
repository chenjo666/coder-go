package com.cj.studycirclebackend.util;

import com.sun.mail.iap.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Component
public class DataUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 8;
    // UUID 生成器
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    // md5 加密
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    // 8位数字+字母
    public static String generateStringWithOctal() {
        StringBuilder sb = new StringBuilder(LENGTH);
        Random random = new Random();
        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static String generateRandomUsername() {
        return "用户" + generateStringWithOctal();
    }

    // 转日期格式
    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date formatStringToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    // 延迟 2 - 10 秒
    public static Date afterRandomSeconds(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int randomSeconds = (int) (Math.random() * 9) + 2;
        calendar.add(Calendar.SECOND, randomSeconds);
        return calendar.getTime();
    }
}
