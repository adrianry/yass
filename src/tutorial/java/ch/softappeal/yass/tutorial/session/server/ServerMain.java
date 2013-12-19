package ch.softappeal.yass.tutorial.session.server;

import ch.softappeal.yass.core.remote.Server;
import ch.softappeal.yass.core.remote.session.Connection;
import ch.softappeal.yass.core.remote.session.Session;
import ch.softappeal.yass.core.remote.session.SessionFactory;
import ch.softappeal.yass.core.remote.session.SessionSetup;
import ch.softappeal.yass.transport.socket.SessionTransport;
import ch.softappeal.yass.tutorial.session.contract.Config;
import ch.softappeal.yass.tutorial.session.contract.ServerServices;
import ch.softappeal.yass.util.ContextLocator;
import ch.softappeal.yass.util.Exceptions;
import ch.softappeal.yass.util.NamedThreadFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class ServerMain {

  public static final SocketAddress ADDRESS = new InetSocketAddress("localhost", 28947);

  public static void main(final String... args) {
    final Executor executor = Executors.newCachedThreadPool(new NamedThreadFactory("executor", Exceptions.STD_ERR));
    new SessionTransport(
      new SessionSetup(
        new Server(
          Config.METHOD_MAPPER_FACTORY,
          ServerServices.InstrumentServiceId.service(new InstrumentServiceImpl(), Logger.SERVER),
          ServerServices.PriceEngineId.service(
            new PriceEngineImpl(
              new ContextLocator<PriceEngineContext>() {
                @Override public PriceEngineContext context() {
                  return (PriceEngineContext)Session.get();
                }
              }
            ),
            Logger.SERVER
          )
        ),
        executor,
        new SessionFactory() {
          @Override public Session create(final SessionSetup setup, final Connection connection) {
            return new ServerSession(setup, connection);
          }
        }
      ),
      Config.PACKET_SERIALIZER,
      executor, executor,
      Exceptions.STD_ERR
    ).start(executor, ADDRESS);
    System.out.println("started");
  }

}
