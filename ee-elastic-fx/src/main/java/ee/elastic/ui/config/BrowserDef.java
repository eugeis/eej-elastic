package ee.elastic.ui.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ee.elastic.ui.integ.Func;
import ee.elastic.ui.integ.Utils;

@SuppressWarnings("rawtypes")
public class BrowserDef extends MapObject {
  private static final long serialVersionUID = 1L;
  private List<ColumnDef> columns;

  public BrowserDef(Map source) {
    super(source);
  }

  @SuppressWarnings({ "unchecked" })
  public List<ColumnDef> columns() {
    if (columns == null) {
      columns = Utils.transform((Collection<Map>) source.get("columns"), TRANSFORMER);
    }
    return columns;
  }

  private static Func<Map, ColumnDef> TRANSFORMER = new Func<Map, ColumnDef>() {
    @Override
    public ColumnDef call(Map source) {
      return new ColumnDef(source);
    }
  };
}
