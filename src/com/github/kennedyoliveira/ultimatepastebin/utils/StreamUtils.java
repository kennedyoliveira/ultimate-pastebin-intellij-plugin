package com.github.kennedyoliveira.ultimatepastebin.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by kennedyoliveira on 1/25/16.
 */
public class StreamUtils {

    /**
     * @deprecated Utility Class
     */
    private StreamUtils() {
    }

    /**
     * Read all lines for the input stream and closes it
     *
     * @param is InputStream to read
     * @return A string with the information
     */
    public static String readAllLines(@NotNull InputStream is) throws IOException {
        Objects.requireNonNull(is, "The input stream can't be null!");

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
        }
    }
}
