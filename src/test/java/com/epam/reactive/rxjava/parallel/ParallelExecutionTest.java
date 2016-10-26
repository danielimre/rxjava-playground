package com.epam.reactive.rxjava.parallel;

import static com.epam.reactive.common.LogSupport.log;
import static com.epam.reactive.common.WaitSupport.waitFor;
import static com.epam.reactive.rxjava.ObservableTestSupport.onNextEventsOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.testng.annotations.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Shows how to achieve parallel execution.
 *
 * @author Daniel_Imre
 */
public class ParallelExecutionTest {
    @Test
    public void shouldExecuteInParallel() {
        Observable<Integer> obs = Observable.just(1, 2, 3)
                .doOnNext(log("premap"))
                .flatMap(inner -> Observable.just(inner).observeOn(Schedulers.computation())
                    .map(i -> {
                        waitFor(500);
                        return i + 1;
                    })
                )
                .doOnNext(log("postmap"));
        // please note that timeout is 1 sec but all 3 tasks requires 500 ms each
        assertThat(onNextEventsOf(obs, 1), containsInAnyOrder(4, 3, 2));
    }

    @Test
    public void bewareCanDeadlock() {
        Observable<Integer> obs = Observable.range(0, 10)
                .doOnNext(log("premap"))
                .observeOn(Schedulers.computation())
                .flatMap(inner -> {
                    List<Integer> range = Observable.just(inner).observeOn(Schedulers.computation()).doOnNext(log("map")).map(i -> {
                        waitFor(25);
                        return i;
                    }).toList().toBlocking().first(); //this should be avoided
                    return Observable.from(range);
                })
                .doOnNext(log("postmap")).subscribeOn(Schedulers.io());
        //next line can deadlock easily. see https://github.com/ReactiveX/RxJava/issues/3054 for more information
        assertThat(obs.toList().toBlocking().first(), is(not(empty())));
    }

}
