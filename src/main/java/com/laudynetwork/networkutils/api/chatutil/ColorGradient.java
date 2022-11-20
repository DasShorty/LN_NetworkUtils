package com.laudynetwork.networkutils.api.chatutil;

import java.awt.*;

@SuppressWarnings("unused")
public class ColorGradient {
    public static Color getInterpolationColor(Color color1, Color color2, float percent) {
        int r = (int) (color1.getRed() * percent + color2.getRed() * (1 - percent));
        int g = (int) (color1.getGreen() * percent + color2.getGreen() * (1 - percent));
        int b = (int) (color1.getBlue() * percent + color2.getBlue() * (1 - percent));
        return new Color(r, g, b);
    }

    public static String colorGradient(String text, Color color1, Color color2) {
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            messageBuilder.append(getHexColor(getInterpolationColor(color1, color2, i / (float) text.length())));

            messageBuilder.append(text.charAt(i));
        }
        return messageBuilder.toString();
    }

    public static String colorGradient(String text, Color color1, Color color2, int shift) {
        StringBuilder messageBuilder = new StringBuilder();
        Color[] colorArray = new Color[text.length()];

        for (int i = 0; i < text.length(); i++) {
            colorArray[i] = getInterpolationColor(color1, color2, i / (float) text.length());
        }

        for (int i = 0; i < text.length(); i++) {
            messageBuilder.append(getHexColor(colorArray[(i + shift) % text.length()]));

            messageBuilder.append(text.charAt(i));
        }

        return messageBuilder.toString();
    }

    public static String colorGradient(String text, Color color1, Color color2, Color color3) {
        StringBuilder messageBuilder = new StringBuilder();
        int lenght = text.length();
        for (int i = 0; i < lenght; i++) {
            if (i >= lenght / 2) {
                messageBuilder.append(getHexColor(
                        getInterpolationColor(color2, color3, ((i - (float) lenght / 2) / (float) lenght) * 2)));
            } else {
                messageBuilder.append(getHexColor(getInterpolationColor(color1, color2, (i / (float) lenght) * 2)));
            }
            messageBuilder.append(text.charAt(i));
        }
        return messageBuilder.toString();
    }

    public static String colorGradient(String text, Color color1, Color color2, Color color3, int shift) {
        StringBuilder messageBuilder = new StringBuilder();
        Color[] colorArray = new Color[text.length()];

        int lenght = text.length();

        for (int i = 0; i < text.length(); i++) {
            if (i >= text.length() / 2) {
                colorArray[i] = getInterpolationColor(color2, color3, ((i - (float) lenght / 2) / (float) lenght) * 2);
            } else {
                colorArray[i] = getInterpolationColor(color1, color2, (i / (float) lenght) * 2);
            }
        }

        for (int i = 0; i < text.length(); i++) {
            messageBuilder.append(getHexColor(colorArray[(i + shift) % text.length()]));

            messageBuilder.append(text.charAt(i));
        }

        return messageBuilder.toString();
    }

    private static String getHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
