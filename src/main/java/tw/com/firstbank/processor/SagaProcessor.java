package tw.com.firstbank.processor;

public interface SagaProcessor<I, O> {
    void configure() throws Exception;
    O doSAGA(final String routeName, final I input, final Class<O> typeOutputClass);
}
