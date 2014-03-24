package ch.softappeal.yass.core;

import ch.softappeal.yass.util.Check;
import ch.softappeal.yass.util.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * {@link Interceptor} utilities.
 */
public final class Interceptors {


  private Interceptors() {
    // disable
  }


  /**
   * @param <C> the contract type
   * @return a proxy for implementation using interceptors
   */
  public static <C> C proxy(final Class<C> contract, final C implementation, final Interceptor... interceptors) {
    Check.notNull(implementation);
    final Interceptor interceptor = composite(interceptors);
    return contract.cast(Proxy.newProxyInstance(
      contract.getClassLoader(),
      new Class<?>[] {contract},
      (proxy, method, arguments) -> interceptor.invoke(method, arguments, () -> {
        try {
          return method.invoke(implementation, arguments);
        } catch (final InvocationTargetException e) {
          throw e.getCause();
        }
      })
    ));
  }


  /**
   * Calls {@link Invocation#proceed()}.
   */
  public static final Interceptor DIRECT = (method, arguments, invocation) -> invocation.proceed();


  public static Interceptor composite(final Interceptor interceptor1, final Interceptor interceptor2) {
    Check.notNull(interceptor1);
    Check.notNull(interceptor2);
    if (interceptor1 == DIRECT) {
      return interceptor2;
    }
    if (interceptor2 == DIRECT) {
      return interceptor1;
    }
    return (method, arguments, invocation) -> interceptor1.invoke(
      method, arguments, () -> interceptor2.invoke(method, arguments, invocation)
    );
  }

  /**
   * @return an interceptor that is a composite of interceptors
   */
  public static Interceptor composite(final Interceptor... interceptors) {
    Interceptor compositeInterceptor = DIRECT;
    for (final Interceptor interceptor : interceptors) {
      compositeInterceptor = composite(compositeInterceptor, interceptor);
    }
    return compositeInterceptor;
  }


  /**
   * @param <T> the type of the {@link ThreadLocal}
   * @return an interceptor that changes threadLocal to value during {@link Interceptor#invoke(Method, Object[], Invocation)}
   */
  public static <T> Interceptor threadLocal(final ThreadLocal<T> threadLocal, final T value) {
    Check.notNull(threadLocal);
    Check.notNull(value);
    return (method, arguments, invocation) -> {
      @Nullable final T oldValue = threadLocal.get();
      threadLocal.set(value);
      try {
        return invocation.proceed();
      } finally {
        threadLocal.set(oldValue);
      }
    };
  }

}
