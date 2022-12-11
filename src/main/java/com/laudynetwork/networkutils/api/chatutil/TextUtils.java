package com.laudynetwork.networkutils.api.chatutil;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
@SuppressWarnings("unused")
public class TextUtils {

    public String formatTime(int seconds, boolean hoursVisible) {
        int sec = seconds % 60;
        int min = (seconds / 60) % 60;
        int hours = ((seconds / 60) / 60) % 24;
        int days = ((seconds / 60) / 60) / 24;

        String strSec = (sec < 10) ? "0" + sec : Integer.toString(sec);
        String strMin = (min < 10) ? "0" + min : Integer.toString(min);
        String strHours = (hours < 10) ? "0" + hours : Integer.toString(hours);

        return (days > 0 ? days + " Days " : "") + ((hours > 0) && hoursVisible ? strHours + ":" : "") + strMin + ":" +
                strSec;
    }

    public static @NotNull String toRoman(int number) {
        StringBuilder sb = new StringBuilder();

        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanLetters = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                sb.append(romanLetters[i]);
            }
        }
        return sb.toString();
    }
}
