package br.edu.ifce.ppd.tria.server.socket.model;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.server.socket.SocketConnection;

/**
 * Created by andrecoelho on 2/19/16.
 */
public class SocketClient extends Client {

    private transient SocketConnection connection;

    public SocketClient(SocketConnection connection, Client client) {
        super(client.getId());
        this.connection = connection;
    }

    public void send(Action action) {
        connection.send(action);
    }
}
