package com.epam.reactive.common;

/**
 * Simplifies waiting.
 *
 * @author Daniel_Imre
 */
public class WaitSupport {

    public static void waitFor(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }
}
