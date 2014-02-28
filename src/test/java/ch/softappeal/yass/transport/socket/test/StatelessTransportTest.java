package ch.softappeal.yass.transport.socket.test;

import ch.softappeal.yass.core.Interceptor;
import ch.softappeal.yass.core.Invocation;
import ch.softappeal.yass.core.remote.Server;
import ch.softappeal.yass.core.remote.TaggedMethodMapper;
import ch.softappeal.yass.core.remote.test.ContractIdTest;
import ch.softappeal.yass.core.test.InvokeTest;
import ch.softappeal.yass.transport.socket.SocketListenerTest;
import ch.softappeal.yass.transport.socket.StatelessTransport;
import ch.softappeal.yass.transport.test.MessageSerializerTest;
import ch.softappeal.yass.util.Exceptions;
import ch.softappeal.yass.util.NamedThreadFactory;
import ch.softappeal.yass.util.Nullable;
import ch.softappeal.yass.util.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatelessTransportTest extends InvokeTest {

  private static final class SocketInterceptor implements Interceptor {
    private final String side;
    SocketInterceptor(final String side) {
      this.side = side;
    }
    @Override public Object invoke(final Method method, @Nullable final Object[] arguments, final Invocation invocation) throws Throwable {
      println(side, "entry", StatelessTransport.socket().getLocalPort());
      try {
        return invocation.proceed();
      } finally {
        println(side, "exit", StatelessTransport.socket().getLocalPort());
      }
    }
  }

  @Test public void test() throws Exception {
    Assert.assertNull(StatelessTransport.socket());
    final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("executor", TestUtils.TERMINATE));
    try {
      new StatelessTransport(
        new Server(
          TaggedMethodMapper.FACTORY,
          ContractIdTest.ID.service(new TestServiceImpl(), new SocketInterceptor("Server"), SERVER_INTERCEPTOR)
        ),
        MessageSerializerTest.SERIALIZER,
        executor,
        Exceptions.STD_ERR
      ).start(executor, SocketListenerTest.ADDRESS);
      invoke(
        ContractIdTest.ID.invoker(StatelessTransport.client(
          TaggedMethodMapper.FACTORY,
          MessageSerializerTest.SERIALIZER,
          SocketListenerTest.ADDRESS
        )).proxy(PRINTLN_AFTER, new SocketInterceptor("Client"), CLIENT_INTERCEPTOR)
      );

    } finally {
      SocketListenerTest.shutdown(executor);
    }
  }

}
