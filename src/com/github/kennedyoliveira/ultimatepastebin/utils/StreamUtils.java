package com.github.kennedyoliveira.ultimatepastebin.utils;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Utility class to manage Stream (IO).
 */
public class StreamUtils {

  private final static Logger log = UltimatePasteBinUtils.LOG;

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
      bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
      }

      return sb.toString();
    } finally {
      closeQuietly(bufferedReader);
    }
  }

  /**
   * Closes a resource without throwing any exception, if some exception is throwed by the {@link Closeable#close()} method, it will be logged.
   *
   * @param closeable Resource to close.
   */
  public static void closeQuietly(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
        log.info("Error when closing a closeable resource", e);
      }
    }
  }
}
