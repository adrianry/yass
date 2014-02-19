package ch.softappeal.yass.transport.ws.echo;

import org.eclipse.jetty.websocket.jsr356.ClientContainer;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.util.concurrent.TimeUnit;

public final class EchoJettyClient {

  public static void run(final WebSocketContainer container) throws Exception {
    final ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();
    {
      final Session session = container.connectToServer(new EchoClientEndpoint(), config, EchoJettyServer.THE_URI);
      session.getBasicRemote().sendText("hello from client 1");
      TimeUnit.MILLISECONDS.sleep(100);
      session.close();
    }
    {
      final Session session = container.connectToServer(new EchoClientEndpoint(), config, EchoJettyServer.THE_URI);
      session.getBasicRemote().sendText("hello from client 2");
      TimeUnit.MILLISECONDS.sleep(100);
      session.close(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, "My Reason"));
    }
    {
      final Session session = container.connectToServer(new EchoClientEndpoint(), config, EchoJettyServer.THE_URI);
      System.out.println("isOpen: " + session.isOpen());
      session.getBasicRemote().sendText("error");
      TimeUnit.MILLISECONDS.sleep(100);
      System.out.println("isOpen: " + session.isOpen());
    }
    {
      final Session session = container.connectToServer(new EchoClientEndpoint(), config, EchoJettyServer.THE_URI);
      final RemoteEndpoint.Basic remote = session.getBasicRemote();
      int count = 0;
      while (true) {
        TimeUnit.MILLISECONDS.sleep(100);
        session.getBasicRemote().sendText("message " + count++);
      }
    }
  }

  public static void main(final String... args) throws Exception {
    final ClientContainer container = new ClientContainer();
    container.start();
    run(container);
  }

}
