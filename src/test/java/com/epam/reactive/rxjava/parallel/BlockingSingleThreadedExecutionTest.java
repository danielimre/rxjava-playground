package com.epam.reactive.rxjava.parallel;

import static com.epam.reactive.common.LogSupport.summay;
import static com.epam.reactive.common.WaitSupport.waitFor;
import static com.epam.reactive.rxjava.ObservableTestSupport.onNextEventsOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import com.epam.reactive.common.LogSupport;
import org.testng.annotations.Test;
import rx.Observable;

/**
 * Shows that by default RxJava is single threaded and blocks on current thread.
 *
 * @author Daniel_Imre
 */
public class BlockingSingleThreadedExecutionTest {
    @Test
    public void shouldFinishWithoutExplicitWait() {
        Observable<Integer> obs = Observable.just(1, 2, 3).map(i -> {
            waitFor(300);
            return i + 1;
        });
        assertThat(onNextEventsOf(obs), contains(2, 3, 4));

        summay("Non-waiting subscribe");
        //this also finishes without explicit wait, blocks current thread
        obs.subscribe(LogSupport::log);
    }
}

