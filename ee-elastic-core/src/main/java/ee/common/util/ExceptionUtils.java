package ee.common.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class ExceptionUtils {
  private static final int MAX_DEEP_FOR_EXCEPTION_CAUSE_HIERARCHY = 5;

  public static String formatExceptionCauseHierarchy(Throwable e) {
    return formatExceptionCauseHierarchy(e, null);
  }

  public static String formatExceptionCauseHierarchy(Throwable e, Pattern messageLinesFilter) {
    Set<Throwable> convertedAlready = new HashSet<Throwable>();
    StringBuffer buffer = new StringBuffer();
    fillExceptionCauseRecursive(e, buffer, convertedAlready, 0, messageLinesFilter);
    return buffer.toString();
  }

  private static void fillExceptionCauseRecursive(Throwable e, StringBuffer buffer, Set<Throwable> convertedAlready, int deep, Pattern messageLinesFilter) {
    if (deep < MAX_DEEP_FOR_EXCEPTION_CAUSE_HIERARCHY) {
      if (e != null && !convertedAlready.contains(e)) {

        convertedAlready.add(e);

        buffer.append("[");
        buffer.append(e.getClass().getSimpleName());
        buffer.append("@");
        buffer.append(Integer.toHexString(e.hashCode()));
        buffer.append("[");
        if (messageLinesFilter != null) {
          buffer.append(StringUtils.filterLines(e.getMessage(), messageLinesFilter));
        } else {
          buffer.append(e.getMessage());
        }
        fillExceptionCauseRecursive(e.getCause(), buffer, convertedAlready, deep + 1, messageLinesFilter);
        buffer.append("]");
        buffer.append("]");
      }
    }
  }

}
