package ch.softappeal.yass.core.remote.test;

import ch.softappeal.yass.core.Interceptor;
import ch.softappeal.yass.core.remote.Client;
import ch.softappeal.yass.core.remote.ContractId;
import ch.softappeal.yass.core.remote.Server;
import ch.softappeal.yass.core.remote.Service;
import ch.softappeal.yass.core.remote.TaggedMethodMapper;
import ch.softappeal.yass.core.test.InvokeTest;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {

  static final Client client = new Client(TaggedMethodMapper.FACTORY) {
    @Override public Object invoke(final ClientInvocation invocation) throws Throwable {
      return invocation.invoke(Interceptor.DIRECT, request -> new Server(
        TaggedMethodMapper.FACTORY, new Service(ContractIdTest.ID, new InvokeTest.TestServiceImpl())
      ).invocation(request).invoke(Interceptor.DIRECT));
    }
  };

  @Test public void duplicatedService() {
    final Service service = new Service(ContractIdTest.ID, new InvokeTest.TestServiceImpl());
    try {
      new Server(TaggedMethodMapper.FACTORY, service, service);
      Assert.fail();
    } catch (final IllegalArgumentException e) {
      Assert.assertEquals("serviceId 'TestService' already added", e.getMessage());
    }
  }

  @Test public void noService() {
    try {
      client.invoker(ContractId.create(InvokeTest.TestService.class, "xxx")).proxy().nothing();
      Assert.fail();
    } catch (final RuntimeException e) {
      Assert.assertEquals("no serviceId 'xxx' found (methodId '0')", e.getMessage());
    }
  }

}
