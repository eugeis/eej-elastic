package ee.elastic.ui.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter implements Formatter<Date> {
  private static final long serialVersionUID = 1L;
  private DateFormat format;

  public DateFormatter(String pattern) {
    this.format = new SimpleDateFormat(pattern);
  }

  public DateFormatter(DateFormat formatter) {
    this.format = formatter;
  }

  @Override
  public String string(Date value) {
    return format.format(value);
  }

}
