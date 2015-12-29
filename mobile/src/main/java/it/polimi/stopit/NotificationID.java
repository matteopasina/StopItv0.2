package it.polimi.stopit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by matteo on 29/12/15.
 */
public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}
