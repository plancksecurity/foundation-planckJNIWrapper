package foundation.pEp.jniadapter.test.framework;

public interface TestContextInterface {
    void init() throws Throwable;
    boolean isInitialized();
    void setInitialized(boolean initialized);
    boolean isUninitializable();
    void setUninitializable(boolean uninitializable);
    String getTestContextName();
    void setTestContextName(String name);
}
