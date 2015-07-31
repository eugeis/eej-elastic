package ee.elastic.ui.fx;

import java.util.HashMap;

import ee.elastic.ui.config.ViewDef;
import ee.elastic.ui.core.View;

@SuppressWarnings("rawtypes")
public class ViewFactory {
  private final static String DEFAULT = "htmlView";
  private HashMap<String, Class<? extends View>> idToConverterClass;

  public ViewFactory() {
    super();
    idToConverterClass = new HashMap<>();
    fillBuildIn();
  }

  private void fillBuildIn() {
    idToConverterClass.put(DEFAULT, HtmlView.class);
    idToConverterClass.put("treeView", DetailsTreeView.class);
    idToConverterClass.put("textArea", TextAreaEditor.class);
  }

  public View view() {
    return view(null);
  }

  public View view(ViewDef viewDef) {
    return createInstance(viewDef);
  }

  private View createInstance(ViewDef viewDef) {
    View ret = null;
    String id = viewDef != null ? viewDef.getId() : DEFAULT;
    Class<? extends View> clazz = idToConverterClass.get(id);
    ret = createInstance(clazz);
    return ret;
  }

  private View createInstance(Class<? extends View> clazz) {
    View ret = null;
    if (clazz != null) {
      try {
        ret = clazz.newInstance();
      } catch (Exception e) {
        System.err.println(String.format("Exception [%s] ocurred at creation of converter from class [%s]. Use default converter.", e.getMessage(), clazz));
      }
    }
    if (ret == null) {
      ret = new DetailsTreeView();
    }
    return ret;
  }
}
