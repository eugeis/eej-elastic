package ee.elastic.ui.formatter;

import java.text.DecimalFormat;

public class BytesToSizeFormatter implements Formatter<Long> {
  private static final long serialVersionUID = 1L;
  private static final String[] SIZES = new String[] { "B", "KB", "MB", "GB", "TB" };
  private DecimalFormat formatter = new DecimalFormat("#,##,###,####.######");

  public BytesToSizeFormatter(int precision) {
    super();
    buildFormatter(precision);
  }

  private void buildFormatter(int precision) {
    StringBuffer pattern = new StringBuffer("#,##,###,####");
    if (precision > 0) {
      pattern.append(".");
      for (int i = 0; i < precision; i++) {
        pattern.append("#");
      }
    }
    formatter = new DecimalFormat(pattern.toString());
  }

  @Override
  public String string(Long value) {
    String ret;
    if (value != 0 && value > 0) {
      float size = value.floatValue();
      int posttxt = 0;
      while (size >= 1024) {
        posttxt++;
        size = size / 1024;
      }
      ret = formatter.format(size) + " " + SIZES[posttxt];
    } else {
      ret = "0";
    }
    return ret;
  }
}
