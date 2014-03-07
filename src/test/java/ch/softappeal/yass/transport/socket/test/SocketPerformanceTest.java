package ch.softappeal.yass.transport.socket.test;

import ch.softappeal.yass.core.remote.session.test.PerformanceTest;
import ch.softappeal.yass.core.test.InvokeTest;
import ch.softappeal.yass.serialize.Serializer;
import ch.softappeal.yass.serialize.test.SerializerTest;
import ch.softappeal.yass.transport.MessageSerializer;
import ch.softappeal.yass.transport.PacketSerializer;
import ch.softappeal.yass.transport.StringPathSerializer;
import ch.softappeal.yass.transport.socket.PathResolver;
import ch.softappeal.yass.transport.socket.SocketListenerTest;
import ch.softappeal.yass.transport.socket.SocketTransport;
import ch.softappeal.yass.util.NamedThreadFactory;
import ch.softappeal.yass.util.Nullable;
import ch.softappeal.yass.util.TestUtils;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SocketPerformanceTest extends InvokeTest {

  public static final int COUNTER = 1;

  public static final Serializer MESSAGE_SERIALIZER = new MessageSerializer(SerializerTest.TAGGED_FAST_SERIALIZER);

  public static final Serializer PACKET_SERIALIZER = new PacketSerializer(MESSAGE_SERIALIZER);

  private static SocketTransport createTransport(final Executor executor, @Nullable final CountDownLatch latch) {
    return new SocketTransport(
      PerformanceTest.createSetup(executor, latch, COUNTER),
      PACKET_SERIALIZER,
      executor, executor,
      TestUtils.TERMINATE
    );
  }

  @Test public void test() throws InterruptedException {
    final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("executor", TestUtils.TERMINATE));
    try {
      SocketTransport.listener(StringPathSerializer.INSTANCE, new PathResolver(SocketTransportTest.PATH, createTransport(executor, null)), executor, TestUtils.TERMINATE).start(executor, SocketListenerTest.ADDRESS);
      final CountDownLatch latch = new CountDownLatch(1);
      createTransport(executor, latch).connect(SocketListenerTest.ADDRESS, StringPathSerializer.INSTANCE, SocketTransportTest.PATH);
      latch.await();
      TimeUnit.MILLISECONDS.sleep(100L);
    } finally {
      SocketListenerTest.shutdown(executor);
    }
  }

}
