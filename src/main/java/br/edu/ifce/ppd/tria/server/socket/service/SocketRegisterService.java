package br.edu.ifce.ppd.tria.server.socket.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.service.RegisterService;
import br.edu.ifce.ppd.tria.server.business.RegistrationBusiness;
import br.edu.ifce.ppd.tria.server.socket.SocketConnection;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;

import java.util.UUID;

/**
 * Created by andrecoelho on 2/19/16.
 */
public class SocketRegisterService implements RegisterService{

    private RegistrationBusiness registrationBusiness;

    public SocketRegisterService(RegistrationBusiness registrationBusiness) {
        this.registrationBusiness = registrationBusiness;
    }

    @Override
    public Client register() {
        return new Client(UUID.randomUUID().toString());
    }

    @Override
    public void deregister(Client client) {
        Game closedGame = registrationBusiness.deregister(client);

        if (closedGame == null)
            return; // do nothing

        if (closedGame.isFirstPlayer(client)) {
            // notify second Player
        } else {
            // notify second Player
        }
    }

    public SocketClient register(SocketConnection connection) {
        return (SocketClient) registrationBusiness.register(new SocketClient(connection, register()));
    }


}
