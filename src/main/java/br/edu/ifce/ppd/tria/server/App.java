package br.edu.ifce.ppd.tria.server;


import br.edu.ifce.ppd.tria.core.service.ChatService;
import br.edu.ifce.ppd.tria.core.service.GameService;
import br.edu.ifce.ppd.tria.core.service.RegisterService;
import br.edu.ifce.ppd.tria.server.business.GameBusiness;
import br.edu.ifce.ppd.tria.server.business.RegistrationBusiness;
import br.edu.ifce.ppd.tria.server.repositroy.ClientRepository;
import br.edu.ifce.ppd.tria.server.repositroy.GameRepository;
import br.edu.ifce.ppd.tria.server.socket.SocketServer;
import br.edu.ifce.ppd.tria.server.socket.service.SocketChatService;
import br.edu.ifce.ppd.tria.server.socket.service.SocketGameService;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRegisterService;

import java.io.IOException;

/**
 * Created by andrecoelho on 2/16/16.
 */
public class App {



    public static void main(String... args) throws IOException {
        ClientRepository clients = new ClientRepository();
        GameRepository games = new GameRepository();

        GameBusiness gameBusiness = new GameBusiness(games, clients);
        RegistrationBusiness registrationBusiness = new RegistrationBusiness(clients, games);

        ChatService chatService = new SocketChatService(games, clients);
        GameService gameService = new SocketGameService(gameBusiness);
        RegisterService registerService = new SocketRegisterService(registrationBusiness);

        Server server = new SocketServer(registerService, gameService, chatService);
        server.start();
    }

}
