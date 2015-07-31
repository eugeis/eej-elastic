package ee.elastic.ui.fx;

import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.web.WebView;

import org.elasticsearch.search.SearchHitField;

import ee.elastic.ui.html.Element;
import ee.elastic.ui.html.Generator;
import ee.elastic.ui.html.GeneratorImpl;
import ee.elastic.ui.html.TemplateFactory;

public class HtmlView<E> extends BaseView<WebView, E> {
  private Generator generator;
  private TemplateFactory templates;
  private String cssContent;

  public HtmlView() {
    super();
    init();
  }

  private void init() {
    root = new WebView();
    generator = new GeneratorImpl();
    templates = generator.templates();
    this.cssContent = "p,div,table,ul,ol { margin-left:10px; } span.name {font-weight:bold} span.value {}";
  }

  public HtmlView(String cssContent) {
    this();
    this.cssContent = cssContent;
  }

  @SuppressWarnings({ "rawtypes" })
  private void createPropertyRecursive(Element parent, Map<?, ?> spec) {
    for (Entry prop : spec.entrySet()) {
      Element child = createName(prop.getKey());
      parent.addChild(child);
      if (prop.getValue() instanceof Map) {
        createPropertyRecursive(child, Map.class.cast(prop.getValue()));
      } else if (prop.getValue() instanceof SearchHitField) {
        SearchHitField searchHitField = (SearchHitField) prop.getValue();
        Element fieldChild;
        if (searchHitField.getValue() instanceof Map) {
          fieldChild = createName(searchHitField.getName());
          createPropertyRecursive(fieldChild, ((Map) searchHitField.getValue()));
        } else {
          fieldChild = createName(searchHitField.getName());
          fieldChild.addChild(templates.content(": "));
          fieldChild.addChild(createValue(prop.getValue()));
        }
        child.addChild(fieldChild);
      } else {
        child.addChild(templates.content(": "));
        child.addChild(createValue(prop.getValue()));
      }
    }
  }

  private Element createValue(Object value) {
    String stringValue = String.valueOf(value);
    return templates.span("value", stringValue);
  }

  private Element createName(Object name) {
    return templates.divSpan("name", String.valueOf(name));
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void data(E data) {

    Element div = templates.div();
    Element head = templates.head().addChild(templates.style().content(cssContent));
    Element page = templates.page(head, div);
    if (data instanceof Map) {
      createPropertyRecursive(div, (Map) data);
    } else {
      div.content(String.valueOf(data));
    }
    StringBuffer ret = generator.generate(page);
    root.getEngine().loadContent(ret.toString());
  }
}