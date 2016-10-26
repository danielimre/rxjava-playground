package com.epam.reactive.rxjava;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.observers.TestSubscriber;

/**
 * Support methods for {@link Observable} testing.
 *
 * @author Daniel_Imre
 */
public class ObservableTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ObservableTestSupport.class);

    private static final int DEFAULT_TIME_OUT_IN_SECONDS = 1;

    /**
     * Returns the onNext events of an {@link Observable} expecting normal operation within 1 second.
     *
     * @param observable the observable to subscribe to
     * @param <T>        the type of events
     * @return the list of onNext events
     */
    public static <T> List<T> onNextEventsOf(Observable<T> observable) {
        return onNextEventsOf(observable, DEFAULT_TIME_OUT_IN_SECONDS);
    }

    /**
     * Returns the onNext events of an {@link Observable} expecting normal operation within the specified time.
     *
     * @param observable       the observable to subscribe to
     * @param timeOutInSeconds timeout in seconds
     * @param <T>              the type of events
     * @return the list of onNext events
     */
    public static <T> List<T> onNextEventsOf(Observable<T> observable, int timeOutInSeconds) {
        CountDownLatch latch = new CountDownLatch(1);
        assertThat(observable, is(not(nullValue())));
        TestSubscriber<T> sub = new TestSubscriber<>(new Observer<T>() {
            @Override
            public void onCompleted() {
                LOG.info("completed");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                LOG.info("error");
                latch.countDown();
            }

            @Override
            public void onNext(T t) {
                LOG.info("next {}", t);
            }
        });
        try {
            new Thread(() -> observable.subscribe(sub)).start();
            assertTrue(latch.await(timeOutInSeconds, TimeUnit.SECONDS), "Subscription timed out after " + timeOutInSeconds + " second(s).");
        } catch (InterruptedException e) {
            fail("Interrupted.");
        }
        sub.assertNoErrors();
        sub.assertCompleted();
        return sub.getOnNextEvents();
    }
}
