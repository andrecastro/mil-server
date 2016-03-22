package br.edu.ifce.ppd.tria.server;


import br.edu.ifce.ppd.tria.core.service.ChatService;
import br.edu.ifce.ppd.tria.core.service.GameService;
import br.edu.ifce.ppd.tria.core.service.RegisterService;
import br.edu.ifce.ppd.tria.server.business.GameBusiness;
import br.edu.ifce.ppd.tria.server.business.RegistrationBusiness;
import br.edu.ifce.ppd.tria.server.repositroy.ClientRepository;
import br.edu.ifce.ppd.tria.server.repositroy.GameRepository;
import br.edu.ifce.ppd.tria.server.rmi.RmiServer;
import br.edu.ifce.ppd.tria.server.rmi.service.RmiRemoteChatService;
import br.edu.ifce.ppd.tria.server.rmi.service.RmiRemoteGameService;
import br.edu.ifce.ppd.tria.server.rmi.service.RmiRemoteRegisterService;
import br.edu.ifce.ppd.tria.server.socket.SocketServer;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteChatService;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteGameService;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteRegisterService;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by andrecoelho on 2/16/16.
 */
public class App {

    public static void main(String... args) throws IOException {
        ClientRepository clients = new ClientRepository();
        GameRepository games = new GameRepository();

        GameBusiness gameBusiness = new GameBusiness(games, clients);
        RegistrationBusiness registrationBusiness = new RegistrationBusiness(clients, games);

//        Server server = socketServer(gameBusiness, registrationBusiness);
        Server server = rmiServer(gameBusiness, registrationBusiness);
        server.start();
    }

    private static SocketServer socketServer(GameBusiness gameBusiness, RegistrationBusiness registrationBusiness)
            throws IOException {
        ChatService chatService = new SocketRemoteChatService(gameBusiness);
        GameService gameService = new SocketRemoteGameService(gameBusiness);
        RegisterService registerService = new SocketRemoteRegisterService(registrationBusiness);

        return new SocketServer(registerService, gameService, chatService);
    }

    private static RmiServer rmiServer(GameBusiness gameBusiness, RegistrationBusiness registrationBusiness)
            throws RemoteException {
        LocateRegistry.createRegistry(2020);

        RmiRemoteChatService chatService = new RmiRemoteChatService(gameBusiness);
        RmiRemoteGameService gameService = new RmiRemoteGameService(gameBusiness);
        RmiRemoteRegisterService registerService = new RmiRemoteRegisterService(registrationBusiness);

        return new RmiServer(gameService, chatService, registerService);
    }


}
