package ee.elastic.ui.html;

import java.util.Map.Entry;

public class GeneratorImpl implements Generator {
  private TemplateFactory templates = new TemplateFactoryImpl();

  @Override
  public Generator templates(TemplateFactory templates) {
    this.templates = templates;
    return this;
  }

  @Override
  public TemplateFactory templates() {
    return templates;
  }

  @Override
  public StringBuffer generate(Element root) {
    return fill(new StringBuffer(), root);
  }

  private StringBuffer fill(StringBuffer fill, Element element) {
    if (element.isTag()) {
      start(fill, element);
    }
    if (element.isContent()) {
      fill.append(element.content());
    }
    if (element.hasChilds()) {
      for (Element child : element.childs()) {
        fill(fill, child);
      }
    }
    if (element.isTag()) {
      end(fill, element);
    }
    return fill;
  }

  private StringBuffer start(StringBuffer fill, Element element) {
    fill.append("<").append(element.tag());
    if (element.isCss()) {
      fill.append(" style=\"").append(element.css()).append("\"");
    }
    if (element.isCssClass()) {
      fill.append(" class=\"").append(element.cssClass()).append("\"");
    }
    if (element.hasAttrs()) {
      for (Entry<String, String> attr : element.attrs().entrySet()) {
        fill.append(" ").append(attr.getKey()).append("\"").append(attr.getValue()).append("\"");
      }
    }
    return fill.append(">");
  }

  private StringBuffer end(StringBuffer fill, Element element) {
    return fill.append("</").append(element.tag()).append(">");
  }

}
