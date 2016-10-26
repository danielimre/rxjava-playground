package com.epam.reactive.rxjava.parallel;

import static com.epam.reactive.common.LogSupport.log;
import static com.epam.reactive.common.LogSupport.summay;
import static com.epam.reactive.common.WaitSupport.waitFor;
import static com.epam.reactive.rxjava.ObservableTestSupport.onNextEventsOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import com.epam.reactive.common.LogSupport;
import org.testng.annotations.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Shows how to utilize other threads to avoid blocking.
 *
 * @author Daniel_Imre
 */
public class MultiThreadedExecutionTest {
    @Test
    public void shouldExecuteOnComputationThreads() {
        Observable<Integer> obs = Observable.just(1, 2, 3)
            .doOnNext(log("premap"))
            .map(i -> {
                waitFor(300);
                return i + 1;
            })
            .doOnNext(log("postmap"))
            .subscribeOn(Schedulers.computation());
        assertThat(onNextEventsOf(obs), contains(2, 3, 4));

        summay("Blocking subscribe");
        //we have to block to get each item
        obs.toBlocking().forEach(LogSupport::log);

        summay("Non-blocking subscribe");
        //this won't finish without explicit wait
        obs.subscribe(LogSupport::log);
    }

    @Test
    public void shouldExecuteOnIoThreads() {
        Observable<Integer> obs = Observable.just(1, 2, 3)
                .doOnNext(log("premap"))
                .map(i -> {
                    waitFor(300);
                    return i + 1;
                })
                .subscribeOn(Schedulers.io())
                .doOnNext(log("postmap"))
                .subscribeOn(Schedulers.computation());
        assertThat(onNextEventsOf(obs), contains(2, 3, 4));
    }

    @Test
    public void shouldSubscribeOnIOButExecuteOnComputational() {
        Observable<Integer> obs = Observable.just(1, 2, 3)
                .doOnNext(log("premap"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(i -> {
                    waitFor(300);
                    return i + 1;
                })
                .doOnNext(log("postmap"));
        assertThat(onNextEventsOf(obs), contains(2, 3, 4));
    }

    @Test
    public void shouldExecuteOnComputationalThenOnNewThread() {
        Observable<Integer> obs = Observable.just(1, 2, 3)
                .doOnNext(log("premap"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(i -> {
                    waitFor(300);
                    return i + 1;
                })
                .doOnNext(log("postmap"))
                .observeOn(Schedulers.newThread())
                .map(i -> {
                    waitFor(300);
                    return i + 1;
                })
                .doOnNext(log("postmap2"));
        assertThat(onNextEventsOf(obs, 2), contains(3, 4, 5));
    }

    @Test
    public void shouldSubscribeOnIOAndObserveOnIo() {
        Observable<Integer> obs = Observable.just(1, 2, 3)
                .doOnNext(log("premap"))
                .map(i -> {
                    waitFor(300);
                    return i + 1;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .doOnNext(log("postmap"));
        assertThat(onNextEventsOf(obs), contains(2, 3, 4));
    }
}
