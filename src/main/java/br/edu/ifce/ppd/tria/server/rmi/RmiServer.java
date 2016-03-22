package br.edu.ifce.ppd.tria.server.rmi;

import br.edu.ifce.ppd.tria.server.Server;
import br.edu.ifce.ppd.tria.server.rmi.service.RmiRemoteChatService;
import br.edu.ifce.ppd.tria.server.rmi.service.RmiRemoteGameService;
import br.edu.ifce.ppd.tria.server.rmi.service.RmiRemoteRegisterService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by andrecoelho on 2/18/16.
 */
public class RmiServer implements Server {

    private RmiRemoteGameService gameService;
    private RmiRemoteChatService chatService;
    private RmiRemoteRegisterService registerService;

    public RmiServer(RmiRemoteGameService gameService, RmiRemoteChatService chatService, RmiRemoteRegisterService registerService) {
        this.gameService = gameService;
        this.chatService = chatService;
        this.registerService = registerService;
    }

    @Override
    public void start() throws IOException {
        System.out.println("Server initiating fot RMI....");

        try {
            Registry remoteRegistry = LocateRegistry.getRegistry("localhost", 2020);
            remoteRegistry.rebind("GameService", gameService);
            remoteRegistry.rebind("ChatService", chatService);
            remoteRegistry.rebind("RegisterService", registerService);
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Server has successfully initiated");
    }
}
