package ch.softappeal.yass.core.remote;

import ch.softappeal.yass.core.Interceptor;
import ch.softappeal.yass.util.Nullable;

import java.lang.reflect.Proxy;

/**
 * Factory for {@link Invoker}.
 */
public abstract class Client extends Common implements InvokerFactory {


  public static final class ClientInvocation {

    public final boolean oneWay;
    private final Interceptor invocationInterceptor;
    private final Object serviceId;
    private final MethodMapper.Mapping methodMapping;
    private final Object[] arguments;

    ClientInvocation(final Interceptor invocationInterceptor, final Object serviceId, final MethodMapper.Mapping methodMapping, final Object[] arguments) {
      this.invocationInterceptor = invocationInterceptor;
      this.serviceId = serviceId;
      this.methodMapping = methodMapping;
      this.arguments = arguments;
      oneWay = methodMapping.oneWay;
    }

    /**
     * @param interceptor prepended to the interceptor chain
     * @see Client#invoke(ClientInvocation)
     */
    @Nullable public Object invoke(final Interceptor interceptor, final Tunnel tunnel) throws Throwable {
      return Interceptor.composite(interceptor, invocationInterceptor).invoke(methodMapping.method, arguments, () -> {
        final Reply reply = tunnel.invoke(new Request(serviceId, methodMapping.id, arguments));
        return oneWay ? null : reply.process();
      });
    }

  }


  protected Client(final MethodMapper.Factory methodMapperFactory) {
    super(methodMapperFactory);
  }


  @Override public final <C> Invoker<C> invoker(final ContractId<C> contractId) {
    final MethodMapper methodMapper = methodMapper(contractId.contract);
    return interceptors -> {
      final Interceptor interceptor = Interceptor.composite(interceptors);
      return contractId.contract.cast(Proxy.newProxyInstance(
        contractId.contract.getClassLoader(),
        new Class<?>[] {contractId.contract},
        (proxy, method, arguments) -> invoke(new ClientInvocation(interceptor, contractId.id, methodMapper.mapMethod(method), arguments))
      ));
    };
  }


  /**
   * @return {@link ClientInvocation#invoke(Interceptor, Tunnel)}
   */
  @Nullable protected abstract Object invoke(ClientInvocation invocation) throws Throwable;


}
