package br.edu.ifce.ppd.tria.server.socket;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.core.service.ChatService;
import br.edu.ifce.ppd.tria.core.service.GameService;
import br.edu.ifce.ppd.tria.core.service.RegisterService;
import br.edu.ifce.ppd.tria.server.Server;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteChatService;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteGameService;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteRegisterService;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 * Created by andrecoelho on 2/18/16.
 */
public class SocketServer implements Server {

    private ServerSocket serverSocket;
    private Route route;

    public SocketServer(RegisterService registerService, GameService gameService, ChatService chatService) throws IOException {
        this.serverSocket = new ServerSocket(8088);
        this.route = new Route((SocketRemoteRegisterService)registerService,
                (SocketRemoteGameService)gameService, (SocketRemoteChatService) chatService);
    }

    private SocketConnection listenConnection() throws IOException {
        return new SocketConnection(serverSocket.accept(), route);
    }

    @Override
    public void start() throws IOException {
        System.out.println("Server initiating for socket...");
        System.out.println("Server started at port " + 8088 + "...");

        while (true) {
            SocketConnection connection = listenConnection();
            connection.start();
        }
    }

    private Action notifyAction(Client client) {
        HashMap<String, Serializable> body = new HashMap<>();
        body.put("newClient", client);
        return new Action("connected", body);
    }
}
