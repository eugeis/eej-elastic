package ee.elastic.ui.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import ee.elastic.ui.config.ColumnDef;
import ee.elastic.ui.core.StringValue;
import ee.elastic.ui.formatter.DateFormatter;
import ee.elastic.ui.formatter.Formatter;

public class DateConverter extends ConverterAbstract {
  private SimpleDateFormat parser;
  private Formatter<Date> formater;

  @Override
  public void setColumn(ColumnDef column) {
    super.setColumn(column);
    parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    parser.setTimeZone(TimeZone.getTimeZone("UTC"));
    formater = new DateFormatter(parseDateFormat(column.converter().get("pattern"), "dd.MM.yy HH:mm"));
  }

  @Override
  public Object value(Map<String, Object> source) {
    Object ret = resolveProperty(source, column.path());
    if (ret != null) {
      try {
        Date date = parser.parse(String.valueOf(ret));
        ret = new StringValue<Date>(date, formater);
      } catch (Exception e) {
        System.err.println(String.format("Exception [%s] during convertion of [%s] to date.", e.getMessage(), ret));
      }
    } else {
      ret = "";
    }
    return ret;
  }
}
