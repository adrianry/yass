package ch.softappeal.yass.tutorial.client;

import ch.softappeal.yass.transport.TransportSetup;
import ch.softappeal.yass.transport.ws.WsConnection;
import ch.softappeal.yass.transport.ws.WsEndpoint;
import ch.softappeal.yass.tutorial.server.JettyServer;
import ch.softappeal.yass.util.Exceptions;
import ch.softappeal.yass.util.NamedThreadFactory;
import org.eclipse.jetty.websocket.jsr356.ClientContainer;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.concurrent.Executors;

public final class JettyClient {

  private static final TransportSetup TRANSPORT_SETUP = SocketClient.createTransportSetup(
    Executors.newCachedThreadPool(new NamedThreadFactory("requestExecutor", Exceptions.STD_ERR))
  );

  public static final class Endpoint extends WsEndpoint {
    @Override protected WsConnection createConnection(final Session session) throws Exception {
      return new WsConnection(TRANSPORT_SETUP, session);
    }
  }

  private static final URI THE_URI = URI.create("ws://" + JettyServer.HOST + ":" + JettyServer.PORT + JettyServer.PATH);

  public static void run(final WebSocketContainer container) throws Exception {
    final ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();
    container.connectToServer(new Endpoint(), config, THE_URI);
    System.out.println("started");
  }

  public static void main(final String... args) throws Exception {
    final ClientContainer container = new ClientContainer();
    container.start();
    run(container);
  }

}
