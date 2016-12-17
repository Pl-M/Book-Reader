/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader;

import java.util.Random;

public class MockBook {

    public static String getBook() {
        // Generate a test book.

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++){
            for (int j = 0; j < 5; j++){
                for (int k = 0; k < 7; k++){
                    char c = chars[random.nextInt(chars.length)];
                    sb.append(c);
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return (sb.toString());
    }
}
