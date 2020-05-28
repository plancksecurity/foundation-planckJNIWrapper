package foundation.pEp.jniadapter.test.framework;

public enum TestState {
    UNEVALUATED,
    SKIPPED,
    SUCCESS,
    FAILED,
    STARTING,
    CTX_INIT,
    CTX_INIT_FAILED,
    RUNNING;
}