package foundation.pEp.jniadapter;
import java.util.concurrent.atomic.AtomicLong;

abstract class UniquelyIdentifiable {
    static final AtomicLong NEXT_ID = new AtomicLong(1);
    final long id = NEXT_ID.getAndIncrement();

    public long getId() {
        return id;
    }
}
