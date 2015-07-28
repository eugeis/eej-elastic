package ee.elastic.ui.integ;

public interface Func<S, T> {
	T call(S source);
}
