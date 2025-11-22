package com.lezord.system_api.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OTPGenerator {

    public String generateOTP(int length) {

        StringBuilder stringBuilder = new StringBuilder(length);

        Random random = new Random();

        for (int i = 0; i < length; i++) stringBuilder.append(random.nextInt(10));

        if (stringBuilder.charAt(0) == '0')  stringBuilder.setCharAt(0, (char) ('1' + random.nextInt(9)));

        return stringBuilder.toString();
    }
}
