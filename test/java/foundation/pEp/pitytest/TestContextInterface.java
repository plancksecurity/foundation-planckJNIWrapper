package foundation.pEp.pitytest;

public interface TestContextInterface {
    void init() throws Throwable;
    boolean isInitialized();
    void setInitialized(boolean initialized);
    boolean isUninitializable();
    void setUninitializable(boolean uninitializable);
    String getTestContextName();
    void setTestContextName(String name);
}
