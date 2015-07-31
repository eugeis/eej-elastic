package ee.elastic.ui.integ;

public class Link<E extends Comparable<? super E>> implements Comparable<Link<E>> {
  private E from;
  private E to;

  public Link() {
    super();
  }

  public Link(E from, E to) {
    super();
    this.from = from;
    this.to = to;
  }

  public E from() {
    return from;
  }

  public Link<E> from(E from) {
    this.from = from;
    return this;
  }

  public E to() {
    return to;
  }

  public Link<E> to(E to) {
    this.to = to;
    return this;
  }

  @Override
  public int compareTo(Link<E> o) {
    return this.from().compareTo(o.from());
  }
}
