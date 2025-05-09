package com.reliaquest.api.web;

import java.util.concurrent.Callable;

import com.reliaquest.api.exception.ServiceUnavailableException;


/**
 * Provides a simple retry mechanism for network calls.
 * Note: The Server app has intentional rate limiting enabled
 * 
 * @param callable the callable to retry
 * @param retries the number of retries
 * @param delay the delay between retries in milliseconds
 * @return the result of the callable
 */
public class NetworkHandler {
    public static <T> T call(Callable<T> callable, int maxAttempts, int initialDelay) {
        int attempt = 0;
        int delay = initialDelay;
        while (attempt < maxAttempts) {
            try {
                return callable.call();
            } catch (Exception e) {
                attempt++;
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(delay);
                        delay *= 2;
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw new ServiceUnavailableException("The server is not available.");
                }
            }
        }
        return null; // should never occur
    }
}
