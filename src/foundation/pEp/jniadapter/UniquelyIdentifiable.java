package foundation.pEp.jniadapter;
import java.util.concurrent.atomic.AtomicLong;

// Abstract here so you can only inherit from, but not instantiate
abstract public class UniquelyIdentifiable {
    private static final AtomicLong NEXT_ID = new AtomicLong(1);
    private final long id = NEXT_ID.getAndIncrement();

    protected long getId() {
        return id;
    }
}
