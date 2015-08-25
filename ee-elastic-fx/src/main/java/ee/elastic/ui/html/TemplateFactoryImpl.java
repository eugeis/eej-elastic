package ee.elastic.ui.html;

public class TemplateFactoryImpl implements TemplateFactory {
  @Override
  public Element head() {
    return new Element().tag("head");
  }

  @Override
  public Element div() {
    return new Element().tag("div");
  }

  @Override
  public Element span() {
    return new Element().tag("span");
  }

  @Override
  public Element link(String cssClass, String href, String content) {
    Element span = span().cssClass(cssClass);
    return span.addChild(new Element().tag("a").addAttr("href=", href).content(content));
  }

  @Override
  public Element style() {
    return new Element().tag("style").addAttr("type", "text/css");
  }

  @Override
  public Element divSpan(String cssClass, String content) {
    return div().addChild(span(cssClass, content));
  }

  @Override
  public Element span(String cssClass, String content) {
    return span().cssClass(cssClass).content(content);
  }

  @Override
  public Element body() {
    return new Element().tag("body");
  }

  @Override
  public Element html() {
    return new Element().tag("html");
  }

  @Override
  public Element content(String content) {
    return new Element().content(content);
  }

  @Override
  public Element page(Element head, Element bodyChild) {
    Element html = html();
    if (head != null) {
      html.addChild(head);
    }
    html.addChild(body().addChild(bodyChild));
    return content("<!DOCTYPE html>").addChild(html);
  }
}
