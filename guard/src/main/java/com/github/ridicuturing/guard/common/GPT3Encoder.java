package com.github.ridicuturing.guard.common;

import java.util.ArrayList;
import java.util.List;

public class GPT3Encoder {
    public static void main(String[] args) {
        String str = "好This is an example sentence to try encoding out on!";
        List<Integer> encoded = encode(str);
        System.out.println("Encoded this string looks like: " + encoded.toString());

        System.out.println("We can look at each token and what it represents");
        for (Integer token : encoded) {
            System.out.println("{token: " + token + ", string: " + decode(token) + "}");
        }

        String decoded = decode(encoded);
        System.out.println("We can decode it back into:\n" + decoded);
    }

    public static List<Integer> encode(String str) {
        List<Integer> encoded = new ArrayList<>();
        for (Character ch : str.toCharArray()) {
            encoded.add((int) ch);
        }
        return encoded;
    }

    public static String decode(List<Integer> encoded) {
        StringBuilder sb = new StringBuilder();
        for (Integer code : encoded) {
            sb.append((char) (int) code);
        }
        return sb.toString();
    }

    public static String decode(int code) {
        return Character.toString((char) code);
    }
}
