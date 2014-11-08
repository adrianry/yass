package ch.softappeal.yass.core.remote;

import ch.softappeal.yass.core.Interceptor;

/**
 * Factory for proxies that invoke a remote service.
 * @param <C> the contract type
 */
@FunctionalInterface public interface Invoker<C> {

    /**
     * @return a proxy for the contract using interceptors
     */
    C proxy(Interceptor... interceptors);

}
