package ee.elastic.ui.core;

public class Sort<E, O> {
  private E element;
  private O order;

  public Sort(E path, O order) {
    super();
    this.element = path;
    this.order = order;
  }

  public E element() {
    return element;
  }

  public O order() {
    return order;
  }

  @Override
  public String toString() {
    return "Sort [element=" + element + ", order=" + order + "]";
  }
}
