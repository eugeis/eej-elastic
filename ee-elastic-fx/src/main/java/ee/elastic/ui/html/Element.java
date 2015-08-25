package ee.elastic.ui.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Element {
  private Map<String, String> attrs;
  private String tag;
  private String css;
  private String cssClass;
  private String content;
  private List<Element> childs;

  public String tag() {
    return tag;
  }

  public Element tag(String tag) {
    this.tag = tag;
    return this;
  }

  public boolean isTag() {
    return tag != null;
  }

  public String css() {
    return css;
  }

  public Element css(String css) {
    this.css = css;
    return this;
  }

  public boolean isCss() {
    return css != null;
  }

  public String cssClass() {
    return cssClass;
  }

  public boolean isCssClass() {
    return cssClass != null;
  }

  public Element cssClass(String cssClass) {
    this.cssClass = cssClass;
    return this;
  }

  public String content() {
    return content;
  }

  public boolean isContent() {
    return content != null;
  }

  public Element content(String content) {
    this.content = content;
    return this;
  }

  public List<Element> childs() {
    return childs;
  }

  public boolean hasChilds() {
    return childs != null;
  }

  public Element addChild(Element child) {
    if (childs == null) {
      childs = new ArrayList<>();
    }
    childs.add(child);
    return this;
  }

  public Element addAttr(String name, String value) {
    if (attrs == null) {
      attrs = new HashMap<>();
    }
    attrs.put(name, value);
    return this;
  }

  public Map<String, String> attrs() {
    return attrs;
  }

  public boolean hasAttrs() {
    return attrs != null;
  }
}
