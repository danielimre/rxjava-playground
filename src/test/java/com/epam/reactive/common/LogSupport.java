package com.epam.reactive.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.functions.Action1;

/**
 * Simplifies logging.
 *
 * @author Daniel_Imre
 */
public class LogSupport {
    private static final Logger LOG = LoggerFactory.getLogger(LogSupport.class);

    public static void log(Object value) {
        log("Got", value);
    }

    public static void log(String prefix, Object value) {
        LOG.info(prefix + " {}", value);
    }

    public static <T> Action1<T> log(String prefix) {
        return i -> log(prefix, i);
    }

    public static void summay(String text) {
        LOG.info("\r\n\r\n" + text + "\r\n");
    }
}
