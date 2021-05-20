package foundation.pEp.jniadapter.test.utils.transport;

@FunctionalInterface
public interface StringProcessorInterface<T> {
    // generic method
    T func(T t);
}
