package foundation.pEp.jniadapter.test.pitytest;

public abstract class AbstractTestContext implements TestContextInterface{
    private boolean isInitialized = false;
    private boolean isUninitializable = false;
    private String testContextName = "Undefined";

    public AbstractTestContext() {
        setTestContextName(this.getClass().getSimpleName());
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public boolean isUninitializable() {
        return isUninitializable;
    }

    public void setUninitializable(boolean uninitializable) {
        isUninitializable = uninitializable;
    }

    public String getTestContextName() {
        return testContextName;
    }

    public void setTestContextName(String name) {
        this.testContextName = name;
    }

}