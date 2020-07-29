package foundation.pEp.pitytest;

import java.time.Duration;

public class StopWatch {
    private long timeStart = 0;
    private long timeEnd = 0;
    private Duration duration = null;

    public StopWatch(Runnable lambda) {
        timeStart = System.nanoTime();
        lambda.run();
        timeEnd = System.nanoTime();
        duration = Duration.ofNanos(timeEnd - timeStart);
    }

    public Duration getDuration() {
        return duration;
    }
}
