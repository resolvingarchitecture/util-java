package ra.util;

public interface UncaughtExceptionHandler {
    void handleUncaughtException(Throwable throwable, boolean doShutDown);
}
