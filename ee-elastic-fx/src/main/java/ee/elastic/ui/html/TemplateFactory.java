package ee.elastic.ui.html;

public interface TemplateFactory {
  Element head();

  Element body();

  Element span();

  Element span(String cssClass, String content);

  Element link(String cssClass, String href, String content);

  Element style();

  Element content(String content);

  Element html();

  Element page(Element head, Element body);

  Element div();

  Element divSpan(String cssClass, String content);
}
