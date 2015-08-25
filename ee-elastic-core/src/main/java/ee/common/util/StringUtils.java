/*
 * Controlguide
 * Copyright (c) Siemens AG 2015, All Rights Reserved, Confidential
 */
package ee.common.util;

import java.util.regex.Pattern;

public class StringUtils {

  public static String filterLines(String message, Pattern linesFilter) {
    String ret = message;
    if (message != null) {
      String[] lines = splitToLines(message);
      if (lines.length > 1) {
        StringBuffer buffer = new StringBuffer();
        for (String line : lines) {
          if (linesFilter.matcher(line).matches()) {
            buffer.append(line).append("; ");
          }
        }
        ret = buffer.toString();
      } else {
        ret = lines[0];
      }
    }
    return ret;
  }

  public static String[] splitToLines(String message) {
    return message.split("\\r?\\n");
  }
}
